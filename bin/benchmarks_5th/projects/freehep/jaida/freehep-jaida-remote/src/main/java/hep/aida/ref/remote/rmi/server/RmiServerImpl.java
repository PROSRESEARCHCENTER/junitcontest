/*
 * RmiServerImpl.java
 *
 * Created on October 15, 2003, 7:02 PM
 */

package hep.aida.ref.remote.rmi.server;

// For the tests in main
import java.io.*;
import java.util.*;
import hep.aida.*;
import hep.aida.dev.*;
import hep.aida.ref.remote.*;
import hep.aida.ref.remote.rmi.*;
import hep.aida.ref.remote.rmi.client.*;
import hep.aida.ref.remote.rmi.converters.*;
import hep.aida.ref.remote.rmi.data.*;
import hep.aida.ref.remote.rmi.interfaces.*;
import org.freehep.util.FreeHEPLookup;
//

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import  java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.logging.Level;

import hep.aida.ref.remote.RemoteConnectionException;
import hep.aida.ref.remote.RemoteServer;
import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaTreeServant;
import hep.aida.ref.remote.interfaces.AidaTreeServer;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;
import hep.aida.ref.remote.rmi.RmiRemoteUtils;
import hep.aida.ref.remote.rmi.interfaces.RmiClient;
import hep.aida.ref.remote.rmi.interfaces.RmiServant;
import hep.aida.ref.remote.rmi.interfaces.RmiServer;

/**
 *
 * @author  serbo
 */
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {
    
    static final long serialVersionUID = 5979753791192166996L;
    private AidaTreeServer aidaServer;
    private int port;
    private String currentHost = null;
    private String bindName;
    private boolean useValidation;
    private Map servants;
    private transient Logger remoteLogger;
    
    /** Creates a new instance of RmiClientImpl */
    public RmiServerImpl(AidaTreeServer aidaServer) throws MalformedURLException, RemoteException, UnknownHostException {
        this(aidaServer, null);
    }
    public RmiServerImpl(AidaTreeServer aidaServer, String bindName) throws MalformedURLException, RemoteException, UnknownHostException {
        super();
        port = RmiRemoteUtils.port;
        this.aidaServer = aidaServer;
        this.bindName = bindName;
        remoteLogger = Logger.getLogger("hep.aida.ref.remote");
        remoteLogger.setLevel(Level.SEVERE);
        this.servants = new Hashtable();
        this.currentHost = InetAddress.getLocalHost().getHostName();
        this.port = RmiRemoteUtils.port;
        useValidation = true;
        connect();
    }
    
    
    // Service methods
    
    /**
     * If useValidation = true, client has to call "setValid" method after
     * receiving update from the ManagedObject in order to reseive next update.
     * If useValidation = false, client receives all updates.
     */
    public synchronized void setUseValidation(boolean state) { useValidation = state; }

    public void connect() throws MalformedURLException, RemoteException, UnknownHostException {
        if (bindName == null) bindName = createBindName();
        else {
            int index = bindName.indexOf(":");
            if (index >0) {
                String portString = bindName.substring(index+1);
                int index2 = portString.indexOf("/");
                if (index2 > 0) { portString = portString.substring(0, index2); }
                try {
                    int tmpPort = Integer.parseInt(portString);
                    port = tmpPort;
                } catch (NumberFormatException nfe) {
                    //throw new RuntimeException("Bind Name is not formatted correctly: "+bindName, nfe);
                }
                
            }
        }
        remoteLogger.fine("RmiServer: Binding in Registry: "+bindName+", port="+port);
        try {
            Naming.rebind(bindName, this);
        } catch (ConnectException co) {
            //co.printStackTrace();
            remoteLogger.fine("RmiServer: No RMI Registry is currently available for port="+port+". Starting new RMI Registry.");
            LocateRegistry.createRegistry(port);
            Naming.rebind(bindName, this);
        }            
        remoteLogger.info("RmiServer ready at rmi:"+bindName);                
    }
    
    public void unbind() {
        try {
            remoteLogger.fine("RmiServer: unbinding server from Registry: "+bindName+", port="+port);
            Naming.unbind(bindName);
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public void disconnect() {
	remoteLogger.fine("RmiServer.disconnect: Start");
        unbind();
        synchronized ( servants ) {
            try {
		if (!servants.isEmpty()) {
		    Set s = servants.keySet();
                    Object[] arr = new Object[s.size()];
                    s.toArray(arr);
                    for (int i=0; i<arr.length; i++) {
			Object clientRef = arr[i];
                        try {
                            unRegisterServant(clientRef);
                        } catch (Exception e3) { e3.printStackTrace(); }
		    }
                    s.clear();
		    servants.clear();
		}
                unexportObject(this, true);
            } catch (Exception e2) { e2.printStackTrace(); }
        }
 	remoteLogger.finest("RmiServer.disconnect: Finish");
        servants = null;
        aidaServer = null;
    }
    
    private String createBindName() {
        String name = "/RmiAidaServer";
        String dateString = RmiRemoteUtils.getCurrentDateString();
        
        name = "//"+currentHost+":"+port+name+"/"+dateString;
        
        return name;
    }
    
    private boolean checkServant(java.lang.Object key) throws RemoteException {
        boolean ok = false;
        if (servants.containsKey(key)) { ok = true; }
        return ok;
    }
    
    private void registerServant(java.lang.Object key, RmiServantImpl rmiServant) throws RemoteException {
        servants.put(key,  rmiServant);
    }
    
    private boolean unRegisterServant(java.lang.Object key) throws RemoteException {
        RmiServantImpl rmiServant = (RmiServantImpl) servants.remove(key);
        if (rmiServant == null) return false;
        rmiServant.disconnect();        
        return true;        
    }
    
    // RmiServer methods
    
    public String getBindName() throws RemoteException { return bindName; }
    
    public RmiServant connectDuplex(RmiClient client) throws RemoteException {
        RmiServantImpl rmiServant = null;
        if (checkServant(client)) {
            String clientRef = client.toString();
            if (client instanceof RmiClient) clientRef = ((RmiClient) client).getBindName();
            throw new RemoteConnectionException("This client is already connected. Please disconnect first.\nClient: "+clientRef);
        }
        rmiServant = new RmiServantImpl(client);
        rmiServant.setUseValidation(useValidation);
        AidaTreeServant aidaServant = aidaServer.connectDuplex(rmiServant);
        if (aidaServant == null) throw new RemoteConnectionException("Can not connect to Server: "+aidaServer.treeName());
        rmiServant.setAidaTreeServant(aidaServant);
        registerServant(client, rmiServant);
        return rmiServant;
    }
    
    public RmiServant connectNonDuplex(String clientID) throws RemoteException {
        if (checkServant(clientID)) {
           throw new RemoteConnectionException("This client is already connected. Please disconnect first.\nClient: "+clientID);
        }
        AidaTreeServant aidaServant = aidaServer.connectNonDuplex(clientID);
        if (aidaServant == null) throw new RemoteConnectionException("Can not connect to Server: "+aidaServer.treeName());
        RmiServantImpl rmiServant = new RmiServantImpl();
        rmiServant.setUseValidation(useValidation);
        rmiServant.setAidaTreeServant(aidaServant);
        registerServant(clientID, rmiServant);
        return rmiServant;
   }
    
    public boolean disconnectDuplex(RmiClient client) throws RemoteException {
        if (!checkServant(client)) {
            String clientRef = client.toString();
            if (client instanceof RmiClient) clientRef = ((RmiClient) client).getBindName();
            throw new RemoteConnectionException("This client is not connected.\nClient: "+clientRef);
        }
        RmiServantImpl rmiServant = (RmiServantImpl) servants.get(client);
        boolean ok = aidaServer.disconnectDuplex(rmiServant);
        return unRegisterServant(client) && ok;
    }
    
    public boolean disconnectNonDuplex(String clientID) throws RemoteException {
        if (!checkServant(clientID)) {
            throw new RemoteConnectionException("This client is not connected.\nClient: "+clientID);
        }
        boolean ok = aidaServer.disconnectNonDuplex(clientID);
        return unRegisterServant(clientID) && ok;
    }
    
    public boolean supportDuplexMode() throws RemoteException {
        return aidaServer.supportDuplexMode();
    }
    
    public String treeName() throws RemoteException {
        return aidaServer.treeName();
    }
    
    
    
    // Do some tests here
    public static void main(String[] args) throws Exception {
        
        // Create RmiStoreFactory and register it in lookup
        RmiStoreFactory rsf = new RmiStoreFactory();

        // Create an AIDA tree
        System.out.println("Creating AIDA server tree");
	IAnalysisFactory anf = IAnalysisFactory.create();
	ITreeFactory tf = anf.createTreeFactory();
	
        IDevTree tree = (IDevTree) tf.create();    
        
        // Populate tree
        System.out.println("Populating AIDA server tree");
        Vector hist1D = new Vector();
        tree.mkdirs("/dir1-1");
        tree.mkdirs("/dir1-2");
        tree.mkdirs("/dir1-1/dir2-1/dir3-1");
        tree.mkdirs("/dir1-1/dir2-2");
        tree.mkdirs("/dir1-2/dir2-1");
        
        IHistogramFactory hf = anf.createHistogramFactory(tree);
        
        tree.cd("/dir1-1");
        hist1D.add(hf.createHistogram1D("Hist1D 1", "Flat Histogram 1", 50, 0, 0.9));
        
        tree.cd("/dir1-2/dir2-1");
        hist1D.add(hf.createHistogram1D("Hist1D 2", "Flat Histogram 2", 50, 0.1, 1));
        hist1D.add(hf.createHistogram1D("Hist1D 3", "Gauss Histogram 3", 50, -3, 3));
        
        tree.cd("/dir1-1/dir2-1/dir3-1");
        hist1D.add(hf.createHistogram1D("Hist1D 4", "Gauss Histogram 4", 50, -3, 3));
        
        tree.cd("/dir1-1/dir2-2");
         
        // Update histograms
        System.out.println("Updating histograms in AIDA server tree");
        Random r = new Random();

        for (int k=0; k<hist1D.size(); k++) {
            for (int i = 0; i < 1000; i++ ) {
             IHistogram1D h1D = (IHistogram1D) hist1D.get(k);
                h1D.fill(r.nextDouble());
            }
        }   
        
        System.out.println("Creating RemoteServer");
        RemoteServer treeServer = new RemoteServer(tree);
        treeServer.setBlocking(true);
        
        System.out.println("Creating RmiServer");
        RmiServerImpl rmiTreeServer = new RmiServerImpl(treeServer);
        
        String clientName = "RmiClientTree";
        String bindName = rmiTreeServer.getBindName();
        String options = "duplex=\"false\",RmiServerName=\""+bindName+"\"";
        
        /*
        System.out.println("Creating Client Tree: bindName="+bindName+", options="+options);
        ITree clientTree = tf.create(clientName, RmiStoreFactory.storeType, true, false, options);        
        
        
        // Open all client Directories
        String[] dirs = clientTree.listObjectNames("/");
        String[] types = clientTree.listObjectTypes("/");
        for (int i=0; i<dirs.length; i++) {
            if (types[i].equalsIgnoreCase("dir")) {
                String[] dirs1 = clientTree.listObjectNames("/"+dirs[i]);
                String[] types1 = clientTree.listObjectTypes("/"+dirs[i]);
                for (int ii=0; ii<dirs1.length; ii++) {
                    if (types1[ii].equalsIgnoreCase("dir")) {
                        String[] dirs2 = clientTree.listObjectNames(dirs1[ii]);
                        String[] types2 = clientTree.listObjectTypes(dirs1[ii]);
                        for (int iii=0; iii<dirs2.length; iii++) {
                            if (types2[iii].equalsIgnoreCase("dir")) {
                                String[] dirs3 = clientTree.listObjectNames(dirs2[iii]);
                                String[] types3 = clientTree.listObjectTypes(dirs2[iii]);
                            }
                        }
                    }
                }
            }
        }
        */
        
        // Wait for user input
        System.out.println("\n\nInput: u - update histograms, a - add histogram to a tree, d - delete last added histogram, e - exit");
        System.out.print("> ");
        BufferedReader console = new BufferedReader( new InputStreamReader(System.in));
        String input = null;
        while ((input = console.readLine()) != null ) {
            try {
                if (input.equalsIgnoreCase("e")) System.exit(1);
                else if (input.equalsIgnoreCase("u")) {  // update existing histograms
                    for (int k=0; k<hist1D.size(); k++) {
                        for (int i = 0; i < 1000; i++ ) {
                            IHistogram1D h1D = (IHistogram1D) hist1D.get(k);
                            h1D.fill(r.nextDouble());
                        }
                    }                   
                } else if (input.equalsIgnoreCase("a")) {  // Add new histogram
                    int id = hist1D.size() + 1;
                    hist1D.add(hf.createHistogram1D("Extra Hist1D "+id, "Extra Flat Histogram id="+id, 50, 0.1, 0.9));               
                } else if (input.equalsIgnoreCase("d")) {  // Delete histogram that was added last
                    int id = hist1D.size() - 1;
                    IManagedObject h1D = (IManagedObject) hist1D.remove(id);
                    String path = tree.findPath(h1D);
                    tree.rm(path);
                } else {
                    System.out.println("Wrong input: "+input);
                    System.out.println("\n\nInput: u - update histograms, a - add histogram to a tree, d - delete last added histogram, e - exit");
                }
                System.out.print("> ");
            } catch (Exception e) { 
                e.printStackTrace(); 
                System.out.println("\n\nInput: u - update histograms, a - add histogram to a tree, d - delete last added histogram, e - exit");
                System.out.print("> ");
            }
        }
    }
}
