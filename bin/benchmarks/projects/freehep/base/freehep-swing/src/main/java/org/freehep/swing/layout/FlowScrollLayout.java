package org.freehep.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/** This class is a replacement for a FlowLayout inside a JScrollPane.
 * It can be used as a plain FlowLayout, but it is intended to be
 * used on a container inside a JScrollPane.  If it is the layout for
 * a Container that is the view of a JViewport inside a JScrollPane,
 * then it will cause the Container's children to be wrapped so that
 * the JScrollPane only scrolls vertically.
 * <p>
 *
 * It can optionally resize all children on each row to be as tall as
 * the tallest one.  IMHO, this often looks better, but is off by
 * default for compatiblity with FlowLayout.
 *
 * <p>
 * <em>Note:</em>Each FlowScrollLayout should be used for only one Container.
 * <p>
 * <em>Bug:</em>The JViewport inside the JScrollPane that you give to
 * FlowScrollLayout must have a child during the whole time that
 * FlowScrollLayout is used with that JScrollPane.  (Typically, that
 * child is the widget whose layout is the FlowScrollLayout.)
 * Otherwise FlowScrollLayout will throw a NullPointerException.  It
 * should handle this condition gracefully, but I do not have time to
 * fix it right now.  If anyone wants to send me a patch, it would be
 * welcome.

 * <P>
 * This software is distributed under the
 * <A HREF="http://guir.berkeley.edu/projects/COPYRIGHT.txt">
 * Berkeley Software License</A>.
 *
 * @author This was written by A. Chris Long <chrislong@acm.org> (but modified for FreeHEP)
 * @version $Id: FlowScrollLayout.java 8584 2006-08-10 23:06:37Z duns $
 */
public class FlowScrollLayout extends FlowLayout
implements ComponentListener, LayoutManager2 {
    private JScrollPane scroller = null;
    private boolean uniformHeight;
    private boolean firstTime = true;
    
    public FlowScrollLayout()
    {
        this(null);
    }
    
    public FlowScrollLayout(JScrollPane scrollPane)
    {
        this(scrollPane, false);
    }
    
    public FlowScrollLayout(JScrollPane scrollPane, boolean uniformHeight)
    {
        super();
        setScrollPane(scrollPane);
        this.uniformHeight = uniformHeight;
    }
    
    public void setScrollPane(JScrollPane scrollPane)
    {
        if (scrollPane != scroller) {
            if (scroller != null) {
                scroller.removeComponentListener(this);
            }
            scroller = scrollPane;
            if (scroller != null) {
                scroller.addComponentListener(this);
            }
        }
    }
    
  /** If uniformHeight is turned on, all widgets on each row will have
   * their preferred height set to the height of the tallest one
   * (based on preferredSize). */
    public void setUniformHeight(boolean on)
    {
        if (uniformHeight != on) {
            uniformHeight = on;
            if (scroller != null) {
                scroller.doLayout();
            }
        }
    }
    
    public boolean isUniformHeight()
    {
        return uniformHeight;
    }
    
  /** Follow the layout algorithm that FlowLayout uses to compute how
   * big we would like to be, given the size of the containing
   * JScrollPane.  Should not be called unless a non-null JScrollPane
   * has been specified in the constructor or with setScrollPane. */
    protected Dimension computeDesiredSize()
    {
        JViewport viewport = scroller.getViewport();
        Dimension extent = viewport.getExtentSize();
        Component child = viewport.getView();
        
        if (child instanceof Container) {
            Container container = (Container) child;
            Insets insets = container.getInsets();
            int vgap = getVgap();
            int hgap = getHgap();
            int maxAllowedWidth = extent.width -
            (insets.left + insets.right + hgap*2);
            int numComponents = container.getComponentCount();
            int x = 0;
            int y = insets.top + vgap;
            int rowh = 0;
            int maxRowWidth = 0;
            int start = 0;
            for (int i = 0; i < numComponents; i++) {
                Component comp = container.getComponent(i);
                if (comp.isVisible()) {
                    Dimension dim = comp.getPreferredSize();
                    if ((x == 0) || ((x + dim.width) <= maxAllowedWidth)) {
                        if (x > 0) {
                            x += hgap;
                        }
                        x += dim.width;
                        rowh = Math.max(rowh, dim.height);
                    }
                    else {
                        if (uniformHeight) {
                            setHeights(container, rowh, start, i);
                        }
                        if (x > (maxRowWidth - hgap)) {
                            maxRowWidth = x + hgap;
                        }
                        x = dim.width;
                        y += vgap + rowh;
                        rowh = dim.height;
                        start = i;
                    }
                }
            }
            if (uniformHeight) {
                setHeights(container, rowh, start, numComponents);
            }
            if (x > (maxRowWidth - hgap)) {
                maxRowWidth = x + hgap;
            }
            y += vgap + rowh + insets.bottom;
            return new Dimension(maxRowWidth, y);
        }
        else if (child != null) {
            Dimension prefSize = child.getPreferredSize();
            return new Dimension(extent.width, prefSize.height);
        }
        else return extent;
    }
    
  /** Set the preferred size of all JComponents inside container to
   * have height height, starting at the startIndex'th child and
   * going up to endIndex-1. */
    public static void setHeights(Container container, int height,
    int startIndex, int endIndex)
    {
        for (int i = startIndex; i < endIndex; i++) {
            Component comp = container.getComponent(i);
            if (comp instanceof JComponent) {
                setPreferredHeight((JComponent) comp, height);
            }
        }
    }
    
  /** Set preferredSize of comp to be Dimension(current preferredSize.width, height) */
    public static void setPreferredHeight(JComponent comp, int height)
    {
        Dimension prefSize = comp.getPreferredSize();
        prefSize.height = height;
        comp.setPreferredSize(prefSize);
    }
    
  /** Update the layout of the managed widget and the containing
   * scrollbar (only if the current size doesn't match the desired
   * size) */
    protected void updateLayout()
    {
        if (scroller != null) {
            JViewport viewport 	= scroller.getViewport();
            Dimension viewSize 	= viewport.getViewSize();
            Dimension extentSize 	= viewport.getExtentSize();
            Dimension desiredSize 	= computeDesiredSize();
            if ((viewSize.width != extentSize.width) ||
            (viewSize.height != desiredSize.height)) {
                // all is not right, so update sizes
                Dimension newSize = new
                Dimension(Math.max(desiredSize.width, extentSize.width),
                Math.max(desiredSize.height, extentSize.height));
                Component child = viewport.getView();
                if (child instanceof JComponent) {
                    ((JComponent) child).setPreferredSize(newSize);
                }
                viewport.setViewSize(newSize);
                // You might think that when the preferred size of the child and
                // the view size of the viewport change that things would
                // automatically update.  But they don't.  So...
                if (!firstTime) {
                    child.doLayout();
                    scroller.doLayout();
                }
            }
        }
    }
    
    public void layoutContainer(Container c)
    {
        if (firstTime) {
            updateLayout();
            firstTime = false;
        }
        super.layoutContainer(c);
    }
    
    public void componentResized(ComponentEvent e)
    {
        updateLayout();
    }
    
    public void componentMoved(ComponentEvent e)
    {
    }
    
    public void componentShown(ComponentEvent e)
    {
    }
    
    public void componentHidden(ComponentEvent e)
    {
    }
    
    public void invalidateLayout(Container target) {
        firstTime = true;
    }
    
    
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }
    
    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }
    
    /**
     * Returns the maximum size of this component.
     * @see java.awt.Component#getMinimumSize()
     * @see java.awt.Component#getPreferredSize()
     * @see LayoutManager
     */
    public Dimension maximumLayoutSize(Container target) {
        return preferredLayoutSize(target);
    }
    
    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.
     * @param comp the component to be added
     * @param constraints  where/how the component is added to the layout.
     */
    public void addLayoutComponent(Component comp,Object constraints) {
    }
    
}

/*
Copyright (c) 2000 Regents of the University of California.
All rights reserved.
 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
 
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
 
3. All advertising materials mentioning features or use of this software
   must display the following acknowledgement:
 
      This product includes software developed by the Group for User
      Interface Research at the University of California at Berkeley.
 
4. The name of the University may not be used to endorse or promote products
   derived from this software without specific prior written permission.
 
THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
 */
