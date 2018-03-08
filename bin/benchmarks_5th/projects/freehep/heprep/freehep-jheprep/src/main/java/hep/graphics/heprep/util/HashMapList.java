// Copyright 2003, FreeHEP.
package hep.graphics.heprep.util;

import java.util.*;

/**
 * Extends a Map and keeps a List to be able to return the list of Values.
 *
 * @author M.Donszelmann
 *
 * @version $Id: HashMapList.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HashMapList extends HashMap implements MapList {

    private List list = new ArrayList();

    public void clear() {
        super.clear();
        list.clear();
    }

    public Object put(Object key, Object value) {
        Object old = super.put(key, value);
        if (old != null) list.remove(old);
        list.add(value);
        return old;
    }

    public void putAll(Map t) {
        for (Iterator i=t.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key) {
        Object value = super.remove(key);
        if (value != null) list.remove(value);
        return value;
    }
    
    public Collection values() {
        return list;
    }        

    public List valueList() {
        return list;
    }
    
    public Set valueSet() {
        Set s = new HashSet();
        for (Iterator i=entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            s.add(entry.getValue());
        }
        return s;
    }
}