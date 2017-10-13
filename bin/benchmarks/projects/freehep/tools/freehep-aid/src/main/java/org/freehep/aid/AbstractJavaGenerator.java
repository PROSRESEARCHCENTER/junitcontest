// Copyright 2002-2005, FreeHEP.
package org.freehep.aid;

import java.io.*;
import java.util.*;

import org.freehep.rtti.*;
import org.freehep.util.*;
import org.freehep.util.io.*;

/**
 * @author Mark Donszelmann
 * @version $Id: AbstractJavaGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class AbstractJavaGenerator extends AbstractGenerator {

    protected static final String language = "java";

    protected UserProperties importProperties = new UserProperties();
    protected UserProperties typeProperties = new UserProperties();
    protected UserProperties valueProperties = new UserProperties();
    protected UserProperties templateProperties = new UserProperties();    
    protected UserProperties primitiveProperties = new UserProperties();    

    private Map/*<Id, Type>*/ template;

    public AbstractJavaGenerator(String propDir) {
        super();

        AidUtil.loadProperties(properties, getClass(), propDir, "aid.java.properties");;
        AidUtil.loadProperties(importProperties, getClass(), propDir, "aid.imports."+language+".properties");
        AidUtil.loadProperties(typeProperties, getClass(), propDir, "aid.types."+language+".properties");
        AidUtil.loadProperties(valueProperties, getClass(), propDir, "aid.values."+language+".properties");
        AidUtil.loadProperties(templateProperties, getClass(), propDir, "aid.templates."+language+".properties");
        AidUtil.loadProperties(primitiveProperties, getClass(), propDir, "aid.primitives."+language+".properties");
    }

    public String directory(IClass clazz) {
        String directory = clazz.getPackageName();
        return directory.replace('.',File.separatorChar);
    }

    public abstract String filename(IClass clazz);

    protected boolean isClass(IClass clazz) {
        return clazz.isClass();
    }

    public boolean print(File file, IClass clazz) throws IOException {
        IndentPrintWriter out = new IndentPrintWriter(new PrintWriter(new BufferedWriter(new FileWriter(file))));
        out.setIndentString("    ");

        // these go in separate files
        printEnumFields(file, clazz);

        printHeader(out, clazz);

        printImportStatements(out, clazz);

        printClassHeader(out, clazz);

        String[] concrete = getConcrete(clazz);
        if (concrete == null) {
            printClassMembers(out, clazz, null);            
        } else {
            for (int i=0; i<concrete.length; i++) {
                StringBuffer innerClassName = new StringBuffer();
                String[] templateVars = concrete[i].split(",");
                for (int j=0; j<templateVars.length; j++) {
                    String varName = templateVars[j].trim();
                    innerClassName.append(primitiveProperties.getProperty(varName, varName));
                }
                out.println();
                out.indent();
                out.println("/**");
                out.println("/* Template Instantiated Interface: "+clazz.getName()+"<"+concrete[i].trim()+">");
                out.println("*/");
                out.print("public static interface ");
                out.print(innerClassName.toString());
                out.print(" extends ");
                out.print(clazz.getName());
                out.println(" {");
                printClassMembers(out, clazz, concrete[i]);
                out.println("} //"+clazz.getName()+"."+innerClassName.toString());
                out.outdent();
                out.println();
            }
        }
        
        printEOCComments(out, clazz);

        out.println("} // class or interface");

        printEOPComments(out, clazz);
        printEOFComments(out, clazz);

        out.println();
        out.close();
        
        return false;
    }

    private void printHeader(IndentPrintWriter out, IClass clazz) {
        warning(out);

        out.println();
        String[] packageComments = clazz.getPackageComments(language);
        for (int i=0; i<packageComments.length; i++) {
            out.println(packageComments[i]);
        }

        String packageName = getPackageName(clazz);

        if (!packageName.equals("")) {
            out.println("package "+packageName+";");
        }
    }

    private void printEnumFields(File file, IClass clazz) throws IOException {
        IField[] enums = clazz.getEnumFields();
        
        for (int i=0; i<enums.length; i++) {
            IndentPrintWriter eout = new IndentPrintWriter(new BufferedWriter(new FileWriter(
                    new File(file.getParentFile(), enums[i].getNamedType().getType().getName()+".java"))));
            printHeader(eout, clazz);
        
            printField(eout, enums[i], false);
            eout.close();
        }    
    }

    private void printClassMembers(IndentPrintWriter out, IClass clazz, String concrete) {
        template = getTemplateMap(clazz.getTemplateParameters(), concrete);

        IMethod[] methods = clazz.getMethods();
        if (methods.length > 0) out.println();
        for (int i=0; i<methods.length; i++) {
            if (i!= 0) out.println();
            printMethod(out, clazz, methods[i], concrete != null);
        }

        IField[] fields = clazz.getFields();
        if (fields.length > 0) out.println();
        for (int i=0; i<fields.length; i++) {
            if (i!= 0) out.println();
            printField(out, fields[i], true);
        }
    }
    
    protected abstract void printClassHeader(IndentPrintWriter out, IClass clazz);
    protected void printEOCComments(IndentPrintWriter out, IClass clazz) {
    }
    protected void printEOPComments(IndentPrintWriter out, IClass clazz) {
    }
    protected void printEOFComments(IndentPrintWriter out, IClass clazz) {
    }

    protected void printImportStatements(IndentPrintWriter out, IClass clazz) {
        SortedSet imports = new TreeSet();
        String packageName = clazz.getPackageName();

        String[] interfaces = clazz.getInterfaces();
        for (int i=0; i<interfaces.length; i++) {
            importFrom(interfaces[i], packageName, imports);
        }

        IMethod[] methods = clazz.getMethods();
        for (int m=0; m<methods.length; m++) {
            // check return type
            importFrom(methods[m].getReturnType(), packageName, imports);

            INamedType[] parameterTypes = methods[m].getParameterTypes();
            for (int p=0; p<parameterTypes.length; p++) {
                importFrom(parameterTypes[p].getType(), packageName, imports);
            }

            String[] exceptionTypes = methods[m].getExceptionTypes();
            for (int e=0; e<exceptionTypes.length; e++) {
                importFrom(exceptionTypes[e], packageName, imports);
            }
        }

        IField[] fields = clazz.getFields();
        for (int i=0; i<fields.length; i++) {
            IType type = fields[i].getNamedType().getType();
            importFrom(type, packageName, imports);
        }

        out.println();
        for (Iterator i = imports.iterator(); i.hasNext(); ) {
            out.println("import "+i.next()+";");
        }
    }

    protected void importFrom(IType type, String packageName, SortedSet imports) {
        if (type.isPrimitive()) return;
        importFrom(type.getName(), packageName, imports);
    }

    protected void importFrom(String name, String packageName, SortedSet imports) {
        if (name == null) return;

        name = typeProperties.getProperty(name, name);
        String importName = importProperties.getProperty(name, null);
        if ((importName != null) &&
            !importName.equals("") &&
            !importName.equals("java.lang."+name) &&
            !importName.equals(packageName+"."+name)) {

            imports.add(importName);
        }
    }

    protected void printMethod(IndentPrintWriter out, IClass clazz, IMethod method, boolean useFullyQualifiedNames) {
        printMethod(out, clazz, method, useFullyQualifiedNames, method.getParameterTypes().length);
    }

    protected void printMethod(IndentPrintWriter out, IClass clazz, IMethod method, boolean useFullyQualifiedNames, int noOfParameters) {

        INamedType[] parameterTypes = method.getParameterTypes();
        if (noOfParameters > 0) {
            String init = parameterTypes[noOfParameters-1].getInit();
            if (init != null) init = valueProperties.getProperty(init, init);

            if ((init != null) && !init.equals("")) {
                printMethod(out, clazz, method, useFullyQualifiedNames,noOfParameters-1);
            }
        }

        printMethodComments(out, method, noOfParameters);
        printMethodHeader(out, clazz, method, useFullyQualifiedNames, noOfParameters);
        printMethodBody(out, clazz, method, noOfParameters);
    }

    protected void printMethodComments(IndentPrintWriter out, IMethod method, int noOfParameters) {
    }

    protected void printMethodHeader(IndentPrintWriter out, IClass clazz, IMethod method, boolean useFullyQualifiedNames, int noOfParameters) {
        out.print("    public ");
        INamedType[] parameterTypes = method.getParameterTypes();
        if (isClass(clazz) && (noOfParameters == parameterTypes.length)) out.print("abstract ");

        Map methodTemplate = template;
        // for templates
        String[] templateParameters = method.getTemplateParameters();
        if (templateParameters.length > 0) {
            // make sure we know we have templates
            methodTemplate = new HashMap();
            String[] concrete = getConcrete(clazz);
            if (concrete == null) {
                // use templates
                out.print(" <");
                out.print(templateParameters[0]);
                for (int i=1; i<templateParameters.length; i++) {
                    out.print(", ");
                    out.print(templateParameters[i]);
                }
                out.print("> ");
            } else {
                System.err.println("Cannot use templated methods in concrete template interfaces");
                System.exit(1);
            }
        }

        out.print(type(method.getReturnType(), methodTemplate, useFullyQualifiedNames));
        out.print(" ");
        out.print(method.getName());
        out.print("(");

        for (int i=0; i<noOfParameters; i++) {
            out.print(namedType(parameterTypes[i], methodTemplate, useFullyQualifiedNames));
            if (i<noOfParameters-1) out.print(", ");
        }
        out.print(")");

        String[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            out.print(" throws ");
            for (int i=0; i<exceptionTypes.length; i++) {
                out.print(exceptionTypes[i]);
                if (i<exceptionTypes.length-1) out.print(", ");
            }
        }
    }

    protected void printMethodBody(IndentPrintWriter out, IClass clazz, IMethod method, int noOfParameters) {
        INamedType[] parameterTypes = method.getParameterTypes();

        if (!isClass(clazz) || (noOfParameters == parameterTypes.length)) {
            out.println(";");
        } else {
            out.println(" {");
            out.print("        ");
            if (!method.getReturnType().isVoid()) out.print("return ");
            out.print(method.getName()+"(");
            for (int i=0; i<noOfParameters; i++) {
                out.print(parameterTypes[i].getName());
                out.print(", ");
            }
            String init = parameterTypes[noOfParameters].getInit();
            init = valueProperties.getProperty(init, init);
            out.print(init);
            out.println(");");
            out.println("    }");
        }
    }

    protected void printField(IndentPrintWriter out, IField field, boolean innerClass) {
    }

    protected String namedType(INamedType namedType, Map/*<Id, Type>*/ template, boolean useFullyQualifiedNames) {
        return type(namedType.getType(), template, useFullyQualifiedNames)+" "+namedType.getName();
    }

    protected String type(IType type, Map/*<Id, Type>*/ template, boolean useFullyQualifiedNames) {
        return type(type, 0, template, useFullyQualifiedNames);
    }

    private String type(IType type, int nesting, Map/*<Id, Type>*/ template, boolean useFullyQualifiedNames) {
        IType[] types = type.getTypes();
        StringBuffer s = new StringBuffer();
        String typeName = type.isEnumeration() ? "int" : type.getName();
        if (typeName.equals("[]")) {
            s.append(type(types[0], template, useFullyQualifiedNames));
        } else {
            String javaType = typeProperties.getProperty(typeName, typeName);
            if (template != null) {
                String templateType = (String)template.get(javaType);
                javaType = (templateType != null) ? templateType : javaType;
            }
            if (useFullyQualifiedNames) javaType = importProperties.getProperty(javaType, javaType); 
            s.append(javaType);
            if (types.length > 0) {
                if ((template == null) || (template.size() == 0)) {
                    // generate templated version
                    // print subtypes in comments, except if the template type was empty
                    if (!javaType.equals("")) {
                        if ((nesting == 0) && (template == null)) s.append("/*");
                        s.append("<");
                    }
                    s.append(type(types[0], nesting+1, template, useFullyQualifiedNames));
                    for (int i=1; i<types.length; i++) {
                        s.append(", ");
                        s.append(type(types[i], nesting+1, template, useFullyQualifiedNames));
                    }
                    if (!javaType.equals("")) {
                        s.append(">");
                        if ((nesting == 0) && (template == null)) s.append("*/");
                    }
                } else {
                    // generate instantiated version (one level only)
                    s.append(".");
                    for (int i=0; i<types.length; i++) {
                        String varName = (String)template.get(types[i].getName());
                        s.append(primitiveProperties.getProperty(varName, varName)); 
                    }                   
                }
            }
        }
        for (int i=0; i<type.getDimension(); i++) s.append("[]");
        return s.toString();
    }
    
    private String getPackageName(IClass clazz) {
        String packageName = clazz.getPackageName();
        if (!packageName.equals("")) packageName = typeProperties.getProperty(packageName, packageName);
        return packageName;
    }
    
    protected String[] getConcrete(IClass clazz) {
        String concrete = templateProperties.getProperty(getPackageName(clazz)+"."+clazz.getName(), null);
        if (concrete == null) return null;
        return concrete.split(";");
    }    
    
    protected Map getTemplateMap(String[] templateParameters, String concrete) {        
        Map/*<Id, Type>*/ template = null;
        if (templateParameters.length > 0) {
            template = new HashMap();

            if (concrete != null) {
                String[] templateVars = concrete.split(",");
                if (templateVars.length != templateParameters.length) {
                    System.err.println("Number of template variables ("+templateVars.length+") not equal to number of template parameters ("+templateParameters.length+")");
                    System.exit(1);
                }
                for (int j=0; j<templateVars.length; j++) {                                       
                    template.put(templateParameters[j], templateVars[j].trim());
                }
            }
        }
        return template;
    }   
}

