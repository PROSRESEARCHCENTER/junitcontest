package org.freehep.conditions;

/**
 * Exception thrown to indicate that a specific {@link Conditions} does not exist or is in invalid state.
 */
public class ConditionsInvalidException extends RuntimeException {

  public ConditionsInvalidException() {
    super();
  }

  public ConditionsInvalidException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConditionsInvalidException(String message) {
    super(message);
  }

  public ConditionsInvalidException(Throwable cause) {
    super(cause);
  }

}
