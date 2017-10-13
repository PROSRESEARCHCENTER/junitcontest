package hep.io.root.daemon.xrootd;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * A URLStreamHandlerFactory for registering the root: protocol
 * <p>
 * Usage:
 * <pre>
 *    URL.setURLStreamHandlerFactory(new XrootdURLStreamFactory());
 *    URL url = new URL("root://root.cern.ch/demo.root");
 * </pre>
 * @author Tony Johnson
 */
public class XrootdURLStreamFactory implements URLStreamHandlerFactory {

    private URLStreamHandler handler;

    public XrootdURLStreamFactory() {
        handler = new XrootdStreamHandler();
    }

    public XrootdURLStreamFactory(XrootdInputStreamFactory factory) {
        handler = new XrootdStreamHandler(factory);
    }

    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("root".equals(protocol) || "xroot".equals(protocol) || "scalla".equals(protocol)) {
            return handler;
        }
        return null;
    }
}
