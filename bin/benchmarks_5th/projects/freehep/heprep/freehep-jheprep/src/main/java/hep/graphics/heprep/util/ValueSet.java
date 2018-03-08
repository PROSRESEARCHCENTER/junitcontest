// Copyright 2003, FreeHEP.
package hep.graphics.heprep.util;

import java.util.*;

/**
 * This Set is created from a Map and will allow iteration over its values as a Set.
 * This implies that all values added to the Map are distinctive, for instance because
 * each value contains its own key. The methods equals() and hashCode() of the values
 * need to be implemented properly for this to work.
 *
 * @author M.Donszelmann
 * @version $Id: ValueSet.java 8584 2006-08-10 23:06:37Z duns $
 */

public class ValueSet extends AbstractSet {

    private Map map;

    /** 
     * Construct a ValueSet of the entries in this Map. Map is allowed to be null which results in
     * an empty set.
     * @param map map to use for value set
     */
    public ValueSet(Map map) {
        this.map = map;
    }
            
    public Iterator iterator() {
        Iterator r;
        if (map == null) {
            r = new Iterator() {
                public boolean hasNext() {
                    return false;
                }
                
                public Object next() {
                    throw new NoSuchElementException();
                }
                
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        } else {
            r = new Iterator() {
                private Iterator i = map.entrySet().iterator();
            
                public boolean hasNext() {
                    return i.hasNext();
                }
            
                public Object next() {
                    return ((Map.Entry)i.next()).getValue();    
                }
            
                public void remove() {
                    i.remove();
                }
            };
        }
        return r;
    }
        
    public int size() {
        return (map == null) ? 0 : map.size();
    }    
    
    public boolean contains(Object v) {
        return (map == null) ? false : map.containsValue(v);
    }
}
