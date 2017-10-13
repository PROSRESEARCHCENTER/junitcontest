// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.util.*;

/**
 * Defines a named type in the RTTI, e.g. a name coupled to a type.
 *
 * @author Mark Donszelmann
 * @version $Id: INamedType.java 8584 2006-08-10 23:06:37Z duns $
 */
public class INamedType {
    private String name;
    private IType type;
    private String init;

    INamedType(String name, IType type, String init) {
        this.name = name;
        this.type = type;
        this.init = init;
    }

    /**
     * Returns the name of the variable
     *
     *
     * @return name of variable
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the variable
     *
     *
     * @return type of the variable
     */
    public IType getType() {
        return type;
    }

    /**
     * Returns the init of the variable
     *
     *
     * @return init of the variable
     */
    public String getInit() {
        return init;
    }

    public String getSignature(String packageName, Properties imports) {
        return type.getSignature(packageName, imports);
    }

    /**
     * Returns a string representation of this named type
     *
     *
     * @return a string representation of this named type
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(getType());
        s.append(" ");
        s.append(getName());
        if (getInit() != null) {
            s.append(" = ");
            s.append(getInit());
        }
        return s.toString();
    }

}

