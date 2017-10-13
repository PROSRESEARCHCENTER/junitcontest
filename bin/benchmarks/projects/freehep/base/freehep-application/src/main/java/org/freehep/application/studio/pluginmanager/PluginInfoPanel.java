package org.freehep.application.studio.pluginmanager;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.freehep.application.studio.PluginInfo;

/**
 * Plugin information panel used by the plugin manager dialog.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: PluginInfoPanel.java 14533 2013-04-18 22:14:05Z onoprien $
 */
class PluginInfoPanel extends JPanel {

    private JLabel name;
    private JLabel author;
    private JLabel currversion;
    private JTextArea textarea;
    private PluginInfo info;

    PluginInfoPanel() {
        super(new java.awt.GridBagLayout());

        java.awt.GridBagConstraints gbc1 = new java.awt.GridBagConstraints();
        gbc1.ipadx = 7;
        gbc1.anchor = java.awt.GridBagConstraints.EAST;

        java.awt.GridBagConstraints gbc2 = new java.awt.GridBagConstraints();
        gbc2.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gbc2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc2.anchor = java.awt.GridBagConstraints.WEST;
        gbc2.weightx = 1.0;

        java.awt.GridBagConstraints gbc3 = new java.awt.GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gbc3.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gbc3.fill = java.awt.GridBagConstraints.BOTH;
        gbc3.anchor = java.awt.GridBagConstraints.SOUTH;
        gbc3.weightx = 1.0;
        gbc3.weighty = 1.0;

        add(new JLabel("Name:"), gbc1);
        add(name = new JLabel(), gbc2);
        add(new JLabel("Author:"), gbc1);
        add(author = new JLabel(), gbc2);
        add(new JLabel("Version:"), gbc1);
        add(currversion = new JLabel(), gbc2);
        addExtraInfo(gbc1, gbc2);
//        add(new JLabel("Description:"), gbc1);

        textarea = new JTextArea(6, 30);
        textarea.setEditable(false);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        JScrollPane editorscroll = new JScrollPane(textarea);
        add(editorscroll, gbc3);
    }

    void addExtraInfo(Object contraint1, Object contraint2) {
    }

    void setPlugin(PluginInfo info) {
        this.info = info;
        if (info != null) {
            name.setText(info.getName());
            author.setText(info.getAuthor());
            currversion.setText(info.getVersion());
            textarea.setText(info.getDescription());
        } else {
            name.setText(null);
            author.setText(null);
            currversion.setText(null);
            textarea.setText(null);
        }
        setExtraInfo(info);
    }

    PluginInfo getPlugin() {
        return info;
    }

    void setExtraInfo(PluginInfo info) {
    }
}
