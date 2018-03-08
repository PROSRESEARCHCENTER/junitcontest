// Copyright 2000-2003, FreeHEP.

package hep.graphics.heprep.ref;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.freehep.util.io.*;

import hep.graphics.heprep.*;

/**
 * This abstract HepRep Reader handles ZipInputStreams, ZipFiles, reads the "heprep.properties"
 * file, allows for properties to be quesried and handles files to be skipped while iterating.
 * It assumes the concrete subclass has Sequential access. ZipFiles will provide Random access
 * as well.
 * The reset method should implement the opening of the file(s), or call super.reset(). 
 * Reset should be called from the constructor to open the initial file.
 *
 * @author M.Donszelmann
 *
 * @version $Id: AbstractHepRepReader.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class AbstractHepRepReader implements HepRepReader {

    // NOTE: either of input or name is set.
    protected InputStream input;
    protected String name;
    
    private ZipEntry entry;
    
    private ZipInputStream zip;
    
    protected ZipFile zipFile;
    private Enumeration/*<ZipEntry>*/ zipEntries;
    private int position;
    
    private Properties properties;
    private Set/*<String>*/ skip;

    private AbstractHepRepReader() {
        super();
        properties = new Properties();
        skip = new HashSet();
    }

    protected AbstractHepRepReader(InputStream in) throws IOException {
        this();
        input = in;
    }

    protected AbstractHepRepReader(String fileName) throws IOException {
        this();
        name = fileName;
    }

    public String getProperty(String key, String defaultValue) throws IOException {
        return properties.getProperty(key, defaultValue);
    }

    public void close() throws IOException {
        if (zip != null) {
            zip.close();
        }
        if (zipFile != null) {
            zipFile.close();
        }
    }

    public boolean hasSequentialAccess() throws IOException {
        return true;
    }

    public void reset() throws IOException, UnsupportedOperationException {
        if (input instanceof ZipInputStream) {
            zip = (ZipInputStream)input;
            zip.reset();
        } else if (name != null) {
            if (name.toLowerCase().endsWith(".zip")) {
                zipFile = new ZipFile(name);
                zipEntries = zipFile.entries();
                position = 0;
            
                ZipEntry propEntry = zipFile.getEntry("heprep.properties");
                if (propEntry != null) {
                    skip.add("heprep.properties");
                    InputStream propStream = zipFile.getInputStream(propEntry);
                    properties.load(propStream);
                    propStream.close();
                    String ignoreList = properties.getProperty("RecordLoop.ignore",null);
                    if (ignoreList != null) skip.addAll(Arrays.asList(ignoreList.split(":")));
                }
            } 
        }
    }
    

    public int size() {
        if (zipFile != null) return zipFile.size() - skip.size();
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
        if (zipFile != null) return (size() - position) > 0;
        // best we can do here, since the zip.available() seems unreliable in an XML context
        return true;
    }

    public HepRep next() throws IOException, UnsupportedOperationException, NoSuchElementException {
        if (!hasNext()) throw new UnsupportedOperationException(getClass()+" no more elements");

        if (zip != null) {
            entry = zip.getNextEntry();
            while (skip.contains(entry.getName())) {
                entry = zip.getNextEntry();
            }
            InputStream stream = new BufferedInputStream(new NoCloseInputStream(zip));
            return readHepRep(stream, entry.getName().endsWith(".bheprep"));
        } 
        
        if (zipFile != null) {
            entry = (ZipEntry)zipEntries.nextElement();
            while (skip.contains(entry.getName())) {
                entry = (ZipEntry)zipEntries.nextElement();
            }
            position++;
            InputStream stream = zipFile.getInputStream(entry);
            if (entry.getName().toLowerCase().endsWith(".gz")) {
                stream = new GZIPInputStream(stream);
            }
            stream = new BufferedInputStream(stream);
            HepRep heprep = readHepRep(stream, entry.getName().endsWith(".bheprep"));
            
            // Merge in all related instance trees
            // FIXME this merging should be available in a general fashion...
            // FIXME, this does not work yet.
            for (Iterator i=heprep.getInstanceTreeList().iterator(); i.hasNext(); ) {
                HepRepInstanceTree tree = (HepRepInstanceTree)i.next();
//                System.out.println(tree);
                for (Iterator j=tree.getInstanceTreeList().iterator(); j.hasNext(); ) {
                    HepRepTreeID id = (HepRepTreeID)j.next();
                    String[] name = id.getName().split("#",2);
                    ZipEntry zipEntry = zipFile.getEntry(name[0]);
                    if (zipEntry != null) {
                        InputStream zipStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                        HepRep h = readHepRep(zipStream, name[0].endsWith(".bheprep"));
            for (Iterator k=h.getInstanceTreeList().iterator(); k.hasNext(); ) {
                k.next();
            }
                    }
                }
//                System.out.println("==");
            }
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
        return readHepRep(stream, name.endsWith(".bheprep"));
    }

    public String entryName() {
        return (entry != null) ? entry.getName() : null;
    }

    public List/*<String>*/ entryNames() {
        if (zipFile == null) return null;
        
        List list = new AbstractSequentialList() {
            public int size() {
                return AbstractHepRepReader.this.size();
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
                            while ((entry != null) && skip.contains(entry.getName())) {
                                entry = entries.hasMoreElements() ? (ZipEntry)entries.nextElement() : null;
                            }
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

    protected abstract HepRep readHepRep(InputStream input, boolean binary) throws IOException;
    
    /**
     * Reads the HepRep
     * @param input stream to read from
     * @return HepRep read
     * @throws IOException if stream cannot be read 
     * @deprecated use readHepRep(InputStream, boolean) instead.
     */
    protected HepRep readHepRep(InputStream input) throws IOException {
        return readHepRep(input, false);
    }
}
