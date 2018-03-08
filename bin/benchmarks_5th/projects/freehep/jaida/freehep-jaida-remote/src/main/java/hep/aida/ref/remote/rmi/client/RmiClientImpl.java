/*
 * RmiClientImpl.java
 *
 * Created on October 15, 2003, 7:02 PM
 */

package hep.aida.ref.remote.rmi.client;

import hep.aida.ref.remote.RemoteConnectionException;
import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaTreeServant;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;
import hep.aida.ref.remote.rmi.RmiRemoteUtils;
import hep.aida.ref.remote.rmi.interfaces.RmiClient;
import hep.aida.ref.remote.rmi.interfaces.RmiServant;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

/**
 *
 * @author  serbo
 */
//public class RmiClientImpl extends UnicastRemoteObject implements RmiClient, AidaTreeServant {
public class RmiClientImpl extends UnicastRemoteObject implements RmiClient, AidaTreeServant {
    
    static final long serialVersionUID = 2380161439616929888L;
    private RmiServant rmiServant;
    private AidaTreeClient aidaClient;
    private int port = RmiRemoteUtils.port;
    private String currentHost = null;
    private String bindName;
    private boolean duplex;
    private transient Logger remoteLogger;
    
    /** Creates a new instance of RmiClientImpl */
    public RmiClientImpl(boolean duplex) throws MalformedURLException, RemoteException, UnknownHostException  {
        this(null, null, duplex);
    }
    
    public RmiClientImpl(RmiServant rmiServant, boolean duplex) throws MalformedURLException, RemoteException, UnknownHostException  {
        this(rmiServant, null, duplex);
    }

    public RmiClientImpl(AidaTreeClient aidaClient, boolean duplex) throws MalformedURLException, RemoteException, UnknownHostException  {
        this(null, aidaClient, duplex);
    }

    public RmiClientImpl(RmiServant rmiServant, AidaTreeClient aidaClient, boolean duplex) throws MalformedURLException, RemoteException, UnknownHostException  {
        super();
        this.rmiServant = rmiServant;
        this.aidaClient = aidaClient;
        this.duplex = duplex;
        remoteLogger = Logger.getLogger("hep.aida.ref.remote");
        if (duplex) connect();
    }
    
    // Service methods
    
    void setAidaTreeClient(AidaTreeClient aidaClient) {
        this.aidaClient = aidaClient;
    }
    
    void setRmiServant(RmiServant rmiServant) {
        this.rmiServant = rmiServant;
    }
    
    void connect() throws MalformedURLException, RemoteException, UnknownHostException {
        currentHost = InetAddress.getLocalHost().getHostAddress();
        bindName = createBindName();
        /*
        System.out.println("RmiClient: Bind in Registry: "+bindName);
        try {
            Naming.rebind(bindName, this);
        } catch (ConnectException co) {
            System.out.println("RmiClient: No RMI Registry is currently available for port="+port+". Starting new RMI Registry.");
            LocateRegistry.createRegistry(port);
            Naming.rebind(bindName, this);
        }            
         */
        remoteLogger.fine("RmiClient ready");
    }
    
    void disconnect() {
	remoteLogger.finest("RmiClient.disconnect: Start");
        
        if (duplex) {
            try {
                unexportObject(this, true);
            } catch (Exception e2) { e2.printStackTrace(); }
        }
        
	remoteLogger.finest("RmiClient.disconnect: Finish");
        rmiServant = null;
        aidaClient = null;
    }
    
    private String createBindName() {
        String name = "/RmiAidaClient";
        String dateString = RmiRemoteUtils.getCurrentDateString();
        
        name = "//"+currentHost+":"+port+name+"/"+dateString;
        
        return name;
    }
    
    
    // RmiClient methods
    
    public String getBindName() throws RemoteException { return bindName; }
    
    public void stateChanged(AidaUpdateEvent[] events) throws RemoteException{
        aidaClient.stateChanged(events);
    }
    
    
    // AidaTreeServant methods
    
    public java.lang.Object find(String path) {
        remoteLogger.finest("RmiClient find for path="+path);     
        java.lang.Object result = null;
        try {
            result = rmiServant.find(path);
        } catch (RemoteException re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }
        remoteLogger.finest("RmiClient find for path="+path+", obj="+result);     
        return result;
    }
    
    public String[] listObjectNames(String path, boolean recursive) {
        String[] result = null;
        try {
            result = rmiServant.listObjectNames(path, recursive);
        } catch (RemoteException re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }
        return result;
    }
    
    public String[] listObjectTypes(String path, boolean recursive) {
        String[] result = null;
        try {
            result = rmiServant.listObjectTypes(path, recursive);
        } catch (RemoteException re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }
        return result;
    }
    
    public void setValid(String[] nodePaths) {
        remoteLogger.finest("RmiClient setValid for path="+nodePaths[0]);     
        try {
            rmiServant.setValid(nodePaths);
        } catch (RemoteException re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }
        remoteLogger.finest("RmiClient setValid AFTER for path="+nodePaths[0]);     
    }
    
    public AidaUpdateEvent[] updates() {
        AidaUpdateEvent[] result = null;
        try {
            result = rmiServant.updates();
        } catch (RemoteException re) {
            throw new RemoteConnectionException(re.getMessage(), re);
        }
        remoteLogger.fine("RmiClient updates gotEvents="+result.length);     
        return result;
    }
        
}
