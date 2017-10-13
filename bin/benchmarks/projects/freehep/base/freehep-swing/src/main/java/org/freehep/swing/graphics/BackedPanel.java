// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * This class extends JPanel by adding a backing store.  This is
 * intended to be used in situations in which redrawing the contents
 * of the panel is extremely expensive.
 *
 * To keep things simple, this component does NOT support borders.  If
 * a border is desired, then this component should be embedded within
 * another container which has one.
 *
 * Likewise, children components should NOT be added to this
 * component.
 *
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: BackedPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class BackedPanel
    extends JPanel
    implements java.io.Serializable {

    /**
     * The graphics context of the backing image. */
    private Graphics backingGraphics = null;

    /**
     * The backing image itself. */
    protected BufferedImage backingImage = null;

    /**
     * An object to act as a lock for the backing image. */
    private Object lock = new Object();

    /**
     * The old dimensions of this panel. */
    private Dimension oldDimension = new Dimension();

    /**
     * Object to hold a temporary dimension value. */
    private Dimension dim = new Dimension();

    /**
     * Rectangle to hold the clipping bounds. */
    protected Rectangle clip = new Rectangle();

    /**
     * Error string when user attempts to set a non-null border. */
    private final static String NON_NULL_BORDER_ERROR =
        "BackedPanel does not support borders.";

    /**
     * Creates a new BackedPanel with a width and height set to zero,
     * and the backing image and graphics object to null.  By default,
     * the Swing double buffering is turned off (since we're writing
     * to a backing image anyway).  The caller of this constructor
     * selects whether this is a transparent or opaque panel.
     *
     * @param opaque transparent panel */
    public BackedPanel(boolean opaque) {

        // First turn off the Swing double buffering.
        super(false);

        // Make this either opaque or transparent.
        setOpaque(opaque);
    }

    /**
     * Return the graphics object for the backing image.  All drawing
     * goes to this image first.  Only on a repaint() is the image
     * flushed to the screen.
     *
     * @return Graphics of backing image */
    public Graphics getGraphics() {
        return backingGraphics;
    }

    /**
     * Get the object which acts as a lock on the backing image.
     * Drawing to the graphics context supplied by getGraphics() or
     * the image returned from getBackingImage() should be
     * synchronized to this object.
     *
     * @return lock for the backing image */
    public Object getLock() {
        return lock;
    }

    /**
     * Paint this panel by flushing the backing image to the screen.
     *
     * @param g Graphics object to draw backing store into */
    public void paintComponent(Graphics g) {
        // Allow the parent to do any custom painting.
        // FIXME: do not..., since it will also paint.
//        super.paintComponent(g);

        // Actually paint this component.
        if (g!=null && backingImage!=null) {
            // Despite what the API documentation says,
            // getClipBounds() returns the current dirty region.  This
            // can then be used to speedup the drawing of the
            // this BackedPanel.
            clip = g.getClipBounds(clip);

            synchronized (getLock()) {
                BufferedImage subimage =
                    ((BufferedImage)backingImage).getSubimage(clip.x,clip.y,
                                             clip.width,clip.height);
                g.drawImage(subimage,clip.x,clip.y,this);
            }
        }
    }

    /**
     * Since this component does not support borders, override this
     * method to do nothing.
     *
     * @param g ignored Graphics context */
    public void paintBorder(Graphics g) {
    }

    /**
     * Since this component should not contain children, override this
     * method to do nothing.
     *
     * @param g ignored Graphics context */
    public void paintChildren(Graphics g) {
    }

    /**
     * Printing should be handled directly by the component which
     * paints into the backing store.  Hence, this method does
     * nothing.
     *
     * @param g ignored Graphics context */
    public void printComponent(Graphics g) {
    }

    /**
     * Since this component does not support borders, override this
     * method to do nothing.
     *
     * @param g ignored Graphics context */
    public void printBorder(Graphics g) {
    }

    /**
     * Since this component should not contain children, override this
     * method to do nothing.
     *
     * @param g ignored Graphics context */
    public void printChildren(Graphics g) {
    }

    /**
     * This component does not support borders.  If this method is
     * called with any non-null argument, then an
     * IllegalArgumentException is thrown.  If a border is desired,
     * then this component should be embedded within another container
     * which has one.
     *
     * @param border must be null */
    public final void setBorder(Border border) {
        if (border!=null)
            throw new IllegalArgumentException(NON_NULL_BORDER_ERROR);
    }

    /**
     * Resize and move a component. */
    public void setBounds(int x, int y, int w, int h) {
        // Make sure that the parent's method is called first;
        // otherwise, the resize never happens and new images are NOT
        // made.
        super.setBounds(x,y,w,h);

        // Make the backing image.
        makeImage();
    }

    /**
     * Make the backing image for this panel.  Check to see if the
     * size has changed before doing anything. */
    private void makeImage() {

        // Get the full size of the panel.
        dim = getSize(dim);
        int w = dim.width;
        int h = dim.height;

        // Check that the current size is positive and that the new
        // dimension is not equal to the old one.
        if (w>0 && h>0) {
            if (!oldDimension.equals(dim)) {

                synchronized (getLock()) {

                    // Make the actual backing image and get the
                    // graphics context for this image.
                    backingImage = (isOpaque()) ? (BufferedImage)super.createImage(w,h) :
                                                  new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
                    backingGraphics = backingImage.getGraphics();

                    // Reset the old size of this panel.
                    oldDimension.setSize(dim);
                }
            }
        } else {
            synchronized (getLock()) {
                backingImage = null;
                backingGraphics = null;
            }
        }
    }

}
