// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import org.freehep.rtti.IType;

/**
 * @author Mark Donszelmann
 * @version $Id: PrimitiveArrayToPrimitiveVector.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PrimitiveArrayToPrimitiveVector extends JNITypeConversion {

    public PrimitiveArrayToPrimitiveVector(String indent, String cr) {
        super(indent, cr);
    }

    public String convertToJava(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        String primitive = type.getName();
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// convert vector<");
        s.append(primitive);
        s.append("> to ");
        s.append(primitive);
        s.append("[]");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(" = env->New");
        s.append(converter.jniCall(type, dimension-1));
        s.append("Array(");
        s.append(src);
        s.append(".size());");
        s.append(cr);

        s.append(indent(scope));
        s.append("for (int i");
        s.append(scope);
        s.append("=0; i");
        s.append(scope);
        s.append("<");
        s.append(src);
        s.append(".size(); i");
        s.append(scope);
        s.append("++) {");
        s.append(cr);

        s.append(indent(scope+1));
        s.append(converter.jniType(type, dimension-1));
        s.append(" buf");
        s.append(scope+1);
        s.append(" = ");
        s.append(src);
        s.append("[i");
        s.append(scope);
        s.append("];");
        s.append(cr);

        s.append(indent(scope+1));
        s.append("env->Set");
        s.append(converter.jniCall(type, dimension-1));
        s.append("ArrayRegion(");
        s.append(dst);
        s.append(", i");
        s.append(scope);
        s.append(", 1, &buf");
        s.append(scope+1);
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("}");
        s.append(cr);

        return s.toString();
    }

    public String convertToCPP(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        String primitive = type.getName();
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// convert ");
        s.append(primitive);
        s.append("[] to vector<");
        s.append(primitive);
        s.append(">");
        s.append(cr);

        s.append(indent(scope));
        s.append("unsigned int len");
        s.append(scope);
        s.append(" = env->GetArrayLength(");
        s.append(src);
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("for (unsigned int i");
        s.append(scope);
        s.append("=0; i");
        s.append(scope);
        s.append("<len");
        s.append(scope);
        s.append("; i");
        s.append(scope);
        s.append("++) {");
        s.append(cr);

        s.append(indent(scope+1));
        s.append("jdouble d");
        s.append(scope+1);
        s.append(";");
        s.append(cr);

        s.append(indent(scope+1));
        s.append("env->Get");
        s.append(Character.toUpperCase(primitive.charAt(0))+primitive.substring(1));
        s.append("ArrayRegion(");
        s.append(src);
        s.append(", i");
        s.append(scope);
        s.append(", 1, &d");
        s.append(scope+1);
        s.append(");");
        s.append(cr);

        s.append(indent(scope+1));
        s.append(dst);
        s.append(".push_back(d");
        s.append(scope+1);
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("}");
        s.append(cr);

        return s.toString();
    }
}