package org.freehep.conditions;

/**
 * Base interface implemented by all types of conditions.
 *
 * @version $Id: $
 * @author Tony Johnson
 */
public interface Conditions {
  
  public enum Category {RAW, SET, CACHED, OTHER}
  
  /** Returns this <tt>Conditions</tt> category. */
  Category getCategory();

  /** Returns this <tt>Conditions</tt> name. */
  String getName();

  /** Registers a listener to be notified of changes in this <tt>Conditions</tt>. */
  void addConditionsListener(ConditionsListener listener);

  /** Removes a listener from these <tt>Conditions</tt>. */
  void removeConditionsListener(ConditionsListener listener);
  
  /**
   * Invalidates this <tt>Conditions</tt> object and releases all resources associated with it.
   * No data can be retrieved from this object once it has been destroyed. Any listeners that were
   * registered on this <tt>Conditions</tt> will no longer receive notifications.
   */
  void destroy();
}
