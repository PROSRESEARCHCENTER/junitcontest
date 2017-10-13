// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import org.freehep.rtti.IMethod;
import org.freehep.rtti.IType;
import org.freehep.util.UserProperties;

/**
 * @author Mark Donszelmann
 * @version $Id: CPPTypeConverter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class CPPTypeConverter {

    protected UserProperties typeProperties = new UserProperties();
    protected UserProperties includeProperties = new UserProperties();
    protected UserProperties importProperties = new UserProperties();

    public CPPTypeConverter(String propDir) {
        AidUtil.loadProperties(typeProperties, getClass(), propDir, "aid.types.cpp.properties");
        AidUtil.loadProperties(includeProperties, getClass(), propDir, "aid.includes.cpp.properties");
        AidUtil.loadProperties(importProperties, getClass(), propDir, "aid.imports.java.properties");
    }

    public String namespace(String packageName) {
        String namespace = packageName;
        if (!namespace.equals("")) namespace = typeProperties.getProperty(namespace, namespace);
        namespace = namespace.replaceAll("\\.","::");
        return namespace;
    }

    public String name(String name) {
        return typeProperties.getProperty(name, name);
    }

    public String qualifiedName(String name, String nameSpace) {
        String qualifiedName = typeProperties.getProperty(name, name);
        int colon = qualifiedName.lastIndexOf("::");
        if (colon >= 0) {
            String targetNameSpace = qualifiedName.substring(0, colon);
            if (targetNameSpace.equals(nameSpace)) {
                qualifiedName = qualifiedName.substring(colon+2);
            }
        }
        return qualifiedName;
    }

    protected String type(IType type, String nameSpace) {
        StringBuffer s = new StringBuffer();
        if (type.isConst()) s.append("const ");
        s.append(basicType(type, nameSpace));
        if (type.isReference()) s.append(" &");
        for (int i=0; i<type.getPointer(); i++) {
            s.append(" *");
        }
        return s.toString();
    }

    protected String basicType(IType type, String nameSpace) {
        return basicType(type, type.getDimension(), nameSpace);
    }

    protected String basicType(IType type, int dimension, String nameSpace) {
        IType[] types = type.getTypes();
        StringBuffer s = new StringBuffer();
        for (int i=0; i<dimension; i++) s.append("std::vector<");
        String typeName = type.getName();
        if (typeName.equals("[]")) {
            s.append(type(types[0], nameSpace));
        } else {
            s.append(qualifiedName(typeName, nameSpace));
            if (types.length > 0) {
                s.append("<");
                s.append(type(types[0], nameSpace));
                for (int i=1; i<types.length; i++) {
                    s.append(", ");
                    s.append(type(types[i], nameSpace));
                }
                s.append("> ");
            }
        }
        for (int i=0; i<dimension; i++) s.append("> ");
        return s.toString();
    }

    public String getSignature(IType type, int dimension, String packageName) {
        if (dimension == 0) {
            String s = type.getSignature(packageName, importProperties);
            s = s.substring(type.getDimension()+1, s.length()-1);
            s = s.replace('/', '.');
            return s;
        }
        return "java.lang.Object";
    }

    public String getSignature(IMethod method, String packageName) {
        String s = method.getSignature(packageName, importProperties);
        s = s.replace('(','O');
        s = s.replace(')','C');
        s = s.replace(';','E');
        s = s.replace('[','A');
        s = s.replace('/','_');
        s = s.replace('$','S');
        return s;
    }

}
