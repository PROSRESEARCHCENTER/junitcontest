package org.freehep.conditions;

import java.util.*;
import org.freehep.conditions.base.DefaultConditionsManager;

/**
 * Manager of the conditions system.
 * 
 * @version $Id: $
 * @author Tony Johnson
 */
public abstract class ConditionsManager {
  
// -- Private parts : ----------------------------------------------------------

  private static ConditionsManager _manager;


// -- Access to default instance : ---------------------------------------------
  
  /**
   * Get the default (shared) condition manager implementation.
   *
   * @return The default conditions manager.
   */
  public static ConditionsManager defaultInstance() {
    if (_manager == null) {
      _manager = new DefaultConditionsManager();
    }
    return _manager;
  }

  public static void setDefaultConditionsManager(ConditionsManager manager) {
    _manager = manager;
  }


// -- Triggering changes : -----------------------------------------------------

  /**
   * Trigger checking whether reading conditions from storage and updating <tt>Conditions</tt> objects is necessary.
   * Normally called by the framework to notify this manager of any changes.
   * <p>
   * Note that if this method returns without throwing <tt>ConditionsUpdateException</tt>, this
   * does not guarantee that every existing {@link Conditions} object has been successfully updated.
   * It is up to a specific <tt>ConditionsManager</tt> implementation to decide whether an issue encountered while
   * updating is serious enough to inform the client immediately by throwing <tt>ConditionsUpdateException</tt>.
   * If a specific {@link Conditions} object has not been successfully updated, it will remain in invalid state until the 
   * next update, and any attempt to extract data from it will result in <tt>ConditionsInvalidException</tt>.
   *
   * @param event An event that provides information required by this manager to fetch relevant conditions data from the storage.
   * @throws ConditionsUpdateException If the conditions specified by the configuration event can not be found.
   */
  public abstract void update(ConditionsEvent event) throws ConditionsUpdateException;

  /**
   * Trigger checking whether reading conditions from storage and updating <tt>Conditions</tt> objects is necessary.
   * Normally called by the framework to notify this manager of any changes.
   * <p>
   * Note that if this method returns without throwing <tt>ConditionsUpdateException</tt>, this
   * does not guarantee that every existing {@link Conditions} object has been successfully updated.
   * It is up to a specific <tt>ConditionsManager</tt> implementation to decide whether an issue encountered while
   * updating is serious enough to inform the client immediately by throwing <tt>ConditionsUpdateException</tt>.
   * If a specific {@link Conditions} object has not been successfully updated, it will remain in invalid state until the 
   * next update, and any attempt to extract data from it will result in <tt>ConditionsInvalidException</tt>.
   *
   * @param detector Detector name (<tt>null</tt> if unknown).
   * @param run Run number (<tt>null</tt> if unknown). 
   * @param timestamp Time stamp (<tt>null</tt> if unknown).
   * @throws ConditionsUpdateException If the conditions specified by the configuration event can not be found.
   */
  public abstract void update(String detector, Integer run, Date timestamp) throws ConditionsUpdateException;

  
// -- Getters : ----------------------------------------------------------------
  
  /**
   * Returns current configuration of the conditions system - the last event received by this ConditionsManager.
   * The event can be user to extract current detector name, run number, etc.
   */
  public abstract ConditionsEvent getConfiguration();

  /**
   * Returns conditions set associated with the given name.
   *
   * @param name The name of the conditions to search for.
   * @throws ConditionsInvalidException if the named conditions can not be found.
   */
  public abstract ConditionsSet getConditions(String name) throws ConditionsInvalidException;

  /**
   * Returns conditions converted to a java object using a conditions converter.
   * The conditions are cached so that they do not need to be re-read each time the same object is requested.
   *
   * @param type The type of conditions requested (used to select an appropriate conditions converter).
   * @param name The name of the conditions requested.
   * @throws ConditionsInvalidException if the specified conditions can not be found.
   */
  public abstract <T> CachedConditions<T> getCachedConditions(Class<T> type, String name) throws ConditionsInvalidException;

  /**
   * Returns raw conditions associated with the given name.
   *
   * @param name The name of the conditions requested.
   * @throws ConditionsInvalidException if the requested conditions can not be found.
   */
  public abstract RawConditions getRawConditions(String name) throws ConditionsInvalidException;
  
  /**
   * Returns a list of known <tt>Conditions</tt> in the specified category.
   * The definition of "known" may be implementation-dependent.
   */
  public abstract List<Conditions> listConditions(Conditions.Category category);
  

// -- Handling conditions converters : -----------------------------------------

  /**
   * Adds a conditions converter. A conditions converter can be used to convert
   * data requested by the user into a specific Java object.
   */
  public abstract void addConditionsConverter(ConditionsConverter conv);

  /**
   * Removes a conditions converter.
   */
  public abstract void removeConditionsConverter(ConditionsConverter conv);
  
  
// -- Handling listeners : -----------------------------------------------------

  /**
   * Add a listener to be notified of changes to ANY conditions.
   */
  public abstract void addConditionsListener(ConditionsListener listener);

  /**
   * Remove a global change listener.
   */
  public abstract void removeConditionsListener(ConditionsListener listener);

}
