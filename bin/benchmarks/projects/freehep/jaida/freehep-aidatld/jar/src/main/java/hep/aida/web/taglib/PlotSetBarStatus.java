package hep.aida.web.taglib;

import java.util.List;

/*
 * PlotSetStatus.java
 *
 * Created on October 17, 2007, 10:52 AM
 *
 * This class keeps the information about the current status of
 * the PlotSetBarTag processing, mainly href-s to the different pages
 *
 * @author The AIDA Team @ SLAC
 */

public class PlotSetBarStatus {
    private String defaultBar = null;
    private String first    = null;
    private String previous = null;
    private String next     = null;
    private String last     = null;
    private String[] pages  = null;

    public String getDefaultbar() {
        return defaultBar;
    }

    public void setDefaultbar(String defaultBar) {
        this.defaultBar = defaultBar;
    }

    public String getFirst() {
        return first;
    }
    public void setFirst(String first) {
        this.first = first;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String[] getPages() {
        return pages;
    }

    public void setPages(String[] pages) {
        this.pages = pages;
    }

    public int getNpages() {
        return (pages == null) ? 0 : pages.length;
    }

}

