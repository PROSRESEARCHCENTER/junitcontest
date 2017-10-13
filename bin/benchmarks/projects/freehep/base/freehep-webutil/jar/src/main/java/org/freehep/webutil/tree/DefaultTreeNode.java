package org.freehep.webutil.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class DefaultTreeNode implements TreeNode, Serializable {
    protected String href = null;
    protected String target = null;
    private List children;
    private String name;
    private boolean isExpanded;
    private DefaultTreeNode parent;
    private String title = null;
    
    public DefaultTreeNode(String name) {
        this(name,null);
    }
    
    public DefaultTreeNode(String name, DefaultTreeNode parent) {
        this.name =  name;
        this.parent = parent;
        if ( parent != null ) {
            parent.add(this);
        }
    }
    
    public void add(TreeNode child) {
        if (children == null) children = new ArrayList();
        ( (DefaultTreeNode) child).setParent(this);
        children.add(child);
    }
    
    public boolean isLeaf() {
        return children == null;
    }
    
    public String getTarget() {
        return target;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getTitle() {
        return title;
    }
    
    public boolean isExpanded() {
        return isExpanded;
    }
        
    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
    

    public String getLabel() {
        return name;
    }
        
    public void setLabel(String name) {
        this.name = name;
    }
    
    public Icon getIcon() {
        return null;
    }
    
    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public List children() {
        return children != null ? children : Collections.EMPTY_LIST;
    }
    
    void setParent(DefaultTreeNode parent) {
        this.parent = parent;
    }
    
    public DefaultTreeNode parent() {
        return parent;
    }
    
    public String getPath() {
        if ( parent == null ) return "";
        return parent().getPath()+"/"+getLabel();
    }
    
    public DefaultTreeNode getRoot() {
        if ( parent() != null )
            return parent.getRoot();
        return this;
    }
    
    public DefaultTreeNode findNode(String path, boolean createMissing) {
        StringTokenizer st = new StringTokenizer(path, "/");
        DefaultTreeNode pr = getRoot();
        while( st.hasMoreTokens() ) {
            String name = (String) st.nextToken();
            DefaultTreeNode child = (DefaultTreeNode)TreeUtils.findChild(pr, name);
            if ( child == null ) {
                if ( createMissing ) {
                    child = new DefaultTreeNode(name);
                    pr.add(child);
                } else
                    return null;
            }
            pr = child;
        }
        return pr;
    }
    
    public void addNodeAtPath(TreeNode node, String path) {
        DefaultTreeNode parent = getRoot().findNode(path,true);
        parent.add(node);
    }
    
    public void createNodeAtPath(String path) {
        String name = getName(path);
        DefaultTreeNode child = new DefaultTreeNode(name);
        addNodeAtPath(child,getParentPath(path));
    }

    private String getName(String path) {
        if ( path.indexOf("/") != -1 )
            return path.substring( path.lastIndexOf("/") +1 );
        else 
            return path;
    }

    private String getParentPath(String path) {
        if ( path.indexOf("/") != -1 )
            return path.substring(0, path.lastIndexOf("/"));
        else 
            return "";
    }

    public String processHref(String href) {
        return href;
    }
    
}
