package org.freehep.swing.print.table;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;

import javax.swing.table.TableModel;

/** Utility for printing tables. */
public class TablePrinter implements Pageable, Printable
{
   private Font cellFont;
   private Font headerFont;
   private FontRenderContext lastFontRenderContext;
   private PageFormat pf;
   private PrintableTableModel model;
   private float[] widths;
   private float headerHeight;
   private float rowHeight;
   private int nPages = UNKNOWN_NUMBER_OF_PAGES;
   private int rowsPerPage;

   /** Create a TablePrinter from a PrintableTableModel
    * @param model The table model to print
    * @param pf The page format to use for printing
    * @param headerFont The font to be used for the table header
    * @param cellFont The font to be used for the cells in the body of the table.
    * @param frc The font render context for the printing device
    */   
   public TablePrinter(PrintableTableModel model, PageFormat pf, Font headerFont, Font cellFont, FontRenderContext frc)
   {
      this.pf = pf;
      this.model = model;
      this.headerFont = headerFont;
      this.cellFont = cellFont;
      calculateTableSize(pf, frc);
   }
   /** Create a TablePrinter from a TableModel
    * @param model The table model to print
    * @param title The title to use for the print job
    * @param pf The page format to use for printing
    * @param headerFont The font to be used for the table header
    * @param cellFont The font to be used for the cells in the body of the table.
    * @param frc The font render context for the printing device
    */  
   public TablePrinter(TableModel model, String title, PageFormat pf, Font headerFont, Font cellFont, FontRenderContext frc)
   {
      this(new PrintModelAdapter(model, title),pf,headerFont,cellFont,frc);
   }  

   public int getNumberOfPages()
   {
      return nPages;
   }

   public PageFormat getPageFormat(int p1)
   {
      return pf;
   }

   public Printable getPrintable(int p1)
   {
      return this;
   }

   public int print(Graphics g, PageFormat pf, int page)
   {
      Graphics2D g2 = (Graphics2D) g;
      FontRenderContext frc = g2.getFontRenderContext();
      if (!frc.equals(lastFontRenderContext))
      {
         calculateTableSize(pf, frc);
      }

      if (page >= nPages)
      {
         return NO_SUCH_PAGE;
      }

      g2.setColor(Color.black);
      g2.setStroke(new BasicStroke(0));

      float xx = (float) pf.getImageableX();
      float yy = (float) pf.getImageableY();

      float totalWidth = 4;
      for (int i = 0; i < widths.length; i++)
      {
         if (widths[i] > 0)
         {
            totalWidth += (widths[i] + 3);
         }
      }

      float y = 4 + xx;
      float x = 4 + yy;
      Float rect = new Float();
      for (int c = 0; c < model.numberOfColumns(); c++)
      {
         if (widths[c] == 0)
         {
            continue;
         }
         rect.setRect(x, y, widths[c], headerHeight);

         CellPrinter cp = model.getHeaderPrinter(c);
         cp.setFont(headerFont);
         cp.setValue(model.headerForColumn(c));
         cp.print(g, rect);
         x += (widths[c] + 3);
      }
      y += (headerHeight + 1);

      Line2D line = new java.awt.geom.Line2D.Float();
      for (int r = page * rowsPerPage; r < ((page + 1) * rowsPerPage); r++)
      {
         if (r >= model.numberOfRows())
         {
            break;
         }

         x = 4 + xx;
         for (int c = 0; c < model.numberOfColumns(); c++)
         {
            if (widths[c] == 0)
            {
               continue;
            }
            rect.setRect(x, y, widths[c], rowHeight);

            CellPrinter cp = model.getCellPrinter(c);
            cp.setFont(cellFont);
            cp.setValue(model.valueAt(r, c));
            cp.print(g, rect);
            x += (widths[c] + 3);
         }
         line.setLine(xx, y - 1, xx + totalWidth, y - 1);
         g2.draw(line);
         y += (rowHeight + 1);
      }

      x = xx + 2;
      for (int c = 0; c < (model.numberOfColumns() - 1); c++)
      {
         if (widths[c] == 0)
         {
            continue;
         }
         x += (widths[c] + 3);
         line.setLine(x, yy + 1, x, y);
         g2.draw(line);
      }

      g2.setStroke(new BasicStroke(2));
      rect.setRect(xx + 1, yy + 2, totalWidth, y - yy);
      g2.draw(rect);

      String footer = "Page " + (page + 1) + " of " + nPages; // +"   header="+headerFont+" cell="+cellFont;
      g2.drawString(footer, xx, (yy + (float) pf.getImageableHeight()) - g.getFontMetrics().getDescent());

      return PAGE_EXISTS;
   }

   private void calculateTableSize(PageFormat pf, FontRenderContext frc)
   {
      lastFontRenderContext = frc;

      double height = pf.getImageableHeight();
      
      widths = new float[model.numberOfColumns()];
      headerHeight = 0;
      rowHeight = 0;

      for (int c = 0; c < model.numberOfColumns(); c++)
      {
         if (model.hideColumn(c))
         {
            continue;
         }

         CellPrinter hp = model.getHeaderPrinter(c);
         hp.setFont(headerFont);
         hp.setValue(model.headerForColumn(c));
         widths[c] = hp.getWidth(frc);

         float h = hp.getHeight(frc);
         if (h > headerHeight)
         {
            headerHeight = h;
         }

         CellPrinter cp = model.getCellPrinter(c);
         cp.setFont(cellFont);
         for (int r = 0; r < model.numberOfRows(); r++)
         {
            cp.setValue(model.valueAt(r, c));

            float w = cp.getWidth(frc);
            if (w > widths[c])
            {
               widths[c] = w;
            }
            h = cp.getHeight(frc);
            if (h > rowHeight)
            {
               rowHeight = h;
            }
         }
      }

      int nRows = model.numberOfRows();

      double effectiveHeight = height - headerHeight - 6 - rowHeight; // leave room for header
      rowsPerPage = (int) Math.floor(effectiveHeight / (rowHeight + 1));

      nPages = 1 + ((nRows - 1) / rowsPerPage);
   }
}