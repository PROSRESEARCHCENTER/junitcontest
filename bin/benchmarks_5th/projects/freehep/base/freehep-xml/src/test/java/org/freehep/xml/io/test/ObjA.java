/*
 * ObjA.java
 *
 * Created on July 15, 2002, 11:24 AM
 */

package org.freehep.xml.io.test;
import java.util.*;
import org.freehep.xml.io.*;
import org.jdom.*;

/**
 *
 * @author  turri
 */
public class ObjA extends AbstractObj implements org.freehep.xml.io.XMLIO {
            
    public ObjA() {
    }

    public ObjA( int status ) {
        super( status );
    }
    
    /** Restore the object configuration.
     * @param xmlioManager the objects ID manager
     * @param nodeEl is the jdom node containing the
     *               object configuration info
     */
    public void restore(XMLIOManager xmlioManager, Element nodeEl) {
        setStatus( Integer.parseInt( nodeEl.getAttributeValue( "status" ) ) );
        for (Iterator it = nodeEl.getChildren().iterator(); it.hasNext(); )
            addObject( xmlioManager.restore( (Element) it.next() ) );        
    }
    
    /** Save the current configuration.
     * @param xmlioManager the objects ID manager
     * @return Element  a jdom node containing the object's
     *                  configuration info
     */
    public void save(XMLIOManager xmlioManager, Element el) {
        el.setAttribute( "status", String.valueOf( getStatus() ) );
        for ( int i = 0; i < vect.size(); i++ ) 
                el.addContent( xmlioManager.save( vect.get(i) ) );
    }
        
}
