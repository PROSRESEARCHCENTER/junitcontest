/*
 * JNLPServiceManager.java
 *
 * Created on February 1, 2001, 12:53 PM
 */

package org.freehep.application.services.jnlp;
import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import javax.jnlp.BasicService;
import javax.jnlp.ClipboardService;
import javax.jnlp.DownloadService;
import javax.jnlp.DownloadServiceListener;
import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.PersistenceService;
import javax.jnlp.PrintService;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.freehep.application.Application;
import org.freehep.application.services.FileAccess;
import org.freehep.application.services.ServiceManager;

/**
 *
 * @author tonyj
 * @version $Id: JNLPServiceManager.java 8584 2006-08-10 23:06:37Z duns $
 */
public class JNLPServiceManager implements ServiceManager
{
    private FileOpenService fileOpenService;
    private FileSaveService fileSaveService;
    private PrintService printService;
    private BasicService basicService;
    private PersistenceService persistenceService;
    private DownloadService downloadService;
    private ClipboardService clipboardService;
    private Application app;
    
    /** Creates new JNLPServiceManager */
    public JNLPServiceManager()
    {
        app = Application.getApplication();
    }
    private Object createService(String serviceName)
    {
        try
        {
            return javax.jnlp.ServiceManager.lookup(serviceName);
        }
        catch (UnavailableServiceException x)
        {
            throw new RuntimeException("Could not find "+serviceName);
        }      
    }
    private PrintService createPrintService()
    {
        return (PrintService) createService("javax.jnlp.PrintService");
    }
    private BasicService createBasicService()
    {
        return (BasicService) createService("javax.jnlp.BasicService");
    }
    private FileSaveService createFileSaveService()
    {
        return (FileSaveService) createService("javax.jnlp.FileSaveService");
    }
    private FileOpenService createFileOpenService()
    {
        return (FileOpenService) createService("javax.jnlp.FileOpenService");
    }
    private PersistenceService createPersistenceService()
    {
        return (PersistenceService) createService("javax.jnlp.PersistenceService");
    }
    private DownloadService createDownloadService()
    {
        return (DownloadService) createService("javax.jnlp.DownloadService");
    }  
    private ClipboardService createClipboardService()
    {
        return (ClipboardService) createService("javax.jnlp.ClipboardService");
    } 
    /**
      * Creates a new PageFormat instance and sets it to the default size and orientation.
      */
    public PageFormat getDefaultPage()
    {
        if (printService == null) printService = createPrintService();
        return printService.getDefaultPage();
    }
    
    /**
     * Prints a document using the given Pageable object
     */
    public boolean print(Pageable document)
    {
        if (printService == null) printService = createPrintService();
        return printService.print(document);
    }
    
    /**
     * Prints a document using the given Printable object.
     */
    public boolean print(Printable painter)
    {
        if (printService == null) printService = createPrintService();
        return printService.print(painter);
    }
    
    /**
     * Displays a dialog that allows modification of a PageFormat instance.
     */
    public PageFormat showPageFormatDialog(PageFormat page)
    {
        if (printService == null) printService = createPrintService();
        return printService.showPageFormatDialog(page);
    }
    public void loadUserPreferences(Properties props)
    {
        if (basicService == null) basicService = createBasicService();
        if (persistenceService == null) persistenceService = createPersistenceService();
        
        try
        {
            URL codebase = basicService.getCodeBase();
            URL fileURL = new URL(codebase,app.getAppName());
            FileContents file = persistenceService.get(fileURL);
            InputStream in = new BufferedInputStream(file.getInputStream());
            try
            {
               props.load(in);
            }
            finally
            {
               in.close();
            }
        }
        catch (Exception x)
        {
        }
    }
    public void storeUserPreferences(Properties props)
    {
        if (basicService == null) basicService = createBasicService();
        if (persistenceService == null) persistenceService = createPersistenceService();
        try
        {
            URL codebase = basicService.getCodeBase();
            URL fileURL = new URL(codebase,app.getAppName());
            OutputStream out;
            try
            {
                FileContents file = persistenceService.get(fileURL);
                out = new BufferedOutputStream(file.getOutputStream(true));
            }
            catch (FileNotFoundException x)
            {
                long size = persistenceService.create(fileURL,10000);
                FileContents file = persistenceService.get(fileURL);
                out = new BufferedOutputStream(file.getOutputStream(false));
            }
            try
            {
               props.store(out,app.getAppName()+" User Properties");
            }
            finally
            {
               out.close();
            }
        }
        catch (Exception x)
        {
        }
    }
    
    public FileAccess openFileDialog(FileFilter[] filters, FileFilter defaultFilter, String key)
    {
        if (basicService == null) basicService = createBasicService();
        if (fileOpenService == null) fileOpenService = createFileOpenService();
        try
        {
            FileContents fc = fileOpenService.openFileDialog(null,null);
            if (fc == null) return null;
            return new JNLPFileAccess(fc);
        }
        catch (IOException x)
        {
            return null;
        }
    }
        
    public FileAccess saveFileAsDialog(FileFilter[] filters, FileFilter defaultFilter, String key, InputStream in)
    {
        if (basicService == null) basicService = createBasicService();
        if (fileSaveService == null) fileSaveService = createFileSaveService();
        try
        {
            FileContents fc = fileSaveService.saveFileDialog(null,new String[]{ "gif" },in,"xxx.gif");
            if (fc == null) return null;
            return new JNLPFileAccess(fc);
        }
        catch (IOException x)
        {
            x.printStackTrace();
            return null;
        }       
    }
    public boolean isAvailable(String part)
    {
        if (basicService == null) basicService = createBasicService();
        if (downloadService == null) downloadService = createDownloadService();
        return downloadService.isPartCached(part);
    }
    public boolean makeAvailable(final String part)
    {
        return makeAvailable(part,"Loading "+part);
    }
    public boolean makeAvailable(final String part, final String message)
    {
        if (isAvailable(part)) return true;
        
        try
        {
            // note, in JAWS 1.0 there appears to be a (bug?) such that loadPart()
            // fails if used with the defaultProgressWindow from the event dispatch thread.
            // Maybe this will be fixed in future, but for now we use our own implementation
            // of the progress window.
            if (SwingUtilities.isEventDispatchThread())
            {             
                final ProgressDialog progress =  new ProgressDialog(message);
                final JDialog dlg = progress.createDialog(Application.getApplication(),"Downloading...");
                final Thread t = new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            downloadService.loadPart(part,progress);
                        }
                        catch (IOException x)
                        {
                            progress.setFailed();
                        }
                        finally
                        {
                            dlg.dispose();
                        }
                    }
                };
                t.start();
                dlg.setVisible(true);
                return progress.isDone();
            }
            else
            {
                downloadService.loadPart(part,downloadService.getDefaultProgressWindow());
                return true;
            }
        }
        catch (Exception x)
        {
            x.printStackTrace();
            return false;
        }
    }
    
    public Transferable getClipboardContents()
    {
       if (clipboardService == null) clipboardService = createClipboardService();
       return clipboardService.getContents();
    }
    
    public void setClipboardContents(Transferable contents)
    {
       if (clipboardService == null) clipboardService = createClipboardService();
       clipboardService.setContents(contents);
    }    
}
class ProgressDialog extends JOptionPane implements DownloadServiceListener, Runnable, ActionListener
{
    private JProgressBar progress = new JProgressBar(0,100);
    private JLabel statusLabel = new JLabel("Status");
    private int pc = 0;
    private String status;
    private boolean failed = false;
    private boolean abort = false;
    private JButton cancel = new JButton("Cancel");
    
    ProgressDialog(String message)
    {
        super();
        JPanel messagePane = new JPanel(new BorderLayout());
        messagePane.add(new JLabel(message),BorderLayout.NORTH);
        messagePane.add(progress);
        messagePane.add(statusLabel,BorderLayout.SOUTH);
        cancel.addActionListener(this);
        setMessage(messagePane);
        setMessageType(INFORMATION_MESSAGE);
        setOptions(new Object[]{ cancel });
    }
    public void downloadFailed(java.net.URL url, java.lang.String version)
    {
        failed = true;
    }
    public void progress(java.net.URL url, java.lang.String version, long readSoFar, long total, int overallPercent)
    {
        if (abort) throw new RuntimeException("Download aborted");
        pc = overallPercent;
        status = "Downloading "+readSoFar;
        SwingUtilities.invokeLater(this);
    }
    public void upgradingArchive(java.net.URL url, java.lang.String version, int patchPercent, int overallPercent)
    {
        if (abort) throw new RuntimeException("Download aborted");
        pc = overallPercent;
        status = "Upgrading "+patchPercent+"%";
        SwingUtilities.invokeLater(this);
    }
    public void validating(java.net.URL url, java.lang.String version, long entry, long total, int overallPercent)
    {
        if (abort) throw new RuntimeException("Download aborted");
        pc = overallPercent;
        status = "Validating "+entry+"/"+total;
        SwingUtilities.invokeLater(this);
    }
    public void run()
    {
        progress.setValue(pc);
        statusLabel.setText(status);
    }
    void setFailed()
    {
        failed = true;
    }
    boolean isDone()
    {
        return !failed && !abort;
    }
    public void actionPerformed(ActionEvent e)
    {
        abort = true;
    }
    
}
