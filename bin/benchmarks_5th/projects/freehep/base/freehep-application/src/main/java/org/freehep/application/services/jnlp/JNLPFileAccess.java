/*
 * JNLPFileAccess.java
 *
 * Created on February 22, 2001, 11:39 AM
 */

package org.freehep.application.services.jnlp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jnlp.FileContents;

import org.freehep.application.services.FileAccess;

/**
 * Implementation of FileAccess for use in unsigned JNLP applications.
 * @author tonyj
 * @version $Id: JNLPFileAccess.java 8584 2006-08-10 23:06:37Z duns $
 */
public class JNLPFileAccess implements FileAccess
{
    private FileContents fc;
    
    /** Creates new JNLPFileAccess */
    public JNLPFileAccess(FileContents fc) 
    {
        this.fc = fc;
    }
    public boolean canRead() throws IOException 
    {
        return fc.canRead();
    }
    public boolean canWrite() throws IOException 
    {
        return fc.canWrite();
    }
    public InputStream getInputStream() throws IOException
    {
        return fc.getInputStream();
    }
    public OutputStream getOutputStream(boolean append) throws IOException 
    {
        return fc.getOutputStream(append);
    }
    public String getName() throws IOException 
    {
        return fc.getName();
    }
    public long getLength() throws IOException 
    {
        return fc.getLength();
    }
    public long getMaxLength() throws IOException 
    {
        return fc.getMaxLength();
    }
    public long setMaxLength(long length) throws IOException 
    {
        return fc.setMaxLength(length);
    }
    public File getFile() throws IOException, SecurityException 
    {
        throw new SecurityException("JNLP does not allow access to File");
    }
}

