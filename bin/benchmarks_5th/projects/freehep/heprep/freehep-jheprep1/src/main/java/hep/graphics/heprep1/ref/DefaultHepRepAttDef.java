// Copyright 2000-2004, FreeHEP.

package hep.graphics.heprep1.ref;

import java.io.Serializable;

import hep.graphics.heprep1.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepAttDef.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepAttDef implements HepRepAttDef, Serializable {

    private String name, desc, type, extra;

    /**
     * Create HepRep Definition
     * @param name attribute name 
     * @param desc attribute description
     * @param type attribute type
     * @param extra attribute unit
     */
    public DefaultHepRepAttDef(String name, String desc, String type, String extra) {
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.extra = extra;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return desc;
    }
    
    public String getType() {
        return type;
    }
        
    public String getExtra() {
        return extra;
    }
}

