/*
 * RemoteTree.java
 *
 * Created on February 5, 2005, 5:43 PM
 */

package hep.aida.ref.remote;

import hep.aida.IAnnotation;
import hep.aida.IBaseHistogram;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.ITuple;
import hep.aida.dev.IDevMutableStore;
import hep.aida.dev.IDevTree;
import hep.aida.dev.IStore;
import hep.aida.dev.IStoreFactory;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.Annotation;
import hep.aida.ref.ManagedObject;
import hep.aida.ref.event.AIDAObservable;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;


/**
 *
 * @author  serbo
 */
public class RemoteTree extends AIDAObservable implements IDevTree, IsObservable {
    protected IStore  aidaStore;
    private String    storeName;
    private boolean   readOnly;
    private boolean   readOnlyUserDefined;
    private boolean   appendAxisType;
    private boolean   createNew;
    protected String  storeType = null;
    protected Map     optionsMap;
    protected boolean overwrite;
    protected Vector  updateBuffer;
    protected Logger  remoteLogger;
    
    private Object lock;
    protected TreeMap map = new java.util.TreeMap();
    
    
    /** Creates a new instance of RemoteTree */
    public RemoteTree() {
        this("Tree");
    }
    public RemoteTree(String name) {
        this(name, null);
    }
    public RemoteTree(String name, IDevMutableStore aidaStore) {
        this(name, aidaStore, false);
    }
    public RemoteTree(String name, IDevMutableStore aidaStore, boolean overwrite) {
        this(name, aidaStore, overwrite, false);
    }
    public RemoteTree(String name, IDevMutableStore aidaStore, boolean overwrite, boolean appendAxisType) {
        super();
        this.storeName = name;
        this.aidaStore = aidaStore;
        this.lock = new Object();
        this.overwrite = overwrite;
        this.remoteLogger = Logger.getLogger("hep.aida.ref.remote");
        this.appendAxisType = appendAxisType;
        this.updateBuffer = new Vector();
        setIsValidAfterNotify(true);
        addFolder("/");
    }
    
    
    // Service methods
    
    public void submitEventToListeners(AidaUpdateEvent ev) {
        remoteLogger.finest("RemoteTree.submitEventToListeners ::  id="+ev.id()+", path="+ev.path());
        if (ev instanceof RemoteUpdateEvent) fireStateChanged((EventObject) ev);
        else {
            int id = ev.id();
            String path = ev.path();
            String type = ev.nodeType();
            RemoteUpdateEvent rev = new RemoteUpdateEvent(id, path, type);
            fireStateChanged(rev);
        }
    }
    
    public void init( String storeName, boolean readOnly, boolean createNew, String storeType, String options, boolean readOnlyUserDefined ) throws IOException {
        this.storeName = storeName;
        this.readOnly = readOnly;
        this.readOnlyUserDefined = readOnlyUserDefined;
        this.createNew = createNew;
        this.storeType = storeType;
        
        if (readOnly && createNew) throw new IllegalArgumentException("readOnly and createNew not allowed");
        optionsMap = AidaUtils.parseOptions( options );
        
        if (storeName!=null && storeName.length()>0 && ! createNew) {
            if (getLock() != null) {
                synchronized (getLock()) {
                    createStore().read(this,optionsMap, readOnly, createNew);
                }
            } else {
                createStore().read(this,optionsMap, readOnly, createNew);
            }
        }
    }
    protected IStore createStore() throws IOException {
        if (aidaStore == null) {
            if (storeType == null || storeType.length()==0) storeType = "xml";
            
            // Look for a handler for this storeType
            Lookup.Template template = new Lookup.Template(IStoreFactory.class);
            Lookup.Result result = FreeHEPLookup.instance().lookup(template);
            for (Iterator i = result.allInstances().iterator(); i.hasNext(); ) {
                IStoreFactory factory = (IStoreFactory) i.next();
                if (factory.supportsType(storeType)) {
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
        } else return aidaStore;
    }
    
    public String correctPath(String path, boolean isDir) {
        String tmpPath = path;
        if (path == null || path.trim().equals("")) return path;
        if (tmpPath.equals("/")) return tmpPath;
        tmpPath.replaceAll("///", "/");
        tmpPath.replaceAll("//", "/");
        if (!tmpPath.startsWith("/")) tmpPath = "/"+tmpPath;
        if (isDir && !tmpPath.endsWith("/")) tmpPath = tmpPath+"/";
        if (!isDir && tmpPath.endsWith("/")) tmpPath = tmpPath.substring(0, tmpPath.length()-1);
        return tmpPath;
    }

    protected String[] executeListObjectNames(String path, boolean recursive) {
        String[] names = null;
        remoteLogger.finest("RemoteTree.executeListObjectNames for recursive="+recursive+",  Path: "+ path);
        Map tailMap = map.tailMap(path);
        ArrayList list = new ArrayList(tailMap.size());
        Iterator tailIt = tailMap.keySet().iterator();
        while (tailIt.hasNext()) {
            String key = (String) tailIt.next();
            if (path.equals(key)) {
                continue;
            } else if (recursive && path.equals("/")) {
                list.add(key);
            } else if (key.startsWith(path)) {
                if (recursive) {
                    list.add(key);
                } else {
                    String folder = AidaUtils.parseDirName(key);
                    String name = AidaUtils.parseName(key);
                    if (path.equals(folder)) {
                        list.add(key);
                    } else if (name == null || name.equals("")) {
                        int index1 = path.length();
                        int index2 = folder.length();
                        if (folder.endsWith("//")) index2 = index2 - 2;
                        else if (folder.endsWith("/")) index2 = index2 - 1;
                        String tmp = folder.substring(index1, index2);
                        if (tmp.indexOf("/") < 0) list.add(key);
                    }
                }
            }
        }
        list.trimToSize();
        names = new String[list.size()];
        list.toArray(names);
        
        return names;
    }
    
    protected String[] executeListObjectTypes(String path, boolean recursive) {
        String[] types = null;
        remoteLogger.finest("RemoteTree.executeListObjectTypes for recursive="+recursive+",  Path: "+ path);
        Map tailMap = map.tailMap(path);
        ArrayList list = new ArrayList(tailMap.size());
        Iterator tailIt = tailMap.keySet().iterator();
        while (tailIt.hasNext()) {
            String key = (String) tailIt.next();
            String type = null;
            if (path.equals(key)) continue;
            else if (key.startsWith(path)) {
                boolean acceptKey = false;
                if (recursive) {
                    acceptKey = true;
                } else {
                    String folder = AidaUtils.parseDirName(key);
                    String name = AidaUtils.parseName(key);
                    if (path.equals(folder)) {
                        acceptKey = true;
                    } else if (name == null || name.equals("")) {
                        int index1 = path.length();
                        int index2 = folder.length();
                        if (folder.endsWith("//")) index2 = index2 - 2;
                        else if (folder.endsWith("/")) index2 = index2 - 1;
                        String tmp = folder.substring(index1, index2);
                        if (tmp.indexOf("/") < 0) acceptKey = true;
                    }
                }
                if (acceptKey) {
                    Object obj = map.get(key);
                    if (obj instanceof IManagedObject) type = ((IManagedObject) obj).type();
                    else type = obj.getClass().getName();
                    if (appendAxisType && !type.equalsIgnoreCase("dir")) {
                        String xType = "double";
                        String tmp = null;
                        if (obj instanceof ManagedObject) {
                            synchronized (obj) {
                                boolean isFillable = false;
                                boolean isAnnotationFillable = false;
                                Annotation an = null;
                                isFillable = ((ManagedObject) obj).isFillable();
                                if (!isFillable) ((ManagedObject) obj).setFillable(true);
                                if (obj instanceof IBaseHistogram) an = (Annotation) ((IBaseHistogram) obj).annotation();
                                else if (obj instanceof IDataPointSet) an = (Annotation) ((IDataPointSet) obj).annotation();
                                if (an != null) {
                                    isAnnotationFillable = an.isFillable();
                                    if (!isAnnotationFillable) an.setFillable(true);
                                    try {
                                        tmp = an.value("xAxisType");
                                    } catch (IllegalArgumentException e) {}
                                    an.setFillable(isAnnotationFillable);
                                }
                                ((ManagedObject) obj).setFillable(isFillable);
                            }
                        } else if (obj != null) {
                            IAnnotation an = null;
                            if (obj instanceof IBaseHistogram) an = (IAnnotation) ((IBaseHistogram) obj).annotation();
                            else if (obj instanceof IDataPointSet) an = (IAnnotation) ((IDataPointSet) obj).annotation();
                            try {
                                tmp = an.value("xAxisType");
                            } catch (Exception e) {}
                        }
                        if (tmp != null && !tmp.trim().equals("")) {
                            xType = tmp;
                            type = type + ":" + xType;
                        }
                    }
                    list.add(type);
                }
            }
        }
        list.trimToSize();
        types = new String[list.size()];
        list.toArray(types);
        
        return types;
    }
    
    public IManagedObject executeFind(String path) {
        String p = correctPath(path, false);
        if (!map.containsKey(p)) throw new IllegalArgumentException("Object does not exist for path: "+p);
        IManagedObject obj = (IManagedObject) map.get(p);
        
        return obj;
    }
    
    protected void executeClose() {
        if (aidaStore != null) {
            try {
                aidaStore.close();
            } catch (IOException ioe) { ioe.printStackTrace(); }
        }
        
        RemoteUpdateEvent rev = new RemoteUpdateEvent(AidaUpdateEvent.TREE_CLOSED, "/", "dir");
        submitEventToListeners(rev);
        map.clear();
    }
    
    // Next four methods are used by Store to change Tree structure
    // Store must provide correct path String
    // Those methods must not be called by anybody except the Store
    public void addObject(String path, IManagedObject object) throws IllegalArgumentException {
        String p = correctPath(path, true);
        String fullPath = p + AidaUtils.modifyName(object.name());
        String tmpName = storeName;
        if (tmpName == null || tmpName.trim().equals("")) tmpName = this.storeName();
        String jas3FullPath = "/"+tmpName+fullPath;
                
        remoteLogger.finest("RemoteTree.addObject path="+path+", correctedPath="+p+",  fullPath="+fullPath+", object="+object);
        if (map.containsKey(fullPath) && !overwrite) throw new IllegalArgumentException("Object already exists for path: "+fullPath);
        map.put(fullPath, object);
        
        /*
        try {
            if (object != null) {
                IAnnotation an = null;
                if (object instanceof IBaseHistogram) {
                    an = ((IBaseHistogram) object).annotation();
                } else if (object instanceof IDataPointSet) {
                    an = ((IDataPointSet) object).annotation();
                } else if (object instanceof IFunction) {
                    an = ((IFunction) object).annotation();
                } else if (object instanceof ITuple) {
                    an = ((ITuple) object).annotation();
                }
                
                if (an == null) return;
                try {
                    if (an.hasKey(Annotation.aidaPathKey)) {
                        an.setValue(Annotation.aidaPathKey, fullPath);
                    } else {
                        an.addItem(Annotation.aidaPathKey, fullPath, true);
                    }
                } catch (Exception e) { e.printStackTrace(); }
                
                try {
                    
                    if (an.hasKey(Annotation.fullPathKey)) {
                        an.setValue(Annotation.fullPathKey, jas3FullPath);
                    } else {
                        an.addItem(Annotation.fullPathKey, jas3FullPath, true);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        } catch (Exception e) {
            remoteLogger.log(Level.INFO, "RemoteTree.addObject  Exception for Path: "+path+", fullPath="+fullPath+", jas3FullPath="+jas3FullPath, e);
            remoteLogger.log(Level.FINEST, "", e.getStackTrace());
        }
         */
    }
    
    public void addFolder(String path) throws java.lang.IllegalArgumentException {
        String p = correctPath(path, true);
        //if (map.containsKey(path) && !overwrite) throw new IllegalArgumentException("Folder already exists for path: "+path);
        Object obj = map.get(p);
        if (obj != null) return;
        String objectName = AidaUtils.parseName(p);
        String folderName = AidaUtils.parseDirName(p);
        RemoteFolder f = new RemoteFolder(objectName);
        f.setTreeFolder(folderName);
        map.put(p, f);
    }
    
    void removeObject(String path) throws java.lang.IllegalArgumentException {
        String p = correctPath(path, false);
        if (!map.containsKey(p)) throw new IllegalArgumentException("Object does not exist for path: "+p);
        Object obj = map.remove(p);
        remoteLogger.finest("RemoteTree.removeObject path="+path+", correctedPath="+p+", object="+obj);
        //updateBuffer.remove(path);
    }
    
    void removeFolder(String path) throws java.lang.IllegalArgumentException {
        String p = correctPath(path, true);
        if (!map.containsKey(p)) throw new IllegalArgumentException("Folder does not exist for path: "+p);
        remoteLogger.finest("RemoteTree.removeFolder path="+path+", correctedPath="+p);
        Map tailMap = map.tailMap(p);
        Set keySet = tailMap.keySet();
        int size = keySet.size();
        Object[] a = new Object[size];
        keySet.toArray(a);
        for (int i=0; i<size; i++) {
            String key = (String) a[i];
            if (key.startsWith(p)) {
                Object obj = map.remove(key);
                //updateBuffer.remove(key);
            }
        }
    }
    
    String executeFindPath(IManagedObject iManagedObject) {
        String path = null;
        String key = null;
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            key = (String) it.next();
            if (map.get(key).equals(iManagedObject)) {
                path = key;
                break;
            }
        }
        return path;
    }
    
    int executeUpdate(String updatePath) {
        if (updateBuffer.isEmpty()) return 0;
        Object[] paths = null;
        if (lock != null) {
            synchronized (lock) {
                paths = updateBuffer.toArray();
                updateBuffer.clear();
            }
        } else {
            paths = updateBuffer.toArray();
            updateBuffer.clear();
        }
        int nObjects = paths.length;
        
        for (int i=0; i<paths.length; i++) {
            String path = (String) paths[i];
            if (updatePath == null || updatePath.equals("/") || path.startsWith(updatePath)) {
                try {
                    Object obj = (IManagedObject) map.get(path);
                    remoteLogger.finest("RemoteTree.executeUpdate: Path="+path+",  object="+obj);
                    //System.out.println("RemoteTree.executeUpdate: Path="+path+",  object="+obj);
                    if (obj == null) continue;
                    if (obj instanceof RemoteManagedObject) {
                        RemoteManagedObject rmo = (RemoteManagedObject) obj;
                        if (rmo.isDataValid()) {
                            //rmo.setValidForAll();
                            rmo.setDataValid(false);
                        }
                    } else if (obj instanceof ManagedObject) {
                        ManagedObject mo = (ManagedObject) obj;
                        // don't do anything here for now
                    }
                } catch (Exception e) {
                    remoteLogger.log(Level.INFO, "RemoteTree.executeUpdate  Exception for Path: "+path, e);
                    remoteLogger.log(Level.FINEST, "", e.getStackTrace());
                }
            }
        }
        return nObjects;
    }
    
    public int doUpdate(String updatePath) {
        if (lock != null) {
            //synchronized (lock) {
            return executeUpdate(updatePath);
            //}
        } else {
            return executeUpdate(updatePath);
        }
    }
    
    // IDevTree methods
    
    public Object getLock() { return lock; }
    
    public void setLock(Object lock) { this.lock = lock; }
    
    public void close() throws java.io.IOException {
        if (lock != null) {
            synchronized (lock) {
                executeClose();
            }
        } else {
            executeClose();
        }
        removeAllListeners();
    }
    
    public hep.aida.IManagedObject find(String path) throws java.lang.IllegalArgumentException {
        String p = correctPath(path, false);
        IManagedObject mo = null;
        if (lock != null) {
            synchronized (lock) {
                mo = executeFind(p);
                if (!updateBuffer.contains(p)) updateBuffer.add(p);
            }
        } else {
            mo = executeFind(p);
            if (!updateBuffer.contains(p)) updateBuffer.add(p);
        }
        return mo;
    }
    
    public void hasBeenFilled(String path) throws IllegalArgumentException {
        return;
    }
    
    public String[] listObjectNames(String path) throws java.lang.IllegalArgumentException {
        return listObjectNames(path, false);
    }
    
    public String[] listObjectNames(String path, boolean param) throws java.lang.IllegalArgumentException {
        String p = correctPath(path, true);
        if (lock != null) {
            synchronized (lock) {
                return executeListObjectNames(p, param);
            }
        } else {
            return executeListObjectNames(p, param);
        }
    }
    
    public String[] listObjectTypes(String path) throws java.lang.IllegalArgumentException {
        return listObjectTypes(path, false);
    }
    
    public String[] listObjectTypes(String path, boolean param) throws java.lang.IllegalArgumentException {
        String p = correctPath(path, true);
        if (lock != null) {
            synchronized (lock) {
                return executeListObjectTypes(p, param);
            }
        } else {
            return executeListObjectTypes(p, param);
        }
    }
    
    public String findPath(hep.aida.IManagedObject iManagedObject) throws java.lang.IllegalArgumentException {
        if (lock != null) {
            synchronized (lock) {
                return executeFindPath(iManagedObject);
            }
        } else {
            return executeFindPath(iManagedObject);
        }
    }
    
    public void setOverwrite() {
        overwrite = true;
    }
    
    public void setOverwrite(boolean param) {
        overwrite = param;
    }
    
    public String storeName() {
        return storeName;
    }
    
    //FIXME
    public String name() {
        throw new UnsupportedOperationException();
    }
    
    public String storeType() {
        return storeType;
    }
    
    public boolean isReadOnly() {
        return readOnly;
    }
    
    
    // Unsupported IDevTree methods below
    
    public void add(String path, IManagedObject object) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void mkdir(String str) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void mkdirs(String str) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void rm(String str) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void rmdir(String str) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void symlink(String str, String str1) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void unmount(String str) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void mount(String str, hep.aida.ITree iTree, String str2) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void mv(String str, String str1) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public String pwd() {
        throw new UnsupportedOperationException();
    }
    
    public void ls() throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void ls(String str) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void ls(String str, boolean param) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void ls(String str, boolean param, java.io.OutputStream outputStream) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void cd(String str) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void commit() throws java.io.IOException {
        throw new UnsupportedOperationException();
    }
    
    public void cp(String str, String str1) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public void cp(String str, String str1, boolean param) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public hep.aida.ITree findTree(String str) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public String[] listObjectNames() throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    public String[] listObjectTypes() throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        java.util.logging.Logger remoteLogger = java.util.logging.Logger.getLogger("hep.aida.ref.remote");
        Level level = Level.FINEST;
        remoteLogger.setLevel(level);
        Handler[] handlers = remoteLogger.getHandlers();
        for (int i=0; i<handlers.length; i++) {
            handlers[i].setLevel(level);
            handlers[i].setFormatter( new SimpleFormatter() {
                public String format(LogRecord record) {
                    String tmp = super.format(record);
                    String result = tmp.replaceFirst("\n", " :: \t");
                    return result;
                }
            } );
            
        }
        
        RemoteTree tree = new RemoteTree();
        tree.addObject("/", new RemoteHistogram1D("h00"));
        tree.addFolder("/hist/");
        tree.addObject("/hist/", new RemoteHistogram1D("h0111"));
        tree.addFolder("/hist/dir0/");
        tree.addObject("/hist/dir0/", new RemoteHistogram1D("h01"));
        tree.addObject("/hist/dir0/", new RemoteHistogram1D("h02"));
        tree.addObject("/hist/dir0/", new RemoteHistogram1D("h03"));
        
        tree.addFolder("/hist/dir1/");
        tree.addObject("/hist/dir1/", new RemoteHistogram1D("h11"));
        tree.addObject("/hist/dir1/", new RemoteHistogram1D("h12"));
        tree.addObject("/hist/dir1/", new RemoteHistogram1D("h13"));
        tree.addFolder("/hist/dir1/subdir/");
        tree.addObject("/hist/dir1/subdir/", new RemoteHistogram1D("h13"));
        
        tree.addFolder("/hist/dir2/");
        tree.addObject("/hist/dir2/", new RemoteHistogram1D("h21"));
        tree.addObject("/hist/dir2/", new RemoteHistogram1D("h22"));
        tree.addObject("/hist/dir2/", new RemoteHistogram1D("h23"));
        
        String path = "/hist/dir1/h12";
        Object obj = tree.find(path);
        System.out.println("PATH="+path+",  FOUND="+obj);
        
        path = "/hist/dir2/h22";
        obj = tree.find(path);
        System.out.println("PATH="+path+",  FOUND="+obj);
        
        path = "/hist/dir2/h23";
        obj = tree.find(path);
        System.out.println("PATH="+path+",  FOUND="+obj);
        
        
        //hep.aida.ref.tree.TreeUpdater updater = new hep.aida.ref.tree.TreeUpdater(tree);
        //updater.startUpdating();
        
        path = "/JasServerInfo/Free\\/Used Memory";
        String name = AidaUtils.parseName(path);
        String objDir = AidaUtils.parseDirName(path);
        
        IManagedObject h = new RemoteHistogram1D(name);
        
        // Make sure all directories in the path exist.
        if(tree instanceof RemoteTree) ((RemoteTree) tree).addFolder(objDir);
        else tree.mkdirs(objDir);
        
        if(tree instanceof RemoteTree) ((RemoteTree) tree).addObject(objDir, h);
        else tree.add(objDir, h);
        
        if (h instanceof RemoteManagedObject) {
            ((RemoteManagedObject) h).setTreeFolder(objDir);
            if (h instanceof RemoteUnavailableObject) ((RemoteManagedObject) h).setDataValid(true);
            else ((RemoteManagedObject) h).setDataValid(false);
        }
        
        String pathS = "/";
        boolean recursiveS = false;
        String[] names = tree.listObjectNames(pathS, recursiveS);
        String[] types = tree.listObjectTypes(pathS, recursiveS);
        System.out.println("\nFor path=\""+pathS+"\", recursive="+recursiveS+",  N_names="+names.length+"   N_types="+types.length);
        for (int i=0; i<names.length; i++) {
            System.out.println("\t"+i+"  name="+names[i]+"   type="+types[i]);
        }
        
        pathS = "/";
        recursiveS = true;
        names = tree.listObjectNames(pathS, recursiveS);
        types = tree.listObjectTypes(pathS, recursiveS);
        System.out.println("\nFor path=\""+pathS+"\", recursive="+recursiveS+",  N_names="+names.length+"   N_types="+types.length);
        for (int i=0; i<names.length; i++) {
            System.out.println("\t"+i+"  name="+names[i]+"   type="+types[i]);
        }
        
        pathS = "/hist/";
        recursiveS = false;
        names = tree.listObjectNames(pathS, recursiveS);
        types = tree.listObjectTypes(pathS, recursiveS);
        System.out.println("\nFor path=\""+pathS+"\", recursive="+recursiveS+",  N_names="+names.length+"   N_types="+types.length);
        for (int i=0; i<names.length; i++) {
            System.out.println("\t"+i+"  name="+names[i]+"   type="+types[i]);
            //System.out.println("\t"+i+"  name="+names[i]);
        }
        
        pathS = "/hist/";
        recursiveS = true;
        names = tree.listObjectNames(pathS, recursiveS);
        types = tree.listObjectTypes(pathS, recursiveS);
        System.out.println("\nFor path=\""+pathS+"\", recursive="+recursiveS+",  N_names="+names.length+"   N_types="+types.length);
        for (int i=0; i<names.length; i++) {
            System.out.println("\t"+i+"  name="+names[i]+"   type="+types[i]);
            //System.out.println("\t"+i+"  name="+names[i]);
        }
        
        pathS = "/hist/dir1";
        recursiveS = false;
        names = tree.listObjectNames(pathS, recursiveS);
        types = tree.listObjectTypes(pathS, recursiveS);
        System.out.println("\nFor path=\""+pathS+"\", recursive="+recursiveS+",  N_names="+names.length+"   N_types="+types.length);
        for (int i=0; i<names.length; i++) {
            System.out.println("\t"+i+"  name="+names[i]+"   type="+types[i]);
            //System.out.println("\t"+i+"  name="+names[i]);
        }
        
        pathS = "/hist/dir1";
        recursiveS = true;
        names = tree.listObjectNames(pathS, recursiveS);
        types = tree.listObjectTypes(pathS, recursiveS);
        System.out.println("\nFor path=\""+pathS+"\", recursive="+recursiveS+",  N_names="+names.length+"   N_types="+types.length);
        for (int i=0; i<names.length; i++) {
            System.out.println("\t"+i+"  name="+names[i]+"   type="+types[i]);
            //System.out.println("\t"+i+"  name="+names[i]);
        }
        
    }
    
    
}
