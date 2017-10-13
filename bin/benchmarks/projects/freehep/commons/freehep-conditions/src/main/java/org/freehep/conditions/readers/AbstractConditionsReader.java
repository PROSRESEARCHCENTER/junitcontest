package org.freehep.conditions.readers;

import org.freehep.conditions.EventFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.freehep.conditions.ConditionsConverter;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.ConditionsUpdateException;
import org.freehep.conditions.ConditionsInvalidException;
import org.freehep.conditions.base.AbstractConditionsSet;
import org.freehep.conditions.base.ConditionsReader;
import org.freehep.conditions.base.DefaultCachedConditions;
import org.freehep.conditions.base.DefaultConditions;
import org.freehep.conditions.base.DefaultConditionsManager;
import org.freehep.conditions.base.DefaultConditionsSet;
import org.freehep.conditions.base.DefaultRawConditions;

/**
 * Base class for implementing {@link ConditionsReader}s.
 * <p>
 * In addition to implementing the default conditions creation and updating mechanism
 * (see <tt>createXXX(...)</tt> and <tt>update(...)</tt> methods documentation for details),
 * this class provides machinery for maintaining current configuration of the reader -
 * a map of keys to values based on processed update triggering events. For an example of
 * how this configuration can be used, see {@link URLConditionsReader} and {@link DatabaseConditionsReader}.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
abstract public class AbstractConditionsReader implements ConditionsReader {

// -- Private parts : ----------------------------------------------------------
  
  private final DefaultConditionsManager _manager;
  
  /** Filter currently set on this reader. */
  protected EventFilter _filter;
  /**
   * Current configuration (based on parameters of previously processed update triggering events that passed the filter).
   * Maintained only if <tt>setConfiguration(...)</tt> has been called before a call to {@link #update(ConditionsEvent)}.
   */
  protected LinkedHashMap<String,Object> _config;
  /** True if configuration parameters should be accumulated from all previously processed events. Default is false. */
  private boolean _configAccumulate = false;
  /** True if configuration should only include a fixed set of parameters. Default is true. */
  private boolean _configFixedSet = true;
  

// -- Construction : -----------------------------------------------------------
  
  protected AbstractConditionsReader(DefaultConditionsManager manager) {
    _manager = manager;
  }


// -- Setters and getters : ----------------------------------------------------
  
  /**
   * Sets a filter to be applied to update triggering events triggers.
   * Replaces any previously set filters. Calling this method with <tt>null</tt> argument
   * removes the currently set filter, if any.
   */
  public void setFilter(EventFilter filter) {
    _filter = filter;
  }
  
  /**
   * Once this method has been called, this reader will maintain current configuration
   * (map of parameter names to parameter values) based on the initial configuration and
   * subsequently processed update triggering events.
   * 
   * @param initialConfiguration Initial configuration. If <tt>null</tt>, the configuration
   *                             will be maintained but no initial values will be set.
   * @param fixed If true, configuration will only include a fixed set of parameters corresponding to keys in the initial configuration.
   *              Other parameters present in update triggering events will be ignored.
   * @param accumulate If true, configuration parameters will be accumulated from all previously processed events.
   *                   If false, only the last update triggering event is used to set current parameter values.
   */
  public void setConfiguration(Map<String,? extends Object> initialConfiguration, boolean fixed, boolean accumulate) {
    _config = initialConfiguration == null ? new LinkedHashMap<String,Object>() : new LinkedHashMap<>(initialConfiguration);
    _configFixedSet = fixed;
    _configAccumulate = accumulate;
  }
  
  /**
   * Returns current configuration 
   * @return Map of parameter names to values. <tt>Null</tt> if the configuration is not maintained.
   */
  public Map<String,Object> getConfiguration() {
    return _config == null ? null : Collections.unmodifiableMap(_config);
  }


// -- Implementing ConditionsReader : ------------------------------------------

  /**
   * Returns <tt>ConditionsManager</tt> this reader is used by.
   */
  @Override
  public DefaultConditionsManager getConditionsManager() {
    return _manager;
  }

  /**
   * Creates <tt>RawConditions</tt> for the given name.
   * <p>
   * Implementation provided by this adapter creates an instance of <tt>DefaultRawConditions</tt> that 
   * will forward its getters to {@link #open open(...)} and {@link #getReader getReader(...)} methods of this reader.
   * Never throws <tt>ConditionsInvalidException</tt>.
   */
  @Override
  public DefaultRawConditions createRawConditions(String name) throws ConditionsInvalidException {
    return new DefaultRawConditions(this, name);
  }

  /**
   * Creates <tt>ConditionsSet</tt> for the given name and converter.
   * <p>
   * Implementation provided by this adapter creates an instance of <tt>DefaultConditionsSet</tt> without populating it with data.
   * Never throws <tt>ConditionsInvalidException</tt>.
   */
  @Override
  public AbstractConditionsSet createConditions(String name) throws ConditionsInvalidException {
    return new DefaultConditionsSet(this, name);
  }

  /**
   * Creates <tt>CachedConditions</tt> for the given name and converter.
   * <p>
   * Implementation provided by this adapter creates an instance of <tt>DefaultCachedConditions</tt> without trying to create its content.
   * Never throws <tt>ConditionsInvalidException</tt>.
   */
  @Override
  public <T> DefaultCachedConditions<T> createCachedConditions(String name, ConditionsConverter<T> converter) throws ConditionsInvalidException {
    return new DefaultCachedConditions<>(this, name, converter);
  }

  /**
   * Opens input stream that can be used by <tt>RawConditions</tt> with the given name.
   * <p>
   * Implementation provided by this adapter treats the given name as URL.
   * 
   * @throws IOException
   * @throws ConditionsInvalidException if the <tt>conditionsName</tt> is not a valid URL.
   */
  @Override
  public InputStream open(String conditionsName) throws IOException, ConditionsInvalidException {
    try {
      URL url = new URL(conditionsName);
      return url.openStream();
    } catch (MalformedURLException x) {
      throw new ConditionsInvalidException(x);
    }
  }

  /**
   * Creates <tt>Reader</tt> that can be used by <tt>RawConditions</tt> with the given name.
   * <p>
   * Implementation provided by this adapter wraps a reader around the stream returned by {@link #open open(...)}.
   * 
   * @throws IOException 
   * @throws ConditionsInvalidException if the <tt>conditionsName</tt> is not a valid URL.
   */
  @Override
  public Reader getReader(String conditionsName) throws IOException, ConditionsInvalidException {
    return new InputStreamReader(open(conditionsName));
  }

  /**
   * Updates this reader if necessary in response to the specified event.
   * This method is called by the conditions framework.
   * <p>
   * Implementation provided by this adapter returns <tt>false</tt> is the event does not pass
   * the filter, or the configuration (based on event parameters) has not changed. Otherwise,
   * the configuration ({@link #_config}) is updated and <tt>true</tt> is returned.
   * 
   * @param event The event triggering this update.
   * @return <tt>True</tt> if conditions handled by this reader may have changed as a result of the update.
   * @throws ConditionsUpdateException if this reader is unable to update itself.
   */
  @Override
  public boolean update(ConditionsEvent event) throws ConditionsUpdateException {
    
    if (_filter != null && !_filter.pass(event)) return false;
    
    if (_config == null) return true;
    
    boolean changed = false;
    if (_configFixedSet) {
      if (_configAccumulate) {  // fixed set of parameters, accumulating
        for (Map.Entry<String, Object> e : _config.entrySet()) {
          if (event.containsKey(e.getKey())) {
            Object current = event.get(e.getKey());
            Object old = e.setValue(current);
            changed = changed || !(old == null ? current == null : old.equals(current));
          }
        }
      } else {                  // fixed set of parameters, not accumulating
        for (Map.Entry<String, Object> e : _config.entrySet()) {
          Object current = event.get(e.getKey());
          Object old = e.setValue(current);
          changed = changed || !(old == null ? current == null : old.equals(current));
        }
      }
    } else {
      if (_configAccumulate) {  // all parameters, accumulating
        for (Map.Entry<String, Object> e : event.entrySet()) {
          Object current = e.getValue();
          if (current == null) {
            Object old = _config.remove(e.getKey());
            changed = changed || (old != null);
          } else {
            Object old = _config.put(e.getKey(), current);
            changed = changed || !current.equals(old);
          }
        }
      } else {                  // all parameters, not accumulating
        Iterator<Map.Entry<String,Object>> it = _config.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<String,Object> e = it.next();
          Object current = event.get(e.getKey());
          if (current == null) {
            it.remove();
            changed = true;
          }
        }
        for (Map.Entry<String, Object> ee : event.entrySet()) {
          Object current = ee.getValue();
          if (current != null) {
            Object old = _config.put(ee.getKey(), current);
            changed = changed || !current.equals(old);
          }
        }
      }
    }
    return changed;
  }

  /**
   * Updates the specified Conditions if necessary.
   * This method is called by the conditions framework. The method is called after the reader itself has been updated in a call to
   * {@link #update(ConditionsEvent)}, and only if that call returned <tt>true</tt>.
   * <p>
   * Implementation provided by this adapter:
   * <ul>
   * <li>If the argument is an instance of {@link DefaultRawConditions}, does nothing and returns <tt>true</tt>.</li>
   * <li>If the argument is an instance of {@link DefaultCachedConditions}, re-fetches the content data
   *     by calling <tt>ConditionsConverter.getData()</tt>. Returns <tt>true</tt>.</li>
   * <li>Otherwise, throws ConditionsInvalidException.</li>
   * </ul>
   * @return <tt>True</tt> if the Conditions have changed as a result of the update.
   * @throws ConditionsInvalidException if this reader is unable to update the specified <tt>Conditions</tt>.
   */
  @Override
  public boolean update(DefaultConditions conditions) throws ConditionsInvalidException {
    if (conditions instanceof DefaultCachedConditions) {
      DefaultCachedConditions conCached = (DefaultCachedConditions) conditions;
      try {
        conCached.set(conCached.getConverter().getData(_manager, conCached.getName()));
      } catch (IllegalArgumentException x) {
        throw new ConditionsInvalidException("Failed to retrieve data of type "+ conCached.getConverter().getType() +
                                                 " for CachedConditions "+ conCached.getName(), x);
      }
    } else if (conditions instanceof DefaultRawConditions) {
    } else {
      throw new ConditionsInvalidException(getClass().getName() +" cannot update "+ conditions.getClass().getName());
    }
    return true;
  }
  
}
