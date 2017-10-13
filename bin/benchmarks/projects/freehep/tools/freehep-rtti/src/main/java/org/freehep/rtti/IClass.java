// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.util.*;

/**
 * Defines a class/interface in the RTTI.
 *
 * @author Mark Donszelmann
 * @version $Id: IClass.java 8584 2006-08-10 23:06:37Z duns $
 */
public class IClass {
    private String name;
    private boolean isClass;
    private String packageName;
    private Vector packageComments;
    private Vector comments;
    private Vector eocComments;
    private Vector eopComments;
    private Vector eofComments;
    private String[] templateParameters;
    private String[] interfaces;
    private IConstructor[] constructors;
    private IDestructor destructor;
    private IMethod[] methods;
    private IField[] fields;
    private IField[] enumFields = new IField[0];
    
    IClass(String name, boolean isClass,
           String packageName, Vector packageComments,
           Vector comments, Vector eocComments,
           String[] templateParameters, String[] interfaces, IConstructor[] constructors,
           IDestructor destructor, IMethod[] methods, IField[] fields) {
        this.name = name;
        this.isClass = isClass;
        this.packageName = packageName;
        this.packageComments = packageComments;
        this.comments = comments;
        this.eocComments = eocComments;
        this.templateParameters = templateParameters;
        this.interfaces = interfaces;
        this.constructors = constructors;
        this.destructor = destructor;
        this.methods = methods;
        this.fields = fields;
    }

    /**
     * returns the name of this class
     *
     *
     * @return name of the class (unqualified)
     */
    public String getName() {
        return name;
    }

    /**
     * returns true if this is a class
     *
     *
     * @return true if this is a class
     */
    public boolean isClass() {
        return isClass;
    }

    /**
     * Returns the package name
     *
     * @return full package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Returns the package name, consisting of the prefix and the defined package name.
     *
     *
     * @param packagePrefix prefix to be prefix to the defined package name, or null, if not necessary.
     *
     * @return full package name including prefix
     */
    public String getPackageName(String packagePrefix) {
        if ((packagePrefix != null) && (!packagePrefix.equals(""))) {
            if ((packageName != null) && (!packageName.equals(""))) {
                return packagePrefix + "." + packageName;
            } else {
                return packagePrefix;
            }
        }

        return packageName;
    }

    /**
     * Returns the list of package comments describing this class.
     *
     *
     * @return list of package comments.
     */
    public String[] getPackageComments(String language) {
        return RTTI.getComments(packageComments, language);
    }

    /**
     * Returns the list of comments describing this class.
     *
     *
     * @return list of comments.
     */
    public String[] getComments(String language) {
        return RTTI.getComments(comments, language);
    }

    /**
     * Returns the list of end-of-class comments.
     *
     *
     * @return list of comments.
     */
    public String[] getEOCComments(String language) {
        return RTTI.getComments(eocComments, language);
    }

    /**
     * Returns the list of end-of-package comments.
     *
     *
     * @return list of comments.
     */
    public String[] getEOPComments(String language) {
        return RTTI.getComments(eopComments, language);
    }
    public void setEOPComments(Vector eopComments) {
        this.eopComments = eopComments;
    }

    /**
     * Returns the list of end-of-file comments.
     *
     *
     * @return list of comments.
     */
    public String[] getEOFComments(String language) {
        return RTTI.getComments(eofComments, language);
    }
    public void setEOFComments(Vector eofComments) {
        this.eofComments = eofComments;
    }

    public IField[] getEnumFields() {
        return enumFields;
    }

    public void setEnumFields(Vector enumFields) {
        this.enumFields = new IField[enumFields.size()];
        enumFields.toArray(this.enumFields);
    }

    /**
     * Returns the list of template parameters used by this class.
     *
     *
     * @return list of template parameter names
     */
    public String[] getTemplateParameters() {
        return templateParameters;
    }

    /**
     * Returns the list of interfaces, implemented by this class.
     *
     *
     * @return list of interface names
     */
    public String[] getInterfaces() {
        return interfaces;
    }

    /**
     * Returns the list of constructors defined in this class.
     *
     *
     * @return list of constructors
     */
    public IConstructor[] getConstructors() {
        return constructors;
    }

    /**
     * Returns the destructor defined in this class.
     *
     *
     * @return destructor
     */
    public IDestructor getDestructor() {
        return destructor;
    }

    /**
     * Returns the list of methods (including static ones) defined in this class
     *
     *
     * @return list of methods
     */
    public IMethod[] getMethods() {
        return methods;
    }

    /**
     * Returns the list of fields (including static ones) defined in this class
     *
     *
     * @return list of fields
     */
    public IField[] getFields() {
        return fields;
    }

    /**
     * Semi-java string representation of this class without package prefix.
     *
     *
     * @return Semi-java string representation of this class without package prefix.
     */
    public String toString() {
        return toString(null);
    }

    /**
     * Semi-java string representation of this class without package prefix.
     *
     * @param packagePrefix prefix to be used in the string representation.
     *
     * @return Semi-java string representation of this class without package prefix.
     */
    public String toString(String packagePrefix) {
        StringBuffer s = new StringBuffer();

        String[] packageComments = getPackageComments(null);
        for (int i = 0; i < packageComments.length; i++) {
            s.append(packageComments[i]);
        }

        s.append("package ");
        s.append(getPackageName(packagePrefix));
        s.append(";");

        String[] comments = getComments(null);
        for (int i = 0; i < comments.length; i++) {
            s.append(comments[i]);
        }
        s.append("\n");

        s.append("public ");
        s.append(isClass() ? "class" : "interface");
        s.append(" ");
        s.append(getName());

        String[] interfaces = getInterfaces();

        if (interfaces.length > 0) {
            s.append(" extends ");

            for (int i = 0; i < interfaces.length; i++) {
                s.append(interfaces[i]);

                if (i < interfaces.length - 1) {
                    s.append(", ");
                }
            }
        }

        s.append(" {");

        s.append("\n");
        IConstructor[] constructors = getConstructors();

        for (int i = 0; i < constructors.length; i++) {
            s.append(constructors[i]);
        }

        IDestructor destructor = getDestructor();

        if (destructor != null) {
            s.append(destructor);
        }

        IMethod[] methods = getMethods();

        for (int i = 0; i < methods.length; i++) {
            s.append(methods[i]);
        }

        IField[] fields = getFields();

        for (int i = 0; i < fields.length; i++) {
            s.append(fields[i]);
        }

        String[] eocComments = getEOCComments(null);
        for (int i = 0; i < eocComments.length; i++) {
            s.append(eocComments[i]);
        }
        s.append("\n");

        s.append("\n}\n");

        String[] eofComments = getEOFComments(null);
        for (int i = 0; i < eofComments.length; i++) {
            s.append(eofComments[i]);
        }
        s.append("\n");

        return s.toString();
    }

}

