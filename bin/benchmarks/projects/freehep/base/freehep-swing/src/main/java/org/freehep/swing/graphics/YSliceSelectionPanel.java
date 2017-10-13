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
 * A panel which selects a slice of the window in the x-direction.
 * That is, it selects a rectangular region with a height equal to the
 * full height of the window and with an adjustable width.
 *
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: YSliceSelectionPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class YSliceSelectionPanel 
    extends AbstractRegionSelectionPanel {

    /**
     * Creates a YSliceSelectionPanel. */
    public YSliceSelectionPanel() {
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
            case 4:
                k = 5;
                break;
            case 1:
            case 2:
            case 5:
                k = 4;
                break;
            default: return FreeHepImage.getCursor("YSliceCursor");
        }
        int dy = yCtrlPts[index] - yCtrlPts[k];
        if (dy == 0) {
            return FreeHepImage.getCursor("YSliceCursor");
        } else {
            return compassCursor("Resize", 1, dy, 4, false);
        }
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
        Arrays.fill(yCtrlPts,y);

        int xmax = getWidth();
        int xmid = xmax/2;
        xCtrlPts[0] = 0;
        xCtrlPts[1] = 0;
        xCtrlPts[2] = xmax;
        xCtrlPts[3] = xmax;
        xCtrlPts[4] = xmid;
        xCtrlPts[5] = xmid;
    }

    /**
     * Move the active control point to the point (x,y).  
     *
     * @param x x-coordinate of the new point 
     * @param y y-coordinate of the new point */    
    public void updateActiveControlPoint(int x, int y) {

        // Bring the x-location within bounds.
        y = forceYCoordinateWithinBounds(y);

        // Change what is done depending on which control point is
        // active.
        if (activeCtrlPt==0 ||
            activeCtrlPt==3 ||
            activeCtrlPt==4) {

            yCtrlPts[0] = y;
            yCtrlPts[3] = y;
            yCtrlPts[4] = y;

        } else if (activeCtrlPt==1 ||
                   activeCtrlPt==2 ||
                   activeCtrlPt==5) {

            yCtrlPts[1] = y;
            yCtrlPts[2] = y;
            yCtrlPts[5] = y;
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
     * Make the affine transform which corresponds to this slice of
     * the y-axis.
     *
     * @return AffineTransform which describes the selected region */
    public AffineTransform makeAffineTransform() {

        // Sort out which are the three control points.
        int first = 1;
        int second = 2;
        int third = 3;
        if (yCtrlPts[0]<yCtrlPts[1]) {
            first = 0;
            second = 3;
            third = 2;
        }

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
            (yCtrlPts[4]!=yCtrlPts[5]) &&
            (xCtrlPts[0]!=xCtrlPts[3]);
    } 

}
