// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepType;

import java.util.Enumeration;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepTypeFromInstanceAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepTypeFromInstanceAdapter extends HepRepTypeAdapter {

    private String suffix;

    /**
     * Wrapper for HepRep1 Instance
     * @param type1 heprep1 type
     * @param instance1 heprep1 instance
     * @param parent heprep2 parent type
     * @param instance heprep2 instance
     * @param suffix ???
     */
    public HepRepTypeFromInstanceAdapter(hep.graphics.heprep1.HepRepType type1,
                                         hep.graphics.heprep1.HepRepInstance instance1,
                                         HepRepType parent,
                                         HepRepInstance instance,
                                         String suffix) {
                                            
        super(type1, instance1, parent);
        this.suffix = suffix;
        HepRepAdapterFactory factory = HepRepAdapterFactory.getFactory();        

        for (Enumeration e=instance1.getTypes(); e.hasMoreElements(); ) {
            types.add(factory.createHepRepType((hep.graphics.heprep1.HepRepType)e.nextElement(), this, instance));
        }

        for (Enumeration e=instance1.getPrimitives(); e.hasMoreElements(); ) {
            instance.addInstance(factory.createHepRepInstance(null, (hep.graphics.heprep1.HepRepPrimitive)e.nextElement(), instance, this));
        }

        // add points as instance
        Enumeration ep=instance1.getPoints();
        if (ep.hasMoreElements()) {
            HepRepInstance pointInstance = factory.createHepRepInstance(instance, this);
            while (ep.hasMoreElements()) {
                pointInstance.addPoint(factory.createHepRepPoint((hep.graphics.heprep1.HepRepPoint)ep.nextElement(), 
                                                                 pointInstance));
            } 
        }
    }
    
    public String getName() {
        return super.getName()+suffix;
    }
}
