package hep.io.sio;

import java.io.IOException;

/**
 * A record read from an SIOReader.
 * @author tonyj
 */
public interface SIORecord {

    /**
     * Get the record name.
     * @return The name
     * @throws IOException If an error occurs.
     */
    String getRecordName() throws IOException;

    /**
     * Get the length of the record.
     * @return The length in bytes (uncompressed)
     * @throws IOException If an error occurs.
     */
    int getRecordLength() throws IOException;

    /**
     * Get the next block from this record.
     * @return The next block.
     * @throws IOException If an error occurs.
     */
    SIOBlock getBlock() throws IOException;
}