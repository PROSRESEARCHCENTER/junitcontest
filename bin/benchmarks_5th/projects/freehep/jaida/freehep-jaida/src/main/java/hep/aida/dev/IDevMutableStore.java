/*
 * IMutableStore.java
 *
 * Created on May 11, 2003, 6:51 PM
 */

package hep.aida.dev;

import hep.aida.IManagedObject;

/**
 * Developer level interface to an IMutableStore.
 *
 * @author  serbo
 */
public interface IDevMutableStore extends IMutableStore 
{
    /**
     * Initiates the process of updating content for an IManagedObject.
     * Can be done synchronously or can just schedule update in some queue.
     * If validation mechanism is used for updates, servant's "setValid(path)"  
     * method should be called here after successfull date update.
     * @param path      The path to the object which is to be updated.
     * @param type      Type of object (this parameter does not have to be used).
     * @throws          IllegalArgumentException If the path does not exist, or if it is a directory.
     */
    void updateData(String path, String type) throws IllegalArgumentException;
    
    /**
     * Create an IManagedObject that can be later updated.
     * If object already exist, do not overwrite it, just
     * return with, maybe, a warning.
     *
     * @param name      Simple name of an object which is to be created, no directories.
     * @param type      Type of object.
     * @throws          IllegalArgumentException If unknown type.
     */
    IManagedObject createObject(String name, String type) throws IllegalArgumentException;
    
}
