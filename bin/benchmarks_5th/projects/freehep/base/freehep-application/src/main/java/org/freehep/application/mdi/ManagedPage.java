package org.freehep.application.mdi;

/**
 * An optional interface that can be implemented by pages managed by a {@link PageManager}.
 * A graphical component (page) implementing this interface will have its methods 
 * called by the {@link PageManager} when its state changes. Alternatively, one can
 * register a {@link PageListener} on a {@link PageContext} instance returned by 
 * {@code PageManager.openPage(...)} method when submitting a page to the page manager.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ManagedPage.java 16331 2015-09-10 23:37:57Z onoprien $
 */
public interface ManagedPage {

    /**
     * Called BEFORE the page is closed. Page can veto the close operation by
     * returning false.
     *
     * @return false to veto the page close operation.
     */
    boolean close();

    /**
     * Called after the page is created to set its page context.
     *
     * @param context The PageContext for this page.
     */
    void setPageContext(PageContext context);

    /**
     * Called when this page becomes the current "selected" page.
     */
    void pageSelected();

    /**
     * Called when this page is no longer the "selected" page.
     */
    void pageDeselected();

    /**
     * Called when this page is iconized.
     */
    void pageIconized();

    /**
     * Called when this page is deiconized.
     */
    void pageDeiconized();

    /**
     * Called after this page has been closed.
     */
    void pageClosed();
}
