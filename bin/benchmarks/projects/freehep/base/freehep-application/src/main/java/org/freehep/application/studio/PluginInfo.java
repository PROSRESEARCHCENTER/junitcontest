package org.freehep.application.studio;

import java.util.*;
import org.freehep.util.VersionComparator;
import org.jdom.Element;

/**
 * Plugin descriptor. 
 * The plugin may or may not be downloaded or started.
 *
 * @author tonyj
 * @version $Id: PluginInfo.java 16195 2014-11-12 19:04:56Z onoprien $
 */
public class PluginInfo implements Comparable<PluginInfo> {
    
// -- Private parts : ----------------------------------------------------------
    
    private static final String[] NOCATEGORY = new String[0];
    
    private final String name;
    private final String author;
    private final String version;
    private final String mainClass;
    private String title;
    private String description;
    private final String category;
    private final boolean defaultLoadAtStart;
    private final Map<String,String> properties;
    private final String[] j2se; // [minVersion, maxVersion]
    private final String[] appVersion; // [minVersion, maxVersion]
    private final Map<String,String[]> depends; // plugin name --> [minVersion, maxVersion]
    private ArrayList<LibInfo> files;
    
    private Throwable errorStatus;
    private Plugin plugin;
    private PluginDir directory;
    private boolean loadAtStart;

// -- Construction : -----------------------------------------------------------
    
    public PluginInfo(String name, String author, String version, String mainClass, String title, 
                      String description, String category, boolean defaultLoadAtStart) {
        this(name, author, version, mainClass, title, description, category, defaultLoadAtStart, null, null, null, null);
    }
    
    public PluginInfo(String name, String author, String version, String mainClass, String title, 
                      String description, String category, boolean defaultLoadAtStart,
                      Map<String,String> properties, String[] j2se, String[] appVersion, Map<String,String[]> depends) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.mainClass = mainClass;
        this.title = title;
        this.description = description;
        this.category = category;
        this.defaultLoadAtStart = defaultLoadAtStart;
        this.loadAtStart = defaultLoadAtStart;
        this.properties = properties;
        this.j2se = j2se;
        this.appVersion = appVersion;
        this.depends = depends;
    }
    
    /**
     * Builds a PluginInfo from a JDOM element
     */
    public PluginInfo(Element node) {
      
        Element info = node.getChild("information");
        name = info.getChildTextNormalize("name");
        author = info.getChildTextNormalize("author");
        category = info.getChildTextNormalize("category");

        String version = verifyVersion(info.getChildTextNormalize("version"));
        this.version = version == null ? "1.0.0" : version;
        
        defaultLoadAtStart = loadAtStart = info.getChild("load-at-start") != null;
        Element pluginDescription = node.getChild("plugin-desc");
        mainClass = pluginDescription == null ? null : pluginDescription.getAttributeValue("class");
        List<Element> desc = info.getChildren("description");
        for (Element d : desc) {
            String type = d.getAttributeValue("type");
            String text = d.getTextNormalize();
            if (type != null && type.equals("short")) {
                title = text;
            } else {
                description = text;
            }
        }

        String[] j2se = null;
        String[] appVersion = null;
        Map<String,String[]> depends = null;
        Map<String,String> properties = null;
        Element resources = node.getChild("resources");
        if (resources != null) {

            Element e = resources.getChild("j2se");
            if (e != null) {
                String minVersion = verifyVersion(e.getAttributeValue("minVersion"));
                String maxVersion = verifyVersion(e.getAttributeValue("maxVersion"));
                if (!(minVersion == null && maxVersion == null)) j2se = new String[]{minVersion, maxVersion};
            }

            e = resources.getChild("application");
            if (e != null) {
                String minVersion = verifyVersion(e.getAttributeValue("minVersion"));
                String maxVersion = verifyVersion(e.getAttributeValue("maxVersion"));
                if (!(minVersion == null && maxVersion == null)) appVersion = new String[]{minVersion, maxVersion};
            }
             
            List<Element> depList = resources.getChildren("depends");
            if (!depList.isEmpty()) {
                depends = new HashMap<String, String[]>();
                for (Element f : depList) {
                    String plug = f.getAttributeValue("plugin");
                    String minVersion = verifyVersion(f.getAttributeValue("minVersion"));
                    String maxVersion = verifyVersion(f.getAttributeValue("maxVersion"));
                    String[] v = (minVersion == null && maxVersion == null) ? null : new String[]{minVersion, maxVersion};
                    depends.put(plug, v);
                }
            }
           
            List<Element> fileList = resources.getChildren("file");
            if (!fileList.isEmpty()) {
                files = new ArrayList<LibInfo>(fileList.size());
                for (Element f : fileList) {
                    String href = f.getAttributeValue("href");
                    String location = f.getAttributeValue("location");
                    files.add(new LibInfo(location, href));
                }
            }
            
            List<Element> propList = resources.getChildren("property");
            if (!propList.isEmpty()) {
                properties = new HashMap<String,String>();
                for (Element f : propList) {
                    String propertyName = f.getAttributeValue("name");
                    String value = f.getAttributeValue("value");
                    properties.put(propertyName, value);
                }
            }
            
        }
        
        this.j2se = j2se;
        this.appVersion = appVersion;
        this.depends = depends;
        this.properties = properties;
    }
    
    /**
     * Copy constructor.
     * Deep copies of all effectively mutable fields are made.
     */
    public PluginInfo(PluginInfo original) {
        name = original.name;
        author = original.author;
        version = original.version;
        mainClass = original.mainClass;
        title = original.title;
        description = original.description;
        category = original.category;
        defaultLoadAtStart = original.defaultLoadAtStart;
        properties = original.properties;
        j2se = original.j2se;
        appVersion = original.appVersion;
        depends = original.depends;
        files = new ArrayList<LibInfo>(original.files.size());
        for (LibInfo lib : original.files) files.add(new LibInfo(lib));

        errorStatus = original.errorStatus;
        directory = original.directory;
        loadAtStart = original.loadAtStart;
    }
    
// -- Getters : ----------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }
    
    public String getMainClass() {
        return mainClass;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Deprecated
    public String[] getCategory() {
      return category == null ? NOCATEGORY : category.split("\\.");
    }

    public List<String[]> getCategories() {
      if (category != null) {
        String[] tokens = category.split("\\,", -1);
        List<String[]> out = new ArrayList<String[]>(tokens.length);
        for (String token : tokens) {
          String[] cat = token.trim().split("\\.");
          if (cat.length == 1 && cat[0].isEmpty()) cat = NOCATEGORY;
          if (!out.contains(cat)) out.add(cat);
        }
        return out;
      } else {
        return null;
      }
    }

    public Map<String,String> getProperties() {
        return properties == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(properties);
    }
    
    public String getJavaMinVersion() {
        return j2se == null ? null : j2se[0];
    }
    
    public String getJavaMaxVersion() {
        return j2se == null ? null : j2se[1];
    }
    
    public String getApplicationMinVersion() {
        return appVersion == null ? null : appVersion[0];
    }
    
    public String getApplicationMaxVersion() {
        return appVersion == null ? null : appVersion[1];
    }
    
    public Set<String> getRequiredPluginNames() {
        return depends == null ? Collections.<String>emptySet() : Collections.unmodifiableSet(depends.keySet());
    }
    
    public String getRequiredPluginMinVersion(String pluginName) {
        if (depends == null) return null;
        String[] ver = depends.get(pluginName);
        return ver == null ? null : ver[0];
    }
    
    public String getRequiredPluginMaxVersion(String pluginName) {
        if (depends == null) return null;
        String[] ver = depends.get(pluginName);
        return ver == null ? null : ver[1];
    }

    public List<LibInfo> getLibraries() {
        return files == null ? Collections.<LibInfo>emptyList() : Collections.unmodifiableList(files);
    }
    
    public boolean isLoadAtStart() {
        return loadAtStart;
    }

    public boolean hasMainClass() {
        return mainClass != null;
    }

    public PluginDir getDirectory() {
        return directory;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Throwable getErrorStatus() {
        return errorStatus;
    }

    /**
     * Returns <tt>true</tt> if this plugin is compatible with the application version.
     * Also returns <tt>true</tt> if versions are not specified or are in unknown formats.
     */
    public boolean isApplicationCompatible(Studio app) {
        if (appVersion == null) return true;
        String currentVersion = app.getVersion();
        if (currentVersion == null) return true;
        try {
            String minVersion = appVersion[0];
            if (minVersion != null && VersionComparator.compareVersion(minVersion, currentVersion) > 0) return false;
            String maxVersion = appVersion[1];
            if (maxVersion != null && VersionComparator.compareVersion(maxVersion, currentVersion) < 0) return false;
        } catch (NumberFormatException x) {
        }
        return true;
    }

    /**
     * Returns <tt>true</tt> if this plugin is compatible with the Java runtime version.
     * Also returns <tt>true</tt> if versions are not specified or are in unknown formats.
     */
    public boolean isJavaCompatible() {
        if (appVersion == null) return true;
        String currentVersion = System.getProperty("java.version");
        if (currentVersion == null) return true;
        try {
            String minVersion = j2se[0];
            if (minVersion != null && VersionComparator.compareVersion(minVersion, currentVersion) > 0) return false;
            String maxVersion = j2se[1];
            if (maxVersion != null && VersionComparator.compareVersion(maxVersion, currentVersion) < 0) return false;
        } catch (IllegalArgumentException x) {
        }
        return true;
    }
    
    
// -- Utility methods : --------------------------------------------------------
    
    public boolean isRequiredPluginValid(PluginInfo required) {
        if (required == null) return false;
        String min = getRequiredPluginMinVersion(required.getName());
        String max = getRequiredPluginMaxVersion(required.getName());
        return (min == null || VersionComparator.compareVersion(min, required.getVersion()) <= 0) &&
               (max == null || VersionComparator.compareVersion(max, required.getVersion()) >= 0);
    }
    
    
// -- Setters : ----------------------------------------------------------------

    public void setDirectory(PluginDir directory) {
        this.directory = directory;
        if (files != null) {
            for (LibInfo lib : files) {
                lib.setDir(directory);
            }
        }
    }

    public void setLoadAtStart(boolean loadAtStart) {
        this.loadAtStart = loadAtStart;
    }

    void setErrorStatus(Throwable t) {
        errorStatus = t;
    }

    void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public void setLibraries(ArrayList<LibInfo> libraries) {
        files = libraries;
        setDirectory(getDirectory());
    }
    
    
// -- Loading and saving user preferences : ------------------------------------

    void loadUserProperties(Properties user) {
        String prop = user.getProperty("loadAtStart." + name);
        if (prop != null) {
            loadAtStart = Boolean.valueOf(prop).booleanValue();
        }
    }

    void saveUserProperties(Properties user) {
        String key = "loadAtStart." + name;
        if (loadAtStart == defaultLoadAtStart) {
            user.remove(key);
        } else {
            user.setProperty(key, String.valueOf(loadAtStart));
        }
    }

// -- Local methods : ----------------------------------------------------------
    
    private String verifyVersion(String version) {
      if (version == null) return null;
      try {
        return VersionComparator.getVersion(version).toString();
      } catch (IllegalArgumentException x) {
        return null;
      }
    }
    
    
// -- Equality, comparisons : --------------------------------------------------

    @Override
    public int compareTo(PluginInfo o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PluginInfo) {
            return name.equals(((PluginInfo) o).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}