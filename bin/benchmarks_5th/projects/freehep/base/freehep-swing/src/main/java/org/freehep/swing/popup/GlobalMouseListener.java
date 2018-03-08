package org.freehep.swing.popup;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.EventListenerList;

/**
 * This class is designed to work around a deficiency in Swing's mouse handling.
 * It enables a listener to be applied to a component so that all mouse events
 * in that component, or any of its subcomponents will be reported. This is
 * useful for example in creating Popup menus for complex graphical objects
 * (such as plots).
 * <p>
 * There is no easy way to implement this functionality in Swing, so this class
 * works by recursively adding mouse listeners to all components in the heirarchy.
 * It also adds a ComponentListener so that it can add and remove listeners from
 * items as they are added to the tree.
 * <p>
 * Warning: This class can have unexpected side effects in some rare cases.
 * Normally swing will delegate mouse events to a component's parent if that component
 * does not listen for mouse events itself. Since this class adds mouse listeners to
 * all components, it can have the side effect of preventing this mouse event delegation.
 * (In Java 1.4 it is possible to tell if a mouse listener already exists on a component,
 * so maybe it would be possible to work around this limitation. Not clear how you would
 * know if a mouse listener was subsequently added).
 * @author tonyj
 * @version $Id: GlobalMouseListener.java 8584 2006-08-10 23:06:37Z duns $
 */
public class GlobalMouseListener {
   /** Create a new GlobalMouseListener associated to a component
    * @param c The component on which to listen for mouse events
    */   
    public GlobalMouseListener(Component c) {
        final MouseListener ml = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                redispatch(e);
            }
            public void mouseEntered(MouseEvent e) {
                redispatch(e);
            }
            public void mouseExited(MouseEvent e) {
                redispatch(e);
            }
            public void mousePressed(MouseEvent e) {
                redispatch(e);
            }
            public void mouseReleased(MouseEvent e) {
                redispatch(e);
            }
        };
        ContainerListener cl = new ContainerListener() {
            public void componentAdded(ContainerEvent e) {
                Component source = e.getChild();
                changeGlobalMouseListener(true,source,ml,this);
            }
            public void componentRemoved(ContainerEvent e) {
                Component source = e.getChild();
                changeGlobalMouseListener(false,source,ml,this);
            }
        };
        changeGlobalMouseListener(true,c,ml,cl);
    }
    private void changeGlobalMouseListener(boolean add, Component c, MouseListener ml, ContainerListener cl) {
        if (add) c.addMouseListener(ml);
        else     c.removeMouseListener(ml);
        
        if (c instanceof Container) {
            Container cc = (Container) c;
            
            if (add) cc.addContainerListener(cl);
            else     cc.removeContainerListener(cl);
            
            int l = cc.getComponentCount();
            for (int i=0; i<l; i++) {
                Component child = cc.getComponent(i);
                changeGlobalMouseListener(add,child,ml,cl);
            }
        }
    }
    /** Add a mouse listener.
     * @param l The listener to add
     */    
    public void addMouseListener(MouseListener l) {
        listeners.add(MouseListener.class,l);
    }
    /** Remove a mouse listener
     * @param l The listener to remove
     */    
    public void removeMouseListener(MouseListener l) {
        listeners.remove(MouseListener.class,l);
    }
    private void redispatch(MouseEvent e)
    {
        int count = listeners.getListenerCount(MouseListener.class);
        if (count > 0)
        {
            MouseListener[] list = (MouseListener[]) listeners.getListeners(MouseListener.class);
            switch (e.getID())
            {
                case MouseEvent.MOUSE_CLICKED:  for (int i=0; i<count; i++) list[i].mouseClicked(e);  break;
                case MouseEvent.MOUSE_ENTERED:  for (int i=0; i<count; i++) list[i].mouseEntered(e);  break;
                case MouseEvent.MOUSE_EXITED:   for (int i=0; i<count; i++) list[i].mouseExited(e);   break;
                case MouseEvent.MOUSE_PRESSED:  for (int i=0; i<count; i++) list[i].mousePressed(e);  break;
                case MouseEvent.MOUSE_RELEASED: for (int i=0; i<count; i++) list[i].mouseReleased(e); break;
            }
        }
    }
    private EventListenerList listeners = new EventListenerList();
}