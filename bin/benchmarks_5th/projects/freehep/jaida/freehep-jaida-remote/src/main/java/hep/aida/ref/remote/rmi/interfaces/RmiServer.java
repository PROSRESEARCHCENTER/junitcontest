/*
 * RmiServer.java
 *
 * Created on October 15, 2003, 12:48 AM
 */

package hep.aida.ref.remote.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author  serbo
 */

public interface RmiServer extends Remote {
    
    String getBindName() throws RemoteException;
    
    RmiServant connectDuplex(RmiClient client) throws RemoteException;
    
    RmiServant connectNonDuplex(String clientID) throws RemoteException;
    
    boolean disconnectDuplex(RmiClient client) throws RemoteException;
    
    boolean disconnectNonDuplex(String clientID) throws RemoteException;
    
    boolean supportDuplexMode() throws RemoteException;
    
    String treeName() throws RemoteException;
    
}
