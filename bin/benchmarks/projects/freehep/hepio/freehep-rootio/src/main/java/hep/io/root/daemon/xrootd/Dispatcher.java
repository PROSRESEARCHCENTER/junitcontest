package hep.io.root.daemon.xrootd;

import hep.io.root.daemon.xrootd.MultiplexorManager.MultiplexorReadyCallback;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dispatches messages using a thread pool.
 * @author tonyj
 */
class Dispatcher {

    private static Logger logger = Logger.getLogger(Dispatcher.class.getName());
    private static final int WAIT_TIMEOUT = Integer.getInteger("hep.io.root.deamon.xrootd.timeout", 3000).intValue();
    private static final int WAIT_LIMIT = Integer.getInteger("hep.io.root.deamon.xrootd.waitLimit", 1000).intValue();
    private static Dispatcher theDispatcher = new Dispatcher();
    private ScheduledThreadPoolExecutor scheduler;
    private MultiplexorManager manager;

    private Dispatcher() {
        scheduler = new ScheduledThreadPoolExecutor(1,new DaemonThreadFactory());
        manager = new MultiplexorManager(scheduler);
    }

    private static class DaemonThreadFactory implements ThreadFactory
    {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "xrootd-dispatcher");
            t.setDaemon(true);
            return t;
        }
    }
    
    static Dispatcher instance() {
        return theDispatcher;
    }

    <V> FutureResponse<V> send(Destination destination, Operation<V> operation) {
        MessageExecutor executor = new MessageExecutor(destination, operation);
        //scheduler.execute(executor);
        executor.run();
        return new FutureMessageResponse<V>(executor);
    }

    private void resend(MessageExecutor executor) {
        resend(executor, 0, TimeUnit.SECONDS);
    }

    private void resend(MessageExecutor executor, long time, TimeUnit units) {
        scheduler.schedule(executor, time, units);
    }

    /**
     * The chain callback is used when a prerequisite message has been scheduled.
     * It fires the callback associated with the prerequisite, and 
     * then resends the original message.
     */
    private class ChainCallback<V> extends Callback<V> {

        private MessageExecutor originalMessageExecutor;
        private Callback<V> chain;

        ChainCallback(Callback<V> chain, MessageExecutor originalMessageExecutor) {
            this.originalMessageExecutor = originalMessageExecutor;
            this.chain = chain;
        }

        public V responseReady(Response response) throws IOException {
            V result = chain.responseReady(response);
            if (response.isComplete()) {
                // If the prerequisite was redirected we need the original message to be
                // redirected too.
                originalMessageExecutor.destination = response.getDestination();
                resend(originalMessageExecutor);
            }
            return result;
        }

        @Override
        public void clear() {
            chain.clear();
        }
    }

    private static class FutureMessageResponse<V> extends FutureResponse<V> {

        private final MessageExecutor<V> listener;

        FutureMessageResponse(MessageExecutor<V> listener) {
            this.listener = listener;
        }

        public V getResponse(long timeout, TimeUnit timeUnit) throws IOException {
            long start = System.nanoTime();
            long timeoutNS = timeUnit.toNanos(timeout);
            long waitTimeoutNS = TimeUnit.NANOSECONDS.convert(WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
            try {
                for (;;) {
                    synchronized (listener) {
                        if (listener.isDone()) return listener.getResult();
                        TimeUnit.NANOSECONDS.timedWait(listener, Math.min(timeoutNS,waitTimeoutNS));
                        if (listener.isDone()) return listener.getResult();
                    }
                    long totalWaitNS = System.nanoTime() - start;
                    if (totalWaitNS > timeoutNS) return null;                   
                    logger.warning("Waiting for response for " + TimeUnit.SECONDS.convert(totalWaitNS,TimeUnit.NANOSECONDS) + " secs " + listener.toString());
                    if (totalWaitNS >= WAIT_LIMIT * waitTimeoutNS) {
                        throw new IOException("Timeout waiting for response after " + TimeUnit.SECONDS.convert(totalWaitNS,TimeUnit.NANOSECONDS) + "secs");
                    }
                }
            } catch (InterruptedException x) {
                IOException iio = new InterruptedIOException("Xrootd IO interrupted");
                iio.initCause(x);
                throw iio;
            }
        }

        public boolean isDone() {
            return listener.isDone();
        }
    }

    /**
     * A MessageExecutor consists of a message, a destination, plus a callback 
     * to be called when the response from the message is available. The message executor
     * is also a runnable which when executed submits the message to an appropriate multiplexor.
     * The destination associated with the message executor may be changed as a result of 
     * a "redirect" response from the server, or because the multiplexor associated with
     * the destination reports a problem. Some messages have "prerequisites" which must be
     * executed when the destination changes, in which case a separate MessageExecutor is created
     * for the prerequsite, with the callback for the new MessageExecutor set to reexecute the
     * original MessageExecutor.
     * @param <V>
     */
    private class MessageExecutor<V> implements ResponseListener, Runnable, MultiplexorReadyCallback {

        private Operation<V> operation;
        private V result;
        private IOException exception;
        private boolean isDone = false;
        private int errors = 0;
        private Destination destination;
        private long startTime = System.currentTimeMillis();

        MessageExecutor(Destination destination, Operation<V> operation) {
            this.destination = destination;
            this.operation = operation;
        }

        public void run() {
            try {
                
                Multiplexor multiplexor = manager.getMultiplexor(destination,this);
                if (multiplexor == null)
                {
                   return;
                }
                Multiplexor expectedMultiplexor = operation.getMultiplexor();
                if (expectedMultiplexor != null && multiplexor != expectedMultiplexor)
                {
                   Operation preReq = operation.getPrerequisite();
                   ChainCallback cc = new ChainCallback(preReq.getCallback(), this);
                   MessageExecutor executor = new MessageExecutor(destination, new Operation(preReq.getName() + "-chain", preReq.getMessage(), cc));
                   resend(executor);                    
                }
                else
                {
                    multiplexor.sendMessage(operation.getMessage(), this);
                    logger.fine(String.format("Sent %s to %s after %,dms",operation,multiplexor,System.currentTimeMillis()-startTime));
                }
            } catch (IOException x) {
                handleSocketError(x);
            } catch (Throwable x) {
                logger.log(Level.SEVERE, "Unexpected error while sending message", x);
            }
        }

        public void multiplexorReady(Multiplexor multiplexor) {
            resend(this);
        }
        
        public synchronized void handleError(IOException exception) {
            this.exception = exception;
            isDone = true;
            notify();
            logger.fine(String.format("Received error for %s after %,dms",operation,System.currentTimeMillis()-startTime));
        }

        public void reschedule(long time, TimeUnit units) {
            resend(this, time, units);
        }

        public void handleRedirect(String host, int port) throws UnknownHostException {
            Destination redirected = destination.getRedirected(host, port);
            operation.getCallback().clear();
            destination = redirected;
            resend(this);
        }

        public synchronized void handleResponse(Response response) throws IOException {
            result = operation.getCallback().responseReady(response);
            if (response.isComplete()) {
                isDone = true;
                notify();
                logger.fine(String.format("Received response %s from %s after %,dms",operation,response.getMultiplexor(),System.currentTimeMillis()-startTime));
            }
        }

        public void handleSocketError(IOException iOException) {
            errors++;
            if (errors > 1 && destination.getPrevious() != null) {
                destination = destination.getPrevious();
            }
            operation.getCallback().clear();
            resend(this,1,TimeUnit.SECONDS);
        }

        synchronized V getResult() throws IOException {
            if (exception != null) {
                throw exception;
            }
            return result;
        }

        synchronized boolean isDone() {
            return isDone;
        }
        
        @Override
        public String toString()
        {
            return operation+"@"+destination;
        }
    }
}
