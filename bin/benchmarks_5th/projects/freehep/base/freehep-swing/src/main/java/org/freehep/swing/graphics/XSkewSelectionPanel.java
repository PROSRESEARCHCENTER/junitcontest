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
 * A panel which selects a parallogram-shaped region in which two
 * sides are parallel to the y-axis and the other two are skewed with
 * respect to the x-axis.
 *
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: XSkewSelectionPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class XSkewSelectionPanel 
    extends AbstractRegionSelectionPanel {

    /**
     * The initial starting width of the skewed parallogram. */
    final private static int STARTING_WIDTH = 25;

    /**
     * Creates a XSkewSelectionPanel. */
    public XSkewSelectionPanel() {
        super();
    }

    /**
     * Return the number of control points 6---the four corners and
     * the centerpoints of the two sides parallel to the y-axis.
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
                k = 4;
                break;
            case 1:
            case 2:
                k = 5;
                break;
            case 4:
            case 5: return FreeHepImage.getCursor("0_MoveCursor", 16, 16);
            default: return FreeHepImage.getCursor("XSkewCursor");
        }
        return compassCursor("Resize", xCtrlPts[index] - xCtrlPts[k], 
                                       yCtrlPts[index] - yCtrlPts[k], 4, false);
    }
    
    /**
     * Initialize the control points based in the starting point
     * (x,y). 
     *
     * @param x x-coordinate of starting point
     * @param y y-coordinate of starting point */
    public void initializeControlPoints(int x, int y) {

        // Set the fifth control point to be the active one and
        // initialize all of the coordinates. 
        activeCtrlPt = 5;
        Arrays.fill(xCtrlPts,x);
        yCtrlPts[0] = y-STARTING_WIDTH;
        yCtrlPts[1] = y-STARTING_WIDTH;
        yCtrlPts[2] = y+STARTING_WIDTH;
        yCtrlPts[3] = y+STARTING_WIDTH;
        yCtrlPts[4] = y;
        yCtrlPts[5] = y;
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
        int width;
        switch (activeCtrlPt) {

        case 0:

            width = y-yCtrlPts[4];

            yCtrlPts[0] = yCtrlPts[4]+width;
            yCtrlPts[1] = yCtrlPts[5]+width;

            yCtrlPts[2] = yCtrlPts[5]-width;
            yCtrlPts[3] = yCtrlPts[4]-width;

            break;

        case 1:

            width = y-yCtrlPts[5];

            yCtrlPts[0] = yCtrlPts[4]+width;
            yCtrlPts[1] = yCtrlPts[5]+width;

            yCtrlPts[2] = yCtrlPts[5]-width;
            yCtrlPts[3] = yCtrlPts[4]-width;

            break;

        case 2:

            width = y-yCtrlPts[5];

            yCtrlPts[0] = yCtrlPts[4]-width;
            yCtrlPts[1] = yCtrlPts[5]-width;

            yCtrlPts[2] = yCtrlPts[5]+width;
            yCtrlPts[3] = yCtrlPts[4]+width;

            break;

        case 3:

            width = y-yCtrlPts[4];

            yCtrlPts[0] = yCtrlPts[4]-width;
            yCtrlPts[1] = yCtrlPts[5]-width;

            yCtrlPts[2] = yCtrlPts[5]+width;
            yCtrlPts[3] = yCtrlPts[4]+width;

            break;

        case 4:

            // Determine the width.
            width = yCtrlPts[4] - yCtrlPts[0];
            
            // Update the active control point.
            xCtrlPts[activeCtrlPt] = x;
            yCtrlPts[activeCtrlPt] = y;
            
            // Update the control points on either side.
            xCtrlPts[0] = x;
            yCtrlPts[0] = y-width;
            xCtrlPts[3] = x;
            yCtrlPts[3] = y+width;

            break;
        
        case 5:
            
            // Determine the width.
            width = yCtrlPts[4] - yCtrlPts[0];
            
            // Update the active control point.
            xCtrlPts[activeCtrlPt] = x;
            yCtrlPts[activeCtrlPt] = y;
            
            // Update the control points on either side.
            xCtrlPts[1] = x;
            yCtrlPts[1] = y-width;
            xCtrlPts[2] = x;
            yCtrlPts[2] = y+width;

            break;
         
        default:
            break;
        }
        
        repaintPanel();
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
     * Make the affine transform which corresponds to this skewed
     * region. 
     *
     * @return AffineTransform which describes the selected region */
    public AffineTransform makeAffineTransform() {

        // Find first the upper, left-hand point.   This is the one
        // with the smallest x-value and then the smallest y-value.
        int first = 0;
        int xSavedValue = xCtrlPts[first];
        int ySavedValue = yCtrlPts[first];
        for (int i=1; i<4; i++) {
            int xValue = xCtrlPts[i];
            int yValue = yCtrlPts[i];
            if (xValue<xSavedValue) {
                xSavedValue = xValue;
                ySavedValue = yValue;
                first = i;
            } else if (xValue==xSavedValue) {
                if (yValue<ySavedValue) {
                    xSavedValue = xValue;
                    ySavedValue = yValue;
                    first = i;
                }
            }
        }

        // Calculate which is the opposite corner.
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
     * Check that the selection is valid; regions with a zero area are
     * invalid. 
     *
     * @return flag indicating whether the region is valid */
    public boolean isValidSelection() {
        return (visible) &&
            (xCtrlPts[4]!=xCtrlPts[5]) &&
            (yCtrlPts[0]!=yCtrlPts[3]);
    } 

}
