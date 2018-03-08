/*
 * CorbaTreeClient.java
 *
 * Created on June 8, 2003, 5:08 PM
 */

package hep.aida.ref.remote.corba;

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPointSet;
import hep.aida.IHistogram1D;
import hep.aida.IManagedObject;
import hep.aida.IPlotter;
import hep.aida.ITreeFactory;
import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.AnalysisFactory;
import hep.aida.ref.remote.RemoteConnectionException;
import hep.aida.ref.remote.RemoteUpdatableQueue;
import hep.aida.ref.remote.RemoteUpdateEvent;
import hep.aida.ref.remote.corba.generated.EventFlags;
import hep.aida.ref.remote.corba.generated.EventID;
import hep.aida.ref.remote.corba.generated.EventStruct;
import hep.aida.ref.remote.corba.generated.TreeClient;
import hep.aida.ref.remote.corba.generated.TreeClientHelper;
import hep.aida.ref.remote.corba.generated.TreeClientPOA;
import hep.aida.ref.remote.corba.generated.TreeServant;
import hep.aida.ref.remote.corba.generated.TreeServer;
import hep.aida.ref.remote.corba.generated.TreeServerHelper;
import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;
import hep.aida.ref.tree.Tree;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.StringTokenizer;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

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

public class CorbaTreeClientImpl extends TreeClientPOA implements AidaTreeClient, Runnable { 
    
    protected TreeServer server;
    protected TreeServant servant;
    protected IDevMutableStore store;
    protected RemoteUpdatableQueue queue;
    protected boolean duplex;
    protected boolean isConnected;
    protected long updateInterval;
    protected boolean keepUpdating;
    protected String clientID;
    
    protected ORB orb;
    protected POA rootPOA;
    protected TreeClient treeClient;
    protected String ior;
    protected String nsName;
    //protected NamingContext nc;

    /** Creates a new instance of CorbaTreeClient 
     * Duplex is default to "true". If TreeServer does not support
     * Duplex mode, try to connect in non-Duplex.
     */
    public CorbaTreeClientImpl() {
        this(null, null, null);
    }

    public CorbaTreeClientImpl(IDevMutableStore store, String ior) {
        this(store, ior, null);
    }
    
    public CorbaTreeClientImpl(IDevMutableStore store, String ior, String nsName) {
        init();
        this.store = store;
        this.ior = ior.trim();
        this.nsName = nsName;
        if (nsName != null) {
        }
        this.duplex = true;
        this.keepUpdating = !duplex;
        System.out.println("Created CorbaTreeClientImpl with nsName="+nsName+", ior="+ior);
    }
    
    
    // Service methods
    
    protected void init() {     
        server = null;
        servant = null;
        isConnected = false;
        updateInterval = 2000;
        clientID = "CorbaTreeClient"+"_"+System.currentTimeMillis();
    }
    
    /**
     * Retrieves reference to the TreeServer.
     */
    protected  TreeServer getServer() {
        TreeServer server = null;
        if (nsName == null) {
            // Use IOR string of server object to resolve it
            try {
                org.omg.CORBA.Object objRef = orb.string_to_object(ior);
                server = TreeServerHelper.narrow(objRef);
                boolean supportsDuplex = server.supportDuplexMode();
                System.out.println("\nTreeServer support for the Duplex Mode: "+supportsDuplex);
            } catch (Exception t) { 
                t.printStackTrace();
                server = null;
            }
        } else {
            // Use IOR string of CORBA Name Service
            StringTokenizer tokenizer = new StringTokenizer(nsName, "/");
            int nTokens = tokenizer.countTokens();
            NameComponent[] pathName = new NameComponent[nTokens];
            for(int i = 0; i < nTokens; i++)
                {
                    pathName[i] = new NameComponent(tokenizer.nextToken(), "");
                    //System.out.println("AmbientDataProvider.connect: Token "+i+", pathName: "+pathName[i].id);
                }

            // Get the root naming context
            try {
                org.omg.CORBA.Object objRef = orb.string_to_object(ior);
                NamingContext nc = NamingContextHelper.narrow(objRef);
                // Resolve OdcBdbServer object
                org.omg.CORBA.Object serverObject = nc.resolve(pathName);
                server = TreeServerHelper.narrow(objRef);
            } catch (Exception e) {
                e.printStackTrace();
                server = null;
            }
        }
        return server;
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
        System.out.println("CorbaTreeClientImpl.setDuplex "+duplex);
    }
    
    /**
     * Retrieves Duplex AidaTreeServant from the AidaTreeServer.
     */
    protected void connectDuplex() throws RemoteConnectionException {
        // Get the Root POA
        try {
            org.omg.CORBA.Object rootRef = orb.resolve_initial_references("RootPOA");
            rootPOA = POAHelper.narrow(rootRef);

            // Activate POA and create a reference for the TreeServantImpl
            rootPOA.the_POAManager().activate();
            org.omg.CORBA.Object obj = rootPOA.servant_to_reference(this);
            String ref = orb.object_to_string(obj);
            System.out.println("TreeClient IOR String: \n\t"+ref);

            treeClient = TreeClientHelper.narrow(obj);
            servant = server.connectDuplex(treeClient);
        } catch (RemoteConnectionException ae) { 
            throw ae;
        } catch (Exception e) { 
            String name = "null";
            if (server != null) name = server.treeName();
            throw new RemoteConnectionException("Can not connect to TreeServer: "+name, e);
        }

        if (servant == null) {
            throw new RemoteConnectionException("Can not retrieve Duplex TreeServant from: "+server.treeName());
        }

        new Thread(this).start(); // Wait for requests.
        isConnected = true;
    }
    
    /**
     * Retrieves non-Duplex AidaTreeServant from the AidaTreeServer.
     */
    protected void connectNonDuplex() throws RemoteConnectionException {
        servant = server.connectNonDuplex(clientID);
        if (servant == null) {
            throw new RemoteConnectionException("Can not retrieve non-Duplex TreeServant from: "+server.treeName());
        }
        //if (!clientID.equals(newClientID)) clientID = newClientID; 
        new Thread(this).start(); // Start asking servant for updates.
        isConnected = true;
    }
    
    public void setValid(String path) {
        servant.setValid( new String[] { path } );        
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


     public java.lang.Object find(String path) throws IllegalArgumentException {
        if (!isConnected) {
            System.out.println("WARNING: Client is not connected.");
            return null;
        }
        Any data = servant.find(path);
        //servant.setValid(new String[] { path });
        return data;
      }

      /**
     * In this implementation stateChanged(UpdateEvent[] events) method simply
     * schedules updates in the UpdatableQueue. Later queue invokes stateChanged(UpdateEvent event)
     * method on a separate thread to process updates.
     */
    public void stateChanged(EventStruct[] events) {
        if (events != null && events.length != 0) {
            RemoteUpdateEvent[] aidaEvents = new RemoteUpdateEvent[events.length];
            String[] paths = new String[events.length];
            for (int i=0; i<events.length; i++) { 
                String path = events[i].path;
                boolean folder = (events[i].flags == EventFlags.FOLDER_MASK);
                String type = events[i].nodeType;
                if (folder) type = "dir";
                int id = -1;
                if      ( events[i].id == EventID.NODE_UPDATED) id = AidaUpdateEvent.NODE_UPDATED;
                else if ( events[i].id == EventID.NODE_ADDED)   id = AidaUpdateEvent.NODE_ADDED;
                else if ( events[i].id == EventID.NODE_DELETED) id = AidaUpdateEvent.NODE_DELETED;
                    
                aidaEvents[i] = new RemoteUpdateEvent(id, path, type);
                paths[i] = path;
            }
            stateChanged(aidaEvents);
            //servant.setValid(paths);
        }
    }

    public void stateChanged(AidaUpdateEvent[] events) {
        if (events != null && events.length > 0) {
            for (int i=0; i<events.length; i++) {
                queue.schedule(store, events[i]);
            }
        }
    }
    
    public boolean isConnected() {
        return isConnected;
    }  
    
    public boolean connect() {
        if (isConnected) {
            String name = "null";
            if (server != null) name = server.treeName();
            System.out.println("WARNING: Already connected to TreeServer: "+ name +". No action taken.");
            return false;
        }
        // Create and initialize the ORB
        String[] orbArgs = {};
        orb = ORB.init(orbArgs, null);

        server = getServer(); // Get server
        System.out.println("Connecting:  duplex="+duplex);
        if (server == null) 
            throw new RemoteConnectionException("Can not get reference to TreeServer.");
        queue = new RemoteUpdatableQueue();
        try {
            if (duplex) {
                boolean supportsDuplex = server.supportDuplexMode();
                if (!supportsDuplex) {
                    System.out.println("Warning: TreeServer \""+server.treeName()+
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
            throw new RemoteConnectionException("Can not connect to TreeServer: "+name, e);
        }
        return true;
    }
    
    public boolean disconnect() {
        System.out.print("\nCorbaTreeClientImpl.disconnect: for Client="+clientID+", isConnected="+isConnected);
        queue.close();
        queue = null;
        if (!isConnected) {
            keepUpdating = false;
            server = null;
            servant = null;
            try {
                orb.shutdown(true);
                orb.destroy();
            } catch (Exception ex) { ex.printStackTrace(); }
            return true;
        }
        boolean status = true;
        keepUpdating = false;
        if (server != null) {
            if (duplex) status = server.disconnectDuplex(treeClient);
            else status = server.disconnectNonDuplex(clientID);
        }
        server = null;
        servant = null;
        System.out.print(" Done.\n");

        System.out.println("Shutting down CORBA Client ... ");
        try {
            orb.shutdown(true);
            orb.destroy();
        } catch (Exception ex) { ex.printStackTrace(); }
        orb = null;
        rootPOA = null;
        System.out.println(" Done!\n");
        return status;
    }
    
    
    // Runnable methods
    
    public void run() {
        if (duplex) {
            orb.run();
        } else {
            while (keepUpdating) {
                if (isConnected) {
                    EventStruct[] events = servant.updates();
                    System.out.println("BasicTreeClient.run: GOT "+events.length+" events.");
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
    }
        
    
    // Do some simple tests here
    public static void main(String[] args) {
        
        java.util.Random r = new java.util.Random();
        IAnalysisFactory af = new AnalysisFactory();
        ITreeFactory tf = af.createTreeFactory();
        Tree clientTree = null;
        IPlotter plotter = af.createPlotterFactory().create("Plot");
        plotter.createRegions(2,2,0);
        plotter.show();
        
        try {
            //clientTree = (Tree) tf.create("CORBA_Test", "corba", true, false, "iorFileURL=file:///C:/Temp/TreeServer.ior");
            clientTree = (Tree) tf.create("CORBA_Test", "corba", true, false, "iorFileURL=http://www.slac.stanford.edu/~serbo/jas3/TreeServer.ior");
        } catch (Exception e) { e.printStackTrace(); System.exit(1); }
        
            
            // Setup the io
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            PrintStream out = System.out;       ;
            String menuMain = "\n\n" +
                "===============================\n" +
                " cd <path> \n"+
                " pwd \n"+
                " ls\n"+
                " find <path> \n"+
                " plot <path> <region> \n"+
                " check <path> \n"+
                " mkdirs <path> \n"+
                " Q \n" +
                "-------------------------------\n" +
                "User Input => " ;
            while (true) {
                try {
                out.print(menuMain);
                String input = console.readLine();

                StringTokenizer st = new StringTokenizer(input.trim(), " ");
                String[] tokens = new String[st.countTokens()];
                int i = 0;
                while (st.hasMoreTokens()) { tokens[i] = (st.nextToken()).trim(); i++; }

                if (tokens[0].startsWith("cd")) {
                    clientTree.cd(tokens[1]);
                }
                else if (tokens[0].startsWith("pwd")) {
                    out.println(clientTree.pwd());
                }
                else if (tokens[0].startsWith("ls")) {
                    clientTree.ls();
                }
                else if (tokens[0].startsWith("find")) {
                    IManagedObject mo = clientTree.find(tokens[1]);
                    out.println("Found: name="+mo.name()+",  mo="+mo);
                }
                else if (tokens[0].startsWith("check")) {
                    clientTree.checkForChildren(tokens[1]);
                }
                else if (tokens[0].startsWith("mkdirs")) {
                    clientTree.mkdirs(tokens[1]);
                }
                else if (tokens[0].startsWith("plot")) {
                    IManagedObject mo =  clientTree.find(tokens[1]);
                    if (mo instanceof IHistogram1D)
                        plotter.region(Integer.parseInt(tokens[2])).plot((IHistogram1D) mo);
                    else if (mo instanceof IDataPointSet)
                        plotter.region(Integer.parseInt(tokens[2])).plot((IDataPointSet) mo);
                    
                    plotter.show();
                }
                else if (input.toLowerCase().startsWith("q")) {
                    clientTree.close();
                    System.exit(0);
                }
                } catch (Exception e) { e.printStackTrace(); }
        

            } // End while
    } // End main   
        
}
