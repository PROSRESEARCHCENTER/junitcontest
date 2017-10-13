// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.util.Properties;
import java.util.Vector;

/**
 * Defines a method in the RTTI.
 *
 * @author Mark Donszelmann
 * @version $Id: IMethod.java 8584 2006-08-10 23:06:37Z duns $
 */
public class IMethod {
    private String name;
    private Vector comments;
    private boolean isStatic;
    private boolean isConst;
    private String[] templateParameters;
    private IType returnType;
    private INamedType[] parameterTypes;
    private String[] exceptionTypes;

    IMethod(String name, Vector comments, boolean isStatic, 
            String[] templateParameters, IType returnType, boolean isConst, 
            INamedType[] parameterTypes,
            String[] exceptionTypes) {
        this.name = name;
        this.comments = comments;
        this.isStatic = isStatic;
        this.templateParameters = templateParameters;
        this.returnType = returnType;
        this.isConst = isConst;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
    }

    /**
     * Returns the name of the method
     *
     *
     * @return name of the method
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of comments decribing this method.
     *
     *
     * @return list of comments
     */
    public String[] getComments(String language) {
        return RTTI.getComments(comments, language);
    }

    /**
     * Indicates if this method is static.
     *
     *
     * @return true if method is static
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Indicates the templateParameters used.
     *
     * @return array of template parameter names.
     */
    public String[] getTemplateParameters() {
        return templateParameters;
    }

    /**
     * Indicates the return type of this method
     *
     *
     * @return return type
     */
    public IType getReturnType() {
        return returnType;
    }

    /**
     * Indicates if this method is const.
     *
     *
     * @return true if method is const
     */
    public boolean isConst() {
        return isConst;
    }

    /**
     * Returns a list of named types which are parameters to this method
     *
     *
     * @return list of named parameter types
     */
    public INamedType[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Returns a list of exceptions, thrown by this method
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
        s.append(")");
        s.append(returnType.getSignature(packageName, imports));
        return s.toString();
    }

    /**
     * Semi-java string representation of this method
     * @return Semi-java string representation of this method
     */
    public String toString() {
        StringBuffer s = new StringBuffer("");
        String[] comments = getComments(null);

        for (int i = 0; i < comments.length; i++) {
            s.append(comments[i]);
        }
        s.append("\n");

        s.append(getSignature("", new Properties()));
        s.append("\n");

        s.append("    public ");

        if (isStatic()) {
            s.append("static ");
        }

        s.append(getReturnType());
        s.append(" ");
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

        if (isConst()) {
            s.append(" const");
        }

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
    /**
     * We consider two methods equal if one would override the other
     */
    public boolean equals(Object o)
    {
       if (o instanceof IMethod)
       {
          IMethod other = (IMethod) o;
          if (this.isStatic != other.isStatic) return false;
          if (!this.name.equals(other.name)) return false;
          if (!this.returnType.equals(other.returnType)) return false;
          if (this.parameterTypes.length != other.parameterTypes.length) return false;
          for (int i=0; i<parameterTypes.length; i++)
          {
             if (!this.parameterTypes[i].equals(other.parameterTypes[i])) return false;
          }
          return true;
       }
       return false;
    }
}

