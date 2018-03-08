/*
 * TreeObjectAlreadyExistException.java
 *
 * Created on June 1, 2003, 4:31 PM
 */

package hep.aida.ref.tree;

/**
 *
 * @author  serbo
 */
public class TreeObjectAlreadyExistException extends IllegalArgumentException {
    
    /** Creates a new instance of TreeObjectAlreadyExistException */
    public TreeObjectAlreadyExistException() {
        super();
    }
    
    public TreeObjectAlreadyExistException(String text) {
        super(text);
    }
    
}
