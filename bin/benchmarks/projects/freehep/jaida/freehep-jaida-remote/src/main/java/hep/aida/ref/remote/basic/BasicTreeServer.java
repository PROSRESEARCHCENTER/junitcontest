/*
 * BasicTreeServer.java
 *
 * Created on May 12, 2003, 10:46 AM
 */

package hep.aida.ref.remote.basic;

import hep.aida.ITree;
import hep.aida.ref.remote.basic.interfaces.AidaTreeClient;
import hep.aida.ref.remote.basic.interfaces.AidaTreeServant;
import hep.aida.ref.remote.basic.interfaces.AidaTreeServer;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Basic implementation of the AidaTreeServer, no remote stuff.
 * @author  serbo
 */
public class BasicTreeServer implements AidaTreeServer {
    
    protected ITree tree;
    protected String treeName;
    protected Hashtable hash;
   
    /** Creates a new instance of BasicTreeServer */
    public BasicTreeServer(ITree tree) {
        this(tree, tree.storeName());
    }
    
    public BasicTreeServer(ITree tree, String treeName) {
        System.out.print("\nStarting TreeServer with Tree Name: "+treeName+" ... ");
        this.tree = tree;
	this.treeName = treeName;
        init();
        System.out.print(" Done.\n");
    }
    
    
    // Service methods
    
    protected void init() {
        hash = new Hashtable();   
    }
    
    protected boolean disconnectClient(Object client) {
        System.out.print("\n\tBasicTreeServer.disconnectClient disconnecting Client: "+client.toString());
        BasicTreeServant servant = null;
	synchronized ( hash ) {
	    if (hash.containsKey(client)) {
		servant = (BasicTreeServant) hash.get(client);
		hash.remove(client);
            } else {
                System.out.print("\n\t*** Warning: Can not find AidaTreeServant for this Client!");
                return false;
            }
        }
        servant.close();
        System.out.print(" Done.\n");
	return true;
    }
    
    public void close() {
        System.out.print("Shutting down BasicTreeServer ... ");
        synchronized ( this ) {
            if (!hash.isEmpty()) {
		Iterator it = hash.values().iterator();
		while (it.hasNext()) {
		    BasicTreeServant servant = (BasicTreeServant) it.next();
		    servant.close();
		}
		hash.clear();
	    }
	    hash = null;
	    tree = null;
	    treeName = null;
	}
	System.out.print(" Done!\n");
    }

    
    // AidaTreeServer methods
    
    public AidaTreeServant connectDuplex(AidaTreeClient client) {
        BasicTreeServant servant = null;
        if (hash.containsKey(client)) {
            System.out.println("Warning: this Client is already connected, returning its servant");
            servant = (BasicTreeServant) hash.get(client);
        } else {
            servant = new BasicTreeServant(tree, client);
            hash.put(client, servant);
        }
        return servant;
    }
    
    public AidaTreeServant connectNonDuplex(String clientID) {
        BasicTreeServant servant = null;
        System.out.print("\nBasicTreeServer.connect: clientID="+clientID+" ... ");
        if (hash.containsKey(clientID)) {
            System.out.println("Warning: this Client is already connected, returning its servant");
            servant = (BasicTreeServant) hash.get(clientID);
        } else {
            servant = new BasicTreeServant(tree, clientID);
            hash.put(clientID, servant);
        }
        System.out.print(" Done.\n");
        return servant;
    }
    
    public boolean disconnectDuplex(AidaTreeClient client) {
        return disconnectClient(client);
    }
    
    public boolean disconnectNonDuplex(String clientID) {
        return disconnectClient(clientID);
    }

    public boolean supportDuplexMode() { return true; }
    
    public String treeName() { return treeName; }
    
}
