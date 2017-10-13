/*
 * RmiAidaTreeServer.java
 *
 * Created on October 15, 2003, 1:07 AM
 */

package hep.aida.ref.remote.rmi.client;

import hep.aida.ref.remote.RemoteConnectionException;
import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaTreeServant;
import hep.aida.ref.remote.interfaces.AidaTreeServer;
import hep.aida.ref.remote.rmi.interfaces.RmiServant;
import hep.aida.ref.remote.rmi.interfaces.RmiServer;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * This is a wraper class that allows separation of RMi
 * transport layer and internal hep.aide.ref.remote interfaces.
 * Not a very elegant solution, but needed for now since transport
 * interfaces may be different for different protocol type.
 * @author  serbo
 */

public class RmiRemoteServer implements AidaTreeServer {
    
    private RmiServer server;
    private Map hash;
    
    /** Creates a new instance of RmiAidaTreeServer */
    public RmiRemoteServer(RmiServer server) {
        this.server = server;
        this.hash = new Hashtable();
    }
    
    
    
    // Service methods
    
    void disconnect() {
	//System.out.println("RmiRemoteServer.disconnect");
        synchronized ( hash ) {
            if (!hash.isEmpty()) {
                Iterator it = hash.keySet().iterator();
                while (it.hasNext()) {
                    Object clientRef = it.next();
                    unRegisterClient(clientRef);
                }
                hash.clear();
            }
        }
        hash = null;
    }
    
    private boolean checkClient(java.lang.Object key) {
        boolean ok = false;
        if (hash.containsKey(key)) { ok = true; }
        return ok;
    }
    
    private void registerClient(java.lang.Object key, RmiClientImpl rmiClient) {
        hash.put(key,  rmiClient);
    }
    
    private boolean unRegisterClient(java.lang.Object key) {
        RmiClientImpl rmiClient = (RmiClientImpl) hash.remove(key);
        if (rmiClient == null) return false;
        rmiClient.disconnect();        
        return true;        
    }
    
    
    
    // AidaTreeServant methods
    
    public AidaTreeServant connectDuplex(AidaTreeClient aidaClient) {
        if (checkClient(aidaClient)) {
            String clientRef = aidaClient.toString();
            throw new RemoteConnectionException("This client is already connected. Please disconnect first.\nClient: "+clientRef);
        }
        boolean duplex = true;
        RmiClientImpl aidaServant = null;
	RmiServant rmiServant = null;
	try {
	    aidaServant = new RmiClientImpl(aidaClient, duplex);
	    rmiServant = server.connectDuplex(aidaServant);
        } catch (RemoteConnectionException re) {
            throw re;
        } catch (Exception re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }

        aidaServant.setRmiServant(rmiServant);
        registerClient(aidaClient, aidaServant);
        return aidaServant;
    }
    
    public AidaTreeServant connectNonDuplex(String clientID) {
        if (checkClient(clientID)) {
            throw new RemoteConnectionException("This client is already connected. Please disconnect first.\nClient: "+clientID);
        }
        boolean duplex = false;
	RmiServant rmiServant = null;
	RmiClientImpl aidaServant = null;
	try {
	    rmiServant = server.connectNonDuplex(clientID);
	    aidaServant = new RmiClientImpl(rmiServant, duplex);
        } catch (Exception e) {
            throw new RemoteConnectionException(e.getMessage(), e);
        }
        
        registerClient(clientID, aidaServant);
        return aidaServant;
    }
    
    public boolean disconnectDuplex(AidaTreeClient aidaClient) {
        if (!checkClient(aidaClient)) {
            String clientRef = aidaClient.toString();
            throw new RemoteConnectionException("This client is not connected.\nClient: "+clientRef);
        }
        RmiClientImpl rmiClient = (RmiClientImpl) hash.get(aidaClient);
        boolean ok = false;
	try {
	    ok = server.disconnectDuplex(rmiClient);
         } catch (RemoteException re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }
       return unRegisterClient(aidaClient) && ok;
    }
    
    public boolean disconnectNonDuplex(String clientID) {
        if (!checkClient(clientID)) {
            throw new RemoteConnectionException("This client is not connected.\nClient: "+clientID);
        }
        RmiClientImpl rmiClient = (RmiClientImpl) hash.get(clientID);
	boolean ok = false;
	try {
	    ok = server.disconnectNonDuplex(clientID);
        } catch (RemoteException re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }
        return unRegisterClient(clientID) && ok;
    }
    
    public boolean supportDuplexMode() {
        boolean result = false;
	try {
            result = server.supportDuplexMode();
        } catch (RemoteException re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }
        return result;
    }
    
    public String treeName() {
        String result = null;
        try {
            result = server.treeName();
        } catch (RemoteException re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }
        return result;
    }
    
}
