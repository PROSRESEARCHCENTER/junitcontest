package hep.aida.ref.hbook;

import hep.aida.dev.IStore;
import hep.aida.dev.IStoreFactory;


/**
 * An implementation of IStoreFactory that creates HBookStore.
 */
public class HBookStoreFactory implements IStoreFactory
{
    public IStore createStore() {
	return new HBookStore();
    }    
    public String description()
    {
       	return "hbook";
    }  
    public boolean supportsType(String type)
    {
       return "hbook".equalsIgnoreCase(type);
    }   
}