/*
 * AidaTreeServer.java
 *
 * Created on May 11, 2003, 5:29 PM
 */

package hep.aida.ref.remote.interfaces;

/**
 * Main AIDA Tree Server - manages connect/disconnect of AidaTreeClients
 * and life cycle of AidaTreeServants. Each TreeServant can be associated 
 * with only one TreeClient.
 * Each AidaTreeServer can be associated with only one ITree.
 * @author  serbo
 */
public interface AidaTreeServer {

      /**
       * Return the name of the ITree it is connected to.
       */
      String treeName();

      /**
       * Returns "true" if this TreeServer/TreeServant implementation
       * support "Duplex Mode".
       */
      boolean supportDuplexMode();

      /**
       * connect/disconnect methods for TreeClient that does not support "Duplex" Mode.
       * TreeClient has to provide a unique "clientID" to TreeServer. If "clientID" is
       * not unique, TreeServer changes it, so TreeClient must check "newClientID",
       * returned by TreeServer.
       */
      AidaTreeServant connectNonDuplex(String clientID);
      boolean disconnectNonDuplex(String clientID);

      /**
       * connect/disconnect methods for TreeClient that does support "Duplex" Mode.
       * Reference to instance of TreeClient serves also as a unique client ID.
       */
      AidaTreeServant connectDuplex(AidaTreeClient client);
      boolean disconnectDuplex(AidaTreeClient client);
}
