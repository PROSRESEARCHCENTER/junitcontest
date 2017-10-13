/*
 * RemoteManagedObject.java
 *
 * Created on June 9, 2003, 5:17 PM
 */

package hep.aida.ref.remote;

import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.ManagedObject;
import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.IsObservable;

import java.util.logging.Logger;

/**
 * Base class for all IManagedObjects in local AIDA Tree
 * that are copies of remote AIDA Tree objects. If dataIsValid
 * state changes, event is fired to notify listeners.
 * @author  serbo
 */
public abstract class RemoteManagedObject extends ManagedObject implements IsObservable {
    
    public static int    DEFAULT_INT = 0;
    public static long   DEFAULT_LONG = 0L;
    public static float  DEFAULT_FLOAT = 0.0F;
    public static double DEFAULT_DOUBLE = 0.0;
    public static double rmsFactor = Math.sqrt(12);
    
    protected IDevMutableStore store = null;
    protected String treePath = null;
    protected String aidaType = "IManagedObject";
    protected boolean dataIsValid = false;
    protected boolean stateDidChange = false;
    protected Logger remoteLogger = Logger.getLogger("hep.aida.ref.remote");
    //protected long timeOfLastUpdate; // in milliseconds
    //protected String xAxisType;
    //protected String yAxisType;
    protected boolean isLocked;

    /** Creates a new instance of RemoteManagedObject */
    public RemoteManagedObject(String name) {
        super(name);
        fillable = false;
        //xAxisType = null;
        //yAxisType = null;
    }
    
    static String getCurrentTime() {
        long millis = System.currentTimeMillis();
        long ml = millis %1000;
        java.util.Date date = new java.util.Date(millis);
        String tmp = java.text.DateFormat.getDateTimeInstance().format(date);
        tmp = tmp + " ["+ml+"] :: ";
        return tmp;
    }
    
    public void makeSureDataIsValid() {

//        System.out.println("**************************************** MAKE SURE DATA IS VALID!!!!! ");

        if (dataIsValid || fillable || !isConnected || isLocked ) return;
        
        //remoteLogger.finest("RemoteManagedObject.makeSureDataIsValid :: data="+dataIsValid+", isValid="+isValid+", stateDidChange="+stateDidChange+", path="+treePath);
        if (store instanceof RemoteMutableStore)
            ((RemoteMutableStore) store).handleDataUpdate(this, treePath, aidaType);
        else if (store != null)
            store.updateData(treePath, aidaType);
        }

    public boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public String type() {
        if (aidaType != null && !aidaType.trim().equals("")) return aidaType;
        else return super.type();
    }
    
    public void setStore(IDevMutableStore store) {
        this.store = store;
    }
    
    public IDevMutableStore getStore() {
        return store;
    }
    
    //public void setXAxisType(String type) { xAxisType = type; }
    //public String getXAxisType() { return xAxisType; }
    
    //public void setYAxisType(String type) { yAxisType = type; }
    //public String getYAxisType() { return yAxisType; }
    
    /** Set what folder this histogram belongs to. */
    public void setTreeFolder(String treeFolder) {
        String histName = (name() == null) ? "Unknown" : name();
        if (histName.startsWith("/")) {
            histName = histName.substring(1);
            histName = AidaUtils.modifyName(histName);
            histName = "/" + histName;
        } else {
            histName = AidaUtils.modifyName(histName);
        }
        
        //System.out.println("RemoteManagedObject:: name="+histName+",  path="+treeFolder);
        
        if (treeFolder.endsWith("//")) treeFolder = treeFolder.substring(0, treeFolder.length()-2);
        
        if (!treeFolder.startsWith("/")) treeFolder = "/"+treeFolder;
        else if (treeFolder.startsWith("//")) treeFolder = treeFolder.substring(1);
        
        if (treeFolder.equals("/") && histName.startsWith("/")) this.treePath = histName;
        else if (treeFolder.endsWith("/") && histName.startsWith("/")) this.treePath = treeFolder+histName.substring(1);
        else if (treeFolder.endsWith("/") || histName.startsWith("/")) this.treePath = treeFolder+histName;
        else this.treePath = treeFolder+"/"+histName;
        
        //System.out.println("\tpath="+treePath);
    }
    
    public String getTreePath() { return treePath; }
    
    public void setConnected(boolean connected) {
        super.setConnected(connected);
        setDataValid(false);
    }
    
    public void setDataValid(boolean dataIsValid) {
        boolean fireEvent = false;
        synchronized (this) {
            remoteLogger.finest("RemoteManagedObject.setDataValid :: DATA: new="+dataIsValid+", old="+this.dataIsValid+", isValid="+isValid+", stateDidChange="+stateDidChange+", path="+treePath);
            if (this.dataIsValid != dataIsValid) {
                this.dataIsValid = dataIsValid;
                if (isValid) {
                    fireEvent = true;
                    stateDidChange = false;
                } else {
                    stateDidChange = true;
                }
            }
        }
        if (fireEvent) fireStateChanged();
    }
    
    public boolean isDataValid() {
        return dataIsValid;
    }
    
    public void setValid(AIDAListener l) {
        boolean fireEvent = false;
        synchronized (this) {
            remoteLogger.finest("RemoteManagedObject.setValid :: dataIsValid="+dataIsValid+", oldIsValid="+isValid+", stateDidChange="+stateDidChange+", for path="+getTreePath()+",  AIDAListener: "+l);
            if (stateDidChange) {
                fireEvent = true;
                stateDidChange = false;
            }
        }
        super.setValid(l);
        if (fireEvent) fireStateChanged();
    }
    
    public void setValidForAll() {
        boolean fireEvent = false;
        synchronized (this) {
            remoteLogger.finest("RemoteManagedObject.setValidForAll :: dataIsValid="+dataIsValid+", oldIsValid="+isValid+", stateDidChange="+stateDidChange+", for path="+getTreePath());
            if (stateDidChange) {
                fireEvent = true;
                stateDidChange = false;
            }
        }
        super.setValidForAll();
        if (fireEvent) fireStateChanged();
    }
    
}
