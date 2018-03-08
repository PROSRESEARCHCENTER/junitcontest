package org.freehep.application.studio.pluginmanager;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by IntelliJ IDEA. User: Tony Johnson Date: Feb 3, 2004 Time: 10:28:08
 * PM
 */
class PluginManagerDialog extends JDialog {

    private PluginManager manager;
    private JTabbedPane tabs;
    private JDialog waitDialog;
    private ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };
    private ChangeListener cl = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            setTabLabels();
            if (waitDialog != null) {
                waitDialog.dispose();
            }
        }
    };

    @Override
    public void addNotify() {
        super.addNotify();
        manager.addChangeListener(cl);
    }

    @Override
    public void removeNotify() {
        manager.removeChangeListener(cl);
        super.removeNotify();
    }

    PluginManagerDialog(JFrame frame, PluginManager manager) {
        super(frame);
        this.manager = manager;

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tabs = new JTabbedPane() {
            @Override
            public void setSelectedIndex(int index) {
                if (index == 1 && !PluginManagerDialog.this.manager.isPluginListIsReady()) {
                    final JOptionPane option = new JOptionPane();
                    option.setMessage("Please wait while plugin list is downloaded");
                    JButton cancel = new JButton("Cancel") {
                        @Override
                        protected void fireActionPerformed(ActionEvent e) {
                            option.setValue(this);
                        }
                    };
                    cancel.setMnemonic('C');
                    option.setOptions(new JButton[]{cancel});
                    waitDialog = option.createDialog(this, "Waiting");
                    waitDialog.setVisible(true);
                    waitDialog = null;
                    if (option.getValue() == cancel) {
                        return;
                    }
                }
                super.setSelectedIndex(index);
            }
        };

        InstalledPluginPanel availablePanel = new InstalledPluginPanel(manager);
        tabs.add(availablePanel);

        AvailablePluginPanel installPanel = new AvailablePluginPanel(manager);
        tabs.add(installPanel);

        setTabLabels();
        panel.add(tabs, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton close = new JButton("Close");
        close.addActionListener(al);
        buttons.add(close);
        panel.add(buttons, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    private void setTabLabels() {
        int n = manager.getActivePlugins().size();
        String title = "Installed (" + n + ")";
        tabs.setTitleAt(0, title);
        title = "Available";
        List available = manager.getInstallablePlugins();
        if (available != null) {
            n = available.size();
            title += " (" + n + ")";
            tabs.setEnabledAt(1, n > 0);
        }
        tabs.setTitleAt(1, title);
    }
}
