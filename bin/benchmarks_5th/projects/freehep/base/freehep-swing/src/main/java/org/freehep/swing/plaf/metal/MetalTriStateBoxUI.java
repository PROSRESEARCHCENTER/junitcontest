// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing.plaf.metal;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalCheckBoxUI;



/**
 *
 * @author Mark Donszelmann
 * @version $Id: MetalTriStateBoxUI.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MetalTriStateBoxUI extends MetalCheckBoxUI {
    
    private final static MetalTriStateBoxUI tristateUI = new MetalTriStateBoxUI();

    private final static String propertyPrefix = "CheckBox" + ".";

    private boolean defaults_initialized = false;

    public static ComponentUI createUI(JComponent b) {
        return tristateUI;
    }

    public String getPropertyPrefix() {
	    return propertyPrefix;
    }

    public void installDefaults(AbstractButton b) {
       	super.installDefaults(b);
    	if(!defaults_initialized) {
    	    icon = new MetalTriStateBoxIcon();
    	    defaults_initialized = true;
    	}
    }
    
    protected void uninstallDefaults(AbstractButton b) {
    	super.uninstallDefaults(b);
    	defaults_initialized = false;
    }
}
