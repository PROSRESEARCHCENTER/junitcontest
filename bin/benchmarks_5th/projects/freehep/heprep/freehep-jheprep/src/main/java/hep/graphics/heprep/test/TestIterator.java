// Copyright 2000-2004, FreeHEP.
package hep.graphics.heprep.test;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAttributeListener;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepIterator;
import hep.graphics.heprep.util.HepRepIO;
import hep.graphics.heprep.util.HepRepUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests iteration of a HepRep file. Prints out the changes to all subscribed
 * attributes as they should be set in the target system.
 *
 * @author M.Donszelmann
 *
 * @version $Id: TestIterator.java 8584 2006-08-10 23:06:37Z duns $
 */

public class TestIterator implements HepRepAttributeListener {

    /**
     * @param args
     * @throws Throwable
     */
    public void run(String[] args) throws Throwable {
        String fname = args[0];

        HepRep hepRep;
        // read File
        hepRep = HepRepIO.readHepRep(fname);
        
        List layers;
        if (args.length == 1) {
            layers = null;
        } else {
            layers = new ArrayList();
            for (int j=1; j<args.length; j++) {
                layers.add(args[j]);
            }
        }
        HepRepIterator it = HepRepUtil.getInstances(hepRep.getInstanceTreeList(), layers, null, false);
        
        it.addHepRepAttributeListener("Layer", this);
        it.addHepRepAttributeListener("DrawAs", this);
        it.addHepRepAttributeListener("Color", this);
            
        while (it.hasNext()) {
            HepRepInstance instance = (HepRepInstance)it.next();
            System.out.print("Instance of ");
            System.out.print(instance.getType());
            System.out.print(" as ");
            System.out.println(instance.getAttValue("drawas"));
        }
    }

    public void setAttribute(HepRepInstance instance, String key, String value, String lowerCaseValue, int showLabel) {
        if (key.equals("layer")) System.out.println("============================================");
        System.out.println(key+"="+value+":"+showLabel);
    }

    public void setAttribute(HepRepInstance instance, String key, Color value, int showLabel) {
        System.out.println(key+"="+value+":"+showLabel);
    }

    public void setAttribute(HepRepInstance instance, String key, long value, int showLabel) {
        System.out.println(key+"="+value+":"+showLabel);
    }

    public void setAttribute(HepRepInstance instance, String key, int value, int showLabel) {
        System.out.println(key+"="+value+":"+showLabel);
    }

    public void setAttribute(HepRepInstance instance, String key, double value, int showLabel) {
        System.out.println(key+"="+value+":"+showLabel);
    }

    public void setAttribute(HepRepInstance instance, String key, boolean value, int showLabel) {
        System.out.println(key+"="+value+":"+showLabel);
    }

    public void removeAttribute(HepRepInstance instance, String key) {
        System.out.println("*"+key);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: TestIterator filename [layernames]");
            System.exit(1);
        }

        try {
            new TestIterator().run(args);
        } catch (Throwable e) {
	        System.out.println(e);
	        e.printStackTrace();
        }
    }


}
