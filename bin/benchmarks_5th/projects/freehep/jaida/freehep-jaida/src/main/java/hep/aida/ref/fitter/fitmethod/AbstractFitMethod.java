package hep.aida.ref.fitter.fitmethod;

import hep.aida.IFunction;
import hep.aida.dev.IDevFitDataIterator;
import hep.aida.ext.IFitMethod;

/**
 *
 * @author  The AIDA team @ SLAC.
 *
 * Default implementation of IFitMethod.
 *
 * This class provides the default implementation of the evaluate(..) method.
 * This is the sum, over the data of evaluateSumElement(...).
 * Most classes extending this class should only implement the evaluateSumElement(...) method
 * unless efficiency is gained by overwriting the evaluate(...) method.
 *
 */
public abstract class AbstractFitMethod implements IFitMethod {
    
    private String[] fitMethodNames;
    private int fitType;
    private Object correlationObject;

    public AbstractFitMethod(int fitType, String[] fitMethodNames) {
        this.fitType = fitType;
        this.fitMethodNames = fitMethodNames;
    }

    public int fitType() {
        return fitType;
    }
        
    public String[] fitMethodNames() {
        return fitMethodNames;
    }    

    public double[] evaluateGradient(int dimension, IDevFitDataIterator dataIter, IFunction function) {
        if ( correlationObject() != null )
            throw new IllegalArgumentException("Correlation Object not used by this fit method.");
        dataIter.start();
        double[] fitFunctionGradients = new double[ dimension ];
        while( dataIter.next() ) {
            double[] grad = evaluateGradientSumElement(dataIter, function);
            for ( int i = 0; i<dimension; i++ )
                    fitFunctionGradients[i] += grad[i];
        }
        return fitFunctionGradients;
    }    
    
    public double evaluate(IDevFitDataIterator dataIter, IFunction function) {
        if ( correlationObject() != null )
            throw new IllegalArgumentException("Correlation Object not used by this fit method.");
        dataIter.start();
        double fitFunctionValue = 0;
        while( dataIter.next() )
            fitFunctionValue += evaluateSumElement(dataIter, function);
        return fitFunctionValue;
    }    
        
    public void setCorrelationObject(Object correlationObj) {
        this.correlationObject = correlationObj;
    }
    
    public void clear() {
        correlationObject = null;
    }
    
    public Object correlationObject() {
        return correlationObject;
    }
    
}
