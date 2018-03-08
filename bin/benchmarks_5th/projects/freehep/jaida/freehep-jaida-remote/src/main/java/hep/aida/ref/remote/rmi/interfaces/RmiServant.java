/*
 * RmiServant.java
 *
 * Created on October 15, 2003, 12:49 AM
 */

package hep.aida.ref.remote.rmi.interfaces;

import hep.aida.ref.remote.interfaces.AidaUpdateEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author  serbo
 */

public interface RmiServant extends Remote {
    
    java.lang.Object find(String path) throws RemoteException;
    
    String[] listObjectNames(String path, boolean recursive) throws RemoteException;
    
    String[] listObjectTypes(String path, boolean recursive) throws RemoteException;
    
    void setValid(String[] nodePaths) throws RemoteException;
    
    AidaUpdateEvent[] updates() throws RemoteException;
    
}
