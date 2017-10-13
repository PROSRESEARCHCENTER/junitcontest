// Copyright 2000-2004, FreeHEP.
package hep.graphics.heprep.test;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAttributeListener;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepIterator;
import hep.graphics.heprep.util.HepRepIO;
import hep.graphics.heprep.util.HepRepUtil;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.freehep.util.io.NoCloseInputStream;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: BenchmarkIterator.java 8584 2006-08-10 23:06:37Z duns $
 */

public class BenchmarkIterator implements HepRepAttributeListener {

    private int runIterator(HepRep hepRep, List layers) throws Exception {
        int count = 0;
        HepRepIterator it = HepRepUtil.getInstances(hepRep.getInstanceTreeList(), layers, null, false);

        it.addHepRepAttributeListener("Layer", this);
        it.addHepRepAttributeListener("DrawAs", this);

        it.addHepRepAttributeListener("Color", this);
        it.addHepRepAttributeListener("LineWidth", this);

        it.addHepRepAttributeListener("FrameColor", this);
        it.addHepRepAttributeListener("FrameWidth", this);

        it.addHepRepAttributeListener("FillColor", this);
        it.addHepRepAttributeListener("Fill", this);

        it.addHepRepAttributeListener("MarkSymbol", this);
        it.addHepRepAttributeListener("MarkSize", this);

        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

    /**
     * @param fname
     * @param is
     * @param args
     * @throws Exception
     */
    public void run(String fname, InputStream is, String[] args) throws Exception {
        long t0;
        HepRep hepRep;
        // read File
        t0 = System.currentTimeMillis();
        hepRep = HepRepIO.readHepRep(fname, is);
        System.out.println("Read "+fname+" in "+(System.currentTimeMillis()-t0)+" ms.");

        List layers;
        if (args.length == 1) {
            layers = hepRep.getLayerOrder();
        } else {
            layers = new ArrayList();
            for (int i=1; i<args.length; i++) {
                layers.add(args[i]);
            }
        }
        System.out.print("Sleeping 2 seconds... ");
        Thread.sleep(2000);
        System.out.println("done");

        t0 = System.currentTimeMillis();
        int count;
        count = runIterator(hepRep, layers);
        System.out.println("Iterating "+count+" Instances, first time: "+((double)System.currentTimeMillis()-t0)+" ms/iteration.");
        
        System.out.print("Sleeping 2 seconds... ");
        Thread.sleep(2000);
        System.out.println("done");

        int n = 100;
        count = 0;
        t0 = System.currentTimeMillis();
        for (int j=0; j<n; j++) {
            count += runIterator(hepRep, layers);
        }
        System.out.println("Iterating "+(count/n)+" Instances, "+n+" times: "+((double)(System.currentTimeMillis()-t0)/n)+" ms/iteration.");
    }

    public void setAttribute(HepRepInstance instance, String key, String value, String lowerCaseValue, int showLabel) {
    }

    public void setAttribute(HepRepInstance instance, String key, Color value, int showLabel) {
    }

    public void setAttribute(HepRepInstance instance, String key, long value, int showLabel) {
    }

    public void setAttribute(HepRepInstance instance, String key, int value, int showLabel) {
    }

    public void setAttribute(HepRepInstance instance, String key, double value, int showLabel) {
    }

    public void setAttribute(HepRepInstance instance, String key, boolean value, int showLabel) {
    }

    public void removeAttribute(HepRepInstance instance, String key) {
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: BenchmarkIterator filename [layers]");
            System.exit(1);
        }

        try {
            BenchmarkIterator bi = new BenchmarkIterator();
            if (args[0].endsWith(".zip")) {
                System.out.println("Zip file");
                ZipInputStream zip = new ZipInputStream(new FileInputStream(args[0]));
                ZipEntry entry = zip.getNextEntry();
                while (entry != null) {
                    bi.run(entry.getName(), new NoCloseInputStream(zip), args);
                    entry = zip.getNextEntry();
                }
            } else {
                bi.run(args[0], null, args);
            }
        } catch (Exception e) {
	        System.out.println(e);
	        e.printStackTrace();
        }
    }


}
