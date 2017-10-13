package hep.graphics.heprep.util;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepReader;
import hep.graphics.heprep.HepRepWriter;
import hep.graphics.heprep.xml.XMLHepRepFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepIO.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepIO {

    // Static class, not to be instantiated
    private HepRepIO() {
    }

    /**
     * reads HepRep from a file, xml or ser
     * If the stream was opened here, it is also closed.
     * @param name name of heprep file
     * @param is input stream to read heprep from, or null
     * @return HepRep read
     * @throws IOException in case of an IO error
     */
    public static HepRep readHepRep(String name, InputStream is) throws IOException {
        boolean close = false;
        if (is == null) {
            is = new FileInputStream(name);
            close = true;
        }
        Object input = getHepRepInput(name, is);
        HepRep heprep = readHepRep(input);
        if (close) is.close();
        return heprep;
    }

    /**
     * REMOVE
     * @param name name of the stream
     * @param is stream of an heprep file
     * @return either a Reader (xml), an InputStream (ser) or a ZipInputStream (zip)
     * @throws IOException in case of an I/O Error
     */
    public static Object getHepRepInput(String name, InputStream is) throws IOException {
        is = new BufferedInputStream(is, 1024000);

        if (name.toLowerCase().endsWith(".gz")) {
            is = new GZIPInputStream(is);
            name = name.substring(0, name.length()-3);
        }

        if (name.toLowerCase().endsWith(".ser")) {
            return new ObjectInputStream(is);
        } else if (name.toLowerCase().endsWith(".zip")) {
            return new ZipInputStream(is);
        }

        return is;
    }

    /**
     * Read HepRep from any type of stream (ObjectInputStream, InputStream, String)
     * @param input Any type of input stream
     * @return HepRep read
     * @throws IOException in case of an I/O error
     */
    public static HepRep readHepRep(Object input) throws IOException {
        if (input instanceof ObjectInputStream) {
            return readHepRep((ObjectInputStream)input);
        } else if (input instanceof InputStream) {
            return readHepRep((InputStream)input);
        } else if (input instanceof String) {
            return readHepRep((String)input, null);
        }
        throw new RuntimeException("HepRepUtil.readHepRep, unrecognized input class: "+input+", accept only String, InputStream or ObjectInputStream");
    }

    /**
     * Read HepRep
     * @param objectInputStream stream to read from
     * @return HepRep read
     * @throws IOException in case of an I/O error
     */
    public static HepRep readHepRep(ObjectInputStream objectInputStream) throws IOException {
        try {
            return (HepRep)objectInputStream.readObject();
        } catch (Exception e) {
            IOException exception = new IOException();
            exception.initCause(e);
            throw exception;
        }
    }

    /**
     * Read HepRep
     * @param stream stream to read from
     * @return HepRep read
     * @throws IOException in case of an I/O error
     */
    public static HepRep readHepRep(InputStream stream) throws IOException {
        HepRepReader reader = new XMLHepRepFactory().createHepRepReader(stream);
        return reader.next();
    }

    /**
     * Write HepRep to file
     * @param heprep heprep
     * @param name name of the file
     * @throws IOException in case of an I/O error
     */
    public static void writeHepRep(HepRep heprep, String name) throws IOException {
        writeHepRep(heprep, name, null);
    }

    /**
     * Writes a HepRep to a file, xml, ser or zip (use ! point to specify entry name).
     * @param heprep heprep
     * @param name name of the file
     * @param os stream to write to, or null
     * @return the open output stream, or null
     * @throws IOException in case of an I/O error
     */
    public static OutputStream writeHepRep(HepRep heprep, String name, OutputStream os) throws IOException {

        String entryName = null;
        boolean random = false;
        boolean compress = false;
        
        // Zip file?
        int pos = name.lastIndexOf('!');
        if (pos > 0) {
            String zipName = name.substring(0, pos);
            if (zipName.toLowerCase().endsWith(".zip")) {
                random = true;
                compress = true;
                entryName = name.substring(pos+1);
                if (entryName.equals("")) {
                    entryName = "HepRep";
                } else {
                    name = zipName;
                }
            }
        }

        // no Stream
        boolean close = false;
        if (os == null) {
            boolean append = (new File(name)).exists();
            os = new FileOutputStream(name, append);
            close = true;
        }

        // Zip entry ?
        if (random) {
            name = entryName;
        } 

        // Buffering ?
        os = new BufferedOutputStream(os, 1024000);

        // GZipped file ?
        if (name.toLowerCase().endsWith(".gz")) {
            compress = true;
            name = name.substring(0, name.length()-3);
        }

        // Encoding ?
        if (name.toLowerCase().endsWith(".xml") || name.toLowerCase().endsWith(".heprep")) {
            HepRepWriter writer = new XMLHepRepFactory().createHepRepWriter(os, random, compress);
//            writer.setBitEncoding(true);
            writer.write(heprep, name);
            if (close) writer.close();
        } else if (name.toLowerCase().endsWith(".ser")) {
            ObjectOutputStream dos = new ObjectOutputStream(os);
            dos.writeObject(heprep);
            if (close) dos.close();
        } else if (name.toLowerCase().endsWith(".zip")) {
            throw new RuntimeException("HepRepUtil.writeHepRep, zip extension needs entryName specified by '!entryName'");
        } else {
            throw new RuntimeException("HepRepUtil.writeHepRep, unrecognized extension: "+name);
        }

        return (close) ? null : os;
    }
}