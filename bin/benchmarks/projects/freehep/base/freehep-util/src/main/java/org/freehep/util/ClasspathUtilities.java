package org.freehep.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 *
 * @author turri
 */
public abstract class ClasspathUtilities {

    /**
     * Utility method to extract all jar files in Class-Path manifest information
     * for jar files in the System Classpath.
     *
     * The resulting path is set as a System property for the provided name.
     *
     * @param property
     */
    public static void setFullSystemClasspathInSystemProperty(String property) {
        StringBuilder appPath = new StringBuilder();

        URL[] urls = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
        HashMap<String, String> jarFilesFullPathHash = new HashMap<>();
        for (URL url : urls) {
            try {
                URI uri = new URI(url.toExternalForm());
                File file = new File(uri.normalize());
                jarFilesFullPathHash.put(file.getName(), file.getAbsolutePath());
                if (!file.isDirectory()) {
                    try {
                        JarFile jar = new JarFile(file);
                        Manifest manifest = jar.getManifest();
                        String manifestClassPath = null;
                        try {
                            manifestClassPath = manifest.getMainAttributes().getValue("Class-Path").trim();
                        } catch (NullPointerException x) {
                        }
                        File parentDir = file.getParentFile();
                        if (manifestClassPath != null) {
                            StringTokenizer classPathTokens = new StringTokenizer(manifestClassPath, " ");
                            while (classPathTokens.hasMoreTokens()) {
                                String manifestClassPathJar = classPathTokens.nextToken();
                                File cpEntry = new File(parentDir, manifestClassPathJar);
                                if (cpEntry.exists() && !jarFilesFullPathHash.containsKey(manifestClassPathJar)) {
                                    jarFilesFullPathHash.put(manifestClassPathJar, cpEntry.getAbsolutePath());
                                }
                            }
                        }
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                }
            } catch (URISyntaxException x) {
                x.printStackTrace();
            }
        }

        String javaClassPath = System.getProperty("java.class.path");
        if (javaClassPath != null) {
            StringTokenizer st = new StringTokenizer(javaClassPath, File.pathSeparator);
            while (st.hasMoreTokens()) {
                String classPathEntry = st.nextToken();
                File cpEntry = new File(classPathEntry);
                if (cpEntry.exists() && !jarFilesFullPathHash.containsKey(cpEntry.getName())) {
                    jarFilesFullPathHash.put(classPathEntry, cpEntry.getAbsolutePath());
                }
            }
        }

        Iterator<String> keysIter = jarFilesFullPathHash.keySet().iterator();
        while (keysIter.hasNext()) {
            String jarFullPath = jarFilesFullPathHash.get(keysIter.next());
            appPath.append(jarFullPath);
            if (keysIter.hasNext()) {
                appPath.append(File.pathSeparatorChar);
            }

        }

        System.setProperty(property, appPath.toString());
    }

}
