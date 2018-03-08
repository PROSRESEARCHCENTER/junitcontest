/*
 * HasPopupItems.java
 * Created on March 13, 2001, 1:56 PM
 */
package org.freehep.swing.popup;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JPopupMenu;

/**
 * Implemented by any component that wants to contribute to a popup menu.
 * When an Application processes a popup trigger event it searches for the
 * deepest component beneath event, then works up throught all the parents
 * checking each component in turn to see if it implements HasPopupItems, and 
 * it so calling its modifyPopupMenu item.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: HasPopupItems.java 8584 2006-08-10 23:06:37Z duns $ 
 */
public interface HasPopupItems 
{
    /**
     * Allows a component to create or modify a popup menu
     * @param menu The menu created by the components descendents
     * @param source The deepest component
     * @return The modified menu
     */
    public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p);
}

