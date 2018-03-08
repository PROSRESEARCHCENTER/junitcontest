// Copyright 2002-2005, FreeHEP.
package org.freehep.aid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import org.freehep.rtti.IClass;
import org.freehep.rtti.IConstructor;
import org.freehep.rtti.IMethod;
import org.freehep.rtti.INamedType;
import org.freehep.rtti.IType;
import org.freehep.util.UserProperties;
import org.freehep.util.io.IndentPrintWriter;

/**
 * @author Mark Donszelmann
 * @version $Id: JNICodeGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class JNICodeGenerator extends AbstractCPPGenerator {

    public final static String indent = "    ";
    public final static String cr = "\n";

    protected UserProperties jniProperties = new UserProperties();
    protected UserProperties importProperties = new UserProperties();
    protected JNITypeConverter converter;

    public JNICodeGenerator(String propDir) {
        super(propDir);

        properties.setProperty("jni", true);
        properties.setProperty("jni.code", true);
        AidUtil.loadProperties(jniProperties, getClass(), propDir, "aid.jni.cpp.properties");
        AidUtil.loadProperties(importProperties, getClass(), propDir, "aid.imports.java.properties");
        converter = new JNITypeConverter(propDir, indent, cr);
    }

    protected String prefix() {
        return "J";
    }

    public String filename(IClass clazz) {
        return prefix() + clazz.getName() + ".cpp";
    }

    public boolean print(File file, IClass clazz) throws IOException {
        IndentPrintWriter out = new IndentPrintWriter(new PrintWriter(new BufferedWriter(new FileWriter(file))));
        out.setIndentString("    ");
        warning(out);
        printIncludeStatements(out, clazz);
        if (!namespace(clazz).equals("")) {
            out.println();
            out.println("using namespace " + namespace(clazz) + ";");
            out.println("using namespace " + prefix()+namespace(clazz) + ";");
        }

        out.println();

        printJNIConstructor(out, clazz);

        printDestructor(out, clazz);

        IMethod methods[] = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            printMethod(out, clazz, methods[i]);
        }
        out.close();
        
        return false;
    }

    protected void includeStatements(IndentPrintWriter out, IClass clazz, SortedSet sysIncludes, SortedSet includes, String namespace, SortedSet types) {
        sysIncludes.add("cstdlib");
        sysIncludes.add("iostream");

        includes.add(prefix() + clazz.getName()+".h");

        String[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            includes.add(prefix()+interfaces[i]+".h");
        }

        IConstructor constructors[] = clazz.getConstructors();
        for (int c = 0; c < constructors.length; c++) {
            INamedType parameterTypes[] = constructors[c].getParameterTypes();
            for (int p = 0; p < parameterTypes.length; p++) {
                includeFrom(parameterTypes[p].getType(), null, sysIncludes, includes, prefix()+namespace, includes);
            }

        }

        IMethod methods[] = clazz.getMethods();
        for (int m = 0; m < methods.length; m++) {
            includeFrom(methods[m].getReturnType(), null, sysIncludes, includes, prefix()+namespace, includes);
            INamedType parameterTypes[] = methods[m].getParameterTypes();
            for (int p = 0; p < parameterTypes.length; p++) {
                includeFrom(parameterTypes[p].getType(), null, sysIncludes, includes, prefix()+namespace, includes);
            }
        }

    }

    protected void printJNIConstructor(IndentPrintWriter out, IClass clazz) {
        out.println();
        out.print(prefix() + clazz.getName());
        out.print("::");
        out.print(prefix() + clazz.getName());
        out.print("(JNIEnv *env, jobject object)");
        String interfaces[] = clazz.getInterfaces();
        int k = 0;
        out.println();
        out.print ((interfaces.length == 0) ? "        : JAID::JAIDRef(env, object)" : "        : ");

        for (int i = 0; i < interfaces.length; i++) {
            if (k > 0) {
                out.print(", ");
            }
            k++;
            out.print(prefix() + interfaces[i]);
            out.print("(env, object)");
        }
        out.println(" {");

        out.print(indent);
        out.print("jclass cls = env->GetObjectClass(getRef());");
        out.print(cr);
        out.print(cr);

        String packageName = clazz.getPackageName();
        Map methods = new HashMap();
        for (int i=0; i<clazz.getMethods().length; i++) {
            IMethod m = clazz.getMethods()[i];
            String name = m.getName()+converter.getSignature(m, clazz.getPackageName());
            methods.put(name, m);
        }

        for (Iterator i = methods.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            IMethod method = (IMethod)entry.getValue();
            String methodID = (String)entry.getKey()+"Method";
            String methodName = "\""+method.getName()+"\"";
            String signature = "\""+method.getSignature(packageName, importProperties)+"\"";
            out.print(indent);
            out.print(methodID);
            out.print(" = env->GetMethodID(cls, ");
            out.print(methodName);
            out.print(", ");
            out.print(signature);
            out.print(");");
            out.print(cr);

            out.print(indent);
            out.print("if (");
            out.print(methodID);
            out.print(" == NULL) {");
            out.print(cr);

            out.print(indent+indent);
            out.print("std::cerr << ");
            out.print("\""+clazz.getName()+"\"");
            out.print(" << ");
            out.print("\": Could not find method: \"");
            out.print(" << ");
            out.print(methodName);
            out.print(" << ");
            out.print(signature);
            out.print(" << std::endl;");
            out.print(cr);

            out.print(indent);
            out.print("}");
            out.print(cr);

            out.print(cr);
        }

        out.println("}");
    }

    protected void printDestructor(IndentPrintWriter out, IClass clazz) {
        out.println();
        out.print(prefix() + clazz.getName());
        out.print("::");
        out.println("~" + prefix() + clazz.getName() + "() {");
        out.println("}");
    }

    protected void printMethod(IndentPrintWriter out, IClass clazz, IMethod method) {
        out.println();
        if ((method.getExceptionTypes().length > 0) && (method.getReturnType().isVoid())) {
            out.print("bool");
        } else {
            out.print(converter.type(method.getReturnType(), namespace(clazz)));
        }
        out.print(" ");
        out.print(prefix() + clazz.getName());
        out.print("::");
        out.print(method.getName());
        out.print("(");
        INamedType parameterTypes[] = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                out.print(", ");
            }
            out.print(namedType(parameterTypes[i], namespace(clazz)));
        }

        out.print(")");
        if (method.isConst()) {
            out.print(" const");
        }
        out.println(" {");
// FIXME, code inserted to generate dummy stubs
//        printMethodBody(out, clazz, method);
        printDummyBody(out, clazz, method);

        out.println("}");
    }

    // FIXME this is temporary until we can generate the code for these
    // methods. However, the return values are being generated as
    // well as we can do now...
    protected void printDummyBody(IndentPrintWriter out, IClass clazz, IMethod method) {
        out.println("   std::cerr << \"'"+prefix()+clazz.getName()+"."+method.getName()+"(..)' not implemented!\" << std::endl;");

        IType returnType = method.getReturnType();
        if (returnType.isVoid()) {
            if (method.getExceptionTypes().length > 0) {
                out.println("   return true;");
            }
            return;
        }

        if (returnType.getPointer() > 0) {
            out.println("   return NULL;");
            return;
        }
// handle arrays...
        if (returnType.getDimension() == 0) {

            if (returnType.getName().equals("boolean")) {
                out.println("   return false;");
                return;
            }

            if (returnType.getName().equals("int") || returnType.getName().equals("long")) {
                out.println("   return 0;");
                return;
            }

            if (returnType.getName().equals("double") || returnType.getName().equals("float")) {
                out.println("   return 0.0;");
                return;
            }
        }

        String btname = converter.basicType(returnType, "");
        out.println("   "+btname+" *r;");
        out.println("   return *r;");
    }

    protected void printMethodBody(IndentPrintWriter out, IClass clazz, IMethod method) {
        StringBuffer pre  = new StringBuffer();
        StringBuffer call = new StringBuffer();
        StringBuffer post = new StringBuffer();

        IType returnType = method.getReturnType();
        call.append(indent);
        call.append("// Call to Java");
        call.append(cr);

        call.append(indent);
        // analyze result
        if (!returnType.isVoid()) {
            pre.append(indent);
            pre.append(converter.basicType(returnType, namespace(clazz)));
            pre.append(" result;");
            pre.append(cr);

            call.append(converter.jniType(returnType, returnType.getDimension()));
            call.append(" jniResult = ");
            call.append("(");
            call.append(converter.jniType(returnType, returnType.getDimension()));
            call.append(")");

            post.append(converter.convertToCPP(1, returnType, returnType.getDimension(), namespace(clazz), "jniResult", "result"));

            // FIXME should move
//          s.append(freeStringToCharStar(1, src, "tmpResult"));

            if ((returnType.getPointer() > 0) || returnType.isReference()) {
                post.append(indent);
                post.append("// copying into instance variable");
                post.append(cr);

                String resultName = method.getName()+converter.getSignature(method,"")+"Result";
                post.append(indent);
                post.append("const_cast<");
                post.append(prefix()+clazz.getName());
                post.append("*>(this) -> ");
                post.append(resultName);
                post.append(" = result;");
                post.append(cr);

                post.append(indent);
                post.append("return ");
                post.append((returnType.getPointer() > 0) ? "&"+resultName : resultName);
                post.append(";");
                post.append(cr);
            } else {

                post.append(indent);
                post.append("return ");
                post.append("result");
                post.append(";");
                post.append(cr);
            }
        } else {
            // return is void, but exceptions could be thrown
            if (method.getExceptionTypes().length > 0) {
                post.append(indent);
                post.append("jthrowable e = env->ExceptionOccurred();");
                post.append(cr);

                post.append(indent);
                post.append("env->ExceptionClear();");
                post.append(cr);

                boolean returnInCaseOfException = valueProperties.isProperty("returnInCaseOfException", true);
                post.append(indent);
                post.append("return (e != NULL) ? ");
                post.append(returnInCaseOfException ? "true" : "false");
                post.append(" : ");
                post.append(returnInCaseOfException ? "false" : "true");
                post.append(";");
                post.append(cr);
            }
        }

        call.append("env->Call");
        call.append(converter.jniCall(returnType, returnType.getDimension()));
        call.append("Method");

        // fixed parameters
        call.append("(ref, ");
        call.append(method.getName()+converter.getSignature(method,"")+"Method");

        // parameters
        INamedType[] parameters = method.getParameterTypes();
        for (int i=0; i<parameters.length; i++) {
            INamedType parameter = parameters[i];
            IType type = parameter.getType();
            String jniName = "jni"+parameter.getName();

            pre.append(indent);
            pre.append(converter.jniType(type));
            pre.append(" ");
            pre.append(jniName);
            pre.append(";");
            pre.append(cr);

            pre.append(converter.convertToJava(1, type, type.getDimension(), namespace(clazz), parameter.getName(), jniName));

            call.append(", ");
            call.append(jniName);
        }

        // end call
        call.append(");");
        call.append(cr);

        // write
        if (pre.length() > 0)  out.print(pre);
        if (call.length() > 0) out.print(call);
        if (post.length() > 0) out.print(post);
    }
}