package hep.io.root.daemon.xrootd;

/**
 * An xrootd operation consists of a message to be sent to the server, plus
 * a callback to handle the response from the server.
 * @author tonyj
 */
public class Operation <V> {
    private Message message;
    private Callback<V> callback;
    private String name;
    
    Operation(String operationName, Message message, Callback<V> callback) {
        this.name = operationName;
        this.message = message;
        this.callback = callback;
    }

    Callback<V> getCallback() {
        return callback;
    }

    Message getMessage() {
        return message;
    }
    
    @Override
    public String toString()
    {
        return name;
    }

    String getName() {
        return name;
    }
    
    /** Some operations have prerequisites which must be executed before they
     * can be executed. For instance a ReadOperation may require that an OpenOperation
     * has been preformed first. 
     * @return The prerequsite or <code>null</code> if operation has no prerequsite.
     */
    
    Operation getPrerequisite() {
        return null;
    }
    
    /** Some operations have preferred destinations, such as a read operation on 
     * an open file, which would prefer to be executed where the file is already
     * open.
     * @return The preferred destination, or <code>null</code> if no preferred destination.
     */
    
    Destination getDestination() {
        return null;
    }
    
    Multiplexor getMultiplexor() {
        return null;
    }

}
