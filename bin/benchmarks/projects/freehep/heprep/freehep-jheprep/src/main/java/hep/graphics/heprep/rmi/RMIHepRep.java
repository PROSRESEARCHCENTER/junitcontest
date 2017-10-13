// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.rmi;

import hep.graphics.heprep.HepRepAction;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepTypeTree;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: RMIHepRep.java 8584 2006-08-10 23:06:37Z duns $
 */

public class RMIHepRep extends UnicastRemoteObject implements Remote, RMIHepRepServer {

    /**
     * Create an RMI HepRep Object/Service
     * @throws RemoteException if object/service cannot be created
     */
    public RMIHepRep() throws RemoteException {
    }

    public List getLayerOrder() throws RemoteException {
        return null;
    }

    public HepRepTypeTree getTypeTree(String name, String version) throws RemoteException {
        return null;
    }

    public HepRepInstanceTree getInstanceTreeTop(String name, String version) throws RemoteException {
        return null;
    }

    public HepRepInstanceTree getInstances(String name, String version, String[] typeNames) throws RemoteException {
        return null;
    }

    public HepRepInstanceTree getInstancesAfterAction(
                                    String name,
                                    String version,
                                    String[] typeNames,
                                    HepRepAction[] actions,
                                    boolean getPoints,
                                    boolean getDrawAtts,
                                    boolean getNonDrawAtts,
                                    String[] invertAtts) throws RemoteException {
        return null;
    }

    public String checkForException() throws RemoteException {
        return null;
    }
}
