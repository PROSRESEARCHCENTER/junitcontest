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
 * @version $Id: DefaultHepRepInstance.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepInstance extends DefaultHepRepAttribute implements HepRepInstance, Serializable {

    private Vector points;
    private Vector primitives;
    private Vector types;

    /**
     * Create Instance
     * @param parent instance parent
     */
    public DefaultHepRepInstance(DefaultHepRepAttribute parent) {
        super(parent);
        parent.add(this);
    }
    
    public HepRepType getType() {
        return (HepRepType)getParent();
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

    public void add(HepRepType node) {
        if (types == null) {
            types = new Vector();
        }
        
        types.addElement(node);
    }

    public Enumeration getTypes() {
        return (types == null) ? empty : types.elements();
    }
}

