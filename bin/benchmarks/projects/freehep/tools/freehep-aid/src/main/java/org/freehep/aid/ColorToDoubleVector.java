// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import org.freehep.rtti.IType;

/**
 * @author Mark Donszelmann
 * @version $Id: ColorToDoubleVector.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ColorToDoubleVector extends JNITypeConversion {

    public ColorToDoubleVector(String indent, String cr) {
        super(indent, cr);
    }

    public String convertToJava(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// convert vector<double> to Color");
        s.append(cr);

        s.append(indent(scope));
        s.append("jfloat alpha");
        s.append(scope);
        s.append(" = color[0];");
        s.append(cr);

        s.append(indent(scope));
        s.append("jfloat red");
        s.append(scope);
        s.append(" = color[1];");
        s.append(cr);

        s.append(indent(scope));
        s.append("jfloat green");
        s.append(scope);
        s.append(" = color[2];");
        s.append(cr);

        s.append(indent(scope));
        s.append("jfloat blue");
        s.append(scope);
        s.append(" = color[3];");
        s.append(cr);

        s.append(indent(scope));
        s.append("jclass colorClass");
        s.append(scope);
        s.append(" = env->FindClass(\"java.awt.Color\");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jmethodID constructor");
        s.append(scope);
        s.append(" = env->GetMethodID(colorClass");
        s.append(scope);
        s.append(", \"<init>\", \"(FFFF)V\");");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(" = env->NewObject(colorClass");
        s.append(scope);
        s.append(", constructor");
        s.append(scope);
        s.append(", red");
        s.append(scope);
        s.append(", green");
        s.append(scope);
        s.append(", blue");
        s.append(scope);
        s.append(", alpha");
        s.append(scope);
        s.append(");");
        s.append(cr);

        return s.toString();
    }

    public String convertToCPP(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// convert Color to vector<double>");
        s.append(cr);

        s.append(indent(scope));
        s.append("jclass colorClass");
        s.append(scope);
        s.append(" = env->GetObjectClass(");
        s.append(src);
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jmethodID getRGBComponentsMethod");
        s.append(scope);
        s.append(" = env->GetMethodID(colorClass");
        s.append(scope);
        s.append(", \"getRGBComponents\", \"([F)[F\");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jfloatArray o");
        s.append(scope);
        s.append(" = (jfloatArray)env->CallObjectMethod(");
        s.append(src);
        s.append(", getRGBComponentsMethod");
        s.append(scope);
        s.append(", NULL");
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("float* c");
        s.append(scope);
        s.append(" = env->GetFloatArrayElements(o");
        s.append(scope);
        s.append(", NULL);");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(".push_back(*c");
        s.append(scope);
        s.append("++);");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(".push_back(*c");
        s.append(scope);
        s.append("++);");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(".push_back(*c");
        s.append(scope);
        s.append("++);");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(".push_back(*c");
        s.append(scope);
        s.append("++);");
        s.append(cr);

        return s.toString();
    }
}
