package hep.aida.web.taglib;

import hep.aida.IManagedObject;
import hep.aida.ITuple;
import org.freehep.webutil.tree.DefaultTreeNode;
import org.freehep.webutil.tree.Icon;
import org.freehep.webutil.tree.Tree;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class AidaTupleTreeNode extends AidaDefaultTreeNode {
    
    public AidaTupleTreeNode(ITuple tuple) {
        this(tuple, null);
    }
    
    public AidaTupleTreeNode(ITuple tuple, DefaultTreeNode parent) {
        super(((IManagedObject) tuple).name(), "ituple", parent);
        icon = null;
        href = Tree.noHref;
        
        // fill columns and sub-tuples
        String[] names = tuple.columnNames();
        AidaDefaultTreeNode child = null;
        for ( int i = 0; i < names.length; i++ ) {
            String colName = names[i];
            if ( tuple.columnType(i) == ITuple.class) {
                child = new AidaTupleTreeNode(tuple.findTuple(i), this);
            } else {
                child = new AidaDefaultTreeNode(colName, "ituplecolumn", this);
                if (parent != null && parent instanceof AidaTupleTreeNode) 
                    child.setHref(Tree.noHref);
            }
        }
    }
    
    
}
