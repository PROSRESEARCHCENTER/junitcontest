package hep.aida.ref.remote;

import hep.aida.ref.remote.interfaces.AidaUpdateEvent;

import java.io.Serializable;
import java.util.EventObject;

/**
 *
 * @author  serbo
 */
public class RemoteUpdateEvent extends EventObject implements AidaUpdateEvent, Serializable {
    
    static final long serialVersionUID = -605227416706961615L;
    protected int id;
    protected String path;
    protected String nodeType;
    protected String xAxisType = "double";
    
    /** Creates a new instance of BasicTreeEvent */
    public RemoteUpdateEvent() {
        super("");
        init();
    }
    public RemoteUpdateEvent(int id, String path, String nodeType) {
        this(id, path, nodeType, "double");
    }
    public RemoteUpdateEvent(int id, String path, String nodeType, String xAxisType) {
        super("");
        this.id = id;
        this.path = path;
        this.nodeType = nodeType;
        if (xAxisType != null) this.xAxisType = xAxisType;
    }
    
    // Service methods
    public void init() {
        this.id = -1;
        this.path = null;
        this.nodeType = null;
        this.xAxisType = "double";
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }    
    public void setPath(String path) {
        this.path = path;
    }

    public void setXAxisType(String xAxisType) {
        this.xAxisType = xAxisType;
    }

    public String getXAxisType() {
        return xAxisType;
    }

    
    // UpdateEvent methods
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
