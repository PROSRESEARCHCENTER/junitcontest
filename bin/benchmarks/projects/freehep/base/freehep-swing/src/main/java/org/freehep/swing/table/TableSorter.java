// Copyright 2004, FreeHEP.
package org.freehep.swing.table;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import org.freehep.swing.table.TableColumnHeaderListener;
import org.freehep.swing.table.TableHeaderCellRenderer;

/**
 * Utility to add a sorter for columns to a JTable which has a SortableTableModel.
 * <p>
 * Example of use:
 * <pre>
 *  TableModel model = ...
 *  DefaultSortableTableModel sm = new DefaultSortableTableModel(model);
 *  JTable table = new JTable(sm);
 *  TableSorter sorter = new TableSorter(table);
 * </pre>
 * @author Mark Donszelmann
 * @author Tony Johnson
 * @version $Id: TableSorter.java 10766 2007-06-02 17:29:04Z tonyj $
 * @see org.freehep.swing.table.SortableTableModel
 */
public class TableSorter
{
   private static Icon downTriangle = new Triangle(false);
   private static Icon upTriangle = new Triangle(true);
   
   private SortableTableModel model;
   private JTableHeader header;
   
   /**
    * Create a TableSorter. The table will initially be unsorted.
    * @param table The table to be sorted
    */
   public TableSorter(JTable table)
   {
      table.addPropertyChangeListener("model",new ModelChangeListener());
      modelChanged(table.getModel());
      header = table.getTableHeader();
      header.addMouseListener(new HeaderListener());
      header.setDefaultRenderer(new HeaderRenderer());
   }
   
   /**
    * Create a TableSorter. The table will initially be sorted in ascending order by the given column.
    * @param table The table to be sorted.
    * @param column The column on which to sort, or <CODE>SortableTableModel.UNSORTED</CODE>
    */
   public TableSorter(JTable table, int column)
   {
      this(table, column, true);
   }
   
   /**
    * Create a TableSorter specifiying initial sorting parameters.
    * @param table The table to be sorted.
    * @param column The column on which to sort, or <CODE>SortableTableModel.UNSORTED</CODE>
    * @param ascending <CODE>true</CODE> for ascending order, <CODE>false</CODE> for descending order
    */
   public TableSorter(JTable table, int column, boolean ascending)
   {
      this(table);
      sort(column,ascending);
   }
   
   /**
    * Find the current sort column.
    * @return The current sort column, or <CODE>SortableTableModel.UNSORTED</CODE>
    */
   public int getSortOnColumn()
   {
      return model == null ? SortableTableModel.UNSORTED : model.getSortOnColumn();
   }
   
   /**
    * Set the sort column.
    * @param column The column on which to sort, or <CODE>SortableTableModel.UNSORTED</CODE>
    */
   public void setSortOnColumn(int column)
   {
      if (model != null) model.sort(column,true);
   }
   
   /**
    * Get the current sort order.
    * @return <CODE>true</CODE> if ascending order, <CODE>false</CODE> for descending order.
    */
   public boolean isSortAscending()
   {
      return model == null ? true : model.isSortAscending();
   }
   
   /**
    * Set the current sort order.
    * @param ascending <CODE>true</CODE> for ascending order, <CODE>false</CODE> for descending order
    */
   public void setSortAscending(boolean ascending)
   {
      if (model != null) model.sort(model.getSortOnColumn(),ascending);
   }
   
   private void sort(int sortOnColumn, boolean sortAscending)
   {
      if (model != null)
      {
         model.sort(sortOnColumn, sortAscending);
         header.resizeAndRepaint();
      }
   }
   private void modelChanged(TableModel model)
   {
      this.model = model instanceof SortableTableModel ? (SortableTableModel) model : null;
   }
   private class ModelChangeListener implements PropertyChangeListener
   {
      public void propertyChange(PropertyChangeEvent evt)
      {
         modelChanged((TableModel) evt.getNewValue());
      }
   }
   private class HeaderListener extends TableColumnHeaderListener
   {
      
      public void headerClicked(JTable table, int col)
      {
         if (model != null)
         {
            if (col != model.getSortOnColumn())
            {
               sort(col, true);
            }
            else
            {
               sort(model.getSortOnColumn(), !model.isSortAscending());
            }
         }
      }
   }
   
   private class HeaderRenderer extends TableHeaderCellRenderer
   {
      
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
      {
         JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
         if (model != null && table.convertColumnIndexToModel(col) == model.getSortOnColumn())
         {
            label.setIcon(model.isSortAscending() ? downTriangle : upTriangle);
         }
         else
         {
            label.setIcon(null);
         }
         return label;
      }
   }
   private static class Triangle implements Icon
   {
      private boolean up;
      private static final int size = 16;
      private static final int[] xxdown = { 3 , 12, 7 };
      private static final int[] yydown = { 5 , 5, 10 };
      private static final int[] xxup = { 2 , 12, 7 };
      private static final int[] yyup = { 10 , 10, 4 };
      Triangle(boolean up)
      {
         this.up = up;
      }
      
      public int getIconHeight()
      {
         return size;
      }
      
      public int getIconWidth()
      {
         return size;
      }
      
      public void paintIcon(Component c, Graphics g, int x, int y)
      {
         int[] xp = new int[3];
         int[] yp = new int[3];
         for (int i=0; i<3; i++)
         {
            xp[i] = x + (up ? xxup[i] : xxdown[i]);
            yp[i] = y + (up ? yyup[i] : yydown[i]);
         }
         g.setColor(c.getForeground());
         g.fillPolygon(xp,yp,3);
      }
   }
}
