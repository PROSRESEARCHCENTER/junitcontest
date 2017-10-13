// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.SwingUtilities;

import org.freehep.swing.images.FreeHepImage;

/**
 * This abstract class defines the majority of the functionality
 * needed to make selections of arbitrary parallelogram regions
 * on the screen.
 *
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: AbstractRegionSelectionPanel.java 8584 2006-08-10 23:06:37Z duns $ */
abstract public class AbstractRegionSelectionPanel
    extends GraphicalSelectionPanel {

    /**
     * A constant which flags that no control point was near the
     * mouse-pressed event. */
    final public static int NO_CONTROL_POINT = -1;

    /**
     * Flag indicating whether or not additional guide lines should be
     * visible. */
    protected boolean visibleGuides;

    /**
     * The bounding rectangle of the next box to be drawn. */
    protected Rectangle rectToDraw = new Rectangle();

    /**
     * The bounding rectangle of the last box which was drawn. */
    protected Rectangle lastDrawnRect = new Rectangle();

    /**
     * The bounding box of the region to repaint, usually the union of
     * the rectToDraw and lastDrawnRect rectangles. */
    protected Rectangle updateRect = new Rectangle();

    /**
     * Flag indicating whether or not the selection box is visible. */
    protected boolean visible;

    /**
     * Flag indicating whether or not the last drawn rectangle is
     * valid.  (Not valid when the box is first made visible.) */
    protected boolean lastDrawnRectValid;

    /**
     * The maximum distance from a control point the cursor can be and
     * still be selected. */
    protected int hitThreshold = 10;

    /**
     * The size of the control point boxes. */
    protected static int ctrlPtSize = 1;

    /**
     * The number of control points for this component. */
    protected int nCtrlPts;

    /**
     * Which control point is the active one, or which one can be
     * controlled from the arrow keys on the keyboard? */
    protected int activeCtrlPt;

    /**
     * The x-coordinates of the control points.  The first four of
     * these control points MUST define the outer boundries of the
     * selected region. */
    protected int[] xCtrlPts;

    /**
     * The y-coordinates of the control points.  The first four of
     * these control points MUST define the outer boundries of the
     * selected region. */
    protected int[] yCtrlPts;

    /**
     * This constructor makes a new AbstractRegionSelectionPanel.
     * This constructor only sets the visiblilty flag and the
     * last-drawn rectangle valid flag to false.  */
    public AbstractRegionSelectionPanel() {

        // First make the selection region invisible and invalidate
        // the last-drawn rectangle.
        visible = false;
        lastDrawnRectValid = false;
        setSelectionActionsEnabled(false);

        // The guides are by default visible.
        visibleGuides = true;

        // Create the arrays of the x- and y-coordinates.  There must
        // be at least four control points.
        nCtrlPts = Math.max(4,getNumberOfControlPoints());
        xCtrlPts = new int[nCtrlPts];
        yCtrlPts = new int[nCtrlPts];
        activeCtrlPt = NO_CONTROL_POINT;

        // set the default cursor
        setCursor();
    }

    /**
     * Determine whether or not to display guide lines. */
    public void setVisibleGuides(boolean visibleGuides) {
        this.visibleGuides = visibleGuides;
        repaintPanel();
    }

    /**
     * Get whether or not the guides are visible. */
    public boolean getVisibleGuides() {
        return visibleGuides;
    }

    /**
     * Process key-released events.  This allows selection panels
     * which derive from this one to automatically have the default
     * behaviour.
     *
     *<pre>
     * arrow keys:    move the active control point in the specified
     *                direction.
     * backspace key: reset selection region (make invisible).
     * delete key:    reset selection region (make invisible).
     * escape key:    leave selection mode (make component invisible).
     * tab key:       next selection mode (make next component visible).
     * enter key:     accept selection region (send off region selected
     *                event)
     * spacebar:      accept selection region (send off region selected
     *                event)
     * </pre>
     *
     * @param e KeyEvent describing the key which has been released */
    public void keyReleased(KeyEvent e) {

        // Change the size of the increment in the given direction.
        int increment = (e.isShiftDown()) ? 1 : 2;

        switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
            moveActiveControlPoint(0,-increment);
            break;
        case KeyEvent.VK_DOWN:
            moveActiveControlPoint(0,increment);
            break;
        case KeyEvent.VK_RIGHT:
            moveActiveControlPoint(increment,0);
            break;
        case KeyEvent.VK_LEFT:
            moveActiveControlPoint(-increment,0);
            break;
        default:
            super.keyReleased(e);
            break;
        }
    }

    /**
     * A utility function which creates an appropriate selection event
     * when the user accepts the current selection. */
    protected void makeSelectionEvent(int actionCode) {
        switch (actionCode) {

        case GraphicalSelectionEvent.DEFAULT_MODE:
            resetSelection();
            setVisible(false);
            fireGraphicalSelectionMade(new
                GraphicalSelectionEvent(this,
                                        GraphicalSelectionEvent.DEFAULT_MODE,
                                        null,null));
            break;

        case GraphicalSelectionEvent.NEXT_MODE:
            resetSelection();
            setVisible(false);
            fireGraphicalSelectionMade(new
                GraphicalSelectionEvent(this,
                                        GraphicalSelectionEvent.NEXT_MODE,
                                        null,null));
            break;

        case GraphicalSelectionEvent.PREVIOUS_MODE:
            resetSelection();
            setVisible(false);
            fireGraphicalSelectionMade(new
                GraphicalSelectionEvent(this,
                                        GraphicalSelectionEvent.PREVIOUS_MODE,
                                        null,null));
            break;

        default:

            if (visible) {

                // Make an array of points describing the corners of this
                // rectangular region.  NOTE: the first four control
                // points must define the outer extent of the selected
                // region.
                Point[] points = new Point[4];
                for (int i=0; i<4; i++) {
                    points[i] = new Point(xCtrlPts[i], yCtrlPts[i]);
                }

                // Send off the event to interested parties.
                fireGraphicalSelectionMade(new
                    RegionSelectionEvent(this,actionCode,
                                         makeOutlinePolygon(),
                                         makeAffineTransform()));
            }
            resetSelection();
            break;
        }
    }

    /**
     * Sets the cursor to whatever the current active control point dictates.
     */
    private void setCursor() {
        // active cursor
        Cursor cursor = getControlPointCursor(activeCtrlPt);

        // default cursor if no active cursor
        if (cursor == null) {
            cursor = getControlPointCursor(NO_CONTROL_POINT);
        }

        // set only when available
        if (cursor != null) {
            setCursor(cursor);
        }
    }

    /**
     * Changes the active control point according to mouse movements
     *
     */
    public void mouseMoved(MouseEvent e) {
        if (!isProcessingPopup(e)) {
            if (visible) {
                int newCtrlPt = nearWhichControlPoint(e.getX(), e.getY(),
                                                      hitThreshold);
                if (newCtrlPt != activeCtrlPt) {
                    activeCtrlPt = newCtrlPt;
                    setCursor();
                    repaintPanel();
                    return;
                }
            }
        }
    }

    /**
     * Handle the mousePressed events.  */
    public void mousePressed(MouseEvent e) {

        // Only do something if this isn't part of a popup menu
        // selection.
        if (!isProcessingPopup(e)) {

            // If the selection box is visible AND the user has
            // clicked near one of the existing control points, make
            // the nearest one the active control point and update the
            // current selection.  Return when finished.
            if (visible) {
                int newCtrlPt = nearWhichControlPoint(e.getX(), e.getY(),
                                                      hitThreshold);
                if (newCtrlPt>=0) {
                    activeCtrlPt = newCtrlPt;
                    setCursor();
                    repaintPanel();
                    return;
                }
            }

            // User wants to start a new selection.  So first set the
            // flag to make the selection region visible.
            visible = true;
            setSelectionActionsEnabled(true);

            // Get the mouse point and force point within boundries.
            int x = forceXCoordinateWithinBounds(e.getX());
            int y = forceYCoordinateWithinBounds(e.getY());

            // The initialize method is responsible for setting all of
            // the control points to reasonable values and for setting
            // which point should be the active one.
            activeCtrlPt = NO_CONTROL_POINT;
            initializeControlPoints(x,y);
            setCursor();

            // Update the display.
            repaintPanel();
        }
    }

    /**
     * A utility method which forces the x-coordinate to be within the
     * component boundries.
     *
     * @param x x-coordinate to force within boundries
     * @return modified x-value */
    public int forceXCoordinateWithinBounds(int x) {
        int xmin = 0;
        int xmax = getWidth()-1;
        return Math.max(Math.min(x,xmax),xmin);
    }

    /**
     * A utility method which forces the y-coordinate to be within the
     * component boundries.
     *
     * @param y y-coordinate to force within boundries
     * @return modified y-value */
    public int forceYCoordinateWithinBounds(int y) {
        int ymin = 0;
        int ymax = getHeight()-1;
        return Math.max(Math.min(y,ymax),ymin);
    }

    public void mouseDragged(MouseEvent e) {
        if (!isProcessingPopup(e)) {
            updateActiveControlPoint(e.getX(),e.getY());
            setCursor();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (!isProcessingPopup(e)) {
            updateActiveControlPoint(e.getX(),e.getY());
            if (!isValidSelection()) resetSelection();
            setCursor();
        } else {
            activeCtrlPt = NO_CONTROL_POINT;
            setCursor();
        }
    }

    /**
     * This returns whether the current selected region is valid.
     * Generally if the area has zero volume, then this method should
     * return false. */
    abstract public boolean isValidSelection();

    /**
     * A utility method which moves the currently active control point
     * by the given delta-x and delta-y.  It does this by calling
     * updateActiveControlPoint(x,y), so subclasses shouldn't normally
     * need to override this method.
     *
     * @param dx the distance to move the x-coordinate
     * @param dy the distance to move the y-coordinate */
    protected void moveActiveControlPoint(int dx, int dy) {
        if (activeCtrlPt>=0) {
            int x = xCtrlPts[activeCtrlPt] + dx;
            int y = yCtrlPts[activeCtrlPt] + dy;
            updateActiveControlPoint(x,y);
        }
    }

    /**
     * Check to see if the point (x,y) is near one of the control
     * points.  If it is, return the index of the nearest one,
     * otherwise return NO_CONTROL_POINT.
     *
     * @param x x-coordinate to compare to control points
     * @param y y-coordinate to compare to control points
     * @param maxDist the maximum distance from a control point which
     * still selects it
     *
     * @return the index of the nearest control point */
    protected int nearWhichControlPoint(int x, int y, int maxDist) {

        // Initialize to no control point selected.
        int nearestCtrlPt = NO_CONTROL_POINT;
        int minDist2 = -1;

        // Loop over all control points and get the closest one.
        // (Actually calculate distance-squared here.)
        for (int i=0; i<nCtrlPts; i++) {
            int dx = x-xCtrlPts[i];
            int dy = y-yCtrlPts[i];
            int dist = dx*dx + dy*dy;
            if (dist<minDist2 || i==0) {
                minDist2 = dist;
                nearestCtrlPt = i;
            }
        }

        // If the closest one isn't close enough, delete the index.
        if (minDist2>maxDist*maxDist)
            nearestCtrlPt = NO_CONTROL_POINT;

        return nearestCtrlPt;
    }

    /**
     * Make the selection box invisible. */
    public void resetSelection() {
        visible = false;
        lastDrawnRectValid = false;
        setSelectionActionsEnabled(false);
        activeCtrlPt = NO_CONTROL_POINT;
        setCursor();
        repaintPanel();
    }

    /**
     * Repaint the panel.  Calculate the bounding box of the selection
     * box along with associated control points.  */
    protected void repaintPanel() {

        // Find the bounding points for the polygon.
        int x0 = xCtrlPts[0];
        int y0 = yCtrlPts[0];
        int x1 = xCtrlPts[0];
        int y1 = yCtrlPts[0];
        for (int i=1; i<nCtrlPts; i++) {
            if (xCtrlPts[i]<x0) x0 = xCtrlPts[i];
            if (yCtrlPts[i]<y0) y0 = yCtrlPts[i];
            if (xCtrlPts[i]>x1) x1 = xCtrlPts[i];
            if (yCtrlPts[i]>y1) y1 = yCtrlPts[i];
        }

        // Adjust for the size of the active control point and line
        // widths.
        x0 -= ctrlPtSize+2;
        y0 -= ctrlPtSize+2;
        x1 += ctrlPtSize+2;
        y1 += ctrlPtSize+2;

        // Set the bounds of the current polygon.
        rectToDraw.setRect(x0,y0,x1-x0,y1-y0);

        // Repaint the new bounds.
        updateRect.setBounds(rectToDraw);
        if (lastDrawnRectValid) {
            updateRect = SwingUtilities.computeUnion(lastDrawnRect.x,
                                                     lastDrawnRect.y,
                                                     lastDrawnRect.width,
                                                     lastDrawnRect.height,
                                                     updateRect);
        }
        repaint(updateRect);
    }

    /**
     * Initialize the control points.  Subclasses must provide an
     * implementation of this method which initializes the control
     * points to reasonable values given the first mouse-pressed
     * coordinates, and must also set the activeCtrlPt to the index of
     * the control point which should be active.
     *
     * @param x x-coordinate of initial mouse-pressed event
     * @param y y-coordinate of initial mouse-pressed event */
    abstract public void initializeControlPoints(int x, int y);

    /**
     * Change the active control point to the point (x,y).  Subclasses
     * should implement this routine to get the behaviour which is
     * desired.  This is the place to impose constraints on how the
     * control points can move.  NOTE: repaintPanel() should be called
     * at the end of this method to update the display.
     *
     * @param x x-coordinate of the new point
     * @param y y-coordinate of the new point */
    abstract public void updateActiveControlPoint(int x, int y);

    /**
     * Useful subclasses must define the number of control points on
     * the selected region.  The first four control points define the
     * outer extent of the selected region.  This method MUST NOT
     * return a number less than four. */
    abstract public int getNumberOfControlPoints();

    /**
     * Returns the Cursor to be displayed for a certain control point
     * and the default cursor for this SelectionPanel for an index of
     * NO_CONTROL_POINT. Return of null will not change the cursor.
     * Subclasses should override this method to provide a default
     * cursor and/or to provide cursors for the different control points.
     */
    public Cursor getControlPointCursor(int index) {
        return null;
    }

    /**
     * Repaint this component.  This must be overridden by subclasses
     * so that the selection region appears correctly.  The subclass
     * should check the visibility flag (visible) to decide if any
     * painting needs to be done.
     *
     * @param g Graphics context in which to draw */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Update the last drawn rectangle.
        lastDrawnRectValid = true;
        lastDrawnRect.setBounds(rectToDraw);
    }

    /**
     * Make the outline of the selection.  Note that the order of the
     * points is not guaranteed.
     *
     * @return a polygon object which describes the outline of
     * the selection */
    public Polygon makeOutlinePolygon() {
        Polygon polygon = new Polygon();

        // Only take the first four control points, since these define
        // the outline of the selection.
        for (int i=0; i<4; i++) {
            polygon.addPoint(xCtrlPts[i], yCtrlPts[i]);
        }
        return polygon;
    }

    /**
     * Make the affine transform which corresponds to this rectangular
     * selection.
     *
     * @return AffineTransform which describes the selected region */
    abstract public AffineTransform makeAffineTransform();

    /**
     * A utility which makes an AffineTransform given three corner
     * points.  The first point must be the upper, left-hand corner
     * point, the second, the upper, right-hand corner point, and the
     * third, the lower, right-hand corner point.
     *
     * @return AffineTransform which does the appropriate mapping */
    protected AffineTransform makeTransform(double x0, double y0,
                                            double x1, double y1,
                                            double x2, double y2) {

        double sx = 0.;
        double kx = 0.;
        double tx = 0.;
        double sy = 0.;
        double ky = 0.;
        double ty = 0.;

        double delta = (x2*(y1-y0)-x1*(y2-y0)+x0*(y2-y1));

        if (delta==0) {
            return null;
        } else {

            delta = 1./delta;

            double w = getWidth();
            double h = getHeight();

            sx = -(delta*w)*(y2-y1);
            kx = (delta*w)*(x2-x1);
            tx = -(x0*sx+y0*kx);

            ky = (delta*h)*(y1-y0);
            sy = -(delta*h)*(x1-x0);
            ty = -(x0*ky+y0*sy);

            return new AffineTransform(sx,ky,kx,sy,tx,ty);
        }

    }

    /**
     * returns the appropriate cursor for any of the
     * compass points. If both dx and dy are zero, null is returned
     *
     * @param type type of cursor (Resize/Rotation)
     * @param dx screen x of direction
     * @param dy screen y of direction (positive is down)
     * @param n number of compass points (4 or 8)
     * @param diagonal in case n = 4, a diagonal compass point is returned
     * @return XX_RESIZE_CURSOR
     */
    public static Cursor compassCursor(String type, int dx, int dy, int n, boolean diagonal) {
        if ((dx == 0) && (dy == 0)) return null;
        double offset;
        if (n == 4) {
            offset = (diagonal) ? 0 : Math.PI/4;
        } else {
            n = 8;
            offset = Math.PI/8;
        }
        double delta = 2*Math.PI/n;
        double alpha = (Math.atan2(-dy, dx) + 2*Math.PI + offset) % (2*Math.PI);
        int d = (int)(alpha / delta);
        if (n == 4) {
            d = (diagonal) ? d * 2 + 1 : d * 2;
        }
        switch(d) {
            case 0: return FreeHepImage.getCursor("E_"+type+"Cursor", 16, 16);
            case 1: return FreeHepImage.getCursor("NE_"+type+"Cursor", 16, 16);
            case 2: return FreeHepImage.getCursor("N_"+type+"Cursor", 16, 16);
            case 3: return FreeHepImage.getCursor("NW_"+type+"Cursor", 16, 16);
            case 4: return FreeHepImage.getCursor("W_"+type+"Cursor", 16, 16);
            case 5: return FreeHepImage.getCursor("SW_"+type+"Cursor", 16, 16);
            case 6: return FreeHepImage.getCursor("S_"+type+"Cursor", 16, 16);
            case 7: return FreeHepImage.getCursor("SE_"+type+"Cursor", 16, 16);
        }
        System.err.println("compassCursor invalid value: "+d);
        return Cursor.getDefaultCursor();
    }
}
