// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.geom.AffineTransform;

/**
 * GraphicalSelectionEvent is an event which is generated when an user
 * makes a graphical selection.  This event contains information about
 * the source, the type of selection which was made, and the selection
 * type.
 *
 * @author Charles Loomis
 * @version $Id: GraphicalSelectionEvent.java 8584 2006-08-10 23:06:37Z duns $ */
public class GraphicalSelectionEvent
    extends java.util.EventObject {

    /**
     * This action code indicates no action. */
    final static public int NO_ACTION = -1;

    /**
     * This action code indicates that the view should be zoomed with the
     * given parameters in the same view. */
    final static public int ZOOM = 0;

    /**
     * This action code indicates that the view should be cloned and the zoom
     * applied to the clone. */
    final static public int ZOOM_NEW_VIEW = 1;

    /**
     * This indicates that the selection should be used to select event
     * objects.  These objects should replace any previously selected
     * objects. */
    final static public int PICK = 2;

    /**
     * This indicates that the selection should be used to select event
     * objects and that these objects should be selected in addition to any
     * previously selected objects. */
    final static public int PICK_ADD = 3;

    /**
     * This indicates that the selection should be used to select event
     * objects and that these objects should be deselected. */
    final static public int UNPICK = 4;

    /**
     * This indicates that the component should switch to the next selection
     * mode.  */
    final static public int NEXT_MODE = 10;

    /**
     * This indicates that the component should switch to the previous
     * selection mode.  */
    final static public int PREVIOUS_MODE = 11;

    /**
     * This indicates that the component should switch to the default
     * selection mode. */
    final static public int DEFAULT_MODE = 13;

    /**
     * An integer which describes what should be done with this selection. */
    protected int actionCode;

    /**
     * The Object which describes the graphical selection which has
     * been made. */
    protected Object selection;

    /**
     * The associated AffineTransform to be used if zooming into the
     * selected region is desired. */
    private AffineTransform transform;

    /**
     * The constructor requires the source object, the selection type,
     * and the actual selection. 
     *
     * @param source the Object which generates this event 
     * @param selection the selection itself
     * @param actionCode integer giving action to be done
     * @param transform an AffineTransform which will, for example,
     * zoom into a selected region */
    public GraphicalSelectionEvent(Object source, 
                                   int actionCode,
                                   Object selection,
                                   AffineTransform transform) {
        super(source);
        this.actionCode = actionCode;
        this.selection = selection;
        this.transform = transform;
    }

    /**
     * Get the Object which describes the graphical selection.
     *
     * @return Object which describes the selection */
    public Object getSelection() {
        return selection;
    }

    /**
     * Return an AffineTransform which is appropriate for this
     * selection.  For a point, the transform might center the display
     * on this point.  For a region, the transform might zoom into the
     * selected region.
     *
     * @return the appropriate AffineTransform */
    public AffineTransform getTransform() {
        if (transform!=null) {
            return (AffineTransform) transform.clone();
        } else {
            return null;
        }
    }

    /**
     * Return the action code for this event.
     *
     * @return an integer describing the action which should be taken */
    public int getActionCode() {
        return actionCode;
    }
}
