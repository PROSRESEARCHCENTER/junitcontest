// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.rmi;

import hep.graphics.heprep.HepRepAction;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepTypeTree;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface of the RMI HepRep Server, which resembles HepRep
 * 
 * @author M.Donszelmann
 * @version $Id: RMIHepRepServer.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface RMIHepRepServer extends Remote {

    /**
     * Returns the list of layers.
     *
     * @return the known layer names, in the order back-to-front.
     * @throws RemoteException in case the RMI call fails
     */
    public List getLayerOrder() throws RemoteException;

    /**
     * Returns a named and versioned typetree.
     *
     * @param name name of the typetree.
     * @param version version of the typetree.
     * @return named and versioned HepRepTypeTree.
     * @throws RemoteException in case the RMI call fails
     */
    public HepRepTypeTree getTypeTree(String name, String version) throws RemoteException;

    /**
     * Returns a named and versioned instancetree.
     * FIXME: doc is incorrect here, should only return TOP of the tree. Filling
     * in should be done by getInstances calls.
     * <p>
     * This tree needs to be added to the heprep afterwards.
     *
     * @param name name of the instancetree.
     * @param version version of the instancetree.
     * @return HepRepIntanceTree
     * @throws RemoteException in case the RMI call fails
     */
    public HepRepInstanceTree getInstanceTreeTop(String name, String version) throws RemoteException;

    /**
     * Returns a named and versioned instancetree for a list of typenames.
     * <p>
     * This tree needs to be added to the heprep afterwards.
     *
     * @param name name of the instancetree.
     * @param version version of the instancetree.
     * @param typeNames a list of typenames for which we need instancetrees.
     * @return HepRepIntanceTree
     * @throws RemoteException in case the RMI call fails
     */
    public HepRepInstanceTree getInstances(String name, String version, String[] typeNames) throws RemoteException;

    /**
     * Returns a named and versioned instancetree for a list of typenames
     * after executing some action and for specific filters.
     *
     * This tree needs to be added to the heprep afterwards.
     * <p>
     * The inversion effect of invertAtts depends on the values of
     * GetDrawAtts and GetNonDrawAtts as follows:
     * <UL>
     * <LI>GetDrawAtts  GetNonDrawAtts  effect of InvertAtts
     * <LI>FALSE        FALSE           all Attributes specified will be downloaded
     * <LI>TRUE         FALSE           Draw Attributes specified will be omitted, NonDraw Attributes specified will be included
     * <LI>FALSE        TRUE            Draw Attributes specified will be included, NonDraw Attributes specified will be omitted
     * <LI>TRUE         TRUE            all Attributes specified will be omitted
     * </UL>
     *
     * @param name name of the instancetree.
     * @param version version of the instancetree.
     * @param typeNames a list of typenames for which we need instancetrees.
     * @param actions execute this list of actions before returning.
     * @param getPoints include the HepRepPoints in the instance tree.
     * @param getDrawAtts include the Draw attributes in the instance tree.
     * @param getNonDrawAtts include the Non-Draw attributes in the instance tree.
     * @param invertAtts list of attributes to be included or not depending on getDrawAtts and getNonDrawAtts.
     * @return HepRepIntanceTree
     * @throws RemoteException in case the RMI call fails
     */
    public HepRepInstanceTree getInstancesAfterAction(
                                    String name,
                                    String version,
                                    String[] typeNames,
                                    HepRepAction[] actions,
                                    boolean getPoints,
                                    boolean getDrawAtts,
                                    boolean getNonDrawAtts,
                                    String[] invertAtts) throws RemoteException ;

    /**
     * Returns last exception thrown and clears it. Useful for implementations without
     * exception handling.
     *
     * @return last exception and clears it.
     * @throws RemoteException in case the RMI call fails
     */
    public String checkForException() throws RemoteException;
}
