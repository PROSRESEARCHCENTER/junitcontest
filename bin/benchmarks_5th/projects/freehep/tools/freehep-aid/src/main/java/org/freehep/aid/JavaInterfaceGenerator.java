// Copyright 2002-2005, FreeHEP.
package org.freehep.aid;

import org.freehep.rtti.IClass;
import org.freehep.rtti.IField;
import org.freehep.rtti.IMethod;
import org.freehep.rtti.INamedType;
import org.freehep.rtti.IType;
import org.freehep.util.io.IndentPrintWriter;

/**
 * @author Mark Donszelmann
 * @version $Id: JavaInterfaceGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class JavaInterfaceGenerator extends AbstractJavaGenerator {

    public JavaInterfaceGenerator(String propDir) {
        super(propDir);

        properties.setProperty("java.interface", "true");
    }

    public String filename(IClass clazz) {
        return clazz.getName()+".java";
    }

    protected void printClassHeader(IndentPrintWriter out, IClass clazz) {
        out.println();
        String[] comments = clazz.getComments(language);
        for (int i=0; i<comments.length; i++) {
            out.println(comments[i]);
        }

        String[] templateParameters = clazz.getTemplateParameters();
        String[] concrete = getConcrete(clazz);

        out.print("public ");
        if (clazz.isClass() || (concrete != null)) out.print("abstract ");
        out.print(clazz.isClass() ? "class " : "interface ");
        out.print(clazz.getName());
        
        if (templateParameters.length > 0) {
            if (concrete == null) {            
                out.print("<");
                out.print(templateParameters[0]);
                for (int i=1; i<templateParameters.length; i++) {
                    out.print(", ");
                    out.print(templateParameters[i]);
                }
                out.print("> ");
            }
        }

        String[] interfaces = clazz.getInterfaces();
        int k = 0;
        for (int i=0; i<interfaces.length; i++) {
            String javaInterface = typeProperties.getProperty(interfaces[i], interfaces[i]);
            if (!javaInterface.equals("")) {
                if (k == 0) {
                    out.print(clazz.isClass() ? " implements " : " extends ");
                } else {
                    out.print(", ");
                }
                out.print(javaInterface);
                k++;
            }
        }

        out.println(" {");
    }

    protected void printEOCComments(IndentPrintWriter out, IClass clazz) {
        String[] eocComments = clazz.getEOCComments(language);
        if (eocComments.length > 0) {
            out.println();
            for (int i=0; i<eocComments.length; i++) {
                out.println(eocComments[i]);
            }
        }
    }

    protected void printEOPComments(IndentPrintWriter out, IClass clazz) {
        String[] eopComments = clazz.getEOPComments(language);
        if (eopComments.length > 0) {
            out.println();
            for (int i=0; i<eopComments.length; i++) {
                out.println(eopComments[i]);
            }
            out.println();
        }
    }

    protected void printEOFComments(IndentPrintWriter out, IClass clazz) {
        String[] eofComments = clazz.getEOFComments(language);
        if (eofComments.length > 0) {
            out.println();
            for (int i=0; i<eofComments.length; i++) {
                out.println(eofComments[i]);
            }
        }
    }

    protected void printMethodComments(IndentPrintWriter out, IMethod method, int noOfParameters) {
        String[] comments = method.getComments(language);
        int param = 0;
        for (int i=0; i<comments.length; i++) {
            // adjust comments for number of parameters
            if (comments[i].indexOf("@param") >= 0) {
                if (param < noOfParameters) {
                    param++;
                    out.println(comments[i]);
                }
            } else {
                out.println(comments[i]);
            }
        }
    }

    protected void printField(IndentPrintWriter pw, IField field, boolean innerClass) {
        IType type = field.getNamedType().getType();

        String[] comments = field.getComments(language);
        for (int i=0; i<comments.length; i++) {
            pw.println(comments[i]);
        }

        if (type.isEnumeration() && (!type.getName().equals(""))) {
            pw.print("    public ");
            if (innerClass) pw.print("static ");
            pw.println("interface "+type.getName()+" {");
            pw.indent();
        }

        pw.print("    public ");
        if (type.isConst()) {
            pw.print("final static ");
        }
        pw.print(type(type, null, innerClass && type.isEnumeration()));

        int enumInit = 0;
        pw.print(" ");
        while (field != null) {
            INamedType namedType = field.getNamedType();

            pw.print(namedType.getName());
            String init = namedType.getInit();
            if (init != null) init = valueProperties.getProperty(init, init);
            if ((init != null) && !init.equals("")) {
                pw.print(" = ");
                pw.print(init);
                if (type.isEnumeration()) {
                    enumInit = Integer.decode(init).intValue();
                    enumInit++;
                }
            } else if (type.isEnumeration()) {
                pw.print(" = ");
                pw.print(enumInit);
                enumInit++;
            }

            field = field.getNext();
            if (field != null) {
                pw.print(", ");
            }
        }
        pw.println(";");

        if (type.isEnumeration() && (!type.getName().equals(""))) {
            pw.outdent();
            pw.println("    } // "+type.getName());
        }
    }
}

