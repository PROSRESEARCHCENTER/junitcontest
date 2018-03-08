// Copyright 2002-2006, FreeHEP.
package hep.aida.ref.optimizer.fminuit;

import hep.aida.IFunction;
import hep.aida.ext.IVariableSettings;

import java.util.ArrayList;

import org.freehep.math.fminuit.FMinuitCommands;
import org.freehep.math.fminuit.FMinuitFunction;

/**
 *
 *  Minuit commands.
 *
 * @author Tony Johnson, Victor Serbo, Max Turri, Mark Donszelmann
 *
 */
public class MinuitCommands extends FMinuitCommands {
    
    private static IFunction function = null;
    private String[] variableNames;
    
    private MinuitOptimizer theOptimizer;
    
    private int[] parInfo = new int[2];
    private double[] minInfo = new double[3];
    private double[] arglist = new double[100];
    private ArrayList varList = new ArrayList();
    
    /**
     * The possible status of the covariance matrix
     *
     */
    protected static final int MATRIX_NOT_CALCULATED  = 0;
    protected static final int MATRIX_DIAGONAL_APPROX = 1;
    protected static final int MATRIX_FULL_FORCED_POS = 2;
    protected static final int MATRIX_FULL_ACCURATE   = 3;
    
    /**
     * The printout levels for the minimizer output are the following:
     *
     */
    protected static final int NO_OUTPUT       = -1;
    protected static final int MINIMAL_OUTPUT  = 0;
    protected static final int NORMAL_OUTPUT   = 1;
    protected static final int DETAILED_OUTPUT = 2;
    protected static final int MAXIMAL_OUTPUT  = 3;
    
    /**
     * The different types of minimization.
     *
     */
    protected static final int SIMPLEX_MIN  = 0;
    protected static final int MIGRAD_MIN   = 1;
    protected static final int MINIMIZE_MIN = 2;
    
    private boolean isInitialized = false;
                                    
    protected void loadAndInitialize() {
        super.loadAndInitialize(new FMinuitFunction() {
            public void initializeFunction() {
                // nop
            }
            
            public double evaluateFunction(double[] vars) {
                return function.value( vars );
            }
            
            public double[] evaluateDerivatives(double[] vars) {
                return function.gradient( vars );
            }
            
            public void finalizeFunction() {
                // nop
            }
        });
        isInitialized = true;
        jmninit( 5, 6, 7);
        ((MinuitOptimizerConfiguration)theOptimizer.configuration()).setDefaults();
    }
    
    /**
     * Default constructor.
     *
     */
    protected MinuitCommands(MinuitOptimizer theOptimizer) {
        this.theOptimizer = theOptimizer;
    }
    
    
    public void setPrintLevel(int printLevel) throws java.lang.IllegalArgumentException {
        if ( !isInitialized )
            loadAndInitialize();
        arglist[0] = printLevel;
        jmnexcm("SET PRINT",arglist,1);
    }
    
    /**
     * Reset Minuit.
     *
     */
    protected void resetMinuit() {
        if ( !isInitialized )
            loadAndInitialize();
        function = null;
        variableNames = null;
        varList.clear();
        jmnexcm( "CLEAR", arglist, 0 );
    }
        
    /**
     * Set the function to be minimized.
     * When setting a new function the minimizer is cleared of previous information.
     * @param function The function to be minimized.
     *
     */
    protected void setFunction( IFunction function, MinuitOptimizer optimizer ) {
        MinuitCommands.function = function;
        this.variableNames = function.variableNames();
        if ( variableNames == null || variableNames.length == 0 ) throw new IllegalArgumentException("Cannot optimize!! There are no variables in this function!");
        for ( int i = 0; i<variableNames.length; i++ ) {
            String varName = variableNames[i];
            IVariableSettings varSet = optimizer.variableSettings(varName);
            double value = varSet.value();
            if ( Double.isNaN(value) ) throw new IllegalArgumentException("No initial value set for variable "+varName);
            
            if ( varSet.isBound() )
                addVariable( varName, value, varSet.stepSize(), varSet.lowerBound(), varSet.upperBound() );
            else
                addVariable( varName, value, varSet.stepSize(), 0, 0 );
            if ( varSet.isFixed() ) fixVariable(varName);
        }
        
        if ( getNVariables() == 0 ) throw new IllegalArgumentException("Cannot optimize!! There are no free variable registered in Minuit!");
    }

    protected void updateFunction( MinuitOptimizer optimizer ) {
        if ( !isInitialized )
            loadAndInitialize();
        for ( int i = 0; i<variableNames.length; i++ ) {
            String varName = variableNames[i];
            IVariableSettings varSet = optimizer.variableSettings(varName);
            if ( ! varSet.isFixed() ) {
                String[] name = new String[1];
                double[] vals = new double[4];
                jmnpout(i+1,name,vals);
                //            if ( ! name[0].equals(variableNames[i]) ) throw new RuntimeException("Something went wrong!! Variables name mismatch!! "+name[0]+" "+variableNames[i]);
                varSet.setValue(vals[0]);
                varSet.setStepSize(vals[1]);
            }
        }
    }
    
    protected void fixVariable( String varName ) {
        if ( !isInitialized )
            loadAndInitialize();
        arglist[0] = varList.indexOf(varName)+1;
        if ( jmnexcm("FIX", arglist, 1) != 0 ) throw new RuntimeException();
    }
    
    /**
     * Add a variable to the fit.
     * @param parNum   The variable number as referenced by the Function.
     * @param var      The Variable.
     * @param stepSize The initial step size.
     * @return <code>true</code> if the variable was added successfully.
     *         <code>false</code> otherwise.
     *
     */
    private void addVariable( String varName, double value, double step, double lowerBound, double upperBound ) {
        if ( !isInitialized )
            loadAndInitialize();
        if ( jmnparm( varList.size()+1,varName,value,step,lowerBound,upperBound) != 0 ) throw new RuntimeException();
        varList.add( varName );
    }
    
    
    /**
     * Get the status of the fit.
     * @return The status of the fit based on the current status
     *         of the covariance matrix.
     *
     */
    protected int getStatus() {
        if ( !isInitialized )
            loadAndInitialize();
        return jmnstat(minInfo, parInfo);
    }
    
    /**
     * Get the number of (released) variables in the minimization.
     * @return The number of variables in the minimization.
     *
     */
    protected int getNVariables() {
        if ( !isInitialized )
            loadAndInitialize();
        jmnstat(minInfo, parInfo);
        return parInfo[0];
    }
    
    /**
     * Get the value of UP, the errorDefinition.
     * @return The number of UP.
     *
     */
    protected double getErrorDef() {
        if ( !isInitialized )
            loadAndInitialize();
        jmnstat(minInfo, parInfo);
        return minInfo[2];
    }
    
    /**
     * Set the Error Definition, the Minuit UP value.
     * Minuit defines variable's errors as the change in the variable's value
     * required to change the function value by the Error Definition (UP).
     * For chiSquared fits UP=1, for negative log likelihood UP=0.5.
     * @param errDef The new value for the Error Definition
     *
     */
    protected void setErrorDef( int errDef ) {
        if ( !isInitialized )
            loadAndInitialize();
        if ( errDef == MinuitOptimizerConfiguration.DEFAULT_ERROR  || errDef == MinuitOptimizerConfiguration.CHI2_FIT_ERROR ) arglist[0] = 1.;
        else if ( errDef == MinuitOptimizerConfiguration.LOGL_FIT_ERROR ) arglist[0] = 0.5;
        if ( jmnexcm("SET ERR",arglist,1) != 0 ) throw new RuntimeException();
    }
    
    protected void setErrorDefinition( double errDef ) {
        if ( !isInitialized )
            loadAndInitialize();
        arglist[0] = errDef;
        if ( jmnexcm("SET ERR",arglist,1) != 0 ) throw new RuntimeException();
    }
    /**
     * Tell Minuit if the derivatives provided by the function are to be used.
     *
     */
    protected void setUseFunctionGradient( boolean useFunctionGradient ) {
        if ( !isInitialized )
            loadAndInitialize();
        if ( useFunctionGradient ) {
            // This forces Minuit to use the function's derivatives.
            // without arglist[0] = 1, Minuit will calculate its own derivatives
            // and compare it with the ones provided by the funciton.
            arglist[0] = 1;
            if ( jmnexcm("SET GRADIENT",arglist,1) != 0 ) throw new RuntimeException();
        }
        else {
            if ( jmnexcm("SET NOGRADIENT",arglist,0) != 0 ) throw new RuntimeException();
        }
    }
    
    
    /**
     * Set the strategy to be used in calculating the first and second derivative and
     * in certain optimization methods. It determines the reliability of the calculation
     * as it changes the number of allowed function calls.
     *
     */
    protected void setStrategy( int strategy ) {
        if ( !isInitialized )
            loadAndInitialize();
        arglist[0] = strategy;
        if ( jmnexcm("SET STRATEGY",arglist,1) != 0 ) throw new RuntimeException();
    }
    
    /**
     * Informs Minuit on the machin precision.
     *
     */
    protected void setPrecision( double precision ) {
        if ( !isInitialized )
            loadAndInitialize();
        arglist[0] = precision;
        if ( jmnexcm("SET EPSMACHINE",arglist,1) != 0 ) throw new RuntimeException();
    }
    
    /**
     * Minimize the function.
     * @param method The optimization method. It can be:
     *                - SIMPLEX performs a minimixation using the simplex method of
     *                  Nedler and Mead. The minimization will stop after maxIter call have
     *                  been performed or when the EDM is less than the tolerance (default
     *                  is 0.1*UP)
     *                - MIGRAD The default minimization using the Migrad method. maxIter is the (optional)
     *                  maximum amount of iterations; when this is reached the minimization will
     *                  stopped even if it did not converge. The minimization converges when
     *                  the EDM is less than 0.001*tolerance*UP.
     *                - MINIMIZE It starts by using the MIGRAD minimization; it this
     *                  does not converge it switches to the SIMPLEX method.
     *                - IMPROVE If a previous minimization has converged and the function is in
     *                  and optimal solution, it searches for additional global optimal points.
     *                  The calculation will be stopped after maxIterations calls.
     *                - SEEK Causes a Monte Carlo minimization of the function, by choosing uniformely random values
     *                  of the variables in an hypercube centered in the current variable values. The size of the
     *                  hypercube is specified by the value of tolerance.
     * @param maxIterations   The maximum amount of allowed iterations.
     * @param tolerance The tolerance for the minimization.
     * @return <code>true</code> if the command was successfull; <code>false</code> otherwise.
     *         Check the status of the minimization to know what went wrong.
     *
     */
    protected void optimize( String method, int maxIterations, double tolerance ) {
        if ( !isInitialized )
            loadAndInitialize();
        arglist[0] = 1;
        if ( jmnexcm("CALL FCN",arglist,1) != 0 ) throw new RuntimeException();
        int nArg = 2;
        arglist[0] = maxIterations;
        arglist[1] = tolerance;
        if ( method.startsWith("IMP") ) nArg = 1;
        jmnexcm(method, arglist, nArg);
  //        if ( jmnexcm(method, arglist, nArg) != 0 ) throw new RuntimeException();
    }
    
    /**
     * Perform the Minos error analysis on the specified variables.
     * param maxIter The maximum number of iterations allowed.
     * param vars    The list of variables whose errors are to be recalculated.
     * return <code>true</code> if the command was successfull; <code>false</code> otherwise.
     *         Check the status of the minimization to know what went wrong.
     *
     */
    /*
    protected boolean minos( int maxIter, ArrayList vars ) {
        int nArg = 0;
        arglist[nArg++] = maxIter;
        Iterator iter = vars.iterator();
        while( iter.hasNext() )
            arglist[nArg++] = varList.indexOf( iter.next() ) + 1;
        return internMinos( nArg );
    }
     */
    protected void minos() {
        internMinos(0);
    }
    
    private void internMinos( int nArg ) {
        if ( !isInitialized )
            loadAndInitialize();
        if ( function == null ) throw new RuntimeException("A function has to be provided before minimizing!!!");
        if ( jmnexcm("MINOS",arglist,nArg) != 0 ) throw new RuntimeException();
    }
    
    /**
     * Calculate the Error Matrix in the current configuration.
     * @return <code>true</code> if the error matrix was calculated successfully.
     *         <code>false</code> otherwise.
     *
     */
    /*
    protected boolean calculateErrorMatrix() {
        if ( function == null ) {
            System.out.println("A function has to be provided before minimizing!!!");
            return false;
        }
        if ( jmnexcm("HESSE",arglist,0) == 0 ) return true;
        return false;
    }
     */
    protected double[][] calculateContour( String parName1, String parName2 ) {
        return calculateContour( parName1, parName2, 20);
    }
    protected double[][] calculateContour( String parName1, String parName2, int nPoints ) {
        return calculateContour( parName1, parName2, nPoints, 1 );
    }
    protected double[][] calculateContour( String parName1, String parName2, int nPoints, double nSigmas ) {
        if ( !isInitialized )
            loadAndInitialize();
        double errDef = getErrorDef();
        if( nSigmas < 1 ) throw new IllegalArgumentException("The number of sigmas has to at least 1");
        setErrorDefinition( nSigmas*nSigmas*errDef );
        
        int parIndex1 = varList.indexOf(parName1)+1;
        int parIndex2 = varList.indexOf(parName2)+1;

        double[] xPoints = new double[nPoints];
        double[] yPoints = new double[nPoints];
        int[] nFound = new int[1];
        
        jmncont( parIndex1, parIndex2, nPoints, xPoints, yPoints, nFound );
        
        int pointFound = nFound[0];
        double[][] contour = new double[2][pointFound];
        for ( int i = 0; i < pointFound; i++ ) {
            contour[0][i] = xPoints[i];
            contour[1][i] = yPoints[i];
        }
        
        setErrorDefinition( errDef );
        
        return contour;
    }
    
    
    protected double[][] getCovarianceMatrix() {
        if ( !isInitialized )
            loadAndInitialize();
        int nDim = getNVariables();
        double[][] covMatrix = new double[nDim][nDim];
        double[] matrix = new double[nDim*nDim];
        jmnemat(matrix, nDim);
        for ( int i = 0; i< nDim; i++ ) 
            for ( int j = 0; j< nDim; j++ ) 
                covMatrix[i][j] = matrix[i*nDim+j];
        return covMatrix;
    }        
 
}
