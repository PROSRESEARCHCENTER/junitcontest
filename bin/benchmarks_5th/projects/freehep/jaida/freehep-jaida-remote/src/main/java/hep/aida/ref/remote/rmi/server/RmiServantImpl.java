/*
 * RmiClientImpl.java
 *
 * Created on October 15, 2003, 7:02 PM
 */

package hep.aida.ref.remote.rmi.server;

import hep.aida.IManagedObject;
import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaTreeServant;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;
import hep.aida.ref.remote.rmi.converters.RmiConverter;
import hep.aida.ref.remote.rmi.interfaces.RmiClient;
import hep.aida.ref.remote.rmi.interfaces.RmiServant;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

/**
 *
 * @author  serbo
 */
public class RmiServantImpl extends UnicastRemoteObject implements RmiServant, AidaTreeClient {
    
    static final long serialVersionUID = 5141620123191080485L;
    private Logger remoteLogger = Logger.getLogger("hep.aida.ref.remote");
    private RmiClient rmiClient;
    private AidaTreeServant aidaServant;
    private Map converters;
    private boolean useValidation;
    
    /** Creates a new instance of RmiClientImpl */
    public RmiServantImpl() throws RemoteException {
        this(null, null);
    }
    
    public RmiServantImpl(RmiClient rmiClient) throws RemoteException {
        this(rmiClient, null);
    }

    public RmiServantImpl(AidaTreeServant aidaServant) throws RemoteException {
        this(null, aidaServant);
    }

    public RmiServantImpl(RmiClient rmiClient, AidaTreeServant aidaServant) throws RemoteException {
        super();
        this.rmiClient = rmiClient;
        this.aidaServant = aidaServant;
        useValidation = true;
        converters = new Hashtable();        
        connect();
    }
    
    
    // Service methods
    
    /**
     * If useValidation = true, client has to call "setValid" method after
     * receiving update from the ManagedObject in order to reseive next update.
     * If useValidation = false, client receives all updates.
     */
    public synchronized void setUseValidation(boolean state) { useValidation = state; }

    void connect() {
    }
    
    void setAidaTreeServant(AidaTreeServant aidaServant) {
        this.aidaServant = aidaServant;
    }
    
    void setRmiClient(RmiClient rmiClient) {
        this.rmiClient = rmiClient;
    }
    
    void disconnect() {
	try {
	    unexportObject(this, true);
	} catch (Exception e2) { e2.printStackTrace(); }
        
        rmiClient = null;
        aidaServant = null;
    }
    
    protected RmiConverter findConverter(String aidaType) {
        RmiConverter converter = null;
        if (converters.containsKey(aidaType)) {
            converter = (RmiConverter) converters.get(aidaType);
        } else {
            Lookup.Template template = new Lookup.Template(RmiConverter.class, aidaType, null);
            Lookup.Item item = FreeHEPLookup.instance().lookupItem(template);
            if (item == null) throw new IllegalArgumentException("No Converter for AIDA Type: "+aidaType);

            converter = (RmiConverter) item.getInstance();
            converters.put(aidaType, converter);
        }
        return converter;
        
    }
    
    // RmiServant methods
    
    public java.lang.Object find(String path) throws RemoteException {
        remoteLogger.finest("RmiServantImpl.find for path="+path);
        java.lang.Object mo = aidaServant.find(path);
        String aidaType = null;
        if (mo instanceof IManagedObject) aidaType = ((IManagedObject) mo).type();
        else aidaType = mo.getClass().getName();

        remoteLogger.finest("RmiServantImpl.find for path="+path+", mo="+mo);
        RmiConverter converter = findConverter(aidaType);
        java.lang.Object data = converter.extractData(mo);
        if (!useValidation) aidaServant.setValid(new String[] { path } );

        remoteLogger.finest("RmiServantImpl.find for path="+path+", data="+data);
        return data;
    }
    
    public String[] listObjectNames(String path, boolean recursive) throws RemoteException {
        return aidaServant.listObjectNames(path, recursive);
    }
    
    public String[] listObjectTypes(String path, boolean recursive) throws RemoteException {
        return aidaServant.listObjectTypes(path, recursive);
    }
    
    public void setValid(String[] nodePaths) throws RemoteException {
        remoteLogger.finest("RmiServantImpl.setValid for path="+nodePaths[0]);
        aidaServant.setValid(nodePaths);
        remoteLogger.finest("RmiServantImpl.setValid AFTER for path="+nodePaths[0]);
    }
    
    public AidaUpdateEvent[] updates() throws RemoteException {
        remoteLogger.finest("RmiServantImpl.updates");
        AidaUpdateEvent[] result = aidaServant.updates();
        remoteLogger.finest("RmiServantImpl.updates AFTER, gotEvents="+result.length);
        return result;
    }
    

    // AidaTreeClient methods
    
    public void stateChanged(AidaUpdateEvent[] events) {
        try {
            rmiClient.stateChanged(events);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
}
