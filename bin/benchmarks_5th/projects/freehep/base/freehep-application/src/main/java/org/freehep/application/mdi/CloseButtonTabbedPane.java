/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.freehep.application.mdi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.images.ImageHandler;

/**
 * <
 * code>JTabbedPane</code> which also optionally uses close button on tab.
 *
 * @author Tran Duc Trung (freehep mods by tonyj)
 * @version $Id: CloseButtonTabbedPane.java 16332 2015-09-14 22:18:07Z onoprien $
 *
 */
class CloseButtonTabbedPane extends JTabbedPane implements HasPopupItems {

    public static final boolean CLOSE_BUTTON_ENABLED = true;
    /**
     * this string is appended to the tab name to make room for the small close
     * button. Ideally one should be able to set the insets for the tab handles.
     */
    static final String TAB_NAME_TRAILING_SPACE;

    static {
        if (CLOSE_BUTTON_ENABLED) {
            //if(Dependency.JAVA_SPEC.compareTo(
            //new SpecificationVersion("1.4")) < 0) { // NOI18N
            //    TAB_NAME_TRAILING_SPACE = "   "; // NOI18N
            //} else {
            TAB_NAME_TRAILING_SPACE = "   "; // NOI18N

            //}
        } else {
            TAB_NAME_TRAILING_SPACE = ""; // NOI18N
        }
    }
    private static Image closeTabImage = ImageHandler.getImage("tabclose", CloseButtonTabbedPane.class);
    private static Image closeTabInactiveImage = ImageHandler.getImage("tabcloseinactive", CloseButtonTabbedPane.class);
    private boolean draggedOut = false;
    private int mouseOverCloseButtonIndex = -1;
    private int pressedCloseButtonIndex = -1;
    private int dragIndex = -1;

    CloseButtonTabbedPane() {
        if (CLOSE_BUTTON_ENABLED) {
            CloseButtonListener cl = new CloseButtonListener();
            addMouseListener(cl);
            addMouseMotionListener(cl);
            addChangeListener(cl);
        }
    }

    // #24033. Needles insets in tabbed panes (making difference between single).
    /**
     * Overrides superclass method.
     *
     * @return [0, 0, 0, 0] insets
     */
    @Override
    public Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (!CLOSE_BUTTON_ENABLED) {
            return;
        }

        // Have a look at
        // http://ui.netbeans.org/docs/ui/closeButton/closeButtonUISpec.html
        // to see how the buttons are specified to be drawn.
        int selectedIndex = getSelectedIndex();

        for (int i = 0, n = getTabCount(); i < n; i++) {
            Rectangle r = getCloseButtonBoundsAt(i);
            if (r == null) {
                continue;
            }

            if ((i == pressedCloseButtonIndex) && !draggedOut) {
                g.setColor(new Color(153, 153, 153));
                g.fillRect(r.x, r.y, r.width, r.height);
            }

            if (i != selectedIndex) // && i != mouseOverCloseButtonIndex && i != pressedCloseButtonIndex)
            {
                g.drawImage(closeTabInactiveImage, r.x + 2, r.y + 2, this);
            } else {
                g.drawImage(closeTabImage, r.x + 2, r.y + 2, this);
            }

            if ((i == mouseOverCloseButtonIndex) || (i == pressedCloseButtonIndex && draggedOut)) {
                g.setColor(new Color(102, 102, 102));
                g.drawRect(r.x, r.y, r.width, r.height);
                g.setColor((i == selectedIndex) ? new Color(255, 255, 255) : new Color(204, 204, 204));
                g.drawRect(r.x + 1, r.y + 1, r.width, r.height);

                // Draw the dots.
                g.setColor(new Color(162, 162, 162));
                g.drawLine(r.x + r.width, r.y + 1, r.x + r.width, r.y + 1);
                g.drawLine(r.x + 1, r.y + r.height, r.x + 1, r.y + r.height);
            } else if (i == pressedCloseButtonIndex) {
                g.setColor(new Color(102, 102, 102));
                g.drawRect(r.x, r.y, r.width, r.height);
                g.setColor((i == selectedIndex) ? new Color(255, 255, 255) : new Color(204, 204, 204));
                g.drawLine(r.x + 1, r.y + r.height + 1, r.x + r.width + 1, r.y + r.height + 1);
                g.drawLine(r.x + r.width + 1, r.y + 1, r.x + r.width + 1, r.y + r.height + 1);

                // Draw the lines.
                g.setColor(new Color(153, 153, 153));
                g.drawLine(r.x + 1, r.y + 1, r.x + r.width, r.y + 1);
                g.drawLine(r.x + 1, r.y + 1, r.x + 1, r.y + r.height);
            }
        }
    }

    protected void fireCloseTabAt(int index) {
        removeTabAt(index);
    }
    
    protected void fireUndockTabAt(int index, int x, int y) {
        
    }

    private Rectangle getCloseButtonBoundsAt(int i) {
        Rectangle b = getBoundsAt(i);
        if (b == null) {
            return null;
        } else {
            b = new Rectangle(b);

            //JdkBug4620540Hack.fixGetBoundsAt(b);
            Dimension tabsz = getSize();
            if (((b.x + b.width) >= tabsz.width) || ((b.y + b.height) >= tabsz.height)) {
                return null;
            }

            return new Rectangle((b.x + b.width) - 13, (b.y + (b.height / 2)) - 5, 8, 8);
        }
    }

    private void setMouseOverCloseButtonIndex(int index) {
        if (mouseOverCloseButtonIndex == index) {
            return;
        }

        if (mouseOverCloseButtonIndex >= 0) {
            Rectangle r = getCloseButtonBoundsAt(mouseOverCloseButtonIndex);
            repaint(r.x, r.y, r.width + 2, r.height + 2);

            //TopComponent tc =
            //    (TopComponent) getComponentAt(mouseOverCloseButtonIndex);
            //setToolTipTextAt(mouseOverCloseButtonIndex, tc.getToolTipText());
        }

        mouseOverCloseButtonIndex = index;

        if (mouseOverCloseButtonIndex >= 0) {
            Rectangle r = getCloseButtonBoundsAt(mouseOverCloseButtonIndex);
            repaint(r.x, r.y, r.width + 2, r.height + 2);
            setPressedCloseButtonIndex(-1);
            setToolTipTextAt(mouseOverCloseButtonIndex, null);
        }
    }

    private void setPressedCloseButtonIndex(int index) {
        if (pressedCloseButtonIndex == index) {
            return;
        }

        if (pressedCloseButtonIndex >= 0) {
            Rectangle r = getCloseButtonBoundsAt(pressedCloseButtonIndex);
            repaint(r.x, r.y, r.width + 2, r.height + 2);

            //TopComponent tc =
            //    (TopComponent) getComponentAt(pressedCloseButtonIndex);
            //setToolTipTextAt(pressedCloseButtonIndex, tc.getToolTipText());
        }

        pressedCloseButtonIndex = index;

        if (pressedCloseButtonIndex >= 0) {
            Rectangle r = getCloseButtonBoundsAt(pressedCloseButtonIndex);
            repaint(r.x, r.y, r.width + 2, r.height + 2);
            setMouseOverCloseButtonIndex(-1);
            setToolTipTextAt(pressedCloseButtonIndex, null);
        }
    }

    private void reset() {
        setMouseOverCloseButtonIndex(-1);
        setPressedCloseButtonIndex(-1);
        draggedOut = false;
        dragIndex = -1;
    }

    @Override
    public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
        return menu;
    }

    private class CloseButtonListener extends MouseAdapter implements MouseMotionListener, ChangeListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (pressedCloseButtonIndex >= 0) {
                Rectangle r = getCloseButtonBoundsAt(pressedCloseButtonIndex);
                if (r != null
                        && draggedOut != !r.contains(e.getPoint())) {
                    draggedOut = !r.contains(e.getPoint());
                    repaint(r.x, r.y, r.width + 2, r.height + 2);
                }
                e.consume();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int index = indexAtLocation(e.getX(), e.getY());
            if (index >= 0) {
                Rectangle r = getCloseButtonBoundsAt(index);
                if (r != null && r.contains(e.getPoint())) {
                    setMouseOverCloseButtonIndex(index);
                    draggedOut = false;
                    e.consume();
                } else if (mouseOverCloseButtonIndex >= 0) {
                    setMouseOverCloseButtonIndex(-1);
                    draggedOut = false;
                    e.consume();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            dragIndex = indexAtLocation(e.getX(), e.getY());
            if (dragIndex >= 0) {
                Rectangle r = getCloseButtonBoundsAt(dragIndex);
                if (r != null && r.contains(e.getPoint())) {
                    setPressedCloseButtonIndex(dragIndex);
                    draggedOut = false;
                    e.consume();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int tabIndex = pressedCloseButtonIndex;
            if (tabIndex >= 0) {
                Rectangle r = getCloseButtonBoundsAt(tabIndex);
                if (r != null && r.contains(e.getPoint()) && (tabIndex >= 0)) {
                    reset();
                    fireCloseTabAt(tabIndex);
                    e.consume();
                    return;
                }
            } else if (dragIndex >= 0) {
                int index = indexAtLocation(e.getX(), e.getY());
                if (index < 0) {
                    fireUndockTabAt(dragIndex, e.getXOnScreen(), e.getYOnScreen());
                    e.consume();
                }
            }
            reset();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            reset();
        }
    }
}