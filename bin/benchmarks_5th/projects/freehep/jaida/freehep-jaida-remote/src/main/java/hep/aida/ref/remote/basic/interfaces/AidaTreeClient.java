/*
 * AideTreeClient.java
 *
 * Created on May 11, 2003, 5:29 PM
 */

package hep.aida.ref.remote.basic.interfaces;

/**
 * Basic interface for AIDA Tree Client.
 * TreeClient can be used in "Duplex Mode" and it has only one method 
 * that can be called by TreeServant to post messages about updates.
 *
 * If validation mecanism is used, setValid() method of AidaTreeServant
 * must be called by AidaTreeClient to tell AidaTreeServant that 
 * it is ready to receive information about changes/updates of particular set
 * of ImanagedObjects
 * @author  serbo
 */
public interface AidaTreeClient {
    
    /**
     * Get the list of names of the IManagedObjects under a given path, including
     * directories (but not "." and ".."). Directories end with "/". 
     * The returned names are appended to the given path unless the latter is "." 
     * The path is an absolute path from the ITree root.
       *
       * @throws IllegalArgumentException if path does not exist.
     */
     String[] listObjectNames(String path) throws IllegalArgumentException;

     /**
      * Get the list of types of the IManagedObjects under a given path. The types
      * are the leaf class of the Interface, e.g. "IHistogram1D", "ITuple", etc. 
      * Directories are marked with "dir". The order of the types is the same as the
      * order for the listObjectNames() method to achieve a one-to-one correspondance 
      * between object names and types.
      * The path is an absolute path from the ITree root.
       *
       * @throws IllegalArgumentException if path does not exist.
      */
      String[] listObjectTypes(String path) throws IllegalArgumentException;


      /**
       * Get the data for IManagedObject, at a given path in the ITree. 
       * The path is an absolute path from the ITree root.
       *
       * @throws IllegalArgumentException if path does not exist, or path does
       *         not point to a directory.
       */
      java.lang.Object find(String path) throws IllegalArgumentException;

    /**
     * This method can be called by AidaTreeServant to notify
     * AideTreeClient about updates in the server-side ITree.
     * If "Duplex Mode" is not used, AideTreeClient has to call
     * updates() method of AideTreeServant to get a list of current 
     * updates.
     */
    void stateChanged(UpdateEvent[] events);
    
    /**
     * Return true if AidaTreeClient is already connected to the AidaTreeServer
     */
    boolean isConnected();
    
    /**
     * Make initial connection to the AidaTreeServer. If BasicTreeClient is already
     * connected, throws AidaConnectionException.
     */
    boolean connect() throws AidaConnectionException;
    
    /**
     * Disconnect from the AidaTreeServer and free all resources associated with it.
     */
    boolean disconnect();
    
}
