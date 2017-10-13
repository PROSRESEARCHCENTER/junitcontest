package hep.aida.ref.root;

import hep.aida.dev.IStore;
import hep.aida.dev.IStoreFactory;

/**
 * An implementation of IStoreFactory that creates RootStore.
 */
public class XrootdStoreFactory implements IStoreFactory
{
    public IStore createStore() {
	return new XrootdStore();
    }   
    public String description()
    {
       return "xroot";
    }
    public boolean supportsType(String type)
    {
       return "xroot".equalsIgnoreCase(type);
    }    
}