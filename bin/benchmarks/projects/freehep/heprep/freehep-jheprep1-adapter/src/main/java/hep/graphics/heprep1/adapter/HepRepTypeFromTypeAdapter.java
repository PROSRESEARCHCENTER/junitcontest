// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepType;

import java.util.Enumeration;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepTypeFromTypeAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepTypeFromTypeAdapter extends HepRepTypeAdapter {

    private int typeNo;

    /**
     * Wrapper for HepRep1 Type
     * @param type1 heprep1 type
     * @param parent heprep2 parent type
     * @param instance heprep2 instance
     */
    public HepRepTypeFromTypeAdapter(hep.graphics.heprep1.HepRepType type1,
                                     HepRepType parent,
                                     HepRepInstance instance) {
        super(type1, type1, parent);
        typeNo = 1;
        HepRepAdapterFactory factory = HepRepAdapterFactory.getFactory();        
        
        for (Enumeration e=type1.getTypes(); e.hasMoreElements(); ) {
            types.add(factory.createHepRepType((hep.graphics.heprep1.HepRepType)e.nextElement(), this, instance));
        }

        for (Enumeration e=type1.getInstances(); e.hasMoreElements(); ) {
            hep.graphics.heprep1.HepRepInstance instance1 = (hep.graphics.heprep1.HepRepInstance)e.nextElement();
            if (instance1.getAttValues().hasMoreElements()) {
                // check any of the primitives
                boolean atts = false;
                for (Enumeration ep=instance1.getPrimitives(); ep.hasMoreElements(); ) {
                    hep.graphics.heprep1.HepRepPrimitive primitive1 = (hep.graphics.heprep1.HepRepPrimitive)ep.nextElement();
                    atts = atts || primitive1.getAttValues().hasMoreElements();
                }
                
                if (atts) {
                    // attributes on both instance1 and primitive1
                    if (instance1.getTypes().hasMoreElements()) {
                        // generate a suffixed type.
                        HepRepType type = factory.createHepRepType(type1, instance1, this, instance, "-"+typeNo);
                        typeNo++;
                        types.add(type);
                    } else {
                        // add primitives as instances with attributes from both primitive1 and instance1 (in that order).
                        for (Enumeration ep=instance1.getPrimitives(); ep.hasMoreElements(); ) {
                            instance.addInstance(factory.createHepRepInstance(instance1, (hep.graphics.heprep1.HepRepPrimitive)ep.nextElement(), 
                                                                              instance, this));
                        }
                    }
                } else {
                    // attributes on instance1, but not on primitive, 
                    // so just add a blank instance and all prims as instances with the attribute from this instance
                    HepRepInstance instanceInstance = factory.createHepRepInstance(instance, this);
                    for (Enumeration ep=instance1.getPrimitives(); ep.hasMoreElements(); ) {
                        instanceInstance.addInstance(factory.createHepRepInstance(instance1, (hep.graphics.heprep1.HepRepPrimitive)ep.nextElement(), 
                                                                               instanceInstance, this));
                    }                
                }                
            } else {
                // no attributes on instance1, just ignore it and add all primitives as instances
                HepRepInstance primitiveInstance = factory.createHepRepInstance(instance, this);
                for (Enumeration ep=instance1.getPrimitives(); ep.hasMoreElements(); ) {
                    primitiveInstance.addInstance(factory.createHepRepInstance(null, (hep.graphics.heprep1.HepRepPrimitive)ep.nextElement(), 
                                                                               primitiveInstance, this));
                }                
            }
        }

        // add primitives as instances
        Enumeration ep1=type1.getPrimitives();
        if (ep1.hasMoreElements()) {
            HepRepInstance primitiveInstance = factory.createHepRepInstance(instance, this);
            while (ep1.hasMoreElements()) {
                primitiveInstance.addInstance(factory.createHepRepInstance(null, (hep.graphics.heprep1.HepRepPrimitive)ep1.nextElement(), 
                                                                           primitiveInstance, this));
            } 
        }
             
        // add points as instance
        Enumeration ep2=type1.getPoints();
        if (ep2.hasMoreElements()) {
            HepRepInstance pointInstance = factory.createHepRepInstance(instance, this);
            while (ep2.hasMoreElements()) {
                pointInstance.addPoint(factory.createHepRepPoint((hep.graphics.heprep1.HepRepPoint)ep2.nextElement(), 
                                                                  pointInstance));
            } 
        }
    }
    
    public String toString() {
        return "HepRepTypeFromTypeAdapter: "+super.toString();
    }
}
