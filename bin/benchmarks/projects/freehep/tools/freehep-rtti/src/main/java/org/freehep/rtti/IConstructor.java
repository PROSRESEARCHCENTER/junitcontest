// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.util.*;

/**
 * Defines a constructor in the RTTI.
 *
 * @author Mark Donszelmann
 * @version $Id: IConstructor.java 8584 2006-08-10 23:06:37Z duns $
 */
public class IConstructor {
    private String name;
    private Vector comments;
    private INamedType[] parameterTypes;
    private String[] exceptionTypes;

    IConstructor(String name, Vector comments, INamedType[] parameterTypes, String[] exceptionTypes) {
        this.name = name;
        this.comments = comments;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
    }

    /**
     * Returns the name of the constructor
     *
     *
     * @return name of constructor
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of comments describing this constructor
     *
     *
     * @return list of comments
     */
    public String[] getComments(String language) {
        return RTTI.getComments(comments, language);
    }

    /**
     * Returns the list of named types, which are parameters of this constructor
     *
     *
     * @return list of named parameter types
     */
    public INamedType[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Returns the list of exceptions, thrown by this constructor
     *
     *
     * @return list of exception names (may be fully qualified)
     */
    public String[] getExceptionTypes() {
        return exceptionTypes;
    }

    public String getSignature(String packageName, Properties imports) {
        StringBuffer s = new StringBuffer();
        s.append("(");
        for (int i=0; i<parameterTypes.length; i++) {
            s.append(parameterTypes[i].getSignature(packageName, imports));
        }
        s.append(")V");
        return s.toString();
    }

    /**
     * Semi-java string representation of this constructor
     *
     *
     * @return Semi-java string representation of this constructor
     */
    public String toString() {
        StringBuffer s = new StringBuffer("");
        String[] comments = getComments(null);

        for (int i = 0; i < comments.length; i++) {
            s.append(comments[i]);
            s.append("\n");
        }

        s.append(getSignature("", new Properties()));
        s.append("\n");

        s.append("    public ");
        s.append(getName());
        s.append(" (");

        INamedType[] parameterTypes = getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            s.append(parameterTypes[i]);

            if (i < parameterTypes.length - 1) {
                s.append(", ");
            }
        }

        s.append(" )");

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

