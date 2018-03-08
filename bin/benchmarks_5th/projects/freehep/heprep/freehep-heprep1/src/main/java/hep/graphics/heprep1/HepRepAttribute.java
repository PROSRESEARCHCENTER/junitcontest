// Copyright FreeHEP 2000-2005.
package hep.graphics.heprep1;

import java.util.Enumeration;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepAttribute.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface HepRepAttribute {

    /**
     * @param name case insensitive name of attribute definition
     * @return attribute definition
     */
    public HepRepAttDef getAttDef(String name);

    /**
     * @param name case insensitive name of attribute value
     * @return attribute value
     */
    public HepRepAttValue getAttValue(String name);
    
    /**
     * @return parent
     */
    public HepRepAttribute getParent();
    
    /**
     * @param name attribute name
     * @return attribute value on node
     */
    public HepRepAttValue getAttValueFromNode(String name);
    
    /**
     * @return all attribute values
     */
    public Enumeration/*<HepRepAttValue>*/ getAttValues();
    
    /**
     * @param name definition name
     * @return attribute definition on node
     */
    public HepRepAttDef getAttDefFromNode(String name);
    
    /**
     * @return all attribute definitions
     */
    public Enumeration/*<HepRepAttDef>*/ getAttDefs();

    /**
     * @param key attribute name
     * @param value attribute value
     */
    public void addValue(String key, String value);

    /**
     * @param key attribute name
     * @param value attribute value
     * @param showLabel what to show as label
     */
    public void addValue(String key, int value, int showLabel);

    /**
     * @param key attribute name
     * @param value attribute value
     * @param showLabel what to show as label
     */
    public void addValue(String key, double value, int showLabel);

    /**
     * @param key attribute name
     * @param value attribute value
     * @param showLabel what to show as label
     */
    public void addValue(String key, boolean value, int showLabel);

    /**
     * @param key attribute name
     * @param value attribute value
     * @param showLabel what to show as label
     */
    public void addValue(String key, String value, int showLabel);

    /**
     * @param key attribute name
     * @param colorName attribute value
     * @param showLabel what to show as label
     */
    public void addColor(String key, String colorName, int showLabel);            

    /**
     * @param key attribute name
     * @param r red value
     * @param g green value
     * @param b blue value
     * @param alpha alpha value
     * @param showLabel what to show as label
     */
    public void addColor(String key, double r, double g, double b, double alpha, int showLabel);

    /**
     * @param name attribute definition name
     * @param desc of attribute description
     * @param type type of the attribute
     * @param extra unit of attribute
     */
    public void addDefinition(String name, String desc, String type, String extra);
}        
