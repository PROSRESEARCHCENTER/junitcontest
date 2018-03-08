package org.freehep.application.studio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.freehep.util.VersionComparator;

/**
 * Contains information on installed plugins and libraries, and supports operations on them.
 *
 * @author onoprien
 */
final public class PluginMap {

    /** Enumeration of unclaimed libraries treatment options. */
    public enum Orphan {Remove, Ignore, Load}
    
// -- Private parts : ----------------------------------------------------------

    private Studio app;
    
    // primary data structures
    
    private EnumMap<PluginDir,Map<String,PluginInfo>> pluginMap;
    private EnumMap<PluginDir,Map<String,LibInfo>> libMap;
    
    private ArrayList<LibInfo> duplicateLibraries;
    private ArrayList<PluginInfo> inMemoryPlugins;
    
    // temporary data structures
    
    private volatile Map<String,LibInfo> activeLibraries;
    private volatile Map<String,PluginInfo> activePlugins;
    private volatile EnumMap<PluginDir,ArrayList<LibInfo>> unclaimedLibraries;
    private ArrayList<LibInfo> missingLibraries;
    
    // ID map
    
    private EnumMap<PluginDir,Map<String,String>> name2id; // file name --> id
    static private final String ID_MAP_NAME = "library.map";

    
// -- Construction : -----------------------------------------------------------

    /**
     * Constructs PluginMap by scanning application extension directories.
     * Attempts to  delete empty files while scanning.
     */
    public PluginMap(Studio application) {
        app = application;
        scan();
    }
    
    /**
     * Constructs PluginMap from a collections of plugins.
     * Copies of plugin and library descriptors are made in the process.
     */
    protected PluginMap(Collection<PluginInfo> plugins) {
        for (PluginInfo plugin : plugins) {
            plugin = new PluginInfo(plugin);
            PluginDir dir = plugin.getDirectory();
            Map<String,PluginInfo> dirMapPlugin = pluginMap.get(dir);
            if (dirMapPlugin == null) {
                dirMapPlugin = new LinkedHashMap<String,PluginInfo>();
                pluginMap.put(dir, dirMapPlugin);
            }
            dirMapPlugin.put(plugin.getName(), plugin);
            Map<String,LibInfo> dirMapLib = libMap.get(dir);
            if (dirMapLib == null) {
                dirMapLib = new LinkedHashMap<String,LibInfo>();
                libMap.put(dir, dirMapLib);
            }
            for (LibInfo lib : plugin.getLibraries()) {
                lib = new LibInfo(lib);
                lib.setDir(dir);
                LibInfo other = dirMapLib.get(lib.getId());
                if ( other == null || other.getVersion() == null || 
                     (lib.getVersion() != null && (VersionComparator.compareVersion(lib.getVersion(), other.getVersion()) > 0)) ) {
                    dirMapLib.put(lib.getId(), lib);
                }
            }
        }
    }
    
    /**
     * Scans all extension directories and constructs primary data structures in this map.
     * Any information previously contained in this map is lost, except for in-memory plugins
     * (currently, those are used to keep track of unclaimed libraries present at the 
     * application startup.
     */
    synchronized public void scan() {
        
        invalidate();
        
        pluginMap = new EnumMap(PluginDir.class);
        libMap = new EnumMap(PluginDir.class);
        name2id = new EnumMap(PluginDir.class);
        duplicateLibraries = new ArrayList<LibInfo>();
        
        for (PluginDir extdir : PluginDir.values()) {
            scanDirectory(extdir);
        }
        if (inMemoryPlugins != null) {
            for (PluginInfo plugin : inMemoryPlugins) {
                addPlugin(plugin);
            }
        }
        
        if (duplicateLibraries.isEmpty()) {
            duplicateLibraries = null;
        } else {
            duplicateLibraries.trimToSize();
        }
        
    }
    
    /**
     * Scans extension directory and adds found items to this map.
     * Deletes files marked for deletion.
     */
    private void scanDirectory(PluginDir dir) {
        
        String dirPath = app.getExtensionsDir(dir);
        if (dirPath == null) return;
        File extdir = new File(dirPath);
        if (!extdir.isDirectory()) return;
        invalidate();
        
        // read (library name --> artifact id) map
        
        File infile = new File(dirPath, ID_MAP_NAME);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(infile));
            HashMap<String,String> map = new HashMap<String,String>();
            name2id.put(dir, map);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] mapping = line.trim().split("\\s");
                if (mapping.length == 2) { // true if line is in "name id" format
                    map.put(mapping[0], mapping[1]);
                }
            }
            reader.close();
        } catch (FileNotFoundException x) {
            name2id.remove(dir);
        } catch (IOException x) {
            throw new RuntimeException(x);
        }
        
        // scan for libraries and plugins

        Map<String,PluginInfo> dirMapPlug = new LinkedHashMap<String,PluginInfo>();
        Map<String,LibInfo> dirMapLib = new LinkedHashMap<String,LibInfo>();
        String[] files = extdir.list();
        for (int i = 0; i < files.length; i++) {
            String file = files[i];
            if (file.endsWith(".jar") || file.endsWith(".tmp")) {
                File f = new File(extdir, file);
                if (f.length() > 0) {
                    try {
                        JarFile jarFile = new JarFile(f);
                        JarEntry manifest = jarFile.getJarEntry("PLUGIN-inf/plugins.xml");
                        if (manifest != null) {
                            InputStream in = jarFile.getInputStream(manifest);
                            List<PluginInfo> newPlugins = app.buildPluginList(in);
                            for (PluginInfo plugin : newPlugins) {
                                plugin.setDirectory(dir);
                                translateLibraryIDs(plugin);
                                if (plugin.isApplicationCompatible(app) && plugin.isJavaCompatible() && !app.isBlacklisted(plugin)) {
                                    PluginInfo prev = dirMapPlug.get(plugin.getName());
                                    if ( prev == null || (VersionComparator.compareVersion(prev.getVersion(), plugin.getVersion()) < 0) ) {
                                        dirMapPlug.put(plugin.getName(), plugin);
                                    }
                                }
                            }
                        }
                        jarFile.close();
                        LibInfo library = new LibInfo(f, dir);
                        LibInfo prev = dirMapLib.get(library.getId());
                        if (prev == null) {
                            dirMapLib.put(library.getId(), library);
                        } else {
                            if ( prev.getVersion() == null || 
                                 (library.getVersion() != null && (VersionComparator.compareVersion(library.getVersion(), prev.getVersion()) > 0)) ) {
                                dirMapLib.put(library.getId(), library);
                                duplicateLibraries.add(prev);
                            } else {
                                duplicateLibraries.add(library);
                            }
                        }
                    } catch (IOException x) {
                        System.err.println("Error reading extension file " + file + " : " + x);
                    }
                } else { // Files with length 0 have been flagged for deletion by the plugin manager.
                    f.delete();
                }
            }
        }
        
//        // replace plugin descriptors identical to those already loaded
//        
//        HashMap<String,PluginInfo> loadedMap = new HashMap<String,PluginInfo>();
//        for (PluginInfo plugin : app.getPlugins()) {
//            if (plugin.getDirectory() == dir) loadedMap.put(plugin.getName(), plugin);
//        }
//        for (Map.Entry<String,PluginInfo> e : dirMapPlug.entrySet()) {
//            PluginInfo loaded = 
//        }
                
        // save primary data structures
        
        if (!dirMapPlug.isEmpty() || !libMap.isEmpty()) {
            pluginMap.put(dir, dirMapPlug);
            libMap.put(dir, dirMapLib);
        }
        
    }


// -- Modification : -----------------------------------------------------------
    
    /**
     * Insert specified plugins into this map without downloading or replacing libraries.
     * This can be useful when one needs to add plugins found on the classpath and not in the
     * extensions directories, for example. Plugins in the collection should have their target
     * directories set before this method is called. If the target directory is BUILTIN,
     * libraries referenced by added plugins will be added to the list of available libraries.
     */
    synchronized public void insertPlugins(Collection<PluginInfo> plugins) {
        for (PluginInfo plugin : plugins) {
            PluginDir dir = plugin.getDirectory();
            if (getPlugin(plugin.getName(), dir) == null) {
                addPlugin(plugin);
                if (dir == PluginDir.BUILTIN) {
                    for (LibInfo lib : plugin.getLibraries()) {
                        LibInfo already = this.getLibrary(lib.getId(), dir);
                        if (already == null) {
                            Map<String, LibInfo> dirMap = libMap.get(PluginDir.BUILTIN);
                            if (dirMap == null) {
                                dirMap = new LinkedHashMap<String, LibInfo>();
                                libMap.put(PluginDir.BUILTIN, dirMap);
                            }
                            dirMap.put(lib.getId(), new LibInfo(lib));
                        }
                    }
                } else {
                    missingLibraries = null;
                }
            }
        }
    }
    
    /** 
     * Creates plugins for unclaimed libraries if necessary,
     * and sets their properties to ensure that {@link #purge} method will take appropriate action.
     */
    synchronized public void processUnclaimedLibraries(Orphan action) {
        if (action != Orphan.Remove) {
            EnumMap<PluginDir, ArrayList<LibInfo>> unclaimedLibs = getUnclaimedLibraries();
            if (!unclaimedLibs.isEmpty()) {
                for (PluginDir dir : unclaimedLibs.keySet()) {
                    List<LibInfo> unclaimed = unclaimedLibs.get(dir);
                    ArrayList<LibInfo> libs = new ArrayList<LibInfo>(unclaimed.size());
                    StringBuilder pluginDescription = new StringBuilder("Libraries found in the ");
                    pluginDescription.append(dir.getLabel()).append(" extensions directory but not claimed by any installed plugins:");
                    for (LibInfo lib : unclaimed) {
                        StringBuilder location = new StringBuilder(lib.getId());
                        if (lib.getVersion() != null) {
                            location.append("-").append(lib.getVersion());
                        }
                        location.append(".jar");
                        String href = null;
                        try {
                            href = lib.getFile().toURI().toURL().toString();
                        } catch (Exception x) {
                        }
                        String fileName = location.toString();
                        libs.add(new LibInfo(fileName, href));
                        pluginDescription.append("\n").append(fileName);
                    }
                    PluginInfo unclaimedPlugin = new PluginInfo(getUnclaimedPluginName(dir), "", "1.0", null, "Unclaimed libraries",
                            pluginDescription.toString(), null, action == Orphan.Load);
                    unclaimedPlugin.setDirectory(dir);
                    unclaimedPlugin.setLibraries(libs);
                    
                    addPlugin(unclaimedPlugin);
                    if (inMemoryPlugins == null) inMemoryPlugins = new ArrayList<PluginInfo>(1);
                    inMemoryPlugins.add(unclaimedPlugin);
                }
                invalidate();
            }
        }
    }
    
    /**
     * Creates a PluginMap that can be used to download and install the specified plugins.
     * This method should be called on the application master map, which is not modified as a 
     * result of the call. The returned map can be used to get a collection of required downloads 
     * through a call to {@link #getDownloads()}.
     * Once the files are downloaded, it can be passed to the master map's {@link #commit()}
     * method to install or update the plugins.
     * 
     * <h4>Implementation notes:</h4>
     * <ul>
     * <li>Ignores plugins if equal or later version is already present.</li>
     * <li>Creates File objects for libraries that do not have equal or later version is already present.</li>
     * <li>No dependency checking.</li>
     * </ul>
     * @param plugins Collection of plugins to be installed.
     *                Must contain all required dependencies.
     * @return PluginMap that contains only plugins that have to be installed.
     *         For each library, <tt>getFile()</tt> will return the path where the file needs to be downloaded,
     *         or <tt>null</tt> if the same or newer version of this library is already installed.
     */
    synchronized public PluginMap add(Collection<PluginInfo> plugins) {
        ArrayList<PluginInfo> pluginsToBeInstalled = new ArrayList<PluginInfo>(plugins.size());
        for (PluginInfo plugin : plugins) {
           PluginInfo installed = getPlugin(plugin.getName(), plugin.getDirectory());
           if (installed == null || VersionComparator.compareVersion(installed.getVersion(), plugin.getVersion()) < 0) {
               pluginsToBeInstalled.add(plugin);
               translateLibraryIDs(plugin);
           }
        }
        PluginMap updateMap = new PluginMap(pluginsToBeInstalled);
        for (LibInfo candidate : updateMap.getLibraries()) {
            LibInfo installed = getLibrary(candidate.getId(), candidate.getDir());
            String candidateVersion = candidate.getVersion();
            if (installed == null || installed.getVersion() == null || candidateVersion == null || 
                    (VersionComparator.compareVersion(installed.getVersion(), candidateVersion) < 0)) {
                candidate.setFile(makePath(candidate, "tmp"));
            }
        }
        return updateMap;
    }
    
    /**
     * Installs specified libraries..
     * 
     * @param update
     * @return True if the update cannot be immediately loaded and therefore requires restart.
     */
    synchronized public void commit(List<LibInfo> libraries) {
        
        EnumSet<PluginDir> addedToIdMap = EnumSet.noneOf(PluginDir.class);
        for (LibInfo candidate : libraries) {
            if (candidate.getFile() != null) {
                if (candidate.getVersion() == null) {
                    if (candidate.checkMavenID()) {
                        String location = candidate.getLocation();
                        String id = candidate.getId();
                        if (! location.equals(id)) {
                            idMapPut(candidate.getDir(), location, id);
                            addedToIdMap.add(candidate.getDir());
                        }
                        File renameTo = makePath(candidate, "tmp");
                        File renameFrom = candidate.getFile();
                        if (renameFrom.renameTo(renameTo)) {
                            candidate.setFile(renameTo);
                        }
                    }
                }
            }
        }
        
        for (PluginDir dir : addedToIdMap) {
            saveIdMap(dir);
        }
        
        scan();
        purge();
    }
    
    /**
     * Removes the specified plugins from this map and deletes (or marks for deletion) the files that are no longer needed.
     * 
     * @param plugins Collection of plugins to be removed. 
     *                Should not contain dependencies of other plugins - no checking is done by this method.
     * @return True if restart is required before the changes can take effect.
     */
    synchronized public boolean remove(Collection<PluginInfo> plugins) {
        
        boolean restart = false;
        
        // remove plugin descriptors from this map
        
        for (PluginInfo plugin : plugins) {
            restart = restart || isLoaded(plugin);
            removePlugin(plugin);
        }
        invalidate();

        // remove no longer needed files
        
        restart = removeUnclaimedLibraries() || restart;        
        updateIdMap();
        
        return restart;
    }
    
    /**
     * Purges unused libraries.
     */
    synchronized public void purge() {
        
        // remove duplicate libraries
        
        if (duplicateLibraries != null) {
            for (LibInfo lib : duplicateLibraries) {
                lib.getFile().delete();
            }
            duplicateLibraries = null;
        }
        
        // remove unclaimed libraries
        
        removeUnclaimedLibraries();
        
        // rename .tmp files
        
        for (LibInfo lib : getLibraries()) {
            if (lib.getDir() == PluginDir.BUILTIN) continue;
            File file = lib.getFile();
            String path = file.getPath();
            if (path.endsWith(".tmp")) {
                path = path.substring(0, path.length()-4) +".jar";
                File newFile = new File(path);
                if (!isLoaded(newFile)) {
                    if (file.renameTo(newFile)) lib.setFile(newFile.getAbsoluteFile());
                }
            }
        }
        
        // remove unnecessary entries from ID map files
        
        updateIdMap();
        
    }
    
    /** Clears all secondary data structures. */
    synchronized public void invalidate() {
        activeLibraries = null;
        activePlugins = null;
        unclaimedLibraries = null;
        missingLibraries = null;
    }

    
// -- Public getters : ---------------------------------------------------------
    
    /**
     * Returns a map of names to descriptors for all currently active plugins.
     * A plugin is active if it would be loaded if the application was starting now.
     * A plugin may be present in this map but inactive if and only if an identically named plugin exists in one of the 
     * extensions directories that appear earlier in the directories search order (builtin - user - group - system).
     */
    synchronized public Map<String,PluginInfo> getActivePlugins() {
        if (activePlugins == null) {
            
            // choose active plugins
            
            activePlugins = new HashMap<String, PluginInfo>();
            for (PluginDir dir : PluginDir.inverseSearchOrder()) {
                Map<String, PluginInfo> dirMap = pluginMap.get(dir);
                if (dirMap != null) {
                    for (Map.Entry<String, PluginInfo> entry : dirMap.entrySet()) {
                        PluginInfo plugin = entry.getValue();
                        activePlugins.put(entry.getKey(), plugin);
                    }
                }
            }
            
            // choose active libraries
            
            chooseActiveLibraries();
            
            // find missing libraries
            
            missingLibraries = new ArrayList<LibInfo>();
            for (PluginInfo plugin : activePlugins.values()) {
                PluginException.reportNoMissingPlugins(plugin);
                PluginException.reportNoMissingLibraries(plugin);
                for (LibInfo lib : plugin.getLibraries()) {
                    if (getActiveLibrary(lib) == null) {
                        missingLibraries.add(lib);
                        PluginException.reportMissingLibrary(plugin, lib.getId());
                    }
                }
            }
            
            // mark plugins missing dependencies
            
            for (PluginInfo plugin : activePlugins.values()) {
                HashSet<String> visited = new HashSet<String>(activePlugins.size()*2);
                ArrayDeque<PluginInfo> dfs = new ArrayDeque<PluginInfo>(visited.size());
                dfs.push(plugin);
                while (!dfs.isEmpty()) {
                    PluginInfo plug = dfs.pop();
                    for (String requiredName : plug.getRequiredPluginNames()) {
                        PluginInfo requiredPlugin = activePlugins.get(requiredName);
                        if (plug.isRequiredPluginValid(requiredPlugin) && requiredPlugin.getErrorStatus() == null) {
                            if (!visited.add(requiredName)) {
                                dfs.push(plug);
                            }
                        } else {
                            PluginException.reportMissingPlugin(plugin, requiredName);
                        }
                    }
                }
            }

        }
        return Collections.unmodifiableMap(activePlugins);
    }
    
    /**
     * Returns a map of names to descriptors for all currently active non-broken load-at-start plugins and their dependencies.
     */
    synchronized public Map<String,PluginInfo> getLoadablePlugins() {
        
        Map<String,PluginInfo> active = getActivePlugins();
        HashMap<String,PluginInfo> out = new HashMap<String,PluginInfo>(active.size()*2);

        ArrayDeque<PluginInfo> queue = new ArrayDeque<PluginInfo>(active.size()*2);
        for (PluginInfo plugin : active.values()) {
            if (plugin.getErrorStatus() == null && plugin.isLoadAtStart()) {
                out.put(plugin.getName(), plugin);
                for (String name : plugin.getRequiredPluginNames()) {
                    PluginInfo dependency = active.get(name);
                    if (dependency != null && dependency.getErrorStatus() == null) {
                        PluginInfo alreadyAdded = out.put(name, dependency);
                        if (alreadyAdded == null) {
                            queue.add(dependency);
                        }
                    }
                }
            }
        }
        while (!queue.isEmpty()) {
            PluginInfo plugin = queue.poll();
            for (String name : plugin.getRequiredPluginNames()) {
                PluginInfo dependency = active.get(name);
                PluginInfo alreadyAdded = out.put(name, dependency);
                if (alreadyAdded == null) {
                    queue.add(dependency);
                }
            }
        }
        
        return out;
    }
    
    /**
     * Returns a map of IDs to descriptors for all currently active libraries.
     * A library is active if it would be added to the classpath if the application was starting now.
     * The algorithm for choosing an active library for a given ID:
     * if there is a built-in library with this ID, choose it; otherwise, among all libraries with this ID
     * located in extensions directories where there are installed plugins referencing it, choose the latest version.
     * If there are identical versions, choose the library in the directory that appears earlier in the 
     * search order (user - group - system). Unknown version is treated as latest.
     */
    synchronized public Map<String,LibInfo> getActiveLibraries() {
        if (activeLibraries == null) getActivePlugins();
        return Collections.unmodifiableMap(activeLibraries);
    }
    
    /** Returns a list of libraries missed by active plugins. */
    synchronized public List<LibInfo> getMissingLibraries() {
        if (missingLibraries == null) getActivePlugins();
        return Collections.unmodifiableList(missingLibraries);
    }
    
    /**
     * Returns a map of IDs to descriptors for currently active libraries claimed by the specified plugin.
     * @throws IllegalArgumentException if there is no active library to satisfy the plugin's requirements.
     */
    synchronized public Map<String,LibInfo> getActiveLibraries(PluginInfo plugin) {
        if (activeLibraries == null) getActivePlugins();
        Map<String,LibInfo> out = new HashMap<String,LibInfo>();
        for (LibInfo aim : plugin.getLibraries()) {
            LibInfo target = getActiveLibrary(aim);
            if (target == null) throw new IllegalArgumentException(aim.getId());
            out.put(target.getId(), target);
        }
        return out;
    }
    
    /**
     * Returns active library that corresponds to the specified library.
     * @param library Library descriptor, typically part of a plugin descriptor.
     * @return Descriptor of the corresponding active library (<tt>null</tt> if none found).
     */
    synchronized public LibInfo getActiveLibrary(LibInfo library) {
        if (activeLibraries == null) getActivePlugins();
        LibInfo target = activeLibraries.get(library.getId());
        if (target == null) {
            String location = library.getLocation();
            for (LibInfo lib : activeLibraries.values()) {
                if (lib != null && location.equals(lib.getLocation())) {
                    target = lib;
                    break;
                }
            }
        }
        return target;
    }
    
    /** Returns URLs for all active libraries. */
    synchronized public URL[] getExtensionClasspath() {
        Map<String,LibInfo> al = getActiveLibraries();
        ArrayList<URL> alList = new ArrayList<URL>(al.size());
        try {
            for (LibInfo lib : al.values()) {
                File f = lib.getFile();
                if (f != null) alList.add(f.toURI().toURL());
            }
        } catch (MalformedURLException x) {
            throw new RuntimeException(x);
        }
        return alList.toArray(new URL[alList.size()]);
    }
    
    /** Returns a list of all plugin descriptors in this map. */
    synchronized public List<PluginInfo> getPlugins() {
        ArrayList<PluginInfo> out = new ArrayList<PluginInfo>(20);
        for (Map<String,PluginInfo> dirMap : pluginMap.values()) {
            if (dirMap != null) {
                for (PluginInfo plugin : dirMap.values()) {
                    out.add(plugin);
                }
            }
        }
        return out;
    }
    
    /** Returns a map of plugin names to descriptors in the specified directory. */
    synchronized public Map<String,PluginInfo> getPlugins(PluginDir dir) {
        Map<String,PluginInfo> out = pluginMap.get(dir);
        return out == null ? Collections.<String,PluginInfo>emptyMap() : Collections.unmodifiableMap(out);
    }
    
    /** 
     * Returns active plugin descriptor with the specified name.
     * Returns <tt>null</tt> if there is no such plugin in this map.
     */
    synchronized public PluginInfo getPlugin(String name) {
        return getActivePlugins().get(name);
    }
    
    /**
     * Returns a plugin descriptor for the specified plugin name and directory.
     * Returns <tt>null</tt> if there is no such plugin in this map.
     */
    synchronized public PluginInfo getPlugin(String name, PluginDir dir) {
        Map<String,PluginInfo> dirMap = pluginMap.get(dir);
        return dirMap == null ? null : dirMap.get(name);
    }
    
    /** Returns a list of all non-duplicate libraries in this map. */
    synchronized public List<LibInfo> getLibraries() {
        ArrayList<LibInfo> out = new ArrayList<LibInfo>(50);
        for (Map<String,LibInfo> dirMap : libMap.values()) {
            if (dirMap != null) {
                for (LibInfo lib : dirMap.values()) {
                    out.add(lib);
                }
            }
        }
        return out;
    }
    
    /** Returns a collection of all non-duplicate libraries in the specified directory. */
    synchronized public Collection<LibInfo> getLibraries(PluginDir dir) {
        Map<String,LibInfo> dirMap = libMap.get(dir);
        if (dirMap == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableCollection(dirMap.values());
        }
    }
    
    /** 
     * Returns library descriptor for the specified ID and directory.
     * Returns <tt>null</tt> if there is no such library in this map.
     */
    synchronized public LibInfo getLibrary(String id, PluginDir dir) {
        Map<String,LibInfo> dirMap = libMap.get(dir);
        if (dirMap == null) return null;
        return dirMap.get(id);
    }
    
    /**
     * Returns installed library that corresponds to the specified library.
     * @param library Library descriptor, typically part of a plugin descriptor.
     * @return Descriptor of the corresponding installed library (<tt>null</tt> if none found).
     */
    synchronized public LibInfo getLibrary(LibInfo library) {
        PluginDir dir = library.getDir();
        Map<String,LibInfo> dirMap = libMap.get(dir);
        if (dirMap == null) return null;
        LibInfo out = dirMap.get(library.getId());
        if (out == null && dir != PluginDir.BUILTIN) {
            for (LibInfo lib : dirMap.values()) {
                if (lib.getLocation().equals(library.getLocation())) {
                    return lib;
                }
            }
        }
        return out;
    }

    /**
     * Returns a set of dependents (direct and transient) of the specified plugin.
     * Only looks for dependents installed in the same directory. No version checking is done.
     */
    synchronized public Set<PluginInfo> getDependentPlugins(PluginInfo plugin) {
        Set<PluginInfo> out = new HashSet<PluginInfo>();
        Map<String,PluginInfo> all = getPlugins(plugin.getDirectory());
        findDependentPlugins(plugin, out, all);
        return out;
    }
    
    /**
     * Returns mapping of extension directories to lists of unclaimed libraries found in those directories.
     * A directory that does not contain unclaimed libraries will not be in the map.
     */
    synchronized public EnumMap<PluginDir,ArrayList<LibInfo>> getUnclaimedLibraries() {
        if (unclaimedLibraries == null) {
            unclaimedLibraries = new EnumMap<PluginDir, ArrayList<LibInfo>>(PluginDir.class);
            for (PluginDir dir : PluginDir.sgu()) {
                ArrayList<LibInfo> unclaimed = null;
                Collection<LibInfo> libs = getLibraries(dir);
                for (LibInfo lib : libs) {
                    if (getReferencingPlugins(lib).isEmpty()) {
                        if (unclaimed == null) unclaimed = new ArrayList<LibInfo>(libs.size());
                        unclaimed.add(lib);
                    }
                }
                if (unclaimed != null) {
                    unclaimed.trimToSize();
                    unclaimedLibraries.put(dir, unclaimed);
                }
            }
        }
        return unclaimedLibraries;
    }
    
    /**
     * Returns a list of libraries that need to be downloaded to install specified plugins.
     * Libraries that have a satisfactory version already installed are not included.
     * If a plugin from the specified collection has a dependency that is neither present in the collection nor
     * already installed, that dependency is added from the collection of available plugins if possible.
     * <p>
     * This method should be called on the application master map, which is not modified as a 
     * result of the call. 
     * 
     * @param plugins Collection of plugins to be installed.
     * @param available Collection of plugins available for download.
     * @return List of newly created LibInfo objects with their File properties set to appropriate download location.
     */
    synchronized public List<LibInfo> getDownloads(Collection<PluginInfo> plugins, Map<String,PluginInfo> available) {
        ArrayList<PluginInfo> all = new ArrayList<PluginInfo>(plugins.size());
        for (PluginDir dir : PluginDir.sgu()) {
            HashMap<String,PluginInfo> dirMap = new HashMap<String,PluginInfo>(plugins.size()*2);
            for (PluginInfo plugin : plugins) {
                if (plugin.getDirectory() == dir) addWithDependencies(plugin, dirMap, available);
            }
            all.addAll(dirMap.values());
        }
        return getDownloads(all);
    }
    
    /**
     * Returns a list of libraries that need to be downloaded to install specified plugins.
     * The latest version is chosen between installed and multiple requests for the same library.
     * No plugin dependency checking.
     * <p>
     * This method should be called on the application master map, which is not modified as a 
     * result of the call. 
     * 
     * @param plugins Collection of plugins to be installed.
     * @return List of newly created LibInfo objects with their File properties set to appropriate download location.
     */
    synchronized public List<LibInfo> getDownloads(Collection<PluginInfo> plugins) {
        ArrayList<LibInfo> all = new ArrayList<LibInfo>(64);
        for (PluginDir dir : PluginDir.sgu()) {
            HashMap<String,LibInfo> dirMap = new HashMap<String,LibInfo>();
            for (PluginInfo plugin : plugins) {
                if (plugin.getDirectory() == dir) {
                    for (LibInfo candidate : plugin.getLibraries()) {
                        String id = candidate.getId();
                        String candidateVersion = candidate.getVersion();
                        LibInfo chosen = dirMap.get(id);
                        if (chosen == null) {
                            LibInfo installed = getLibrary(id, dir);
                            if ( installed == null || 
                                 (  candidateVersion != null && 
                                    ( 
                                      installed.getVersion() == null || 
                                      VersionComparator.compareVersion(candidateVersion, installed.getVersion()) > 0
                                    )
                                 )
                               ) {
                                        dirMap.put(id, candidate);
                                 }
                        } else {
                            String chosenVersion = chosen.getVersion();
                            if (chosenVersion == null ||
                               (candidateVersion != null && VersionComparator.compareVersion(candidateVersion, chosenVersion) > 0)) {
                                    dirMap.put(id, candidate);
                            }
                        }
                    }
                }
            }
            for (LibInfo lib : dirMap.values()) {
                lib = new LibInfo(lib);
                lib.setFile(makePath(lib, "tmp"));
                all.add(lib);
            }
        }
        return all;
    }
    
    /**
     * Returns a list of libraries that need to be downloaded to install specified libraries.
     * 
     * @param libraries List of libraries to download. Objects from this list will not be modified.
     * @param checkInstalled If true, only libraries that do not have same of newer version already installed will be included.
     * @return List of newly created LibInfo objects with their File properties set to appropriate download location.
     */
    public List<LibInfo> getDownloads(Collection<LibInfo> libraries, boolean checkInstalled) {
        ArrayList<LibInfo> out = new ArrayList<LibInfo>(libraries.size());
        for (LibInfo lib : libraries) {
            if (checkInstalled) {
                LibInfo installed = getLibrary(lib.getId(), lib.getDir());
                if (!(installed == null || installed.getVersion() == null || lib.getVersion() == null ||
                    VersionComparator.compareVersion(lib.getVersion(), installed.getVersion()) > 0)) {
                        break;
                }
            }
            lib = new LibInfo(lib);
            lib.setFile(makePath(lib, "tmp"));
            out.add(lib);
        }
        return out;
    }


// -- Local methods : ----------------------------------------------------------
    
    private void addWithDependencies(PluginInfo plugin, Map<String,PluginInfo> target, Map<String,PluginInfo> available) {
        String name = plugin.getName();
        if (target.containsKey(name)) return;
        target.put(name, plugin);
        for (String requiredName : plugin.getRequiredPluginNames()) {
            if (!target.containsKey(requiredName)) {
                PluginInfo candidate = available.get(requiredName);
                if (plugin.isRequiredPluginValid(candidate)) {
                    candidate = new PluginInfo(candidate);
                    candidate.setDirectory(plugin.getDirectory());
                    addWithDependencies(candidate, target, available);
                }
            }
        }
    }
    
    private String getUnclaimedPluginName(PluginDir dir) {
        return "Unclaimed libraries in "+ dir.getLabel() +" directory";
    }

    /**
     * Compiles a map of active libraries.
     * A library is active if it would be added to the classpath if the application was starting now.
     * The algorithm for choosing an active library for a given ID:
     * if there is a built-in library with this ID, choose it; otherwise, among all libraries with this ID
     * located in extensions directories where there are installed plugins referencing it, choose the latest version.
     * If there are identical versions, choose the library in the directory that appears earlier in the 
     * search order (user - group - system). Unknown version is treated as latest.
     */
    private void chooseActiveLibraries() {
        
        activeLibraries = new HashMap<String,LibInfo>();
        
        // map library IDs to sets of directories where plugins need them
        
        Map<String,EnumSet<PluginDir>> id2dirs = new HashMap<String,EnumSet<PluginDir>>();
        for (PluginInfo plugin : activePlugins.values()) {
            PluginDir dir = plugin.getDirectory();
            for (LibInfo lib : plugin.getLibraries()) {
                String id = getId(lib);
                EnumSet<PluginDir> dirSet = id2dirs.get(id);
                if (dirSet == null) {
                    dirSet = EnumSet.of(dir);
                    id2dirs.put(id, dirSet);
                } else {
                    dirSet.add(dir);
                }
            }
        }
        
        // for each ID, choose library
        
        for (Map.Entry<String,EnumSet<PluginDir>> e : id2dirs.entrySet()) {
            String id = e.getKey();
            LibInfo lib = getLibrary(id, PluginDir.BUILTIN);
            if (lib == null) {
                EnumSet<PluginDir> dirSet = e.getValue();
                for (PluginDir dir : PluginDir.ugs()) {
                    if (dirSet.contains(dir)) {
                        LibInfo candidate = getLibrary(id, dir);
                        if (candidate != null) {
                            if (lib == null || (candidate.getVersion() != null && 
                                                VersionComparator.compareVersion(lib.getVersion(), candidate.getVersion()) < 0)) {
                                lib = candidate;
                                if (lib.getVersion() == null) break;
                            }
                        }
                    }
                }
            }
            activeLibraries.put(id, lib);
        }
        
    }
    
    /**
     * Finds and removes unclaimed libraries.
     * @return true if application restart is required as a result.
     */ 
    private boolean removeUnclaimedLibraries() {
        boolean restart = false;
        for (ArrayList<LibInfo> libs : getUnclaimedLibraries().values()) {
            for (LibInfo lib : libs) {
                restart = removeLibrary(lib) || restart;
            }
            invalidate();
        }
        return restart;
    }
    
    private void findDependentPlugins(PluginInfo plugin, Set<PluginInfo> out, Map<String,PluginInfo> all) {
        for (PluginInfo p : all.values()) {
            if (p.getRequiredPluginNames().contains(plugin.getName()) && out.add(p)) {
                findDependentPlugins(p, out, all);
            }
        }
    }
    
    /** Removes the specified plugin descriptor from this map. */
    private void removePlugin(PluginInfo plugin) {
        Map<String,PluginInfo> dirMap = pluginMap.get(plugin.getDirectory());
        if (dirMap != null) {
            dirMap.remove(plugin.getName());
            if (dirMap.isEmpty()) pluginMap.remove(plugin.getDirectory());
        }
    }
    
    /** Adds the specified plugin descriptor to this map. */
    private void addPlugin(PluginInfo plugin) {
        Map<String,PluginInfo> dirMap = pluginMap.get(plugin.getDirectory());
        if (dirMap == null) {
            dirMap = new LinkedHashMap<String,PluginInfo>();
            pluginMap.put(plugin.getDirectory(), dirMap);
        }
        dirMap.put(plugin.getName(), plugin);
    }
    
    /**
     * Remove the specified descriptor from this map, delete the corresponding file if it is 
     * not on the classpath, mark for deletion otherwise.
     * 
     * @return True if the file was not actually deleted.
     */
    private boolean removeLibrary(LibInfo library) {
        boolean restart = false;
        Map<String,LibInfo> dirMap = libMap.get(library.getDir());
        if (dirMap != null) {
            LibInfo lib = dirMap.remove(library.getId());
            if (dirMap.isEmpty()) libMap.remove(library.getDir());
            if (lib != null) {
                File file = lib.getFile();
                if (file != null) {
                    if (isLoaded(file)) {
                        markFileForDeletion(file);
                        restart = true;
                    } else {
                        if (file.delete()) {
                            restart = false;
                        } else {
                            markFileForDeletion(file);
                            restart = true;
                        }
                    }
                }
            }
        }
        return restart;
    }
    
    /**
     * Add or replace the specified descriptor to this map. 
     * If a library with the same ID and installation directory is already present in this
     * map, that library is removed, and the corresponding file is
     * deleted unless it is currently on the classpath. If the library being added corresponds
     * to a file with ".tmp" extension and is expected to be immediately loadable, the 
     * extension is changed to ".jar".
     * 
     * @return True if the replaced library was on the classpath or its file could not be deleted.
     */
    private boolean addLibrary(LibInfo library) {
        boolean restart = false;
        Map<String,LibInfo> dirMap = libMap.get(library.getDir());
        if (dirMap == null) {
            dirMap = new LinkedHashMap<String,LibInfo>();
            libMap.put(library.getDir(), dirMap);
        } else {
            LibInfo old = dirMap.get(library.getId());
            if (old != null) {
                File oldFile = old.getFile();
                if (isLoaded(oldFile)) {
                    restart = true;
                } else {
                    restart = !oldFile.delete() || restart;
                }
            }
        }
        dirMap.put(library.getId(), library);
        if (!restart && library.getFile().getName().endsWith(".tmp")) {
            File renameTo = makePath(library, "jar");
            restart = isLoaded(renameTo) || !library.getFile().renameTo(renameTo);
            if (!restart) library.setFile(renameTo);
        }
        return restart;
    }
    
    /**
     * Returns an identity set of plugin descriptors referencing the specified library.
     * A plugin references a library if both are installed in the same directory, and the 
     * plugin descriptor specifies a library with a matching (or mapped) ID.
     */
    private Set<PluginInfo> getReferencingPlugins(LibInfo library) {
        Set<PluginInfo> out = Collections.newSetFromMap(new IdentityHashMap<PluginInfo,Boolean>());
        PluginDir dir = library.getDir();
        Map<String,PluginInfo> dirMap = getPlugins(dir);
        for (PluginInfo plugin : dirMap.values()) {
            for (LibInfo lib : plugin.getLibraries()) {
                if (library == getLibrary(lib)) out.add(plugin);
            }
        }
        return out;
    }
    
    /**
     * Puts a mapping from location to ID into the ID map.
     * @return Previous value.
     */
    private String idMapPut(PluginDir dir, String location, String id) {
        Map<String,String> dirMap = name2id.get(dir);
        if (dirMap == null) {
            dirMap = new HashMap<String,String>();
            name2id.put(dir, dirMap);
        }
        return dirMap.put(location, id);
    }
    
    /**
     * Removes a mapping for the specified location from the ID map.
     * @return Previous value.
     */
    private String idMapRemove(PluginDir dir, String location) {
        Map<String,String> dirMap = name2id.get(dir);
        if (dirMap == null) return null;
        String out = dirMap.remove(location);
        if (dirMap.isEmpty()) name2id.remove(dir);
        return out;
    }
    
    /**
     * Returns library ID to which the specified location is mapped in the ID map.
     * Returns <tt>name</tt> if the ID map contains no such key.
     */
    private String idMapGet(PluginDir dir, String location) {
        Map<String,String> dirMap = name2id.get(dir);
        if (dirMap == null) {
            return location;
        } else {
            String id = dirMap.get(location);
            return id == null ? location : id;
        }
    }
    
    /**
     * Returns an ID of the required library, taking ID map into account if necessary.
     * @param library Library descriptor that is a part of plugin descriptor.
     * @return Library ID to be used in identifying the jar file.
     */
    protected String getId(LibInfo library) {
        String id = library.getId();
        if (library.getVersion() == null) id = idMapGet(library.getDir(), id);
        return id;
    }
    
    /** Translate library IDs in a plugin descriptor using ID map. */
    private void translateLibraryIDs(PluginInfo plugin) {
        Map<String,String> idMap = name2id.get(plugin.getDirectory());
        if (idMap != null) {
            for (LibInfo lib : plugin.getLibraries()) {
                if (lib.getVersion() == null) {
                    String translatedID = idMap.get(lib.getLocation());
                    if (translatedID != null) lib.setId(translatedID);
                }
            }
        }
    }
    
    /**
     * Constructs abstract absolute path to a library file based on its ID and version.
     * 
     * @param library
     * @param extension File extension.
     */
    private File makePath(LibInfo library, String extension) {
        String version = library.getVersion();
        StringBuilder sb = new StringBuilder(library.getId());
        if (version != null) sb.append('-').append(version);
        sb.append('.').append(extension);
        File out = new File(app.getExtensionsDir(library.getDir()), sb.toString());
        return out.getAbsoluteFile();
    }

    /**
     * Checks whether the file is on the extensions class loader classpath.
     * This method should only be called on the master map.
     */
    private boolean isLoaded(File file) {
        ExtensionClassLoader loader = app.getExtensionLoader();
        if (loader == null) return false;
        URL[] classpath = loader.getURLs();
        try {
            URL url = file.toURI().toURL();
            for (URL u : classpath) {
                if (u.equals(url)) return true;
            }
        } catch (MalformedURLException x) {
        }
        return false;
    }
    
    /**
     * Returns <tt>true</tt> if a plugin with the same name and directory as the argument is currently loaded.
     */
    private boolean isLoaded(PluginInfo plugin) {
        String name = plugin.getName();
        PluginDir dir = plugin.getDirectory();
        for (PluginInfo loaded : app.getPlugins()) {
            if (name.equals(loaded.getName()) && dir == loaded.getDirectory()) {
                return true;
            }
        }
        return false;
    }
    
    private void updateIdMap() {
      
        HashSet<String> builtin = new HashSet<String>();
        Map<String,LibInfo> bm = libMap.get(PluginDir.BUILTIN);
        if (bm != null) builtin.addAll(bm.keySet());

        for (PluginDir dir : PluginDir.sgu()) {
            
            // remove unnecessary entries
          
            Map<String,PluginInfo> pn2pi = pluginMap.get(dir);
            if (pn2pi == null) name2id.remove(dir);
            Map<String,String> n2id = name2id.get(dir);
            if (n2id != null) {
                Set<String> names = new HashSet<String>();
                for (PluginInfo plugin : pn2pi.values()) {
                    for (LibInfo lib : plugin.getLibraries()) {
                        names.add(lib.getLocation());
                    }
                }
                Set<String> ids = new HashSet<String>();
                Map<String,LibInfo> libs = libMap.get(dir);
                if (libs != null) ids.addAll(libs.keySet());
                ids.addAll(builtin);
                Iterator<Map.Entry<String, String>> it = n2id.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> e = it.next();
                    String name = e.getKey();
                    if (!names.contains(name) || ids.contains(name)) {
                        it.remove();
                    }
                }
                if (n2id.isEmpty()) {
                    pluginMap.remove(dir);
                    n2id = null;
                }
            }
            
            // save into files
            
            File f = new File(app.getExtensionsDir(dir), ID_MAP_NAME);
            if (f.exists()) f.delete();
            if (n2id != null) {
                try {
                    PrintWriter pw = new PrintWriter(f);
                    for (Map.Entry<String, String> e : n2id.entrySet()) {
                        pw.println(e.getKey() +" "+ e.getValue());
                    }
                    pw.close();
                } catch (FileNotFoundException x) {
                    throw new IllegalArgumentException(x);
                }
            }
            
        }
    }
    
    private void saveIdMap(PluginDir dir) {
        Map<String,String> dirMap = name2id == null ? null : name2id.get(dir);
        String dirName = app.getExtensionsDir(dir);
        if (dirName == null) return;
        File f = new File(dirName, ID_MAP_NAME);
        if (f.exists()) f.delete();
        if (dirMap == null || dirMap.isEmpty()) return;
        try {
            PrintWriter pw = new PrintWriter(f);
            for (Map.Entry<String, String> e : dirMap.entrySet()) {
                pw.println(e.getKey() + " " + e.getValue());
            }
            pw.close();
        } catch (FileNotFoundException x) {
            throw new IllegalArgumentException(x);
        }
    }
    
    private boolean markFileForDeletion(File file) {
        try {
            (new FileOutputStream(file)).close();
            return true;
        } catch (IOException x) {
            return false;
        }
    }
    
// -----------------------------------------------------------------------------    
}
