// Copyright 2000, FreeHEP.
package hep.graphics.heprep.test;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAttribute;
import hep.graphics.heprep.HepRepFactory;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.HepRepWriter;
import hep.graphics.heprep.ref.DefaultHepRepFactory;
import hep.graphics.heprep.xml.XMLHepRepFactory;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author duns
 * @version $Id: CreateTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class CreateTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {

        System.out.println("Finding Factory...");
    	HepRepFactory factory = DefaultHepRepFactory.create();

        System.out.println("Creating HepRep...");
    	HepRep root = factory.createHepRep();

        System.out.println("Creating Layer...");
    	String layer = "Detector";
    	root.addLayer(layer);

        System.out.println("Creating HepRepTreeID...");
        HepRepTreeID treeID = factory.createHepRepTreeID("CylinderType", "1.0");

    //    <heprep:typetree name="CylinderType" version="1.0">
        System.out.println("Creating HepRepTypeTree...");
        HepRepTypeTree typeTree = factory.createHepRepTypeTree(treeID);
        root.addTypeTree(typeTree);

        System.out.println("Creating HepRepType...");
        HepRepType type = factory.createHepRepType(typeTree, "Cylinder");
    //    <heprep:attvalue name="banner" value="true" />
    //    <heprep:attvalue name="framed" value="true" />
    //    <heprep:attvalue name="drawAs" value="Cylinder" />
    //    <heprep:attvalue name="radius1" value="2" />
    //    <heprep:attvalue name="radius2" value="2" />
        type.addAttValue("layer",  layer);
        type.addAttValue("banner", "true");
        type.addAttValue("framed", "true");
        type.addAttValue("drawAs", "Cylinder");
        type.addAttValue("radius", "2", HepRepAttribute.SHOW_VALUE + HepRepAttribute.SHOW_NAME + 0x0400);

        System.out.println("Creating HepRepInstanceTree...");
    //    <heprep:instancetree name="TestCylinder" version="CPlusPlus Generated" typename="CylinderType" typeversion="1.0">
        HepRepInstanceTree instanceTree = factory.createHepRepInstanceTree("TestCylinder", "CPlusPlus Generated", typeTree);
        root.addInstanceTree(instanceTree);

        System.out.println("Creating HepRepInstance...");
    //    <heprep:instance type="Cylinder">
        HepRepInstance instance1 = factory.createHepRepInstance(instanceTree, type);

    //    <heprep:attvalue name="color" value="cyan" />
    //    <heprep:attvalue name="label" value="x-axis" showLabel="VALUE" />
        instance1.addAttValue("color", Color.CYAN);
        instance1.addAttValue("label", "x-axis", HepRepAttribute.SHOW_VALUE);

    //    <heprep:point x="0.0" y="0.0" z="0.0" />
    //    <heprep:point x="4.0" y="0.0" z="0.0" />
        factory.createHepRepPoint(instance1, 0, 0, 0);
        factory.createHepRepPoint(instance1, 4, 0, 0);

    //    <heprep:instance type="Cylinder">
        HepRepInstance instance2 = factory.createHepRepInstance(instanceTree, type);

    //    <heprep:attvalue name="color" value="orange" />
    //    <heprep:attvalue name="label" value="y-axis" showLabel="VALUE" />
        instance2.addAttValue("color", Color.ORANGE, 0);
        instance2.addAttValue("label", "y-axis", HepRepAttribute.SHOW_VALUE);

    //    <heprep:point x="0.0" y="0.0" z="0.0" />
    //    <heprep:point x="0.0" y="4.0" z="0.0" />
        factory.createHepRepPoint(instance2, 0, 0, 0);
        factory.createHepRepPoint(instance2, 0, 4, 0);

    //    <heprep:instance type="Cylinder">
        HepRepInstance instance3 = factory.createHepRepInstance(instanceTree, type);

    //    <heprep:attvalue name="color" value="green" />
    //    <heprep:attvalue name="label" value="z-axis" showLabel="VALUE" />
        instance3.addAttValue("color", Color.GREEN, 0);
        instance3.addAttValue("label", "z-axis", HepRepAttribute.SHOW_VALUE);

    //    <heprep:point x="0.0" y="0.0" z="0.0" />
    //    <heprep:point x="0.0" y="0.0" z="4.0" />
        factory.createHepRepPoint(instance3, 0, 0, 0);
        factory.createHepRepPoint(instance3, 0, 0, 4);

    //    <heprep:instance type="Cylinder">
        HepRepInstance instance4 = factory.createHepRepInstance(instanceTree, type);

    //    <heprep:point x="10.0" y="10.0" z="10.0">
    //      <heprep:attvalue name="Phi" value="0.2" showLabel="false" />
    //    </heprep:point>
    //    <heprep:point x="12.3" y="12.3" z="12.3">
    //      <heprep:attvalue name="Phi" value="0.3" showLabel="false" />
    //    </heprep:point>
        HepRepPoint p1 = factory.createHepRepPoint(instance4, 10, 10, 10);
        p1.addAttValue("Phi", "0.2", 0);

        HepRepPoint p2 = factory.createHepRepPoint(instance4, 12.3, 12.3, 12.3);
        p2.addAttValue("Phi", "0.3", 0);

        System.out.println("Saving HepRep as ser ...");
        try {
            OutputStream out = new FileOutputStream("HepRepTest.ser");
            HepRepWriter writer = factory.createHepRepWriter(out, false, false);
            writer.write(root, null);
            writer.close();
        } catch (IOException ioe) {
            System.err.println("Could not write ser file "+ioe);
        }

        System.out.println("Saving HepRep as ser.gz ...");
        try {
            OutputStream out = new FileOutputStream("HepRepTest.ser.gz");
            HepRepWriter writer = factory.createHepRepWriter(out, false, true);
            writer.write(root, null);
            writer.close();
        } catch (IOException ioe) {
            System.err.println("Could not write ser.gz file "+ioe);
        }

        System.out.println("Saving HepRep as xml ...");
        try {
            OutputStream out = new FileOutputStream("HepRepTest.xml");
            HepRepWriter writer = new XMLHepRepFactory().createHepRepWriter(out, false, false);
            writer.write(root, null);
            writer.close();
        } catch (IOException ioe) {
            System.err.println("Could not write xml file "+ioe);
        }

        System.out.println("Saving HepRep as xml.gz ...");
        try {
            OutputStream out = new FileOutputStream("HepRepTest.xml.gz");
            HepRepWriter writer = new XMLHepRepFactory().createHepRepWriter(out, false, true);
            writer.write(root, null);
            writer.close();
        } catch (IOException ioe) {
            System.err.println("Could not write xml.gz file "+ioe);
        }

        System.out.println("Test finished ok.");
	}
}
