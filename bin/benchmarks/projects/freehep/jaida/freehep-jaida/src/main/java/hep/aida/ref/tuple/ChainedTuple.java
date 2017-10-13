/*
 * Tuple.java
 *
 * Created on May 6, 2002, 2:05 pm
 */

package hep.aida.ref.tuple;

import hep.aida.IManagedObject;
import hep.aida.ITuple;

import org.freehep.util.Value;

/**
 *
 * @author  The AIDA team @ SLAC.
 *
 */
public class ChainedTuple extends ReadOnlyAbstractTuple {
    
    private ITuple[] set;
    private int currentRow;
    private ITuple currentTuple;
    private int currentTupleID;
    private int currentTupleRow;
    private ChainedTuple[] tuples;
    
    /**
     * Tuple constructor.
     * @param name the Tuple's name
     * @param title the Tuple's title
     * @param set Array of ITuples to be chained
     */
    public ChainedTuple(String name, String title, ITuple[] set) {
        super(name, title);

        int nCol = 0;;        
        
        // Check if ITuples are compatible
        if (set.length > 1) {
            nCol = set[0].columns();        
            String[] colNames = new String[nCol];
            Class[] colTypes = new Class[nCol];
            for (int i=0; i<nCol; i++) {
                colNames[i] = set[0].columnName(i);
                colTypes[i] = set[0].columnType(i);
            }
            
            for (int n=1; n<set.length; n++) {
                if (set[n].columns() != nCol)
                    throw new IllegalArgumentException("ITuples in the set have different number of columns!");
                
                for (int i=0; i<nCol; i++) {
                    if (!(colNames[i].equals(set[n].columnName(i))))
                        throw new IllegalArgumentException("ITuples in the set have different column names!");
                    
                    if (colTypes[i] != set[n].columnType(i))
                        throw new IllegalArgumentException("ITuples in the set have different column types!");
                }
            }
        } else
            throw new IllegalArgumentException("Not enough tuples provided. Two or more tuples must be chained together.");
        
        
        tuples = new ChainedTuple[nCol];
        for ( int i = 0; i < nCol; i++ ) {
            
            if ( set[0].columnType(i) == ITuple.class ) {
                ITuple[] folders = new ITuple[set.length];
                for ( int j = 0; j < set.length; j++ ) {
                    set[j].start();
                    folders[j] = set[j].findTuple(i);
                }
                tuples[i] = new ChainedTuple(set[0].columnName(i),"",folders);
            }
        }
        
        setTitle(title);
        this.set = set;
        currentRow = -1;
        currentTuple = set[0];
        currentTuple.start();
        currentTupleID = 0;
        currentTupleRow = -1;
        
    }
    
    
    
    public boolean supportsRandomAccess() {
        for (int i=0; i<set.length; i++) {
            if ( set[i] instanceof FTuple ) {
                if ( ! ((FTuple) set[i]).supportsRandomAccess() )
                    return false;
            }
            return false;
        }
        return true;
    }
    
    public boolean supportsMultipleCursors() {
        for (int i=0; i<set.length; i++) {
            if ( set[i] instanceof FTuple ) {
                if ( ! ((FTuple) set[i]).supportsMultipleCursors() )
                    return false;
            }
            return false;
        }
        return true;
    }
    
    public boolean isInMemory() {
        for (int i=0; i<set.length; i++) {
            if ( set[i] instanceof FTuple ) {
                if ( ! ((FTuple) set[i]).isInMemory() )
                    return false;
            }
            return false;
        }
        return true;
    }
    
    public boolean providesColumnDefaultValues() {
        for (int i=0; i<set.length; i++) {
            if ( set[i] instanceof AbstractTuple ) {
                if ( ! ((AbstractTuple) set[i]).providesColumnDefaultValues() )
                    return false;
            }
            return false;
        }
        return true;
    }    
    
    public void columnValue(int column, Value v) {
        Class type = currentTuple.columnType(column);
        if ( type == Integer.TYPE )
            v.set(currentTuple.getInt(column));
        else if ( type == Short.TYPE )
            v.set(currentTuple.getShort(column));
        else if ( type == Long.TYPE )
            v.set(currentTuple.getLong(column));
        else if ( type == Float.TYPE )
            v.set(currentTuple.getFloat(column));
        else if ( type == Double.TYPE )
            v.set(currentTuple.getDouble(column));
        else if ( type == Boolean.TYPE )
            v.set(currentTuple.getBoolean(column));
        else if ( type == Byte.TYPE )
            v.set(currentTuple.getByte(column));
        else if ( type == Character.TYPE )
            v.set(currentTuple.getChar(column));
        else if ( type == String.class )
            v.set(currentTuple.getString(column));
        else
            v.set(currentTuple.getObject(column));
    }    
    
    public String columnDefaultString( int column ) {
        if (set[0] instanceof Tuple) return ((Tuple) set[0]).columnDefaultString(column);
        else if ( columnType( column ) != ITuple.class )
            return set[0].columnDefaultValue(column).toString();
        else {
            ITuple tup = findTuple(column);
            if ( tup != null ) {
                String tupName = "";
                if (tup instanceof IManagedObject) tupName = ((IManagedObject) tup).name();
                else tupName = tup.title();
                String tmpColumnsString = "";
                int nCol = tup.columns();
                for (int i=0; i<nCol; i++) {
                    Class colType = tup.columnType(i);
                    String colName =tup.columnName(i);
                    tmpColumnsString += colType + " " + colName;
                    if ( i < nCol ) tmpColumnsString += ";";
                }
                return tupName+" = {"+tmpColumnsString+"}";
            } else
                return "null";
        }
    }
    
    /**
     * Get the number of columns in the Tuple.
     * @return the number of columns in the Tuple
     */
    public int columns() {
        return set[0].columns();
    }
    
    /**
     * Get the name of a column from its index
     * @param column the column's index
     * @return the column's name
     */
    public String columnName(int column) {
        return set[0].columnName(column);
    }
    
    /**
     * Get the type of the column from its index
     * @param column the column's index
     * @return the column's type
     */
    public Class columnType(int column) {
        return set[0].columnType(column);
    }
        
    /**
     * Get the minimum value of a column.
     * @param column The column's index.
     * @return The minimum value of the column. If the minimum
     *         cannot be calculated Double.NaN is returned.
     *
     */
    public double columnMin(int column) {
        double min = Double.NaN;
        for (int i=0; i<set.length; i++) {
            double tmp = set[i].columnMin(column);
            if (Double.isNaN(min) || tmp<min) min = tmp;
        }
        return min;
    }
    
    /**
     * Get the maximum value of a column.
     * @param column The column's index.
     * @return The maximum value of the column. If the maximum
     *         cannot be calculated Double.NaN is returned.
     *
     */
    public double columnMax(int column) {
        double max = Double.NaN;
        for (int i=0; i<set.length; i++) {
            double tmp = set[i].columnMax(column);
            if (Double.isNaN(max) || tmp>max) max = tmp;
        }
        return max;
    }
    
    /**
     * Get the mean value of a column.
     * @param column The column's index.
     * @return The mean value of the column. If the mean
     *         cannot be calculated Double.NaN is returned.
     *
     */
    public double columnMean(int column) {
        double mean = 0.;
        int  rows = 0;
        for (int i=0; i<set.length; i++) {
            double m = set[i].columnMean(column);
            int r = set[i].rows();
            if (r<=0) continue;
            if (!Double.isNaN(m)) {
                mean += m*r;
                rows += r;
            } else return Double.NaN;
        }
        return mean/rows;
    }
    
    /**
     * Get the rms of a column.
     * @param column The column's index.
     * @return The rms of the column. If the rms
     *         cannot be calculated Double.NaN is returned.
     *
     */
    public double columnRms(int column) {
        double rms = 0;
        int  rows = 0;
        for (int i=0; i<set.length; i++) {
            double m = set[i].columnMean(column);
            double rm = set[i].columnRms(column);
            int r = set[i].rows();
            if (r<=0) continue;
            if (!Double.isNaN(m) && !Double.isNaN(rm)) {
                rms += (rm*rm + m*m)*r;
                rows += r;
            } else return Double.NaN;
        }
        return Math.sqrt(rms/rows-Math.pow(columnMean(column),2));
    }
    
    /**
     * The number of rows currently in the ntuple.
     * @return -1 if cannot be determined.
     */
    public int rows() {
        int rows = 0;
        for (int i=0; i<set.length; i++) {
            int r = set[i].rows();
            if (r>0) rows += r;
        }
        return rows;
    }
    
    /**
     * Get the current row.
     * @return The current row;
     *
     */
    public int getRow() {
        return currentRow;
    }
    
    public hep.aida.ITuple findTuple(int column) {
        return tuples[column];
    }

    
    /**
     * Set the current row.
     * @param row The current row;
     * @return True if the opeartion was succesfull.
     *
     */
    public void setRow( int row ) {
        if (row > rows())
            throw new IllegalArgumentException("Row "+row+" is bigger than the length of this ChainedTuple ("+rows()+")");
        int rows = 0;
        int i = 0;
        int r = 0;
        ITuple tup = null;
        for (i=0; i<set.length; i++) {
            r = set[i].rows();
            if (rows<row && (rows+r)>row) break;
            if (r>0) rows += r;
        }
        currentRow = row;
        currentTupleID = i;
        currentTuple = set[currentTupleID];
        currentTupleRow = row-rows;
        currentTuple.setRow(currentTupleRow);
    }
    
    /**
     * Positions the read cursor immediately before the first row.
     */
    public void start() {
        currentRow = -1;
        currentTupleID = 0;
        currentTuple = set[currentTupleID];
        currentTuple.start();
        currentTupleRow = -1;
    }
    
    /**
     * Skips rows.
     * @param rows number of rows to skip, greater than 0.
     * @return false if it cannot skip rows.
     */
    public void skip(int rows) {
        setRow(currentRow+rows);
    }
    
    /**
     * Positions the cursor at the next row.
     * @return false if there is no next row.
     */
    public boolean next() {
        if (currentTuple.next()) {
            currentRow++;
            currentTupleRow++;
            return true;
        } else if (currentTupleID < set.length-1) {
            currentTupleID++;
            currentTuple = set[currentTupleID];
            currentTuple.start();
            currentTupleRow = 0;
            if ( !currentTuple.next()) return next();
            currentRow++;
            return true;
        }
        return false;
    }
    
    /**
     * Convert a name to a column index.
     * Note: C++ version may return -1 if column not found
     * @return the column number for a given name
     */
    public int findColumn(String name) throws IllegalArgumentException {
        return set[0].findColumn(name);
    }
        
    public Object columnDefaultValue(int column) {
        return set[0].columnDefaultValue(column);
    }    
}
