// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.test;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.util.HepRepIO;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: CompareTest.java 8584 2006-08-10 23:06:37Z duns $
 */

public class CompareTest {

    /**
     * @param iname1
     * @param iname2
     * @throws Exception
     */
    public void run(String iname1, String iname2) throws Exception {
        long t0, t1, t2, t3;
        t0 = System.currentTimeMillis();

        HepRep heprep1 = HepRepIO.readHepRep(iname1);
        t1 = System.currentTimeMillis();
        System.out.println("Read file "+iname1+" in "+(t1-t0)+" ms.");

        HepRep heprep2 = HepRepIO.readHepRep(iname2);
        t2 = System.currentTimeMillis();
        System.out.println("Read file "+iname2+" in "+(t1-t0)+" ms.");

        if (heprep1.equals(heprep2)) {
            System.out.println("HepReps are equal");
        } else {
            System.out.println("HepReps are different");
        }
        t3 = System.currentTimeMillis();
        System.out.println("Comparing files used "+(t3-t2)+" ms.");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: CompareTest inputfilename1 inputfilename2");
            System.exit(1);
        }

        try {
            new CompareTest().run(args[0], args[1]);
        } catch (Exception e) {
	        System.out.println(e);
	        e.printStackTrace();
        }
    }
}
