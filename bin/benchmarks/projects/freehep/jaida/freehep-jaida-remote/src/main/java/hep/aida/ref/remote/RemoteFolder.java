/*
 * RemoteFolder.java
 *
 * Created on February 5, 2005, 7:11 PM
 */

package hep.aida.ref.remote;

/**
 *
 * @author  serbo
 */
public class RemoteFolder extends RemoteManagedObject {
    
    /** Creates a new instance of RemoteFolder */
    public RemoteFolder(String name) {
        super(name);
        aidaType = "dir";
        dataIsValid = true;
    }
    
    // Folder is controlled by Tree and Store, do nothing here
    public void makeSureDataIsValid() {
        return;
    }

    // Folder is always valid, do nothing ere
    public void setDataValid(boolean dataIsValid) {
        return; 
    }
    
}
