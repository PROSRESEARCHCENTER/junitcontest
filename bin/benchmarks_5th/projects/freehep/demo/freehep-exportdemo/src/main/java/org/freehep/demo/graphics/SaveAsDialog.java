package org.freehep.demo.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import org.freehep.graphicsbase.util.export.ExportDialog;
import org.freehep.graphicsbase.util.export.ExportFileType;



/**
 * A "Save As" dialog for saving files in a variety of formats.
 * This dialog is designed to work in both signed and unsigned web start
 * applications, as well as standalone applications. WebStart puts some limits
 * on the possible functionality, for example we can get the full file name,
 * and it has limited support for setting file filters, so the dialog has
 * to work around these limitations.
 *
 * @author tonyj
 * @version $Id: SaveAsDialog.java 14938 2013-05-17 20:54:40Z turri $
 */

public class SaveAsDialog extends ExportDialog
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
            t.exportToFile(os,component,this,new Properties(), "Export Graphics Demo");
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
}
