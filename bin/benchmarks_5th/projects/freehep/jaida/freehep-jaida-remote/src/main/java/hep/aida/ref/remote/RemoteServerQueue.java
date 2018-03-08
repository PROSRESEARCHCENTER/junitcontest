/*
 * ServerQueue.java
 *
 * Created on May 11, 2003, 9:32 PM
 */

package hep.aida.ref.remote;

import hep.aida.ref.remote.interfaces.AidaTreeClient;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is thread-safe queue for UpdateEvents.
 * Events can be accumulated in the queue and then retrieved
 * in one chunk. Mainly used on the server side.
 * If client != null new thread will be started with this queue
 * to send updates to the client.
 *
 * @author  serbo
 */
public class RemoteServerQueue implements Runnable {
    
    protected AidaTreeClient client;
    protected List queue;
    protected List holdQueue;
    protected long blockingTimeout = 10000;
    protected boolean keepRunning;
    protected boolean blocking;
    protected Object blockingLock;
    protected boolean hold;
    protected Logger remoteLogger;
    
    /** Creates a new instance of UpdatableQueue */
    public RemoteServerQueue() {
        this(null);
    }
    
    public RemoteServerQueue(AidaTreeClient client) {
        queue = new ArrayList(100);
        holdQueue = new ArrayList(100);
        keepRunning = true;
        this.client = client;
        this.hold = false;
        this.blocking = false;
        this.blockingLock = new Object();
        remoteLogger = Logger.getLogger("hep.aida.ref.remote");
        remoteLogger.fine("RemoteServerQueue: START");
        if (client != null) new Thread(this).start();
    }
    
    public void setBlocking(boolean b) { 
        blocking = b;
        synchronized (blockingLock) { blockingLock.notify(); }
    }
    public boolean isBlocking() { return blocking; }
    
    /**
     * Once the RemoteServerQueue is closed, it can not be restarted again.
     */
    public synchronized void close() { 
        keepRunning = false;
        queue.clear();
        queue = null;
        holdQueue.clear();
        this.notify();
        synchronized (blockingLock) { blockingLock.notify(); }
    }
    
    /*
     * If hold set to "true", do not shedule events, just accumulate till
     * hold is set to "false"
     */
    public void setHold(boolean h) {
        if (h) {
            this.hold = h;
        } else {
            synchronized ( this ) {
                this.hold = h;
                queue.addAll(holdQueue);
                holdQueue.clear();
                this.notify();
                synchronized (blockingLock) { blockingLock.notify(); }
            }
        }
    }
    
    public int size() { return (queue == null) ? 0 : queue.size(); }
    
    /**
     * Add events to the queue.
     */
    public void schedule(AidaUpdateEvent event) {
        remoteLogger.finest("RemoteServerQueue.schedule id="+event.id()+", path="+event.path()+", type="+event.nodeType()+", event="+event);
        if (keepRunning) addToQueue(event);
    }
    
    /**
     * Returns the array of events that are currently in the queue.
     * Also deletes those events from the queue. Never returns null.
     */
    public AidaUpdateEvent[] getEvents() {
        AidaUpdateEvent[] events = new AidaUpdateEvent[0];
        int size = size();
        remoteLogger.finest("RemoteServerQueue.getEvents size="+size);
        if (size == 0)  {
            if (client == null && blocking) {
                synchronized(blockingLock) {
                    try {
                        blockingLock.wait(blockingTimeout);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else return events;
        }
        
        synchronized ( this ) {
            size = size();
            if (size == 0) return events;
            events = new AidaUpdateEvent[size];
            queue.toArray(events);
            queue.clear();
        }
        return events;
    }

    /**
     * This method adds event to the queue and can do some cleanup, like
     * remove multiple update events for the same path (not implemented yet), etc.
     */
    protected void addToQueue(AidaUpdateEvent event) {
        synchronized ( this ) {            
            if (hold) holdQueue.add(event);
            else {
                queue.add(event);            
                this.notify();
                synchronized (blockingLock) { blockingLock.notify(); }
            }
        }
    }
    
    
    
    // Runnable methods
    
    /**
     * In Duplex mode sends updates to AidaTreeClient
     */    
    public void run() {
        int size = 0;
        AidaUpdateEvent[] events = null;
        while (keepRunning) {
            try {
                synchronized (this) {
		    if(size() == 0) this.wait();
                    if (queue == null) return;
                    size = size();
                    if (size > 0) {
                        events = getEvents();
                    }
                }
                remoteLogger.finest("RemoteServerQueue.run Processing: "+size);
                if (events != null || events.length > 0) {
                    if (client != null) client.stateChanged(events);
                }
            } catch (InterruptedException e2) {
                remoteLogger.log(Level.INFO, "RemoteServerOueue InterruptedException.", e2);
                remoteLogger.log(Level.FINE, "", e2.getStackTrace());
	    } catch (Exception e3) {
                remoteLogger.log(Level.INFO, "Exception in RemoteServerQueue: ", e3);
                remoteLogger.log(Level.FINE, "", e3.getStackTrace());
            } // end of try/catch
        } // end of while
    } //end of run  
    
}
