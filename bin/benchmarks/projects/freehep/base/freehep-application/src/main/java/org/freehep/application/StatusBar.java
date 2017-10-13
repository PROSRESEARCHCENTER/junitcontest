package org.freehep.application;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * An status bar typically displayed at the bottom of the application window.
 * The status bar has an area for text messages. Additional areas can be added
 * to either the left or right sides to contain application specific status
 * areas. And a progress bar which displays the progress of extended operations,
 * as well as a "stop" button that can be used to abort certain prolonged
 * actions.
 *
 * @author tonyj
 * @version $Id: StatusBar.java 14082 2012-12-12 16:16:53Z tonyj $
 */
public class StatusBar extends JPanel {

    /**
     * Create a new StatusBar
     */
    public StatusBar() {
        setLayout(new StatusLayout());
        // We need to leave room for the little sprinkles in the corner for MacOSX
        boolean macOSX = false;
        try {
            macOSX = System.getProperty("mrj.version") != null;
        } catch (SecurityException x) {
        }

        setBorder(new EmptyBorder(0, 0, 0, (macOSX ? 18 : 0)));
        super.add(m_label);
        m_label.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    }

    /**
     * Set the message to display in the status bar. This message is thread safe
     * and can be called from any thread.
     *
     * @param message The message to display, or null to clear the message
     */
    public void setMessage(final String message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    setMessage(message);
                }
            };
            SwingUtilities.invokeLater(run);
        } else {
            if (message == null) {
                m_label.setText(" ");
            } else {
                m_label.setText(message);
            }
        }
    }
    private JLabel m_label = new JLabel();

    private class StatusLayout implements LayoutManager2, Comparator {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void addLayoutComponent(Component comp, Object constraints) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public float getLayoutAlignmentX(Container target) {
            return 0.5f;
        }

        @Override
        public float getLayoutAlignmentY(Container target) {
            return 0.5f;
        }

        @Override
        public void invalidateLayout(Container target) {
        }

        @Override
        public void layoutContainer(Container parent) {
            Insets insets = parent.getInsets();
            Dimension size = parent.getSize();
            // We need to sort the elements based on their XAlignment
            // Components with XAlignment < .5 go to the left of the label
            // Components with XAlignment >= .5 go to teh right of the label
            // The label takes all remaining space
            Component[] children = parent.getComponents();
            Arrays.sort(children, this);
            int x1 = insets.left;
            for (int i = 0; i < children.length; i++) {
                Component c = children[i];
                if (c == m_label) {
                    continue;
                }
                float align = c.getAlignmentX();
                if (align >= .5) {
                    break;
                }
                Dimension s = c.getPreferredSize();
                s.height = size.height - insets.top - insets.bottom;
                c.setSize(s);
                c.setLocation(x1, insets.top);
                x1 += s.width;
            }
            int x2 = size.width - insets.right;
            for (int i = children.length; i-- > 0;) {
                Component c = children[i];
                if (c == m_label) {
                    continue;
                }
                float align = c.getAlignmentX();
                if (align < .5) {
                    break;
                }
                Dimension s = c.getPreferredSize();
                s.height = size.height - insets.top - insets.bottom;
                c.setSize(s);
                x2 -= s.width;
                c.setLocation(x2, insets.top);
            }
            Dimension s = new Dimension(x2 - x1, size.height - insets.top - insets.bottom);
            m_label.setSize(s);
            m_label.setLocation(x1, insets.top);
        }

        @Override
        public Dimension maximumLayoutSize(Container target) {
            Insets insets = target.getInsets();
            int width = insets.left + insets.right;
            int height = Integer.MAX_VALUE;
            int count = target.getComponentCount();
            for (int i = 0; i < count; i++) {
                Component c = target.getComponent(i);
                if (!c.isVisible()) {
                    continue;
                }
                Dimension size = c.getMaximumSize();
                width += size.width;
                height = Math.min(height, size.height);
            }
            height += insets.top + insets.bottom;
            return new Dimension(width, height);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            Insets insets = parent.getInsets();
            int width = insets.left + insets.right;
            int height = 0;
            int count = parent.getComponentCount();
            for (int i = 0; i < count; i++) {
                Component c = parent.getComponent(i);
                if (!c.isVisible()) {
                    continue;
                }
                Dimension size = c.getMinimumSize();
                width += size.width;
                height = Math.max(height, size.height);
            }
            height += insets.top + insets.bottom;
            return new Dimension(width, height);
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Insets insets = parent.getInsets();
            int width = insets.left + insets.right;
            int height = 0;
            int count = parent.getComponentCount();
            for (int i = 0; i < count; i++) {
                Component c = parent.getComponent(i);
                if (!c.isVisible()) {
                    continue;
                }
                Dimension size = c.getPreferredSize();
                width += size.width;
                height = Math.max(height, size.height);
            }
            height += insets.top + insets.bottom;
            return new Dimension(width, height);
        }

        @Override
        public int compare(Object o1, Object o2) {
            float delta = ((Component) o1).getAlignmentX() - ((Component) o2).getAlignmentX();
            if (delta == 0) {
                return 0;
            } else if (delta > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}