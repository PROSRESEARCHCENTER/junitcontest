// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.test;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.ref.DefaultHepRep;
import hep.graphics.heprep.util.HepRepIO;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: Count.java 8584 2006-08-10 23:06:37Z duns $
 */

public class Count {

    /**
     * @param iname
     * @throws Exception
     */
    public void run(String iname) throws Exception {
        long t0, t1, t2;
        t0 = System.currentTimeMillis();

        HepRep heprep = HepRepIO.readHepRep(iname);
        t1 = System.currentTimeMillis();
        System.out.println("Read file in "+(t1-t0)+" ms.");

        ((DefaultHepRep)heprep).display();
        t2 = System.currentTimeMillis();
        System.out.println("Counted heprep in "+(t2-t1)+" ms.");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: Count filename");
            System.exit(1);
        }

        try {
            new Count().run(args[0]);
        } catch (Exception e) {
	        System.out.println(e);
	        e.printStackTrace();
        }
    }


}
