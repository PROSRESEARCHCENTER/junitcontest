/*
 * BasicMutableStore.java
 *
 * Created on May 28, 2003, 4.39 PM
 */

package hep.aida.ref.remote.basic;

import hep.aida.IManagedObject;
import hep.aida.dev.IDevMutableStore;
import hep.aida.dev.IDevTree;
import hep.aida.ref.remote.basic.interfaces.UpdateEvent;

import java.io.IOException;
import java.util.Map;

/**
 * This is Basic implementation of Read-Only IMutableStore.
 * It has extra methods that allow to change state of the tree 
 * and to create IManagedObject in that tree and update its data.
 *
 * @author  serbo
 */
public abstract class BasicMutableStore implements AidaUpdatable, IDevMutableStore {
    
    protected IDevTree tree;
    protected UpdatableQueue queue;
    protected boolean hurry; // If true, do not wait for data update,
                             // just schedule data update and return.

    /** 
     * Creates a new instance of BasicMutableStore.
    */
    public BasicMutableStore() {
        this(null, false);
    }
    
    public BasicMutableStore(boolean hurry) {
        this(null, hurry);
    }
    
    public BasicMutableStore(IDevTree tree, boolean hurry) {
        this.tree = tree;
        this.hurry = hurry;
        queue = new UpdatableQueue();
    }
       
    
    // Service methods
 

    // IDevMutableStore methods
    
    public IManagedObject createObject(String name, String type) {
        //System.out.println("BasicMutableStore.createObject:  name="+name+",  type="+type);
        return BasicAdapter.create(name, type);
    }
    
    public abstract void updateData(String path, String type);
    

    // IMutableStore methods
    public void close() throws IOException {
        queue.close();
        tree = null;
    }
    
    public void commit(IDevTree tree, Map options) throws IOException {
        throw new UnsupportedOperationException("Can not commit changes to the Read-Only Store");
    }
    
    public boolean isReadOnly() {
        return true;
    }
    
    public abstract void read(IDevTree tree, String path) throws IllegalArgumentException, IOException;
    
    public abstract void read(IDevTree tree, Map options, boolean readOnly, boolean createNew) throws IOException;
    
    // AidaUpdatable methods
    
    /**
     * This method actually does the job of modifying the client tree.
     * If directory or node already does exist, it will not be overwritten.
     */
    public void stateChanged(UpdateEvent event) {
        int id = event.id();
        String path = event.path();    
        String type = event.nodeType();
        
        if (id == hep.aida.ref.remote.basic.interfaces.UpdateEvent.NODE_ADDED) {
            //System.out.println("Adding Node, path="+event.path()+",  type="+type);
            if (type.equalsIgnoreCase("dir")) {
       		try { // If directory already exists, IllegalArgumentException is thrown
                    tree.mkdirs(path);
		} catch (IllegalArgumentException ex) {}
            } else {
                int index = path.lastIndexOf("/");
		String name = path.substring(index+1);
		String objDir = path.substring(0, index+1);
                
                IManagedObject h = null;
		try { // If object already exists, do not overwrite it.
                    h = tree.find(path);
                } catch (IllegalArgumentException exFind) {}
                if (h != null) return;
                
		try { // Make sure all directories in the path exist.
                    tree.mkdirs(objDir);
                } catch (IllegalArgumentException exObj) {}
		//System.out.println("\tCreating ManagedObject with name: "+name+",  path: "+objDir+",  type="+type);
		h = createObject(name, type);
		tree.add(objDir, h);
                updateData(path, type);
		//servant.setValid(new String[] {path} );
            } 
            
        } else if (id == hep.aida.ref.remote.basic.interfaces.UpdateEvent.NODE_DELETED) {
            //System.out.println("Deleting Node, path="+event.path()+",  type="+type);
            if (type.equalsIgnoreCase("dir")) {
       		try {
		    String[] list =  tree.listObjectNames(path);
                    tree.rmdir(path);
		} catch (IllegalArgumentException ex) { }
            } else {
                try {
                    IManagedObject obj = tree.find(path);
                    tree.rm(path);
		} catch (IllegalArgumentException ex) { }
            }
            
        } else if (id == hep.aida.ref.remote.basic.interfaces.UpdateEvent.NODE_UPDATED) {
            //System.out.println("Updating Node, path="+event.path()+",  type="+type);
            updateData(path, type);
	} else {
            //System.out.println("Wrong ID="+event.id()+", path="+event.path()+",  type="+type);
        }

    }
    
    
    // Do some simple tests here
    public static void main(String[] args) {
    }
}
