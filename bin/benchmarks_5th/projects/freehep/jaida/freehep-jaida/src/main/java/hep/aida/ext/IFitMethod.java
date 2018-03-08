/*
 * IFitMethod.java
 *
 * Created on August 15, 2002, 4:35 PM
 */

package hep.aida.ext;
import hep.aida.IFunction;
import hep.aida.dev.IDevFitDataIterator;

/**
 *
 * @author  The AIDA team @ SLAC.
 *
 * Interface to be implemented by all classes providing the value of the Objective Function
 * to be optimized in the process of fitting.
 *
 */
public interface IFitMethod {
    
    public final static int BINNED_FIT = 0, UNBINNED_FIT = 1;


    /**
     * Return the fit type: BINNED_FIT if it is a binned fit,
     * UNBINNED_FIT if unbinned.
     *
     */
    public int fitType();

    /**
     * Evaluate the Objective Function for a given data iterator and a given function.
     *
     */
    public double evaluate(IDevFitDataIterator dataIter, IFunction function);
    
    /**
     * Evaluate the the sum contribution to the Objective Function value.
     *
     */
    public double evaluateSumElement(IDevFitDataIterator dataIter, IFunction function);

    /**
     * Evaluate the gradient of the Objective Function.
     *
     */
    public double[] evaluateGradient(int dimension, IDevFitDataIterator dataIter, IFunction function);
    
    /**
     * Evaluate the the sum contribution to the Objective Function gradient.
     *
     */
    public double[] evaluateGradientSumElement(IDevFitDataIterator dataIter, IFunction function);
    
    /**
     * Get the names for this fit method.
     * The first one in the array is the default.
     * The array cannot be null. It must have at least one name.
     *
     */
    public String[] fitMethodNames();
    
    /**
     * Clear the fitMethod.
     * This method is invoked before the function is evaluated.
     *
     */
    public void clear();
    
    /**
     * Set the correlation object among the data.
     *
     */
    public void setCorrelationObject(Object correlationObj);
    
    /**
     * Get the correlation object.
     *
     */
    public Object correlationObject();
}
