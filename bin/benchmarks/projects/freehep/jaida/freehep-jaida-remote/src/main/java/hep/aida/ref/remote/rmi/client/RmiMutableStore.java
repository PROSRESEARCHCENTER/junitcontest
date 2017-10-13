/*
 * RmiMutableStore.java
 *
 * Created on October 14, 2003, 7:23 PM
 */

package hep.aida.ref.remote.rmi.client;

import hep.aida.IManagedObject;
import hep.aida.ref.remote.RemoteClient;
import hep.aida.ref.remote.RemoteManagedObject;
import hep.aida.ref.remote.RemoteMutableStore;
import hep.aida.ref.remote.rmi.converters.RmiConverter;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

/**
 *
 * @author  serbo
 */
public class RmiMutableStore extends RemoteMutableStore {
    
    private Map converters;

    /** Creates a new instance of RmiMutableStore */
    public RmiMutableStore() {
        super();
        init();
    }
    
    public void init() {
        super.init();
        converters = new Hashtable();        
    }
    
    
    // Abstract methods from RemoteMutableStore
    
    protected RemoteClient createClient(Map options) {
        RmiRemoteClient client = null;
        
        String rmiBindName = null;
        boolean duplex = true;
        if (options == null || options.isEmpty())
            throw new IllegalArgumentException("No information about AidaTreeServer, options empty");
        
        Iterator it = options.keySet().iterator();
        while (it.hasNext()) {
            String key = ((String) it.next());
            String value = ((String) options.get(key));
            remoteLogger.fine("RmiMutableStore.createClient: Key = "+key+" \tValue = "+value);
            if (key.equalsIgnoreCase("rmiBindName")) {
                rmiBindName = value; 
            } else if (key.equalsIgnoreCase("duplex")) {
                if (value.equalsIgnoreCase("true")) duplex = true;
                else duplex = false;
            } else if (key.equalsIgnoreCase("hurry")) {
                if (value.equalsIgnoreCase("true")) setHurry(true);
                else setHurry(false);
            }
        }
        
        client = new RmiRemoteClient(this, duplex, options);

        return client;
    }
    
    public IManagedObject createObject(String name, String aidaType) throws IllegalArgumentException {
        remoteLogger.finest("RmiMutableStore.createObject:     path="+name+",   type="+aidaType);
        
        // Find Convertor for this AIDA Type
        RmiConverter converter = findConverter(aidaType);

        // Create object
        IManagedObject mo = (IManagedObject) converter.createAidaObject(name);
        if (mo instanceof RemoteManagedObject) ((RemoteManagedObject) mo).setStore(this);
        return mo;
    }
    
    public void updateData(String path, String aidaType) throws IllegalArgumentException {
        remoteLogger.finest("RmiMutableStore.updateData:     path="+path+",   type="+aidaType);
        
        try {
            // Find Convertor for this AIDA Type
            RmiConverter converter = findConverter(aidaType);
            
            // Get local AIDA Tree object and remote data
            IManagedObject mo = tree.find(path);
            Object data = client.find(path);
            
            // If mo is RemoteManagedObject, converter must setDataValid(true) on it
            converter.updateAidaObject(mo, data);
 
            remoteLogger.finest("RmiMutableStore.updateData:     path="+path+",   type="+aidaType+", data="+data);
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    // IMutableStore methods
    public void close() throws IOException {
        super.close();
        converters.clear();
    }

    
    // Service methods
    
    /** Find Convertor for this AIDA Type */
    private RmiConverter findConverter(String aidaType) {
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
}
