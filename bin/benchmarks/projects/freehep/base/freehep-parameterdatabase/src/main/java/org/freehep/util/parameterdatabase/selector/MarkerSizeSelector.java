package org.freehep.util.parameterdatabase.selector;

import java.util.Iterator;
import java.util.Vector;

public class MarkerSizeSelector extends Selector {

    private static int numMarkers = 3;

    private static Vector selectors = new Vector(numMarkers);

    // These selectors describe the available sizes.
    public static MarkerSizeSelector SMALL = new MarkerSizeSelector("Small",
            new Double(3.));

    public static MarkerSizeSelector MEDIUM = new MarkerSizeSelector("Medium",
            new Double(5.));

    public static MarkerSizeSelector LARGE = new MarkerSizeSelector("Large",
            new Double(7.));

    // Statically initialize these vectors. This must be done before anything
    // else because the methods which access these data are called from the
    // public constructors.
    static {
        selectors.add(SMALL);
        selectors.add(MEDIUM);
        selectors.add(LARGE);
    }

    protected MarkerSizeSelector(String tag, Object value) {
        super(tag, value);
    }

    public MarkerSizeSelector(Object value) {
        super(value);
    }

    public MarkerSizeSelector(String tag) {
        super(tag);
    }

    public Iterator iterator() {
        return selectors.iterator();
    }

}
