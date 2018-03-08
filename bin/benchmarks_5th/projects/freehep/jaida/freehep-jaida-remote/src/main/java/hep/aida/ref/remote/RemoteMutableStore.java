/*
 * BasicMutableStore.java
 *
 * Created on May 28, 2003, 4.39 PM
 */

package hep.aida.ref.remote;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;

import hep.aida.IManagedObject;
import hep.aida.IBaseHistogram;
import hep.aida.IDataPointSet;
import hep.aida.dev.IDevTree;
import hep.aida.dev.IDevMutableStore;

import hep.aida.ref.ManagedObject;
import hep.aida.ref.Annotation;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.remote.interfaces.AidaUpdatable;
import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;
import hep.aida.ref.tree.Tree;

/*
import java.awt.Component;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.WebBrowser;
import org.freehep.jas.plugin.web.*;
*/

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

/**
 * This is Basic implementation of Read-Only IDevMutableStore.
 * It has extra methods that allow to change state of the tree
 * and to create IManagedObject in that tree and update its data.
 * This implementation creates appropriate subclass of RemoteClient
 * to connect to a remote server.
 *
 * All subclasses need to implement 3 methods:
 *
 *   protected RemoteClient createClient(Map options);
 *   public IManagedObject createObject(String name, String type);
 *   public void updateData(String path, String type);
 *
 * @author  serbo
 */
public abstract class RemoteMutableStore implements AidaUpdatable, IDevMutableStore {
    
    protected IDevTree tree;
    protected RemoteClient client;
    protected boolean initDone;
    protected boolean recursive;
    protected RemoteUpdateEvent[] events;
    protected Logger remoteLogger;
    protected boolean acceptEvents = true;
    protected boolean hurry; // If true, do not wait for data update,
    // just schedule data update and return.
    
    /**
     * Creates a new instance of BasicMutableStore.
     */
    public RemoteMutableStore() {
        this(false);
    }
    
    public RemoteMutableStore(boolean hurry) {
        this(null, null, hurry);
    }
    
    public RemoteMutableStore(IDevTree tree) {
        this(tree, null, false);
    }
    
    public RemoteMutableStore(IDevTree tree, boolean hurry) {
        this(tree, null, hurry);
    }
    
    private RemoteMutableStore(IDevTree tree, RemoteClient client, boolean hurry) {
        this.tree = tree;
        this.client = client;
        this.hurry = hurry;
        this.remoteLogger = Logger.getLogger("hep.aida.ref.remote");
        init();
    }
    
    
    // Service methods and classes
    
    public class TreeEntry {
        public String[] entryNames = null;
        public String[] entryTypes = null;
    }
    
    TreeEntry getEntries(IDevTree t, String path, boolean rec, boolean check) throws IOException {
        TreeEntry entries = new TreeEntry();
        boolean wrong = true;
        String[] names = t.listObjectNames(path, rec);
        String[] types = t.listObjectTypes(path, rec);
        while (check && wrong) {
            int l1 = names.length;
            int l2 = types.length;
            remoteLogger.fine("RemoteMutableStore.getEntries Tree l1="+l1+", l2="+l2+", recursive="+rec+", path="+path);;
            wrong = (l1 != l2);
            if (wrong) {
                try {
                    Thread.sleep(200);
                    names = t.listObjectNames(path, rec);
                    types = t.listObjectTypes(path, rec);
                } catch (Exception ex) {
                    remoteLogger.log(Level.INFO, "RemoteMutableStore.getEntries: \n\t"+ex.getMessage());
                    remoteLogger.log(Level.FINE, "", ex);
                }
            } else {
                entries.entryNames = names;
                entries.entryTypes = types;
            }
        }
        return entries;
    }
    
    TreeEntry getEntries(RemoteClient t, String path, boolean rec, boolean check) throws IOException {
        TreeEntry entries = new TreeEntry();
        boolean wrong = true;
        String[] names = t.listObjectNames(path, rec);
        String[] types = t.listObjectTypes(path, rec);
        while (check && wrong) {
            int l1 = names.length;
            int l2 = types.length;
            remoteLogger.fine("RemoteMutableStore.getEntries Client l1="+l1+", l2="+l2+", recursive="+rec+", path="+path);;
            wrong = (l1 != l2);
            if (wrong) {
                try {
                    Thread.sleep(400);
                    names = t.listObjectNames(path, rec);
                    types = t.listObjectTypes(path, rec);
                } catch (Exception ex) {
                    remoteLogger.log(Level.INFO, "RemoteMutableStore.getEntries: \n\t"+ex.getMessage());
                    remoteLogger.log(Level.FINE, "", ex);                    
                }
            } else {
                entries.entryNames = names;
                entries.entryTypes = types;
            }
        }
        return entries;
    }
    
    void setRecursive(boolean b) { 
        this.recursive = b; 
    }
    
    public boolean isRecursive() { return recursive; }
    
    public void init() {
        this.recursive = true;
        this.events = new RemoteUpdateEvent[1];
        this.initDone = false;
        this.acceptEvents = true;
    }
    
    public void setHurry(boolean hurry) {
        this.hurry = hurry;
        remoteLogger.fine("RemoteMutableStore.setHurry  to "+hurry);
    }
    
    // Can schedule data update for later or just directly execute it now
    public void handleDataUpdate(IManagedObject mo, String path, String type) throws IllegalArgumentException {
        handleDataUpdate(mo, path, type, hurry);
    }
    public void handleDataUpdate(IManagedObject mo, String path, String type, boolean hurryUp) throws IllegalArgumentException {
        if (hurryUp) {
            // Schedule data update here and pretend that object is up-to-date
            RemoteUpdateEvent evt = new RemoteUpdateEvent(AidaUpdateEvent.DO_DATA_UPDATE_NOW, path, type);
            RemoteUpdateEvent[] eventArray = new RemoteUpdateEvent[1];
            eventArray[0] = evt;
            if (client != null) {
                client.stateChanged(eventArray);
                if (mo instanceof RemoteManagedObject) {
                    ((RemoteManagedObject) mo).setDataValid(true);
                }
            } else {
                updateData(path, type);
            }
        } else {
            updateData(path, type);
        }
    }
    
    
    // This method must be overwritten by subclass
    protected abstract RemoteClient createClient(Map options);
    
    
    // IDevMutableStore methods
    
    public abstract void updateData(String path, String type) throws IllegalArgumentException;
    
    public abstract IManagedObject createObject(String name, String aidaType) throws IllegalArgumentException;
    
    
    // IMutableStore methods
    public void close() throws IOException {
        acceptEvents = false;
        if (tree instanceof Tree) ((Tree) tree).fireConnectionEvent("/", false);
        if (client != null) client.disconnect();
        client = null;
        tree = null;
        events = null;
    }
    
    public void commit(IDevTree tree, Map options) throws IOException {
        throw new UnsupportedOperationException("Can not commit changes to the Read-Only Store");
    }
    
    public boolean isReadOnly() {
        return true;
    }
        
    // This method is only called for Tree (not RemoteTree)
    public void reloadFolder(String path)  throws IllegalArgumentException, IOException {
        reloadFolder(path, isRecursive());
    }
    public void reloadFolder(String path, boolean localRec)  throws IllegalArgumentException, IOException {
        remoteLogger.fine("RemoteMutableStore.reloadFolder: path="+path+", recursive="+localRec);
        if (!client.isConnected())
            throw new IOException("Can not read from the Store: client is not connected!");

        long t0 = System.currentTimeMillis();
        
        TreeEntry entries = getEntries(client, path, localRec, true);
        String[] names = entries.entryNames;
        String[] types = entries.entryTypes;
        
        long t1 = System.currentTimeMillis();
        
        IDevTree localTree = (IDevTree) tree;        
        TreeEntry localEntries = getEntries(localTree, path, localRec, true);
        String[] oldNames = localEntries.entryNames;
        String[] oldTypes = localEntries.entryTypes;
        
        long t2 = System.currentTimeMillis();

        if (names != null && types != null) {
            
            /*
            for (int i=0; i<names.length; i++) {
                System.out.println("reloadFolder: \t"+i+"  name: "+names[i]+"\t type: "+types[i]);
            }
             */
            
            // add new nodes as needed
            for (int i=0; i<names.length; i++) {
                int id = AidaUpdateEvent.DO_ADD_NODE_NOW;
                String pathString = names[i];
                String typeString = types[i];
                String type = typeString;
                String xType = "double";
                
                // Parse out possible X Axis type
                int indexT = typeString.lastIndexOf(":");
                if (indexT > 0) {
                    type  = typeString.substring(0, indexT);
                    String tmpType = typeString.substring(indexT+1);
                    if (tmpType != null && !tmpType.equals("")) xType = tmpType;
                }
                
                try {
                    int index = AidaUtils.findInArray(names[i], oldNames);
                    if (index < 0) {
                        // no local node with such name, need to add
                        id = AidaUpdateEvent.DO_ADD_NODE_NOW;
                    } else {
                        if (type.equals(oldTypes[index]) || typeString.equals(oldTypes[index])) {
                            oldNames[index] = null;
                            oldTypes[index] = null;
                            id = AidaUpdateEvent.NODE_UPDATED;
                            if (type.equalsIgnoreCase("dir") || type.equalsIgnoreCase("mnt")) continue;
                        } else {
                            // delete node with wrong type first
                            oldNames[index] = null;
                            oldTypes[index] = null;

                            id = AidaUpdateEvent.NODE_DELETED;
                            executeStateChanged(id, pathString, type, xType);
                            
                            id = AidaUpdateEvent.DO_ADD_NODE_NOW;
                        }
                    }
                    
                    executeStateChanged(id, pathString, type, xType);
                    
                    // If "recursive", mark all sub-directories as "filled"
                    if (localRec) {
                        if (type.equalsIgnoreCase("dir") || type.equalsIgnoreCase("mnt")) tree.hasBeenFilled(pathString);
                    }
                } catch (Exception e) {
                    remoteLogger.log(Level.FINE, "Exception in RemoteMutableStore.reloadFolder: "+ path+"\n\t"+ e.getMessage());
                    remoteLogger.log(Level.FINEST, "Exception in RemoteMutableStore.reloadFolder: "+ path, e);
                }
            }
            
            // now delete all old nodes that are not used anymore
            String xType = "double";
            int id = AidaUpdateEvent.NODE_DELETED;
            for (int i=0; i<oldNames.length; i++) {
                if (oldNames[i] == null) continue;
                
                try {
                    executeStateChanged(id, oldNames[i], oldTypes[i], xType);                    
                } catch (Exception e) {
                    remoteLogger.log(Level.FINE, "Exception in RemoteMutableStore.reloadFolder: "+ path+"\n\t"+ e.getMessage());
                    remoteLogger.log(Level.FINEST, "Exception in RemoteMutableStore.reloadFolder: "+ path, e);
                }
            }
        }
        long t3 = System.currentTimeMillis();
        long dt1 = (t1-t0);
        long dt2 = (t2-t1);
        long dt3 = (t3-t0);
        remoteLogger.fine("RemoteMutableStore.reloadFolder: Client time: "+dt1+", Tree time: "+dt2+", Total: "+dt3+", path="+path+", recursive="+localRec);
    }

    public void read(IDevTree tree, String path) throws IllegalArgumentException, IOException {
        if (this.tree == null) this.tree = tree;
        if (!client.isConnected())
            throw new IOException("Can not read from the Store: client is not connected!");
        
        remoteLogger.fine("RemoteMutableStore.read: path="+path+", recursive="+isRecursive());
        String[] names = null;
        String[] types = null;
        TreeEntry entries = getEntries(client, path, isRecursive(), true);
        names = entries.entryNames;
        types = entries.entryTypes;
        
        
        /*
        for (int i=0; i<names.length; i++) {
            System.out.println("read: \t"+i+"  name: "+names[i]+"\t type: "+types[i]);
        }
        
        for (int i=0; i<types.length; i++) {
            System.out.println("\t"+i+"  type: "+types[i]);
        }
        System.out.println("\n");
         */
        
        //initDone = false;
        if (names != null && types != null) {
           String pathString = "";
           for (int i=0; i<names.length; i++) {
                try {
                    int id = AidaUpdateEvent.DO_ADD_NODE_NOW;
                    pathString = names[i];
                    String typeString = types[i];
                    String type = typeString;
                    String xType = "double";
                    
                    // Parse out possible X Axis type
                    int indexT = typeString.lastIndexOf(":");
                    if (indexT > 0) {
                        type  = typeString.substring(0, indexT);
                        String tmpType = typeString.substring(indexT+1);
                        if (tmpType != null && !tmpType.equals("")) xType = tmpType;
                    }
                    executeStateChanged(id, pathString, type, xType);
                    
                    // If "recursive", mark all sub-directories as "filled"
                    if (isRecursive()) {
                        if (type.equalsIgnoreCase("dir") || type.equalsIgnoreCase("mnt")) tree.hasBeenFilled(pathString);
                    }
                } catch (Exception e) {
                    remoteLogger.log(Level.FINE, "Exception in RemoteMutableStore.read: "+ pathString+"\n\t"+ e.getMessage());
                    remoteLogger.log(Level.FINEST, "Exception in RemoteMutableStore.read: "+ pathString, e);
                }
            }
            tree.hasBeenFilled(path);            
        }
        //initDone = true;
    }
    
    public void read(IDevTree tree, Map options, boolean readOnly, boolean createNew) throws IOException {
        if (this.tree == null) this.tree = tree;
        //if (tree != null && tree.getLock() == null) tree.setLock(new Object());
        if (client == null) {
            client = createClient(options);
        }
        if (!client.isConnected()) {
            client.connect();
        }
        boolean rec = options.containsKey("recursive");
	setRecursive(rec);

        initDone = false;
        remoteLogger.fine("RemoteMutableStore.read: initial read for the top directory, recursive="+isRecursive());
        read(tree, "/");
        initDone = true;
    }

    
    // AidaUpdatable methods
    
    /**
     * This method actually does the job of modifying the client tree.
     * If directory or node already does exist, it will not be overwritten.
     * Synchronizes with Tree lock, if the Tree has lock object set.
     */
    public void stateChanged(AidaUpdateEvent event) {
        if (!acceptEvents) return;
        
        Object lock = tree.getLock();
        remoteLogger.finest("RemoteMutableStore.stateChanged for EVENT:: id="+event.id()+", path="+event.path()+",  type="+event.nodeType());
        if (lock != null) {
            synchronized (lock) {
                executeStateChanged(event);
            }
        } else {
            executeStateChanged(event);
        }
    }
    
    protected void executeStateChanged(AidaUpdateEvent event) {
        int id = event.id();
        String path = event.path();
        String typeString = event.nodeType();
        String type = typeString;
        String xType = "double";
        
        // Parce out possible X Axis type
        int indexT = typeString.lastIndexOf(":");
        if (indexT > 0) {
            type  = typeString.substring(0, indexT);
            String tmpType = typeString.substring(indexT+1);
            if (tmpType != null && !tmpType.equals("")) xType = tmpType;
        } else if (event instanceof RemoteUpdateEvent) {
            String tmp = ((RemoteUpdateEvent) event).getXAxisType();
            if (tmp != null && !tmp.equals("")) xType = tmp;
        }
        executeStateChanged(id, path, type, xType);
        remoteLogger.finest("RemoteMutableStore.executeStateChanged for EVENT:: id="+id+", path="+path+",  type="+type+", xAxisType="+xType);
        if(tree instanceof RemoteTree && id != AidaUpdateEvent.NODE_UPDATED) ((RemoteTree) tree).submitEventToListeners(event);
    }
    protected void executeStateChanged(int id, String path, String type, String xType) {
        Object lock = tree.getLock();
        remoteLogger.finest("RemoteMutableStore.executeStateChanged:: id="+id+", path="+path+",  type="+type+", xAxisType="+xType+",  acceptEvents="+acceptEvents+",  lock="+lock);
        if (!acceptEvents) return;
        if (id == AidaUpdateEvent.NODE_ADDED || id == AidaUpdateEvent.DO_ADD_NODE_NOW) {
            try {
                if (type.equalsIgnoreCase("dir") || type.equalsIgnoreCase("mnt")) {
                    if(tree instanceof RemoteTree) { 
                        ((RemoteTree) tree).addFolder(path);
                    } else { 
                        tree.mkdirs(path);
                    }
                    try {
                        if (!(id == AidaUpdateEvent.DO_ADD_NODE_NOW) && isRecursive()) read(tree, path);
                    } catch (Exception e) {
                        remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: Exception while adding node: "+type+", "+path+", Skip this.\n\t", e.getMessage());                     
                        remoteLogger.log(Level.FINEST, "RemoteMutableStore.executeStateChanged: Exception while adding node: "+type+", "+path+", Skip this.", e.getStackTrace());                     
                    }
                } else {
                    // Parse object name and folder path
                    if (path.endsWith("/")) path = path.substring(0, path.length()-1);
                    if (path.endsWith("/")) path = path.substring(0, path.length()-1);
                    String name = AidaUtils.parseName(path);
                    String objDir = AidaUtils.parseDirName(path);
                    
                    IManagedObject h = null;
                    
                    // Make sure all directories in the path exist.
                    if(tree instanceof RemoteTree) ((RemoteTree) tree).addFolder(objDir);
                    else tree.mkdirs(objDir);
                    
                    if (type.equals("RemoteUnavailableObject")) h = new RemoteUnavailableObject(name);
                    else h = createObject(name, type);
                    
                    // Set X Axis Type
                    if (h instanceof RemoteManagedObject) {
                        RemoteManagedObject r = (RemoteManagedObject) h;
                        r.setFillable(true);
                        if ( h instanceof IBaseHistogram) {
                            Annotation a = (Annotation) ((IBaseHistogram) h).annotation();
                            a.setFillable(true);
                            try {
                                a.setValue("xAxisType", xType);
                            } catch (IllegalArgumentException e) {
                                a.addItem("xAxisType", xType);
                            }
                            a.setFillable(false);
                        } else if ( h instanceof IDataPointSet) {
                            Annotation a = (Annotation) ((IDataPointSet) h).annotation();
                            a.setFillable(true);
                            try {
                                a.setValue("xAxisType", xType);
                            } catch (IllegalArgumentException e) {
                                a.addItem("xAxisType", xType);
                            }
                            a.setFillable(false);
                        }
                        r.setFillable(false);
                    }
                    
                    if(tree instanceof RemoteTree) ((RemoteTree) tree).addObject(objDir, h);
                    else tree.add(objDir, h);
                    
                    if (h instanceof RemoteManagedObject) {
                        ((RemoteManagedObject) h).setTreeFolder(objDir);
                        if (h instanceof RemoteUnavailableObject) ((RemoteManagedObject) h).setDataValid(true);
                        else ((RemoteManagedObject) h).setDataValid(false);
                    } else {
                        handleDataUpdate(h, path, type);
                    }
                    
                }
            } catch (IllegalArgumentException ex) {
                remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: IllegalArgumentException while ADDING node: "+type+", "+path+", Skip this: \n\t"+ex.getMessage());
                remoteLogger.log(Level.FINE, "", ex);
            }
            
        } else if (id == AidaUpdateEvent.FOLDER_IS_FILLED) {
            if (type.equalsIgnoreCase("dir"))
                tree.hasBeenFilled(path);
        } else if (id == AidaUpdateEvent.NODE_DELETED) {
            try {
                if (type.equalsIgnoreCase("dir")) {
                    if(tree instanceof RemoteTree) ((RemoteTree) tree).removeFolder(path);
                    else tree.rmdir(path);                    
                } else {
                    if(tree instanceof RemoteTree) ((RemoteTree) tree).removeObject(path);
                    else tree.rm(path);
                }
            } catch (IllegalArgumentException ex) {
                remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: IllegalArgumentException while DELETING node: "+type+", "+path+", Skip this.\n\t", ex.getMessage());
                remoteLogger.log(Level.FINE, "", ex);
            }
            
        } else if (id == AidaUpdateEvent.NODE_UPDATED) {
            // Here we just mark RemoteManagedObject as not valid, do not actually get new data.
            try {
                IManagedObject h = null;
                if(tree instanceof RemoteTree) h = ((RemoteTree) tree).executeFind(path);
                else h = tree.find(path);
                 if (h instanceof RemoteManagedObject) {
                    ((RemoteManagedObject) h).setDataValid(false);
                } else {
                    handleDataUpdate(h, path, type);
                }
                
            } catch (IllegalArgumentException ex) {
                remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: IllegalArgumentException while UPDATING node: "+type+", "+path+", Skip this.\n\t", ex.getMessage());
                remoteLogger.log(Level.FINE, "", ex);
            }
        } else if (id == AidaUpdateEvent.DO_DATA_UPDATE_NOW) {
            updateData(path, type);
        } else if (id == AidaUpdateEvent.TREE_CLOSED) {
            remoteLogger.info("***** Got TREE_CLOSED event from Remote AIDA Server.");
            remoteLogger.info("***** Connection to the Remote AIDA Server is Lost");
            acceptEvents = false;
            try {
                close();
            } catch (Exception ex) {
                remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: Exception while closing tree", ex);
                remoteLogger.log(Level.FINE, "", ex);
            }
        } else if (id == AidaUpdateEvent.NODE_TEMPORARY_UNAVAILABLE) {
            remoteLogger.finest("RemoteMutableStore.executeStateChanged:: id="+id+", path="+path+",  type="+type+", xAxisType="+xType+",  lock="+lock+", Tree="+tree.storeName());
            // Remove existing object first
            try {
                
                if (tree instanceof RemoteTree) {
                    if (type.equalsIgnoreCase("dir")) {
                        ((RemoteTree) tree).removeFolder(path);
                    } else {
                        //IManagedObject obj = tree.find(path);
                        ((RemoteTree) tree).removeObject(path);
                    }
                } else if (tree instanceof Tree) {
                    ((Tree) tree).fireConnectionEvent(path, false);
                }
                
            } catch (IllegalArgumentException ex) {
                remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: IllegalArgumentException while NODE_TEMPORARY_UNAVAILABLE operation: "+type+", "+path+", Skip this.\n\t", ex.getMessage());
                remoteLogger.log(Level.FINE, "", ex);
            }
            
            // Put in RemoteUnavailableObject
            if (tree instanceof RemoteTree) {
                String name = AidaUtils.parseName(path);
                String objDir = AidaUtils.parseDirName(path);
                
                RemoteUnavailableObject h = new RemoteUnavailableObject(name);
                ((RemoteTree) tree).addObject(objDir, h);
                
                h.setTreeFolder(objDir);
                h.setDataValid(true);
                reloadWebPage();
            }
            
        } else if (id == AidaUpdateEvent.NODE_IS_AVAILABLE_AGAIN) {
            remoteLogger.finest("RemoteMutableStore.executeStateChanged:: id="+id+", path="+path+",  type="+type+", xAxisType="+xType+",  lock="+lock+", Tree="+tree.storeName());
            // Remove existing RemoteUnavailableObject first
            try {
                IManagedObject obj = null;
                if (tree instanceof RemoteTree) {
                    obj = ((RemoteTree) tree).executeFind(path);
                    if (obj != null) {
                        ((RemoteTree) tree).removeObject(path);
                    }                   
                } else if (tree instanceof Tree) {
                    obj = ((hep.aida.ref.tree.Tree) tree).findObject(path);
                    if (obj.type().equalsIgnoreCase("RemoteUnavailableObject")) {
                        tree.rm(path);
                    }
                }
                
            } catch (IllegalArgumentException ex) {
                remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: IllegalArgumentException while NODE_IS_AVAILABLE_AGAIN operation: "+type+", "+path+",  Skip this.\n\t", ex.getMessage());
                remoteLogger.log(Level.FINE, "", ex);
            }
            
            try {
                if (tree instanceof RemoteTree) {
                    if (type.equalsIgnoreCase("dir")) {
                        ((RemoteTree) tree).addFolder(path);
                        if (isRecursive()) try {
                            read(tree, path);
                        } catch (Exception ioe) {
                            remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: Exception while updating PATH: "+type+", "+path, ioe);
                            remoteLogger.log(Level.FINE, "", ioe.getStackTrace());
                        }
                    } else {
                        RemoteUpdateEvent evt = new RemoteUpdateEvent(AidaUpdateEvent.NODE_ADDED, path, type);
                        RemoteUpdateEvent[] eventArray = new RemoteUpdateEvent[1];
                        eventArray[0] = evt;
                        if (client != null) {
                            client.stateChanged(eventArray);
                        }
                    }
                }  else if (tree instanceof Tree) {
                    if (type.equalsIgnoreCase("dir")) {
                        tree.mkdirs(path);
                        reloadFolder(path);
                        ((Tree) tree).fireConnectionEvent(path, true);
                    } else {
                        RemoteUpdateEvent evt = new RemoteUpdateEvent(AidaUpdateEvent.NODE_ADDED, path, type);
                        RemoteUpdateEvent[] eventArray = new RemoteUpdateEvent[1];
                        eventArray[0] = evt;
                        if (client != null) {
                            client.stateChanged(eventArray);
                        }
                        ((Tree) tree).fireConnectionEvent(path, true);
                    }
                }
            } catch (Exception e) {
                remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: IllegalArgumentException while NODE_IS_AVAILABLE_AGAIN operation: "+type+", "+path+", Skip this.\n\t", e.getMessage());
                remoteLogger.log(Level.FINE, "", e);
            }
        } else if (id == AidaUpdateEvent.REMOTE_CONNECTION_EXCEPTION) {
            remoteLogger.info("***** Got REMOTE_CONNECTION_EXCEPTION event from Remote AIDA Server.");
            remoteLogger.info("***** Connection to the Remote AIDA Server is Lost");
            acceptEvents = false;
            try {
                close();
            } catch (Exception exc) { exc.printStackTrace(); }
        } else {
            remoteLogger.log(Level.INFO, "RemoteMutableStore.executeStateChanged: Wrong ID="+id+", path="+path+",  type="+type);
 }        
    }
    
    protected void reloadWebPage() {
        /*
        Studio app = (Studio) org.freehep.application.Application.getApplication();    
        if (app != null) {
            SimpleWebBrowser webBrowser = (SimpleWebBrowser) app.getLookup().lookup(WebBrowser.class);
        }
         */
   }
    
    // Thread that does reading updates
    // Currently no used, as updates go through the client.stateChanged()
    public class ReadThread extends Thread {
        private String readPath;
        private long wait;
        
        public ReadThread(String readPath) {
            this(readPath, 0);
        }
        public ReadThread(String readPath, long wait) {
            this.readPath = readPath;            
            this.wait = wait;            
        }
        
        public void run() {
             try {
                if (wait > 0) Thread.sleep(wait);
                read(tree, readPath);
            } catch (InterruptedException e2) {
                remoteLogger.log(Level.INFO, "RemoteMutableStore InterruptedException: "+ e2);
                remoteLogger.log(Level.FINE, "RemoteMutableStore InterruptedException: ", e2);
	    } catch (Exception e3) {
                remoteLogger.log(Level.INFO, "Exception in RemoteMutableStore: "+ e3);
                remoteLogger.log(Level.FINE, "Exception in RemoteMutableStore: ", e3);
            }            
        }
    }
    
}
