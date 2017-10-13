// Copyright 2002-2007, FreeHEP.
package org.freehep.aid;

import java.util.SortedSet;

import org.freehep.rtti.IClass;
import org.freehep.rtti.IField;
import org.freehep.rtti.IMethod;
import org.freehep.rtti.INamedType;
import org.freehep.rtti.IType;
import org.freehep.util.io.IndentPrintWriter;

/**
 * @author Mark Donszelmann
 * @version $Id: CPPHeaderGenerator.java 13274 2007-08-21 20:29:55Z duns $
 */
public class CPPHeaderGenerator extends AbstractCPPHeaderGenerator {


    public CPPHeaderGenerator(String propDir) {
        super(propDir);
    }

    public String directory(IClass clazz) {
        return namespace(clazz).replaceAll("::","_");
    }

    public String filename(IClass clazz) {
        return clazz.getName()+".h";
    }

    protected void includeStatements(IClass clazz, SortedSet sysIncludes, SortedSet includes, String namespace, SortedSet types) {
        String[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            for (int i=0; i<interfaces.length; i++) {
                // include all, rather than typing, therefore do NOT specify namespace
                includeFrom(prefix()+interfaces[i], prefix()+clazz.getName(), sysIncludes, includes, "", includes);
            }
        }

        IField[] fields = clazz.getFields();
        for (int i=0; i<fields.length; i++) {
            IType type = fields[i].getNamedType().getType();
            if (!type.isConst()) {
                // forward these or sysinclude
                includeFrom(type, prefix()+clazz.getName(), sysIncludes, includes, namespace, types);
            }
        }

        super.includeStatements(clazz, sysIncludes, includes, namespace, types);
    }

    protected void printClassHeader(IndentPrintWriter out, IClass clazz) {
        boolean useVirtualInheritance = properties.isProperty("useVirtualInheritance", true);        
        
        String[] templateParameters = clazz.getTemplateParameters();
        if (templateParameters.length > 0) {
            out.print("template <class ");
            out.print(templateParameters[0]);
            for (int i=1; i<templateParameters.length; i++) {
                out.print(", class ");
                out.print(templateParameters[i]);
            }
            out.println(">");
        }
        
        out.print("class "+clazz.getName());

        String[] interfaces = clazz.getInterfaces();
        int k = 0;
        for (int i=0; i<interfaces.length; i++) {
            String cppInterface = converter.qualifiedName(interfaces[i], namespace(clazz));
            if (!cppInterface.equals("")) {
                out.print(k == 0 ? " : " : ", ");
                if (useVirtualInheritance) out.print("virtual ");
                out.print("public ");
                out.print(cppInterface);
                k++;
            }
        }

        out.println(" {");
    }

    protected void printPublic(IndentPrintWriter out, IClass clazz) {
        out.println();
        out.println("public: ");
        out.println("    /// Destructor.");
        out.println("    virtual ~"+clazz.getName()+"() { /* nop */; }");
    }

    protected void printMethod(IndentPrintWriter out, IMethod method, String nameSpace) {
        super.printMethod(out, method, nameSpace);
        out.print(" = 0");
        out.println(";");
    }

    /**
     * @return true if a class needs to be written (no need if it contains only #defines)
     */
    protected boolean printDefines(IndentPrintWriter out, IClass clazz) {
        boolean writeClass = false;
        IField[] fields = clazz.getFields();
        for (int j=0; j<fields.length; j++) {
            IField field = fields[j];

            IType type = field.getNamedType().getType();

            if (!type.isConst() || type.isEnumeration()) {
                writeClass = true;
                continue;
            }

            String[] comments = field.getComments(language);
            if (comments.length>0) {
                out.println();
                for (int i=0; i<comments.length; i++) {
                    out.println(comments[i]);
                }
            }

            while (field != null) {
                INamedType namedType = field.getNamedType();

                out.print("#define ");
                out.print(namedType.getName());
                out.print(" ");
                String init = namedType.getInit();
                if (init != null) init = valueProperties.getProperty(init, init);
                if ((init != null) && !init.equals("")) {
                    out.print(init);
                } else {
                    out.print("\"Constants should be initialized.\"");
                }
                out.println();

                field = field.getNext();
            }
        }

        // write class if there are methods, or if the class is really empty
        if ((clazz.getMethods().length > 0)
           || ((clazz.getMethods().length == 0) && (clazz.getFields().length == 0))) {
            writeClass = true;
        }

        return writeClass;
    }

    protected void printField(IndentPrintWriter out, IField field, String nameSpace) {

        IType type = field.getNamedType().getType();

        // FIXME check this...
        if (type.isConst() && !type.isEnumeration()) return;

        out.println();
        String[] comments = field.getComments(language);
        for (int i=0; i<comments.length; i++) {
            out.println(comments[i]);
        }

        out.print("    ");
        if (type.isEnumeration()) {
            out.print("enum");
            if (type.getName() != null) {
                out.print(" ");
                out.print(type.getName());
            }
            out.print(" {");
        } else {
            out.print(converter.type(type, nameSpace));
        }
        out.print(" ");
        while (field != null) {
            INamedType namedType = field.getNamedType();

            String name = namedType.getName();
            out.print(namesProperties.getProperty(name, name));
            String init = namedType.getInit();
            if (init != null) init = valueProperties.getProperty(init, init);
            if ((init != null) && !init.equals("")) {
                out.print(" = ");
                out.print(init);
            }

            field = field.getNext();
            if (field != null) {
                out.print(", ");
            }
        }
        if (type.isEnumeration()) out.print(" }");
        out.println(";");
    }

    protected String namedType(INamedType namedType, String nameSpace) {
        StringBuffer s = new StringBuffer();
        s.append(super.namedType(namedType, nameSpace));
        String init = namedType.getInit();
        if (init != null) init = valueProperties.getProperty(init, init);
        if ((init != null) && !init.equals("")) {
            s.append(" = ");
            s.append(init);
        }
        return s.toString();
    }
}

