package hep.io.root.daemon.xrootd;

import java.io.IOException;
import java.io.InputStream;

/**
 * A class that can be overriden to create custom input streams.
 * @author tonyj
 */
public class XrootdInputStreamFactory {

    protected InputStream createStream(XrootdURLConnection connection) throws IOException {
        return new XrootdInputStream(connection);
    }
}
