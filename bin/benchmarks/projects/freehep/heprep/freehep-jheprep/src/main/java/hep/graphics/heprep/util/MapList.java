// Copyright 2003, FreeHEP.
package hep.graphics.heprep.util;

import java.util.*;

/**
 * A Map where its values can be retrieved as a List
 *
 * @author M.Donszelmann
 *
 * @version $Id: MapList.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface MapList extends Map {

    /**
     * Return values of map as a List
     * @return list of values
     */
    public List valueList();
    
    /**
     * Return values of map as a Set (sub-optimal)
     * @return set of values
     * @deprecated, use valueList
     */
    public Set valueSet();
}