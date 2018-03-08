package org.freehep.swing.treetable;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


/**
 * TreeTableModel is the model used by a JTreeTable. It extends TreeModel
 * to add methods for getting inforamtion about the set of columns each
 * node in the TreeTableModel may have. Each column, like a column in
 * a TableModel, has a name and a type associated with it. Each node in
 * the TreeTableModel can return a value for each of the columns and
 * set that value if isCellEditable() returns true.
 *
 * @author Philip Milne
 * @author Scott Violet
 * @version $Id: TreeTableModel.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface TreeTableModel extends TreeModel
{
   /**
    * Indicates whether the the value for node <code>node</code>,
    * at column number <code>column</code> is editable.
    */
   public boolean isCellEditable(TreePath path, int column);

   /**
    * Returns the type for column number <code>column</code>.
    */
   public Class getColumnClass(int column);

   /**
    * Returns the number ofs availible column.
    */
   public int getColumnCount();

   /**
    * Returns the name for column number <code>column</code>.
    */
   public String getColumnName(int column);

   /**
    * Sets the value for node <code>node</code>,
    * at column number <code>column</code>.
    */
   public void setValueAt(Object aValue, TreePath path, int column);

   /**
    * Returns the value to be displayed for node <code>node</code>,
    * at column number <code>column</code>.
    */
   public Object getValueAt(TreePath path, int column);
}