// Copyright 2000-2006, FreeHEP.
package hep.graphics.heprep.corbavalue;

import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.HepRepDefinition;
import hep.graphics.heprep.ref.DefaultHepRepAttDef;
import hep.graphics.heprep.util.ValueSet;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepDefinitionAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public abstract class HepRepDefinitionAdapter extends HepRepAttributeAdapter implements HepRepDefinition {

    private hep.graphics.heprep.corbavalue.idl.HepRepDefinition hepRepDefinition;
    private transient Map/*<LowerCaseName, HepRepAttDef>*/ atts;

    /**
     * Create a CORBA wrapper for a Definition 
     * @param hepRepDefinition corba definition
     */
    public HepRepDefinitionAdapter(hep.graphics.heprep.corbavalue.idl.HepRepDefinition hepRepDefinition) {
        super(hepRepDefinition);
        this.hepRepDefinition = hepRepDefinition;
    }

    private void fillAtts() {
        if (atts == null) {
            atts = new Hashtable();
            int n = hepRepDefinition.attDefs.length;
            for (int i=0; i<n; i++) {
                HepRepAttDef def = new HepRepAttDefAdapter(hepRepDefinition.attDefs[i]);
                atts.put(def.getLowerCaseName(), def);
            }
        }
    }

    public Set getAttDefsFromNode() {
        fillAtts();
        return new ValueSet(atts);
    }

    public HepRepAttDef getAttDefFromNode(String lowerCaseName) {
        lowerCaseName = lowerCaseName.intern();
        fillAtts();
        return (HepRepAttDef)atts.get(lowerCaseName);
    }

    public void addAttDef(HepRepAttDef attDef) {
        fillAtts();
        atts.put(attDef.getLowerCaseName(), attDef);
    }

    public void addAttDef(String name, String desc, String category, String extra) {
        addAttDef(new DefaultHepRepAttDef(name, desc, category, extra));
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!super.equals(o)) return false;
        if (o instanceof HepRepDefinition) {
            HepRepDefinition def = (HepRepDefinition)o;

            return def.getAttDefsFromNode().equals(getAttDefsFromNode());
        }
        return false;
    }
    
    public int hashCode() {
        long code = super.hashCode();
        code +=getAttDefsFromNode().hashCode();
        return (int)code;
    }

}

