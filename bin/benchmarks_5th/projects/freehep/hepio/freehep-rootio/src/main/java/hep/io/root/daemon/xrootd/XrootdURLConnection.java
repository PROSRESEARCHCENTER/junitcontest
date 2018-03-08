package hep.io.root.daemon.xrootd;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

/**
 * The core class for dealing with root: protocol connections.
 * Currently only supports reading files. Currently only supports
 * plain text (insecure) authorization.
 * @author Tony Johnson
 */
public class XrootdURLConnection extends URLConnection {

    private String username;
    private String password;
    private String auth; // authorization mode to use
    private int bufferSize = 32768;
    private long date;
    private long fSize;
    private int flags;
    private static Logger logger = Logger.getLogger(XrootdURLConnection.class.getName());
    public static final String XROOT_AUTHORIZATION_SCHEME = "scheme";
    public static final String XROOT_AUTHORIZATION_SCHEME_ANONYMOUS = "anonymous";
    public static final String XROOT_AUTHORIZATION_USER = "user";
    public static final String XROOT_AUTHORIZATION_PASSWORD = "password";
    public static final String XROOT_BUFFER_SIZE = "bufferSize";
    private Session session;
    private int openStreamCount;
    private XrootdInputStreamFactory streamFactory;

    XrootdURLConnection(URL url, XrootdInputStreamFactory factory) {
        super(url);
        this.streamFactory = factory;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        connect();
        InputStream stream = streamFactory.createStream(this);
        openStreamCount++;
        return stream;
    }

    public void connect() throws IOException {
        if (connected) {
            return;
        }
        if (auth == null) {
            auth = System.getProperty("root.scheme");
        }
        if (auth != null && auth.equalsIgnoreCase(XROOT_AUTHORIZATION_SCHEME_ANONYMOUS)) {
            username = XROOT_AUTHORIZATION_SCHEME_ANONYMOUS;
            try {
                password = System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getCanonicalHostName();
            } catch (SecurityException x) {
                password = "freehep-user@freehep.org";
            }
        }

        if (username == null) {
            username = System.getProperty("root.user");
        }
        if (password == null) {
            password = System.getProperty("root.password"); 
        }
        // Check for username password, if not present, and if allowed, prompt the user.
        if ((password == null || username == null) && getAllowUserInteraction()) {
            int port = url.getPort();
            if (port == -1) {
                port = XrootdProtocol.defaultPort;
            }
            PasswordAuthentication pa = Authenticator.requestPasswordAuthentication(url.getHost(), null, port, "root", "Username/Password required", auth);
            if (pa != null) {
                username = pa.getUserName();
                password = new String(pa.getPassword());
            }
        }

        if (password == null || username == null) {
            throw new IOException("Authorization Required");
        }
        logger.fine("Opening rootd connection to: " + url);
        Destination dest = new Destination(url.getHost(), url.getPort(), username);
        session = new Session(dest);
        try {
            FileStatus status = session.stat(url.getFile());
            fSize = status.getSize();
            flags = status.getFlags();
            date = status.getModTime().getTime();
            // Prepare to do a checksum
            // FIXME: The file location may contain the original redirector, which may result
            // in the checksum being sent to the redirector
            if (!dest.equals(status.getFileLocation())) {
                session.close();
                session = new Session(status.getFileLocation());
            }
            connected = true;
        } catch (IOException t) {
            disconnect();
            throw t;
        }
    }

    public void disconnect() throws IOException {
        if (session != null) {
            session.close();
            session = null;
        }
        connected = false;
    }

    @Override
    public int getContentLength() {
        if (session == null) {
            return -1;
        }
        return (int) fSize;
    }

    public long getLongContentLength() {
        if (session == null) {
            return -1;
        }
        return fSize;
    }

    @Override
    public long getLastModified() {
        if (session == null) {
            return 0;
        }
        return date;
    }

    @Override
    public long getDate() {
        return getLastModified();
    }

    public long getCheckSum() throws IOException {
        if (session == null) {
            return -1;
        } else {
            String result = session.query(XrootdProtocol.kXR_Qcksum, url.getFile());
            String[] split = result.split(" ");
            return Long.parseLong(split[1]);
        }
    }

    @Override
    public void setRequestProperty(String key, String value) {
        if (key.equalsIgnoreCase(XROOT_AUTHORIZATION_USER)) {
            username = value;
        } else if (key.equalsIgnoreCase(XROOT_AUTHORIZATION_PASSWORD)) {
            password = value;
        } else if (key.equalsIgnoreCase(XROOT_AUTHORIZATION_SCHEME)) {
            auth = value;
        } else if (key.equalsIgnoreCase(XROOT_BUFFER_SIZE)) {
            bufferSize = Integer.parseInt(value);
        }
    }

    int getBufferSize() {
        return bufferSize;
    }

    Session getSession() {
        return session;
    }

    void streamClosed() throws IOException {
        openStreamCount--;
        if (openStreamCount == 0) {
            disconnect();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }
}
