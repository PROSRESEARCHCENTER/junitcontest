/*
 * XMLIORegistry.java
 */

package org.freehep.xml.io;
/**
 * Interface of an XMLIORegistry. The XMLIOFactories used
 * in the restore procedure of an object and the XMLIOProxyes
 * should be register to an XMLIORegistry that should be set
 * in the XMLIOManager passed to the restore method.
 *
 * @author  turri
 * @version 1.0
 */

public interface XMLIORegistry
{
    /**
     * Register an XMLIOFactory or an XMLIOProxy to this Registry.
     * @param obj Either an XMLIOFactory or an XMLIOProxy to be registered.
     * @throws    An IllegalArgumentException if the object does not implement
     *            either XMLIOFactory or XMLIOProxy.
     *
     */
    void register( Object obj ) throws IllegalArgumentException;

    /**
     * Get the appropriate XMLIOFactory to create the object
     * that wrote the xml node. The XMLIOFactory identification string
     * is attached to the node's attribute <code>xmlioFactory<\code>.
     * If this attribute is not present, the node name is taken as
     * the identification string. The identification string is
     * assigned by the XMLIOManager by using the object's final
     * part of the Class name.
     * @param objClass The Class of the object to be created.
     * @return         The corresponding XMLIOFactory.
     * @throws         IllegalArgumentException if the XMLIOFactory does not exist.
     *
     */
    XMLIOFactory getXMLIOFactory( Class objClass ) throws IllegalArgumentException;

    /**
     * Get the appropriate XMLIOProxy to restore the object
     * that wrote the xml node. The XMLIOProxy identification string
     * is the node name. The identification string is
     * assigned by the XMLIOManager by using the object's final
     * part of the Class name.
     * @param objClass The Class of the object to be restored.
     * @return         The corresponding XMLIOProxy.
     * @throws         IllegalArgumentException if the XMLIOProxy does not exist.
     *
     */
    XMLIOProxy getXMLIOProxy( Class objClass ) throws IllegalArgumentException;

    /**
     * Get the identification string corresponding to a Class.
     * @param clName The Class.
     * @return       The identification String.
     *
     */
    String getClassId( Class clName );
    
    /**
     * Get the Class corresponding to an identification String.
     * @param objName The object's identification String.
     * @return        The corresponding Class.
     *
     */
    Class  getIdClass( String objName );

}
