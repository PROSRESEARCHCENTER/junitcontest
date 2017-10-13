package org.freehep.application.mdi;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.freehep.application.Application;
import org.freehep.swing.popup.GlobalMouseListener;
import org.freehep.swing.popup.GlobalPopupListener;
import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.commanddispatcher.CommandTargetManager;

/**
 * {@code PageManager} that displays its pages as tabs (when docked) or windows (un-docked).
 *
 * @author onoprien
 * @version $Id: DockPageManager.java 16429 2016-10-03 04:23:23Z onoprien $
 */
public class DockPageManager extends PageManager {
    
// -- Fields : -----------------------------------------------------------------
    
    /** Un-docked pages. */
    protected HashMap<PageContext,PageFrame> frames;
    /** Tabbed pane. */
    protected JTabbedPane tabs;
    /** Listens to selection changes in the tabbed pane. */
    private MouseListener ml;
    /** Vetoes tabbed pane selection listeners if false. */
    private boolean watchTabs;
    
    private int tabPlacement = JTabbedPane.TOP;


// -- Life cycle : -------------------------------------------------------------

    public DockPageManager() {
        setPageManagerType("Page");
    }

    @Override
    protected void setActive(boolean active) {
        if (active) {
            tabs = new CloseButtonTabbedPane() {
                @Override
                protected void fireCloseTabAt(int index) {
                    close(getContext(index));
                }
                @Override
                protected void fireUndockTabAt(int index, int x, int y) {
                    undock(getContext(index), x, y, false);
                }
                @Override
                public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
                    return DockPageManager.this.modifyPopupMenu(menu, source, p);
                }
            };
            tabs.setTabPlacement(tabPlacement);
            ml = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!e.isPopupTrigger()) {
                        fireSelectionChanged();
                    }
                }
            };
            tabs.addMouseListener(ml);
            watchTabs = true;
            frames = new HashMap<>();
        } else {
            tabs.removeAll();
            tabs.removeMouseListener(ml);
            tabs = null;
            frames = null;
        }
    }

    @Override
    protected void init(List<PageContext> pages, PageContext selected) {
        super.init(pages, selected);
        watchTabs = false;
        for (PageContext context : pages) {
            tabs.addTab(hackedTitle(context.getTitle()), context.getIcon(), context.getPage());
            if (context == selected) {
                tabs.setSelectedComponent(context.getPage());
            }
        }
        watchTabs = true;
        if (selected == null && pages.size() > 0) {
            fireSelectionChanged((PageContext) pages().get(tabs.getSelectedIndex()));
        }
    }
    
    
// -- Operations on pages : ----------------------------------------------

    /**
     * Submits a new page to this page manager, to be open in the docked mode.
     * 
     * @param page Graphical component to be handled by this page manager.
     * @param title Name for the page.
     * @param icon Icon for the page.
     * @param type Type of the page.
     * @return {@code PageContext} instance that can be used to handle the submitted page.
     */
    @Override
    public PageContext openPage(Component page, String title, Icon icon, String type, boolean selectOnOpen) {
        PageContext context = super.openPage(page, title, icon, type, selectOnOpen);
        tabs.addTab(hackedTitle(title), icon, page);
        firePageOpened(context);
        if (selectOnOpen) {
            tabs.setSelectedComponent(page);
            fireSelectionChanged();
        }
        return context;
    }

    /**
     * Submits a new page to this page manager, to be open in the un-docked mode.
     * 
     * @param page Graphical component to be handled by this page manager.
     * @param title Name for the page.
     * @param icon Icon for the page.
     * @param type Type of the page.
     * @param selectOnOpen If {@code true}, the page will be selected once open.
     * @param size Requested page size. If {@code null}, the size is chosen based on the page properties.
     * @param location Requested window location. If {@code null}, the window is positioned in the top left corner.
     * @param device The screen ID. If {@code null}, the default screen is used.
     * @param maximized If {@code true}, the window will be maximized.
     * @return {@code PageContext} instance that can be used to handle the submitted page.
     */
    public PageContext openPage(Component page, String title, Icon icon, String type, boolean selectOnOpen,
                                Dimension size, Point location, String device, boolean maximized) {
        PageContext context = super.openPage(page, title, icon, type, selectOnOpen);
        displayUndocked(context, size, location, device, maximized);
        firePageOpened(context);
        if (selectOnOpen) {
            show(context);
        }
        return context;
    }

    /**
     * Closes a page handled by this page manager.
     * 
     * @param context Page to be closed.
     * @return {@code true} if the page has been successfully closed;
     *         {@code false} if the managed page canceled the closure. 
     */
    @Override
    protected boolean close(PageContext context) {
        boolean ok = super.close(context);
        if (ok) {
            PageFrame frame = frames.get(context);
            if (frame == null) {
                tabs.removeTabAt(getTab(context));
            } else {
                frame.setVisible(false);
                frame.dispose();
            }
            if (getSelectedPage() == null) {
                chooseSelection();
            }
        }
        return ok;
    }

    /**
     * Makes the specified page selected and visible.
     * @param context The context to be shown.
     */
    @Override
    protected void show(PageContext context) {
        PageFrame frame = frames.get(context);
        if (frame == null) {
            tabs.setSelectedComponent(context.getPage());
            fireSelectionChanged();
        } else {
            frame.setVisible(true);
            frame.toFront();
            frame.setState(Frame.NORMAL);
        }
    }

    @Override
    protected void titleChanged(PageContext context) {
        PageFrame frame = frames.get(context);
        if (frame == null) {
            tabs.setTitleAt(getTab(context), hackedTitle(context.getTitle()));
        } else {
            frame.setTitle(context.getTitle());
        }
    }

    @Override
    protected void iconChanged(PageContext context) {
        PageFrame frame = frames.get(context);
        if (frame == null) {
            tabs.setIconAt(getTab(context), context.getIcon());
        } else {
            try {
                frame.setIconImage(((ImageIcon)context.getIcon()).getImage());
            } catch (ClassCastException x) {
            }
        }
    }
    
    
// -- Queries on pages : -------------------------------------------------------
    
    public boolean isDocked(Component component) {
        if (component == null) throw new IllegalArgumentException();
        Window w = SwingUtilities.getWindowAncestor(component);
        if (w == null) throw new IllegalArgumentException();
        return ! (w instanceof PageFrame);
    }
    
    
// -- Getters and setters : ----------------------------------------------------

    @Override
    protected Component getEmbodiment() {
        return tabs;
    }

    public void setTabPlacement(int placement) {
        tabPlacement = placement;
        if (tabs != null) {
            tabs.setTabPlacement(placement);
        }
    }

    public int getTabPlacement() {
        return tabPlacement;
    }
    
    
// -- Docking and undocking : --------------------------------------------------
    
    protected void dock(PageContext context) {
        hideUndocked(context);
        displayDocked(context);
    }
    
    /**
     * Un-docks the page.
     * 
     * @param context Page to undock.
     * @param x Absolute horizontal position on screen where the page should be placed.
     * @param y Absolute vertical position on screen where the page should be placed.
     * @param maximized If {@code true} the page frame is maximized.
     */
    protected void undock(PageContext context, int x, int y, boolean maximized) {
        hideDocked(context);
        displayUndocked(context, null, new Point(x,y), null, maximized);
    }

    @Override
    protected JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
        
        menu = super.modifyPopupMenu(menu, source, p);
        
        JComponent dockMenu;
        int n = menu.getComponentCount();
        if (n > 0) {
            Component last = menu.getComponent(n-1);
            if (last instanceof JMenu) {
                dockMenu = new JMenu("Dock");
                menu.add(dockMenu);
            } else {
                dockMenu = menu;
                menu.addSeparator();
            }
        } else {
            dockMenu = menu;
        }
        
        CommandTargetManager cm = Application.getApplication().getCommandTargetManager();

        JMenuItem fullScreen = new JMenuItem("Full Screen");
        fullScreen.setActionCommand("fullScreen");
        cm.add(new CommandSourceAdapter(fullScreen));
        dockMenu.add(fullScreen);

        JMenuItem dock = new JMenuItem("Dock");
        dock.setActionCommand("dock");
        cm.add(new CommandSourceAdapter(dock));
        dockMenu.add(dock);

        JMenuItem dockAll = new JMenuItem("Dock All");
        dockAll.setActionCommand("dockAll");
        cm.add(new CommandSourceAdapter(dockAll));
        dockMenu.add(dockAll);

        return menu;
    }

    @Override
    protected CommandProcessor createCommandProcessor() {
        return new PageManagerCommandProcessor() {

            public void onFullScreen() {
                undock(getSelectedPage(), 0, 0, true);
            }

            public void enableFullScreen(CommandState state) {
                PageContext context = getSelectedPage();
                state.setEnabled(context != null && frames.get(context) == null);
            }

            public void onDock() {
                dock(getSelectedPage());
            }

            public void enableDock(CommandState state) {
                PageContext context = getSelectedPage();
                state.setEnabled(context != null && frames.get(context) != null);
            }

            public void onDockAll() {
                for (PageContext page : pages()) {
                    dock(page);
                }
            }

            public void enableDockAll(CommandState state) {
                state.setEnabled(!frames.isEmpty());
            }
            
        };
    }
    
    
// -- Local methods : ----------------------------------------------------------
    
    protected void displayDocked(PageContext context) {
        if (context == null) return;
        watchTabs = false;
        tabs.addTab(hackedTitle(context.getTitle()), context.getIcon(), context.getPage());
        if (context == getSelectedPage()) {
            tabs.setSelectedComponent(context.getPage());
        }
        watchTabs = true;        
    }
    
    protected void displayUndocked(PageContext context, Dimension size, Point location, String device, boolean maximized) {
        if (context == null) return;
        if (size == null) {
            size = context.getPage().getSize();
        }
        if (location == null) {
            location = new Point();
        }
        PageFrame frame = null;
        if (device == null) {
            frame = new PageFrame(context);
        } else {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
             GraphicsDevice[] devices = ge.getScreenDevices();
             for (GraphicsDevice dev : devices) {
                 String id = dev.getIDstring();
                 GraphicsConfiguration[] configs = dev.getConfigurations();
                 if (device.equals(id)) {
                     frame = new PageFrame(context, dev.getDefaultConfiguration());
                     break;
                 }
             }
             if (frame == null) {
                 frame = new PageFrame(context);
             }
        }
        frame.add(context.getPage(), BorderLayout.CENTER);
        if (maximized) {
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        } else {
            frame.setLocation(location);
        }
        context.getPage().setPreferredSize(size);
        frame.pack();
        frames.put(context, frame);
        frame.setVisible(true);
    }
    
    protected void hideDocked(PageContext context) {
        if (context == null) return;
        int tabIndex = getTab(context);
        if (tabIndex >= 0) {
            watchTabs = false;
            Dimension d = context.getPage().getSize();
            tabs.removeTabAt(tabIndex);
            tabs.repaint();
            watchTabs = true;
        }
    }
    
    protected void hideUndocked(PageContext context) {
        if (context == null) return;
        PageFrame frame = frames.remove(context);
        if (frame == null) return;
        frame.dispose();        
    }

    private String hackedTitle(String title) {
        return title == null ? CloseButtonTabbedPane.TAB_NAME_TRAILING_SPACE : title + CloseButtonTabbedPane.TAB_NAME_TRAILING_SPACE;
    }
    
    /**
     * Chooses and selects a page (called when previously selected page was
     * deselected for some reason other than selection of another page.
     */
    protected void chooseSelection() {
        // is there an already selected tab?
        int index = tabs.getSelectedIndex();
        if (index >= 0) {
            fireSelectionChanged(getContext(index));
            return;
        }
        // is there a tab we can select?
        try {
            tabs.setSelectedIndex(0);
            return;
        } catch (IndexOutOfBoundsException x) {
        }
        // is there any de-iconized frame?
        for (PageFrame pf : frames.values()) {
            if ((pf.getExtendedState() & JFrame.ICONIFIED) == 0) {
                pf.toFront();
                return;
            }
        }
        // nothing will be selected
        fireSelectionChanged(null);
    }

    /**
     * Returns the index of the tab for the specified page.
     * Returns -1 if there is no tab for this page.
     */
    protected int getTab(PageContext context) {
        return tabs.indexOfComponent(context.getPage());
    }
    
    protected PageContext getContext(int tabIndex) {
        return getContext(tabs.getComponentAt(tabIndex));
    }
    
    protected void fireSelectionChanged() {
        if (watchTabs) {
            int index = tabs.getSelectedIndex();
            if (index != -1) {
                fireSelectionChanged(getContext(index));
            }
        }
    }
    
    
// -- Undocked page frame class : ----------------------------------------------

    protected class PageFrame extends JFrame implements HasPopupItems {
        
        private final PageContext context;
        
        private final WindowAdapter wl = new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                fireSelectionChanged(context);
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
                fireSelectionChanged(context);
            }
            @Override
            public void windowIconified(WindowEvent e) {
                chooseSelection();
            }
            @Override
            public void windowClosing(WindowEvent e) {
                close(context);
            }
        };

        PageFrame(PageContext pageContext) {
            super(pageContext.getTitle());
            context = pageContext;
            setAutoRequestFocus(true);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }

        PageFrame(PageContext pageContext, GraphicsConfiguration gc) {
            super(pageContext.getTitle(), gc);
            context = pageContext;
            setAutoRequestFocus(true);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
        
        @Override
        public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
            return DockPageManager.this.modifyPopupMenu(menu, source, p);
        }

        @Override
        public void setVisible(boolean b) {
            if (b) {
                addWindowListener(wl);
                GlobalMouseListener gml = new GlobalMouseListener(this);
                gml.addMouseListener(new GlobalPopupListener());
            } else {
                removeWindowListener(wl);
            }
            super.setVisible(b);
        }

    }

    
}
