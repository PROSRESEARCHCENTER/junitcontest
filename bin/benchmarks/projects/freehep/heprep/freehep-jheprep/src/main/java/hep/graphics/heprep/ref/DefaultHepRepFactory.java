// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAction;
import hep.graphics.heprep.HepRepFactory;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.HepRepReader;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.HepRepWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: DefaultHepRepFactory.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepFactory extends HepRepFactory {

    /**
     * Create a Default HepRep Factory, which uses the reference implementation
     */
    public DefaultHepRepFactory() {
    }

    public HepRepReader createHepRepReader(InputStream in) throws IOException {
        return new DefaultHepRepReader(in);
    }

    public HepRepReader createHepRepReader(String inputFileName) throws IOException {
        return new DefaultHepRepReader(inputFileName);
    }

    public HepRepWriter createHepRepWriter(OutputStream out, boolean randomAccess, boolean compress) throws IOException {
        return new DefaultHepRepWriter(out, randomAccess, compress);
    }

    public HepRepPoint createHepRepPoint (HepRepInstance instance,
                                   double x, double y, double z) {
        return new DefaultHepRepPoint(instance, x, y, z);
    }

    public HepRepInstance createHepRepInstance (HepRepInstance parent, HepRepType type) {
        return new DefaultHepRepInstance(parent, type);
    }

    public HepRepInstance createHepRepInstance (HepRepInstanceTree parent, HepRepType type) {
        return new DefaultHepRepInstance(parent, type);
    }

    public HepRepTreeID createHepRepTreeID (String name, String version, String qualifier) {
        return new DefaultHepRepTreeID(name, version, qualifier);
    }

    public HepRepAction createHepRepAction (String name, String expression) {
        return new DefaultHepRepAction(name, expression);
    }

    public HepRepInstanceTree createHepRepInstanceTree (String name, String version,
                                                        HepRepTreeID typeTree) {
        return new DefaultHepRepInstanceTree(name, version, typeTree);
    }

    public HepRepType createHepRepType (HepRepType parent, String name) {
        return new DefaultHepRepType(parent, name);
    }

    public HepRepType createHepRepType (HepRepTypeTree parent, String name) {
        return new DefaultHepRepType(parent, name);
    }

    public HepRepTypeTree createHepRepTypeTree (HepRepTreeID treeID) {
        return new DefaultHepRepTypeTree(treeID);
    }

    public HepRep createHepRep () {
        return new DefaultHepRep();
    }
}
