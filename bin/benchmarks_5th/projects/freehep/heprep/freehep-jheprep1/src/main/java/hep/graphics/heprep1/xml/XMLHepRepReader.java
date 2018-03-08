// Copyright 2000-2004, FreeHEP.
package hep.graphics.heprep1.xml;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.freehep.util.io.*;

import hep.graphics.heprep1.*;
import hep.graphics.heprep1.ref.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: XMLHepRepReader.java 8584 2006-08-10 23:06:37Z duns $
 */

public class XMLHepRepReader implements HepRepReader {

    // NOTE: either of input or name is set.
    protected InputStream input;
    protected String name;

    private ZipEntry entry;
    
    private ZipInputStream zip;
    
    protected ZipFile zipFile;
    private Enumeration/*<ZipEntry>*/ zipEntries;
    private int position;

    private XMLSequence sequence;

    /**
     * Create a HepRep Reader for a stream
     * @param input stream to read from
     * @throws IOException if stream cannot be read
     */
    public XMLHepRepReader(InputStream input) throws IOException {
        this.input = input;
        reset();
    }

    /**
     * Create a HepRep Reader for filename
     * @param name filename
     * @throws IOException if file cannot be read
     */
    public XMLHepRepReader(String name) throws IOException {
        this.name = name;
        reset();
    }

    protected HepRep readHepRep(InputStream stream) throws IOException {

        HepRep heprep = new DefaultHepRep();
    	try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XMLReader reader = factory.newSAXParser().getXMLReader();
    //        reader.setFeature("http://xml.org/sax/features/validation", true);
            Handler handler = new Handler(heprep);

            reader.setContentHandler(handler);
            reader.setDTDHandler(handler);
            reader.setErrorHandler(handler);
            reader.setEntityResolver(handler);

            String attributesFile = "AttributeDefaults.xml";
            InputStream attributes = this.getClass().getResourceAsStream(attributesFile);
            if (attributes == null) throw new IOException(getClass()+": Could not find "+attributesFile);
            InputSource ds = new InputSource(attributes);
            reader.parse(ds);

            // now read real document
            InputSource is = new InputSource(stream);
            reader.parse(is);
    	} catch (ParserConfigurationException e) {
            IOException exception = new IOException();
            exception.initCause(e);
            throw exception;
        } catch (SAXParseException e) {
            IOException exception = new IOException();
            exception.initCause(new SAXParseException(e.getMessage(), e.getPublicId(), e.getSystemId(), e.getLineNumber(), e.getColumnNumber()) {
                public String getMessage() {
                    return "line "+getLineNumber()+" column "+getColumnNumber()+": "+super.getMessage();
                }
            });
            throw exception;
        } catch (SAXException e) {
            IOException exception = new IOException();
            exception.initCause(e);
            throw exception;
        } 

        return heprep;
    }

    class Handler extends DefaultHandler {

        private Hashtable labelTable;

        {
            labelTable = new Hashtable(4);
            labelTable.put("NONE", new Integer(HepRepAttValue.SHOW_NONE));
        }


        private HepRep heprep;
        private Stack stack = new Stack();      // of parents
        private DefaultHepRepAttribute parent = null;

        /**
         * Create a HepRep Handler 
         * @param heprep heprep to be populated
         */
        public Handler(HepRep heprep) {
            this.heprep = heprep;
        }

        public void startDocument() throws SAXException {
            stack = new Stack();
            parent = (DefaultHepRepAttribute)heprep;
        }

        public void startElement(String namespace, String tag, String qName, Attributes atts) throws SAXException {
//            System.out.println(namespace+", "+tag+", "+qName);
            if (tag.equals("point")) {
                double x = Double.valueOf(atts.getValue("x")).doubleValue();
                double y = Double.valueOf(atts.getValue("y")).doubleValue();
                double z = Double.valueOf(atts.getValue("z")).doubleValue();
                DefaultHepRepPoint node = new DefaultHepRepPoint(parent, x, y, z);
                stack.push(parent);
                parent = node;

            } else if (tag.equals("primitive")) {
                DefaultHepRepPrimitive node = new DefaultHepRepPrimitive(parent);
                stack.push(parent);
                parent = node;

            } else if (tag.equals("instance")) {
                DefaultHepRepInstance node = new DefaultHepRepInstance(parent);
                stack.push(parent);
                parent = node;

            } else if (tag.equals("type")) {
                String name = atts.getValue("name");
                String version = atts.getValue("version");
                DefaultHepRepType node = new DefaultHepRepType(parent, name, version);
                stack.push(parent);
                parent = node;

            } else if (tag.equals("heprep")) {
                // ignored: heprep already created
            } else if (tag.equals("attvalue")) {
                String name = atts.getValue("name");
                String value = atts.getValue("value");
                String labelString = atts.getValue("showLabel");
                int showLabel = HepRepAttValue.SHOW_NONE;
                if (labelString != null) {
                    StringTokenizer st = new StringTokenizer(labelString, ", ");
                    while (st.hasMoreTokens()) {
                        String label = st.nextToken();
                        Integer number = (Integer)labelTable.get(label);
                        if (number != null) {
                            showLabel += number.intValue();
                        } else {
                            showLabel += Integer.parseInt(label);
                        }
                    }
                }
                parent.addValue(name, value, showLabel);

            } else if (tag.equals("attdef")) {
                String name = atts.getValue("name");
                String desc = atts.getValue("desc");
                String type = atts.getValue("type");
                String extra = atts.getValue("extra");
                parent.addDefinition(name, desc, type, extra);

            } else {
                throw new SAXException("[XMLHepRepReader] Unknown tag: "+tag);
            }
        }

        public void endElement(String namespace, String tag, String qName) throws SAXException {
//            System.out.println("/"+namespace+", "+tag+", "+qName);
            // FIXME: Xerces 1.1.1 seems to report qNames for tags in case the element is non empty
            if (tag.lastIndexOf(':') >= 0) tag = tag.substring(tag.lastIndexOf(':')+1);
            if (tag.equals("point")) {
                parent = (DefaultHepRepAttribute)stack.pop();
            } else if (tag.equals("primitive")) {
                parent = (DefaultHepRepAttribute)stack.pop();
            } else if (tag.equals("instance")) {
                parent = (DefaultHepRepAttribute)stack.pop();
            } else if (tag.equals("type")) {
                parent = (DefaultHepRepAttribute)stack.pop();
            } else if (tag.equals("heprep")) {
                // ignored, toplevel already stored
            } else if (tag.equals("attvalue")) {
            } else if (tag.equals("attdef")) {
            } else {
                throw new SAXException("[XMLHepRepReader] Unknown tag: "+tag);
            }
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
                is = XMLHepRepReader.this.getClass().getResourceAsStream(file);
                if (is == null) {
                    is = XMLHepRepReader.class.getResourceAsStream(file);
                }
            }

            return new InputSource(is);
    	}

    } // class Handler
    
    //
    // HepRepReader interface (from HepRep2).
    //
    public void close() throws IOException {
        if (zip != null) {
            zip.close();
        }
        if (zipFile != null) {
            zipFile.close();
        }
        if (sequence != null) {
            sequence.close();
        }
    }

    public boolean hasSequentialAccess() throws IOException {
        return true;
    }

    public void reset() throws IOException, UnsupportedOperationException {
        if ((input != null) && !(input instanceof ZipInputStream)) {
            sequence = new XMLSequence(new BufferedInputStream(input));
        } else if ((name != null) && !name.toLowerCase().endsWith(".zip")) {
            if (sequence != null) sequence.close();
            if (name.toLowerCase().endsWith(".gz")) {
                sequence = new XMLSequence(new BufferedInputStream(new GZIPInputStream(new FileInputStream(name))));
            } else {
                sequence = new XMLSequence(new BufferedInputStream(new FileInputStream(name)));
            }    
        } else {
            if (input instanceof ZipInputStream) {
                zip = (ZipInputStream)input;
                zip.reset();
            } else if (name != null) {
                if (name.toLowerCase().endsWith(".zip")) {
                    zipFile = new ZipFile(name);
                    zipEntries = zipFile.entries();
                    position = 0;
                } 
            }
        }
    }
    
    public int size() {
        if (zipFile != null) return zipFile.size();
        return -1;
    }

    public int skip(int n) throws UnsupportedOperationException {
        int i = n;
        try {
            while ((i > 0) && hasNext()) {
                next();
                i--;
            }
        } catch (IOException e) {
        }
        return n-i;
    }

    public boolean hasNext() throws IOException, UnsupportedOperationException {
        if (sequence != null) return sequence.hasNext();
        if (zipFile != null) return (size() - position) > 0;
        // best we can do here, since the zip.available() seems unreliable in an XML context
        return true;
    }

    public HepRep next() throws IOException, UnsupportedOperationException, NoSuchElementException {
        if (!hasNext()) throw new UnsupportedOperationException(getClass()+" no more elements");

        if (sequence != null) {
            return readHepRep(sequence.next());
        }

        if (zip != null) {
            entry = zip.getNextEntry();
            InputStream stream = new BufferedInputStream(new NoCloseInputStream(zip));
            return readHepRep(stream);
        } 
        
        if (zipFile != null) {
            entry = (ZipEntry)zipEntries.nextElement();
            position++;
            InputStream stream = zipFile.getInputStream(entry);
            if (entry.getName().toLowerCase().endsWith(".gz")) {
                stream = new GZIPInputStream(stream);
            }
            stream = new BufferedInputStream(stream);
            HepRep heprep = readHepRep(stream);
            
            return heprep;
        }
                
        return null;
    }

    public boolean hasRandomAccess() {
        return zipFile != null;
    }

    public HepRep read(String name) throws IOException, UnsupportedOperationException, NoSuchElementException {
        if (!hasRandomAccess()) throw new UnsupportedOperationException(getClass()+" does not support random access");

        entry = zipFile.getEntry(name);
        if (entry == null) throw new NoSuchElementException(getClass()+" cannot access entry '"+name+"'");

        InputStream stream = new BufferedInputStream(zipFile.getInputStream(entry));
        return readHepRep(stream);
    }

    public String entryName() {
        return (entry != null) ? entry.getName() : null;
    }

    public List/*<String>*/ entryNames() {
        if (zipFile == null) return null;
        
        List list = new AbstractSequentialList() {
            public int size() {
                return XMLHepRepReader.this.size();
            }
            
            public ListIterator listIterator(int index) {
                final int startIndex = index;
                
                return new ListIterator() {
                    private int position;
                    private Enumeration entries;
                    private ZipEntry entry;
                
                    {
                        entries = zipFile.entries();
                        position = startIndex;
                        for (int i=0; i<=position; i++) {
                            entry = entries.hasMoreElements() ? (ZipEntry)entries.nextElement() : null;
                            if (entry == null) break;
                        }
                        if (entry == null) position = size();
                    }
                                        
                    public void add(Object o) { 
                        throw new UnsupportedOperationException();
                    }
                    
                    public boolean hasNext() {
                        return entry != null;
                    }
                    
                    public boolean hasPrevious() {
                        return false;
                    }
                    
                    public Object next() {
                        if (entry == null) throw new NoSuchElementException();
                        return entry.getName();
                    }
                    
                    public int nextIndex() {
                        return position;
                    }
                    
                    public Object previous() {
                        throw new NoSuchElementException();
                    }
                    
                    public int previousIndex() {
                        return position - 1;
                    }
                    
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    public void set(Object o) {
                        throw new UnsupportedOperationException();
                    }
                }; // ListIterator
            }
        }; // AbstractSequentialList
        return list;
    }
    
}
