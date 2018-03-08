package org.freehep.webutil.util.properties;

import java.io.InputStream;
import java.util.Properties;


/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */

public abstract class PropertiesLoader {
    
    private static boolean propertiesLoaded = false;
    private static String propertiesFile = "/freehepWebapp.properties";
    private static Properties props = new Properties();
    
    private static void loadProperties() {
        
        if ( ! propertiesLoaded ) {
            propertiesLoaded = true;
            
            InputStream input = null;
            try {
                input = PropertiesLoader.class.getResourceAsStream(propertiesFile);
                if (input != null)
                    props.load(input);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (input!= null) {
                        input.close();
                    }
                } catch (java.io.IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static String property(String property) {
        return property(property, null);
    }
    
    public static String property(String property, String defaultValue) {
        if ( !propertiesLoaded )
            loadProperties();
        return props.getProperty(property, defaultValue);
    }
    
    public static String filterPath() {
        return property("freehep.nonavailablefilter.path", "/admin/available");
    }
    
    public static String defaultReason() {
        return property("freehep.nonavailablefilter.defaultreason");
    }
    
    public static String nonAvailablePage() {
        return property("freehep.nonavailablefilter.redirectpage");
    }
    
    public static String adminPassword() {
        return property("freehep.nonavailablefilter.password");
    }
    
    public static String tabsId() {
        return property("freehep.tabs.id");
    }
    
    public static String tabsUsestylesheet() {
        return property("freehep.tabs.usestylesheet","false");
    }
    
    public static String tabsColor() {
        return property("freehep.tabs.color","#d1fae7");
    }
    
    public static String tabsBkgColor() {
        return property("freehep.tabs.bkgColor","white");
    }
    public static String tabsSelectedColor() {
        return property("freehep.tabs.selectedColor","#a2d7c8");
    }
    public static String tabsPosition() {
        return property("freehep.tabs.position","top");
    }
    /*
    public static String tabsAlign() {
        return property("freehep.tabs.align","left");
    }
     */
    public static String tabsMargin() {
        return property("freehep.tabs.margin","10px");
    }
    
    public static String tabsShowline() {
        return property("freehep.tabs.showline","false");
    }
    
    public static String tabsTextStyle() {
        return property("freehep.tabs.textstyle","font-family: verdana, arial, sans-serif;color: black;");
    }
    
    public static String tabsSelectedTextStyle() {
        return property("freehep.tabs.selectedtextstyle","font-family: verdana, arial, sans-serif;color: black;font-weight: bold;");
    }
    
    public static String treeFolderStyle() {
        return property("freehep.tree.folderstyle","font-size: 10pt; font-weight: bold;");
    }

    public static String treeLeafStyle() {
        return property("freehep.tree.leafstyle","font-size: 10pt; font-weight: bold;");
    }
}