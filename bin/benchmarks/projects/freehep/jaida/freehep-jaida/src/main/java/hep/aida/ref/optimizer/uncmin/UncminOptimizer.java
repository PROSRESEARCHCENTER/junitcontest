/*
 * UncminOptimizer.java
 *
 * Created on May 28, 2002, 11:21 AM
 */

package hep.aida.ref.optimizer.uncmin;
import hep.aida.IFunction;
import hep.aida.ext.IOptimizerResult;
import hep.aida.ext.IVariableSettings;
import hep.aida.ref.optimizer.OptimizerResult;
import optimization.Uncmin_f77;
import optimization.Uncmin_methods;

/**
 *
 * @author  turri
 */
public class UncminOptimizer extends hep.aida.ref.optimizer.AbstractOptimizer {
    
    int[] varIndex;
    
    /** Creates a new instance of UncminOptimizer */
    public UncminOptimizer() {
        result = new OptimizerResult();
        configuration = new UncminOptimizerConfiguration();
        domainConstraint = null;
    }
    
    /**
     * Perform the optimization.
     * @return <code>true</code> if the optimization was
     *        successfull, <code>false</code> otherwise.
     *
     */
    public void optimize() {
        
        if ( function == null ) throw new IllegalArgumentException("Cannot optimize!! The function was not set correctely!");
        
        String[] variableNames = function.variableNames();
        varIndex = new int[variableNames.length];
        
        if ( variableNames == null || variableNames.length == 0 ) throw new IllegalArgumentException("Cannot optimize!! There are no variables in this function!");
        
        int dimension = 0;
        
        for ( int i = 0; i<variableNames.length; i++ ) {
            String varName = variableNames[i];
            IVariableSettings varSet = variableSettings(varName);
            if ( ! varSet.isFixed() ) {
                varIndex[dimension] = i;
                dimension++;
            }
        }
        
        if ( dimension == 0 ) throw new IllegalArgumentException("There are no free variables!!");
        
        UncminFunc uncminFunc = new UncminFunc( function, dimension, this );
        
        String method = configuration.method();
        int strategy = configuration.strategy();
        
        // Input to the optimizer
        double[] xIn  = new double[dimension+1];
        double[] stepSize = new double[dimension+1];
        double[] fscale = new double[2];
        int[] methodIndex = new int[2];
        int[] strategyIndex = new int[2];
        int[] performChecks = new int[2];
        int[] ndigit = new int[2];
        int[] maxIter = new int[2];
        int[] useGradient = new int[2];
        int[] useHessian = new int[2];
        double[] trustRegionRadius = new double[2];
        double gradientTol[] = new double[2];
        double stepTol[] = new double[2];
        double stepMax[] = new double[2];
        
        
        // Optimizer's output
        double[] xOut = new double[dimension+1];
        double[] fOut = new double[2];
        double[] gradientOut = new double[dimension+1];
        double[][] hessianOut = new double[dimension+1][dimension+1];
        double[] hessianDiagOut = new double[dimension+1];
        int[] terminationCode = new int[2];
        
        // What are these fields for?
        fscale[1] = 1;
        ndigit[1] = -1;
        trustRegionRadius[1] = -1.0;
        stepMax[1] = 0.0;
        
        // Set the method
        method.toUpperCase();
        if ( method.startsWith(UncminOptimizerConfiguration.LINE_SEARCH) ) methodIndex[1] = 1;
        else if ( method.startsWith(UncminOptimizerConfiguration.DOUBLE_DOGLEG) ) methodIndex[1] = 2;
        else if ( method.startsWith(UncminOptimizerConfiguration.MORE_HEBDON) ) methodIndex[1] = 3;
        
        // Set the strategy
        switch ( strategy ) {
            case UncminOptimizerConfiguration.LOW_CALL_STRATEGY:
                strategyIndex[1] = 1;
                performChecks[1] = 0;
                break;
            case UncminOptimizerConfiguration.HIGH_CALL_STRATEGY:
                strategyIndex[1] = 0;
                performChecks[1] = 0;
                break;
            case UncminOptimizerConfiguration.LOW_CALL_STRATEGY_CHECK:
                strategyIndex[1] = 1;
                performChecks[1] = 1;
                break;
            case UncminOptimizerConfiguration.HIGH_CALL_STRATEGY_CHECK:
                strategyIndex[1] = 0;
                performChecks[1] = 1;
                break;
        }
        
        // Set maxIterations
        maxIter[1] = configuration.maxIterations();
        
        // Set gradient and Hessian calculation
        if ( configuration.useFunctionGradient() ) useGradient[1] = 1;
        if ( configuration.useFunctionHessian() ) useHessian[1] = 1;
        
        // Set Tolerance
        double tolerance = configuration.tolerance();
        gradientTol[1] = tolerance;
        stepTol[1] = tolerance;
        
        for ( int i = 0; i<dimension; i++ ) {
            String varName = variableNames[ varIndex(i) ];
            IVariableSettings varSet = variableSettings(varName);
            xIn[i+1] = varSet.value();
            stepSize[i+1] = varSet.stepSize();
        }
        
        Uncmin_f77.optif9_f77(dimension,xIn,uncminFunc,stepSize,fscale,
        methodIndex,strategyIndex,performChecks,ndigit,maxIter,useGradient,useHessian,
        trustRegionRadius,gradientTol,stepMax,stepTol,xOut,fOut,gradientOut,terminationCode,hessianOut,
        hessianDiagOut);
        
        switch( terminationCode[1] ) {
            case 0:
                result.setOptimizationStatus( IOptimizerResult.CONVERGED );
                break;
            case 1:
                result.setOptimizationStatus( IOptimizerResult.CONVERGED_SMALL_GRADIENT );
                break;
            case 2:
                result.setOptimizationStatus( IOptimizerResult.CONVERGED_SMALL_STEP_SIZE );
                break;
            case 3:
                result.setOptimizationStatus( IOptimizerResult.NOT_CONVERGED );
                break;
            case 4:
                result.setOptimizationStatus( IOptimizerResult.REACHED_MAX_ITER );
                break;
            case 5:
                result.setOptimizationStatus( IOptimizerResult.TOO_MANY_LARGE_STEPS );
                break;
        }
        
        
        
        double[] pars = new double[variableNames.length];
        int outCount = 0;
        for ( int i = 0; i<variableNames.length; i++ ) {
            String varName = variableNames[i];
            IVariableSettings varSet = variableSettings(varName);
            if ( ! varSet.isFixed() ) {
                double val = xOut[outCount+1];
                double err = Math.sqrt(2./hessianDiagOut[outCount+1]);
                varSet.setValue(val);
                varSet.setStepSize(err);
                outCount++;
            }
            pars[i] = varSet.value();
        }
        
        result.setParameters(pars);
        
        double[][] covMatrix = new double[dimension][dimension];

        for ( int i = 0; i<dimension; i++ ) {
            for ( int j = i; j<dimension; j++ ) {
                    covMatrix[i][j] = Math.pow(1/(hessianOut[i+1][j+1]/2),2);
                    covMatrix[j][i] = Math.pow(1/(hessianOut[i+1][j+1]/2),2);
            }
        }
        
        result.setCovarianceMatrix( covMatrix );
    }
    
    private int varIndex( int index ) {
        return varIndex[index];
    }
    
    private class UncminFunc implements Uncmin_methods {
        
        IFunction function;
        
        UncminOptimizer optimizer;
        int dimension;
        double[] vars;
        
        UncminFunc( IFunction function, int dimension, UncminOptimizer optimizer ) {
            this.dimension = dimension;
            this.function = function;
            this.optimizer = optimizer;
            String[] variableNames = function.variableNames();
            vars = new double[variableNames.length];
            for( int i = 0; i < variableNames.length; i++ )
                vars[i] = optimizer.variableSettings( variableNames[i] ).value();
        }
        
        public double f_to_minimize(double x[]) {
            for ( int i = 0; i<dimension; i++ )
                vars[optimizer.varIndex(i)] = x[i+1];
            return function.value(vars);
        }
        
        public void gradient(double x[], double g[]) {
            g = function.gradient( x );
        }
        public void hessian(double x[], double h[][]) {
            throw new UnsupportedOperationException("Cannot calculate function's Hessian. Please report this problem");
        }
        
        private int indexOf( int index ) {
            return varIndex[ index ];
        }
    }
}
