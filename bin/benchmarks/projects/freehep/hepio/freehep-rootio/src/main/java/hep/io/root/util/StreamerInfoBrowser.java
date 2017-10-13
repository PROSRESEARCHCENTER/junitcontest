package hep.io.root.util;

import hep.io.root.RootObject;
import hep.io.root.interfaces.TKey;
import hep.io.root.interfaces.TNamed;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


/**
 * A panel for browsing Streamer Info from a Root file.
 * @author tonyj
 * @version $Id: StreamerInfoBrowser.java 13617 2009-04-09 22:48:46Z tonyj $
 */
class StreamerInfoBrowser extends JPanel implements TreeSelectionListener
{
   private final static TreeModel emptyTree = null;
   private JTree objTree;
   private JTree tree;

   /** Creates new StreamerInfoBrowser */
   public StreamerInfoBrowser(List streamerInfo)
   {
      super(new BorderLayout());
      tree = new JTree(new StreamerInfoTreeModel(streamerInfo));
      tree.setCellRenderer(new StreamerInfoTreeCellRenderer());
      tree.addTreeSelectionListener(this);
      tree.setRootVisible(false);
      tree.setShowsRootHandles(true);

      objTree = new JTree(emptyTree);
      objTree.setCellRenderer(new RootObjectTreeCellRenderer());
      ToolTipManager.sharedInstance().registerComponent(objTree);

      JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JScrollPane(objTree));
      add(split, BorderLayout.CENTER);

      setPreferredSize(new java.awt.Dimension(500, 300));
      split.setDividerLocation(245);

      ToolTipManager.sharedInstance().setEnabled(true);
   }

   public void valueChanged(TreeSelectionEvent event)
   {
      Cursor old = getCursor();
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try
      {
         TreePath path = tree.getSelectionPath();
         if (path == null)
            objTree.setModel(emptyTree);
         else
         {
            Object node = path.getLastPathComponent();

            //if (node instanceof BranchEntry) node = ((BranchEntry) node).getValue();
            if (node instanceof TKey)
            {
               TKey key = (TKey) node;
               objTree.setModel(new RootObjectTreeModel(key.getObject(), key.getName()));
            }
            else if (node instanceof TNamed)
               objTree.setModel(new RootObjectTreeModel(node, ((TNamed) node).getName()));
            else if (node instanceof RootObject)
               objTree.setModel(new RootObjectTreeModel(node, ""));
            else if (node.getClass().isArray())
               objTree.setModel(new RootObjectTreeModel(node, ""));
            else if (node instanceof java.util.List)
               objTree.setModel(new RootObjectTreeModel(node, ""));
            else
               objTree.setModel(emptyTree);
         }
      }
      catch (Throwable x)
      {
         objTree.setModel(emptyTree);
         x.printStackTrace();
         error(x);
      }
      finally
      {
         setCursor(old);
      }
   }

   private void error(Throwable x)
   {
      JOptionPane.showMessageDialog(this, x.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
   }
}
