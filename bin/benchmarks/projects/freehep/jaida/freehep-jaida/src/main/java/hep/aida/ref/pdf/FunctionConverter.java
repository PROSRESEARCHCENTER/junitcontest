package hep.aida.ref.pdf;

import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.IModelFunction;

/**
 * Converts IFunctions to Functions and vice-versa.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FunctionConverter {
    
    public static IModelFunction convert(Function f) {
        return (IModelFunction)f;
    }
    
    public static Function convert(IFunction f) {
        String name = "";
        if ( f instanceof IManagedObject )
            name = ((IManagedObject)f).name();            
        return new IFunctionWrapper(name,f);
    }
    
    public static IFunction getIFunction(Object obj) {
        if ( obj instanceof IFunction )
            return (IFunction)obj;
        if ( obj instanceof Function )
            return convert((Function)obj);
        else
            throw new IllegalArgumentException( "Cannot convert object of type "+obj.getClass()+" to an IFunction.");
    }

    public static IModelFunction getIModelFunction(Object obj) {
        if ( obj instanceof IModelFunction )
            return (IModelFunction)obj;
        if ( obj instanceof Function )
            return convert((Function)obj);
        else
            throw new IllegalArgumentException( "Cannot convert object of type "+obj.getClass()+" to an IFunction.");
    }

    public static Function getFunction(Object obj) {
        if ( obj instanceof Function )
            return (Function)obj;
        if ( obj instanceof IFunction )
            return convert((IFunction)obj);
        else
            throw new IllegalArgumentException( "Cannot convert object of type "+obj.getClass()+" to a Function.");
    }
}
