package org.freehep.util.parameterdatabase.selector;

import java.util.Iterator;
import java.util.Vector;

public class OnOffSelector extends Selector {

    private static Vector selectors = new Vector(2);

    /**
     * This Selector describes the "on" state.
     */
    public static OnOffSelector ON = new OnOffSelector("on", Boolean.TRUE);

    /**
     * This Selector describes the "off" state.
     */
    public static OnOffSelector OFF = new OnOffSelector("off", Boolean.FALSE);

    // Statically initialize these vectors. This must be done before anything
    // else because the methods which access these data are called from the
    // public constructors.
    static {
        selectors.add(ON);
        selectors.add(OFF);
    }

    protected OnOffSelector(String tag, Object value) {
        super(tag, value);
    }

    public OnOffSelector(Object value) {
        super(value);
    }

    public OnOffSelector(String tag) {
        super(tag);
    }

    public Iterator iterator() {
        return selectors.iterator();
    }

}
