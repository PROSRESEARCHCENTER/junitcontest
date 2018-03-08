package org.freehep.application.mdi;

import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import org.freehep.application.Application;

import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.commanddispatcher.CommandTargetManager;

/**
 * Base class for managers responsible for handling sets of pages within an application.
 * 
 * <h4>Notes on event dispatching.</h4><ul>
 * <li>Data structures maintained by this class: list of listeners, list of pages, mapping between pages and contexts, selected page.</li>
 * <li>{@code openPage(...)} methods create a context and update data structures, but they do not notify listeners.</li>
 * <li>{@code firePageOpened(...)} method notifies listeners.</li>
 * <li>{@code fireSelectionChanged(...)} method notifies listeners and updates selected page field.</li>
 * <li>{@code close(...)} method notifies listeners and updates data structures.</li>
 * <li>{@code firePageEvent(...)} method sends the event to explicit listeners (does not call {@link ManagedPage} methods.</li>
 * </ul>
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: PageManager.java 16417 2016-09-30 21:31:39Z onoprien $
 */
public abstract class PageManager {

    /**
     * Called whenever the page manager becomes, or ceases to be, in use. Can be
     * used to allocated and/or free-up resources used by the page manager.
     *
     * @param active {@code true} if this page manager is becoming active,
     *               {@code false} if it is ceasing to be active
     */
    protected void setActive(boolean active) {
    }

    /**
     * Initializes data structures maintained by this {@code PageManager}.
     * 
     * @param pages List of pages to managed. The list will be owned by this instance.
     * @param selected Currently selected page.
     */
    protected void init(List<PageContext> pages, PageContext selected) {
        this.pages = pages;
        this.selected = selected;
        page2context = new IdentityHashMap<>();
        for (PageContext context : pages) {
            context.setPageManager(this);
            page2context.put(context.getPage(), context);
        }
    }

    /**
     * Submits a new page to this page manager.
     * 
     * @param page Graphical component to be handled by this page manager.
     * @param title Name for the page.
     * @param icon Icon for the page.
     * @return {@code PageContext} instance that can be used to handle the submitted page.
     */
    public PageContext openPage(Component page, String title, Icon icon) {
        return openPage(page, title, icon, null);
    }

    /**
     * Submits a new page to this page manager.
     * 
     * @param page Graphical component to be handled by this page manager.
     * @param title Name for the page.
     * @param icon Icon for the page.
     * @param type Type of the page.
     * @return {@code PageContext} instance that can be used to handle the submitted page.
     */
    public PageContext openPage(Component page, String title, Icon icon, String type) {
        return openPage(page, title, icon, null, true);
    }
    
    /**
     * Submits a new page to this page manager.
     * 
     * @param page Graphical component to be handled by this page manager.
     * @param title Name for the page.
     * @param icon Icon for the page.
     * @param type Type of the page.
     * @param selectOnOpen If {@code true}, the page will be selected and displayed.
     *                     Implementation provided by this class ignores this parameter.
     * @return {@code PageContext} instance that can be used to handle the submitted page.
     */
    public PageContext openPage(Component page, String title, Icon icon, String type, boolean selectOnOpen) {
        PageContext context = new PageContext(page, title, icon, type);
        context.setPageManager(this);
        pages.add(context);
        page2context.put(page, context);
        return context;
    }

    protected void firePageOpened(PageContext context) {
        ManagedPage mp = getManagedPage(context.getPage());
        if (mp != null) {
            mp.setPageContext(context);
        }
        firePageEvent(context, PageEvent.PAGEOPENED);
        getCommandProcessor().setChanged();
    }

    protected void fireSelectionChanged(PageContext context) {
        if (selected != context) {
            if (selected != null) {
                ManagedPage mp = getManagedPage(selected.getPage());
                if (mp != null) {
                    mp.pageDeselected();
                }
                firePageEvent(selected, PageEvent.PAGEDESELECTED);
            }
            selected = context;
            if (context != null) {
                ManagedPage mp = getManagedPage(selected.getPage());
                if (mp != null) {
                    mp.pageSelected();
                }
                firePageEvent(selected, PageEvent.PAGESELECTED);
            }
            getCommandProcessor().setChanged();
        }
    }

    /**
     * Closes a page handled by this page manager.
     * 
     * @param page Page to be closed.
     * @return {@code true} if the page has been successfully closed;
     *         {@code false} if the managed page canceled the closure. 
     */
    protected boolean close(PageContext page) {
        ManagedPage mp = getManagedPage(page.getPage());
        if (mp != null && !mp.close()) {
            return false;
        }
        pages.remove(page);
        page2context.remove(page.getPage());
        if (page == selected) {
            fireSelectionChanged(null);
        }
        if (mp != null) {
            mp.pageClosed();
        }
        firePageEvent(page, PageEvent.PAGECLOSED);
        getCommandProcessor().setChanged();
        return true;
    }

    protected void firePageEvent(PageContext context, int id) {
        PageEvent event = new PageEvent(context, id);
        if (listenerList != null) {
            PageListener[] listeners = (PageListener[]) listenerList.getListeners(PageListener.class);

            for (int i = 0; i < listeners.length; i++) {
                listeners[i].pageChanged(event);
            }
        }
        context.firePageEvent(event, id);
    }

    /** Makes the specified page selected and visible. */
    protected abstract void show(PageContext page);

    protected abstract void titleChanged(PageContext page);

    protected abstract void iconChanged(PageContext page);

    protected abstract Component getEmbodiment();

    public boolean closeAll() {
        return closeAll(null);
    }

    private boolean closeAll(String type) {
        Iterator i = new ArrayList(pages).iterator();
        while (i.hasNext()) {
            PageContext tmpPage = (PageContext) i.next();
            if (type == null || tmpPage.type().equals(type)) {
                if (!close(tmpPage)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean closeOther(PageContext page) {
        return closeOther(page, false);
    }

    private boolean closeOther(PageContext page, boolean byType) {
        Iterator i = new ArrayList(pages).iterator();
        while (i.hasNext()) {
            PageContext tmpPage = (PageContext) i.next();
            if (tmpPage != page) {
                if (!byType || tmpPage.type().equals(page.type())) {
                    if (!close(tmpPage)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int getPageCount() {
        return pages.size();
    }

    private int getPageCount(String type) {
        if (type == null) {
            return getPageCount();
        }
        int count = 0;
        Iterator i = new ArrayList(pages).iterator();
        while (i.hasNext()) {
            PageContext tmpPage = (PageContext) i.next();
            if (tmpPage.type().equals(type)) {
                count++;
            }
        }
        return count;
    }

    public PageContext getSelectedPage() {
        return selected;
    }

    public List<PageContext> pages() {
        return pages;
    }

    static ManagedPage getManagedPage(Component c) {
        if (c instanceof ManagedPage) {
            return (ManagedPage) c;
        } else if (c instanceof JScrollPane) {
            Component cc = ((JScrollPane) c).getViewport().getView();
            if (cc instanceof ManagedPage) {
                return (ManagedPage) cc;
            }
        }
        return null;
    }
    
    public PageContext getContext(Component page) {
        return page2context.get(page);
    }

    /**
     * Add a page listener to receive notifications of user initiated changes
     *
     * @param listener The PageListener to install
     */
    public void addPageListener(PageListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(PageListener.class, listener);
    }

    /**
     * Remove a previously installed PageListener
     *
     * @param listener The PageListener to remove
     */
    public void removePageListener(PageListener listener) {
        listenerList.remove(PageListener.class, listener);
    }

    List<PageListener> getPageListenerList() {
        if (listenerList == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(listenerList.getListeners(PageListener.class));
        }
    }

    void removeAllPageListeners() {
        listenerList = null;
    }

    protected CommandProcessor createCommandProcessor() {
        return new PageManagerCommandProcessor();
    }

    protected CommandProcessor getCommandProcessor() {
        if (commandProcessor == null) {
            commandProcessor = createCommandProcessor();
        }
        return commandProcessor;
    }

    protected JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {

        CommandTargetManager cm = Application.getApplication().getCommandTargetManager();
        ((MDIApplication) Application.getApplication()).setSelectedPageManager(this);

        String pageType = getSelectedPage() != null ? getSelectedPage().type() : null;
        boolean hasType = pageType != null;

        JComponent closeMenu = new JMenu("Close");

        if (menu.getComponentCount() > 0) {
            menu.addSeparator();
        } else {
            closeMenu = menu;
        }

        JMenuItem close = new JMenuItem(makeTitle("Close", false));
        close.setActionCommand("closePage");
        cm.add(new CommandSourceAdapter(close));
        closeMenu.add(close);

        JMenuItem closeAll = new JMenuItem(makeTitle("Close All", true));
        closeAll.setActionCommand("closeAllPages");
        cm.add(new CommandSourceAdapter(closeAll));
        closeMenu.add(closeAll);

        if (hasType) {
            JMenuItem closeAllByType = new JMenuItem(makeTitle("Close All " + pageType, true));
            closeAllByType.setActionCommand("closeAllPagesByType");
            cm.add(new CommandSourceAdapter(closeAllByType));
            closeMenu.add(closeAllByType);
        }

        JMenuItem closeOther = new JMenuItem(makeTitle("Close Other", true));
        closeOther.setActionCommand("closeOtherPages");
        cm.add(new CommandSourceAdapter(closeOther));
        closeMenu.add(closeOther);

        if (hasType) {
            JMenuItem closeOtherByType = new JMenuItem(makeTitle("Close Other " + pageType, true));
            closeOtherByType.setActionCommand("closeOtherPagesByType");
            cm.add(new CommandSourceAdapter(closeOtherByType));
            closeMenu.add(closeOtherByType);
        }

        if (menu != closeMenu) {
            menu.add(closeMenu);
        }
        return menu;
    }

    private String makeTitle(String title, boolean plural) {
        if (pageManagerType() == null) {
            return title;
        }
        title = title + " " + pageManagerType();
        if (plural) {
            title += "s";
        }
        return title;
    }

    protected String pageManagerType() {
        return pageManagerType;
    }

    public final void setPageManagerType(String type) {
        pageManagerType = type;
    }

    public class PageManagerCommandProcessor extends CommandProcessor {

        public void onCloseAllPages() {
            closeAll();
        }

        public void enableCloseAllPages(CommandState state) {
            state.setEnabled(getPageCount() > 1);
        }

        public void onClosePage() {
            close(getSelectedPage());
        }

        public void enableClosePage(CommandState state) {
            state.setEnabled(getSelectedPage() != null);
        }

        public void onCloseOtherPages() {
            closeOther(getSelectedPage());
        }

        public void enableCloseOtherPages(CommandState state) {
            state.setEnabled(getPageCount() > 1);
        }

        public void onCloseAllPagesByType() {
            closeAll(getSelectedPage().type());
        }

        public void enableCloseAllPagesByType(CommandState state) {
            state.setEnabled(getPageCount(getSelectedPage().type()) > 1);
        }

        public void onCloseOtherPagesByType() {
            closeOther(getSelectedPage(), true);
        }

        public void enableCloseOtherPagesByType(CommandState state) {
            state.setEnabled(getPageCount(getSelectedPage().type()) > 1);
        }
    }
    
    private String pageManagerType;
    private CommandProcessor commandProcessor;
    protected EventListenerList listenerList;
    private List<PageContext> pages = new ArrayList<>();
    private PageContext selected;
    private Map<Component,PageContext> page2context = new IdentityHashMap<>();
}
