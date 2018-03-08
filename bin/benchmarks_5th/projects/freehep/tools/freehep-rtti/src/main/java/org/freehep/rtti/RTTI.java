// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.util.*;

/**
 * Factory class to build the RTTI (RunTime Type Identification) needed for AID and JACO.
 * <p>
 *
 * @author Mark Donszelmann
 * @version $Id: RTTI.java 8584 2006-08-10 23:06:37Z duns $
 */
public class RTTI {
    private Hashtable/*<String, IPackage>*/ packages = new Hashtable();

    /**
     * Creates an empty RTTI
     *
     */
    public RTTI() {
    }

    /**
     * Returns the list of packages.
     *
     *
     * @return array of packages
     */
    public IPackage[] getPackages() {
        IPackage[] p = new IPackage[packages.size()];
        int i = 0;

        for (Enumeration e = packages.keys(); e.hasMoreElements(); ) {
            p[i++] = (IPackage)packages.get(e.nextElement());
        }

        return p;
    }

    /**
     * Returns the Package with fully qualified name.
     *
     *
     * @param packageName fully qualified name
     *
     * @return package definition
     */
    public IPackage getPackage(String packageName) {
        return (IPackage)packages.get(packageName);
    }

    public IPackage createPackage(String packageName) {
        return new IPackage(packageName);
    }

    /**
     * Creates a class and registers it under its package.
     * <p>
     * A warning is issued if a class definition already exists under this name.
     * The class definition is overridden.
     *
     * @param name name of the class
     * @param packageName package name
     * @param comments list of comments (String)
     * @param interfaces list of interface names (String: fully qualified)
     * @param constructors list of constructors (IConstructor)
     * @param destructor destructors (IDestructor)
     * @param methods list of methods (IMethod)
     *
     * @return class definition
     */
    public IClass createClass(String name, boolean isClass,
                              String packageName, Vector packageComments,
                              Vector comments, Vector eocComments,
                              Vector interfaces, Vector constructors,
                              IDestructor destructor, Vector methods, Vector fields) {
        return createClass(name, isClass, packageName, packageComments, comments, eocComments, 
                           null, interfaces, constructors, destructor, methods, fields);
    }
    
    /**
     * Creates a class and registers it under its package.
     * <p>
     * A warning is issued if a class definition already exists under this name.
     * The class definition is overridden.
     *
     * @param name name of the class
     * @param packageName package name
     * @param comments list of comments (String)
     * @param interfaces list of interface names (String: fully qualified)
     * @param constructors list of constructors (IConstructor)
     * @param destructor destructors (IDestructor)
     * @param methods list of methods (IMethod)
     *
     * @return class definition
     */
    public IClass createClass(String name, boolean isClass,
                              String packageName, Vector packageComments,
                              Vector comments, Vector eocComments,
                              Vector templateParameters, Vector interfaces, Vector constructors,
                              IDestructor destructor, Vector methods, Vector fields) {

        if (packageComments == null) packageComments = new Vector();
        if (comments == null) comments = new Vector();
        if (eocComments == null) eocComments = new Vector();

        String[] a0;

        if (templateParameters == null) {
            a0 = new String[0];
        } else {
            a0 = new String[templateParameters.size()];

            templateParameters.copyInto(a0);
        }

        String[] a1;

        if (interfaces == null) {
            a1 = new String[0];
        } else {
            a1 = new String[interfaces.size()];

            interfaces.copyInto(a1);
        }

        IConstructor[] a2;

        if (constructors == null) {
            a2 = new IConstructor[0];
        } else {
            a2 = new IConstructor[constructors.size()];

            constructors.copyInto(a2);
        }

        IMethod[] a3;

        if (methods == null) {
            a3 = new IMethod[0];
        } else {
            a3 = new IMethod[methods.size()];

            methods.copyInto(a3);
        }

        IField[] a4;

        if (fields == null) {
            a4 = new IField[0];
        } else {
            a4 = new IField[fields.size()];

            fields.copyInto(a4);
        }

        IPackage p = getPackage(packageName);
        if (p == null) {
            p = createPackage(packageName);
            packages.put(p.getName(), p);
        } else {
            if (p.getClass(name) != null) {
                System.err.println("Warning: Class " + name + " already exists.");
            }
        }

        IClass clazz = new IClass(name, isClass, packageName, packageComments,
                                  comments, eocComments, a0, a1, a2, destructor, a3, a4);

        p.addClass(clazz);

        return clazz;
    }

    /**
     * Creates a constructor.
     *
     * @param name name of the constructor
     * @param comments list of comments (String)
     * @param parameterTypes list of parameter types (InamedType)
     * @param exceptionTypes list of exception names (String)
     *
     * @return constructor definition
     */
    public IConstructor createConstructor(String name, Vector comments, Vector parameterTypes, Vector exceptionTypes) {

        if (comments == null) comments = new Vector();

        INamedType[] a1;

        if (parameterTypes == null) {
            a1 = new INamedType[0];
        } else {
            a1 = new INamedType[parameterTypes.size()];

            parameterTypes.copyInto(a1);
        }

        String[] a2;

        if (exceptionTypes == null) {
            a2 = new String[0];
        } else {
            a2 = new String[exceptionTypes.size()];

            exceptionTypes.copyInto(a2);
        }

        return new IConstructor(name, comments, a1, a2);
    }

    /**
     * Creates a destructor.
     *
     * @param name name of the destructor
     * @param comments list of comments (String)
     * @param exceptionTypes list of exception names (String)
     *
     * @return destructor definition
     */
    public IDestructor createDestructor(String name, Vector comments, Vector exceptionTypes) {

        if (comments == null) comments = new Vector();

        String[] a1;

        if (exceptionTypes == null) {
            a1 = new String[0];
        } else {
            a1 = new String[exceptionTypes.size()];

            exceptionTypes.copyInto(a1);
        }

        return new IDestructor(name, comments, a1);
    }

    /**
     * Creates a method.
     *
     *
     * @param name name of the method
     * @param comments list of comments (String)
     * @param isStatic true if method is static
     * @param returnType type of return parameter (IType)
     * @param parameterTypes list of parameter types (INamedType)
     * @param exceptionTypes list of exception names (String)
     *
     * @return method definition
     */
    public IMethod createMethod(String name, Vector comments, boolean isStatic, 
                                IType returnType, boolean isConst, 
                                Vector parameterTypes,
                                Vector exceptionTypes) {
        return createMethod(name, comments, isStatic, null, returnType, isConst, parameterTypes, exceptionTypes);
    }
    
    /**
     * Creates a method.
     *
     *
     * @param name name of the method
     * @param comments list of comments (String)
     * @param isStatic true if method is static
     * @param returnType type of return parameter (IType)
     * @param parameterTypes list of parameter types (INamedType)
     * @param exceptionTypes list of exception names (String)
     *
     * @return method definition
     */
    public IMethod createMethod(String name, Vector comments, boolean isStatic, 
                                Vector templateParameters, IType returnType, boolean isConst, 
                                Vector parameterTypes,
                                Vector exceptionTypes) {

        if (comments == null) comments = new Vector();

        String[] a0;

        if (templateParameters == null) {
            a0 = new String[0];
        } else {
            a0 = new String[templateParameters.size()];

            templateParameters.copyInto(a0);
        }

        INamedType[] a1;

        if (parameterTypes == null) {
            a1 = new INamedType[0];
        } else {
            a1 = new INamedType[parameterTypes.size()];

            parameterTypes.copyInto(a1);
        }

        String[] a2;

        if (exceptionTypes == null) {
            a2 = new String[0];
        } else {
            a2 = new String[exceptionTypes.size()];

            exceptionTypes.copyInto(a2);
        }

        return new IMethod(name, comments, isStatic, a0, returnType, isConst, a1, a2);
    }

    /**
     * Creates a named field.
     *
     *
     * @param namedType named type (INamedType)
     *
     * @return field definition
     */
    public IField createField(INamedType namedType, Vector comments) {

        if (comments == null) comments = new Vector();

        return new IField(namedType, comments);
    }

    /**
     * Creates a named type.
     *
     *
     * @param name name of the parameter (String)
     * @param type type of the parameter (IType)
     * @param init init of the parameter (String)
     *
     * @return named type definition
     */
    public INamedType createNamedType(String name, IType type, String init) {
        return new INamedType(name, type, init);
    }

    /**
     * Creates a type (primitive or Class)
     *
     *
     * @param name name of the type
     * @param primitive true if this type is a primitive
     * @param enumeration true if this is an enumeration
     * @param reference true if this type is a reference
     * @param pointer number of pointer postfixes (*)
     * @param dimension dimension of the type, 0 for no dimension.
     *
     * @return type definition
     */
    public IType createType(String name, boolean konst, boolean primitive, boolean enumeration,
                            boolean reference, int pointer, int dimension, Vector types) {
        IType[] a2;
        if (types == null) {
            a2 = new IType[0];
        } else {
            a2 = new IType[types.size()];

            types.copyInto(a2);
        }
        return new IType(name, konst, primitive, enumeration, reference, pointer, dimension, a2);
    }

    /**
     * Creates an aliased type
     *
     *
     * @param name name of the type
     * @param alias pointing to aliased type (typedef)
     * @param reference true if this type is a reference
     * @param pointer number of pointer postfixes (*)
     * @param dimension dimension of the type, 0 for no dimension.
     *
     * @return type definition
     */
    public IType createType(String name, IType alias, boolean reference, int pointer, int dimension) {
        return new IType(name, alias, reference, pointer, dimension);
    }

    /**
     * String representation of the RTTI, which lists the full RTTI in semi-java format, without
     * package prefix.
     *
     *
     * @return String representation of the RTTI.
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
        StringBuffer s = new StringBuffer("Repository RTTI:\n");

        s.append("================\n");

        for (Enumeration e = packages.keys(); e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            IPackage p = (IPackage)packages.get(key);

            s.append(p.toString(packagePrefix));
            s.append("\n");
            s.append("================\n");
        }

        return s.toString();
    }

    /**
     * returns language specific and general comments
     *
     * @param comments list of all comments, each may be preceded by @xxx: to specify language xxx
     * @param language to be used to filter comments
     * @return array of comments
     */
    public static String[] getComments(Vector comments, String language) {
        Vector result = new Vector();
        for (Iterator i = comments.iterator(); i.hasNext(); ) {
            String comment = (String)i.next();
            if (comment.startsWith("@")) {
                // language specific clause "@java: ...."
                if (comment.startsWith("@"+language+":")) {
                    result.add(comment.substring(1+language.length()+1));
                }
            } else {
                if ((language != null) && language.equals("java") && comment.startsWith("/** @")) {
                    // filter "/** @xxx " doxygen comments for java, not to confuse JavaDoc
                    result.add("/**");
                } else {
                    result.add(comment);
                }
            }
        }

        String[] r = new String[result.size()];
        result.copyInto(r);
        return r;
    }
}

