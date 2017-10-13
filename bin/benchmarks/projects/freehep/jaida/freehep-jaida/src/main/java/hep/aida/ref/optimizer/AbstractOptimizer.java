/*
 * AbstractOptimizer.java
 *
 * Created on May 22, 2002, 9:58 PM
 */

package hep.aida.ref.optimizer;
import hep.aida.IFunction;
import hep.aida.ext.IDomainConstraint;
import hep.aida.ext.IOptimizerConfiguration;
import hep.aida.ext.IOptimizerResult;
import hep.aida.ext.IVariableSettings;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 *  Abstract implementation of IOptimizer
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public abstract class AbstractOptimizer implements hep.aida.ext.IOptimizer {
    
    protected IFunction function = null;
    protected OptimizerResult result;
    protected IOptimizerConfiguration configuration;
    protected IDomainConstraint domainConstraint;
    private Hashtable varSetHash = new Hashtable();
    
    /** 
     * Creates a new instance of AbstractOptimizer 
     *
     */
    public AbstractOptimizer() {
    }

    /**
     * Get the IOptimizer configuration
     * @return The configuration.
     *
     */
    public IOptimizerConfiguration configuration() {
        return configuration;
    }
    
    /**
     * Perform the optimization.
     * @return <code>true</code> if the optimization was
     *         successfull, <code>false</code> otherwise.
     *
     */
    public abstract void optimize();
    
    /**
     * Reset the function, the domain of constraints and the internal configuration.
     *
     */
    public void reset() {
        function = null;
    }
    
    /**
     * Get the optimization results.
     * @return The result.
     *
     */
    public IOptimizerResult result() {
        return (IOptimizerResult)result;
    }
    
    /**
     * Set the internal configuration of the Optimizer.
     * @param configuration The configuration.
     * @return <code>true</code> if the configuration was set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setConfiguration(IOptimizerConfiguration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * Set the domain of constraints for the IOptimizer.
     * If a domain of constraints already exists it will be overwritten.
     * @param iDomainConstraint The domain of constraints.
     * @return <code>true</code> if the IDomainConstraint is set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setConstraints(hep.aida.ext.IDomainConstraint iDomainConstraint) {
        domainConstraint = iDomainConstraint;
    }
    
    /**
     * Set the IFunction to be optimized.
     * When setting a function the current  
     * configuration is reset.
     * @param iFunction The IFunction to be optimized.
     * @return <code>true</code> if the IFunction is set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setFunction(hep.aida.IFunction iFunction) {
        function = iFunction;
        String[] varNames = function.variableNames();
        for ( int i = 0; i < varNames.length; i++ ) 
            variableSettings(varNames[i]);
    }
    
    public String[] listVariableSettings() {
        int size = varSetHash.size();
        String[] varNames = new String[size];
        Enumeration e = varSetHash.keys();
        for (int i = 0; e.hasMoreElements(); i++)
            varNames[i] = (String) e.nextElement();
        return varNames;
    }
    
    public void resetVariableSettings() {
        varSetHash.clear();
    }
    
    public IVariableSettings variableSettings(String name) {
        if ( varSetHash.containsKey(name) ) return (IVariableSettings) varSetHash.get(name);
        IVariableSettings varSet = new VariableSettings(name);
        varSetHash.put(name,varSet);
        return varSet;
    }
    
    public boolean acceptsConstraints() {
        return false;
    }
    
    public boolean canCalculateContours() {
        return false;
    }

    public double[][] calculateContour(String par1, String par2, int npts, double nSigmas) {
        throw new UnsupportedOperationException();
    }
    

}
