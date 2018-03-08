// Copyright FreeHEP, 2007.
package org.freehep.wbxml;

import java.util.List;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public interface MutableAttributes extends Attributes {

    public void clear();
    public void set(int tag, boolean value);
    public void set(int tag, boolean[] value);
    
    public void set(int tag, byte value);
    public void set(int tag, byte[] value);

    public void set(int tag, char value);
    public void set(int tag, char[] value);

    public void set(int tag, double value);
    public void set(int tag, double[] value);

    public void set(int tag, float value);
    public void set(int tag, float[] value);

    public void set(int tag, int value);
    public void set(int tag, int[] value);

    public void set(int tag, long value);
    public void set(int tag, long[] value);

    public void set(int tag, short value);
    public void set(int tag, short[] value);

    public void set(int tag, String value);
    public void set(int tag, String[] value);

    public void set(int tag, List value);
}
