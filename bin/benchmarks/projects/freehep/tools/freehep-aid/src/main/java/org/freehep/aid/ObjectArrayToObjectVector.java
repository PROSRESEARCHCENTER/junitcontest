// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import org.freehep.rtti.IType;

/**
 * @author Mark Donszelmann
 * @version $Id: ObjectArrayToObjectVector.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ObjectArrayToObjectVector extends JNITypeConversion {

    public ObjectArrayToObjectVector(String indent, String cr) {
        super(indent, cr);
    }

    public String convertToJava(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// convert vector<objects> into Objects[]");
        s.append(cr);

        s.append(indent(scope));
        s.append("jclass cls");
        s.append(scope);
        s.append(" = env->FindClass(\"");
        // FIXME, we need the package name here, via the RTTI?
        s.append(converter.getSignature(type, dimension-1, "dummypackagename"));
        s.append("\");");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(" = env->NewObjectArray(");
        s.append(src);
        s.append(".size(), cls");
        s.append(scope);
        s.append(", NULL);");
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
        s.append(" v");
        s.append(scope+1);
        s.append(";");
        s.append(cr);

        s.append(converter.convertToJava(scope+1, type,
                                         dimension-1, nameSpace, src+"[i"+scope+"]", "v"+(scope+1)));

        s.append(indent(scope+1));
        s.append("env->SetObjectArrayElement(");
        s.append(dst);
        s.append(", i");
        s.append(scope);
        s.append(", v");
        s.append(scope+1);
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("}");
        s.append(cr);

        return s.toString();
    }

    public String convertToCPP(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// convert Objects[] into vector<objects>");
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
        s.append(converter.jniType(type, dimension-1));
        s.append(" o");
        s.append(scope+1);
        s.append(" = (");
        s.append(converter.jniType(type, dimension-1));
        s.append(")env->GetObjectArrayElement(");
        s.append(src);
        s.append(", i");
        s.append(scope);
        s.append(");");
        s.append(cr);

        s.append(indent(scope+1));
        s.append(converter.basicType(type, dimension-1, nameSpace));
        s.append(" d");
        s.append(scope+1);
        s.append(";");
        s.append(cr);

        s.append(converter.convertToCPP(scope+1, type, dimension-1, nameSpace, "o"+(scope+1), "d"+(scope+1)));

        s.append(indent(scope+1));
        s.append(dst);
        s.append(".push_back(d");
        s.append(scope+1);
        s.append(");");
        s.append(cr);

//        post.append(freeStringToCharStar(2, "s", "c"));

        s.append(indent);
        s.append("}");
        s.append(cr);

        return s.toString();
    }
}