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
 * @version $Id: DefaultHepRepPrimitive.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepPrimitive extends DefaultHepRepAttribute implements HepRepPrimitive, Serializable {

    private Vector points;

    /**
     * Create Primitive
     * @param parent primitive parent
     */
    public DefaultHepRepPrimitive(DefaultHepRepAttribute parent) {
        super(parent);
        parent.add(this);
    }
    
    public HepRepInstance getInstance() {
        return (HepRepInstance)getParent();
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
}

