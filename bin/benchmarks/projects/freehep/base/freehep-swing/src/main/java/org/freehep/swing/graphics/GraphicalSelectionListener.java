// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.util.EventListener;

/**
 * This listener interface is intended for objects which wish to be
 * notified of specific graphical selections made by the user.  The
 * "graphical selection" is a general term which should be made
 * appropriately concrete by subclasses of GraphicalSelectionEvent and
 * of GraphicalSelectionPanel. 
 *
 * @author $Author: duns $
 * @version $Id: GraphicalSelectionListener.java 8584 2006-08-10 23:06:37Z duns $ */
public interface GraphicalSelectionListener
    extends EventListener {

    /**
     * Called when the user makes a graphical selection.  The
     * graphical selection is an abstract idea which should be make
     * specific by subclasses of the GraphicalSelectionEvent and of
     * GraphicalSelectionPanel. */
    public void graphicalSelectionMade(GraphicalSelectionEvent gsEvent);

}
