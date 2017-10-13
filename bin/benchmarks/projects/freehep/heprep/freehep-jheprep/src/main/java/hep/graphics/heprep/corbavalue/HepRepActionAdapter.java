// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.corbavalue;

import hep.graphics.heprep.HepRepAction;
import hep.graphics.heprep.ref.DefaultHepRepFactory;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepActionAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepActionAdapter implements HepRepAction {

    private hep.graphics.heprep.corbavalue.idl.HepRepAction hepRepAction;

    /**
     * Create a CORBA wrapper for an Action 
     * @param hepRepAction corba action
     */
    public HepRepActionAdapter(hep.graphics.heprep.corbavalue.idl.HepRepAction hepRepAction) {
        super();
        this.hepRepAction = hepRepAction;
    }

    public HepRepAction copy() throws CloneNotSupportedException {
        return new DefaultHepRepFactory().createHepRepAction(getName(), getExpression());
    }

    public String getName() {
        return hepRepAction.name;
    }

    public String getExpression() {
        return hepRepAction.expression;
    }

    public boolean equals(Object o) {
        if (o instanceof HepRepAction) {
            HepRepAction ref = (HepRepAction)o;
            return (ref.getName().equals(getName()) && ref.getExpression().equals(getExpression()));
        }
        return false;
    }
    
    public int hashCode() {
        return getName().hashCode() + getExpression().hashCode();
    }
}

