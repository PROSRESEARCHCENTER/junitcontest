/*
 * UpdatableQueue.java
 *
 * Created on May 11, 2003, 9:32 PM
 */

package hep.aida.ref.remote.basic;

import hep.aida.ref.remote.basic.interfaces.UpdateEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  serbo
 */
public class UpdatableQueue extends Thread {
    
    private List updatables;
    private List events;
    private boolean keepRunning;
    
    
    /** Creates a new instance of UpdatableQueue */
    public UpdatableQueue() {
        updatables = new ArrayList(100);
        events = new ArrayList(100);
        keepRunning = true;
        System.out.println("UpdatableQueue: START");
        this.start();
    }
    
    public synchronized void close() { 
        keepRunning = false; 
        updatables.clear();
        events.clear();
        this.notify();
    }
    public void schedule(Object source, UpdateEvent event) {
        if (!keepRunning) return; // Do not schedule new events if queue is shutting down
        //System.out.println("UpdatableQueue.schedule ");
        synchronized( this ) {
            updatables.add(source);
            events.add(event);
            this.notify();
        }
    }
    public void run() {
        while (keepRunning) {
            Object obj = null;
            AidaUpdatable source = null;
            UpdateEvent event = null;
            int size = 0;
            try {
                synchronized (this) {
		    if(updatables.size() == 0) this.wait();
                    size = updatables.size();
                    if (size > 0) {
                        obj = updatables.remove(0);
                        event = (UpdateEvent) events.remove(0);
                    }
                }
                if (obj == null) return;
                //System.out.println("UpdatableQueue.run Processing: "+size);
                if (obj instanceof AidaUpdatable) {
                    source = (AidaUpdatable) obj;
                    source.stateChanged(event);
                } else {
                    // If not AidaUpdatable, do something else here.
                }
            } catch (InterruptedException e2) {
                System.out.println("UpdatableQueue Thread InterruptedException.");
                e2.printStackTrace();
                
	    } catch (Exception e3) {
                System.out.println("Problems in UpdatableQueue!.");
                e3.printStackTrace();
            } // end of try/catch
        } // end of while
    } //end of run
    
}
