package org.freehep.application.mdi;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;

import org.freehep.swing.layout.ToolBarLayout;
import org.freehep.swing.popup.HasPopupItems;

// TODO: No externally accessible toolbar menu
// TODO: Undocking toobars doesnt work properly
public class ToolBarHolder extends JPanel implements HasPopupItems {

    private static final String nameKey = "nameKey";
    private static final String modeKey = "modekey";
    private HashMap toolbars = new HashMap();
    private HashMap autos = new HashMap();

    protected ToolBarHolder() {
        super(new ToolBarLayout(ToolBarLayout.LEFT, 5, 5));
    }

    void add(JToolBar bar, String name, int mode) {
        if (!toolbars.containsKey(name)) {
            if (mode == MDIApplication.TOOLBAR_DEFAULT) {
                mode = MDIApplication.TOOLBAR_AUTO;
            }
            if (mode == MDIApplication.TOOLBAR_AUTO) {
                addAuto(bar);
            } else {
                bar.setVisible(mode == MDIApplication.TOOLBAR_VISIBLE);
            }
            bar.putClientProperty(modeKey, new Integer(mode));
            bar.putClientProperty(nameKey, name);
            toolbars.put(name, bar);
            add(bar);
            revalidate();
            repaint();
        }
    }

    void save(Properties props) {
        for (Iterator i = toolbars.values().iterator(); i.hasNext();) {
            JToolBar bar = (JToolBar) i.next();
            props.setProperty("ToolBar." + bar.getClientProperty(nameKey).toString(), bar.getClientProperty(modeKey).toString());
        }
    }

    void remove(JToolBar bar) {
        Object name = bar.getClientProperty(nameKey);
        if (name != null) {
            toolbars.remove(name);
        }
        removeAuto(bar);
        super.remove(bar);
        revalidate();
        repaint();
    }

    @Override
    public JPopupMenu modifyPopupMenu(JPopupMenu popup, Component src, java.awt.Point p) {
        for (int i = 0; i < getComponentCount(); i++) {
            final JToolBar bar = (JToolBar) getComponent(i);
            final String name = (String) bar.getClientProperty(nameKey);
            final int mode = ((Integer) bar.getClientProperty(modeKey)).intValue();
            if (mode == MDIApplication.TOOLBAR_PROGRAM) {
                continue;
            }
            JMenu menu = new JMenu(name);
            final JRadioButtonMenuItem show = new JRadioButtonMenuItem("Show");
            final JRadioButtonMenuItem hide = new JRadioButtonMenuItem("Hide");
            final JRadioButtonMenuItem auto = new JRadioButtonMenuItem("Auto");
            show.setSelected(mode == MDIApplication.TOOLBAR_VISIBLE);
            hide.setSelected(mode == MDIApplication.TOOLBAR_INVISIBLE);
            auto.setSelected(mode == MDIApplication.TOOLBAR_AUTO);
            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object source = e.getSource();
                    if (source == hide) {
                        bar.putClientProperty(modeKey, new Integer(MDIApplication.TOOLBAR_INVISIBLE));
                        bar.setVisible(false);
                        removeAuto(bar);
                    } else if (source == show) {
                        bar.putClientProperty(modeKey, new Integer(MDIApplication.TOOLBAR_VISIBLE));
                        bar.setVisible(true);
                        removeAuto(bar);
                    } else if (source == auto) {
                        bar.putClientProperty(modeKey, new Integer(MDIApplication.TOOLBAR_AUTO));
                        addAuto(bar);
                    }
                }
            };
            show.addActionListener(al);
            hide.addActionListener(al);
            auto.addActionListener(al);
            menu.add(show);
            menu.add(hide);
            menu.add(auto);
            popup.add(menu);
        }
        return popup;
    }

    private void addAuto(JToolBar bar) {
        autos.put(bar, new AutoToolbarListener(bar));
    }

    private void removeAuto(JToolBar bar) {
        AutoToolbarListener l = (AutoToolbarListener) autos.remove(bar);
        if (l != null) {
            l.dispose();
        }
    }

    private class AutoToolbarListener {

        private JToolBar bar;
        private int count = 0;
        private PropertyChangeListener pcl  = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Component source = (Component) evt.getSource();
                if (source.isEnabled()) {
                    count++;
                } else {
                    count--;
                }
                bar.setVisible(count > 0);
            }
        };

        AutoToolbarListener(JToolBar bar) {
            this.bar = bar;
            int n = bar.getComponentCount();
            for (int i = 0; i < n; i++) {
                Component c = bar.getComponent(i);
                if (c.isEnabled()) {
                    count++;
                }
                c.addPropertyChangeListener("enabled", pcl);
            }
            bar.setVisible(count > 0);
        }

        void dispose() {
            int n = bar.getComponentCount();
            for (int i = 0; i < n; i++) {
                Component c = bar.getComponent(i);
                c.removePropertyChangeListener("enabled", pcl);
            }
        }
    }
}
