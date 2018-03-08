/*
 * XMLIOFactory.java
 */

package org.freehep.xml.io;

/**
 * Interface for object factories. The XMLIOFactories are meant to 
 * create objects in a minimal configuration. These objects will
 * be then restored either by invoking their restore() method
 * (if they implement the XMLIO interface) or by invoking the
 * restore method on the XMLIOProxy responsible to save/restore
 * the given object.
 *
 * @author  turri
 * @version 1.0
 */

public interface XMLIOFactory
{
    /** 
     * Create a given object in a minimal state.
     * @param objClass The Class of the object to be created.
     * @return         The corresponding object.
     * @throws         An IllegalArgumentException if an object of the given Class
     *                 cannot be created.
     *
     */
    Object createObject(Class objClass) throws IllegalArgumentException;

    /**
     * Returns an array of Classes of the objects that the factory is
     * able to create.
     * @return An array containing the Classes of the objects that
     *         this factory in able to create.
     *
     */
    Class[] XMLIOFactoryClasses();
}
