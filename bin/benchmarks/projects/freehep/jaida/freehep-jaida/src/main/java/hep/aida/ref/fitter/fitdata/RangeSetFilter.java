package hep.aida.ref.fitter.fitdata;

import hep.aida.IFilter;
import hep.aida.IRangeSet;
import hep.aida.ITuple;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class RangeSetFilter implements IFilter {
    
    private ITuple tup;
    private IRangeSet[] rangeSet;
    
    public RangeSetFilter(IRangeSet[] rangeSet) {
        this.rangeSet = rangeSet;
    }
            
            
    
    public boolean accept() throws java.lang.RuntimeException {
        for ( int i = 0; i < rangeSet.length; i++ ) 
            if ( ! rangeSet[i].isInRange( tup.getDouble(3+i) ) ) return false;
        return true;
    }
    
    public String expression() {
        return null;
    }
    
    public void initialize(hep.aida.ITuple iTuple) throws java.lang.IllegalArgumentException {
        this.tup = iTuple;
    }
    
}
