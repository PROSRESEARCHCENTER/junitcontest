package jas.export;

import jas.hist.SaveAsPlugin;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.freehep.graphicsbase.util.export.ExportFileType;

/**
 *
 * @author tonyj
 * @version $Id: SaveAsPluginAdapter.java 14048 2012-10-23 23:11:12Z onoprien $
 */
public class SaveAsPluginAdapter implements SaveAsPlugin
{
   private ExportFileType eft;
   /** Creates a new instance of SaveAsPluginAdapter */
   public SaveAsPluginAdapter(ExportFileType eft)
   {
      this.eft = eft;
   }
   public File adjustFilename(File file)
   {
      return eft.adjustFilename(file, null);
   } 
   public FileFilter getFileFilter()
   {
      return eft.getFileFilter();
   }
   public JPanel getOptionsPanel()
   {
      return eft.createOptionPanel(null);
   }
   public boolean hasOptions()
   {
      return eft.hasOptionPanel();
   }
   public void saveAs(Component c, OutputStream out, File file, Component dialogParent) throws IOException
   {
      eft.exportToFile(file, c, dialogParent, null, "");
   }
   public boolean supportsClass(Object o)
   {
      return true;
   }
   public void saveOptions(Properties props)
   {
//      eft.saveOptions(props);
   }
   public void restoreOptions(Properties props)
   {
//      eft.restoreOptions(props);
   }
}
