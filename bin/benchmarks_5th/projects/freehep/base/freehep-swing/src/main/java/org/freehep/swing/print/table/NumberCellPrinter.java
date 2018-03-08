package org.freehep.swing.print.table;

import java.text.Format;
import java.text.NumberFormat;


/** A cell printer used for printing cells containing numeric values. */
public class NumberCellPrinter extends DefaultCellPrinter
{
   private static Format format = NumberFormat.getInstance();

   NumberCellPrinter()
   {
      super(ALIGN_RIGHT);
   }

   /** Prints the given object
    * @param o The object to print
    * @return The string to print.
    */   
   protected String format(Object o)
   {
      return format.format(o);
   }
}