/*
 * RmiTestServer.java
 *
 * Created on December 7, 2005, 2:30 PM
 */

package hep.aida.ref.remote.test.rmiConnection;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface RmiTestServer extends Remote {
    
    /** 
     * Get object with array of length=size
     */
    RmiSerializableObject getObject(int size) throws RemoteException;
    
    Object getRMIObject(int i) throws RemoteException;
    
    void setObject(RmiSerializableObject obj) throws RemoteException;
    
    void close() throws RemoteException;
}
