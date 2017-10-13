// Copyright FreeHEP 2000-2005.
package hep.graphics.heprep1;

import java.util.Enumeration;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepPrimitive.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface HepRepPrimitive extends HepRepAttribute {

    /**
     * @return parent primitive
     * @throws Throwable when using corba or rmi
     */
    public HepRepInstance getInstance() throws Throwable;

    /**
     * @return child points
     */
    public Enumeration getPoints();
}
