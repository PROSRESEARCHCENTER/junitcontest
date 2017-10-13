package hep.aida.ref.tuple;

import org.freehep.util.Value;

/**
 *
 * @author  The FreeHEP team @ SLAC.
 *
 */
public interface FTupleColumn {
            
    String name();
    
    void defaultValue(Value value);
    
    boolean hasDefaultValue();
    
    public Class type();
    
    public void minValue(Value value);
    
    public void maxValue(Value value);
    
    public void meanValue(Value value);
    
    public void rmsValue(Value value);
    
}

