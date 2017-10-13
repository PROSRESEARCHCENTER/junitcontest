package org.freehep.record.source;

import java.util.EventListener;

/**
 * Interface to be implemented by classes that need to be notified of {@link RecordReadyEvent}s.
 * @version $Id$
 */
public interface RecordReadyListener extends EventListener {

  void nextRecordReady(RecordReadyEvent event);
}
