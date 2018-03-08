// Copyright FreeHEP 2000-2005.
package hep.graphics.heprep1;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepAttDef.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface HepRepAttDef {

    /**
     * @return name
     */
    public String getName();
    
    /**
     * @return description
     */
    public String getDescription();
    
    /**
     * @return type
     */
    public String getType();
    
    /**
     * @return unit
     */
    public String getExtra();
}
