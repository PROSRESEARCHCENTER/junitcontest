package org.freehep.application.studio.pluginmanager;

import java.awt.Component;
import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.freehep.application.Application;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.studio.LibInfo;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.PluginDir;
import org.freehep.application.studio.PluginInfo;
import org.freehep.application.studio.PluginMap;
import org.freehep.application.studio.PluginMap.Orphan;
import org.freehep.application.studio.Studio;
import org.freehep.util.VersionComparator;

/**
 * A Plugin which provides a Plugin Manager.
 * 
 * @author tonyj
 * @version $Id: PluginManager.java 16101 2014-08-11 19:42:54Z onoprien $
 */
public class PluginManager extends Plugin implements Runnable {
    
// -- Private parts : ----------------------------------------------------------

    protected PluginPreferences preferences;
    
    private final Object pluginListLock = new Object(); // threads need to hold this object's monitor while modifying list of available plugins, etc.
    protected PluginListHandler pluginListHandler; // IoC service - handles fetching a list of available plugins
    protected volatile Thread pluginListReaderThread; // thread used to download the list of available plugins
    protected volatile boolean pluginListIsReady = false; // flag - set to true once the list of available plugins has been downloaded
    
    protected Map<String,PluginInfo> availablePlugins; // available plugins as reported by PluginListHandler
    
    private final CountDownLatch updateLatch = new CountDownLatch(1);
    protected ArrayList<PluginInfo> installablePlugins;  // available plugins not currently installed
    protected ArrayList<PluginInfo>  updatablePlugins;  // installed plugins for which newer version is available
    protected Map<PluginInfo,PluginInfo>  updateMap; // mapping from installed to available plugins, regardless of version
    
    protected PluginMap masterMap;
    
    protected boolean dialogVisible = false;
    
    private EventListenerList listeners = new EventListenerList();
    
    static final Logger logger = null; // Logger.getLogger(PluginManager.class.getName());

    
// -- Plugin SPI : -------------------------------------------------------------

    @Override
    protected void init() throws org.xml.sax.SAXException, java.io.IOException {
        
        masterMap = getApplication().getPluginMap();
        
        // read preferences
        
        preferences = makePreferences();        
        pluginListIsReady = false;

        // start downloading the list of available plugins in a separate thread
        
        if ((preferences.isCheckAtStart() || preferences.isDownloadMissing()) && preferences.getUrl() != null) {
            startPluginListDownload();
        } else if (preferences.isDownloadMissing()) {
            List<LibInfo> missing = masterMap.getMissingLibraries();
            if (!missing.isEmpty()) {
                download(getApplication(), masterMap.getDownloads(missing, false));
            }
        }

    }

    @Override
    protected void applicationVisible() {
        
        preferences = makePreferences();        
        
        // process unclaimed libraries
        
        Orphan orphanAction = preferences.getOrphanAction();
        if (preferences.isOrphanPrompt()) {
            EnumMap<PluginDir,ArrayList<LibInfo>> unclaimed = masterMap.getUnclaimedLibraries();
            if (!unclaimed.isEmpty() && preferences.isOrphanPrompt()) {
                preferences.showUnclaimedLibrariesWarning();
            }
        }
        masterMap.processUnclaimedLibraries(orphanAction);
        
        // purge plugin map
        
        masterMap.purge();
        
        // allow receiving the list of available plugins
        
        updateLatch.countDown();
    }

    /**
     * Creates a {@link PluginPreferences} instance that contains current saved settings.
     * Can be overridden by subclasses to return customized or extended preferences.
     */
    protected PluginPreferences makePreferences() {
        return new PluginPreferences(this, true);
    }

    
// -- Getters : ----------------------------------------------------------------

    /** Returns an object that provides access to current PluginManager settings. */
    public PluginPreferences getPreferences() {
        if (preferences == null) preferences = makePreferences();
        return preferences;
    }
    
    /** Returns <tt>true</tt> if the list of available plugins has been downloaded. */
    protected boolean isPluginListIsReady() {
        return pluginListIsReady;
    }
    
    /** Returns a map of currently installed plugins. */
    protected PluginMap getPluginMap() {
        return masterMap;
    }

    /** Returns the list of available plugins, or <tt>null</tt> if the list has not yet been downloaded. */
    protected Map<String,PluginInfo> getAvailablePlugins() {
        return pluginListIsReady ? Collections.unmodifiableMap(availablePlugins) : Collections.<String,PluginInfo>emptyMap();
    }

    /**
     * Returns a map from installed plugins to available plugins with the same name.
     * The map contains plugin pairs regardless of whether the available version is greater than the 
     * installed one. Installed or available plugins that do not have a counterpart are not in the map.
     * Returns <tt>null</tt> if the list of available plugins is not ready yet.
     */
    Map<PluginInfo,PluginInfo> getUpdateMap() {
        if (!pluginListIsReady) return null;
        if (updateMap == null) processAvailablePlugins();
        return updateMap;
    }

    /**
     * Returns a map of names to plugin descriptors for active plugins.
     */
    protected Map<String,PluginInfo> getActivePlugins() {
        return masterMap.getActivePlugins();
    }

    /**
     * Returns a list of available plugins that do not have installed counterparts with the same name.
     */
    List<PluginInfo> getInstallablePlugins() {
        if (!pluginListIsReady) return null;
        if (installablePlugins == null) processAvailablePlugins();
        return installablePlugins;
    }

    /**
     * Returns the list of updatable installed plugins.
     * A plugin is updatable if the list of available plugins contains a newer version of it. 
     */
    List<PluginInfo> getUpdatablePlugins() {
        if (!pluginListIsReady) return null;
        if (updatablePlugins == null) processAvailablePlugins();
        return updatablePlugins;
    }
    
    
// -- Setters : ----------------------------------------------------------------
    
    /* Setter for PluginListHandler service. */
    public void setPluginListHandler(PluginListHandler pluginListHandler) {
        this.pluginListHandler = pluginListHandler;
    }
    
    
// -- GUI display : ------------------------------------------------------------

    /** 
     * Displays PluginManagerDialog.
     * Should be called from the event processing thread.
     */
    public void showPluginManager() {
        if (!pluginListIsReady && pluginListReaderThread == null) startPluginListDownload();
        JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getApplication());
        JDialog dlg = new PluginManagerDialog(frame, PluginManager.this);
        dlg.setModal(true);
        dlg.setTitle("Plugin Manager");
        dlg.pack();
        dlg.setLocationRelativeTo(getApplication());
        try {
            dialogVisible = true;
            dlg.setVisible(true);
        } finally {
            dialogVisible = false;
            cleanup();
        }
    }

    /** 
     * Offers the user to update installed plugins and executes user's command.
     * Should be called from the event processing thread.
     */
    public void offerUpdate() {
        Box message = Box.createVerticalBox();
        JLabel label = new JLabel("Updated plugins available");
        label.setAlignmentX(1.0f);
        message.add(label);
        JCheckBox ask = new JCheckBox("Don't show me this again");
        message.add(ask);
        String[] options = {"Install now", "Plugin Manager...", "Cancel"};
        int rc = JOptionPane.showOptionDialog(getApplication(), message, "Updates available", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (ask.isSelected()) {
            preferences.setNotifyPluginUpdates(false, true);
        }
        if (rc == 0) { // install now
            update(getApplication());
        } else if (rc == 1) { // plugin manager
            showPluginManager();
        }
    }

    
// -- Downloading and processing list of available plugins : -------------------

    /** Launch downloading the list of available plugins in a new thread. */
    protected void startPluginListDownload() {
        synchronized (pluginListLock) {
            pluginListIsReady = false;
            pluginListReaderThread = new Thread(this);
            pluginListReaderThread.setDaemon(true);
            pluginListReaderThread.setPriority(Thread.NORM_PRIORITY);
            pluginListReaderThread.start();
        }
    }

    @Override
    /** Download and process the list of available plugins.*/
    public void run() {
        
        // download the list of available plugins
        
        PluginListHandler handler = (pluginListHandler == null ? new PluginListHandler() : pluginListHandler);
        List<PluginInfo> available = handler.getAvailablePlugins(preferences.getUrl(), logger, getApplication());
        Map<String,PluginInfo> availableMap = new LinkedHashMap<String,PluginInfo>((int)(available.size()*1.5f)+1);
        Studio studio = getApplication();
        boolean keepSnapshots = preferences.isSnapshots();
        for (PluginInfo plugin : available) {
            if ( !studio.isBlacklisted(plugin) && 
                 (keepSnapshots || !plugin.getVersion().contains("SNAPSHOT")) &&
                  plugin.isApplicationCompatible(getApplication()) && 
                  plugin.isJavaCompatible() ) {
                PluginInfo prev = availableMap.put(plugin.getName(), plugin);
                if (prev != null && VersionComparator.compareVersion(prev.getVersion(), plugin.getVersion()) > 0) {
                    availableMap.put(prev.getName(), prev);
                }
            }
        }
        
        // wait until plugin manager initialization is finished
        
        try {
            updateLatch.await();
        } catch (InterruptedException x) {
            return;
        }
        
        // launch plugin list processor in the event processing thread
        
        synchronized (pluginListLock) {
            if (pluginListReaderThread == Thread.currentThread()) {
                availablePlugins = availableMap;
                pluginListReaderThread = null;
                SwingUtilities.invokeLater(new PluginListProcessor(pluginListReaderThread));
            }
        }
    }
    
    private class PluginListProcessor implements Runnable {
        private Thread thr;
        PluginListProcessor(Thread readerThread) {
            thr = readerThread;
        }
        @Override
        public void run() {
            synchronized (pluginListLock) {
                if (thr != pluginListReaderThread) return; // another list download was started after me
                processAvailablePlugins();
                pluginListReaderThread = null;
                pluginListIsReady = true;
            }
            if (!updatablePlugins.isEmpty() && !dialogVisible && preferences.isNotifyPluginUpdates()) {
                offerUpdate();
            }
            if (preferences.isDownloadMissing()) {
                Studio app = getApplication();
                List<LibInfo> missing = masterMap.getDownloads(getActivePlugins().values(), availablePlugins);
                if (!missing.isEmpty() && download(app, missing)) {
                    boolean restart = app.loadPlugins(masterMap.getLoadablePlugins().values());
                    if (restart) {
                        restart(app);
                    } else {
                        app.reportPluginException();
                    }
                }
            }
            cleanup();
            fireStateChanged();
        }
    }

    /** 
     * Fills lists of updatable and installable plugins.
     * Called once the list of available plugins is downloaded.
     */
    protected void processAvailablePlugins() {
        installablePlugins = new ArrayList<PluginInfo>(availablePlugins.size());
        updatablePlugins = new ArrayList<PluginInfo>(availablePlugins.size());
        updateMap = new HashMap<PluginInfo,PluginInfo>();
        Map<String,PluginInfo> activePlugins = masterMap.getActivePlugins();
        for (PluginInfo info : availablePlugins.values()) {
            PluginInfo old = activePlugins.get(info.getName());
            if (old != null) {
                if (old.getDirectory() != PluginDir.BUILTIN) {
                    updateMap.put(old, info);
                    if (VersionComparator.compareVersion(info.getVersion(), old.getVersion()) > 0) {
                        updatablePlugins.add(old);
                    }
                }
            } else {
                installablePlugins.add(info);
            }
        }
    }
    
    
    
// -- Downloading and installing plugins : -------------------------------------

    /**
     * Updates all installed plugins for which newer versions are available.
     * 
     * @param parent Component to be used as parent by any GUI windows displayed while 
     *               executing this method, if any. If <tt>null</tt>, no error notifications 
     *               will be displayed, and <tt>IllegalArgumentException</tt> will be thrown instead.
     * @return true if restarting the application is required for the changes to take effect.
     * @throws IllegalArgumentException if errors occur while updating, and <tt>parent</tt> was not specified.
     */
    public boolean update(Component parent) {
        return update(parent, updatablePlugins);
    }
    
    
    /**
     * Installs specified plugins into the application extensions directories.
     * 
     * @param parent Component to be used as parent by any GUI windows displayed while 
     *               executing this method, if any. If <tt>null</tt>, no error notifications 
     *               will be displayed, and <tt>IllegalArgumentException</tt> will be thrown instead.
     * @param plugins Plugins to be installed. May or may not contain required dependencies.
     * @return true if restarting the application is required for the changes to take effect.
     * @throws IllegalArgumentException if errors occur while updating, and <tt>parent</tt> was not specified.
     */
    public boolean install(Component parent, Collection<PluginInfo> plugins) {
                
        // Figure out required downloads
        
        List<LibInfo> downloads = pluginListIsReady ? masterMap.getDownloads(plugins, availablePlugins)
                                                    : masterMap.getDownloads(plugins);
        // Download and install files

        if (! download(parent, downloads)) return false;
        
        // Load new plugins
        
        Collection<PluginInfo> active = masterMap.getLoadablePlugins().values();
        boolean restart = getApplication().loadPlugins(active);
        for (PluginInfo plugin : active) {
            if (plugin.getErrorStatus() != null) {
                String message = "At least one plugin failed to load, see Plugin Manager for details";
                if (parent == null) throw new IllegalArgumentException(message);
                Application.error(parent, message);
            }
        }
        
        // Update PluginManager and its listeners
        
        cleanup();
        fireStateChanged();
        return restart;
    }
    
    /**
     * Uninstall specified plugins.
     * 
     * @param parent Component to be used as parent by any GUI windows displayed while 
     *               executing this method, if any. If <tt>null</tt>, no error notifications 
     *               will be displayed, and <tt>IllegalArgumentException</tt> will be thrown instead.
     * @param plugins Plugins to be uninstalled. May or may not contain required dependencies.
     * @return true if restarting the application is required for the changes to take effect.
     * @throws IllegalArgumentException if errors occur while updating, and <tt>parent</tt> was not specified.
     */
    public boolean uninstall(Component parent, Collection<PluginInfo> plugins) {
        
        // Compile a set of plugins to remove, taking dependencies between plugins into account
        
        ArrayList<PluginInfo> verified = new ArrayList<PluginInfo>(plugins.size());
        for (PluginDir dir : PluginDir.sgu()) {
            HashSet<PluginInfo> verDir = new HashSet<PluginInfo>();
            for (PluginInfo plugin : plugins) {
                if (plugin.getDirectory() == dir) {
                    PluginInfo installed = masterMap.getPlugin(plugin.getName(), dir);
                    if (installed != null) {
                        verDir.add(installed);
                        verDir.addAll(masterMap.getDependentPlugins(installed));
                    }
                }
            }
            verified.addAll(verDir);
        }
        
        // remove plugins from the master map
        
        masterMap.remove(verified);
        
        // if a removed plugin was loaded, attempt to stop it
        
        boolean restart = false;
        for (PluginInfo plugin : verified) {
            try {
                PluginInfo loadedPlugin = getApplication().getPlugin(plugin.getName());
                if (loadedPlugin != null && loadedPlugin.getDirectory() == plugin.getDirectory()) {
                    getApplication().stopPlugin(plugin);
                    restart = true;
                }
            } catch (IllegalArgumentException x) {
                restart = true;
            }
        }
        cleanup();
        fireStateChanged();
        return restart;
    }

    /**
     * Downloads, installs, and loads latest versions of files for the specified plugins.
     * 
     * @param parent Component to be used as parent by any GUI windows displayed while 
     *               executing this method, if any. If <tt>null</tt>, no error notifications 
     *               will be displayed, and <tt>IllegalArgumentException</tt> will be thrown instead.
     * @param plugins Plugins to update.
     * @return true if restarting the application is required for the changes to take effect.
     * @throws IllegalArgumentException if the update fails, and <tt>parent</tt> was not specified.
     */
    public boolean update(Component parent, Collection<PluginInfo> plugins) {
        ArrayList<PluginInfo> updates = new ArrayList<PluginInfo>(plugins.size());
        for (PluginInfo plugin : plugins) {
            PluginInfo availablePlugin = availablePlugins.get(plugin.getName());
            PluginInfo installedPlugin = masterMap.getPlugin(plugin.getName(), plugin.getDirectory());
            if (availablePlugin != null && installedPlugin != null && 
                VersionComparator.compareVersion(availablePlugin.getVersion(), installedPlugin.getVersion()) > 0) {
                PluginInfo update = new PluginInfo(availablePlugin);
                update.setDirectory(installedPlugin.getDirectory());
                updates.add(update);
            }
        }
        return install(parent, updates);
    }


// -- Utility methods : --------------------------------------------------------
    
    /** Remove data structures only needed while plugin manager dialog is open. */
    protected void cleanup() {
        installablePlugins = null;
        updatablePlugins = null;
        updateMap = null;
    }

    /**
     * Downloads specified libraries.
     * @return True is successful.
     */
    private boolean download(Component parent, List<LibInfo> libraries) {
        Map<File,String> downloads = new HashMap<File,String>(libraries.size()*2); // File(resource) --> href
        for (LibInfo lib : libraries) {
            downloads.put(lib.getFile(), lib.getHref());
        }
        PluginDownload download = new PluginDownload(downloads);
        Thread t = new Thread(download);
        t.start();
        JOptionPane.showMessageDialog(parent, download, "Downloading...", JOptionPane.PLAIN_MESSAGE);

        Throwable status = download.getStatus();
        if (status != null) {
            download.cleanUp();
            String message = "Download failed: " + status;
            if (parent == null) throw new IllegalArgumentException(message);
            JOptionPane.showMessageDialog(parent, message, "Download error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            download.commit();
            masterMap.commit(libraries);
            return true;
        }
    }
    
    /**
     * Displays dialog informing the user that the application needs to be restarted.
     * If the user selects "Restart Now", restarting is attempted. If the restart fails for any
     * reason, the application is shut down. If the user selects "Restart Later", this
     * method returns without any further actions.
     * 
     * @param parentComponent Parent component for the dialog. If <tt>null</tt>, the application window is used as a parent.
     */
    public void restart(Component parentComponent) {
        if (parentComponent == null) parentComponent = getApplication();
        int opt = JOptionPane.showOptionDialog(parentComponent, 
                  "You must restart "+ getApplication().getAppName() +" for these changes to take effect",
                  "Restart application",
                  JOptionPane.DEFAULT_OPTION, 
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  new String[] {"Restart Now", "Restart Later"},
                  "Restart Now");
        if (opt == 0) getApplication().restart();
    }
    
// -- Handling listeners : -----------------------------------------------------

    void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
            listener.stateChanged(event);
        }
    }

}