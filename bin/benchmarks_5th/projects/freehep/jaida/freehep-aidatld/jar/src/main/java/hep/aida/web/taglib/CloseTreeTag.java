package hep.aida.web.taglib;

/**
 * A top level tag to close an AIDA ITree.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface CloseTreeTag {

    /**
     * The name of the AIDA store to be closed.
     * This is a required attribute. 
     * 
     * @param storeName   the AIDA store name
     * 
     */
    public void setStoreName(String storeName);
    
}