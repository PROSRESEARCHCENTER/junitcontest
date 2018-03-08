// Copyright 2000-2004, FreeHEP.
package hep.graphics.heprep1.ref;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import hep.graphics.heprep1.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepType.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepType extends DefaultHepRepAttribute implements HepRepType, Serializable {

    private String name;
    private String version;
    private Vector points;
    private Vector primitives;
    private Vector instances;
    private Vector types;

    /**
     * Create Type
     * @param parent type parent
     * @param name type name
     * @param version type version
     */
    public DefaultHepRepType(DefaultHepRepAttribute parent, String name, String version) {
        super(parent);
        parent.add(this);
        this.name = name;
        this.version = version;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public HepRep getRoot() {
        HepRepAttribute parent = getParent();
        while (!(parent instanceof HepRep)) {
            parent = parent.getParent();
        }
        return (HepRep)parent;
    }
    
    public void add(HepRepPoint node) {
        if (points == null) {
            points = new Vector();
        }
        
        points.addElement(node);
    }

    public Enumeration getPoints() {
        return (points == null) ? empty : points.elements();
    }

    public void add(HepRepPrimitive node) {
        if (primitives == null) {
            primitives = new Vector();
        }
        
        primitives.addElement(node);
    }

    public Enumeration getPrimitives() {
        return (primitives == null) ? empty : primitives.elements();
    }

    public void add(HepRepInstance node) {
        if (instances == null) {
            instances = new Vector();
        }
        
        instances.addElement(node);
    }

    public Enumeration getInstances() {
        return (instances == null) ? empty : instances.elements();
    }

    public void add(HepRepType node) {
        if (types == null) {
            types = new Vector();
        }
        
        types.addElement(node);
    }

    public Enumeration getTypes() {
        return (types == null) ? empty : types.elements();
    }
    
    public String toString() {
        return "HepRepType: "+getName();
    }
}

