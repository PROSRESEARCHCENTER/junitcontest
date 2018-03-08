package org.freehep.conditions;

import java.util.*;

/**
 * An event sent to {@link ConditionsListener}s to notify them of changes.
 * 
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
final public class ConditionsEvent extends HashMap<String,Object> {
  
// -- Private parts : ----------------------------------------------------------
  
  private Conditions _source;
  private ConditionsManager _manager;
  
// -- Construction : -----------------------------------------------------------

  public ConditionsEvent(Conditions source, ConditionsManager manager, String detector, Integer run, Date timestamp) {
    _source = source;
    _manager = manager;
    if (detector != null) put("detector", detector);
    if (run != null) put("run", run);
    if (timestamp != null) put("timestamp", timestamp);
  }

  public ConditionsEvent(Conditions source, ConditionsManager manager, ConditionsEvent event) {
    this(source, manager, event.getDetector(), event.getRun(), event.getTimestamp());
  }

  public ConditionsEvent(Conditions source, ConditionsEvent event) {
    this(source, event.getConditionsManager(), event.getDetector(), event.getRun(), event.getTimestamp());
  }

  public ConditionsEvent(String detector, Integer run, Date timestamp) {
    this(null, null, detector, run, timestamp);
  }
  
// -- Getters : ----------------------------------------------------------------

  public Conditions getSource() {
    return _source;
  }

  public ConditionsManager getConditionsManager() {
    return _manager;
  }

  public String getDetector() {
    return get("detector").toString();
  }

  public int getRun() {
    Integer run = (Integer) get("run");
    return run == null ? -1 : run.intValue();
  }

  public Date getTimestamp() {
    return (Date) get("timestamp");
  }
  
// -- Setters : ----------------------------------------------------------------
  
  public void setSource(Conditions source) {
    _source = source;
  }
  
  public void setConditionsManager(ConditionsManager manager) {
    _manager = manager;
  }
  
  @Override
  public Object put(String key, Object value) {
    if ("run".equals(key) && value != null) {
      try {
        return super.put(key, (Integer)value);
      } catch (ClassCastException x) {
        throw new IllegalArgumentException("Run must be Integer", x);
      }
    } else if ("timestamp".equals(key) && value != null) {
      try {
        return super.put(key, (Date)value);
      } catch (ClassCastException x) {
        throw new IllegalArgumentException("Timestamp must be Date", x);
      }
    }
    return super.put(key, value);
  }
  
}
