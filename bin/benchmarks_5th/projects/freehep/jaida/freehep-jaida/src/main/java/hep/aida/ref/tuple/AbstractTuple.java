package hep.aida.ref.tuple;

import hep.aida.IAnnotation;
import hep.aida.IBaseTupleColumn;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IEvaluator;
import hep.aida.IFilter;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITuple;
import hep.aida.ITupleColumn;
import hep.aida.OutOfStorageException;
import hep.aida.ref.Annotation;
import hep.aida.ref.ManagedObject;

import java.util.Hashtable;

import org.freehep.util.Value;

/**
 * Base class for Tuple, ChainedTuple etc.
 * This implements all methods which modify the tuple to throw exceptions.
 *
 * @author The AIDA team @ SLAC.
 *
 */

public abstract class AbstractTuple extends ManagedObject implements ITuple, FTuple {
    
    private String title;
    private String options;
    private IAnnotation annotation = new Annotation();
    private Hashtable columnHash = new Hashtable();
    private Value v = new Value();
    private TupleCursor cursor;
    
    public AbstractTuple(String name, String options) {
        this(name, null, options);
    }
    
    public AbstractTuple(String name, String title, String options) {
        super(name);
        setTitle(title);
        this.options = options;
    }
    
    // ITuple-related Methods:
    
    public abstract double columnMax(int index) throws IllegalArgumentException;
    
    public abstract double columnMean(int index) throws IllegalArgumentException;
    
    public abstract double columnMin(int index) throws IllegalArgumentException;
    
    public abstract String columnName(int index) throws IllegalArgumentException;
    
    public abstract double columnRms(int index) throws IllegalArgumentException;
    
    public abstract Class columnType(int index) throws IllegalArgumentException;
    
    public abstract int columns();
    
    public abstract String columnDefaultString( int column );
    
    public abstract void addRow() throws OutOfStorageException;
    
    public abstract int findColumn(String str) throws IllegalArgumentException;
    
    public abstract ITuple findTuple(int index);
    
    public abstract int rows();
    
    public abstract void reset();
    
    public abstract void resetRow();
        
    public abstract void columnValue(int param, Value value);

    public abstract void fill(int param, Value value);

    public FTuple tuple( int index ) {
        return (FTuple) findTuple(index);
    }    
    
    public boolean isInMemory() {
        return true;
    }

    public boolean providesColumnDefaultValues() {
        return true;
    }

    public boolean supportsMultipleCursors() {
        return true;
    }

    public boolean supportsRandomAccess() {
        return true;
    }
        

    // Methods that can be overwritten
    
    int startRow() {
        return 0;
    }
    
    /***************************/
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }
    
    private String defaultStringForType(Class type) {
        String defString = "";
        if ( type == Integer.TYPE )
            defString = "0";
        else if ( type == Short.TYPE )
            defString = "0";
        else if ( type == Long.TYPE )
            defString = "0";
        else if ( type == Float.TYPE )
            defString = "0";
        else if ( type == Double.TYPE )
            defString = "0";
        else if ( type == Boolean.TYPE )
            defString = "false";
        else if ( type == Byte.TYPE )
            defString = "0";
        else if ( type == Character.TYPE ) {
            char[] chArr = new char[] {'\u0000'}; 
            defString = new String(chArr);
        }
        return defString;
    }
    
    public Object columnDefaultValue(int column) {
        Class type = columnType(column);
        String defString = columnDefaultString(column);
        
        // Use predefined default string for the column type,
        // if user did not specify it
        if (isEmpty(defString)) defString = defaultStringForType(type);
        
        if ( type == Integer.TYPE )
            return new Integer(defString);
        else if ( type == Short.TYPE )
            return new Short(defString);
        else if ( type == Long.TYPE )
            return new Long(defString);
        else if ( type == Float.TYPE )
            return new Float(defString);
        else if ( type == Double.TYPE )
            return new Double(defString);
        else if ( type == Boolean.TYPE )
            return new Boolean(defString);
        else if ( type == Byte.TYPE )
            return new Byte(defString);
        else if ( type == Character.TYPE ) {
            return new Character(defString.toCharArray()[0]);
        } else if ( type == ITuple.class )
            return new Tuple(name(), title(), defString, null);
        else
            return defString;
    }
    
    public String getOptions() {
        return options;
    }
    
    public FTupleCursor cursor() {
        return new TupleCursor(this);
    }
    
    public void newInternalCursor() {
        cursor = null;
    }
    
    protected TupleCursor internalCursor() {
        if ( cursor == null ) {
            cursor = new TupleCursor(this);
            cursor.start();
        }
        return cursor;
    }
        
    public void setRow( int row ) {
        internalCursor().setRow(row);
    }
    
    public void start() {
        internalCursor().start();
    }
    
    public void skip(int rows) {
        internalCursor().skip(rows);
    }
    
    public boolean next() {
        return internalCursor().next();
    }
    
    public int getRow() {
        return internalCursor().row();
    }
    
    public void columnValue(int index, FTupleCursor c, Value v) {
        if ( ! supportsRandomAccess() && c.row() != getRow() )
            throw new RuntimeException("This tuple does not support random access");
        setRow(c.row());
        columnValue(index,v);
    }
    
    public boolean getBoolean(int index) throws ClassCastException {
        if ( columnType(index) != Boolean.TYPE )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to boolean.");
        columnValue(index, v);
        return v.getBoolean();
    }
    
    public byte getByte(int index) throws ClassCastException {
        if ( columnType(index) != Byte.TYPE )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to byte.");
        columnValue(index, v);
        return v.getByte();
    }
    
    public char getChar(int index) throws ClassCastException {
        if ( columnType(index) != Character.TYPE )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to char.");
        columnValue(index, v);
        return v.getChar();
    }
    
    public double getDouble(int index) throws ClassCastException {
        if ( columnType(index) != Double.TYPE )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to double.");
        columnValue(index, v);
        return v.getDouble();
    }
    
    public float getFloat(int index) throws ClassCastException {
        if ( columnType(index) != Float.TYPE )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to float.");
        columnValue(index, v);
        return v.getFloat();
    }
    
    public int getInt(int index) throws ClassCastException {
        if ( columnType(index) != Integer.TYPE )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to int.");
        columnValue(index, v);
        return v.getInt();
    }
    
    public long getLong(int index) throws ClassCastException {
        if ( columnType(index) != Long.TYPE )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to long.");
        columnValue(index, v);
        return v.getLong();
    }
    
    public Object getObject(int index) throws ClassCastException {
        if ( columnType(index).isPrimitive() )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to Object.");
        columnValue(index, v);
        return v.getObject();
    }
    
    public short getShort(int index) throws ClassCastException {
        if ( columnType(index) != Short.TYPE )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to short.");
        columnValue(index, v);
        return v.getShort();
    }
    
    public String getString(int index) throws ClassCastException {
        if ( columnType(index) != String.class )
            throw new ClassCastException("Cannot cast column "+columnName(index)+"  to String.");
        columnValue(index, v);
        return v.getString();
    }
    
    public void fill(double[] values) throws IllegalArgumentException {
        int nCol = columns();
        if ( values.length != nCol ) throw new IllegalArgumentException("Wrong number of values provided "+values.length+". It has to match the number of columns "+nCol);
        for( int i = 0; i < nCol; i++ )
            fill(i,v.set(values[i]));
    }
    
    public void fill(float[] values) throws IllegalArgumentException {
        int nCol = columns();
        if ( values.length != nCol ) throw new IllegalArgumentException("Wrong number of values provided "+values.length+". It has to match the number of columns "+nCol);
        for( int i = 0; i < nCol; i++ )
            fill(i,v.set(values[i]));
    }
    
    public void fill(int index, boolean value) throws IllegalArgumentException {
        fill(index,v.set(value));
    }
    
    public void fill(int index, byte value) throws IllegalArgumentException {
        fill(index,v.set(value));
    }
    
    public void fill(int index, char value) throws IllegalArgumentException {
        fill(index,v.set(value));
    }
    
    public void fill(int index, double value) throws IllegalArgumentException {
        fill(index,v.set(value));
    }
    
    public void fill(int index, float value) throws IllegalArgumentException {
        fill(index,v.set(value));
    }
    
    public void fill(int index, int value) throws IllegalArgumentException {
        fill(index,v.set(value));
    }
    
    public void fill(int index, Object obj) throws IllegalArgumentException {
        fill(index,v.set(obj));
    }
    
    public void fill(int index, String str) throws IllegalArgumentException {
        fill(index,v.set(str));
    }
    
    public void fill(int index, long value) throws IllegalArgumentException {
        fill(index,v.set(value));
    }
    
    public void fill(int index, short value) throws IllegalArgumentException {
        fill(index,v.set(value));
    }
    
    public String[] columnNames() {
        String[] result = new String[columns()];
        for (int i=0; i<result.length; i++) result[i] = columnName(i);
        return result;
    }
    public Class[] columnTypes() {
        Class[] result = new Class[columns()];
        for (int i=0; i<result.length; i++) result[i] = columnType(i);
        return result;
    }
    
    public String title() {
        return title == null ? name() : title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public hep.aida.IAnnotation annotation() {
        return annotation;
    }
    
    public void setAnnotation( IAnnotation annotation ) {
        this.annotation = annotation;
    }
    
    public ITuple getTuple(int col) {
        return findTuple(col);
    }
    
    public IBaseTupleColumn column(int index) {
        return column(columnName(index));
    }
    
    public IBaseTupleColumn column(String name) {
        Object col = columnHash.get(name);
        if ( col == null ) {
            col = AbstractTupleColumnFactory.createColumn(this, findColumn(name) );
            columnHash.put(name, col);
        }
        return (IBaseTupleColumn) col;
    }
    
    public FTupleColumn columnByIndex(int index) {
        return (FTupleColumn)column(index);
    }
    
    public FTupleColumn columnByName(String name) {
        return (FTupleColumn) column(name);
    }
    
    // Evaluate Min
    public double evaluateMin(IEvaluator evaluator) throws IllegalArgumentException {
        double min = Double.NaN;
        start();
        evaluator.initialize(this);
        double tmp;
        while ( next() ) {
            tmp = evaluator.evaluateDouble();
            if ( Double.isNaN(min) || tmp < min )
                min = tmp;
        }
        return min;
    }
    
    public double evaluateMin(IEvaluator evaluator, IFilter filter) throws IllegalArgumentException {
        double min = Double.NaN;
        start();
        evaluator.initialize(this);
        double tmp;
        while ( next() ) {
            if ( filter.accept() ) {
                tmp = evaluator.evaluateDouble();
                if ( Double.isNaN(min) || tmp < min )
                    min = tmp;
            }
        }
        return min;
    }
    
    // Evaluate Max
    public double evaluateMax(IEvaluator evaluator) throws IllegalArgumentException {
        double max = Double.NaN;
        start();
        evaluator.initialize(this);
        double tmp;
        while ( next() ) {
            tmp = evaluator.evaluateDouble();
            if ( Double.isNaN(max) || tmp > max )
                max = tmp;
        }
        return max;
    }
    
    public double evaluateMax(IEvaluator evaluator, IFilter filter) throws IllegalArgumentException {
        double max = Double.NaN;
        start();
        evaluator.initialize(this);
        double tmp;
        while ( next() ) {
            if ( filter.accept() ) {
                tmp = evaluator.evaluateDouble();
                if ( Double.isNaN(max) || tmp > max )
                    max = tmp;
            }
        }
        return max;
    }
    
    // Projections on IHistogram1D
    
    public void project(IHistogram1D histogram, IEvaluator evaluator) {
        start();
        evaluator.initialize(this);
        while ( next() )
            histogram.fill( evaluator.evaluateDouble() );
    }
    
    
    public void project(IHistogram1D histogram, IEvaluator evaluator, IEvaluator weightEvaluator) {
        start();
        evaluator.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            histogram.fill( evaluator.evaluateDouble(), weightEvaluator.evaluateDouble() );
    }
    
    public void project(IHistogram1D histogram, IEvaluator evaluator, IFilter filter, IEvaluator weightEvaluator) {
        start();
        filter.initialize(this);
        evaluator.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            if ( filter.accept() )
                histogram.fill( evaluator.evaluateDouble(), weightEvaluator.evaluateDouble() );
    }
    
    public void project(IHistogram1D histogram, IEvaluator evaluator, IFilter filter) {
        start();
        evaluator.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                histogram.fill( evaluator.evaluateDouble() );
    }
    
    // Projections on IHistogram2D
    
    public void project(IHistogram2D histogram, IEvaluator evaluatorX, IEvaluator evaluatorY, IFilter filter) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                histogram.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble());
    }
    
    public void project(IHistogram2D histogram, IEvaluator evaluatorX, IEvaluator evaluatorY) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        while ( next() )
            histogram.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble());
    }
    public void project(IHistogram2D histogram, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            histogram.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    public void project(IHistogram2D histogram, IEvaluator evaluatorX, IEvaluator evaluatorY, IFilter filter, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        weightEvaluator.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                histogram.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    // Projections on IHistogram3D
    
    public void project(IHistogram3D histogram, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ, IFilter filter) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                histogram.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble());
    }
    
    public void project(IHistogram3D histogram, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        while ( next() )
            histogram.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble());
    }
    
    public void project(IHistogram3D histogram, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            histogram.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    public void project(IHistogram3D histogram, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ, IFilter filter, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        weightEvaluator.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                histogram.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    // Projections on ICloud1D
    
    public void project(ICloud1D cloud, IEvaluator evaluator) {
        start();
        evaluator.initialize(this);
        while ( next() )
            cloud.fill( evaluator.evaluateDouble() );
    }
    
    public void project(ICloud1D cloud, IEvaluator evaluator, IFilter filter) {
        start();
        evaluator.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                cloud.fill( evaluator.evaluateDouble() );
    }
    
    public void project(ICloud1D cloud, IEvaluator evaluator, IEvaluator weightEvaluator) {
        start();
        evaluator.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            cloud.fill( evaluator.evaluateDouble(), weightEvaluator.evaluateDouble() );
    }
    
    public void project(ICloud1D cloud, IEvaluator evaluator, IFilter filter, IEvaluator weightEvaluator) {
        start();
        filter.initialize(this);
        evaluator.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            if ( filter.accept() )
                cloud.fill( evaluator.evaluateDouble(), weightEvaluator.evaluateDouble() );
    }
    
    // Projections on ICloud2D
    
    public void project(ICloud2D cloud, IEvaluator evaluatorX, IEvaluator evaluatorY) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        while ( next() )
            cloud.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble());
    }
    
    public void project(ICloud2D cloud, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            cloud.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    public void project(ICloud2D cloud, IEvaluator evaluatorX, IEvaluator evaluatorY, IFilter filter) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                cloud.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble());
    }
    
    public void project(ICloud2D cloud, IEvaluator evaluatorX, IEvaluator evaluatorY, IFilter filter, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        weightEvaluator.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                cloud.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    // Projections on ICloud3D
    
    public void project(ICloud3D cloud, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        while ( next() )
            cloud.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble());
    }
    
    public void project(ICloud3D cloud, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ, IFilter filter) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                cloud.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble());
    }
    
    public void project(ICloud3D cloud, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            cloud.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    public void project(ICloud3D cloud, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ, IFilter filter, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        weightEvaluator.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                cloud.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    // Projections on IProfile1D
    
    public void project(IProfile1D profile, IEvaluator evaluatorX, IEvaluator evaluatorY) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        while ( next() )
            profile.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble());
    }
    
    public void project(IProfile1D profile, IEvaluator evaluatorX, IEvaluator evaluatorY, IFilter filter) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                profile.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble());
    }
    
    public void project(IProfile1D profile, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            profile.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    public void project(IProfile1D profile, IEvaluator evaluatorX, IEvaluator evaluatorY, IFilter filter, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        weightEvaluator.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                profile.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    // Projections on IProfile1D
    
    public void project(IProfile2D profile, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        while ( next() )
            profile.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble());
    }
    
    public void project(IProfile2D profile, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        weightEvaluator.initialize(this);
        while ( next() )
            profile.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble(), weightEvaluator.evaluateDouble());
    }
    
    public void project(IProfile2D profile, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ, IFilter filter) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                profile.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble());
    }
    
    public void project(IProfile2D profile, IEvaluator evaluatorX, IEvaluator evaluatorY, IEvaluator evaluatorZ, IFilter filter, IEvaluator weightEvaluator) {
        start();
        evaluatorX.initialize(this);
        evaluatorY.initialize(this);
        evaluatorZ.initialize(this);
        weightEvaluator.initialize(this);
        filter.initialize(this);
        while ( next() )
            if ( filter.accept() )
                profile.fill( evaluatorX.evaluateDouble(), evaluatorY.evaluateDouble(), evaluatorZ.evaluateDouble(), weightEvaluator.evaluateDouble());
    }

    public static abstract class AbstractTupleColumnFactory {
        
        public static IBaseTupleColumn createColumn(hep.aida.ITuple tuple, int columnIndex) {
            Class type = tuple.columnType(columnIndex);
            if ( type == Boolean.TYPE )
                return new BooleanTupleColumn(tuple, columnIndex);
            else if ( type == Float.TYPE )
                return new FloatTupleColumn(tuple, columnIndex);
            else if ( type == Integer.TYPE )
                return new IntTupleColumn(tuple, columnIndex);
            else if ( type == Double.TYPE )
                return new DoubleTupleColumn(tuple, columnIndex);
            else if ( type == Short.TYPE )
                return new ShortTupleColumn(tuple, columnIndex);
            else if ( type == Long.TYPE )
                return new LongTupleColumn(tuple, columnIndex);
            else if ( type == Character.TYPE )
                return new CharTupleColumn(tuple, columnIndex);
            else if ( type == Byte.TYPE )
                return new ByteTupleColumn(tuple, columnIndex);
            else if ( type == String.class )
                return new StringTupleColumn(tuple, columnIndex);
            else if ( type == hep.aida.ITuple.class )
                return new ITupleTupleColumn(tuple, columnIndex);
            else
                return new ObjectTupleColumn(tuple, columnIndex);
        }
        
        
        public static class BaseTupleColumn implements IBaseTupleColumn, FTupleColumn {
            
            ITuple tuple;
            int index;
            
            BaseTupleColumn(hep.aida.ITuple tuple, int index) {
                this.tuple = tuple;
                this.index = index;
            }
            
            public String name() {
                return tuple.columnName(index);
            }
            
            public Class type() {
                return tuple.columnType(index);
            }
            
            public double minimum() {
                return tuple.columnMin(index);
            }
            
            public double maximum() {
                return tuple.columnMax(index);
            }
            
            public double mean() {
                return tuple.columnMean(index);
            }
            
            public double rms() {
                return tuple.columnRms(index);
            }

            public void defaultValue(Value value) {
                value.set(tuple.columnDefaultValue(index));
            }

            public boolean hasDefaultValue() {
                return true;
            }

            public void maxValue(Value value) {
                value.set(maximum());
            }

            public void meanValue(Value value) {
                value.set(mean());
            }

            public void minValue(Value value) {
                value.set(minimum());
            }

            public void rmsValue(Value value) {
                value.set(rms());
            }
            
        }
        
        
        public static class BooleanTupleColumn extends BaseTupleColumn implements ITupleColumn.Z {
            
            BooleanTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(boolean value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public boolean value() {
                return tuple.getBoolean(index);
            }
            
            public boolean defaultValue() {
                return ((Boolean)tuple.columnDefaultValue(index)).booleanValue();
            }
            
            public boolean fillableObject() {
                return defaultValue();
            }
            
        }
        
        public static class FloatTupleColumn extends BaseTupleColumn implements ITupleColumn.F {
            
            FloatTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(float value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public float value() {
                return tuple.getFloat(index);
            }
            
            public float defaultValue() {
                return ((Float)tuple.columnDefaultValue(index)).floatValue();
            }
            
            public float fillableObject() {
                return defaultValue();
            }
        }
        
        public static class DoubleTupleColumn extends BaseTupleColumn implements ITupleColumn.D {
            
            DoubleTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(double value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public double value() {
                return tuple.getDouble(index);
            }
            
            public double defaultValue() {
                return ((Double)tuple.columnDefaultValue(index)).doubleValue();
            }
            
            public double fillableObject() {
                return defaultValue();
            }
        }
        
        public static class ByteTupleColumn extends BaseTupleColumn implements ITupleColumn.B {
            
            ByteTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(byte value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public byte value() {
                return tuple.getByte(index);
            }
            
            public byte defaultValue() {
                return ((Byte)tuple.columnDefaultValue(index)).byteValue();
            }
            
            public byte fillableObject() {
                return defaultValue();
            }
        }
        
        public static class ShortTupleColumn extends BaseTupleColumn implements ITupleColumn.S {
            
            ShortTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(short value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public short value() {
                return tuple.getShort(index);
            }
            
            public short defaultValue() {
                return ((Short)tuple.columnDefaultValue(index)).shortValue();
            }
            
            public short fillableObject() {
                return defaultValue();
            }
        }
        
        public static class IntTupleColumn extends BaseTupleColumn implements ITupleColumn.I {
            
            IntTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(int value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public int value() {
                return tuple.getInt(index);
            }
            
            public int defaultValue() {
                return ((Integer)tuple.columnDefaultValue(index)).intValue();
            }
            
            public int fillableObject() {
                return defaultValue();
            }
        }
        
        public static class LongTupleColumn extends BaseTupleColumn implements ITupleColumn.L {
            
            LongTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(long value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public long value() {
                return tuple.getLong(index);
            }
            
            public long defaultValue() {
                return ((Long)tuple.columnDefaultValue(index)).longValue();
            }
            
            public long fillableObject() {
                return defaultValue();
            }
        }
        
        public static class CharTupleColumn extends BaseTupleColumn implements ITupleColumn.C {
            
            CharTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(char value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public char value() {
                return tuple.getChar(index);
            }
            
            public char defaultValue() {
                return ((Character)tuple.columnDefaultValue(index)).charValue();
            }
            
            public char fillableObject() {
                return defaultValue();
            }
        }
        
        public static class StringTupleColumn extends BaseTupleColumn implements ITupleColumn.String {
            
            StringTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(java.lang.String value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public java.lang.String value() {
                return tuple.getString(index);
            }
            
            public java.lang.String defaultValue() {
                return (java.lang.String)tuple.columnDefaultValue(index);
            }
            
            public java.lang.String fillableObject() {
                return defaultValue();
            }
        }
        
        public static class ITupleTupleColumn extends BaseTupleColumn implements ITupleColumn.ITuple {
            
            ITupleTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public Class type() {
                return hep.aida.ITuple.class;
            }
            
            public void fill(hep.aida.ITuple value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public hep.aida.ITuple value() {
                return (hep.aida.ITuple) tuple.getObject(index);
            }
            
            public hep.aida.ITuple defaultValue() {
                return (hep.aida.ITuple)tuple.columnDefaultValue(index);
            }
            
            public hep.aida.ITuple fillableObject() {
                return tuple.findTuple(index);
            }
        }
        
        public static class ObjectTupleColumn extends BaseTupleColumn implements ITupleColumn.Object {
            
            ObjectTupleColumn(hep.aida.ITuple tuple, int index) {
                super(tuple, index);
            }
            
            public void fill(java.lang.Object value) throws IllegalArgumentException {
                tuple.fill(index,value);
            }
            
            public java.lang.Object value() {
                return tuple.getObject(index);
            }
            
            public java.lang.Object defaultValue() {
                return tuple.columnDefaultValue(index);
            }
            
            public java.lang.Object fillableObject() {
                return defaultValue();
            }
        }
    }
    
}
