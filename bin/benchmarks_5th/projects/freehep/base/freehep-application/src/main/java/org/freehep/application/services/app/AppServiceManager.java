/*
 * AppServiceManager.java
 *
 * Created on January 29, 2001, 2:50 PM
 */

package org.freehep.application.services.app;
import java.awt.datatransfer.Transferable;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.freehep.application.Application;
import org.freehep.application.services.FileAccess;
import org.freehep.application.services.ServiceManager;

/**
 * A basic implementation of ServiceManager, for use in applications.
 * @author  tonyj
 * @version $Id: AppServiceManager.java 8584 2006-08-10 23:06:37Z duns $
 */
public class AppServiceManager implements ServiceManager
{
    private PrinterJob pj;
    protected Application app;
    
    public AppServiceManager()
    {
       app = Application.getApplication();
    }
    public PageFormat getDefaultPage()
    {
        if (pj == null) pj = createPrintJob();
        return pj.defaultPage();
    }
    public PageFormat showPageFormatDialog(PageFormat page)
    {
        if (pj == null) pj = createPrintJob();
        return pj.pageDialog(page);
    }
    public boolean print(Printable painter)
    {
        if (pj == null) pj = createPrintJob();
        pj.setPrintable(painter);
        return print();
    }
    public boolean print(Pageable document)
    {
        if (pj == null) pj = createPrintJob();
        pj.setPageable(document);
        return print();
    }
    private boolean print()
    {
        if (!pj.printDialog()) return false;
        try
        {
            pj.print();
            return true;
        }
        catch (PrinterException p)
        {
            return false;
        }
        finally
        {
            pj = null; // Do not reuse
        }
    }
    private PrinterJob createPrintJob()
    {
        return PrinterJob.getPrinterJob();
    }
    private String userPrefFile()
    {
       return app.getAppProperties().getProperty("userPrefDir");
    }
    public void loadUserPreferences(Properties props)
    {
        try
        {
            InputStream in = new BufferedInputStream(new FileInputStream(userPrefFile()));
            try
            {
               props.load(in);
            }
            finally
            {
               in.close();
            }
        }
        catch (IOException x) {}
    }
    public void storeUserPreferences(Properties props)
    {
        try
        {
            File f = new File(userPrefFile());
            if (!f.exists())
            {
               File parent = f.getParentFile();
               if (!parent.exists()) parent.mkdirs();
            }
            OutputStream out = new BufferedOutputStream(new FileOutputStream(userPrefFile()));
            try
            {
               props.store(out,app.getAppName()+" User Properties");
            }
            finally
            {
               out.close();
            }
        }
        catch (IOException x) {}
    }
    public FileAccess openFileDialog(FileFilter[] filters, FileFilter defaultFilter, String key)
    {
       JFileChooser dlg = new JFileChooser();
       Properties user = app.getUserProperties();
       if (key != null)
       {
         String dir = user.getProperty(key);
         if (dir != null) dlg.setCurrentDirectory(new File(dir));
       }
       if (filters != null) for (int i=0; i<filters.length; i++) dlg.addChoosableFileFilter(filters[i]);
       if (defaultFilter != null) dlg.setFileFilter(defaultFilter);
       if (dlg.showOpenDialog(app) == dlg.APPROVE_OPTION)
       {
           File file = dlg.getSelectedFile();
           if (key != null) user.setProperty(key,file.getAbsolutePath());
           return new AppFileAccess(file);
       }
       else return null;
    }
        
    public FileAccess saveFileAsDialog(FileFilter[] filters, FileFilter defaultFilter, String key, InputStream in)
    {
       JFileChooser dlg = new JFileChooser();
       Properties user = app.getUserProperties();
       if (key != null)
       {
         String dir = user.getProperty(key);
         if (dir != null) dlg.setCurrentDirectory(new File(dir));
       }
       if (filters != null) for (int i=0; i<filters.length; i++) dlg.addChoosableFileFilter(filters[i]);
       if (defaultFilter != null) dlg.setFileFilter(defaultFilter);
       if (dlg.showSaveDialog(app) == dlg.APPROVE_OPTION)
       {
          File file = dlg.getSelectedFile();
          if (key != null) user.setProperty(key,file.getAbsolutePath());
          return new AppFileAccess(file);
       }
       else return null;
    }
    public boolean isAvailable(String part)
    {
        return true;
    }
    public boolean makeAvailable(String part)
    {
        return true;
    }
    public Transferable getClipboardContents()
    {
       return app.getToolkit().getSystemClipboard().getContents(null);
    }
    public void setClipboardContents(Transferable contents)
    {
       app.getToolkit().getSystemClipboard().setContents(contents,null);
    }    
}