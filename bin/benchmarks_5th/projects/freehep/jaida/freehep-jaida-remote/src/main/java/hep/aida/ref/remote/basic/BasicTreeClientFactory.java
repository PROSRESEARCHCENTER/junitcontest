/*
 * BasicTreeClientFactory.java
 *
 * Created on May 25, 2003, 5:02 PM
 */

package hep.aida.ref.remote.basic;

import hep.aida.dev.IStore;
import hep.aida.dev.IStoreFactory;

/**
 *
 * @author  serbo
 */
public class BasicTreeClientFactory implements IStoreFactory {
    
    /** Creates a new instance of BasicTreeClientFactory */
    public BasicTreeClientFactory() {
    }

    public IStore createStore() {
	//return new BasicTreeClient();
        return null;
    }    
    
    public String description()
    {
       	return "BasicTreeClient";
    }  
    
    public boolean supportsType(String type)
    {
       return "BasicTreeClient".equalsIgnoreCase(type);
    }   
}
