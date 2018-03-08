package hep.aida.ref.tuple;

import hep.aida.IManagedObject;
import hep.aida.ITuple;

import java.util.Date;

import org.freehep.util.Value;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class FTupleAdapter implements FTuple {
    
    private ITuple tuple;
    private FTupleColumn[] columns;
    private FTuple[] tuples;
    private FTupleCursor cursor;
    private boolean supportsRandomAccess = false;
    
    public FTupleAdapter( ITuple tuple ) {
        setTuple( tuple );
        columns = new FTupleColumn[ tuple.columns() ];
        tuples = new FTuple[ tuple.columns() ];
        
        cursor = new FTupleCursorAdapter( tuple );
        try {
            if ( tuple.rows() > 0 )
                cursor.setRow(0);
            supportsRandomAccess = true;
        } catch (Throwable t) {}
        cursor.start();
        
        for ( int i = 0; i < columns.length; i++ ) {
            columns[i] = new FTupleColumnAdapter( tuple, i );
            if ( tuple.columnType(i) == ITuple.class )
                tuples[i] = new FTupleAdapter( tuple.getTuple(i) );
        }
        
    }
    
    public void close() {
    }
    
    public FTupleColumn column(int index) {
        return columns[ index ];
    }
    
    public FTupleColumn columnByName(String name) {
        return columns[ tuple.findColumn( name ) ];
    }
    
    public FTupleColumn columnByIndex(int index) {
        return columns[ index ];
    }

    public String columnName(int index) {
        return tuple.columnName( index );
    }
    
    public Class columnType(int index) {
        return columns[index].type();
    }
    
    public void columnValue(int column, FTupleCursor cursor, Value value) {
        Class type = columnType( column );
        
        if ( type == Integer.TYPE ) value.set( tuple.getInt( column ) );
        else if ( type == Short.TYPE ) value.set( tuple.getShort( column ) );
        else if ( type == Long.TYPE ) value.set( tuple.getLong( column ) );
        else if ( type == Float.TYPE ) value.set( tuple.getFloat( column ) );
        else if ( type == Double.TYPE ) value.set( tuple.getDouble( column ) );
        else if ( type == Boolean.TYPE ) value.set( tuple.getBoolean( column ) );
        else if ( type == Byte.TYPE ) value.set( tuple.getByte( column ) );
        else if ( type == Character.TYPE ) value.set( tuple.getChar( column ) );
        else if ( type == String.class ) value.set( tuple.getString( column ) );
        else if ( type == Date.class ) value.set( (Date) tuple.getObject( column ) );
        else value.set( tuple.getObject( column ) );
    }
    
    public int columns() {
        return tuple.columns();
    }
    
    public FTupleCursor cursor() throws IllegalStateException {
        return cursor;
    }
    
    public boolean isInMemory() {
        return false;
    }
    
    public String name() {
        if ( tuple instanceof IManagedObject )
            return ( (IManagedObject) tuple ).name();
        else
            return "title";
    }
    
    public int rows() {
        return tuple.rows();
    }
    
    public boolean supportsMultipleCursors() {
        return false;
    }
    
    public boolean supportsRandomAccess() {
        return supportsRandomAccess;
    }
    
    public String title() {
        return tuple.title();
    }
    
    public FTuple tuple(int index) {
        FTuple fTuple = tuples[index];
        ( (FTupleAdapter)fTuple ).setTuple( tuple.getTuple( index ) );
        return fTuple;
    }
    
    protected void setTuple( ITuple tuple ) {
        this.tuple = tuple;
    }
    
    private class FTupleColumnAdapter implements FTupleColumn {
        
        private ITuple tuple;
        private int col;
        
        FTupleColumnAdapter( ITuple tuple, int col ) {
            this.tuple = tuple;
            this.col = col;
        }
        
        public boolean hasDefaultValue() {
            return false;
        }
        
        public void defaultValue(Value value) {
        }
        
        public void maxValue(Value value) {
            value.set( tuple.columnMax( col ) );
        }
        
        public void meanValue(Value value) {
            value.set( tuple.columnMean( col ) );
        }
        
        public void minValue(Value value) {
            value.set( tuple.columnMin( col ) );
        }
        
        public String name() {
            return tuple.columnName( col );
        }
        
        public void rmsValue(Value value) {
            value.set( tuple.columnRms( col ) );
        }
        
        public Class type() {
            Class type = tuple.columnType(col);
            if ( type == ITuple.class )
                return FTuple.class;
            return type;
        }
        
    }
    
    private class FTupleCursorAdapter implements FTupleCursor {
        
        private ITuple tuple;
        private int row = -1;
        
        FTupleCursorAdapter( ITuple tuple ) {
            this.tuple = tuple;
        }
        
        public void close() {
        }
        
        public boolean next() {
            boolean advanced = tuple.next();
            if ( advanced ) row++;
            return advanced;
        }
        
        public int row() {
            return row;
        }
        
        public void setRow(int n) {
            tuple.setRow(n);
            row = n;
        }
        
        public void skip(int rows) {
            tuple.skip( rows );
            row += rows;
        }
        
        public void start() {
            tuple.start();
            row = -1;
        }
        
    }
}
