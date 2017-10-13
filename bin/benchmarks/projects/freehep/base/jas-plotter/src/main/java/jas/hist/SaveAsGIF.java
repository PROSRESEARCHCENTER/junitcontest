package jas.hist;
import jas.util.FileTypeFileFilter;
import jas.util.encoder.GifEncoder;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author  tonyj
 */
public class SaveAsGIF implements SaveAsPlugin
{
   public boolean hasOptions()
   {
      return false;
   }
   public FileFilter getFileFilter()
   {
      return new FileTypeFileFilter("gif","GIF File (*.gif)");
   }
   public JPanel getOptionsPanel()
   {
      return null;
   }
   public void saveAs(Component c, OutputStream os, File file, Component dialogParent) throws IOException
   {
        Image img = c.createImage(c.getWidth(),c.getHeight());
        Graphics g = img.getGraphics();
         
        // TODO: It would be better to use the PrintHelper to do this??
        // TODO: Make sure we get high quality printing for GIF.
        RepaintManager pm = RepaintManager.currentManager(c);
        boolean save = pm.isDoubleBufferingEnabled();
        pm.setDoubleBufferingEnabled(false);
        c.print(g);
        g.dispose();
        pm.setDoubleBufferingEnabled(save);
         
        // The rest could be done in a separate thread
        
        GifEncoder encoder = new GifEncoder(img,os);
        encoder.encode();
        img.flush();
   }
   public File adjustFilename(File file)
   {
      String name = file.getName();
      if (name.endsWith(".gif")) return file;
      int pos = name.indexOf('.');
      if (pos >= 0) name = name.substring(0,pos);
      name += ".gif";
      File parent = file.getParentFile();
      return new File(parent,name);
   }
   
   public boolean supportsClass(Object o)
   {
      return true;
   }
   public void saveOptions(Properties props)
   {
   }
   public void restoreOptions(Properties props)
   {
   }
}