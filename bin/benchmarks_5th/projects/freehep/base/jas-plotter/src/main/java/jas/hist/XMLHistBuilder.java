package jas.hist;

import jas.hist.normalization.AreaNormalizer;
import jas.hist.normalization.BinNormalizer;
import jas.hist.normalization.EntriesNormalizer;
import jas.hist.normalization.MaxBinNormalizer;
import jas.hist.normalization.Normalizer;
import jas.hist.normalization.RelativeNormalizer;
import jas.hist.normalization.SimpleNormalizer;
import jas.hist.normalization.StatisticsNormalizer;
import jas.plot.Axis;
import jas.plot.DataArea;
import jas.plot.EditableLabel;
import jas.plot.Legend;
import jas.plot.Title;
import jas.util.ScientificFormat;
import jas.util.xml.ClassPathEntityResolver;
import jas.util.xml.JASDOMParser;
import jas.util.xml.XMLNodeTraverser;
import jas.util.xml.XMLNodeTraverser.BadXMLException;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Create a Plot from an XML file.
 *
 * Reads an XML file describing a Plot and creates
 * or modifies a plot to correspond to the data specified in
 * the XML file.
 */
public class XMLHistBuilder
{
    //
    // constructors
    //
   /**
    * Create an XMLHistBuilder by reading an XML file
    * @param xmlFile The xmlFile to read.
    * @param fileName The file name (for error messages)
    */
    public XMLHistBuilder(Reader xmlFile, String fileName) throws JASDOMParser.JASXMLException, BadXMLException
    {
        ClassPathEntityResolver er = new ClassPathEntityResolver("plotML.dtd",XMLHistBuilder.class);
        Document node = JASDOMParser.instance().parse(xmlFile,fileName,er);
        plotML = new PlotMLNodeTraverser(node);
    }
   /**
    * Create an XMLHistBuilder from a pre-parsed Document Object Model
    */
    public XMLHistBuilder(Document dom) throws BadXMLException
    {
        plotML = new PlotMLNodeTraverser(dom);
    }
    
   /**
    * Create a plot using the parsed XML.
    */
    public JASHist getSoloPlot()  throws BadXMLException
    {
        return modifyPlot(new JASHist());
    }
    
   /**
    * Modify an existing plot using the parsed XML.
    */
    public JASHist modifyPlot(JASHist theHist) throws BadXMLException
    {
        Node node = plotML.getPlot();
        JASHistNodeTraverser traverser = new JASHistNodeTraverser();
        theHist.removeAllData();
        traverser.traverse(node,theHist);
        return theHist;
    }
    
   /**
    * Get a plot page. Not implemented yet.
    */
    public jas.plot.PlotPanel getPlotPage()
    {
        return null;//!PA - stub
        //return (jas.plot.PlotPanel)(getIt("plotPage"));
    }
    
    private PlotMLNodeTraverser plotML;
}
class PlotMLNodeTraverser extends XMLNodeTraverser
{
    PlotMLNodeTraverser(Document node) throws BadXMLException
    {
        traverse(node);
    }
    protected void handleElement(Element node, String name) throws BadXMLException
    {
        if (name.equals("plotML")) traverse(node);
        else hash.put(name,node);
    }
    protected void handleOtherNode(Node node, String name) throws BadXMLException
    {
        if (node.getNodeType() == node.DOCUMENT_TYPE_NODE) return;
        else super.handleOtherNode(node,name);
    }
    Node getPlot() throws BadXMLException
    {
        Node node = (Node) hash.get("plot");
        if (node == null) throw new BadXMLException("<plot> node not found");
        return node;
    }
    private Hashtable hash = new Hashtable();
}
abstract class ComponentNodeTraverser extends XMLNodeTraverser
{
    void traverse(Node n,JComponent c) throws BadXMLException
    {
        this.c = c;
        super.traverse(n);
    }
    protected void handleElement(Element node, String name) throws BadXMLException
    {
        if      (name.equals("border"))      bt.traverse(node,c);
        else if (name.equals("bounds"))      dt.traverse(node,c);
        else if (name.equals("colorScheme")) ct.traverse(node,c);
        else if (name.equals("font"))        ft.traverse(node,c);
        else super.handleElement(node,name);
    }
    private JComponent c;
    private static BorderNodeTraverser bt = new BorderNodeTraverser();
    private static ColorSchemeNodeTraverser ct = new ColorSchemeNodeTraverser();
    private static FontNodeTraverser ft = new FontNodeTraverser();
    private static BoundsNodeTraverser dt = new BoundsNodeTraverser();
}

class BorderNodeTraverser extends XMLNodeTraverser
{
    void traverse(Node node, JComponent c) throws BadXMLException
    {
        super.traverse(node);
        if      (type.equals("Bevel In"))  c.setBorder(BorderFactory.createLoweredBevelBorder());
        else if (type.equals("Bevel Out")) c.setBorder(BorderFactory.createRaisedBevelBorder());
        else if (type.equals("Etched"))    c.setBorder(BorderFactory.createEtchedBorder());
        else if (type.equals("Shadow"))    c.setBorder(jas.util.border.ShadowBorder.createShadowBorder());
        else if (type.equals("Line"))      c.setBorder(BorderFactory.createLineBorder(color));
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("type")) type = value;
        else if (name.equals("color")) color = toColor(value);
        else super.handleAttributeNode(node,name,value);
    }
    private Color color;
    private String type;
}

class FontNodeTraverser extends XMLNodeTraverser
{
    void traverse(Node node, JComponent c) throws BadXMLException
    {
        super.traverse(node);
        c.setFont(new Font(face,style,size));
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("face")) face = value;
        else if (name.equals("style")) style = toFontStyle(value);
        else if (name.equals("points")) size = toInt(value);
        else super.handleAttributeNode(node,name,value);
    }
    private int toFontStyle(String s)
    {
        if (s.equalsIgnoreCase("PLAIN")) return Font.PLAIN;
        if (s.equalsIgnoreCase("BOLD")) return Font.BOLD;
        if (s.equalsIgnoreCase("ITALIC")) return Font.ITALIC;
        if (s.equalsIgnoreCase("BOLD+ITALIC")) return (Font.BOLD + Font.ITALIC);
        return Font.PLAIN;
    }
    private int size;
    private String face;
    private int style;
}

class ColorSchemeNodeTraverser extends XMLNodeTraverser
{
    void traverse(Node node, JComponent c) throws BadXMLException
    {
        this.c = c;
        super.traverse(node);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("foregroundColor")) c.setForeground(toColor(value));
        else if (name.equals("backgroundColor")) c.setBackground(toColor(value));
        else super.handleAttributeNode(node,name,value);
    }
    private JComponent c;
}

class BoundsNodeTraverser extends XMLNodeTraverser
{
    void traverse(Node node, JComponent c) throws BadXMLException
    {
        super.traverse(node);
        Rectangle r = new Rectangle(x,y,width,height);
        if (c instanceof jas.plot.MovableObject)
        {
            ((jas.plot.MovableObject)c).setMovableObjectBounds(r);
        }
        else
        {
            c.setBounds(r);
            c.setPreferredSize(new Dimension(width,height));
        }
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("x")) x = toInt(value);
        else if (name.equals("y")) y = toInt(value);
        else if (name.equals("width"))  width = toInt(value);
        else if (name.equals("height")) height = toInt(value);
        else super.handleAttributeNode(node,name,value);
    }
    private int x,y,width,height;
}

class TitleNodeTraverser extends ComponentNodeTraverser
{
    void traverse(Node node, Title t) throws BadXMLException
    {
        title = t;
        super.traverse(node,t);
    }
    protected void handleElement(Element node, String name) throws BadXMLException
    {
        if (name.equals("label")) lt.traverse(node,title.getLabel());
        else super.handleElement(node,name);
    }
    private static LabelNodeTraverser lt = new LabelNodeTraverser();
    private Title title;
}
class JASHistNodeTraverser extends ComponentNodeTraverser
{
    void traverse(Node node, JASHist plot) throws BadXMLException
    {
        this.plot = plot;
        super.traverse(node,plot);
        // We cant apply the legend entries until after the data is added,
        // so do it here
        lt.applyLegendEntries();
    }
    protected void handleElement(Element node, String name) throws BadXMLException
    {
        if      (name.equals("title"))
        {
            Title t = plot.getTitleObject();
            if (t == null) plot.setTitleObject(t=new Title());
            tt.traverse(node,t);
        }
        else if (name.equals("stats"))
        {
            st.traverse(node,plot.getStats());
            plot.setShowStatistics(true);
        }
        else if (name.equals("dataArea"))   dt.traverse(node,plot.getDataArea(),plot);
        else if (name.equals("legend"))     lt.traverse(node,plot.getLegend(),plot);
        else super.handleElement(node,name);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if (name.equals("allowUserInteraction")) plot.setAllowUserInteraction(toBoolean(value));
        else super.handleAttributeNode(node,name,value);
    }
    private static TitleNodeTraverser tt = new TitleNodeTraverser();
    private static StatsNodeTraverser st = new StatsNodeTraverser();
    private static DataAreaNodeTraverser dt = new DataAreaNodeTraverser();
    private static LegendNodeTraverser lt = new LegendNodeTraverser();
    private JASHist plot;
}
class LabelNodeTraverser extends ComponentNodeTraverser
{
    void traverse(Node node, EditableLabel label) throws BadXMLException
    {
        this.label = label;
        super.traverse(node,label);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if (name.equals("text")) label.setText(value);
        else super.handleAttributeNode(node,name,value);
    }
    private EditableLabel label;
}
class StatsNodeTraverser extends ComponentNodeTraverser
{
    void traverse(Node node, StatisticsBlock stats) throws BadXMLException
    {
        this.stats = stats;
        super.traverse(node,stats);
        if (selected != null)
        {
            String[] elements = new String[selected.size()];
            selected.copyInto(elements);
            stats.setSelectedEntries(elements);
        }
    }
    protected void handleElement(Element node, String name) throws BadXMLException
    {
        if      (name.equals("statsEntry"))
        {
            if (selected == null) selected = new Vector();
            selected.addElement(node.getAttribute("name"));
        }
        else if (name.equals("format"))
        {
            FormatNodeTraverser ft = new FormatNodeTraverser(node);
            stats.setFormat(ft.getFormat());
        }
        else super.handleElement(node,name);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("showTitles")) stats.setShowTitles(XMLPrintWriter.convertStringToShowTitle(value));
        else if (name.equals("alignment")) stats.setSplitStringAlign(XMLPrintWriter.convertStringToAlignment(value));
        else super.handleAttributeNode(node,name,value);
    }
    private Vector selected;
    private StatisticsBlock stats;
}
class FormatNodeTraverser extends XMLNodeTraverser
{
    FormatNodeTraverser(Node node) throws BadXMLException
    {
        super.traverse(node);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("maximumWidth")) f.setMaxWidth(toInt(value));
        else if (name.equals("significantDigits")) f.setSigDigits(toInt(value));
        else if (name.equals("style")) f.setScientificNotationStyle(value.equals("pure"));
        else super.handleAttributeNode(node,name,value);
    }
    ScientificFormat getFormat()
    {
        return f;
    }
    private ScientificFormat f = new ScientificFormat();
}
class LegendNodeTraverser extends ComponentNodeTraverser
{
    void traverse(Node node, Legend legend, JASHist plot) throws BadXMLException
    {
        this.plot = plot;
        this.legend = legend;
        this.node = node;
    }
    protected void handleElement(Element node, String name) throws BadXMLException
    {
        if (name.equals("legendEntry"))
        {
            int index = toInt(node.getAttribute("index"));
            legend.setCurrentTitle(index,node.getAttribute("title"));
        }
        else super.handleElement(node,name);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if (name.equals("visible")) plot.setShowLegend(toVisibility(value));
        else super.handleAttributeNode(node,name,value);
    }
    void applyLegendEntries() throws BadXMLException
    {
        if (node != null) super.traverse(node,legend);
    }
    private int toVisibility(String value) throws BadXMLException
    {
        return XMLPrintWriter.convertStringToLegend(value);
    }
    private Node node;
    private Legend legend;
    private JASHist plot;
}
class DataAreaNodeTraverser extends ComponentNodeTraverser
{
    void traverse(Node node, DataArea da, JASHist plot) throws BadXMLException
    {
        this.da = da;
        this.plot = plot;
        super.traverse(node,da);
    }
    protected void handleElement(Element node, String name) throws BadXMLException
    {
        if (name.equals("axis"))
        {
            NamedNodeMap nnm = node.getAttributes();
            String position = nnm.getNamedItem("location").getNodeValue();
            JASHistAxis theAxis;
            if      (position.equals("x0")) theAxis = plot.getXAxis();
            else if (position.equals("y0")) theAxis = plot.getYAxis();
            else if (position.equals("y1")) theAxis = plot.getYAxis(1);
            else throw new BadXMLException("Illegal axis position"+position);
            at.traverse(node,theAxis);
        }
        else if (name.equals("data1d"))
        {
            Data1DTraverser d1d = new Data1DTraverser(node);
            DataSource ds = d1d.getDataSource();
            if (ds == null) return; // Just ignore it ... needed for custom HasDataSource implementations
            String refName = d1d.getRefName();
            if (refName != null) map.put(refName,ds);
            JASHistData data = plot.addData(ds);
            data.setYAxis(d1d.getYAxis());
            JASHistStyle style = d1d.getStyle();
            if (style != null) data.setStyle(style);
            Normalizer norm = d1d.getNormalizer(ds,map);
            if (norm != null) data.setNormalization(norm);
            data.show(true);
        }
        else if (name.equals("data2d"))
        {
            Data2DTraverser d2d = new Data2DTraverser(node);
            DataSource ds = d2d.getDataSource();
            if (ds == null) return; // Just ignore it ... needed for custom HasDataSource implementations
            String refName = d2d.getRefName();
            if (refName != null) map.put(refName,ds);
            JASHistData data = plot.addData(ds);
            JASHistStyle style = d2d.getStyle();
            if (style != null) data.setStyle(style);
            data.show(true);
        }
        else if (name.equals("function1d"))
        {
            Function1DTraverser f1d = new Function1DTraverser(node,plot);
            DataSource ds = f1d.getDataSource();
            if (ds != null)
            {
                String refName = f1d.getRefName();
                if (refName != null) map.put(refName,ds);
                String fitRef = f1d.getFitRef();
                if (fitRef != null)
                {
                    DataSource data = (DataSource) map.get(fitRef);
                    if (data == null) throw new BadXMLException("Cannot resolve reference "+fitRef);
                    if (!(data instanceof Rebinnable1DHistogramData)) throw new BadXMLException("Cannot fit 2D data");
                    if (!(ds instanceof Fittable1DFunction)) throw new BadXMLException("Function is not fittable");
                    Fitter fitter = FitterRegistry.instance().getDefaultFitter();
                    fitter.setData((XYDataSource) data);
                    fitter.setFunction((Fittable1DFunction) ds);
                    try
                    {
                        fitter.fit();
                    }
                    catch (FitFailed x) { x.printStackTrace(); }
                }
                JASHistData data = plot.addData(ds);
                JASHistStyle style = f1d.getStyle();
                if (style != null) data.setStyle(style);
                data.show(f1d.isVisible());
            }
        }
        else super.handleElement(node,name);
    }
    private Hashtable map = new Hashtable();
    private DataArea da;
    private JASHist plot;
    private static AxisNodeTraverser at = new AxisNodeTraverser();
}
class AxisNodeTraverser extends XMLNodeTraverser
{
    void traverse(Node node, JASHistAxis axis) throws BadXMLException
    {
        this.axis = axis;
        super.traverse(node);
    }
    protected void handleElement(Element node, String name) throws BadXMLException
    {
        if      (name.equals("label"))
        {
            EditableLabel l = axis.getLabelObject();
            if (l == null) axis.setLabelObject(l = new EditableLabel("","Axis Label"));
            lt.traverse(node,l);
        }
        else if (name.equals("font")) ft.traverse(node,(Axis) axis);
        else super.handleElement(node,name);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("logarithmic"  )) axis.setLogarithmic(toBoolean(value));
        else if (name.equals("allowSuppressedZero")) axis.setAllowSuppressedZero(toBoolean(value));
        else if (name.equals("numberOfBins" )) axis.setBins(toInt(value));
        else if (name.equals("type"         )) axis.setAxisType(XMLPrintWriter.convertStringToAxisType(value));
        else if (name.equals("min"          )) axis.setMin(toDouble(value));
        else if (name.equals("max"          )) axis.setMax(toDouble(value));
        else if (name.equals("showOverflows")) axis.setShowOverflows(toBoolean(value));
        else if (name.equals("location")) {} // Dealt with elsewhere
        else super.handleAttributeNode(node,name,value);
    }
    private static LabelNodeTraverser lt = new LabelNodeTraverser();
    private static FontNodeTraverser ft = new FontNodeTraverser();
    private JASHistAxis axis;
}
class Style1DNodeTraverser extends XMLNodeTraverser
{
    void traverse(Node node,JASHist1DHistogramStyle style) throws BadXMLException
    {
        this.style = style;
        super.traverse(node);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("histogramBarsFilled"   )) style.setHistogramFill(toBoolean(value));
        else if (name.equals("showHistogramBars"     )) style.setShowHistogramBars(toBoolean(value));
        else if (name.equals("showErrorBars"         )) style.setShowErrorBars(toBoolean(value));
        else if (name.equals("showDataPoints"        )) style.setShowDataPoints(toBoolean(value));
        else if (name.equals("showLinesBetweenPoints")) style.setShowLinesBetweenPoints(toBoolean(value));
        else if (name.equals("dataPointSize"         )) style.setDataPointSize(toInt(value));
        else if (name.equals("histogramBarColor"     )) style.setHistogramBarColor(toColor(value));
        else if (name.equals("errorBarColor"         )) style.setErrorBarColor(toColor(value));
        else if (name.equals("dataPointColor"        )) style.setDataPointColor(toColor(value));
        else if (name.equals("lineColor"             )) style.setLineColor(toColor(value));
        else if (name.equals("dataPointStyle"        )) style.setDataPointStyle(XMLPrintWriter.convertStringToStyle(value));
        else super.handleAttributeNode(node,name,value);
    }
    private JASHist1DHistogramStyle style;
}
class Style2DNodeTraverser extends XMLNodeTraverser
{
    void traverse(Node node, JASHist2DHistogramStyle style) throws BadXMLException
    {
        this.style = style;
        super.traverse(node);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("startDataColor"    )) style.setStartDataColor(toColor(value));
        else if (name.equals("endDataColor"      )) style.setEndDataColor(toColor(value));
        else if (name.equals("showOverflow"      )) style.setShowOverflow(toBoolean(value));
        else if (name.equals("showPlot"          )) style.setShowPlot(toBoolean(value));
        else if (name.equals("histStyle"         )) style.setHistStyle(toStyle(value));
        else if (name.equals("colorMapScheme"    )) style.setColorMapScheme(toColorMapScheme(value));
        else if (name.equals("shapeColor"        )) style.setShapeColor(toColor(value));
        else if (name.equals("overflowBinColor"  )) style.setOverflowBinColor(toColor(value));
        else if (name.equals("logZ"              )) style.setLogZ(toBoolean(value));
        else super.handleAttributeNode(node,name,value);
    }
    private int toStyle(String s)
    {
        if (s.equalsIgnoreCase("STYLE_BOX")) return JASHist2DHistogramStyle.STYLE_BOX;
        if (s.equalsIgnoreCase("STYLE_ELLIPSE")) return JASHist2DHistogramStyle.STYLE_ELLIPSE;
        if (s.equalsIgnoreCase("STYLE_COLORMAP")) return JASHist2DHistogramStyle.STYLE_COLORMAP;
        return JASHist2DHistogramStyle.STYLE_BOX;
    }
    private int toColorMapScheme(String s)
    {
        if (s.equalsIgnoreCase("COLORMAP_WARM")) return JASHist2DHistogramStyle.COLORMAP_WARM;
        if (s.equalsIgnoreCase("COLORMAP_COOL")) return JASHist2DHistogramStyle.COLORMAP_COOL;
        if (s.equalsIgnoreCase("COLORMAP_THERMAL")) return JASHist2DHistogramStyle.COLORMAP_THERMAL;
        if (s.equalsIgnoreCase("COLORMAP_RAINBOW")) return JASHist2DHistogramStyle.COLORMAP_RAINBOW;
        if (s.equalsIgnoreCase("COLORMAP_GRAYSCALE")) return JASHist2DHistogramStyle.COLORMAP_GRAYSCALE;
        if (s.equalsIgnoreCase("COLORMAP_USERDEFINED")) return JASHist2DHistogramStyle.COLORMAP_USERDEFINED;
        return JASHist2DHistogramStyle.COLORMAP_WARM;
    }
    private JASHist2DHistogramStyle style;
}
class ScatterStyleNodeTraverser extends Style2DNodeTraverser
{
    void traverse(Node node, JASHistScatterPlotStyle style) throws BadXMLException
    {
        this.style = style;
        super.traverse(node,style);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("displayAsScatterPlot")) style.setDisplayAsScatterPlot(toBoolean(value));
        else if (name.equals("dataPointSize"       )) style.setDataPointSize(toInt(value));
        else if (name.equals("dataPointStyle"      )) style.setDataPointStyle(toDataPointStyle(value));
        else if (name.equals("dataPointColor"      ))style.setDataPointColor(toColor(value));
        else super.handleAttributeNode(node,name,value);
    }
    private int toDataPointStyle(String value) throws BadXMLException
    {
        
        if (toInt(value)==(JASHistScatterPlotStyle.SYMBOL_BOX )) return JASHistScatterPlotStyle.SYMBOL_BOX;
        if (toInt(value)==(JASHistScatterPlotStyle.SYMBOL_CROSS )) return JASHistScatterPlotStyle.SYMBOL_CROSS;
        if (toInt(value)==(JASHistScatterPlotStyle.SYMBOL_DIAMOND )) return JASHistScatterPlotStyle.SYMBOL_DIAMOND;
        if (toInt(value)==(JASHistScatterPlotStyle.SYMBOL_HORIZ_LINE )) return JASHistScatterPlotStyle.SYMBOL_HORIZ_LINE;
        if (toInt(value)==(JASHistScatterPlotStyle.SYMBOL_SQUARE)) return JASHistScatterPlotStyle.SYMBOL_SQUARE;
        if (toInt(value)==(JASHistScatterPlotStyle.SYMBOL_STAR )) return JASHistScatterPlotStyle.SYMBOL_STAR;
        if (toInt(value)==(JASHistScatterPlotStyle.SYMBOL_TRIANGLE)) return JASHistScatterPlotStyle.SYMBOL_TRIANGLE;
        return JASHistScatterPlotStyle.SYMBOL_VERT_LINE;
        
    }
    
    private JASHistScatterPlotStyle style;
}
class Function1DTraverser extends XMLNodeTraverser
{
    Function1DTraverser(Node node, JASHist plot) throws BadXMLException
    {
        this.plot = plot;
        traverse(node);
    }
    protected void handleElement(Element node, String name) throws BadXMLException
    {
        if      (name.equals("functionStyle1d")) st.traverse(node,style=new JASHist1DFunctionStyle());
        else if (name.equals("fit")) traverse(node);
        else if (name.equals("functionParam"))
        {
            if (function != null)
            {
                try
                {
                    fp.traverse(node);
                    String paramName = fp.getName();
                    String[] parameterNames = function.getParameterNames();
                    for (int i=0; i<parameterNames.length; i++)
                    {
                        if (parameterNames[i].equals(paramName))
                        {
                            function.setParameter(i,fp.getValue());
                            return;
                        }
                    }
                    throw new BadXMLException("Unknown parameter "+name+" for function "+className);
                }
                catch (InvalidFunctionParameter x)
                {
                    throw new BadXMLException("Invalid value for parameter "+name+" for function "+className);
                }
            }
        }
        else super.handleElement(node,name);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("axis")) yAxis = toInt(value.substring(1));
        else if (name.equals("name")) refName = value;
        else if (name.equals("isVisible")) isVisible = toBoolean(value);
        else if (name.equals("ref")) fitRef = value;
        else if (name.equals("type"))
        {
            className = value;
            FunctionFactory ff = theRegistry.find(value);
            if (ff == null) System.err.println("Could not find function "+value+" in function registry -- ignored");
            else
            {
                try
                {
                    function = ff.createFunction(plot);
                }
                catch (FunctionFactoryError ffe)
                {
                    throw new BadXMLException("Unable to create function "+value);
                }
            }
        }
        else super.handleAttributeNode(node,name,value);
    }
    DataSource getDataSource()
    {
        return function;
    }
    int getYAxis()
    {
        return yAxis;
    }
    JASHistStyle getStyle()
    {
        return style;
    }
    boolean isVisible()
    {
        return isVisible;
    }
    String getRefName()
    {
        return refName;
    }
    String getFitRef()
    {
        return fitRef;
    }
    private String fitRef;
    private String refName;
    private boolean isVisible = true;
    private static FunctionRegistry theRegistry = FunctionRegistry.instance();
    private JASHist plot;
    private int yAxis;
    private JASHist1DFunctionStyle style;
    private String className;
    private Basic1DFunction function;
    private FunctionParamNodeTraverser fp = new FunctionParamNodeTraverser();
    private FunctionStyle1DNodeTraverser st = new FunctionStyle1DNodeTraverser();
}
class FunctionStyle1DNodeTraverser extends XMLNodeTraverser
{
    void traverse(Node node,JASHist1DFunctionStyle style) throws BadXMLException
    {
        this.style = style;
        super.traverse(node);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if (name.equals("lineColor")) style.setLineColor(toColor(value));
        else super.handleAttributeNode(node,name,value);
    }
    private JASHist1DFunctionStyle style;
}
class FunctionParamNodeTraverser extends XMLNodeTraverser
{
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("name")) paramName = value;
        else if (name.equals("value")) paramValue = Double.valueOf(value).doubleValue();
        else super.handleAttributeNode(node,name,value);
    }
    double getValue()
    {
        return paramValue;
    }
    String getName()
    {
        return paramName;
    }
    private double paramValue;
    private String paramName;
}
class NormalizationTraverser extends XMLNodeTraverser
{
    NormalizationTraverser(Node node) throws BadXMLException
    {
        traverse(node);
    }
    protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
    {
        if      (name.equals("method")) method = value;
        else if (name.equals("param")) param = value;
        else if (name.equals("multiplier")) multiplier = toDouble(value);
        else if (name.equals("relativeTo")) relativeTo= value;
        else super.handleAttributeNode(node,name,value);
    } 
    Normalizer getNormalizer(DataSource data, Hashtable map) throws BadXMLException
    {
        SimpleNormalizer norm = createNormalizer(method,param,data);
        if (relativeTo != null)
        {
            DataSource ds = (DataSource) map.get(relativeTo);
            if (ds == null) throw new BadXMLException("Could not find data ref "+relativeTo);
            Normalizer rel = createNormalizer(method,param,ds);
            return new RelativeNormalizer(norm,rel);
        }
        else
        {
            norm.setFactor(1./multiplier);
            return norm;
        }
    }
    private SimpleNormalizer createNormalizer(String method, String param, DataSource data) throws BadXMLException
    {
        if      (method.equals("CONSTANT"))   return new SimpleNormalizer(1);
        else if (method.equals("MAXBIN"))     return new MaxBinNormalizer(data);
        else if (method.equals("AREA"))       return new AreaNormalizer(data);
        else if (method.equals("ENTRIES"))    return new EntriesNormalizer(data);
        else if (method.equals("BIN"))        return new BinNormalizer(data,toInt(param));
        else if (method.equals("STATISTICS")) return new StatisticsNormalizer(data,param);
        else throw new BadXMLException("Unrecognized normalization method "+method);
    }
    private String method;
    private String param;
    private String relativeTo;
    private double multiplier;
}
