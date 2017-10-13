// Copyright FreeHEP 2000-2005.
package hep.graphics.heprep1;

import java.util.Enumeration;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRep.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface HepRep extends HepRepAttribute {

    /**
     * @return all type information
     */
    public Enumeration getTypeInfo();
    
    /**
     * @param name name of type
     * @param version version of type
     * @return top-level type
     */
    public HepRepType getRepresentablesUncut(String name,
					     String version);
    
    /**
     * @param name
     * @param version
     * @param cutList
     * @param getPoints
     * @param getDrawAtts
     * @param getNonDrawAtts
     * @param invertAtts
     * @return top-level type
     */
    public HepRepType getRepresentables(String name,
					String version, 
                                        HepRepCut[] cutList,
                                        boolean getPoints,
                                        boolean getDrawAtts,
                                        boolean getNonDrawAtts,
                                        HepRepAttName[] invertAtts);    

    /**
     * @return get all types
     */
    public Enumeration getTypes();
    
    /**
     * @param type type to add
     */
    public void addType(HepRepType type);
    
    /**
     * @param type type to remove
     * @return true if found
     */
    public boolean removeType(HepRepType type);
}
