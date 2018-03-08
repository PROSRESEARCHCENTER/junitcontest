// Copyright 2000-2004, FreeHEP.

package hep.graphics.heprep1.ref;

import hep.graphics.heprep1.HepRep;
import hep.graphics.heprep1.HepRepAttName;
import hep.graphics.heprep1.HepRepCut;
import hep.graphics.heprep1.HepRepType;

import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRep.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRep extends DefaultHepRepAttribute implements HepRep {

    private Vector types;

    /**
     * Create default HepRep
     */
    public DefaultHepRep() {
        super(null);
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

    public void addType(HepRepType type) {
        add(type);
    }
    
    public boolean removeType(HepRepType type) {
        return (types == null) ? false : types.removeElement(type);
    }
    
    // FIXME: if we would want to split the XML in KnownTyped files, then we can use the methods below.    
    // there are no known types
    public Enumeration getTypeInfo() {
        return empty;
    }
    
    // no selection on knowntypes either
    public HepRepType getRepresentablesUncut(String name,
					     String version) {
        return null;
    }
    
    public HepRepType getRepresentables(String name,
					String version, 
					HepRepCut[] cutList,
					boolean getPoints,
					boolean getDrawAtts,
					boolean getNonDrawAtts,
					HepRepAttName[] invertAtts) {
        return null;
    }

}

