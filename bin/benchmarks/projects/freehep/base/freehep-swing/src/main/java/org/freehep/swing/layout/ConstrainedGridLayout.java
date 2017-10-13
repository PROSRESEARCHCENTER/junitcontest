// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/** 
 * The <code>ConstrainedGridLayout</code> layout manager is based on
 * the <code>GraphPaperLayout</code> layout manager written by Michael
 * Martak. 
 *
 * This layout manager divides the container into a set of equally
 * sized cells arranged in a grid which has <code>nw</code> cells
 * in the horizontal direction and <code>nh</code> cells in the
 * vertical direction.  Components can occupy an rectangular subset of
 * these cells; the component's position and size are based on given
 * rectangular constraints.
 * 
 * This extends the functionality of the <code>GraphPaperLayout</code>
 * manager by also constraining the overall aspect ratio of the
 * container.   It will also force a containing window to resize if
 * the desired size of the container isn't a size which is allowed by
 * the contraints.  A byproduct of this is that the preferred sizes of
 * components controlled by this layout manager will be reset to the
 * largest allowed size which is smaller than the current size.
 *
 * Components can overlap making this layout manager a good choice for
 * a <code>JLayeredPane</code>.  To allow the standard moveToFront and
 * moveToBack methods to work as expected, the constraints of a
 * removed component are retained so that if it is later added again
 * without contraints the layout manager will still arrange the
 * components reasonably.  (The standard moveToFront implementation,
 * removes a component and re-adds it without taking into account the
 * possible constraints.)
 *
 * @author Charles A. Loomis, Jr.
 * @version $Id: ConstrainedGridLayout.java 8584 2006-08-10 23:06:37Z duns $ 
 * */

public class ConstrainedGridLayout 
    implements LayoutManager2 {

    /**
     * The virtual grid size. */
    private Dimension gridSize;

    /**
     * The aspect ratio of the container. */
    private Dimension aspectRatio;

    /**
     * The smallest dimension which maintains the aspect ratio and has
     * a width and height which are multiples of the grid size. */
    private Dimension containerFormat;

    final public static String GRID_SIZE_ERROR =
	"Grid size must have width and height > 0.";
    final public static String ASPECT_RATIO_ERROR =
	"Aspect ratio must have width and height > 0.";
    final public static String CONSTRAINT_ERROR = 
	"Constraint must be a Rectangle with positive width and height.";

    final public static String CONSTRAINED_GRID_LAYOUT_CONSTRAINT = 
	"ConstrainedGridLayout.RectangularConstraint";

    final public static int MINIMUM_LAYOUT = 0;
    final public static int PREFERRED_LAYOUT = 1;
    
    /**
     * Creates a <code>ConstrainedGridLayout</code> with the given
     * grid size and aspect ratio. 
     *
     * @param gridSize size of grid in logical units (width x height)
     * @param aspectRatio aspect ratio of container (width x height) */
    public ConstrainedGridLayout(Dimension gridSize, 
				 Dimension aspectRatio) {

	// Check that all of the dimensions given are reasonable.
	if (gridSize.width<=0 || gridSize.height<=0 ||
	    gridSize.width>100 || gridSize.height>100) {
	    throw new IllegalArgumentException(GRID_SIZE_ERROR);
	}
	if (aspectRatio.width<=0 || aspectRatio.height<=0 ||
	    aspectRatio.width>100 || aspectRatio.height>100) {
	    throw new IllegalArgumentException(ASPECT_RATIO_ERROR);
	}

	// Copy the information into the layout manager.
	this.gridSize = new Dimension(gridSize);
	this.aspectRatio = new Dimension(aspectRatio);

	// Calculate the minimum container format.
	containerFormat = new Dimension();
	containerFormat = getSmallestDimension(containerFormat,
					       aspectRatio, 
					       gridSize);
    }

    /**
     * Return a new <code>Dimension</code> which is a copy of the
     * current grid size.
     *
     * @return current grid size */
    public Dimension getGridSize() {
	return new Dimension(gridSize);
    }

    /**
     * Set the current grid size to the given <code>Dimension</code>.
     * The grid size must have horizontal and vertical components
     * which are positive.
     *
     * @param gridSize new grid size */
    public void setGridSize(Dimension gridSize) {
	if (gridSize.width<=0 || gridSize.height<=0 ||
	    gridSize.width>100 || gridSize.height>100) {
	    throw new IllegalArgumentException(GRID_SIZE_ERROR);
	}
	this.gridSize.setSize(gridSize);

	// Get the smallest fraction which keeps the aspect ratio and
	// grid size.
	containerFormat = getSmallestDimension(containerFormat,
					       aspectRatio,
					       gridSize);
    }
    
    /**
     * Return a new <code>Dimension</code> which is a copy of the
     * current aspect ratio.
     *
     * @return current aspect ratio */
    public Dimension getAspectRatio() {
	return new Dimension(aspectRatio);
    }

    /**
     * Set the current aspect ratio to the given
     * <code>Dimension</code>.  The aspect ratio must have horizontal
     * and vertical components which are positive.
     *
     * @param aspectRatio new aspect ratio for the container */
    public void setAspectRatio(Dimension aspectRatio) {
	if (aspectRatio.width<=0 || aspectRatio.height<=0) {
	    throw new IllegalArgumentException(ASPECT_RATIO_ERROR);
	}
	this.aspectRatio.setSize(aspectRatio);

	// Get the smallest fraction which keeps the aspect ratio and
	// grid size.
	containerFormat = getSmallestDimension(containerFormat,
					       aspectRatio,
					       gridSize);
    }
    
    /**
     * Get the constraints being used for the given component.
     *
     * @param comp the component to lookup
     *
     * @return <code>Rectangle</code> describing the position and size
     * of the given component */
    public Rectangle getConstraints(Component comp) {
	Rectangle r = null;
	if (comp instanceof JComponent) {
	    JComponent jc = (JComponent) comp;
	    Object constraint = 
		jc.getClientProperty(CONSTRAINED_GRID_LAYOUT_CONSTRAINT);
	    if (constraint instanceof Rectangle) {
		r = (Rectangle) constraint;
	    }
	}
	return (r!=null) ? new Rectangle(r) : null;
    }

    /**
     * Set (or reset) the constraints for the given component. 
     *
     * @param comp the component to constrain
     * @param constraints <code>Rectangle</code> describing the
     * position and size of the component */
    public void setConstraints(Component comp, Rectangle constraints) {
	if (comp instanceof JComponent) {
	    Rectangle copy = new Rectangle(constraints);
	    JComponent jc = (JComponent) comp;
	    jc.putClientProperty(CONSTRAINED_GRID_LAYOUT_CONSTRAINT,
				 copy);
	}
    }
    
    /**
     * Adds the specified component with the specified name to the
     * layout.  <code>ConstrainedGridLayout</code> will arrange this
     * component normally if a constraint has been set previously or
     * is set before the container is next validated.  If not, then
     * this component will be ignored by this layout manager.
     *
     * The component name is always ignored by this layout manager.
     *
     * @param name name of component (ignored)
     * @param comp component to add to the layout */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout.  This method
     * actually does nothing since the component list is obtained from
     * the container when it is laid-out.  
     *
     * The constraint is still stored in case the component is later
     * added again to the container without the constraint being
     * specified.  (Happens when the standard implementation of
     * moveToFront in a <code>JLayeredPane</code> is called.)
     *
     * @param comp the component to be removed */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Calculates the preferred size dimensions for the specified 
     * panel given the components in the specified parent container.
     *
     * @param parent the component to be laid out
     *  
     * @see #minimumLayoutSize */
    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize(parent, PREFERRED_LAYOUT);
    }

    /** 
     * Calculates the minimum size dimensions for the specified 
     * panel given the components in the specified parent container.
     *
     * @param parent the component to be laid out
     *
     * @see #preferredLayoutSize */
    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent, MINIMUM_LAYOUT);
    }
    
    /** 
     * Calculate the largest minimum or preferred cell size.  
     *
     * For the minimum or preferred cell sizes: The components'
     * minimum or preferred sizes are obtained and then divided by the
     * number of rows and columns that the component spans.  The
     * largest cell size is returned.
     *
     * @param parent the container in which to do the layout.
     * @param selectionFlag either MINIMUM_LAYOUT or PREFERRED_LAYOUT.
     *
     * @return the appropriate cell size */
    protected Dimension getLayoutSize(Container parent, 
				      int selectionFlag) {

	// Keep track of the cell size.
	int cellHeight = 0;
	int cellWidth = 0;

	// Loop over all of the parent's components.
        for (int i=0; i<parent.getComponentCount(); i++) {

	    // Get the component and the constraint.
            Component c = parent.getComponent(i);
            Rectangle r = getConstraints(c);

            if (c!=null && r!=null) {

                Dimension componentSize = null;

		switch (selectionFlag) {

		case MINIMUM_LAYOUT: 
		    componentSize = c.getMinimumSize();
		    break;

		case PREFERRED_LAYOUT:
                    componentSize = c.getPreferredSize();
		    break;
		}

		// Select the largest width and height needed.
		cellWidth = 
		    Math.max(cellWidth,
			     componentSize.width/r.width);
		cellHeight = 
		    Math.max(cellHeight,
			     componentSize.height/r.height);
            }
        }

	// Calculate the total MINIMUM width and height needed.
	int totalWidth = cellWidth*gridSize.width;
	int totalHeight = cellHeight*gridSize.height;

	int widthMultiple = totalWidth/containerFormat.width;
	int heightMultiple = totalHeight/containerFormat.height;
	int finalMultiple = Math.max(widthMultiple, heightMultiple);

	Dimension containerSize = 
	    new Dimension(containerFormat.width*finalMultiple,
			  containerFormat.height*finalMultiple);

	if (parent instanceof JComponent)
	    ((JComponent) parent).setPreferredSize(containerSize);

	return containerSize;
    }

    /**
     * Adjust the given size of the parent (minus the insets) to an
     * appropriate size.   This being the largest size which fits
     * inside of the original size and is a multiple of the aspect
     * ratio and of the grid size.  Note that the input
     * <code>Dimension</code> is modified and returned rather than
     * creating a new object.
     *
     * @param trialSize parent's size minus insets to adjust;
     * trialSize is overwritten with the adjusted size of the
     * component 
     *
     * @return boolean flag indicating whether the size was actually
     * changed */
    public boolean adjustSize(Dimension trialSize) {

	int originalWidth = trialSize.width;
	int originalHeight = trialSize.height;

	int widthMultiple = 
	    Math.max(1,trialSize.width/containerFormat.width);
	int heightMultiple = 
	    Math.max(1,trialSize.height/containerFormat.height);

	int finalMultiple = 
	    Math.min(widthMultiple, heightMultiple);

	trialSize.width = containerFormat.width*finalMultiple;
	trialSize.height = containerFormat.height*finalMultiple;

	return (originalWidth!=trialSize.width ||
		originalHeight!=trialSize.height);
    }

    /**
     * Change the size of the component by the given increment in the
     * size.  Positive increments enlarge the component; negative
     * increments reduce the component.
     *
     * @param parent the component controlled by this layout manager
     * @param sizeIncrement number of increments by which to increase
     * the size
     *
     * @return flag indicating whether the size actually changed  */
    public boolean adjustSize(Container parent, int sizeIncrement) {

	boolean sizeChanged = false;

	if (parent instanceof JComponent) {
	    
	    JComponent jc = (JComponent) parent;

	    Insets insets = jc.getInsets();
	    Dimension size = jc.getSize();
	    size.width -= (insets.left+insets.right);
	    size.height -= (insets.top+insets.bottom);
	    
	    int originalMultiple = size.width/containerFormat.width;
	    int newMultiple =
		Math.max(1,originalMultiple+sizeIncrement);

	    if (originalMultiple!=newMultiple) {
		size.width = containerFormat.width*newMultiple;
		size.height = containerFormat.height*newMultiple;
		
		// Add in the insets again.
		size.width += (insets.left+insets.right);
		size.height += (insets.top+insets.bottom);

		// Set the preferred size.
		sizeChanged = true;
		jc.setPreferredSize(size);
	    }
	}

	// If another validation is needed, then tell the window
	// containing this container.
	if (sizeChanged)
	    SwingUtilities.invokeLater(new RunAnotherLayout(parent));

	return sizeChanged;
    }

    /** 
     * Lays out the container in the specified container.
     *
     * @param parent the component which needs to be laid out */
    public void layoutContainer(Container parent) {

	// Flag used to indicate that the constraints caused this
	// layout manager to set the size of the component to a
	// smaller one than requested.  Force the containing window to
	// layout its contents again.
	boolean needAnotherLayout = false;

	// Lock the component tree while the layout is in progress.
        synchronized (parent.getTreeLock()) {

	    // Get the number of components and the parent's insets.
            int n = parent.getComponentCount();
            Insets insets = parent.getInsets();
	    
	    // Total size of the parent without the insets.
	    Dimension size = parent.getSize();
	    size.width -= (insets.left+insets.right);
	    size.height -= (insets.top+insets.bottom);
	    
	    // Adjust the size for the constraints (component size
	    // and aspect ratio).  If an adjustment is made then
	    // the layout will need to be done again.  (But only
	    // do this if it is a JComponent, otherwise this will
	    // just cause an infinite loop.) 
	    if (parent instanceof JComponent) {
		
		JComponent jc = (JComponent) parent;
		
		needAnotherLayout = adjustSize(size);
		
		// Set the parent's preferred size.
		if (needAnotherLayout) {
		    
		    // Calculate the full size of parent.
		    Dimension fullSize = new Dimension(size);
		    fullSize.width += (insets.left+insets.right);
		    fullSize.height += (insets.top+insets.bottom);
		    
		    jc.setPreferredSize(fullSize);
		}
	    }
		    
	    if (n>0) {
            
		// Calculate the cell dimensions.
		Dimension cellSize = 
		    new Dimension(size.width/gridSize.width,
				  size.height/gridSize.height);

		// Now resize the components.
		for (int i=0; i<n; i++) {
		    Component c = parent.getComponent(i);
		    Rectangle r = getConstraints(c);

		    // If the constraints exist, then resize.  Also
		    // set the preferred size of the components to the
		    // given size; this way extra space will be
		    // removed at the next revalidation of the
		    // container. 
		    if (r!=null) {
			int x = insets.left + (r.x * cellSize.width);
			int y = insets.top + (r.y * cellSize.height);
			int w = r.width*cellSize.width;
			int h = r.height*cellSize.height;

			// Set the size of the component.
			c.setBounds(x, y, w, h);
		    }
		}
	    }
	}

	// If another validation is needed, then tell the window
	// containing this container.
	if (needAnotherLayout)
	    SwingUtilities.invokeLater(new RunAnotherLayout(parent));
    }

    /**
     * Return a rectangle containing the constrained bounds for the
     * given input bounds.  This is used to constrain the size of a
     * component to this layout manager's grid.
     *
     * @param parent the Container which contains this component
     * @param constrainedBounds the rectangle which will be
     * overwritten with the result (if null, new Rectangle is created) 
     * @param inputBounds the input bounds to constrain to this layout
     * manager's grid 
     *
     * @return the constrained rectangle */
    public Rectangle getConstrainedBounds(Container parent,
					  Rectangle constrainedBounds,
					  Rectangle inputBounds) {

	// Get the parent's insets.
	Insets insets = parent.getInsets();
	
	// Total size of the parent without the insets.
	Dimension size = parent.getSize();
	size.width -= (insets.left+insets.right);
	size.height -= (insets.top+insets.bottom);

	// Get the number of pixels per division.
	int xPixelsPerDivision = size.width/gridSize.width;
	int yPixelsPerDivision = size.height/gridSize.height;

	// Get the origin of the input rectangle.
	int x0 = inputBounds.x - insets.left;
	int y0 = inputBounds.y - insets.top;

        // First quantize the width and height.
        int iw = (int) 
            Math.round(((float) inputBounds.width)*gridSize.width/
                       ((float) parent.getWidth()));
        int ih = (int) 
            Math.round(((float) inputBounds.height)*gridSize.height/
                       ((float) parent.getHeight()));

        // And force these to be no bigger than the full grid size.  Don't
        // allow the window to disappear in either direction.
        iw = Math.max(1,Math.min(iw,gridSize.width));
        ih = Math.max(1,Math.min(ih,gridSize.height));

        // Now calculate the quantized origin.
        int ix0 = (int) 
            Math.round(((float) x0)*gridSize.width/
                       ((float) parent.getWidth()));
        int iy0 = (int) 
            Math.round(((float) y0)*gridSize.width/
                       ((float) parent.getHeight()));

        // Constrain these to allow the whole window to be within the grid. 
        ix0 = Math.max(0,Math.min(ix0,gridSize.width-iw));
        iy0 = Math.max(0,Math.min(iy0,gridSize.height-ih));

	// Calculate the origin in pixel coordinates.
	x0 = xPixelsPerDivision*ix0 + insets.left;
	y0 = yPixelsPerDivision*iy0 + insets.top;

	// Make the return rectangle.
	Rectangle result = constrainedBounds;
	if (result==null) result = new Rectangle();

	// Fill in the values.
	result.width = xPixelsPerDivision*iw;
	result.height = yPixelsPerDivision*ih;
	result.x = x0;
	result.y = y0;

	return result;
    }

    /**
     * Return a rectangle containing the appropriate constraint
     * rectangle for the given input bounds.  This constrains the
     * input bounds to this layout manager's grid and returns the
     * constraint rectangle in grid coordinates.
     *
     * @param parent the Container which contains this component
     * @param constraints the rectangle which will be
     * overwritten with the result (if null, new Rectangle is created) 
     * @param inputBounds the input bounds to constrain to this layout
     * manager's grid 
     *
     * @return the constraint rectangle */
    public Rectangle getConstraints(Container parent,
				    Rectangle constraints,
				    Rectangle inputBounds) {

	// Get the parent's insets.
	Insets insets = parent.getInsets();
	
	// Total size of the parent without the insets.
	Dimension size = parent.getSize();
	size.width -= (insets.left+insets.right);
	size.height -= (insets.top+insets.bottom);

	// Get the origin of the input rectangle.
	int x0 = inputBounds.x - insets.left;
	int y0 = inputBounds.y - insets.top;

        // First quantize the width and height.
        int iw = (int) 
            Math.round(((float) inputBounds.width)*gridSize.width/
                       ((float) parent.getWidth()));
        int ih = (int) 
            Math.round(((float) inputBounds.height)*gridSize.height/
                       ((float) parent.getHeight()));

        // And force these to be no bigger than the full grid size.  Don't
        // allow the window to disappear in either direction.
        iw = Math.max(1,Math.min(iw,gridSize.width));
        ih = Math.max(1,Math.min(ih,gridSize.height));

        // Now calculate the quantized origin.
        int ix0 = (int) 
            Math.round(((float) x0)*gridSize.width/
                       ((float) parent.getWidth()));
        int iy0 = (int) 
            Math.round(((float) y0)*gridSize.width/
                       ((float) parent.getHeight()));

        // Constrain these to allow the whole window to be within the grid. 
        ix0 = Math.max(0,Math.min(ix0,gridSize.width-iw));
        iy0 = Math.max(0,Math.min(iy0,gridSize.height-ih));

	// Make the return rectangle.
	Rectangle result = constraints;
	if (result==null) result = new Rectangle();

	// Fill in the values.
	result.width = iw;
	result.height = ih;
	result.x = ix0;
	result.y = iy0;

	return result;
    }

    /**
     * This private class simply forces the containing window to
     * layout its components once again.  This is used to force the
     * window to remove extra space which was created by satisying the
     * contraints of this layout manager. */
    private class RunAnotherLayout
	implements Runnable {

	Container parent;

	public RunAnotherLayout(Container parent) {
	    this.parent = parent;
	}
	public void run() {
	    Window window = 
		(Window) SwingUtilities.getWindowAncestor(parent);

	    if (window!=null) window.pack();
	}
    }
    
    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.
     *
     * @param comp the component to be added
     * @param constraints where/how the component is added to the
     * layout; this must be a <code>Rectangle</code> */
    public void addLayoutComponent(Component comp, Object constraints) {

        if (constraints instanceof Rectangle) {

            Rectangle r = (Rectangle) constraints;
            if (r.width<=0 || r.height<=0) {
                throw new IllegalArgumentException(CONSTRAINT_ERROR);
            }

	    // Go ahead and put in the given constraints.
            setConstraints(comp, r);

        } else if (constraints != null) {
            throw new IllegalArgumentException(CONSTRAINT_ERROR);
        }
    }

    /** 
     * Returns the maximum size of this component.  This just returns
     * the largest size possible.
     *
     * @see java.awt.Component#getMinimumSize()
     * @see java.awt.Component#getPreferredSize()
     * @see LayoutManager */
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis.  This specifies how the
     * component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1 where
     * 0 represents alignment along the origin, 1 is aligned the
     * furthest away from the origin, 0.5 is centered, etc. 
     *
     * This just returns the centering alignment (0.5). */
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how the
     * component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1 where
     * 0 represents alignment along the origin, 1 is aligned the
     * furthest away from the origin, 0.5 is centered, etc. 
     *
     * This just returns the centering alignment. */
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.  */
    public void invalidateLayout(Container target) {
    }

    /**
     * The first prime numbers up to 100. */
    final private static int[] primes = { 1,  2,  3,  5,  7, 11,
					13, 17, 19, 23, 29,
					31, 37, 41, 43, 47,
					53, 59, 61, 67, 71,
                                        73, 79, 83, 89, 97};

    /**
     * The prime factorizations of the numbers up to 100. */
    final private static int[][] factorization = {
	{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,3,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,3,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
	{1,4,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
	{1,1,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,3,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
	{1,2,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,2,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0},
	{1,2,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0},
	{1,3,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0},
	{1,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0},
	{1,4,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0},
	{1,2,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,3,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
	{1,1,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,5,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
	{1,1,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,0,2,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	{1,2,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};

    /**
     * Get the smallest dimension which maintains the aspect ratio and
     * has a width and height which is evenly divisible by the grid
     * size. */
    private static Dimension getSmallestDimension(Dimension containerFormat,
						  Dimension aspectRatio,
						  Dimension gridSize) {

	int[] widthFactors = new int[primes.length];
	int[] widthCommonDenominator = new int[primes.length];
	int[] heightFactors = new int[primes.length];
	int[] heightCommonDenominator = new int[primes.length];

	// Make the prime factorization for the product.
	primeFactorsOfProduct(widthFactors,
			      aspectRatio.width,
			      gridSize.width,
			      gridSize.height);
	primeFactorsOfProduct(heightFactors,
			      aspectRatio.height,
			      gridSize.width,
			      gridSize.height);

	// Get the least common denominator for the width and height.
	leastCommonDenominator(widthCommonDenominator,
			       aspectRatio.width, gridSize.width);
	leastCommonDenominator(heightCommonDenominator,
			       aspectRatio.height, gridSize.height);

	// Remove the common factors.
	removeFactors(widthFactors, widthCommonDenominator);
	removeFactors(heightFactors, heightCommonDenominator);
	removeCommonFactors(widthFactors,heightFactors);

	// Put back in the common denominators.
	addFactors(widthFactors, widthCommonDenominator);
	addFactors(heightFactors, heightCommonDenominator);

	// Return the modified dimension.
	containerFormat.setSize(product(widthFactors),
				product(heightFactors));
	return containerFormat;
    }

    private static void primeFactorsOfProduct(int[] result, 
					     int a, int b, int c) {
	int[] factorsA = factorization[a];
	int[] factorsB = factorization[b];
	int[] factorsC = factorization[c];
	for (int i=0; i<result.length; i++) {
	    result[i] = factorsA[i]+factorsB[i]+factorsC[i];
	}
    }

    private static void leastCommonDenominator(int[] result, int a, int b) {
	int[] factorsA = factorization[a];
	int[] factorsB = factorization[b];
	for (int i=0; i<result.length; i++) {
	    result[i] = Math.max(factorsA[i], factorsB[i]);
	}
    }

    private static void removeFactors(int[] a, int[] b) {
	for (int i=0; i<Math.min(a.length,b.length); i++) {
	    a[i] -= b[i];
	}
    }

    private static void addFactors(int[] a, int[] b) {
	for (int i=0; i<Math.min(a.length,b.length); i++) {
	    a[i] += b[i];
	}
    }

    private static void removeCommonFactors(int[] a, int[] b) {
	for (int i=0; i<Math.min(a.length,b.length); i++) {
	    int common = Math.min(a[i],b[i]);
	    a[i] -= common;
	    b[i] -= common;
	}
    }

    private static int product(int[] arrays1) {
	int result = 1;
	for (int i=0; i<arrays1.length; i++) {
	    int number = primes[i];
	    for (int j=0; j<arrays1[i]; j++) {
		result *= number;
	    }
	}
	return result;
    }

}
