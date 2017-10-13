/*
 * MinuitFactory.java
 *
 * Created on October 31, 2002, 3:07 PM
 */

package hep.aida.ref.optimizer.fminuit;
import hep.aida.ext.*;

/**
 *
 * @author  The AIDA team @SLAC.
 *
 */
public class MinuitOptimizerFactory implements IOptimizerFactory {
    
    /** Creates a new instance of MinuitFactory */
    public MinuitOptimizerFactory() {
    }
    
    /** Create an optimizer with default configuration.
     *
     *
     */
    public IOptimizer create() {
        return create(names[0]);
    }

    public IOptimizer create(String name) {
        String n = name.toLowerCase();
        for ( int i = 0; i < names.length; i++ )
            if ( n.equals(names[i]) )
                return new MinuitOptimizer();
        throw new IllegalArgumentException("Cannot create IOptimizer with name "+name);
    }    
    
    public String[] optimizerFactoryNames() {
        return names;
    }
    
    private String[] names = new String[] {"fminuit","minuit"};
    
}
