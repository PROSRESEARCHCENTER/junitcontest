package org.freehep.application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * A menu for setting the look and feel of an application
 *
 * @author tonyj
 * @version $Id: LookAndFeelMenu.java 14082 2012-12-12 16:16:53Z tonyj $
 */
public class LookAndFeelMenu extends JMenu {

    /**
     * Creates a Look and Feel menu
     */
    public LookAndFeelMenu() {
        super("Look and Feel");
    }

    @Override
    public void fireMenuSelected() {
        removeAll();
        ActionListener listener = new LAFActionListener();
        LookAndFeelInfo info[] = UIManager.getInstalledLookAndFeels();
        String currentLAF = UIManager.getLookAndFeel().getClass().getName();
        for (int i = 0; i < info.length; i++) {
            JRadioButtonMenuItem radio = new JRadioButtonMenuItem(info[i].getName());
            String className = info[i].getClassName();
            radio.setActionCommand(className);
            radio.setSelected(className.equals(currentLAF));
            radio.addActionListener(listener);
            add(radio);
        }
        super.fireMenuSelected();
    }

    private class LAFActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Application.getApplication().setLookAndFeel(e.getActionCommand());
        }
    }
}