package hep.aida.web.taglib;

import hep.aida.ITree;

/**
 * A top level tag to graphically display an AIDA ITree.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface DisplayTreeTag {

    /**
     * Set name of the AIDA store to be displayed.
     * 
     * @param storeName The name of the AIDA store.
     * 
     */
    public void setStoreName(String storeName);
    
    /**
     * Set the href link to which the leaf nodes should point to.
     * The wildcard "%p" will be replaced with the node's path while
     * the wildcard "%l" will be replaced with the node's label.
     *
     * @param leafHref The leaf href.
     *
     */
    public void setLeafHref(String leafHref);
    
    /**
     * Set the href link to which the folder nodes should point to.
     * The wildcard "%p" will be replaced with the node's path while
     * the wildcard "%l" will be replaced with the node's label.
     *
     * @param folderHref The folder href.
     *
     */
    public void setFolderHref(String folderHref);
    
    /**
     * Flag to control the visibility of the root node.
     *
     * @param isRootVisible If <code>true</code> the root node appears in the tree.
     *
     */
    public void setRootVisible(boolean isRootVisible);
    
    /**
     * Set the label to display for the root node, default is "/".
     *
     * @param rootLabel Label to display for the root node (it it is visible)
     *
     */
    public void setRootLabel(String rootLabel);
    
    /**
     * If set to <code>true</code> next to each folder the number of children
     * will be written.
     *
     * @param showItemCount Set to <code>true</code> to view the number of children in a folder.
     *
     */
    public void setShowItemCount(boolean showItemCount);
    
    /**
     * If set to <code>true</code> folderHref is added only to the folders with
     * that have direct leaf nodes (not recursive!)
     *
     * @param showFolderHrefForNodesWithLeavesOnly Set to <code>true</code> 
     *
     */
    public void setShowFolderHrefForNodesWithLeavesOnly(boolean showFolderHrefForNodesWithLeavesOnly);
}