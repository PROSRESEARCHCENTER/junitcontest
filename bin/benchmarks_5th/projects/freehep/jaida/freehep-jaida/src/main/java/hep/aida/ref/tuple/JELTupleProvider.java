/*
 * JELTupleProvider.java
 *
 * Created on October 21, 2002, 1:24 PM
 */

package hep.aida.ref.tuple;

import gnu.jel.DVMap;
import hep.aida.ITuple;

/**
 * Serves as a Resolver and ValueProvider for JEL-based
 * Evaluator and Filter
 * @author  The AIDA team @ SLAC.
 *
 */
public class JELTupleProvider extends DVMap {

    private ITuple tuple;

    public JELTupleProvider(ITuple tuple) {
	this.tuple = tuple;
    }

    public ITuple getTuple() { return tuple; }

    public String getTypeName(String name) {
	int index = -1;
	try {
	    index = tuple.findColumn(name);
	} catch (IllegalArgumentException e) { return null; }
	if (index < 0 )  return null;
     
        Class colType = tuple.columnType( index );
        if ( colType == Integer.TYPE ) {
            return "Int";
        } else if ( colType == Short.TYPE ) {
            return "Short";
        } else if ( colType == Long.TYPE ) {
            return "Long";
        } else if ( colType == Float.TYPE ) {
            return "Float";
        } else if ( colType == Double.TYPE ) {
            return "Double";
        } else if ( colType == Boolean.TYPE ) {
            return "Boolean";
        } else if ( colType == Byte.TYPE ) {
            return "Byte";
        } else if ( colType == Character.TYPE ) {
            return "Char";
        } else if ( colType == String.class ) {
            return "String";
        } else if ( colType == Object.class ) {
            return "Double.NaN";
        } else return "Double.NaN";
    }

    public Object translate(String name) {
	int index = -1;
	try {
	    index = tuple.findColumn(name);
	} catch (IllegalArgumentException e) { return null; }
	if (index < 0 )  return null;
	return new Integer(index);
    }

    public int getIntProperty(int i) {
	return tuple.getInt(i);
    }
    public short getShortProperty(int i) {
	return tuple.getShort(i);
    }
    public long getLongProperty(int i) {
	return tuple.getLong(i);
    }
    public float getFloatProperty(int i) {
	return tuple.getFloat(i);
    }
    public double getDoubleProperty(int i) {
	return tuple.getDouble(i);
    }
    public boolean getBooleanProperty(int i) {
	return tuple.getBoolean(i);
    }
    public byte getByteProperty(int i) {
	return tuple.getByte(i);
    }
    public char getCharProperty(int i) {
	return tuple.getChar(i);
    }
    public String getStringProperty(int i) {
	return tuple.getString(i);
    }


}
