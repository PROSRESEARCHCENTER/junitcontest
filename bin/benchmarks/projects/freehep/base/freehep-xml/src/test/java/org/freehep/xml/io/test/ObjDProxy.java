/*
 * ObjDProxy.java
 *
 * Created on July 17, 2002, 11:54 AM
 */

package org.freehep.xml.io.test;
import org.freehep.xml.io.*;
import org.jdom.*;
import java.util.*;

/**
 *
 * @author  turri
 */
public class ObjDProxy implements org.freehep.xml.io.XMLIOProxy {
    
    private Class[] objClasses;

    /** Creates a new instance of ObjDProxy */
    public ObjDProxy() {
        objClasses = new Class[1];
        objClasses[0] = ObjD.class;        
    }
    
    /** Returns the array containing the Class of the objects that the proxy is
     * able to save and restore.
     * @return    The classes.
     *
     */
    public Class[] XMLIOProxyClasses() {
        return objClasses;
    }
        
    /** Restore the configuration of an object.
     * @param obj          The object to be restored.
     * @param xmlioManager The objects ID manager.
     * @param nodeEl        Is the jdom node containing the
     *                      object's configuration info
     */
    public void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        ((ObjD)obj).setStatus( Integer.parseInt( nodeEl.getAttributeValue( "status" ) ) );
        for (Iterator it = nodeEl.getChildren().iterator(); it.hasNext(); )
            ((ObjD)obj).addObject( xmlioManager.restore( (Element) it.next() ) );        
    }
    
    /** Save the current configuration of an object.
     * @param obj          The object to be saved to xml.
     * @param xmlioManager The objects ID manager.
     * @return Element     A jdom node containing the object's
     *                     configuration info.
     * @throws             An IllegalArgumentException of the object
     *                     is not saveble by this proxy.
     *
     */
    public void save(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        nodeEl.setAttribute( "status", String.valueOf( ((ObjD)obj).getStatus() ) );
        for ( int i = 0; i < ((ObjD)obj).vect.size(); i++ ) 
                nodeEl.addContent( xmlioManager.save( ((ObjD)obj).vect.get(i) ) );
    }
    
}
