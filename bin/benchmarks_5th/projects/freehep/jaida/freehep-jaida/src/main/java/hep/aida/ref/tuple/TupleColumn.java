/*
 * TupleColumn.java
 *
 * Created on February 12, 2002, 4:16 PM
 */

package hep.aida.ref.tuple;

import hep.aida.IBaseTupleColumn;
import hep.aida.ITupleColumn;
import hep.aida.ref.AidaUtils;

import java.util.ArrayList;
import java.util.Map;

import org.freehep.util.Value;

/**
 *
 * @author  The FreeHEP team @ SLAC.
 *
 */
public abstract class TupleColumn implements IBaseTupleColumn, FTupleColumn{
    
    /**
     * Each TupleColumn should be created with a given size, length = arraySize.
     * The TupleColumn is divided in three regions:
     * -1- a filled region for permanent storage.
     * -2- a stack region for temporary storage; the value on the stack
     *     is not used to calculate min, max, mean and rms.
     * -3- an unfilled region (filled with the default value if provided)
     *
     * At any given time the TupleColumn's configuration should be the
     * following:
     * - there should be nFilled rows with nFilled =< length-2.
     * - there should be a stack region with nStack = nFilled+1.
     * - there can be an unfilled set of rows.
     *
     * A TupleColumn is initialized with the default value.
     * To add data to the stack invoke the fill( value ) method.
     * To commit the stack to the filled region invoke the method addRow().
     * To reset the stack invoke the method resetRow().
     *
     * A TupleColumn comes in two flavours: open-ended or close-ended.
     * In the first case the TupleColumn's size is automatically increased by arraySize
     * when needed. In the second case the length of the array is fixed.
     * The default is an open-ended TupleColumn.
     *
     * When creating a new TupleColumn an array of the appropriate type is
     * created with length arraySize.If needed additional arrays of length arraySize
     * are created. All the arrays are stored in an ArrayList to avoid time consuming array copying.
     *
     */
    
    protected Value  defaultValue = null;
    protected ArrayList arrayList = new ArrayList();
    protected int arraySize;
    protected int maximumSize;
    protected int nStack;
    protected java.lang.Object currentArray;
    protected int currentArrayIndex = -1;
    private boolean isOpenEnded;
    private int sizeOfLastCreatedArray;
    private int nFilled;
    private java.lang.String columnName;
    private Class  columnType;
    private double minValue, maxValue;
    private double meanValue, rmsValue;
    private Value colValue = new Value();
    private boolean hasDefaultValue = false;
    private int nNaN = 0;
    private boolean calculateStatistics = true;
    private Tuple tuple;
    
    /**
     * The constructor.
     * @param name The TupleColumn's name.
     * @param type The TupleColumn's type.
     * @param value The TupleColumn's default value.
     * @param length The TupleColumn's length.
     * @param maxLength The TupleColumn's maximum length. If negative the
     *                  TupleColumn is open-ended, if greater than length it
     *                  is close-ended; if greater than zero and less than length
     *                  an IllegalArgumentException is thrown.
     *
     */
    TupleColumn( java.lang.String name, Class type, Value value, java.lang.String options, Tuple tuple ) {
        
        this.tuple = tuple;
        
        Map optionMap = AidaUtils.parseOptions(options);
        
        int length = Tuple.COLUMN_ROWS;
        int maxLength = -1;
        
        if ( optionMap.containsKey( "length" ) ) length = Integer.parseInt( ( java.lang.String ) ( optionMap.get( "length" ) ) );
        
        if ( optionMap.containsKey( "maxlength" ) ) {
            maxLength = Integer.parseInt( ( java.lang.String ) ( optionMap.get( "maxlength" ) ) );
            if ( ! optionMap.containsKey( "length" ) )
                length = maxLength;
        }
        
        if ( optionMap.containsKey("calculatestatistics") )
            calculateStatistics = Boolean.valueOf( (java.lang.String) (optionMap.get("calculatestatistics")) ).booleanValue();
        
        columnName = name;
        columnType = type;
        if ( value != null ) {
            if ( value.getObject() != null && ! ( type.isAssignableFrom(value.getType()) ) ) throw new IllegalArgumentException("The default value's type "+value.getType()+" is incompatible with the TupleColumn's type "+type);
            setDefaultValue( value );
            hasDefaultValue = true;
        }
        
        if ( hasStatistics() ) {
            minValue = Double.NaN;
            maxValue = Double.NaN;
        }
        
        if ( length < 0 ) throw new IllegalArgumentException("Wrong length "+length+" it must be positive!");
        arraySize = length;
        
        if ( maxLength < 0 ) isOpenEnded = true;
        else if ( maxLength >= length ) isOpenEnded = false;
        else throw new IllegalArgumentException("Wrong maximum length "+maxLength+" it cannot be smaller than the internal arraySize "+length);
        maximumSize = maxLength;
        
        reset();
    }
    
    Tuple tuple() {
        return tuple;
    }
    
    Value colValue() {
        return colValue;
    }
    
    public double minimum() {
        minValue(colValue());
        return colValue().getDouble();
    }
    
    public double maximum() {
        maxValue(colValue());
        return colValue().getDouble();
    }
    
    public double mean() {
        meanValue(colValue());
        return colValue().getDouble();
    }
    
    public double rms() {
        rmsValue(colValue());
        return colValue().getDouble();
    }
    
    public boolean hasDefaultValue() {
        return hasDefaultValue;
    }
    
    public java.lang.String name() {
        return columnName;
    }
    
    public Class type() {
        return columnType;
    }
    
    public void value( FTupleCursor cursor, Value value ) {
        value( cursor.row(), value );
    }
    
    public abstract void value( int index, Value value );
    
    public void fill( Value value ) {
        setValue(nStack,value);
    }
    
    public abstract void setValue(int index, Value value);
    
    public void addRow() {
        if ( calculateStatistics && hasStatistics() ) {
            value( nStack, colValue() );
            double v = colValue().getDouble();
            if ( v < minValue || Double.isNaN(minValue) ) minValue = v;
            if ( v > maxValue || Double.isNaN(maxValue) ) maxValue = v;
            if ( ! Double.isNaN(v) ) {
                meanValue += v;
                rmsValue += v*v;
            } else
                nNaN++;
        }
        nFilled = nStack;
        nStack++;
        
        int columnLength = getColumnLength();
        
        if ( nStack == columnLength ) {
            if ( !isOpenEnded() ) {
                int spaceLeft = maximumSize - columnLength;
                if ( spaceLeft < arraySize ) sizeOfLastCreatedArray = spaceLeft;
            }
            if ( sizeOfLastCreatedArray > 0 )
                createArray( sizeOfLastCreatedArray );
        }
    }
    
    public void resetRow() {
        if ( hasDefaultValue() )
            fill( getDefaultValue() );
    }
    
    public void reset() {
        arrayList.clear();
        sizeOfLastCreatedArray = arraySize;
        createArray( sizeOfLastCreatedArray );
        setCurrentArray( 0 );
        initTupleColumn();
    }
    
    public void minValue(Value value) {
        if ( ! hasStatistics() || nFilled < 0 ) value.set(Double.NaN);
        else value.set(minValue);
    }
    
    public void maxValue(Value value) {
        if ( ! hasStatistics() || nFilled < 0 ) value.set(Double.NaN);
        else value.set(maxValue);
    }
    
    public void meanValue(Value value) {
        if ( !hasStatistics() || nFilled < 0 ) value.set(Double.NaN);
        else value.set(meanValue/(getFilledRows()-nNaN));
    }
    public void rmsValue(Value value) {
        if ( !hasStatistics() || nFilled < 0 ) value.set(Double.NaN);
        else {
            double rows = getFilledRows()-nNaN;
            value.set(Math.sqrt( rmsValue/rows - meanValue*meanValue/rows/rows ));
        }
    }
    
    /**
     * Protected and Private Methods.
     *
     */
    
    /**
     * Initialize the TupleColumn.
     *
     */
    private void initTupleColumn() {
        nFilled = -1;
        nStack = 0;
        meanValue = 0.;
        rmsValue = 0.;
        nNaN = 0;
    }
    
    /**
     * Get the TupleColumn current length.
     * @return The TupleColumn's current length.
     *
     */
    private int getColumnLength() {
        return arraySize*( arrayList.size() - 1 ) + sizeOfLastCreatedArray;
    }
    
    /**
     * Get the number of filled rows of this TupleColumn.
     * @return The number of filled rows.
     *
     */
    private int getFilledRows() {
        return nFilled+1;
    }
    
    /**
     * Check if the TupleColumn is open-ended.
     * @return <code>true</code> if the TupleColumn is open-ended
     *         <code>false</code> otherwise.
     *
     */
    private boolean isOpenEnded() {
        return isOpenEnded;
    }
    
    /**
     * Create a new array of the appropriate type with given length.
     * @param size The size of the array to be created.
     *
     */
    abstract protected void createArray( int size );
    
    /**
     * Get the column's default value.
     * @return The default Value.
     *
     */
    protected Value getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Set the column's default value.
     * @param value The default value.
     *
     */
    protected void setDefaultValue( Value value ) {
        defaultValue = new Value(value);
    }
    
    /**
     * Does this column carry any statistic information?
     * @return <code>true</code> if the statistics is updated.
     *
     */
    protected abstract boolean hasStatistics();
    
    /**
     * Set the internal current array to the one corresponding to a given value's index.
     * @param index The value's index.
     *
     */
    protected void setCurrentArray( int index ) {
        if ( index < 0 || index > getFilledRows() ) {
            java.lang.String message = "Illegal cursor position. ";
            if ( index < 0 )
                message += "next() has to be invoked after start() before accessing the data in the ITuple";
            if ( index > nFilled )
                message += "Before accessing the data start() has to be invoked.";
            throw new IllegalArgumentException(message);
        }
        int arrayIndex = index/arraySize;
        if ( arrayIndex != currentArrayIndex ) {
            currentArrayIndex = arrayIndex;
            currentArray = arrayList.get( currentArrayIndex );
            currentArrayUpdated();
        }
    }
    
    /**
     *  Update the current array in the column.
     *  This method is invoked within the setCurrentArray( int ) method when the current array is actually changed.
     *  The FTupleColumn on which the method is invoked should internally update the current array.
     *
     */
    protected abstract void currentArrayUpdated();
    
    /**
     * Clears the values on the stack.
     */
    public void resetRows(int numberOfRows) {
        if ( hasDefaultValue() )
            fill( getDefaultValue() );
        while ( numberOfRows-- > 0 ) {
            value( nFilled--, colValue() );
            if ( hasStatistics() ) {
                double v = colValue().getDouble();
                if ( Double.isNaN(v) )
                    nNaN--;
                else {
                    meanValue -= v;
                    rmsValue -= v*v;
                }
            }
            nStack--;
            if ( hasDefaultValue() )
                fill( getDefaultValue() );
        }
        if ( hasStatistics() ) calculateMinMax();
    }
    
    /**
     * Re-calculate Min and Max
     *
     */
    private void calculateMinMax() {
        minValue = Double.NaN; //defaultValue.getDouble();
        maxValue = Double.NaN; //defaultValue.getDouble();
        int rows = getFilledRows();
        for ( int i = 0; i<rows; i++ ) {
            value( i, colValue() );
            double v = colValue().getDouble();
            if ( v < minValue ||Double.isNaN(minValue)) minValue = v;
            if ( v > maxValue ||Double.isNaN(maxValue)) maxValue = v;
        }
    }

    public void defaultValue(Value value) {
        throw new UnsupportedOperationException();
    }
    
    
    public static class TupleColumnBoolean extends TupleColumn  implements ITupleColumn.Z {
        
        private boolean[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnBoolean( java.lang.String name, Value value, java.lang.String options, Tuple tuple) {
            super( name, Boolean.TYPE, value, options, tuple);
        }
        
        /**
         * Create a new array of the appropriate type with length
         * equal to arraySize.
         * @return The newly created array.
         *
         */
        protected void createArray(int size) {
            boolean[] data = new boolean[ size ];
            if ( hasDefaultValue() )
                for ( int i = 0; i < size; i++ )
                    data[ i ] = getDefaultValue().getBoolean();
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                data[ index%arraySize ] = value.getBoolean();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnBoolean" );
            }
        }
        
        protected boolean hasStatistics() {
            return false;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getBoolean() );
        }
        
        protected void currentArrayUpdated() {
            data = (boolean[]) currentArray;
        }
        
        public void fill(boolean value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public boolean value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getBoolean();
        }
        
        public boolean defaultValue() {
            defaultValue(colValue());
            return colValue().getBoolean();
        }
        
        public boolean fillableObject() {
            return defaultValue();
        }
        
    }
    
    public static class TupleColumnFloat extends TupleColumn implements ITupleColumn.F {
        
        private float[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnFloat( java.lang.String name, Value value, java.lang.String options, Tuple tuple) {
            super( name, Float.TYPE, value, options, tuple);
        }
        
        protected void createArray(int size) {
            float[] data = new float[ size ];
            if ( hasDefaultValue() )
                for ( int i = 0; i < size; i++ )
                    data[ i ] = getDefaultValue().getFloat();
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                data[ index%arraySize ] = value.getFloat();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnFloat" );
            }
        }
        
        protected boolean hasStatistics() {
            return true;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getFloat() );
        }
        
        protected void currentArrayUpdated() {
            data = (float[]) currentArray;
        }
        
        public void fill(float value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public float value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getFloat();
        }
        
        public float defaultValue() {
            defaultValue(colValue());
            return colValue().getFloat();
        }
        
        public float fillableObject() {
            return defaultValue();
        }
    }
    
    public static class TupleColumnDouble extends TupleColumn implements ITupleColumn.D {
        
        private double[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnDouble( java.lang.String name, Value value, java.lang.String options, Tuple tuple) {
            super( name, Double.TYPE, value, options, tuple);
        }
        
        protected void createArray(int size) {
            double[] data = new double[ size ];
            if ( hasDefaultValue() )
                for ( int i = 0; i < size; i++ )
                    data[ i ] = getDefaultValue().getDouble();
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                data[ index%arraySize ] = value.getDouble();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnDouble" );
            }
        }
        
        protected boolean hasStatistics() {
            return true;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getDouble() );
        }
        
        protected void currentArrayUpdated() {
            data = (double[]) currentArray;
        }
        
        public void fill(double value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public double value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getDouble();
        }
        
        public double defaultValue() {
            defaultValue(colValue());
            return colValue().getDouble();
        }
        
        public double fillableObject() {
            return defaultValue();
        }
    }
    
    public static class TupleColumnByte extends TupleColumn implements ITupleColumn.B {
        
        private byte[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnByte( java.lang.String name, Value value, java.lang.String options, Tuple tuple ) {
            super( name, Byte.TYPE, value, options, tuple);
        }
        
        protected void createArray(int size) {
            byte[] data = new byte[ size ];
            if ( hasDefaultValue() )
                for ( int i = 0; i < size; i++ )
                    data[ i ] = getDefaultValue().getByte();
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                data[ index%arraySize ] = value.getByte();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnByte "+cce.getMessage() );
            }
        }
        
        protected boolean hasStatistics() {
            return true;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getByte() );
        }
        
        protected void currentArrayUpdated() {
            data = (byte[]) currentArray;
        }
        
        public void fill(byte value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public byte value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getByte();
        }
        
        public byte defaultValue() {
            defaultValue(colValue());
            return colValue().getByte();
        }
        
        public byte fillableObject() {
            return defaultValue();
        }
    }
    
    public static class TupleColumnShort extends TupleColumn implements ITupleColumn.S {
        
        private short[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnShort( java.lang.String name, Value value, java.lang.String options, Tuple tuple) {
            super( name, Short.TYPE, value, options, tuple);
        }
        
        protected void createArray(int size) {
            short[] data = new short[ size ];
            if ( hasDefaultValue() )
                for ( int i = 0; i < size; i++ )
                    data[ i ] = getDefaultValue().getShort();
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            short[] data = (short[]) currentArray;
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                short[] data = (short[]) currentArray;
                data[ index%arraySize ] = value.getShort();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnShort" );
            }
        }
        
        protected boolean hasStatistics() {
            return true;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getShort() );
        }
        
        protected void currentArrayUpdated() {
            data = (short[]) currentArray;
        }
        
        public void fill(short value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public short value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getShort();
        }
        
        public short defaultValue() {
            defaultValue(colValue());
            return colValue().getShort();
        }
        
        public short fillableObject() {
            return defaultValue();
        }
    }
    
    public static class TupleColumnInt extends TupleColumn implements ITupleColumn.I {
        
        private int[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnInt( java.lang.String name, Value value, java.lang.String options, Tuple tuple) {
            super( name, Integer.TYPE, value, options, tuple);
        }
        
        protected void createArray(int size) {
            int[] data = new int[ size ];
            if ( hasDefaultValue() ) {
                for ( int i = 0; i < size; i++ )
                    data[ i ] = getDefaultValue().getInt();
            }
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                data[ index%arraySize ] = value.getInt();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnInt" );
            }
        }
        
        protected boolean hasStatistics() {
            return true;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getInt() );
        }
        
        protected void currentArrayUpdated() {
            data = (int[]) currentArray;
        }
        public void fill(int value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public int value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getInt();
        }
        
        public int defaultValue() {
            defaultValue(colValue());
            return colValue().getInt();
        }
        
        public int fillableObject() {
            return defaultValue();
        }
    }
    
    public static class TupleColumnLong extends TupleColumn implements ITupleColumn.L {
        
        private long[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnLong( java.lang.String name, Value value, java.lang.String options, Tuple tuple) {
            super( name, Long.TYPE, value, options, tuple);
        }
        
        protected void createArray(int size) {
            long[] data = new long[ size ];
            if ( hasDefaultValue() )
                for ( int i = 0; i < size; i++ )
                    data[ i ] = getDefaultValue().getLong();
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                data[ index%arraySize ] = value.getLong();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnLong" );
            }
        }
        
        protected boolean hasStatistics() {
            return true;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getLong() );
        }
        
        protected void currentArrayUpdated() {
            data = (long[]) currentArray;
        }
        
        public void fill(long value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public long value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getInt();
        }
        
        public long defaultValue() {
            defaultValue(colValue());
            return colValue().getInt();
        }
        
        public long fillableObject() {
            return defaultValue();
        }
    }
    
    public static class TupleColumnChar extends TupleColumn implements ITupleColumn.C {
        
        private char[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnChar( java.lang.String name, Value value, java.lang.String options, Tuple tuple) {
            super( name, Character.TYPE, value, options, tuple);
        }
        
        protected void createArray(int size) {
            char[] data = new char[ size ];
            if ( hasDefaultValue() )
                for ( int i = 0; i < size; i++ )
                    data[ i ] = getDefaultValue().getChar();
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                data[ index%arraySize ] = value.getChar();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnChar" );
            }
        }
        
        protected boolean hasStatistics() {
            return false;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getChar() );
        }
        
        protected void currentArrayUpdated() {
            data = (char[]) currentArray;
        }
        
        public void fill(char value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public char value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getChar();
        }
        
        public char defaultValue() {
            defaultValue(colValue());
            return colValue().getChar();
        }
        
        public char fillableObject() {
            return defaultValue();
        }
    }
    
    public static class TupleColumnString extends TupleColumn implements ITupleColumn.String {
        
        private java.lang.String[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnString( java.lang.String name, Value value, java.lang.String options, Tuple tuple) {
            super( name, java.lang.String.class, value, options, tuple);
        }
        
        protected void createArray(int size) {
            java.lang.String[] data = new java.lang.String[ size ];
            if ( hasDefaultValue() )
                for ( int i = 0; i < size; i++ )
                    data[ i ] = getDefaultValue().getString();
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                data[ index%arraySize ] = value.getString();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnString" );
            }
        }
        
        protected boolean hasStatistics() {
            return false;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getString() );
        }
        
        protected void currentArrayUpdated() {
            data = (java.lang.String[]) currentArray;
        }
        
        public void fill(java.lang.String value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public java.lang.String value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getString();
        }
        
        public java.lang.String defaultValue() {
            defaultValue(colValue());
            return colValue().getString();
        }
        
        public java.lang.String fillableObject() {
            return defaultValue();
        }
    }
    
    public static class TupleColumnFolder extends TupleColumn implements ITupleColumn.ITuple, HasFTuple {
        
        private int index;
        private boolean indexCalculated = false;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnFolder(java.lang.String name, Tuple folderTuple, java.lang.String options, Tuple parentTuple) {
            super( name, hep.aida.ITuple.class, new Value().set(folderTuple), "length=1; maxlength=2", parentTuple );
        }
        
        private int index() {
            if ( ! indexCalculated ) {
                index = tuple().findColumn(name());
                indexCalculated = true;                
            }
            return index;
        }
        
        protected void createArray(int size) {
            throw new UnsupportedOperationException("createArray not supported for TupleColumnFolder");
        }
        
        public void value(int index, Value value) {
            value.set( ((Tuple)getDefaultValue().getObject()) );
        }
        
        public void fill(Value value) {
            throw new UnsupportedOperationException("fill not supported for TupleColumnFolder");
        }
        public void setValue(int index, Value value) {
            throw new UnsupportedOperationException("setValue not supported for TupleColumnFolder");
        }
        
        public FTuple fTuple() {
            return (FTuple) value();
        }
        
        public void fill(hep.aida.ITuple tp) {
            hep.aida.ITuple folder = tuple().findTuple(index());
            tp.start();
            while (tp.next()) {
                for (int i=0; i<tp.columns(); i++) {
                    Class columnType = tp.columnType(i);
                    if ( columnType == Integer.TYPE ) folder.fill(i, tp.getInt(i));
                    else if ( columnType == Short.TYPE) folder.fill(i, tp.getShort(i));
                    else if ( columnType == Long.TYPE) folder.fill(i, tp.getLong(i));
                    else if ( columnType == Float.TYPE) folder.fill(i, tp.getFloat(i));
                    else if ( columnType == Double.TYPE) folder.fill(i, tp.getDouble(i));
                    else if ( columnType == Boolean.TYPE) folder.fill(i, tp.getBoolean(i));
                    else if ( columnType == Byte.TYPE) folder.fill(i, tp.getByte(i));
                    else if ( columnType == Character.TYPE) folder.fill(i, tp.getChar(i));
                    else if ( columnType == hep.aida.ITuple.class) {
                        Tuple tOld = (Tuple) tp.getObject(i);
                        //hep.aida.ITuple tNew = this.copy(tOld.name(), tOld.label(), tOld, null);
                        folder.fill(i, tOld);
                    } else folder.fill(i, tp.getObject(i));
                }
                folder.addRow();
            }
        }
        
        public hep.aida.ITuple value() {
            return (hep.aida.ITuple) tuple().getObject(index());
        }
        
        public hep.aida.ITuple defaultValue() {
            return (hep.aida.ITuple)tuple().columnDefaultValue(index());
        }
        
        public hep.aida.ITuple fillableObject() {
            return tuple().findTuple(index());
        }
        
        
        protected boolean hasStatistics() {
            return false;
        }
        
        private int getColumnLength() {
            return -1;
        }
        
        private int getFilledRows() {
            return -1;
        }
        
        private boolean isOpenEnded() {
            return false;
        }
        
        protected void setCurrentArray( int index ) {
            throw new RuntimeException("Unsupported method");
        }
        
        public void addRow() {
            //        throw new RuntimeException("This method is not supported by Folders");
        }
        
        public void resetRow() {
            //        throw new RuntimeException("This method is not supported by Folders");
        }
        
        public void resetRows(int numberOfRows) {
            //        throw new RuntimeException("This method is not supported by Folders");
        }
        
        public void reset() {
            //        throw new RuntimeException("This method is not supported by Folders");
        }
        
        public void defaultValue( Value value ) {
            value.set( getDefaultValue().getObject() );
        }
        
        protected void currentArrayUpdated() {
        }
        
        
    }
    
    public static class TupleColumnObject extends TupleColumn implements ITupleColumn.Object {
        
        private java.lang.Object[] data;
        
        /**
         * The constructor.
         * @param name The TupleColumn's name.
         * @param type The TupleColumn's type.
         * @param value The TupleColumn's default value.
         * @param options The TupleColumn's options.
         *
         */
        TupleColumnObject( java.lang.String name, Class type, Value value, java.lang.String options, Tuple tuple) {
            super( name, type, value, options, tuple);
        }
        
        protected void createArray(int size) {
            java.lang.Object[] data = new java.lang.Object[ size ];
            if ( hasDefaultValue() ) {
                java.lang.Object def = getDefaultValue().getObject();
                for ( int i = 0; i < size; i++ )
                    if ( def == null )
                        data[ i ] = def;
            }
            arrayList.add( data );
        }
        
        public void value(int index, Value value) {
            setCurrentArray( index );
            value.set( data[ index%arraySize ] );
        }
        
        public void setValue(int index, Value value) {
            try {
                setCurrentArray( index );
                data[ index%arraySize ] = value.getObject();
            } catch ( ClassCastException cce ) {
                throw new IllegalArgumentException( "Wrong argument for TupleColumnObject" );
            }
        }
        
        protected boolean hasStatistics() {
            return false;
        }
        
        public void defaultValue( Value value ) {
            if ( hasDefaultValue() )
                value.set( getDefaultValue().getObject() );
        }
        
        protected void currentArrayUpdated() {
            data = (java.lang.Object[]) currentArray;
        }
        
        public void fill(java.lang.Object value) throws IllegalArgumentException {
            colValue().set(value);
            fill(colValue());
        }
        
        public java.lang.Object value() {
            value( tuple().internalCursor(), colValue());
            return colValue().getObject();
        }
        
        public java.lang.Object defaultValue() {
            defaultValue(colValue());
            return colValue().getObject();
        }
        
        public java.lang.Object fillableObject() {
            return defaultValue();
        }
    }
    
    
}

