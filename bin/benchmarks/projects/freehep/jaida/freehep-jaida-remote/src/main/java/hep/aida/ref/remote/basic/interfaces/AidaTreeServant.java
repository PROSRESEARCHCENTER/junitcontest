/*
 * AidaTreeServant.java
 *
 * Created on May 11, 2003, 6:06 PM
 */

package hep.aida.ref.remote.basic.interfaces;

/**
 * Basic interface for AIDA Tree Servant.
 * Conveys all communication between the AIDA ITree and the AidaTreeClient.
 *
 * Can notify AidaTreeClient about changes in the ITree by calling 
 * client's "stateChanged" method ("Duplex" Mode) or by returning 
 * list of updates when AidaTreeClient calls "updates()" method. Communication
 * mode ("Duplex" or "non-Duplex" mode) is fixed at the AidaTreeServant creation
 * time and can not be changed.
 * 
 * If validation mechanism is used, AidaTreeClient should call "setValid" 
 * method (after processing an UpdateEvent) in order to tell AidaTreeServant 
 * that it is ready to receive information about changes/updates of particular 
 * set of IManagedObjects in ITree.
 * @author  serbo
 */
public interface AidaTreeServant {

    /**
     * Get the list of names of the IManagedObjects under a given path, including
     * directories (but not "." and ".."). Directories end with "/". 
     * The returned names are appended to the given path unless the latter is "." 
     * The path is an absolute path from the ITree root.
     */
     String[] listObjectNames(String path);

     /**
      * Get the list of types of the IManagedObjects under a given path. The types
      * are the leaf class of the Interface, e.g. "IHistogram1D", "ITuple", etc. 
      * Directories are marked with "dir". The order of the types is the same as the
      * order for the listObjectNames() method to achieve a one-to-one correspondance 
      * between object names and types.
      * The path is an absolute path from the ITree root.
      */
      String[] listObjectTypes(String path);


      /**
       * Get the IManagedObject, at a given path in the ITree. 
       * The path is an absolute path from the ITree root.
       */
      java.lang.Object find(String path);

      /**
       * This method can be called by AidaTreeClient to tell AidaTreeServant that 
       * it is ready to receive information about changes/updates of particular set
       * of IManagedObjects.
       */
      void setValid(String[] nodePaths);

      /**
       * This method can be called by AidaTreeClient to get list of current updates.
       */
      UpdateEvent[] updates();

}
