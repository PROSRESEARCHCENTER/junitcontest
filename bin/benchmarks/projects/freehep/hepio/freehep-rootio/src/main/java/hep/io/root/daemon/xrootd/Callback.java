package hep.io.root.daemon.xrootd;

import java.io.IOException;

/**
 *
 * @author tonyj
 */
abstract class Callback<V> {
    /**
     * Called by the system when a response is ready. This method must completely
     * read the response before returning. Since xrootd supports partial responses
     * this method may be called multiple times for a single message (in which case 
     * <code>response.isComplete() == false</code> for all but the last call).
     * @param response The response
     * @return The final result of processing the response.
     * @throws java.io.IOException If there is an error reading the response
     */
    abstract V responseReady(Response response) throws IOException;
    /**
     * Called if the original message had to be resubmitted. In this case any
     * partial responses previously received should be discarded. Default implementation
     * does nothing.
     */
    void clear() {
        
    }
    
    /**
     * The default response callback reads any data available in the response,
     * and returns null as the response.
     */
    static class DefaultCallback extends Callback<Void> {

        public Void responseReady(Response response) throws IOException {
            response.readData();
            return null;
        }
    }
    
    static class StringCallback extends Callback<String> {

        String responseReady(Response response) throws IOException {
            return response.getDataAsString();
        }
    }
}
