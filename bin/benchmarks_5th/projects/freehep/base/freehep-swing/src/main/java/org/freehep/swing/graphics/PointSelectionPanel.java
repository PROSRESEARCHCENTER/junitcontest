// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import org.freehep.swing.images.FreeHepImage;

/**
 * This panel allows the user to select a point on the screen.  The
 * crosshair cursor fill follow the system pointer when the left mouse
 * button is pressed.  The system pointer will become invisible while
 * the user is actively repositioning the crosshair.  
 *
 * This panel will also respond to keystroke input.
 * <ul>
 * <li>arrow keys: move the active control point in the specified
 * direction. 
 * <li>backspace key: reset selection region (make invisible).
 * <li>delete key: reset selection region (make invisible).
 * <li>escape key: leave selection mode (make component invisible).
 * <li>enter key: accept selection region (send off region
 * selected event) 
 * </ul>
 * Normally, the arrow keys move the cursor by 2 pixels per key
 * release; however if the shift key is pressed simultaneously,
 * then the arrow keys move the cursor by 1 pixel. 
 *
 * @author Charles Loomis
 * @version $Id: PointSelectionPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class PointSelectionPanel 
    extends GraphicalSelectionPanel {

    final private static int cursorSize=10;

    Rectangle oldBounds = new Rectangle();
    Rectangle lastDrawnRect = new Rectangle();
    Rectangle updateRect = new Rectangle();

    // Private class variables.
    private Point currentPoint = new Point();
    
    // The maximum and minimum values of the coordinates.
    private int xmin, xmax;
    private int ymin, ymax;

    // Flag to indicate whether or not the crosshair cursor is
    // visible. 
    private boolean cursorVisible;

    // This is a custom system cursor which is invisible.  Used when a
    // drag is active to avoid the system cursor from obscuring the
    // crosshairs. 
    private static Cursor invisibleCursor;

    // Temporary variable to save old cursor so that it can be
    // restored after the cursor drag has been completed. 
    private Cursor savedCursor;

    /**
     * Construct a PointSelectionPanel.  Initially the cursor is not
     * visible. */
    public PointSelectionPanel() {
        cursorVisible = false;
        setSelectionActionsEnabled(false);

        BufferedImage cursorImage = 
            new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
        Graphics g = cursorImage.getGraphics();
        g.setColor(new Color(0,0,0,0));
        g.fillRect(0,0,16,16);  

        Toolkit toolKit = Toolkit.getDefaultToolkit();
        invisibleCursor = toolKit.createCustomCursor(cursorImage,
                                                     new Point(0,0),
                                                     "InvisibleCursor");

        setCursor(FreeHepImage.getCursor("PointCursor"));
    }

    /**
     * Process key-released events.  This allow the selection panel to
     * move the cursor with the keyboard arrows. 
     *
     * <ul>
     * <li>arrow keys: move the active control point in the specified
     * direction. 
     * <li>backspace key: reset selection region (make invisible).
     * <li>delete key: reset selection region (make invisible).
     * <li>escape key: leave selection mode (make component invisible).
     * <li>tab key: next selection mode (next component made visible).
     * <li>enter key: accept selection region (send off region
     * selected event) 
     * <li>spacebar: accept selection region (send off region
     * selected event) 
     * </ul>
     * Normally, the arrow keys move the cursor by 2 pixels per key
     * release; however if the shift key is pressed simultaneously,
     * then the arrow keys move the cursor by 1 pixel. 
     *
     * @param e KeyEvent describing the key which has been released */
    public void keyReleased(KeyEvent e) {

        // Change the size of the increment in the given direction.
        int increment = (e.isShiftDown()) ? 1 : 2;

        switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
            updatePosition(currentPoint.x,currentPoint.y-increment);
            break;
        case KeyEvent.VK_DOWN:
            updatePosition(currentPoint.x,currentPoint.y+increment);
            break;
        case KeyEvent.VK_RIGHT:
            updatePosition(currentPoint.x+increment,currentPoint.y);
            break;
        case KeyEvent.VK_LEFT:
            updatePosition(currentPoint.x-increment,currentPoint.y);
            break;
        default:
            super.keyReleased(e);
            break;
        }
    }

    /**
     * The crosshair cursor will begin to follow the system pointer;
     * the system pointer is made invisible. 
     *
     * @param e the mouse pressed event to which to respond */
    public void mousePressed(MouseEvent e) {
        if (!isProcessingPopup(e)) {
            cursorVisible = true;
            setSelectionActionsEnabled(true);
            savedCursor = getCursor();
            setCursor(invisibleCursor);
            updatePosition(e.getX(),e.getY());
        }
    }

    /**
     * The crosshair cursor will stop following the system pointer
     * when the mouse is released.  The system pointer will again be
     * visible.
     *
     * @param e the mouse release event to which to respond */    
    public void mouseReleased(MouseEvent e) {   
        if (!isProcessingPopup(e)) {
            setCursor(savedCursor);
            updatePosition(e.getX(),e.getY());
        }
    }
    
    /**
     * The crosshair cursor follows the system pointer during a mouse
     * drag event. 
     *
     * @param e the mouse drag event to which to respond */ 
    public void mouseDragged(MouseEvent e) {
        if (!isProcessingPopup(e)) {
            updatePosition(e.getX(),e.getY());
        }
    }
    
    /**
     * This method updates the position of the crosshair cursor.
     * Normally this is called internally; however, it can be called
     * programatically by other object, for example, to move the
     * cursor on top of a selected object. 
     *
     * @param x the x coordinate for the cursor (in pixels in the
     * PointSelectionPanel's coordinate system)
     * @param y the y coordinate for the cursor (in pixels in the
     * PointSelectionPanel's coordinate system) */
    public void updatePosition(int x, int y) {

        if (cursorVisible) {

            // Get the current boundries.
            xmin = 1;
            xmax = getWidth()-1;
            ymin = 1;
            ymax = getHeight()-1;
            
            // Bring the location within bounds.
            x = Math.max(Math.min(x,xmax),xmin);
            y = Math.max(Math.min(y,ymax),ymin);        
            currentPoint.setLocation(x,y);
            
            // Update the current bounds to be the new position.  
            int xLow = currentPoint.x-cursorSize;
            int yLow = currentPoint.y-cursorSize;

            // Repaint the new bounds.
            updateRect.setBounds(lastDrawnRect);
            updateRect = SwingUtilities.computeUnion(xLow,
                                                     yLow,
                                                     2*cursorSize,
                                                     2*cursorSize,
                                                     updateRect);
            repaint(updateRect);
        }
    }

    /**
     * Set the visiblility of the crosshair cursor. 
     *
     * @param visible boolean indicating whether or not the crosshair
     * cursor should be visible */
    public void setCursorVisible(boolean visible) {
        if (cursorVisible!=visible) {
            cursorVisible = visible;
            setSelectionActionsEnabled(cursorVisible);

            repaint();
        }
    }

    /**
     * Get the visibility of the crosshair cursor.
     *
     * @return boolean indicating whether or not the crosshair cursor
     * is visible */
    public boolean getCursorVisible() {
        return cursorVisible;
    }

    /**
     * Reset the selection; this remove the crosshair cursor from the
     * screen. */
    public void resetSelection() {
        cursorVisible = false;
        updateRect.setBounds(lastDrawnRect);
        setSelectionActionsEnabled(false);
        repaint(updateRect);
    }

    public void paintComponent(Graphics g) {
        
        // Allow parent to draw any custom painting.
        super.paintComponent(g);

        if (cursorVisible) {

            // Make a 2D graphics context.
            Graphics2D g2d = (Graphics2D) g;

            // Get the limits.
            int xLow = currentPoint.x-cursorSize;
            int xHigh = currentPoint.x+cursorSize;
            int yLow = currentPoint.y-cursorSize;
            int yHigh = currentPoint.y+cursorSize;

            // Paint the crosshairs.    
            g2d.setStroke(thickStroke);
            g.setColor(Color.black);
            g.drawLine(currentPoint.x, yLow, currentPoint.x, yHigh);
            g.drawLine(xLow, currentPoint.y, xHigh, currentPoint.y);
            g2d.setStroke(thinStroke);
            g.setColor(Color.white);
            g.drawLine(currentPoint.x, yLow, currentPoint.x, yHigh);
            g.drawLine(xLow, currentPoint.y, xHigh, currentPoint.y);

            // Update the last drawn rectangle.
            lastDrawnRect.setRect(xLow-1,yLow-1,2*cursorSize+3,2*cursorSize+3);
        }
    }

    /**
     * Make the affine transform which will center the display on the
     * current point if applied.
     *
     * @return AffineTransform will center the current point */
    public AffineTransform makeAffineTransform() {
        return new AffineTransform(1.,0.,0.,1.,
                                   getWidth()/2.-currentPoint.x,
                                   getHeight()/2.-currentPoint.y);
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

        case GraphicalSelectionEvent.ZOOM:
        case GraphicalSelectionEvent.ZOOM_NEW_VIEW:

            // For all of the zooming just center the selected point in the
            // view. 
            if (cursorVisible) {
                fireGraphicalSelectionMade(new 
                    PointSelectionEvent(this,actionCode,
                                        currentPoint,
                                        makeAffineTransform()));
            }
            resetSelection();
            break;

        case GraphicalSelectionEvent.PICK:
        case GraphicalSelectionEvent.PICK_ADD:
        case GraphicalSelectionEvent.UNPICK:

            // For all of the picking modify the transform to center the
            // selected point on the view, but also to zoom into the n x n
            // region around this point.
            if (cursorVisible) {

                // Get the centering transform.
                AffineTransform trans = makeAffineTransform();

                // Now get the transform which maintains the center of the
                // view but scales so that +-size pixels fill the view.
                double size = 20.;
                double halfWidth = getWidth()/2.;
                double sx = halfWidth/size;
                double halfHeight = getHeight()/2.;
                double sy = halfHeight/size;
                AffineTransform scaling = 
                    new AffineTransform(sx,0.,0.,sy,
                                        halfWidth*(1.-sx),
                                        halfHeight*(1.-sy));
                trans.preConcatenate(scaling);

                fireGraphicalSelectionMade(new 
                    PointSelectionEvent(this,actionCode,currentPoint,trans));
            }
            resetSelection();
            break;

        }
    }

}
