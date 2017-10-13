// Copyright 2005, FreeHEP.
package hep.graphics.heprep.xml;

import hep.graphics.heprep.ref.DefaultHepRepAttValue;

import java.awt.Color;
import java.io.Writer;

import org.freehep.xml.util.XMLWriter;

/**
 * A class to make XMLWriter implement XMLTagWriter
 *
 * @author Mark Donszelmann
 * @version $Id: ASCIIHepRepWriter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ASCIIHepRepWriter extends XMLWriter implements XMLTagWriter {
    
    /**
     * Create a ASCIIHepRepWriter
     * @param w writer
     * @param indentString indent string
     * @param defaultNameSpace name space to use
     */
    public ASCIIHepRepWriter(Writer w, String indentString, String defaultNameSpace) {
        super(w, indentString, defaultNameSpace);  
    }

    /**
     * Create a ASCIIHepRepWriter
     * @param w writer
     * @param indentString indent string
     */
    public ASCIIHepRepWriter(Writer w, String indentString) {
        super(w, indentString);
    }

    /**
     * Create a ASCIIHepRepWriter
     * @param w writer
     */
	public ASCIIHepRepWriter(Writer w) {
        super(w);
	}
	
    public void setAttribute(String name, Color value) {
        if (name.equals("value")) setAttribute("type", "Color");
        setAttribute(name, DefaultHepRepAttValue.getAsString(value));
    }
    
    public void setAttribute(String name, double value) {
        if (name.equals("value")) setAttribute("type", "double");
        setAttribute(name, DefaultHepRepAttValue.getAsString(value));
    }
    
    public void setAttribute(String name, long value) {
        if (name.equals("value")) setAttribute("type", "long");
        setAttribute(name, DefaultHepRepAttValue.getAsString(value));
    }
    
    public void setAttribute(String name, int value) {
        if (name.equals("showlabel")) {
            String label = DefaultHepRepAttValue.toShowLabel(value);
            setAttribute("showlabel", label);
        } else {
            if (name.equals("value")) setAttribute("type", "int");
            setAttribute(name, DefaultHepRepAttValue.getAsString(value));
        }
    }
    
    public void setAttribute(String name, boolean value) {
        if (name.equals("value")) setAttribute("type", "boolean");
        setAttribute(name, DefaultHepRepAttValue.getAsString(value));
    }
    
}
