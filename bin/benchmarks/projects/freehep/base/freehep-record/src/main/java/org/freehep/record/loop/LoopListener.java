package org.freehep.record.loop;

/**
 * Interface to be implemented by classes listening to events fired by this loop when its state changes.
 *
 * @author Dmitry Onoprienko
 * @version $Id$
 */
public interface LoopListener {

  /** Processes an event received from {@link RecordLoop}. */
  void process(LoopEvent event);

}
