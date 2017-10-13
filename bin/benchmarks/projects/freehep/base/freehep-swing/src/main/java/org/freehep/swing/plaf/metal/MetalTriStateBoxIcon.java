// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.plaf.metal.MetalCheckBoxIcon;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.freehep.swing.JTriStateBox;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: MetalTriStateBoxIcon.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MetalTriStateBoxIcon extends MetalCheckBoxIcon {

    protected void drawCheck(Component c, Graphics g, int x, int y) {
    	int controlSize = getControlSize();
        JTriStateBox b = (JTriStateBox)c;
        switch(b.getTriState()) {
            case -1:    // half
                Color color = g.getColor();
    	        g.setColor( MetalLookAndFeel.getControlShadow() );
    	        g.fillRect( x+2, y+2, controlSize-3, controlSize-3);
    	        g.setColor(color);
                super.drawCheck(c, g, x, y);
                break;
            case 0:     // false
                break;
            case 1:     // true
                super.drawCheck(c, g, x, y);
       	        break;
       	}
    }        
}
