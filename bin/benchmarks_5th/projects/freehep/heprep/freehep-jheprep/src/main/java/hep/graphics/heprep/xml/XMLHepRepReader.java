// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep.xml;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.xml.parsers.*;

import org.xml.sax.*;

import org.xmlpull.v1.XmlPullParserException;

import org.freehep.util.io.*;
import hep.graphics.heprep.*;
import hep.graphics.heprep.ref.*;
import hep.graphics.heprep.wbxml.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: XMLHepRepReader.java 8584 2006-08-10 23:06:37Z duns $
 */
public class XMLHepRepReader extends AbstractHepRepReader {

    private InputStream sequence = null;
    
    XMLHepRepReader(InputStream in) throws IOException {
        super(in);
        reset();
    }

    XMLHepRepReader(String fileName) throws IOException {
        super(fileName);
        reset();
    }

    public void close() throws IOException {
        super.close();
        if (sequence != null) {
            sequence.close();
        }
    }

    public void reset() throws IOException, UnsupportedOperationException {
        if ((input != null) && !(input instanceof ZipInputStream)) {
            sequence = new BufferedInputStream(input);
            if (input instanceof DataInputStream) {
                sequence = new PushbackInputStream(sequence);
            } else {
                sequence = new XMLSequence(sequence);
            }
        } else if ((name != null) && !name.toLowerCase().endsWith(".zip")) {
            if (sequence != null) sequence.close();
            
            sequence = new FileInputStream(name);
            
            if (name.toLowerCase().endsWith(".gz")) {
                sequence = new GZIPInputStream(sequence);
            }
            sequence = new BufferedInputStream(sequence);
            
            if (name.toLowerCase().indexOf(".bheprep") >= 0) {
                sequence = new PushbackInputStream(sequence);
            } else {
                sequence = new XMLSequence(sequence);
            }
        } else {
            super.reset();
        }
    }
    
    public boolean hasNext() throws IOException, UnsupportedOperationException {
        // NOTE: for binary heprep we just assume there is a next bheprep available.
        // FIXME: we could add a PushBackInputStream JHEPREP-20
        if (sequence != null) {
            if (sequence instanceof XMLSequence) return ((XMLSequence)sequence).hasNext();
               
            if (sequence instanceof PushbackInputStream) {
                int b = sequence.read();
                if (b < 0) return false;
                ((PushbackInputStream)sequence).unread(b);
                return true;
            }   
               
            // benefit of the doubt
            return true;
        }
        return super.hasNext();
    }

    public HepRep next() throws IOException, UnsupportedOperationException, NoSuchElementException {
        if (!hasNext()) throw new UnsupportedOperationException(getClass()+" no more elements");
        
        if (sequence != null) {
            return (sequence instanceof XMLSequence) ? readHepRep(((XMLSequence)sequence).next(), false) 
                                                     : readHepRep(sequence, true);
        }
        
        return super.next();
    }


    protected HepRep readHepRep(InputStream stream, boolean binary) throws IOException {
        HepRep heprep = readHepRep(new XMLHepRepFactory().createHepRep(), stream, binary);
        return heprep;
    }

    private HepRep readHepRep(HepRep heprep, InputStream stream, boolean binary) throws IOException {
    
        if (binary) {
            try {
                BHepRepReader reader = new BHepRepReader(heprep);
                reader.parse(stream);
    
                return heprep;
            } catch (XmlPullParserException e) {
                IOException exception = new IOException();
                exception.initCause(e);
                throw exception;
            }                 
        } else {
            try {
                XMLHepRepHandler handler = new XMLHepRepHandler(heprep);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                SAXParser xml = factory.newSAXParser();
                // show namespace specific attributes
                xml.getXMLReader().setFeature("http://xml.org/sax/features/namespace-prefixes", true);
    
                // now read real document
                InputSource source = new InputSource(new BufferedReader(new InputStreamReader(stream)));
                xml.parse(source, handler);
    
                return heprep;
            } catch (ParserConfigurationException e) {
                IOException exception = new IOException();
                exception.initCause(e);
                throw exception;
            } catch (SAXParseException e) {
                if ((e.getLineNumber() == 1) && (e.getColumnNumber() == -1)) throw new EOFException();
                IOException exception = new IOException("Syntax error at Line: "+e.getLineNumber()+"("+e.getColumnNumber()+")");
                exception.initCause(e);
                throw exception;
            } catch (SAXException e) {
                IOException exception = new IOException();
                exception.initCause(e);
                throw exception;
            }
        }
    }


    /**
     * Read the default attribute definitions and values from an XML file into HepRepDefaults.
     * @throws IOException when AttributeDefaults cannot be found and or read.
     */
    public static void readDefaults() throws IOException {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            XMLHepRepDefaultsHandler handler = new XMLHepRepDefaultsHandler();
            xmlReader.setContentHandler(handler);
            xmlReader.setDTDHandler(handler);
            xmlReader.setErrorHandler(handler);
            xmlReader.setEntityResolver(handler);

            Reader reader = new BufferedReader(
                            new InputStreamReader(XMLHepRepReader.class.getResourceAsStream("AttributeDefaults.xml")));
            InputSource source = new InputSource(reader);
            xmlReader.parse(source);
            reader.close();
        } catch (Exception e) {
            IOException exception = new IOException();
            exception.initCause(e);
            throw exception;
        }
    }
}
