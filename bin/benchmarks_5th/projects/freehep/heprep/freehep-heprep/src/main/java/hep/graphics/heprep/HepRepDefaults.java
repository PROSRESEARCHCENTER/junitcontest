// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep;

import java.util.Set;

/**
 * Handles default values for HepRep
 * 
 * @author Mark Donszelmann
 * @version $Id: HepRepDefaults.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepDefaults {
    private static HepRepType defaults;
    static {
        try {
            defaults = HepRepFactory.create().createHepRepType((HepRepType)null, "HepRepDefaults");
        } catch (Exception e) {
            System.err.println("Problem loading HepRepDefaults, cannot find or instantiate HepRepType");
        }
    }

    // Singleton
    private HepRepDefaults() {
    }

    /**
     * Add attribute definition
     * @param attDef attribute definition
     */
    public static void addAttDef(HepRepAttDef attDef) {
        defaults.addAttDef(attDef);
    }

    /**
     * Lookup attribute definition
     * @param lowerCaseName name in lower case
     * @return attribute definition or null
     */
    public static HepRepAttDef getAttDef(String lowerCaseName) {
        return defaults.getAttDefFromNode(lowerCaseName);
    }

    /**
     * Return all attribute definitions
     * @return set of attribute definitions
     */
    public static Set/*HepRepAttDef*/ getAttDefs() {
        return defaults.getAttDefsFromNode();
    }

    /**
     * Add attribute value
     * @param attValue attribute value
     */
    public static void addAttValue(HepRepAttValue attValue) {
        defaults.addAttValue(attValue);
    }

    /**
     * Lookup attribute value
     * @param lowerCaseName name in lowercase
     * @return attribute value or null
     */
    public static HepRepAttValue getAttValue(String lowerCaseName) {
        return defaults.getAttValueFromNode(lowerCaseName);
    }
    
    /**
     * Return all attribute values
     * @return set of attribute values
     */
    public static Set/*<HepRepAttValue>*/ getAttValues() {
        return defaults.getAttValuesFromNode();
    }
}