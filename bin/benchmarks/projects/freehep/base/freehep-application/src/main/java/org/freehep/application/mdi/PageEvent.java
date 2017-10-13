package org.freehep.application.mdi;

import java.awt.AWTEvent;

/**
 * An event delivered to {@link PageListener}s in response to user interaction with a page.
 *
 * @see PageListener
 * @see PageContext
 */
public class PageEvent extends AWTEvent {

    public PageEvent(PageContext source, int id) {
        super(source, id);
    }

    public PageContext getPageContext() {
        return (PageContext) getSource();
    }

    @Override
    public String toString() {
        int tid = getID();
        String sid = String.valueOf(tid);
        if (tid == PAGESELECTED) {
            sid = "PAGESELECTED";
        } else if (tid == PAGEDESELECTED) {
            sid = "PAGEDESELECTED";
        } else if (tid == PAGECLOSED) {
            sid = "PAGECLOSED";
        } else if (tid == PAGEICONIZED) {
            sid = "PAGEICONIZED";
        } else if (tid == PAGEDEICONIZED) {
            sid = "PAGEDEICONIZED";
        } else if (tid == PAGEOPENED) {
            sid = "PAGEOPENED";
        }
        return "PageEvent: ID=" + sid + " Source=" + getSource();
    }
    
    public static final int PAGESELECTED = RESERVED_ID_MAX + 2000;
    public static final int PAGEDESELECTED = RESERVED_ID_MAX + 2001;
    public static final int PAGECLOSED = RESERVED_ID_MAX + 2002;
    public static final int PAGEICONIZED = RESERVED_ID_MAX + 2003;
    public static final int PAGEDEICONIZED = RESERVED_ID_MAX + 2004;
    public static final int PAGEOPENED = RESERVED_ID_MAX + 2005;
}
