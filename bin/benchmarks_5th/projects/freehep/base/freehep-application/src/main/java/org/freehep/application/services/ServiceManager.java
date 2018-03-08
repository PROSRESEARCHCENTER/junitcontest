/*
 * ServiceManager.java
 *
 * Created on January 29, 2001, 2:41 PM
 */

package org.freehep.application.services;

import java.awt.datatransfer.Transferable;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.filechooser.FileFilter;

/**
 * This interface is implemented by all ServiceManagers. By using this interface
 * instead of directly using JFileChooser, Printer etc, applications can make
 * themselves JavaWebStart compatible, but still run as normal applications without
 * needing the Java Web Start classes. By using a custom ServiceManager it is possible
 * to provide a customized filechooser (for example) without making changes at many
 * places within your program.
 *
 * @author tonyj
 * @version $Id: ServiceManager.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface ServiceManager
{
    // Printing services
    /**
     * Creates a new PageFormat instance and sets it to the default size and orientation.
     */
    PageFormat getDefaultPage();
    /**
     * Prints a document using the given Pageable object
     */
    boolean print(Pageable document);
     /**
      * Prints a document using the given Printable object.
      */
    boolean print(Printable painter);
     /**
      * Displays a dialog that allows modification of a PageFormat instance.
      */
    PageFormat showPageFormatDialog(PageFormat page);
    
    // Persistency services

    void loadUserPreferences(Properties props);
    void storeUserPreferences(Properties props);

    // File Access services
    
    FileAccess openFileDialog(FileFilter[] filters, FileFilter defaultFilter,String key);
    //FileAccess openMultiFileDialog(FileFilter[] xxx, defaultFileFilter xxx, String key);
    FileAccess saveFileAsDialog(FileFilter[] filters, FileFilter defaultFilter, String key, InputStream in);
    
    // Application extensions services
    
    boolean isAvailable(String part);
    boolean makeAvailable(String part);
    
    // Clipboard service extensions
    
    Transferable getClipboardContents();
    void setClipboardContents(Transferable contents);
}

