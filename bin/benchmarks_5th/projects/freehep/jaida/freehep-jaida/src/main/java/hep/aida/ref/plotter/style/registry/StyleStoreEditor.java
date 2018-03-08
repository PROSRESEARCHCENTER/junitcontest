package hep.aida.ref.plotter.style.registry;

import hep.aida.ref.plotter.Style;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * This class is a stand-alone Style Store Editor
 *
 */
public class StyleStoreEditor extends JSplitPane {
    private JTabbedPane tabbedPanel;
    private JTree tree;
    private JPopupMenu entryMenu;
    private JPopupMenu storeMenu;
    private CreateStyleStorePanel createPanel;
    private OpenStyleStorePanel openPanel;
    private AddStyleStoreEntryPanel addStyleStoreEntryPanel;
    private WriteStyleStorePanel writeStyleStorePanel;
    
    
    public StyleStoreEditor() {
        super(JSplitPane.HORIZONTAL_SPLIT);
        init();
    }
    
    private void init() {
        ActionListener aL = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String command = ev.getActionCommand();
                if (command == null || command.trim().equals("")) return;
                
                if (command.equals("Move Up")) moveStoreEntryUpAction();
                else if (command.equals("Move Down")) moveStoreEntryDownAction();
                else if (command.equals("Remove This Entry")) removeStoreEntryAction();
                else if (command.equals("Save IPlotterStyle to XML...")) savePlotterStyleAction();
                else if (command.equals("Add Store Entry...")) addStoreEntryAction();
                else if (command.equals("Save As...")) saveStoreAsAction();
                else if (command.equals("Commit Store")) commitStoreAction();
                else if (command.equals("Close Store")) closeStoreAction();
            }
        };
        
        JMenuItem item = null;
        entryMenu = new JPopupMenu("Store Entry Menu");
        item = new JMenuItem("Edit this Entry");
        item.addActionListener(aL);
        entryMenu.add(item);
        item = new JMenuItem("Move Up");
        item.addActionListener(aL);
        entryMenu.add(item);
        item = new JMenuItem("Move Down");
        item.addActionListener(aL);
        entryMenu.add(item);
        item = new JMenuItem("Remove This Entry");
        item.addActionListener(aL);
        entryMenu.add(item);
        item = new JMenuItem("Save IPlotterStyle to XML...");
        item.addActionListener(aL);
        entryMenu.add(item);
        
        storeMenu = new JPopupMenu("Store Menu");
        item = new JMenuItem("Add Store Entry...");
        item.addActionListener(aL);
        storeMenu.add(item);
        item = new JMenuItem("Save As...");
        item.addActionListener(aL);
        storeMenu.add(item);
        item = new JMenuItem("Commit Store");
        item.addActionListener(aL);
        storeMenu.add(item);
        item = new JMenuItem("Close Store");
        item.addActionListener(aL);
        storeMenu.add(item);
        
        tabbedPanel = new JTabbedPane();
        this.setRightComponent(new JScrollPane(tabbedPanel));
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        //treeModel.addTreeModelListener(new MyTreeModelListener());
        
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setExpandsSelectedPaths(true);
        
        // Fistern for double-click events on the leaf nodes
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) popupAction(e);
                else if(e.getClickCount() == 2) doubleClickAction(e);
            }
            
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) popupAction(e);
            }
        };
        tree.addMouseListener(ml);
        
        this.setLeftComponent(new JScrollPane(tree));
        
        this.setDividerLocation(150);
    }
    
    private void addMenus(JMenuBar menuBar) {
        // FILE Menu
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem item = new JMenuItem("Create Style Store...");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newStoreAction();
            }
        });
        fileMenu.add(item);
        
        item = new JMenuItem("Open Style Store...");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openStoreAction();
            }
        }); 
        fileMenu.add(item);
        
        item = new JMenuItem("Save As...");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveStoreAsAction();
            }
        });
        item.setEnabled(false);
        //fileMenu.add(item);
        
        item = new JMenuItem("Close Store");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeStoreAction();
            }
        });
        item.setEnabled(false);
        //fileMenu.add(item);
        
        
        fileMenu.addSeparator();
        item = new JMenuItem("Exit");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitAction();
            }
        });
        fileMenu.add(item);
        
        menuBar.add(fileMenu);
    }
    
    
    // Menu item actions
    
    void newStoreAction() {
        if (createPanel == null) createPanel = new CreateStyleStorePanel(this);
        try {
            IStyleStore store = createPanel.createStore();
            if (store != null) {
                addStoreNode(store);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void openStoreAction() {
        if (openPanel == null) openPanel = new OpenStyleStorePanel(this);
        else openPanel.updateAction();
        try {
            IStyleStore store = openPanel.openStore();
            if (store != null) addStoreNode(store);
        } catch (Exception e) {
            e.printStackTrace();
            //app.error("Error while openning IStyleStore: "+e.getMessage(), e);
        }
    }
    
    void addStoreNode(IStyleStore store) {
        DefaultMutableTreeNode storeNode = new DefaultMutableTreeNode(store, true);
        
        String[] names = store.getAllStyleNames();
        StyleStoreEntry entry = null;
        StoreEntryNode node = null;
        for (int i=0; i<names.length; i++) {
            if (store instanceof BaseStyleStore) entry = ((BaseStyleStore) store).getStoreEntry(names[i]);
            else entry = new StyleStoreEntry(names[i], store.getStyle(names[i]), store.getRuleForStyle(names[i]));
            node = new StoreEntryNode(entry);
            storeNode.add(node);
        }
        
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        ((DefaultTreeModel) tree.getModel()).insertNodeInto(storeNode, root, root.getChildCount());
        tree.setSelectionPath(new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(storeNode)));
    }
    
    void commitStoreAction() {
        try {
            IStyleStore store = getSelectedStore();
            store.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //app.error("Error while Committing StyleStore: \n\t"+e.getMessage(), e);
        }
    }
    
    void saveStoreAsAction() {
        if (writeStyleStorePanel == null) writeStyleStorePanel = new WriteStyleStorePanel(this);
        try {
            IStyleStore store = getSelectedStore();
            writeStyleStorePanel.writeStore(store);
        } catch (Exception e) {
            e.printStackTrace();
            //app.error("Error while Writing StyleStore to XML File: \n\t"+e.getMessage(), e);
        }
   }
    
    void closeStoreAction() {
        IStyleStore store = getSelectedStore();
        
        // Remove Store Node and its children from the Tree
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        int index = findInNode(root, store.getStoreName());
        DefaultMutableTreeNode storeNode = (DefaultMutableTreeNode) root.getChildAt(index);
        for (int i=0; i<storeNode.getChildCount(); i++) {
            TreeNode child = storeNode.getChildAt(i);
            if (child instanceof StoreEntryNode) {
                JComponent page = ((StoreEntryNode) child).getPage();
                if (page != null) {
                    tabbedPanel.remove(page);
                }
            }
        }
        storeNode.removeAllChildren();
        root.remove(index);
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(root);
       
        store.close();
    }
    
    void addStoreEntryAction() {
        if (addStyleStoreEntryPanel == null) addStyleStoreEntryPanel = new AddStyleStoreEntryPanel(this);
        try {
            StyleStoreEntry entry = addStyleStoreEntryPanel.createStoreEntry();
            if (entry == null) return;
            IStyleStore store = getSelectedStore();
            
            String styleName = entry.getName();
            String fullStyleName = store.getStoreName() + "." + styleName;
            entry.getStyle().setParameter(Style.PLOTTER_STYLE_NAME, fullStyleName);
            if (store instanceof BaseStyleStore) {
                ((BaseStyleStore) store).addStoreEntry(entry);
            } else {
                store.addStyle(styleName, entry.getStyle(), entry.getRule());
            }
            
            // Now rearrange the Store Node
            StoreEntryNode node = new StoreEntryNode(entry);
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
            int index = findInNode(root, store.getStoreName());
            DefaultMutableTreeNode storeNode = (DefaultMutableTreeNode) root.getChildAt(index);
            storeNode.add(node);
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(storeNode);
        } catch (Exception e) {
            e.printStackTrace();
            //app.error("Error while creating StyleStoreEntry: \n\t"+e.getMessage(), e);
        }
    }
    
  void removeStoreEntryAction() {
        StyleStoreEntry entry = getSelectedEntry();
        if (entry == null) return;
        String nodeName = entry.getName();
        IStyleStore store = getSelectedStore();
        store.removeStyle(nodeName);
        
        // Now rearrange the Store Node
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        int index = findInNode(root, store.getStoreName());
        DefaultMutableTreeNode storeNode = (DefaultMutableTreeNode) root.getChildAt(index);
        index = findInNode(storeNode, nodeName);
        if (index < 0) return;
        storeNode.remove(index);
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(storeNode);
    }
    
    void moveStoreEntryUpAction() {
        StyleStoreEntry entry = getSelectedEntry();
        if (entry == null) return;
        String nodeName = entry.getName();
        IStyleStore store = getSelectedStore();
        store.moveUp(nodeName);
        
        // Now rearrange the Store Node
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        int index = findInNode(root, store.getStoreName());
        DefaultMutableTreeNode storeNode = (DefaultMutableTreeNode) root.getChildAt(index);
        index = findInNode(storeNode, nodeName);
        if (index <= 0) return;
        DefaultMutableTreeNode entryNode = (DefaultMutableTreeNode) storeNode.getChildAt(index);
        storeNode.remove(index);
        storeNode.insert(entryNode, index-1);
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(storeNode);
    }
    
    void moveStoreEntryDownAction() {
        StyleStoreEntry entry = getSelectedEntry();
        if (entry == null) return;
        String nodeName = entry.getName();
        IStyleStore store = getSelectedStore();
        store.moveDown(nodeName);
        
        // Now rearrange the Store Node
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        int index = findInNode(root, store.getStoreName());
        DefaultMutableTreeNode storeNode = (DefaultMutableTreeNode) root.getChildAt(index);
        index = findInNode(storeNode, nodeName);
        if (index >= (storeNode.getChildCount()-1)) return;
        DefaultMutableTreeNode entryNode = (DefaultMutableTreeNode) storeNode.getChildAt(index);
        storeNode.remove(index);
        storeNode.insert(entryNode, index+1);
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(storeNode);
    }
    
    void savePlotterStyleAction() {
        
    }
    
    void editStoreEntry() {
        StoreEntryNode node = null;
        Object obj = tree.getLastSelectedPathComponent();
        if (obj instanceof StoreEntryNode) node = (StoreEntryNode) obj;
        else return;
        
        showEntryPage(node);
    }
    
    void exitAction() {
        int n = tabbedPanel.getTabCount();
        for (int i=n-1; i>=0; i--) {
            Component comp = tabbedPanel.getComponentAt(i);
            if (comp instanceof StoreEntryEditorPanel) {
                ((StoreEntryEditorPanel) comp).close();
            }
            tabbedPanel.removeTabAt(i);
        }
        System.exit(0);
    }
    
    void doubleClickAction(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if(selRow == -1) return;
        StoreEntryNode node = null;
        Object obj = tree.getLastSelectedPathComponent();
        if (obj instanceof StoreEntryNode) node = (StoreEntryNode) obj;
        else return;
        
        showEntryPage(node);
    }
    
    
    void popupAction(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if(selRow == -1) return;
        tree.setSelectionPath(selPath);
        Object obj = tree.getLastSelectedPathComponent();
        enableMenus();
        if (obj instanceof StoreEntryNode) {
            entryMenu.show(tree, e.getX(), e.getY());
        } else  if (obj instanceof DefaultMutableTreeNode) {
            storeMenu.show(tree, e.getX(), e.getY());
        }
    }
    
    // can do enable/disable of menu elements here
    private void enableMenus() {
    }
    
    private void showEntryPage(StoreEntryNode node) {
        JComponent page = node.getPage();
        if (page == null) {
            page = new StoreEntryEditorPanel(node.getStoreEntry());
            node.setPage(page);
        }
        int index = tabbedPanel.indexOfComponent(page);
        if (index >= 0) tabbedPanel.setSelectedIndex(index);
        else {
            tabbedPanel.addTab(node.getName(), page);
            tabbedPanel.setSelectedComponent(page);
        }
    }
    
    private StyleStoreEntry getSelectedEntry() {
        StyleStoreEntry entry = null;
        Object obj = tree.getLastSelectedPathComponent();
        if (obj instanceof StoreEntryNode) {
            entry = ((StoreEntryNode) obj).getStoreEntry();
        }
        return entry;
    }
    
    private IStyleStore getSelectedStore() {
        IStyleStore store = null;
        Object node = tree.getLastSelectedPathComponent();
        if (node instanceof StoreEntryNode) {
            store = (IStyleStore) ((DefaultMutableTreeNode) ((StoreEntryNode) node).getParent()).getUserObject();
        } else  if (node instanceof DefaultMutableTreeNode) {
            Object obj = ((DefaultMutableTreeNode) node).getUserObject();
            if (obj instanceof IStyleStore) store = (IStyleStore) obj;
        }
        return store;
    }
    
    private int findInNode(DefaultMutableTreeNode node, String childName) {
        int index = -1;
        for (int i=0; i<node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            String tmpName = null;
            if (child instanceof StoreEntryNode) tmpName = ((StoreEntryNode) child).getName();
            else if (child != null) tmpName = child.toString();
            if (childName.equals(tmpName)) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public static void main(String[] args) {
        StyleStoreEditor editor = new StyleStoreEditor();
        
        JFrame frame = new JFrame("Style Store Editor");
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        editor.addMenus(menuBar);
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add("Center", editor);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 600);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation( (d.width-frame.getSize().width )/2, (d.height-frame.getSize().height )/2 );
        frame.setVisible(true);
    }
    
}
