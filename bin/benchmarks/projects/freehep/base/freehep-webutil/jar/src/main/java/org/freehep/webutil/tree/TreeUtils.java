package org.freehep.webutil.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class TreeUtils {
    
    public static TreeNode[] nodesForPath(String path, TreeNode root, boolean recursive) {
        TreeNode node = findNode(root, path);
        if (node.isLeaf())
            return new TreeNode[] { node };
        return getAllChildren(node, recursive);
    }
    
    private static TreeNode[] getAllChildren(TreeNode node, boolean recursive) {
        List allChildren = new ArrayList();
        addChildrenToList(node, allChildren, recursive);
        TreeNode[] nodes = new TreeNode[allChildren.size()];
        for ( int i = 0; i < allChildren.size(); i++ ) {
            TreeNode child = (TreeNode) allChildren.get(i);
            nodes[i] = child;
        }
        return nodes;        
    }
    
    private static void addChildrenToList(TreeNode node, List list, boolean recursive) {
        List children = node.children();
        for ( int i = 0; i < children.size(); i++ ) {
            TreeNode child = (TreeNode) children.get(i);
            if ( child.isLeaf() )
                list.add(child);
            else if ( recursive )
                addChildrenToList(child, list, true);
        }
    }
    
    public static TreeNode findNode(TreeNode root, String path) {
        StringTokenizer st = new StringTokenizer(path, "/");
        TreeNode parent = root;
        while( st.hasMoreTokens() ) {
            String name = (String) st.nextToken();
            TreeNode child = findChild(parent, name);
            if ( child == null )
                    return null;
            parent = child;
        }
        return parent;
    }

    public static TreeNode findChild(TreeNode node, String name) {
        if (node.children() == null) return null;
        for (Iterator i = node.children().iterator(); i.hasNext(); ) {
            TreeNode child = (TreeNode) i.next();
            if (name.equals(child.getLabel())) return child;
        }
        return null;
    }
    
    public static void expandNode(TreeNode root, String path) {
        StringTokenizer st = new StringTokenizer(path, "/");
        TreeNode parent = root;
        while( st.hasMoreTokens() ) {
            String name = (String) st.nextToken();
            TreeNode child = findChild(parent, name);
            if ( child == null )
                    return;
            if ( child instanceof DefaultTreeNode )
                ((DefaultTreeNode) child).setIsExpanded(true);
            parent = child;
        }
    }
    
    public static boolean nodeHasInnerLeaves(TreeNode node) {
        return nodeHasInnerLeaves(node, true);
    }
    
    public static boolean nodeHasInnerLeaves(TreeNode node, boolean recursive) {
        if ( node.isLeaf() )
            throw new RuntimeException("Improper invocation of this method. Node must be a folder.");
        List children = node.children();
        if ( children.size() == 0 )
            return false;
        for ( int i = 0; i < children.size(); i++ ) {
            TreeNode child = (TreeNode) children.get(i);
            if ( child.isLeaf() )
                return true;
            if (recursive) {
                boolean hasInnerLeaves = nodeHasInnerLeaves(child);
                if ( hasInnerLeaves )
                    return true;
            }
        }
        return false;
    }
    
}