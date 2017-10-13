/*
 * RmiStoreFactory.java
 *
 * Created on October 14, 2003, 7:18 PM
 */

package hep.aida.ref.remote.rmi.client;

import hep.aida.dev.IStoreFactory;
import hep.aida.ref.remote.RemoteTable;
import hep.aida.ref.remote.rmi.converters.RmiCloud1DConverter;
import hep.aida.ref.remote.rmi.converters.RmiCloud2DConverter;
import hep.aida.ref.remote.rmi.converters.RmiDataPointSetConverter;
import hep.aida.ref.remote.rmi.converters.RmiHist1DConverter;
import hep.aida.ref.remote.rmi.converters.RmiHist2DConverter;
import hep.aida.ref.remote.rmi.converters.RmiProfile1DConverter;
import hep.aida.ref.remote.rmi.converters.RmiTableConverter;

import org.freehep.util.FreeHEPLookup;

/**
 *
 * @author  serbo
 */
public class RmiStoreFactory implements IStoreFactory {
    public static String storeType = "aidaRmi";
    
    /** Creates a new instance of RmiStoreFactory */
    public RmiStoreFactory() {

        FreeHEPLookup lookup = FreeHEPLookup.instance();
        
        // Register needed RMI Converters
        Object item = lookup.lookup(RmiHist1DConverter.class);
        if (item == null) {
            lookup.add(RmiHist1DConverter.getInstance(), "IHistogram1D");
        }
        item = lookup.lookup(RmiHist2DConverter.class);
        if (item == null) {
            lookup.add(RmiHist2DConverter.getInstance(), "IHistogram2D");
        }
        item = lookup.lookup(RmiDataPointSetConverter.class);
        if (item == null) {
            lookup.add(RmiDataPointSetConverter.getInstance(), "IDataPointSet");
        }
        item = lookup.lookup(RmiCloud1DConverter.class);
        if (item == null) {
            lookup.add(RmiCloud1DConverter.getInstance(), "ICloud1D");
        }
        item = lookup.lookup(RmiCloud2DConverter.class);
        if (item == null) {
            lookup.add(RmiCloud2DConverter.getInstance(), "ICloud2D");
        }
        item = lookup.lookup(RmiProfile1DConverter.class);
        if (item == null) {
            lookup.add(RmiProfile1DConverter.getInstance(), "IProfile1D");
        }
        item = lookup.lookup(RmiTableConverter.class);
        if (item == null) {
            RemoteTable rt = new RemoteTable("tmp");
            lookup.add(RmiTableConverter.getInstance(), rt.type());
        }
    }
    
    public hep.aida.dev.IStore createStore() {
        return new RmiMutableStore();
    }
    
    public String description() {
        return storeType;
    }
    
    public boolean supportsType(String type) {
        return storeType.equalsIgnoreCase(type);
    }
    
}
