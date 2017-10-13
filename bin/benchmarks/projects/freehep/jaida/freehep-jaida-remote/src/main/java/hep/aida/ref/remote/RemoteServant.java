/*
 * RemoteServant.java
 *
 * Created on October 22, 2003, 9:20 PM
 */

package hep.aida.ref.remote;

import hep.aida.IAnnotation;
import hep.aida.IBaseHistogram;
import hep.aida.IDataPointSet;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.dev.IDevTree;
import hep.aida.ref.Annotation;
import hep.aida.ref.ManagedObject;
import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.DataPointSetEvent;
import hep.aida.ref.event.HistogramEvent;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.event.TreeEvent;
import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaTreeServant;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;
import hep.aida.ref.tree.Tree;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is implementation of the AidaTreeServant. It mainly deals with the
 * Tree and Tree Objects - change in Tree structure, updates of Tree Objects'
 * Data, etc.
 * This class does not have any remote transport layer (RMI, CORBA, etc.),
 * so special adapter classes, like RmiRemoteServant, are used to provide
 * such functionality.
 * The default for useValidation = true;
 *
 * @author  serbo
 */

public class RemoteServant implements AidaTreeServant, AIDAListener  {
    
    private IDevTree tree;
    private AidaTreeClient client;
    private String clientID;
    private boolean duplex;
    protected boolean blocking = false;
    private boolean appendAxisType;
    private boolean keepRunning;
    private Vector sources;
    private Hashtable hash;
    private RemoteServerQueue eventQueue;
    protected boolean useValidation;
    protected Logger remoteLogger;
    
    /** Creates a new instance of RemoteServant */
    public RemoteServant(IDevTree tree, String clientID) {
        this.tree = tree;
        this.clientID = clientID;
        this.duplex = false;
        this.appendAxisType = false;
        eventQueue = new RemoteServerQueue();
        remoteLogger = Logger.getLogger("hep.aida.ref.remote");
        init();
    }
    
    public RemoteServant(IDevTree tree, AidaTreeClient client) {
        this.tree = tree;
        this.client = client;
        this.duplex = true;
        this.appendAxisType = false;
        eventQueue = new RemoteServerQueue(client);
        remoteLogger = Logger.getLogger("hep.aida.ref.remote");
        init();
    }
    
    
    // Service methods
    
    // If "true", append ":date" or ":double" to the object type
    public void setAppendAxisType(boolean a) { appendAxisType = a; }
    public boolean getAppendAxisType() { return appendAxisType; }
    
    public void setBlocking(boolean b) { 
        blocking = b; 
        eventQueue.setBlocking(b);
    }
    public boolean isBlocking() { return blocking; }
    
    protected void init() {
        sources = new Vector();
        hash = new Hashtable();
        useValidation = true;
        keepRunning = duplex;
        Object lock = tree.getLock();
        if (lock != null) {
            //synchronized (lock) {
            if (tree instanceof IsObservable) {
                ((IsObservable) tree).addListener(this);
                sources.add(tree);
                ((IsObservable) tree).setValid(this);
            }
            if (tree instanceof Tree)
                ((Tree) tree).setFolderIsWatched("/", true);
            //}
        } else {
            if (tree instanceof IsObservable) {
                ((IsObservable) tree).addListener(this);
                sources.add(tree);
                ((IsObservable) tree).setValid(this);
            }
            if (tree instanceof Tree)
                ((Tree) tree).setFolderIsWatched("/", true);
        }
    }
    
    
    /**
     * If useValidation = true, client has to call "setValid" method after
     * receiving update from the ManagedObject in order to reseive next update.
     * If useValidation = false, client receives all updates.
     */
    public synchronized void setUseValidation(boolean state) { useValidation = state; }
    
    
    /**
     * Close all connections and release all allocated resources.
     */
    public void close() {
        synchronized ( this ) {
            remoteLogger.fine("\n\tClosing RemoteServant ... ");
            keepRunning = false;
            if (eventQueue != null) eventQueue.close();
            Object lock = null;
            if (tree != null) tree.getLock();
            if (lock != null) {
                //synchronized (lock) {
                if (tree instanceof IsObservable) {
                    try {
                        ((IsObservable) tree).removeListener(this);
                    } catch (Exception e) { e.printStackTrace(); }
                }
                if (sources != null)
                    for (int i=0; i<sources.size(); i++) {
                        try {
                            IsObservable o = (IsObservable) sources.get(i);
                            if (o != null) o.removeListener(this);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                //}
            } else {
                if (tree instanceof IsObservable) {
                    try {
                        ((IsObservable) tree).removeListener(this);
                    } catch (Exception e) { e.printStackTrace(); }
                }
                if (sources != null)
                    for (int i=0; i<sources.size(); i++) {
                        try {
                            IsObservable o = (IsObservable) sources.get(i);
                            if (o != null) o.removeListener(this);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
            }
        }
        if (sources != null) sources.clear();
        if (hash != null) hash.clear();
        sources = null;
        hash = null;
        tree = null;
        client = null;
        eventQueue = null;
        remoteLogger.fine("  ... RemoteServant is closed\n");
    }
    
    
    public void setValid(String path) {
        remoteLogger.finest("RemoteServant.setValid for path="+path);
        if (path == null || path.equals("") || path.equals("/") ) {
            if (tree instanceof IsObservable) ((IsObservable) tree).setValid(this);
        } else {
            IManagedObject mo =  tree.find(path);
            if (!sources.contains(mo) && mo instanceof IsObservable) {
                ((IsObservable) mo).addListener(this);
                sources.add(mo);
            }
            if (mo instanceof IsObservable) {
                ((IsObservable) mo).setValid(this);
            }
        }
    }
    
    // AidaTreeServant methods
    
    /**
     * Just return an IManagedObject itself. Later the transport layer class
     * (like RmiRemoteServant) will have to extract the data from the IManagedObject
     * and send the data over the network.
     */
    public java.lang.Object find(String path) {
        remoteLogger.finest("RemoteServant.find for path="+path);
        IManagedObject mo =  tree.find(path);
        
        if (mo instanceof IsObservable) {
            if (!sources.contains((IsObservable) mo)) {
                ((IsObservable) mo).addListener(this);
                sources.add((IsObservable) mo);
                hash.put(path, mo);
                if (!useValidation) ((IsObservable) mo).setValid(this);
            }
        }
        remoteLogger.finest("RemoteServant.find for path="+path+",  found MO="+mo.toString());
        return mo;
    }
    
    public String[] listObjectNames(String path, boolean recursive) {
        remoteLogger.finest("RemoteServant.listObjectNames for path="+path);
        if (tree instanceof Tree) ((Tree) tree).setFolderIsWatched(path, true);
        String[] list =  tree.listObjectNames(path, recursive);
        /*
        if (list == null || list.length == 0) return list;
        if (tree instanceof Tree) {
            Tree tTree = (Tree) tree;
            for (int i=0; i<list.length; i++) {
                String objPath = list[i];
                if (!useValidation) {
                    IManagedObject mo =  tTree.findObject(objPath);
                    if (mo instanceof IsObservable) {
                        ((IsObservable) mo).setValid(this);
                    }
                }
            }
        }
         */
        return list;
    }
    
    public String[] listObjectTypes(String path, boolean recursive) {
        remoteLogger.finest("RemoteServant.listObjectTypes for path="+path+",  appendAxisType="+appendAxisType);
        if (tree instanceof Tree) ((Tree) tree).setFolderIsWatched(path, true);
        String[] names =  tree.listObjectNames(path, recursive);
        String[] list  =  tree.listObjectTypes(path, recursive);
        if (list == null || list.length == 0) return list;
        
        if (tree instanceof Tree) {
            Tree tTree = (Tree) tree;
            for (int i=0; i<names.length; i++) {
                String objPath = names[i];
                IManagedObject mo =  tTree.findObject(objPath);
                if (appendAxisType && !list[i].equalsIgnoreCase("dir")) {
                    String xType = "double";
                    String tmp = null;
                    if (mo instanceof ManagedObject) {
                        synchronized (mo) {
                            boolean isFillable = false;
                            boolean isAnnotationFillable = false;
                            Annotation an = null;
                            isFillable = ((ManagedObject) mo).isFillable();
                            if (!isFillable) ((ManagedObject) mo).setFillable(true);
                            if (mo instanceof IBaseHistogram) an = (Annotation) ((IBaseHistogram) mo).annotation();
                            else if (mo instanceof IDataPointSet) an = (Annotation) ((IDataPointSet) mo).annotation();
                            if (an != null) {
                                isAnnotationFillable = an.isFillable();
                                if (!isAnnotationFillable) an.setFillable(true);
                                try {
                                    tmp = an.value("xAxisType");
                                } catch (IllegalArgumentException e) {}                            
                                an.setFillable(isAnnotationFillable);
                            }
                            ((ManagedObject) mo).setFillable(isFillable);
                        }
                    } else if (mo != null) {
                        IAnnotation an = null;
                        if (mo instanceof IBaseHistogram) an = (IAnnotation) ((IBaseHistogram) mo).annotation();
                        else if (mo instanceof IDataPointSet) an = (IAnnotation) ((IDataPointSet) mo).annotation();
                        try {
                            tmp = an.value("xAxisType");
                        } catch (Exception e) {}
                    }
                    if (tmp != null && !tmp.trim().equals("")) {
                        xType = tmp;
                        list[i] = list[i]+":"+xType;
                    }
                }
                //if (!useValidation && mo instanceof IsObservable) {
                //    ((IsObservable) mo).setValid(this);
                //}
                
            }
        }
        return list;
    }
    
    public void setValid(String[] path) {
        if (path != null && path.length != 0) {
            for (int i=0; i<path.length; i++) {
                //System.out.println("RemoteServant.setValid: path["+i+"] = "+path[i]);
                setValid(path[i]);
            }
        }
    }
    
    public AidaUpdateEvent[] updates() {
        remoteLogger.finest("RemoteServant.updates");
        AidaUpdateEvent[] events = new AidaUpdateEvent[0];
        
        if (eventQueue != null) events = eventQueue.getEvents();
        remoteLogger.finest("RemoteServant.updates gotEvents="+events.length);
        return events;
    }
    
    
    // AIDAListener methods
    
    /**
     * Mainly this method translates Tree, DataPointSet, Histogram, etc.
     * event into AidaUpdateEvent. Also add event to the queue.
     */
    public void stateChanged(java.util.EventObject ev) {
        remoteLogger.finest("RemoteServant: got   Event: "+ev.toString());
        
        AidaUpdateEvent event = null;
        int id = -1;
        String pathString = "";
        String nodeType = "null";
        String xAxisType = "double";
        IManagedObject mo = null;
        
        if (ev instanceof AidaUpdateEvent) {
            AidaUpdateEvent aev = (AidaUpdateEvent) ev;
            id = aev.id();
            pathString = aev.path();
            String nodeTypeString = aev.nodeType();
            nodeType = nodeTypeString;
            xAxisType = "double";
            
            // Parce out possible X Axis type
            int indexT = nodeTypeString.lastIndexOf(":");
            if (indexT > 0) {
                nodeType  = nodeTypeString.substring(0, indexT);
                String tmpType = nodeTypeString.substring(indexT+1);
                if (tmpType != null && !tmpType.equals("")) xAxisType = tmpType;
            } else if (event instanceof RemoteUpdateEvent) {
                String tmp = ((RemoteUpdateEvent) event).getXAxisType();
                if (tmp != null) xAxisType = tmp;
            }
            
        } else if (ev instanceof TreeEvent) {   //TreeEvent
            TreeEvent tev = (TreeEvent) ev;
            if (tev.getType() != null)  nodeType = tev.getType().getName();
            if (tev.getFlags() == TreeEvent.FOLDER_MASK) {
                nodeType = "dir";
            }
            
            String[] path = tev.getPath();
            if (path != null) for (int i=0; i<path.length; i++) { pathString += "/" + path[i]; }
            
            if (tev.getID() == TreeEvent.NODE_ADDED) {
                id = AidaUpdateEvent.NODE_ADDED;
                try {
                    if (!nodeType.equalsIgnoreCase("dir")) {
                        IManagedObject tmpMo = ((ITree) ev.getSource()).find(pathString);
                        nodeType = tmpMo.type();
                    }
                    
                } catch (Exception ex) {
                    remoteLogger.log(Level.INFO, "RemoteServant.stateChanged: Exception while setting type:"+nodeType+ " \n\t"+ex.getMessage());
                    remoteLogger.log(Level.FINEST, "", ex);                    
                }
            } else if (tev.getID() == TreeEvent.NODE_DELETED) {
                id = AidaUpdateEvent.NODE_DELETED;
            } else if (tev.getID() == TreeEvent.TREE_CLOSED) {
                id = AidaUpdateEvent.TREE_CLOSED;
                nodeType = "dir";
            }
        } else if (ev instanceof HistogramEvent) {
            mo  = (IManagedObject) ev.getSource();            
        } else if (ev instanceof DataPointSetEvent) {
            mo = (IManagedObject) ev.getSource();            
        } else {  // Unknown Event
            remoteLogger.fine("RemoteServant.stateChanged Unknown Event: "+ev);
            return;
        }
        
        
        if (mo != null) {
            pathString = tree.findPath((IManagedObject) mo);
            id = AidaUpdateEvent.NODE_UPDATED;
            nodeType = ((IManagedObject)mo).type();
            //if (!useValidation && mo instanceof IsObservable) ((IsObservable) mo).setValid(this);
        }
        
        if (!nodeType.equalsIgnoreCase("dir") && pathString.endsWith("/")) pathString = pathString.substring(0, pathString.length()-1);
        if (nodeType.equalsIgnoreCase("dir") && !pathString.endsWith("/")) pathString += "/";
        
        if (id == AidaUpdateEvent.NODE_ADDED) {
            if (nodeType.equalsIgnoreCase("dir")) {
                // nothing to do here
            } else if (mo != null) {
                String tmp = null;
                if (mo instanceof ManagedObject) {
                    synchronized (mo) {
                        boolean isFillable = false;
                        boolean isAnnotationFillable = false;
                        Annotation an = null;
                        isFillable = ((ManagedObject) mo).isFillable();
                        if (!isFillable) ((ManagedObject) mo).setFillable(true);
                        if (mo instanceof IBaseHistogram) an = (Annotation) ((IBaseHistogram) mo).annotation();
                        else if (mo instanceof IDataPointSet) an = (Annotation) ((IDataPointSet) mo).annotation();
                        if (an != null) {
                            isAnnotationFillable = an.isFillable();
                            if (!isAnnotationFillable) an.setFillable(true);
                            try {
                                tmp = an.value("xAxisType");
                            } catch (IllegalArgumentException e) {}                            
                            an.setFillable(isAnnotationFillable);
                        }
                        ((ManagedObject) mo).setFillable(isFillable);
                    }
                } else if (mo != null) {
                    IAnnotation an = null;
                    if (mo instanceof IBaseHistogram) an = (IAnnotation) ((IBaseHistogram) mo).annotation();
                    else if (mo instanceof IDataPointSet) an = (IAnnotation) ((IDataPointSet) mo).annotation();

                    try {
                        tmp = an.value("xAxisType");
                    } catch (Exception e) {}
                }
                if (tmp != null && !tmp.trim().equals("")) xAxisType = tmp;
            }
        } else if (id == AidaUpdateEvent.NODE_DELETED) {
            
            if (nodeType.equalsIgnoreCase("dir")) {
                Enumeration en = hash.keys();
                while (en.hasMoreElements()) {
                    String tmpPath = (String) en.nextElement();
                    if (tmpPath.startsWith(pathString)) {
                        IsObservable o = (IsObservable) hash.remove(pathString);
                        if (mo != null) {
                            ((IsObservable) o).removeListener(this);
                            sources.remove(o);
                        }
                    }
                }
            } else {
                IsObservable o = (IsObservable) hash.remove(pathString);
                if (mo != null) {
                    ((IsObservable) o).removeListener(this);
                    sources.remove(o);
                }
            }
        }
        
        if (id < 0) {
            remoteLogger.fine("RemoteServant.stateChanged wrong event ID="+id);
            return;
        }
        
        if (!useValidation && tree instanceof IsObservable) ((IsObservable) tree).setValid(this);
        
        
        
        remoteLogger.finest("RemoteServant: process Event: id = "+id+",  path = "+pathString+
        ",  type = "+nodeType+",  xAxisType="+xAxisType);
        event = new RemoteUpdateEvent(id, pathString, nodeType, xAxisType);
        eventQueue.schedule(event);
    }
    
}
