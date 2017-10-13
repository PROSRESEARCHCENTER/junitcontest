// Copyright 2002-2005, FreeHEP.
package org.freehep.aid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import org.freehep.rtti.IClass;
import org.freehep.rtti.IConstructor;
import org.freehep.rtti.IField;
import org.freehep.rtti.IMethod;
import org.freehep.rtti.INamedType;
import org.freehep.util.io.IndentPrintWriter;

/**
 * @author Mark Donszelmann
 * @version $Id: JNIHeaderGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class JNIHeaderGenerator extends AbstractCPPHeaderGenerator {

    public JNIHeaderGenerator(String propDir) {
        super(propDir);

        properties.setProperty("jni", "true");
        properties.setProperty("jni.header", "true");
    }

    protected String prefix() {
        return "J";
    }

    public String filename(IClass clazz) {
        return prefix() + clazz.getName() + ".h";
    }

    protected void includeStatements(IClass clazz, SortedSet sysIncludes, SortedSet includes, String namespace, SortedSet types) {
        sysIncludes.add("jni.h");


        // add interface name we are implementing
        String iface = converter.qualifiedName(clazz.getName(), prefix()+namespace(clazz));
        includes.add(includeProperties.getProperty(iface, iface));

        String interfaces[] = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            includes.add(prefix() + interfaces[i]+".h");
        }

        IConstructor constructors[] = clazz.getConstructors();
        for (int c = 0; c < constructors.length; c++) {
            INamedType parameterTypes[] = constructors[c].getParameterTypes();
            for (int p = 0; p < parameterTypes.length; p++) {
                includeFrom(parameterTypes[p].getType(), clazz.getName(), sysIncludes, includes, prefix()+namespace, types);
            }

        }

        super.includeStatements(clazz, sysIncludes, includes, prefix()+namespace, types);

        // add AID stuff
        if (interfaces.length == 0) {
            includes.add("JAID/JAIDRef.h");
        }
    }

    protected void printClassHeader(IndentPrintWriter out, IClass clazz) {
        out.print("class " + prefix() + clazz.getName() + ": ");

        String interfaces[] = clazz.getInterfaces();
        if (interfaces.length == 0) {
            out.print("public JAID::JAIDRef");
        } else {
            int k = 0;
            for (int i = 0; i < interfaces.length; i++) {
                if (k > 0) out.print(", ");
                out.print("public ");
                out.print(prefix() + interfaces[i]);
                k++;
            }
        }

        out.print(", public virtual ");
        out.print(converter.qualifiedName(clazz.getName(), prefix()+namespace(clazz)));
        out.println(" {");
    }

    protected void printPrivate(IndentPrintWriter out, IClass clazz) {
        Map methods = new HashMap();
        for (int i=0; i<clazz.getMethods().length; i++) {
            IMethod m = clazz.getMethods()[i];
            String name = m.getName()+converter.getSignature(m, clazz.getPackageName());
            methods.put(name, m);
        }
        if (!methods.isEmpty()) {
            out.println();
            out.println("private: ");
        }
        for (Iterator i = methods.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
//            IMethod method = (IMethod)entry.getValue();
            out.print("    jmethodID " + entry.getKey() + "Method");
            out.println(";");
/* FIXME later
            IType type = method.getReturnType();
            if (!type.isPrimitive() && !type.isVoid()) {
                if (type.isReference() || (type.getPointer() > 0)) {
                    out.print("    "+converter.basicType(type, namespace(clazz))+" "+name+"Result");
                    out.println(";");
                }
            }
*/
        }
    }

    protected void printProtected(IndentPrintWriter out, IClass clazz) {
        String name = prefix() + clazz.getName();
        out.println();
        out.println("protected:");
        out.println("    inline " + name + "() { };");
        out.println("    inline " + name + "(const " + name + "& r) { };");
        out.println("    inline " + name + "& operator=(const " + name + "&) { return *this; };");
    }

    protected void printPublic(IndentPrintWriter out, IClass clazz) {
        out.println();
        out.println("public: ");
        out.println("    /**");
        out.println("     * Default JNI Constructor");
        out.println("     */");
        out.println("    " + prefix() + clazz.getName() + "(JNIEnv *env, jobject object);");
        out.println();
        out.println("    /// Destructor.");
        out.println("    virtual ~" + prefix() + clazz.getName() + "();");
    }

    protected void printConstructor(IndentPrintWriter out, IConstructor constructor, String nameSpace) {
        String comments[] = constructor.getComments("cpp");
        for (int i = 0; i < comments.length; i++) {
            out.print(comments[i]);
        }

        out.print("    ");
        out.print(prefix() + constructor.getName());
        out.print("(");
        INamedType parameterTypes[] = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            out.print(namedType(parameterTypes[i], nameSpace));
            if (i < parameterTypes.length - 1) {
                out.print(", ");
            }
        }

        out.print(");");
    }

    protected void printMethod(IndentPrintWriter out, IMethod method, String nameSpace) {
        super.printMethod(out, method, prefix()+nameSpace);
        out.println(";");
    }

    protected void printField(IndentPrintWriter out, IField ifield, String nameSpace) {
    }

    protected boolean printDefines(IndentPrintWriter out, IClass clazz) {
        return true;
    }
}
