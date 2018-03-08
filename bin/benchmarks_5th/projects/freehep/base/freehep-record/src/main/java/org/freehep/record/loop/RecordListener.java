package org.freehep.record.loop;

/**
 * Interface to be implemented by classes that process records supplied by {@link RecordLoop}.
 *
 * @author Dmitry Onoprienko
 * @version $Id$
 */
public interface RecordListener {
  
  /** Process a record. */
  void recordSupplied(RecordEvent event);
  
}
