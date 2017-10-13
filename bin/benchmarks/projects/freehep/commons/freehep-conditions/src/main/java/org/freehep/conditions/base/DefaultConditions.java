package org.freehep.conditions.base;

import org.freehep.conditions.EventFilter;
import java.util.concurrent.CopyOnWriteArraySet;
import org.freehep.conditions.Conditions;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.ConditionsListener;
import org.freehep.conditions.ConditionsInvalidException;
import org.freehep.conditions.ConditionsManager;

/**
 * Base class for implementing various types of conditions.
 * <p/>
 * The class provides machinery for handling listeners and setting filters, accessors for {@link ConditionsManager}
 * and {@link ConditionsReader}.
 * <p/>
 * The class also specifies additional methods that support conditions updates in the scheme employed by
 * the <tt>org.freehep.conditions.base</tt> package. Two status flags are associated with every instance of 
 * <tt>DefaultConditions</tt>, along with public getters. The "changed" flag is set to indicate that this 
 * <tt>Conditions</tt> has been updated, but its listeners has not yet been notified. The "invalid" flag indicates
 * that the latest attempt to fetch the current data for this <tt>Conditions</tt> from storage has failed. Subclasses are
 * expected to ensure that any attempt to access the content of this <tt>Conditions</tt> while this flag is set
 * results in <tt>ConditionsInvalidException</tt> without further attempts to retrieve the data from storage.
 * The flag should be reset whenever the content of this Conditions is modified.
 * <p/>
 * The <tt>update(ConditionsEvent)</tt> is implemented to apply the filter, if any, then
 * call <tt>ConditionsReader.update(this)</tt>. The value returned by that call is used to set
 * the "changed" flag. Subsequent call to <tt>fireConditionsEvent(ConditionsEvent)</tt>
 * will do nothing unless this flag is set. The "invalid" flag is set based on whether or not the 
 * <tt>ConditionsReader.update(this)</tt> call throws <tt>ConditionsInvalidException</tt>.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class DefaultConditions implements Conditions {
  
// -- Private parts : ----------------------------------------------------------

  private String _name;
  private ConditionsReader _reader;  
  protected EventFilter _filter;
  protected CopyOnWriteArraySet<ConditionsListener> _listeners;
  
  protected boolean _changed = false;
  protected boolean _invalid = true;


// -- Construction : -----------------------------------------------------------

  /**
   * Creates a new instance of DefaultConditions.
   */
  public DefaultConditions(ConditionsReader reader, String name) {
    _reader = reader;
    _name = name;
  }
  
  
// -- Getters : ----------------------------------------------------------------
  
  /** Returns this <tt>Conditions</tt> type. */
  @Override
  public Category getCategory() {
    return Category.OTHER;
  }

  /** Returns this Conditions name. */
  @Override
  public String getName() {
    return _name;
  }

  /** Returns the conditions framework manager instance. */
  public DefaultConditionsManager getConditionsManager() {
    return _reader.getConditionsManager();
  }
  
  /** Returns ConditionsReader that created this Conditions object and handles its updates. */
  public ConditionsReader getConditionsReader() {
    return _reader;
  }
  
  /** Returns <tt>true</tt> if the "changed" flag is set. */
  public boolean isChanged() {
    return _changed;
  }
  
  /** Returns <tt>true</tt> if the "invalid" flag is set. */
  public boolean isInvalid() {
    return _invalid;
  }
  
  
// -- Setters : ----------------------------------------------------------------
  
  /**
   * Sets a filter that will be applied to update triggers.
   * Only events that pass the filter will trigger update of this Conditions and listener notification. 
   */
  public void setFilter(EventFilter filter) {
    _filter = filter;
  }
  
//  public void setChanged(boolean changed) {
//    _changed = changed;
//  }
  
//  public void setInvalid(boolean invalid) {
//    _invalid = invalid;
//  }
  
// -- Updating : ---------------------------------------------------------------

  /**
   * Called by the framework to notify this <tt>Conditions</tt> of possible changes.
   * This method is called by {@link DefaultConditionsManager} after the {@link ConditionsReader}
   * that handles this <tt>Conditions</tt> has been updated if the reader indicated possibility
   * of changes.
   * <p>
   * Implementation provided by this class does nothing and returns <tt>true</tt> if the "changed" 
   * flag is already set. Otherwise, it returns <tt>false</tt> if the "invalid" flag is not set and the triggering
   * event does not pass the filter associated with this <tt>Conditions</tt> instance. Otherwise,
   * it attempts to update this <tt>Conditions</tt> by calling the 
   * {@link ConditionsReader#update(DefaultConditions) update(DefaultConditions)} method of its
   * {@link ConditionsReader}, and resets the "invalid" flag.
   * <p>
   * Subclasses may override this method to implement different update strategies.
   * 
   * @return True if this Conditions has changed as a result of the update.
   * @throws ConditionsInvalidException if the update fails.
   */
  protected boolean update(ConditionsEvent trigger) throws ConditionsInvalidException {
    checkForDestruction();
    if (_changed) return true;
    if (_invalid || _filter == null || _filter.pass(trigger)) {
      try {
        _changed = getConditionsReader().update(this);
        _invalid = false;
      } catch (ConditionsInvalidException x) {
        _changed = !_invalid;
        _invalid = true;
        throw x;
      }
    }
    return _changed;
  }

  /**
   * Invalidates this <tt>Conditions</tt> object and releases all resources associated with it.
   * No data can be retrieved from this object once it has been destroyed. Any listeners that were
   * registered on this <tt>Conditions</tt> will no longer receive notifications.
   */
  @Override
  public void destroy() {
    getConditionsManager().removeConditions(this);
    _name = null;
    _reader = null;
    _filter = null;
    _listeners = null;
    _changed = false;
    _invalid = true;
  }
  
  
// -- State checks : -----------------------------------------------------------
  
  /**
   * Throws IllegalStateException if this <tt>Conditions</tt> object has been previously destroyed.
   */
  protected void checkForDestruction() {
    if (_name == null) throw new IllegalStateException("Attempt to use destroyed conditions object");
  }

  /**
   * Checks validity of this  <tt>Conditions</tt>.
   * @throws IllegalStateException if this <tt>Conditions</tt> object has been previously destroyed.
   * @throws ConditionsInvalidException if the "invalid" flag is set.
   */
  protected void checkValidity() {
    if (_name == null) throw new IllegalStateException("Attempt to use destroyed conditions object");
    if (isInvalid()) throw new ConditionsInvalidException("Conditions "+ getName() +" is currently invalid");
  }


// -- Handling listeners : -----------------------------------------------------

  /** Registers a listener to be notified of changes in this Conditions. */
  @Override
  public void addConditionsListener(ConditionsListener listener) {
    checkForDestruction();
    if (_listeners == null) _listeners = new CopyOnWriteArraySet<>();
    _listeners.add(listener);
  }

  /** Removes a listener from these conditions. */
  @Override
  public void removeConditionsListener(ConditionsListener listener) {
    checkForDestruction();
    if (_listeners != null) _listeners.remove(listener);
  }
  
  /**
   * Notifies listeners of changes in this Conditions.
   */
  protected void fireConditionsEvent(ConditionsEvent trigger) {
    if (!_changed) return;
    _changed = false;
    if (_listeners != null) {
      ConditionsEvent event = new ConditionsEvent(this, trigger);
      for (ConditionsListener listener : _listeners) {
        listener.conditionsChanged(event);
      }
    }
  }

}