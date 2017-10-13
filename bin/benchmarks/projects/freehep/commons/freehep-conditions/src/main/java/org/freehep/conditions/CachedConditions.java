package org.freehep.conditions;

/**
 * General purpose {@link Conditions} class that presents its content as an arbitrary Object.
 * 
 * All getter methods throw <tt>ConditionsInvalidException</tt> if this <tt>CachedConditions</tt>
 * object has not been successfully updated in response to the latest update triggering event.
 * 
 * @version $Id: $
 * @author Tony Johnson
 */
public interface CachedConditions<T> extends Conditions {

  /**
   * Returns the current value of this CachedConditions.
   * @throws IllegalArgumentException if error occurs while retrieving the data. 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  T getCachedData() throws ConditionsInvalidException;
  
}