package org.freehep.record.source;

import java.util.EventObject;

/**
 * Event fired by {@link AsynchronousRecordSource} when the record is ready to be retrieved.
 *
 * @version $Id: RecordReadyEvent.java 13906 2011-10-19 22:01:30Z onoprien $
 */
public class RecordReadyEvent extends EventObject {

  RecordReadyEvent(AsynchronousRecordSource source) {
    super(source);
  }
}
