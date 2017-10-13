package hep.aida.web.taglib.util;

import hep.aida.IAnalysisFactory;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.web.taglib.PlotterRegistry;
import hep.aida.web.taglib.TreeTagSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Asynchronous AIDA tree cache.
 * 
 * @author The FreeHEP team at SLAC
 */
public class TreeCache implements ServletContextListener, HttpSessionListener {
  
// -- Private parts : ----------------------------------------------------------

  private static ITreeFactory _treeFactory;
  private static ConcurrentHashMap<String, Future<CachedTree>> _treeCache;
  private static volatile int _capacity = 5; // maximum number of trees in cache
  private static volatile long _timeout = 60L; // timeout for creating a tree, in seconds
  private static ExecutorService _executor;
  private static final Runnable _treeCachePurger = new Runnable() {
    public void run() {
      purge();
    }
  };
  
// -- Setters : ----------------------------------------------------------------

  public static void setCapacity(int capacity) {
    _capacity = capacity;
  }

  public static void setTimeout(long timeoutInSeconds) {
    _timeout = timeoutInSeconds;
  }
  
// -- Getters : ----------------------------------------------------------------
  
  public static int size() {
    return _treeCache.size();
  }
  
  public static List<String> getTreeNames() {
    return new ArrayList<String>(_treeCache.keySet());
  }
  
// -- Listening to webapp events : ---------------------------------------------

  public void contextInitialized(ServletContextEvent servletContextEvent) {
    _treeFactory = IAnalysisFactory.create().createTreeFactory();
    _treeCache = new ConcurrentHashMap<String, Future<CachedTree>>();
    _executor = Executors.newFixedThreadPool(2);
  }

  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    for (Future<CachedTree> fts : _treeCache.values()) {
      try {
        fts.get(30L, TimeUnit.SECONDS).closeTree();
      } catch (Exception x) {}
    }
    _treeFactory = null;
    _treeCache = null;
    _executor.shutdownNow();
    _executor = null;
  }

  public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

    String sessionId = httpSessionEvent.getSession().getId();

    for (Future<CachedTree> fts : _treeCache.values()) {
      try {
        fts.get(0L, TimeUnit.MICROSECONDS).removeSession(sessionId);
      } catch (Exception x) {
      }
    }

    //Also clear the plotter registry
    Object obj = httpSessionEvent.getSession().getAttribute(PlotterRegistry.REGISTRY_SESSION_NAME);
    if (obj != null) {
      ((PlotterRegistry) obj).clear();
    }
  }

  public void sessionCreated(HttpSessionEvent httpSessionEvent) {
  }
  
// -- Fetching trees : ---------------------------------------------------------

  public static ITree getTree(TreeTagSupport tag, String sessionId, boolean isPinned) throws IOException {
    return getTree(tag.getStoreName(), tag.getStoreType(), tag.getOptions(), sessionId, isPinned);
  }

  public static ITree getTree(TreeTagSupport tag, String sessionId) throws IOException {
    return getTree(tag.getStoreName(), tag.getStoreType(), tag.getOptions(), sessionId, false);
  }
  
  public static ITree getTree(final String storeName, final String storeType, final String options, final String sessionId) throws IOException {
    return getTree(storeName, storeType, options, sessionId, false);
  }

  public static ITree getTree(final String storeName, final String storeType, final String options, final String sessionId, final boolean isPinned) throws IOException {

    Future<CachedTree> fts = _treeCache.get(storeName);

    if (fts == null) {
      Callable<CachedTree> treeLoader = new Callable<CachedTree>() {
        public CachedTree call() throws InterruptedException, IOException {
          ITree tree = _treeFactory.create(storeName, storeType, true, false, options);
          CachedTree cachedTree = new CachedTree(tree);
          cachedTree.addSession(sessionId);
          cachedTree.pin(isPinned);
          return cachedTree;
        }
      };
      FutureTask<CachedTree> newFts = new FutureTask<CachedTree>(treeLoader);
      fts = _treeCache.putIfAbsent(storeName, newFts);
      if (fts == null) {
        fts = newFts;
        newFts.run();
        _executor.execute(_treeCachePurger);
      }
    }

    try {
      CachedTree tree = fts.get(_timeout, TimeUnit.SECONDS);
      tree.addSession(sessionId);
      if (isPinned) tree.pin(true);
      return tree;
    } catch (ExecutionException x) {
      _treeCache.remove(storeName, fts);
      Throwable t = x.getCause();
      try {
        throw (IOException) t;
      } catch (ClassCastException xx) {
        throw new IOException(t);
      }
    } catch (Exception x) {
      _treeCache.remove(storeName, fts);
      throw new IOException(x);
    }
  }

  /**
   * Returns the tree specified by <tt>storeName</tt> from cache.
   * Returns <tt>null</tt> if the tree is not in cache. 
   */
  public static ITree getTree(String storeName, String sessionId) {
    Future<CachedTree> ft = _treeCache.get(storeName);
    if (ft == null) return null;
    try {
      CachedTree tree = ft.get(_timeout, TimeUnit.SECONDS);
      if (sessionId != null) tree.addSession(sessionId);
      return tree;
    } catch (Exception x) {
      return null;
    }
  }
  
// -- Operations on cache : ----------------------------------------------------

  /**
   * For compatibility with old code.
   * If the specified tree is in cache, disassociates it from the specified session. 
   * The tree is not closed.
   */
  public static void closeTree(String storeName, String sessionId) throws IOException {
    Future<CachedTree> fts = _treeCache.get(storeName);
    if (fts != null) {
      try {
        fts.get(0L, TimeUnit.MICROSECONDS).removeSession(sessionId);
      } catch (Exception x) {
      }
    }
  }

  /** 
   * Remove trees from cache until the cache size gets acceptable.
   */
  static public void purge() {
    while (_treeCache.size() > _capacity) {
      String candidate = findEvictionCandidate();
      if (candidate == null) return;
      _treeCache.remove(candidate);
    }
    System.gc();
    System.runFinalization();
  }
  
  static public void evict(String storeName) {
    _treeCache.remove(storeName);
  }
  
  static public boolean pin(String storeName, boolean isPinned) {
    Future<CachedTree> ft = _treeCache.get(storeName);
    if (ft == null) return false;
    try {
      CachedTree tree = ft.get(0L, TimeUnit.SECONDS);
      tree.pin(isPinned);
      return true;
    } catch (Throwable x) {
      return false;
    }
  }
  
  public static void cache(final String storeName, final String storeType, final String options, final String sessionId, final boolean isPinned) {
    if (!_treeCache.containsKey(storeName)) {
      _executor.submit(new Runnable() {
        public void run() {
          try {
            if (!_treeCache.containsKey(storeName)) {
              CachedTree tree = (CachedTree) getTree(storeName, storeType, options, sessionId);
              tree.pin(isPinned);
            }
          } catch (IOException x) {}
        }
      });
    }
  }
  
  public static void cache(TreeTagSupport tag, String sessionId, final boolean isPinned) {
    cache(tag.getStoreName(), tag.getStoreType(), tag.getOptions(), sessionId, isPinned);
  }
  
// -- Local methods : ----------------------------------------------------------
  
  static private String findEvictionCandidate() {
    String out = null;
    boolean isPinned = true;
    boolean hasSessions = true;
    long lastAccess = Long.MAX_VALUE;
    Iterator<Map.Entry<String, Future<CachedTree>>> iter = _treeCache.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, Future<CachedTree>> entry = iter.next();
      try {
        CachedTree tree = entry.getValue().get(0L, TimeUnit.SECONDS);
        boolean isCandidate;
        if (isPinned != tree.isPinned()) {
          isCandidate = isPinned;
        } else if (hasSessions != tree.hasSessions()) {
          isCandidate = hasSessions;
        } else {
          isCandidate = lastAccess >= tree.getLastAccess();
        }
        if (isCandidate) {
          out = entry.getKey();
          isPinned = tree.isPinned();
          hasSessions = tree.hasSessions();
          lastAccess = tree.getLastAccess();
        }
      } catch (TimeoutException x) {
        continue;
      } catch (Exception x) {
        iter.remove();
      }
    }
    return out;
  }
  
  
// -- Decorated ITree that keeps track of its cache status : -------------------
  
  final private static class CachedTree implements ITree {
    
    // -- Private parts :
    
    private final ITree _tree;
    
    private final ConcurrentHashMap<String, Boolean> _sessions = new ConcurrentHashMap<String, Boolean>();
    private volatile long _lastAccess;
    private volatile boolean _isPinned;
    
    // -- Constructor :
    
    CachedTree(ITree tree) {
      _tree = tree;
      _lastAccess = System.currentTimeMillis();
    }
    
    // -- Access to cache info :
    
    long getLastAccess() {
      return _lastAccess;
    }
    
    void updateLastAccess() {
      _lastAccess = System.currentTimeMillis();
    }
    
    void addSession(String sessionId) {
      _sessions.putIfAbsent(sessionId, Boolean.TRUE);
      updateLastAccess();
    }
    
    void removeSession(String sessionId) {
      _sessions.remove(sessionId);
    }

    boolean containsSession(String sessionId) {
      return _sessions.containsKey(sessionId);
    }

    boolean hasSessions() {
      return !_sessions.isEmpty();
    }
    
    void pin(boolean isPinned) {
      _isPinned = isPinned;
    }
    
    boolean isPinned() {
      return _isPinned;
    }
    
    void closeTree() {
      try {
        _tree.close();
      } catch (Throwable x) {}
    }
    
    // -- Non-trivially overridden methods :

    public void close() throws IOException {
    }
    
    public void finalize() throws Throwable {
      try {
        _tree.close();
      } catch (Throwable x) {}
      super.finalize();
    }
    
    // -- Forward calls to underlying tree after updating last access time :

    public String name() {
      updateLastAccess();
      return _tree.name();
    }

    public String storeName() {
      updateLastAccess();
      return _tree.storeName();
    }

    public String storeType() {
      updateLastAccess();
      return _tree.storeType();
    }

    public IManagedObject find(String string) throws IllegalArgumentException {
      updateLastAccess();
      return _tree.find(string);
    }

    public ITree findTree(String string) throws IllegalArgumentException {
      updateLastAccess();
      return _tree.findTree(string);
    }

    public void cd(String string) throws IllegalArgumentException {
      updateLastAccess();
      _tree.cd(string);
    }

    public String pwd() {
      updateLastAccess();
      return _tree.pwd();
    }

    public void ls() throws IllegalArgumentException {
      updateLastAccess();
      _tree.ls();
    }

    public void ls(String string) throws IllegalArgumentException {
      updateLastAccess();
      _tree.ls(string);
    }

    public void ls(String string, boolean bln) throws IllegalArgumentException {
      updateLastAccess();
      _tree.ls(string, bln);
    }

    public void ls(String string, boolean bln, OutputStream out) throws IllegalArgumentException {
      updateLastAccess();
      _tree.ls(string, bln, out);
    }

    public String[] listObjectNames() throws IllegalArgumentException {
      updateLastAccess();
      return _tree.listObjectNames();
    }

    public String[] listObjectNames(String string) throws IllegalArgumentException {
      updateLastAccess();
      return _tree.listObjectNames(string);
    }

    public String[] listObjectNames(String string, boolean bln) throws IllegalArgumentException {
      updateLastAccess();
      return _tree.listObjectNames(string, bln);
    }

    public String[] listObjectTypes() throws IllegalArgumentException {
      updateLastAccess();
      return _tree.listObjectTypes();
    }

    public String[] listObjectTypes(String string) throws IllegalArgumentException {
      updateLastAccess();
      return _tree.listObjectTypes(string);
    }

    public String[] listObjectTypes(String string, boolean bln) throws IllegalArgumentException {
      updateLastAccess();
      return _tree.listObjectTypes(string, bln);
    }

    public void mkdir(String string) throws IllegalArgumentException {
      updateLastAccess();
      _tree.mkdir(string);
    }

    public void mkdirs(String string) throws IllegalArgumentException {
      updateLastAccess();
      _tree.mkdirs(string);
    }

    public void rmdir(String string) throws IllegalArgumentException {
      updateLastAccess();
      _tree.rmdir(string);
    }

    public void rm(String string) throws IllegalArgumentException {
      updateLastAccess();
      _tree.rm(string);
    }

    public String findPath(IManagedObject imo) throws IllegalArgumentException {
      updateLastAccess();
      return _tree.findPath(imo);
    }

    public void mv(String string, String string1) throws IllegalArgumentException {
      updateLastAccess();
      _tree.mv(string, string1);
    }

    public void commit() throws IOException {
      updateLastAccess();
      _tree.commit();
    }

    public void setOverwrite() {
      updateLastAccess();
      _tree.setOverwrite();
    }

    public void setOverwrite(boolean bln) {
      updateLastAccess();
      _tree.setOverwrite(bln);
    }

    public void cp(String string, String string1) throws IllegalArgumentException {
      updateLastAccess();
      _tree.cp(string, string1);
    }

    public void cp(String string, String string1, boolean bln) throws IllegalArgumentException {
      updateLastAccess();
      _tree.cp(string, string1, bln);
    }

    public void symlink(String string, String string1) throws IllegalArgumentException {
      updateLastAccess();
      _tree.symlink(string, string1);
    }

    public void mount(String string, ITree itree, String string1) throws IllegalArgumentException {
      updateLastAccess();
      _tree.mount(string, itree, string1);
    }

    public void unmount(String string) throws IllegalArgumentException {
      updateLastAccess();
      _tree.unmount(string);
    }

    public boolean isReadOnly() {
      updateLastAccess();
      return _tree.isReadOnly();
    }
    
  }
  
// -----------------------------------------------------------------------------
}
