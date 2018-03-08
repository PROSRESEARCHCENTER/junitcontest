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
 * Selects a rectangular screen region.  The sides of the rectangle
 * are parallel to the x and y-axes.
 *
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: RectangularSelectionPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class RectangularSelectionPanel
    extends AbstractRegionSelectionPanel {

    /**
     * Creates a RectangularSelectionPanel. */
    public RectangularSelectionPanel() {
        super();
    }

    /**
     * Returns the number of control points for the rectangle (4).
     *
     * @return 4, the control points at the corner of the rectangle */
    public int getNumberOfControlPoints() {
        return 4;
    }

    public Cursor getControlPointCursor(int index) {
        if (index >= 0) {
            int k = (index+2) % 4;
            return compassCursor("Resize", xCtrlPts[index] - xCtrlPts[k],
                                           yCtrlPts[index] - yCtrlPts[k], 4, true);
        }
        return FreeHepImage.getCursor("RectangularCursor");
    }

    /**
     * Initialize the control points based on the given starting point
     * (x,y).
     *
     * @param x x-coordinate (in pixels) of the starting point
     * @param y y-coordinate (in pixels) fo the starting point */
    public void initializeControlPoints(int x, int y) {
        activeCtrlPt = 2;
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

        // Update the active control point.
        xCtrlPts[activeCtrlPt] = x;
        yCtrlPts[activeCtrlPt] = y;

        // Get the opposite control point.
        int oppCtrlPt = (activeCtrlPt+2)%nCtrlPts;
        int xOpp = xCtrlPts[oppCtrlPt];
        int yOpp = yCtrlPts[oppCtrlPt];

        // Now choose the next control point so that we maintain a
        // clockwise order to the points.
        int otherCtrlPt =
            ((x<xOpp && y>yOpp)||(x>xOpp && y<yOpp)) ? 1 : 3;
        otherCtrlPt = (activeCtrlPt+otherCtrlPt)%4;

        // Set the other two control points.
        xCtrlPts[otherCtrlPt] = x;
        yCtrlPts[otherCtrlPt] = yOpp;
        otherCtrlPt = (otherCtrlPt+2)%nCtrlPts;
        xCtrlPts[otherCtrlPt] = xOpp;
        yCtrlPts[otherCtrlPt] = y;

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
            g.drawPolygon(xCtrlPts, yCtrlPts, nCtrlPts);
            g2d.setStroke(thinStroke);
            g.setColor(Color.white);
            g.drawPolygon(xCtrlPts, yCtrlPts, nCtrlPts);

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

        int second = 0;
        int third = 0;

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

        // Calculate the adjacent partner and the opposite corner.
        second = (first+1)%4;
        third = (first+2)%4;

        // Just call the utility function of the parent now.
        return makeTransform((double) xCtrlPts[first],
                             (double) yCtrlPts[first],
                             (double) xCtrlPts[second],
                             (double) yCtrlPts[second],
                             (double) xCtrlPts[third],
                             (double) yCtrlPts[third]);
    }

    /**
     * Determine if the selection is valid.  Return false if the area
     * is zero. */
    public boolean isValidSelection() {
        return (visible) && (xCtrlPts[0]!=xCtrlPts[2] ||
                             yCtrlPts[0]!=yCtrlPts[2]);
    }

}
