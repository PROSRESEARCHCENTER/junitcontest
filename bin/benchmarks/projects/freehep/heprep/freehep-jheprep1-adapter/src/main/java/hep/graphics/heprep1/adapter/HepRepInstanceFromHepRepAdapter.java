// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepType;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepInstanceFromHepRepAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepInstanceFromHepRepAdapter extends AbstractHepRepInstanceAdapter {

    /**
     * Wrapper for HepRep1 HepRep
     * @param heprep1 heprep1 heprep
     * @param parent heprep2 parent instance
     * @param type heprep2 type
     */
    public HepRepInstanceFromHepRepAdapter(hep.graphics.heprep1.HepRep heprep1,
                                           HepRepInstance parent,
                                           HepRepType type) {
        super(heprep1, parent, type);
    }
            
    public List/*<HepRepPoint>*/ getPoints() {
        return Collections.EMPTY_LIST;
    }
    
    public Set getAttValuesFromNode() {
        return Collections.EMPTY_SET;
    }
    
    public HepRepAttValue getAttValueFromNode(String lowerCaseName) {
        return null;     
    } 
}
