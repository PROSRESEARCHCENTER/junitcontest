package jas.hist;

import jas.plot.DataArea;
import jas.plot.EditableLabel;
import jas.plot.Legend;
import jas.plot.MovableObject;
import jas.plot.Title;
import jas.util.ColorConverter;
import jas.util.ScientificFormat;
import jas.util.xml.XMLWriter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.Writer;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

final class XMLPrintWriter extends XMLWriter
{
   XMLPrintWriter(Writer w)
   {
      super(w);
   }
   void print(JASHist plot,boolean snapshot)
   {
      printPlotHeader();
      Title t = plot.getTitleObject();
      if (t != null && t.isVisible()) print(t);
      print(plot.getLegend(),plot.getShowLegend());
      StatisticsBlock s = plot.getStats();
      if (s != null && s.isVisible()) print(s);
      DataArea d = plot.getDataArea();
      if (d != null && d.isVisible()) print(d,plot.getDataManager(),snapshot);
      setAttribute("width",plot.getWidth());
      setAttribute("height",plot.getHeight());
      printTag("bounds");
      printColorScheme(plot);
      printPlotFooter();
   }
   void printColorScheme(Component c)
   {
      Component pp = c.getParent();
      Color fg = c.getForeground();
      Color bg = c.getBackground();
      if (pp == null ||
      fg != pp.getForeground() ||
      bg != pp.getBackground())
      {
         if (pp == null || fg != pp.getForeground())
            setAttribute("foregroundColor",ColorConverter.colorToString(fg));
         if (pp == null || bg != pp.getBackground())
            setAttribute("backgroundColor",ColorConverter.colorToString(bg));
         printTag("colorScheme");
      }
   }
   void print(Title t)
   {
      openTag("title");
      print(t.getInsideBorder());
      printBounds(t);
      print(t.getLabel());
      printColorScheme(t);
      closeTag();
   }
   void print(JASHistAxis a, String p_location)
   {
      int type = a.getAxisType();
      setAttribute("location",p_location);
      setAttribute("type",convertAxisTypeToString(type));
      setAttribute("showOverflows",a.getShowOverflows());
      if (a.isBinned()) setAttribute("numberOfBins",a.getBins());
      if (type != a.STRING)
      {
         setAttribute("logarithmic",a.isLogarithmic());
         setAttribute("allowSuppressedZero",a.getAllowSuppressedZero());
         if (!a.getRangeAutomatic())
         {
            setAttribute("min",a.getMin());
            setAttribute("max",a.getMax());
         }
      }
      openTag("axis");
      EditableLabel label = a.getLabelObject();
      if (label != null) print(label);
      print(a.getFont());
      closeTag();
   }
   void print(EditableLabel label)
   {
      setAttribute("text",label.getText());
      openTag("label");
      print(label.getFont());
      closeTag();
   }
   void print(Legend legend,int visibility)
   {
      setAttribute("visible",convertLegendToString(visibility));
      openTag("legend");
      if (legend != null && legend.isVisible())
      {
         print(legend.getInsideBorder());
         printBounds(legend);
         printColorScheme(legend);
         print(legend.getFont());
         int n = legend.getNEntries();
         for (int i=0; i<n; i++)
         {
            String title = legend.getCurrentTitle(i);
            if (legend.isTitleChanged(i))
            {
               setAttribute("title",title);
               setAttribute("index",String.valueOf(i));
               printTag("legendEntry");
            }
         }
      }
      closeTag();
   }
   void print(StatisticsBlock stats)
   {
      setAttribute("showTitles",convertShowTitleToString(stats.getShowTitles()));
      setAttribute("alignment",convertAlignmentToString(stats.getSplitStringAlign()));
      openTag("stats");
      print(stats.getInsideBorder());
      printBounds(stats);
      printColorScheme(stats);
      print(stats.getFont());
      java.text.Format format = stats.getFormat();
      if (format instanceof ScientificFormat) print((ScientificFormat) stats.getFormat());
      String[] elements = stats.getSelectedEntries();
      if (elements != null)
      {
         for (int i=0; i<elements.length; i++)
         {
            setAttribute("name",elements[i]);
            printTag("statsEntry");
         }
      }
      closeTag();
   }
   void print(ScientificFormat format)
   {
      setAttribute("maximumWidth",format.getMaxWidth());
      setAttribute("significantDigits",format.getSigDigits());
      if (format.getScientificNotationStyle()) setAttribute("style","pure");
      printTag("format");
   }
   void printBinnedDataAxisAttributes(
   String axis, String min, String max,
   String numberOfBins, String type)
   {
      setAttribute("axis",axis);
      setAttribute("min",min);
      setAttribute("max",max);
      setAttribute("numberOfBins",numberOfBins);
      setAttribute("type",type);
      printTag("binnedDataAxisAttributes");
   }
   void printPointDataAxisAttributes(
   String axis, String type)
   {
      setAttribute("axis",axis);
      setAttribute("type",type);
      printTag("pointDataAxisAttributes");
   }
   void print(jas.plot.DataArea da, DataManager dm, boolean snapshot)
   {
      
      openTag("dataArea");
      print(da.getInsideBorder());
      printBounds(da);
      printColorScheme(da);
      Enumeration e = dm.getDataSources();
      while (e.hasMoreElements())
      {
         JASHistData theData = (JASHistData)e.nextElement();
         if (theData.isShowing()) print(theData,snapshot);
      }
      if (dm instanceof SupportsFunctions)
      {
         e = ((SupportsFunctions) dm).getFunctions();
         while (e.hasMoreElements())
         {
            JASHistData theData = (JASHistData) e.nextElement();
            if (theData.isShowing()) print(theData,snapshot);
         }
      }
      JASHistAxis theXAxis = dm.getXAxis();
      print(theXAxis, "x0");
      JASHistAxis[] theYAxes = dm.getYAxes();
      for (int i=0; i<theYAxes.length; i++)
      {
         if (theYAxes[i] != null) print(theYAxes[i] , "y"+i);
      }
      closeTag();
   }
   void print(JASHistData data, boolean snapshot)
   {
      data.writeAsXML(this,snapshot);
   }
   void printPlotHeader()
   {
      openDoc("1.0", "ISO-8859-1", false);
      referToDTD("plotML", "plotML.dtd");
      openTag("plotML");
      openTag("plot");
   }
   void printPlotFooter()
   {
      closeTag();
      closeTag();
   }
   void printBounds(MovableObject t)
   {
      if (!t.hasDefaultLayout())
      {
         setAttribute("x",t.getX());
         setAttribute("y",t.getY());
         setAttribute("width",t.getWidth());
         setAttribute("height",t.getHeight());
         printTag("bounds");
      }
   }
   void print(Border b)
   {
      if (b == null) setAttribute("type","None");
      else if (b == BorderFactory.createLoweredBevelBorder()) setAttribute("type","Bevel In");
      else if (b == BorderFactory.createRaisedBevelBorder())  setAttribute("type","Bevel Out");
      else if (b == BorderFactory.createEtchedBorder())       setAttribute("type","Etched");
      else if (b instanceof jas.util.border.ShadowBorder)     setAttribute("type","Shadow");
      else if (b instanceof LineBorder)
      {
         setAttribute("type","Line");
         setAttribute("color",ColorConverter.colorToString(((LineBorder)b).getLineColor()));
      }
      else setAttribute("type","None");
      printTag("border");
   }
   void print(Font f)
   {
      String theStyle;
      switch (f.getStyle())
      {
         default:
         case Font.PLAIN:
            theStyle = "PLAIN";
            break;
         case Font.BOLD:
            theStyle = "BOLD";
            break;
         case Font.ITALIC:
            theStyle = "ITALIC";
            break;
         case (Font.BOLD + Font.ITALIC):
            theStyle = "BOLD+ITALIC";
            break;
      }
      setAttribute("face",f.getName());
      setAttribute("points",f.getSize());
      setAttribute("style",theStyle);
      printTag("font");
   }
   static String convertStyleToString(int style)
   {
      switch (style)
      {
         case JASHist1DHistogramStyle.SYMBOL_BOX:
            return "box";
         case JASHist1DHistogramStyle.SYMBOL_CIRCLE:
            return "circle";
         case JASHist1DHistogramStyle.SYMBOL_CROSS:
            return "cross";
         case JASHist1DHistogramStyle.SYMBOL_DIAMOND:
            return "diamond";
         case JASHist1DHistogramStyle.SYMBOL_DOT:
            return "dot";
         case JASHist1DHistogramStyle.SYMBOL_HORIZ_LINE:
            return "horiz line";
         case JASHist1DHistogramStyle.SYMBOL_SQUARE:
            return "square";
         case JASHist1DHistogramStyle.SYMBOL_STAR:
            return "star";
         case JASHist1DHistogramStyle.SYMBOL_TRIANGLE:
            return "triangle";
         case JASHist1DHistogramStyle.SYMBOL_VERT_LINE:
            return "vert line";
         default:
            System.err.println("ERROR: unrecognized style, using default instead.");
            return "box";
      }
   }
   static int convertStringToStyle(String s)
   {
      if (s.equalsIgnoreCase("box")) return JASHist1DHistogramStyle.SYMBOL_BOX;
      if (s.equalsIgnoreCase("circle")) return JASHist1DHistogramStyle.SYMBOL_CIRCLE;
      if (s.equalsIgnoreCase("cross")) return JASHist1DHistogramStyle.SYMBOL_CROSS;
      if (s.equalsIgnoreCase("diamond")) return JASHist1DHistogramStyle.SYMBOL_DIAMOND;
      if (s.equalsIgnoreCase("dot")) return JASHist1DHistogramStyle.SYMBOL_DOT;
      if (s.equalsIgnoreCase("horiz line")) return JASHist1DHistogramStyle.SYMBOL_HORIZ_LINE;
      if (s.equalsIgnoreCase("square")) return JASHist1DHistogramStyle.SYMBOL_SQUARE;
      if (s.equalsIgnoreCase("star")) return JASHist1DHistogramStyle.SYMBOL_STAR;
      if (s.equalsIgnoreCase("triangle")) return JASHist1DHistogramStyle.SYMBOL_TRIANGLE;
      if (s.equalsIgnoreCase("vert line")) return JASHist1DHistogramStyle.SYMBOL_VERT_LINE;
      System.err.println("ERROR: unrecognized style, using default instead.");
      return JASHist1DHistogramStyle.SYMBOL_BOX;
   }
   static String convertAxisTypeToString(int type)
   {
      switch (type)
      {
         case DataSource.STRING :
            return "string";
         case DataSource.DOUBLE :
            return "double";
         case DataSource.DATE :
            return "date";
         case DataSource.DELTATIME : //!PA - is this correct?
            return "time";
         default:
            System.err.println("ERROR: unrecognized axis type, using double instead.");
            return "double";
      }
   }
   static int convertStringToAxisType(String s)
   {
      if (s.equalsIgnoreCase("string")) return DataSource.STRING;
      if (s.equalsIgnoreCase("double")) return DataSource.DOUBLE;
      if (s.equalsIgnoreCase("date")) return DataSource.DATE;
      if (s.equalsIgnoreCase("time")) return DataSource.DELTATIME;
      System.err.println("ERROR: unrecognized axis type, using double instead.");
      return DataSource.DOUBLE;
   }
   private static String convertLegendToString(int legend)
   {
      switch (legend)
      {
         case JASHist.LEGEND_AUTOMATIC:
            return "automatic";
         case JASHist.LEGEND_NEVER:
            return "never";
         case JASHist.LEGEND_ALWAYS:
            return "always";
         default:
            System.err.println("ERROR: unrecognized legend type, using AUTOMATIC instead.");
            return "automatic";
      }
   }
   static int convertStringToLegend(String s)
   {
      if (s.equalsIgnoreCase("automatic")) return JASHist.LEGEND_AUTOMATIC;
      if (s.equalsIgnoreCase("always")) return JASHist.LEGEND_ALWAYS;
      if (s.equalsIgnoreCase("never")) return JASHist.LEGEND_NEVER;
      if (s.equalsIgnoreCase("true")) return JASHist.LEGEND_ALWAYS;
      if (s.equalsIgnoreCase("false")) return JASHist.LEGEND_NEVER;
      System.err.println("ERROR: unrecognized legend type, using AUTOMATIC instead");
      return JASHist.LEGEND_AUTOMATIC;
   }
   private static String convertShowTitleToString(int showTitle)
   {
      switch (showTitle)
      {
         case StatisticsBlock.SHOWTITLES_AUTOMATIC:
            return "automatic";
         case StatisticsBlock.SHOWTITLES_NEVER:
            return "never";
         case StatisticsBlock.SHOWTITLES_ALWAYS:
            return "always";
         default:
            System.err.println("ERROR: unrecognized showTitles type, using AUTOMATIC instead.");
            return "automatic";
      }
   }
   static int convertStringToShowTitle(String s)
   {
      if (s.equalsIgnoreCase("automatic")) return StatisticsBlock.SHOWTITLES_AUTOMATIC;
      if (s.equalsIgnoreCase("never")) return StatisticsBlock.SHOWTITLES_NEVER;
      if (s.equalsIgnoreCase("always")) return StatisticsBlock.SHOWTITLES_ALWAYS;
      System.err.println("ERROR: unrecognized showTitle type, using SHOWTITLES_AUTOMATIC instead");
      return StatisticsBlock.SHOWTITLES_AUTOMATIC;
   }
   private static String convertAlignmentToString(int alignment)
   {
      switch (alignment)
      {
         case StatisticsBlock.LEFTALIGNSPLIT:
            return "left";
         case StatisticsBlock.RIGHTALIGNSPLIT:
            return "right";
         case StatisticsBlock.NOALIGNSPLIT:
            return "none";
         default:
            System.err.println("ERROR: unrecognized alignment type, using NONE instead.");
            return "none";
      }
   }
   static int convertStringToAlignment(String s)
   {
      if (s.equalsIgnoreCase("left")) return StatisticsBlock.LEFTALIGNSPLIT;
      if (s.equalsIgnoreCase("right")) return StatisticsBlock.RIGHTALIGNSPLIT;
      if (s.equalsIgnoreCase("none")) return StatisticsBlock.NOALIGNSPLIT;
      System.err.println("ERROR: unrecognized alignment type, using NOALIGNSPLIT instead");
      return StatisticsBlock.NOALIGNSPLIT;
   }
}
