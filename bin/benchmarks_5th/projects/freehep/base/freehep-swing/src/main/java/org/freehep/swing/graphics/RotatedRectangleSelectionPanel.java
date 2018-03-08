// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import org.freehep.swing.images.FreeHepImage;

/**
 * A panel which selects a rectangular region on the screen which can
 * be arbitrarily rotated.
 *
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: RotatedRectangleSelectionPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class RotatedRectangleSelectionPanel 
    extends AbstractRegionSelectionPanel {

    /**
     * The initial starting width of the first and last sides. */
    final private static int STARTING_WIDTH = 25;

    /**
     * Creates a RotatedRectangleSelectionPanel. */
    public RotatedRectangleSelectionPanel() {
        super();
    }

    /**
     * The number of control points is 6---the four corners and the
     * centerpoints of the first and last sides.
     *
     * @return 6 the number of control points */
    public int getNumberOfControlPoints() {
        return 6;
    }
    
    public Cursor getControlPointCursor(int index) {
        int k;
        String type;
        switch(index) {
            case 0:
            case 3:
                k = 4;
                type = "Resize";
                break;
            case 1:
            case 2: 
                k = 5;
                type = "Resize";
                break;    
            case 4:
                k = 5;
                type = "Rotation";
                break;
            case 5: 
                k = 4;
                type = "Rotation";
                break;
            default: 
                return FreeHepImage.getCursor("RotatedRectangleCursor");
        }
        return compassCursor(type, xCtrlPts[index] - xCtrlPts[k], 
                                   yCtrlPts[index] - yCtrlPts[k], 8, true);
    }
    
    /**
     * Initialize the control points given the starting point (x,y). 
     *
     * @param x x-coordinate of the starting point
     * @param y y-coordinate of the starting point */
    public void initializeControlPoints(int x, int y) {

        // Set the fifth control point to be the active one and
        // initialize all of the coordinates. 
        activeCtrlPt = 5;
        Arrays.fill(yCtrlPts,y);
        xCtrlPts[0] = x-STARTING_WIDTH;
        xCtrlPts[1] = x-STARTING_WIDTH;
        xCtrlPts[2] = x+STARTING_WIDTH;
        xCtrlPts[3] = x+STARTING_WIDTH;
        xCtrlPts[4] = x;
        xCtrlPts[5] = x;
    }

    /**
     * Move the active control point to the point (x,y).  
     *
     * @param x x-coordinate of the new point 
     * @param y y-coordinate of the new point */    
    public void updateActiveControlPoint(int x, int y) {

        // Bring the location within bounds.
        x = forceXCoordinateWithinBounds(x);
        y = forceYCoordinateWithinBounds(y);

        // Change what is done depending on which control point is
        // active. 
        int dx;
        int dy;
        int deltax;
        int deltay;
        double angle;
        double radius;
        switch (activeCtrlPt) {

        case 0:

            // Determine the radius of the corners from the middle of
            // the control sides. 
            radius = getRadius(x,y,4);
            
            // Get the angle of the rotated rectangle.
            angle = getAngle();

            deltax = x - xCtrlPts[4];
            deltay = y - yCtrlPts[4];

            dx = (int) -Math.round(radius*Math.sin(angle));
            dy = (int)  Math.round(radius*Math.cos(angle));

            if (deltax*dx+deltay*dy < 0) {
                dx = -dx;
                dy = -dy;
            }
            
            // Update the other control points.
            xCtrlPts[0] = xCtrlPts[4]+dx;
            yCtrlPts[0] = yCtrlPts[4]+dy;
            xCtrlPts[3] = xCtrlPts[4]-dx;
            yCtrlPts[3] = yCtrlPts[4]-dy;

            xCtrlPts[1] = xCtrlPts[5]+dx;
            yCtrlPts[1] = yCtrlPts[5]+dy;
            xCtrlPts[2] = xCtrlPts[5]-dx;
            yCtrlPts[2] = yCtrlPts[5]-dy;

            break;
        
        case 1: 

            // Determine the radius of the corners from the middle of
            // the control sides. 
            radius = getRadius(x,y,5);
            
            // Get the angle of the rotated rectangle.
            angle = getAngle();

            deltax = x - xCtrlPts[5];
            deltay = y - yCtrlPts[5];

            dx = (int) -Math.round(radius*Math.sin(angle));
            dy = (int)  Math.round(radius*Math.cos(angle));

            if (deltax*dx+deltay*dy < 0) {
                dx = -dx;
                dy = -dy;
            }
            
            // Update the other control points.
            xCtrlPts[0] = xCtrlPts[4]+dx;
            yCtrlPts[0] = yCtrlPts[4]+dy;
            xCtrlPts[3] = xCtrlPts[4]-dx;
            yCtrlPts[3] = yCtrlPts[4]-dy;

            xCtrlPts[1] = xCtrlPts[5]+dx;
            yCtrlPts[1] = yCtrlPts[5]+dy;
            xCtrlPts[2] = xCtrlPts[5]-dx;
            yCtrlPts[2] = yCtrlPts[5]-dy;

            break;
        
        case 2:

            // Determine the radius of the corners from the middle of
            // the control sides. 
            radius = getRadius(x,y,5);
            
            // Get the angle of the rotated rectangle.
            angle = getAngle();

            deltax = x - xCtrlPts[5];
            deltay = y - yCtrlPts[5];

            dx = (int) -Math.round(radius*Math.sin(angle));
            dy = (int)  Math.round(radius*Math.cos(angle));

            if (deltax*dx+deltay*dy < 0) {
                dx = -dx;
                dy = -dy;
            }
            
            // Update the other control points.
            xCtrlPts[0] = xCtrlPts[4]-dx;
            yCtrlPts[0] = yCtrlPts[4]-dy;
            xCtrlPts[3] = xCtrlPts[4]+dx;
            yCtrlPts[3] = yCtrlPts[4]+dy;

            xCtrlPts[1] = xCtrlPts[5]-dx;
            yCtrlPts[1] = yCtrlPts[5]-dy;
            xCtrlPts[2] = xCtrlPts[5]+dx;
            yCtrlPts[2] = yCtrlPts[5]+dy;

            break;
        
        case 3:

            // Determine the radius of the corners from the middle of
            // the control sides. 
            radius = getRadius(x,y,4);
            
            // Get the angle of the rotated rectangle.
            angle = getAngle();

            deltax = x - xCtrlPts[4];
            deltay = y - yCtrlPts[4];

            dx = (int) -Math.round(radius*Math.sin(angle));
            dy = (int)  Math.round(radius*Math.cos(angle));

            if (deltax*dx+deltay*dy < 0) {
                dx = -dx;
                dy = -dy;
            }
            
            // Update the other control points.
            xCtrlPts[0] = xCtrlPts[4]-dx;
            yCtrlPts[0] = yCtrlPts[4]-dy;
            xCtrlPts[3] = xCtrlPts[4]+dx;
            yCtrlPts[3] = yCtrlPts[4]+dy;

            xCtrlPts[1] = xCtrlPts[5]-dx;
            yCtrlPts[1] = yCtrlPts[5]-dy;
            xCtrlPts[2] = xCtrlPts[5]+dx;
            yCtrlPts[2] = yCtrlPts[5]+dy;

            break;
        
        case 4:  /* Fall through! */
        case 5:

            // Determine the radius of the corners from the middle of
            // the control sides. 
            radius = getRadius(xCtrlPts[0], yCtrlPts[0], 4);
            
            // Update the active control point.
            xCtrlPts[activeCtrlPt] = x;
            yCtrlPts[activeCtrlPt] = y;

            // Get the angle of the rotated rectangle.
            angle = getAngle();

            dx = (int) -Math.round(radius*Math.sin(angle));
            dy = (int)  Math.round(radius*Math.cos(angle));
            
            // Update the other control points.
            xCtrlPts[0] = xCtrlPts[4]+dx;
            yCtrlPts[0] = yCtrlPts[4]+dy;
            xCtrlPts[3] = xCtrlPts[4]-dx;
            yCtrlPts[3] = yCtrlPts[4]-dy;

            xCtrlPts[1] = xCtrlPts[5]+dx;
            yCtrlPts[1] = yCtrlPts[5]+dy;
            xCtrlPts[2] = xCtrlPts[5]-dx;
            yCtrlPts[2] = yCtrlPts[5]-dy;

            break;
        
        default:
            break;
        }
        
        repaintPanel();
    }

    /**
     * A utility routine to get the radius from one of the control
     * points. */
    private double getRadius(int x, int y, int ctrlPt) {
        int dx = x - xCtrlPts[ctrlPt];
        int dy = y - yCtrlPts[ctrlPt];
        return Math.sqrt((double) (dx*dx+dy*dy));
    }

    /**
     * A utility routine to get the angle of the rotated rectangle. */ 
    private double getAngle() {
        
        // Get the angle of the rotated rectangle.
        double deltax = xCtrlPts[5] - xCtrlPts[4];
        double deltay = yCtrlPts[5] - yCtrlPts[4];
        if (deltax!=0 || deltay!=0) {
            return Math.atan2(deltay,deltax);
        } else {
            return 0.;
        }
    }

    public void paintComponent(Graphics g) {
        
        // Allow parent to draw any custom painting.
        super.paintComponent(g);
        
        // If the selection region is visible, paint it.
        if (visible) {

            // Make a 2D graphics context.
            Graphics2D g2d = (Graphics2D) g;
            
            // Draw a rectangle on top the the image.
            g2d.setStroke(thickStroke);
            g.setColor(Color.black);
            g.drawPolygon(xCtrlPts, yCtrlPts, 4);

            if (visibleGuides) {
                g.drawLine(xCtrlPts[4], yCtrlPts[4], xCtrlPts[5],
                           yCtrlPts[5]);
            }
            g2d.setStroke(thinStroke);
            g.setColor(Color.white);
            g.drawPolygon(xCtrlPts, yCtrlPts, 4);

            if (visibleGuides) {
                g.drawLine(xCtrlPts[4], yCtrlPts[4], xCtrlPts[5],
                           yCtrlPts[5]);
            }

            if (activeCtrlPt >= 0) {
                // Draw the active control point.
                g.setColor(Color.black);
                g.fillRect(xCtrlPts[activeCtrlPt]-ctrlPtSize-1, 
                           yCtrlPts[activeCtrlPt]-ctrlPtSize-1,
                           2*ctrlPtSize+3, 2*ctrlPtSize+3);
                g.setColor(Color.white);
                g.fillRect(xCtrlPts[activeCtrlPt]-ctrlPtSize, 
                           yCtrlPts[activeCtrlPt]-ctrlPtSize,
                           2*ctrlPtSize+1, 2*ctrlPtSize+1);
            }
        }
    }    


    /**
     * Make the affine transform which corresponds to this rectangular
     * selection. 
     *
     * @return AffineTransform which describes the selected region */
    public AffineTransform makeAffineTransform() {

        // Find first the upper, left-hand point.
        int first = 0;
        int savedValue = xCtrlPts[0]*xCtrlPts[0]+yCtrlPts[0]*yCtrlPts[0];
        for (int i=1; i<4; i++) {
            int value = xCtrlPts[i]*xCtrlPts[i]+yCtrlPts[i]*yCtrlPts[i];
            if (value<savedValue) {
                savedValue = value;
                first = i;
            }
        }

        // Calculate the index of the opposite corner.
        int third = (first+2)%4;

        // Now use the cross-product to determine which of the
        // remaining points is the one which keep the path going
        // clockwise. 
        int second = (first+1)%4;
        int dx0 = xCtrlPts[third]-xCtrlPts[first];
        int dy0 = yCtrlPts[third]-yCtrlPts[first];
        int dx1 = xCtrlPts[second]-xCtrlPts[first];
        int dy1 = yCtrlPts[second]-yCtrlPts[first];
        if (dx0*dy1-dy0*dx1>0) second = (first+3)%4;

        // Get the appropriate radius.
        int centerIndex = (first==0 || first==3) ? 4 : 5;
        double radius = 
            getRadius(xCtrlPts[first], yCtrlPts[first], centerIndex);
        double angle = getAngle();

        // Calculate the delta-x and delta-y for the points.
        double dx = Math.abs(radius*Math.sin(angle));
        double dy = Math.abs(radius*Math.cos(angle));

        // Get the sign of the offsets from the control points.
        double sdx;
        double sdy;

        // The point closest to the origin.
        centerIndex = (first==0 || first==3) ? 4 : 5;
        sdx = 
            ((xCtrlPts[first]-xCtrlPts[centerIndex])>0) ? 1. : -1.;
        sdy = 
            ((yCtrlPts[first]-yCtrlPts[centerIndex])>0) ? 1. : -1.;
        double x0 = xCtrlPts[centerIndex]+sdx*dx;
        double y0 = yCtrlPts[centerIndex]+sdy*dy;

        // The next point clockwise.
        centerIndex = (second==0 || second==3) ? 4 : 5;
        sdx = 
            ((xCtrlPts[second]-xCtrlPts[centerIndex])>0) ? 1. : -1.;
        sdy = 
            ((yCtrlPts[second]-yCtrlPts[centerIndex])>0) ? 1. : -1.;
        double x1 = xCtrlPts[centerIndex]+sdx*dx;
        double y1 = yCtrlPts[centerIndex]+sdy*dy;

        // The next point clockwise.
        centerIndex = (third==0 || third==3) ? 4 : 5;
        sdx = 
            ((xCtrlPts[third]-xCtrlPts[centerIndex])>0) ? 1. : -1.;
        sdy = 
            ((yCtrlPts[third]-yCtrlPts[centerIndex])>0) ? 1. : -1.;
        double x2 = xCtrlPts[centerIndex]+sdx*dx;
        double y2 = yCtrlPts[centerIndex]+sdy*dy;

        // The control points are in the correct order, so we can just
        // call the utility function of the parent.
        return makeTransform(x0,y0,x1,y1,x2,y2);
    }

    /**
     * Check that the area of the selection is non-zero.
     *
     * @return flag indicating whether the selection is valid */
    public boolean isValidSelection() { 
        return (visible) && 
            (xCtrlPts[4]!=xCtrlPts[5] || yCtrlPts[4]!=yCtrlPts[5]) &&
            (xCtrlPts[0]!=xCtrlPts[3] || yCtrlPts[0]!=yCtrlPts[3]);
    } 

}
