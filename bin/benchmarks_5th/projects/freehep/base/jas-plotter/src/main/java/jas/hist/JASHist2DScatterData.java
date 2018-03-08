package jas.hist;
import jas.plot.Overlay;
import jas.util.xml.HasXMLRepresentation;

import java.util.Observable;

class JASHist2DScatterData extends JASHist2DHistogramData
{
   
   JASHist2DScatterData(final DataManager dm, final HasScatterPlotData ds)
   {
      super(dm,ds);
      dataSource = ds;
   }
   JASHistStyle createStyle()
   {
      return  new JASHistScatterPlotStyle();
   }
   boolean hasScatterPlotData()
   {
      return dataSource.hasScatterPlotData();
   }
   public void setStyle(final JASHistStyle style)
   {
      if (!(style instanceof JASHistScatterPlotStyle))
         throw new IllegalArgumentException("Style "+style.getClass()+" is not subclass of JASHistScatterPlotStyle");
      if (this.style != null) this.style.deleteObserver(this);
      this.style = (JASHistScatterPlotStyle) style;
      this.style.addObserver(this);
   }
   Overlay createOverlay()
   {
      return new ScatterOverlay(this);
   }
   public void update(Observable o, Object arg)
   {
      // Dragons: Likely to be called by different thread
      if (o == dataSource)
      {
         parent.update((HistogramUpdate) arg, this);
      }
      else if (o == style)
      {
         parent.styleUpdate(this);
      }
   }
   void restartImage(final boolean newEnumNeeded)
   {
      if (overlay instanceof ScatterOverlay)
      {
         ((ScatterOverlay) overlay).restartImage(newEnumNeeded);
      }
   }
   void continueImage()
   {
      if (overlay instanceof ScatterOverlay)
      {
         ((ScatterOverlay) overlay).continueImage();
      }
   }
   public JASHistStyle getStyle()
   {
      return style;
   }
   HasScatterPlotData dataSource;
   boolean dataChanged, resetSent, onNewAxis; // the data manager uses these flags
   
   //	static final long serialVersionUID = 0/* ?? */;
   
   protected void calcZLimits()
   {
      if (((JASHistScatterPlotStyle) style).getDisplayAsScatterPlot()) return;
      else super.calcZLimits();
   }
   public void writeAsXML(XMLPrintWriter pw, boolean snapshot)
   {
      String theXAxisType = pw.convertAxisTypeToString(dataSource.getXAxisType());
      String theYAxisType = pw.convertAxisTypeToString(dataSource.getYAxisType());
      pw.setAttribute("type","scatter2d");
      pw.openTag("data2d");
      
      
      int xBins = dataSource.getXBins();
      double xLow = dataSource.getXMin();
      double xHigh = dataSource.getXMax();
      int yBins = dataSource.getYBins();
      double yLow = dataSource.getYMin();
      double yHigh = dataSource.getYMax();
      
      if (snapshot)
      {
         double[][][] result = dataSource.rebin(xBins,xLow,xHigh,yBins,yLow,yHigh,true,hurry,
         style.getShowOverflow());
         if (result == null) result = new double[1][xBins][yBins];
         double[][] data = result[0];
         
         pw.setAttribute("title",getTitle());
         pw.setAttribute("dimensions","2");
         pw.openTag("points");
         final double[] d = new  double[2];
         
         if (dataSource.hasScatterPlotData())
         {
            ScatterEnumeration se = dataSource.startEnumeration();
            while (se.getNextPoint(d))
            {
               pw.println(d[0] + "," + d[1]);
            }
         }
         pw.closeTag();
         
         
         //output the x,y axis attributes
         pw.printPointDataAxisAttributes("x",theXAxisType);//5 string args
         pw.printPointDataAxisAttributes("y",theYAxisType);
         
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
      else	// !snapshot
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
      pw.setAttribute("displayAsScatterPlot", ((JASHistScatterPlotStyle) style).getDisplayAsScatterPlot());
      pw.setAttribute("dataPointSize" , ((JASHistScatterPlotStyle) style).getDataPointSize());
      pw.setAttribute("dataPointStyle" , ((JASHistScatterPlotStyle) style).getDataPointStyle());
      pw.setAttribute("dataPointColor",jas.util.ColorConverter.colorToString(((JASHistScatterPlotStyle) style).getDataPointColor()));
      pw.printTag("style2d");
      
      pw.closeTag();
   }
}
