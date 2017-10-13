// these interfaces may move at some point to something like: hep.heprep
package hep.graphics.heprep1.ref;

import hep.graphics.heprep1.HepRepAttName;

import java.io.Serializable;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepAttName.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepAttName implements HepRepAttName, Serializable {

    private String name;
    
    /**
     * Create Attribute Name
     * @param name attribute name
     */
    public DefaultHepRepAttName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
