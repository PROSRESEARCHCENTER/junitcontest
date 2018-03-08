// Copyright 2004-2005, FreeHEP.
package org.freehep.aid;

import java.io.*;
import java.util.*;

import org.freehep.rtti.*;
import org.freehep.util.*;
import org.freehep.util.io.*;

/**
 * @author Mark Donszelmann
 * @version $Id: PythonClassGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PythonClassGenerator extends AbstractGenerator {

    protected static final String language = "py";
    
    protected static final Set builtinTypes = new HashSet();
    static {
        builtinTypes.add("NoneType");
        builtinTypes.add("TypeType");
        builtinTypes.add("BooleanType");
        builtinTypes.add("IntType");
        builtinTypes.add("LongType");
        builtinTypes.add("FloatType");
        builtinTypes.add("ComplexType");
        builtinTypes.add("StringType");
        builtinTypes.add("UnicodeType");
        builtinTypes.add("TupleType");
        builtinTypes.add("ListType");
        builtinTypes.add("DictType");
        builtinTypes.add("DictionaryType");
        builtinTypes.add("FunctionType");
        builtinTypes.add("LambdaType");
        builtinTypes.add("GeneratorType");
        builtinTypes.add("CodeType");
        builtinTypes.add("ClassType");
        builtinTypes.add("InstanceType");
        builtinTypes.add("MethodType");
        builtinTypes.add("UnboundMethodType");
        builtinTypes.add("BuiltinFunctionType");
        builtinTypes.add("BuiltinMethodType");
        builtinTypes.add("ModuleType");
        builtinTypes.add("FileType");
        builtinTypes.add("XRangeType");
        builtinTypes.add("SliceType");
        builtinTypes.add("EllipsisType");
        builtinTypes.add("TracebackType");
        builtinTypes.add("FrameType");
        builtinTypes.add("BufferType");
        builtinTypes.add("StringTypes");
    }
    
    protected UserProperties importProperties = new UserProperties();
    protected UserProperties typeProperties = new UserProperties();
    protected UserProperties namesProperties = new UserProperties();
    protected UserProperties valueProperties = new UserProperties();
    protected UserProperties commentProperties = new UserProperties();

    public PythonClassGenerator(String propDir) {
        super();

        AidUtil.loadProperties(properties, getClass(), propDir, "aid.py.properties");;
        AidUtil.loadProperties(importProperties, getClass(), propDir, "aid.imports."+language+".properties");
        AidUtil.loadProperties(typeProperties, getClass(), propDir, "aid.types."+language+".properties");
        AidUtil.loadProperties(namesProperties, getClass(), propDir, "aid.names."+language+".properties");
        AidUtil.loadProperties(valueProperties, getClass(), propDir, "aid.values."+language+".properties");
        AidUtil.loadProperties(commentProperties, getClass(), propDir, "aid.comments."+language+".properties");

        properties.setProperty("py.interface", "true");
    }

    public String directory(IClass clazz) {
        String directory = clazz.getPackageName();
        directory = typeProperties.getProperty(directory, directory);
        return directory.replaceAll("\\.","/");
    }

    public String filename(IClass clazz) {
        return clazz.getName()+".py";
    }

    protected boolean isClass(IClass clazz) {
        return true;
    }

    public boolean print(File file, IClass clazz) throws IOException {
        IndentPrintWriter out = new IndentPrintWriter(new PrintWriter(new BufferedWriter(new FileWriter(file))));
        out.setIndentString("    ");

        // these go in separate files
        printEnumFields(file, clazz);

        // Prepare...
        IMethod[] methods = clazz.getMethods();
        Set/*<String>*/ allNames = new HashSet();
        Map/*<String, IMethod>*/ single = new HashMap();
        Map/*<String, List<IMethod> >*/ overloaded = new HashMap();
        
        // FIXME, we should also check the superclass if the same methodname is defined.
        if (methods.length > 0) {
            // loop over all methods to find overloaded ones, store the methods and the names.
            for (int i=0; i<methods.length; i++) {
                String name = methods[i].getName();
                if (allNames.contains(name)) {
                    List methodList = (List)overloaded.get(name);
                    if (methodList == null) {
                        methodList = new ArrayList();
                        overloaded.put(name, methodList);
                    } 
                    methodList.add(methods[i]);
                    
                    // remove the entry from single, and add to methodList if exist
                    IMethod first = (IMethod)single.remove(name);
                    if (first != null) methodList.add(first);
                } else {
                    allNames.add(name);
                    single.put(name, methods[i]);
                }
            }
        }

        printHeader(out, clazz);

        SortedSet sysImports = new TreeSet();
        SortedSet imports = new TreeSet();
        
        // print import statements
        importFromSingle(clazz, single, sysImports, imports);
        importFromOverloaded(clazz, overloaded, sysImports, imports);
        if (!sysImports.isEmpty()) {
            out.println();
            for (Iterator i = sysImports.iterator(); i.hasNext(); ) {
                out.println(i.next());
            }
        }     
        if (!imports.isEmpty()) {
            out.println();
            for (Iterator i = imports.iterator(); i.hasNext(); ) {
                out.println(i.next());
            }
        }
        out.println();

        printClassHeader(out, clazz);

        // fields first for enums
        IField[] fields = clazz.getFields();
        if (fields.length > 0) out.println();
        for (int i=0; i<fields.length; i++) {
            if (i!= 0) out.println();
            printField(out, fields[i], true);
        }

        if (methods.length > 0) {
                        
            // output the single methods
            for (Iterator i=single.keySet().iterator(); i.hasNext(); ) {
                String name = (String)i.next();
                IMethod method = (IMethod)single.get(name);
                printMethod(out, clazz, method);
            }
            
            // output the overloaded methods, filter out duplicates, and print their dispatch method
            for (Iterator i=overloaded.keySet().iterator(); i.hasNext(); ) {
                String name = (String)i.next();
                List methodList = (List)overloaded.get(name);
                int maxNumberOfArguments = 0;
                Set overloadedNames = new HashSet();
                for (Iterator j=methodList.iterator(); j.hasNext(); ) {
                    IMethod method = (IMethod)j.next();
                    String overloadedName = overloadedMethodName(method);
                    if (!overloadedNames.contains(overloadedName)) {
                        overloadedNames.add(overloadedName);
                        printOverloadedMethod(out, clazz, method, overloadedName);
                        maxNumberOfArguments = Math.max(maxNumberOfArguments, method.getParameterTypes().length);
                    }
                }
                printDispatchMethod(out, clazz, methods, name, maxNumberOfArguments);
            }
        }

        printEOCComments(out, clazz);
        printEOFComments(out, clazz);

        out.println("# end of class or interface");

        printEOFComments(out, clazz);

        out.println();
        out.close();
        
        return false;
    }


    protected void printEnumFields(File file, IClass clazz) throws IOException {
        IField[] enums = clazz.getEnumFields();
        
        for (int i=0; i<enums.length; i++) {
            String name = enums[i].getNamedType().getType().getName();
            IndentPrintWriter eout = new IndentPrintWriter(new BufferedWriter(new FileWriter(
                    new File(file.getParentFile(), name+".py"))));
            printHeader(eout, clazz);
        
            eout.println("class "+name+":");
        
            printField(eout, enums[i], false);
            eout.close();
        }    
    }

    protected void printHeader(IndentPrintWriter out, IClass clazz) {
        warning(out);

        out.println();
        String[] packageComments = clazz.getPackageComments(language);
        if (packageComments.length > 0) {
            out.println("\"\"\"");
            for (int i=0; i<packageComments.length; i++) {
                out.println(packageComments[i]);
            }
            out.println("\"\"\"");
        }

//        String packageName = clazz.getPackageName();
//        if (!packageName.equals("")) packageName = typeProperties.getProperty(packageName, packageName);

//        if (!packageName.equals("")) {
//            out.println("package "+packageName+";");
//        }
    }

    protected void warning(IndentPrintWriter out) {
	    // first line to signal our files!
        out.println("\"\"\" AID-GENERATED");
        out.println("=========================================================================");
        out.println("This class was generated by AID - Abstract Interface Definition          ");
        out.println("DO NOT MODIFY, but use the org.freehep.aid.Aid utility to regenerate it. ");
        out.println("=========================================================================");
        out.println("\"\"\"");
    }

    protected void importFromOverloaded(IClass clazz, Map/*<String, List<IMethod> >*/ overloaded, SortedSet sysImports, SortedSet imports) {

        String[] interfaces = clazz.getInterfaces();
        for (int i=0; i<interfaces.length; i++) {
            importFrom(interfaces[i], clazz, sysImports, imports, true);
        }

        // we only care about overloaded methods, since the others have no types.
        for (Iterator i=overloaded.keySet().iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            List methodList = (List)overloaded.get(name);
            for (Iterator j=methodList.iterator(); j.hasNext(); ) {
                IMethod method = (IMethod)j.next();
                INamedType[] parameterTypes = method.getParameterTypes();
                for (int p=0; p<parameterTypes.length; p++) {
                    importFrom(parameterTypes[p].getType(), clazz, sysImports, imports, true);
                    
                    String init = parameterTypes[p].getInit();
                    if (init != null) {
                        init = valueProperties.getProperty(init, init);
                        importFrom(init, clazz, sysImports, imports, false);
                    }
                }
            }
        }

        IField[] fields = clazz.getFields();
        for (int i=0; i<fields.length; i++) {
            IType type = fields[i].getNamedType().getType();
            importFrom(type, clazz, sysImports, imports, true);
        }
    }

// imports only definitions for init values, since py has no types.
    protected void importFromSingle(IClass clazz, Map/*<String, IMethod>*/ single, SortedSet sysImports, SortedSet imports) {
        for (Iterator i=single.keySet().iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            IMethod method = (IMethod)single.get(name);
            INamedType[] parameterTypes = method.getParameterTypes();
            for (int p=0; p<parameterTypes.length; p++) {
                String init = parameterTypes[p].getInit();
                if (init != null) {
                    init = valueProperties.getProperty(init, init);
                    importFrom(init, clazz, sysImports, imports, false);
                }
            }
        }
    }

    protected void importFrom(IType type, IClass clazz, SortedSet sysImports, SortedSet imports, boolean report) {
        if (type.isEnumeration()) return;
        importFrom(type.getName(), clazz, sysImports, imports, report);
    }

    protected void importFrom(String name, IClass clazz, SortedSet sysImports, SortedSet imports, boolean report) {
        if (name == null) return;
        
        // escape the array name.
        if (name.equals("[]")) name = "\\[\\]";

        name = typeProperties.getProperty(name, name).trim();
        if (name.equals("")) return;
        String importName = importProperties.getProperty(name, null);
        if (importName != null) {
            importName = importName.trim();
            if (!importName.equals(clazz.getPackageName()+"."+clazz.getName())) {
                int dot = importName.lastIndexOf(".");
                if (dot >= 0) {
                    importName = importName.substring(0, dot);
                    importName = typeProperties.getProperty(importName, importName).trim() + "." + name;
                    imports.add("from "+importName+" import *");    
                } else {               
                    sysImports.add("from "+importName+" import "+name);
                }
            }
        } else {
            if (report) System.err.println("Do not know how to import '"+name+"'");
        }
    }

    protected void printClassHeader(IndentPrintWriter out, IClass clazz) {
        out.print("class ");
        out.print(clazz.getName());

        String[] classes = clazz.getInterfaces();
        if (classes.length > 0) {
            int k = 0;
            for (int i=0; i<classes.length; i++) {
                String superclass = typeProperties.getProperty(classes[i], classes[i]);
                if (!superclass.equals("")) {
                    if (k == 0) {
                        out.print("(");
                    } else {
                        out.print(", ");
                    }
                    out.print(superclass);
                    k++;
                }
            }
            if (k > 0) out.print(")");
        }
        out.println(": ");

        String[] comments = clazz.getComments(language);
        if (comments.length > 0) {
            out.println("    \"\"\"");
            for (int i=0; i<comments.length; i++) {
                out.print("    ");
                out.println(comments[i]);
            }
            out.println("    \"\"\"");
            out.println();
        }            
    }

    protected void printMethod(IndentPrintWriter out, IClass clazz, IMethod method) {
        printMethodHeader(out, clazz, method);
        printMethodComments(out, method);
        printMethodBody(out, clazz, method);
    }

    protected void printDispatchMethod(IndentPrintWriter out, IClass clazz, IMethod[] methods, String name, int maxNumberOfParameters) {
        // method header
        out.print("    def ");
        out.print(name);
        out.print("(self");
        for (int i=1; i<=maxNumberOfParameters ; i++) {
            out.print(", ");
            out.print("arg"+i+" = None");
        }
        out.println("):");
                
        // special comment
        out.println("        \"\"\"Dispatch method for the '"+name+"' routine.");
        out.println("        This method takes a maximum number of arguments = "+maxNumberOfParameters);
        out.println("        Look at the individual methods with name '"+name+"_...' for documentation.");
        out.println("        @throws TypeError if number of parameters incorrect or types incompatible.");
        out.println("        \"\"\"");
        out.println();
        
        // find all overloaded methods and put the expressions to select them into a map, mapping to the calls.
        // this will filter all duplicates.
        Map expressions = new HashMap();
        for (int m=0; m<methods.length; m++) {
            IMethod method = methods[m];
            if (method.getName().equals(name)) {
                INamedType[] parameterTypes = method.getParameterTypes();
                
                // the expression
                StringBuffer expression = new StringBuffer();
                
                // FIXME, since we map parameters of M types to N types where M >= N, some if clauses are never
                // reached and could be taken out...
                for (int i=0; i<maxNumberOfParameters; i++) {
                    if (i != 0) expression.append(" and ");
                    if (i < parameterTypes.length) {
                        String type = type(parameterTypes[i].getType());
                        String listSubType = null;
                        if (!builtinTypes.contains(type)) {
                            // special case for listType, we check on subtype later...
                            String listType = "ListType";
                            if (type.endsWith(listType)) {
                                listSubType = type.substring(0, type.length()-listType.length());
                                type = listType;
                            }
                        }
                        expression.append("isinstance(arg"+(i+1)+", "+type+")");
                        // NOTE: does not handle Lists of Lists
                        // check on subtype.
                        if (listSubType != null) {
                            expression.append(" and (((len(arg"+(i+1)+") > 0) and isinstance(arg"+(i+1)+"[0], "+listSubType+")) or (len(arg"+(i+1)+") == 0))");
                        }
                    } else {
                        expression.append("(arg"+(i+1)+" == None)");
                    }
                }
                
                if (!expressions.containsKey(expression.toString())) {
                    // the call
                    StringBuffer call = new StringBuffer();
                    call.append("            self.");
                    call.append(overloadedMethodName(method));
                    call.append("(");
                    for (int i=0; i<parameterTypes.length; i++) {
                        if (i != 0) call.append(", ");
                        call.append("arg"+(i+1));
                    }
                    call.append(")");
                    
                    expressions.put(expression.toString(), call.toString());
                }
            }
        }

        // now print all left over expressions and their calls.
        String expression = null;
        for (Iterator i = expressions.keySet().iterator(); i.hasNext(); ) {
            // the if
            out.print("        ");
            out.print(expression == null ? "if" : "elif");
            out.print(" ");
            
            expression = (String)i.next();
            out.print(expression);
            out.print(":");
            out.println();
            out.println(expressions.get(expression));
        }

        out.println("        else:");
        out.println("            raise TypeError");
        out.println();
    }

    protected void printOverloadedMethod(IndentPrintWriter out, IClass clazz, IMethod method, String overloadedName) {
        INamedType[] parameterTypes = method.getParameterTypes();

        out.print("    def ");
        out.print(overloadedName);
        out.print("(self");

        for (int i=0; i<parameterTypes.length; i++) {
            out.print(", ");
            String name = parameterTypes[i].getName();
            out.print(namesProperties.getProperty(name, name));
        }
        out.println("):");
        printMethodComments(out, method);
        
        out.println("        raise NotImplementedError");
        out.println();
    }

    protected void printMethodComments(IndentPrintWriter out, IMethod method) {
        String[] comments = method.getComments(language);
        if (comments.length > 0) {
            out.println("        \"\"\"");
            for (int i=0; i<comments.length; i++) {
                // type translation
                comments[i] = translate(comments[i], typeProperties);
                // value translation
                comments[i] = translate(comments[i], valueProperties);
                // comment translation
                comments[i] = translate(comments[i], commentProperties);

                out.print("        ");
                out.println(comments[i]);
            }
            out.println("        \"\"\"");
            out.println();
        }
    }

    private String translate(String s, Properties p) {
        for (Enumeration e=p.propertyNames(); e.hasMoreElements(); ) {
            String name = (String)e.nextElement();
            // FIXME: name should be escaped...
            s = s.replaceAll("(^|\\s)"+name+"(\\s|$)", "$1"+p.getProperty(name)+"$2");
        }
        return s;
    }

    protected String overloadedMethodName(IMethod method) {
        StringBuffer s = new StringBuffer();
        s.append(method.getName());

        INamedType[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            for (int i=0; i<parameterTypes.length; i++) {
    //            if (typeName.length() > 0) typeName.setCharAt(0, Character.toUpperCase(typeName.charAt(0)));
                s.append("_");
                s.append(type(parameterTypes[i].getType()));
            }
        } else {
            s.append("_None");
        }
        return s.toString();
    }

    protected void printMethodHeader(IndentPrintWriter out, IClass clazz, IMethod method) {
        out.print("    def ");
        out.print(method.getName());
        out.print("(self");

        INamedType[] parameterTypes = method.getParameterTypes();
        int noOfParameters = parameterTypes.length;
        for (int i=0; i<noOfParameters; i++) {
            out.print(", ");
            String name = parameterTypes[i].getName();
            out.print(namesProperties.getProperty(name, name));
            String init = parameterTypes[i].getInit();
            if (init != null) init = valueProperties.getProperty(init, init);
            if ((init != null) && !init.equals("")) {
                out.print(" = ");
                out.print(init);
            }
        }
        out.println("):");
    }

    protected void printMethodBody(IndentPrintWriter out, IClass clazz, IMethod method) {
        out.println("        raise NotImplementedError");
        out.println();
    }

    protected void printField(IndentPrintWriter out, IField field, boolean prefix) {
        IType type = field.getNamedType().getType();
        String[] comments = field.getComments(language);

        int enumInit = 0;
        boolean enumSet = false;
        StringBuffer initStringBuffer = new StringBuffer();
        out.print("    ");
        while (field != null) {
            INamedType namedType = field.getNamedType();

            String name = namedType.getName();
            name = namesProperties.getProperty(name, name);
            if (type.isEnumeration() && prefix && !type.getName().equals("")) out.print(type.getName()+"_"); 
            out.print(name);
            String init = namedType.getInit();
            if (init != null) init = valueProperties.getProperty(init, init);
            if ((init != null) && !init.equals("")) {
                if (type.isEnumeration()) {
                    int value = Integer.decode(init).intValue();
                    initStringBuffer.append(enumInit == 0 ? "[" : ", ");
                    initStringBuffer.append(value);
                    enumInit = value+1;
                    enumSet = true;
                } else {
                    out.print(" = ");
                    out.print(init);
                }
            } else if (type.isEnumeration()) {
                initStringBuffer.append(enumInit == 0 ? "[" : ", ");
                initStringBuffer.append(enumInit);
                enumInit++;
            }

            field = field.getNext();
            if (field != null) {
                out.print(", ");
            }
        }
        if (type.isEnumeration()) {
            if (enumSet) {
                out.println(" = "+initStringBuffer.toString()+"]");
            } else {
                out.println(" = range("+enumInit+")");
            }
        }
        
        if (comments.length > 0) {
            out.println("    \"\"\"");
            for (int i=0; i<comments.length; i++) {
                out.print("    ");
                out.println(comments[i]);
            }
            out.println();
            out.println("    \"\"\"");
        }
        
        out.println();
        out.println();
    }

    protected String type(IType type) {
        return type(type, 0);
    }

    private String type(IType type, int nesting) {
        IType[] types = type.getTypes();
        StringBuffer s = new StringBuffer();
        String typeName = type.getName();
        if (typeName.equals("[]")) {
            s.append(type(types[0]));
        } else {
            typeName = typeProperties.getProperty(typeName, typeName);
            s.append(typeName);
            if (types.length > 0) {
                // print subtypes as "of", except if the template type was empty
                if (!typeName.equals("")) {
                    if (nesting == 0) s.append("of");
                }
                s.append(type(types[0], nesting+1));
                for (int i=1; i<types.length; i++) {
                    s.append("and");
                    s.append(type(types[i], nesting+1));
                }
            }
        }
        for (int i=0; i<type.getDimension(); i++) s.append("ListType");
        return s.toString();
    }

    protected void printEOCComments(IndentPrintWriter out, IClass clazz) {
        String[] eocComments = clazz.getEOCComments(language);
        if (eocComments.length > 0) {
            out.println();
            out.println("    \"\"\"");
            for (int i=0; i<eocComments.length; i++) {
                out.print("    ");
                out.println(eocComments[i]);
            }
            out.println("    \"\"\"");
        }            
    }

    protected void printEOPComments(IndentPrintWriter out, IClass clazz) {
        String[] eopComments = clazz.getEOPComments(language);
        if (eopComments.length > 0) {
            out.println();
            out.println("    \"\"\"");
            for (int i=0; i<eopComments.length; i++) {
                out.print("    ");
                out.println(eopComments[i]);
            }
            out.println("    \"\"\"");
        }            
    }

    protected void printEOFComments(IndentPrintWriter out, IClass clazz) {
        String[] eofComments = clazz.getEOFComments(language);
        if (eofComments.length > 0) {
            out.println();
            out.println("\"\"\"");
            for (int i=0; i<eofComments.length; i++) {
                out.println(eofComments[i]);
            }
            out.println("\"\"\"");
        }            
    }
}

