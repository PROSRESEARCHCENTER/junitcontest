/*
 * ServerQueue.java
 *
 * Created on May 11, 2003, 9:32 PM
 */

package hep.aida.ref.remote.corba;

import hep.aida.ref.remote.corba.generated.EventStruct;
import hep.aida.ref.remote.corba.generated.TreeClient;

import java.util.ArrayList;
import java.util.List;

/**
 * This is thread-safe queue for UpdateEvents.
 * Events can be accumulated in the queue and then retrieved
 * in one chunk. Mainly used on the server side.
 * If TreeClient != null, notifies treeClient about events.
 * @author  serbo
 */
public class CorbaServerEventQueue implements Runnable {
    
    protected List queue;
    protected boolean keepRunning;
    protected EventStruct[] emptyEvents;
    protected TreeClient treeClient;
    
    /** Creates a new instance of UpdatableQueue */
    public CorbaServerEventQueue() {
        queue = new ArrayList(100);
        keepRunning = true;
        emptyEvents = new EventStruct[0];
    }
    
    
    public CorbaServerEventQueue(TreeClient treeClient) {
        queue = new ArrayList(100);
        keepRunning = true;
        emptyEvents = new EventStruct[0];
        this.treeClient = treeClient;
        if (treeClient != null) new Thread(this).start();
    }
    
    
    /**
     * Once the ServerQueue is closed, it can not be restarted again.
     */
    public synchronized void close() { 
        keepRunning = false;
        queue.clear();
        this.notify();
        //queue = null;
        //emptyEvents = null;
    }
    
    public int size() { return (queue == null) ? 0 : queue.size(); }
    
    /**
     * Add events to the queue.
     */
    public void schedule(EventStruct event) {
        System.out.println("ServerQueue.schedule id="+event.id+", path="+event.path+", type="+event.nodeType);
        if (keepRunning) addToQueue(event);
    }
    
    /**
     * Returns the array of events that are currently in the queue.
     * Also deletes those events from the queue. Never returns null.
     */
    public EventStruct[] getEvents() {
        int size = size();
        if (size == 0) {
            return emptyEvents;
        }
        EventStruct[] events = null;
        synchronized ( this ) {
            size = queue.size();
            events = new EventStruct[size];
            queue.toArray(events);
	    queue.clear();
        }
        return events;
    }
    
    /**
     * This method adds event to the queue and does some cleanup, like
     * remove multiple update events for the same path, etc.
     */
    protected synchronized void addToQueue(EventStruct event) {
            queue.add(event);
            System.out.println("ServerQueue.addToQueue queue size="+queue.size()+", path="+event.path+", type="+event.nodeType);            
            this.notify();
    }
    
   // Runnable methods
    
    /**
     * In Duplex mode sends updates to TreeClient
     */
    
    public void run() {
        int size = 0;
        EventStruct[] events = null;
        while (keepRunning) {
            try {
                synchronized (this) {
		    if(queue.size() == 0) this.wait();
                    size = queue.size();
                    if (size > 0) {
                        events = getEvents();
                    }
                }
                System.out.println("UpdatableQueue.run Processing: "+size);
                if (events == null || events.length == 0) return;
                treeClient.stateChanged(events);
            } catch (InterruptedException e2) {
                System.out.println("UpdatableQueue Thread InterruptedException.");
                e2.printStackTrace();                
	    } catch (Exception e3) {
                System.out.println("Problems in CorbaServerEventQueue!.");
                e3.printStackTrace();
            } // end of try/catch
        } // end of while
    } //end of run  
    
}
