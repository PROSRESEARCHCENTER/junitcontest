/*
 * ServerQueue.java
 *
 * Created on May 11, 2003, 9:32 PM
 */

package hep.aida.ref.remote.basic;

import hep.aida.ref.remote.basic.interfaces.UpdateEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This is thread-safe queue for UpdateEvents.
 * Events can be accumulated in the queue and then retrieved
 * in one chunk. Mainly used on the server side.
 * @author  serbo
 */
public class ServerQueue {
    
    protected List queue;
    protected boolean keepRunning;
    
    /** Creates a new instance of UpdatableQueue */
    public ServerQueue() {
        queue = new ArrayList(100);
        keepRunning = true;
    }
    
    /**
     * Once the ServerQueue is closed, it can not be restarted again.
     */
    public synchronized void close() { 
        keepRunning = false;
        queue.clear();
        queue = null;
    }
    
    public int size() { return (queue == null) ? 0 : queue.size(); }
    
    /**
     * Add events to the queue.
     */
    public void schedule(UpdateEvent event) {
        //System.out.println("ServerQueue.schedule id="+event.id()+", path="+event.path()+", type="+event.nodeType());
        if (keepRunning) addToQueue(event);
    }
    
    /**
     * Returns the array of events that are currently in the queue.
     * Also deletes those events from the queue. Never returns null.
     */
    public UpdateEvent[] getEvents() {
        UpdateEvent[] events = new UpdateEvent[0];
        int size = size();
        if (size == 0) {
            return events;
        }
        synchronized ( queue ) {
            size = queue.size();
            events = new UpdateEvent[size];
            queue.toArray(events);
	    queue.clear();
        }
        return events;
    }
    
    /**
     * This method adds event to the queue and does some cleanup, like
     * remove multiple update events for the same path, etc.
     */
    protected void addToQueue(UpdateEvent event) {
        synchronized ( this ) {
            
            queue.add(event);
            this.notify();
        }
    }
}
