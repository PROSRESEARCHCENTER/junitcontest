package hep.io.root.daemon.xrootd;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A future returned when executing asynchronous operations.
 * @author tonyj
 */
public abstract class FutureResponse<V> implements Future<V> {

    public V getResponse() throws IOException {
        return getResponse(Integer.MAX_VALUE, TimeUnit.SECONDS);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("Cancel not supported.");
    }

    public boolean isCancelled() {
        return false;
    }

    public V get() throws InterruptedException, ExecutionException {
        try {
            return getResponse(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (IOException x) {
            throw new ExecutionException(x);
        }
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            V result = getResponse(timeout, unit);
            if (result == null) {
                throw new TimeoutException();
            }
            return result;
        } catch (IOException x) {
            throw new ExecutionException(x);
        }
    }

    public abstract V getResponse(long timeout, TimeUnit unit) throws IOException;
}
