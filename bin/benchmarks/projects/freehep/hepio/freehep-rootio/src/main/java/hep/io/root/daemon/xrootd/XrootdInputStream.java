package hep.io.root.daemon.xrootd;

import hep.io.root.daemon.DaemonInputStream;
import java.io.IOException;

/**
 *
 * @author tonyj
 */
public class XrootdInputStream extends DaemonInputStream {

    private static int MAXGETSIZE = -1;
    private byte[] buffer;
    private int bpos = 0;
    private int blen = 0;
    private OpenFile file;
    private Session handle;
    private XrootdURLConnection connection;

    public XrootdInputStream(XrootdURLConnection conn) throws IOException {
        this.connection = conn;
        this.handle = conn.getSession();
        this.file = handle.open(conn.getURL().getFile(), 0, XrootdProtocol.kXR_open_read);
        buffer = new byte[conn.getBufferSize()];
    }

    public int read() throws IOException {
        if (bpos >= blen) {
            if (!fillBuffer()) {
                return -1;
            }
        }
        int i = buffer[bpos++];
        if (i < 0) {
            i += 256;
        }
        return i;
    }

    @Override
    public void close() throws IOException {
        if (handle != null) {
            handle.close(file);
            handle = null;
            if (connection != null) {
                connection.streamClosed();
            }
        }
    }

    @Override
    public int read(byte[] values, int offset, int size) throws IOException {
        if (bpos >= blen) {
            long pos = this.position + bpos;
            int n = size;
            if (MAXGETSIZE > 0 && n > MAXGETSIZE) {
                n = MAXGETSIZE;
            }
            int l = handle.read(file, pos, values, offset, n);
            if (l > 0) {
                this.position += l;
            } else {
                l = -1;
            }
            return l;
        } else {
            int l = Math.min(size, blen - bpos);
            System.arraycopy(buffer, bpos, values, offset, l);
            bpos += l;
            return l;
        }
    }

    public long skip(long skip) throws IOException {
        setPosition(getPosition() + skip);
        return skip;
    }

    public void setPosition(long pos) {
        if (pos > position && pos < position + blen) {
            bpos = (int) (pos - position);
        } else {
            blen = 0;
            bpos = 0;
            super.setPosition(pos);
        }
    }

    @Override
    public int available() throws IOException {
        return blen - bpos;
    }

    private boolean fillBuffer() throws IOException {
        position += bpos;
        bpos = 0;
        int n = buffer.length;
        if (MAXGETSIZE > 0 && n > MAXGETSIZE) {
            n = MAXGETSIZE;
        }
        blen = handle.read(file, position, buffer, 0, n);
        return true;
    }

    @Override
    public long getPosition() {
        return position + bpos;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}