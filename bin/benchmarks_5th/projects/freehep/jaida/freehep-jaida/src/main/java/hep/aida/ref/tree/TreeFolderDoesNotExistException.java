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
public class TreeFolderDoesNotExistException extends IllegalArgumentException {
    
    /** Creates a new instance of TreeObjectAlreadyExistException */
    public TreeFolderDoesNotExistException() {
        super();
    }
    
    public TreeFolderDoesNotExistException(String text) {
        super(text);
    }
    
}
