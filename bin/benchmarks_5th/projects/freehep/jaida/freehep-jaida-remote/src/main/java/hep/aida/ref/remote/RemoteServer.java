/*
 * RemoteServer.java
 *
 * Created on October 22, 2003, 9:01 PM
 */

package hep.aida.ref.remote;

import hep.aida.dev.IDevTree;
import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.event.TreeEvent;
import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaTreeServant;
import hep.aida.ref.remote.interfaces.AidaTreeServer;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This is implementation of the AidaTreeServer.
 * This class does not have any remote transport layer (RMI, CORBA, etc.),
 * so special adapter classes, like RmiRemoteServer, are used to provide
 * such functionality.
 * Default is to support duplex mode.
 *
 * @author  serbo
 */

public class RemoteServer implements AidaTreeServer, AIDAListener {
    
    private IDevTree tree;
    private String treeName;
    private boolean duplex;
    protected boolean blocking;
    private boolean useValidation;
    private boolean appendAxisType;
    private Map servantHash;
    private boolean acceptNewConnections;
    private long timeOut;
    protected Logger remoteLogger;
    
    /** Creates a new instance of RemoteServer */
    public RemoteServer(IDevTree tree) {
        this(tree, true);
    }
    
    public RemoteServer(IDevTree tree, boolean duplex) {
        this(tree, duplex, false);
    }
    public RemoteServer(IDevTree tree, boolean duplex, boolean appendAxisType) {
        this.tree = tree;
        this.duplex = duplex;
        blocking = false;
        this.appendAxisType = appendAxisType;
        useValidation = true;
        treeName = tree.storeName();
        servantHash = new Hashtable();
        acceptNewConnections = true;
        remoteLogger = Logger.getLogger("hep.aida.ref.remote");
        timeOut = 5000;
        
        if (tree instanceof IsObservable) ((IsObservable) tree).addListener(this);
    }
    
    
    // Service methods
    
    public void setBlocking(boolean b) { blocking = b; }
    public boolean isBlocking() { return blocking; }
    
    public void setTimeout(long t) { timeOut = t; }
    public long getTimeout() { return timeOut; }
    
    /**
     * If useValidation = true, client has to call "setValid" method after
     * receiving update from the ManagedObject in order to reseive next update.
     * If useValidation = false, client receives all updates.
     */
    public synchronized void setUseValidation(boolean state) { useValidation = state; }

    private AidaTreeServant connect(java.lang.Object clientRef) {
        remoteLogger.info("New connection from Client:  "+clientRef+", acceptNewConnections="+acceptNewConnections);
        if (clientRef == null) {
            throw new RemoteConnectionException("Can not connect with NULL Client Reference.");
        } 
        AidaTreeServant servant = null;
        if (!acceptNewConnections) return servant;
        synchronized ( servantHash ) {
            servant = (AidaTreeServant) servantHash.get(clientRef);
            if (servant != null) {
                throw new RemoteConnectionException("This client is already connected. Please disconnect first.\nClient: "+clientRef.toString());
            } else {
                if (clientRef instanceof String) {
                    servant = new RemoteServant(tree, (String) clientRef);
                    boolean tmpBlocking = blocking;
                    if (((String) clientRef).indexOf("blocking") >= 0) tmpBlocking = true;
                    ((RemoteServant) servant).setBlocking(tmpBlocking);
                }
                else if (clientRef instanceof AidaTreeClient) 
                    servant = new RemoteServant(tree, (AidaTreeClient) clientRef);
                
                ((RemoteServant) servant).setAppendAxisType(appendAxisType);
                ((RemoteServant) servant).setUseValidation(useValidation);
                
                servantHash.put(clientRef, servant);
            }
        }
        return servant;
    }

    /**
     * Disconnect servant for a particular client.
     * RemoteServer remains functional after that
     */
    private boolean disconnect (java.lang.Object clientRef) {
        remoteLogger.info("\tDisconnecting Client: "+clientRef+", acceptNewConnections="+acceptNewConnections);
        AidaTreeServant servant = (AidaTreeServant) servantHash.get(clientRef);
        try {
            if (servant instanceof RemoteServant) ((RemoteServant) servant).close();
            servantHash.remove(clientRef);
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Disconnect servants for all client.
     * RemoteServer remains functional after that
     */
    private void disconnectAll() {
        synchronized ( servantHash ) {
            if (!servantHash.isEmpty()) {
                Set keySet = servantHash.keySet();
                int size = keySet.size();
                Object[] a = new Object[size];
                keySet.toArray(a);
                for (int i=0; i<size; i++) {
                    Object clientRef = a[i];
                    disconnect(clientRef);
                }
                servantHash.clear();
            }
        }
    }
    
    /**
     * Disconnect servants for all client, server and release all
     * allocated resources. RemoteServer does not function after that
     */
    public void close() {
        acceptNewConnections = false;
        try {
            if (tree instanceof IsObservable) ((IsObservable) tree).removeListener(this);
        } catch (Exception e) { e.printStackTrace(); }
        
        disconnectAll();
        servantHash.clear();
        servantHash = null;
        tree = null;
    }
    
    
    // AidaTreeServer methods
    
    public AidaTreeServant connectDuplex(AidaTreeClient client) {
        return connect(client);
    }
    
    public AidaTreeServant connectNonDuplex(String clientID) {
        return connect(clientID);
    }
    
    public boolean disconnectDuplex(AidaTreeClient client) {
        return disconnect(client);
    }
    
    public boolean disconnectNonDuplex(String clientID) {
        return disconnect(clientID);
    }
    
    public boolean supportDuplexMode() { return duplex; }
    
    public String treeName() { return treeName; }
    
    
    // AIDAListener method
    
    public void stateChanged(java.util.EventObject event) {
        boolean closeTree = false;
        if (event instanceof TreeEvent) {
            if (((TreeEvent) event).getID() == TreeEvent.TREE_CLOSED) {
                closeTree = true;
            }
        } else if (event instanceof AidaUpdateEvent) {
            if (((AidaUpdateEvent) event).id() == AidaUpdateEvent.TREE_CLOSED) {
                closeTree = true;
            }
        }
        
        if (!closeTree) return;
        
        acceptNewConnections = false;
        remoteLogger.info("Got TREE_CLOSED event. Closing this RemoteServer after delay: "+timeOut);
        
        Thread t = new Thread( new Runnable() {
            public void run() {
                // Give Servants time to disconnect, then close everything
                try {
                    Thread.sleep(timeOut);
                    close();
                    remoteLogger.info("Server is closed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } );
        
        t.start();
    }
    
}
