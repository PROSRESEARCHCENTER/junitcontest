/*
 * UncminOptimizerFactory.java
 *
 * Created on October 31, 2002, 3:10 PM
 */

package hep.aida.ref.optimizer.uncmin;
import hep.aida.ext.IOptimizer;
import hep.aida.ext.IOptimizerFactory;

/**
 *
 * @author  The AIDA team @ SLAC.
 *
 */
public class UncminOptimizerFactory implements IOptimizerFactory {
    
    /** Creates a new instance of UncminOptimizerFactory */
    public UncminOptimizerFactory() {
    }
    
    /** 
     * Create an optimizer with default configuration.
     *
     */
    public IOptimizer create() {
        return create(names[0]);
    }

    public IOptimizer create(String name) {
        String n = name.toLowerCase();
        for ( int i = 0; i < names.length; i++ )
            if ( n.equals(names[i]) )
                return new UncminOptimizer();
        throw new IllegalArgumentException("Cannot create IOptimizer with name "+name);
    }    
    
    public String[] optimizerFactoryNames() {
        return names;
    }
    
    private String[] names = new String[] {"uncmin"};
        
}
