// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.test;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.util.HepRepIO;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: CopyTest.java 8584 2006-08-10 23:06:37Z duns $
 */

public class CopyTest {

    /**
     * @param iname
     * @param oname
     * @throws Exception
     */
    public void run(String iname, String oname) throws Exception {
        long t0, t1, t2, t3, t4, t5;
        t0 = System.currentTimeMillis();

        HepRep heprep = HepRepIO.readHepRep(iname);
        t1 = System.currentTimeMillis();
        System.out.println("Read file in "+(t1-t0)+" ms.");

        HepRep copy = heprep.copy();
        t2 = System.currentTimeMillis();
        System.out.println("Copied heprep in "+(t2-t1)+" ms.");
        
        if (copy.equals(heprep)) {
            System.out.println("Copy is equal to Original");
        } else {
            System.out.println("Copy is DIFFERENT than Original");
        }
        t3 = System.currentTimeMillis();
        System.out.println("Comparing files used "+(t3-t2)+" ms.");

        HepRepIO.writeHepRep(heprep, "orig."+oname);
        t4 = System.currentTimeMillis();
        System.out.println("Written orig file in "+(t4-t3)+" ms.");

        HepRepIO.writeHepRep(copy, oname);
        t5 = System.currentTimeMillis();
        System.out.println("Written file in "+(t5-t4)+" ms.");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: CopyTest inputfilename outputfilename");
            System.exit(1);
        }

        try {
            new CopyTest().run(args[0], args[1]);
        } catch (Exception e) {
	        System.out.println(e);
	        e.printStackTrace();
        }
    }


}
