// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.HepRepDefinition;

import java.util.Enumeration;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepDefinitionAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class HepRepDefinitionAdapter extends HepRepAttributeAdapter implements HepRepDefinition {

    /**
     * Wrapper for HepRep1 Definition
     * @param attribute heprep1 attribute
     */
    public HepRepDefinitionAdapter(hep.graphics.heprep1.HepRepAttribute attribute) {
        this(null, attribute);
    }

    /**
     * Wrapper for HepRep1 Definition
     * @param parentAttribute heprep1 parent attribute
     * @param attribute heprep1 attribute
     */
    public HepRepDefinitionAdapter(hep.graphics.heprep1.HepRepAttribute parentAttribute, hep.graphics.heprep1.HepRepAttribute attribute) {
        super(parentAttribute, attribute);
        HepRepAdapterFactory factory = HepRepAdapterFactory.getFactory();
        for (Enumeration e=attribute.getAttDefs(); e.hasMoreElements(); ) {
            addAttDef(factory.createHepRepAttDef((hep.graphics.heprep1.HepRepAttDef)e.nextElement()));
        }
        if (parentAttribute != null) {
            for (Enumeration e=parentAttribute.getAttDefs(); e.hasMoreElements(); ) {
                addAttDef(factory.createHepRepAttDef((hep.graphics.heprep1.HepRepAttDef)e.nextElement()));
            }
        }  
    }
        
    public abstract HepRepAttDef getAttDef(String name);
}