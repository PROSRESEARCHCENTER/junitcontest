// Copyright 2002-2007, FreeHEP.
package org.freehep.aid;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.freehep.rtti.IClass;
import org.freehep.rtti.IMethod;
import org.freehep.rtti.INamedType;
import org.freehep.rtti.IType;
import org.freehep.util.UserProperties;
import org.freehep.util.io.IndentPrintWriter;

/**
 * @author Mark Donszelmann
 * @version $Id: AbstractCPPGenerator.java 13274 2007-08-21 20:29:55Z duns $
 */
public abstract class AbstractCPPGenerator extends AbstractGenerator {

    protected final static String language = "cpp";

    protected UserProperties typeProperties = new UserProperties();
    protected UserProperties sysIncludeProperties = new UserProperties();
    protected UserProperties includeProperties = new UserProperties();
    protected UserProperties valueProperties = new UserProperties();
    protected UserProperties namesProperties = new UserProperties();
    protected CPPTypeConverter converter;

    public AbstractCPPGenerator(String propDir) {
        super();

        AidUtil.loadProperties(properties, getClass(), propDir, "aid.cpp.properties");;
        AidUtil.loadProperties(sysIncludeProperties, getClass(), propDir, "aid.sysincludes."+language+".properties");
        AidUtil.loadProperties(includeProperties, getClass(), propDir, "aid.includes."+language+".properties");
        AidUtil.loadProperties(valueProperties, getClass(), propDir, "aid.values."+language+".properties");
        AidUtil.loadProperties(typeProperties, getClass(), propDir, "aid.types."+language+".properties");
        AidUtil.loadProperties(namesProperties, getClass(), propDir, "aid.names."+language+".properties");
        converter = new CPPTypeConverter(propDir);
    }

    protected abstract String prefix();

    protected String namespace(IClass clazz) {
        return converter.namespace(clazz.getPackageName());
    }

    protected SortedSet printIncludeStatements(IndentPrintWriter out, IClass clazz) {
        SortedSet sysIncludes = new TreeSet();
        SortedSet includes = new TreeSet();
        SortedSet types = new TreeSet();

        includeStatements(clazz, sysIncludes, includes, namespace(clazz), types);

        Iterator it = sysIncludes.iterator();
        if (it.hasNext()) out.println();
        while (it.hasNext()) {
            String includeName = (String)it.next();
            if (includeName.indexOf("::") >= 0) System.err.println("WARNING: "+clazz.getName()+" does not map '"+includeName+"' to a proper include file...");
            out.println("#include <"+includeName+">");
        }

        it = includes.iterator();
        if (it.hasNext()) out.println();
        while (it.hasNext()) {
            String includeName = (String)it.next();
            if (includeName.indexOf("::") >= 0) System.err.println("WARNING: "+clazz.getName()+" does not map '"+includeName+"' to a proper include file...");
            out.println("#include \""+includeName+"\"");
        }

        return types;
    }

    protected void includeStatements(IClass clazz, SortedSet sysIncludes, SortedSet includes, String namespace, SortedSet types) {

        IMethod[] methods = clazz.getMethods();
        for (int m=0; m<methods.length; m++) {
            // check return type
            includeFrom(methods[m].getReturnType(), prefix()+clazz.getName(), sysIncludes, includes, namespace, types);

            INamedType[] parameterTypes = methods[m].getParameterTypes();
            for (int p=0; p<parameterTypes.length; p++) {
                includeFrom(parameterTypes[p].getType(), prefix()+clazz.getName(), sysIncludes, includes, namespace, types);
                // FIXME: need to do something with init values used from different class
            }

    	    // add includes for exceptions
            if (properties.isProperty("useExceptions")) {
    	        String[] exceptionTypes = methods[m].getExceptionTypes();
    	        for (int e=0; e<exceptionTypes.length; e++) {
    		        includeFrom(exceptionTypes[e], prefix()+clazz.getName(), sysIncludes, includes, namespace, types);
    	        }
    	    }
        }
    }

//    protected void includeFrom(IType type, String className, SortedSet sysIncludes, SortedSet includes, SortedSet types) {
//        includeFrom(type, className, sysIncludes, includes, "", types);
//   }

    protected void includeFrom(IType type, String className, SortedSet sysIncludes, SortedSet includes, String namespace, SortedSet types) {
        if (type.getDimension() > 0) sysIncludes.add("vector");
        if (type.isPrimitive()) return;
        IType[] subTypes = type.getTypes();
        for (int i=0; i<subTypes.length; i++) {
            includeFrom(subTypes[i], className, sysIncludes, includes, namespace, types);
        }
        if (type.getName().equals("[]")) return;
        includeFrom(type.getName(), className, sysIncludes, includes, namespace, types);
    }

    protected void includeFrom(String name, String className, SortedSet sysIncludes, SortedSet includes, String namespace, SortedSet types) {
        if (name == null) return;

        name = converter.qualifiedName(name, namespace);

        String inc = sysIncludeProperties.getProperty(name, null);
        if (inc != null) {
        	if (!inc.trim().equals("")) {
                sysIncludes.add(inc);
        	}
            return;
        }

        inc = includeProperties.getProperty(name, null);
        if (inc != null) {
        	if (!inc.trim().equals("")) {
        		includes.add(inc);
        	}
            return;
        }

        if (!name.equals("") && !name.equals(className)) {
            types.add(name);
        }
    }

    protected String namedType(INamedType namedType, String nameSpace) {
        StringBuffer s = new StringBuffer();
        s.append(converter.type(namedType.getType(), nameSpace));
        s.append(" ");
        String name = namedType.getName();
        s.append(namesProperties.getProperty(name, name));        
        return s.toString();
    }
}

