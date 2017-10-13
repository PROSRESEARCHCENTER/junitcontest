package org.freehep.swing.print.table;

/** Used to provide table data for printing */
public interface PrintableTableModel
{
   /** Get the cell printer to be used for the table body
    * @return The cell printer for this column
    * @param column The column index
    */   
   CellPrinter getCellPrinter(int column);

   /** Get the cell printer to be used for the table header
    * @return The cell printer to use for this column's header
    * @param column The column index
    */   
   CellPrinter getHeaderPrinter(int column);

   /** Get the title for the document
    * @return The title.
    */   
   String getTitle();

   /** Get the header for a column
    * @return The header text for this column
    * @param i The column index.
    */   
   Object headerForColumn(int i);

   /** Allows some columns to be skipped when printing
    * @return <CODE>true</CODE> if this column should be skipped
    * @param i The column index
    */   
   boolean hideColumn(int i);

   /** Total number of columns (including hidden columns)
    * @return The number of columns
    */   
   int numberOfColumns();

   /** Total number of rows
    * @return The number of rows
    */   
   int numberOfRows();

   /** Get the data to be printed in a certain cell.
    * @return The object representing the data at the referenced cell
    * @param i The column index
    * @param j The row index
    */   
   Object valueAt(int i, int j);
}