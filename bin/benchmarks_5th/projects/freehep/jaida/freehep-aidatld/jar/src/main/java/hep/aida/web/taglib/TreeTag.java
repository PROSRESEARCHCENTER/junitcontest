package hep.aida.web.taglib;

/**
 * A top level tag which provides read-only access to any AIDA ITree.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface TreeTag {

    /**
     * Set the AIDA store name. This is a required attribute. For example, one
     * might access a ROOT file using the store name
     * <code>root://sldrh2.slac.stanford.edu/pawdemo.root</code>, the store
     * type <code>root</code> (see {@link #setStoreType(String)}) and the
     * store options <code>scheme=anonymous</code> (see
     * {@link #setOptions(String)}).
     * 
     * @param storeName
     *            the AIDA store name
     * 
     * @see #setStoreType(String)
     * @see #setOptions(String)
     */
    public void setStoreName(String storeName);

    /**
     * Set the AIDA store type. This is an optional attribute. For example, one
     * might access a ROOT file using the store name
     * <code>root://sldrh2.slac.stanford.edu/pawdemo.root</code> (see
     * {@link #setStoreName(String)}), the store type <code>root</code>, and
     * the store options <code>scheme=anonymous</code> (see
     * {@link #setOptions(String)}).
     * 
     * @param storeType
     *            the AIDA store type
     * 
     * @see #setStoreName(String)
     * @see #setOptions(String)
     */
    public void setStoreType(String storeType);

    /**
     * Set the AIDA store options. This is an optional attribute. For example,
     * one might access a ROOT file using the store name
     * <code>root://sldrh2.slac.stanford.edu/pawdemo.root</code> (see
     * {@link #setStoreName(String)}), the store type <code>root</code> (see
     * {@link #setStoreType(String)}) and the store options
     * <code>scheme=anonymous</code>.
     * 
     * @param options The options
     * 
     * @see #setStoreName(String)
     * @see #setStoreType(String)
     */
    public void setOptions(String options);
    
}