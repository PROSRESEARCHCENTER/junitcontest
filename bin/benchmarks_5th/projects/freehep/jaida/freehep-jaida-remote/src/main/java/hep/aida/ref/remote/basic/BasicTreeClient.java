/*
 * BasicTreeClient.java
 *
 * Created on May 11, 2003, 6:46 PM
 */

package hep.aida.ref.remote.basic;

import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.remote.basic.interfaces.AidaConnectionException;
import hep.aida.ref.remote.basic.interfaces.AidaTreeClient;
import hep.aida.ref.remote.basic.interfaces.AidaTreeServant;
import hep.aida.ref.remote.basic.interfaces.AidaTreeServer;
import hep.aida.ref.remote.basic.interfaces.UpdateEvent;

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
public class BasicTreeClient implements AidaTreeClient, Runnable {
    
    protected AidaTreeServer server;
    protected AidaTreeServant servant;
    protected IDevMutableStore store;
    protected UpdatableQueue queue;
    protected boolean duplex;
    protected boolean isConnected;
    protected long updateInterval;
    protected boolean keepUpdating;
    protected String clientID;
    
    private AidaTreeServer testServer; // Used for local tests only

    /** 
     * Creates a new instance of BasicTreeClient.
     * Duplex is default to "true". If AidaTreeServer does not support
     * Duplex mode, try to connect in non-Duplex.
     */
    public BasicTreeClient() {
        this(null, true);
    }
    
    public BasicTreeClient(IDevMutableStore store) {
        this(store, true);
    }
    
    public BasicTreeClient(IDevMutableStore store, boolean duplex) {
        init();
        this.store = store;
        this.duplex = duplex;
        this.keepUpdating = !duplex;
        queue = new UpdatableQueue();
    }
    
    /**
     * This constructor is used for local tests only.
     */
     BasicTreeClient(IDevMutableStore store, boolean duplex, AidaTreeServer server) {
        init();
        this.store = store;
        this.duplex = duplex;
        this.keepUpdating = !duplex;
        this.testServer = server;
    }
    
    
    // Service methods
    
    protected void init() {     
        server = null;
        servant = null;
        isConnected = false;
        updateInterval = 2000;
        clientID = "AidaTreeClient";
    }
    
    /**
     * Retrieves reference to the AidaTreeServer. Should be overwritten by 
     * subclasses.
     */
    protected AidaTreeServer getServer() {
        return testServer;
    }
    
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
            System.out.println("WARNING: Client is connected, can not change DUPLEX settings. "+
                               "Please disconnect first");
            return;
        }
        this.duplex = duplex;
        this.keepUpdating = !duplex;
    }
    
    /**
     * Retrieves Duplex AidaTreeServant from the AidaTreeServer.
     */
    protected void connectDuplex() throws AidaConnectionException {
        servant = server.connectDuplex(this);
        if (servant == null) {
            throw new AidaConnectionException("Can not retrieve non-Duplex AidaTreeServant from: "+server.treeName());
        }
        isConnected = true;
    }
    
    /**
     * Retrieves non-Duplex AidaTreeServant from the AidaTreeServer.
     */
    protected void connectNonDuplex() throws AidaConnectionException {
        servant = server.connectNonDuplex(clientID);
        if (servant == null) {
            throw new AidaConnectionException("Can not retrieve non-Duplex AidaTreeServant from: "+server.treeName());
        }
        //if (!clientID.equals(newClientID)) clientID = newClientID; 
        new Thread(this).start(); // Start asking servant for updates.
        isConnected = true;
    }
    

    // AidaTreeClient methods
    
     public String[] listObjectNames(String path) throws IllegalArgumentException {
        if (!isConnected) {
            System.out.println("WARNING: Client is not connected.");
            return null;
        }
        String[] names = servant.listObjectNames(path);
        return names;
     }

     public String[] listObjectTypes(String path) throws IllegalArgumentException {
        if (!isConnected) {
            System.out.println("WARNING: Client is not connected.");
            return null;
        }
        String[] types = servant.listObjectTypes(path);
        return types;
      }


     public Object find(String path) throws IllegalArgumentException {
        if (!isConnected) {
            System.out.println("WARNING: Client is not connected.");
            return null;
        }
        Object obj = servant.find(path);
        return obj;
      }

      /**
     * In this implementation stateChanged(UpdateEvent[] events) method simply
     * schedules updates in the UpdatableQueue. Later queue invokes stateChanged(UpdateEvent event)
     * method on a separate thread to process updates.
     */
    public void stateChanged(UpdateEvent[] events) {
        if (events != null && events.length > 0) {
            for (int i=0; i<events.length; i++) {
                queue.schedule(store, events[i]);
            }
        }
    }
    
    public boolean isConnected() {
        return isConnected;
    }  
    
    public boolean connect() throws AidaConnectionException {
        if (isConnected) {
            String name = "null";
            if (server != null) name = server.treeName();
            System.out.println("WARNING: Already connected to AidaTreeServer: "+ name);
            return false;
        }
        queue = new UpdatableQueue();
        server = getServer();
        System.out.println("Connecting:  duplex="+duplex);
        if (server == null) 
            throw new AidaConnectionException("Can not get reference to AidaTreeServer.");
        try {
            if (duplex) {
                boolean supportsDuplex = server.supportDuplexMode();
                if (!supportsDuplex) {
                    System.out.println("Warning: AidaTreeServer \""+server.treeName()+
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
        } catch (AidaConnectionException ae) { 
            throw ae;
        } catch (Exception e) { 
            String name = "null";
            if (server != null) name = server.treeName();
            throw new AidaConnectionException("Can not connect to AidaTreeServer: "+name, e);
        }
        return true;
    }
    
    public boolean disconnect() {
        System.out.print("\nBasicTreeClient.disconnect: for Client="+clientID+" ... ");
        queue.close();
        queue = null;
        if (!isConnected) {
            keepUpdating = false;
            server = null;
            servant = null;
            return true;
        }
        boolean status = true;
        keepUpdating = false;
        if (server != null) {
            if (duplex) status = server.disconnectDuplex(this);
            else status = server.disconnectNonDuplex(clientID);
        }
        server = null;
        servant = null;
        System.out.print(" Done.\n");
        return status;
    }
    
    
    // Runnable methods
    
    public void run() {
        while (keepUpdating) {
            if (isConnected) {
                UpdateEvent[] events = servant.updates();
                //System.out.println("BasicTreeClient.run: GOT "+events.length+" events.");
                if (events != null && events.length > 0) stateChanged(events);
            }
	    try {
		Thread.sleep(updateInterval);
            } catch (InterruptedException ie) {
                System.out.println("AidaTreeClient non-DUPLEX Update Thread InterruptedException.");
                ie.printStackTrace();
                
	    } catch (Exception ex) { ex.printStackTrace(); }
	}
    }
        
    
    // Do some simple tests here
    public static void main(String[] args) {
    }
            
}
