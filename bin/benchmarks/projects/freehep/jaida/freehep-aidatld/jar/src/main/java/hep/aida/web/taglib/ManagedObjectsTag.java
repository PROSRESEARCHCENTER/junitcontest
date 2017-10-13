package hep.aida.web.taglib;

/**
 * A tag to retrieve objects from an AIDA store.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface ManagedObjectsTag {

    /**
     * The name of the output variable containing the
     * List of ManagedObjects matching the provided path.
     * 
     * @param var  The name of the output variable.
     * 
     * @see #setScope(String)
     * 
     */
    public void setVar(String var);

    /**
     * Set the scope of the output variable. This is an optional attribute, and
     * can be one of <code>page</code>,<code>request</code>,
     * <code>session</code> or <code>application</code>. The default is
     * <code>page</code>.
     * 
     * @param scope scope of the output variable
     * 
     * @see #setVar(String)
     */
    public void setScope(String scope);

    /**
     * The name of the AIDA store from which to get the objects.
     * This is a required attribute. 
     * 
     * @param storeName   the AIDA store name
     * 
     */
    public void setStoreName(String storeName);
    
    /**
     * The path of the objects to be retrieved.
     * This is a required attribute. 
     *
     * @param path The object's path.
     *
     */
    public void setPath(String path);
    
}