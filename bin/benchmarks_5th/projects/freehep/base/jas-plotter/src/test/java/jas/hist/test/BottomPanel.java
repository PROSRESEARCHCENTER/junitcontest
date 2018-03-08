package jas.hist.test;

import jas.hist.JASHist;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistData;
import jas.hist.util.TwoDSliceAdapter;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class BottomPanel extends JPanel implements ItemListener,ChangeListener
{
	BottomPanel(JASHist hist)
	{
		m_gauss1.setStats();
		m_hist = hist;
		
		m_time = TimeGauss.create("Time 1",1000,new Date("1 Jan 1997"),new Date("2 Jan 1997"));
		
		JASHist1DHistogramStyle style = new JASHist1DHistogramStyle();
		style.setShowHistogramBars(false);
		style.setShowDataPoints(true); 
		m_gauss2.setStyle(style);

		setLayout(new GridLayout(2,5));

		add(box1 = new JCheckBox("Gaussian 1"));
		add(box2 = new JCheckBox("Gaussian 2"));
		add(box3 = new JCheckBox("Gaussian 3"));
		add(box4 = new JCheckBox("String 1"));
		add(box5 = new JCheckBox("Time 1"));

		add(box6 = new JCheckBox("2D Gaussian 1"));
		add(box7 = new JCheckBox("2D Gaussian 2"));
		add(box8 = new JCheckBox("ScatterPlot 1"));
		add(box9 = new JCheckBox("ScatterPlot 2"));
		add(box10 = new JCheckBox("TimeScatter"));
		
		box1.addItemListener(this);
		box2.addItemListener(this);
		box3.addItemListener(this);
		box4.addItemListener(this);
		box5.addItemListener(this);
		box6.addItemListener(this);
		box7.addItemListener(this);
		box8.addItemListener(this);
		box9.addItemListener(this);
		box10.addItemListener(this);

		//JASHist.registerFunction(StraightLineFunction.class,"Straight Line");
		//JASHist.registerFunction(GaussianFunction.class,"Gaussian");
		//JASHist.registerFunction(new SplineFactory());
		//JASHist.registerFunction(new SumFunctionFactory());
		//JASHist.registerFitter(LeastSquaresFit.class,"Least Squares Fitter");
	}
	public void stateChanged(ChangeEvent ev) 
	{
		int events = ((JSlider) ev.getSource()).getValue();
		m_gauss1.setSize(events);
		m_gauss2.setSize(events);
		//m_gauss3.setSize(events);
		m_string.setSize(events);
		m_time.setSize(events);
		m_gauss6.setSize(events);
		m_gauss7.setSize(events);
	}
	public void itemStateChanged(ItemEvent evt)
	{
		if (recursive) return;
		recursive = true;

		Object source = evt.getSource();
		if (source == box5)
		{
			if (m_mode != 5)
			{
				m_hist.removeAllData();
				data5 = m_hist.addData(m_time);
				m_mode = 5;
				box1.setSelected(false);
				box2.setSelected(false);
				box3.setSelected(false);
				box4.setSelected(false);
				box6.setSelected(false);
				box7.setSelected(false);
				box8.setSelected(false);
				box9.setSelected(false);
				box10.setSelected(false);
			}
			data5.show(box5.isSelected());
		}
		else if (source == box4)
		{
			if (m_mode != 4)
			{
				m_hist.removeAllData();
				data4 = m_hist.addData(m_string);
				m_mode = 4;
				box1.setSelected(false);
				box2.setSelected(false);
				box3.setSelected(false);
				box5.setSelected(false);
				box6.setSelected(false);
				box7.setSelected(false);
				box8.setSelected(false);
				box9.setSelected(false);
				box10.setSelected(false);
			}
			data4.show(box4.isSelected());
		}
		else if (source == box6 || source == box7)
		{
			if (m_mode != 6)
			{
				m_hist.removeAllData();
				data6 = m_hist.addData(new TwoDSliceAdapter(m_gauss6));
				data7 = m_hist.addData(m_gauss7);
				m_mode = 6;
				box1.setSelected(false);
				box2.setSelected(false);
				box3.setSelected(false);
				box4.setSelected(false);
				box5.setSelected(false);
				box8.setSelected(false);
				box9.setSelected(false);
				box10.setSelected(false);
			}
			data6.show(box6.isSelected());
			data7.show(box7.isSelected());
		}
		else if (source == box8 || source == box9)
		{
			if (m_mode != 8)
			{
				m_hist.removeAllData();
				data8 = m_hist.addData(m_scat8);
				data9 = m_hist.addData(m_scat9);
				m_mode = 8;
				box1.setSelected(false);
				box2.setSelected(false);
				box3.setSelected(false);
				box4.setSelected(false);
				box5.setSelected(false);
				box6.setSelected(false);
				box7.setSelected(false);
				box10.setSelected(false);
			}
			data8.show(box8.isSelected());
			data9.show(box9.isSelected());
		}
		else if (source == box10)
		{
			if (m_mode != 10)
			{
				m_hist.removeAllData();
				data10 = m_hist.addData(m_scat10);
				m_mode = 10;
				box1.setSelected(false);
				box2.setSelected(false);
				box3.setSelected(false);
				box4.setSelected(false);
				box5.setSelected(false);
				box6.setSelected(false);
				box7.setSelected(false);
				box8.setSelected(false);
				box9.setSelected(false);
			}
			data10.show(box10.isSelected());
		}
		else 
		{
			if (m_mode != 1)
			{
				m_hist.removeAllData();
				data1 = m_hist.addData(m_gauss1);
				data2 = m_hist.addData(m_gauss2);
				data3 = m_hist.addData(m_gauss3);
				m_mode = 1;
				box4.setSelected(false);
				box5.setSelected(false);
				box6.setSelected(false);
				box7.setSelected(false);
				box8.setSelected(false);
				box9.setSelected(false);
				box10.setSelected(false);
			}
			data1.show(box1.isSelected());
			data2.show(box2.isSelected());
			data3.show(box3.isSelected());
		}

		recursive = false;
	}
	private JCheckBox box1;
	private JCheckBox box2;
	private JCheckBox box3;
	private JCheckBox box4;
	private JCheckBox box5;
	private JCheckBox box6;
	private JCheckBox box7;
	private JCheckBox box8;
	private JCheckBox box9;
	private JCheckBox box10;

	private JASHistData data1;
	private JASHistData data2;
	private JASHistData data3;
	private JASHistData data4;
	private JASHistData data5;
	private JASHistData data6;
	private JASHistData data7;
	private JASHistData data8;
	private JASHistData data9;
	private JASHistData data10;

	private Gauss m_gauss1 = new Gauss("Gaussian 1",1000,1,0,0);
	private Gauss m_gauss2 = new Gauss("Gaussian 2",1000,1,0.5,-100);
	private Gauss m_gauss3 = new LiveGauss("Gaussian 3",0,1,1,0);
	private Gauss m_string = new StringGauss("String 1",1000,1,0,0);
	private Gauss m_time;   
	private Gauss2D m_gauss6 = new Gauss2D("Gaussian 6",1000);
	private Gauss2D m_gauss7 = new Gauss2D("Gaussian 7",1000);
	private TestScatterPlotSource m_scat8 = new TestScatterPlotSource();
	private TestScatterPlotSource m_scat9 = new TestScatterPlotSource(10);
	//private TestScatterPlotSource m_scat10 = new TimeScatterPlotSource();
	private TestCustomOverlay m_scat10 = new TestCustomOverlay();

	private JASHist m_hist;
	private int m_mode = 0;
	private boolean recursive = false;
}
