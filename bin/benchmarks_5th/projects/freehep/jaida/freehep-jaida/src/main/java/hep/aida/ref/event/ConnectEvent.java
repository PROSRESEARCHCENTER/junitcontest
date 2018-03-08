package hep.aida.ref.event;

/**
 * An event send to AIDAListeners if connection between AIDA object
 * and it's data provider is re-open
 * @author tonyj
 * @version $Id: ConnectEvent.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ConnectEvent extends java.util.EventObject {
    protected boolean connected;
    
    public ConnectEvent(Object source) {
        this(source, true);
    }
    public ConnectEvent(Object source, boolean connected) {
        super(source);
        this.connected = connected;
    }

    public void setConnected(boolean connected) { this.connected = connected; }
    
    public boolean isConnected() { return connected; }
}
