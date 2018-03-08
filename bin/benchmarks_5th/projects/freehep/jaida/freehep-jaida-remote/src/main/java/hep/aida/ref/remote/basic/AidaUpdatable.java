/*
 * AidaUpdatable.java
 *
 * Created on May 11, 2003, 9:42 PM
 */

package hep.aida.ref.remote.basic;

import hep.aida.ref.remote.basic.interfaces.UpdateEvent;

/**
 * Interface that is used by the Queue to process updates on a separate thread.
 * @author  serbo
 */
public interface AidaUpdatable {
    void stateChanged(UpdateEvent event);
}
