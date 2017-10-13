/**
 *
 *  User level interface to the optimizer factory.
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
package hep.aida.ext;

public interface IOptimizerFactory {

    /**
     * Create the default optimizer with default configuration.
     *
     */
    IOptimizer create();
    IOptimizer create(String name);
    
    /**
     * The array of the names of the optimizer that this factory can
     * create.
     * The first one is the default. The array cannot be null and it must
     * have at least one element.
     *
     */
    String[] optimizerFactoryNames();
}
