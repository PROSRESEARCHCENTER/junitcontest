package org.freehep.application.studio.pluginmanager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.freehep.application.studio.PluginDir;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.PluginInfo;
import org.freehep.application.studio.Studio;
import org.freehep.swing.ErrorDialog.ErrorDetailsDialog;
import org.freehep.util.VersionComparator;
import org.freehep.util.images.ImageHandler;

/**
 * GUI panel that handles operations on installed plugins.
 * 
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: $
 */
class InstalledPluginPanel extends JPanel {

    private JButton remove;
    private JButton update;
    private JTree tree;
    private PluginInfoPanel infoPanel;
    private PluginActivationPanel activation;
    private PluginManager manager;
    static final Logger logger = Logger.getLogger(InstalledPluginPanel.class.getName());
    private ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == remove) {
                try {
                    removeSelectedPlugins();
                } catch (Throwable io) {
                    logger.log(Level.SEVERE,"Error while removing plugin",io);
                }
            } else if (source == update) {
                boolean restart = manager.update(InstalledPluginPanel.this);
                if (restart) manager.restart(InstalledPluginPanel.this);
            }
        }
    };
    private ChangeListener cl = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateTree();
        }
    };
    
    private HashMap<String,PluginInfo> loadedPlugins;
    private Set<PluginInfo> updatablePlugins;

    InstalledPluginPanel(PluginManager manager) {
        super(new BorderLayout());
        this.manager = manager;

        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        tree = new JTree();
        tree.setCellRenderer(new Renderer());
        tree.setRootVisible(false);
        tree.setVisibleRowCount(8);
        tree.setShowsRootHandles(true); // For people who can't double-click
        tree.getSelectionModel().addTreeSelectionListener(new TreeHandler());

        JScrollPane treescroll = new JScrollPane(tree);
        treescroll.setBorder(BorderFactory.createTitledBorder("Installed Plugins"));
        panel.add(treescroll);

        JPanel rightPanel = new JPanel(new BorderLayout());
        infoPanel = new PluginInfoPanel() {
            private JLabel updateLabel;

            @Override
            void addExtraInfo(Object c1, Object c2) {
                add(new JLabel("Latest published version:"), c1);
                add(updateLabel = new JLabel(), c2);
            }

            @Override
            void setExtraInfo(PluginInfo info) {
                String label = null;
                Map<PluginInfo, PluginInfo> updateMap = InstalledPluginPanel.this.manager.getUpdateMap();
                if (updateMap != null) {
                    PluginInfo update = updateMap.get(info);
                    if (update != null) {
                        label = update.getVersion();
                    }
                }
                updateLabel.setText(label);
            }
        };
        infoPanel.setBorder(BorderFactory.createTitledBorder("Plugin Info"));
        rightPanel.add(infoPanel, BorderLayout.CENTER);

        activation = new PluginActivationPanel();
        activation.setBorder(BorderFactory.createTitledBorder("Plugin Activation"));
        rightPanel.add(activation, BorderLayout.SOUTH);

        panel.add(rightPanel);

        JPanel buttons = new JPanel();
        remove = new JButton("Remove selected plugins");
        remove.addActionListener(al);
        buttons.add(remove);
        update = new JButton("Update installed plugins");
        update.addActionListener(al);
        buttons.add(update);

        add(BorderLayout.SOUTH, buttons);
        add(BorderLayout.CENTER, panel);

        updateTree();
    }

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

    private DefaultMutableTreeNode addSubNode(DefaultMutableTreeNode root, EnumMap<PluginDir,DefaultMutableTreeNode> map, PluginDir dir) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir.getLabel(), true);
        map.put(dir, node);
        root.add(node);
        return node;
    }

    protected final void updateTree() {

        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode();
        EnumMap<PluginDir,DefaultMutableTreeNode> map = new EnumMap<PluginDir,DefaultMutableTreeNode>(PluginDir.class);
        
        List<PluginInfo> updates = manager.getUpdatablePlugins();
        updatablePlugins = updates == null ? Collections.emptySet() : new HashSet(updates);
        
        Map<String,PluginInfo> activePlugins = manager.getActivePlugins();
        loadedPlugins = new HashMap<String,PluginInfo>(activePlugins.size()*2);
        for (PluginInfo loaded : manager.getApplication().getPlugins()) {
            PluginInfo active = activePlugins.get(loaded.getName());
            if (active != null && active.getDirectory() == loaded.getDirectory() && 
                VersionComparator.compareVersion(active.getVersion(), loaded.getVersion()) == 0) {
                  loadedPlugins.put(loaded.getName(), loaded);
            } else {
                  loadedPlugins.put(loaded.getName(), null);
            }
        }
        for (PluginInfo info : activePlugins.values()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(info, false);
            DefaultMutableTreeNode rootNode = map.get(info.getDirectory());
            if (rootNode == null) {
                rootNode = addSubNode(treeRoot, map, info.getDirectory());
            }
            rootNode.add(node);
        }

        DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot, true);
        tree.setModel(treeModel);
        DefaultMutableTreeNode userNode = map.get(PluginDir.USER);
        if (userNode != null) tree.expandPath(new TreePath(new Object[]{treeRoot, userNode}));

        remove.setEnabled(false);
        update.setEnabled(!updatablePlugins.isEmpty());
    }

    protected void removeSelectedPlugins() throws IOException {
        TreePath[] selectednodes = tree.getSelectionPaths();
        if (selectednodes != null && selectednodes.length > 0) {
            ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>(selectednodes.length);
            for (int i = 0; i < selectednodes.length; i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectednodes[i].getLastPathComponent();
                PluginInfo plugleaf = (PluginInfo) node.getUserObject();
                plugins.add(plugleaf);
            }
            boolean restart = manager.uninstall(this, plugins);
            updateTree();
            if (restart) manager.restart(InstalledPluginPanel.this);
        }
    }

    private class PluginActivationPanel extends JPanel implements ActionListener {

        PluginActivationPanel() {
            initComponents();
            setPlugin(null);
        }

        private void initComponents() {
            GridBagConstraints gridBagConstraints;

            startCheckBox = new JCheckBox();
            startButton = new JButton();
            stopButton = new JButton();
            errorButton = new JButton();

            setLayout(new GridBagLayout());

            startCheckBox.setText("Load when application starts");
            startCheckBox.addActionListener(this);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            add(startCheckBox, gridBagConstraints);

            startButton.setText("Start");
            startButton.addActionListener(this);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(0, 20, 0, 0);
            add(startButton, gridBagConstraints);

            stopButton.setText("Stop");
            stopButton.addActionListener(this);
            gridBagConstraints.insets = new Insets(0, 5, 0, 0);
            add(stopButton, gridBagConstraints);

            errorButton.setText("Show Error...");
            errorButton.addActionListener(this);
            add(errorButton, gridBagConstraints);
        }

        final void setPlugin(PluginInfo info) {
            this.info = info;
            setEnable();
        }

        private void setEnable() {
            if (info == null) {
                startCheckBox.setEnabled(false);
                startButton.setEnabled(false);
                stopButton.setEnabled(false);
                errorButton.setEnabled(false);
            } else {
                startCheckBox.setEnabled(true);
                startCheckBox.setSelected(info.isLoadAtStart());
                PluginInfo loaded = loadedPlugins.get(info.getName());
                Plugin plugin = loaded == null ? null : loaded.getPlugin();
                if (loaded == null) {
                    startButton.setText("Load");
                    startButton.setEnabled(info.getErrorStatus() == null && !loadedPlugins.containsKey(info.getName()));
                    errorButton.setEnabled(info.getErrorStatus() != null);
                } else {
                    startButton.setText("Start");
                    startButton.setEnabled(plugin == null && loaded.hasMainClass() && loaded.getErrorStatus() == null);
                    errorButton.setEnabled(info.getErrorStatus() != null || loaded.getErrorStatus() != null);
                }
                stopButton.setEnabled(plugin != null && plugin.canBeShutDown());
            }
        }

        private void startCheckBoxActionPerformed(ActionEvent evt) {
            info.setLoadAtStart(startCheckBox.isSelected());
        }

        private void stopButtonActionPerformed(ActionEvent evt) {
            manager.getApplication().stopPlugin(info);
            setEnable();
            InstalledPluginPanel.this.repaint();
        }

        private void startButtonActionPerformed(ActionEvent evt) {
            try {
                if (evt.getActionCommand().equals("Start")) {
                    manager.getApplication().startPlugin(info);
                } else {
                    manager.getApplication().loadPlugins(Collections.singletonList(info));
                }
                cl.stateChanged(null);
                setEnable();
                InstalledPluginPanel.this.repaint();
            } catch (Throwable t) {
                JDialog parent = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this);
                Studio.error(parent, "Error starting plugin", t);
            }
        }

        private void errorButtonActionPerformed(ActionEvent evt) {
            JDialog parent = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this);
            Throwable t = info.getErrorStatus();
            if (t == null) {
                PluginInfo loaded = loadedPlugins.get(info.getName());
                if (loaded != null) t = loaded.getErrorStatus();
                if (t == null) return;
            }
            ErrorDetailsDialog dlg = new ErrorDetailsDialog(parent, t);
            dlg.setDefaultCloseOperation(ErrorDetailsDialog.DISPOSE_ON_CLOSE);
            dlg.pack();
            dlg.setLocationRelativeTo(parent);
            dlg.setVisible(true);
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            Object source = actionEvent.getSource();
            if (source == startCheckBox) {
                startCheckBoxActionPerformed(actionEvent);
            } else if (source == startButton) {
                startButtonActionPerformed(actionEvent);
            } else if (source == stopButton) {
                stopButtonActionPerformed(actionEvent);
            } else if (source == errorButton) {
                errorButtonActionPerformed(actionEvent);
            }
        }
        
        private PluginInfo info;
        // Variables declaration - do not modify
        private JButton startButton;
        private JCheckBox startCheckBox;
        private JButton stopButton;
        private JButton errorButton;
        // End of variables declaration
    }

    private class Renderer extends DefaultTreeCellRenderer {

        private Icon pluginIcon = ImageHandler.getIcon("icons/plugin.gif", InstalledPluginPanel.class);
        private Icon updateIcon = ImageHandler.getIcon("icons/plugin_update.gif", InstalledPluginPanel.class);
        private Icon disabledIcon = ImageHandler.getIcon("icons/plugin_disabled.gif", InstalledPluginPanel.class);
        private Icon errorIcon = ImageHandler.getIcon("icons/plugin_error.gif", InstalledPluginPanel.class);

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                Object info = ((DefaultMutableTreeNode) value).getUserObject();
                if (info instanceof PluginInfo) {
                    PluginInfo plugleaf = (PluginInfo) info;
                    String label = plugleaf.getName();
                    setIcon(pluginIcon);
                    boolean isUpdatable = updatablePlugins.contains(plugleaf);
                    if (isUpdatable) {
                        label = "<b>" + label + " (update available)</b>";
                        setIcon(updateIcon);
                    }
                    PluginInfo loaded = loadedPlugins.get(plugleaf.getName());
                    if (loaded == null) {
                        label = "<font color=\"#888888\">" + label + "</font>";
                        if (plugleaf.getErrorStatus() != null) {
                            setIcon(errorIcon);
                        } else if (!isUpdatable) {
                            setIcon(disabledIcon);
                        }
                    } else {
                        if (plugleaf.getErrorStatus() != null || loaded.getErrorStatus() != null) {
                            setIcon(errorIcon);
                        } else if (!isUpdatable && loaded.getPlugin() == null) {
                            setIcon(disabledIcon);
                        }
                    }
                    if (label.startsWith("<")) label = "<html>" + label;
                    setText(label);
                }
            }
            return this;
        }
    }

    private class TreeHandler implements TreeSelectionListener {

        @Override
        public void valueChanged(TreeSelectionEvent evt) {
            TreePath selection = evt.getPath();
            DefaultMutableTreeNode node = selection == null ? null : (DefaultMutableTreeNode) selection.getLastPathComponent();

            if (node != null && node.getUserObject() instanceof PluginInfo) {
                PluginInfo plugprops = (PluginInfo) node.getUserObject();
                infoPanel.setPlugin(plugprops);
                activation.setPlugin(plugprops);
            } else {
                infoPanel.setPlugin(null);
                activation.setPlugin(null);
            }

            boolean enableRemove = false;
            TreePath[] checkselection = tree.getSelectionPaths();
            if (checkselection != null) {
                enableRemove = true;

                EnumSet<PluginDir> writeableDirs = EnumSet.noneOf(PluginDir.class);
                checkDir(writeableDirs, PluginDir.USER);
                checkDir(writeableDirs, PluginDir.GROUP);
                checkDir(writeableDirs, PluginDir.SYSTEM);

                for (int i = 0; i < checkselection.length; i++) {
                    node = (DefaultMutableTreeNode) checkselection[i].getLastPathComponent();
                    if (!(node.getUserObject() instanceof PluginInfo)) {
                        enableRemove = false;
                        break;
                    } else {
                        PluginInfo info = (PluginInfo) node.getUserObject();
                        if (!writeableDirs.contains(info.getDirectory())) {
                            enableRemove = false;
                            break;
                        }
                    }
                }
            }
            remove.setEnabled(enableRemove);
        }

        private void checkDir(EnumSet<PluginDir> writeableDirs, PluginDir dir) {
            if (dir != null) {
                String path = manager.getApplication().getExtensionsDir(dir);
                if (path != null) {
                    File file = new File(path);
                    if (file.canWrite()) writeableDirs.add(dir);
                }
            }
        }
    }
}