package org.freehep.conditions;

/**
 * A filter to be applied to events that might trigger conditions updates.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public interface EventFilter {
  
  /**
   * Filters update triggering events.
   * This method can have side effects and/or modify the event.
   * 
   * @param event Trigger event.
   * @return <tt>True</tt> if the event may trigger updates and therefore needs to be further processed.
   */
  boolean pass(ConditionsEvent event);

}
