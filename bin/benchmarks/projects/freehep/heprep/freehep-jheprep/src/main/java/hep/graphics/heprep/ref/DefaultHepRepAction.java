// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRepAction;

import java.io.Serializable;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepAction.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepAction implements HepRepAction, Serializable {

    private String name;
    private String expression;

    protected DefaultHepRepAction(String name, String expression) {
        this.name = name;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }

    public HepRepAction copy() throws CloneNotSupportedException {
        return new DefaultHepRepAction(getName(), getExpression());
    }
    
/* Disabled for FREEHEP-386
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
*/
}

