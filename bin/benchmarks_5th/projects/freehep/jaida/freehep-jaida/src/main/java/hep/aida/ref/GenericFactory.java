package hep.aida.ref;

import hep.aida.IGenericFactory;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class GenericFactory implements IGenericFactory {
    
    private String type;
    
    public GenericFactory(String type) {
        this.type = type;
    }
    
    public String type() {
        return type;
    }
    
    
}
