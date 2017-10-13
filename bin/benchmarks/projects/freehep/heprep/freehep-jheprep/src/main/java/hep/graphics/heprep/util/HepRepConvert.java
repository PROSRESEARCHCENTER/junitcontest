// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.util;

import hep.graphics.heprep.HepRep;

/**
 * Converter to convert HepRep xml files into compressed format
 * 
 * @author M.Donszelmann
 * @version $Id: HepRepConvert.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepConvert {

    /**
     * Main method
     * 
     * @param args see usage
     * @throws Exception see usage
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: HepRepConvert inputfile outputfile");
            System.out.println("    where input and outputfile may have extensions:");
            System.out.println("        .xml:    HepRep XML format");
            System.out.println("        .xml.gz: Compressed HepRep XML format");
            System.out.println("        .ser:    Serialized HepRep format");
            System.out.println("        .ser.gz: Compressed Serialized HepRep format");
            System.exit(1);
        }

        long t0;
        HepRep hepRep = null;

        t0 = System.currentTimeMillis();
        hepRep = HepRepIO.readHepRep(args[0]);
        System.out.println("Read "+args[0]+" in "+(System.currentTimeMillis()-t0)+" ms.");

        t0 = System.currentTimeMillis();
        HepRepIO.writeHepRep(hepRep, args[1]);
        System.out.println("Written "+args[1]+" in "+(System.currentTimeMillis()-t0)+" ms.");
    }
}
