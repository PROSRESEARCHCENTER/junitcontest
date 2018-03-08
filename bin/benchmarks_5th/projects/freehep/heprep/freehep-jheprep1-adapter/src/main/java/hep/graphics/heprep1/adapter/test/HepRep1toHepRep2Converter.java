// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter.test;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepWriter;
import hep.graphics.heprep1.adapter.HepRepAdapterFactory;
import hep.graphics.heprep1.adapter.NumericalComparator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRep1toHepRep2Converter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRep1toHepRep2Converter {


    /**
     * Convert heprep1 source into heprep2 destination
     * @param source 
     * @param dest
     * @throws Exception
     */
    public HepRep1toHepRep2Converter(String source, String dest) throws Exception {

        hep.graphics.heprep1.xml.XMLHepRepReader in = new hep.graphics.heprep1.xml.XMLHepRepReader(new FileInputStream(source));
        hep.graphics.heprep1.HepRep heprep1 = in.next();
        in.close();

        HepRepAdapterFactory factory = HepRepAdapterFactory.getFactory();        
        HepRep heprep = factory.createHepRep();
        List layers = new ArrayList();
        
        // FIXME, this should go somewhere else...
        // read geometry
/*
        WGF wgf = new WGF("/babargeometry");
        wgf.parse("babar.xml.gz");
        HepRep geometry = wgf.getHepRep();

        heprep.addTypeTree(geometry.getTypeTree("GeometryTypes", "1.0"));
        heprep.addInstanceTree(geometry.getInstanceTreeTop("BaBarGeometry", "1.0"));
        layers.addAll(geometry.getLayerOrder());
*/        
        // read event
        HepRep event = factory.createHepRep(heprep1);
                              
        // add to new heprep
        heprep.addTypeTree(event.getTypeTree("Types", "1.0"));        
        
        HepRepInstanceTree instanceTree = event.getInstanceTreeTop("Instances", "1.0");
        heprep.addInstanceTree(instanceTree);
        layers.addAll(event.getLayerOrder());

//        instanceTree.addInstanceTree(factory.createHepRepTreeID("BaBarGeometry", "1.0"));
        
        // merge layers
        Collections.sort(layers, new NumericalComparator());
        for (Iterator i=layers.iterator(); i.hasNext(); ) {
            heprep.addLayer((String)i.next());
        }
        
        HepRepWriter writer = factory.createHepRepWriter(new FileOutputStream(dest), false, false);
        writer.write(heprep, "Event");
        writer.close();
    }

    /**
     * Command Line Utility to convert HepRep 1 into HepRep 2 files.
     * @param args see usage
     * @throws Exception for any exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: HepRep1toHepRep2Converter sourcefile destfile");
            System.exit(1);
        }
        
        try {
            new HepRep1toHepRep2Converter(args[0], args[1]);
        } catch (SAXParseException e) {
            System.out.println(e+" in "+args[0]+" at line: "+e.getLineNumber()+", col: "+e.getColumnNumber());
        } catch (SAXException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}