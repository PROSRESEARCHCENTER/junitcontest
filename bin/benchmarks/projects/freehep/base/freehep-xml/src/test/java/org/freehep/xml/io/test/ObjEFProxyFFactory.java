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
public class ObjEFProxyFFactory implements org.freehep.xml.io.XMLIOProxy, org.freehep.xml.io.XMLIOFactory {
    
    private Class[] proxyClasses;
    private Class[] factoryClasses;

    /** Creates a new instance of ObjDProxy */
    public ObjEFProxyFFactory() {
        proxyClasses = new Class[2];
        proxyClasses[0] = ObjE.class;
        proxyClasses[1] = ObjF.class;
        factoryClasses = new Class[1];
        factoryClasses[0] = ObjF.class; 
    }
    
    /** Returns the array containing the Class of the objects that the proxy is
     * able to save and restore.
     * @return    The classes.
     *
     */
    public Class[] XMLIOProxyClasses() {
        return proxyClasses;
    }
        
    /** Returns the identifiers of the objects that the factory is
     * able to create. This identification is left by the object
     * in the jdom node during the save procedure and should be
     * used to identify the objectFactory during the restore phase.
     * @return    The signatures
     *
     */
    public Class[] XMLIOFactoryClasses() {
        return factoryClasses;
    }

    /** Restore the configuration of an object.
     * @param obj          The object to be restored.
     * @param xmlioManager The objects ID manager.
     * @param nodeEl        Is the jdom node containing the
     *                      object's configuration info
     */
    public void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        ((AbstractObj)obj).setStatus( Integer.parseInt( nodeEl.getAttributeValue( "status" ) ) );
        for (Iterator it = nodeEl.getChildren().iterator(); it.hasNext(); )
            ((AbstractObj)obj).addObject( xmlioManager.restore( (Element) it.next() ) );        
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
        nodeEl.setAttribute( "status", String.valueOf( ((AbstractObj)obj).getStatus() ) );
        for ( int i = 0; i < ((AbstractObj)obj).vect.size(); i++ ) 
                nodeEl.addContent( xmlioManager.save( ((AbstractObj)obj).vect.get(i) ) );
    }
    
    /**
     * Create XMLIO objects
     * @param objSignature The type of object to be created.
     * @return the XMLIO object in the standard configuration
     *
     */
    public Object createObject(Class objClass) {
        if ( objClass == ObjF.class ) return new ObjF();
        else throw new IllegalArgumentException("ObjEFProxyFFactory cannot create object of class "+objClass);
    }
    
    
}
