// Copyright 2000-2003, FreeHEP.

package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRep;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

/**
 * Read in java serialized IO
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepReader.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepReader extends AbstractHepRepReader {

    protected DefaultHepRepReader(InputStream in) throws IOException {
        super(in);
        reset();        
    }

    protected DefaultHepRepReader(String fileName) throws IOException {
        super(fileName);
        reset();
    }

    public void reset() throws IOException, UnsupportedOperationException {
        if (name != null) {
            if (name.toLowerCase().endsWith(".gz")) {
                input = new GZIPInputStream(new FileInputStream(name));
            } else {
                input = new FileInputStream(name);
            }
        } else {
            super.reset();
        }
    }

    public HepRep readHepRep(InputStream input, boolean binary) throws IOException {
        try {
            ObjectInputStream oin = new ObjectInputStream(input);
            return (HepRep)oin.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(getClass()+" Class not found: "+e.getMessage());
        } catch (NoClassDefFoundError e) {
            throw new IOException(getClass()+" ClassDef not found: "+e.getMessage());
        }
    }
}

