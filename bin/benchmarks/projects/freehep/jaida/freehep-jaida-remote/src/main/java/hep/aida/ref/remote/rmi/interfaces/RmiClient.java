/*
 * RmiClient.java
 *
 * Created on October 15, 2003, 12:50 AM
 */

package hep.aida.ref.remote.rmi.interfaces;

import hep.aida.ref.remote.interfaces.AidaUpdateEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author  serbo
 */

public interface RmiClient extends Remote {
    
    String getBindName() throws RemoteException;
    
    void stateChanged(AidaUpdateEvent[] events) throws RemoteException;

}
