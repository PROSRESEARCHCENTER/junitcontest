/*
 * MinpackOptimizer.java
 *
 * Created on May 27, 2002, 4:26 PM
 */

package hep.aida.ref.optimizer.minpack;

import hep.aida.ref.optimizer.OptimizerResult;

/**
 *
 *  Implementation of IOptimizerConfiguration for the Minpack optimizer
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public class MinpackOptimizer extends hep.aida.ref.optimizer.AbstractOptimizer {
    
    /** 
     * Creates a new instance of MinpackOptimizer 
     */
    public MinpackOptimizer() {
        result = new OptimizerResult();
        configuration = new MinpackOptimizerConfiguration();
        domainConstraint = null;
    }
    
    public void optimize() {
        /*
        int nFunctions = functions.size();
        if ( nFunctions == 0 ) {
            System.out.println("Cannot optimize!! The function was not set correctely!");
            return false;
        }

        double tolerance = configuration.tolerance();
        
        ArrayList variables = new ArrayList();

        for ( int i = 0; i<nFunctions; i++ ) {
            IVariable[] vars = ( (IFunction)functions.get(i) ).variables();
            for ( int j = 0; j<vars.length; j++ ) {
                IVariable var = vars[j];
                if ( ! var.isFixed() ) 
                    if ( ! variables.contains( var ) )
                        variables.add( (IVariable)var );
            }
        }

        int nVariables = variables.size();

        double[] xVal = new double[ nVariables+1 ];
        for ( int i = 0; i<nVariables; i++ )
            xVal[i+1] = ( (IVariable) variables.get(i) ).value();


        IVariable[] v = new IVariable[nVariables];
        for ( int i = 0; i<nVariables; i++ )
            v[i] = (IVariable) variables.get(i);

        IFunction[] f = new IFunction[nFunctions];
        for ( int i = 0; i<nFunctions; i++ )
            f[i] = (IFunction) functions.get(i);

        MinpackLmdifFunc lmdifFunc = new MinpackLmdifFunc( f, v );
        
        double[] fVal = new double[ nFunctions+1 ];

        lmdifFunc.fcn( nFunctions, nVariables, xVal, fVal, null );

        int info[] = new int[2];
        
        Minpack_f77.lmdif1_f77(lmdifFunc,nFunctions,nVariables,xVal,fVal,tolerance,info);

        System.out.println("\n Info: "+info[0]+" "+info[1]);
        for ( int i = 0; i<nVariables; i++ )
            System.out.println("Var "+i+" "+xVal[i+1]);
*/
    }

/*
    private class MinpackLmdifFunc implements Lmdif_fcn {
        
        protected IFunction[] functions;
        protected IVariable[] vars;
        
        MinpackLmdifFunc( IFunction[] functions, IVariable[] vars ) {
            this.functions = functions;
            this.vars = vars;
        }
  
        public void fcn(int m, int n, double x[], double fvec[], int iflag[]) {
            if ( n != vars.length ) throw new RuntimeException("Wrong number of variables in MinpackLmdifFunc!!");
            if ( m != functions.length ) throw new RuntimeException("Wrong number of functions in MinpackLmdifFunc!!");
            
            for ( int i = 0; i<n; i++ ) 
                vars[i].setValue( x[i+1] );

            for ( int i = 0; i<m; i++ )
                fvec[i+1] = ((AbstractModelFunction)functions[i]).value();
        }
    }
*/
}

