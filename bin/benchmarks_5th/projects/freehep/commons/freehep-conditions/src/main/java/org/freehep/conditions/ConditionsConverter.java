package org.freehep.conditions;

/**
 * Converter that creates objects to be accessed through {@link CachedConditions}.
 * 
 * @version $Id: $
 * @author Tony Johnson
 */
public interface ConditionsConverter<T> {

  /**
   * Returns the type of the object created by this converter.
   */
  Class<T> getType();

  /**
   * Returns the current value of the content.
   * @throws IllegalArgumentException if the content object cannot be created or retrieved.
   */
  T getData(ConditionsManager manager, String name);
  
}