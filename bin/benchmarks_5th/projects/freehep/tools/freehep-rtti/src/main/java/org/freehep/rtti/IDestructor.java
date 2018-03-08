// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.util.*;

/**
 * Defines a destructor in the RTTI.
 *
 * @author Mark Donszelmann
 * @version $Id: IDestructor.java 8584 2006-08-10 23:06:37Z duns $
 */
public class IDestructor {
    private String name;
    private Vector comments;
    private String[] exceptionTypes;

    IDestructor(String name, Vector comments, String[] exceptionTypes) {
        this.name = name;
        this.comments = comments;
        this.exceptionTypes = exceptionTypes;
    }

    /**
     * Returns the name of the destructor
     *
     *
     * @return name of destructor
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of comments describing this destructor
     *
     *
     * @return list of comments
     */
    public String[] getComments(String language) {
        return RTTI.getComments(comments, language);
    }

    /**
     * Returns the list of exceptions, thrown by this destructor
     *
     *
     * @return list of exception names (may be fully qualified)
     */
    public String[] getExceptionTypes() {
        return exceptionTypes;
    }

    /**
     * Semi-java string representation of this destructor
     *
     *
     * @return Semi-java string representation of this destructor
     */
    public String toString() {
        StringBuffer s = new StringBuffer("");
        String[] comments = getComments(null);

        for (int i = 0; i < comments.length; i++) {
            s.append(comments[i]);
            s.append("\n");
        }

        s.append("    public ");
        s.append(getName());
        s.append(" ()");

        String[] exceptionTypes = getExceptionTypes();

        if (exceptionTypes.length > 0) {
            s.append(" throws ");

            for (int i = 0; i < exceptionTypes.length; i++) {
                s.append(exceptionTypes[i]);

                if (i < exceptionTypes.length - 1) {
                    s.append(", ");
                }
            }
        }

        s.append(";");

        return s.toString();
    }
}

