/*
 * RmiAnnotationItem.java
 *
 * Created on October 26, 2003, 9:37 PM
 */

package hep.aida.ref.remote.rmi.data;

import java.io.Serializable;

/**
 * This class contais data for an Annotation Item
 * @author  serbo
 */
public class RmiAnnotationItem implements Serializable {
    
    static final long serialVersionUID = 8860992383615392200L;
    public String key;
    public String value;
    public boolean sticky = false;
    
    /** Creates a new instance of RmiAnnotationItem */
    public RmiAnnotationItem() {
    }
    
    public RmiAnnotationItem(String key, String value, boolean sticky) {
        this.key = key;
        this.value = value;
        this.sticky = sticky;
    }
    
}
