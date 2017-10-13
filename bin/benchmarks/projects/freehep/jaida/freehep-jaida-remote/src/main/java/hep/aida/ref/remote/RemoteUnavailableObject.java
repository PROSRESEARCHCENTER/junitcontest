/*
 * RemoteUnavailableObject.java
 *
 * Created on January 31, 2005, 3:34 PM
 */

package hep.aida.ref.remote;

/**
 *
 * @author  serbo
 */
public class RemoteUnavailableObject extends RemoteManagedObject {
    
    /** Creates a new instance of RemoteUnavailableObject */
    public RemoteUnavailableObject(String name) {
        super(name);
        
        aidaType = "RemoteUnavailableObject";
        this.dataIsValid = true;
    }

    public void makeSureDataIsValid() {
        return;
    }
    
}
