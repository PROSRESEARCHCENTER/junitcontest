package org.freehep.application.studio.pluginmanager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.freehep.application.studio.PluginDir;
import org.freehep.application.studio.PluginInfo;
import org.freehep.util.images.ImageHandler;

/**
 * GUI panel that handles selection and installation of new plugins.
 * 
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: AvailablePluginPanel.java 14095 2013-01-29 20:57:42Z tonyj $
 */
class AvailablePluginPanel extends JPanel {

    private PluginManager manager;
    private JButton install;
    private JRadioButton cb1, cb2, cb3;
    private JTree tree;
    private PluginInfoPanel infoPanel;
    static final Logger logger = Logger.getLogger(AvailablePluginPanel.class.getName());
    private ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PluginDir dir = null;
            if (cb1.isSelected()) {
                dir = PluginDir.USER;
            } else if (cb2.isSelected()) {
                dir = PluginDir.GROUP;
            } else if (cb3.isSelected()) {
                dir = PluginDir.SYSTEM;
            }
            TreePath[] selected = tree.getSelectionPaths();
            List<PluginInfo> selectedPlugins = new ArrayList<PluginInfo>();
            for (TreePath path : selected) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                try {
                    PluginInfo plugin = (PluginInfo) node.getUserObject();
                    plugin.setDirectory(dir);
                    selectedPlugins.add(plugin);
                } catch (ClassCastException x) {
                }
            }
            boolean restart = manager.install(AvailablePluginPanel.this, selectedPlugins);
            if (restart) manager.restart(AvailablePluginPanel.this);
        }
    };
    private ChangeListener cl = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateTree();
        }
    };

    AvailablePluginPanel(PluginManager manager) {
        super(new BorderLayout(5, 5));
        this.manager = manager;

        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        tree = new JTree();
        tree.setCellRenderer(new Renderer());
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true); // For people who can't double-click
        tree.setVisibleRowCount(8);
        tree.getSelectionModel().addTreeSelectionListener(new TreeHandler());
        JScrollPane treescroll = new JScrollPane(tree);
        treescroll.setBorder(BorderFactory.createTitledBorder("Available Plugins"));

        panel.add(treescroll);

        updateTree();

        infoPanel = new PluginInfoPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Plugin Info"));
        panel.add(infoPanel);

        add(panel, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel cb = new JPanel(new BorderLayout());

        cb1 = createExtensionButton(PluginDir.USER);
        cb2 = createExtensionButton(PluginDir.GROUP);
        cb3 = createExtensionButton(PluginDir.SYSTEM);

        if (cb1.isEnabled()) {
            cb1.setSelected(true);
        } else if (cb2.isEnabled()) {
            cb2.setSelected(true);
        } else if (cb3.isEnabled()) {
            cb3.setSelected(true);
        }

        cb.add(cb1, BorderLayout.NORTH);
        cb.add(cb2, BorderLayout.CENTER);
        cb.add(cb3, BorderLayout.SOUTH);

        ButtonGroup rg = new ButtonGroup();
        rg.add(cb1);
        rg.add(cb2);
        rg.add(cb3);
        bottom.add(cb, BorderLayout.NORTH);

        install = new JButton("Install selected plugins");
        install.addActionListener(al);
        install.setEnabled(false);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(install);
        bottom.add(buttonPanel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
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

    private void updateTree() {
        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode();
        List<PluginInfo> plugins = manager.getInstallablePlugins();
        if (plugins != null) {
            Map map = new HashMap();
            map.put(Collections.EMPTY_LIST, treeRoot);
            for (PluginInfo info : plugins) {
                List<String[]> categories = info.getCategories();
                if (categories == null) {
                    categories = Collections.singletonList(new String[]{"Uncategorized"});
                }
                for (String[] category : categories) {
                    DefaultMutableTreeNode parent = findParent(map, Arrays.asList(category));
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(info, false);
                    parent.add(node);
                }
            }
        }
        DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot, true);
        tree.setModel(treeModel);
    }

    private static DefaultMutableTreeNode findParent(Map map, List category) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) map.get(category);
        if (node != null) {
            return node;
        }
        int size = category.size();
        List parentCategory = category.subList(0, size - 1);
        DefaultMutableTreeNode parent = findParent(map, parentCategory);
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(category.get(size - 1));
        parent.add(child);
        map.put(category, child);
        return child;
    }
    
    private void insertNode(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
      
    }

    private JRadioButton createExtensionButton(PluginDir directory) {
        String dir = manager.getApplication().getExtensionsDir(directory);
        boolean enabled = dir != null;
        if (enabled) {
            File df = new File(dir);
            if (!df.exists()) {
                df.mkdirs(); // Doesn't throw an exception
            }
            enabled = df.canWrite();
            try {
                dir = df.getCanonicalPath();
            } catch (IOException x) {
            }
        } else {
            dir = ".";
        }
        JRadioButton rb = new JRadioButton("Install in " + directory.getLabel() + " extensions directory (" + dir + ")");
        rb.setEnabled(enabled);
        return rb;
    }

    private class Renderer extends DefaultTreeCellRenderer {

        private Icon pluginIcon = ImageHandler.getIcon("icons/plugin.gif", AvailablePluginPanel.class);

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                Object info = ((DefaultMutableTreeNode) value).getUserObject();
                if (info instanceof PluginInfo) {
                    PluginInfo plugleaf = (PluginInfo) info;
                    setIcon(pluginIcon);
                    setText(plugleaf.getName());
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
            } else {
                infoPanel.setPlugin(null);
            }
            boolean enabled = false;
            TreePath[] checkselection = tree.getSelectionPaths();
            if (checkselection != null) {
                for (int i = 0; i < checkselection.length; i++) {
                    node = (DefaultMutableTreeNode) checkselection[i].getLastPathComponent();
                    if (node.getUserObject() instanceof PluginInfo) {
                        enabled = true;
                        break;
                    }
                }
            }
            install.setEnabled(enabled);
        }
    }
}
