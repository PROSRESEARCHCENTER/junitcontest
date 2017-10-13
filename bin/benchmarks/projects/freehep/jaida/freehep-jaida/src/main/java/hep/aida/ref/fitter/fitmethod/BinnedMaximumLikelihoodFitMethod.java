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
public class BinnedMaximumLikelihoodFitMethod extends AbstractFitMethod {
    
    private static String[] names = new String[] {"bml","binnedmaxlikelihood","binnedmaximumlikelihood"};

    public BinnedMaximumLikelihoodFitMethod() {
        super(IFitMethod.BINNED_FIT, names);
    }
    
    public double evaluateSumElement(IDevFitDataIterator dataIter, IFunction function) {
        double fVal = function.value( dataIter.vars() );
        return fVal -dataIter.value()*Math.log(fVal);
    }
    
    public double[] evaluateGradientSumElement(IDevFitDataIterator dataIter, IFunction function) {
        double f = function.value( dataIter.vars() );
        double[] der = ((IModelFunction)function).parameterGradient( dataIter.vars() );
        double[] newDer = new double[der.length];
        double val = dataIter.value();
        double c = (1.-val/f);
        for ( int i = 0; i < der.length; i++ )
            newDer[i] = der[i]*c;
        return newDer;
    }    

}
