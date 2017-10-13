/*
 * UpdatableQueue.java
 *
 * Created on May 11, 2003, 9:32 PM
 */

package hep.aida.ref.remote;

import hep.aida.ref.remote.interfaces.AidaUpdatable;
import hep.aida.ref.remote.interfaces.AidaUpdateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author  serbo
 */
public class RemoteUpdatableQueue extends Thread {
    
    private List updatables;
    private List events;
    private boolean keepRunning;
    private Logger remoteLogger;
    
    
    /** Creates a new instance of UpdatableQueue */
    public RemoteUpdatableQueue() {
        updatables = new ArrayList(100);
        events = new ArrayList(100);
        keepRunning = true;
        remoteLogger = Logger.getLogger("hep.aida.ref.remote");
        remoteLogger.fine("UpdatableQueue: START");
        //this.setPriority(this.getPriority()-1);
        this.start();
    }
    
    public synchronized void close() { 
        keepRunning = false; 
        updatables.clear();
        events.clear();
        this.notify();
    }
    public void schedule(Object source, AidaUpdateEvent event) {
        if (!keepRunning) return; // Do not schedule new events if queue is shutting down
        remoteLogger.finest("RemoteUpdatableQueue.schedule: id="+event.id()+",  nodeType="+event.nodeType()+",  path="+event.path());
        synchronized( this ) {
            updatables.add(source);
            events.add(event);
            this.notify();
        }
    }
    public void schedule(Object source, AidaUpdateEvent[] evts) {
        if (!keepRunning) return; // Do not schedule new events if queue is shutting down
        synchronized( this ) {
            if (evts == null || evts.length == 0) return;
            for (int i=0; i<evts.length; i++) {
                AidaUpdateEvent event = evts[i];
                remoteLogger.finest("RemoteUpdatableQueue.schedule: id="+event.id()+",  nodeType="+event.nodeType()+",  path="+event.path());
                updatables.add(source);
                events.add(event);
            }
            this.notify();
        }
    }
    public void run() {
        while (keepRunning) {
            Object obj = null;
            AidaUpdatable source = null;
            AidaUpdateEvent event = null;
            int size = 0;
            try {
                synchronized (this) {
		    if(updatables.size() == 0) this.wait();
                    size = updatables.size();
                    if (size > 0) {
                        obj = updatables.remove(0);
                        event = (AidaUpdateEvent) events.remove(0);
                    }
                }
                    remoteLogger.finest("RemoteUpdatableQueue.run *** Start for event: "+event+", object: "+obj);
                    if (event == null) {
                        remoteLogger.fine("RemoteUpdatableQueue.run event=null for object: "+obj);
                    } else if (obj == null) {
                        remoteLogger.fine("RemoteUpdatableQueue.run object=null for event: id="+event.id()+",  nodeType="+event.nodeType()+",  path="+event.path());
                    } else if (obj instanceof AidaUpdatable) {
                        source = (AidaUpdatable) obj;
                        source.stateChanged(event);
                        remoteLogger.finest("RemoteUpdatableQueue.run *** Finished for event: id="+event.id()+",  nodeType="+event.nodeType()+",  path="+event.path()+", object: "+obj);
                    } else {
                        // If not AidaUpdatable, do something else here.
                        remoteLogger.fine("RemoteUpdatableQueue.run object is not AidaUpdatable: "+obj);
                    }
                
            } catch (InterruptedException e2) {
                remoteLogger.log(Level.INFO, "RemoteUpdatableQueue InterruptedException: \n\t" + e2.getMessage());
                remoteLogger.log(Level.FINE, "", e2.getStackTrace());
               
	    } catch (Exception e3) {
                remoteLogger.log(Level.INFO, "Exception in RemoteUpdatableQueue: \n\t" + e3.getMessage());
                remoteLogger.log(Level.FINE, "", e3.getStackTrace());
            } // end of try/catch
        } // end of while
    } //end of run
    
}
