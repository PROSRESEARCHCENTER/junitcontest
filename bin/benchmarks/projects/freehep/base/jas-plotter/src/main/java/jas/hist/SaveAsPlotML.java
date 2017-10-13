/*
 * SaveAsPlotML.java
 * Created on March 28, 2002, 3:36 PM
 */

package jas.hist;
import jas.util.FileTypeFileFilter;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author  tonyj
 */
public class SaveAsPlotML implements SaveAsPlugin
{
   public boolean hasOptions()
   {
      return true;
   }
   public FileFilter getFileFilter()
   {
      return new FileTypeFileFilter("plotml","XML Plot File (*.plotml)");
   }
   public JPanel getOptionsPanel()
   {
      JPanel custom = new JPanel();
		custom.add(button1);
		custom.add(button2);
		ButtonGroup bg = new ButtonGroup();
		bg.add(button1);
		bg.add(button2);
      return custom;
   }
   public void saveAs(Component c, OutputStream os, File file, Component dialogParent) throws IOException
   {
      Writer writer = new OutputStreamWriter(os);
      ((JASHist) c).writeXML(writer,button1.isSelected());
      writer.close();
   }
   
   public File adjustFilename(File file)
   {
      String name = file.getName();
      if (name.endsWith(".plotml")) return file;
      int pos = name.indexOf('.');
      if (pos >= 0) name = name.substring(0,pos);
      name += ".plotml";
      File parent = file.getParentFile();
      return new File(parent,name);
   }
   
   public boolean supportsClass(Object o)
   {
      return (o instanceof JASHist);
   }
   public void restoreOptions(Properties props)
   {
   }
   public void saveOptions(Properties props)
   {
   }
   private JRadioButton button1 = new JRadioButton("Save current snapshot of data",true);
	private JRadioButton button2 = new JRadioButton("Save reference to live data");
}
