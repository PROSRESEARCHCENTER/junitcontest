// Copyright 2000-2004, FreeHEP.
package hep.graphics.heprep;

import java.awt.Color;


/**
 * HepRepAttributeAdapter. The implementor is called back for changes
 * of attributes while using the HepRepIterator to iterate over all the HepRepInstances.
 *
 * @author Mark Donszelmann
 */
public class HepRepAttributeAdapter implements HepRepAttributeListener {

    public void setAttribute(HepRepInstance instance, String key, String value, String lowerCaseValue, int showLabel) {
    }

    public void setAttribute(HepRepInstance instance, String key, Color value, int showLabel) {
    }

    /**
     * If not overridden will call "long" setAttribute.
     */
    public void setAttribute(HepRepInstance instance, String key, int value, int showLabel) {
        // NOTE: see JHEPREP-57 and HEPREP-19  
        setAttribute(instance, key, (long)value, showLabel);
    }

    /**
     * If not overridden will call "double" setAttribute.
     */
    public void setAttribute(HepRepInstance instance, String key, long value, int showLabel) {
        // NOTE: see JHEPREP-57 and HEPREP-19
        setAttribute(instance, key, (double)value, showLabel);
    }

    public void setAttribute(HepRepInstance instance, String key, double value, int showLabel) {
        // NOTE: see JHEPREP-57 and HEPREP-19
    }

    public void setAttribute(HepRepInstance instance, String key, boolean value, int showLabel) {
    }

    public void removeAttribute(HepRepInstance instance, String key){
    }
} 
