package org.freehep.swing.print.table;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D.Float;

/** A default implementation of CellPrinter */

public class DefaultCellPrinter implements CellPrinter
{
   public static final int ALIGN_LEFT = 0;
   public static final int ALIGN_CENTER = 1;
   public static final int ALIGN_RIGHT = 2;
   protected String value;
   private Float rect;
   private Font font;
   private int align;

   DefaultCellPrinter(int align)
   {
      this.align = align;
   }

   public void setFont(Font font)
   {
      this.font = font;
   }

   public float getHeight(FontRenderContext frc)
   {
      if (rect == null)
      {
         rect = (Float) font.getStringBounds(value, frc);
      }
      return rect.height;
   }

   public void setValue(Object o)
   {
      value = format(o);
      rect = null;
   }

   public float getWidth(FontRenderContext frc)
   {
      if (rect == null)
      {
         rect = (Float) font.getStringBounds(value, frc);
      }
      return rect.width;
   }

   public void print(Graphics g, Float r)
   {
      if (value == null)
      {
         return;
      }

      Graphics2D g2 = (Graphics2D) g;
      FontRenderContext frc = g2.getFontRenderContext();
      if (rect == null)
      {
         rect = (Float) font.getStringBounds(value, frc);
      }

      float y = r.y - rect.y;
      float x = r.x;
      if (align == ALIGN_RIGHT)
      {
         x += (r.width - getWidth(frc));
      }
      else if (align == ALIGN_CENTER)
      {
         x += ((r.width - getWidth(frc)) / 2);
      }
      g2.setFont(font);
      g2.drawString(value, x, y);
   }

   protected String format(Object o)
   {
      return (o == null) ? "" : o.toString();
   }
}