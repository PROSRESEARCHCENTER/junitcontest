package org.freehep.conditions;

import java.util.EventListener;

/**
 * Interface to be implemented by objects that need to be notified of changes in {@link Conditions}.
 * 
 * @version $Id: $
 * @author Tony Johnson
 */
public interface ConditionsListener extends EventListener {

  /**
   * Called when the conditions this listener is registered on change.
   *
   * @param event The event associated with the change.
   */
  void conditionsChanged(ConditionsEvent event);
  
}
