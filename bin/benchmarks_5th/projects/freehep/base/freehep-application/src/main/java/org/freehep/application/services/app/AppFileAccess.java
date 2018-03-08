/*
 * AppFileAccess.java
 *
 * Created on February 22, 2001, 11:31 AM
 */

package org.freehep.application.services.app;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.freehep.application.services.FileAccess;

/**
 * An implementation of FileAccess for use in applications
 * @author  tonyj
 * @version $Id: AppFileAccess.java 8584 2006-08-10 23:06:37Z duns $ 
 */
class AppFileAccess implements FileAccess 
{
    private File file;
    
    /** Creates new AppFileAccess */
    public AppFileAccess(File file) 
    {
        this.file = file;
    }
    public boolean canRead() throws IOException
    {
        return file.canRead();
    }
    public boolean canWrite() throws IOException 
    {
        return file.canWrite();
    }
    public InputStream getInputStream() throws IOException 
    {
        return new FileInputStream(file);
    }
    public OutputStream getOutputStream(boolean append) throws IOException 
    {
        return new FileOutputStream(file);
    }
    public String getName() throws IOException 
    {
        return file.getPath();
    }
    public long getLength() throws IOException 
    {
        return file.length();
    }
    public long getMaxLength() throws IOException 
    {
        return Long.MAX_VALUE;
    }
    public long setMaxLength(long length) throws IOException 
    {
        return Long.MAX_VALUE;
    }
    public File getFile() throws IOException, SecurityException
    {
        return file;
    }
}

