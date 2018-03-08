/*
 * XMLIOStreamManager.java
 *
 * Created on October 16, 2001, 8:03 AM
 *
 */
package org.freehep.xml.io;
import java.io.*;
import java.lang.String;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.*;

/**
 * Class XMLIOStreamManager deals with xml files.
 *
 * @author  turri
 * @version 1.0
 *
 */

public class XMLIOStreamManager
{

    private InputStream  xmlInputStream;
    private OutputStream xmlOutputStream;
    private String       systemId;
    private SAXBuilder   xmlBuilder   = new SAXBuilder(false);
    private XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());


    /**
     * The main constructor
     *
     */
    public XMLIOStreamManager() {
    }
    
    /**
     * The main constructor
     * @param xmlInputStream  the input Stream
     * @param xmlOutputStream the output Stream
     *
     */
    public XMLIOStreamManager( InputStream xmlInputStream, OutputStream xmlOutputStream ) 
    {
	setXMLInputStream( xmlInputStream );
	setXMLOutputStream( xmlOutputStream );
    }

    public SAXBuilder getBuilder() {
        return xmlBuilder;
    }
    
    /**
     * Set the input Stream
     * @param xmlInputStream the input Stream
     *
     */
    public void setXMLInputStream( InputStream xmlInputStream ) 
    {
	this.xmlInputStream = xmlInputStream;
    }
     public void setXMLInputStream( InputStream xmlInputStream, String systemId ) 
    {
	this.xmlInputStream = xmlInputStream;
        this.systemId = systemId;
    }   
    /**
     * Set the output Stream
     * @param xmlOutputStream the output Stream
     *
     */
    public void setXMLOutputStream( OutputStream xmlOutputStream ) 
    {
	this.xmlOutputStream = xmlOutputStream;
    }
    
    /**
     * From the input Stream convert the text in xml format
     * and return the main root Element
     * @return the jdom main Element
     * @exception JDOMException from the build method of SAXBuilder
     * @see org.jdom.input.SAXBuilder#build( InputStream )
     * @exception IOMException for the specific implementations of InputStream
     *
     */
    public Element getRootElement() throws JDOMException, IOException 
    {
        Document outputDoc = xmlBuilder.build(xmlInputStream, systemId);
        return outputDoc.getRootElement();
    }

    /** 
     * Write to the output Stream performing an xml to text conversion
     * @param rootEl is the root Element that gets converted
     *               to text and dumped in the output file.
     * @exception IOException from the output method of XMLOutputter
     * @see org.jdom.output.XMLOutputter#output( Document, OutputStream )
     *
     */
    public void saveRootElement( Element rootEl ) throws IOException {
        saveRootElement(rootEl,null);
    }
    public void saveRootElement( Element rootEl, DocType docType ) throws IOException 
    {
	Document inputDoc = docType != null ? new Document(rootEl, docType) : new Document(rootEl);
	xmlOutputter.output( inputDoc, xmlOutputStream );
        xmlOutputStream.close();
        
    }
}





