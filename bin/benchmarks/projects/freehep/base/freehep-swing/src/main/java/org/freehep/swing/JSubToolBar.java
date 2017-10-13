package org.freehep.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.freehep.swing.layout.StackedLayout;

/**
 * This defines a swing-like element which provides a popup toolbar.  This is
 * intended to provide a submenu-type functionality to toolbars.  The
 * component has a single selected element.  If the visible button is clicked,
 * the action associated with this button is run.  However, if the button is
 * pressed and held, a popup toolbar with all options is shown.  The user then
 * may select the desired option which will then become the selected button.
 * The action associated with this is also run. 
 *
 * @author Charles Loomis
 */
public class JSubToolBar 
    extends JLayeredPane {

    JPopupMenu popup;
    JToolBar subToolBar;
    JComponent selectedComponent = null;

    MousePanel interceptor;

    LinkedList buttonList;

    public JSubToolBar() {

        // Make the button list.
        buttonList = new LinkedList();

        // Set the layout manager for this component.
        setLayout(new StackedLayout());

        // Add the panel which will intercept mouse events.
        interceptor = new MousePanel();
        add(interceptor, DRAG_LAYER);

        // Make the toolbar which will be used to hold actions.
        subToolBar = new JToolBar();
        subToolBar.setFloatable(false);
        subToolBar.setBorderPainted(false);
        subToolBar.setMargin(new Insets(1,1,1,1));

        // Make the popup which will contain the sub-toolbar.
        popup = new JPopupMenu();
        popup.add(subToolBar);
        popup.setBorderPainted(true);

        // Set a small default size.  This will be updated with each
        // additional action which is added.
        Dimension d = new Dimension(10,10);
        setPreferredSize(d);
    }

    /**
     * This is used by subclasses to make a button from an action. */
    protected JButton makeButtonFromAction(Action action) {

        // FIXME: there may be a better solution to this!
        JButton button = new JButton() {
                public Dimension getPreferredSize() {
                    if (getIcon()!=null) {
                        return new Dimension(24,24);
                    } else {
                        Dimension d = super.getPreferredSize();
                        d.height = 24;
                        return d;
                    }
                }
                
                public String getText() {
                    return (getIcon()==null) ? super.getText() : null;
                }
            };
        button.setAction(action);
        return button;
    }

    /**
     * Add an action to this component.  Currently, only actions are supported
     * and thus this should be the only add method used.   A button is made
     * from the action which is then added to the sub-toolbar.  */
    public void add(Action action) {

        JButton button = makeButtonFromAction(action);
        button.addActionListener(interceptor);
        subToolBar.add(button);

        // Keep track of all buttons which are added.
        buttonList.add(button);

        // Increase the default size if necessary.
        updatePreferredSize(button.getPreferredSize());

        if (selectedComponent==null) setSelectedComponent(button);
    }

    /**
     * Set the delay (in ms) between a mouse press and the popup toolbar being
     * activated.  This must be sufficiently long that clicks on the component
     * are recognized as such. */
    public void setDelay(int msDelay) {
        interceptor.setDelay(msDelay);
    }
    
    /**
     * Get the delay (in ms) between a mouse press and the popup toolbar being
     * activated. */
    public int getDelay() {
        return interceptor.getDelay();
    }
        
    /**
     * Set which component is selected.  This component must already have been
     * added to the toolbar. */
    public void setSelectedComponent(JComponent component) {
        if (buttonList.contains(component)) {

            // Put the selected component into the visible area.
            if (selectedComponent!=null) remove(selectedComponent);
            selectedComponent = component;
            add(component,DEFAULT_LAYER);

            // Try to give this component the keyboard focus.
            component.requestFocus();

            // Do the validation after the component has received the focus,
            // so that the component is correctly drawn.  (There is a bug
            // here; the focus works correctly, but not always the
            // appearance.)
            validate();

            // Re-add all of the buttons to the toolbar.  This is necessary
            // because a component can be in only one visible container.
            // Therefore, the button will be lost if we don't put it back into
            // the toolbar.
            subToolBar.removeAll();
            Iterator i = buttonList.iterator();
            while (i.hasNext()) {
                Component c = (Component) i.next();
                if (c!=selectedComponent) subToolBar.add(c);
            }
        }
    }

    /**
     * Update the preferred, minimum, and maximum sizes of this component.
     * This is the smallest rectangle which will contain all of the child
     * components. */
    protected void updatePreferredSize(Dimension dim) {
        Dimension currentDim = getPreferredSize();
        if (dim.width>currentDim.width) currentDim.width = dim.width;
        if (dim.height>currentDim.height) currentDim.height = dim.height;
        setPreferredSize(currentDim);
        setMaximumSize(currentDim);
        setMinimumSize(currentDim);
    }

    /**
     * Update the orientation of this sub-toolbar.  The toolbar will be in the
     * perpendicular direction to the parent toolbar. */
    private void updateOrientation() {

        // Look to see if we are inside of another toolbar.
        JToolBar parent = (JToolBar) 
            SwingUtilities.getAncestorOfClass(JToolBar.class,this);
        int orientation = JToolBar.HORIZONTAL;

        // If so, change the orientation to be perpendicular to the
        // parent's orientation.
        if (parent!=null) {
            int parentOrientation = parent.getOrientation();
            if (parentOrientation==JToolBar.HORIZONTAL) {
                orientation = JToolBar.VERTICAL;
            }
        }

        // Now actually update the orientation.
        subToolBar.setOrientation(orientation);
    }

    /**
     * This panel is used to intercept mouse events from the visible button in
     * the sub-toolbar.  */
    private class MousePanel
        extends JPanel
        implements MouseListener,
                   ActionListener {

        private Timer timer;
        private int delay = 300;

        public MousePanel() {
            super();
            setOpaque(false);
            timer = new Timer(delay,this);
            timer.setRepeats(false);
            addMouseListener(this);
        }

        /**
         * Set the delay (in ms) between a mouse press and the popup toolbar
         * being activated.  This must be sufficiently long that clicks on the
         * component are recognized as such. */
        public void setDelay(int msDelay) {
            delay = msDelay;
        }
        
        /**
         * Get the delay (in ms) between a mouse press and the popup toolbar
         * being activated. */
        public int getDelay() {
            return delay;
        }
        
        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {
            timer.stop();
        }

        public void mousePressed(MouseEvent e) {
            timer.start();
        }

        public void mouseReleased(MouseEvent e) {
            timer.stop();
        }

        public void mouseClicked(MouseEvent e) {
            timer.stop();
            if (!popup.isVisible() &&
                selectedComponent!=null && 
                selectedComponent instanceof AbstractButton) {
                ((AbstractButton)selectedComponent).doClick();
            }
        }
        public void actionPerformed(ActionEvent e) {

            Object source = e.getSource();
            if (source instanceof Timer) {

                // This is a timer event, so the user must have pressed and
                // held the visible button.  Bring up the popup toolbar.
                updateOrientation();

                int orientation = subToolBar.getOrientation();
                int x0 = 0;
                int y0 = 0;
                if (orientation==JToolBar.HORIZONTAL) {
                    x0 = getWidth();
                } else {
                    y0 = getHeight();
                }
                popup.show(this,x0,y0);

                // Check to see that the popup menu is visible on the
                // screen. If it isn't, then move the popup to a more
                // convenient position.
                int w = popup.getWidth();
                int h = popup.getHeight();
                Point point = new Point(x0+w,y0+h);
                SwingUtilities.convertPointToScreen(point,this);

                // Get information about the screen.
                GraphicsEnvironment ge = 
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice gd = ge.getDefaultScreenDevice();
                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                Rectangle screenBounds = gc.getBounds();

                // If the popup isn't on the screen, then change the
                // direction.  Unfortunately, there is no way to get the
                // correct width and height of the popup until it is
                // displayed.  So, if it is incorrect, redisplay the popup.
                // The redisplay will also change the popup to a heavy-weight
                // component if necessary.
                if (!screenBounds.contains(point)) {
                    if (subToolBar.getOrientation()==JToolBar.HORIZONTAL) {

                        // Switch to the left direction. 
                        int x = -w;
                        int y = 0;
                        popup.setVisible(false);
                        popup.show(this,x,y);
                    } else {

                        // Switch to the upward direction. 
                        int x = 0;
                        int y = -h;
                        popup.setVisible(false);
                        popup.show(this,x,y);
                    }
                }

            } else if (source instanceof JComponent) {

                // We have gotten an event from one of the buttons.  Just
                // close the popup and set the selected component to the
                // chosen one.
                popup.setVisible(false);
                setSelectedComponent((JComponent) source);
            }
        }

    }
        
}
