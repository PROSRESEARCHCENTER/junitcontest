/*
 * RmiConverter.java
 *
 * Created on October 14, 2003, 7:28 PM
 */

package hep.aida.ref.remote.rmi.converters;

import hep.aida.ref.remote.RemoteConverter;
import hep.aida.ref.remote.rmi.client.RmiStoreFactory;

/**
 *
 * @author  serbo
 */
public abstract class RmiConverter extends RemoteConverter {
    
    /** Creates a new instance of RmiConverter */
    public RmiConverter() {
        super();
        protocol = RmiStoreFactory.storeType;
    }
    
}
