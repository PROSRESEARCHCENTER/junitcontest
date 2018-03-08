package org.freehep.application.studio;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.freehep.util.VersionComparator;

/**
 * Descriptor of a library file.
 * <p>
 * Typically, instances of this class describe either libraries referenced in a plugin 
 * descriptor, or library files installed in one of the extension directories.
 * <p>
 * In a library descriptor that is a part of a plugin descriptor (obtained from a list returned by 
 * {@link PluginInfo#getLibraries()}, <tt>href</tt> property is copied from the plugin manifest.
 * If <tt>LOCATION</tt> string found in the plugin manifest is in valid <tt>ID-VERSION.ext</tt> format,
 * both <tt>id</tt> and <tt>location</tt> properties are set to <tt>ID</tt>, and <tt>version</tt> property is set
 * to <tt>VERSION</tt>. If <tt>LOCATION</tt> string in the plugin manifest is not in valid <tt>ID-VERSION.ext</tt>
 * format, both <tt>id</tt> and <tt>location</tt> are initially set to <tt>LOCATION</tt> with stripped extension,
 * and <tt>version</tt> remains undefined. When a library descriptor with undefined <tt>version</tt> is added to
 * {@link PluginMap}, it's <tt>id</tt> property may be modified if the location-to-id map found in the application 
 * extension directory where the plugin is installed maps its <tt>location</tt> to some <tt>id</tt>.
 * <tt>dir</tt> property corresponds to the plugin installation directory, if known. All other properties are undefined.
 * <p>
 * In a library descriptor that corresponds to a file on disk, if the file name is in valid <tt>ID-VERSION.ext</tt> 
 * format, both <tt>id</tt> and <tt>location</tt> properties are set to <tt>ID</tt>, and <tt>version</tt> property is set
 * to <tt>VERSION</tt>. Otherwise, <tt>location</tt> is set to the file name (without extension), <tt>id</tt> and 
 * <tt>version</tt> are set based on maven manifest embedded in the file. If the manifest is not found, <tt>id</tt> is 
 * set to <tt>location</tt> (file name without extension), and <tt>version</tt> remains undefined. 
 * <tt>dir</tt> corresponds to the installation directory, and <tt>file</tt> is an absolute abstract path of the file.
 * <p>
 * Getters for all undefined properties return <tt>null</tt>.
 *
 * @author onoprien
 */
public class LibInfo {
    
// -- Private parts : ----------------------------------------------------------
    
    private String id;
    private String version;
    
    private String location;
    private String href;
    
    private PluginDir dir;
    private File file;

// -- Constructors : -----------------------------------------------------------

    /**
     * Constructs library descriptor from information found in a plugin manifest.
     * 
     * @param location Location as read from plugin manifest, expected to end with a file extension.
     * @param href HREF as read from plugin manifest.
     */
    public LibInfo(String location, String href) {
        this.href = href;
        int i = location.lastIndexOf('.');
        if (i != -1) location = location.substring(0, i); // strip extension
        try {
            id = VersionComparator.stripVersion(location);
            version = location.substring(id.length()+1);
        } catch (IllegalArgumentException x) {
            id = location;
        }
        this.location = id;
    }
    
    /**
     * Constructs library descriptor given a file and an installation directory.
     */
    public LibInfo(File file, PluginDir dir) {
        this.file = file.getAbsoluteFile();
        this.dir = dir;
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        if (i != -1) fileName = fileName.substring(0, i); // strip extension
        try {
            id = VersionComparator.stripVersion(fileName);
            version = fileName.substring(id.length()+1);
            location = id;
        } catch (IllegalArgumentException x) {
            location = fileName;
            if (!checkMavenID()) id = fileName;
        }
    }
    
    /**
     * Copy constructor.
     * All fields are copied. 
     */
    public LibInfo(LibInfo other) {
        id = other.id;
        version = other.version;
        location = other.location;
        href = other.href;
        dir = other.dir;
        file = other.file;
    }

// -- Field getters and setters : ----------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public PluginDir getDir() {
        return dir;
    }

    public void setDir(PluginDir dir) {
        this.dir = dir;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    
// -- Utility methods : --------------------------------------------------------
    
    /**
     * Attempts to read maven manifest and set this library id and version to maven artifact and version.
     * @return True if artifact and version were successfully extracted.
     */
    public final boolean checkMavenID() {
        if (file == null || !file.exists()) return false;
        JarFile jar = null;
        try {
            jar = new JarFile(file);
            JarEntry je = jar.getJarEntry("META-INF/maven");
            if (je != null) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    je = entries.nextElement();
                    String name = je.getName();
                    if (name.startsWith("META-INF/maven") && name.endsWith("pom.properties")) {
                        Properties p = new Properties();
                        p.load(jar.getInputStream(je));
                        
                        String mid = p.getProperty("artifactId");
                        String mver = p.getProperty("version");
                        if (mver == null || mid == null) throw new IllegalArgumentException();
                        VersionComparator.getVersion(mver);
                        id = mid;
                        version = mver;
                        return true;
                    }
                }
            }
        } catch (IOException x) {
        } catch (IllegalArgumentException x) {
        } finally {
          try {
            if (jar != null) jar.close();
          } catch (IOException x) {}
        }
        return false;
    }
        
// -----------------------------------------------------------------------------  
}
