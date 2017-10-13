package hep.io.sio;

import hep.io.xdr.XDROutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * An SIOOutputStream provides all the functionality of an XDROutputStream but
 * adds the capability of writing pointers to objects.
 * @author tonyj
 * @version $Id: SIOOutputStream.java 13673 2009-10-16 23:56:44Z tonyj $
 */
public class SIOOutputStream extends XDROutputStream {

    private Map map = new HashMap();

    SIOOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Write a pointer to an object
     * @param obj The object pointed to
     * @throws IOException If an errors occurs
     */
    public void writePntr(Object obj) throws IOException {
        if (obj == null) {
            super.writeInt(0);
        } else {
            //TODO: This map could be handled much more efficiently.
            Integer i = (Integer) map.get(obj);
            if (i == null) {
                map.put(obj, i = new Integer(map.size() + 1));
            }
            super.writeInt(i.intValue());
        }
    }

    /**
     * Write a PTag for a given object.
     * @param tag The object to be tagged.
     * @throws IOException If an exception occurs
     */
    public void writePTag(Object tag) throws IOException {
        Integer i = (Integer) map.get(tag);
        if (i == null) {
            map.put(tag, i = new Integer(map.size() + 1));
        }
        super.writeInt(i.intValue());
    }

    void clear() {
        map.clear();
    }
}