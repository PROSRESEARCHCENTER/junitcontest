package org.freehep.swing.popup;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author tonyj
 */
public class GlobalPopupListener extends PopupListener {
    
    public GlobalPopupListener()
    {
        super(null);
    }
    protected void maybeShowPopup(MouseEvent me) {
        JPopupMenu popup = new JPopupMenu();
        if (popup.isPopupTrigger(me)) {
            Component source = me.getComponent();
            Window w = (Window) (source instanceof Window ? source :  SwingUtilities.getAncestorOfClass(Window.class,source));
            if (w != null) // FreeHEP-455
            {
               Point p = SwingUtilities.convertPoint(source,me.getPoint(),w);
               processPopupEvent(popup,w,p.x,p.y);
            }
        }
    }   
      
    private void processPopupEvent(JPopupMenu menu, Component source, int x, int y) {
        Component target = SwingUtilities.getDeepestComponentAt(source,x,y);
        for (Component c = target; c != null; c = c.getParent()) {
            if (c instanceof HasPopupItems) {
                Point p = SwingUtilities.convertPoint(source,x,y,c);
                menu = ((HasPopupItems) c).modifyPopupMenu(menu,target,p);
            }
        }
        if (menu != null && menu.getComponentCount() > 0) {
            menu.show(source,x,y);
        }
    }
    
}
