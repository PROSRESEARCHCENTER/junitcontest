package hep.aida.ref.pdf;

import hep.aida.ITuple;
import hep.aida.ref.tuple.Tuple;

import java.util.Random;

/**
 * An utility class to generate toy MC data sets distributed according to a given function.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public abstract class FunctionMcStudy {
    

    public static ITuple generateTuple(Function f, int nEntries) {
        double maxHeight = f.maxValue();
        if ( maxHeight == Double.NaN )
            maxHeight = evaluateMaxHeight(f,nEntries/10);
        maxHeight *= 1.2;
        return generateTuple(f,nEntries,maxHeight);
    }
    
    
    protected static ITuple generateTuple(Function f, int nEntries, double maxHeight) {
        
        int dim = f.numberOfDependents();
        Dependent[] deps = new Dependent[dim];
        String[] columnNames = new String[dim+1];
        Class[]  columnTypes = new Class[dim+1];
        for ( int i = 0; i < dim; i++ ) {
            columnNames[i] = f.getDependent(i).name();
            columnTypes[i] = Double.TYPE;
            deps[i] = f.getDependent(i);
        }
        columnNames[dim] = "functionValue";
        columnTypes[dim] = Double.TYPE;
        
        Tuple t = new Tuple("tup", "", columnNames, columnTypes, ""); 
                
        Random r = new Random();
        
        double x;
        int count = 0;
        while(true) {
            for ( int j = 0; j < dim; j++ ) {
                x = deps[j].range().generatePoint();
                deps[j].setValue(x);
                t.fill(j,x);
            }
            x = f.functionValue();
            t.fill(dim,x);
            
            if ( x > maxHeight ) {
                System.out.println("Function value "+x+" exceeds maximum "+maxHeight+". Increasing maximum by 20% and restarting the MC study.");
                return generateTuple(f,nEntries, maxHeight*1.2);
            }
            
            if ( r.nextDouble()*maxHeight < x ) {
                t.addRow();
                if ( t.rows() == nEntries )
                    return t;
            }
                
        }

    }
    
    
    
    private static double evaluateMaxHeight(Function f, int entries) {

        int dim = f.numberOfDependents();
        Dependent[] deps = new Dependent[dim];
        for ( int i = 0; i < dim; i++ )
            deps[i] = f.getDependent(i);
        
        
        double maxHeight = 0;
        double x;
        for ( int i = 0; i < entries; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                x = deps[j].range().generatePoint();
                deps[j].setValue(x);
            }
            x = f.functionValue();
            if ( x > maxHeight )
                maxHeight = x;
        }        
        return maxHeight;
    }
    
}
