package org.freehep.record.loop;

/**
 * Event sent by {@link RecordLoop} to {@link RecordListener}s to initiate record processing.
 *
 * @author Dmitry Onoprienko
 * @version $Id$
 */
public class RecordEvent {

// -- Private parts : ----------------------------------------------------------

  final private RecordLoop _loop;
  final private Object _record;

// -- Construction and initialization : ----------------------------------------

  public RecordEvent(RecordLoop loop, Object record) {
    _loop = loop;
    _record = record;
  }
  
// -- Getters : ----------------------------------------------------------------
 
  /** Return <tt>RecordLoop</tt> that fired this event. */
  public RecordLoop getSource() {
    return _loop;
  }

  /** Returns the record associated with this event. */
  public Object getRecord() {
    return _record;
  }

// -----------------------------------------------------------------------------
}
