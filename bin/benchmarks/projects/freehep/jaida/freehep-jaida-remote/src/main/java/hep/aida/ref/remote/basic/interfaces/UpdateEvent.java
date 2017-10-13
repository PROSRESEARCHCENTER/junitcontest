/*
 * UpdateEvant.java
 *
 * Created on May 11, 2003, 5:38 PM
 */

package hep.aida.ref.remote.basic.interfaces;

/**
 * Is passed to the TreeClient to notify it about changes in the 
 * server-side ITree.
 * Event ID describes the change. Can be expanded later.
 *
 * @author  serbo
 */
public interface UpdateEvent {
    
      // Specify what kind of change happened in ITree
      public static int NODE_UPDATED = 0;
      public static int NODE_ADDED   = 1;
      public static int NODE_DELETED = 2;
     
      /**
       * Return ID for this Event
       */
      int id();
      
      /**
       * Return ABSOLUTE path for the node.
       */
      String path();
      
      /**
       * Return type of the node. In Java it is full class name.
       * Node type for a folder is "dir".
       */
      String nodeType();
}
