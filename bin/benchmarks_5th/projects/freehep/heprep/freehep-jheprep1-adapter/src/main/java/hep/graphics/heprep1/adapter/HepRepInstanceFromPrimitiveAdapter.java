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
 * @version $Id: HepRepInstanceFromPrimitiveAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepInstanceFromPrimitiveAdapter extends AbstractHepRepInstanceAdapter {

    private List points;

    /**
     * Wrapper for HepRep1 Primitive
     * @param instance1 heprep1 instance
     * @param primitive1 heprep1 primitive
     * @param parent heprep2 parent instance
     * @param type heprep2 type
     */
    public HepRepInstanceFromPrimitiveAdapter(hep.graphics.heprep1.HepRepInstance instance1,
                                              hep.graphics.heprep1.HepRepPrimitive primitive1,
                                              HepRepInstance parent,
                                              HepRepType type) {
        super(instance1, primitive1, parent, type);
        points = new ArrayList();

        HepRepAdapterFactory factory = HepRepAdapterFactory.getFactory();        
        for (Enumeration e=primitive1.getPoints(); e.hasMoreElements(); ) {
            points.add(factory.createHepRepPoint((hep.graphics.heprep1.HepRepPoint)e.nextElement(), this));
        }
    }
        
    public List/*<HepRepPoint>*/ getPoints() {
        return points;
    }    

}
