package hep.aida.dev;

import hep.aida.IManagedObject;
import hep.aida.ITree;

/**   
 * An interface to a Tree that allows adding nodes. 
 */
public interface IDevTree extends ITree, IAddable
{
    /**
     * Add IManagedObject to the directory. 
     * If some folders in the path don't exist, can create 
     * new folders in the tree. Does not overwrite Objects,
     * if IManagedObject already exists - do nothing and just
     * return with, maybe, a warning.
     *
     * @param path      The path of the diretory in which the object has to be added.
     * @param object    The IManagedObject to be added.
     * @throws          IllegalArgumentException if the path is not a directory.
     */
    void add(String path, IManagedObject object) throws IllegalArgumentException;
    
    /**
     * Is called by the Store to let Tree know that a particular folder has been filled.
     * "path" is path to a folder, cannot point to an Object. 
     * IDevTree relies on this method for its internal book-keeping, so
     * Store MUST call "hasBeenFilled"  after it fills a particular folder.
     *
     * @param path      The path of the diretory which has been filled by the Store.
     * @throws          IllegalArgumentException If the path does not exist, or if it is not a directory.
     */
    void hasBeenFilled(String path) throws IllegalArgumentException;
    
    /*
     * Ability to lock (synchronize) Tree methods maybe needed
     * when using Tree in a multi-thread program. User have to
     * set the lock Object before useinf it. Default lock=null
     */
    void setLock(Object lock);
    
    /*
     * Ability to lock (synchronize) Tree methods maybe needed
     * when using Tree in a multi-thread program. User have to 
     * obtain lock object from the Tree and synchronize on it
     * Default lock=null
     */
    Object getLock();
    
}
