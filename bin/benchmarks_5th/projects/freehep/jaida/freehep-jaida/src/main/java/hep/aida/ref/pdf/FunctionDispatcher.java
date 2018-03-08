package hep.aida.ref.pdf;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface FunctionDispatcher {
    
    void addFunctionListener( FunctionListener listener );
    void removeFunctionListener( FunctionListener listener );
    
}
