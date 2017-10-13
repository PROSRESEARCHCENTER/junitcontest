package jas.hist;

import jas.hist.normalization.Normalizer;
import jas.util.CheckBoxBorderPanel;
import jas.util.ColorChooser;
import jas.util.DateChooser;
import jas.util.JASTextField;
import jas.util.JTextFieldBinding;
import jas.util.PropertyBinding;
import jas.util.PropertyDialog;
import jas.util.PropertyPage;
import jas.util.PropertySite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import tablelayout.TableLayout;


public final class JASHistPropertyDialog extends PropertyDialog
{
   public JASHistPropertyDialog(Frame f,JASHist bean)
   {
      this(f, bean, DEFAULT);
   }
   public JASHistPropertyDialog(final Frame f, final JASHist bean, final byte axis)
   {
      super(f,"Histogram properties...",bean);
      
      addPage("General",new JASHistPropGeneral(), false);
      addPage("Y Axis" ,new JASHistPropYAxis(axis == Y_AXIS_RIGHT), axis == Y_AXIS_LEFT || axis == Y_AXIS_RIGHT);
      addPage("X Axis" ,new JASHistPropXAxis(), axis == X_AXIS);
      DataManager dm = bean.getDataManager();
      if (bean.numberOfDataSets() > 0) addPage("Data",new JASHistPropData(dm), false);
      if (dm instanceof SupportsFunctions)
         addPage("Functions",new JASHistPropFunctions(), false);
      pack();
   }
   final static byte DEFAULT = 0;
   final static byte Y_AXIS_LEFT = 1;
   final static byte Y_AXIS_RIGHT = 2;
   final static byte X_AXIS = 3;
}
final class JASHistPropGeneral extends PropertyPage
{
   public JASHistPropGeneral()
   {
      setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
      
      JPanel p1 = new FixedHeightPanel();
      p1.add(new JLabel("Title"));
      JASTextField title = new JASTextField(40);
      p1.add(title);
      add(p1);
      
      JPanel p4 = new FixedHeightPanel();
/*
                JCheckBox axis = new JCheckBox("Axis Bounding Box");
                axis.setMnemonic('B');
                p4.add(axis);
 */
      JComboBox legend = new JComboBox();
      legend.addItem("Hide Legend");
      legend.addItem("Automatic");
      legend.addItem("Show Legend");
      p4.add(legend);
      add(p4);
      
      JPanel p3 = new FixedHeightPanel();
      p3.setBorder(BorderFactory.createTitledBorder("Border"));
      JComboBox btype = new JComboBox();
      btype.addItem("None");
      btype.addItem("Bevel In");
      btype.addItem("Bevel Out");
      btype.addItem("Etched");
      btype.addItem("Line");
      btype.addItem("Shadow");
      p3.add(btype);
/*
                p3.add(new JLabel("Width"));
                SpinBox bwidth = new SpinBox(2,0,20);
                p3.add(bwidth);
 */
      add(p3);
      
      JPanel p2 = new FixedHeightPanel();
      p2.setBorder(BorderFactory.createTitledBorder("Color"));
      p2.add(new JLabel("Background"));
      ColorChooser back = new ColorChooser();
      p2.add(back);
      p2.add(new JLabel("Foreground"));
      ColorChooser fore = new ColorChooser();
      p2.add(fore);
      p2.add(new JLabel("Data Area"));
      ColorChooser data = new ColorChooser();
      p2.add(data);
      add(p2);
      add(Box.createVerticalGlue());
      
      addBinding(new PropertyBinding(title,"Title"));
      addBinding(new PropertyBinding(back,"Background"));
      addBinding(new PropertyBinding(fore,"Foreground"));
      addBinding(new PropertyBinding(data,"DataAreaColor"));
      addBinding(new PropertyBinding(btype,"DataAreaBorderType"));
/*
                addBinding(new PropertyBinding(bwidth,"DataAreaBorderWidth"));
                addBinding(new PropertyBinding(axis,"AxisBoundingBox"));
 */
      addBinding(new PropertyBinding(legend,"ShowLegend"));
   }
   public String getHelpTopic()
   {
      return "userInterface.propertiesDialog.generalTab";
   }
}
final class JASHistPropYAxis extends PropertyPage implements ListSelectionListener, PropertySite
{
   public JASHistPropYAxis(final boolean selectY2)
   {
      m_init = false;
      
      setLayout(new BorderLayout());
      
      m_listModel = new DefaultListModel();
      m_list = new JList(m_listModel);
      if (selectY2) m_list.setSelectedIndex(1);
      m_list.addListSelectionListener(this);
      JScrollPane scroll = new JScrollPane();
      scroll.setViewportView(m_list);
      scroll.setPreferredSize(new Dimension(100,120));
      add(scroll,BorderLayout.WEST);
      
      m_propAxis = new JASHistPropAxis();
      m_propAxis.setPropertySite(this);
      add(m_propAxis,BorderLayout.CENTER);
   }
   public String getHelpTopic()
   {
      return "userInterface.propertiesDialog.axesTabs";
   }
   public void activate()
   {
      m_init = false; // Recalculate list of axes each time page is activated
   }
   public void doDataExchange(boolean set,Object bean)
   {
      if (!m_init)
      {
         JASHist hist = (JASHist) bean;
         JASHistAxis[] axes = hist.getYAxes();
         
         m_listModel.removeAllElements();
         for (int i=0; i<axes.length; i++)
         {
            if (axes[i] != null) m_listModel.addElement("Y"+i);
         }
         m_selected = 0;
         m_list.setSelectedIndex(0);
         m_jHist = hist;
         m_init = true;
      }
      JASHist hist = (JASHist) bean;
      JASHistAxis yAxis = hist.getYAxis(m_selected);
      m_propAxis.doDataExchange(set,yAxis);
      setChanged(false);
   }
   public void valueChanged(ListSelectionEvent evt)
   {
      if (!m_init) return;
      doDataExchange(true,m_jHist);
      m_selected = m_list.getSelectedIndex();
      doDataExchange(false,m_jHist);
   }
   public void callEnable()
   {
      setChanged(true);
   }
   private JASHistPropAxis m_propAxis;
   private boolean m_init;
   private int m_selected;
   private DefaultListModel m_listModel;
   private JList m_list;
   private JASHist m_jHist;
}

final class JASHistPropXAxis extends PropertyPage implements PropertySite
{
   public JASHistPropXAxis()
   {
      setLayout(new BorderLayout());
      m_propAxis = new JASHistPropAxis();
      m_propAxis.setPropertySite(this);
      add(m_propAxis,BorderLayout.CENTER);
   }
   public void doDataExchange(boolean set,Object bean)
   {
      JASHist hist = (JASHist) bean;
      JASHistAxis xAxis = hist.getXAxis();
      m_propAxis.doDataExchange(set,xAxis);
      setChanged(false);
   }
   public void callEnable()
   {
      setChanged(true);
   }
   public String getHelpTopic()
   {
      return "userInterface.propertiesDialog.axesTabs";
   }
   private JASHistPropAxis m_propAxis;
}
final class JASHistPropAxis extends PropertyPage implements ActionListener
{
   public JASHistPropAxis()
   {
      setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
      add(createTopPanel());
      add(createRangePanel());
      add(createBinsPanel());
      add(Box.createVerticalGlue());
   }
   private JPanel createTopPanel()
   {
      JASTextField label = new JASTextField(20);
      m_log = new JCheckBox("Logarithmic");
      m_log.setMnemonic('L');
      m_type = new JLabel();
      
      addBinding(new PropertyBinding(label,"Label"));
      addBinding(new PropertyBinding(m_log,"logarithmic"));
      
      JPanel p = new FixedHeightPanel(new TableLayout());
      p.add("0 0 rw", new JLabel("Label:"));
      p.add("0 1 rw", new JLabel("Type:"));
      p.add("1 0 2",label);
      p.add("1 1",m_type);
      p.add("2 1",m_log);
      return p;
   }
   private JPanel createRangePanel()
   {
      min = new JASTextField(15);
      max = new JASTextField(15);
      JCheckBox auto = new JCheckBox("Automatic");
      auto.setMnemonic('u');
      m_zero = new JCheckBox("Allow Suppressed Zero");
      m_zero.setMnemonic('Z');
      t1 = new JButton("...");
      t2 = new JButton("...");
      
      addBinding(new PropertyBinding(auto,"RangeAutomatic"));
      addBinding(new PropertyBinding(m_zero,"AllowSuppressedZero"));
      
      t1.addActionListener(this);
      t2.addActionListener(this);
      
      JPanel p = new FixedHeightPanel(new TableLayout());
      p.setBorder(new TitledBorder("Range"));
      p.add("0 0 rw",new JLabel("Min:"));
      p.add("0 1 rw",new JLabel("Max:"));
      p.add("1 0 h",min);
      p.add("1 1 h",max);
      p.add("2 0 w",t1);
      p.add("2 1 w",t2);
      p.add("3 0 lW",auto);
      p.add("3 1 lW",m_zero);
      
      m_rangePanel = p;
      return p;
   }
   private JPanel createBinsPanel()
   {
      m_bins = new JASTextField(10);
      m_binw = new JASTextField(10);
      m_binwUnits = new JComboBox();
      m_binwUnits.addItem("Default Units");
      m_binwUnits.addItem("Seconds");
      m_binwUnits.addItem("Minutes");
      m_binwUnits.addItem("Hours");
      m_binwUnits.addItem("Days");
      m_binwUnits.addItem("Weeks");
      m_binwUnits.addItem("Years");
      m_showOverflow = new JCheckBox("Under/Overflow");
      
      JPanel p = new FixedHeightPanel(new FlowLayout(FlowLayout.LEFT));
      p.setBorder(new TitledBorder("Binning"));
      p.add(new JLabel("Bins:"));
      p.add(m_bins);
      p.add(new JLabel("Bin Width:"));
      p.add(m_binw);
      p.add(m_binwUnits);
      p.add(m_showOverflow);
      
      m_binsPanel = p;
      return p;
   }
   public void doDataExchange(boolean set,Object bean)
   {
      
      JASHistAxis axis = (JASHistAxis) bean;
      m_axisType = axis.getAxisType();
      
      if (!init)
      {
         init = true;
         if (m_axisType != Rebinnable1DHistogramData.STRING)
         {
            addBinding(new PropertyBinding(new MinMaxFieldBinding(min),"MinObject"));
            addBinding(new PropertyBinding(new MinMaxFieldBinding(max),"MaxObject"));
            addBinding(new PropertyBinding(m_bins,"Bins", axis.isBinned() ?
               (byte) (JTextFieldBinding.MUST_BE_INTEGER | JTextFieldBinding.MUST_BE_POSITIVE) : (byte) 0));
            addBinding(new PropertyBinding(new BinWidthFieldBinding(m_binw),"BinWidth"));
            addBinding(new PropertyBinding(m_showOverflow,"ShowOverflows"));
         }
      }
      if (!set)
      {
         boolean enable = axis.isBinned();
         m_binsPanel.setEnabled(enable);
         
         enable = ( m_axisType != Rebinnable1DHistogramData.STRING);
         m_rangePanel.setEnabled(enable);
         m_log.setEnabled(enable);
         
         enable &= axis.getRangeAutomatic();
         m_zero.setEnabled(enable);
         
         enable = ( m_axisType == Rebinnable1DHistogramData.DATE);
         t1.setEnabled(enable);
         t2.setEnabled(enable);
         
         m_type.setText(types[m_axisType]);
      }
      
      super.doDataExchange(set,bean);
   }
   public void actionPerformed(ActionEvent evt)
   {
      Container w = this;
      while (!(w instanceof Frame)) w = w.getParent();
      
      if (evt.getSource() == t1)
      {
         Date t = new Date(min.getText());
         DateChooser dlg = new DateChooser((Frame) w,t);
         if (dlg.doModal()) min.setText(dlg.getDate().toString());
      }
      else if (evt.getSource() == t2)
      {
         Date t = new Date(max.getText());
         DateChooser dlg = new DateChooser((Frame) w,t);
         if (dlg.doModal()) max.setText(dlg.getDate().toString());
      }
   }
   private JASTextField min;
   private JASTextField max;
   private JButton t1;
   private JButton t2;
   private int m_axisType;
   private JASTextField m_bins;
   private JASTextField m_binw;
   private JPanel m_binsPanel;
   private JPanel m_rangePanel;
   private JComboBox m_binwUnits;
   private JLabel m_type;
   private JCheckBox m_zero;
   private JCheckBox m_log;
   private JCheckBox m_showOverflow;
   private String types[] = {"","Numeric","String","Date/Time"};
   private boolean init = false;
   
   private final class MinMaxFieldBinding extends JTextFieldBinding
   {
      MinMaxFieldBinding(JASTextField field)
      {
         super(field);
      }
      protected Object getValue(String s, Class type)
      {
         if (m_axisType == JASHistAxis.DATE) return new Date(s);
         else return new Double(s);
      }
   }
   private final class BinWidthFieldBinding extends JTextFieldBinding implements ActionListener
   {
      BinWidthFieldBinding(JASTextField field)
      {
         super(field, JTextFieldBinding.MUST_BE_NUMBER);
         m_binwUnits.addActionListener(this);
      }
      protected String setValue(Object value)
      {
         if (m_axisType == JASHistAxis.DATE)
         {
            double d = ((Double) value).doubleValue();
            if (conversionIndexIsDefault)
            {
               int i = 1;
               for (;;)
               {
                  i++;
                  if (i == conversion.length) break;
                  if (d/conversion[i] < 1) break;
               }
               conversionIndex = i-1;
            }
            m_binwUnits.setEnabled(true);
            m_binwUnits.setSelectedIndex(conversionIndex);
            return String.valueOf(d / conversion[conversionIndex]);
         }
         else
         {
            conversionIndex = 0;
            m_binwUnits.setEnabled(false);
            m_binwUnits.setSelectedIndex(conversionIndex);
            return value.toString();
         }
      }
      protected Object getValue(String s, Class type)
      {
         double d = Double.valueOf(s).doubleValue();
         d *= conversion[conversionIndex];
         return new Double(d);
      }
      public void actionPerformed(ActionEvent evt)
      {
         int i = m_binwUnits.getSelectedIndex();
         if (i == conversionIndex) return;
         
         String s = m_binw.getText();
         double d = Double.valueOf(s).doubleValue();
         d *= conversion[conversionIndex];
         conversionIndex = i;
         d /= conversion[conversionIndex];
         m_binw.setText(String.valueOf(d));
         
         conversionIndexIsDefault = false;
      }
      private boolean conversionIndexIsDefault = true;
      private int conversionIndex;
      private double[] conversion = {
         1 , // no conversion
                 1 , // seconds
                 60 , // minutes
                 60*60 , // hours
                 24*60*60 , // days
                 7*24*60*60 , // weeks
                 365*24*60*60 , // years TODO: get accurate number
      };
   }
}

final class JASHistPropData extends PropertyPage implements ListSelectionListener, PropertySite
{
   public JASHistPropData(DataManager dm)
   {
      m_init = false;
      
      setLayout(new BorderLayout());
      
      m_listModel = new DefaultListModel();
      m_list = new JList(m_listModel);
      m_list.setCellRenderer(DataRenderer.createRenderer());
      m_list.addListSelectionListener(this);
      JScrollPane scroll = new JScrollPane();
      scroll.setViewportView(m_list);
      scroll.setPreferredSize(new Dimension(100,120));
      add(scroll,BorderLayout.WEST);
      
      JPanel b = new JPanel();
      JPanel df = new FixedHeightPanel(new FlowLayout(FlowLayout.LEFT));
      m_dataStyle = new JASHistPropDataStyle();
      m_dataStyle.setPropertySite(this);
      df.add(m_dataStyle);
      df.add(m_normalized = new JCheckBox("Normalized"));
      df.add(m_normalizationSettings = new JButton("Normalization Settings..."));
      df.setBorder(BorderFactory.createTitledBorder("Data"));
      b.add(df);
      b.setLayout(new BoxLayout(b,BoxLayout.Y_AXIS));
      
      if (dm instanceof TwoDDataManager) m_propStyle = new JASHist2DPropStyle();
      else                               m_propStyle = new JASHistPropStyle();
      
      m_propStyle.setPropertySite(this);
      b.add(m_propStyle);
      b.add(Box.createVerticalGlue());
      add(b,BorderLayout.CENTER);
   }
   public String getHelpTopic()
   {
      return "userInterface.propertiesDialog.dataTab";
   }
   public void doDataExchange(boolean set, Object bean)
   {
      /////////////////////////////////////
      //	System.out.println("doDataEx called");
      
      if (!m_init)
      {
         final JASHist hist = (JASHist) bean;
         final Enumeration e = hist.getDataSources();
         while (e.hasMoreElements())
         {
            m_listModel.addElement(e.nextElement());
         }
         m_selected = (JASHistData) m_listModel.elementAt(0);
         m_list.setSelectedValue(m_selected,true);
         m_jHist = hist;
         m_init = true;
      }
      if (!set)
      {
         Normalizer norm = m_selected.getNormalization();
         m_normalized.setSelected(norm != null);
         m_normalizationSettings.setEnabled(norm != null);
      }
      
      final JASHistStyle s = m_selected.getStyle();
      m_propStyle.doDataExchange(set,s);
      
      m_dataStyle.doDataExchange(set,m_selected);
      setChanged(false);
   }
   public void valueChanged(ListSelectionEvent evt)
   {
      if (!m_init) return;
      doDataExchange(true,m_jHist);
      m_selected = (JASHistData) m_list.getSelectedValue();
      doDataExchange(false,m_jHist);
   }
   public void callEnable()
   {
      setChanged(true);
   }
   
   private PropertyPage m_propStyle;
   private JASHistPropDataStyle m_dataStyle;
   private boolean m_init;
   private JASHistData m_selected;
   private DefaultListModel m_listModel;
   private JList m_list;
   private JASHist m_jHist;
   private JCheckBox m_normalized;
   private JButton m_normalizationSettings;
   
   private JASHist2DHistogramStyle m_histStyle;
   
}// 1D - JASHistPropData


final class JASHistPropStyle extends PropertyPage
{
   JASHistPropStyle()
   {
      setLayout(new TableLayout());
      setBorder(BorderFactory.createTitledBorder("1D Plot Style"));
      
      
      final JCheckBox hist = new JCheckBox("Histogram Bars");
      final ColorChooser histFillColor = new ColorChooser();
      final ColorChooser histOutlineColor = new ColorChooser();
      final JCheckBox histFilled = new JCheckBox("Filled");
      final JComboBox histStyle = new JComboBox();
      histStyle.addItem("Solid");
      histStyle.addItem("Dotted");
      histStyle.addItem("Dashed");
      histStyle.addItem("DotDashed");
      final JSpinner histWidth = new JSpinner(new SpinnerNumberModel(1,0.0,5.5,0.5));
      
      JPanel p= new JPanel();
      p.add(new JLabel("Outline"));
      p.add(histFillColor);
      p.add(new JLabel("Fill"));
      p.add(histFilled);
      p.add(histStyle);
      p.add(histWidth);
      
      add("0 0 w",hist);
      add("1 0 Hw",histOutlineColor);
      add("2 0 lW",p);
      
      final JCheckBox error  = new JCheckBox("Error Bars");
      final ColorChooser errorColor = new ColorChooser();
      final JComboBox errorStyle = new JComboBox();
      errorStyle.addItem("Solid");
      errorStyle.addItem("Dotted");
      errorStyle.addItem("Dashed");
      errorStyle.addItem("DotDashed");
      final JSpinner errorWidth = new JSpinner(new SpinnerNumberModel(1,0.0,5.5,0.5));
      
      p = new JPanel();
      p.add(errorStyle);
      p.add(errorWidth);
      
      add("0 1 w",error);
      add("1 1 Hw",errorColor);
      add("2 1 lW",p);
      
      final JCheckBox points = new JCheckBox("Data Points");
      final ColorChooser pointColor = new ColorChooser();
      final JComboBox pointStyle = new JComboBox();
      final JSpinner pointSize = new JSpinner(new SpinnerNumberModel(2,1,99,1));
      
      p = new JPanel();
      //		p.add(new JLabel("Data Points"));
      p.add(pointStyle);
      p.add(pointSize);
      
      add("0 2 w",points);
      add("1 2 Hw",pointColor);
      add("2 2 lW",p);
      
      final JCheckBox lines  = new JCheckBox("Lines Between Points");
      final ColorChooser lineColor = new ColorChooser();
      final JComboBox lineStyle = new JComboBox();
      lineStyle.addItem("Solid");
      lineStyle.addItem("Dotted");
      lineStyle.addItem("Dashed");
      lineStyle.addItem("DotDashed");
      final JSpinner lineWidth = new JSpinner(new SpinnerNumberModel(1,0.0,5.5,0.5));
      
      p = new JPanel();
      p.add(lineStyle);
      p.add(lineWidth);
      
      add("0 3 w",lines);
      add("1 3 Hw",lineColor);
      add("2 3 lW",p);
      
      addBinding(new PropertyBinding(pointColor,"DataPointColor"));
      addBinding(new PropertyBinding(pointStyle,"DataPointStyle"));
      addBinding(new PropertyBinding(pointSize,"DataPointSize"));
      addBinding(new PropertyBinding(hist,"ShowHistogramBars"));
      addBinding(new PropertyBinding(error,"ShowErrorBars"));
      addBinding(new PropertyBinding(points,"ShowDataPoints"));
      addBinding(new PropertyBinding(lines,"ShowLinesBetweenPoints"));
      addBinding(new PropertyBinding(histFillColor,"HistogramBarColor"));
      addBinding(new PropertyBinding(histOutlineColor,"HistogramBarLineColor"));
      addBinding(new PropertyBinding(errorColor,"ErrorBarColor"));
      addBinding(new PropertyBinding(lineColor,"LineColor"));
      addBinding(new PropertyBinding(histFilled,"HistogramFill"));
      addBinding(new PropertyBinding(lineStyle,"LinesBetweenPointsStyle"));
      addBinding(new PropertyBinding(lineWidth,"LinesBetweenPointsWidth"));
      addBinding(new PropertyBinding(histStyle,"HistogramBarLineStyle"));
      addBinding(new PropertyBinding(histWidth,"HistogramBarLineWidth"));
      addBinding(new PropertyBinding(errorStyle,"ErrorBarStyle"));
      addBinding(new PropertyBinding(errorWidth,"ErrorBarWidth"));
      
      pointStyle.addItem("Dot");
      pointStyle.addItem("Box");
      pointStyle.addItem("Triangle");
      pointStyle.addItem("Diamond");
      pointStyle.addItem("Star");
      pointStyle.addItem("Vert Line");
      pointStyle.addItem("Horiz Line");
      pointStyle.addItem("Cross");
      pointStyle.addItem("Circle");
      pointStyle.addItem("Square");
   }
   public Dimension getMaximumSize()
   {
      Dimension d1 = super.getMaximumSize();
      Dimension d2 = super.getPreferredSize();
      d1.height = d2.height;
      return d1;
   }
}// 1D JASHistPropStyle

final class JASHistScatterPropStyle extends PropertyPage
{
   JASHistScatterPropStyle()
   {
      setLayout(new BorderLayout());
      
      final ColorChooser pointColor = new ColorChooser();
      final JComboBox pointStyle = new JComboBox();
      final JSpinner pointSize = new JSpinner(new SpinnerNumberModel(2,1,20,1));
      
      JPanel p = new JPanel();
      p.add(new JLabel("Data Points"));
      p.add(pointColor);
      p.add(pointStyle);
      p.add(pointSize);
      add(p,BorderLayout.NORTH);
      
      addBinding(new PropertyBinding(pointColor,"DataPointColor"));
      addBinding(new PropertyBinding(pointStyle,"DataPointStyle"));
      addBinding(new PropertyBinding(pointSize,"DataPointSize"));
      
      pointStyle.addItem("Box");
      pointStyle.addItem("Triangle");
      pointStyle.addItem("Diamond");
      pointStyle.addItem("Star");
      pointStyle.addItem("Vert Line");
      pointStyle.addItem("Horiz Line");
      pointStyle.addItem("Cross");
      pointStyle.addItem("Square");
   }
   public Dimension getMaximumSize()
   {
      Dimension d1 = super.getMaximumSize();
      Dimension d2 = super.getPreferredSize();
      d1.height = d2.height;
      return d1;
   }
}// 1D JASHistPropStyle

// JASHistPropDataStyle


/////////////////////////
// Begin 2D Properties //
/////////////////////////
/*
final class JAS2DHistPropData extends PropertyPage implements ListSelectionListener, PropertySite
{
        public JAS2DHistPropData()
        {
                m_init = false;
 
                setLayout(new BorderLayout());
 
                m_listModel = new DefaultListModel();
                m_list = new JList(m_listModel);
                m_list.setCellRenderer(DataRenderer.createRenderer());
                m_list.addListSelectionListener(this);
                JScrollPane scroll = new JScrollPane();
                scroll.setViewportView(m_list);
                scroll.setPreferredSize(new Dimension(100,120));
                add(scroll,BorderLayout.WEST);
 
                JPanel b = new JPanel();
                m2D_propStyle = new JAS2DHistPropStyle();
                m2D_propStyle.setPropertySite(this);
                m2D_propStyle.setBorder(BorderFactory.createTitledBorder("2D-Plot Style"));
                b.add(m2D_propStyle);
                b.setLayout(new BoxLayout(b,BoxLayout.Y_AXIS));
        //	b.add(Box.createVerticalGlue());
                add(b,BorderLayout.CENTER);
        }
        public String getHelpTopic()
        {
                return "2D-Data";
        }
        public void doDataExchange(boolean set, Object bean)
        {
 
                super.doDataExchange(set,bean);
                if (!m_init)
                {
                        final JASHist hist = (JASHist) bean;
                        final Enumeration e = hist.get1DDataElements();
                        while (e.hasMoreElements())
                        {
                                m_listModel.addElement(e.nextElement());
                        }
                        m_selected = (JASHist1DData) m_listModel.elementAt(0);
                        m_list.setSelectedValue(m_selected,true);
                        m2D_propStyle.init(m_selected instanceof JASHist2DHistogramData);
                        m_jHist = hist;
                        m_init = true;
                }
                final JASHist1DStyle s = m_selected.getStyle();
                m2D_propStyle.doDataExchange(set,s);
        //	m2D_dataStyle.doDataExchange(set,m_selected);
 
                setChanged(false);
        }
        public void valueChanged(ListSelectionEvent evt)
        {
                System.out.println("Value Changed");
                if (!m_init) return;
                doDataExchange(true,m_jHist);
                m_selected = (JASHist1DData) m_list.getSelectedValue();
                doDataExchange(false,m_jHist);
        }
        public void callEnable()
        {
                setChanged(true);
        }
        private JAS2DHistPropStyle m2D_propStyle;
        private boolean m_init;
        private JASHist1DData m_selected;
        private DefaultListModel m_listModel;
        private JList m_list;
        private JASHist m_jHist;
 
        //test area//
        private JASHist2DHistogramStyle m2D_histStyle;
 
}// 2D - JASHistPropData
 */
final class JASHist2DPropStyle extends PropertyPage
{
   
   JASHist2DPropStyle()
   {
      setLayout(new BorderLayout());
      
      CheckBoxBorderPanel p1 = create2DPanel();
      CheckBoxBorderPanel p2 = createScatterPanel();
      
      twoDCheckBox = p1.getCheckBox();
      scatCheckBox = p2.getCheckBox();
      ButtonGroup bg = new ButtonGroup();
      bg.add(twoDCheckBox);
      bg.add(scatCheckBox);
      scatCheckBox.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            callEnable();
         }
      });
      
      add(p1, BorderLayout.CENTER);
      add(p2, BorderLayout.SOUTH);
      
   }
   private CheckBoxBorderPanel create2DPanel()
   {
      CheckBoxBorderPanel p = new CheckBoxBorderPanel("2D Plot Style");
      p.setLayout(new TableLayout());
      
      //////////Plot Scheme//////////
      plotSchemePanel = new JPanel(new FlowLayout());
      plotLabel = new JLabel("Plot Scheme:");
      plotScheme = new JComboBox();
      plotScheme.addItem("Box");
      plotScheme.addItem("Ellipse");
      plotScheme.addItem("ColorMap");
      
      plotSchemePanel.add(plotLabel);
      plotSchemePanel.add(plotScheme);
      
      colorMapLabel = new JLabel("ColorMap Scheme:");
      colorMapScheme = new JComboBox();
      colorMapScheme.addItem("Warm");
      colorMapScheme.addItem("Cool");
      colorMapScheme.addItem("Thermal");
      colorMapScheme.addItem("Rainbow");
      colorMapScheme.addItem("GrayScale");
      colorMapScheme.addItem("Select Range");
      
      plotSchemePanel.add(colorMapLabel);
      plotSchemePanel.add(colorMapScheme);
      
      p.add("0 0 5 1",plotSchemePanel);
      
      shapeColorLabel = new JLabel("Bin Color");
      shapeColor = new ColorChooser(Color.black);
      
      p.add("1 1 w",shapeColor);
      p.add("0 1 Wr",shapeColorLabel);
      
      startColorLabel = new JLabel("Set Range Min");
      startColor = new ColorChooser(Color.white);
      
      p.add("3 1 w",startColor);
      p.add("2 1 Wr",startColorLabel);
      
      overFlowColorLabel = new JLabel("OverFlow Color");
      overFlowColor = new ColorChooser(Color.red);
      
      p.add("1 2 w",overFlowColor);
      p.add("0 2 Wr",overFlowColorLabel);
      
      endColorLabel = new JLabel("Set Range Max");
      endColor = new ColorChooser(Color.black);
      
      p.add("3 2 w",endColor);
      p.add("2 2 Wr",endColorLabel);
      
      invertRange = new JButton("Invert")
      {
         final protected void fireActionPerformed(final ActionEvent e)
         {
            Color tempColor = startColor.getColor();
            startColor.setColor(endColor.getColor());
            endColor.setColor(tempColor);
         }
      };
      
      p.add("4 1 1 2 HWl",invertRange);
      
      addBinding(new PropertyBinding(plotScheme,"HistStyle"));
      addBinding(new PropertyBinding(colorMapScheme,"ColorMapScheme"));
      
      addBinding(new PropertyBinding(shapeColor,"ShapeColor"));
      addBinding(new PropertyBinding(overFlowColor,"OverflowBinColor"));
      
      addBinding(new PropertyBinding(startColor,"StartDataColor"));
      addBinding(new PropertyBinding(endColor,"EndDataColor"));
      
      return p;
   }
   private CheckBoxBorderPanel createScatterPanel()
   {
      final ColorChooser pointColor = new ColorChooser();
      final JComboBox pointStyle = new JComboBox();
      final JSpinner pointSize = new JSpinner(new SpinnerNumberModel(2,1,20,1));
      
      CheckBoxBorderPanel p = new CheckBoxBorderPanel("Scatter Plot Style",new FlowLayout());
      
      p.add(new JLabel("Data Points"));
      p.add(pointColor);
      p.add(pointStyle);
      p.add(pointSize);
      add(p,BorderLayout.NORTH);
      
      addConditionalBinding(new PropertyBinding(pointColor,"DataPointColor"));
      addConditionalBinding(new PropertyBinding(pointStyle,"DataPointStyle"));
      addConditionalBinding(new PropertyBinding(pointSize,"DataPointSize"));
      
      pointStyle.addItem("Box");
      pointStyle.addItem("Triangle");
      pointStyle.addItem("Diamond");
      pointStyle.addItem("Star");
      pointStyle.addItem("Vert Line");
      pointStyle.addItem("Horiz Line");
      pointStyle.addItem("Cross");
      pointStyle.addItem("Square");
      
      return p;
   }
   public void addConditionalBinding(PropertyBinding b)
   {
      b.setBeanClass(JASHistScatterPlotStyle.class);
      addBinding(b);
   }
   
   public void doDataExchange(boolean set, Object bean)
   {
      super.doDataExchange(set,bean);
      s2D = (JASHist2DHistogramStyle) bean;
      
      if (s2D instanceof JASHistScatterPlotStyle)
      {
         JASHistScatterPlotStyle ss = (JASHistScatterPlotStyle) s2D;
         if (set) ss.setDisplayAsScatterPlot(scatCheckBox.isSelected());
         scatCheckBox.setSelected(ss.getDisplayAsScatterPlot());
         twoDCheckBox.setSelected(!ss.getDisplayAsScatterPlot());
         scatCheckBox.setEnabled(true);
      }
      else
      {
         twoDCheckBox.setSelected(true);
         scatCheckBox.setEnabled(false);
      }
      
      boolean colormap = s2D.getHistStyle() == s2D.STYLE_COLORMAP;
      shapeColor.setEnabled(!colormap);
      shapeColorLabel.setEnabled(!colormap);
      colorMapScheme.setEnabled(colormap);
      colorMapLabel.setEnabled(colormap);
      
      boolean overflow = !colormap && s2D.getShowOverflow();
      overFlowColor.setEnabled(overflow);
      overFlowColorLabel.setEnabled(overflow);
      
      boolean userDefined = colormap &&
              s2D.getColorMapScheme() == s2D.COLORMAP_USERDEFINED;
      
      invertRange.setEnabled(userDefined);
      startColor.setEnabled(userDefined);
      startColorLabel.setEnabled(userDefined);
      endColorLabel.setEnabled(userDefined);
      endColor.setEnabled(userDefined);
   }
   public void valueChanged(ListSelectionEvent evt)
   {
      System.out.println("2D valueChanged called");
   }
   public void callEnable()
   {
      setChanged(true);
   }
   
   public Dimension getMaximumSize()
   {
      Dimension d1 = super.getMaximumSize();
      Dimension d2 = super.getPreferredSize();
      d1.height = d2.height;
      return d1;
   }
   void init(final boolean isHistData)
   {
      System.out.println("2D - Its really Alive!");
   }
   
   private JCheckBox twoDCheckBox;
   private JCheckBox scatCheckBox;
   
   private JPanel plotSchemePanel;
   private JComboBox plotScheme;
   private JLabel plotLabel;
   
   private ColorChooser shapeColor;
   private JLabel shapeColorLabel;
   
   private ColorChooser overFlowColor;
   private JLabel overFlowColorLabel;
   
   private JComboBox colorMapScheme;
   private JLabel colorMapLabel;
   
   private ColorChooser startColor;
   private JLabel startColorLabel;
   
   private ColorChooser endColor;
   private JLabel endColorLabel;
   
   private JCheckBox showOverFlow;
   
   private JButton invertRange;
   
   private JASHist2DHistogramStyle s2D;
   private JASHist2DHistogramStyle m_selected;
}// JAS2DHistPropStyle
///////////////////////
// End 2D Properties //
///////////////////////


final class CheckBoxBorder extends AbstractBorder
{
   CheckBoxBorder(JCheckBox cb)
   {
      this.cb = cb;
   }
   public void paintBorder(Component c, Graphics g,
           int x, int y, int width, int height)
   {
      border1.paintBorder(c,g,x,y,width,height);
      System.out.println("x="+x+" y="+y+" w="+width+" h="+height);
   }
   public Insets getBorderInsets(Component c)
   {
      System.out.println("insets="+border1.getBorderInsets(c));
      Insets insets = border1.getBorderInsets(c);
      Dimension cbSize = cb.getPreferredSize();
      insets.top = 2 + cbSize.height;
      System.out.println("insets="+border1.getBorderInsets(c));
      return insets;
   }
   private JCheckBox cb;
   private Border border1 = UIManager.getBorder("TitledBorder.border");
}
final class FixedHeightPanel extends DisabledPanel
{
   FixedHeightPanel()
   {
      super();
   }
   FixedHeightPanel(LayoutManager layout)
   {
      super(layout);
   }
   public Dimension getMaximumSize()
   {
      Dimension d1 = super.getMaximumSize();
      Dimension d2 = super.getPreferredSize();
      d1.height = d2.height;
      return d1;
   }
}
class DisabledPanel extends JPanel
{
   DisabledPanel()
   {
      super();
   }
   DisabledPanel(LayoutManager layout)
   {
      super(layout);
   }
   final public void setEnabled(boolean value)
   {
      Component[] children = getComponents();
      for (int i=0; i<children.length; i++)
         children[i].setEnabled(value);
      super.setEnabled(value);
   }
}
