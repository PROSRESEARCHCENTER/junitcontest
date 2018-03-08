// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep.xml;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import hep.graphics.heprep.HepRepDefaults;
import hep.graphics.heprep.ref.DefaultHepRepAttDef;
import hep.graphics.heprep.ref.DefaultHepRepAttValue;

/**
 * Handles the reading of the XML HepRep defaults file. This file is only allowed
 * to have a HepRepType at the top-level and HepRepAttDefs as well as HepRepAttValues
 * as children. No Sub-types are allowed. Names, Values and Types NEED to be specified.
 *
 * @author M.Donszelmann
 *
 * @version $Id: XMLHepRepDefaultsHandler.java 8584 2006-08-10 23:06:37Z duns $
 */

public class XMLHepRepDefaultsHandler extends DefaultHandler {

    XMLHepRepDefaultsHandler() {
    }

    public void startDocument() throws SAXException {
    }

    public void startElement(String namespace, String tag, String qName, Attributes atts) throws SAXException {
//            System.err.println(namespace+", "+tag+", "+qName);
        tag = tag.intern();
        if (tag == "type") {
            // ignored
        } else if (tag == "typetree") {
            // ignored
        } else if (tag == "heprep") {
            // ignored
        } else if (tag == "layer") {
            // ignored
        } else if (tag == "attvalue") {
            String name = atts.getValue("name");
            if (name == null) throw new SAXException("[XMLHepRepDefaultsHandler] tag: "+tag+" should have 'name' attribute");

            String value = atts.getValue("value");
            if (value == null) throw new SAXException("[XMLHepRepDefaultsHandler] tag: "+tag+" with name: "+name+" should have 'value' attribute");

            String type = atts.getValue("type");
            if (type == null) throw new SAXException("[XMLHepRepDefaultsHandler] tag: "+tag+" with name: "+name+" should have 'type' attribute");

            int showLabel = DefaultHepRepAttValue.toShowLabel(atts.getValue("showlabel"));
            HepRepDefaults.addAttValue(new DefaultHepRepAttValue(name, value, type, showLabel));

        } else if (tag == "attdef") {
            String name = atts.getValue("name");
            String desc = atts.getValue("desc");
            String category = atts.getValue("category");
            String extra = atts.getValue("extra");
            HepRepDefaults.addAttDef(new DefaultHepRepAttDef(name, desc, category, extra));

        } else {
            throw new SAXException("[XMLHepRepDefaultsHandler] Unknown tag: "+tag);
        }
    }

    public void endElement(String namespace, String tag, String qName) throws SAXException {
//            System.out.println("/"+namespace+", "+tag+", "+qName);
        if (tag.lastIndexOf(':') >= 0) tag = tag.substring(tag.lastIndexOf(':')+1);
        tag = tag.intern();
        if (tag == "type") {
        } else if (tag == "typetree") {
        } else if (tag == "heprep") {
        } else if (tag == "layer") {
        } else if (tag == "attvalue") {
        } else if (tag == "attdef") {
        } else {
            throw new SAXException("[XMLHepRepDefaultsHandler] Unknown tag: "+tag);
        }
        if (Thread.interrupted()) throw new SAXException(new InterruptedException());
    }

	public InputSource resolveEntity(String publicId, String systemId) {
	    System.out.println("Resolving: "+systemId);
	    if (publicId != null) {
	        return null;
	    }

        // try to open systemId directly
        InputStream is = null;
        URL url = null;

        try {
            url = new URL(systemId);
            is = url.openStream();
        } catch (MalformedURLException mfue) {
            return null;
        } catch (IOException ioe) {
            // try to resolve systemId relative to object or class
            String file = url.getFile().substring(url.getFile().lastIndexOf('/')+1);
            is = getClass().getResourceAsStream(file);
        }

        return new InputSource(is);
	}
}
