/*
 * TreeUpdater.java
 *
 * Created on August 27, 2004, 11:15 AM
 */

package hep.aida.ref.remote;

import java.util.logging.*;
import hep.aida.*;
import hep.aida.ref.ManagedObject;

/**
 * This class can be used to force updates to RemoteManagedObjects in a Tree.
 * It scans the Tree recursively down from the "updatePath" and sets 
 * RemoteManagedObject.setDataValid(false) on any RemoteManagedObject that
 * is currently been watched
 *
 * @author  serbo
 */
public class RemoteTreeUpdater implements Runnable {
    
    private ITree tree;
    private String updatePath;
    private long updateInterval; // in milliseconds
    private boolean keepUpdating;
    private Thread thread;
    private Logger remoteLogger;
    
    /** Creates a new instance of AisTreeUpdater */
    public RemoteTreeUpdater(ITree tree) {
        this(tree, 30000);
    }
    
    public RemoteTreeUpdater(ITree tree, long updateInterval) {
        this(tree, updateInterval, "/");
    }
    
    public RemoteTreeUpdater(ITree tree, long updateInterval, String updatePath) {
        this.tree = tree;
        this.updateInterval = updateInterval;
        this.updatePath = updatePath;
        this.keepUpdating = false;
        this.remoteLogger = Logger.getLogger("hep.aida.ref.remote");
    }
    
    /** updateInterval is defined in milliseconds */
    public void setUpdateInterval(long updateInterval) { this.updateInterval = updateInterval; }
    public long getUpdateInterval() { return updateInterval; }
    
    /** updatePath is the top node in the Tree down from 
     *  where updates are performed recursively
     */
    public void setUpdatePath(String updatePath) { this.updatePath = updatePath; }
    public String getUpdatePath() { return updatePath; }
    
    public boolean isUpdating() { return keepUpdating; }
    
    public void startUpdating() {
        if (keepUpdating) {
            remoteLogger.fine("TreeUpdater.startUpdaiting:  already is updating, no action taken");
            return;
        }
        keepUpdating = true;
        thread = new Thread(this);
        thread.start();
    }
    
    public void stopUpdating(boolean urgent) {
        if (!keepUpdating || thread == null) {
            remoteLogger.fine("TreeUpdater.stopUpdaiting:  not updating, no action taken");
            return;            
        }
        if (!urgent) keepUpdating = false;
        else {
            try { 
                thread.stop(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public int updateTree(String pathString) {
        int nObjects = 0;
        //System.out.println("***** Updating: path="+pathString);
        String[] names = tree.listObjectNames(pathString, true);
        String[] types = tree.listObjectTypes(pathString, true);
        
        if (names == null) return 0;
        
        for (int i=0; i<names.length; i++) {
            //System.out.println("\t"+names[i]+" "+types[i]);
            if (!types[i].equalsIgnoreCase("dir")) {
                try {
                    IManagedObject obj = tree.find(names[i]);
                    remoteLogger.finer("\t\t Object: "+obj+",  dataValid="+((RemoteManagedObject) obj).isDataValid()+",  path="+names[i]);
                    if (obj instanceof RemoteManagedObject) {
                        RemoteManagedObject rmo = (RemoteManagedObject) obj;
                        if (rmo.isDataValid()) { 
                            rmo.setValidForAll();
                            rmo.setDataValid(false);
                            nObjects++;
                        }
                    } else if (obj instanceof ManagedObject) {
                        ManagedObject mo = (ManagedObject) obj;
                        // don't do anything here for now
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return nObjects;
    }
    /*
    private void executeUpdateTree(String pathString) {
        IManagedObject[] objects = null;
        objects = tree.findAlreadyCreatedObjects(pathString);
        
        if (objects == null || objects.length == 0) return;
        IManagedObject object = objects[objects.length-1];
        if (object == null) return;     
        if (object instanceof Folder) {
            Folder folder = (Folder) object;
            checkFolder(folder);
        } else if (object instanceof RemoteManagedObject) {
            RemoteManagedObject rmo = (RemoteManagedObject) object;
            if (rmo.isDataValid()) rmo.setDataValid(false);
        } else if (object instanceof ManagedObject) {
            ManagedObject mo = (ManagedObject) object;
	    // don't do anything here for now
        }        
    }
    
    
    void checkFolder(Folder folder) {
        if (!folder.isFilled()) return;
        int n = folder.getChildCount();
        for (int i=0; i<n; i++) {
            IManagedObject object = folder.getChild(i);
            if (object == null) continue;     
            if (object instanceof Folder) {
                Folder f = (Folder) object;
                checkFolder(f);
            } else if (object instanceof RemoteManagedObject) {
                RemoteManagedObject rmo = (RemoteManagedObject) object;
                if (rmo.isDataValid()) rmo.setDataValid(false);
            } else if (object instanceof ManagedObject) {
                ManagedObject mo = (ManagedObject) object;
                // don't do anything here for now
            }

        }
       
    }
    */
    
    // Runnable methods
    public void run() {
        //System.out.println("TreeUpdater.run: Starting Update Thread");
        int nObjects = 0;
        while (keepUpdating) {
            try {
                //System.out.println("\tTreeUpdater.run: Starting Tree Update");
                long t1 = System.currentTimeMillis();
                if (tree != null) { 
                    if (tree instanceof RemoteTree) nObjects = ((RemoteTree) tree).doUpdate(updatePath);
                    else nObjects = updateTree(updatePath);
                }
                long t2 = System.currentTimeMillis();
                long time = t2 - t1;
                remoteLogger.finer("\tRemoteTreeUpdater.run: Finished Tree Update. Updated: "+nObjects+" objects, Time="+time+" milliseconds");
                Thread.sleep(updateInterval);
            } catch (InterruptedException ie) {
                //System.out.println("TreeUpdater Update Thread InterruptedException.");
                ie.printStackTrace();
                
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        //System.out.println("TreeUpdater.run: Exiting Update Thread");
    }
    
}
