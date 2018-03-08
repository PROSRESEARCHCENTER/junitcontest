/*
 * MinuitOptimizer.java
 *
 * Created on May 23, 2002, 9:17 AM
 */

package hep.aida.ref.optimizer.fminuit;

import hep.aida.IFunction;
import hep.aida.ref.optimizer.OptimizerResult;

/**
 *
 *  Minuit implementation of IOptimizer
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public class MinuitOptimizer extends hep.aida.ref.optimizer.AbstractOptimizer {
    
    private MinuitCommands minuitCommands;
    
    /**
     * Creates a new instance of MinuitOptimizer
     *
     */
    public MinuitOptimizer() {
        configuration = new MinuitOptimizerConfiguration(this);
        minuitCommands = new MinuitCommands(this);
        result = new OptimizerResult();
        domainConstraint = null;
    }
    
    /**
     * Perform the optimization.
     * @return <code>true</code> if the optimization was
     *        successfull, <code>false</code> otherwise.
     *
     */
    public void optimize() {
        // First reset Minuit
        minuitCommands.resetMinuit();
        
        // Load the function in Minuit. This will load all the variables.
        if ( function == null ) throw new IllegalArgumentException("Cannot optimize!! The function was not set correctely!");
        
        minuitCommands.setFunction( function, this );
        
        String method = configuration.method();
        int maxIterations = configuration.maxIterations();
        double tolerance = configuration.tolerance();
        // Perform the actual optimization
        minuitCommands.optimize( method, maxIterations, tolerance );
        
        minuitCommands.updateFunction(this);
        
        String[] varNames = function.variableNames();
        double[] pars = new double[varNames.length];
        for ( int i = 0; i<varNames.length; i++ ) 
            pars[i] = variableSettings(varNames[i]).value();
        result.setParameters(pars);
        
        result.setCovarianceMatrix( minuitCommands.getCovarianceMatrix() );
        
        result.setOptimizationStatus( minuitCommands.getStatus() );
        
    }
    
    public void minosAnalysis() {
        minuitCommands.minos();
    }
    
    public void setFunction(IFunction function) {
        super.setFunction(function);
        minuitCommands.setFunction( function, this );
    }
    
    protected MinuitCommands commands() {
        return minuitCommands;
    }
    

    public boolean canCalculateContours() {
        return true;
    }

    public double[][] calculateContour( String parName1, String parName2, int nPoints, double nSigmas ) {
        return commands().calculateContour( parName1, parName2, nPoints, nSigmas );
    }
    
    public void reset() {
        super.reset();
        minuitCommands.resetMinuit();
    }
    
    public boolean acceptsConstraints() {
        return true;
    }
}
