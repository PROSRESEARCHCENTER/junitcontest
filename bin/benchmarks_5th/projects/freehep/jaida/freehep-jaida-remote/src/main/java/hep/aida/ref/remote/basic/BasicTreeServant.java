/*
 * BasicTreeServant.java
 *
 * Created on May 12, 2003, 11:16 AM
 */

package hep.aida.ref.remote.basic;

import hep.aida.IAnalysisFactory;
import hep.aida.IBaseHistogram;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.HistogramEvent;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.event.TreeEvent;
import hep.aida.ref.remote.basic.interfaces.AidaTreeClient;
import hep.aida.ref.remote.basic.interfaces.AidaTreeServant;
import hep.aida.ref.remote.basic.interfaces.UpdateEvent;
import hep.aida.ref.tree.Tree;

import java.util.EventObject;
import java.util.Vector;

/**
 * Basic implementation of the AidaTreeServant, no remote stuff.
 * @author  serbo
 */
public class BasicTreeServant extends Thread implements AidaTreeServant, AIDAListener {
    
    protected ITree tree;
    protected AidaTreeClient client;
    protected String clientID;
    protected boolean duplex;
    protected boolean keepRunning;
    protected Vector sources;
    protected ServerQueue queue;
    protected boolean useValidation;
    
    /** Creates a new instance of BasicTreeServant */
    public BasicTreeServant(ITree tree, String clientID) {
        System.out.println("BasicTreeServant() clientID="+clientID);
        this.tree = tree;
        this.clientID = clientID;
        this.client = null;
        duplex = false;
        init();
    }
    
    public BasicTreeServant(ITree tree, AidaTreeClient client) {
        System.out.println("BasicTreeServant() client="+client);
        this.tree = tree;
        this.clientID = null;
        this.client = client;
        duplex = true;
        init();
        this.start(); // Start thread that pushes updates into the client.
    }
    
    // Service methods
    
    protected void init() {
        sources = new Vector();
        queue = new ServerQueue();
        useValidation = true;
        keepRunning = duplex;
        if (tree instanceof IsObservable) {
            ((IsObservable) tree).addListener(this);
            sources.add(tree);
            ((IsObservable) tree).setValid(this);
        }
        if (tree instanceof hep.aida.ref.tree.Tree) 
            ((Tree) tree).setFolderIsWatched("/", true);

    }
    
    /**
     * If useValidation = true, client has to call "setValid" method after
     * receiving update from the ManagedObject in order to reseive next update.
     * If useValidation = false, client receives all updates.
     */
    public void setUseValidation(boolean state) { useValidation = state; }
    
    public void close() {
        synchronized ( this ) {
            System.out.print("\n\tClosing BasicTreeServant ... "); 
            keepRunning = false;
            if (duplex) synchronized ( queue ) { queue.notify(); }
	    for (int i=0; i<sources.size(); i++) {
		IsObservable o = (IsObservable) sources.get(i);
		    if (o != null) o.removeListener(this);
	    }
	    sources.clear();
	    sources = null;
	    tree = null;
	    client = null;
	    queue.close();
	    queue = null;
            System.out.print(" Done\n");
	}
    }
    
    // AidaTreeServant methods
    
    public java.lang.Object find(String path) {
        IManagedObject mo = tree.find(path);
        //System.out.println("BasicTreeServant.find path="+path+",  mo="+mo);
        if (mo instanceof IsObservable && !sources.contains(mo)) {
            ((IsObservable) mo).addListener(this);
            sources.add(mo);
        }
        if (!useValidation && mo instanceof IsObservable) ((IsObservable) mo).setValid(this);
        return mo;
    }
    
    public String[] listObjectNames(String path) { 
        //System.out.println("BasicTreeServant.listObjectNames path="+path);
        if (tree instanceof hep.aida.ref.tree.Tree) 
            ((Tree) tree).setFolderIsWatched(path, true);
        return tree.listObjectNames(path); 
    }    

    public String[] listObjectTypes(String path) { 
        //System.out.println("BasicTreeServant.listObjectTypes path="+path);
        if (tree instanceof hep.aida.ref.tree.Tree) 
            ((Tree) tree).setFolderIsWatched(path, true);
        return tree.listObjectTypes(path); 
    }
    
    public void setValid(String[] paths) { 
        if (paths == null || paths.length == 0) return;
        for (int i=0; i<paths.length; i++) {
            if (paths[i] == null || paths[i].equals("") || paths[i].equals("/") ) {
                if (tree instanceof IsObservable) ((IsObservable) tree).setValid(this);
            } else {
                IManagedObject mo =  tree.find(paths[i]);
                if (mo instanceof IsObservable && !sources.contains(mo)) {
                    ((IsObservable) mo).addListener(this);
                    sources.add(mo);
                }
                //System.out.println("BasicTreeServant.setValid: name="+mo.name()+",  path="+paths[i]);
                if (mo instanceof IsObservable) ((IsObservable) mo).setValid(this);
            }
        }
    }
    
    /**
     * This method can be used only in non-Duplex mode. Never returns null.
     */
    public UpdateEvent[] updates() {
        UpdateEvent[] events = new UpdateEvent[0];
        if (!duplex) {
            events = queue.getEvents();
        }
        //System.out.println("BasicTreeServant.updates: return "+events.length+" events.");
        return events;
    }
    
    // AIDAListener methods
    
    /**
     * Create new UpdateEvent from the EventObject and put it in the queue
     * of current updates. This method is called by "IsObservable" sources
     * to report their change of state.
     */
    public void stateChanged(EventObject ev) {
        int id = -1;
        String pathString = "";
        String nodeType = "null";
        if (ev instanceof TreeEvent) {
            TreeEvent tev = (TreeEvent) ev;
            //System.out.println("BasicTreeServant.stateChanged GOT TreeEvent: id="+tev.getID()+", type="+
            //                    tev.getType()+", flags="+tev.getFlags()+
            //                    ", path="+(tev.getPath())[(tev.getPath()).length-1]);
            
            String[] path = tev.getPath();
            if (path != null) for (int i=0; i<path.length; i++) { pathString += "/" + path[i]; }
            
            if (tev.getType() != null)  nodeType = tev.getType().getName();
            if (tev.getFlags() == TreeEvent.FOLDER_MASK) nodeType = "dir";
            
            if ( tev.getID()== TreeEvent.NODE_ADDED) {
                id = UpdateEvent.NODE_ADDED;
                IManagedObject hist = tree.find(pathString);
                nodeType = hist.type();
            }
            else if ( tev.getID()== TreeEvent.NODE_DELETED) id = UpdateEvent.NODE_DELETED;
            //else if ( tev.getID()== TreeEvent.NODE_RENAMED) id = UpdateEvent.NODE_UPDATED;
            else id = -1;
            
	    if (tree instanceof IsObservable) ((IsObservable) tree).setValid(this);
        } else if (ev instanceof HistogramEvent) {
            IBaseHistogram hist = (IBaseHistogram) ev.getSource();
            id = UpdateEvent.NODE_UPDATED;
            pathString = tree.findPath((IManagedObject) hist);
            nodeType = ((IManagedObject)hist).type();
            if (!useValidation && hist instanceof IsObservable) ((IsObservable) hist).setValid(this);
        }

        if (id >= 0) {
            queue.schedule((UpdateEvent) (new BasicUpdateEvent(id, pathString, nodeType)));
            //System.out.println("TreeServant: process Event: id = "+id+",  path = "+pathString+",  type = "+nodeType);
        }
    }
 
    // Thread methods
    public void run() {
        while (duplex && keepRunning) {
            int size = 0;
            UpdateEvent[] events = null;
            try {
                synchronized ( queue ) {
		    if(queue.size() == 0) queue.wait();
                    size = (queue == null) ? 0 : queue.size();
                    if (size > 0) events = queue.getEvents();
                }
                //System.out.println("ServerQueue.run Processing: "+size);
                if (events == null || events.length == 0) return;
                client.stateChanged(events);
           } catch (InterruptedException e2) {
                System.out.println("ServerQueue Thread InterruptedException.");
                e2.printStackTrace();
                
	    } catch (Exception e3) {
                System.out.println("Problems in ServerQueue!.");
                e3.printStackTrace();
            } // end of try/catch
        } // end of while
    }
    
    public static void main(String[] args) {
        java.util.Random r = new java.util.Random();
        IAnalysisFactory af = IAnalysisFactory.create();

        ITreeFactory tf = af.createTreeFactory();
        ITree serverTree = tf.create();
        ((IsObservable) serverTree).addListener(new TestBasic((Tree) serverTree));
        
        IHistogramFactory histogramFactory = af.createHistogramFactory(serverTree);
        
        int nEntries = 1000;
        int xbins = 10;
        double xLowerEdge = -10.;
        double xUpperEdge = 10.;

        serverTree.mkdir("/dir1");
        IHistogram1D h1 = histogramFactory.createHistogram1D("Hist-1",xbins,xLowerEdge,xUpperEdge);
        IHistogram1D h2 = histogramFactory.createHistogram1D("Hist-2",xbins,xLowerEdge,xUpperEdge);
        /* Fill the histogram */
        for (int i=0; i<nEntries; i++) {
            double xval = r.nextGaussian()*3+2.;
            h1.fill( xval );
            xval = r.nextGaussian()*3+2.;
            h2.fill( xval );
        }

        System.out.println("Creating TreeServant ...");
        BasicTreeServant servant = new BasicTreeServant(serverTree, "Test Servant");
        
        try {
            System.out.println("Servant is ready. To add Hist-3 press ENTER");
            System.in.read();

            IHistogram1D h3 = histogramFactory.createHistogram1D("Hist-1",xbins,xLowerEdge,xUpperEdge);

            System.out.println("To call \"updates()\" press ENTER");
            System.in.read();
            UpdateEvent[] ue = servant.updates();
            System.out.println("Got "+ue.length+"  events");
            for (int i=0; i<ue.length; i++)
                System.out.println("Event "+i+"   id="+ue[i].id()+"  path="+ue[i].path()+"   type="+ue[i].nodeType());
            
        }  catch(Exception e) {
            e.printStackTrace();
        }
    }
}
