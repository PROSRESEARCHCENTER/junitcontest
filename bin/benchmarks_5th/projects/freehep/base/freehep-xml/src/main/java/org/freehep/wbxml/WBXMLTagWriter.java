// Copyright 2007, FreeHEP.
package org.freehep.wbxml;

import java.awt.Color;
import java.io.IOException;

/**
 * WBXMLTagWriter Interface. Attributes need to be set before tags are written.
 *
 * @author Mark Donszelmann
 * @version $Id: XMLTagWriter.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface WBXMLTagWriter {
                            
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
    
    public void referToDTD(String name, String system);
    public void referToDTD(String name, String pid, String ref);
    
    /**
     * Write an xml open tag
     * @param name tagname
     * @throws IOException if stream cannot be written
     */
    public void openTag(int tag) throws IOException;
    
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
    public void printTag(int tag) throws IOException;
    
    public void printComment(String comment) throws IOException;
    
    public void print(String text) throws IOException;
    
    /**
     * Set String attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, String value);

    /**
     * Set String attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, String[] value, int offset, int length);

    /**
     * Set Color attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, Color value);
    
    /**
     * Set Color attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, Color[] value, int offset, int length);
    
    /**
     * Set byte attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, byte value);

    /**
     * Set byte attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, byte[] value, int offset, int length);

    /**
     * Set char attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, char value);

    /**
     * Set char attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, char[] value, int offset, int length);

    /**
     * Set long attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, long value);

    /**
     * Set long attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, long[] value, int offset, int length);

    /**
     * Set int attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, int value);

    /**
     * Set int attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, int[] value, int offset, int length);

    /**
     * Set short attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, short value);

    /**
     * Set short attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, short[] value, int offset, int length);

    /**
     * Set boolean attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, boolean value);
    
    /**
     * Set boolean attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, boolean[] value, int offset, int length);
    
    /**
     * Set float attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, float value);

    /**
     * Set float attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, float[] value, int offset, int length);

    /**
     * Set double attribute
     * @param tag attributeID
     * @param value attribute value
     */
    public void setAttribute(int tag, double value);

    /**
     * Set double attribute
     * @param tag attributeID
     * @param value attribute value
     * @param offset start index in array
     * @param length number of primitives/object to write
     */
    public void setAttribute(int tag, double[] value, int offset, int length);
}
