package jas.hist;

import jas.util.JASIcon;
import jas.util.JASTextField;
import jas.util.PropertyPage;
import jas.util.PropertySite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


class JASHistPropFunctions extends PropertyPage implements ListSelectionListener, PropertySite
{
   public JASHistPropFunctions()
   {
      m_init = false;
      
      setLayout(new BorderLayout());
      JPanel p1 = new JPanel(new BorderLayout());
      
      m_listModel = new DefaultListModel();
      m_list = new JList(m_listModel);
      m_list.setCellRenderer(DataRenderer.createRenderer());
      m_list.addListSelectionListener(this);
      
      JScrollPane scroll = new JScrollPane();
      scroll.setViewportView(m_list);
      scroll.setPreferredSize(new Dimension(100,120));
      
      p1.add(scroll,BorderLayout.CENTER);
      JPanel p2 = new JPanel();
      JButton add = new JButton("Add...");
      add.addActionListener(new AddButtonListener());
      add.setMnemonic('d');
      p2.add(add);
      JButton remove = new JButton("Remove");
      remove.addActionListener(new RemoveButtonListener());
      remove.setMnemonic('R');
      p2.add(remove);
      p1.add(p2,BorderLayout.SOUTH);
      add(p1,BorderLayout.WEST);
      
      JPanel p = new JPanel(new BorderLayout());
      JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT));
      m_propStyle = new JASHistPropFunctionStyle();
      m_propStyle.setPropertySite(this);
      b.add(m_propStyle);
      m_dataStyle = new JASHistPropDataStyle();
      m_dataStyle.setPropertySite(this);
      b.add(m_dataStyle);
      advanced = new JButton("Advanced...");
      advanced.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            try
            {
               FunctionAdvancedOptions fao =
                       (FunctionAdvancedOptions) m_selected.getFunction();
               Container w = JASHistPropFunctions.this;
               while (!(w instanceof Frame)) w = w.getParent();
               fao.openAdvancedDialog((Frame) w, m_jHist);
               // The argument pf simply allows the
               // dialog box to have access to the
               // PropFunctions fields
            }
            catch (Exception exception)
            {}
            // do nothing if an exception is thrown
         }
      });
      advanced.setMnemonic('v');
      b.add(advanced);
      p.add(b,BorderLayout.NORTH);
      p.setBorder(BorderFactory.createTitledBorder("Function"));
      
      m_paramManager = new ParamManager();
      p.add(m_paramManager,BorderLayout.CENTER);
      
      m_fitManager = new FitManager();
      p.add(m_fitManager,BorderLayout.SOUTH);
      
      add(p,BorderLayout.CENTER);
      updateAdvanced();
   }
   public String getHelpTopic()
   {
      return "functionsAndFilters.functions";
   }
   public synchronized void doDataExchange(boolean set,Object bean)
   {
      if (!m_init)
      {
         JASHist hist = (JASHist) bean;
         Enumeration e = hist.get1DFunctions();
         JASHist1DFunctionData d;
         while (e.hasMoreElements())
         {
            d = (JASHist1DFunctionData) e.nextElement();
            m_listModel.addElement(d);
            Basic1DFunction f = d.getFunction();
            if (f instanceof FunctionAdvancedOptions)
            {
               f.addObserver(m_listNameChangeListener);
            }
         }
         if (m_listModel.size()>0)
         {
            m_selected = (JASHist1DFunctionData) m_listModel.elementAt(0);
            m_list.setSelectedValue(m_selected,true);
         }
         else m_selected = null;
         m_jHist = hist;
         m_fitManager.init(hist);
         m_init = true;
      }
      if (m_selected != null)
      {
         m_propStyle.doDataExchange(set,m_selected.getStyle());
         m_dataStyle.doDataExchange(set,m_selected);
         m_paramManager.doDataExchange(set,m_selected.getFunction());
         m_fitManager.setFunction(m_selected);
      }
      setChanged(false);
      updateAdvanced();
   }
   private synchronized void addFunction(JASHist1DFunctionData d)
   {
      m_selected = d;
      m_listModel.addElement(d);
      m_list.setSelectedValue(d,true);
      doDataExchange(true,m_jHist);
   }
   private synchronized void removeFunction()
   {
      m_selected.delete();
      m_listModel.removeElement(m_selected);
      doDataExchange(true, m_jHist);
   }
   public void valueChanged(ListSelectionEvent evt)
   {
      if (!m_init) return;
      doDataExchange(true,m_jHist);
      if (m_listModel.getSize() > 0)
      {
         m_selected = (JASHist1DFunctionData) m_list.getSelectedValue();
         doDataExchange(false,m_jHist);
         updateAdvanced();
      }
   }
   private void updateAdvanced()
   {
      advanced.setEnabled(m_selected != null &&
              m_selected.getFunction() instanceof FunctionAdvancedOptions);
   }
   public void callEnable()
   {
      setChanged(true);
   }
   protected void deactivate()
   {
      m_paramManager.deactivate();
      m_fitManager.deactivate();
   }
   private class AddButtonListener implements ActionListener
   {
      public void actionPerformed(ActionEvent evt)
      {
         FunctionRegistry fr = FunctionRegistry.instance();
         Container w = JASHistPropFunctions.this;
         while (!(w instanceof Frame)) w = w.getParent();
         FunctionFactory ff = fr.chooseFunction((Frame) w);
         if (ff != null)
         {
            try
            {
               Basic1DFunction f = ff.createFunction(m_jHist);
               JASHistData d = m_jHist.addData(f);
               d.show(true);
               addFunction((JASHist1DFunctionData) d);
               if (f instanceof FunctionAdvancedOptions)
                  f.addObserver(m_listNameChangeListener);
            }
            catch (FunctionFactoryError x)
            {
               x.printStackTrace(); // ????
            }
         }
      }
   }
   private class RemoveButtonListener implements ActionListener
   {
      public void actionPerformed(ActionEvent evt)
      {
         removeFunction();
      }
   }
   private class ParamTableModel
           extends AbstractTableModel
           implements Observer
   {
      ParamTableModel()
      {
         format.setMaximumFractionDigits(6);
      }
      void setFunction(Basic1DFunction f)
      {
         // We want to observe the function, so that we can update if the
         // function changes (e.g. due to a fit), but we need to be careful
         // to remove the link when we are deactivated.
         
         if (f != null) f.deleteObserver(this);
         this.f = f;
         f.addObserver(this);
         
         fireTableChanged(new TableModelEvent(this, -1));
      }
      void deactivate()
      {
         if (f != null) f.deleteObserver(this);
      }
      public void update(Observable obs, Object arg)
      {
         if (obs == f)
         {
            fireTableChanged(new TableModelEvent(this, -1));
         }
      }
      public int getRowCount()
      {
         if (f==null) return 0;
         String[] names =  f.getParameterNames();
         return (names == null) ? 0 : names.length;
      }
      public int getColumnCount()
      {
         return columns.length;
      }
      public Object getValueAt(int row, int col)
      {
         if (col == 0) return f.getParameterNames()[row];
         else if (col == 1) return format.format(f.getParameterValues()[row]);
         else if (col == 2)
         {
            boolean value;
            if (f instanceof Fittable1DFunction)
            {
               value = ((Fittable1DFunction) f).getIncludeParametersInFit()[row];
            }
            else value = false;
            return new Boolean(value);
         }
         else if (col == 3)
         {
            if (f instanceof Fittable1DFunction)
            {
               Fittable1DFunction func = (Fittable1DFunction) f;
               Fitter fit = func.getFit();
               if (fit != null)
               {
                  boolean[] inFit = func.getIncludeParametersInFit();
                  if (! inFit[row]) return "Not in fit";
                  int i = 0, j = 0;
                  while (i < row)
                  {
                     if (inFit[i]) j++;
                     i++;
                  }
                  return format.format(fit.getParameterSigmas()[j]);
               }
            }
         }
         return null;
      }
      public String getColumnName(int col)
      {
         return columns[col];
      }
      public Class getColumnClass(int col)
      {
         if (col == 2) return Boolean.class;
         if (col == 1) return String.class;
         return super.getColumnClass(col);
      }
      public boolean isCellEditable(int row, int col)
      {
         return (col == 2 || col == 1);
      }
      public void setValueAt(Object value, int row, int col)
      {
         if (col == 1)
         {
            try
            {
               double d = Double.valueOf((String) value).doubleValue();
               f.setParameter(row,d);
            }
            catch (InvalidFunctionParameter e)
            {getToolkit().beep();}
            catch (NumberFormatException    e)
            {getToolkit().beep();}
         }
         else if (col == 2)
         {
            if (f instanceof Fittable1DFunction)
            {
               boolean b = ((Boolean) value).booleanValue();
               ((Fittable1DFunction) f).setIncludeParameterInFit(row,b);
            }
            else getToolkit().beep();
         }
      }
      private String[] columns = {"Parameter","Value","Fit","Error"};
      private Basic1DFunction f = null;
   }
   private class ParamManager extends JPanel
   {
      ParamManager()
      {
         this.setLayout(new BorderLayout());
         
         m_model = new ParamTableModel();
         m_table = new JTable(m_model);
         m_table.setAutoCreateColumnsFromModel(false);
         TableColumn col = m_table.getColumn("Value");
         
         col.setCellRenderer(new ParamCellRenderer());
         col.setCellEditor(new DefaultCellEditor(new JTextField("")));
         
         //col = m_table.getColumn("Fit");
         //col.setHeaderRenderer(new FitHeaderRenderer(m_table, col.getHeaderRenderer()));
         //col.sizeWidthToFit(); // just wide enough for header cell
         
         JScrollPane scrollpane = new JScrollPane(m_table);
         
         scrollpane.setPreferredSize(new Dimension(350,100));
         this.add("Center",scrollpane);
      }
      void doDataExchange(boolean set,Basic1DFunction f)
      {
         if (m_f != f)
         {
            m_model.setFunction(f);
            m_f = f;
         }
      }
      void deactivate()
      {
         m_model.deactivate();
         m_f = null;
      }
      private JTable m_table;
      private Basic1DFunction m_f = null;
      private ParamTableModel m_model;
      
      private class ParamCellRenderer implements TableCellRenderer
      {
         public Component getTableCellRendererComponent(JTable table, Object obj, boolean sel,
                 boolean hasFocus, int col, int row)
                        /*
                         * bug: hasFocus not used
                         */
         {
            if (obj != null) text.setText(obj.toString());
            if (sel)
            {
               text.setBackground(UIManager.getColor("textHighlight"));
               text.setForeground(UIManager.getColor("textHighlightText"));
            }
            else
            {
               text.setBackground(b);
               text.setForeground(f);
            }
            text.setScrollOffset(0);
            return text;
         }
         private JASTextField text = new JASTextField();
         private Color f = text.getForeground();
         private Color b = text.getBackground();
      }
      private class FitHeaderRenderer implements TableCellRenderer
      {
         FitHeaderRenderer(JTable table, TableCellRenderer renderer)
         {
            m_table = table;
            m_renderer = renderer;
         }
         public Component getTableCellRendererComponent(JTable table, Object obj, boolean sel,
                 boolean hasFocus, int col, int row)
         {
            if (table == null) table = m_table;
            return m_renderer.getTableCellRendererComponent(table, obj, sel, hasFocus, col, row);
         }
         private TableCellRenderer m_renderer;
         private JTable m_table;
      }
   }
   private class FitManager extends JPanel implements Observer,ItemListener
   {
      FitManager()
      {
         states = new javax.swing.Icon[5];
         states[Fitter.FIT]     = JASIcon.create(this,"tick.gif");
         states[Fitter.FAILED]  = JASIcon.create(this,"cross.gif");
         states[Fitter.FITTING] = JASIcon.create(this,"running.gif");
         states[Fitter.READYTOFIT]  = states[Fitter.FIT];
         states[Fitter.NOTREADYTOFIT] = states[Fitter.FAILED];
         
         JPanel p4 = new JPanel();
         m_state = new JLabel(states[Fitter.FIT]);
         p4.add(m_state);
         m_chi2 = new JLabel(" ")
         {
            public Dimension getPreferredSize()
            {
               Dimension result = super.getPreferredSize();
               result.width = 100; // Leave sufficient room
               return result;
            }
         };
         m_chi2.setIcon(JASIcon.create(this,"chi2.gif"));
         p4.add(m_chi2);
         
         JPanel p2 = new JPanel(new BorderLayout());
         p2.add("North",p4);
         JProgressBar bar = new JProgressBar();
         m_fitWatcher = new FitWatcher(bar.getModel());
         p2.add("Center",bar);
         
         JPanel p3 = new JPanel();
         p3.add(new JLabel("Using"));
         m_fitterChoice = new JComboBox();
         m_fitterChoice.addItemListener(this);
         p3.add(m_fitterChoice);
         
         JPanel p1 = new JPanel();
         p1.add(new JLabel("Data"));
         m_choice = new JComboBox();
         p1.add(m_choice);
         
         JPanel p5 = new JPanel(new GridLayout(0,1));
         p5.add(p3);
         p5.add(p1);
         
         this.setLayout(new BorderLayout());
         m_fit = new JCheckBox("Fit");
         m_fit.addActionListener(new FitNowHandler());
         m_fit.setMnemonic('F');
         
         // It would be nice to make the title contain the
         // fit checkbox, needs custom swing border
         
         //this.setBorder(new CheckBoxBorder(m_fit));
         this.setBorder(BorderFactory.createTitledBorder("Fit"));
         
         this.add("North",m_fit);
         this.add("West",p5);
         this.add("Center",p2);
         //add("South",new Checkbox("Update fit when data changes"));
         setEnabled(false);
      }
      void init(JASHist hist)
      {
         Enumeration e = hist.getDataSources();
         if (e.hasMoreElements())
         {
            m_choice.setRenderer(DataRenderer.createRenderer());
            m_choice.addItemListener(this);
         }
         else
            m_choice.setEnabled(false);
         while (e.hasMoreElements())
         {
            JASHist1DHistogramData d = (JASHist1DHistogramData) e.nextElement();
            m_choice.addItem(d.getFittableDataSource());
         }
         
         e = FitterRegistry.instance().elements();
         while (e.hasMoreElements())
         {
            FitterFactory ff = (FitterFactory) e.nextElement();
            m_fitterChoice.addItem(ff);
         }
         m_fitterChoice.setSelectedItem(FitterRegistry.instance().getDefaultFitterFactory());
      }
      void setFunction(JASHist1DFunctionData fd)
      {
         if (m_function != null) m_function.deleteObserver(this);
         m_fitWatcher.clearFit();
         
         if (fd != null &&
                 fd.getFunction() instanceof Fittable1DFunction &&
                 m_choice.getItemCount() > 0 &&
                 m_fitterChoice.getItemCount() > 0)
         {
            m_fd = fd;
            m_function = (Fittable1DFunction) fd.getFunction();
            m_function.addObserver(this);
            Fitter fitter = m_function.getFit();
            if (fitter != null) m_fitWatcher.setFit(fitter);
            update();
            setEnabled(true);
         }
         else
         {
            setEnabled(false);
            m_function = null;
            m_fd = null;
         }
      }
      private void update()
      {
         Fitter fitter = m_function.getFit();
         m_fit.setSelected(fitter != null);
         if (fitter != null)
         {
            m_chi2.setText(format.format(fitter.getChiSquared()));
            //TO-DO make sure all fitters get data assigned (?).
            Object data = fitter.getData();
            if ( data != null )
               m_choice.setSelectedItem(data);
         }
      }
      public void update(Observable obs, Object arg)
      {
         //System.out.println("FitManager update"+obs+" "+m_function);
         if (obs == m_function) update();
      }
      void deactivate()
      {
         //System.out.println("Deactivating FitManager");
         setFunction(null);
      }
      public void setEnabled(boolean state)
      {
         m_fit.setEnabled(state);
         m_choice.setEnabled(state);
         m_fitterChoice.setEnabled(state);
      }
      public void itemStateChanged(ItemEvent e)
      {
         // TODO: This causes problems when ComboBox is changed programatically!
         //if (m_function != null) m_function.clearFit();
         //if (m_fit != null && m_fit.isSelected()) m_fit.setSelected(false);
      }
      private Fittable1DFunction m_function;
      private JASHist1DFunctionData m_fd;
      private JLabel m_state;
      private JCheckBox m_fit;
      private JComboBox m_choice;
      private JComboBox m_fitterChoice;
      private JLabel m_chi2;
      private FitWatcher m_fitWatcher;
      private javax.swing.Icon states[];
      
      private class FitNowHandler implements ActionListener
      {
         public void actionPerformed(ActionEvent e)
         {
            if (m_fit.isSelected())
            {
               FitterFactory ff = (FitterFactory) m_fitterChoice.getSelectedItem();
               Fitter fitter = ff.createFitter();
               fitter.setFunction(m_function);
               
               XYDataSource data = (XYDataSource) m_choice.getSelectedItem();
               
               fitter.setData(data);
               m_fitWatcher.setFit(fitter);
               fitter.start();
            }
            else
            {
               m_fitWatcher.clearFit().dispose();
            }
         }
      }
      private class FitWatcher implements Observer
      {
         FitWatcher(BoundedRangeModel model)
         {
            model.setMinimum(0);
            model.setMaximum(100);
            this.model = model;
            clearFit();
         }
         void setFit(Fitter fit)
         {
            m_fitter = fit;
            m_fitter.addObserver(this);
            m_state.setEnabled(true);
            m_state.repaint(); // Not needed? Swing 0.6.1
            m_chi2.setEnabled(true);
            m_chi2.repaint(); // Not needed? Swing 0.6.1
         }
         Fitter clearFit()
         {
            Fitter fit = m_fitter;
            if (fit != null)
            {
               fit.deleteObserver(this);
               m_fitter = null;
            }
            m_state.setEnabled(false);
            m_state.repaint(); // Not needed? Swing 0.6.1
            m_chi2.setEnabled(false);
            m_chi2.repaint(); // Not needed? Swing 0.6.1
            return fit;
         }
         public void update(Observable obs, Object arg)
         {
            if (arg instanceof FitUpdate)
            {
               FitUpdate fu = (FitUpdate) arg;
               int state = fu.getState();
               if (state == Fitter.OUTAHERE) clearFit();
               else
               {
                  model.setValue(fu.getPercent());
                  m_state.setIcon(states[state]);
                  
                  if (state == Fitter.FAILED)
                  {
                     Container w = JASHistPropFunctions.this;
                     while (!(w instanceof Frame)) w = w.getParent();
                     JOptionPane.showMessageDialog(w, fu.getReason(),
                             "Fit error...", JOptionPane.ERROR_MESSAGE);
                  }
               }
            }
         }
         private BoundedRangeModel model;
         private Fitter m_fitter;
      }
   }
   public JASHist getHist()
   {
      return m_jHist;
   }
   private JASHistPropFunctionStyle m_propStyle;
   private JASHistPropDataStyle m_dataStyle;
   private ParamManager m_paramManager;
   private FitManager m_fitManager;
   private boolean m_init;
   private JASHist1DFunctionData m_selected;
   private JList m_list;
   private DefaultListModel m_listModel;
   private TextField m_value;
   private JASHist m_jHist;
   private java.text.NumberFormat format = java.text.NumberFormat.getInstance();
   private JButton advanced;
   private ListNameChangeListener m_listNameChangeListener = new ListNameChangeListener();
   private class ListNameChangeListener implements Observer
   {
      public void update(Observable o, Object arg)
      {
         m_list.repaint();
      }
   }
}
