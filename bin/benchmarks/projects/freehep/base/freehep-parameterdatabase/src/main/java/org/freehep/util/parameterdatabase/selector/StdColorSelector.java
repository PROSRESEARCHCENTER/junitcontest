package org.freehep.util.parameterdatabase.selector;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

public class StdColorSelector extends Selector {

    private static int numChoices = 13;

    private static Vector selectors = new Vector(numChoices);

    // These selectors describe the various markers.
    public static StdColorSelector RED = new StdColorSelector("Red", Color.red);

    public static StdColorSelector GREEN = new StdColorSelector("Green",
            Color.green);

    public static StdColorSelector BLUE = new StdColorSelector("Blue",
            Color.blue);

    public static StdColorSelector CYAN = new StdColorSelector("Cyan",
            Color.cyan);

    public static StdColorSelector MAGENTA = new StdColorSelector("Magenta",
            Color.magenta);

    public static StdColorSelector YELLOW = new StdColorSelector("Yellow",
            Color.yellow);

    public static StdColorSelector ORANGE = new StdColorSelector("Orange",
            Color.orange);

    public static StdColorSelector PINK = new StdColorSelector("Pink",
            Color.pink);

    public static StdColorSelector WHITE = new StdColorSelector("White",
            Color.white);

    public static StdColorSelector LIGHT_GRAY = new StdColorSelector(
            "LightGray", Color.lightGray);

    public static StdColorSelector GRAY = new StdColorSelector("Gray",
            Color.gray);

    public static StdColorSelector DARK_GRAY = new StdColorSelector("DarkGray",
            Color.darkGray);

    public static StdColorSelector BLACK = new StdColorSelector("Black",
            Color.black);

    // Statically initialize these vectors. This must be done before anything
    // else because the methods which access these data are called from the
    // public constructors.
    static {
        selectors.add(RED);
        selectors.add(GREEN);
        selectors.add(BLUE);
        selectors.add(CYAN);
        selectors.add(MAGENTA);
        selectors.add(YELLOW);
        selectors.add(ORANGE);
        selectors.add(PINK);
        selectors.add(WHITE);
        selectors.add(LIGHT_GRAY);
        selectors.add(GRAY);
        selectors.add(DARK_GRAY);
        selectors.add(BLACK);
    }

    protected StdColorSelector(String tag, Object value) {
        super(tag, value);
    }

    public StdColorSelector(Object value) {
        super(value);
    }

    public StdColorSelector(String tag) {
        super(tag);
    }

    public Iterator iterator() {
        return selectors.iterator();
    }
}
