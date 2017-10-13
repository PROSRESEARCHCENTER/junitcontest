/// Copyright FreeHEP 2000-2005.
package hep.graphics.heprep1;

import java.util.Enumeration;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepTypeInfo.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface HepRepTypeInfo extends HepRepAttribute {

    /**
     * @return name
     */
    public String getName();
    
    /**
     * @return version
     */
    public String getVersion();
        
    /**
     * @return child types
     */
    public Enumeration getSubTypes();
}
