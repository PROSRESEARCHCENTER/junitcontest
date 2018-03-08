// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import java.io.*;
import java.util.*;

/**
 * @author Mark Donszelmann
 * @version $Id: AidUtil.java 8584 2006-08-10 23:06:37Z duns $
 */
public class AidUtil {

    private AidUtil() {}

    public static void loadProperties(Properties properties, Class clazz, String propDir, String name) {
        // load system defaults
        try {
            properties.load(clazz.getResourceAsStream(name));
        } catch (IOException ioe) {
            System.err.println("Could not load aid property file: "+name);
            System.err.println(ioe);
        }

        // load user defaults
        try {
            if ((propDir != null) && !propDir.equals(".") && !propDir.equals("")) {
                name = propDir + File.separator + name;
            }
            properties.load(new FileInputStream(name));
//            System.out.println("Loaded user property file: "+name);
        } catch (IOException ioe) {
        }
    }
}
