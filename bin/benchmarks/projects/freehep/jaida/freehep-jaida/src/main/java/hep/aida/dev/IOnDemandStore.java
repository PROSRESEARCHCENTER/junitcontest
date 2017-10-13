package hep.aida.dev;

import java.io.IOException;

/**
 * An interface to the store that can provide fine-grained data.
 */

public interface IOnDemandStore extends IStore
{
    /** 
     * Populate folder or create AIDA object in the tree.
     * If some folders along the path are missing, IOnDemandStore 
     * must create them using ITree "mkdirs(path)" method.
     * Tree relies on Store calling IDevTree.hasBeenFilled(String path) method,
     * to let Tree know that a particular folder has been filled.
     *
     * @param path      The path to the diretory which is to be filled.
     * @throws          IllegalArgumentException If the path does not exist, or if it is not a directory.
     * @throws          IOException If there are problems reading from the Store
     */
    void read(IDevTree tree, String path) throws IllegalArgumentException, IOException;

}

