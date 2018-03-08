// Copyright 2003, FreeHEP.
package hep.graphics.heprep.test;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepFactory;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.HepRepWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Writes multiple hepreps to one xml file.
 *
 * @author M.Donszelmann
 *
 * @version $Id: MultiWriteTest.java 8584 2006-08-10 23:06:37Z duns $
 */

public class MultiWriteTest {

    private final static int NUMBER_OF_POLYGONS = 255;
    private final static int NUMBER_OF_POINTS = 4;
    private final static int NUMBER_OF_HITS = 512;
    private final static int NUMBER_OF_TRACKS = 16;
    private final static int NUMBER_OF_TRACK_POINTS = 32;
    private Random random;

    /**
     * 
     */
    public MultiWriteTest() {
        random = new Random();
    }

    private HepRep makeRandomHepRep(HepRepFactory factory) throws IOException {
        HepRep heprep = factory.createHepRep();

        // layers
    	heprep.addLayer("Geometry");
    	heprep.addLayer("Event");

        // geometry types
        HepRepTreeID geometryTypeTreeID = factory.createHepRepTreeID("GeometryTypeTree", "1.0");
        HepRepTypeTree geometryTypeTree = factory.createHepRepTypeTree(geometryTypeTreeID);
        heprep.addTypeTree(geometryTypeTree);
        HepRepType geometryType = factory.createHepRepType(geometryTypeTree, "GeometryType");
        geometryType.addAttValue("drawAs", "polygon");
        geometryType.addAttValue("color", "gray");

        // geometry
        HepRepInstanceTree geometryTree = factory.createHepRepInstanceTree("Geometry", "MultiTest", geometryTypeTree);
        heprep.addInstanceTree(geometryTree);
        HepRepInstance geometry = factory.createHepRepInstance(geometryTree, geometryType);

        for (int p=0; p<NUMBER_OF_POLYGONS; p++) {
            HepRepInstance polygon = factory.createHepRepInstance(geometry, geometryType);
            for (int i=0; i<NUMBER_OF_POINTS; i++) {
                factory.createHepRepPoint(polygon, nextRandom(), nextRandom(), nextRandom());
            }
        }

        // event types
        HepRepTreeID eventTypeTreeID = factory.createHepRepTreeID("EventTypeTree", "1.0");
        HepRepTypeTree eventTypeTree = factory.createHepRepTypeTree(eventTypeTreeID);
        heprep.addTypeTree(eventTypeTree);
        HepRepType eventType = factory.createHepRepType(eventTypeTree, "Event");
        HepRepType hitType = factory.createHepRepType(eventType, "Hits");
        hitType.addAttValue("drawAs", "point");
        hitType.addAttValue("color", "yellow");
        HepRepType trackType = factory.createHepRepType(eventType, "Tracks");
        trackType.addAttValue("drawAs", "polyline");
        trackType.addAttValue("color", "blue");

        // event
        HepRepInstanceTree eventTree = factory.createHepRepInstanceTree("Event", "MultiTest", eventTypeTree);
        heprep.addInstanceTree(eventTree);
        HepRepInstance event = factory.createHepRepInstance(eventTree, eventType);

        for (int h=0; h<NUMBER_OF_HITS; h++) {
            HepRepInstance hit = factory.createHepRepInstance(event, hitType);
            factory.createHepRepPoint(hit, nextRandom(), nextRandom(), nextRandom());
        }

        for (int t=0; t<NUMBER_OF_TRACKS; t++) {
            HepRepInstance track = factory.createHepRepInstance(event, trackType);
            for (int i=0; i<NUMBER_OF_TRACK_POINTS; i++) {
                factory.createHepRepPoint(track, nextRandom(), nextRandom(), nextRandom());
            }
        }

        return heprep;
    }

    private double nextRandom() {
        return random.nextDouble()*1000;
    }

    private void write(HepRepFactory factory, int nevents, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        boolean zip = filename.endsWith(".zip");
        boolean gz = filename.endsWith(".gz");
        HepRepWriter writer = factory.createHepRepWriter(fos, zip, zip || gz);
        for (int i=0; i<nevents; i++) {
            HepRep heprep = makeRandomHepRep(factory);
            writer.write(heprep, "event"+i+".heprep");
        }
        writer.close();
        fos.close();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: MultiTest #events filename [factoryclass]");
            System.exit(1);
        }

        HepRepFactory factory;
        if (args.length == 3) {
            Class factoryClass = Class.forName(args[2]);
            factory = (HepRepFactory)factoryClass.newInstance();
        } else {
            factory = HepRepFactory.create();
        }
        int nevents = Integer.parseInt(args[0]);
        long start = System.currentTimeMillis();
        new MultiWriteTest().write(factory, nevents, args[1]);
        System.out.println("Written "+nevents+" events in "+(System.currentTimeMillis()-start)+" ms.");
    }
}
