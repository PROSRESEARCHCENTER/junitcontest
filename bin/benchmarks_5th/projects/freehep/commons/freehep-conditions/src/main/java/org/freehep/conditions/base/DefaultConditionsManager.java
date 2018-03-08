package org.freehep.conditions.base;

import org.freehep.conditions.EventFilter;
import org.freehep.conditions.ConditionsManager;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.freehep.conditions.CachedConditions;
import org.freehep.conditions.Conditions;
import org.freehep.conditions.ConditionsConverter;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.ConditionsListener;
import org.freehep.conditions.ConditionsUpdateException;
import org.freehep.conditions.ConditionsSet;
import org.freehep.conditions.ConditionsInvalidException;
import org.freehep.conditions.RawConditions;

/**
 * Default implementation of {@link ConditionsManager}.
 * <p>
 * This manager delegates actual creation and updating of {@link Conditions} objects
 * to {@link ConditionsReader} instances that handle specific conditions storage types.
 * <p>
 * Conditions names are expected to be in the <pre>prefix:id</pre> format (both <tt>prefix</tt>
 * and <tt>id</tt> may be empty). The prefix is used to select the appropriate {@link ConditionsReader}.
 * The prefix is the part of the name before the first occurrence of the ":" symbol
 * if it matches one of the prefixes made known to this <tt>ConditionsManager</tt> through
 * calls to {@link #addConditionReader addConditionReader(...)} method; otherwise, the prefix
 * is an empty string; if an empty string is not a known prefix either, the name is considered 
 * invalid, and {@link ConditionsNotFoundException} is thrown.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class DefaultConditionsManager extends ConditionsManager {
  
// -- Private parts : ----------------------------------------------------------
  
  protected ConditionsEvent _lastEvent;
  
  protected ConcurrentHashMap<Class,ConditionsConverter> _converters;
  protected CopyOnWriteArraySet<ConditionsListener> _listeners;
  protected EventFilter _filter;
  
  protected ConcurrentHashMap<String,DefaultRawConditions> _raw;
  protected ConcurrentHashMap<String,AbstractConditionsSet> _sets;
  protected ConcurrentHashMap<String,ArrayList<DefaultCachedConditions>> _cached;
  
  protected ArrayList<ConditionsReader> _readers;
  protected HashMap<String,ConditionsReader> _readerMap = new HashMap<>(8);
  
  protected File _home;

  private String _dateFormat = "yyyy-MM-dd HH:mm:ss";
  private ThreadLocal<DateFormat> _dateFormatHolder;
  
// -- Construction : -----------------------------------------------------------
  
  public DefaultConditionsManager() {
  }

// -- Triggering changes : -----------------------------------------------------

  /**
   * Trigger checking whether reading conditions from storage and updating <tt>Conditions</tt> objects is necessary.
   * Normally called by the framework to notify this manager of any changes.
   *
   * @param trigger An event that provides information required by this manager to fetch relevant conditions data from the storage.
   * @throws ConditionsUpdateException If the conditions specified by the configuration event can not be found.
   */
  @Override
  synchronized public void update(ConditionsEvent trigger) throws ConditionsUpdateException {
    
    // filter triggering events
    
    if (_filter != null && !_filter.pass(trigger)) {
      _lastEvent = trigger;
      return;
    }
    trigger.setConditionsManager(this);
    trigger.setSource(null);
    
    // compile a list of readers whose conditions might have changed
    
    List<ConditionsReader> changedReaders = null;
    for (ConditionsReader reader : _readers) {
      boolean readerChanged = reader.update(trigger);
      if (readerChanged) {
        if (changedReaders == null) changedReaders = new ArrayList<>(_readers.size());
        changedReaders.add(reader);
      }
    }
    if (changedReaders == null) {
      _lastEvent = trigger;
      return;
    }
    
    // notify conditions whose readers indicated possibility of changes
    
    ArrayList<DefaultConditions> all = new ArrayList<>();
    if (_raw != null) all.addAll(_raw.values());
    if (_sets != null) all.addAll(_sets.values());
    if (_cached != null) {
      for (ArrayList<DefaultCachedConditions> cond : _cached.values()) {
        all.addAll(cond);
      }
    }
    ArrayList<DefaultConditions> changed = new ArrayList<>(all.size());
    for (DefaultConditions condition : all) {
      ConditionsReader reader = condition.getConditionsReader();
      for (ConditionsReader read : changedReaders) {
        if (read == reader) {
          try {
            if (condition.update(trigger)) changed.add(condition);
          } catch (ConditionsInvalidException x) {
            changed.add(condition);
          }
        }
      }
    }
    
    // notify listeners
    
    for (DefaultConditions c : changed) {
      c.fireConditionsEvent(trigger);
    }
    if (_listeners != null) {
      for (ConditionsListener listener : _listeners) {
        listener.conditionsChanged(trigger);
      }
    }
    
    _lastEvent = trigger;
  }

  /**
   * Trigger checking whether reading conditions from storage and updating <tt>Conditions</tt> objects is necessary.
   * Normally called by the framework to notify this manager of any changes.
   *
   * @param detector Detector name (<tt>null</tt> if unknown).
   * @param run Run number (<tt>null</tt> if unknown). 
   * @param timestamp Time stamp (<tt>null</tt> if unknown).
   * @throws ConditionsUpdateException If the conditions specified by the configuration event can not be found.
   */
  @Override
  public void update(String detector, Integer run, Date timestamp) throws ConditionsUpdateException {
    update(new ConditionsEvent(detector, run, timestamp));
  }

  
// -- Getters : ----------------------------------------------------------------
  
  /** 
   * Returns default instance of <tt>DateFormat</tt> to be used by readers and conditions for parsing and formatting timestamps.
   * Equal but distinct instances are returned if this method is called from different threads.
   */
  public DateFormat getDateFormat() {
    if (_dateFormatHolder == null) setDateFormat(_dateFormat);
    return _dateFormatHolder.get();
  }
  
  /**
   * Returns root directory used by the conditions framework for local storage.
   */
  public File getConditionsHome() {
    if (_home == null) {
      _home = new File(System.getProperty("user.home"), ".conditions");
    }
    return _home;
  }

  
  /**
   * Returns current configuration of the conditions system - the last event received by this ConditionsManager.
   * The event can be user to extract current detector name, run number, and other parameters.
   */
  @Override
  public ConditionsEvent getConfiguration() {
    return _lastEvent;
  }
  
  /**
   * Returns <tt>ConditionsReader</tt> that should be used for creating and updating {@link Conditions} with the specified name.
   * The reader is selected from those added with calls to 
   * {@link #addConditionReader addConditionReader(...)} based on the conditions name prefix. 
   * The prefix is the part of the name before the first occurrence of the ":" symbol if it 
   * matches one of the prefixes known to this <tt>ConditionsManager</tt>; otherwise, the
   * prefix is an empty string; if an empty string is not a known prefix either,
   * <tt>null</tt> is returned.
   */
  public ConditionsReader getConditionsReader(String conditionsName) {
    int i = conditionsName.indexOf(":");
    if (i == -1) {
      return _readerMap.get("");
    } else {
      String prefix = conditionsName.substring(0, i);
      ConditionsReader reader = _readerMap.get(prefix);
      return reader == null ? _readerMap.get("") : reader;
    }
  }
  
  /**
   * Returns converter for creating data objects of the specified type.
   */
  public <T> ConditionsConverter<T> getConditionsConverter(Class<T> type) {
    return _converters == null ? null : _converters.get(type);
  }

  /**
   * Returns raw conditions associated with the given name.
   *
   * @param name The name of the conditions requested.
   * @throws ConditionsInvalidException if the requested conditions do not exist and cannot be created.
   */
  @Override
  public DefaultRawConditions getRawConditions(String name) throws ConditionsInvalidException {
    DefaultRawConditions out = getRaw(name);
    if (out == null) {
      ConditionsReader conReader = getConditionsReader(name);
      if (conReader == null) throw new ConditionsInvalidException("No ConditionsReader for "+ name);
      out = conReader.createRawConditions(name);
      try {
        conReader.update(out);
      } catch (ConditionsInvalidException x) {
      }
      _raw.put(name, out);
    }
    return out;
  }

  /**
   * Returns conditions set associated with the given name.
   *
   * @param name The name of the conditions to search for.
   * @throws ConditionsInvalidException if the requested conditions do not exist and cannot be created.
   */
  @Override
  public ConditionsSet getConditions(String name) throws ConditionsInvalidException {
    AbstractConditionsSet out = getSet(name);
    if (out == null) {
      ConditionsReader conReader = getConditionsReader(name);
      if (conReader == null) throw new ConditionsInvalidException("No ConditionsReader for "+ name);
      out = conReader.createConditions(name);
      try {
        conReader.update(out);
      } catch (ConditionsInvalidException x) {
      }
      _sets.put(name, out);
    }
    return out;    
  }

  /**
   * Returns conditions converted to a java object using a conditions converter.
   * The conditions are cached so that they do not need to be re-read each time the same object is requested.
   *
   * @param type The type of conditions requested (used to select an appropriate conditions converter).
   * @param name The name of the conditions requested.
   * @throws ConditionsInvalidException if the requested conditions do not exist and cannot be created.
   */
  @Override
  public <T> CachedConditions<T> getCachedConditions(Class<T> type, String name) throws ConditionsInvalidException {
    DefaultCachedConditions<T> out = getCached(type, name);
    if (out == null) {
      ConditionsConverter<T> converter = getConditionsConverter(type);
      if (converter == null) {
        throw new ConditionsInvalidException("No converter for type "+ type.getName());
      }
      ConditionsReader conReader = getConditionsReader(name);
      if (conReader == null) throw new ConditionsInvalidException("No ConditionsReader for "+ name);
      out = conReader.createCachedConditions(name, converter);
      try {
        conReader.update(out);
      } catch (ConditionsInvalidException x) {
      }
      _cached.get(name).add(out);
    }
    return out;    
  }
  
  /**
   * Remove all references to the specified <tt>Conditions</tt> instance.
   */
  public void removeConditions(DefaultConditions conditions) {
    String name = conditions.getName();
    if (conditions instanceof RawConditions) {
      if (_raw != null && _raw.get(name) == conditions) _raw.remove(name);
    } else if (conditions instanceof ConditionsSet) {
      if (_sets != null && _sets.get(name) == conditions) _sets.remove(name);
    } else if (conditions instanceof CachedConditions) {
      if (_cached != null) {
        ArrayList<DefaultCachedConditions> list = _cached.get(name);
        Iterator<DefaultCachedConditions> it = list.iterator();
        while (it.hasNext()) {
          if (it.next() == conditions) {
            it.remove();
            break;
          }
        }
        if (list.isEmpty()) _cached.remove(name);
      }
    }
  }
  
  /**
   * Returns a list of known <tt>Conditions</tt> in the specified category.
   * The <tt>Conditions</tt> is "known" if it has been obtained from this <tt>ConditionsManager</tt>
   * by calling one of the <tt>getXxxConditions(...)</tt> methods, and has not been removed after that.
   */
  @Override
  public List<Conditions> listConditions(Conditions.Category category) {
    switch (category) {
      case RAW:
        if (_raw == null) {
          return Collections.emptyList();
        } else {
          List<Conditions> out = new ArrayList<>(_raw.size());
          out.addAll(_raw.values());
          return out;
        }
      case SET:
        if (_sets == null) {
          return Collections.emptyList();
        } else {
          List<Conditions> out = new ArrayList<>(_sets.size());
          out.addAll(_sets.values());
          return out;
        }
      case CACHED:
        if (_cached == null) {
          return Collections.emptyList();
        } else {
          List<Conditions> out = new ArrayList<>();
          for (ArrayList<DefaultCachedConditions> cond : _cached.values()) {
            out.addAll(cond);
          }
          return out;
        }
      default:
        return Collections.emptyList();
    }
  }


// -- Setters : ----------------------------------------------------------------
  
  /** 
   * Sets default instance of <tt>DateFormat</tt> to be used by readers and conditions for parsing and formatting timestamps.
   * @param pattern Pattern to be provided to {@link SimpleDateFormat} constructor.
   */
  public void setDateFormat(String pattern) {
    new SimpleDateFormat(pattern); // validate pattern
    _dateFormat = pattern;
    _dateFormatHolder = new ThreadLocal<DateFormat>() {
      protected DateFormat initialValue() {
        return new SimpleDateFormat(_dateFormat);
      }
    };
  }
  
  /**
   * Sets root directory to be used by the conditions framework for local storage.
   */
  public void setConditionsHome(File directory) {
    _home = directory;
  }
  
  /**
   * Sets a pass to be applied to set triggers.
   * Only events that pass the pass will trigger set of conditions and listeners notification. 
   */
  public void setFilter(EventFilter filter) {
    _filter = filter;
  }
  
  /**
   * Sets <tt>ConditionsReader</tt> to be used for creating and updating <tt>Conditions</tt> whose names start with one of the specified prefixes.
   * If <tt>ConditionsReader</tt> has already been set for one of the specified prefixes, it is replaced by the 
   * <tt>ConditionsReader</tt> passed to this method, and <tt>IllegalArgumentException</tt> is thrown at the end
   * of the method call. Calling this method without specifying any prefixes adds the default <tt>ConditionsReader</tt>
   * to be used with <tt>Conditions</tt> whose names have no prefixes.
   * 
   * @throws IllegalArgumentException if <tt>ConditionsReader</tt> has already been set for one of the specified prefixes. 
   */
  public void addConditionReader(ConditionsReader reader, String... prefixes) {
    IllegalArgumentException x = null;
    if (prefixes.length == 0) prefixes = new String[] {""};
    for (String prefix : prefixes) {
      if (_readerMap.put(prefix, reader) != null) {
        x = new IllegalArgumentException("ConditionsReader for prefix "+ prefix +" has already been set");
      }
    }
    _readers = new ArrayList<>(_readerMap.values());
    if (x != null) throw x;
  }
  
  /**
   * Removes the specified <tt>ConditionsReader</tt>.
   */
  public void removeConditionsReader(ConditionsReader reader) {
    Iterator<Map.Entry<String,ConditionsReader>> it = _readerMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String,ConditionsReader> e = it.next();
      if (reader == e.getValue()) it.remove();
    }
    _readers = new ArrayList<>(_readerMap.values());
  }
  
  /**
   * Removes <tt>ConditionsReader</tt>s mapped to specified prefixes.
   * If no prefixes are listed, all <tt>ConditionsReader</tt>s are removed.
   */
  public void removeConditionsReaders(String... prefixes) {
    if (prefixes.length == 0) {
      _readerMap.clear();
      _readers = new ArrayList<>(0);
    } else {
      for (String prefix : prefixes) {
        _readerMap.remove(prefix);
      }
      _readers = new ArrayList<>(_readerMap.values());
    }
  }

// -- Handling conditions converters : -----------------------------------------

  /**
   * Adds a conditions converter. A conditions converter can be used to convert
   * data requested by the user into a specific Java object.
   */
  @Override
  public void addConditionsConverter(ConditionsConverter converter) {
    if (_converters == null) _converters = new ConcurrentHashMap<>(8, .75f, 2);
    if (_converters.putIfAbsent(converter.getType(), converter) != null) {
      throw new IllegalArgumentException("Converter for type "+ converter.getType() +" is already registered");
    }
  }

  /**
   * Removes a conditions converter.
   */
  @Override
  public void removeConditionsConverter(ConditionsConverter converter) {
    if (_converters != null) _converters.remove(converter.getType());
  }
  
  
// -- Handling listeners : -----------------------------------------------------

  /**
   * Add a listener to be notified of changes to any conditions.
   */
  @Override
  public void addConditionsListener(ConditionsListener listener) {
    if (_listeners == null) _listeners = new CopyOnWriteArraySet<>();
    _listeners.add(listener);
  }

  /**
   * Remove a global conditions change listener.
   */
  @Override
  public void removeConditionsListener(ConditionsListener listener) {
    if (_listeners != null) _listeners.remove(listener);
  }

  
// -- Local methods and classes : ----------------------------------------------
  
  protected DefaultRawConditions getRaw(String name) {
    if (_raw == null) {
      _raw = new ConcurrentHashMap<>(8, .75f, 2);
      return null;
    } else {
      return _raw.get(name);
    }
  }
  
  protected AbstractConditionsSet getSet(String name) {
    if (_sets == null) {
      _sets = new ConcurrentHashMap<>(16, .75f, 2);
      return null;
    } else {
      return _sets.get(name);
    }
  }
  
  protected <T> DefaultCachedConditions<T> getCached(Class<T> type, String name) {
    if (_cached == null) {
      _cached = new ConcurrentHashMap<>(16, .75f, 2);
      _cached.put(name, new ArrayList<DefaultCachedConditions>(1));
      return null;
    } else {
      ArrayList<DefaultCachedConditions> list = _cached.get(name);
      if (list == null) {
        _cached.put(name, new ArrayList<DefaultCachedConditions>(1));
        return null;
      } else {
        for (DefaultCachedConditions c : list) {
          if (c.getType().equals(type)) {
            return c;
          }
        }
        return null;
      }
    }
  }

}
