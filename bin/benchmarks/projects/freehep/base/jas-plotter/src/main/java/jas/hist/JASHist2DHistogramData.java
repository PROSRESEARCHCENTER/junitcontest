package jas.hist;

import jas.plot.Overlay;
import jas.util.xml.HasXMLRepresentation;

import java.util.Observable;

/**
 * JASHist2DHistogramData is used for data which can be viewed as a 2D plot
 * but NOT as a ScatterPlot. The subclass JASHistScatterPlotData is used
 * for data which can also be displayed as a ScatterPlot.
 */
class JASHist2DHistogramData extends JASHistData
{
   JASHist2DHistogramData(DataManager dm,Rebinnable2DHistogramData ds)
   {
      super(dm);
      dataSource = ds;
      initTransientData();
      
      JASHistStyle s = null;
      if (ds instanceof HasStyle) s = ((HasStyle) ds).getStyle();
      if (s == null) s = createStyle();
      setStyle(s);
      String property = System.getProperty("hurry", "false");
      hurry = property != null && property.equalsIgnoreCase("true");
   }
   JASHistStyle createStyle()
   {
      return new JASHist2DHistogramStyle();
   }
   private void initTransientData()
   {
      zLimitsValid = false;
      isBinned = false;
   }
   public void setStyle(JASHistStyle style)
   {
      if (!(style instanceof JASHist2DHistogramStyle))
         throw new IllegalArgumentException("Style is not subclass of JASHist2DHistogramStyle");
      if (this.style != null) this.style.deleteObserver(this);
      this.style = (JASHist2DHistogramStyle) style;
      this.style.addObserver(this);
   }
   public String getTitle()
   {
      return dataSource.getTitle();
   }
   Overlay createOverlay()
   {
      return new TwoDOverlay(this);
   }
   void writeAsXML(XMLPrintWriter pw, boolean snapshot)
   {
      String theXAxisType = pw.convertAxisTypeToString(dataSource.getXAxisType());
      String theYAxisType = pw.convertAxisTypeToString(dataSource.getYAxisType());
      pw.setAttribute("type","histogram2d");
      pw.openTag("data2d");
      
      if (snapshot)
      {
         double[][][] result = dataSource.rebin(xBins,xLow,xHigh,yBins,yLow,yHigh,true,hurry,
         style.getShowOverflow());
         if (result == null) result = new double[1][xBins][yBins];
         double[][] data = result[0];
         
         pw.setAttribute("title",getTitle());
         pw.setAttribute("xSize",data.length);
         pw.setAttribute("ySize",data[0].length);
         pw.openTag("bins2d");
         
         
         double[][] positiveError = new double[0][0];
         double[][] negativeError = new double[0][0];
         boolean havePlusError = (result.length > 1);
         boolean haveMinusError = (result.length > 2);
         if (havePlusError)
         {
            positiveError = result[1];
            if (haveMinusError)
            {
               negativeError = result[2];
            }
         }
         for (int i=0; i < data.length; i++)
         {
            for (int j=0; j < data[i].length; j++)
            {
               pw.print(data[i][j]);
               if (havePlusError && positiveError.length > i && positiveError[i].length > j)
               {
                  pw.print("," + positiveError[i][j]);
                  if (haveMinusError && negativeError.length > i && negativeError[i].length > j)
                  {
                     pw.println("," + negativeError[i][j]);
                  } else
                  {
                     pw.println();
                  }
               } else
               {
                  pw.println();
               }
            }
         }
         pw.closeTag();
         
         //output the x axis attributes
         pw.printBinnedDataAxisAttributes(
         "x", "" + getXMin(), "" + getXMax(),
         "" + dataSource.getXBins(), theXAxisType);
         
         //output the y axis attributes
         pw.printBinnedDataAxisAttributes(
         "y", "" + getYMin(), "" + getYMax(),
         "" + dataSource.getYBins(), theYAxisType);
         
         if (dataSource instanceof HasStatistics)
         {
            Statistics stats = ((HasStatistics) dataSource).getStatistics();
            if (stats != null)
            {
               pw.openTag("statistics");
               String[] names = stats.getStatisticNames();
               for (int i=0; i<names.length; i++)
               {
                  String name = names[i];
                  pw.setAttribute("name",name);
                  String valueString = null;
                  if (stats instanceof ExtendedStatistics)
                  {
                     Object value = ((ExtendedStatistics) stats).getExtendedStatistic(name);
                     if (value != null) valueString = value.toString();
                  }
                  if (valueString == null) valueString = String.valueOf(stats.getStatistic(name));
                  pw.setAttribute("value",valueString);
                  pw.printTag("statistic");
               }
               pw.closeTag();
            }
         }
      }
      else // !snapshot
      {
         if (dataSource instanceof HasXMLRepresentation)
         {
            ((HasXMLRepresentation) dataSource).writeAsXML(pw);
         }
         else
         {
            if (dataSource instanceof HasDataSource) pw.setAttribute("name",dataSource.getClass().getName());
            else pw.setAttribute("name","???");
            pw.setAttribute("param","???");
            pw.printTag("class");
         }
      }
      
      
                /*
                //!PA - implement writing 2d string axis labels if and when we actually implement 2d string axes
                //		(and watch out for min and max undef bug on string labels)
                if (theAxisType.equals("string")) {
                        jas.hist.JASHistXMLUtils.writeTabs(pw, (indentLevel + 1));
                        pw.println("<axisLabels>");
                        String[] labels = getAxisLabels();
                        for (int i=0; i < labels.length; i++) {
                                jas.hist.JASHistXMLUtils.writeTabs(pw, (indentLevel + 2));
                                pw.println("<axisLabel value=\"" + labels[i] + "\">");
                        }
                        jas.hist.JASHistXMLUtils.writeTabs(pw, (indentLevel + 1));
                        pw.println("</axisLabels>");
                }
                 */
      String histStyleName = JASHist2DHistogramStyle.getHistStyleName(style.getHistStyle());
      pw.setAttribute("histStyle",histStyleName);
      if (histStyleName.equals("STYLE_COLORMAP"))
      {
         pw.setAttribute("colorMapScheme",
         JASHist2DHistogramStyle.getColorMapSchemeName(style.getColorMapScheme()));
      }
      pw.setAttribute("shapeColor",
      jas.util.ColorConverter.colorToString(style.getShapeColor()));
      pw.setAttribute("overflowBinColor",
      jas.util.ColorConverter.colorToString(style.getOverflowBinColor()));
      pw.setAttribute("startDataColor",
      jas.util.ColorConverter.colorToString(style.getStartDataColor()));
      pw.setAttribute("endDataColor",
      jas.util.ColorConverter.colorToString(style.getEndDataColor()));
      pw.setAttribute("showOverflow",style.getShowOverflow());
      pw.setAttribute("showPlot",style.getShowPlot());
      if (style.getLogZ()) pw.setAttribute("logZ",true);
      pw.printTag("style2d");
      
      pw.closeTag();
   }
   boolean isRebinnable()
   {
      return dataSource.isRebinnable();
   }
   double getXMin()
   {
      double result = dataSource.getXMin();
      if (style.getShowOverflow())
      {
         result -= (dataSource.getXMax()-result)/dataSource.getXBins();
      }
      return result;
   }
   double getXMax()
   {
      double result = dataSource.getXMax();
      if (style.getShowOverflow())
      {
         result += (result-dataSource.getXMin())/dataSource.getXBins();
      }
      return result;
   }
   double getYMin()
   {
      double result = dataSource.getYMin();
      if (style.getShowOverflow())
      {
         result -= (dataSource.getYMax()-result)/dataSource.getYBins();
      }
      return result;
   }
   double getYMax()
   {
      double result = dataSource.getYMax();
      if (style.getShowOverflow())
      {
         result += (result-dataSource.getYMin())/dataSource.getYBins();
      }
      return result;
   }
   
   int getXBins()
   {
      return dataSource.getXBins();
   }
   int getYBins()
   {
      return dataSource.getYBins();
   }
   
   void setXRange(int xBins,double xLow, double xHigh)
   {
      if (isRebinnable())
      {
         if (xBins != this.xBins || xLow != this.xLow || xHigh != this.xHigh)
         {
            this.xBins = xBins;
            
            isBinned = false;
            zLimitsValid = false;
         }
      }
      else
      {
         this.xBins = dataSource.getXBins();
      }
      this.xLow = xLow;
      this.xHigh = xHigh;
   }
   void setYRange(int yBins,double yLow, double yHigh)
   {
      if (isRebinnable())
      {
         if (yBins != this.yBins || yLow != this.yLow || yHigh != this.yHigh)
         {
            this.yBins = yBins;
            
            isBinned = false;
            zLimitsValid = false;
         }
      }
      else
      {
         this.yBins = dataSource.getYBins();
      }
      this.yLow = yLow;
      this.yHigh = yHigh;
   }
   private void doBin()
   {
      // no support for String axes yet
      isBinned = true; // Set before call to rebin to avoid race condition

      double xl, xh, yl, yh;
	  if (isRebinnable()) 
	  {
	      xl = xLow;
          xh = xHigh;
	      yl = yLow;
		  yh = yHigh;
	  }
	  else 
	  {
		  xl = ((Rebinnable2DHistogramData) dataSource).getXMin();
		  xh = ((Rebinnable2DHistogramData) dataSource).getXMax();
	      yl = ((Rebinnable2DHistogramData) dataSource).getYMin();
		  yh = ((Rebinnable2DHistogramData) dataSource).getYMax();
	   }
      
      double[][][] result = dataSource.rebin(xBins,xl,xh,yBins,yl,yh,true,hurry,style.getShowOverflow());
      if (result == null) result = new double[1][xBins][yBins];
      
      // apply normalization
      if (normalization != null)
      {
         double factor = 1./normalization.getNormalizationFactor();
         for (int k=0; k<result.length; k++)
         {
            double[][] yy = result[k];
            for (int j=0; j<yy.length; j++)
            {
               double[] y = yy[j];
               for (int i=0; i<y.length; i++) y[i] *= factor;
            }
         }
      }
      
      data = result[0];
      
      if (data.length != xBins)
         System.err.println("Warning xbins="+xBins+" data.length="+data.length);
      
      // todo: no handling for error bars yet
      
      if (overlay instanceof TwoDOverlay)
      {
         ((TwoDOverlay) overlay).setData(data,xl,xh,yl,yh, xBins, yBins);
      }
   }
   protected void calcZLimits()
   {
      if (!isBinned) doBin();
      zLimitsValid = true;
      double zLogMin;
      if (xBins == 0 || yBins == 0)
      {
         zMin = 0;
         zLogMin = Double.POSITIVE_INFINITY;
         zMax = 1;
      }
      else
      {
         zLogMin = Double.POSITIVE_INFINITY;
         zMin= Double.POSITIVE_INFINITY;
         zMax = Double.NEGATIVE_INFINITY;
         
         for(int j=0; j<data[0].length; j++)
         {
            for(int i=0; i<data.length; i++)
            {
               double d = data[i][j];
               zMin = Math.min(zMin, d);
               if (d>0) zLogMin = Math.min(zLogMin,d);
               zMax = Math.max(zMax, d);
            }
         }
      }
      if (overlay instanceof TwoDOverlay)
      {
         ((TwoDOverlay) overlay).setZMinMax(zMin,zMax,zLogMin);
      }
   }
   public void update(Observable o, Object arg)
   {
      // Dragons: Likely to be called by different thread
      if (o == dataSource)
      {
         // Currently there are several types of update notifications
         // - DataUpdate
         // - RangeUpdate
         // - FinalUpdate
         // - TitleUpdate
         // - Reset (Ususlly for when partition changes)
         
         HistogramUpdate hu = (HistogramUpdate) arg;
         
         // If we are not binned, we must pass the update onto any fits.
         // If we are binned then hopefully our observer will ask us to rebin
         // ourselves, and the fit will be informed then.
         
         isBinned = false;
         zLimitsValid = false;
         
         //if (hu.isReset())
         //parent.resetNumberOfBins(this);
         
         //if (hu.isRangeUpdate() || hu.isReset())
         //parent.invalidateXAxis();
         
         //long delay = hu.isFinalUpdate() || hu.isReset() ? 0 : 1000;
         //parent.scheduleDataUpdate(delay);
         parent.update(hu, this);
      }
      else if (o == style)
      {
         // Binning may change due to overflow bins being changed.
         // TODO: Could be more selective about when we need to rebin/redraw
         isBinned = false;
         parent.styleUpdate(this);
      }
      else if (o == normalization)
      {
         normalizationChanged(false);
      }
   }
   void normalizationChanged(boolean now)
   {
      // treat the same as dataChanged
      // Could be more efficient by just renormalizing the cached copy of the data
      isBinned = false;
      zLimitsValid = false;
      parent.update(null,this);
   }
   public boolean hasChanged()
   {
      return !isBinned;
   }
   void validate()
   {
      if (!isBinned) doBin();
      System.out.println("validate called");
   }
   void axisChanged()
   {
      parent.axisChanged(this);
   }
   public DataSource getDataSource()
   {
      return dataSource;
   }
   public JASHistStyle getStyle()
   {
      return style;
   }
   void destroy()
   {
      if (dataSource instanceof Observable) ((Observable) dataSource).deleteObserver(this);
      style.deleteObserver(this);
      super.deleteNormalizationObserver();
   }
   
   protected Rebinnable2DHistogramData dataSource;
   protected JASHist2DHistogramStyle style; // directly accessed by Overlay
   
   protected boolean hurry;
   protected boolean dataValid;
   boolean isBinned = false;
   private boolean zLimitsValid = false;
   private double[][] data;
   private int xBins;
   private double xLow;
   private double xHigh;
   private int yBins;
   private double yLow;
   private double yHigh;
   private double zMin;
   private double zMax;
   
   //static final long serialVersionUID = -3529869583896718619L;
}
