package jas.hist;

import jas.util.xml.XMLNodeTraverser;

import java.io.Serializable;
import java.util.Observable;
import java.util.StringTokenizer;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


public class XML2DScatterDataSource extends Observable implements ScatterPlotSource, Serializable
{
	public XML2DScatterDataSource(int p_xAxisType, int p_yAxisType,
								  String p_title, double[][] p_data) {
		
		m_xAxisType = p_xAxisType;
		m_yAxisType = p_yAxisType;
		m_title = p_title;
		m_data = p_data;
		
		//now calculate the x and y min and max once and for all
		//(so we can return them quickly)
		m_xmin = m_data[0][0];
		m_xmax = m_xmin;
		m_ymin = m_data[1][0];
		m_ymax = m_ymin;
		for (int i=1; i < m_data[0].length; i++) {
			if (m_data[0][i] < m_xmin) m_xmin = m_data[0][i];
			if (m_data[0][i] > m_xmax) m_xmax = m_data[0][i];
			if (m_data[1][i] < m_ymin) m_ymin = m_data[1][i];
			if (m_data[1][i] > m_ymax) m_ymax = m_data[1][i];
		}
	}
	public double getXMin() {
		return m_xmin;
	}
	public double getXMax() {
		return m_xmax;
	}
	public double getYMin() {
		return m_ymin;
	}
	public double getYMax() {
		return m_ymax;
	}
	public int getXAxisType() {
		return m_xAxisType;
	}
	public int getYAxisType() {
		return m_yAxisType;
	}
	public ScatterEnumeration startEnumeration(double xMin, double xMax, double yMin, double yMax) {
		return new FixedEnumeration(m_data, xMin, xMax, yMin, yMax);
	}
	public ScatterEnumeration startEnumeration() {
		return new FixedEnumeration(m_data);
	}
	public String getTitle() {
		return m_title;
	}
	
	
	
	private double m_xmin;
	private double m_xmax;
	private double m_ymin;
	private double m_ymax;
	private int m_xAxisType;
	private int m_yAxisType;
	private String m_title;
	private double[][] m_data;
	
	private class FixedEnumeration implements ScatterEnumeration {
		public FixedEnumeration(double[][] p_data) {
			m_data = p_data;
			selectAll = true;
		}
		public FixedEnumeration(double[][] p_data, double xMin, double xMax, double yMin, double yMax) {
			m_data = p_data;
			selectAll = false;
			m_xmin = xMin;
			m_xmax = xMax;
			m_ymin = yMin;
			m_ymax = yMax;
		}
		public boolean getNextPoint(double[] a) {
			
			if (selectAll) {
				if (pos < (m_data[0].length - 1)) {
					a[0] = m_data[0][pos];
					a[1] = m_data[1][pos++];
					return true;
				} else {
					return false;
				}
			} else {
				
				while (!((m_data[0][pos] >= m_xmin) && (m_data[0][pos] <= m_xmax) &&
						 (m_data[1][pos] >= m_ymin) && (m_data[1][pos] <= m_ymax))) {
					//skip points that don't satisfy the conditions
					pos++;
					if(pos>m_data[0].length-1){
						return false;
					}
					
				}
				//okay, if we're here the point satisfies the min and max conditions
				a[0] = m_data[0][pos];
				a[1] = m_data[1][pos++];
				return pos<m_data[0].length;
			}
		}
		public void resetEndPoint() {
			//what does this do?
			//(it seems like Gauss2D.java doesn't know what to do here either)
		}
		public void restart() {
			pos = 0;
		}
		private int pos = 0;
		private double[][] m_data;
		private boolean selectAll;//true if we return all the points, false if must pass min and max conditions
		private double m_xmin;
		private double m_xmax;
		private double m_ymin;
		private double m_ymax;
	}
	
	
}

class Points2DNodeTraverser extends XMLNodeTraverser
{
	Points2DNodeTraverser(Node node) throws BadXMLException{
		traverse(node);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException{
		if      (name.equals("title")) title = value;
		else if (name.equals("dimensions")) dimensions = toInt(value);
		else super.handleAttributeNode(node,name,value);
	}
	protected void handleTextNode(Text node, String name) throws BadXMLException
	{
		StringTokenizer lineTokens = new StringTokenizer(node.getData());
		int lines = lineTokens.countTokens();
		int x = 0;
		
		for (int l=0; lineTokens.hasMoreTokens(); l++)
		{
			StringTokenizer valueTokens = new StringTokenizer(lineTokens.nextToken().trim(),",");
			int n = valueTokens.countTokens();
			if (data == null) data = new double[n][lines];
			else if (n != data.length) throw new BadXMLException("Inconsistent number of entries in bins2d data at line "+l);
			
			for (int i=0; i<n; i++) data[i][x] = toDouble(valueTokens.nextToken());
			x++;
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
	int getDimensions(){
		return dimensions;
	}
	private String title;
	private double[][] data;
	private int dimensions;

}

class PointDataAxisAttributesNodeTraverser extends XMLNodeTraverser
{
	PointDataAxisAttributesNodeTraverser(Node node) throws BadXMLException
	{
		traverse(node);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		if      (name.equals("axis")) axis = value;
		else if (name.equals("type")) type = XMLPrintWriter.convertStringToAxisType(value);
		else super.handleAttributeNode(node,name,value);
	}
	String getAxis() { return axis; }
	int getType() { return type; }
	private String axis;
	private int type;
}

