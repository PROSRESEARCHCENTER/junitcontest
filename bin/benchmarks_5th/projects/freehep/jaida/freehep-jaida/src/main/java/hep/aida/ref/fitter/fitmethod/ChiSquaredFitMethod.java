/*
 * ChiSquaredFitMethod.java
 *
 * Created on August 15, 2002, 4:38 PM
 */

package hep.aida.ref.fitter.fitmethod;
import hep.aida.IFunction;
import hep.aida.IModelFunction;
import hep.aida.dev.IDevFitDataIterator;
import hep.aida.ext.IFitMethod;

/**
 *
 * @author  The AIDA team @ SLAC.
 *
 */
public class ChiSquaredFitMethod extends AbstractFitMethod {
    
    private static String[] names = new String[]{"chi2","chisquared"};
        
    public ChiSquaredFitMethod() {
        super(IFitMethod.BINNED_FIT, names);
    }

    public double evaluate(IDevFitDataIterator dataIter, IFunction function) {
        if ( correlationObject() == null )
            return super.evaluate(dataIter, function);

        double[][] errorMatrix = (double[][]) correlationObject();
//        if ( errorMatrix[0].length != dataIter.entries() ) {
//            throw new IllegalArgumentException("Wrong dimension for the error matrix "+errorMatrix[0].length+" "+dataIter.entries());
//        }
        double[] fVals = new double[ dataIter.entries() ];
        dataIter.start();
        double fitFunctionValue = 0;
        int count = 0;
        while( dataIter.next() )
            fVals[count++] = dataIter.value() - function.value(dataIter.vars());        
        for ( int i = 0; i < dataIter.entries(); i++ )
            for ( int k = 0; k < dataIter.entries(); k++ )
                fitFunctionValue += (fVals[i]*fVals[k]*errorMatrix[i][k]);
        return fitFunctionValue;
    }    

    public double evaluateSumElement(IDevFitDataIterator dataIter, IFunction function) {
        return Math.pow( dataIter.value() - function.value( dataIter.vars() ) , 2)/Math.pow( dataIter.error(), 2);
    }
    
    public double[] evaluateGradientSumElement(IDevFitDataIterator dataIter, IFunction function) {
        double f = function.value( dataIter.vars() );
        double[] der = ((IModelFunction)function).parameterGradient( dataIter.vars() );
        double[] newDer = new double[der.length];
        double val = dataIter.value();
        double err = dataIter.error();
        double c = 2*(f-val)/(err*err);
        for ( int i = 0; i < der.length; i++ )
            newDer[i] = der[i]*c;
        return newDer;
    }
    
    public double[] evaluateGradient(int dimension, IDevFitDataIterator dataIter, IFunction function) {
        if ( correlationObject() == null )
            return super.evaluateGradient(dimension, dataIter, function);

        double[][] errorMatrix = (double[][]) correlationObject();
//        if ( errorMatrix[0].length != dataIter.entries() )
//            throw new IllegalArgumentException("Wrong dimension for the error matrix");

        double[] fVals = new double[ dataIter.entries() ];
        dataIter.start();

        double[] fitFunctionGradients = new double[ dimension ];
        double[][] derivatives = new double[dataIter.entries()][];
        
        int count = 0;
        while( dataIter.next() ) {
            fVals[count] = dataIter.value() - function.value(dataIter.vars());        
            derivatives[count] = ((IModelFunction)function).parameterGradient( dataIter.vars() );
            count++;
        }

        double[] newDerivatives = new double[dimension];
        for ( int i = 0; i < dataIter.entries(); i++ ) 
            for ( int k = 0; k < dataIter.entries(); k++ ) 
                for ( int j = 0; j < dimension; j++ ) 
                    newDerivatives[j] += (derivatives[i][j]*fVals[k]*errorMatrix[i][k]) + (fVals[i]*derivatives[k][j]*errorMatrix[i][k]);
        
        return newDerivatives;
    
    }

}   
