// Copyright FreeHEP 2000-2005.
package hep.graphics.heprep1;

import java.util.Enumeration;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepInstance.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface HepRepInstance extends HepRepAttribute {

    /**
     * @return parent type
     */
    public HepRepType getType();
    
    /**
     * @return child types
     */
    public Enumeration getTypes();
    
    /**
     * @return child primitives
     */
    public Enumeration getPrimitives();
    
    /**
     * @return child points
     */
    public Enumeration getPoints();
}
