package org.freehep.application.studio;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.freehep.application.ApplicationEvent;
import org.freehep.application.mdi.DockPageManager;
import org.freehep.application.mdi.InternalFramePageManager;
import org.freehep.application.mdi.MDIApplication;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.mdi.PageManager;
import org.freehep.application.mdi.TabbedPageManager;
import org.freehep.util.FreeHEPLookup;
import org.freehep.util.VersionComparator;
import org.freehep.util.commandline.CommandLine;
import org.freehep.xml.util.ClassPathEntityResolver;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.openide.util.Lookup;

/**
 * Swing application that supports plugins.
 *
 * @author tonyj
 * @version $Id: Studio.java 16337 2015-09-18 01:05:47Z onoprien $
 */
public class Studio extends MDIApplication {
    
// -- Fields : -----------------------------------------------------------------

    private FreeHEPLookup lookup;
    private final EventSender sender = new EventSender();
    private final boolean debugExtensions = System.getProperty("debugExtensions") != null;
    private SAXBuilder builder;
    
    private final ArrayList<PluginInfo> loadedPlugins = new ArrayList<>();
    private ExtensionClassLoader extensionLoader;
    public static final String LOADDIR = "loaddir";
    private volatile boolean isApplicationVisible = false;
    private volatile boolean isApplicationInitialized = false;
    private volatile Throwable atLeastOnePluginFailedToLoad;
    
    private volatile PluginMap pluginMap;
    private HashMap<String,String[]> pluginBlacklist;  // name --> [minVersion, maxVersion]
    
    private volatile String pluginManagerName;
    private volatile PluginFactory pluginFactory;


// -- Construction and initialization : ----------------------------------------

    protected Studio(String name) {
        super(name);
        // For the moment at least we will use JDOM for parsing the plugin.xml files
        builder = new SAXBuilder(true);
        builder.setEntityResolver(new ClassPathEntityResolver("plugin.dtd", Studio.class));

        getLookup().add(new TabbedPageManager(), "Tabbed Panes");
        getLookup().add(new InternalFramePageManager(), "Internal Frames");
        getLookup().add(new DockPageManager(), "Docked Frames");
    }

    private Studio() {
        this("Studio");
    }

    @Override
    protected CommandLine createCommandLine() {
        CommandLine cl = super.createCommandLine();
        // register standard options
        cl.addOption("extdir", null, "directory", "Sets the directory to scan for plugins");
        return cl;
    }

    @Override
    protected void init() {
        super.init();
        setStatusMessage("Loading extensions...");
        loadExtensions();
        // Now load the real page manager
        setStatusMessage("Setting page manager...");
        setPageManager(createRealPageManager());
        reportPluginException();
    }

    /** Called from <tt>init()</tt> to load extensions. */
    private void loadExtensions() {
        
        // Scan for extensions

        getPluginMap();

        // Create the extension Loader.

        extensionLoader = new ExtensionClassLoader(new URL[0]);
        createLookup().setClassLoader(extensionLoader);
        // Make sure the extensionClassLoader is set as the contextClassLoader
        // so that services etc can be looked up in jar files from the extension directory.
        Runnable lola = new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setContextClassLoader(extensionLoader);
            }
        };
        lola.run(); // for the main (this) thread
        SwingUtilities.invokeLater(lola); // for the event dispatch thread
        
        
        // Find extensions on classpath
        
        List<PluginInfo> cpPlugins = new ArrayList<>();
        for (PluginDir dir : PluginDir.values()) {
            String path = "PLUGIN-inf/"+ ( dir == PluginDir.BUILTIN ? "" : (dir.getLabel()+"/") ) +"plugins.xml";
            try {
                Enumeration e = extensionLoader.getResources(path);
                while (e.hasMoreElements()) {
                    URL url = (URL) e.nextElement();
                    List<PluginInfo> plugins = buildPluginList(url.openStream());
                    for (PluginInfo p : plugins) {
                        p.setDirectory(dir);
                        cpPlugins.add(p);
                    }
                }
            } catch (IOException x) {
            }
        }
        
        pluginMap.insertPlugins(cpPlugins);
        
        // Create PluginFactory
        
        String factoryClassName = this.getAppProperties().getProperty("org.freehep.application.studio.pluginfactory");
        if (factoryClassName == null) {
            pluginFactory = new DefaultPluginFactory();
        } else {
            try {
                Class c = this.getClass().getClassLoader().loadClass(factoryClassName);
                pluginFactory = (PluginFactory) c.newInstance();
            } catch (ClassNotFoundException|InstantiationException|IllegalAccessException|ClassCastException x) {
                throw new RuntimeException("Failed to instantiate plugin factory "+ factoryClassName, x);
            }
        }
        
        // Load plugin manager, if present
        
        if (pluginManagerName != null) {
            PluginInfo pluginManager = pluginMap.getActivePlugins().get(pluginManagerName);
            if (pluginManager != null) {
                 loadPlugins(Collections.singletonList(pluginManager));
            }
        }
        
        // Load built-in, then other plugins
        
        Map<String,PluginInfo> plugins = pluginMap.getLoadablePlugins();
        ArrayList<PluginInfo> builtin = new ArrayList<>(plugins.size());
        ArrayList<PluginInfo> others = new ArrayList<>(plugins.size());
        for (PluginInfo plugin : plugins.values()) {
            if (plugin.getDirectory() == PluginDir.BUILTIN) {
                builtin.add(plugin);
            } else {
                others.add(plugin);
            }
        }
        loadPlugins(builtin);
        loadPlugins(others);
    }

    public static void main(final String[] args) {
        try {
            new Studio().createFrame(args).setVisible(true);
        } catch (Throwable t) {
            error(null, "Fatal Error", t);
            System.exit(1);
        }
    }

    
// -- Setters : ----------------------------------------------------------------
    
    /**
     * Makes plugins with specified name and version invisible to this application.
     * Blacklisted plugins will not appear in the list of available plugins, and their
     * descriptors found in the application extension directories will be ignored.
     * As a result, any installed libraries claimed only by blacklisted plugins will
     * be considered unclaimed and may be deleted.
     * <p>
     * This method should be called before {@link #init}.<br/>
     * Versions should be given in <tt>major[[.minor].incremental][-qualifier][-build]</tt> format.
     * 
     * @param name Name of the plugin to be blacklisted.
     * @param minVersion Earliest blacklisted version (<tt>null</tt> if blacklisted range has no low bound).
     * @param maxVersion Latest blacklisted version (<tt>null</tt> if blacklisted range has no high bound).
     */
    final protected void blacklistPlugin(String name, String minVersion, String maxVersion) {
        if (pluginBlacklist == null) {
            pluginBlacklist = new HashMap<String,String[]>();
        }
        pluginBlacklist.put(name, new String[] {minVersion, maxVersion});
    }
    
    /**
     * Sets the plugin manager plugin name.
     * If this method is called prior to the Studio initialization, the plugin with the specified name
     * will be loaded before others. This allows the plugin manager to perform maintenance on installed
     * plugins before loading them.
     */
    final protected void setPluginManagerName(String name) {
        pluginManagerName = name;
    }


// -- Getters : ----------------------------------------------------------------
    
    /** Returns <tt>true</tt> if the specified plugin is blacklisted and should be ignored by this application. */
    public boolean isBlacklisted(PluginInfo plugin) {
        if (pluginBlacklist == null) return false;
        String[] versionRange = pluginBlacklist.get(plugin.getName());
        if (versionRange == null) return false;
        String version = plugin.getVersion();
        String min = versionRange[0];
        String max = versionRange[1];
        try {
            return ! ( (min != null && VersionComparator.compareVersion(version, min) < 0) ||
                       (max != null && VersionComparator.compareVersion(max, version) < 0) );
        } catch (IllegalArgumentException x) {
            return true;
        }
    }
    
    /** Returns <tt>true</tt> if the specified file is on the extension loader classpath. */
    public boolean isLoaded(File file) {
        try {
          URL fileUrl = file.toURI().toURL();
          URL[] classpath = getExtensionLoader().getURLs();
          for (URL url : classpath) {
              if (url.equals(fileUrl)) return true;
          }
        } catch (MalformedURLException x) {
        }
        return false;
    }

    public EventSender getEventSender() {
        return sender;
    }

    public final FreeHEPLookup getLookup() {
        if (lookup == null) lookup = createLookup();
        return lookup;
    }

    @Deprecated
    public String getUserExtensionsDir() {
        return getExtensionsDir(PluginDir.USER);
    }

    @Deprecated
    public String getGroupExtensionsDir() {
        return getExtensionsDir(PluginDir.GROUP);
    }

    @Deprecated
    public String getSystemExtensionsDir() {
        return getExtensionsDir(PluginDir.SYSTEM);
    }
    
    /**
     * Returns a path to the specified extension directory.
     * <p>
     * We look for extensions:<ul>
     *   <li>USER: In the directory specified by the org.freehep.application.studio.user.extensions property
     *   <li>GROUP: In the directory specified by the org.freehep.application.studio.group.extensions property
     *   <li>SYSTEM: In the directory specified by the org.freehep.application.studio.system.extensions property</ul>
     * The following defaults apply if the property is not specified
     *   <li>USER: {user.home}/.FreeHEPPlugins
     *   <li>GROUP: none
     *   <li>SYSTEM: {java.home}/.FreeHEPPlugins</ul>
     */
    public String getExtensionsDir(PluginDir dir) {
        String out = null;
        switch (dir) {
            case SYSTEM:
                out = getAppProperties().getProperty("org.freehep.application.studio.system.extensions", "{java.home}/FreeHEPPlugins");
                break;
            case GROUP:
                out = getAppProperties().getProperty("org.freehep.application.studio.group.extensions");
                break;
            case USER:
                out = getCommandLine().getOption("extdir");
                if (out == null) {
                  out = getAppProperties().getProperty("org.freehep.application.studio.user.extensions", "{user.home}/.FreeHEPPlugins");
                }
                break;
            default:
                return null;

        }
        if (out != null) {
            try {
                out = (new File(out)).getCanonicalPath();
            } catch (IOException x) {
                out = null;
            }
        }
        return out;
    }

    public ExtensionClassLoader getExtensionLoader() {
        return extensionLoader;
    }

    /** Return a list of loaded plugins. */
    public List<PluginInfo> getPlugins() {
        return Collections.unmodifiableList(loadedPlugins);
    }
    
    /** 
     * Returns plugin descriptor from the list of loaded plugins with the specified name.
     * Returns <tt>null</tt> if there is no loaded plugin with matching name.
     */
    public PluginInfo getPlugin(String name) {
        for (PluginInfo plugin : loadedPlugins) {
            if (plugin.getName().equals(name)) return plugin;
        }
        return null;
    }
    
    /** Returns a map of installed plugins. */
    public PluginMap getPluginMap() {
        if (pluginMap == null) {
            pluginMap = new PluginMap(this);
        }
        return pluginMap;
    }
    
    /**
     * Reports the latest exception thrown while loading plugins, if any.
     * Clears stored exception so subsequent calls to this method will do nothing unless 
     * new plugin loading failures happen.
     */
    public void reportPluginException() {
        if (atLeastOnePluginFailedToLoad != null) {
            error("At least one plugin failed to initialize, see Plugin Manager for details", atLeastOnePluginFailedToLoad);
            atLeastOnePluginFailedToLoad = null;
        }
    }
    
    
// -- Operations on plugins : --------------------------------------------------

    /**
     * Stops a plugin.
     * Calls <tt>stop()</tt> method on the specified plugin and removes a reference
     * to a {@link Plugin} object from the <tt>PluginInfo</tt>. 
     * The <tt>PluginInfo</tt> object supplied as an argument is used to identify a loaded
     * plugin with the same name. If no matching loaded plugin is found, this method returns 
     * immediately without doing anything.
     * 
     * @throws IllegalArgumentException if the plugin cannot be shut down.
     */
    public void stopPlugin(PluginInfo plugin) {
        plugin = getPlugin(plugin.getName());
        if (plugin == null) return;
        Plugin plug = plugin.getPlugin();
        if (plug == null || !plug.canBeShutDown()) {
            throw new IllegalArgumentException("Plugin can not be stopped");
        }
        plug.stop();
        plugin.setPlugin(null);
    }

    /**
     * Starts and initializes a plugin.
     * Loads the plugin class with the extension class loader, creates an instance, 
     * and calls its initialization methods
     * The <tt>PluginInfo</tt> object supplied as an argument is used to identify a loaded
     * plugin with the same name. If no matching loaded plugin is found, this method returns 
     * immediately without doing anything.
     * 
     * @throws Throwable Re-throws any exceptions thrown by the plugin class code.
     */
    public void startPlugin(PluginInfo plugin) throws Throwable {
        plugin = getPlugin(plugin.getName());
        if (plugin == null) return;
        getAppProperties().putAll(plugin.getProperties());
        initializePlugin(plugin, extensionLoader);
        if (isApplicationInitialized) {
            Plugin plug = plugin.getPlugin();
            if (plug != null && plugin.getErrorStatus() == null) {
                try {
                    plug.postInit();
                    if (isApplicationVisible) {
                        plug.applicationVisible();
                    }
                } catch (Throwable t) {
                    plugin.setErrorStatus(t);
                    throw t;
                }
            }
        }
        revalidate();
    }

    /** Reads the specified stream and creates a list of <tt>PluginInfo</tt> instances. */
    protected List<PluginInfo> buildPluginList(InputStream in) throws IOException {
        Properties user = getUserProperties();
        List<PluginInfo> result = new ArrayList<PluginInfo>();
        try {
            Document doc = builder.build(in);
            List<Element> rootChildren = doc.getRootElement().getChildren();
            for (Element node : rootChildren) {
                PluginInfo plugin = new PluginInfo(node);
                plugin.loadUserProperties(user);
                result.add(plugin);
                if (debugExtensions) System.out.println("\t\tPlugin: " + plugin.getName());
            }
        } catch (JDOMException x) {
            if (debugExtensions) x.printStackTrace();
        } finally {
            in.close();
        }
        return result;
    }

    /**
     * Loads and initializes the specified plugins using the default extensions class loader.
     * @return True if at least one plugin was not loaded due to presence of an identically named loaded plugin.
     */
    public boolean loadPlugins(Collection<PluginInfo> plugins) {
        return loadPlugins(plugins, getExtensionLoader());
    }

    /**
     * Loads and initializes the specified plugins.
     * @return True if some plugins were not loaded due to conflicts with already loaded plugins.
     */
    public boolean loadPlugins(Collection<PluginInfo> plugins, ExtensionClassLoader loader) {

        // extend classpath

        boolean out = false;
        ArrayList<PluginInfo> okLoaded = new ArrayList<>(plugins.size());
        for (PluginInfo plugin : plugins) {
            if (loadedPlugins.contains(plugin)) {
                out = true;
            } else {
                if (plugin.getErrorStatus() == null) {
                    loadedPlugins.add(plugin);
                    try {
                        Map<String, LibInfo> libs = pluginMap.getActiveLibraries(plugin);
                        for (LibInfo lib : libs.values()) {
                            File file = lib.getFile();
                            if (file != null) loader.addURL(file.toURI().toURL());
                        }
                        getAppProperties().putAll(plugin.getProperties());
                        okLoaded.add(plugin);
//                        if (plugin.hasMainClass()) initializePlugin(plugin, loader);
                    } catch (Throwable t) {
                        plugin.setErrorStatus(t);
                        atLeastOnePluginFailedToLoad = plugin.getName().equals(pluginManagerName) ? t : new Exception("See Plugin Manager for details");
                    }
                } else {
                    atLeastOnePluginFailedToLoad = plugin.getName().equals(pluginManagerName) ? plugin.getErrorStatus() : new Exception("See Plugin Manager for details");
                }
            }
        }
        
        // initialize plugins
        
        for (PluginInfo plugin : okLoaded) {
            try {
                if (plugin.hasMainClass()) {
                    initializePlugin(plugin, loader);
                }
            } catch (Throwable t) {
                plugin.setErrorStatus(t);
                atLeastOnePluginFailedToLoad = plugin.getName().equals(pluginManagerName) ? t : new Exception("See Plugin Manager for details");
            }
        }
        
        if (isApplicationInitialized) {
            for (PluginInfo plugin : okLoaded) {
                Plugin plug = plugin.getPlugin();
                if (plug != null && plugin.getErrorStatus() == null) {
                    try {
                        plug.postInit();
                    } catch (Throwable t) {
                        plugin.setErrorStatus(t);
                        atLeastOnePluginFailedToLoad = plugin.getName().equals(pluginManagerName) ? t : new Exception("See Plugin Manager for details");
                    }
                }
            }
        }
        
        if (isApplicationVisible) {
            for (PluginInfo plugin : okLoaded) {
                Plugin plug = plugin.getPlugin();
                if (plug != null && plugin.getErrorStatus() == null) {
                    try {
                        plug.applicationVisible();
                    } catch (Throwable t) {
                        plugin.setErrorStatus(t);
                        atLeastOnePluginFailedToLoad = plugin.getName().equals(pluginManagerName) ? t : new Exception("See Plugin Manager for details");
                    }
                }
            }
        }
        
        // clean up and revalidate
        
        loadedPlugins.trimToSize();
        pluginMap.invalidate();
        revalidate(); // plugins may have added menus etc, so for good measure!
        setStatusMessage("Plugins Loaded");
        return out;
    }


// -- Calling Plugin and PluginInfo SPI methods on loaded plugins : ------------
    
    @Override
    protected void fireInitializationComplete(ApplicationEvent event) {
        super.fireInitializationComplete(event);
        getEventSender().broadcast(event);
        for (PluginInfo info : loadedPlugins) {
            Plugin plugin = info.getPlugin();
            if (plugin != null && info.getErrorStatus() == null) {
                try {
                  plugin.postInit();
                } catch (Throwable t) {
                    info.setErrorStatus(t);
                    atLeastOnePluginFailedToLoad = info.getName().equals(pluginManagerName) ? t : new Exception("See Plugin Manager for details", t);
                }
            }
        }
        isApplicationInitialized = true;
    }

    @Override
    protected void fireApplicationVisible(ApplicationEvent event) {
        super.fireApplicationVisible(event);
        getEventSender().broadcast(event);
        for (PluginInfo info : loadedPlugins) {
            Plugin plugin = info.getPlugin();
            if (plugin != null && info.getErrorStatus() == null) {
                try {
                  plugin.applicationVisible();
                } catch (Throwable t) {
                    info.setErrorStatus(t);
                    atLeastOnePluginFailedToLoad = info.getName().equals(pluginManagerName) ? t : new Exception("See Plugin Manager for details");
                }
            }
        }
        isApplicationVisible = true;
        reportPluginException();
    }

    @Override
    protected void fireAboutToExit(ApplicationEvent event) {
        for (PluginInfo info : loadedPlugins) {
            Properties user = getUserProperties();
            info.saveUserProperties(user);
        }
        super.fireAboutToExit(event);
        getEventSender().broadcast(event);
    }


// -- Utility methods : --------------------------------------------------------
    
    private Plugin initializePlugin(PluginInfo plugin, ClassLoader loader) throws Throwable {
        try {
            Plugin plug = pluginFactory.getInstance(this, plugin, loader);
            plug.setContext(this);
            plugin.setPlugin(plug);
            plugin.setErrorStatus(null);
            return plug;
        } catch (Throwable t) {
            plugin.setErrorStatus(t);
            throw t;
        }
    }

    protected FreeHEPLookup createLookup() {
        return FreeHEPLookup.instance();
    }
    
    
// -- Page management : --------------------------------------------------------

    protected PageManager createRealPageManager() {
        String name = getUserProperties().getProperty("pageManagerName", "Tabbed Panes");
        Lookup.Template template = new Lookup.Template(PageManager.class, name, null);
        Lookup.Result result = getLookup().lookup(template);
        if (!result.allInstances().isEmpty()) {
            return (PageManager) result.allInstances().iterator().next();
        } else {
            // Previously we used the class name as pageManager, so this is just for backward compatibility.
            try {
                return super.createPageManager();
            } catch (Throwable t) {
                // Last chance, use whatever page manager we can find.
                PageManager pm = (PageManager) getLookup().lookup(PageManager.class);
                if (pm != null) return pm;
                return new TabbedPageManager();
            }
        }
    }

    @Override
    protected PageManager createPageManager() {
        // We initially create a dummy page manager, so we can delay creating the 
        // real page manager until after the plugins have been loaded (so that 
        // a plugin can register a plugin manager)
        return new DummyPageManager();
    }

    private static class DummyPageManager extends PageManager {

        private JPanel panel = new JPanel();

        @Override
        protected Component getEmbodiment() {
            return panel;
        }

        @Override
        protected void iconChanged(PageContext page) {
        }

        @Override
        protected void show(PageContext page) {
        }

        @Override
        protected void titleChanged(PageContext page) {
        }
    }
}