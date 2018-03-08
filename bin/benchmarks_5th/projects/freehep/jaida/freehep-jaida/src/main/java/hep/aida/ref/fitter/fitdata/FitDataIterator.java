/*
 * FitDataIterator.java
 *
 * Created on August 16, 2002, 10:55 AM
 */

package hep.aida.ref.fitter.fitdata;
import hep.aida.ITuple;
import hep.aida.dev.IDevFitDataIterator;

/**
 *
 * @author  turri
 */
public class FitDataIterator implements IDevFitDataIterator {
    
    private ITuple tuple;
    private int valCol;
    private int errCol;
    private int errMinusCol;
    private int nVars;
    private double[] vars;
    
    public FitDataIterator(ITuple tuple) {
        this.tuple = tuple;
        
        tuple.start();
        tuple.next();
        
        valCol = tuple.findColumn("value");
        errCol = tuple.findColumn("error");
        errMinusCol = tuple.findColumn("minusError");

        nVars = tuple.columns()-3;
        vars = new double[nVars];
    }
    public int entries() {
        return tuple.rows();
    }
    public boolean next() {
        return tuple.next();
    }
    public void start() {
        tuple.start();
    }
    public double value() {
        if ( valCol != -1 ) return tuple.getDouble( valCol );
        return Double.NaN;
    }
    public double error() {
        if ( errCol != -1 ) return tuple.getDouble( errCol );
        return Double.NaN;
    }
    public double minusError() {
        if ( errMinusCol != -1 ) return tuple.getDouble( errMinusCol );
        return error();
    }
    public double[] vars() {
        for ( int i = 0; i < nVars; i++ ) 
            vars[i] = tuple.getDouble(3+i);
        return vars;
    }
}
