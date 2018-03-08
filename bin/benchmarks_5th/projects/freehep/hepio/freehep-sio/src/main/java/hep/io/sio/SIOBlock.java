package hep.io.sio;

import java.io.IOException;

/**
 * A block read from the SIORecord.
 * @author tonyj
 */
public interface SIOBlock {

    /**
     * Get the block name.
     * @return The name of the block.
     * @throws IOException If an error occurs
     */
    String getBlockName() throws IOException;

    /**
     * Get the length of the block.
     * @return The (uncompressed) length.
     * @throws IOException If an error occurs
     */
    int getBlockLength() throws IOException;

    /**
     * The number of unread bytes remaining in the record.
     * @return The number of bytes remaining.
     * @throws IOException If an error occurs.
     */
    int getBytesLeft() throws IOException;

    /**
     * Get the packed version number.
     * @return <code>major&lt;&lt;16+minor</code>
     * @throws IOException
     */
    int getVersion() throws IOException;

    /**
     * Get the major version of the block.
     * @return The major version.
     * @throws IOException If an error occurs.
     */
    int getMajorVersion() throws IOException;

    /**
     * Get the minor version of the block.
     * @return The minor version.
     * @throws IOException If an error occurs.
     */
    int getMinorVersion() throws IOException;

    /**
     * Get a stream from which the blocks data can be read.
     * @return An SIOStream from which the blocks data can be read.
     * @throws IOException If an error occurs.
     */
    SIOInputStream getData() throws IOException;
}