// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import org.freehep.rtti.IType;

/**
 * @author Mark Donszelmann
 * @version $Id: JNITypeConversion.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class JNITypeConversion {

    protected String indent;
    protected String cr;

    public JNITypeConversion(String indent, String cr) {
        this.indent = indent;
        this.cr = cr;
    }

    protected String indent(int scope) {
        StringBuffer s = new StringBuffer();
        for (int i=0; i<scope; i++) s.append(indent);
        return s.toString();
    }

    public abstract String convertToJava(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst);

    public String cleanJava(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        return "";
    }

    public abstract String convertToCPP(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst);

    public String cleanCPP(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        return "";
    }
}
