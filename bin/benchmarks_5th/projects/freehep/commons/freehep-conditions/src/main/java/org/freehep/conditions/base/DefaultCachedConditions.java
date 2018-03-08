package org.freehep.conditions.base;

import org.freehep.conditions.CachedConditions;
import org.freehep.conditions.ConditionsConverter;
import org.freehep.conditions.ConditionsInvalidException;

/**
 * Default implementation of {@link CachedConditions} interface.
 * 
 * @version $Id: $
 * @author Tony Johnson
 */
public class DefaultCachedConditions<T> extends DefaultConditions implements CachedConditions<T> {
  
// -- Private parts : ----------------------------------------------------------

  private final ConditionsConverter<T> _converter;
  private T _data;
  
// -- Construction : -----------------------------------------------------------

  public DefaultCachedConditions(ConditionsReader reader, String name, ConditionsConverter<T> converter) {
    super(reader, name);
    _converter = converter;
  }
  
// -- Getters : ----------------------------------------------------------------
  
  /** Returns this <tt>Conditions</tt> category. */
  @Override
  public Category getCategory() {
    return Category.CACHED;
  }
  
  public Class<T> getType() {
    return _converter.getType();
  }
  
  public ConditionsConverter<T> getConverter() {
    return _converter;
  }


// -- Updating : ---------------------------------------------------------------
  
  /** Sets the content of this CachedConditions. */
  public void set(T data) {
    _data = data;
  }

  @Override
  public void destroy() {
    _data = null;
    super.destroy();
  }


// -- Implementing CachedConditions : ------------------------------------------

  /**
   * Returns the current value of this CachedConditions.
   * If the "invalid" flag is not set but there is no cached content when this method is called,
   * it attempts to retrieve the content by calling the 
   * {@link ConditionsReader#update(DefaultConditions) update(DefaultConditions)} method of the
   * relevant {@link ConditionsReader}.
   * 
   * @throws ConditionsInvalidException if this Conditions is currently unavailable.
   */
  @Override
  public T getCachedData() {
    checkValidity();
//    if (_data == null) {
//      try {
//        getConditionsReader().update(this);
//      } catch (ConditionsInvalidException x) {
//        _invalid = true;
//        throw x;
//      }
//    }
    return _data;
  }

}
