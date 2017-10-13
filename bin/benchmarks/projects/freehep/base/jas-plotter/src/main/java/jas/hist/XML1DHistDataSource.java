package jas.hist;

import jas.hist.normalization.Normalizer;
import jas.util.xml.XMLNodeTraverser;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Build a DataSource from a DOM node
 */

class Data1DTraverser extends XMLNodeTraverser
{
	Data1DTraverser(Node node) throws BadXMLException
	{
		traverse(node);
	}
	protected void handleElement(Element node, String name) throws BadXMLException
	{
		if      (name.equals("bins1d")) b1d = new Bins1DNodeTraverser(node);
      else if (name.equals("points")) xy = new XYPointsNodeTraverser(node);
		else if (name.equals("binnedDataAxisAttributes")) baa = new BinnedDataAxisAttributesNodeTraverser(node);
      else if (name.equals("pointDataAxisAttributes")) paa = new PointDataAxisAttributesNodeTraverser(node);
		else if (name.equals("class")) ct = new ClassNodeTraverser(node);
		else if (name.equals("datasource")) ct = new DataSourceNodeTraverser(node);
		else if (name.equals("statistics")) stats = new StatisticsTraverser(node);
		else if (name.equals("axisLabels")) labels = new AxisLabelsTraverser(node);
		else if (name.equals("style1d")) st.traverse(node,style=new JASHist1DHistogramStyle());
      else if (name.equals("normalization")) norm = new NormalizationTraverser(node);
		else super.handleElement(node,name);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if      (name.equals("axis")) yAxis = toInt(value.substring(1));
      else if (name.equals("name")) refName = value;
		else super.handleAttributeNode(node,name,value);
	}
	DataSource getDataSource()
	{
		if (ct != null) return ct.getDataSource();
		else if (b1d != null) return new XML1DHistDataSource(b1d,baa,labels,stats);
      else return new XMLXYDataSource(xy,paa,stats);
	}
	int getYAxis()
	{
		return yAxis;
	}
	JASHistStyle getStyle()
	{
		return style;
	}
   String getRefName()
   {
       return refName;
   }
   Normalizer getNormalizer(DataSource data, Hashtable map) throws BadXMLException
   {
       return norm == null ? null : norm.getNormalizer(data,map);
   }
	private int yAxis;
	private StatisticsTraverser stats = null;
	private AxisLabelsTraverser labels = null;
   private NormalizationTraverser norm = null;
	private JASHist1DHistogramStyle style = null;
	private ConstructorNodeTraverser ct = null;
	private Style1DNodeTraverser st = new Style1DNodeTraverser();
	private Bins1DNodeTraverser b1d;
   private XYPointsNodeTraverser xy;
   private PointDataAxisAttributesNodeTraverser paa;
	private BinnedDataAxisAttributesNodeTraverser baa;
   private String refName;
}
class ClassNodeTraverser extends ConstructorNodeTraverser
{
	ClassNodeTraverser(Node node) throws BadXMLException
	{
		super(node);
		if (ds instanceof HasDataSource) return;
		throw new BadXMLException("Class is not a HasDataSource");
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if (name.equals("param")) param = value;
		else super.handleAttributeNode(node,name,value);
	}
	DataSource getDataSource()
	{
		return ((HasDataSource) ds).getDataSource(param);
	}
	private String param;
}
class DataSourceNodeTraverser extends ConstructorNodeTraverser
{
	DataSourceNodeTraverser(Node node)  throws BadXMLException
	{
		super(node);
		if (ds instanceof DataSource) return;
		throw new BadXMLException("Class is not a DataSource");
	}
	DataSource getDataSource()
	{
		return (DataSource) ds;
	}
}
abstract class ConstructorNodeTraverser extends XMLNodeTraverser
{
	ConstructorNodeTraverser(Node node) throws BadXMLException
	{
		int maxSize = node.getChildNodes().getLength();
		types = new String[maxSize];
		values = new String[maxSize];
		traverse(node);
		try
		{
                        Class c = null;
                        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
                        if (ccl != null) {
                            c = ccl.loadClass(className);
                        } else {
                            c = Class.forName(className);
                        }
                        
			if (vc == 0) ds = c.newInstance();
			else 
			{
				Class[] argc = new Class[vc];
				Object[] args = new Object[vc];
				for (int i=0; i<vc; i++)
				{
					argc[i] = toClass(types[i]);
					args[i] = toObject(values[i],types[i]);
				}
				Constructor con = c.getConstructor(argc);
				ds = con.newInstance(args);
			}
		}
		catch (Throwable t)
		{
			throw new BadXMLException("Error instantiating class "+className+": "+t);
		}
	}
	protected void handleElement(Element node, String name) throws BadXMLException
	{
		if      (name.equals("param")) traverse(node);
		else super.handleElement(node,name);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if      (name.equals("name")) className = value;
		else if (name.equals("type")) types[tc++] = value;
		else if (name.equals("value")) values[vc++] = value;
		else super.handleAttributeNode(node,name,value);
	}
	private Class toClass(String type) throws BadXMLException
	{
		if      (type.equals("int")   ) return Integer.TYPE;
		else if (type.equals("double")) return Double.TYPE;
		else if (type.equals("Color") ) return java.awt.Color.class;
		else if (type.equals("String")) return String.class;
		else throw new BadXMLException("Unknown type "+type);
	}
	private Object toObject(String value, String type) throws BadXMLException
	{
		try
		{
			if      (type.equals("int")   ) return new Integer(value);
			else if (type.equals("double")) return new Double(value);
			else if (type.equals("Color") ) return toColor(value);
			else if (type.equals("String")) return value;
			else throw new BadXMLException("Unknown type "+type);
		}
		catch (BadXMLException x) { throw x; }
		catch (Throwable t)
		{
			throw new BadXMLException("Error converting parameter "+value+" to "+type);
		}
	}
	abstract DataSource getDataSource();
	protected Object ds;
	private String className;
	private String[] values;
	private String[] types;
	private int vc = 0;
	private int tc = 0;
}
class AxisLabelsTraverser extends XMLNodeTraverser
{
	AxisLabelsTraverser(Node node) throws BadXMLException
	{
		traverse(node);
	}
	protected void handleElement(Element node, String name) throws BadXMLException
	{
		if      (name.equals("axisLabel")) traverse(node);
		else super.handleElement(node,name);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if      (name.equals("value")) labels.addElement(value);
		else if (name.equals("type")) type = value;
		else super.handleAttributeNode(node,name,value);
	}
	String[] getLabels()
	{
		String[] result = new String[labels.size()];
		labels.copyInto(result);
		return result;
	}
	String getType()
	{
		return type;
	}
	private String type;
	private Vector labels = new Vector();
}
class StatisticsTraverser extends XMLNodeTraverser implements Statistics
{
	StatisticsTraverser(Node node) throws BadXMLException
	{
		int maxSize = node.getChildNodes().getLength();
		names = new String[maxSize];
		values = new double[maxSize];
		traverse(node);
		if (cn < maxSize) 
		{
			String[] copy = new String[cn];
			System.arraycopy(names,0,copy,0,cn);
			names = copy;
		}
		if (cv < maxSize) 
		{
			double[] copy = new double[cn];
			System.arraycopy(values,0,copy,0,cv);
			values = copy;
		}
	}
	protected void handleElement(Element node, String name) throws BadXMLException
	{
		if      (name.equals("statistic")) traverse(node);
		else super.handleElement(node,name);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if      (name.equals("value")) values[cv++] = toDouble(value);
		else if (name.equals("name")) names[cn++] = value;
		else super.handleAttributeNode(node,name,value);
	}
	public String[] getStatisticNames()
	{
		return names;
	}
	public double getStatistic(String name)
	{
		for (int i=0; i<names.length; i++)
		{
			if (name.equals(names[i])) return values[i];
		}
		return 0;
	}
	private String[] names;
	private double[] values;
	private int cn = 0;
	private int cv = 0;
}
class Bins1DNodeTraverser extends XMLNodeTraverser
{
	Bins1DNodeTraverser(Node node) throws BadXMLException
	{
		traverse(node);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if (name.equals("title")) title = value;
		else super.handleAttributeNode(node,name,value);
	}
	protected void handleTextNode(Text node, String name) throws BadXMLException
	{
		StringTokenizer lineTokens = new StringTokenizer(node.getData());
		int lines = lineTokens.countTokens();
		for (int l=0; lineTokens.hasMoreTokens(); l++)
		{
			StringTokenizer valueTokens = new StringTokenizer(lineTokens.nextToken().trim(),",");
			int n = valueTokens.countTokens();
			if (data == null) data = new double[n][lines];
			else if (n != data.length) throw new BadXMLException("Inconsistent number of entries in bins1d data at line "+l);
			
			for (int i=0; i<n; i++) data[i][l] = toDouble(valueTokens.nextToken());
		}
	}
	String getTitle() 
	{
		return title;
	}
	double[][] getData() 
	{
		return data;
	}
	private String title;
	private double[][] data;
}
class BinnedDataAxisAttributesNodeTraverser extends XMLNodeTraverser
{
	BinnedDataAxisAttributesNodeTraverser(Node node) throws BadXMLException
	{
		traverse(node);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if      (name.equals("axis")) axis = value;
		else if (name.equals("min"))  min = toDouble(value);
		else if (name.equals("max"))  max = toDouble(value);
		else if (name.equals("numberOfBins")) nBins = toInt(value);
		else if (name.equals("type")) type = XMLPrintWriter.convertStringToAxisType(value);
		else super.handleAttributeNode(node,name,value);
	}
	String getAxis() { return axis; }
	double getMin() { return min; }
	double getMax() { return max; }
	int getBins() { return nBins; }
	int getType() { return type; }
	private String axis;
	private double min,max;
	private int nBins;
	private int type;
}
class XYPointsNodeTraverser extends XMLNodeTraverser
{
   XYPointsNodeTraverser(Node node) throws BadXMLException
   {
      traverse(node);
   }
   protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if (name.equals("title")) title = value;
      else if (name.equals("dimensions")) dim = toInt(value); 
		else super.handleAttributeNode(node,name,value);
	}
	protected void handleTextNode(Text node, String name) throws BadXMLException
	{
		StringTokenizer lineTokens = new StringTokenizer(node.getData());
		int lines = lineTokens.countTokens();
		for (int l=0; lineTokens.hasMoreTokens(); l++)
		{
			StringTokenizer valueTokens = new StringTokenizer(lineTokens.nextToken().trim(),",");
			int n = valueTokens.countTokens();
			if (data == null) data = new double[n][lines];
			else if (n != data.length) throw new BadXMLException("Inconsistent number of entries in bins1d data at line "+l);
			
			for (int i=0; i<n; i++) data[i][l] = toDouble(valueTokens.nextToken());
		}
	}
	String getTitle() 
	{
		return title;
	}
	double[][] getData() 
	{
		return data;
	}
	private String title;
   private int dim;
	private double[][] data;
}
class XMLXYDataSource implements XYDataSource, HasStatistics
{
	XMLXYDataSource(XYPointsNodeTraverser xy,
						 PointDataAxisAttributesNodeTraverser paa,
						 Statistics stats)
	{
		this.xy = xy;
		this.paa = paa;
		this.stats = stats;
   }
	public int getAxisType()
	{
		return paa.getType();
	}
	public String getTitle()
	{
		return xy.getTitle();
	}
	public Statistics getStatistics()
	{
		return stats;
	}
   public double getX(int index)
   {
      return xy.getData()[0][index];
   }
   public double getY(int index)
   {
      return xy.getData()[1][index];
   }
   public double getPlusError(int index)
   {
      return xy.getData()[2][index];
   }
   public double getMinusError(int index)
   {
      return xy.getData()[3][index];
   }
   public int getNPoints()
   {
      return xy.getData()[0].length;
   }
	private Statistics stats;
	private XYPointsNodeTraverser xy;
	private PointDataAxisAttributesNodeTraverser paa;
}
class XML1DHistDataSource implements Rebinnable1DHistogramData, HasStatistics
{
	XML1DHistDataSource(Bins1DNodeTraverser b1d,
						BinnedDataAxisAttributesNodeTraverser baa,
						AxisLabelsTraverser labels,
						Statistics stats)
	{
		this.b1d = b1d;
		this.baa = baa;
		this.labels = labels;
		this.stats = stats;
	}
	public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry)
	{
		return b1d.getData();
	}
	public double getMin()
	{
		return baa.getMin();
	}
	public double getMax()
	{
		return baa.getMax();
	}
	public int getBins()
	{
		return baa.getBins();
	}
	public boolean isRebinnable()
	{
		return false;
	}
	public int getAxisType()
	{
		return baa.getType();
	}
	public String[] getAxisLabels()
	{
		return labels == null ? null : labels.getLabels();
	}
	public String getTitle()
	{
		return b1d.getTitle();
	}
	public Statistics getStatistics()
	{
		return stats;
	}
	private Statistics stats;
	private Bins1DNodeTraverser b1d;
	private BinnedDataAxisAttributesNodeTraverser baa;
	private AxisLabelsTraverser labels;
}
