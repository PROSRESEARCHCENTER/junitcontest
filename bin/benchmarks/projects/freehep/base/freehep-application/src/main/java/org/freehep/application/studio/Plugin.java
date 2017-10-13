package org.freehep.application.studio;

import java.awt.Component;
import java.awt.Container;
import javax.swing.Action;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.freehep.xml.menus.XMLMenuBuilder;

/**
 * A base class for implementing plugins.
 */
public abstract class Plugin {
    
// -- Fields : -----------------------------------------------------------------
    
    static public final Object GROUP_PROPERTY = new Object();
    static public final Object PLUGIN_PROPERTY = new Object();
    private Studio app;
    
    
// -- Life cycle : -------------------------------------------------------------

    /**
     * Called to initialize the plugin. Note that at this time other plugins may
     * not have been loaded, and the GUI may not yet be visible.
     */
    protected void init() throws Throwable {
    }

    /**
     * Called after all plugins have been loaded, but before the GUI has become
     * visible
     */
    protected void postInit() {
    }

    /**
     * Called after all plugins have been loaded, and the GUI has become visible
     */
    protected void applicationVisible() {
    }

    /**
     * Test if the plugin can be shutdown. The default implementation always
     * returns false, override as necessary.
     */
    public boolean canBeShutDown() {
        return false;
    }

    /**
     * Called to shutdown the plugin (if supported)
     */
    protected void shutdown() {
    }

    void stop() {
        shutdown();
        app = null;
    }

    void setContext(Studio app) throws Throwable {
        this.app = app;
        init();
    }
    
    
// -- Getters : ----------------------------------------------------------------

    public Studio getApplication() {
        return app;
    }
    
    
// -- Adding menu items : ------------------------------------------------------

    protected void addMenu(JMenuItem item, long location) {
        app.addMenu(item, location);
    }
    
}
