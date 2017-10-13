/**
 * XMLIO.java
 */

package org.freehep.xml.io;
import org.jdom.Element;

/**
 * Interface to for objects that can be save/restore to/from xml.
 *
 * @author  turri
 * @version 1.0
 *
 */

public interface XMLIO {
    
    /**
     * Save the current configuration of an object.
     * @param xmlioManager The objects ID manager.
     * @param nodeEl       The jdom node containing the object's configuration info.
     *                     This node is created by the XMLIOManager. Its name is,
     *                     by default, the final part of the object's class.
     *                     The node contains the attribute "objId", i.e. the unique id 
     *                     assigned to this object by the XMLIOManager. This id is
     *                     used internally by the XMLIOManager for cross references
     *                     to this object. The user has to append to this node all
     *                     the attributes and other Elements required to save the
     *                     object's configuration. Any other object, either XMLIO or Proxy-XMLIO,
     *                     that needs to be saved has to be passed to the XMLIOManager through its save(Object) method.
     * @see XMLIOManager#save(Object obj).
     *
     */
    void save( XMLIOManager xmlioManager, Element nodeEl );
    
    /**
     * Restore the object configuration.
     * @param xmlioManager The objects ID manager.
     * @param nodeEl       The jdom node containing the object's configuration info.
     *
     */
    void restore( XMLIOManager xmlioManager, Element nodeEl );
    
}
