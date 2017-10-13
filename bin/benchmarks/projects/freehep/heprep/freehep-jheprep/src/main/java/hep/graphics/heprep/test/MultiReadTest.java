// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.test;

import hep.graphics.heprep.HepRepFactory;
import hep.graphics.heprep.HepRepReader;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * Reads multiple hepreps from one xml file.
 *
 * @author M.Donszelmann
 *
 * @version $Id: MultiReadTest.java 8584 2006-08-10 23:06:37Z duns $
 */

public class MultiReadTest {

    private int read(HepRepFactory factory, String filename) throws IOException {
        InputStream fis = new FileInputStream(filename);
        if (filename.endsWith(".gz")) fis = new GZIPInputStream(fis);
        if (filename.endsWith(".zip")) fis = new ZipInputStream(fis);
        HepRepReader reader = factory.createHepRepReader(fis);
        System.out.println("Sequential: "+reader.hasSequentialAccess());
        System.out.println("Random: "+reader.hasRandomAccess());
        int i=0;
        try {
            while (reader.hasNext()) {
                reader.next();
                i++;
                System.out.print(".");
            }
        } catch (EOFException eof) {
        }
        System.out.println();
        reader.close();
        fis.close();
        return i;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: MultiReadTest filename");
            System.exit(1);
        }

        long start = System.currentTimeMillis();
        HepRepFactory factory = HepRepFactory.create();
        int n = new MultiReadTest().read(factory, args[0]);
        System.out.println("Read "+n+" events in "+(System.currentTimeMillis()-start)+" ms.");
    }
}
