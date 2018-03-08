// Copyright 2002.
package hep.aida.ref.xml;

import hep.aida.dev.IStore;
import hep.aida.dev.IStoreFactory;

/**
 * An implementation of IStoreFactory that creates Aida XML Store.
 * 
 * @author tonyj
 * @version $Id: AidaXMLStoreFactory.java 10560 2007-03-03 00:21:01Z duns $
 */
public class AidaXMLStoreFactory implements IStoreFactory
{   
    public String description()
    {
       return "xml";
    }    
    public boolean supportsType(String type)
    {
       return "xml".equalsIgnoreCase(type) || "zipxml".equalsIgnoreCase(type);
    }
    public IStore createStore()
    {
       return new AidaXMLStore();
    }
}
