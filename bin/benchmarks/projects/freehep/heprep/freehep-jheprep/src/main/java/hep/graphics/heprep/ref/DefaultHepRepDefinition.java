// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.HepRepDefinition;
import hep.graphics.heprep.util.ValueSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepDefinition.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class DefaultHepRepDefinition extends DefaultHepRepAttribute implements HepRepDefinition, Serializable {

    private Map/*<LowerCaseNamae, HepRepAttDef>*/ atts;

    protected DefaultHepRepDefinition() {
        super();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }

    public Set/*<HepRepAttDef>*/ getAttDefsFromNode() {
        return new ValueSet(atts);
    }

    public void addAttDef(HepRepAttDef hepRepAttDef) {
        try {
            DefaultHepRepAttDef def = (DefaultHepRepAttDef)hepRepAttDef;
    
            if (atts == null) atts = new Hashtable();
            atts.put(def.getLowerCaseName(), def);
        } catch (ClassCastException cce) {
            System.err.println("DefaultHepRepAttribute.addDefinition() cannot add argument of class: "+hepRepAttDef.getClass()+", ignored.");
        }
    }

    public void addAttDef(String name, String desc, String category, String extra) {
        addAttDef(new DefaultHepRepAttDef(name, desc, category, extra));
    }

    public HepRepAttDef getAttDefFromNode(String lowerCaseName) {
        lowerCaseName = lowerCaseName.intern();
        if (atts == null) return null;
        return (HepRepAttDef)atts.get(lowerCaseName);
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
    
/* Disabled for FREEHEP-386
    public int hashCode() {
        long code = super.hashCode();
        code += getAttDefsFromNode().hashCode();
        return (int)code;
    }
*/

    public abstract HepRepAttDef getAttDef(String name);
}

