package org.freehep.swing.treetable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;


/**
 * This is a wrapper class takes a TreeTableModel and implements
 * the table model interface. The implementation is trivial, with
 * all of the event dispatching support provided by the superclass:
 * the AbstractTableModel.
 *
 * @author Philip Milne
 * @author Scott Violet
 * @version $id$
 */
public class TreeTableModelAdapter extends AbstractTableModel
{
   JTree tree;
   TreeTableModel treeTableModel;

   public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree)
   {
      this.tree = tree;
      this.treeTableModel = treeTableModel;

      tree.addTreeExpansionListener(new TreeExpansionListener()
      {
         // Don't use fireTableRowsInserted() here; the selection model
         // would get updated twice. 
         public void treeExpanded(TreeExpansionEvent event)
         {
            fireTableDataChanged();
         }

         public void treeCollapsed(TreeExpansionEvent event)
         {
            fireTableDataChanged();
         }
      });


      // Install a TreeModelListener that can update the table when
      // tree changes. We use delayedFireTableDataChanged as we can
      // not be guaranteed the tree will have finished processing
      // the event before us.
      treeTableModel.addTreeModelListener(new TreeModelListener()
      {
         public void treeNodesChanged(TreeModelEvent e)
         {
            delayedFireTableDataChanged();
         }

         public void treeNodesInserted(TreeModelEvent e)
         {
            delayedFireTableDataChanged();
         }

         public void treeNodesRemoved(TreeModelEvent e)
         {
            delayedFireTableDataChanged();
         }

         public void treeStructureChanged(TreeModelEvent e)
         {
            delayedFireTableDataChanged();
         }
      });
   }

   public boolean isCellEditable(int row, int column)
   {
      return treeTableModel.isCellEditable(pathForRow(row), column);
   }

   public Class getColumnClass(int column)
   {
      return treeTableModel.getColumnClass(column);
   }

   // Wrappers, implementing TableModel interface. 
   public int getColumnCount()
   {
      return treeTableModel.getColumnCount();
   }

   public String getColumnName(int column)
   {
      return treeTableModel.getColumnName(column);
   }

   public int getRowCount()
   {
      return tree.getRowCount();
   }

   public void setValueAt(Object value, int row, int column)
   {
      treeTableModel.setValueAt(value, pathForRow(row), column);
   }

   public Object getValueAt(int row, int column)
   {
      return treeTableModel.getValueAt(pathForRow(row), column);
   }

   protected int getRowForLocation(int x, int y)
   {
      return tree.getRowForLocation(x, y);
   }

   /**
    * Invokes fireTableDataChanged after all the pending events have been
    * processed. SwingUtilities.invokeLater is used to handle this.
    */
   protected void delayedFireTableDataChanged()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            fireTableDataChanged();
         }
      });
   }

   protected Object nodeForRow(int row)
   {
      TreePath treePath = tree.getPathForRow(row);
      if (treePath == null)
      {
         System.err.println("WARNING: nodeForRow returning null for " + row);
         return null;
      }
      return treePath.getLastPathComponent();
   }

   protected TreePath pathForRow(int row)
   {
      TreePath treePath = tree.getPathForRow(row);
      if (treePath == null)
      {
         System.err.println("WARNING: pathForRow returning null for " + row);
         return null;
      }
      return treePath;
   }
}