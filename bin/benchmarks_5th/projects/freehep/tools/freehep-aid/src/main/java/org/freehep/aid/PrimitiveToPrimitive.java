// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import org.freehep.rtti.IType;

/**
 * @author Mark Donszelmann
 * @version $Id: PrimitiveToPrimitive.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PrimitiveToPrimitive extends JNITypeConversion {

    public PrimitiveToPrimitive(String indent, String cr) {
        super(indent, cr);
    }

    public String convertToJava(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append(dst);
        s.append(" = ");
        s.append(src);
        s.append(";");
        s.append(cr);

        return s.toString();
    }

    public String convertToCPP(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append(dst);
        s.append(" = ");
        s.append(src);
        s.append(";");
        s.append(cr);

        return s.toString();
    }
}