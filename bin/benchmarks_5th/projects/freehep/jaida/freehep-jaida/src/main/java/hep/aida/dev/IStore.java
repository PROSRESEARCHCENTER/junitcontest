package hep.aida.dev;

import java.io.IOException;
import java.util.Map;

/**
 * An interface to be implemented by AIDA compatible Store.
 * Store is a Proxy that presents data from some resource to a Tree.
 * Store can be associated with only one Tree during its lifetime. 
 * 
 * This is an interface to a basic Store that can provide data
 * in one piece only, like XML file.
 *
 * @author tonyj 
 * @version $Id: IStore.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface IStore
{
    /**
     * Returns true if the store only supports reading.
     * If true, user can not change information in the Store and
     * "commit()" method throws IOException if called.
     */
    boolean isReadOnly();

    /**
     * Populate AIDA Tree: create appropriate AIDA objects in the Tree
     * and fill them with data from the Store.
     * This method is called only once, during Tree-Store association, to
     * populate the Tree. 
     * Tree relies on IStore calling IDevTree.hasBeenFilled(String path) method,
     * to let Tree know that a particular folder has been filled.
     *
     * @throws          IOException If there are problems reading from the Store
     */
    void read(IDevTree tree, Map options, boolean readOnly, boolean createNew) throws IOException;

    /**
     * Copy data from Tree to the Store.
     *
     * @throws          IOException If there are problems writing to the Store or the Store is Read-Only.
     */
    void commit(IDevTree tree, Map options) throws IOException; 

    /**
     * Close Store and free all resources associated with it.
     * Should be called from ITree.close() method only.
     * After the Store is closed, the Tree associated with it becomes unusable.
     */
    void close() throws IOException;
}
