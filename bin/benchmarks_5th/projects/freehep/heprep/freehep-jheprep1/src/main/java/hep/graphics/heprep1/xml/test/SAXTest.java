// Copyright 2000-2004, FreeHEP.
package hep.graphics.heprep1.xml.test;

import hep.graphics.heprep1.HepRep;
import hep.graphics.heprep1.xml.XMLHepRepReader;
import hep.graphics.heprep1.xml.XMLHepRepWriter;

import java.io.FileInputStream;
import java.io.FileWriter;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: SAXTest.java 8584 2006-08-10 23:06:37Z duns $
 */

public class SAXTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: SAXTest filename.xml");
            System.exit(1);
        }
        
        try {
            FileInputStream fr = new FileInputStream(args[0]);
            
            XMLHepRepReader reader = new XMLHepRepReader(fr);
            
            HepRep hepRep = reader.next();

            reader.close();

            FileWriter fw = new FileWriter("SampleEvent.out.xml");
            
            XMLHepRepWriter writer = new XMLHepRepWriter(fw);
            writer.write(hepRep);
            
            writer.close();
        
        } catch (Exception e) {
	        System.out.println(e);
	        e.printStackTrace();
        }
    }    
}
