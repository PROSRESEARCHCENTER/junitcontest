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
 * A panel which selects a "square" region on the screen.  The
 * definition of "square" is that this region will retain the relative
 * scaling of the x and y-axes.  
 *
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: SquareSelectionPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class SquareSelectionPanel 
    extends AbstractRegionSelectionPanel {

    /**
     * Creates a SquareSelectionPanel. */
    public SquareSelectionPanel() {
        super();
    }

    /**
     * Return the number of control points,5---the four corners and
     * one point in the center of the square. 
     *
     * @return 5 the number of control points */
    public int getNumberOfControlPoints() {
        return 5;
    }

    /**
     * Returns the NE, SE, NW and SW cursors for the four different
     * directions and SquareCursor as the default
     */
    public Cursor getControlPointCursor(int index) {
        switch (index) {
            case NO_CONTROL_POINT: return FreeHepImage.getCursor("SquareCursor");
            case 4: return FreeHepImage.getCursor("0_MoveCursor", 16, 16);
            default: 
                return compassCursor("Resize", xCtrlPts[index] - xCtrlPts[4], 
                                               yCtrlPts[index] - yCtrlPts[4], 4, true);
        }
    }

    /**
     * Initialize the control points based on the starting point
     * (x,y).
     *
     * @param x x-coordinate of starting point
     * @param y y-coordinate of starting point */
    public void initializeControlPoints(int x, int y) {
        activeCtrlPt = 0;
        Arrays.fill(xCtrlPts,x);
        Arrays.fill(yCtrlPts,y);
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

        if (activeCtrlPt==4) {

            // Get the change in the center point.
            int dx = x - xCtrlPts[4];
            int dy = y - yCtrlPts[4];

            // Change all control points by this amount.
            for (int i=0; i<nCtrlPts; i++) {
                xCtrlPts[i] += dx;
                yCtrlPts[i] += dy;
            }

        } else {
        
            // Get the distance from center point.
            int dx = x - xCtrlPts[4];
            int dy = y - yCtrlPts[4];

            // Get the sign of the delta-x and delta-y.
            int signx = (dx<0) ? -1 : 1;
            int signy = (dy<0) ? -1 : 1;

            // Calculate the nearest point which keeps the aspect ratio.
            dx = Math.abs(dx);
            dy = Math.abs(dy);
            double ratio = 
                ((double) getHeight())/((double) getWidth());
            int xside = (int) ((((double) dx)+ratio*((double) dy))/
                         (1.+ratio*ratio));
            int yside = xside*getHeight()/getWidth();

            xCtrlPts[activeCtrlPt] = xCtrlPts[4]+signx*xside;
            yCtrlPts[activeCtrlPt] = yCtrlPts[4]+signy*yside;

            xCtrlPts[(activeCtrlPt+1)%4] = xCtrlPts[4]-signx*xside;
            yCtrlPts[(activeCtrlPt+1)%4] = yCtrlPts[4]+signy*yside;

            xCtrlPts[(activeCtrlPt+2)%4] = xCtrlPts[4]-signx*xside;
            yCtrlPts[(activeCtrlPt+2)%4] = yCtrlPts[4]-signy*yside;

            xCtrlPts[(activeCtrlPt+3)%4] = xCtrlPts[4]+signx*xside;
            yCtrlPts[(activeCtrlPt+3)%4] = yCtrlPts[4]-signy*yside;
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
            
            // Draw a rectangle on top of the image.
            g2d.setStroke(thickStroke);
            g.setColor(Color.black);
            g.drawPolygon(xCtrlPts, yCtrlPts, 4);

            if (visibleGuides) {
                g.drawLine(xCtrlPts[0], yCtrlPts[0],
                           xCtrlPts[2], yCtrlPts[2]);
                g.drawLine(xCtrlPts[1], yCtrlPts[1],
                           xCtrlPts[3], yCtrlPts[3]);
            }

            g2d.setStroke(thinStroke);
            g.setColor(Color.white);
            g.drawPolygon(xCtrlPts, yCtrlPts, 4);

            if (visibleGuides) {
                g.drawLine(xCtrlPts[0], yCtrlPts[0],
                           xCtrlPts[2], yCtrlPts[2]);
                g.drawLine(xCtrlPts[1], yCtrlPts[1],
                           xCtrlPts[3], yCtrlPts[3]);
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
     * Make the affine transform which corresponds to this "square"
     * selection. 
     *
     * @return AffineTransform which describes the selected region */
    public AffineTransform makeAffineTransform() {

        // Do a bit of calculation to maintain the aspect ratio.
        double dx = Math.abs(xCtrlPts[0]-xCtrlPts[4]);
        double dy = (dx*getHeight())/getWidth();

        double x0 = xCtrlPts[4]-dx;
        double y0 = yCtrlPts[4]-dy;

        double x1 = xCtrlPts[4]+dx;
        double y1 = yCtrlPts[4]-dy;

        double x2 = xCtrlPts[4]+dx;
        double y2 = yCtrlPts[4]+dy;


        // Now call the utility function of the parent.
        return makeTransform(x0,y0,x1,y1,x2,y2); 
    }

    /**
     * Determine if the selection is valid; regions of zero area are
     * considered invalid.
     *
     * @return flag indicating whether the region is valid */
    public boolean isValidSelection() {
        return (visible) && (xCtrlPts[0]!=xCtrlPts[2] || 
                             yCtrlPts[0]!=yCtrlPts[2]);
    } 

}
