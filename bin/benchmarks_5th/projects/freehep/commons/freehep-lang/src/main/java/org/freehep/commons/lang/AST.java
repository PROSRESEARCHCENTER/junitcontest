package org.freehep.commons.lang;

import java.util.HashMap;

/**
 *
 * @author bvan
 */
public class AST {
    
    public static interface Visitor {
        public boolean visit(Node n);
    }
    
    private Node root;
    
    public AST() { this( new Node() ); }
    public AST(Node n) { root = n; }
    
    public Node getRoot() {return root; }
    public void setRoot(Node root) { this.root = root; }
    public String toString() { return root.toString(); }

    public static class Node {
        private Object value;
        private int type; /* usually from sym */
        /* Useful for attaching metadata to the nodes */
        private HashMap<String,Object> meta = new HashMap<String,Object>();
        boolean visited;
        private Node left, right;

        public Node() { }

        public Node(Object value) { this( value, -1 ); }

        public Node(Object value, int type) { this( value, type, false ); }

        public Node(Object value, int type, Node left) {
            this( value, type, false, left, null );
        }

        public Node(Object value, int type, Node left, Node right) {
            this( value, type, false, left, right );
        }

        public Node(Object value, int type, boolean visited, Node left) {
            this( value, type, visited, left, null );
        }

        public Node(Object value, int type, boolean visited) {
            this( value, type, visited, null, null );
        }

        public Node(Object value, int type, boolean visited, 
                Node left,
                Node right) {
            this.value = value;
            this.type = type;
            this.visited = visited;
            this.left = left;
            this.right = right;
        }

        public Node getLeft() { return this.left; }
        public Node getRight() { return this.right; }
        public int getType() { return this.type; }
        public Object getValue() { return this.value; }
        public boolean isValueNode(){ return this.left == null && this.right == null; }
        public boolean getVisited() { return this.visited; }
        public Object getMetadata(String key) { return meta.get( key ); }

        public void setLeft(Node left) { this.left = left; }
        public void setRight(Node right) { this.right = right; }
        public void setType(int type) { this.type = type; }
        public void setValue(Object value) { this.value = value; }
        public void setVisited(boolean visited) { this.visited = visited; }
        public void setMetadata(String key, Object val) { 
            meta.put( key, val );
        }

        public void resetVisited() { resetVisited( false ); }
        public void resetVisited(boolean visited) { 
            setVisited( false );
            if ( left != null )
                left.resetVisited( visited );
            if ( right != null )
                right.resetVisited( visited );
        }
        
        public boolean accept(Visitor visitor){
            return visitor.visit( this );
        }

        @Override
        public String toString() {
            String str = " " + value.toString() + " ";
            if ( left != null )
                str = left.toString() + str;
            if ( right != null )
                str += right.toString();
            return isValueNode() ? str : "( " + str + " )";
        }
    }
}
