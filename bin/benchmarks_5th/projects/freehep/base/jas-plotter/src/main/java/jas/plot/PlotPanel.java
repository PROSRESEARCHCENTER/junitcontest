package jas.plot;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;
public class PlotPanel extends PlotComponent implements ContainerListener, MouseListener, MouseMotionListener
{
    public PlotPanel()
    {
        super(new ChartLayout());
        enableEvents(AWTEvent.CONTAINER_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }
    public Component add(Component c)
    {
        // Make sure the data area is always last (so other things get painted on top)
        if (c instanceof DataArea) return super.add(c);
        return super.add(c,0);
    }
    final protected void processContainerEvent(final ContainerEvent e)
    {
        if (e.getID() == ContainerEvent.COMPONENT_ADDED)
        {
            componentAdded(e);
        }
        else
        {
            componentRemoved(e);
        }
    }
    public void restoreDefaultLayout()
    {
        ChartLayout cl = (ChartLayout) this.getLayout();
        cl.restoreDefaultLayout();
        revalidate();
    }
    public boolean hasDefaultLayout()
    {
        ChartLayout cl = (ChartLayout) this.getLayout();
        return cl.hasDefaultLayout();
    }
    final protected void processMouseEvent(final MouseEvent e)
    {
        if (!allowUserInteraction) return;
        // this is called when a mouse event occurs on the panel, but not
        // in one of the children components
        
        // if this happens and the mouse is pressed, we want the blue border to go away
        if (e.getID() == MouseEvent.MOUSE_PRESSED && componentWithHandles != null)
        {
            componentWithHandles.hideHandles();
            componentWithHandles = null;
        }
        // The plot panel itself may contain a popup menu, deal with it here
        if (allowPopupMenus) menuBuilder.buildMenu(e);
    }
    private void propagateMouseMotionEvent(final MouseEvent e)
    {
        if (!allowUserInteraction) return;
        
        // If a component is active (ie has handles) and the event is contained within it, then we
        // give the event to it. (What if it has a subobject?)
        // Otherwise give it to the smallest thing that contains it?
        
        Component source = (Component) e.getSource();
        if (componentWithHandles != null)
        {
            componentWithHandles.mouseMotionEventNotify(SwingUtilities.convertMouseEvent(source,e,componentWithHandles));
        }
        else
        {
            for (Component c=source; c != null; c=c.getParent())
            {
                if (c instanceof MovableObject)
                {
                    ((MovableObject) c).mouseMotionEventNotify(SwingUtilities.convertMouseEvent(source,e,c));
                }
            }
        }
    }
    private void propagateMouseEvent(final MouseEvent e)
    {
        if (!allowUserInteraction) return;
        
        // If a component is active (ie has handles) and the event is contained within it, then we
        // give the event to it. (What if it has a subobject?)
        // Otherwise give it to the smallest thing that contains it?
        
        Component source = (Component) e.getSource();
        if (componentWithHandles != null)
        {
            componentWithHandles.mouseEventNotify(SwingUtilities.convertMouseEvent(source,e,componentWithHandles));
        }
        //else
        //{
        for (Component c=source; c != null; c=c.getParent())
        {
            if (c instanceof JASPlotMouseListener)
            {
                ((JASPlotMouseListener) c).mouseEventNotify(SwingUtilities.convertMouseEvent(source,e,c));
            }
        }
        //}
        if (allowPopupMenus) menuBuilder.buildMenu(e);
    }
    private void addComponent(final Component comp)
    {
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
        if (comp instanceof Container)
        {
            final Container cont = (Container) comp;
            cont.addContainerListener(this);
            Component[] children = cont.getComponents();
            for (int i = 0; i < children.length; i++)
                addComponent(children[i]);
        }
    }
    private void removeComponent(final Component comp)
    {
        comp.removeMouseListener(this);
        comp.removeMouseMotionListener(this);
        if (comp instanceof Container)
        {
            final Container cont = (Container) comp;
            cont.removeContainerListener(this);
            Component[] children = cont.getComponents();
            for (int i = 0; i < children.length; i++)
                removeComponent(children[i]);
        }
    }
    public final void deselected()
    {
        if (componentWithHandles != null)
        {
            componentWithHandles.hideHandles();
            componentWithHandles = null;
        }
    }
    //////////////////////////////////////////////////////////////
    // ContainerListener methods
    //
    final public void componentAdded(final ContainerEvent e)
    {
        addComponent(e.getChild());
    }
    final public void componentRemoved(final ContainerEvent e)
    {
        removeComponent(e.getChild());
    }
    //////////////////////////////////////////////////////////////
    // MouseListener methods
    //
    final public void mouseClicked(final MouseEvent e)
    {
        propagateMouseEvent(e);
    }
    final public void mouseEntered(final MouseEvent e)
    {
        propagateMouseEvent(e);
    }
    final public void mouseExited(final MouseEvent e)
    {
        propagateMouseEvent(e);
    }
    final public void mousePressed(final MouseEvent e)
    {
        if (!allowUserInteraction) return;
        
        final Object source = e.getSource();
        MovableObject mo = null;
        
        // If the event is INSIDE the componentWithHandles, then do not change focus.
        
        if (componentWithHandles != null && source instanceof Component)
        {
            Point p = SwingUtilities.convertPoint((Component) source,e.getX(),e.getY(),componentWithHandles);
            if (componentWithHandles.contains(p))
            {
                propagateMouseEvent(e);
                return;
            }
        }
        if (source instanceof MovableObject)
            mo = (MovableObject) source;
        else
            mo = (MovableObject) SwingUtilities.getAncestorOfClass(MovableObject.class, (Component) source);
        // if the source or any of its parents is a MovableObject then mo is that object
        // if not, mo is null
        if (mo != null && mo != componentWithHandles)
        {
            if (componentWithHandles != null) componentWithHandles.hideHandles();
            componentWithHandles = mo;
            componentWithHandles.showHandles();
            // we request focus so we will be told when we lose focus, and can remove the handles
            // BUG: What if someone INSIDE the componentWithHandles gets focus?
            requestFocus();
        }
        propagateMouseEvent(e);
    }
    final public void processFocusEvent(FocusEvent e)
    {
        if (e.getID() == e.FOCUS_LOST) deselected();
        super.processFocusEvent(e);
    }
    final public void mouseReleased(final MouseEvent e)
    {
        propagateMouseEvent(e);
    }
    //////////////////////////////////////////////////////////////
    // MouseMotionListener methods
    //
    final public void mouseDragged(final MouseEvent e)
    {
        propagateMouseMotionEvent(e);
    }
    final public void mouseMoved(final MouseEvent e)
    {
        propagateMouseMotionEvent(e);
    }
    
    public void setAllowUserInteraction(boolean allow)
    {
        allowUserInteraction = allow;
    }
    public boolean getAllowUserInteraction()
    {
        return allowUserInteraction;
    }
    public void setAllowPopupMenus(boolean allow)
    {
        allowPopupMenus = allow;
    }
    public boolean getAllowPopupMenus()
    {
        return allowPopupMenus;
    }    
    ////////////////////////////////////////////////////////////
    // members
    //
    private PopupMenuBuilder menuBuilder = new PopupMenuBuilder();
    private MovableObject componentWithHandles;
    private boolean allowUserInteraction = true;
    private boolean allowPopupMenus = true;
}
