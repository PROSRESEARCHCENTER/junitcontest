// Copyright 2000, CERN, Geneva, Switzerland and SLAC, Stanford, U.S.A.
package org.freehep.util;

import java.io.*;
import java.util.*;

/**
 * Methods for Package Info (version, name, ...)
 *
 * @author Mark Donszelmann
 * @version $Id: PackageInfo.java 8584 2006-08-10 23:06:37Z duns $
 */

public class PackageInfo {

    // static class
    private PackageInfo() {
    }
    
    /**
     * retrieves the name
     */
    public static String getName(Class clazz, String name) {
        return getInfo(clazz, name, "TITLE");
    }
    
    
    /**
     * retrieves the version
     */
    public static String getVersion(Class clazz, String name) {
        return getInfo(clazz, name, "VERSION");
    }
    
    /**
     * retrieves the info for the package of this class
     * either from the MANIFEST file or from the given text file
     * situated at the root of the jar file
     */
    public static String getInfo(Class clazz, String name, String property) {
        Package p = clazz.getPackage();
        String info = null;
        if (p != null) {
            if (property.equals("TITLE")) {
                info = p.getSpecificationTitle();
            } else if (property.equals("VERSION")) { 
                info = p.getSpecificationVersion();
            }
        }
        
        if (info == null) {
            try {
                Properties props = new Properties();
                InputStream in = clazz.getResourceAsStream("/"+name+"-version.txt");
                props.load(in);
                in.close();
    
                info = props.getProperty(property);
            } catch (IOException ioe) {
            } catch (NullPointerException npe) {
            }
        }
        return info;
    }
}
