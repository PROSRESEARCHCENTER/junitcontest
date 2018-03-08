package hep.aida.ref.root;

import hep.aida.dev.IStore;
import hep.aida.dev.IStoreFactory;

/**
 * An implementation of IStoreFactory that creates RootStore.
 */
public class RootStoreFactory implements IStoreFactory
{
    public IStore createStore() {
	return new RootStore();
    }   
    public String description()
    {
       return "root";
    }
    public boolean supportsType(String type)
    {
       return "root".equalsIgnoreCase(type);
    }    
}