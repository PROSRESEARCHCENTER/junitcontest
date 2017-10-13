package org.freehep.application.mdi;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.freehep.application.Application;
import org.freehep.application.PropertyUtilities;
import static org.freehep.application.studio.Plugin.GROUP_PROPERTY;
import static org.freehep.application.studio.Plugin.PLUGIN_PROPERTY;
import org.freehep.util.commanddispatcher.BooleanCommandState;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.xml.menus.XMLMenuBuilder;

/**
 * Extends Application to provide MDI facilities. An MDI application controls
 * three types of sub-windows <ul> <li>Pages <li>Consoles <li>Controls </ul>
 * There is one PageManager for each type of window. Different PageManagers can
 * be installed dynamically.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: MDIApplication.java 16396 2016-04-18 00:37:38Z onoprien $
 */
public class MDIApplication extends Application {
    
// -- Fields : -----------------------------------------------------------------
    
    public static final int TOOLBAR_DEFAULT = 0;
    public static final int TOOLBAR_INVISIBLE = 1;
    public static final int TOOLBAR_VISIBLE = 2;
    public static final int TOOLBAR_AUTO = 3;
    public static final int TOOLBAR_PROGRAM = 4;

    private PageListener pageListener = new MDIPageListener();
    private PageManager pageManager;
    private PageManager controlManager;
    private PageManager consoleManager;
    private JSplitPane consoleSplit;
    private JSplitPane controlSplit;
    private ToolBarHolder mdiToolBarHolder;
    private PageManager selectedPageManager;

    
// -- Life cycle : -------------------------------------------------------------

    public MDIApplication(String appName) {
        super(appName);

        // By default only the page area is visible
        add(getPageManager().getEmbodiment());
    }
    
    
// -- Handling properties : ----------------------------------------------------

    @Override
    protected void loadDefaultProperties(Properties app) throws IOException {
        super.loadDefaultProperties(app);

        InputStream in = MDIApplication.class.getResourceAsStream("MDIDefault.properties");
        app.load(in);
        in.close();
    }

    @Override
    protected void saveUserProperties() {
        Properties user = getUserProperties();
        if (consoleSplit != null) {
            PropertyUtilities.setInteger(user, "consoleSize", consoleSplit.getHeight() - consoleSplit.getDividerLocation());
        }
        if (controlSplit != null) {
            PropertyUtilities.setInteger(user, "controlSize", controlSplit.getDividerLocation());
        }
        if (mdiToolBarHolder != null) {
            mdiToolBarHolder.save(user);
        }
        super.saveUserProperties();
    }
    
    
// -- Toolbars, etc. : ---------------------------------------------------------

    /**
     * Adds a new toolbar to the toolbar area. You can attach any number of
     * toolbars to an MDIApplication. Normally the user gets to choose which
     * toolbars are visible or not, but this is controlled by the toolbar mode,
     * which can be one of: <ul> <li>TOOLBAR_INVISIBLE - Toolbar is not shown
     * <li>TOOLBAR_VISIBLE - Toolbar is shown <li>TOOLBAR_AUTO - Toolbar is
     * shown only when at least one button is enabled <li>TOOLBAR_PROGRAM -
     * Toolbar visibility is controlled by the program, not the user (not
     * recommended) </ul> The user can override the default toolBar more, unless
     * it is set to TOOLBAR_PROGRAM. If mode is omitted, or set to
     * TOOLBAR_DEFAULT, it is controlled by the application properties.
     *
     * @param toolBar The JToolBar to add
     * @param name The name of the toolbar, used for user menus and the app/user
     * properties file
     * @param mode The mode for this toolbar
     */
    public void addToolBar(JToolBar toolBar, String name, int mode) {
        if (mdiToolBarHolder == null) {
            mdiToolBarHolder = new ToolBarHolder();
            getToolBarHolder().add(mdiToolBarHolder, BorderLayout.NORTH);
        }
        if (mode == TOOLBAR_DEFAULT) {
            mode = PropertyUtilities.getInteger(getUserProperties(), "ToolBar." + name, mode);
        }
        mdiToolBarHolder.add(toolBar, name, mode);
    }

    public void addToolBar(JToolBar toolBar, String name) {
        addToolBar(toolBar, name, TOOLBAR_DEFAULT);
    }

    public void removeToolBar(JToolBar toolBar) {
        mdiToolBarHolder.remove(toolBar);
    }
    
    public void addMenu(JMenuItem item, long location) {
        JMenuBar bar = getMenuBar();
        String loc = String.valueOf(location);
        addMenu(bar, loc, item);
    }

    static private void addMenu(Container parent, String loc, JMenuItem item) {
        
        int l = loc.length() % 3;
        if (l == 0) {
            l = 3;
        }
        int ll = Integer.parseInt(loc.substring(0, l));

        Component[] c = parent instanceof JMenu ? ((JMenu) parent).getPopupMenu().getComponents() : parent.getComponents();
        for (int i = 0; i < c.length; i++) {
            Component comp = c[i];
            if (comp instanceof JComponent) {
                JComponent child = (JComponent) comp;
                Object location = child.getClientProperty(XMLMenuBuilder.LOCATION_PROPERTY);
                if (!(location instanceof Integer)) {
                    continue;
                }
                int locat = (Integer) location;
                if (locat == ll) {
                    String remainder = loc.substring(l);
                    if (remainder.length() > 0 && child instanceof Container) {
                        addMenu((Container) comp, remainder, item);
                        return;
                    } else {
                        throw new RuntimeException("Invalid location for addMenu");
                    }
                } else if (locat > ll) {
                    ((Container) parent).add(item, i);
                    item.putClientProperty(XMLMenuBuilder.LOCATION_PROPERTY, ll);
                    return;
                }
            }
        }
        ((Container) parent).add(item);
        item.putClientProperty(XMLMenuBuilder.LOCATION_PROPERTY, ll);
    }

    /**
     * Adds an item to the application menu bar.
     * <p>
     * The location strings specify a hierarchy of menus above the added item. Each
     * string should be in the {@code name[[:group]:position]} format, where
     * {@code name} is the text label of the menu the item should be added to,
     * {@code group} is the ordinal number of the item group inside the menu, and 
     * {@code position} is the position inside the group.
     * The first location string can also be prefixed with the desired location 
     * on the menu bar: {@code [barPosition:]name[[:group]:position]}.
     * 
     * The menu is divided into groups by separators.
     * If the group number is negative, items added with the same group number by 
     * different plugins are put into the same group. If the group is not specified
     * or is equal to 0, the item is added to the default group. The default group
     * is shared by all plugins.
     * 
     * @param plugin Name of the plugin that adds this menu item.
     * @param action Action for the menu item to be added.
     * @param locations Strings that specifies locations of the added item at all levels in the menu hierarchy.
     */
    public void addMenu(Action action, String plugin, String... locations) {
        
        // Strip location on menu bar from the first location string
        
        if (locations.length == 0) {
            throw new IllegalArgumentException("At least top level menu should be specified.");
        }
        
        JMenuBar bar = getMenuBar();
        int pos = locations[0].indexOf(':');
        if (pos != -1) {
            String s = locations[0].substring(0, pos);
            try {
                int i = Integer.parseInt(s);
                locations[0] = locations[0].substring(pos+1);
                pos = i;
            } catch (NumberFormatException|IndexOutOfBoundsException x) {
                pos = -1;
            }
        }
        
        // Convert array of String to array of Location:
        
        Location[] locs = new Location[locations.length];
        for (int i=0; i<locs.length; i++) {
            String[] tokens = locations[i].split(":");
            Location loc = new Location();
            locs[i] = loc;
            loc.name = tokens[0];
            switch (tokens.length) {
                case 1:
                    loc.group = 0;
                    loc.position = 0;
                    break;
                case 2:
                    loc.group = 0;
                    loc.position = Integer.parseInt(tokens[1]);
                    break;
                case 3:
                    loc.group = Integer.parseInt(tokens[1]);
                    loc.position = Integer.parseInt(tokens[2]);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal location string: " + locations[i]);
            }
        }
        
        
        // Find top level menu on menu bar :
        
        JMenu menu = null;
        try {
            menu = (JMenu) findMenu(bar, locs[0].name);
        } catch (ClassCastException x) {
        }
        
        // If no existing top level menu, create one:
        
        if (menu == null) {
            menu = new JMenu(locs[0].name);
            if (pos == -1) {
                bar.add(menu);
            } else {
                menu.putClientProperty(XMLMenuBuilder.LOCATION_PROPERTY, pos);
                Component[] cc = bar.getComponents();
                boolean notDone = true;
                for (int i=0; i<cc.length; i++) {
                    try {
                        JMenu item = (JMenu) cc[i];
                        Object o = item.getClientProperty(XMLMenuBuilder.LOCATION_PROPERTY);
                        int itemPos = o == null ? 0 : (Integer) o;
                        if (itemPos > pos) {
                            bar.add(menu, i);
                            notDone = false;
                            break;
                        }
                    } catch (ClassCastException x) {
                    }
                }
                if (notDone) {
                    bar.add(menu);
                }
            }
        }
        
        // Add menu items:
        
        addMenu(action, plugin, menu, locs, 1);
        
    }
    
    static private void addMenu(Action action, String plugin, JMenu parent, Location[] locations, int level) {

        boolean finalLevel = level == locations.length;
        String name = finalLevel ? action.toString() : locations[level].name;
        
        JMenuItem levelItem = findMenu(parent, name);
        
        Location loc = locations[level - 1];
        if (levelItem == null) { // creating new menu item at this level
            if (finalLevel) {
                levelItem = action.getValue(Action.SELECTED_KEY) == null ? new JMenuItem(action) : new JCheckBoxMenuItem(action);
            } else {
                levelItem = new JMenu(name);
                addMenu(action, plugin, (JMenu)levelItem, locations, level+1);
            }
            levelItem.putClientProperty(XMLMenuBuilder.LOCATION_PROPERTY, loc.position);
            levelItem.putClientProperty(GROUP_PROPERTY, loc.group);
            levelItem.putClientProperty(PLUGIN_PROPERTY, plugin);
            positionMenu(parent, levelItem, plugin, loc.group, loc.position);
        } else { // adding to existing menu item at this level
            if (finalLevel) {
                throw new IllegalArgumentException("Duplicate menu item: "+ name);
            } else {
                addMenu(action, plugin, (JMenu)levelItem, locations, level+1);
            }
        }
        
    }
    
    static private JMenuItem findMenu(JComponent parent, String name) {
        Component[] components = parent instanceof JMenu ? ((JMenu) parent).getPopupMenu().getComponents() : parent.getComponents();
        for (Component e : components) {
            try {
                JMenuItem menu = (JMenuItem) e;
                if (name.equals(menu.getText())) {
                    return menu;
                }
            } catch (ClassCastException x) {
            }
        }
        return null;
    }
    
    static private void positionMenu(JMenu parent, JMenuItem child, String plugin, int group, int position) {

        int pos = 0;
        int gr = 0;
        String plug = null;
        int index = -1;
        Component[] components = parent.getMenuComponents();
        int n = components.length;
        for (int i = 0; i < n; i++) {
            try {
                JMenuItem item = (JMenuItem) components[i];
                Object o = item.getClientProperty(XMLMenuBuilder.LOCATION_PROPERTY);
                int itemPos = o == null ? 0 : (Integer) o;
                o = item.getClientProperty(GROUP_PROPERTY);
                int itemGroup = o == null ? 0 : (Integer) o;
                String itemPlug = (String) item.getClientProperty(PLUGIN_PROPERTY);
                if ( (Math.abs(itemGroup) > Math.abs(group)) || 
                     (itemGroup == group && (itemGroup <= 0  || plugin.equals(itemPlug)) && itemPos > position) ) {
                    if (index == -1) {
                        parent.insert(child, 0);
                        if (itemGroup != group) {
                            parent.insertSeparator(1);
                        }
                    } else if (itemGroup == group) {
                        parent.insert(child, i);
                    } else if (gr == group) {
                        parent.insert(child, index+1);
                    } else {
                        parent.insert(child, i);
                        parent.insertSeparator(i+1);
                    }
                    break;
                } else {
                    pos = itemPos;
                    gr = itemGroup;
                    plug = itemPlug;
                    index = i;
                }
            } catch (ClassCastException x) { // separator
            }
        }        
        
        if (index == n-1) {
            if (n == 0 || (gr == group && (gr <= 0 || plugin.equals(plug)))) {
                parent.add(child);
            } else {
                parent.addSeparator();
                parent.add(child);
            }
        }
        
        
        
    }
    
    private class Location {
        String name;
        int group, position;
    }
    
    
// -- Page management : --------------------------------------------------------

    public final PageManager getPageManager() {
        if (pageManager == null) {
            setPageManager(createPageManager());
        }
        return pageManager;
    }

    public void setPageManager(PageManager manager) {
        if (manager == pageManager) {
            return;
        }
        Component oldEmbodiment = null;
        if (pageManager != null) {
            oldEmbodiment = pageManager.getEmbodiment();
        }
        switchPageManager(pageManager, manager);

        // Now we need to switch the embodiments
        if (pageManager != null) {
            Container parent = oldEmbodiment.getParent();
            int index = 0;
            for (int i = 0; i < parent.getComponentCount(); i++) {
                if (parent.getComponent(i) == oldEmbodiment) {
                    index = i;
                    break;
                }
            }
            parent.remove(index);
            Component page = manager.getEmbodiment();
            updateComponentTreeUI(page); // In case UI has changed 
            parent.add(page, index);
            parent.validate();
            getCommandProcessor().setChanged();
        }
        pageManager = manager;
    }

    public PageManager getControlManager() {
        if (controlManager == null) {
            setControlManager(createControlManager());
        }
        return controlManager;
    }

    public void setControlManager(PageManager manager) {
        if (manager == controlManager) {
            return;
        }
        switchPageManager(controlManager, manager);
        controlManager = manager;
    }

    public PageManager getConsoleManager() {
        if (consoleManager == null) {
            setConsoleManager(createConsoleManager());
        }
        return consoleManager;
    }

    public void setConsoleManager(PageManager manager) {
        if (manager == consoleManager) {
            return;
        }
        switchPageManager(consoleManager, manager);
        consoleManager = manager;
    }

    protected PageManager createPageManager() {
        return createManager("pageManager");
    }

    protected PageManager createControlManager() {
        return createManager("controlManager");
    }

    protected PageManager createConsoleManager() {
        PageManager p = createManager("consoleManager");
        p.setPageManagerType("Console");
        return p;
    }

    protected PageManager createManager(String type) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            Class k = loader.loadClass(getUserProperties().getProperty(type));
            return (PageManager) k.newInstance();
        } catch (Exception x) {
            throw new InitializationException("Could not create PageManager: " + type, x);
        }
    }

    private void switchPageManager(PageManager oldManager, PageManager newManager) {
        newManager.setActive(true);
        if (oldManager != null) {
            List listeners = oldManager.getPageListenerList();
            oldManager.removeAllPageListeners();
            List l = oldManager.pages();
            for (Iterator i = listeners.iterator(); i.hasNext();) {
                newManager.addPageListener((PageListener) i.next());
            }
            newManager.init(l, oldManager.getSelectedPage());
            oldManager.setActive(false);
        } else {
            newManager.addPageListener(pageListener);
        }
    }

    public void setSelectedPageManager(PageManager manager) {
        if (manager == selectedPageManager) {
            return;
        }
        if (selectedPageManager != null) {
            getCommandTargetManager().remove(selectedPageManager.getCommandProcessor());
        }
        getCommandTargetManager().add(manager.getCommandProcessor());
        selectedPageManager = manager;
    }

    public PageManager selectedPageManager() {
        return selectedPageManager;
    }

    private class MDIPageListener implements PageListener {

        @Override
        public void pageChanged(PageEvent event) {
            int id = event.getID();
            if (id == PageEvent.PAGEOPENED) {
                PageManager manager = event.getPageContext().getPageManager();
                if (manager.getPageCount() == 1) {
                    if (manager == controlManager) {
                        showControl(true);
                    }
                    if (manager == consoleManager) {
                        showConsole(true);
                    }
                }
            } else if (id == PageEvent.PAGECLOSED) {
                PageManager manager = event.getPageContext().getPageManager();
                if (manager.getPageCount() == 0) {
                    if (manager == controlManager) {
                        showControl(false);
                    }
                    if (manager == consoleManager) {
                        showConsole(false);
                    }
                }
            }
        }
    }

    private void showControl(boolean show) {
        if (show && controlSplit == null) {
            Component pages = pageManager.getEmbodiment();
            Container parent = pages.getParent();
            parent.remove(pages);
            Component control = controlManager.getEmbodiment();
            updateComponentTreeUI(control); // In case UI has changed 
            controlSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, control, pages);
            int pixels = PropertyUtilities.getInteger(getUserProperties(), "controlSize", -1);
            if (pixels > 0) {
                controlSplit.setDividerLocation(pixels);
            }
            parent.add(controlSplit);
            revalidate();
        } else if (!show && controlSplit != null) {
            PropertyUtilities.setInteger(getUserProperties(), "controlSize", controlSplit.getDividerLocation());
            Component pages = pageManager.getEmbodiment();
            Container parent = controlSplit.getParent();
            parent.remove(controlSplit);
            controlSplit.removeAll();
            controlSplit = null;
            parent.add(pages);
            revalidate();
        }
    }

    private void showConsole(boolean show) {
        if (show && consoleSplit == null) {
            Component old = getComponent(0);
            final int height = old.getHeight();
            remove(old);
            final int pixels = PropertyUtilities.getInteger(getUserProperties(), "consoleSize", -1);

            Component console = consoleManager.getEmbodiment();
            updateComponentTreeUI(console); // In case UI has changed 
            consoleSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, old, console) {
                // If the splitpane is not yet layed out, its height will be 0
                // in this case we need to postpone setting the divider location
                // until later.
                int consoleSize = height > 0 ? -1 : pixels;

                @Override
                public void doLayout() {
                    if (consoleSize > 0) {
                        int height = getHeight();
                        setDividerLocation(height - consoleSize);
                        consoleSize = -1;
                    }
                    super.doLayout();
                }
            };
            consoleSplit.setResizeWeight(1.0);
            if (pixels > 0 && height > 0) {
                consoleSplit.setDividerLocation(height - pixels);
            }

            add(consoleSplit);
            revalidate();
        } else if (!show && consoleSplit != null) {
            int consoleSize = consoleSplit.getHeight() - consoleSplit.getDividerLocation();
            PropertyUtilities.setInteger(getUserProperties(), "consoleSize", consoleSize);
            Component old = consoleSplit.getTopComponent();
            remove(consoleSplit);
            consoleSplit.removeAll();
            consoleSplit = null;
            add(old);
            revalidate();
        }
    }
    
    
// -- Command processing : -----------------------------------------------------

    @Override
    protected CommandProcessor createCommandProcessor() {
        return new MDICommandProcessor();
    }

    protected class MDICommandProcessor extends ApplicationCommandProcessor {

        public void onTabbedPanes(boolean state) {
            if (state) {
                setPageManager(new TabbedPageManager());
                getUserProperties().setProperty("pageManager", getPageManager().getClass().getName());
            }
        }

        public void enableTabbedPanes(BooleanCommandState state) {
            state.setEnabled(true);
            state.setSelected(getPageManager() instanceof TabbedPageManager);
        }

        public void onInternalFrames(boolean state) {
            if (state) {
                setPageManager(new InternalFramePageManager());
                getUserProperties().setProperty("pageManager", getPageManager().getClass().getName());
            }
        }

        public void enableInternalFrames(BooleanCommandState state) {
            state.setEnabled(true);
            state.setSelected(getPageManager() instanceof InternalFramePageManager);
        }
    }
}