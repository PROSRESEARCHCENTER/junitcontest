package hep.aida.ref.remote.basic;

import hep.aida.ref.remote.basic.interfaces.UpdateEvent;

/**
 *
 * @author  serbo
 */
public class BasicUpdateEvent implements UpdateEvent {
    
    protected int id;
    protected String path;
    protected String nodeType;
    
    /** Creates a new instance of BasicTreeEvent */
    public BasicUpdateEvent(int id, String path, String nodeType) {
        this.id = id;
        this.path = path;
        this.nodeType = nodeType;
    }
    
    public int id() {
        return id;
    }
    
    public String nodeType() {
        return nodeType;
    }
    
    public String path() {
        return path;
    }
    
}
