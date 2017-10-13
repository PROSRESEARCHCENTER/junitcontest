package hep.io.sio;

/**
 * A reference to an Object which will not be available until the whole record has been read.
 */
public interface SIORef {

    /**
     * Get the referenced object
     * @return The referenced object, or <code>null</code> if the object does not exist in the record.
     */
    Object getObject();
}