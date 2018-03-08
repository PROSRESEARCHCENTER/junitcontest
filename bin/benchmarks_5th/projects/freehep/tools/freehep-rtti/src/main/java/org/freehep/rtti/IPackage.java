// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.util.*;

/**
 * Defines a Package.
 *
 * @author Mark Donszelmann
 * @version $Id: IPackage.java 8584 2006-08-10 23:06:37Z duns $
 */
public class IPackage {
    private String name;
    private Hashtable/*<String, IClass>*/ classes = new Hashtable();

    IPackage(String name) {
        this.name = ((name == null) || (name.equals(""))) ? "<default>" : name;
    }

    /**
     * Returns the name of the package
     *
     *
     * @return name of package
     */
    public String getName() {
        return name;
    }

    public void addClass(IClass clazz) {
        classes.put(clazz.getName(), clazz);
    }

    public IClass getClass(String name) {
        return (IClass)classes.get(name);
    }

    public IClass[] getClasses() {
        IClass[] c = new IClass[classes.size()];
        int i = 0;

        for (Enumeration e = classes.keys(); e.hasMoreElements(); ) {
            c[i++] = (IClass)classes.get(e.nextElement());
        }

        return c;
    }

    /**
     * Returns a string representation of this package
     *
     *
     * @return a string representation of this package
     */
    public String toString() {
        return toString(null);
    }
    
    
    /**
     * String representation of the RTTI, which lists the full RTTI in semi-java format, with
     * package prefix.
     *
     * @param packagePrefix name to prefix to the package name
     *
     * @return String representation of the RTTI.
     */
    public String toString(String packagePrefix) {
        StringBuffer s = new StringBuffer("Package: ");
        s.append(getName());
        s.append("\n");

        for (Enumeration e = classes.keys(); e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            IClass c = (IClass)classes.get(key);

            s.append(c.toString(packagePrefix));
            s.append("\n");
        }

        return s.toString();
    }
}

