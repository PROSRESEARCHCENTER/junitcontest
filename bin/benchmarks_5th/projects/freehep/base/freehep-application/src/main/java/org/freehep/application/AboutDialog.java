package org.freehep.application;

import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * An about box for use by Applications. By default includes buttons for users
 * to check System/Application/User properties
 *
 * @author tonyj
 * @version $Id: AboutDialog.java 16110 2014-08-21 01:38:46Z onoprien $
 */
public class AboutDialog extends javax.swing.JDialog {

    private Application app;

    /**
     * Creates new AboutDialog.
     *
     * @param app The application owning the AboutDialog
     */
    public AboutDialog(Application app) {
        super((javax.swing.JFrame) javax.swing.SwingUtilities.getAncestorOfClass(javax.swing.JFrame.class, app));
        this.app = app;
        final java.util.Properties props = app.getUserProperties();
        final JPanel panel = new JPanel(new java.awt.BorderLayout());

        java.net.URL image = PropertyUtilities.getURL(props, "aboutImage", null);
        if (image != null) {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(image);
            javax.swing.JLabel l = new javax.swing.JLabel(icon);
            l.setBackground(java.awt.Color.white);
            l.setOpaque(true);
            panel.add(l);
        } else {
            String text = props.getProperty("aboutLabel", "<html><h1>{title}</h1>");
            panel.add(new javax.swing.JLabel(text));
        }
        setTitle(app.getFullVersion());
        setModal(true);
        final JPanel info = createInfoPanel();
        if (info != null) {
            panel.add(info, java.awt.BorderLayout.EAST);
        }
        info.setVisible(PropertyUtilities.getBoolean(props, "aboutShowInfoPanel", true));

        javax.swing.Action toggleInfoPanel = new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (info != null) {
                    PropertyUtilities.setBoolean(props, "aboutShowInfoPanel", !info.isVisible());
                    info.setVisible(!info.isVisible());
                    if (!info.isVisible()) {
                        panel.requestFocus();
                    }
                    pack();
                }
            }
        };
        panel.getInputMap(JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK), "toggleInfoPanel");
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK), "toggleInfoPanel");
        panel.getActionMap().put("toggleInfoPanel", toggleInfoPanel);
        setContentPane(panel);
        setResizable(false);
    }

    /**
     * Override to customize the InfoPanel. By default the InfoPanel contains
     * buttons allowing the user to view the System/Application/User properties.
     */
    protected final JPanel createInfoPanel() {
        return new org.freehep.application.AboutDialog.InfoPanel();
    }

    protected class InfoPanel extends JPanel {

        private JButton b1, b2, b3;
        private ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JButton source = (JButton) e.getSource();
                PropertyUtilities.PropertyTable propTable;
                if (source == b1) {
                    propTable = new PropertyUtilities.PropertyTable(System.getProperties(), null);
                } else if (source == b2) {
                    propTable = new PropertyUtilities.PropertyTable(app.getAppProperties(), System.getProperties());
                } else {
                    propTable = new PropertyUtilities.PropertyTable(app.getUserProperties(), app.getAppProperties());
                }

                JTable table = new JTable(propTable);
                String title = source.getText();
                if (title.endsWith("...")) {
                    title = title.substring(0, title.length() - 3);
                }
                title += " Properties";
                javax.swing.JDialog dlg = new javax.swing.JDialog(AboutDialog.this, title);
                dlg.setContentPane(new javax.swing.JScrollPane(table));
                dlg.setModal(true);
                dlg.pack();
                dlg.setLocationRelativeTo(AboutDialog.this);
                dlg.setVisible(true);
            }
        };

        public InfoPanel() {
            setBorder(javax.swing.BorderFactory.createTitledBorder("Properties"));
            setLayout(new java.awt.BorderLayout());
            JPanel p = new JPanel(new java.awt.GridLayout(0, 1));
            b1 = new JButton("System...");
            b2 = new JButton("Application...");
            b3 = new JButton("User...");
            try {
                System.getProperties();
            } catch (SecurityException x) {
                b1.setEnabled(false);
            }
            b1.addActionListener(al);
            b2.addActionListener(al);
            b3.addActionListener(al);

            p.add(b1);
            p.add(b2);
            p.add(b3);

            add(p, java.awt.BorderLayout.NORTH);
        }
    }
}
