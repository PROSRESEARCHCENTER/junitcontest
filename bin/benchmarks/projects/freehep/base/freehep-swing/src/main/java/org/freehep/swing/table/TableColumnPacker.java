package org.freehep.swing.table;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * @author Tony Johnson
 * @version $Id: TableColumnPacker.java 8584 2006-08-10 23:06:37Z duns $
 */
public class TableColumnPacker
{
   private final static int DEFAULT_MARGIN = 2;
   private final static int DEFAULT_MAXSCAN = 50;
   
   private int margin = DEFAULT_MARGIN;
   private int headerMargin = -1;
   private int maxscan = DEFAULT_MAXSCAN;
   
   /**
    * Sets the preferred with of all columns to be just large enough to
    * contain the widest entry in the column. 
    * 
    * @param table The table to pack
    */
   public void packColumns(JTable table)
   {
      TableColumnModel colModel = table.getColumnModel();

      int totalPreferredWidth = 0; 
      List columns = new ArrayList(table.getColumnCount());     
      for (int c=0; c<table.getColumnCount(); c++)
      {
         TableColumn col = colModel.getColumn(c);
         columns.add(col);
         int width = preferredWidth(table, col, c);
         totalPreferredWidth += width;
         col.setPreferredWidth(width);
      }
      
        // in case we do not want a Horizontal Scrollbar and the columns are wider than the available space.
        if (table.getAutoResizeMode() != JTable.AUTO_RESIZE_OFF) {
            int width = table.getWidth();
            if ((width > 0) && (totalPreferredWidth > table.getWidth())) {
                setWidth(columns, table.getWidth());
            }
        }
   }
    
    // Tries to distribute the widths in the given columns. Each column
    // gets its preferredWidth if that would fit. The remaining space is
    // again distributed over the remaining columns, using this method
    // recursively. If no columns have preferredWidths below the 
    // calculated column width, the remaining width is just divided
    // over the remaining columns and the recursion ends.
    private void setWidth(List/*<TableColumn>*/ columns, int width) {
        List remainingColumns = new ArrayList();
        int colSize = width / columns.size();
        for (Iterator i=columns.iterator(); i.hasNext(); ) {
            TableColumn col = (TableColumn)i.next();
            int prefWidth = col.getPreferredWidth();
            if (prefWidth < colSize) {
                // pref width is ok
                width -= prefWidth;
            } else {
                // pref widh too large in this iteration
                remainingColumns.add(col);
            }
        }
        if (remainingColumns.size() <= 0) return;
        
        if (remainingColumns.size() < columns.size()) {
            // fewer columns
            setWidth(remainingColumns, width);
        } else {
            // all columns wider than colSize
            for (Iterator i = remainingColumns.iterator(); i.hasNext(); ) {
                TableColumn col = (TableColumn)i.next();
                col.setPreferredWidth(colSize);
            }
        }
    }
   
   // Sets the preferred width of the visible column specified by vColIndex. The column
   // will be just wide enough to show the column head and the widest cell in the column.
   // margin pixels are added to the left and right
   // (resulting in an additional width of 2*margin pixels).
   private int preferredWidth(JTable table, TableColumn col, int vColIndex)
   {
      int width = 0;
      
      // Get maximum width of column data
      // To save time we dont scan all rows, instead we scan the first and last MAXSCAN rows
      int rowCount = table.getRowCount();
      int stop = maxscan > 0 ? Math.min(maxscan,rowCount) : 0;
      int start = maxscan > 0 ? Math.max(rowCount-maxscan,stop) : rowCount;
      
      for (int r=0; r<stop; r++)
      {
         TableCellRenderer renderer = table.getCellRenderer(r, vColIndex);
         Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
         width = Math.max(width, comp.getPreferredSize().width);
      }
      for (int r=start; r<rowCount; r++)
      {
         TableCellRenderer renderer = table.getCellRenderer(r, vColIndex);
         Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
         width = Math.max(width, comp.getPreferredSize().width);
      }      
      // Add margin
      width += 2*margin;
      
      // Get width of column header
      TableCellRenderer renderer = col.getHeaderRenderer();
      if (renderer == null)
      {
         renderer = table.getTableHeader().getDefaultRenderer();
      }
      int m = headerMargin < 0 ? margin : headerMargin;
      Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
      width = Math.max(width, comp.getPreferredSize().width + 2*m);
      return width;
   } 
   /**
    * @return Returns the headerMargin.
    */
   public int getHeaderMargin()
   {
      return headerMargin;
   }
   /**
    * The margin to use for the header.
    * @param headerMargin The headerMargin to set.
    */
   public void setHeaderMargin(int headerMargin)
   {
      this.headerMargin = headerMargin;
   }
   /**
    * @return Returns the margin.
    */
   public int getMargin()
   {
      return margin;
   }
   /**
    * A margin on the left and right of the column.
    * @param margin The margin to set.
    */
   public void setMargin(int margin)
   {
      this.margin = margin;
   }
   /**

    * @return Returns the maxscan,
    */
   public int getMaxscan()
   {
      return maxscan;
   }
   /**
    * If maxscan is set (default 50) then only
    * the first and last maxscan rows are measured.
    * @param maxscan The maxscan to set, or 0 to clear maxscan.
    */
   public void setMaxscan(int maxscan)
   {
      this.maxscan = maxscan;
   }
}
