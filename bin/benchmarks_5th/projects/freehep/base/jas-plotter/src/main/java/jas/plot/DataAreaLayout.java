package jas.plot;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

public final class DataAreaLayout implements LayoutManager2 {

    public static final String X_AXIS = "x";
    public static final String Y_AXIS_LEFT = "yl";
    public static final String Y_AXIS_RIGHT = "yr";
    public static final String X_AXIS_LABEL = "xl";
    public static final String Y_AXIS_LEFT_LABEL = "yll";
    public static final String Y_AXIS_RIGHT_LABEL = "yrl";
    
    private Axis xAxis;
    private Axis yAxis_left;
    private Axis yAxis_right;
    private EditableLabel xAxis_label;
    private EditableLabel yAxis_left_label;
    private EditableLabel yAxis_right_label;
    private static final int pad = 5;
    private static final SpaceRequirements nullSpaceRequirements = new SpaceRequirements(); // all fields are zero
    private int xAxisLength = -1;
    private int yLeftAxisLength = -1;
    private int yRightAxisLength = -1;
    private boolean hasColorMap = false;
    private Dimension lastParentSize;
    private static final boolean debug;

    static {
        boolean result;
        try {
            result = System.getProperty("debugDataAreaLayout") != null;
        } catch (SecurityException x) // in case we are in an applet!
        {
            result = false;
        }
        debug = result;
    }

    @Override
    public void addLayoutComponent(final Component c, final Object constraints) {
        if (c instanceof Axis) {
            if (X_AXIS.equals(constraints)) {
                xAxis = (Axis) c;
                xAxisLength = -1;
            } else if (Y_AXIS_LEFT.equals(constraints)) {
                yAxis_left = (Axis) c;
                yLeftAxisLength = -1;
            } else if (Y_AXIS_RIGHT.equals(constraints)) {
                yAxis_right = (Axis) c;
                yRightAxisLength = -1;
                if (c instanceof ColorMapAxis) {
                    hasColorMap = true;
                }
            }
        } else if (c instanceof EditableLabel) {
            if (X_AXIS_LABEL.equals(constraints)) {
                xAxis_label = (EditableLabel) c;
            } else if (Y_AXIS_LEFT_LABEL.equals(constraints)) {
                yAxis_left_label = (EditableLabel) c;
            } else if (Y_AXIS_RIGHT_LABEL.equals(constraints)) {
                yAxis_right_label = (EditableLabel) c;
            }
        }
    }

    @Override
    public void addLayoutComponent(final String s, final Component c) {
        addLayoutComponent(c, s);
    }

    @Override
    public void removeLayoutComponent(final Component c) {
        if (c == xAxis) {
            xAxis = null;
        } else if (c == yAxis_left) {
            yAxis_left = null;
        } else if (c == yAxis_right) {
            yAxis_right = null;
            if (hasColorMap) {
                hasColorMap = false;
            }
        } else if (c == xAxis_label) {
            xAxis_label = null;
        } else if (c == yAxis_left_label) {
            yAxis_left_label = null;
        } else if (c == yAxis_right_label) {
            yAxis_right_label = null;
        }
    }

    private int getLabelSpaceOnTop() {
        int labelSpaceOnTop = 0;
        if (yAxis_left_label != null && !yAxis_left_label.isRotated()) {
            labelSpaceOnTop = yAxis_left_label.getPreferredSize().height + pad;
        }
        if (yAxis_right_label != null && /* no label without matching axis */ yAxis_right != null && !yAxis_right_label.isRotated()) {
            labelSpaceOnTop = Math.max(yAxis_right_label.getPreferredSize().height, labelSpaceOnTop);
        }
        return labelSpaceOnTop;
    }

    @Override
    public void layoutContainer(final Container parent) {
        if (yAxis_left != null && xAxis != null) { // there must be a left axis; right one optional

            final Dimension parentSize = parent.getSize();
            if (parentSize.width <= 0 || parentSize.height <= 0) return; // not sure why this is necessary

            final int labelSpaceOnTop = getLabelSpaceOnTop();
            final int labelSpaceOnBottom = xAxis_label != null ? xAxis_label.getPreferredSize().height + pad : 0;
            final Insets insets = parent.getInsets();
            final int width = parentSize.width - insets.right - insets.left - (hasColorMap ? 30 : 0);
            final int height = parentSize.height - insets.top - insets.bottom - labelSpaceOnTop - labelSpaceOnBottom;
            // we know that the axes always use the same objects for space requirements, so we can obtain
            // final references to them now and have them calculate values later as many times as we need
            final SpaceRequirements x = xAxis.type.spaceRequirements;
            final SpaceRequirements y_left = yAxis_left.type.spaceRequirements;
            final SpaceRequirements y_right = yAxis_right != null ? yAxis_right.type.spaceRequirements : nullSpaceRequirements;

            // now we have to set the axis lengths
            if (lastParentSize != null && xAxisLength > 0) {
                xAxisLength += parentSize.width - lastParentSize.width;
            } else {
                xAxisLength = parentSize.width * 9 / 10; // a first estimate
            }
            if (lastParentSize != null && yLeftAxisLength > 0) {
                yLeftAxisLength += parentSize.height - lastParentSize.height;
            } else {
                yLeftAxisLength = parentSize.height * 9 / 10; // a first estimate
            }
            if (lastParentSize != null && yRightAxisLength > 0 && yAxis_right != null) {
                yRightAxisLength += parentSize.height - lastParentSize.height;
            } else {
                yRightAxisLength = parentSize.height * 9 / 10; // a first estimate
            }
            
            int xorigin;
            int yorigin;
            int distFromRightSide;

            int iterationCounter = 0;
            final int maxIterations = 8; // If we do more that this many iterations, we will give up and quit

            int x_smallest = 0;
            int y_left_smallest = 0;
            int y_right_smallest = 0;
            final int normalMaximumNumberOfIterations = 2; // If we do more than this many iterations, we have an
            // unusual situation, so we will start work in plan B
            final int criticalNumberOfIterations = 5; // If we do more than this many iterations, plan A has definitely
            // failed so we need to start implement the work we started earlier
            // Definitions of plans A and B:
            //  * In plan A, we hope to get an exact fit.  This works in one or two iterations in the vast majority of cases.
            //    We just keep adjusting the axis lengths until we get that perfect fit.
            //  * Plan B is our last resort when plan A takes too many iterations.  We will take the smallest axis lengths
            //    from all of the attempts and use those for the lengths.  This means that an all axes will assume
            //    smaller lengths than they actually get, so there will be fewer labels than there could be.

            while (true) {
                
                boolean allLengthsAreAdequate = true;
                boolean lastIteration = iterationCounter >= (maxIterations - 1);

                if (iterationCounter < criticalNumberOfIterations) { // This is the normal case, where we have few iterations so far
                    xAxis.assumeAxisLength(xAxisLength);
                    yAxis_left.assumeAxisLength(yLeftAxisLength);
                    if (yAxis_right != null) yAxis_right.assumeAxisLength(yRightAxisLength);
                } else { // We've gone too far for plan A, so we'd better start using the minimum values we've been tracking
                    if (debug) System.out.println("******* USED BACKUP PLAN FOR LAYOUT");
                    xAxis.assumeAxisLength(x_smallest);
                    yAxis_left.assumeAxisLength(y_left_smallest);
                    if (yAxis_right != null) yAxis_right.assumeAxisLength(y_right_smallest);
                }

                xorigin = Math.max(x.width, y_left.width) + Axis.padAroundEdge;
                yorigin = Math.max(Math.max(x.height, y_left.height), y_right.height) + Axis.padAroundEdge;
                distFromRightSide = Math.max(x.flowPastEnd, y_right.width) + Axis.padAroundEdge;

                final int minWidth = xorigin + distFromRightSide + xAxisLength + insets.left + insets.right;
                if (minWidth != parentSize.width && !lastIteration) {
                    xAxisLength = Math.max(0, xAxisLength + parentSize.width - minWidth);
                    allLengthsAreAdequate = false;
                }

                int minHeight = yorigin + y_left.flowPastEnd + Axis.padAroundEdge
                        + yLeftAxisLength + insets.top + insets.bottom;
                if (minHeight != parentSize.height && !lastIteration) {
                    yLeftAxisLength = Math.max(0, yLeftAxisLength + parentSize.height - minHeight);
                    allLengthsAreAdequate = false;
                }

                if (yAxis_right != null) {
                    minHeight = yorigin + y_right.flowPastEnd + Axis.padAroundEdge
                            + yRightAxisLength + insets.top + insets.bottom;
                    if (minHeight != parentSize.height && !lastIteration) {
                        yRightAxisLength = Math.max(0, yRightAxisLength + parentSize.height - minHeight);
                        allLengthsAreAdequate = false;
                    }
                }

                iterationCounter++;
                if (allLengthsAreAdequate) {
                    if (debug) {
                        System.out.println("layout required " + iterationCounter + " iteration(s)");
                    }
                    break;
                }
                if (iterationCounter > normalMaximumNumberOfIterations) // If this executes, we've had more than normal number of
                // iterations, so we start tracking the sizes to come up with
                // a size that will work for all sizes
                {
                    if (xAxisLength > 0) {
                        if (x_smallest == 0) {
                            x_smallest = xAxisLength;
                        } else if (xAxisLength < x_smallest) {
                            x_smallest = xAxisLength;
                        }
                    }
                    if (yLeftAxisLength > 0) {
                        if (y_left_smallest == 0) {
                            y_left_smallest = yLeftAxisLength;
                        } else if (yLeftAxisLength < y_left_smallest) {
                            y_left_smallest = yLeftAxisLength;
                        }
                    }
                    if (yAxis_right != null && yRightAxisLength > 0) {
                        if (y_right_smallest == 0) {
                            y_right_smallest = yRightAxisLength;
                        } else if (yRightAxisLength < y_right_smallest) {
                            y_right_smallest = yRightAxisLength;
                        }
                    }
                }
                if (iterationCounter >= maxIterations) {
                    throw new LayoutFailed();
                }
            }

            if (yAxis_left_label != null && yAxis_left_label.isRotated()) {
                xorigin += yAxis_left_label.getPreferredSize().getHeight() + 5;
            }

            yAxis_left.setLocation(xorigin - y_left.width + insets.left, insets.top + labelSpaceOnTop);
            yAxis_left.setSize(y_left.width, height - yorigin + y_left.height);

            if (yAxis_left_label != null) {
                if (yAxis_left_label.isRotated()) {
                    yAxis_left_label.setLocation(pad, pad + insets.top + (height - (int) yAxis_left_label.getPreferredSize().getWidth()) / 2);
                    yAxis_left_label.setSize((int) yAxis_left_label.getPreferredSize().getWidth(), (int) yAxis_left_label.getPreferredSize().getWidth());
                } else {
                    yAxis_left_label.setLocation(insets.left + pad, insets.top + pad);
                    yAxis_left_label.setSize(yAxis_left_label.getPreferredSize());
                }
            }

            if (yAxis_right_label != null && yAxis_right_label.isRotated()) {
                distFromRightSide += (int) yAxis_right_label.getPreferredSize().getHeight();
            }

            xAxis.setLocation(xorigin - x.width + insets.left, height + insets.top - yorigin + labelSpaceOnTop);
            xAxis.setSize(width - xorigin + x.width - distFromRightSide + Axis.padAroundEdge + x.flowPastEnd, x.height);

            if (xAxis_label != null) {
                final Dimension prefSize = xAxis_label.getPreferredSize();
                xAxis_label.setLocation((width - xorigin - distFromRightSide) / 2 + xorigin - prefSize.width / 2 + insets.left,
                        parentSize.height - insets.bottom - pad - prefSize.height);
                xAxis_label.setSize(prefSize);
            }

            if (yAxis_right != null) {

                yAxis_right.setLocation(width - distFromRightSide - insets.left, insets.top + labelSpaceOnTop);
                yAxis_right.setSize(y_right.width + (hasColorMap ? 30 : 0), height - yorigin + y_right.height);

                if (yAxis_right_label != null) {
                    final Dimension prefSize = yAxis_right_label.getPreferredSize();
                    if (yAxis_right_label.isRotated()) {
                        yAxis_right_label.setLocation(parentSize.width - insets.right - pad - prefSize.height, pad + insets.top + (height - (int) prefSize.getWidth()) / 2);
                        yAxis_right_label.setSize((int) prefSize.getWidth(), (int) prefSize.getWidth());
                    } else {
                        yAxis_right_label.setLocation(parentSize.width - insets.right - pad - prefSize.width, insets.top + pad);
                        yAxis_right_label.setSize(prefSize);
                    }
                }
            }
            lastParentSize = parentSize;
        }
    }

    @Override
    public Dimension minimumLayoutSize(final Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public Dimension maximumLayoutSize(final Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public Dimension preferredLayoutSize(final Container parent) {
        return new Dimension(10, 10);
    }

    /**
     * Returns the alignment along the x axis. This specifies how the component
     * would like to be aligned relative to other components. The value should
     * be a number between 0 and 1 where 0 represents alignment along the
     * origin, 1 is aligned the furthest away from the origin, 0.5 is centered,
     * etc.
     */
    @Override
    public float getLayoutAlignmentX(final Container parent) {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis. This specifies how the component
     * would like to be aligned relative to other components. The value should
     * be a number between 0 and 1 where 0 represents alignment along the
     * origin, 1 is aligned the furthest away from the origin, 0.5 is centered,
     * etc.
     */
    @Override
    public float getLayoutAlignmentY(final Container parent) {
        return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager has cached
     * information it should be discarded.
     */
    @Override
    public void invalidateLayout(final Container target) {
    }
}

class LayoutFailed extends RuntimeException {
}
