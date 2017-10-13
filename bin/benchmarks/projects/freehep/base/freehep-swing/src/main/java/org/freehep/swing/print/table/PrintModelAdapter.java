package org.freehep.swing.print.table;

import javax.swing.table.TableModel;

/**
 * Converts a TableModel to a PrintableTableModel
 * @author tonyj
 * @version $Id: PrintModelAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PrintModelAdapter implements PrintableTableModel
{
   private TableModel model;
   private String title;
   private CellPrinter headerPrinter = new DefaultCellPrinter(DefaultCellPrinter.ALIGN_CENTER);
   private CellPrinter textPrinter = new DefaultCellPrinter(DefaultCellPrinter.ALIGN_LEFT);
   private CellPrinter numberPrinter = new NumberCellPrinter();
   /** Creates a new instance of PrintModelAdapter
    * @param model The table model to convert
    * @param title The title for the generater PrintableTableModel
    */
   public PrintModelAdapter(TableModel model, String title)
   {
      this.model = model;
      this.title = title;
   }
   
   public CellPrinter getCellPrinter(int column)
   {
      return Number.class.isAssignableFrom(model.getColumnClass(column)) ? numberPrinter : textPrinter;
   }
   
   public CellPrinter getHeaderPrinter(int column)
   {
      return headerPrinter;
   }
   
   public String getTitle()
   {
      return title;
   }
   
   public Object headerForColumn(int i)
   {
      return model.getColumnName(i);
   }
   
   public boolean hideColumn(int i)
   {
      return false;
   }
   
   public int numberOfColumns()
   {
      return model.getColumnCount();
   }
   
   public int numberOfRows()
   {
      return model.getRowCount();
   }
   
   public Object valueAt(int i, int j)
   {
      return model.getValueAt(i,j);
   }
}
