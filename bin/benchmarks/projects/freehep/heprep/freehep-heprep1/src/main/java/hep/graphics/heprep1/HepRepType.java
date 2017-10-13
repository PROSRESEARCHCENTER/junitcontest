// Copyright FreeHEP 2000-2005.
package hep.graphics.heprep1;

import java.util.Enumeration;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepType.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface HepRepType extends HepRepAttribute {

    /**
     * @return name
     */
    public String getName();
    
    /**
     * @return version
     */
    public String getVersion();
    
    /**
     * @return top-level heprep
     */
    public HepRep getRoot();
    
    // should we add getType() here for the immediate parent. Null in case of a HepRep rather than a HepRepType...
    
    /**
     * @return child types
     */
    public Enumeration getTypes();
    
    /**
     * @return child instances
     */
    public Enumeration getInstances();
    
    /**
     * @return child primitives
     */
    public Enumeration getPrimitives();
    
    /**
     * @return child points
     */
    public Enumeration getPoints();    
}
