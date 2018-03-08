package hep.aida.ref.function;


/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface FunctionDispatcher {
    
    void addFunctionListener( FunctionListener listener );
    void removeFunctionListener( FunctionListener listener );
    
}
