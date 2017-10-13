package hep.io.root.daemon.xrootd;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tonyj
 */
interface ResponseListener {

    void reschedule(long seconds, TimeUnit SECONDS);

    void handleError(IOException iOException);

    void handleRedirect(String host, int port) throws UnknownHostException;

    void handleResponse(Response response) throws IOException;

    void handleSocketError(IOException x);
}
