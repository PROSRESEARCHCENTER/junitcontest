/*
 * RmiTreeClientImpl.java
 *
 * Created on October 14, 2003, 8:29 PM
 */

package hep.aida.ref.remote.rmi.client;

import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.remote.RemoteClient;
import hep.aida.ref.remote.RemoteConnectionException;
import hep.aida.ref.remote.interfaces.AidaTreeServer;
import hep.aida.ref.remote.rmi.RmiRemoteUtils;
import hep.aida.ref.remote.rmi.interfaces.RmiServer;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is implementation of AidaTreeClient interface,
 * based on functionality of the RemoteClient class. It implements
 * only one method - getServer(), that creates an AidaTreeServer
 * wrapper (RmiAidaTreeServer) around the real RmiServer reference.
 * This complex scheme is put in place to isolate the transport layer
 * (RMI, CORBA, etc.) from the functional layer of working with the
 * AIDA Tree, queues, events, threads.
 *
 * @author  serbo
 */
public class RmiRemoteClient extends RemoteClient {
    
    private Map options;
    
    /** Creates a new instance of RmiAidaTreeClient */
    public RmiRemoteClient(IDevMutableStore store, Map options) {
        super(store);
        this.options = options;
    }
    
    public RmiRemoteClient(IDevMutableStore store, boolean duplex, Map options) {
        super(store, duplex);
        this.options = options;
    }
    
    
    protected void init() {   
        super.init();
        clientID = "RmiRemoteClient";
        try {
            clientID = System.getProperty("user.name", "RmiRemoteClient");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        String localHost = null;
        try {
             localHost = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();          
        }        
        if (localHost != null) clientID = clientID +"@"+ localHost;
        
        clientID = clientID+"-"+RmiRemoteUtils.getCurrentDateString();
    }
    
    
    // Try to handle all RMI export and connection here
    protected AidaTreeServer getServer() {
        AidaTreeServer treeServer = null;
        
        try {
            // Try to resolve RMI server
            RmiServer server = null;
            String rmiServerName = null;
            Iterator it = options.keySet().iterator();
            boolean blk = false;
            while (it.hasNext()) {
                String key = ((String) it.next());
                String value = ((String) options.get(key));
                remoteLogger.fine("Key = "+key+" \tValue = "+value);
                
                if (key.equalsIgnoreCase("RmiServerName")) {
                    rmiServerName = value; 
               } else if (key.equalsIgnoreCase("blocking")) {
                   if ("false".equals(value)) blk = false;
                   else blk = true;
               } else if (key.equalsIgnoreCase("updateInterval")) {
                   long millis = this.updateInterval;
                   try {
                        double d = Double.parseDouble(value);
                        millis = (long) (d*1000);
                   } catch (NumberFormatException nfe) {
                       System.out.println("RmiRemoteClient.getServer :: Can not set updateInterval: \""+value+"\"\n\t"+nfe.getMessage());
                   }
                   this.setUpdateTime(millis);
               }
            }
            setBlocking(blk);
            
            server = (RmiServer) Naming.lookup(rmiServerName);

            // Create AidaTreeServer
            treeServer = new RmiRemoteServer(server);
        } catch (Exception e) {
            throw new RemoteConnectionException(e.getMessage(), e);
        }
        return treeServer;
    }    

    public boolean disconnect() {
        boolean ok = super.disconnect();
        if (options != null) options.clear();
        options = null;
        return ok;
    }
}
