// Copyright 2002, SLAC, Stanford University, U.S.A.
package org.freehep.aid;

import org.freehep.rtti.IType;

/**
 * @author Mark Donszelmann
 * @version $Id: ObjectCollectionToObjectVector.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ObjectCollectionToObjectVector extends JNITypeConversion {

    public ObjectCollectionToObjectVector(String indent, String cr) {
        super(indent, cr);
    }

    public String convertToJava(JNITypeConverter converter, int scope, IType type, int dimension, String nameSpace, String src, String dst) {
        StringBuffer s = new StringBuffer();

        s.append(indent(scope));
        s.append("// convert vector<object> to Collection<Object>");
        s.append(cr);

        s.append(indent(scope));
        s.append("jclass cls");
        s.append(scope);
        s.append(" = env->FindClass(\"java.util.Vector\");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jmethodID constructor");
        s.append(scope);
        s.append(" = env->GetMethodID(cls");
        s.append(scope);
        s.append(", \"<init>\", \"()V\");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jmethodID addMethod");
        s.append(scope);
        s.append(" = env->GetMethodID(cls");
        s.append(scope);
        s.append(", \"add\", \"(Ljava/lang/Object;)Z\");");
        s.append(cr);

        s.append(indent(scope));
        s.append(dst);
        s.append(" = env->NewObject(cls");
        s.append(scope);
        s.append(", constructor");
        s.append(scope);
        s.append(");");
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
        s.append("jobject v");
        s.append(scope+1);
        s.append(";");
        s.append(cr);

        s.append(converter.convertToJava(scope+1, type.getTypes()[0],
                                         0, nameSpace, src+"[i"+scope+"]", "v"+(scope+1)));

        s.append(indent(scope+1));
        s.append("env->CallBooleanMethod(");
        s.append(dst);
        s.append(", addMethod");
        s.append(scope);
        s.append(", ");
        s.append("v");
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

        type = type.getTypes()[0];

        s.append(indent(scope));
        s.append("// convert Collection<Object> to vector<object>");
        s.append(cr);

        s.append(indent(scope));
        s.append("jclass collectionClass");
        s.append(scope);
        s.append(" = env->GetObjectClass(");
        s.append(src);
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jmethodID iteratorMethod");
        s.append(scope);
        s.append(" = env->GetMethodID(collectionClass");
        s.append(scope);
        s.append(", \"iterator\", \"()Ljava.util.Iterator;\");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jobject iterator");
        s.append(scope);
        s.append(" = (jobject)env->CallObjectMethod(");
        s.append(src);
        s.append(", iteratorMethod");
        s.append(scope);
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jclass iteratorClass");
        s.append(scope);
        s.append(" = env->GetObjectClass(iterator");
        s.append(scope);
        s.append(");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jmethodID hasNextMethod");
        s.append(scope);
        s.append(" = env->GetMethodID(iteratorClass");
        s.append(scope);
        s.append(", \"hasNext\", \"()Z\");");
        s.append(cr);

        s.append(indent(scope));
        s.append("jmethodID nextMethod");
        s.append(scope);
        s.append(" = env->GetMethodID(iteratorClass");
        s.append(scope);
        s.append(", \"next\", \"()Ljava.lang.Object;\");");
        s.append(cr);

        s.append(indent(scope));
        s.append("while (env->CallBooleanMethod(iterator");
        s.append(scope);
        s.append(", hasNextMethod");
        s.append(scope);
        s.append(")) {");
        s.append(cr);

        s.append(indent(scope+1));
        s.append(converter.jniType(type, 0));
        s.append(" o");
        s.append(scope+1);
        s.append(" = (");
        s.append(converter.jniType(type, 0));
        s.append(")env->CallObjectMethod(iterator");
        s.append(scope);
        s.append(", nextMethod");
        s.append(scope);
        s.append(");");
        s.append(cr);

        s.append(indent(scope+1));
        s.append(converter.basicType(type, 0, nameSpace));
        s.append(" d");
        s.append(scope+1);
        s.append(";");
        s.append(cr);

        s.append(converter.convertToCPP(scope+1, type, 0, nameSpace, "o"+(scope+1), "d"+(scope+1)));

        s.append(indent(scope+1));
        s.append(dst);
        s.append(".push_back(d");
        s.append(scope+1);
        s.append(");");
        s.append(cr);

//        s.append(freeStringToCharStar(scope+1, "s", "c"));

        s.append(indent(scope));
        s.append("}");
        s.append(cr);

        return s.toString();
    }
}