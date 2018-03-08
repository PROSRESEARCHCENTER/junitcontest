package hep.aida.ref.hbook;

import hep.aida.ref.tuple.ReadOnlyAbstractTuple;
import hep.io.hbook.ColumnwiseTuple;
import hep.io.hbook.ColumnwiseTupleColumn;
import org.freehep.util.Value;

/**
 * An implementation of ITuple backed by a PAW column wise tuple.
 * @author tonyj
 * @version $Id: HBookColumnwiseTuple.java 8584 2006-08-10 23:06:37Z duns $
 */
class HBookColumnwiseTuple extends ReadOnlyAbstractTuple {

    private ColumnwiseTuple tuple;
    private ColumnwiseTupleColumn[] columns;
    
    HBookColumnwiseTuple(ColumnwiseTuple tuple) {
        super(String.valueOf(tuple.id()),tuple.getName());
        this.tuple = tuple;
        
        int nCols = tuple.nChildren();
        columns = new ColumnwiseTupleColumn[nCols];
        for ( int i = 0; i < nCols; i++ )
            columns[i] = (ColumnwiseTupleColumn) tuple.getChild(i);        
    }
    
    public double columnMax(int column) throws java.lang.IllegalArgumentException {
        return columns[column].getMax();
    }
    
    public double columnMean(int column) throws java.lang.IllegalArgumentException {
        return Double.NaN;
    }
    
    public double columnMin(int column) throws java.lang.IllegalArgumentException {
        return columns[column].getMin();
    }
    
    public String columnName(int column) throws java.lang.IllegalArgumentException {
        return columns[column].getName();
    }

    public double columnRms(int column) throws java.lang.IllegalArgumentException {
        return Double.NaN;
    }
    
    public Class columnType(int column) throws java.lang.IllegalArgumentException {
        return columns[column].getColumnClass();
    }
    
    public int columns() {
        return columns.length;
    }
    
    public int findColumn(String name) throws java.lang.IllegalArgumentException {
        int index = tuple.getIndex(name);
        if ( index < 0 ) throw new IllegalArgumentException("Column "+name+" does not exist");
        return index;
    }
    
    public boolean supportsRandomAccess() {
        return true;
    }
    
    public boolean supportsMultipleCursors() {
        return true;
    }
    
    public boolean isInMemory() {
        return true;
    }
    
    public boolean providesColumnDefaultValues() {
        return false;
    }    

    public void columnValue(int column, Value v) {
        Class type = columnType(column);
        ColumnwiseTupleColumn child = columns[column];
        if ( type == Integer.TYPE )
            v.set(child.getInt());
        else if ( type == Short.TYPE )
            v.set((short) child.getInt());
        else if ( type == Long.TYPE )
            v.set(child.getInt());
        else if ( type == Float.TYPE )
            v.set((float) child.getDouble());
        else if ( type == Double.TYPE )
            v.set(child.getDouble());
        else if ( type == Boolean.TYPE )
            v.set(child.getBoolean());
        else if ( type == Byte.TYPE )
            v.set((byte) child.getInt());
        else if ( type == Character.TYPE )
            v.set((char) child.getInt());
        else if ( type == String.class )
            v.set(child.getString());
        else
            v.set(child.getObject());
    }    
    
    public hep.aida.ITuple findTuple(int param) {
        throw new UnsupportedOperationException();
    }
    
    public boolean next() {
        boolean advanced = super.next();
        if ( advanced ) tuple.setCurrentRow( getRow()+1 );
        return advanced;
    }

    public int rows() {
        return tuple.getRows();
    }
    
    public void setRow(int row) throws java.lang.IllegalArgumentException {
        super.setRow(row);
        tuple.setCurrentRow(row+1);
    }
    
    public void skip(int n) throws java.lang.IllegalArgumentException {
        super.skip(n);
        tuple.setCurrentRow(getRow()+1);
    }
    
    public int columnIndexByName(String name) {
        return findColumn( name );
    }
    
    public String columnDefaultString(int index) {
        throw new UnsupportedOperationException();
    }
    
    public Object columnDefaultValue(int index) {
        throw new UnsupportedOperationException();        
    }    
}