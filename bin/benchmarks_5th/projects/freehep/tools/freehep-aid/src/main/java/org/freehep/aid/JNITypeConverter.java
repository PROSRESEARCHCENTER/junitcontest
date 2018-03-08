// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.freehep.rtti.IType;

/**
 * @author Mark Donszelmann
 * @version $Id: JNITypeConverter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class JNITypeConverter extends CPPTypeConverter {

    protected Map types = new HashMap();
    protected String cr;
    protected Properties jniProperties = new Properties();

    public JNITypeConverter(String propDir, String indent, String cr) {
        super(propDir);

        this.cr = cr;

        String name = "aid.jni.cpp.properties";
        try {
            jniProperties.load(getClass().getResourceAsStream(name));
        } catch (IOException ioe) {
            System.err.println(getClass().getName()+": Unable to load property file "+name);
        }

        types.put("Color",          new ColorToDoubleVector(indent, cr));
        types.put("Collection",     new ObjectCollectionToObjectVector(indent, cr));
        types.put("Object[]",       new ObjectArrayToObjectVector(indent, cr));
        types.put("String",         new StringToString(indent, cr));
        types.put("primitive",      new PrimitiveToPrimitive(indent, cr));
        types.put("primitive[]",    new PrimitiveArrayToPrimitiveVector(indent, cr));
    }

    public String convertToJava(int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        JNITypeConversion conversion = getConversion(type, dimension);
        if (conversion != null) {
            return conversion.convertToJava(this, scope, type, dimension, nameSpace, src, dst);
        } else {
            return "// WARNING no conversion for "+type+cr;
        }
    }

    public String convertToCPP(int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        JNITypeConversion conversion = getConversion(type, dimension);
        if (conversion != null) {
            return conversion.convertToCPP(this, scope, type, dimension, nameSpace, src, dst);
        } else {
            return "// WARNING no conversion for "+type+cr;
        }
    }

    protected JNITypeConversion getConversion(IType type, int dimension) {

        String typeName = type.getName();
        switch (dimension) {
            case 0:
                if (type.isPrimitive()) {
                    return (JNITypeConversion)types.get("primitive");
                } else {
                    return (JNITypeConversion)types.get(typeName);
                }

            case 1:
                if (type.isPrimitive()) {
                    return (JNITypeConversion)types.get("primitive[]");
                } else {
                    return (JNITypeConversion)types.get("Object[]");
                }

            default:
                return (JNITypeConversion)types.get("Object[]");
        }
    }

    public String jniCall(IType type) {
        return jniCall(type, type.getDimension());
    }

    public String jniCall(IType type, int dimension) {
        if (type.isPrimitive() && (dimension == 0)) {
            return Character.toUpperCase(type.getName().charAt(0))+type.getName().substring(1);
        }
        return "Object";
    }

    public String jniType(IType type) {
        return jniType(type, type.getDimension());
    }

    public String jniType(IType type, int dimension) {
        String jniType = jniProperties.getProperty(type.getName(), "jobject");
        switch (dimension) {
            case 0: return jniType;
            case 1: return jniType.equals("jstring") ? "jobjectArray" : jniType+"Array";
            default: return "jobjectArray";
        }
    }
}
