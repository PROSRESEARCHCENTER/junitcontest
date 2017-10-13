package hep.io.root.daemon.xrootd;

import hep.io.root.daemon.xrootd.Callback.DefaultCallback;

/**
 * Ping a server.
 * @author tonyj
 */
class PingOperation extends Operation<Void> {

    PingOperation() {
        super("ping", new Message(XrootdProtocol.kXR_ping), new DefaultCallback());
    }
}
