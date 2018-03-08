/*
 * SaveAsDialog.java
 *
 * Created on March 28, 2002, 2:44 PM
 */

package jas.hist;
import jas.util.Application;
import jas.util.JASDialog;
import jas.util.JASState;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


/**
 *
 * @author tonyj
 * @version $Id: SaveAsDialog.java 11553 2007-06-05 22:06:23Z duns $
 */
public class SaveAsDialog extends JASDialog implements ActionListener
{ 
   private static Vector list = new Vector();
   private static Properties props;
   static
   {      
       Application app = Application.getApplication();
       if (app != null) props = app.getUserProperties();
       else props = new Properties();

       String prop = "org.freehep.graphics2d.exportchooser.EPS_PSExportFileType.EmbedFonts";
       String embed = props.getProperty(prop);
       if (embed == null) props.setProperty(prop,"Embed unknown as Type3");
       
       register(new SaveAsPlotML());
       register(new SaveAsGIF());
   }
   
   public static void register(SaveAsPlugin plugin)
   {
      list.addElement(plugin);
      plugin.restoreOptions(props);
   }
   
   /** Creates a new instance of SaveAsDialog */
   public SaveAsDialog(Component c)
   {
      super((Frame) SwingUtilities.getAncestorOfClass(Frame.class,c),"Save As...");
      this.component = c;
      
      JPanel pane = new JPanel();
      pane.setLayout(new GridBagLayout());
      
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
      pane.add(file, gridBagConstraints);

      gridBagConstraints.fill = 1;
      gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
      pane.add(browse, gridBagConstraints);

      Vector fileTypes = new Vector();
      for (int i=0; i<list.size(); i++)
      {
         SaveAsPlugin type = (SaveAsPlugin) list.elementAt(i);
         if (type.supportsClass(c)) fileTypes.addElement(type);
      }
      
      type = new JComboBox(fileTypes);
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.gridwidth = 1;
      pane.add(type, gridBagConstraints);
      
      gridBagConstraints.fill = 1;
      gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
      pane.add(advanced, gridBagConstraints);
      getContentPane().add(pane,BorderLayout.CENTER);
      
      String defFile = System.getProperty("user.home")+File.separator+"plot.xxx";

      String dType = props.getProperty("jas.hist.saveAsType");
      if (dType != null)
      {
         for (int i=0; i<fileTypes.size(); i++)
         {
            SaveAsPlugin saveAs = (SaveAsPlugin) fileTypes.elementAt(i);
            if (saveAs.getFileFilter().getDescription().equals(dType)) 
            {
               type.setSelectedItem(saveAs);
               break;
            }
         }
      }
      defFile = props.getProperty("jas.hist.saveAsFile",defFile);

      File f = new File(defFile);
      f = currentType().adjustFilename(f);
      file.setText(f.toString());
      
      browse.addActionListener(this);
      advanced.addActionListener(this);
      advanced.setEnabled(currentType().hasOptions());
      type.setRenderer(new SaveAsRenderer());
      type.addActionListener(this);
      file.getDocument().addDocumentListener(this);
   }
   private SaveAsPlugin currentType()
   {
      return (SaveAsPlugin) type.getSelectedItem();
   }
   public void actionPerformed(ActionEvent e)
   {
      Object source = e.getSource();
      if (source == browse)
      {
         JFileChooser chooser = new JFileChooser(file.getText());
         chooser.setFileFilter(currentType().getFileFilter());
         if (chooser.showDialog(this,"Select") == chooser.APPROVE_OPTION)
         {
            file.setText(chooser.getSelectedFile().toString());
         }  
      }
      else if (source == advanced)
      {
         Properties save = new Properties();
         currentType().saveOptions(save);
         JPanel panel = currentType().getOptionsPanel();
         int rc = JOptionPane.showConfirmDialog(this,panel,"Options for "+currentType().getFileFilter().getDescription(),
                                       JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
         if (rc != JOptionPane.OK_OPTION) currentType().restoreOptions(save);
      }
      else if (source == type)
      {
         advanced.setEnabled(currentType().hasOptions());
         File f1 = new File(file.getText());
         File f2 = currentType().adjustFilename(f1);
         if (!f1.equals(f2)) file.setText(f2.toString());
      }
   }
   protected void onOK()
   {
      try
      {
         File f = new File(file.getText());
         if (f.exists()) 
         {
            int ok = JOptionPane.showConfirmDialog(this,"Replace existing file?");
            if (ok != JOptionPane.OK_OPTION) return;
         }
            
         SaveAsPlugin t = currentType();
         OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
         t.saveAs(component,os,f,this);
         os.close();
         super.onOK();
        
         props.put("jas.hist.saveAsFile",file.getText());
         props.put("jas.hist.saveAsType",currentType().getFileFilter().getDescription());
         t.saveOptions(props);
      }
      catch (IOException x)
      {
         JOptionPane.showMessageDialog(this,x);
      }
   }
   protected void enableOK(JASState state)
   {
      state.setEnabled(file.getText().length()>0);
   }
   private JButton browse = new JButton("Browse...");
   private JButton advanced = new JButton("Options...");
   private JTextField file = new JTextField(40);
   private JComboBox type;
   private Component component;
   
   private class SaveAsRenderer extends DefaultListCellRenderer
   {
      public Component getListCellRendererComponent(JList list,
                                              Object value,
                                              int index,
                                              boolean isSelected,
                                              boolean cellHasFocus)
      {
         super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
         if (value instanceof SaveAsPlugin)
         {
            this.setText(((SaveAsPlugin) value).getFileFilter().getDescription());
         }
         return this;
      }
   }
}
