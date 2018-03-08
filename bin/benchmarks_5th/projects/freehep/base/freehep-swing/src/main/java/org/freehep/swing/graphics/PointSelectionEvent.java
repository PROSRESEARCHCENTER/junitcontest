// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.Point;
import java.awt.geom.AffineTransform;

/**
 * Instances of this class are created when the user graphically
 * selects a point on the screen.  This event gives the object which
 * generated this event, as well as a polygon describing the outline
 * of the region and an AffineTransform which can be used to zoom into
 * the selected region.
 *
 * @author Charles Loomis
 * @version $Id: PointSelectionEvent.java 8584 2006-08-10 23:06:37Z duns $ */
public class PointSelectionEvent
    extends GraphicalSelectionEvent {

    /**
     * The constructor requires the source object and the actual
     * point selected.  The transform should be the one which will
     * center the display on this point if applied.
     *
     * @param source the Object generating this event
     * @param selection the Point which is selected 
     * @param transform the AffineTransform which will center the
     * display on the selected point */
    public PointSelectionEvent(Object source, 
                               int actionCode,
                               Point selection,
                               AffineTransform transform) {
        super(source,actionCode,selection,transform);
    }

}
