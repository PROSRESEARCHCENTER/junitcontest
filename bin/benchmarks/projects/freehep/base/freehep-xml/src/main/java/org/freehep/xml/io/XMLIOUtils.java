/*
 * XMLIOUtils.java
 *
 * Created on October 19, 2001, 4:34 PM
 */

package org.freehep.xml.io;
import java.util.*;
import java.awt.*;

/**
 * XMLIOUtils. This class is static and contains utilities.
 * @author turri
 * @version $Id: XMLIOUtils.java 8584 2006-08-10 23:06:37Z duns $
 */

public abstract class XMLIOUtils 
{
    /**
     * Get the Components implementing the interface XMLIO
     * contained in the object <code>obj<\obj>.
     * Only the first level Components are returned.
     * @param obj the root Container
     * @return    the list of XMLIO Components.
     *
     */

    public static java.util.List getXMLIOComponents( Object obj ) {
	java.util.List xmlioList = new ArrayList();
	Component[] comp = ((Container)obj).getComponents();
	for ( int ii = 0; ii < comp.length; ii++ ) {
	    Component c = comp[ii];
	    if ( c instanceof XMLIO ) {
		xmlioList.add( c );
	    } else if ( c instanceof Container ) {
		xmlioList.addAll( getXMLIOComponents( c ) );
	    }	    
	}
	return xmlioList;
    }

    /**
     * Get the Components implementing the interface XMLIO
     * contained in the object <code>obj<\obj>.
     * All level Components are returned.
     * @param obj the root Container
     * @return    the list of XMLIO Components.
     *
     */

    public static java.util.List getXMLIOComponentsAll( Object obj ) {
	java.util.List xmlioList = new ArrayList();
	Component[] comp = ((Container)obj).getComponents();
	for ( int ii = 0; ii < comp.length; ii++ ) {
	    Component c = comp[ii];
	    if ( c instanceof XMLIO ) {
		xmlioList.add( c );
	    } 
	    if ( c instanceof Container ) {
		xmlioList.addAll( getXMLIOComponentsAll( c ) );
	    }	    
	}
	return xmlioList;
    }

}

