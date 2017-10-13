package jas.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;





interface DateListener

{

	void dateChanged();

}

class DateModel

{

	DateModel(Calendar date)

	{

		this.date = date;

	}

	DateModel(Date date)

	{

		this.date = Calendar.getInstance();

		this.date.setTime(date);

	}

	synchronized void addDateListener(DateListener l)

	{

		listeners.addElement(l);

	}

	synchronized void removeDateListener(DateListener l)

	{

		listeners.removeElement(l);

	}

	synchronized void fireDateChanged()

	{

		Enumeration e = listeners.elements();

		while (e.hasMoreElements())

		{

			DateListener l = (DateListener) e.nextElement();

			l.dateChanged();

		}

	}

	void set(int field,int value)

	{

		date.set(field,value);

		fireDateChanged();

	}

	void roll(int field, boolean up)

	{

		date.roll(field,up);

		fireDateChanged();

	}

	void add(int field, int delta)

	{

		date.add(field,delta);

		fireDateChanged();

	}

	int get(int field)

	{

		return date.get(field);

	}

	Calendar getCalendar()

	{

		return date;

	}

	int getDaysInMonth()

	{
      
		return( date.getActualMaximum( Calendar.DAY_OF_MONTH ) ); 

	}

	Date getTime()

	{

		return date.getTime();

	}

	private Vector listeners = new Vector();

	private Calendar date;

}

class AbstractDateComboModel extends AbstractListModel implements ComboBoxModel, DateListener

{

	AbstractDateComboModel(DateModel model, int field)

	{

		this.model = model;

		this.field = field;

		model.addDateListener(this);

	}

	public void dateChanged()

	{

		fireContentsChanged(this,0,100);

	}

	public Object getElementAt(int index)

	{

		return element[index];

	}

	public int getSize()

	{

		return model.getCalendar().getMaximum(field)+1;

	}

	public Object getSelectedItem()

	{

		int i = model.get(field);

		return element[i];

	}

	public void setSelectedItem(Object value)

	{

		int i = ((Integer) value).intValue();

		model.set(field,i);

	}

	DateModel model; 

	int field;

	static Integer[] element;

	static

	{

		element = new Integer[100];

		for (int i=0; i<element.length; i++) element[i] = new Integer(i);

	}

}

class DateMonthModel extends AbstractDateComboModel

{

	DateMonthModel(DateModel model)

	{

		super(model,Calendar.MONTH);

	}

}

class MonthCellRenderer extends BasicComboBoxRenderer

{

	public Component getListCellRendererComponent

		(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus)

	{

		int i = ((Integer) value).intValue();



		return super.getListCellRendererComponent

			(list,month[i],index,isSelected,cellHasFocus);

	}

	private static final String[] month = {

		"January" , "February", "March", "April", "May", "June",

		"July", "August", "September", "October", "November", "December"};

}

class DateDayModel extends AbstractDateComboModel

{

	DateDayModel(DateModel model)

	{

		super(model,Calendar.DAY_OF_MONTH);

	}

	public int getSize()

	{

		return model.getDaysInMonth();

	}

	public Object getElementAt(int index)

	{

		return element[index+1];

	}

}

class DateHourModel extends AbstractDateComboModel

{

	DateHourModel(DateModel model)

	{

		super(model,Calendar.HOUR_OF_DAY);

	}

	public int getSize()

	{

		return 12;

	}

	public Object getSelectedItem()

	{

		int i = model.get(field);

		if (i >= 12) i -= 12;

		return element[i];

	}

	public void setSelectedItem(Object value)

	{

		int ampm = model.get(Calendar.AM_PM);

		int i = ((Integer) value).intValue();

		if (ampm > 0) i += 12;

		model.set(field,i);

	}

}

class DateMinuteModel extends AbstractDateComboModel

{

	DateMinuteModel(DateModel model)

	{

		super(model,Calendar.MINUTE);

	}

}

class DateSecondModel extends AbstractDateComboModel

{

	DateSecondModel(DateModel model)

	{

		super(model,Calendar.SECOND);

	}

}

class DateAMPMModel extends AbstractDateComboModel

{

	DateAMPMModel(DateModel model)

	{

		super(model,Calendar.AM_PM);

	}

	public void setSelectedItem(Object value)

	{

		int hour = model.get(Calendar.HOUR_OF_DAY);

		int i = ((Integer) value).intValue();

		if (i == 0 && hour >= 12) hour -= 12;

		if (i == 1 && hour <  12) hour += 12;

		model.set(Calendar.HOUR_OF_DAY,hour);

	}

}

class MinuteCellRenderer extends BasicComboBoxRenderer

{

	public Component getListCellRendererComponent

		(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus)

	{

		String s =f.format(value);



		return super.getListCellRendererComponent

			(list,s,index,isSelected,cellHasFocus);

	}

	private static Format f = new DecimalFormat("00");

}

class HourCellRenderer extends BasicComboBoxRenderer

{

	HourCellRenderer()

	{

		setHorizontalAlignment(SwingConstants.RIGHT);

	}

	public Component getListCellRendererComponent

		(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus)

	{

		int i = ((Integer) value).intValue();

		if (i==0) i = 12;

		String s = String.valueOf(i);

		return super.getListCellRendererComponent

			(list,s,index,isSelected,cellHasFocus);

	}	

}

class AMPMCellRenderer extends BasicComboBoxRenderer

{

	public Component getListCellRendererComponent

		(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus)

	{

		int i = ((Integer) value).intValue();



		return super.getListCellRendererComponent

			(list,ampm[i],index,isSelected,cellHasFocus);

	}

	private static String[] ampm = { "AM" , "PM" };

}

class DateYearModel extends AbstractDateComboModel

{	

	DateYearModel(DateModel model,int offset)

	{

		super(model,Calendar.YEAR);

		this.offset = offset;

	}

	public int getSize()

	{

		return element.length;

	}

	public Object getSelectedItem()

	{

		int i = model.get(field);

		return element[i-offset];

	}

	public void setSelectedItem(Object value)

	{

		int i = ((Integer) value).intValue();

		model.set(field,i+offset);

	}

	private int offset;

}

class YearCellRenderer extends BasicComboBoxRenderer

{

	YearCellRenderer(int offset)

	{

		this.offset = offset;

	}

	public Component getListCellRendererComponent

		(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus)

	{

		int i = ((Integer) value).intValue();



		return super.getListCellRendererComponent

			(list,String.valueOf(i+offset),index,isSelected,cellHasFocus);

	}

	private static int offset;

}



class CalendarHeader extends JComponent

{

	CalendarHeader(DateModel model)

	{

		this.model = model;

		int offset = 1950;



		setLayout(new FlowLayout());

		JComboBox day = new JComboBox(new DateDayModel(model));

		JComboBox month = new JComboBox(new DateMonthModel(model));

		JComboBox year  = new JComboBox(new DateYearModel(model,offset));



		month.setRenderer(new MonthCellRenderer());

		year.setRenderer(new YearCellRenderer(offset));



		JButton down = new JButton("<");

		JButton up = new JButton(">");

			

		down.addActionListener(new RollListener(-1));

		up.addActionListener(new RollListener(+1));



		add(down);

		add(day);

		add(month);

		add(year);

		add(up);

	}

	private DateModel model;

	private class RollListener implements ActionListener

	{

		RollListener(int delta)

		{

			this.delta = delta;

		}

		public void actionPerformed(ActionEvent evt)

		{

			model.add(Calendar.MONTH,delta);

		}

		private int delta;

	}

}

class TimeHeader extends JComponent

{

	TimeHeader(DateModel model)

	{

		this.model = model;



		setLayout(new FlowLayout());



		JComboBox hour = new JComboBox(new DateHourModel(model));

		JComboBox minute = new JComboBox(new DateMinuteModel(model));

		JComboBox second  = new JComboBox(new DateSecondModel(model));

		JComboBox ampm  = new JComboBox(new DateAMPMModel(model));



		hour.setRenderer(new HourCellRenderer());

		minute.setRenderer(new MinuteCellRenderer());

		second.setRenderer(new MinuteCellRenderer());

		ampm.setRenderer(new AMPMCellRenderer());



		add(hour);

		add(minute);

		add(second);

		add(ampm);

	}

	private DateModel model;

}

class CalendarPane extends JComponent implements DateListener, ActionListener

{

	CalendarPane(DateModel model)

	{

		this.model = model;

		model.addDateListener(this);



		setLayout(null);

		Insets insets = new Insets(1,1,1,1);



		days = new JButton[31];

		for (int i=0; i<31; i++) 

		{

			days[i] = new JButton(String.valueOf(i+1));

			days[i].addActionListener(this);

			//days[i].setMargin(insets);

			add(days[i]);

		}

		fg = days[30].getForeground();

		bg = days[30].getBackground();

		buttonSize = days[30].getPreferredSize();

		panelSize = new Dimension(buttonSize.width*7,buttonSize.height*7);

		hidden = new Point(buttonSize.width*10,buttonSize.height*10);



		for (int i=0; i<31; i++) days[i].setSize(buttonSize);



		for (int i=0; i<7; i++)

		{

			JLabel l = new JLabel(labels[i],SwingConstants.CENTER);

			add(l);

			l.setLocation(i*buttonSize.width,0);

			l.setSize(buttonSize);

		}



		layoutCalendar();

	}

	public Dimension getPreferredSize()

	{

		return panelSize;

	}

	public void actionPerformed(ActionEvent e)

	{

		String s = e.getActionCommand();

		int day = Integer.valueOf(s).intValue(); 

		model.set(Calendar.DAY_OF_MONTH,day);

	}

	public void dateChanged()

	{

		layoutCalendar();

	}

	void layoutCalendar()

	{

		Calendar selected = model.getCalendar();

		int day = selected.get(Calendar.DAY_OF_MONTH)-1;

		if (selectedButton != days[day])

		{

			if (selectedButton != null)

			{

				selectedButton.setForeground(fg);

				selectedButton.setBackground(bg);

				selectedButton.repaint();

			}

			selectedButton = days[day];

			selectedButton.setForeground(UIManager.getColor("textHighlightText"));

			selectedButton.setBackground(UIManager.getColor("textHighlight"));

			selectedButton.repaint();

		}



		Calendar date = (Calendar) selected.clone();

		date.set(Calendar.DAY_OF_MONTH,1);



		int i=0;

		for (; i<model.getDaysInMonth(); i++)

		{

			int x = (date.get(Calendar.DAY_OF_WEEK)-1) * buttonSize.width;

			int y = (date.get(Calendar.WEEK_OF_MONTH)) * buttonSize.height;

			days[i].setLocation(x,y);

			date.add(Calendar.DATE,1);

		}

		for (; i<31; i++) days[i].setLocation(hidden);

	}

	private JButton selectedButton;

	private Color fg;

	private Color bg;

	private Dimension buttonSize;

	private Dimension panelSize;

	private Point hidden;

	private DateModel model;

	private JButton[] days;

	private final static String[] labels = {

		"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

}

class CalendarTest

{

	public static void main(String[] argv)

	{

		JFrame f = new JFrame("test");

		DateModel model = new DateModel(Calendar.getInstance());



		f.getContentPane().add(new CalendarHeader(model),BorderLayout.NORTH);

		f.getContentPane().add(new CalendarPane(model),BorderLayout.CENTER);

		f.getContentPane().add(new TimeHeader(model),BorderLayout.SOUTH);

		f.pack();

		f.show();

	}

}

public class DateChooser extends JASDialog

{

	public DateChooser(Frame f, Date d)

	{

		super(f,"Choose Date...");

		model = new DateModel(d);

		getContentPane().add(new CalendarHeader(model),BorderLayout.NORTH);

		getContentPane().add(new CalendarPane(model),BorderLayout.CENTER);

		getContentPane().add(new TimeHeader(model),BorderLayout.SOUTH);

		pack();

	}

	public Date getDate()

	{

		return model.getTime();

	}

	private DateModel model;

}
