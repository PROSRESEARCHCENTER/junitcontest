// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepType;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepInstanceFromInstanceAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepInstanceFromInstanceAdapter extends AbstractHepRepInstanceAdapter {

    private List instances;
    private List points;

    /**
     * Wrapper for HepRep1 Instance
     * @param instance1 heprep1 instance
     * @param parent heprep2 parent instance
     * @param type heprep2 type
     */
    public HepRepInstanceFromInstanceAdapter(hep.graphics.heprep1.HepRepInstance instance1,
                                             HepRepInstance parent,
                                             HepRepType type) {
        super(instance1, parent, type);
        HepRepAdapterFactory factory = HepRepAdapterFactory.getFactory();        
        instances = new ArrayList();
        // types
        for (Enumeration e=instance1.getTypes(); e.hasMoreElements(); ) {
            hep.graphics.heprep1.HepRepType type1 = (hep.graphics.heprep1.HepRepType)e.nextElement();
            type.addType(factory.createHepRepType(type1, type, this));
        }
        // primitives
        for (Enumeration e=instance1.getPrimitives(); e.hasMoreElements(); ) {
            hep.graphics.heprep1.HepRepPrimitive primitive1 = (hep.graphics.heprep1.HepRepPrimitive)e.nextElement();
            instances.add(factory.createHepRepInstance(null, primitive1, this, type));
        }
        // points
        points = new ArrayList();
        for (Enumeration e=instance1.getPoints(); e.hasMoreElements(); ) {
            points.add(factory.createHepRepPoint((hep.graphics.heprep1.HepRepPoint)e.nextElement(), this));
        }
    }
    
    public List/*<HepRepInstance>*/ getInstances() {
        return instances;
    }
        
    public List/*<HepRepPoint>*/ getPoints() {
        return points;
    }    
}
