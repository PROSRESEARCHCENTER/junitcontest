package jas.hist;

import jas.util.xml.XMLNodeTraverser;
import jas.util.xml.XMLNodeTraverser.BadXMLException;

import java.util.StringTokenizer;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Build a DataSource from a DOM node
 */

class Data2DTraverser extends XMLNodeTraverser
{
	Data2DTraverser(Node node) throws BadXMLException
	{
		traverse(node);
	}
	protected void handleElement(Element node, String name) throws BadXMLException
	{
		if      (name.equals("bins2d")) b2d = new Bins2DNodeTraverser(node);
		else if (name.equals("points")) p2d = new Points2DNodeTraverser(node);
		else if(name.equals("pointDataAxisAttributes")) paa[paxis++] = new PointDataAxisAttributesNodeTraverser(node);//bug? axis++ ???
		else if (name.equals("binnedDataAxisAttributes")) baa[baxis++] = new BinnedDataAxisAttributesNodeTraverser(node);
		else if (name.equals("class")) ct = new ClassNodeTraverser(node);
		else if (name.equals("datasource")) ct = new DataSourceNodeTraverser(node);
		else if (name.equals("statistics")) stats = new StatisticsTraverser(node);
		else if (name.equals("style2d")) {
			if(type.equals("scatter2d")){
				JASHistScatterPlotStyle scatstyle=new JASHistScatterPlotStyle();
				this.style = scatstyle;
				scst.traverse(node,scatstyle);
			}
			else if(type.equals("histogram2d")) st.traverse(node,style=new JASHist2DHistogramStyle());
			else throw new BadXMLException("type attribute for data2d element must be scatter2d of histogram2d.");
		}
		else super.handleElement(node,name);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if (name.equals("type")) type = value;  
      else if (name.equals("name")) refName = value;
      else super.handleAttributeNode(node,name,value);
	}
	DataSource getDataSource() throws BadXMLException
	{
		if (ct != null) return ct.getDataSource();
		else if(b2d != null) return new XML2DHistDataSource(b2d,baa[0],baa[1],stats);
		else return new XML2DScatterDataSource(paa[0].getType(),paa[1].getType(),p2d.getTitle(),p2d.getData());
	}
	JASHistStyle getStyle()
	{
		return style;
	}
   String getRefName()
   {
       return refName;
   }
   private String type;
	private int paxis = 0;//will this counting work?
	private int baxis = 0;//will this counting work?
	private JASHist2DHistogramStyle style;
	private ConstructorNodeTraverser ct = null;
	private StatisticsTraverser stats = null;
	private Style2DNodeTraverser st = new Style2DNodeTraverser();
	private ScatterStyleNodeTraverser scst = new ScatterStyleNodeTraverser();
	private Bins2DNodeTraverser b2d;
	private BinnedDataAxisAttributesNodeTraverser[] baa = new BinnedDataAxisAttributesNodeTraverser[2];
	private PointDataAxisAttributesNodeTraverser[] paa = new PointDataAxisAttributesNodeTraverser[2];
	private Points2DNodeTraverser p2d;
   private String refName;
}


class Bins2DNodeTraverser extends XMLNodeTraverser
{
	Bins2DNodeTraverser(Node node) throws BadXMLException
	{
		traverse(node);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if      (name.equals("title")) title = value;
		else if (name.equals("xSize")) xSize = toInt(value);
		else if (name.equals("ySize")) ySize = toInt(value);
		else super.handleAttributeNode(node,name,value);
	}
	protected void handleTextNode(Text node, String name) throws BadXMLException
	{
		StringTokenizer lineTokens = new StringTokenizer(node.getData(),"\n\r");
		int lines = lineTokens.countTokens();
		if (lines < xSize * ySize || lines > xSize*ySize + 1) throw new BadXMLException("Inconsistent data length for bins2d (lines="+lines+")");
		int x = 0;
		int y = 0;
		for (int l=0; l<xSize*ySize; l++)
		{
			StringTokenizer valueTokens = new StringTokenizer(lineTokens.nextToken().trim(),",");
			int n = valueTokens.countTokens();
			if (data == null) data = new double[n][xSize][ySize];
			else if (n != data.length) throw new BadXMLException("Inconsistent number of entries in bins2d data at line "+l);
			
			for (int i=0; i<n; i++) data[i][x][y] = toDouble(valueTokens.nextToken());
			if (++y == ySize) { y = 0; x++; }
		}
	}
	String getTitle() 
	{
		return title;
	}
	double[][][] getData() 
	{
		return data;
	}
	private String title;
	private double[][][] data;
	private int xSize, ySize;
}
class XML2DHistDataSource implements Rebinnable2DHistogramData, HasStatistics
{
	XML2DHistDataSource(Bins2DNodeTraverser b2d,
						BinnedDataAxisAttributesNodeTraverser xba,
						BinnedDataAxisAttributesNodeTraverser yba, Statistics stats) throws BadXMLException
	{
		this.xba = xba;
		this.yba = yba;
		this.b2d = b2d;
		this.stats =stats;
	}
	public double[][][] rebin(int xbins, double xmin, double xmax, 
							  int ybins, double ymin, double ymax, 
							  boolean wantErrors, boolean hurry, boolean xxx)
	{
		return b2d.getData();
	}
	public double getXMin()
	{
		return xba.getMin();
	}
	public double getXMax()
	{
		return xba.getMax();
	}
	public double getYMin()
	{
		return yba.getMin();
	}
	public double getYMax()
	{
		return yba.getMax();
	}
	public int getXBins()
	{
		return b2d.getData()[0].length;
	}
	public int getYBins()
	{
		return  b2d.getData()[0][0].length;
	}
	public boolean isRebinnable()
	{
		return false;
	}
	public int getXAxisType()
	{
		return xba.getType();
	}
	public int getYAxisType()
	{
		return yba.getType();
	}
	public String[] getXAxisLabels()
	{
		return null;
	}
	public String[] getYAxisLabels()
	{
		return null;
	}
	public String getTitle()
	{
		return b2d.getTitle();
	}
	public Statistics getStatistics()
	{
		return stats;
	}
	
	private BinnedDataAxisAttributesNodeTraverser xba;
	private BinnedDataAxisAttributesNodeTraverser yba;
	private Bins2DNodeTraverser b2d;
	private Statistics stats;
}
