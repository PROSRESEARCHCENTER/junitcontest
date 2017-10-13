// Copyright FreeHEP, 2007.
package org.freehep.wbxml;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public interface Attributes {

	public static final int UNDEFINED = -1;
	
	public static final int BOOLEAN = 0x00;
	public static final int BYTE = 0x01;
	public static final int CHAR = 0x02;
	public static final int DOUBLE = 0x03;
	public static final int FLOAT = 0x04;
	public static final int INT = 0x05;
	public static final int LONG = 0x06;
	public static final int SHORT = 0x07;
	public static final int STRING = 0x08;
	public static final int OBJECT = 0x09;
	public static final int COLOR = 0x0a;

	public static final int BOOLEAN_ARRAY = 0x10;
	public static final int BYTE_ARRAY = 0x11;
	public static final int CHAR_ARRAY = 0x12;
	public static final int DOUBLE_ARRAY = 0x13;
	public static final int FLOAT_ARRAY = 0x14;
	public static final int INT_ARRAY = 0x15;
	public static final int LONG_ARRAY = 0x16;
	public static final int SHORT_ARRAY = 0x17;
	public static final int STRING_ARRAY = 0x18;

	/**
	 * Return the type for a particular attribute tag, or UNDEFINED if
	 * the tag does not exist.
	 * 
	 * @param tag
	 * @return
	 */
    public int getType(int tag);
    
    /**
     * Return the list of defined attributes
     * @return
     */
    public int[] getTags();
        
    public String  getStringValue(int tag, String def);
    public double  getDoubleValue(int tag, double def);
    public float   getFloatValue(int tag, float def);
    public long    getLongValue(int tag, long def);
    public int     getIntValue(int tag, int def);
    public char    getCharValue(int tag, char def);
    public short   getShortValue(int tag, short def);
    public byte    getByteValue(int tag, byte def);
    public boolean getBooleanValue(int tag, boolean def);

    public String  getStringValue(int tag);
    public double  getDoubleValue(int tag);
    public float   getFloatValue(int tag);
    public long    getLongValue(int tag);
    public int     getIntValue(int tag);
    public char    getCharValue(int tag);
    public short   getShortValue(int tag);
    public byte    getByteValue(int tag);
    public boolean getBooleanValue(int tag);

    public String[]  getStringArray(int tag);
    public double[]  getDoubleArray(int tag);
    public float[]   getFloatArray(int tag);
    public long[]    getLongArray(int tag);
    public int[]     getIntArray(int tag);
    public char[]    getCharArray(int tag);
    public short[]   getShortArray(int tag);
    public byte[]    getByteArray(int tag);
    public boolean[] getBooleanArray(int tag);
}
