package jas.hist;

import java.awt.Color;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class JASHist1DFunctionStyle extends JASHistStyle implements Externalizable
{
   static final Color[] lineColors =
   {
      Color.blue, Color.red, Color.darkGray,Color.magenta,
      Color.yellow, Color.green, Color.orange, Color.cyan,
   };
   static int n = 0;
   static final long serialVersionUID = -3911970150059917139L;
   
   public JASHist1DFunctionStyle()
   {
      lineColor = lineColors[n];
      n++;
      if (n == lineColors.length) n = 0;
   }
   
   public void writeExternal(ObjectOutput p) throws IOException
   {
      p.writeObject(lineColor);
   }
   public void readExternal(ObjectInput p) throws IOException, ClassNotFoundException
   {
      lineColor = (Color) p.readObject();
   }
   public Color getLineColor()
   {
      return lineColor;
   }
   
   public void setLineColor(Color nNewValue)
   {
      lineColor = nNewValue;
      changeNotify();
   }
   public int getLineStyle()
   {
      return lineStyle;
   }
   public void setLineStyle(int style)
   {
      lineStyle = style;
      changeNotify();
   }
   public float getLineWidth()
   {
      return lineWidth != 0 ? lineWidth : (float)0.0001;
   }
   public void setLineWidth(float width)
   {
      lineWidth = width;
      changeNotify();
   }  
   private Color lineColor;
   private float lineWidth;
   private int lineStyle;
}