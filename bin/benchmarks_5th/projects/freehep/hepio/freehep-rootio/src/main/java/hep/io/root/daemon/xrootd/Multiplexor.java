package hep.io.root.daemon.xrootd;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A multiplexor is responsible for managing a single socket connection to an
 * xrootd server. Many clients may use a single multiplexor at the same time.
 * @author tonyj
 */
class Multiplexor implements MultiplexorMBean {

    private static final int MAX_IDLE = Integer.getInteger("hep.io.root.daemon.xrootd.ConnectionTimeout", 60000);
    private static final int SEND_BUFFER_SIZE = Integer.getInteger("hep.io.root.daemon.xrootd.SendBufferSize", 65536);
    private static final int RECEIVE_BUFFER_SIZE = Integer.getInteger("hep.io.root.daemon.xrootd.ReceivedBufferSize", 65536);
    private static Logger logger = Logger.getLogger(Multiplexor.class.getName());
    private Destination descriptor;
    private SocketChannel channel;
    private Response response;
    private BitSet handles = new BitSet();
    private Thread thread;
    private Map<Short, ResponseListener> responseMap = new HashMap<Short, ResponseListener>();
    private boolean socketClosed = false;
    private long bytesSent;
    private long bytesReceived;
    private Date createDate = new Date();
    private Date lastActive = new Date();
    private int pval;
    private int flag;

    Multiplexor(Destination desc) throws IOException {
        logger.fine(desc + " Creating multiplexor");
        this.descriptor = desc;
        channel = SocketChannel.open();
        channel.socket().setReceiveBufferSize(RECEIVE_BUFFER_SIZE);
        channel.socket().setSendBufferSize(SEND_BUFFER_SIZE);
        thread = new Thread(new SocketReader(), "XrootdReader-" + this);
        thread.setDaemon(true);
        response = new Response(this, channel);
    }

    /**
     * Connect to the remote socket. The callback will be called
     * after the initial handshake is complete, or if an error occurs.
     * @param callback
     */
    void connect(ResponseListener listener) {
        addListener(listener);
        thread.start();
    }

    void handleInitialHandshakeResponse(Response response) throws IOException {

        if (response.getLength() != 8) {
            throw new IOException("Unexpected initial handshake length");
        }
        pval = response.readInt();
        flag = response.readInt();
    }

    private void sendInitialHandshake() throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.putInt(12, 4);
        buffer.putInt(16, 2012);
        bytesSent += channel.write(buffer);
    }

    boolean isSocketClosed() {
        return socketClosed;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getUserName() {
        return descriptor.getUserName();
    }

    public String getHostAndPort() {
        return descriptor.getAddressAndPort();
    }

    public Date getLastActive() {
        return lastActive;
    }

    public int getOutstandingResponseCount() {
        return handles.cardinality();
    }

    public long getIdleTime() {
        return System.currentTimeMillis() - lastActive.getTime();
    }

    public int getProtocolVersion() {
        return pval;
    }

    public int getServerFlag() {
        return flag;
    }

    boolean isIdle() {
        return getOutstandingResponseCount() == 0 && getIdleTime() > MAX_IDLE;
    }

    Destination getDestination() {
        return descriptor;
    }

    void sendMessage(Message message, ResponseListener listener) throws IOException {
        short id = addListener(listener);
        try {
            sendMessage(id, message);
        } catch (IOException x) {
            removeListener(id);
            throw x;
        }
    }

    void close() {
        socketClosed = true;
        try {
            if (channel.isConnected()) {
                channel.close();
            }
        } catch (IOException x) {
            logger.log(Level.WARNING, "Error during socket close", x);
        }
    }

    @Override
    public String toString() {

        return descriptor.toString()+";"+channel.socket().getLocalPort();
    }

    private synchronized short addListener(ResponseListener listener) {
        short handle = (short) handles.nextClearBit(0);
        handles.set(handle);
        responseMap.put(handle, listener);
        return handle;
    }

    private synchronized void removeListener(short id) {
        responseMap.remove(id);
        handles.clear(id);
    }

    private void sendMessage(short id, Message message) throws IOException {
        bytesSent += message.send(id, channel);
        lastActive.setTime(System.currentTimeMillis());
    }

    private void handleResponse() throws IOException {
        int status = response.getStatus();
        final Short handle = response.getHandle();
        final ResponseListener handler;
        lastActive.setTime(System.currentTimeMillis());
        synchronized (Multiplexor.this) {
            handler = responseMap.get(handle);
        }

        if (handler == null && status != XrootdProtocol.kXR_attn) {
            if (status == XrootdProtocol.kXR_error) {
                int rc = response.readInt();
                String message = response.getDataAsString();
                logger.log(Level.SEVERE, this + " Out-of-band error " + rc + ": " + message);
                return; // Just carry on in this case??
            }
            throw new IOException(this + " No handler found for handle " + handle + " (status=" + status + ")");
        }
        switch (status) {
            case XrootdProtocol.kXR_error:
                int rc = response.readInt();
                String message = response.getDataAsString();
                handler.handleError(new IOException("Xrootd error " + rc + ": " + message));
                removeListener(handle);
                break;

            case XrootdProtocol.kXR_wait:
                int seconds = response.readInt();
                message = response.getDataAsString();
                logger.info(this + " wait: " + message + " seconds=" + seconds);
                handler.reschedule(seconds, TimeUnit.SECONDS);
                removeListener(handle);
                break;

            case XrootdProtocol.kXR_waitresp:
                seconds = response.readInt();
                message = response.getDataAsString();
                logger.fine(this + " waitresp: " + message + " seconds=" + seconds);
                break;

            case XrootdProtocol.kXR_redirect:
                int port = response.readInt();
                String host = response.getDataAsString();
                logger.fine(this + " redirect: " + host + " " + port);
                handler.handleRedirect(host, port);
                removeListener(handle);
                break;

            case XrootdProtocol.kXR_attn:
                int code = response.readInt();
                if (code == XrootdProtocol.kXR_asynresp) {
                    response.readInt(); // reserved
                    response.regurgitate();
                    handleResponse();
                    return;
                } else {
                    throw new IOException("Xrootd: Unimplemented asycn message received: " + code);
                }

            case XrootdProtocol.kXR_ok:
            case XrootdProtocol.kXR_oksofar:
                handler.handleResponse(response);
                if (response.isComplete()) {
                    removeListener(handle);
                }
                break;

            default:
                throw new IOException("Xrootd: Unimplemented status received: " + status);
        }
    }

    private void handleSocketException(IOException x) {
        if (!socketClosed) {
            logger.log(Level.WARNING, this + " Unexpected IO exception on socket", x);
            close();
            // Notify anyone listening for a response that we are dead
            for (ResponseListener listener : responseMap.values()) {
                logger.fine(this + " sending handleSocketError to " + listener);
                listener.handleSocketError(x);
            }
            responseMap.clear();
        }
    }

    private class SocketReader implements Runnable {

        public void run() {
            try {
                channel.connect(descriptor.getSocketAddress());
                sendInitialHandshake();

                for (; !thread.isInterrupted();) {

                    bytesReceived += response.read();
                    handleResponse();
                }
                logger.log(Level.FINE, this + " multiplexor thread exiting due to interrupt!");
            } catch (IOException x) {
                handleSocketException(x);
            } catch (Throwable x) {
                logger.log(Level.SEVERE, this + " multiplexor thread dead!", x);
            }
        }
    }
}
