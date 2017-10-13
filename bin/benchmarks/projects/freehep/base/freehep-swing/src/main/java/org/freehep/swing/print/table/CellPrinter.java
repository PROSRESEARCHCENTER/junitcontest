package org.freehep.swing.print.table;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D.Float;


/** An interface to be implemented by cell printers */
public interface CellPrinter
{
   /** Sets the font to be used
    * @param fm The font to be used
    */   
   void setFont(Font fm);

   /** The height of one column
    * @return The hieght of each column of the table
    * @param frc The font render context to be used for calculating font sizes.
    */   
   float getHeight(FontRenderContext frc);

   /** Sets the value of the cell to print
    * @param o The object to be printed
    */   
   void setValue(Object o);

   /** Gets the width of the current current value.
    * @return The width needed to print the current value.
    * @param frc The font render context to be used.
    */   
   float getWidth(FontRenderContext frc);

   /** Print this cell
    * @param g The graphics into which to print
    * @param r
    */   
   void print(Graphics g, Float r);
}