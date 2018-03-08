package org.freehep.util.parameterdatabase.selector;

import java.awt.BasicStroke;
import java.util.Iterator;
import java.util.Vector;

public class LineSizeSelector extends Selector {

    private static int numLines = 3;

    private static Vector selectors = new Vector(numLines);

    protected static BasicStroke thinStroke = new BasicStroke(1.f,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    protected static BasicStroke normalStroke = new BasicStroke(2.f,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    protected static BasicStroke thickStroke = new BasicStroke(3.f,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    // These selectors describe the available sizes.
    public static LineSizeSelector THIN = new LineSizeSelector("Thin",
            thinStroke);

    public static LineSizeSelector NORMAL = new LineSizeSelector("Normal",
            normalStroke);

    public static LineSizeSelector THICK = new LineSizeSelector("Thick",
            thickStroke);

    // Statically initialize these vectors. This must be done before anything
    // else because the methods which access these data are called from the
    // public constructors.
    static {
        selectors.add(THIN);
        selectors.add(NORMAL);
        selectors.add(THICK);
    }

    protected LineSizeSelector(String tag, Object value) {
        super(tag, value);
    }

    public LineSizeSelector(Object value) {
        super(value);
    }

    public LineSizeSelector(String tag) {
        super(tag);
    }

    public Iterator iterator() {
        return selectors.iterator();
    }

}
