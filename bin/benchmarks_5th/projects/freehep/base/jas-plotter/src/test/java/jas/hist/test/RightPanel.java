package jas.hist.test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

class RightPanel extends JPanel
{
	RightPanel()
	{
		setLayout(new BorderLayout());
		m_top = new TopRightPanel();
		m_bottom = new BottomRightPanel();
		add("North",m_top);
		add("Center",m_bottom);
	}
	public void addBinsChangeListener(ChangeListener l) 
	{
		m_bottom.addBinsChangeListener(l);
	}
	public void addEventsChangeListener(ChangeListener l) 
	{
		m_bottom.addEventsChangeListener(l);
	}
	public void addActionListener(ActionListener l)
	{
		m_top.addActionListener(l);
	}
	private TopRightPanel m_top;
	private BottomRightPanel m_bottom;
}
class TopRightPanel extends JPanel
{
	TopRightPanel()
	{
		m_about = new JButton("About...");
		m_prop = new JButton("Properties...");
		m_print = new JButton("Print...");
		m_xml = new JButton("Save As...");
		
		setLayout(new GridLayout(4,1));
		add(m_about);
		add(m_prop);
		add(m_print);
		add(m_xml);
	}
	void addActionListener(ActionListener l)
	{
		m_about.addActionListener(l);
		m_prop.addActionListener(l);
		m_print.addActionListener(l);
		m_xml.addActionListener(l);
	}
	private JButton m_about;
	private JButton m_prop;
	private JButton m_print;
	private JButton m_xml;
}
class BottomRightPanel extends JPanel
{
	BottomRightPanel()
	{
		setLayout(new FlowLayout());
		m_bins = new JSlider(JSlider.VERTICAL, 1, 200, 50);
		add(m_bins);
		m_events = new JSlider(JSlider.VERTICAL, 0, 10000, 1000);
		add(m_events);
	}
	public void addBinsChangeListener(ChangeListener l) 
	{
		m_bins.addChangeListener(l);
	}
	public void addEventsChangeListener(ChangeListener l) 
	{
		m_events.addChangeListener(l);
	}
	private JSlider m_bins;
	private JSlider m_events;
}
