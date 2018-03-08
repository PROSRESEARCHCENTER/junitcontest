/*
 * UpdateEvant.java
 *
 * Created on May 11, 2003, 5:38 PM
 */

package hep.aida.ref.remote.interfaces;

import java.io.Serializable;
/**
 * Is passed to the TreeClient to notify it about changes in the 
 * server-side ITree.
 * Event ID describes the change. Can be expanded later.
 *
 * @author  serbo
 */
public interface AidaUpdateEvent extends Serializable {
    
      // Specify what kind of change happened in ITree
      public static int NODE_UPDATED = 0;
      public static int NODE_ADDED   = 1;
      public static int NODE_DELETED = 2;
      public static int TREE_CLOSED = 3;
      public static int FOLDER_IS_FILLED = 5;
      public static int DO_DATA_UPDATE_NOW = 10;
      public static int DO_ADD_NODE_NOW = 11;
      public static int NODE_TEMPORARY_UNAVAILABLE = 20;
      public static int NODE_IS_AVAILABLE_AGAIN = 21;
      public static int REMOTE_CONNECTION_EXCEPTION = 101;
     
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
