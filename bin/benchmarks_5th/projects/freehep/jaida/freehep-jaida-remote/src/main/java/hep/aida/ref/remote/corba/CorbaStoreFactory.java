package hep.aida.ref.remote.corba;

import hep.aida.dev.IStore;
import hep.aida.dev.IStoreFactory;

/**
 * An implementation of IStoreFactory that creates CorbaMutableStore.
 */
public class CorbaStoreFactory implements IStoreFactory
{
    public IStore createStore() {
	return new CorbaMutableStore();
    }    
    public String description()
    {
       	return "corba";
    }  
    public boolean supportsType(String type)
    {
       return "corba".equalsIgnoreCase(type);
    }   
}