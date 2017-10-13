// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.util.*;

/**
 * Defines a type in the RTTI.
 *
 * This type may be cascaded (typedef in C++) to another type.
 * The methods isPrimitive, isEnumeration, getDimension and getPointer accomodate for this.
 *
 * @author Mark Donszelmann
 * @version $Id: IType.java 8584 2006-08-10 23:06:37Z duns $
 */
public class IType {
    private String name;
    private boolean primitive;
    private boolean enumeration;
    private boolean konst;
    private IType alias;
    private int pointer;
    private boolean reference;
    private int dimension;
    private IType[] types;

    IType(String name, boolean konst, boolean primitive, boolean enumeration,
          boolean reference, int pointer, int dimension, IType[] types) {
        this.name = name;
        this.konst = konst;
        this.primitive = primitive;
        this.enumeration = enumeration;
        this.alias = null;
        this.reference = reference;
        this.pointer = pointer;
        this.dimension = dimension;
        this.types = types;
    }

    IType(String name, IType alias, boolean reference, int pointer, int dimension) {
        this.name = name;
        this.primitive = false;
        this.konst = false;
        this.alias = alias;
        this.reference = reference;
        this.pointer = pointer;
        this.dimension = dimension;
    }

    /**
     * Returns the name of the type. This name may be fully qualified. Otherwise it belongs
     * to the current package.
     *
     *
     * @return name of the type
     */
    public String getName() {
        return (alias == null) ? name : alias.getName();
    }

    /**
     * Indicates if this type is a primitive type.
     *
     *
     * @return true if this type is a primitive
     */
    public boolean isPrimitive() {
        return (alias == null) ? primitive : alias.isPrimitive();
    }

    /**
     * Indicates if this type is an enumerated type.
     *
     *
     * @return true if this type is an enumeration
     */
    public boolean isEnumeration() {
        return (alias == null) ? enumeration : alias.isEnumeration();
    }

    /**
     * Indicates if this type is a const type.
     *
     *
     * @return true if this type is a const
     */
    public boolean isConst() {
        return (alias == null) ? konst : alias.isConst();
    }

    /**
     * Indicates if this type is a reference.
     *
     *
     * @return true if reference
     */
    public boolean isReference() {
        if (alias == null) {
            return reference;
        }

        return alias.isReference();
    }

    /**
     * Indicates the number of pointer postfixes this type has.
     *
     *
     * @return pointer postfixes of this type (*)
     */
    public int getPointer() {
        if (alias == null) {
            return pointer;
        }

        return alias.getPointer();
    }

    /**
     * Indicates the dimension of this type.
     * <p>1 = [], 2 = [][], etc...
     *
     * @return dimension of this type, 0 is no dimension.
     */
    public int getDimension() {
        return (alias == null) ? dimension : alias.getDimension()+dimension;
    }

    /**
     * Indicates the template of this type.
     *
     * @return type of template
     */
    public IType[] getTypes() {
        return types;
    }

    public boolean isVoid()
    {
        return getName().equals("void");
    }

    public String getSignature(String packageName, Properties imports) {
        StringBuffer s = new StringBuffer();
        if (alias == null) {
            if (isPrimitive() || isVoid()) {
                s.append(getName());
            } else {
                // FIXME, if not found, add packagename of this class
                String name = getName();
                for (int i=getDimension(); i>0; i--) {
                    name = getTypes()[0].getName();
                }
                name = imports.getProperty(name,null);
                s.append( name != null ? name : packageName+"."+getName());
            }
        } else {
            s.append(alias.getSignature(packageName, imports));
        }
        for (int i = 0; i < getDimension(); i++) {
            s.append("[]");
        }

        return org.apache.bcel.classfile.Utility.getSignature(s.toString());
    }

    /**
     * Returns a string representation of this type.
     *
     *
     * @return string representatoon of this type
     */
    public String toString() {
        StringBuffer s = new StringBuffer();

        if (isConst()) {
            s.append("const ");
        }

        if (isPrimitive()) {
            s.append("#");
        }

        if (isEnumeration()) {
            s.append("enum ");
        }

        s.append(getName());

        IType[] types = getTypes();
        if (types.length > 0) {
            s.append("<");
            s.append(types[0].toString());
            for (int i=1; i<types.length; i++) {
                s.append(", ");
                s.append(types[i].toString());
            }
            s.append(">");
        }

        if (isReference()) {
            s.append("&");
        }

        for (int i=0; i<getPointer(); i++) {
            s.append("*");
        }

        for (int i = 0; i < getDimension(); i++) {
            s.append("[]");
        }

        if (alias != null) {
            s.append("{"+name+"}");
        }

        return s.toString();
    }

    /**
     * returns true is the two types are equivalent, that is after all aliasing
     * has been done
     */
    public boolean equals(Object o) {
       if (o instanceof IType) {
          IType other = (IType) o;
          if (!getName().equals(other.getName())) return false;
          if (getDimension() != other.getDimension()) return false;
          if (isReference() != other.isReference()) return false;
          else return getPointer() == other.getPointer();
       }
       return false;
    }

}

