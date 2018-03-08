/*
 * CorbaMutableStore.java
 *
 * Created on June 6, 2003, 2:06 PM
 */

package hep.aida.ref.remote.corba;

import hep.aida.IManagedObject;
import hep.aida.ref.remote.RemoteClient;
import hep.aida.ref.remote.RemoteManagedObject;
import hep.aida.ref.remote.RemoteMutableStore;
import hep.aida.ref.remote.corba.converters.CorbaConverter;
import hep.aida.ref.remote.corba.converters.CorbaDataPointSetDConverter;
import hep.aida.ref.remote.corba.converters.CorbaHist1DConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

/**
 * This is implementation of IDevMutableStore that works with
 * AIDA CORBA Client. Supported options:
 *  ior=ior_value
 *  iorFileURL=file_URL
 *  ServerName=name_of_server_in_CORBA_Name_service
 * 
 * @author  serbo
 */
public final class CorbaMutableStore extends RemoteMutableStore {
    
    private Map converters;
    
    /** Creates a new instance of CorbaMutableStore */
    public CorbaMutableStore() {
        super();
        converters = new Hashtable();

      // Register available CORBA Converters
      FreeHEPLookup.instance().add(CorbaHist1DConverter.getInstance(), "IHistogram1D");
      FreeHEPLookup.instance().add(CorbaDataPointSetDConverter.getInstance(), "IDataPointSet");
        
    }
    
    // Service Methods
    
    private String readIORFromFile(String fileURL) {
	String ior = null;
	try {
	    URL url = new URL(fileURL);
	    
	    InputStream is = url.openStream();         // throws an IOException 	    
	    BufferedReader br = new BufferedReader(new InputStreamReader(is)); 
	    ior = br.readLine();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}	
	return ior;
    }
        
    
    // From RemoteMutableStore
    
    protected RemoteClient createClient(Map options) {
        String ior = "";
        String nsName = null;
        //CorbaTreeClientImpl client = null;
        RemoteClient client = null;
        boolean duplex = true;
        if (options.isEmpty())
            throw new IllegalArgumentException("No information about AidaTreeServer");
        
        Iterator it = options.keySet().iterator();
        while (it.hasNext()) {
            String key = ((String) it.next());
            String value = ((String) options.get(key));
            System.out.println("Key = "+key+" \tValue = "+value);
            if (key.equalsIgnoreCase("ior")) { ior = value; }
            else if (key.equalsIgnoreCase("iorFileURL")) 
                ior = readIORFromFile(value);
            else if (key.equalsIgnoreCase("ServerName")) 
                nsName = value;
            else if (key.equalsIgnoreCase("duplex")) {
                if (value.equalsIgnoreCase("true")) duplex = true;
                else duplex = false;
            }
        }
        if (nsName == null) {   // Do not use CORBA Name Service
            //client = new CorbaTreeClientImpl(this, ior);
            client.setDuplex(duplex);
        } else {                // Use CORBA Name Service
            //client = new CorbaTreeClientImpl(this, ior, nsName);
            client.setDuplex(duplex);            
        }
        return client;
    }
    
    public void updateData(String path, String aidaType) throws IllegalArgumentException {
        System.out.println("CorbaMutableStore.updateData:     path="+path+",   type="+aidaType);
        
        // Find Convertor for this AIDA Type
        CorbaConverter converter = null;
        if (converters.containsKey(aidaType)) {
            converter = (CorbaConverter) converters.get(aidaType);
        } else {
            Lookup.Template template = new Lookup.Template(CorbaConverter.class, aidaType, null);
            Lookup.Item item = FreeHEPLookup.instance().lookupItem(template);
            if (item == null) throw new IllegalArgumentException("No Converter for AIDA Type: "+aidaType);

            converter = (CorbaConverter) item.getInstance();
            converters.put(aidaType, converter);
        }

        // Get local AIDA Tree object and remote data
        IManagedObject mo = tree.find(path);
        Object data = client.find(path);
        System.out.println("CorbaMutableStore.updateData:    mo="+mo+",  data="+data);
        
        converter.updateAidaObject(mo, data);
        //if (mo instanceof RemoteManagedObject) ((RemoteManagedObject) mo).setDataValid(true);

        //if (!aidaType.equalsIgnoreCase("dir")) ((RemoteClient) client).setValid(path);
    }
       
    
    public IManagedObject createObject(String name, String aidaType) throws IllegalArgumentException {
        System.out.println("CorbaMutableStore.createObject:   name="+name+",   type="+aidaType);
        //super.createObject(name, aidaType);
        
        // Lookup CorbaConverter: first in the local Map, then in the global Lookup
        CorbaConverter converter = null;
        if (converters.containsKey(aidaType)) {
            converter = (CorbaConverter) converters.get(aidaType);
        } else {
            Lookup.Template template = new Lookup.Template(CorbaConverter.class, aidaType, null);
            Lookup.Item item = FreeHEPLookup.instance().lookupItem(template);
            if (item == null) throw new IllegalArgumentException("No Converter for AIDA Type: "+aidaType);

            converter = (CorbaConverter) item.getInstance();
            converters.put(aidaType, converter);
        }
        IManagedObject mo = (IManagedObject) converter.createAidaObject(name);
        if (mo instanceof RemoteManagedObject) ((RemoteManagedObject) mo).setStore(this);
        return mo;
    }
    
    // IMutableStore methods
    public void close() throws IOException {
        super.close();
        converters.clear();
        converters = null;
    }
    
}
