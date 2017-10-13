package hep.aida.ref.tree;

import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IManagedObject;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ITuple;
import hep.aida.dev.IDevManagedObject;
import hep.aida.dev.IDevTree;
import hep.aida.dev.IOnDemandStore;
import hep.aida.dev.IStore;
import hep.aida.dev.IStoreFactory;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.event.AIDAObservable;
import hep.aida.ref.event.Connectable;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.event.TreeEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

/**
 *
 * @author The AIDA team @ SLAC.
 *
 * @version $Id: Tree.java 13360 2007-10-02 23:13:06Z serbo $
 */
public class Tree extends AIDAObservable implements IDevTree, IsObservable {
    private Object lock = null; 
    private Folder    root;
    private Path      currentPath;
    private String    separatorChar = "/";
    private IStore    aidaStore;
    private String    storeName;
    private boolean   readOnly;
    private boolean   readOnlyUserDefined;
    private boolean   createNew;
    private String    storeType = null;
    private Map optionsMap;
    private Hashtable manObjHash = new Hashtable();
    private Hashtable pathHash   = new Hashtable();
    private ArrayList mountList  = new ArrayList();
    private int mountCount = 0;   
    private boolean isClosed = false;
    private boolean overwrite = true;
    private String name = null;
    
    private IAnalysisFactory analysisFactory;

    /**
     * Create a new Tree.
     *
     */
    protected Tree(IAnalysisFactory analysisFactory) {
        root = new Folder("/");
        currentPath = new Path();
        setIsValidAfterNotify(true);
	aidaStore = null;
        this.analysisFactory = analysisFactory;
    }
    
    protected Tree(IAnalysisFactory analysisFactory, String name, String storeName, String storeType, int mode, String options) throws IOException {
        root = new Folder("/");
        currentPath = new Path();
        setIsValidAfterNotify(true);
	aidaStore = null;
        this.analysisFactory = analysisFactory;
        this.name = name;
        init( storeName, storeType, mode, options);
    }

    public String name() {
        return name;
    }

    /**
     * Get the name of the store.
     * @return The store's name.
     *
     */
    public String storeName() {
        return storeName;
    }
    
    public String storeType() {
        return storeType;
    }
    
    /*
     * Ability to lock (synchronize) Tree methods maybe needed
     * when using Tree in a multi-thread program. User have to
     * set the lock Object before useing it. Default lock=null
     */
    public void setLock(Object lock) { this.lock = lock; }
    
    /*
     * Ability to lock (synchronize) Tree methods maybe needed
     * when using Tree in a multi-thread program. User can 
     * obtain lock object from the Tree and synchronize on it
     * Default lock=null
     */
    public Object getLock() { return lock; }
    
    
    // Needed to work with IStores from mount points.
    IStore getStore() {
        return aidaStore;
    }
    
    public boolean hasStore() {
        return aidaStore == null ? false : true;
    }
    
    public IManagedObject findObject(String path) throws IllegalArgumentException {
        Path p = new Path(currentPath, path);
        if ( p.toString().equals("/") )
            return root;
        Folder folder = checkAndFillFolderFromStore(p.parent());
        if ( folder == null ) 
            throw new IllegalArgumentException("Can not find Folder: "+p.parent());
	IManagedObject obj = folder.getChild(p.getName());
        return obj;
    }
    /**
     * Get the IManagedObject at a given path in the ITree. The path can either be
     * absolute or relative to the current working directory.
     * @param path The path.
     * @return     The corresponding IManagedObject.
     * @throws     IllegalArgumentException If the path does not correspond to an IManagedObject.
     *
     */
    public IManagedObject find(String path) throws IllegalArgumentException {
        IManagedObject obj = findObject(path);
        if ( obj == null || obj instanceof Folder ) 
            throw new IllegalArgumentException("The path "+path+" does not correspond to an IManagedObject");
        if ( obj instanceof Link )
            obj = find( ((Link)obj).path().toString() );
        Path p = new Path(currentPath, path);
        registerManagedObject(obj,p);
        return (IManagedObject)obj;
    }
    
    public ITree findTree(String path) throws IllegalArgumentException {
        IManagedObject mp = findObject(path);
        if ( ! ( mp instanceof MountPoint ) ) 
            throw new IllegalArgumentException("The given path "+path+" does not correspond to a mount point.");
        return (ITree)( (MountPoint) mp ).getTree();
    } 
    
    /**
     * Closes the underlying store.
     * Changes will be saved only if commit() has been called before.
     * The call is propagated to the dependent mounted trees.
     * @throws IOException If there are problems writing out
     *         the underlying store.
     *
     */
    public void close() throws IOException {
        if (aidaStore != null)
        {
           aidaStore.close();
           aidaStore = null;
        }
        //The reverse logic here is to protect this from being accessed
        //by two different threads.
        boolean wasClosed = isClosed;
        isClosed = true;
        if ( ! wasClosed ) fireStateChanged(new TreeEvent(this, TreeEvent.TREE_CLOSED, new String[] {"/"}, null, TreeEvent.FOLDER_MASK));
    }
//    public void finalize()
//    {
//       // This is looking to be a bad idea, the tree is often discarded while objects within the tree are still being
//       // used, so we clearly need a better way of doing this. Probably better to leave it up to the stores to close
//       // themselves when they are not being used.
////       try
////       {
////         close();
////       }
////       catch (IOException x) {};
//    }
    /**
     * Change to a given directory.
     * @param path The absolute or relative path of the directory we are changing to.
     * @throws     IllegalArgumentException If the path does not exist or path is not a directory.
     *
     */
    public void cd(String path) throws IllegalArgumentException {
        Path newPath = new Path(currentPath,path);
//        IManagedObject obj = checkAndFillFolder(newPath);
        IManagedObject obj = findObject( newPath.toString() );
        
        if ( obj == null ) throw new IllegalArgumentException("Wrong path "+path);
        if ( obj instanceof Link ) {
            newPath = ((Link)obj).path();
            obj = findObject( ((Link)obj).path().toString() );
        }
        if (!(obj instanceof Folder)) throw new IllegalArgumentException("Path: "+path+" is not a folder");
          
        Folder folder = (Folder) obj;
       currentPath = newPath;
       if (isValid) fireStateChanged(new TreeEvent(this, TreeEvent.CHANGE_DIRECTORY, currentPath.toArray(), null, TreeEvent.FOLDER_MASK));
    }
    
    /**
     * Get the path of the current working directory.
     * @return The path of the current working directory.
     *
     */
    public String pwd() {
        return currentPath.toString();
    }
    
    /**
     * List, into a given output stream, all the IManagedObjects, including directories
     * (but not "." and ".."), in a given path. Directories end with "/". The list can be recursive.
     *                  in all the directories under path (the default is <code>false</code>.
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public void ls() throws IllegalArgumentException {
        ls(".",false,null);
    }
    
    /**
     * List, into a given output stream, all the IManagedObjects, including directories
     * (but not "." and ".."), in a given path. Directories end with "/". The list can be recursive.
     * @param path      The path where the list has to be performed (by default the current directory ".").
     *                  in all the directories under path (the default is <code>false</code>.
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public void ls(String path) throws IllegalArgumentException {
       ls(path,false,null);
    }
    
    /**
     * List, into a given output stream, all the IManagedObjects, including directories
     * (but not "." and ".."), in a given path. Directories end with "/". The list can be recursive.
     * @param path      The path where the list has to be performed (by default the current directory ".").
     * @param recursive If <code>true</code> the list is extended recursively
     *                  in all the directories under path (the default is <code>false</code>.
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public void ls(String path, boolean recursive) throws IllegalArgumentException {
       ls(path,recursive,null);
    }
    
    /**
     * List, into a given output stream, all the IManagedObjects, including directories
     * (but not "." and ".."), in a given path. Directories end with "/". The list can be recursive.
     * @param path      The path where the list has to be performed (by default the current directory ".").
     * @param recursive If <code>true</code> the list is extended recursively
     *                  in all the directories under path (the default is <code>false</code>.
     * @param os        The output stream into which the list is dumped (by default the standard output).
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public void ls(String path, boolean recursive, OutputStream os) throws IllegalArgumentException {
      if (os == null) os = System.out;
      Path p = new Path(currentPath, path);
      //System.out.println("LS :: path="+path+",  PATH="+p.toString()+", recursive="+recursive);
      IManagedObject obj = checkAndFillFolder(p);
      if ( obj == null ) throw new IllegalArgumentException("Wrong path "+path);
      if ( obj instanceof Folder) {
	  if (! path.endsWith(separatorChar) ) path += separatorChar;
      }
        
      PrintWriter pw = new PrintWriter(os,true);
      if ( obj instanceof Folder ) {
         int childNumb = ((Folder)obj).getChildCount();
         for ( int i = 0; i<childNumb; i++ ) {
             IManagedObject child = ((Folder)obj).getChild(i);
             if ( child instanceof Folder ) {
                 pw.println(path+AidaUtils.modifyName(AidaUtils.modifyName(child.name()))+separatorChar);
                 if ( recursive ) ls( path+AidaUtils.modifyName(child.name()), recursive, os );
             } else if ( child instanceof Link ) {
                 pw.println(path+AidaUtils.modifyName(child.name())+" -> "+((Link) child).path().toString());
             }
             else pw.println(path+AidaUtils.modifyName(child.name()));
         }
      } else if ( obj instanceof Link ) {
          pw.println(path+" -> "+((Link)obj).path().toString());
      } else {
         pw.println(path);
      }
    }
    
    /**
     * Get the list of names of the IManagedObjects under a given path, including directories
     * (but not "." and ".."). Directories end with "/".
     * The returned names are appended to the given path unless the latter is ".".
     *                  in all the directories under path (the default is <code>false</code>.
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public String[] listObjectNames() throws IllegalArgumentException {
        return listObjectNames(currentPath.getName());
    }
    
    /**
     * Get the list of names of the IManagedObjects under a given path, including directories
     * (but not "." and ".."). Directories end with "/".
     * The returned names are appended to the given path unless the latter is ".".
     * @param path      The path where the list has to be performed (by default the current directory ".").
     *                  in all the directories under path (the default is <code>false</code>.
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public String[] listObjectNames(String path) throws IllegalArgumentException {
        return listObjectNames(path,false);
    }
    
    /**
     * Get the list of names of the IManagedObjects under a given path, including directories
     * (but not "." and ".."). Directories end with "/".
     * The returned names are appended to the given path unless the latter is ".".
     * @param path      The path where the list has to be performed (by default the current directory ".").
     * @param recursive If <code>true</code> the list is extended recursively
     *                  in all the directories under path (the default is <code>false</code>.
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public String[] listObjectNames(String path, boolean recursive) throws IllegalArgumentException {
       Path p = new Path(currentPath,path);
       IManagedObject obj = checkAndFillFolder(p);
        if ( obj == null ) throw new IllegalArgumentException("Wrong path "+path);
        if ( obj instanceof Folder) {
	    if (! path.endsWith(separatorChar) ) path += separatorChar;
	}
        Vector names = new Vector();
        if ( obj instanceof Folder ) {
            int childNumb = ((Folder)obj).getChildCount();
            for ( int i = 0; i<childNumb; i++ ) {
                IManagedObject child = ((Folder)obj).getChild(i);
                if ( child instanceof Folder ) {
                    names.add( path+AidaUtils.modifyName(child.name())+separatorChar );
                    if ( recursive ) {
                        String[] childNames = listObjectNames( path+AidaUtils.modifyName(child.name()), recursive );
                        for ( int j = 0; j < childNames.length; j++ )
                            names.add( childNames[j] );
                    }
                }
                else
                    names.add( path+AidaUtils.modifyName(child.name()) );
            }
        } else
            names.add(path);
        
        String[] objNames = new String[names.size()];
        for ( int i = 0; i < names.size(); i++ )
            objNames[i] = (String) names.get(i);
        return objNames;
    }
    
    /**
     * Get the list of types of the IManagedObjects under a given path.
     * The types are the leaf class of the Interface, e.g. "IHistogram1D", "ITuple", etc.
     * Directories are marked with "dir".
     * The order of the types is the same as the order for the listObjectNames() method
     * to achieve a one-to-one correspondance between object names and types.
     *                  in all the directories under path (the default is <code>false</code>.
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public String[] listObjectTypes() throws IllegalArgumentException {
        return listObjectTypes(currentPath.getName());
    }
    
    /**
     * Get the list of types of the IManagedObjects under a given path.
     * The types are the leaf class of the Interface, e.g. "IHistogram1D", "ITuple", etc.
     * Directories are marked with "dir".
     * The order of the types is the same as the order for the listObjectNames() method
     * to achieve a one-to-one correspondance between object names and types.
     * @param path      The path where the list has to be performed (by default the current directory ".").
     *                  in all the directories under path (the default is <code>false</code>.
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public String[] listObjectTypes(String path) throws IllegalArgumentException {
        return listObjectTypes(path,false);
    }
    
    /**
     * Get the list of types of the IManagedObjects under a given path.
     * The types are the leaf class of the Interface, e.g. "IHistogram1D", "ITuple", etc.
     * Directories are marked with "dir".
     * The order of the types is the same as the order for the listObjectNames() method
     * to achieve a one-to-one correspondance between object names and types.
     * @param path      The path where the list has to be performed (by default the current directory ".").
     * @param recursive If <code>true</code> the list is extended recursively
     *                  in all the directories under path (the default is <code>false</code>.
     * @throws          IllegalArgumentException If the path does not exist.
     *
     */
    public String[] listObjectTypes(String path, boolean recursive) throws IllegalArgumentException {
        if ( ! path.endsWith(separatorChar) ) path += separatorChar;
        Path p = new Path(currentPath,path);
	IManagedObject obj = checkAndFillFolder(p);
        if ( obj == null ) throw new IllegalArgumentException("Wrong path "+path);
        
        Vector types = new Vector();
        if ( obj instanceof Folder ) {

            int childNumb = ((Folder)obj).getChildCount();
            for ( int i = 0; i<childNumb; i++ ) {
                IManagedObject child = ((Folder)obj).getChild(i);
                types.add( child.type() );
                if ( child instanceof Folder ) {
                    if ( recursive ) {
                        String[] childTypes = listObjectTypes( path+AidaUtils.modifyName(child.name()), recursive );
                        for ( int j = 0; j < childTypes.length; j++ )
                            types.add( childTypes[j] );
                    }
                }
            }
        } else {
            types.add( obj.type() );
        }
        
        String[] objTypes = new String[types.size()];
        for ( int i = 0; i < types.size(); i++ )
            objTypes[i] = (String) types.get(i);
        return objTypes;
    }

    /**
     * Create a new directory. Given a path only the last directory
     * in it is created if all the intermediate subdirectories already exist.
     * @param path The absolute or relative path of the new directory.
     * @throws     IllegalArgumentException If a subdirectory within the path does
     *             not exist or it is not a directory. Also if the directory already
     *              exists.
     *
     */
    public void mkdir(String path) throws IllegalArgumentException {
       Path p = new Path(currentPath,path);
       Path parentPath = p.parent();
       if ( path.endsWith( separatorChar ) ) path = path.substring( 0, path.length()-1 );
       IManagedObject[] mo = findAlreadyCreatedObjects(p);
       if (mo[mo.length-1] != null)
	   throw new TreeObjectAlreadyExistException("mkdir: Directory already exists: "+path);
       //System.out.println("mkdirs: path="+path+", length="+mo.length);
       IManagedObject obj = mo[mo.length-2];   
       if (obj == null) throw new IllegalArgumentException("Cannot create directory, no parent "+path);
       if (obj instanceof Folder)
       {
           // Find if there is MountPoint in the paths and if there is,
           // delegate execution of this function there
           int imp = indexOfTopMountPoint(mo);
           //System.out.println("Tree mkdir: path="+p.toString()+", imp="+imp);
           if (imp > -1) {
                MountPoint mp = (MountPoint) mo[imp];
                String newPath = p.toString(imp, p.size());
                //System.out.println("\t\t\t  newPath="+newPath);
                mp.getTree().mkdir(newPath);
                return;
           }

          Folder parent = (Folder) obj;
          if (parent.getChild(p.getName()) != null) throw new IllegalArgumentException(p.getName()+" already exists");
          Folder child = new Folder(p.getName());
          parent.add(child);
          if (isValid && parent.isBeingWatched()) fireStateChanged(new TreeEvent(this, TreeEvent.NODE_ADDED, p.toArray(), child.getClass(), TreeEvent.FOLDER_MASK));
       }
       else throw new IllegalArgumentException("Cannot create directory "+path);
    }
    
    /**
     * Create a directory recursively. Given a path the last directory
     * and all the intermediate non-existing subdirectories are created.
     * @param path The absolute or relative path of the new directory.
     * @throws     IllegalArgumentException If an intermediate subdirectory
     *             is not a directory.
     *
     */
    public void mkdirs(String path) throws IllegalArgumentException {
       //System.out.println("mkdirs: "+path);
       Path p = new Path(currentPath,path);
       IManagedObject[] mo = findAlreadyCreatedObjects(p);

       // Find if there is MountPoint in the paths and if there is,
       // delegate execution of this function there
       int imp = indexOfTopMountPoint(mo);
       //System.out.println("Tree mkdirs: path="+p.toString()+", imp="+imp);
       if (imp > -1) {
            MountPoint mp = (MountPoint) mo[imp];
            String newPath = p.toString(imp, p.size());
            //System.out.println("\t\t\t  newPath="+newPath);
            mp.getTree().mkdirs(newPath);
            return;
       }
       
       //System.out.println("mkdirs: path="+path+", length="+mo.length);
       //if ( mo[mo.length-1] != null && !(mo[mo.length-1] instanceof Folder)) 
       //    throw new IllegalArgumentException(": "+path);
       IManagedObject child = mo[mo.length-1];
       if (child == null) 
       {
           Folder folder = null;
           for (int i=0; i<mo.length-1; i++) {
               if (!(mo[i] instanceof Folder))
                  throw new IllegalArgumentException("Path: "+path+" contains not Folder, i="+i+", name="+mo[i].name()+", mo="+mo[i]);
               folder = (Folder) mo[i];
               child = mo[i+1];
               if (child == null) {
                   child = new Folder((p.toString(i,i)).substring(1));
                   folder.add(child);
                   mo[i+1] = child;
                   if (isValid && folder.isBeingWatched()) 
                       fireStateChanged(new TreeEvent(this, TreeEvent.NODE_ADDED, p.toArray(), child.getClass(), TreeEvent.FOLDER_MASK));
                   //System.out.println("Add Folder: "+i+"  "+AidaUtils.modifyName(child.name()));
               }
           }
       }
       else if (!(child instanceof Folder)) throw new IllegalArgumentException(path+" is not a folder");
    }
    
    /**
     * Remove a directory and all the contents underneath.
     * @param path The absolute or relative path of the directory to be removed.
     * @throws     IllegalArgumentException If path does not exist or if it is not
     *             a directory.
     *
     */
    public void rmdir(String path) throws IllegalArgumentException {
       Path p = new Path(currentPath, path);
       IManagedObject[] mo = findAlreadyCreatedObjects(p);
       IManagedObject target = mo[mo.length-1];
       if (target == null) throw new IllegalArgumentException("Directory does not exist: "+path);
       if (!(target instanceof Folder)) throw new IllegalArgumentException(path+" is not a folder");
       if (target == root) throw new IllegalArgumentException("Cannot delete root");
       Folder folder = (Folder) mo[mo.length-2];

       // Find if there is MountPoint in the paths and if there is,
       // delegate execution of this function there
       int imp = indexOfTopMountPoint(mo);
       //System.out.println("Tree rmdirs: path="+p.toString()+", imp="+imp);
       if (imp > -1) {
            MountPoint mp = (MountPoint) mo[imp];
            String newPath = p.toString(imp, p.size());
            //System.out.println("\t\t\t  newPath="+newPath);
            mp.getTree().rmdir(newPath);
            return;
       }
       
       folder.remove(target);
       if (isValid && folder.isBeingWatched()) fireStateChanged(new TreeEvent(this, TreeEvent.NODE_DELETED, p.toArray(), null, TreeEvent.FOLDER_MASK));
    }
    
    /**
     * Remove an IManagedObject by specifying its path.
     * If the path points to a mount point, the mount point should first commit, then
     * close and delete the tree object.
     * @param path The absolute or relative path of the IManagedObject to be removed.
     * @throws     IllegalArgumentException If path does not exist.
     *
     */
    public void rm(String path) throws IllegalArgumentException {
       Path p = new Path(currentPath, path);
       IManagedObject[] mo = findAlreadyCreatedObjects(p);
       IManagedObject target = mo[mo.length-1];
       if ( target == null ) throw new IllegalArgumentException("Object does not exist: "+path);
       if (target instanceof Folder) throw new IllegalArgumentException(path+" is a folder");
       if (target == root) throw new IllegalArgumentException("Cannot delete root");
       Folder folder = (Folder) mo[mo.length-2];

       // Find if there is MountPoint in the paths and if there is,
       // delegate execution of this function there
       int imp = indexOfTopMountPoint(mo);
       //System.out.println("Tree rm: path="+p.toString()+", imp="+imp);
       if (imp > -1) {
            MountPoint mp = (MountPoint) mo[imp];
            String newPath = p.toString(imp, p.size());
            //System.out.println("\t\t\t  newPath="+newPath);
            mp.getTree().rm(newPath);
            return;
       }
       
       folder.remove(target);
       if ( ! (target instanceof Folder) ) unRegisterManagedObject(target);
       //System.out.println("RM ::: path="+path+", isValid="+isValid+", folder.isBeingWatched()="+folder.isBeingWatched());
       if (isValid && folder.isBeingWatched()) fireStateChanged(new TreeEvent(this, TreeEvent.NODE_DELETED, p.toArray(), null, 0));
    }
    
    /**
     * Get the full path of an IManagedObject.
     * @param object The IManagedObject whose path is to be returned.
     * @return       The object's absolute path.
     *               In C++ if the object does not exist, an empty string is returned.
     * @throws       IllegalArgumentException If the IManagedObject does not exist.
     *
     */
    public String findPath(IManagedObject object) throws IllegalArgumentException {
        String path = getPathForObject(object);
        if ( path != null ) return path;
        throw new IllegalArgumentException("Object "+object+" could not be found in tree");
    }

    private String getPathForObject(IManagedObject object) {
        Object path = pathHash.get(object);
        if ( path != null ) return ( (Path) path ).toString();
        for ( int i = 0; i < mountList.size(); i++ ) {
            Path mountPath = new Path(currentPath, (String) mountList.get(i) );
            IManagedObject[] mo = findAlreadyCreatedObjects(mountPath);
            MountPoint mountPoint = (MountPoint) mo[mo.length-1];
            String obj = mountPoint.getTree().getPathForObject(object);
            if ( obj != null ) return  (mountPath.toString() + obj);
        }
        return null;
    }
    
    /**
     * Move an IManagedObject or a directory from one directory to another.
     * @param oldPath The path of the IManagedObject or direcoty to be moved.
     * @param newPath The path of the diretory in which the object has to be moved to.
     * @throws        IllegalArgumentException If either path does not exist.
     *
     */
    public void mv(String oldPath, String newPath) throws IllegalArgumentException {

        //Find the object to be moved and the folder that is containing it.
        Path fromPath = new Path(currentPath,oldPath);
        Folder fromFolder;
        IManagedObject objToMove;
        try {
            fromFolder = (Folder) findObject( fromPath.parent().toString() );
            objToMove = findObject( fromPath.toString() );
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Illegal \"from\"-path "+oldPath+"; it does not correspond to an IManagedObejct.");
        }
        if ( objToMove == null ) throw new IllegalArgumentException("Cannot move. Path "+fromPath.toString()+" corresponds to a null object.");
        
        //The fullpath of the destination
        Path toPath = new Path(currentPath, newPath);

        //Get the destination object, if it exists.
        IManagedObject destinationObject = null;
        try { 
            destinationObject = findObject( toPath.toString() );
        } catch ( IllegalArgumentException iae ) {}
        
        String newName = null;
        
        if ( destinationObject != null )  {
            if ( ! (destinationObject instanceof Folder) ) {
                if ( objToMove instanceof Folder )
                    throw new IllegalArgumentException("Cannot move a folder on an existing IManagedObject that is not a folder.");
                else 
                    if ( overwrite ) {
                        newName = AidaUtils.modifyName(destinationObject.name());
                        rm( toPath.toString() );
                    } else
                        throw new IllegalArgumentException("Cannot move. An object exists in the final path and the overwrite flag is set to false.");
            }
        } else
            newName = toPath.getName();
        
        //Get the destination folder
        Path destinationFolderPath;
        if ( destinationObject != null && destinationObject instanceof Folder )
            destinationFolderPath = toPath;
        else
            destinationFolderPath = toPath.parent();

        //Remove the object from the original directory.
        fromFolder.remove( objToMove );
        
        //Rename the object if necessary
        if ( newName != null )
            ( (IDevManagedObject) objToMove ).setName(newName);
        
        //Find the folder in which the object has to be moved
        try {
            add( destinationFolderPath.toString(), objToMove, overwrite, false, false );
        } catch ( Throwable t ) {
            //Maybe a better message is required if the final object cannot be overwritten.
            throw new IllegalArgumentException("Illegal \"to\"-path "+newPath+". Cannot move the object to this path.");
        }
        Folder destinationFolder = (Folder) findObject( destinationFolderPath.toString() );
        if (isValid && ( fromFolder.isBeingWatched() || destinationFolder.isBeingWatched())) fireStateChanged(new TreeEvent(this, TreeEvent.NODE_MOVED, toPath.toArray(), null, fromPath.toArray()));
        
    }
    
    /**
     * Commit any open transaction to the underlying store(s).
     * It flushes objects into the disk for non-memory-mapped stores.
     * @throws IOException If the underlying store cannot be written out.
     *
     */
    public void commit() throws IOException {
        if (aidaStore == null) { // Create new Store if not present and createNew=true
            if (!createNew || storeName == null || storeName.length()==0) throw new IOException("There is not store to commit to");
            createStore();
        }
        if (aidaStore.isReadOnly()) throw new IOException("Cannot commit readonly store");
        if (createNew && !optionsMap.containsKey("createNew")) {
            if (optionsMap == Collections.EMPTY_MAP) optionsMap = new HashMap();
            optionsMap.put(new String("createNew"), Boolean.toString(createNew));
        }
        aidaStore.commit(this,optionsMap);      
    }
    
    /**
     * Set the strategy of what should happen if two objects have the same path.
     * Default is overwrite.
     *
     */
    public void setOverwrite() {
        setOverwrite(true);
    }
    
    /**
     * Set the strategy of what should happen if two objects have the same path.
     * Default is overwrite.
     * @param overwrite <code>true</code> to enable overwriting.
     *
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
    
    /**
     * Copy an object from a path to another.
     * @param oldPath   The path of the object to be copied.
     * @param newPath   The path where the object is to be copied.
     * @throws          IllegalArgumentException If either path does not exist.
     *
     */
    public void cp(String oldPath, String newPath) throws IllegalArgumentException {
        cp(oldPath, newPath, false);
    }
    
    /**
     * Copy an object from a path to another.
     * @param oldPath   The path of the object to be copied.
     * @param newPath   The path where the object is to be copied.
     * @param recursive <code>true</code> if a recursive copy has to be performed.
     * @throws          IllegalArgumentException If either path does not exist.
     *
     */
    public void cp(String oldPath, String newPath, boolean recursive) throws IllegalArgumentException {

        //Find the object to be moved and the folder that is containing it.
        Path fromPath = new Path(currentPath,oldPath);
        IManagedObject objToCopy;
        try {
            objToCopy = findObject( fromPath.toString() );
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Illegal \"from\"-path "+oldPath+"; it does not correspond to an IManagedObejct.");
        }
        if ( objToCopy == null ) throw new IllegalArgumentException("Cannot copy. Path "+fromPath.toString()+" corresponds to a null object.");
        
        //The fullpath of the destination
        Path toPath = new Path(currentPath, newPath);


        //Get the destination object, if it exists.
        IManagedObject destinationObject = null;
        try { 
            destinationObject = findObject( toPath.toString() );
        } catch ( IllegalArgumentException iae ) {}
        
        if ( destinationObject != null )  {
            if ( ! (destinationObject instanceof Folder) ) {
                if ( objToCopy instanceof Folder )
                    throw new IllegalArgumentException("Cannot copy a folder on an existing IManagedObject that is not a folder.");
                else 
                    if ( overwrite ) {
                        rm( toPath.toString() );
                    } else
                        throw new IllegalArgumentException("Cannot copy. An object exists in the final path and the overwrite flag is set to false.");
            } else
                toPath = new Path( toPath, AidaUtils.modifyName(objToCopy.name()) );
        } 
        
        //Make a copy of the IManagedObjec
        copyIManagedObject(toPath.toString(), objToCopy);
        
        if ( recursive && objToCopy instanceof Folder ) {
            String fromPathString = fromPath.toString();
            String[] objsToCopy = listObjectNames(fromPathString);
            for( int i = 0; i < objsToCopy.length; i++ ) {
                String objRelName = objsToCopy[i].substring( fromPathString.length()+1 );
                Path endPath = new Path( toPath, objRelName );
                cp( objsToCopy[i], endPath.toString(), recursive );
            }
        }
    }
    
    /**
     * Create a symbolic link to an object in the ITree.
     * @param path  The absolute or relative path of the object to be linked.
     * @param alias The absolute or relative name of the link.
     * @throws      IllegalArgumentException If path or any
     *              subidrectory within path does not exist.
     *
     */
    public void symlink(String path, String alias) throws IllegalArgumentException {

        //Find the object to be linked.
        Path fromPath = new Path(currentPath,path);
        IManagedObject objToLink;
        try {
            objToLink = findObject( fromPath.toString() );
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Illegal \"from\"-path "+path+"; it does not correspond to an IManagedObejct.");
        }
        if ( objToLink == null ) throw new IllegalArgumentException("Cannot copy. Path "+fromPath.toString()+" corresponds to a null object.");
        
        //Check that there is no IManagedObject (other than a folder) where the link is going to be.
        Path toPath = new Path(currentPath,alias);
        IManagedObject aliasObj = null;
        Folder aliasFolder = null;
        try {
            aliasObj = findObject( toPath.toString() );
            aliasFolder = (Folder) findObject( toPath.parent().toString() );
        } catch ( IllegalArgumentException iae ) {
        }
        
        if ( aliasObj != null ) {
            if ( ! ( aliasObj instanceof Folder ) )
                throw new IllegalArgumentException("There is already an object, other than a directory, in the \"alias\" location "+alias);
            else {
                toPath = new Path( toPath, fromPath.getName() );
                aliasFolder = (Folder) aliasObj;
            }
        } else {
            if ( aliasFolder == null ) 
                throw new IllegalArgumentException("Illegal \"alias\" "+alias);
        }
        
        checkForCircularLink(fromPath, toPath);
        
        Link link = new Link(toPath.getName(), fromPath);
        aliasFolder.add( link );
        if (isValid && aliasFolder.isBeingWatched()) fireStateChanged(new TreeEvent(this, TreeEvent.LINK_ADDED, toPath.toArray(), objToLink.getClass(), fromPath.toArray()));
         
    }
    
    private void checkForCircularLink( Path from, Path to ) {
        if ( from.toString().equals( to.toString() ) )
            throw new IllegalArgumentException("Circular link "+from.toString()+" -> "+to.toString());
        IManagedObject fromObj = null;
        try {
            fromObj = findObject( from.toString() );
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("The link chain somewhere is broken");
        }
        if ( fromObj == null )
            throw new IllegalArgumentException("The link chain somewhere is broken");

        if ( fromObj instanceof Link ) 
            checkForCircularLink( ((Link)fromObj).path(), to );
    }
    
    /**
     * Mounts a tree within another (target) tree. A tree can only be mounted once.
     * Example:
     * <pre>
     *     target.mount("/home/tonyj",tree,"/");
     * </pre>
     * @param path     The path in the target tree
     * @param tree     The tree to mount within the target tree
     * @param treePath The mount point within the tree to be mounted.
     * @throws         IllegalArtumentException If something does not exist.
     *
     */
    public void mount(String path, ITree tree, String treePath) throws IllegalArgumentException {
        // We only support mounting trees we created
	//System.out.println("Tree Mount: path="+path+", treePath="+treePath+", this="+this.storeName()+",  tree="+tree.storeName());
       if (tree instanceof Tree)
       {
          Path p = new Path(currentPath,path);
          IManagedObject[] mo = findAlreadyCreatedObjects(p);
          IManagedObject obj = mo[mo.length-1];
          if (obj != null) throw new IllegalArgumentException(path+" already exists");
          IManagedObject f = mo[mo.length-2];
          if (f == null) {
              f = checkAndFillFolder(p);
          }
          if (f == null) throw new IllegalArgumentException("Folder does not exist: "+p.parent());
          Folder parent = (Folder) f;
          Tree t = (Tree) tree;
          Path mountPath = new Path(new Path(), treePath);
          IManagedObject mountPoint = t.checkAndFillFolder(mountPath);
          if (mountPoint == null) throw new IllegalArgumentException("Can not find Mount Point: "+treePath);
          if (mountPoint instanceof Folder)
          {
               // Find if there is MountPoint in the paths and if there is,
               // delegate execution of this function there
               int imp = indexOfTopMountPoint(mo);
               //System.out.println("Tree mount: path="+p.toString()+", imp="+imp);
               if (imp > -1) {
                    MountPoint mp = (MountPoint) mo[imp];
                    String newPath = p.toString(imp, p.size());
                    //System.out.println("\t\t\t  newPath="+newPath);
                    mp.getTree().mount(newPath, tree, treePath);
                    return;
               }
       
             MountPoint mp = new MountPoint(this, p, t, (Folder) mountPoint, mountPath);
	     mp.setFilled(((Folder) mountPoint).isFilled());
             parent.add(mp);
             //System.out.println("Mounting: "+mp.name()+", into parent: "+parent.name()+", try to get it back: "+
             //                   parent.getChild(mp.name()).name());
             t.incrementMountCount();
             mountList.add(p.toString());

             /*
             IManagedObject[] manObjs = t.getAllManagedObjectsInPath(mountPath);
             for ( int i = 0; i < manObjs.length; i++ ) 
                 registerManagedObject( manObjs[i], new Path(p, t.findPath(manObjs[i]).substring( mountPath.toString().length() ) ) );
              */
             if (isValid && parent.isBeingWatched()) fireStateChanged(new TreeEvent(this, TreeEvent.NODE_ADDED, p.toArray(), mp.getClass(), TreeEvent.FOLDER_MASK));
          }
          else throw new IllegalArgumentException(treePath+" does not point to a folder");
       }
       else throw new UnsupportedOperationException();
    }
    
    /**
     * Unmount a subtree at a given path (mount point).
     * Whenever a tree is destroyed it first unmounts all dependent trees.
     * @param path The path of the subtree to be unmounted.
     * @throws     IllegalArgumentException If path does not exist.
     *
     */
    public void unmount(String path) throws IllegalArgumentException {
        Path p = new Path(currentPath,path);
        IManagedObject[] mo = findAlreadyCreatedObjects(p);

        IManagedObject obj = mo[mo.length-1];
        if (obj == null) throw new IllegalArgumentException("Path does not exist: "+p.parent());
        if (mo[mo.length-2] == null) throw new IllegalArgumentException("Folder does not exist: "+p.parent());

        if (obj instanceof MountPoint)
        {
           // Find if there is MountPoint in the paths and if there is,
           // delegate execution of this function there
           int imp = indexOfTopMountPoint(mo);
           //System.out.println("Tree unmount: path="+p.toString()+", imp="+imp);
           if (imp > -1 && imp < (mo.length-1)) {
                MountPoint mp = (MountPoint) mo[imp];
                String newPath = p.toString(imp, p.size());
                //System.out.println("\t\t\t  newPath="+newPath);
                mp.getTree().unmount(newPath);
                return;
           }
       
           MountPoint mp = (MountPoint) obj;
           Folder parent = (Folder) mo[mo.length-2];
           parent.remove(mp);
           mountList.remove(p.toString());

           IManagedObject[] manObjs = getAllManagedObjectsInPath(p);
           for ( int i = 0; i < manObjs.length; i++ ) 
               if ( manObjHash.containsValue(manObjs[i]) )
                   unRegisterManagedObject( manObjs[i] );
           
           if (isValid && parent.isBeingWatched()) fireStateChanged(new TreeEvent(this, TreeEvent.NODE_DELETED, p.toArray(), null, TreeEvent.FOLDER_MASK));

           mp.unmount();
        }
        else throw new IllegalArgumentException("Not a mount point");
    }
    
    
    /**
     *
     * Non-AIDA methods are down here.
     *
     */
    public void init( String storeName, String storeType, int mode, String options) throws IOException {
        this.storeName = storeName;
        this.storeType = storeType;

        this.readOnly = false;
        this.createNew = false;
        this.readOnlyUserDefined = false;
        
        //FIXME need more options here.
        if ( mode == ITreeFactory.CREATE )
            createNew = true;
        if ( mode == ITreeFactory.RECREATE )
            createNew = true;
        if ( mode == ITreeFactory.READONLY ) {
            readOnly = true;
            readOnlyUserDefined = true;
        }
        if ( mode == ITreeFactory.UPDATE ) {
            readOnly = false;
            readOnlyUserDefined = true;
        }        
        init(storeName, readOnly, createNew, storeType, options, readOnlyUserDefined );        
    }
    
    /**
     * Associate the tree with a store
     * @param storeName The name of the output storage unit.
     * @param readOnly  <code>true</code> if the tree is readonly.
     * @param createNew  <code>true</code> if the tree has to create a new file.
     * @param storeType The type of the output storage unit.
     *
     */
    public void init( String storeName, boolean readOnly, boolean createNew, String storeType, String options, boolean readOnlyUserDefined ) throws IOException {
        this.storeName = storeName;
        this.readOnly = readOnly;
        this.readOnlyUserDefined = readOnlyUserDefined;
        this.createNew = createNew;
        this.storeType = storeType;
        
        if (readOnly && createNew) throw new IllegalArgumentException("readOnly and createNew not allowed");
        optionsMap = AidaUtils.parseOptions( options );
        
        if (storeName!=null && storeName.length()>0 && ! createNew) {
           createStore().read(this,optionsMap, readOnly, createNew);
        }
    }
    private IStore createStore() throws IOException
    {
       if (aidaStore == null)
       {
          if (storeType == null || storeType.length()==0) storeType = "xml";

         // Look for a handler for this storeType
         Lookup.Template template = new Lookup.Template(IStoreFactory.class);
         Lookup.Result result = FreeHEPLookup.instance().lookup(template);
         for (Iterator i = result.allInstances().iterator(); i.hasNext(); )
         {
            IStoreFactory factory = (IStoreFactory) i.next();
            if (factory.supportsType(storeType))
            {   
               //System.out.println("Got item for type: "+storeType+",  "+item.getClass()+",  "+item);
               aidaStore =  factory.createStore();
               
               //If the readOnly flag has not been set by the user AND the store is
               //read-only, then we default to read-only=true.
               //FIX for JAIDA-46
               if ( ! readOnlyUserDefined && aidaStore.isReadOnly() )
                   this.readOnly = true;               
               if ( aidaStore.isReadOnly() && ( ! isReadOnly() ) )
                   throw new IllegalArgumentException("When opening a read-only file, the associated tree must be read-only. Please correct the options with which you created the tree.");
               return aidaStore;
            }
         }
         throw new IOException("Unknown store type: "+storeType);
       }
       else return aidaStore;
    }

    /**
     * Is called by the Store to let Tree know that a particular folder has been filled 
     * already. "path" is path to a folder, cannot point to an Object.
     */
    public void hasBeenFilled(String path) throws IllegalArgumentException {
	Path p = new Path(currentPath,path);
        IManagedObject[] mo = findAlreadyCreatedObjects(p); 
        IManagedObject obj = mo[mo.length-1];
        if (obj == null || !(obj instanceof Folder)) throw new IllegalArgumentException("Invalid path: "+p);
        Folder folder = (Folder) obj;
	
	folder.setFilled(true);
    }

    public boolean isReadOnly() { return readOnly; }

    /**
     * This "add" method is called from the IStore, and can
     * create new folders if it is needed.
     * Does not overwrite existing objects, just skip them.
     */
   public void add(String path, IManagedObject child) 
   {
       add(path, child, false, true);
   }

    /**
     * This "add" method is called from Factories (HistogramFactory, ...),
     * and can create new folders if it is needed.
     * It does overwrite existing objects.
     */
   public void addFromFactory(String path, IManagedObject child)
   {
       Path fullPath = new Path(currentPath,path);
       add(fullPath.toString(), child, true, false);
       AidaUtils.fillPath(this, child);
   } 
    
   void add(String path, IManagedObject child, boolean overwrite, boolean createNewDirs) {
       add(path,child,overwrite, createNewDirs,true);
   }
   
   void add(String path, IManagedObject child, boolean overwrite, boolean createNewDirs, boolean sendEvent) 
   {
      Path folderPath = new Path(currentPath,path);
      //Path objectPath = new Path(currentPath,(path+separatorChar+AidaUtils.modifyName(child.name())));
      Path objectPath = new Path(currentPath,(path+separatorChar+AidaUtils.modifyName(child.name())));
      
      //System.out.println("add: path="+path+", name="+AidaUtils.modifyName(child.name()));
     IManagedObject[] mo = findAlreadyCreatedObjects(objectPath); 
     if (mo == null || mo.length < 1) throw new IllegalArgumentException("Invalid path: +"+objectPath.toString());
     IManagedObject o = mo[mo.length-1];
     if (o != null) {
	 if (overwrite) rm(objectPath.toString());
         else return;
         //System.out.println("WARNING: Object \""+AidaUtils.modifyName(child.name())+"\" already exists in directory: "+folderPath.toString());
         //throw new TreeObjectAlreadyExistException("Object \""+AidaUtils.modifyName(child.name())+"\" already exists in directory: "+folderPath.toString());
     }
     if (mo[mo.length-2] != null && !(mo[mo.length-2] instanceof Folder)) throw new IllegalArgumentException("Illegal path for add: "+path);
     if (mo[mo.length-2] == null) {
         if ( createNewDirs ) {
             mkdirs(path);
             mo = findAlreadyCreatedObjects(objectPath); 
         } else 
             throw new RuntimeException("Some directories in the given path "+folderPath+" do not exist");
         //throw new TreeFolderDoesNotExistException("Folder "+folderPath.toString()+" does not exist");
     }

     // Find if there is MountPoint in the paths and if there is,
     // delegate execution of this function there
     int imp = indexOfTopMountPoint(mo);
     //System.out.println("Tree add: folderPath="+folderPath.toString()+", imp="+imp);
     if (imp > -1) {
        MountPoint mp = (MountPoint) mo[imp];
        String newPath = folderPath.toString(imp, folderPath.size());
        //System.out.println("\t\t\t  newPath="+newPath);
        mp.getTree().add(newPath, child,overwrite,createNewDirs,sendEvent);
        return;
     }

      Folder f = (Folder) mo[mo.length-2];
      f.add(child);
      registerManagedObject( child, new Path(folderPath,AidaUtils.modifyName(child.name())) );
      //System.out.println("ADD :: path="+objectPath.toString()+", isValid="+isValid+", folder.isBeingWatched()="+f.isBeingWatched());
      if ( sendEvent )
          if (isValid && f.isBeingWatched()) fireStateChanged(new TreeEvent(this, TreeEvent.NODE_ADDED, folderPath.toArray(AidaUtils.modifyName(child.name())), child.getClass(), 0));
   }
   
    protected IManagedObject[] getAllManagedObjectsInPath(Path path) {
        ArrayList list = new ArrayList();
        for (Enumeration e = manObjHash.keys() ; e.hasMoreElements() ;) {
            String fullPath = (String)e.nextElement();
            if ( fullPath.startsWith(path.toString()) )
                list.add( manObjHash.get(fullPath) );
        }
        IManagedObject[] manObjs = new IManagedObject[list.size()];
        for ( int i = 0; i < list.size(); i++ )
            manObjs[i] = (IManagedObject) list.get(i);
        return manObjs;
    }

    protected void registerManagedObject( IManagedObject obj, Path path ) {
       manObjHash.put(path.toString(),obj);
       pathHash.put(obj,path);
    }
    
    protected void unRegisterManagedObject( IManagedObject obj ) {
       manObjHash.remove( findPath(obj) );
       pathHash.remove(obj);
    }
    /**
     * List the content of an IManagedObject in the Tree
     * @param obj The IManagedObject to be listed
     * @param recursive <code>true</code> if a recursive list is to be performed
     * @return the ArrayList containing all the IManagedObjects listed
     *
     */
    private List ls( IManagedObject obj, boolean recursive ) {
        ArrayList list = new ArrayList();
        
        if ( obj instanceof Folder ) {
            int childNumb = ((Folder)obj).getChildCount();
            for ( int i = 0; i<childNumb; i++ ) {
                IManagedObject child = ((Folder)obj).getChild(i);
                list.add( child );
                if ( child instanceof Folder && recursive )
                    list.addAll( ls( (Folder)child, recursive ) );
            }
        } else {
            list.add(obj);
        }
        return list;
    }
    
    /**
     * Simple method to find the index of the top MountPoint.
     * @param  objects Array of IManagedObjects
     * @return Index of the top MountPoint object in the array, -1 if not found
     */
    private int indexOfTopMountPoint(IManagedObject[] mo) {
        int index = -1;
        if (mo != null) {
            for (int i=0; i<mo.length; i++) {
                if (mo[i] instanceof MountPoint) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
    
    /**
     * Return all IManagedObject in the path, from the Root down to the last 
     * path node (inclusive). No interaction with the Store - no new objects
     * are created here. If some objects in the path do not exist in the Tree, 
     * fill "null". This method can be used on folder or object
     */
    IManagedObject[] findAlreadyCreatedObjects(String pathString) {
        Path p = new Path(currentPath, pathString);
        return findAlreadyCreatedObjects(p);
    }

    private IManagedObject[] findAlreadyCreatedObjects(Path path) {
        int size = path.size();
        IManagedObject[] mo = new IManagedObject[size+1];
      
        int depth = 0;
        mo[0] = root;
        Folder here = root;
        IManagedObject obj = null;
        for (Iterator i=path.iterator(); i.hasNext(); )
       {
         depth++;
         String name = (String) i.next();
         obj = here.getChild(name);
         if ( obj instanceof Link )
             mo[depth] = findObject( ((Link)obj).path().toString() );
         else
             mo[depth] = obj;
         if (obj == null) { break; }
         if (obj instanceof Folder) here = (Folder) obj;
         else break;
      }

      /*
      System.out.println("Tree.findAlreadyCreatedObjects() result:");
      for ( int i=0; i<size; i++ ) {
	  System.out.println("\t"+i+"   "+( (mo[i] == null) ? "null" : mo[i].name() ) );
      }
      */
      return mo;
    } 

    /**
     * Returns the last object in the Path, the object must be a folder.
     * If object is a Folder, check if it has been filled and fill it 
     * from the Store if needed.
     *
     * @param path Path to the folder that needs to be checked and, possibly, filled. 
     * @throws IllegalArgumentException If the path does not exist in Store, or if it is not a directory.
     */
    private Folder checkAndFillFolderFromStore(Path path) throws IllegalArgumentException {
	Folder folder = null;
        IManagedObject[] mo = findAlreadyCreatedObjects(path);
	if (mo == null || mo.length == 0) throw new IllegalArgumentException("Invalid path: +"+path.toString());
	IManagedObject obj = mo[mo.length-1];
	
	if ( obj != null && !(obj instanceof Folder) )
            throw new IllegalArgumentException("Path does not point to a directory: "+path.toString());
	if (obj != null) {
            folder = (Folder) obj;
	    if (folder.isFilled()) return folder; // nothing to do if folder already is filled.
        }

	MountPoint mp = null; // Now find MountPoint closest to the last node in the path.
	String currentPath = path.toString();
        int depth = 0;
        int mountDepth = 0;
	for (depth=0; depth<mo.length; depth++) {
            if (mo[depth] == null) break;
	    if (mo[depth] instanceof MountPoint) {
		mp = (MountPoint) mo[depth];
                mountDepth = depth;
	    }
	}
	Tree currentTree = this; // get Tree that knows about the Store for the last node in the Path.
        //System.out.println("Path="+path.toString()+", mountDepth="+mountDepth+",  mp="+mp);
	if (mp != null) {
            currentTree = mp.getTree();
            currentPath = path.toString(mountDepth, path.size());
        }
	try {
            IStore currentStore = currentTree.getStore();
	    if (currentStore == null) return folder;
	    if (currentStore instanceof IOnDemandStore) {
		((IOnDemandStore) currentStore).read(currentTree, currentPath);
	    }
	} catch (IOException e) { throw new RuntimeException("Something is wrong with reading from the Store", e); }
        
        if (folder != null) return folder;
        
        // Try to find Folder again
        mo = findAlreadyCreatedObjects(path);
	if (mo == null || mo.length == 0) throw new IllegalArgumentException("Invalid path: +"+path.toString());
	obj = mo[mo.length-1];
	if ( obj != null && !(obj instanceof Folder) )
            throw new IllegalArgumentException("Path does not point to a directory: +"+path.toString());
	if (obj != null) {
            folder = (Folder) obj;
        }
        return folder;
    }

    /**
     * Returns the last object in the Path.
     * If object is a Folder, check if it has been filled and fill it from the Store if needed.
     * 
     * @param path Path to the folder that needs to be checked and, possibly, filled. 
     * @throws IllegalArgumentException If the path does not exist in Store.
    */
    private IManagedObject checkAndFillFolder(Path path) throws IllegalArgumentException {
        IManagedObject[] mo = findAlreadyCreatedObjects(path);
	if (mo == null || mo.length == 0) throw new IllegalArgumentException("Invalid path: +"+path.toString());
	IManagedObject obj = mo[mo.length-1];
	//System.out.println("checkAndFillFolder: path="+path+", name="+(obj==null ? "null" : obj.name()));
        if (obj != null && !(obj instanceof Folder)) return obj;
        
        Folder f = checkAndFillFolderFromStore(path);
        
        return f;
    }
    
    /*
    private IManagedObject checkAndFillFolder(Path path, boolean createFoldersAsNecessary) throws IllegalArgumentException {
        IManagedObject[] result = follow(path, createFoldersAsNecessary);
	if (result == null || result.length == 0) throw new IllegalArgumentException("Invalid path: +"+path.toString());
	IManagedObject obj = result[result.length-1];
	
//        System.out.println("checkAndFillFolder: result.length = "+ result.length);
//        for (int i=0; i<result.length; i++) 
//            System.out.println("checkAndFillFolder: "+i+"   name="+((result[i] == null) ? "null" : result[i].name()));
     
	Folder folder = null;
	if (obj instanceof Folder) folder = (Folder) obj;
	else folder = (Folder) result[result.length-2];
        
	if (folder.isFilled()) return obj; // nothing to do if folder already is filled.

	MountPoint mp = null; // Now find MountPoint closest to the last node in the path.
	String currentPath = "";
	for (int i=result.length-1; i>=0; i--) {
	    if (result[i] instanceof MountPoint) {
		mp = (MountPoint) result[i];
		break;
	    }
	    if (result[i] == null || result[i].name() == null) continue;
	    currentPath = separatorChar + result[i].name() + currentPath; // Need path only up to the MountPoint.
	}
	Tree currentTree = this; // get Tree that knows about the Store for the last node in the Path.
	if (mp != null) currentTree = mp.getTree();
        
	try {
           IStore currentStore = currentTree.aidaStore;
	    if (currentStore == null) return obj;
	    if (currentStore instanceof IOnDemandStore) {
		((IOnDemandStore) currentStore).read(currentTree, currentPath);
	    }
	} catch (IOException e) { throw new RuntimeException("Something is wrong with reading from the Store", e); }
        if (obj == null) obj = folder.getChild(path.getName());
	return obj;
    }
    */
    

    // make all AIDA Objects that are desendents from this path fire Disconnect or
    // Reconnect event
    public void fireConnectionEvent(String pathString, boolean connect) {
        boolean isFolder = true;
        IManagedObject[] mo = findAlreadyCreatedObjects(pathString);
        Object obj = mo[mo.length-1];
        if (obj == null) return;
        else if (obj instanceof Folder) {
            fireConnectionEvent((Folder) obj, connect);
        } else if (obj instanceof Connectable) {
            isFolder = false;
            ((Connectable) obj).setConnected(connect);
        }
        
        String[] path = AidaUtils.stringToArray(pathString);
        int id = connect ? TreeEvent.NODE_AVAILABLE : TreeEvent.NODE_UNAVAILABLE;
        int flags = isFolder ? TreeEvent.FOLDER_MASK : 0;
        Class clazz = obj.getClass();
        TreeEvent te = new TreeEvent(this, id, path, clazz, flags);
        fireStateChanged(te);
    }
    
    void fireConnectionEvent(Folder folder, boolean connect) {
        //if (!folder.isFilled()) return;
        if (folder instanceof Connectable) {
            ((Connectable) folder).setConnected(connect);
        }
        
        int size = folder.getChildCount();
        for (int i=0; i<size; i++) {
            IManagedObject mo = folder.getChild(i);
            if (mo instanceof Folder) {
                fireConnectionEvent((Folder) mo, connect);
            } else if (mo instanceof Connectable) {
                ((Connectable) mo).setConnected(connect);
            }
        }
    }
    
    void fireStateChanged(TreeEvent te)
    {
       super.fireStateChanged(te);
    }
    /**
     * This message is sent by a listener to indicate interest in a particular
     * path within the tree. If the path corresponds to a folder, that folder will 
     * be asked to notify listeners of all existing children, and to continue to notify
     * listeners of any future changes.
     */
    public void checkForChildren(String path)
    {
       Path p = new Path(currentPath,path);
       IManagedObject obj = checkAndFillFolder(p);

       if (obj instanceof Folder)
       {
          Folder folder = (Folder) obj;

          synchronized (folder)
          {
            for (int i=0; i<folder.getChildCount(); i++)
            {
               IManagedObject child = folder.getChild(i);
               int mask = (child instanceof Folder) ? TreeEvent.FOLDER_MASK : 0;
               TreeEvent te = new TreeEvent(this, TreeEvent.NODE_ADDED, p.toArray(AidaUtils.modifyName(child.name())), child.getClass(), mask);
               fireStateChanged(te);
            }
            folder.setIsBeingWatched(true);
          }
       }
    }

    /**
     * This message is sent by a listener to indicate interest in a particular
     * path within the tree. If the path corresponds to a folder, that folder 
     * will be asked to notify listeners of any future changes.
     */
    public void setFolderIsWatched(String path, boolean state)
    {
       Path p = new Path(currentPath,path);
       IManagedObject obj = checkAndFillFolder(p);

       if (obj instanceof Folder)
       {
            Folder folder = (Folder) obj;
            folder.setIsBeingWatched(state);
        }
    }

    /*
    public void setStore(String name, String type, String options)
    {
       storeName = name;
       storeType = type;
       Map extra = AidaUtils.parseOptions( options );
       optionsMap.putAll(extra);
    }
    */
    public Map getOptions()
    {
       return optionsMap;
    }
    private int getMountCount()
    {
       return mountCount;
    }
    private int incrementMountCount()
    {
       mountCount++;
       return mountCount;
    }
    protected int decrementMountCount()
    {
       mountCount--;
       return mountCount;
    }
    
    private void copyIManagedObject(String path, IManagedObject obj) {
        if ( obj instanceof IHistogram1D )
            analysisFactory.createHistogramFactory(this).createCopy(path, (IHistogram1D)obj);
        else if ( obj instanceof IHistogram2D )
            analysisFactory.createHistogramFactory(this).createCopy(path, (IHistogram2D)obj);
        else if ( obj instanceof IHistogram3D )
            analysisFactory.createHistogramFactory(this).createCopy(path, (IHistogram3D)obj);
        else if ( obj instanceof ICloud1D )
            analysisFactory.createHistogramFactory(this).createCopy(path, (ICloud1D)obj);
        else if ( obj instanceof ICloud2D )
            analysisFactory.createHistogramFactory(this).createCopy(path, (ICloud2D)obj);
        else if ( obj instanceof ICloud3D )
            analysisFactory.createHistogramFactory(this).createCopy(path, (ICloud3D)obj);
        else if ( obj instanceof IProfile1D )
            analysisFactory.createHistogramFactory(this).createCopy(path, (IProfile1D)obj);
        else if ( obj instanceof IProfile2D )
            analysisFactory.createHistogramFactory(this).createCopy(path, (IProfile2D)obj);
        else if ( obj instanceof IDataPointSet )
            analysisFactory.createDataPointSetFactory(this).createCopy(path, (IDataPointSet)obj);
        else if ( obj instanceof IFunction )
            analysisFactory.createFunctionFactory(this).cloneFunction(path, (IFunction)obj);
        else if ( obj instanceof ITuple ) 
            analysisFactory.createTupleFactory(this).createFiltered(path, (ITuple)obj, null);
        else if ( obj instanceof Folder )
            mkdirs(path);
        else if ( obj instanceof Link ) {
            Path newPath = new Path(currentPath,path);
            Link link = new Link( newPath.getName(), ((Link)obj).path() );
            add(newPath.parent().toString(), link, true, false);
        }
        else
            throw new IllegalArgumentException("Cannot copy IManagedObject "+obj);
    }
        
        
}
