package jas.hist.test;

import jas.hist.HasStatistics;
import jas.hist.HasStyle;
import jas.hist.HistogramUpdate;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistStyle;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Statistics;
import jas.util.xml.HasXMLRepresentation;
import jas.util.xml.XMLWriter;

import java.util.Observable;

public class Gauss extends Observable implements Rebinnable1DHistogramData, HasStyle, HasStatistics, HasXMLRepresentation
{
	public Gauss()
	{
		this("Gaussian",1000,1,0,0);
	}
	public void writeAsXML(XMLWriter pw)
	{
		pw.println("<datasource name=\""+getClass().getName()+"\">");
		pw.println("<param type=\"String\" value=\""+m_name+"\"/>");
		pw.println("<param type=\"int\" value=\""+m_entries+"\"/>");
		pw.println("<param type=\"double\" value=\""+(m_max-m_min)+"\"/>");
		pw.println("<param type=\"double\" value=\""+m_min+"\"/>");
		pw.println("<param type=\"double\" value=\""+m_offset+"\"/>");
		pw.println("</datasource>");
	}
	public Gauss(String name, int entries,double max,double offX,double offY)
	{
		if (entries > MAXSIZE) throw new IllegalArgumentException("Gauss: Size too big");

		m_tuple = new double[MAXSIZE];
		m_entries = entries;
		m_min = offX;
		m_max = offX + max;
		m_offset = offY;
		m_name = name;

		for (int j=0; j<MAXSIZE; j++)
		{
			double k = Math.random()+Math.random()+Math.random()+Math.random()+Math.random()+Math.random();
			k = offX + max*k/6;
			m_tuple[j] = k;
		}
	}
	public void setStyle(JASHist1DHistogramStyle style)
	{
		this.style = style;
	}
	public JASHistStyle getStyle()
	{
		return this.style;
	}
	public String getTitle()
	{
		return m_name;
	}
	public double getMin()
	{
		return m_min;
	}
	public double getMax()
	{
		return m_max;
	}
	public boolean isRebinnable()
	{
		return true;
	}
	public String[] getAxisLabels()
	{
		return null;
	}
	public int getBins()
	{
		return 40;
	}
	public int getAxisType()
	{
		return Rebinnable1DHistogramData.DOUBLE;
	}

	public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry)
	{
		synchronized (this) { setChanged(); }

		double[] hist = new double[bins];
		double binWidth = (max - min)/bins;

		for (int j=0; j<bins; j++) hist[j] = m_offset;
		for (int k=0; k<m_entries; k++)
		{
			double v = m_tuple[k];
			if (v < min) continue;
			int bin = (int) ((v - min)/binWidth);
			if (bin<bins) hist[bin] += 1;
		}
		double[][] result = new double[1][];
		result[0] = hist;
		return result;
	}
	public int getSize()
	{
		return m_entries;
	}
	public void setSize(int newSize)
	{
		if (newSize > MAXSIZE) throw new IllegalArgumentException("Gauss: Size too big");
		m_entries = newSize;
		synchronized (this) { notifyObservers(new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true)); }
	}
	public double getData(int i)
	{
		return m_tuple[i];
	}
	public Statistics getStatistics()
	{
		return stats;
	}
	double m_tuple[];
	int m_entries;
	double m_max;
	double m_min;
	double m_offset;
	String m_name;
	protected final static int MAXSIZE = 10000;
	private JASHist1DHistogramStyle style;
	private Statistics stats = new GaussStatistics();
	private Statistics paulstats = new PaulStatistics();
	
	
	public void setStats() {
		stats = paulstats;
		
	}
	
	private class GaussStatistics implements Statistics
	{	
		public String[] getStatisticNames()
		{
			return statNames;
			
		}

		public double getStatistic(String name)
		{
				if (name == statNames[0]) return m_entries;
				if (name == statNames[1]) return (m_max + m_min)/2;
				if (name == statNames[2]) return (m_max - m_min);
				return 0;

			
		}
	}
	
	public class PaulStatistics implements Statistics
	{	
		public String[] getStatisticNames()
		{
			return statNames2;
			
		}

		public double getStatistic(String name)
		{
				if (name == statNames2[0]) return m_entries;
				if (name == statNames2[1]) return (m_max + m_min)/2;
				if (name == statNames2[2]) return (m_max - m_min);
				if (name == statNames2[3]) return (m_entries*10);
				return 0;
		}
	}
	
	private static final String[] statNames = {"Entries","Mean","RMS"};
	private static final String[] statNames2 = {"Entries","Mean","RMS", "10Entries"};
}
