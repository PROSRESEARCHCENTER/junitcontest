/*
 * AidaConnectionException.java
 *
 * Created on May 11, 2003, 8:00 PM
 */

package hep.aida.ref.remote.basic.interfaces;

/**
 *
 * @author  serbo
 */
public class AidaConnectionException extends java.lang.IllegalStateException {
    
    /**
     * Creates a new instance of <code>AidaConnectionException</code> without detail message.
     */
    public AidaConnectionException() {
    }
    
    
    /**
     * Constructs an instance of <code>AidaConnectionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public AidaConnectionException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>AidaConnectionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public AidaConnectionException(String msg, Exception e) {
        super(msg);
        this.setStackTrace(e.getStackTrace());
    }
}
