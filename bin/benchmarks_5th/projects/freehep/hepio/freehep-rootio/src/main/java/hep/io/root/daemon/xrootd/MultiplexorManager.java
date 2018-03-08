package hep.io.root.daemon.xrootd;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import hep.io.root.daemon.xrootd.LoginOperation.LoginSession;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Manages the creation and destruction of multiplexors.
 * @author tonyj
 */
class MultiplexorManager {

    private enum Stage {

        CONNECT, LOGIN, AUTH
    }
    private static Logger logger = Logger.getLogger(MultiplexorManager.class.getName());
    private static MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    private Map<Destination, Multiplexor> multiplexorMap = new HashMap<Destination, Multiplexor>();
    private Map<Destination, List<MultiplexorReadyCallback>> inProgressConnections = new HashMap<Destination, List<MultiplexorReadyCallback>>();
    private ScheduledThreadPoolExecutor scheduler;

    MultiplexorManager(ScheduledThreadPoolExecutor scheduler) {
        this.scheduler = scheduler;
        scheduler.scheduleAtFixedRate(new IdleConnectionCloser(), 5, 5, TimeUnit.SECONDS);
    }

    private void createMultiplexor(Destination destination, int attempt) {
        Destination actualDestination = destination.getAlternateDestination(attempt);
        try {
            Multiplexor multiplexor = new Multiplexor(actualDestination);
            multiplexor.connect(new LoginResponseListener(actualDestination, attempt));
        } catch (IOException x) {
            connectionFailed(actualDestination,attempt,x);
        }
    }

    private synchronized void connectionComplete(Multiplexor multiplexor) {
        multiplexorMap.put(multiplexor.getDestination(), multiplexor);
        registerMultiplexor(multiplexor);
        List<MultiplexorReadyCallback> callbacks = inProgressConnections.get(multiplexor.getDestination());
        for (MultiplexorReadyCallback callback : callbacks) {
            callback.multiplexorReady(multiplexor);
        }
        inProgressConnections.remove(multiplexor.getDestination());
    }

    private synchronized void connectionFailed(Destination destination, int attempt, IOException iOException) {
        logger.log(Level.WARNING, String.format("Connection to %s failed (attempt %d) ",destination,attempt), iOException);
        scheduler.schedule(new Reconnect(destination,attempt+1), 2, TimeUnit.SECONDS);
    }

    /** If a multiplexor is ready to be used for the given destination, return it immediately
     * otherwise return <code>void</code> and call the given callback when the connection
     * becomes ready.
     */
    synchronized Multiplexor getMultiplexor(Destination destination, MultiplexorReadyCallback callback) {
        Multiplexor result = multiplexorMap.get(destination);
        if (result != null && result.isSocketClosed()) {
            multiplexorMap.remove(result);
            unregisterMultiplexor(result);
            result = null;
        }
        if (result == null) {
            List<MultiplexorReadyCallback> callbacks = inProgressConnections.get(destination);
            if (callbacks == null) {
                callbacks = new ArrayList<MultiplexorReadyCallback>();
                inProgressConnections.put(destination, callbacks);
                callbacks.add(callback);
                createMultiplexor(destination,0);
            } else {
                callbacks.add(callback);
            }

        }
        return result;
    }

    private ObjectName getObjectNameForMultiplexor(Multiplexor m) throws MalformedObjectNameException {
        return new ObjectName("hep.io.root.daemon.xrootd:type=Multiplexor,name=" + m.toString().replace(":", ";"));
    }

    private void registerMultiplexor(Multiplexor result) {
        try {
            mbs.registerMBean(new StandardMBean(result, MultiplexorMBean.class), getObjectNameForMultiplexor(result));
        } catch (Exception x) {
            logger.log(Level.WARNING, "Could not register multiplexor mbean", x);
        }
    }

    private void unregisterMultiplexor(Multiplexor m) {
        try {
            mbs.unregisterMBean(getObjectNameForMultiplexor(m));
        } catch (Exception x) {
            logger.log(Level.WARNING, "Could not unregister multiplexor mbean", x);
        }
    }

    static interface MultiplexorReadyCallback {

        void multiplexorReady(Multiplexor multiplexor);
    }

    private class LoginResponseListener implements ResponseListener {

        private Destination destination;
        private LoginOperation login;
        private AuthOperation auth;
        private Stage stage = Stage.CONNECT;
        private int attempt;

        LoginResponseListener(Destination destination, int attempt) {
            this.attempt = attempt;
            this.destination = destination;
            login = new LoginOperation(destination.getUserName());
        }

        public void reschedule(long seconds, TimeUnit SECONDS) {
            throw new UnsupportedOperationException("Not supported during login.");
        }

        public void handleError(IOException iOException) {
            throw new UnsupportedOperationException("Not supported during login.");
        }

        public void handleRedirect(String host, int port) throws UnknownHostException {
            throw new UnsupportedOperationException("Not supported during login.");
        }

        public void handleResponse(Response response) throws IOException {
            switch (stage) {

                case CONNECT:
                    response.getMultiplexor().handleInitialHandshakeResponse(response);
                    stage = Stage.LOGIN;
                    response.getMultiplexor().sendMessage(login.getMessage(), this);
                    break;

                case LOGIN:
                    LoginSession session = login.getCallback().responseReady(response);
                    if (session.getSecurity() != null) {
                        stage = Stage.AUTH;
                        auth = new AuthOperation();
                        response.getMultiplexor().sendMessage(auth.getMessage(), this);
                    } else {
                        connectionComplete(response.getMultiplexor());
                    }
                    break;

                case AUTH:
                    auth.getCallback().responseReady(response);
                    connectionComplete(response.getMultiplexor());
            }
        }

        public void handleSocketError(IOException iOException) {
            connectionFailed(destination,attempt,iOException);
        }
    }
    private class Reconnect implements Runnable {
        private Destination destination;
        private int attempt;

        public Reconnect(Destination destination, int attempt) {
            this.destination = destination;
            this.attempt = attempt;
        }

        public void run() {
            createMultiplexor(destination, attempt);
        }
    }
    private class IdleConnectionCloser implements Runnable {

        public void run() {
            for (Iterator<Map.Entry<Destination, Multiplexor>> i = multiplexorMap.entrySet().iterator(); i.hasNext();) {
                Map.Entry<Destination, Multiplexor> entry = i.next();
                Multiplexor m = entry.getValue();
                if (m.isIdle()) {
                    i.remove();
                    unregisterMultiplexor(m);
                    m.close();
                    logger.log(Level.FINE, "Closed idle connection: " + m);
                }
            }
        }
    }
}
