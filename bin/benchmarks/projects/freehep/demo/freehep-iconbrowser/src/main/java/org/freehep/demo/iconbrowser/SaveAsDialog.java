package org.freehep.demo.iconbrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import org.freehep.graphicsbase.util.export.ExportDialog;
import org.freehep.graphicsbase.util.export.ExportFileType;

import org.freehep.graphicsio.exportchooser.*;
import org.freehep.graphicsio.*;
import org.freehep.graphicsio.gif.*;
import org.freehep.graphicsio.ppm.*;
import org.freehep.graphicsio.png.*;

/**
 * A "Save As" dialog for saving files in a variety of formats.
 * This dialog is designed to work in both signed and unsigned web start
 * applications, as well as standalone applications. WebStart puts some limits
 * on the possible functionality, for example we can get the full file name,
 * and it has limited support for setting file filters, so the dialog has
 * to work around these limitations.
 *
 * @author tonyj
 * @version $Id: SaveAsDialog.java 14939 2013-05-17 20:58:48Z turri $
 */

public class SaveAsDialog extends ExportDialog implements SaveAs
{
   private static org.freehep.application.Application app = org.freehep.application.Application.getApplication();
   private org.freehep.application.services.FileAccess fileAccess;

   /**
    * Creates a new instance of SaveAsDialog.
    */
   public SaveAsDialog()
   {
      super(app.getAppName()+" "+app.getVersion(),false);
      setUserProperties(app.getUserProperties());
      addExportFileType(new PNGImageExporter());
      //TODO: Understand why this doesn't work, or better build list of available
      //      export types
      //addExportFileType(new PPMImageExporter());
      addExportFileType(new GIFImageExporter());
   }
   protected boolean writeFile(Component component, ExportFileType t) throws IOException
   {
      try
      {
         return super.writeFile(component,t);
      }
      catch (SecurityException x)
      {
         ByteArrayOutputStream os = new ByteArrayOutputStream(100000);
         try
         {
            t.exportToFile(os,component,this, new Properties(), "FreeHEP IconBrowser");
         }
         finally
         {
            os.close();
         }
         InputStream is = new ByteArrayInputStream(os.toByteArray());
         org.freehep.application.services.FileAccess acc = app.getServiceManager().saveFileAsDialog(null,null,"exportFile",is);
         return acc != null;
      }
   }
   // FIXME: MD these classes should just use the ExportFileType and their options
   // and use VectorGraphics to do the imaging, to make sure all options are set.
   private static class GIFImageExporter extends GIFExportFileType
   {
      public void exportToFile(OutputStream os, Component c, File file, Component parent,
                               Properties options, String creator) throws IOException
      {
         JLabel label = (JLabel) c;
         GIFEncoder encoder = new GIFEncoder(((ImageIcon) label.getIcon()).getImage(),os,progressive.isSelected());
         encoder.encode();
      }
   }
   private static class PNGImageExporter extends ImageExportFileType
   {
      public PNGImageExporter()
      {
         super("png");
      }
      public void exportToFile(OutputStream os, Component c, File file, Component parent,
                               Properties options, String creator) throws IOException
      {
         JLabel label = (JLabel) c;
         PNGEncoder encoder = new PNGEncoder(((ImageIcon) label.getIcon()).getImage(),true);
         byte[] ba = encoder.pngEncode();
         if (ba == null) throw new IOException("Cannot encode PNG.");
         os.write(ba);
      }
   }
   private static class PPMImageExporter extends ImageExportFileType
   {
      public PPMImageExporter()
      {
         super("ppm");
      }
      public void exportToFile(OutputStream os, Component c, File file, Component parent,
                               Properties options, String creator) throws IOException
      {
         JLabel label = (JLabel) c;
         PPMEncoder encoder = new PPMEncoder(((ImageIcon) label.getIcon()).getImage(),os);
         encoder.encode();
      }
   }

}
