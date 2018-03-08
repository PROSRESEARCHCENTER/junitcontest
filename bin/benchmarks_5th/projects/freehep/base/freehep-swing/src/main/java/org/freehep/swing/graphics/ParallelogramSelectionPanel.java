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
 * Selects a screen region shaped like a parallelogram. 
 *
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: ParallelogramSelectionPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class ParallelogramSelectionPanel 
    extends AbstractRegionSelectionPanel {

    /**
     * The initial starting width (in pixels) of the first and last
     * sides. */
    final private static int STARTING_WIDTH = 25;

    /**
     * Creates a ParallelogramSelectionPanel. */
    public ParallelogramSelectionPanel() {
        super();
    }

    /**
     * The number of control points is 6 for the parallelogram.  The
     * four corners and two at the centerpoints of the first and last
     * sides. 
     *
     * @return 6 the number of control points */
    public int getNumberOfControlPoints() {
        return 6;
    }

    public Cursor getControlPointCursor(int index) {
        int k;
        switch(index) {
            case 0:
            case 3:
            case 5:
                k = 4;
                break;
            case 1:
            case 2:
            case 4:
                k = 5;
                break;    
            default: 
                return FreeHepImage.getCursor("ParallelogramCursor");
        }
        return compassCursor("Rotation", xCtrlPts[index] - xCtrlPts[k], 
                                         yCtrlPts[index] - yCtrlPts[k], 8, true);
    }
    
    /**
     * Initialize the control points for this selection given the
     * initial starting point (x,y).
     *
     * @param x the initial x-coordinate
     * @param y the initial y-coordinate */
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
        switch (activeCtrlPt) {

        case 4:

            // Determine the delta-x and delta-y.
            dx = xCtrlPts[0] - xCtrlPts[4];
            dy = yCtrlPts[0] - yCtrlPts[4];
            
            // Update the active control point.
            xCtrlPts[activeCtrlPt] = x;
            yCtrlPts[activeCtrlPt] = y;
            
            // Update the control points on either side.
            xCtrlPts[0] = x+dx;
            yCtrlPts[0] = y+dy;
            xCtrlPts[3] = x-dx;
            yCtrlPts[3] = y-dy;

            break;
        
        case 5:
            
            // Determine the delta-x and delta-y.
            dx = xCtrlPts[1] - xCtrlPts[5];
            dy = yCtrlPts[1] - yCtrlPts[5];
            
            // Update the active control point.
            xCtrlPts[activeCtrlPt] = x;
            yCtrlPts[activeCtrlPt] = y;
            
            // Update the control points on either side.
            xCtrlPts[1] = x+dx;
            yCtrlPts[1] = y+dy;
            xCtrlPts[2] = x-dx;
            yCtrlPts[2] = y-dy;

            break;
         
        case 0:
        case 3:

            // Update the active control point.
            xCtrlPts[activeCtrlPt] = x;
            yCtrlPts[activeCtrlPt] = y;
            
            // Determine the delta-x and delta-y.
            dx = xCtrlPts[activeCtrlPt] - xCtrlPts[4];
            dy = yCtrlPts[activeCtrlPt] - yCtrlPts[4];

            if (activeCtrlPt==3) {
                dx = -dx;
                dy = -dy;
            }
            
            xCtrlPts[1] = xCtrlPts[5]+dx;
            yCtrlPts[1] = yCtrlPts[5]+dy;
            xCtrlPts[2] = xCtrlPts[5]-dx;
            yCtrlPts[2] = yCtrlPts[5]-dy;

            xCtrlPts[0] = xCtrlPts[4]+dx;
            yCtrlPts[0] = yCtrlPts[4]+dy;
            xCtrlPts[3] = xCtrlPts[4]-dx;
            yCtrlPts[3] = yCtrlPts[4]-dy;

            break;

        case 1:
        case 2:

            // Update the active control point.
            xCtrlPts[activeCtrlPt] = x;
            yCtrlPts[activeCtrlPt] = y;
            
            // Determine the delta-x and delta-y.
            dx = xCtrlPts[activeCtrlPt] - xCtrlPts[5];
            dy = yCtrlPts[activeCtrlPt] - yCtrlPts[5];
            
            if (activeCtrlPt==2) {
                dx = -dx;
                dy = -dy;
            }
            
            xCtrlPts[1] = xCtrlPts[5]+dx;
            yCtrlPts[1] = yCtrlPts[5]+dy;
            xCtrlPts[2] = xCtrlPts[5]-dx;
            yCtrlPts[2] = yCtrlPts[5]-dy;

            xCtrlPts[0] = xCtrlPts[4]+dx;
            yCtrlPts[0] = yCtrlPts[4]+dy;
            xCtrlPts[3] = xCtrlPts[4]-dx;
            yCtrlPts[3] = yCtrlPts[4]-dy;

            break;

        default:
            break;
        }
        
        repaintPanel();
    }

    /**
     * Repaint this component. 
     *
     * @param g Graphics context in which to draw */    
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
                g.drawLine(xCtrlPts[4], yCtrlPts[4], xCtrlPts[5], yCtrlPts[5]);
            }

            g2d.setStroke(thinStroke);
            g.setColor(Color.white);
            g.drawPolygon(xCtrlPts, yCtrlPts, 4);

            if (visibleGuides) {
                g.drawLine(xCtrlPts[4], yCtrlPts[4], xCtrlPts[5], yCtrlPts[5]);
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
     * Make the affine transform which corresponds to this
     * paralleogram-shaped selection.
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

        // Calculate the opposite index.
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

        // Now call the utility function of the parent.
        return makeTransform((double) xCtrlPts[first], 
                             (double) yCtrlPts[first],
                             (double) xCtrlPts[second], 
                             (double) yCtrlPts[second],
                             (double) xCtrlPts[third], 
                             (double) yCtrlPts[third]);
    }

    /**
     * Returns a boolean indicating whether or not the selected region
     * is valid.  It is valid only if the region has a non-zero area. 
     * 
     * @return flag indicating whether the region is valid */
    public boolean isValidSelection() {
        return (makeAffineTransform()!=null);
    } 

}
