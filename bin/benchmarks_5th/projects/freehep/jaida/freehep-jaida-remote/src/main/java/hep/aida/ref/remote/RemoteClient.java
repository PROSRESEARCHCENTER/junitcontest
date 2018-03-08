/*
 * BasicTreeClient.java
 *
 * Created on May 11, 2003, 6:46 PM
 */

package hep.aida.ref.remote;

import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaTreeServant;
import hep.aida.ref.remote.interfaces.AidaTreeServer;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is Basic implementation of AidaTreeClient that support both "Duplex"
 * and "non-Duplex" modes of communication with the AIDA Tree server.
 * 
 * In "Duplex" mode AidaTreeServant call stateChanged() method to notify
 * BasicTreeClient about updates/changes in the server-side AIDA tree.
 *
 * In "non-Duplex" mode BasicTreeClient runs as a separate thread and
 * periodically calls updates() method of AidaTreeServant to get information
 * about updates/changes in the server-side AIDA tree.
 *
 * BasicTreeClient also implements IMutableStore, so it can be used as a
 * Store for any IDevTree.
 *
 * @author  serbo
 */
public abstract class RemoteClient implements AidaTreeClient, Runnable {
    
    protected AidaTreeServer server;
    protected AidaTreeServant servant;
    protected IDevMutableStore store;
    protected RemoteUpdatableQueue queue;
    protected boolean duplex;
    protected boolean blocking = false;
    protected boolean isConnected;
    protected long updateInterval;
    protected boolean keepUpdating;
    protected String clientID;
    protected int connectionExceptions = 0;
    
    protected Logger remoteLogger;
    
    private AidaTreeServer testServer; // Used for local tests only

    /** 
     * Creates a new instance of RemoteClient.
     * Duplex is default to "true". If AidaTreeServer does not support
     * Duplex mode, try to connect in non-Duplex.
     */
    public RemoteClient(IDevMutableStore store) {
        this(store, true);
    }
    
    public RemoteClient(IDevMutableStore store, boolean duplex) {
        init();
        this.store = store;
        this.duplex = duplex;
        this.keepUpdating = !duplex;
        remoteLogger = Logger.getLogger("hep.aida.ref.remote");
   }
    
    /**
     * This constructor is used for local tests only.
     */
     RemoteClient(IDevMutableStore store, boolean duplex, AidaTreeServer server) {
        init();
        this.store = store;
        this.duplex = duplex;
        this.keepUpdating = !duplex;
        this.testServer = server;
        remoteLogger = Logger.getLogger("hep.aida.ref.remote");
    }
    
    
    // Service methods
    
    protected void init() {     
        server = null;
        servant = null;
        isConnected = false;
        blocking = false;
        updateInterval = 2000;
        clientID = "AidaTreeClient";
    }
    
    public void setBlocking(boolean b) { 
        blocking = b; 
        if (servant instanceof RemoteServant) ((RemoteServant) servant).setBlocking(blocking);
        if (blocking && clientID.indexOf("blocking") < 0) clientID = clientID + "_blocking";
    }
    public boolean isBlocking() { return blocking; }
    
    /**
     * Retrieves reference to the AidaTreeServer. Must be overwritten by 
     * subclasses.
     */
    protected abstract AidaTreeServer getServer();
    
    /**
     * Set time interval (in milliseconds) for AidaTreeClient to check for updates.
     * Relevant only for non-Duplex Mode, as in Duplex Mode updates are "pushed" 
     * into AidaTreeClient by AidaTreeServer calling "stateChanged()" method. 
     * Default value: updateInterval=2000 milliseconds.
     */
    public synchronized void setUpdateTime(long updateInterval) {  
        this.updateInterval = updateInterval; 
    }
    
    /**
     * Set duplex mode. Default is "true".
     */
    public synchronized void setDuplex(boolean duplex) {  
        if (isConnected) {
            remoteLogger.info("WARNING: Client is connected, can not change DUPLEX settings. "+
                               "Please disconnect first");
            return;
        }
        this.duplex = duplex;
        this.keepUpdating = !duplex;
    }
    
    /**
     * Retrieves Duplex AidaTreeServant from the AidaTreeServer.
     */
    protected void connectDuplex() throws RemoteConnectionException {
        servant = server.connectDuplex(this);
        if (servant == null) {
            throw new RemoteConnectionException("Can not retrieve non-Duplex AidaTreeServant from: "+server.treeName());
        }
        isConnected = true;
    }
    
    /**
     * Retrieves non-Duplex AidaTreeServant from the AidaTreeServer.
     */
    protected void connectNonDuplex() throws RemoteConnectionException {
        servant = server.connectNonDuplex(clientID);
        if (servant == null) {
            throw new RemoteConnectionException("Can not retrieve non-Duplex AidaTreeServant from: "+server.treeName());
        }
        if (servant instanceof RemoteServant) ((RemoteServant) servant).setBlocking(blocking);
        //if (!clientID.equals(newClientID)) clientID = newClientID; 
        new Thread(this).start(); // Start asking servant for updates.
        isConnected = true;
    }
    

    // AidaTreeClient methods
    
     public String[] listObjectNames(String path, boolean recursive) throws IllegalArgumentException {
        if (!isConnected) {
            remoteLogger.info("WARNING: Client is not connected.");
            return null;
        }
        String[] names = servant.listObjectNames(path, recursive);
        return names;
     }

     public String[] listObjectTypes(String path, boolean recursive) throws IllegalArgumentException {
        if (!isConnected) {
            remoteLogger.info("WARNING: Client is not connected.");
            return null;
        }
        String[] types = servant.listObjectTypes(path, recursive);
        return types;
      }


     public Object find(String path) throws IllegalArgumentException {
        remoteLogger.finest("RemoteClient.find: for path="+path);
        if (!isConnected) {
            remoteLogger.info("WARNING: Client is not connected.");
            return null;
        }
        Object obj = servant.find(path);

        remoteLogger.finest("RemoteClient.find: AFTER find for path="+path);
        if (servant != null) servant.setValid(new String[] { path } );
        
        remoteLogger.finest("RemoteClient.find: for path="+path+", obj="+obj);
        return obj;
      }

      /**
     * In this implementation stateChanged(UpdateEvent[] events) method simply
     * schedules updates in the UpdatableQueue. Later queue invokes stateChanged(UpdateEvent event)
     * method on a separate thread to process updates.
     */
    public void stateChanged(AidaUpdateEvent[] events) {
        if (events != null && events.length > 0) {
            remoteLogger.finest("RemoteClient.stateChanged: #events="+events.length);
                queue.schedule(store, events);
        }
    }
    /*
    public void stateChanged(AidaUpdateEvent[] events) {
        if (events != null && events.length > 0) {
            remoteLogger.finest("RemoteClient.stateChanged: #events="+events.length);
            for (int i=0; i<events.length; i++) {
                queue.schedule(store, events[i]);
            }
        }
    }
    */
    public boolean isConnected() {
        return isConnected;
    }  
    
    public boolean connect() throws RemoteConnectionException {
        if (isConnected) {
            String name = "null";
            if (server != null) name = server.treeName();
            remoteLogger.info("WARNING: Already connected to AidaTreeServer: "+ name);
            return false;
        }
        queue = new RemoteUpdatableQueue();
        server = getServer();
        remoteLogger.fine("Connecting:  duplex="+duplex);
        if (server == null) 
            throw new RemoteConnectionException("Can not get reference to AidaTreeServer.");
        try {
            if (duplex) {
                boolean supportsDuplex = server.supportDuplexMode();
                if (!supportsDuplex) {
                    remoteLogger.info("Warning: AidaTreeServer \""+server.treeName()+
                                        "\" does not support DUPLEX mode. \nWill try to connect using non-DUPLEX mode.");
                    duplex = false;
                    keepUpdating = !duplex;
                    connectNonDuplex();
                } else {
                    connectDuplex();
                }
            } else {
                connectNonDuplex();
            }
        } catch (RemoteConnectionException ae) { 
            throw ae;
        } catch (Exception e) { 
            String name = "null";
            if (server != null) name = server.treeName();
            throw new RemoteConnectionException("Can not connect to AidaTreeServer: "+name, e);
        }
        return true;
    }
    
    public boolean disconnect() {
        remoteLogger.finest("\nRemoteClient.disconnect: for Client="+clientID+" ... ");
        keepUpdating = false;
        if (queue != null) {
            queue.close();
        }
        if (!isConnected) {
            server = null;
            servant = null;
            return true;
        }
        boolean status = true;
        keepUpdating = false;
        if (server != null) {
            try {
                if (duplex) status = server.disconnectDuplex(this);
                else status = server.disconnectNonDuplex(clientID);
            } catch (Exception e) {
                remoteLogger.log(Level.FINE, "RemoteClient.disconnect Exception: ", e);                
            }
        }
        server = null;
        servant = null;
        return status;
    }
    
    
    // Runnable methods
    
    public void run() {
        while (keepUpdating) {
	    try {
                if (isConnected) {
                    remoteLogger.finest("RemoteClient.updates");
                    AidaUpdateEvent[] events = servant.updates();
                    connectionExceptions = 0;
                    remoteLogger.finest("RemoteClient.updates BEFORE stateChanged");
                    if (keepUpdating && events != null && events.length > 0) stateChanged(events);
                    remoteLogger.finest("RemoteClient.updates AFTER stateChanged blocking="+blocking);
                }
                
		if (!blocking) Thread.sleep(updateInterval);
		
            } catch (InterruptedException ie) {
                remoteLogger.log(Level.INFO, "RemoteClient non-DUPLEX Update Thread InterruptedException. blocking="+blocking, ie);
                remoteLogger.log(Level.FINE, "", ie.getStackTrace());           
	    } catch (Exception ex) {
                remoteLogger.log(Level.INFO, "RemoteClient non-DUPLEX Update Thread: Exception. blocking="+blocking, ex);
                remoteLogger.log(Level.FINE, "", ex.getStackTrace()); 
                connectionExceptions++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie2) { ie2.printStackTrace(); }
            }
             
            if (connectionExceptions > 6) {
                AidaUpdateEvent[] events = new AidaUpdateEvent[1];
                events[0] = new RemoteUpdateEvent(AidaUpdateEvent.REMOTE_CONNECTION_EXCEPTION, "/", "dir");
                stateChanged(events);
            }
	}
        remoteLogger.fine("RemoteClient run: "+clientID+" ... Exiting");
    }
        
    
    // Do some simple tests here
    public static void main(String[] args) {
    }
}
