package hep.aida.ref.tree;

import hep.aida.IAnalysisFactory;
import hep.aida.ITree;
import hep.aida.ITreeFactory;

import java.io.IOException;

/**
 *
 * @author  The AIDA team @ SLAC.
 *
 * @version $Id: TreeFactory.java 8584 2006-08-10 23:06:37Z duns $
 */
public class TreeFactory implements ITreeFactory {
    
    private IAnalysisFactory analysisFactory;

    /**
     * The default constructor.
     *
     */
    public TreeFactory(IAnalysisFactory analysisFactory) {
        this.analysisFactory = analysisFactory;
    }    

    /**
     * Creates a new tree and associates it with a store.
     * The store is assumed to be read/write.
     * The store will be created if it does not exist.
     *                  in memory and therefore will not be associated with a file.
     *
     */
    public ITree create() {
        return createTree();
    }

    /**
     * Creates a new tree and associates it with a store.
     * The store is assumed to be read/write.
     * The store will be created if it does not exist.
     * @param storeName The name of the store, if empty (""), the tree is created
     *                  in memory and therefore will not be associated with a file.
     * @throws IOException if the store already exists
     * @throws IllegalArgumentException
     *
     */
    public ITree create(String storeName) throws IllegalArgumentException, IOException {
        return create(storeName, null);
    }
    
    /**
     * Creates a new tree and associates it with a store.
     * The store is assumed to be read/write.
     * The store will be created if it does not exist.
     * @param storeName The name of the store, if empty (""), the tree is created
     *                  in memory and therefore will not be associated with a file.
     * @param storeType Implementation specific string, may control store type
     * @throws IOException if the store already exists
     * @throws IllegalArgumentException
     *
     */
    public ITree create(String storeName, String storeType) throws IllegalArgumentException, IOException {
        return createTree(storeName, storeType, false, false, null, false);
    }

    /**
     * Creates a new tree and associates it with a store.
     * The store is assumed to be read/write.
     * The store will be created if it does not exist.
     * @param storeName The name of the store, if empty (""), the tree is created
     *                  in memory and therefore will not be associated with a file.
     * @param storeType Implementation specific string, may control store type
     * @param readOnly If true the store is opened readonly, an exception if it does not exist
     * @throws IOException if the store already exists
     * @throws IllegalArgumentException
     *
     */
    public ITree create(String storeName, String storeType, boolean readOnly) throws IllegalArgumentException, IOException {
        return create(storeName, storeType, readOnly, false);
    }
    
    /**
     * Creates a new tree and associates it with a store.
     * The store is assumed to be read/write.
     * The store will be created if it does not exist.
     * @param storeName The name of the store, if empty (""), the tree is created
     *                  in memory and therefore will not be associated with a file.
     * @param storeType Implementation specific string, may control store type
     * @param readOnly If true the store is opened readonly, an exception if it does not exist
     * @param createNew If false the file must exist, if true the file will be created
     * @throws IOException if the store already exists
     * @throws IllegalArgumentException
     *
     */
    public ITree create(String storeName, String storeType, boolean readOnly, boolean createNew) throws IllegalArgumentException, IOException {
        return create(storeName, storeType, readOnly, createNew, null);
    }

    /**
     * Creates a new tree and associates it with a store.
     * The store is assumed to be read/write.
     * The store will be created if it does not exist.
     * @param storeName The name of the store, if empty (""), the tree is created
     *                  in memory and therefore will not be associated with a file.
     * @param storeType Implementation specific string, may control store type
     * @param readOnly If true the store is opened readonly, an exception if it does not exist
     * @param createNew If false the file must exist, if true the file will be created
     * @param options Other options, currently are not specified
     * @throws IOException if the store already exists
     * @throws IllegalArgumentException
     *
     */
    public ITree create(String storeName, String storeType, boolean readOnly, boolean createNew, String options) throws IllegalArgumentException, IOException {
        return createTree(storeName, storeType, readOnly, createNew, options, true);
    }

    
    
    
    
    

    /**
     * Creates a new tree and associates it with a store.
     * <p>
     * The definition of the various modes than can be specified are:
     * <ul>
     * <li>AUTO default case: create of store does not exist 
     * <li>CREATE: Create a new file (throws an exception if already exists) 
     * <li>RECREATE: overwrite file if existing 
     * <li>READONLY: open in read only mode 
     * <li>UPDATE: read/write mode, overwriting modified objects. 
     * </ul>
     * @param storeName The name of the store, if empty (""), the tree is created
     *                  in memory and therefore will not be associated with a file.
     * @param storeType Implementation specific string, may control store type
     * @param mode One of AUTO, CREATE, RECREATE, READONLY, UPDATE.
     * @throws IOException if the store already exists
     * @throws IllegalArgumentException
     */
    public ITree createTree() {
        return createTree(null);
    }
    public ITree createTree(String options) {
        try {
            return createTree(null, null, options);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }    
    
    public ITree createTree(String storeName, String storeType) throws IllegalArgumentException, IOException {
        return createTree(storeName, storeType, ITreeFactory.AUTO);
    }

    public ITree createTree(String storeName, String storeType, String options) throws IllegalArgumentException, IOException {
        return createTree(storeName, storeType, ITreeFactory.AUTO, options);
    }

    public ITree createTree(String storeName, String storeType, int mode) throws IllegalArgumentException, IOException {
        return createTree(storeName, storeType, mode, null);
    }
    
    public ITree createTree(String storeName, String storeType, int mode, String options) throws IllegalArgumentException, IOException {
        return createNamedTree(null, storeName, storeType, mode, options);
    }

    public ITree createNamedTree(String name, String storeName, String storeType) throws IllegalArgumentException, IOException {
        return createNamedTree(name, storeName, storeType, ITreeFactory.AUTO);
    }

    public ITree createNamedTree(String name, String storeName, String storeType, int mode) throws IllegalArgumentException, IOException {
        return createNamedTree(name, storeName, storeType, mode, null);
    }

    public ITree createNamedTree(String name, String storeName, String storeType, int mode, String options) throws IllegalArgumentException, IOException {
        return new Tree(analysisFactory, name, storeName, storeType, mode, options);
    }

    protected ITree createTree(String storeName, String storeType, boolean readOnly, boolean createNew, String options, boolean readOnlyUserDefined) throws IllegalArgumentException, IOException {
        Tree tree = new Tree(analysisFactory);
        tree.init(storeName, readOnly, createNew, storeType, options, readOnlyUserDefined);
        return tree;
    }


    
    
}
