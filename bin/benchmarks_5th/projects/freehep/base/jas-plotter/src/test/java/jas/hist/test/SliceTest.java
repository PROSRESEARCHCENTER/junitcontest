package jas.hist.test;

import jas.hist.DataSource;
import jas.hist.HasSlices;
import jas.hist.JASHist;
import jas.hist.JASHistData;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.ScatterPlotSource;
import jas.hist.util.ScatterSliceAdapter;
import jas.hist.util.ScatterTwoDAdapter;
import jas.hist.util.SliceAdapter;
import jas.hist.util.SliceEvent;
import jas.hist.util.SliceListener;
import jas.hist.util.TwoDSliceAdapter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class SliceTest extends JPanel implements SliceListener
{
	SliceTest()
	{
		super(new BorderLayout());
		JPanel p = new JPanel(new GridLayout(1,2));
		hist1 = new JASHist();
		hist2 = new JASHist();
		p.add(hist1);
		p.add(hist2);
		
		add(p,BorderLayout.CENTER);
		add(new ButtonPanel(),BorderLayout.SOUTH);	
	}
	void setSliceData(Rebinnable1DHistogramData ds)
	{
		hist2.removeAllData();
		if (ds != null) hist2.addData(ds).show(true);
	}
	void setData(DataSource ds)
	{
		hist1.removeAllData();
		hist2.removeAllData();
		hash.clear();
		hist1.addData(ds).show(true);
		if (ds instanceof SliceAdapter)
		{
			((SliceAdapter) ds).addSliceListener(this);
		}
	}
	public void sliceAdded(SliceEvent e)
	{
		HasSlices source = (HasSlices) e.getSource();
		DataSource ds = source.getSlice(e.getIndex());
		JASHistData data = hist2.addData(ds);
		data.show(true);
		hash.put(ds,data);
	}
	public void sliceRemoved(SliceEvent e)
	{
		HasSlices source = (HasSlices) e.getSource();	
		DataSource ds = source.getSlice(e.getIndex());
		JASHistData data = (JASHistData) hash.get(ds);
		data.show(false);
	}
	public static void main(String[] argv)
	{
		JFrame f = new JFrame();
		f.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) {System.exit(0);}
		});		
		f.setContentPane(new SliceTest());
		f.pack();
		f.show();
	}
	class ButtonPanel extends JPanel implements ActionListener
	{
		ButtonPanel()
		{
			super(new FlowLayout());
			addButton(b1 = new JRadioButton("Scatter Plot"));
			addButton(b2 = new JRadioButton("Rebinnable 2D Plot"));
			addButton(b3 = new JRadioButton("NonRebinnable 2D Plot"));
		}
		private void addButton(JRadioButton b)
		{
			add(b);
			g.add(b);
			b.addActionListener(this);
		}
		public void actionPerformed(ActionEvent e)
		{
			Object b = e.getSource();
			if      (b == b1)
			{
				setData(new ScatterSliceAdapter(source));
			}
			else if (b == b2)
			{
				setData(new TwoDSliceAdapter(new ScatterTwoDAdapter(source)));
			}
			else if (b == b3)
			{
				setData(new TwoDSliceAdapter(new ScatterTwoDAdapter(source)
					{
						public boolean isRebinnable()
						{
							return false;
						}
					}));
			}
		}
		private ButtonGroup g = new ButtonGroup();
		private JRadioButton b1, b2, b3;
	}
	private final ScatterPlotSource source = new TestScatterPlotSource();
	private JASHist hist2;
	private JASHist hist1;
	private Hashtable hash = new Hashtable();
}
