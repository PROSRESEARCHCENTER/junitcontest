package org.freehep.conditions;

/**
 * Thrown to indicate that the {@link ConditionsManager} failed to update itself in preparation
 * for retrieving conditions for the set of configuration parameters requested by the client.
 */
public class ConditionsUpdateException extends RuntimeException {
  
// -- Private parts : ----------------------------------------------------------
  
  private final ConditionsEvent _event;
  
// -- Construction : -----------------------------------------------------------

  public ConditionsUpdateException() {
    super();
    _event = null;
  }

  public ConditionsUpdateException(String message, Throwable cause) {
    super(message, cause);
    _event = null;
  }

  public ConditionsUpdateException(String message) {
    super(message);
    _event = null;
  }

  public ConditionsUpdateException(Throwable cause) {
    super(cause);
    _event = null;
  }

  public ConditionsUpdateException(ConditionsEvent event) {
    super();
    _event = event;
  }

  public ConditionsUpdateException(String message, Throwable cause, ConditionsEvent event) {
    super(message, cause);
    _event = event;
  }

  public ConditionsUpdateException(String message, ConditionsEvent event) {
    super(message);
    _event = event;
  }

  public ConditionsUpdateException(Throwable cause, ConditionsEvent event) {
    super(cause);
    _event = event;
  }
  
// -- Getters : ----------------------------------------------------------------
  
  public ConditionsEvent getTrigger() {
    return _event;
  }

  @Override
  public String getMessage() {
    String message = super.getMessage();
    if (message == null && _event != null) {
      message = "Conditions update for detector "+ _event.getDetector() +" run "+ _event.getRun() +" has failed";
    }
    return message;
  }

}
