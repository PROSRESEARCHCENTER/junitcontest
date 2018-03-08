// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.xml;

import hep.graphics.heprep.HepRepReader;
import hep.graphics.heprep.HepRepWriter;
import hep.graphics.heprep.ref.DefaultHepRepFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * XMLHepRepFactory uses the reference implementation but uses XML for Readers and Writers.
 * 
 * @author Mark Donszelmann
 * @version $Id: XMLHepRepFactory.java 8584 2006-08-10 23:06:37Z duns $
 */

public class XMLHepRepFactory extends DefaultHepRepFactory {

    /**
     * Create an XML HepRep Factory
     */
    public XMLHepRepFactory() {
    }

    public HepRepReader createHepRepReader(InputStream in) throws IOException {
        return new XMLHepRepReader(in);
    }

    public HepRepReader createHepRepReader(String inputFileName) throws IOException {
        return new XMLHepRepReader(inputFileName);
    }

    public HepRepWriter createHepRepWriter(OutputStream out, boolean randomAccess, boolean compress) throws IOException {
        return new XMLHepRepWriter(out, randomAccess, compress);
    }
}
