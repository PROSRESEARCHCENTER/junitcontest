/*
 * CorbaConverter.java
 *
 * Created on June 12, 2003, 7:00 PM
 */

package hep.aida.ref.remote;

/**
 * Basic converter for creating and updating RemoteManagedObjects
 * in the client tree. Also can extract data from "aidaType" AIDA 
 * object using only AIDA public methods. Concrete implementations 
 * should be one per AIDA Type per protocol. 
 * See "hep.aida.ref.remote.corba" package for examples.
 * @author  serbo
 */
public abstract class RemoteConverter {
    
    protected String protocol;
    protected String aidaType;
    protected String dataType;
    
    /** Creates a new instance of CorbaConverter */
    protected RemoteConverter() {
    }
    
    public String protocol() {
        return protocol;
    }
    
    public String aidaType() {
        return aidaType;
    }
    
    public String dataType() {
        return dataType;
    }
    
    /** 
     * Creates new instance of type "aidaType".
     */
    public abstract Object createAidaObject(String name);
    
    /**
     * Updates data contained by object.
     */
    public abstract boolean updateAidaObject(Object aidaObject, Object newData);
    
    /**
     * Extract data from AIDA object
     */
    public abstract Object extractData(Object aidaObject);
    
}
