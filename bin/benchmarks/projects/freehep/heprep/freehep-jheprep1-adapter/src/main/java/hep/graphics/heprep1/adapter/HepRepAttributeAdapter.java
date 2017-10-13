// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.ref.DefaultHepRepDefinition;

import java.util.Enumeration;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepAttributeAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
// NOTE we inherit here from Definition to make sure we can also use this AttributeAdapter for the DefinitionAdapter.
public abstract class HepRepAttributeAdapter extends DefaultHepRepDefinition {

    /**
     * Wrapper for HepRep1 Attribute
     * @param attribute heprep1 attribute
     */
    public HepRepAttributeAdapter(hep.graphics.heprep1.HepRepAttribute attribute) {
        this(null, attribute);
    }
    
    /**
     * Wrapper for HepRep1 Attribute
     * @param parentAttribute heprep1 attribute parent
     * @param attribute heprep1 attribute
     */
    public HepRepAttributeAdapter(hep.graphics.heprep1.HepRepAttribute parentAttribute, hep.graphics.heprep1.HepRepAttribute attribute) {
        HepRepAdapterFactory factory = HepRepAdapterFactory.getFactory();
        for (Enumeration e=attribute.getAttValues(); e.hasMoreElements(); ) {
            addAttValue(factory.createHepRepAttValue((hep.graphics.heprep1.HepRepAttValue)e.nextElement()));
        }
        if (parentAttribute != null) {
            for (Enumeration e=parentAttribute.getAttValues(); e.hasMoreElements(); ) {
                addAttValue(factory.createHepRepAttValue((hep.graphics.heprep1.HepRepAttValue)e.nextElement()));
            }
        }                    
    }

    // NOTE implemented as null since this is not meant to be an HepRepDefinition.
    public HepRepAttDef getAttDef(String name) {
        return null;
    }
}
