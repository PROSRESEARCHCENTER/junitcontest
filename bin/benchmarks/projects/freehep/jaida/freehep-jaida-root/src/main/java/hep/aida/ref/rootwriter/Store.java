package hep.aida.ref.rootwriter;

import hep.aida.IManagedObject;
import hep.aida.ITree;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author onoprien
 */
public abstract class Store {
  
// -- Private parts : ----------------------------------------------------------

  protected HashSet<String> _filter;
  
  
// -- Setters : ----------------------------------------------------------------
  
  /**
   * Sets type filtering.
   * Only <tt>IManagedObject</tt>s whose <tt>type()</tt> method returns one of the specified types will be saved.
   */
  public void setFilter(String... types) {
    if (types == null || types.length == 0) {
      _filter = null;
    } else {
      _filter = new HashSet<String>(Arrays.asList(types));
    }
  }
  
  /**
   * Sets type filtering.
   * Only <tt>IManagedObject</tt>s whose <tt>type()</tt> method returns one of the specified types will be saved.
   */
  public void setFilter(Collection<String> types) {
    if (types == null || types.isEmpty()) {
      _filter = null;
    } else {
      _filter = new HashSet<String>(types);
    }
  }

  
// -- Operations : -------------------------------------------------------------
  
  /**
   * Opens this store.
   * This method should be called before any other operations are performed on this store.
   * The current directory is set to the root of the store.
   * 
   * @throws IOException if IO errors occur while opening the store.
   */
  abstract public void open() throws IOException;

  /**
   * Creates a subdirectory in this store.
   * The specified path is considered absolute if it starts with '/'. Otherwise, it is
   * treated as being relative to the current directory.
   * 
   * @param path
   * @throws IOException if IO errors occur while creating the directory.
   * @throws IllegalArgumentException if an existing object other than directory
   *         corresponds to any part of the specified path.
   */
  abstract public void mkdir(String path) throws IOException;
  
  /**
   * Changes current directory.
   * The specified path is considered absolute if it starts with '/'. Otherwise, it is
   * treated as being relative to the current directory.
   * 
   * @param path
   * @throws IOException 
   * @throws IllegalArgumentException if the specified path does not correspond to an existing directory.
   */
  abstract public void cd(String path) throws IOException;
   
  /**
   * Adds objects from the specified tree to this store.
   * The root of the tree is mapped to the current directory.
   * 
   * @throws IOException if IO errors occur while adding the tree.
   * @throws IllegalArgumentException if the tree contains objects that cannot be handled by this
   *         store, or if there are conflicts with existing paths.
   */
  public void add(ITree tree) throws IOException {
    String[] paths = tree.listObjectNames("/", true);
    for (String p : paths) {
      if (!p.endsWith("/")) {
        IManagedObject item = tree.find(p);
        if (_filter == null || _filter.contains(item.type())) {
          add(item, p.substring(p.startsWith("/") ? 1 : 0, p.lastIndexOf('/')+1));
        }
      }
    }
  }
  
  /**
   * Adds objects from the specified tree to this store.
   * The root of the tree is mapped to the specified path.
   * 
   * @throws IOException if IO errors occur while adding the tree.
   * @throws IllegalArgumentException if the tree contains objects that cannot be handled by this
   *         store, or if there are conflicts with existing paths.
   */
  public void add(ITree tree, String path) throws IOException {
    if (!path.endsWith("/")) path = path + "/";
    String[] paths = tree.listObjectNames("/", true);
    for (String p : paths) {
      if (!p.endsWith("/")) {
        IManagedObject item = tree.find(p);
        if (_filter == null || _filter.contains(item.type())) {
          add(item, path + p.substring(p.startsWith("/") ? 1 : 0, p.lastIndexOf('/')+1));
        }
      }
    }
  }
  
  /**
   * Adds an object to the current directory in this store.
   * 
   * @param object
   * @throws IOException if IO errors occur while adding the object.
   * @throws IllegalArgumentException if the specified object is of a king that is not supported by this store.
   */
  abstract public void add(Object object) throws IOException;
  
  /**
   * Adds an object to the specified directory in this store.
   * If the directory does not exist, it is created.
   * 
   * @param object
   * @param path
   * @throws IOException if IO errors occur while adding the object.
   * @throws IllegalArgumentException if the specified object is of a king that is 
   *         not supported by this store, or if the path is illegal.
   */
  abstract public void add(Object object, String path) throws IOException;
  
  /**
   * Saves all added objects to the underlying storage and closes the store.
   * 
   * @throws IOException if IO errors occur while saving objects and closing the store.
   */
  abstract public void close() throws IOException;
  
}
