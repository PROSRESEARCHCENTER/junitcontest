package org.freehep.util.parameterdatabase.selector;

import java.util.Iterator;
import java.util.Vector;

public class MarkerSelector extends Selector {

    private static int numMarkers = 10 + 1;

    private static Vector selectors = new Vector(numMarkers);

    // These selectors describe the various markers.
    // Copied (and in synch with) VectorGraphicsConstants
    public static MarkerSelector NONE = new MarkerSelector("None", new Integer(
            Integer.MIN_VALUE));

    public static MarkerSelector VLINE = new MarkerSelector("Vert. Line",
            new Integer(0));

    public static MarkerSelector HLINE = new MarkerSelector("Horiz. Line",
            new Integer(1));

    public static MarkerSelector PLUS = new MarkerSelector("Plus", new Integer(
            2));

    public static MarkerSelector CROSS = new MarkerSelector("Cross",
            new Integer(3));

    public static MarkerSelector STAR = new MarkerSelector("Star", new Integer(
            4));

    public static MarkerSelector CIRCLE = new MarkerSelector("Circle",
            new Integer(5));

    public static MarkerSelector BOX = new MarkerSelector("Box", new Integer(
            6));

    public static MarkerSelector UP_TRIANGLE = new MarkerSelector(
            "Up. Triangle", new Integer(7));

    public static MarkerSelector DN_TRIANGLE = new MarkerSelector(
            "Dn. Triangle", new Integer(8));

    public static MarkerSelector DIAMOND = new MarkerSelector("Diamond",
            new Integer(9));

    // Statically initialize these vectors. This must be done before anything
    // else because the methods which access these data are called from the
    // public constructors.
    static {
        selectors.add(NONE);
        selectors.add(VLINE);
        selectors.add(HLINE);
        selectors.add(PLUS);
        selectors.add(CROSS);
        selectors.add(STAR);
        selectors.add(CIRCLE);
        selectors.add(BOX);
        selectors.add(UP_TRIANGLE);
        selectors.add(DN_TRIANGLE);
        selectors.add(DIAMOND);
    }

    protected MarkerSelector(String tag, Object value) {
        super(tag, value);
    }

    public MarkerSelector(Object value) {
        super(value);
    }

    public MarkerSelector(String tag) {
        super(tag);
    }

    public Iterator iterator() {
        return selectors.iterator();
    }

}
