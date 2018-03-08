/*
 * SaveAsPlugin.java
 *
 * Created on March 28, 2002, 3:12 PM
 */

package jas.hist;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author tonyj
 */
public interface SaveAsPlugin
{
   public boolean supportsClass(Object o);
   public boolean hasOptions();
   public FileFilter getFileFilter();
   public JPanel getOptionsPanel();
   public File adjustFilename(File file);
   public void saveAs(Component c, OutputStream os, File file, Component dialogParent) throws IOException;
   public void saveOptions(Properties props);
   public void restoreOptions(Properties props);
}
