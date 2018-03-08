package hep.io.sio;

import hep.io.xdr.XDRInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * An SIOInputStream provides all the functionality of an XDRInputStream, but adds the
 * ability to do pointer relocation within records.
 */
public class SIOInputStream extends XDRInputStream {

    private Vector map;
    private static final SIORef nullRef = new NullRef();

    SIOInputStream(InputStream in) {
        super(in);
        map = new Vector();
    }

    /**
     * An input stream which shares a map with a parent stream
     */
    public SIOInputStream(InputStream in, SIOInputStream parent) {
        super(in);
        map = parent.map;
    }

    void clear() {
        map.removeAllElements();
    }

    /**
     * Read an SIO PTAG and associate it with an Object o
     */
    public void readPTag(Object o) throws IOException {
        int i = readInt();
        if (i < 0) {
            return; // nothing points to this object
        }
        if (map.size() < i + 1) {
            map.setSize(i + 1);
        }
        map.setElementAt(o, i);
    }

    /**
     * Read an SIO PNTR.
     * @return An SIORef which can be used to access the target object after the entire record has been read
     */
    public SIORef readPntr() throws IOException {
        int i = readInt();
        if (i == 0) {
            return nullRef;
        }
        return new SIORefImpl(i);
    }

    private class SIORefImpl implements SIORef {

        SIORefImpl(int i) {
            index = i;
        }

        public Object getObject() {
            try {
                return map.elementAt(index);
            } catch (Exception x) {
                return null;
            }
        }
        private int index;
    }

    private static class NullRef implements SIORef {

        public Object getObject() {
            return null;
        }
    }

    public void close() throws IOException {
        // NOOP -- should not close underlying IO stream
    }
}