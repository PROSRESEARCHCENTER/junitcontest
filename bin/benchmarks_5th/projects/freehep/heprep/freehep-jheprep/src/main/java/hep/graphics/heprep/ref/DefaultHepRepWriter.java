// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAction;
import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepAttribute;
import hep.graphics.heprep.HepRepDefinition;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.HepRepWriter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Writew out java serialized IO
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepWriter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepWriter implements HepRepWriter {

    protected ObjectOutputStream out;
    protected ZipOutputStream zip;
    protected Map/*<String, String>*/ properties;

    protected DefaultHepRepWriter(OutputStream out, boolean randomAccess, boolean compress) throws IOException {
        super();
        if (randomAccess) {
            zip = new ZipOutputStream(out);
            zip.setLevel(compress ? Deflater.DEFAULT_COMPRESSION : Deflater.NO_COMPRESSION);
            // this.out is initialized later in write(HepRep)
        } else if (compress) {
            this.out = new ObjectOutputStream(new GZIPOutputStream(out));
        } else {
            this.out = new ObjectOutputStream(out);
        }
        properties = new HashMap();
    }

    public void addProperty(String key, String value) throws IOException {
        properties.put(key, value);
    }

    public void close() throws IOException {
        if (zip != null) {
            zip.putNextEntry(new ZipEntry("heprep.properties"));
            PrintStream ps = new PrintStream(zip);
            for (Iterator i=properties.keySet().iterator(); i.hasNext(); ) {
                String key = (String)i.next();
                ps.println(key+"="+(String)properties.get(key));
            }
            zip.closeEntry();
            zip.close();
        }
        if (out != null) {
            out.close();
        }
    }

    public void write(HepRep heprep, String name) throws IOException {
        if (zip != null) {
            zip.putNextEntry(new ZipEntry(name));
            out = new ObjectOutputStream(zip);
        }
        out.writeObject(heprep);
        if (zip != null) {
            zip.closeEntry();
        }
    }

    public void write(List/*<String>*/ layerOrder) throws IOException {
        out.writeObject(layerOrder);
    }

    public void write(HepRepTypeTree typeTree) throws IOException {
        out.writeObject(typeTree);
    }

    public void write(HepRepType type) throws IOException {
        out.writeObject(type);
    }

    public void write(HepRepTreeID treeID) throws IOException {
        out.writeObject(treeID);
    }

    public void write(HepRepAction action) throws IOException {
        out.writeObject(action);
    }

    public void write(HepRepInstanceTree instanceTree) throws IOException {
        out.writeObject(instanceTree);
    }

    public void write(HepRepInstance instance) throws IOException {
        out.writeObject(instance);
    }

    public void write(HepRepPoint point) throws IOException {
        out.writeObject(point);
    }

    public void write(HepRepAttribute attribute) throws IOException {
        out.writeObject(attribute);
    }

    public void write(HepRepDefinition definition) throws IOException {
        out.writeObject(definition);
    }

    public void write(HepRepAttValue attValue) throws IOException {
        out.writeObject(attValue);
    }

    public void write(HepRepAttDef attDef) throws IOException {
        out.writeObject(attDef);
    }
}

