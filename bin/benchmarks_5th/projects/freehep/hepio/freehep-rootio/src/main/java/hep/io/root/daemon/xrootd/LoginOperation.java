package hep.io.root.daemon.xrootd;

import hep.io.root.daemon.xrootd.LoginOperation.LoginSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Perform an xrootd login operation
 * @author tonyj
 */
class LoginOperation extends Operation<LoginSession> {

    private static AtomicInteger pseudoPid = new AtomicInteger(1);

    LoginOperation(String user) {
        super("login", new LoginMessage(user), new LoginCallback());
    }

    private static class LoginMessage extends Message {

        LoginMessage(String userName) {
            super(XrootdProtocol.kXR_login);
            writeInt(pseudoPid.getAndIncrement());
            byte[] user;
            try {
                user = userName.getBytes("US-ASCII");
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException("Not possible", ex);
            }
            for (int i = 0; i < 8; i++) {
                writeByte(i < user.length ? user[i] : 0);
            }
            writeByte(0);
            writeByte(0);
            writeByte(XrootdProtocol.kXR_asyncap | XrootdProtocol.XRD_CLIENT_CURRENTVER);
            writeByte(XrootdProtocol.kXR_useruser);
        }
    }

    private static class LoginCallback extends Callback<LoginSession> {

        @Override
        LoginSession responseReady(Response response) throws IOException {
            return new LoginSession(response);
        }
    }

    static class LoginSession {

        private byte[] session;
        private byte[] security;

        LoginSession(Response response) throws IOException {

            int dLen = response.getLength();
            if (dLen > 0) {
                session = new byte[16];
                for (int i = 0; i < session.length; i++) {
                    session[i] = response.getData().get();
                }
                if (dLen > 16) {
                    security = new byte[dLen - 16];
                    for (int i = 0; i < security.length; i++) {
                        security[i] = response.getData().get();
                    }
                }
            }
        }

        public byte[] getSecurity() {
            return security;
        }

        public byte[] getSession() {
            return session;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Session: ");
            for (byte b : session) {
                builder.append(String.format("%02x", b));
            }
            if (security != null) {
                builder.append("  Security: ");
                for (byte b : security) {
                    builder.append(String.format("%02x", b));
                }
            }
            return builder.toString();
        }
    }
}
