package hep.io.root.daemon.xrootd;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * A message contains all of the information needed to perform a single
 * xrootd operation. A message normally consists of an xrootd op code, an 
 * optional string (such as a file path), and op code dependent extra information
 * which can be encoded into the header. The extra information can be written 
 * to the header by calling the write* methods of this class.
 * @author tonyj
 */
class Message {

    ByteBuffer buffer;
    private static Logger logger = Logger.getLogger(Response.class.getName());
    private ByteBuffer data;

    /** Create a message from an Xrootd operation code
     * @param message The op code
     */
    Message(int message) {
        buffer = ByteBuffer.allocate(24);
        buffer.putShort((short) 0);
        buffer.putShort((short)message);
    }
    /**
     * Create a message from an Xrootd operation code plus a string
     * @param message The op code
     * @param string The string to be sent with the message (such as a file path)
     */    
    Message(int message, String string) {
        byte[] bytes = string.getBytes();
        buffer = ByteBuffer.allocate(24+bytes.length);
        buffer.putShort((short) 0);
        buffer.putShort((short) message);
        buffer.position(20);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.position(4);
    }

    int send(short handle, SocketChannel out) throws IOException {
        buffer.position(0);        
        buffer.putShort(handle);
        logger.finest("->" + buffer.getShort());
        writeExtra(buffer);
        if (data != null) buffer.putInt(20,data.remaining());
        buffer.position(0);
        out.write(buffer);
        if (data!= null) out.write(data);
        return buffer.limit();
    }

    void writeByte(int i) {
        buffer.put((byte) (i & 0xff));
    }

    void writeInt(int i) {
        buffer.putInt(i);
    }

    void writeLong(long i) {
        buffer.putLong(i);
    }

    void writeShort(int i) {
        buffer.putShort((short) (i & 0xffff));
    }

    /**
     * This method can be overriden by classes that want to send the extra bytes
     * in the header themselves.
     * @param out
     * @throws java.io.IOException
     */
    void writeExtra(ByteBuffer out) throws IOException {

    }

    /**
     * Used by classes that want to write their own data to the message
     * @param buffer
     * @param offset
     * @param length
     */
    void setData(byte[] buffer, int offset, int length) {
        this.data = ByteBuffer.wrap(buffer,offset,length);
    }
}
