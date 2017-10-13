// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import org.freehep.graphics2d.VectorGraphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * This class implements a Border in which the left and bottom sides
 * contain numerical scales.
 *
 * @author Charles Loomis
 * @version $Id: ScaleBorder.java 8584 2006-08-10 23:06:37Z duns $ */
public class ScaleBorder
    implements Border{

    /**
     * Constant giving the unicode value of a capital Greek delta. */
    final static private String DELTA = "\u0394";

    /**
     * Constant giving the unicode value of a prime symbol. */
    final static private String PRIME = "\u00b4";

    /**
     * Constant giving the unicode value of a middle dot. */
    final static private String DOT = "\u00b7";

    /**
     * A constant giving the horizontal index of the data arrays. */
    final static private int HORIZONTAL = 0;

    /**
     * A constant giving the vertical index of the data arrays. */
    final static private int VERTICAL = 1;

    /**
     * Constant describing a transform in which the transformed x and
     * y axes are parallel (or antiparallel) to the original axes. */
    final static public int TYPE_PARALLEL_TRANSFORM = 0;

    /**
     * Constant describing a transform in which the transformed x and
     * y axes are parallel (or antiparallel) to the original y and x
     * axes, respectively. */
    final static public int TYPE_SWITCHED_TRANSFORM = 1;

    /**
     * Constant describing a transform in which the transformed x-axis
     * is parallel (or antiparallel) to the original x-axis and the
     * transformed y-axis forms a non-zero angle with the original
     * one. */
    final static public int TYPE_X_SKEW_TRANSFORM = 2;

    /**
     * Constant describing a transform in which the transformed y-axis
     * is parallel (or antiparallel) to the original y-axis and the
     * transformed x-axis forms a non-zero angle with the original
     * one. */
    final static public int TYPE_Y_SKEW_TRANSFORM = 3;

    /**
     * Constant describing a transform in which the transformed x-axis
     * is parallel (or antiparallel) to the original y-axis and the
     * transformed y-axis forms a non-zero angle with the original
     * x-axis. */
    final static public int TYPE_SWITCHED_X_SKEW_TRANSFORM = 4;

    /**
     * Constant describing a transform in which the transformed y-axis
     * is parallel (or antiparallel) to the original x-axis and the
     * transformed x-axis forms a non-zero angle with the original
     * y-axis. */
    final static public int TYPE_SWITCHED_Y_SKEW_TRANSFORM = 5;

    /**
     * Constant describing a transform which does not fall into one of
     * the other categories. */
    final static public int TYPE_GENERAL_TRANSFORM = 6;

    /**
     * Background color. */
    private Color bkgColor;

    /**
     * Foreground color. */
    private Color fgColor;

    /**
     * Paths describing the primary tick marks for the two axes. */
    private GeneralPath[] pTicks = new GeneralPath[2];

    /**
     * Paths describing the secondary tick marks for the two axes. */
    private GeneralPath[] sTicks = new GeneralPath[2];

    /**
     * String giving the axis labels. */
    private String[] axisLabels = new String[2];

    /**
     * String giving the axis units. */
    private String[] axisUnits = new String[2];

    /**
     * The labels for the horizontal and vertical axes. */
    private String[][] labels;

    /**
     * The positions of each of the axis labels. */
    private double[][] positions;

    /**
     * The initial font size for labeling. */
    private int fontSize = 12;

    /**
     * The initial font to use. */
    private Font labelFont = new Font("SansSerif", Font.BOLD, fontSize);

    /**
     * The width of the line used for the secondary tick marks. */
    final static private Stroke thinStroke = new BasicStroke(1.f);

    /**
     * The width of the line used for the primary tick marks. */
    final static private Stroke thickStroke = new BasicStroke(2.f);

    /**
     * An array to hold temporary point values for transformation. */
    private double[] axisPts = new double[6];

    /**
     * Flag to indicate that the scale has changed and that it needs
     * to be redrawn. */
    private boolean scaleChanged;

    /**
     * The current width of the horizontal scale. */
    private int currentWidth;

    /**
     * The current width of the vertical scale. */
    private int currentHeight;

    /**
     * Minimum value on the horizontal axis. */
    private double minHoriz;

    /**
     * Maximum value of the horizontal axis. */
    private double maxHoriz;

    /**
     * Minimum value of the vertical axis. */
    private double minVert;

    /**
     * Maximum value on the vertical axis. */
    private double maxVert;

    /**
     * The calculated Insets for this border. */
    private Insets insets;

    /**
     * Constructs a ScaleBorder with the default foreground (black) and
     * background (orange) colors. */
    public ScaleBorder() {
        this(Color.orange,Color.black);
    }

    /**
     * Constructs a ScaleBorder with the given background and
     * foreground colors. */
    public ScaleBorder(Color bkgColor, Color fgColor) {

        // Give default values for the size of the axes.
        minHoriz = 0.;
        maxHoriz = 0.;
        minVert = 0.;
        maxVert = 0.;

        // Make the new insets.
        resetInsets();

        // Make the paths which will hold the axes.
        pTicks[HORIZONTAL] = new GeneralPath();
        pTicks[VERTICAL] = new GeneralPath();
        sTicks[HORIZONTAL] = new GeneralPath();
        sTicks[VERTICAL] = new GeneralPath();

        // Make the labels and positions.
        labels = new String[2][3];
        positions = new double[2][3];

        // Set the current width and height.
        currentWidth = 0;
        currentHeight = 0;
        scaleChanged = false;

        // Set the colors.
        setBackgroundColor(bkgColor);
        setForegroundColor(fgColor);
    }

    /**
     * Set the background color. */
    public void setBackgroundColor(Color bkgColor) {
        this.bkgColor = bkgColor;
    }

    /**
     * Get the current background color. */
    public Color getBackgroundColor() {
        return bkgColor;
    }

    /**
     * Set the foreground color. */
    public void setForegroundColor(Color bkgColor) {
        this.fgColor = bkgColor;
    }

    /**
     * Get the current foreground color. */
    public Color getForegroundColor() {
        return fgColor;
    }

    /**
     * Set the font for the labels. */
    public void setLabelFont(Font labelFont) {
        this.labelFont = labelFont;
        fontSize = labelFont.getSize();
        resetInsets();
    }

    /**
     * Get the current font for the labels. */
    public Font getLabelFont() {
        return labelFont;
    }

    /**
     * Set the horizontal and vertical limits for the scales. */
    public void setLimits(double minHoriz, double maxHoriz,
                          double minVert, double maxVert) {
        this.minHoriz = minHoriz;
        this.maxHoriz = maxHoriz;
        this.minVert = minVert;
        this.maxVert = maxVert;
        scaleChanged = true;
    }

    /**
     * Set the axis labels. */
    public void setAxisLabels(String horizontalLabel,
                              String verticalLabel) {
        axisLabels[HORIZONTAL] = horizontalLabel;
        axisLabels[VERTICAL] = verticalLabel;
    }

    /**
     * Set the axis units. */
    public void setAxisUnits(String horizontalUnits,
                             String verticalUnits) {
        if (horizontalUnits!=null) {
            axisUnits[HORIZONTAL] = "("+horizontalUnits+")";
        } else {
            axisUnits[HORIZONTAL] = "";
        }

        if (verticalUnits!=null) {
            axisUnits[VERTICAL] = "("+verticalUnits+")";
        } else {
            axisUnits[VERTICAL] = "";
        }
    }

    /**
     * Returns the insets of this border. */
    public Insets getBorderInsets(Component c) {
        return (Insets) insets.clone();
    }

    /**
     * Returns whether or not the border is opaque.  This always
     * returns true. */
    public boolean isBorderOpaque() {
        return true;
    }

    /**
     * Paints the border for the specified component with the
     * specified graphics context, position, and size. */
    public void paintBorder(Component c, Graphics g,
                            int x, int y,
                            int width, int height) {

        // Set the flag indicating that the scales must be remade.
        scaleChanged =
            (scaleChanged || width!=currentWidth || height!=currentHeight);

        VectorGraphics vg =
            VectorGraphics.create(g);

        if (vg!=null) {

            // First fill in the background.
            vg.setColor(bkgColor);
            vg.fillRect(0,0,width,insets.top);
            vg.fillRect(0,0,insets.left,height);
            vg.fillRect(width-insets.right,0,insets.right,height);
            vg.fillRect(0,height-insets.bottom,width,insets.bottom);

            // Get the scale.
            if (scaleChanged) {
                Scale.drawLinearScale(minHoriz,maxHoriz,
                                      width-(insets.left+insets.right),3,7,
                                      Scale.RIGHT_TICKS,
                                      pTicks[HORIZONTAL],
                                      sTicks[HORIZONTAL],
                                      labels[HORIZONTAL],
                                      positions[HORIZONTAL]);
            }

            // Create a sub-graphics context to isolate the coordinate
            // transformations.
            Graphics sg = vg.create();
            VectorGraphics svg =
                VectorGraphics.create(sg);
            svg.translate(insets.left,height-insets.bottom+2);

            // Set the color and font.
            svg.setColor(fgColor);
            svg.setFont(labelFont);

            // Draw the primary and secondary tick marks.
            svg.setStroke(thickStroke);
            svg.draw(pTicks[HORIZONTAL]);
            svg.setStroke(thinStroke);
            svg.draw(sTicks[HORIZONTAL]);

            // Paint the tick labels.
            for (int i=0; i<3; i++) {
                if (labels[HORIZONTAL][i]!=null) {
                    svg.drawString(labels[HORIZONTAL][i],
                                   (float) positions[HORIZONTAL][i],
                                   Scale.getPrimaryTickSize()+0.2f*fontSize,
                                   VectorGraphics.TEXT_CENTER,
                                   VectorGraphics.TEXT_TOP);
                }
            }

            // Paint the axis label.  The third position is always set
            // so that the axis label should be between this position
            // and one of the other two.
            float axisPosition;
            if (Math.abs(positions[HORIZONTAL][2]-positions[HORIZONTAL][1])>
                Math.abs(positions[HORIZONTAL][2]-positions[HORIZONTAL][0])) {
                axisPosition = (float) (0.5*(positions[HORIZONTAL][2]+
                                             positions[HORIZONTAL][1]));
            } else {
                axisPosition = (float) (0.5*(positions[HORIZONTAL][2]+
                                             positions[HORIZONTAL][0]));
            }
            svg.drawString(axisLabels[HORIZONTAL]+" "+
                           axisUnits[HORIZONTAL],
                           axisPosition,
                           Scale.getPrimaryTickSize()+0.2f*fontSize,
                           VectorGraphics.TEXT_CENTER,
                           VectorGraphics.TEXT_TOP);

            // End this context.
            svg.dispose();

            // Create the vertical scale.
            if (scaleChanged) {
                Scale.drawLinearScale(minVert,maxVert,
                                      height-(insets.top+insets.bottom),3,7,
                                      Scale.LEFT_TICKS,
                                      pTicks[VERTICAL],
                                      sTicks[VERTICAL],
                                      labels[VERTICAL],
                                      positions[VERTICAL]);
            }

            // Now create a new graphics context for the vertical axis.
            sg = vg.create();
            svg =
                VectorGraphics.create(sg);
            svg.translate(insets.left-2,height-insets.bottom);
            svg.rotate(-Math.PI/2.);

            // Set the color and font.
            svg.setColor(fgColor);
            svg.setFont(labelFont);

            // Draw the primary and secondary tick marks.
            svg.setStroke(thickStroke);
            svg.draw(pTicks[VERTICAL]);
            svg.setStroke(thinStroke);
            svg.draw(sTicks[VERTICAL]);

            // Paint the tick labels.
            for (int i=0; i<3; i++) {
                if (labels[VERTICAL][i]!=null) {
                    svg.drawString(labels[VERTICAL][i],
                                   (float) positions[VERTICAL][i],
                                   -(Scale.getPrimaryTickSize()+0.2f*fontSize),
                                   VectorGraphics.TEXT_CENTER,
                                   VectorGraphics.TEXT_BOTTOM);
                }
            }

            // Paint the axis label.  The third position is always set
            // so that the axis label should be between this position
            // and one of the other two.
            if (Math.abs(positions[VERTICAL][2]-positions[VERTICAL][1])>
                Math.abs(positions[VERTICAL][2]-positions[VERTICAL][0])) {
                axisPosition = (float) (0.5*(positions[VERTICAL][2]+
                                             positions[VERTICAL][1]));
            } else {
                axisPosition = (float) (0.5*(positions[VERTICAL][2]+
                                             positions[VERTICAL][0]));
            }
            svg.drawString(axisLabels[VERTICAL]+" "+
                           axisUnits[VERTICAL],
                           axisPosition,
                           -(Scale.getPrimaryTickSize()+0.2f*fontSize),
                           VectorGraphics.TEXT_CENTER,
                           VectorGraphics.TEXT_BOTTOM);

            // End this context.
            svg.dispose();

            // Reset the current width and height.
            currentWidth = width;
            currentHeight = height;
            scaleChanged = false;

        }
    }

    /**
     * Recalculate the insets based on the current font size. */
    private void resetInsets() {
        int lb = (int) (2+2+Scale.getPrimaryTickSize()+1.5*fontSize);
        int tr = (int) (2+2+Scale.getPrimaryTickSize());
        insets = new Insets(tr,lb,lb,tr);
    }

    /**
     * Set the scale labels taking into account the given linear
     * transformation. */
    public void setScales(String horizLabel,
                          String vertLabel,
                          String horizUnits,
                          String vertUnits,
                          AffineTransform transform,
                          int panelWidth,
                          int panelHeight) {

        // Determine the type of the tranform.
        int type = classifyTransform(transform);

        switch (type) {

        case (TYPE_PARALLEL_TRANSFORM):
            setAxisLabels(horizLabel,vertLabel);
            setAxisUnits(horizUnits,vertUnits);
            break;
        case (TYPE_SWITCHED_TRANSFORM):
            setAxisLabels(vertLabel,horizLabel);
            setAxisUnits(vertUnits,horizUnits);
            break;
        case (TYPE_Y_SKEW_TRANSFORM):
            setAxisLabels(horizLabel+PRIME,DELTA+vertLabel);
            if (horizUnits.equals(vertUnits)) {
                setAxisUnits(horizUnits,vertUnits);
            } else {
                setAxisUnits(horizUnits+DOT+vertUnits,vertUnits);
            }
            break;
        case (TYPE_X_SKEW_TRANSFORM):
            setAxisLabels(DELTA+horizLabel,vertLabel+PRIME);
            if (vertUnits.equals(horizUnits)) {
                setAxisUnits(horizUnits,vertUnits);
            } else {
                setAxisUnits(horizUnits,vertUnits+DOT+horizUnits);
            }
            break;
        case (TYPE_SWITCHED_Y_SKEW_TRANSFORM):
            setAxisLabels(vertLabel+PRIME,DELTA+horizLabel);
            if (vertUnits.equals(horizUnits)) {
                setAxisUnits(vertUnits,horizUnits);
            } else {
                setAxisUnits(vertUnits+DOT+horizUnits,horizUnits);
            }
            break;
        case (TYPE_SWITCHED_X_SKEW_TRANSFORM):
            setAxisLabels(DELTA+vertLabel,horizLabel+PRIME);
            if (horizUnits.equals(vertUnits)) {
                setAxisUnits(vertUnits,horizUnits);
            } else {
                setAxisUnits(vertUnits,horizUnits+DOT+vertUnits);
            }
            break;
        default:
            setAxisLabels(horizLabel+PRIME,vertLabel+PRIME);
            if (horizUnits.equals(vertUnits)) {
                setAxisUnits(horizUnits,vertUnits);
            } else {
                setAxisUnits(horizUnits+DOT+vertUnits,
                             vertUnits+DOT+horizUnits);
            }
            break;
        }

        // Get the size of the scales.
        axisPts[0] = 0.;
        axisPts[1] = panelHeight;
        axisPts[2] = 0.;
        axisPts[3] = panelHeight;
        axisPts[4] = panelWidth;
        axisPts[5] = 0.;
        try {

            // Avoid using the following call because of a bug in
            // AffineTransform.  Instead create the inverse matrix
            // explicitly as done below.
            //transform.inverseTransform(physicsPt,0,physicsPt,0,3);
            AffineTransform ixform = transform.createInverse();
            ixform.transform(axisPts,0,axisPts,0,1);
            ixform.deltaTransform(axisPts,2,axisPts,2,2);

            // Calculate the values for the vertical axis and the
            // distance.
            double vdy = axisPts[3];
            double vdx = axisPts[2];
            double vdist = Math.sqrt(vdx*vdx+vdy*vdy);

            // Calculate the values for the horizontal axis and the
            // distance.
            double hdy = axisPts[5];
            double hdx = axisPts[4];
            double hdist = Math.sqrt(hdx*hdx+hdy*hdy);

            // Initialize the endpoints of the axes.
            double vmin = 0.;
            double vmax = 0.;
            double hmin = 0.;
            double hmax = 0.;

            // Do what is necessary for the different types of
            // transformations.
            switch (type) {
            case (TYPE_PARALLEL_TRANSFORM): {
                double vsign = (vdy<0.) ? 1. : -1.;
                vmin = axisPts[1];
                vmax = axisPts[1]+vsign*vdist;

                double hsign = (hdx>0.) ? 1. : -1.;
                hmin = axisPts[0];
                hmax = axisPts[0]+hsign*hdist;
                break;
            }

            case (TYPE_SWITCHED_TRANSFORM): {
                double hsign = (hdy>0.) ? 1. : -1.;
                hmin = axisPts[1];
                hmax = axisPts[1]+hsign*hdist;

                double vsign = (vdx<0.) ? 1. : -1.;
                vmin = axisPts[0];
                vmax = axisPts[0]+vsign*vdist;
                break;
            }

            case (TYPE_Y_SKEW_TRANSFORM): {
                double vsign = (vdy>0.) ? 1. : -1.;
                vmax = -vsign*vdist/2.;
                vmin = -vmax;

                hmin = 0.;
                hmax = hdist;
                break;
            }

            case (TYPE_X_SKEW_TRANSFORM): {
                double hsign = (hdx>0.) ? 1. : -1.;
                hmax = hsign*hdist/2.;
                hmin = -hmax;

                vmin = 0.;
                vmax = vdist;
                break;
            }

            case (TYPE_SWITCHED_Y_SKEW_TRANSFORM): {
                double vsign = (vdx>0.) ? 1. : -1.;
                vmax = -vsign*vdist/2.;
                vmin = -vmax;

                hmin = 0.;
                hmax = hdist;
                break;
            }

            case (TYPE_SWITCHED_X_SKEW_TRANSFORM): {
                double hsign = (hdy>0.) ? 1. : -1.;
                hmax = hsign*hdist/2.;
                hmin = -hmax;

                vmin = 0.;
                vmax = vdist;
                break;
            }

            default: {
                vmin = 0.;
                vmax = vdist;
                hmin = 0.;
                hmax = hdist;
                break;
            }
            }

            // Actually set the limits.
            setLimits(hmin,hmax,vmin,vmax);

        } catch (Exception e) {
            setLimits(0.,0.,0.,0.);
        }
    }

    /**
     * This is a protected utility method which classifies the given
     * transform into seven categories: parallel, switched, x-skew,
     * y-skew, switched x-skew, switched y-skew, and general.  The
     * parallel category describes transformations in which the
     * transformed x and y axes are parallel or antiparallel to the
     * original x and y axes, respectively.  The switched category
     * describes transformations in which the transformed x and y axes
     * are parallel or antiparallel to the original y and x axes,
     * respectively.  That is, the x and y axes have been switched.
     * The x-skew describes transformations in which the transformed
     * x-axis is parallel (or antiparallel) to the original one while
     * the transformed y-axis forms some non-zero angle to the
     * original one.  The y-skew is similar; the switch skews are just
     * rotated (counter)clockwise by 90 degrees. The general category
     * encompasses all transforms not falling into one of the other
     * categories.  */
    static protected int classifyTransform(AffineTransform xform) {

        // Set the default return type to a general matrix.
        int category = TYPE_GENERAL_TRANSFORM;

        // Get the four non-translation quantities from the
        // transformation.
        double sx = xform.getScaleX();
        double sy = xform.getScaleY();
        double kx = xform.getShearX();
        double ky = xform.getShearY();

        // Check the type.
        if (kx==0. && ky==0.) {
            category = TYPE_PARALLEL_TRANSFORM;
        } else if (sx==0. && sy==0.) {
            category = TYPE_SWITCHED_TRANSFORM;
        } else if (kx==0.) {
            category = TYPE_Y_SKEW_TRANSFORM;
        } else if (ky==0.) {
            category = TYPE_X_SKEW_TRANSFORM;
        } else if (sx==0.) {
            category = TYPE_SWITCHED_Y_SKEW_TRANSFORM;
        } else if (sy==0.) {
            category = TYPE_SWITCHED_X_SKEW_TRANSFORM;
        }

        // Return the transformtion type.
        return category;
    }

}

