/*
 * XMLIOFileManager.java
 *
 * Created on October 16, 2001, 8:03 AM
 */
package org.freehep.xml.io;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jdom.DocType;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Class XMLIOFileManager deals with xml files.
 *
 * @author  turri
 * @version 1.0
 *
 */

public class XMLIOFileManager extends XMLIOStreamManager
{

    private File xmlFile;
    private boolean isZipped = false;
    
    public XMLIOFileManager( String fileName ) 
    {
	xmlFile = new File(fileName);
    }

    /**
     * After opening the input file, converts the text in xml format
     * and returns the main root Element
     * @return the jdom main Element or <code>null</code> if something
     *         went wrong in the text to xml conversion (the trace is
     *         printed).
     * @exception JDOMException from XMLIOStreamManager
     * @see XMLIOStreamManager#getRootElement()
     * @exception IOException from XMLIOStreamManager
     * @see XMLIOStreamManager#getRootElement()
     *
     */
    public Element getRootElement() throws JDOMException, IOException 
    {
        InputStream inputStream = null;
        try {
             inputStream = new GZIPInputStream( new FileInputStream( xmlFile ) );
        } catch ( IOException ioe ) {
            if ( inputStream != null ) inputStream.close();
            inputStream = new FileInputStream( xmlFile );
        }
	super.setXMLInputStream( inputStream, "file:"+xmlFile.getAbsolutePath());
	return super.getRootElement();
    }

    /** 
     * Write the output file performing an xml to text conversion
     * @param rootEl is the root Element that gets converted
     *               to text and dumped in the output file.
     * @exception IOException from XMLIOStreamManager
     * @see XMLIOStreamManager#saveRootElement( Element )
     *
     */
    public void saveRootElement( Element rootEl, String docName, String sysId ) throws IOException {
        if ( isXMLFileZipped() ) super.setXMLOutputStream( new GZIPOutputStream( new FileOutputStream( xmlFile ) ) );
        else super.setXMLOutputStream( new FileOutputStream( xmlFile ));
	if ( ! docName.equals("") && ! sysId.equals("") ) super.saveRootElement( rootEl, new DocType(docName, sysId) );
        else super.saveRootElement( rootEl );
    }
    public void saveRootElement( Element rootEl ) throws IOException {
        saveRootElement(rootEl,"","");
    }
    
    public boolean isXMLFileZipped() {
        return isZipped;
    }
    
    public void setXMLFileZipped( boolean isZipped ) {
        this.isZipped = isZipped;
    }
}
