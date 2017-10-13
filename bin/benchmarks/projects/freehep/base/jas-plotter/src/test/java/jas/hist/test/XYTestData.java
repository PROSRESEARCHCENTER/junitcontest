/*
 * XYTestData.java
 *
 * Created on November 21, 2001, 3:22 PM
 */

package jas.hist.test;
import jas.hist.HasStyle;
import jas.hist.JASHist;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistStyle;
import jas.hist.XYDataSource;

import javax.swing.JFrame;

/**
 * 
 * @author tonyj
 */
public class XYTestData implements XYDataSource, HasStyle
{
   private double[] x;
   private double[] y;
   /** Creates new XYTestData */
   public XYTestData()
   {
      int n = 100;
      x = new double[n];
      y = new double[n];
      for (int i=0; i<n; i++)
      {
         //x[i] = Math.random() * 100;
         //y[i] = Math.random() * 100;
         x[i] = y[i] = ((double) i)/n;
      }
   }
   /**
    * Returns one of DOUBLE or DATE
    */
   public int getAxisType()
   {
      return DOUBLE;
   }
   /**
    * Return the caption to be used in the legend for this data.
    */
   public String getTitle()
   {
      return "Test XY Data";
   }
   public double getMinusError(int index)
   {
      return Math.sqrt(x[index]);
   }
   public double getPlusError(int index)
   {
      return Math.sqrt(x[index]);
   }
   public double getX(int index)
   {
      return x[index];
   }
   public double getY(int index)
   {
      return y[index];
   }
   public int getNPoints()
   {
      return x.length;
   }
   public JASHistStyle getStyle()
   {
      JASHist1DHistogramStyle style = new JASHist1DHistogramStyle();
      style.setShowHistogramBars(false);
      style.setShowDataPoints(true);
      style.setShowLinesBetweenPoints(true);
      style.setShowErrorBars(false);
      return style;
   }
   public static void main(String[] args)
   {
      JFrame frame = new JFrame("XY Test");
      JASHist plot = new JASHist();
      plot.addData(new XYTestData()).show(true);
      frame.getContentPane().add(plot);
      frame.setSize(400,400);
      frame.show();
   }   
}
