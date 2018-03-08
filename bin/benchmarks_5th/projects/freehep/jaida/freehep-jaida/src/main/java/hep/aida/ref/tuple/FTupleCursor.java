package hep.aida.ref.tuple;

/**
 *
 * @author  The FreeHEP team @ SLAC.
 *
 */

public interface FTupleCursor {
    
    public int row();
    
    public void start();
    
    public boolean next();
    
    public void setRow(int n);
    
    public void skip(int rows);
    
}

