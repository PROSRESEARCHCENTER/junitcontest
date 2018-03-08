// Copyright 2003, SLAC, Stanford, U.S.A.
package org.freehep.swing.event;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

/**
 * This class allows MouseEvents from the three buttons to be routed to
 * three MouseInputListeners. The MouseEvents generated for these listeners
 * always have Button1 in their event.
 *
 * @author Mark Donszelmann
 * @version $Id: MouseInputRouter.java 8584 2006-08-10 23:06:37Z duns $
 * @status NOT TESTED
 */
public class MouseInputRouter implements MouseInputListener {

    MouseInputListener left, middle, right;

    public MouseInputRouter(Component c) {
        c.addMouseMotionListener(this);
        c.addMouseListener(this);
    }

    public void setLeftMouseInputListener(MouseInputListener l) {
        left = l;
    }

    public void setMiddleMouseInputListener(MouseInputListener l) {
        middle = l;
    }

    public void setRightInputListener(MouseInputListener l) {
        right = l;
    }

    private MouseEvent convertMouseEvent(MouseEvent event) {
        int modifiers = event.getModifiers() | event.getModifiersEx();
        modifiers |= MouseEvent.BUTTON1_MASK;
        modifiers &= ~MouseEvent.BUTTON2_MASK;
        modifiers &= ~MouseEvent.BUTTON3_MASK;
        modifiers |= MouseEvent.BUTTON1_DOWN_MASK;
        modifiers &= ~MouseEvent.BUTTON2_DOWN_MASK;
        modifiers &= ~MouseEvent.BUTTON3_DOWN_MASK;
        return new MouseEvent(event.getComponent(),
                              event.getID(),
                              event.getWhen(),
                              modifiers,
                              event.getX(),
                              event.getY(),
                              event.getClickCount(),
                              event.isPopupTrigger(),
                              event.getButton());
    }

    public void mousePressed(MouseEvent event) {
        if ((left != null) && (SwingUtilities.isLeftMouseButton(event))) {
            left.mousePressed(event);
        }
        if ((middle != null) && (SwingUtilities.isMiddleMouseButton(event))) {
            middle.mousePressed(convertMouseEvent(event));
        }
        if ((right != null) && (SwingUtilities.isRightMouseButton(event))) {
            right.mousePressed(convertMouseEvent(event));
        }
    }

    public void mouseReleased(MouseEvent event) {
        if ((left != null) && (SwingUtilities.isLeftMouseButton(event))) {
            left.mouseReleased(event);
        }
        if ((middle != null) && (SwingUtilities.isMiddleMouseButton(event))) {
            middle.mouseReleased(convertMouseEvent(event));
        }
        if ((right != null) && (SwingUtilities.isRightMouseButton(event))) {
            right.mouseReleased(convertMouseEvent(event));
        }
    }

    public void mouseClicked(MouseEvent event) {
        if ((left != null) && (SwingUtilities.isLeftMouseButton(event))) {
            left.mouseClicked(event);
        }
        if ((middle != null) && (SwingUtilities.isMiddleMouseButton(event))) {
            middle.mouseClicked(convertMouseEvent(event));
        }
        if ((right != null) && (SwingUtilities.isRightMouseButton(event))) {
            right.mouseClicked(convertMouseEvent(event));
        }
    }

    public void mouseDragged(MouseEvent event) {
         if ((left != null) && (SwingUtilities.isLeftMouseButton(event))) {
            left.mouseDragged(event);
        }
        if ((middle != null) && (SwingUtilities.isMiddleMouseButton(event))) {
            middle.mouseDragged(convertMouseEvent(event));
        }
        if ((right != null) && (SwingUtilities.isRightMouseButton(event))) {
            right.mouseDragged(convertMouseEvent(event));
        }
   }

    public void mouseMoved(MouseEvent event) {
        if (left   != null) left.mouseMoved(event);
        if (middle != null) middle.mouseMoved(event);
        if (right  != null) right.mouseMoved(event);
    }

    public void mouseEntered(MouseEvent event) {
        if (left   != null) left.mouseEntered(event);
        if (middle != null) middle.mouseEntered(event);
        if (right  != null) right.mouseEntered(event);
    }

    public void mouseExited(MouseEvent event) {
        if (left   != null) left.mouseExited(event);
        if (middle != null) middle.mouseExited(event);
        if (right  != null) right.mouseExited(event);
    }
}
