// Copyright 2003, FreeHEP.
package hep.graphics.heprep.util;

import java.util.*;

/**
 * Implementation of ListSet
 * 
 * @author M.Donszelmann
 * @version $Id: ArrayListSet.java 8584 2006-08-10 23:06:37Z duns $
 */

public class ArrayListSet extends ArrayList implements ListSet {

    /**
     * Create an empty ArrayListSet
     */
    public ArrayListSet() {
        super();
    }
    
    /**
     * Create an ArrayListSet with initial set
     * @param set initial set
     */
    public ArrayListSet(Set set) {
        super(set);
    }
    
    /**
     * Create an empty ArrayListSet with initial capacity
     * @param capacity initial capacity
     */
    public ArrayListSet(int capacity) {
        super(capacity);
    }
    
    // FIXME, the add should probably check that all elements are unique.
}