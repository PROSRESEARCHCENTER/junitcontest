/*
 * XMLIOProxy.java
 */

package org.freehep.xml.io;
import org.jdom.Element;

/**
 * Interface for proxyes that can save and restore objects to/from xml.
 *
 * @author  turri
 *
 */
public interface XMLIOProxy {

    /**
     * Save the current configuration of an object.
     * @param obj          The object to be saved to xml.
     * @param xmlioManager The objects ID manager.
     * @return Element     A jdom node containing the object's 
     *                     configuration info.
     * @throws             An IllegalArgumentException if the object
     *                     is not saveble by this proxy.
     *
     */
    void save(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException;
    
    /**
     * Restore the configuration of an object.
     * @param obj          The object to be restored.
     * @param xmlioManager The objects ID manager.
     * @param nodeEl       Is the jdom node containing the
     *                     object's configuration info
     * @throws             An IllegalArgumentException if the object
     *                     cannot be restored by this proxy.
     *
     */
    void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException;

    /**
     * Returns the array containing the Class of the objects that the proxy is
     * able to save and restore. 
     * @return    The classes.
     *
     */
    Class[] XMLIOProxyClasses();
        
}
