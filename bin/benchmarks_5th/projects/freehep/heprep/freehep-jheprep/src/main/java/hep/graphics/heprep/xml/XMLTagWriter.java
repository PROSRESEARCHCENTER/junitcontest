// Copyright 2005, FreeHEP.
package hep.graphics.heprep.xml;

import java.awt.Color;
import java.io.IOException;

/**
 * XMLTagWriter Interface. Attributes need to be set before tags are written.
 *
 * @author Mark Donszelmann
 * @version $Id: XMLTagWriter.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface XMLTagWriter {
    
    /**
     * Write an xml open tag
     * @param ns namespace  
     * @param name tagname
     * @throws IOException if stream cannot be written
     */
    public void openTag(String ns, String name) throws IOException;
    
    /**
     * Write empty tag
     * @param ns namespace
     * @param name tagname
     * @throws IOException if stream cannot be written
     */
    public void printTag(String ns, String name) throws IOException;

    /**
     * Set String attribute
     * @param ns namespace
     * @param name attribute name
     * @param value attribute value
     */
    public void setAttribute(String ns, String name, String value);
    
    /**
     * Set double attribute
     * @param ns namespace
     * @param name attribute name
     * @param value attribute value
     */
    public void setAttribute(String ns, String name, double value);
                        
    /**
     * Close writer
     * @throws IOException if stream cannot be written
     */
    public void close() throws IOException;
    
    /**
     * Open XML doc with standard parameters
     * @throws IOException if stream cannot be written
     */
    public void openDoc() throws IOException;
    
    /**
     * Open XML doc 
     * @param version version string
     * @param encoding encoding
     * @param standalone if XML is standalone
     * @throws IOException if stream cannot be written
     */
    public void openDoc(String version, String encoding, boolean standalone) throws IOException;
    
    /**
     * Close XML doc
     * @throws IOException if stream cannot be written
     */
    public void closeDoc() throws IOException;
    
    /**
     * Write an xml open tag
     * @param name tagname
     * @throws IOException if stream cannot be written
     */
    public void openTag(String name) throws IOException;
    
    /**
     * Close nearest tag
     * @throws IOException if stream cannot be written
     */
    public void closeTag() throws IOException;
    
    /**
     * Write empty tag
     * @param name tagname
     * @throws IOException if stream cannot be written
     */
    public void printTag(String name) throws IOException;
    
    /**
     * Set String attribute
     * @param name attribute name
     * @param value attribute value
     */
    public void setAttribute(String name, String value);

    /**
     * Set Color attribute
     * @param name attribute name
     * @param value attribute value
     */
    public void setAttribute(String name, Color value);
    
    /**
     * Set long attribute
     * @param name attribute name
     * @param value attribute value
     */
    public void setAttribute(String name, long value);

    /**
     * Set int attribute
     * @param name attribute name
     * @param value attribute value
     */
    public void setAttribute(String name, int value);

    /**
     * Set boolean attribute
     * @param name attribute name
     * @param value attribute value
     */
    public void setAttribute(String name, boolean value);
    
    /**
     * Set double attribute
     * @param name attribute name
     * @param value attribute value
     */
    public void setAttribute(String name, double value);

}
