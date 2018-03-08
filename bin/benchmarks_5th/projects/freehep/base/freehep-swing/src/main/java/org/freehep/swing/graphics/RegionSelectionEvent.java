// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;

/**
 * Instances of this class are created when the user graphically
 * selects a region on the screen.  This event gives the object which
 * generated this event, as well as a polygon describing the outline
 * of the region and an AffineTransform which can be used to zoom into
 * the selected region.
 *
 * @author Charles Loomis
 * @version $Id: RegionSelectionEvent.java 8584 2006-08-10 23:06:37Z duns $ */
public class RegionSelectionEvent
    extends GraphicalSelectionEvent {

    /**
     * The constructor requires the source object, the selection type,
     * and the actual selection. 
     *
     * @param source the Object which generates this event 
     * @param selection a Polygon describing the outline of the region
     * @param transform an AffineTransform appropriate for zooming
     * into the selected region */
    public RegionSelectionEvent(Object source, 
                                int actionCode,
                                Polygon selection,
                                AffineTransform transform) {
        super(source,actionCode,selection,transform);
    }
}
