// Copyright 2002-2007, FreeHEP.
package org.freehep.aid;

import java.io.*;
import java.util.*;

import org.freehep.rtti.*;
import org.freehep.util.io.*;

/**
 * @author Mark Donszelmann
 * @version $Id: AbstractCPPHeaderGenerator.java 13206 2007-08-01 16:49:55Z duns $
 */
public abstract class AbstractCPPHeaderGenerator extends AbstractCPPGenerator {

    public AbstractCPPHeaderGenerator(String propDir) {
        super(propDir);

        properties.setProperty("cpp.header", "true");
    }

    public String directory(IClass clazz) {
        return "";
    }

    protected String prefix() {
        return "";
    }

    protected abstract void printClassHeader(IndentPrintWriter out, IClass clazz);
    protected void printPrivate(IndentPrintWriter out, IClass clazz) {
    }
    protected void printProtected(IndentPrintWriter out, IClass clazz) {
    }
    protected void printPublic(IndentPrintWriter out, IClass clazz) {
    }

    public boolean print(File file, IClass clazz) throws IOException {
        IndentPrintWriter out = new IndentPrintWriter(new PrintWriter(new BufferedWriter(new FileWriter(file))));
        out.setIndentString("    ");

        out.println("// -*- C++ -*-");

        warning(out);

        String namespace = namespace(clazz);
        if (!namespace.equals("")) {
            namespace = namespace.replaceAll("::","_")+"_";
        }
        String define = prefix()+namespace+prefix()+clazz.getName()+"_H";
        define = define.toUpperCase();
        out.println("#ifndef "+define);
        out.println("#define "+define+" 1");

        String[] packageComments = clazz.getPackageComments(language);
        if (packageComments.length > 0) out.println();
        for (int i=0; i<packageComments.length; i++) {
            out.println(packageComments[i]);
        }

        SortedSet types = printIncludeStatements(out, clazz);

        // print specific includes which are in the comments
        String[] classComments = clazz.getComments(language);
        for (int i=0; i<classComments.length; i++) {
            if (classComments[i].indexOf("#include") >= 0) {
                out.println(classComments[i]);
            }
        }

        boolean writeClass = printDefines(out, clazz);
        boolean writeNameSpace = (clazz.getEnumFields().length > 0);

        String[] namespaces = (prefix()+namespace(clazz)).split("::");
        if (writeNameSpace || writeClass){
            if (!writeClass) {
                // write class comment here, otherwise it is forgotten
                out.println();
                String[] comments = clazz.getComments(language);
                for (int i=0; i<comments.length; i++) {
                    if (comments[i].indexOf("#include") < 0) {
                        out.println(comments[i]);
                    }
                }
            }            
            
            for (int i=0; i<namespaces.length; i++) {
                if (i == 0) out.println();
                out.println("namespace "+namespaces[i]+" {");
            }
            
            IField[] enums = clazz.getEnumFields();
            for (int i=0; i<enums.length; i++) {
                printField(out, enums[i], null);
            }
        }
        
        if (writeClass) {
            Set/*<String>*/ templateParameters = new HashSet();
            templateParameters.addAll(Arrays.asList(clazz.getTemplateParameters()));
            IMethod[] methods = clazz.getMethods();
            for (int i=0; i<methods.length; i++) {
                templateParameters.addAll(Arrays.asList(methods[i].getTemplateParameters()));
            }
            
            Iterator it = types.iterator();            
            if (it.hasNext()) out.println();
            while (it.hasNext()) {
                String type = (String)it.next();
                if (!templateParameters.contains(type)) out.println("class "+type+";");
            }

            out.println();
            String[] comments = clazz.getComments(language);
            for (int i=0; i<comments.length; i++) {
                if (comments[i].indexOf("#include") < 0) {
                    out.println(comments[i]);
                }
            }
            printClassHeader(out, clazz);

            printPrivate(out, clazz);

            printProtected(out, clazz);

            printPublic(out, clazz);

            IConstructor[] constructors = clazz.getConstructors();
            for (int i=0; i<constructors.length; i++) {
                printConstructor(out, constructors[i], namespace(clazz));
            }

            for (int i=0; i<methods.length; i++) {
                printMethod(out, methods[i], namespace(clazz));
            }

            IField[] fields = clazz.getFields();
            for (int i=0; i<fields.length; i++) {
                printField(out, fields[i], namespace(clazz));
            }

            String[] eocComments = clazz.getEOCComments(language);
            if (eocComments.length>0) out.println();
            for (int i=0; i<eocComments.length; i++) {
                out.println(eocComments[i]);
            }

            out.println("}; // class");

            String[] eopComments = clazz.getEOPComments(language);
            if (eopComments.length > 0) {
                out.println();
                for (int i=0; i<eopComments.length; i++) {
                    out.println(eopComments[i]);
                }
                out.println();
            }
        }
        
        if (writeNameSpace || writeClass) {    
            for (int i=namespaces.length-1; i>=0; i--) {
                out.println("} // namespace "+namespaces[i]);
            }
        }

        String[] eofComments = clazz.getEOFComments(language);
        if (eofComments.length > 0) {
            out.println();
            for (int i=0; i<eofComments.length; i++) {
                out.println(eofComments[i]);
            }
        }
        out.println("#endif /* ifndef "+define+" */");
        out.close();
        
        return false;
    }

    protected void printConstructor(IndentPrintWriter out, IConstructor constructor, String nameSpace) {
    }

    protected void printMethod(IndentPrintWriter out, IMethod method, String nameSpace) {

    	// generate exceptions if needed
        boolean useExceptions = properties.isProperty("useExceptions", true);
        String[] exceptionTypes = method.getExceptionTypes();

        out.println();
        String[] comments = method.getComments(language);
        boolean returnWritten = false;
        for (int i=0; i<comments.length; i++) {
            // remove throws clause and replace by return
            int index = comments[i].indexOf("@throws");
            if ((index >= 0) && !useExceptions) {
                if (!returnWritten ) {
                    if ((exceptionTypes.length > 0) && (method.getReturnType().isVoid())) {
                        int space = index+7;
                        while (Character.isWhitespace(comments[i].charAt(space))) space++;
                        while (!Character.isWhitespace(comments[i].charAt(space))) space++;
                        String value = valueProperties.getProperty("returnInCaseOfException", "true");
                        out.println(comments[i].substring(0, index)+"@return "+value+comments[i].substring(space));
                        returnWritten = true;
                    }
                }
            } else {
                out.println(comments[i]);
            }
        }

        out.print("    ");
        
        String[] templateParameters = method.getTemplateParameters();
        if (templateParameters.length > 0) {
            out.print("template <class ");
            out.print(templateParameters[0]);
            for (int i=1; i<templateParameters.length; i++) {
                out.print(", class ");
                out.print(templateParameters[i]);
            }
            out.print("> ");
        }
                
        out.print("virtual ");

        if ((exceptionTypes.length > 0) && (method.getReturnType().isVoid()) && !useExceptions ) { //fg--
            out.print("AID_EXCEPTION");
        } else {
            out.print(converter.type(method.getReturnType(), nameSpace));
        }
        out.print(" ");
        out.print(method.getName());
        out.print("(");

        INamedType[] parameterTypes = method.getParameterTypes();
        for (int i=0; i<parameterTypes.length; i++) {
            out.print(namedType(parameterTypes[i], nameSpace));
            if (i<parameterTypes.length-1) out.print(", ");
        }
        out.print(")");
        if (method.isConst()) {
            out.print(" const");
        }

	    // add throw statement if needed
	    if((exceptionTypes.length > 0) && useExceptions){
    	    out.print(" throw (");
                for (int e=0; e<exceptionTypes.length; e++) {
    		if( e != 0 ) out.print( ", " ) ;
    		out.print( converter.qualifiedName( exceptionTypes[e], nameSpace)  ) ;
                }
    	    out.print(") ");
    	}
    }

    protected abstract boolean printDefines(IndentPrintWriter out, IClass clazz);
    protected abstract void printField(IndentPrintWriter out, IField field, String nameSpace);
}

