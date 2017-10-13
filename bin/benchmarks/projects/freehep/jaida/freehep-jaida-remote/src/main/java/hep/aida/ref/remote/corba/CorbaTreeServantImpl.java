/*
 * CorbaTreeServantImpl.java
 *
 * Created on June 8, 2003, 8:08 PM
 */

package hep.aida.ref.remote.corba;

import hep.aida.IBaseHistogram;
import hep.aida.IDataPointSet;
import hep.aida.IManagedObject;
import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.DataPointSetEvent;
import hep.aida.ref.event.HistogramEvent;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.event.TreeEvent;
import hep.aida.ref.remote.corba.converters.CorbaConverter;
import hep.aida.ref.remote.corba.generated.EventFlags;
import hep.aida.ref.remote.corba.generated.EventID;
import hep.aida.ref.remote.corba.generated.EventStruct;
import hep.aida.ref.remote.corba.generated.TreeClient;
import hep.aida.ref.remote.corba.generated.TreeServantPOA;
import hep.aida.ref.tree.Tree;

import java.util.EventObject;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.freehep.util.FreeHEPLookup;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.openide.util.Lookup;

/**
 *
 * @author  serbo
 */
public class CorbaTreeServantImpl extends TreeServantPOA implements AIDAListener { //, Runnable { 

    private ORB orb;
    private Tree tree;
    private TreeClient treeClient;
    private boolean duplex;
    private boolean keepRunning;
    private Vector sources;
    private CorbaServerEventQueue eventQueue;
    protected boolean useValidation;
    private Map converters;
    
    // Constructor for non-Duplex Mode
    public CorbaTreeServantImpl(ORB orb, Tree tree) {
        System.out.println("Starting TreeServant, DuplexMode = false");
        this.orb = orb;
        this.tree = tree;
        this.treeClient = null;
        duplex = false;
        init();
        eventQueue = new CorbaServerEventQueue();
    }

    // Constructor for Duplex Mode
    public CorbaTreeServantImpl(ORB orb, Tree tree, TreeClient treeClient) {
        System.out.println("Starting TreeServant, DuplexMode = "+ (treeClient != null) );
        this.orb = orb;
        this.tree = tree;
        this.treeClient = treeClient;
        if (treeClient == null) {
            throw new RuntimeException("Can not connect in Duplex mode with NULL Client Reference.");
        } 
        duplex = true;
        init();
        //new Thread(this).start();
        eventQueue = new CorbaServerEventQueue(treeClient);
    }

    // Service Methods
    
    protected void init() {
        sources = new Vector();
        converters = new Hashtable();
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
	    eventQueue.close();
            //if (duplex) synchronized ( eventQueue ) { eventQueue.notify(); }
	    for (int i=0; i<sources.size(); i++) {
		IsObservable o = (IsObservable) sources.get(i);
		    if (o != null) o.removeListener(this);
	    }
	    sources.clear();
	    sources = null;
            converters.clear();
            converters = null;
            tree = null;
	    treeClient = null;
	    eventQueue = null;
            System.out.print(" Done\n");
	}
    }

    
    // TreeServant Methods
    
    public void stateChanged(EventObject ev) {
        EventID eid = null;
        EventFlags eflags = EventFlags.OBJECT_MASK;
        String pathString = "";
        String nodeType = "null";
        
        if (ev instanceof TreeEvent) {   //TreeEvent
            TreeEvent tev = (TreeEvent) ev;
            if (tev.getType() != null)  nodeType = tev.getType().getName();
            if (tev.getFlags() == TreeEvent.FOLDER_MASK) {
                eflags = EventFlags.FOLDER_MASK;
                nodeType = "dir";
            }
            
            String[] path = tev.getPath();
            if (path != null) for (int i=0; i<path.length; i++) { pathString += "/" + path[i]; }
            
            if (tev.getID() == TreeEvent.NODE_ADDED) {
                eid = EventID.NODE_ADDED;
                if (!nodeType.equalsIgnoreCase("dir")) {
                    IManagedObject mo = tree.find(pathString);
                    nodeType = mo.type();
                    if (mo instanceof IsObservable) {
                        if (!sources.contains(mo)) {
                            ((IsObservable) mo).addListener(this);
                            sources.add(mo);
                        }
                        if (!useValidation) ((IsObservable) mo).setValid(this);
                    }
                }
                
            } else if (tev.getID() == TreeEvent.NODE_DELETED) {
                eid = EventID.NODE_DELETED;
                /*
                if (!nodeType.equalsIgnoreCase("dir")) {
                    IManagedObject mo = tree.find(pathString);
                    nodeType = mo.type();
                    if (mo instanceof IsObservable) {
                        if (!sources.contains(mo)) {
                            ((IsObservable) mo).removeListener(this);
                            sources.remove(mo);
                        }
                    }
                }
                 */
            }
            
            if (eid == null) {
                //System.out.println("CorbaTreeServantImpl.stateChanged wrong event ID="+tev.getID());
                return;
            }

	    if (!useValidation && tree instanceof IsObservable) ((IsObservable) tree).setValid(this);
            
        } else if (ev instanceof HistogramEvent) {
            IBaseHistogram hist = (IBaseHistogram) ev.getSource();

            pathString = tree.findPath((IManagedObject) hist);
            eid = EventID.NODE_UPDATED;
            eflags = EventFlags.OBJECT_MASK;
            nodeType = ((IManagedObject)hist).type();
            if (!useValidation && hist instanceof IsObservable) ((IsObservable) hist).setValid(this);
        
        } else if (ev instanceof DataPointSetEvent) {
            IDataPointSet mo = (IDataPointSet) ev.getSource();

            pathString = tree.findPath((IManagedObject) mo);
            eid = EventID.NODE_UPDATED;
            eflags = EventFlags.OBJECT_MASK;
            nodeType = ((IManagedObject)mo).type();            
            if (!useValidation && mo instanceof IsObservable) ((IsObservable) mo).setValid(this);
        } else {  // Unknown Event
            System.out.println("CorbaTreeServantImpl.stateChanged Unknown Event: "+ev);
            return;
        }


        System.out.println("CorbaTreeServantImpl: process Event: id = "+eid.value()+",  path = "+pathString+
                           ",  type = "+nodeType+",  flags = "+eflags.value());
        eventQueue.schedule(new EventStruct(eid, pathString, nodeType, eflags));
    }

    public String[] listObjectNames(String path) { 
        tree.setFolderIsWatched(path, true);
        return tree.listObjectNames(path); 
    }
    public String[] listObjectTypes(String path) {
        tree.setFolderIsWatched(path, true);
        return tree.listObjectTypes(path); 
    }
    public String findRetString(String path) {
        java.lang.Object mo = tree.find(path);
        String str = mo.toString();
        return str;
    }
    public void setValid(String path) {
       System.out.println("CorbaTreeServantImpl.setValid: path="+path); 
       if (path == null || path.equals("") || path.equals("/") ) {
            if (!useValidation) tree.setValid(this);
        } else {
            IManagedObject mo =  tree.find(path);
            System.out.println("CorbaTreeServantImpl.setValid: Find Object="+(mo==null ? "null" : mo.name()));
            if (!sources.contains(mo) && mo instanceof IsObservable) {
                ((IsObservable) mo).addListener(this);
                sources.add(mo);
            }
            if (mo instanceof IsObservable) {
                ((IsObservable) mo).setValid(this);
                System.out.println("CorbaTreeServantImpl.setValid: Just DID Actual Set Valid!");
            }
        }
    }
    public void setValid(String[] path) {
        if (path != null && path.length != 0) {
            for (int i=0; i<path.length; i++) {
                System.out.println("CorbaTreeServantImpl.setValid: path["+i+"] = "+path[i]); 
                setValid(path[i]);
            }
        }
    }

    public EventStruct[] updates() {
        EventStruct[] events = eventQueue.getEvents();
        return events;
    }

    //public Hist1DData find(String path) {
    public org.omg.CORBA.Any find(String path) {
        System.out.println("TreeServantImpl.find for  "+path);
        IManagedObject mo =  tree.find(path);
        System.out.println("\tTreeServantImpl.find MO  "+mo);

        if (mo instanceof IsObservable) {
            if (!sources.contains((IsObservable) mo)) {
                ((IsObservable) mo).addListener(this);
                sources.add((IsObservable) mo);
                //if (!useValidation) ((IsObservable) mo).setValid();
            }
        }

        String aidaType = "";
        aidaType = mo.type();
        
        // Find Convertor for this AIDA Type
        CorbaConverter converter = null;
        if (converters.containsKey(aidaType)) {
            converter = (CorbaConverter) converters.get(aidaType);
        } else {
            Lookup.Template template = new Lookup.Template(CorbaConverter.class, aidaType, null);
            Lookup.Item item = FreeHEPLookup.instance().lookupItem(template);
            if (item == null) throw new IllegalArgumentException("No Converter for AIDA Type: "+aidaType);

            converter = (CorbaConverter) item.getInstance();
            converters.put(aidaType, converter);
        }
        
        Any a = (Any) converter.extractData(mo);
        try {
            System.out.println("Check Type:");
            TypeCode type = a.type();
            System.out.println("\tGOT TypeCode:  name="+type.name()+
                               ", ID="+type.id()+", kind="+type.kind()+", kind.value()="+type.kind().value());
        } catch (Exception ex) { ex.printStackTrace(); }

        return a;
    }
    

    // Runnable methods
    
    /**
     * In Duplex mode sends updates to TreeClient
     */
    /*
    public void run() {
        while (duplex && keepRunning) {
            int size = 0;
            EventStruct[] events = null;
            try {
                synchronized (eventQueue) {
		    if(eventQueue.size() == 0) eventQueue.wait();
                    size = eventQueue.size();
                    if (size > 0) {
                        events = eventQueue.getEvents();
                    }
                }
                System.out.println("UpdatableQueue.run Processing: "+size);
                if (events == null || events.length == 0) return;
                treeClient.stateChanged(events);
            } catch (InterruptedException e2) {
                System.out.println("UpdatableQueue Thread InterruptedException.");
                e2.printStackTrace();                
	    } catch (Exception e3) {
                System.out.println("Problems in CorbaServerEventQueue!.");
                e3.printStackTrace();
            } // end of try/catch
        } // end of while
    } //end of run  
    */
}    

