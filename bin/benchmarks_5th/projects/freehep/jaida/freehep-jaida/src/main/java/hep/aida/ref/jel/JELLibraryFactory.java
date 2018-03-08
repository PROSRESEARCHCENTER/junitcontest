package hep.aida.ref.jel;

import gnu.jel.CompiledExpression;
import gnu.jel.DVMap;
import gnu.jel.Evaluator;
import gnu.jel.Library;

import java.util.ArrayList;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public abstract class JELLibraryFactory {
    
    private static ArrayList staticLibsList = new ArrayList();
    private static ArrayList dotLibsList = new ArrayList();
    
    static {
        staticLibsList.add(java.lang.Math.class);
        dotLibsList.add(Object.class);
        dotLibsList.add(String.class);
        dotLibsList.add(java.util.Date.class);
    }
    
    
    public static CompiledExpression compile(DVMap map, Class valueProvider, String expression, Class type) {
        Class[] staticLib  = getStaticLibs();
	Class[] dotLib     = getDotLibs();
	Class[] dynamicLib = new Class[]{valueProvider};
        
        Library lib = null;
	try {
	    lib = new Library(staticLib, dynamicLib, dotLib, map, null);
	    lib.markStateDependent("random", null);
	} catch (gnu.jel.CompilationException ec1) {
	    throw new RuntimeException("Can not compile JEL Library!", ec1);
	}
        
        CompiledExpression compExpression = null;
	try {
	    compExpression = Evaluator.compile(expression, lib, type);
	} catch (gnu.jel.CompilationException ec2) { 
	    throw new RuntimeException("Can not compile JEL Expression: "+expression, ec2);
	}
        
        return compExpression;
    }
    
    static Class[] getStaticLibs() {
        int nStaticLibs = staticLibsList.size();
        Class[] staticLibs = new Class[nStaticLibs];
        for ( int i = 0; i < nStaticLibs; i++ )
            staticLibs[i] = (Class) staticLibsList.get(i);
        return staticLibs;
    }
    
    public static void addStaticLib(Class staticLibClass) {
        staticLibsList.add(staticLibClass);
    }

    static Class[] getDotLibs() {
        int nDotLibs = dotLibsList.size();
        Class[] dotLibs = new Class[nDotLibs];
        for ( int i = 0; i < nDotLibs; i++ )
            dotLibs[i] = (Class) dotLibsList.get(i);
        return dotLibs;
    }
    
    public static void addDotLib(Class staticLibClass) {
        dotLibsList.add(staticLibClass);
    }
    
}
