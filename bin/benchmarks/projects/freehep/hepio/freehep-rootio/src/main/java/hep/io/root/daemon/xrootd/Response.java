package hep.io.root.daemon.xrootd;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * Encapsulates a response from the xrootd server.
 * Initially this class reads the response header (always 8 bytes).
 * @author tonyj
 */
class Response {

    private Multiplexor multiplexor;
    private SocketChannel in;
    private ByteBuffer buffer = ByteBuffer.allocate(8);
    private ByteBuffer data;
    private Short handle;
    private int status;
    private int dataLength;
    private static Logger logger = Logger.getLogger(Response.class.getName());

    /**
     * Create a response object for reading from a specific mumtiiplexor
     * @param multiplexor
     * @param in
     */
    Response(Multiplexor multiplexor, SocketChannel in) {
        this.in = in;
        this.multiplexor = multiplexor;
    }

    SocketChannel getSocketChannel() {
        return in;
    }

    /**
     * Read an integer from the data associated with this response.
     * @throws java.io.IOException
     */
    int readInt() throws IOException {
        readData();
        return data.getInt();
    }

    /**
     * Get all of the data from this response as a ByeBuffer
     * @return
     * @throws java.io.IOException
     */
    ByteBuffer getData() throws IOException {
        readData();
        return data;
    }

    /**
     * Read the remaining data associated with this response and convert it
     * to a String.
     * @throws java.io.IOException
     */
    String getDataAsString() throws IOException {
        readData();
        byte[] dataArray = data.array();
        int start = data.position();
        int length = data.remaining();
        if (dataArray[start+length-1]==0) length--; // Trim trailing 0 (if any)
        return new String(dataArray,start,length, "US-ASCII");
    }

    Multiplexor getMultiplexor() {
        return multiplexor;
    }

    void readData() throws IOException {
        if (data == null) {
            data = ByteBuffer.allocate(dataLength);
            readBuffer(data);
            data.flip();
        }
    }
    /** Read data into the given byte buffer, which must be large enough to accept
     * the entire data section of the response.
     * @param buffer
     * @throws java.io.IOException
     */
    void readData(ByteBuffer buffer) throws IOException {
        int oldLimit = -1;
        try {
            if (buffer.remaining() > dataLength) {
                oldLimit = buffer.limit();
                buffer.limit(buffer.position() + dataLength);
            }
            readBuffer(buffer);
        } finally {
            if (oldLimit >= 0) {
                buffer.limit(oldLimit);
            }
        }
    }

    Destination getDestination() {
        return multiplexor.getDestination();
    }

    boolean isComplete() {
        return status != XrootdProtocol.kXR_oksofar;
    }

    int read() throws IOException {
        buffer.clear();
        readBuffer(buffer);
        buffer.flip();
        handle = buffer.getShort();
        status = buffer.getShort();
        dataLength = buffer.getInt();
        data = null;
        logger.finest("<-" + handle + " " + status + " " + dataLength);
        return 8 + dataLength;
    }

    void regurgitate() {
        handle = data.getShort();
        status = data.getShort();
        dataLength = data.getInt();
        logger.finest("<-" + handle + " " + status + " " + dataLength);
    }

    int getStatus() {
        return status;
    }

    int getLength() {
        return dataLength;
    }

    Short getHandle() {
        return handle;
    }

    @Override
    public String toString() {
        return String.format("Response handle: %d status: %d dataLength: %d", handle, status, dataLength);
    }

    private void readBuffer(ByteBuffer buffer) throws EOFException, IOException {
        while (buffer.remaining()>0) {
            int l = in.read(buffer);
            if (l < 0) {
                throw new EOFException();
            }
        }
    }
}
