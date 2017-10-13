// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/** 
 * This layout manager places all of the components the same size and
 * places them at the same position.  This is an appropriate layout
 * manager for a JLayeredPane. 
 *
 * @author Charles Loomis
 * @version $Id: StackedLayout.java 8584 2006-08-10 23:06:37Z duns $
 */

public class StackedLayout 
    implements LayoutManager {

    /**
     * Create a new StackedLayout manager. */
    public StackedLayout() {
    }

    /**
     * Adds the specified component with the specified name to the
     * layout.  (This method actually does nothing since the component
     * list is obtained from the container directly.) 
     * 
     * @param name the name of the component
     * @param comp the component to add */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout.  (This method
     * actually does nothing since the component list is obtained from
     * the container directly.)
     *
     * @param comp the component to be removed */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Calculates the preferred size dimensions for the specified
     * panel given the components in the specified parent container.
     *
     * @param parent the component to be laid out
     *  
     * @see #minimumLayoutSize */
    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize(parent, true);
    }

    /** 
     * Calculates the minimum size dimensions for the specified panel
     * given the components in the specified parent container.
     *
     * @param parent the component to be laid out
     *
     * @see #preferredLayoutSize */
    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent, false);
    }
    
    /** 
     * Scan the list of components and pick out the largest width and
     * height.
     *
     * @param parent the container in which to do the layout.
     * @param isPreferred true for calculating preferred size,
     *                    false for calculating minimum size.
     *
     * @return the largest width and height needed */
    protected Dimension getLayoutSize(Container parent,
				      boolean isPreferred) {

	// Get the number of components in the parent container.
        int n = parent.getComponentCount();

	// Loop over the components and get the maximum widths and
	// heights.
	Dimension returnSize = new Dimension();
        for (int i=0; i<n; i++) {
            Component c = parent.getComponent(i);
            if (c!=null) {
                Dimension componentSize;
                if (isPreferred) {
                    componentSize = c.getPreferredSize();
                } else {
                    componentSize = c.getMinimumSize();
                }
                returnSize.width = Math.max(returnSize.width,
					    componentSize.width);
                returnSize.height = Math.max(returnSize.height,
					     componentSize.height);
            }
        }

	// Get the insets of this panel.
	Insets insets = parent.getInsets();

	// Add in the additional width and height from the border.
	returnSize.width += (insets.left + insets.right);
	returnSize.height += (insets.top + insets.bottom);

	// Send it back.
        return returnSize;
    }

    /** 
     * Lays out the components in the specified container.
     *
     * @param parent the component which needs to be laid out */
    public void layoutContainer(Container parent) {

	// Lock the component tree while the layout is in progress.
        synchronized (parent.getTreeLock()) {

	    // Get the container's insets and the number of components.
            Insets insets = parent.getInsets();
            int n = parent.getComponentCount();
	    	    
            if (n>0) {		
		// Total dimensions of the parent.  We will make all
		// of the components of this size.
		Dimension size = parent.getSize();
		int width = size.width - (insets.left + insets.right);
		int height = size.height - (insets.top + insets.bottom);

		// Actually loop over the components and set the size
		// to the full width and height.
		for (int i=0; i<n; i++) {
		    Component c = parent.getComponent(i);
		    c.setBounds(insets.left, insets.top, width, height);
		}
            }
        }
    }
    
}
