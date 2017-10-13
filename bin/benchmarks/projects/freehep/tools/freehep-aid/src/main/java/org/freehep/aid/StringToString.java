// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import org.freehep.rtti.IType;

/**
 * @author Mark Donszelmann
 * @version $Id: StringToString.java 8584 2006-08-10 23:06:37Z duns $
 */
public class StringToString extends JNITypeConversion {

    public StringToString(String indent, String cr) {
        super(indent, cr);
    }

    public String convertToJava(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// converting string to String");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(" = env->NewStringUTF(");
        s.append(src);
        s.append(".c_str());");
        s.append(cr);

        return s.toString();
    }

    public String cleanJava(int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// free String");
        s.append(cr);

        s.append(indent(scope));
        s.append("env->DeleteLocalRef(");
        s.append(dst);
        s.append(");");
        s.append(cr);

        return s.toString();
    }

    public String convertToCPP(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// converting String to string");
        s.append(cr);

        s.append(indent(scope));
        s.append("jboolean isCopy");
        s.append(scope);
        s.append(";");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(" = env->GetStringUTFChars(");
        s.append(src);
        s.append(", &isCopy");
        s.append(scope);
        s.append(");");
        s.append(cr);

        return s.toString();
    }

    public String cleanCPP(int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// free string");
        s.append(cr);

        s.append(indent(scope));
        s.append("if (isCopy");
        s.append(scope);
        s.append(" == JNI_TRUE) {");
        s.append(cr);

        s.append(indent(scope+1));
        s.append("env->ReleaseStringUTFChars(");
        s.append(src);
        s.append(", ");
        s.append(dst);
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("}");
        s.append(cr);

        return s.toString();
    }
}