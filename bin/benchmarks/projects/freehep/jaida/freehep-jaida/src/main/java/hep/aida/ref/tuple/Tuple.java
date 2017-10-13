package hep.aida.ref.tuple;

import hep.aida.IBaseTupleColumn;
import hep.aida.ITuple;
import hep.aida.ref.AidaUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.freehep.util.Value;

/**
 *
 * @author  The FreeHEP team @ SLAC.
 *
 */
public class Tuple extends AbstractTuple {
    
    protected static final int COLUMN_ROWS = 1000;
    private int columnLength;
    private int columnMaxLength;
    
    private String tupColumnsString = "";
    
    private int startRow = 0;
    private int endRow = -1;
    private int filledRows = 0;
    
    private Map optionMap;
    
    private String[] columnDefaultValues;
    
    private ArrayList tupleColumns = new ArrayList();
    private ArrayList folderList = new ArrayList();
    private Hashtable columnCounters = new Hashtable();
    
    private Value tupleValue = new Value();
    
    public Tuple(String name, String title, String[] columnName, Class[] columnType, String options) {
        super(name, title, options);
        initTuple(name, title,columnName,columnType,options);
    }
    
    public Tuple(String name, String title, String columnsString, String options) {
        super(name, title, options);
        initTuple(name, title,columnsString,options);
    }
    
    private int numberOf(String str, String s) {
        int n = 0;
        int i = str.indexOf(s);
        if ( i <0 )
            return n;
        else {
            n++;
            n += numberOf(str.substring(i+1),s);
        }
        return n;
    }
    
    private void initTuple( String name, String title, String columnsString, String options) {
        setTitle(title);
        
        this.tupColumnsString = columnsString;
        
        optionMap = AidaUtils.parseOptions(options);
        
        StringTokenizer strT = new StringTokenizer( columnsString,",;");
        int bracketCounter = 0;
        int nCol = 0;
        while( strT.hasMoreTokens() ) {
            String token = strT.nextToken();
            if ( bracketCounter == 0 ) nCol++;
            bracketCounter += numberOf(token,"{");
            bracketCounter -= numberOf(token,"}");
        }
        if ( nCol < 1 ) throw new IllegalArgumentException("Cannot build Tuple with no columns\n");
        
        StringTokenizer st = new StringTokenizer(columnsString,",;");
        
        int columnIndex = 0;
        bracketCounter = 0;
        String iTupleColString = "";
        String type = "";
        
        int colDefCount = 0;
        columnDefaultValues = new String[nCol];
        
        while( st.hasMoreTokens() ) {
            String token = st.nextToken();
            token.trim();
            
            bracketCounter += numberOf(token,"{");
            bracketCounter -= numberOf(token,"}");
            
            iTupleColString += token;
            
            if ( st.hasMoreTokens() ) iTupleColString += ";";
            
            if ( bracketCounter == 0 ) {
                String colName;
                String defaultValue = "";
                
                iTupleColString = iTupleColString.trim();
                int brPos = iTupleColString.indexOf("{");
                if ( brPos > 0 ) {
                    iTupleColString.trim();
                    int pos = iTupleColString.indexOf("(");
                    if ( pos > 0 && pos < brPos ) {
                        type = iTupleColString.substring(0,pos).trim();
                        colName = iTupleColString.substring( iTupleColString.indexOf(")")+1, brPos).trim();
                        if ( colName.endsWith("=") ) colName = colName.substring(0,colName.length()-1).trim();
                        defaultValue += iTupleColString.substring( pos+1, iTupleColString.indexOf(")") ).trim();
                        defaultValue += iTupleColString.substring( brPos );
                    } else {
                        int spPos = iTupleColString.indexOf(" ");
                        int eqPos = iTupleColString.indexOf("=");
                        type = iTupleColString.substring(0,spPos).trim();
                        colName = iTupleColString.substring(spPos+1,eqPos).trim();
                        defaultValue = iTupleColString.substring( brPos ).trim();
                    }
                } else {
                    StringTokenizer t = new StringTokenizer( token, "=");
                    int tokens = t.countTokens();
                    
                    String nameAndType = t.nextToken().trim();
                    
                    if ( tokens == 2 ) defaultValue = t.nextToken().trim();
                    else if ( tokens != 1 ) throw new IllegalArgumentException("Wrong format "+token+"\n");
                    
                    StringTokenizer tt = new StringTokenizer( nameAndType, " " );
                    if ( tt.countTokens() == 2 ) type = tt.nextToken().trim();
                    colName = tt.nextToken().trim();
                }
                
                Value value = new Value();
                
                Class colType = null;
                if ( type.endsWith("ITuple") ) {
                    String defVal = defaultValue.substring(defaultValue.indexOf("{")+1,defaultValue.lastIndexOf("}"));
                    Tuple tup = new Tuple( colName, "", defVal, options);
                    addTuple( tup );
                    columnDefaultValues[colDefCount++] = defVal;
                } else {
                    if ( type.equals("int") )          colType = Integer.TYPE;
                    else if ( type.equals("short") )   colType = Short.TYPE;
                    else if ( type.equals("long") )    colType = Long.TYPE;
                    else if ( type.equals("float") )   colType = Float.TYPE;
                    else if ( type.equals("double") )  colType = Double.TYPE;
                    else if ( type.equals("boolean") ) colType = Boolean.TYPE;
                    else if ( type.equals("byte") )    colType = Byte.TYPE;
                    else if ( type.equals("char") )    colType = Character.TYPE;
                    else if ( type.equals("string") )  colType = String.class;
                    else {
                        try {
                            colType = Class.forName( type );
                        } catch ( ClassNotFoundException cnfe ) {
                            throw new IllegalArgumentException("Unsupported type "+type);
                        }
                    }
                    
                    fillDefaultValue(colType, defaultValue,value);
                    TupleColumn fcolumn = TupleColumnFactory.createTupleColumn(colName, colType, value, options, this);
                    addColumn( fcolumn );
                    columnDefaultValues[colDefCount++] = defaultValue;
                }
                iTupleColString = "";
            }
        }
        
        start();
    }
    
    
    private void initTuple(String name, String title, String[] columnName, Class[] columnType, String options){
        
        setTitle(title);
        
        optionMap = AidaUtils.parseOptions(options);
        
        int colNames = columnName.length;
        int colTypes = columnType.length;
        
        if ( colNames < 1 ) throw new IllegalArgumentException("columnName cannot be empty\n");
        if ( colTypes < 1 ) throw new IllegalArgumentException("columnType cannot be empty\n");
        if ( colNames != colTypes ) throw new IllegalArgumentException("The number of column names "
                +colNames+" is different than the number of column types "+colTypes+"\n");
        
        int colDefCount = 0;
        columnDefaultValues = new String[colNames];
        
        for (int i=0; i<colNames; i++) {
            Class colType = columnType[i];
            String colName ="";
            String defaultValue = "";
            tupColumnsString += colType + " " + columnName[i];
            if ( i < colNames ) tupColumnsString += ";";
            
            Value value = new Value();
            
            if ( colType == ITuple.class ) {
                String iTupleString = columnName[i];
                int pos = iTupleString.indexOf(")");
                if ( pos > 0 ) {
                    colName = iTupleString.substring(pos+1,iTupleString.indexOf("{")).trim();
                    if ( colName.endsWith("=") ) colName = colName.substring(0,colName.length()-1).trim();
                    defaultValue += iTupleString.substring(iTupleString.indexOf("(")+1,pos);
                } else {
                    if (iTupleString.indexOf("=") > 0)
                        colName = iTupleString.substring(0,iTupleString.indexOf("=")).trim();
                    else colName = iTupleString.trim();
                }
                if (iTupleString.indexOf("{") > 0)
                    defaultValue += iTupleString.substring(iTupleString.indexOf("{"),iTupleString.lastIndexOf("}")+1).trim();
                
                String defVal = defaultValue.substring(defaultValue.indexOf("{")+1,defaultValue.lastIndexOf("}"));
                
                Tuple tup = new Tuple( colName, "", defVal, options);
                addTuple( tup );
                columnDefaultValues[colDefCount++] = defVal;
                
            } else {
                colName = columnName[i];
                StringTokenizer st = new StringTokenizer(colName,"=");
                colName = st.nextToken().trim();
                if ( st.hasMoreTokens() ) defaultValue = st.nextToken().trim();
                
                fillDefaultValue(colType, defaultValue,value);
                TupleColumn fcolumn = TupleColumnFactory.createTupleColumn(colName, colType, value, options, this);
                addColumn( fcolumn );
                columnDefaultValues[colDefCount++] = defaultValue;
            }
            
        }
        
        start();
    }
    
    // FIXME is this needed?
    private String getColString() {
        return tupColumnsString;
    }
    
    private void fillDefaultValue( Class type, String defaultValue, Value value ) {
        if ( type == Integer.TYPE )
            value.set( defaultValue.equals("") ? (int)0 : Integer.parseInt(defaultValue) );
        else if ( type == Short.TYPE )
            value.set( defaultValue.equals("") ? (short)0 : Short.parseShort(defaultValue) );
        else if ( type == Long.TYPE )
            value.set( defaultValue.equals("") ? (long)0 : Long.parseLong(defaultValue) );
        else if ( type == Float.TYPE )
            value.set( defaultValue.equals("") ? (float)0 : Float.parseFloat(defaultValue) );
        else if ( type == Double.TYPE )
            value.set( defaultValue.equals("") ? (double)0 : Double.parseDouble(defaultValue) );
        else if ( type == Boolean.TYPE )
            value.set( defaultValue.equals("") ? false : Boolean.valueOf(defaultValue).booleanValue() );
        else if ( type == Byte.TYPE )
            value.set( defaultValue.equals("") ? (byte)0 : Byte.parseByte(defaultValue) );
        else if ( type == Character.TYPE )
            value.set( defaultValue.equals("") ? (char)0 : defaultValue.charAt(0) );
        else value.set( defaultValue );
    }
    
    
    public String columnDefaultString( int column ) {
        if ( columnType( column ) != ITuple.class )
            return columnDefaultValues[column];
        else {
            Tuple tup = (Tuple) findTuple(column);
            if ( tup != null )
                return tup.name()+" = {"+tup.getColString()+"}";
            else
                return "null";
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    public boolean providesColumnDefaultValues() {
        return true;
    }
    
    
    /**
     * Get Folder at the current cursor position.
     * @param index The column's index of the Folder.
     * @return The folder.
     *
     */
    public ITuple findTuple(int index) {
        return getFolder( index, filledRows );
    }
    
    public void fill(int column, Value value) {
        tupleColumn(column).fill( value );
    }
    
    public void columnValue(int column, FTupleCursor cursor, Value value) {
        if ( columnType( column ) != ITuple.class )
            tupleColumn(column).value(cursor, value);
        else
            value.set( getFolder( column, cursor.row() ) );
    }
    
    public void columnValue(int column, Value value) {
        if ( columnType( column ) != ITuple.class )
            tupleColumn(column).value(internalCursor(), value);
        else
            value.set( getFolder( column, internalCursor().row() ) );
    }
    
    public boolean isInMemory() {
        return true;
    }
    
    public boolean supportsMultipleCursors() {
        return true;
    }
    
    public boolean supportsRandomAccess() {
        return true;
    }
    
    public void addRow() {
        for ( int i = 0; i < columns(); i++ )
            tupleColumn(i).addRow();
        
        for ( Enumeration keys = columnCounters.keys(); keys.hasMoreElements(); ) {
            String folderName = (String)keys.nextElement();
            Tuple folder = (Tuple) getFolder( findColumn( folderName ), filledRows );
            
            TupleColumn tc = (TupleColumn)columnCounters.get( folderName );
            tc.fill( tupleValue.set( folder.startRow()+folder.rows() ) );
            tc.addRow();
        }
        filledRows++;
        newInternalCursor();
    }
    
    public void resetRow() {
        for ( int i = 0; i<columns(); i++ )
            ((TupleColumn)tupleColumns.get(i)).resetRow();
        for ( Enumeration keys = columnCounters.keys(); keys.hasMoreElements(); ) {
            String folderName = (String)keys.nextElement();
            Tuple folder = (Tuple) getFolder( findColumn( folderName ), filledRows );
            folder.resetRows( folder.rows() );
            TupleColumn tc = (TupleColumn)columnCounters.get( folder.name() );
            tc.resetRow();
        }
    }
    
    public void reset() {
        for ( int i = 0; i<columns(); i++ )
            ((TupleColumn)tupleColumns.get(i)).reset();
        filledRows = 0;
        newInternalCursor();
    }
    
    public int rows() {
        if ( endRow != -1 ) return endRow-startRow;
        return filledRows-startRow;
    }
    
    public int findColumn(String name) {
        for ( int i = 0; i<columns(); i++ )
            if ( columnName(i).equals( name ) )
                return i;
        throw new IllegalArgumentException("Column "+name+" does not exist\n");
    }
    
    public String columnName(int column) {
        return column(column).name();
    }
    
    public Class columnType(int column) {
        return column(column).type();
    }
    
    public double columnMin(int column) {
        tupleColumn(column).minValue(tupleValue);
        return tupleValue.getDouble();
    }
    
    public double columnMax(int column) {
        tupleColumn(column).maxValue(tupleValue);
        return tupleValue.getDouble();
    }
    
    public double columnMean(int column) {
        tupleColumn(column).meanValue(tupleValue);
        return tupleValue.getDouble();
    }
    
    public double columnRms(int column) {
        tupleColumn(column).rmsValue(tupleValue);
        return tupleValue.getDouble();
    }
    
    public int columns() {
        return tupleColumns.size();
    }
    
    
    
    
    int startRow() {
        return startRow;
    }
    
    void addColumn(TupleColumn column) {
        tupleColumns.add( column );
    }
    
    void removeColumn(TupleColumn column) {
        tupleColumns.remove(column);
    }
    
    void addTuple(Tuple tuple) {
        columnCounters.put( tuple.name(), TupleColumnFactory.createTupleColumn(tuple.name(), Integer.TYPE, new Value().set((int)0), "length=1000; maxlength=-1", this) );
        addColumn( TupleColumnFactory.createFolderColumn( tuple.name(), tuple, "", this ) );
        folderList.add(tuple);
    }
    
    void removeTuple(Tuple tuple) {
        String name = ((Tuple)tuple).name();
        columnCounters.remove(name);
        TupleColumn col = (TupleColumn) column(name);
        removeColumn(col);
        folderList.remove(tuple);
    }
    
    
    // PRIVATE METHODS
    /**
     * Set the start row.
     * @param startRow The start row.
     *
     */
    private void setStartEndRow( int startRow, int endRow ) {
        this.startRow = startRow;
        this.endRow = endRow;
    }
    
    /**
     * Clears the values on the stack.
     */
    private void resetRows( int numberOfRows ) {
        for ( int i = 0; i<columns(); i++ )
            ((TupleColumn)tupleColumns.get(i)).resetRows( numberOfRows );
        int cf = filledRows;
        filledRows -= numberOfRows;
        
        for ( Enumeration keys = columnCounters.keys(); keys.hasMoreElements(); ) {
            String folderName = (String)keys.nextElement();
            while ( numberOfRows-- > 0 ) {
                cf--;
                Tuple folder = (Tuple) getFolder( findColumn( folderName ), cf );
                TupleColumn tc = (TupleColumn)columnCounters.get( folder.name() );
                
                tc.value(numberOfRows, tupleValue);
                int end =  tupleValue.getInt();
                int start = 0;
                if ( numberOfRows > 0 ) {
                    tc.value(numberOfRows-1, tupleValue);
                    start =  tupleValue.getInt();
                }
                folder.setStartEndRow(start,end);
                folder.resetRows( folder.rows() );
                
                tc.resetRows(1);
            }
        }
    }
    
    /**
     * Get a Folder in a given configuration.
     * @param index The column's index for this Folder.
     * @param cursor The cursor position for the Folder.
     *
     */
    private Tuple getFolder( int index, int cursor ) {
        Tuple tup = (Tuple)((TupleColumn)tupleColumns.get( index )).getDefaultValue().getObject();
        TupleColumn tc = (TupleColumn)columnCounters.get( tup.name() );
        if ( cursor > -1 && cursor < filledRows ) {
            tc.value(cursor, tupleValue);
            int end = tupleValue.getInt();
            int start = 0;
            if ( cursor > 0 ) {
                tc.value(cursor-1, tupleValue);
                start = tupleValue.getInt();
            }
            tup.setStartEndRow( start, end );
        } else if ( cursor == filledRows && cursor > 0 ) {
            tc.value(cursor-1, tupleValue);
            int start = tupleValue.getInt();
            tup.setStartEndRow( start, -1 );
        }
        tup.newInternalCursor();
        return (Tuple) tup;
    }
    
    public IBaseTupleColumn column( int column ) {
        return (IBaseTupleColumn)tupleColumns.get( column );
    }
    
    public IBaseTupleColumn column( String name ) {
        return column( findColumn( name ) );
    }
    
    private TupleColumn tupleColumn(int column) {
        return (TupleColumn) column(column);
    }
    
    
    public abstract static class TupleColumnFactory {
        
        public static TupleColumn createTupleColumn(String name, Class type, Value value, Tuple tuple) {
            return createTupleColumn(name, type, value, "", tuple);
        }
        
        public static TupleColumn createFolderColumn(String name, Tuple folderTuple, String options, Tuple parentTuple) {
            return new TupleColumn.TupleColumnFolder( name, folderTuple, options, parentTuple);
        }
        
        public static TupleColumn createTupleColumn(String name, Class type, Value value, String options, Tuple tuple) {
            if ( type == ITuple.class )
                throw new RuntimeException("Cannot create Folder tuple. Use createFolderColumn method instead.");
            if ( type == Integer.TYPE ) return new TupleColumn.TupleColumnInt( name, value, options, tuple);
            else if ( type == Short.TYPE ) return new TupleColumn.TupleColumnShort( name, value, options, tuple);
            else if ( type == Long.TYPE ) return new TupleColumn.TupleColumnLong( name, value, options, tuple);
            else if ( type == Float.TYPE ) return new TupleColumn.TupleColumnFloat( name, value, options, tuple);
            else if ( type == Double.TYPE ) return new TupleColumn.TupleColumnDouble( name, value, options, tuple);
            else if ( type == Boolean.TYPE ) return new TupleColumn.TupleColumnBoolean( name, value, options, tuple);
            else if ( type == Byte.TYPE ) return new TupleColumn.TupleColumnByte( name, value, options, tuple);
            else if ( type == Character.TYPE ) return new TupleColumn.TupleColumnChar( name, value, options, tuple);
            else if ( type == String.class ) return new TupleColumn.TupleColumnString( name, value, options, tuple);
            else return new TupleColumn.TupleColumnObject( name, type, value, options, tuple);
        }
    }
    
    
}
