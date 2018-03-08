package hep.aida.ref.tuple;

import org.freehep.util.Value;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public interface FTuple {
 
    static final int ROWS_UNKNOWN = -1;

    public boolean supportsRandomAccess();
    
    public boolean supportsMultipleCursors();
    
    public boolean isInMemory();
    
    public FTupleCursor cursor();    

    String title();
    
    String name();

    int columns();
    
    int rows();
    
    FTupleColumn columnByIndex(int index);
    
    FTupleColumn columnByName(String name);
    
    String columnName(int index);
    
    Class columnType(int index);
    
    void columnValue(int column, FTupleCursor cursor, Value value);

    public FTuple tuple( int index );


}
