package org.freehep.conditions.base;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.*;
import org.freehep.conditions.ConditionsSet;
import org.freehep.conditions.ConditionsInvalidException;

/**
 * Base class to simplify implementing {@link ConditionsSet}.
 * 
 * All getters that do not specify default value behave as if this conditions set is empty.
 * Getters that specify default value are implemented to forward the call to corresponding
 * getters with no default, but return the default value if the latter throw IllegalArgumentException.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
abstract public class AbstractConditionsSet extends DefaultConditions implements ConditionsSet {
  
// -- Construction : -----------------------------------------------------------
  
  public AbstractConditionsSet(ConditionsReader reader, String name) {
    super(reader, name);
  }
 

// -----------------------------------------------------------------------------
  
  /** Returns this <tt>Conditions</tt> category. */
  @Override
  public Category getCategory() {
    return Category.SET;
  }


// -- Content setters : --------------------------------------------------------
  
  /**
   * Sets the key-to-value map.
   */
  abstract public void set(Properties properties);
  
  /**
   * Loads <tt>Properties</tt> instance from the specified stream, and sets the key-to-value map.
   * @throws ConditionsInvalidException if loading fails.
   */
  abstract public void set(InputStream stream) throws ConditionsInvalidException;
  
  /**
   * Sets the tabular data content of this <tt>ConditionsSet</tt> based on the specified <tt>ResultSet</tt>.
   */
  abstract public void set(ResultSet resultSet) throws ConditionsInvalidException;

  
// -- Map of keys to values (implemented for sets that do not contain a map) : -

  /**
   * Returns the number of key-value pairs in this conditions set.
   * Implemented to return zero.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int size() throws ConditionsInvalidException {
    checkValidity();
    return 0;
  }

  /**
   * Returns the set of keys of this conditions set.
   * Implemented to return an empty set.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Set<String> keySet() throws ConditionsInvalidException {
    checkValidity();
    return Collections.emptySet();
  }

  /**
   * Returns <tt>true</tt> if this conditions set contains value for the specified key.
   * Implemented to return <tt>false</tt>.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public boolean containsKey(String key) throws ConditionsInvalidException {
    checkValidity();
    return false;
  }

  /**
   * Returns the type of value mapped to the specified key by this conditions set.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if this conditions set does not have a value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Class getType(String key) throws ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns boolean value corresponding to the specified key.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public boolean getBoolean(String key) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns integer value corresponding to the specified key.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int getInt(String key) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns long value corresponding to the specified key.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public long getLong(String key) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns double value corresponding to the specified key.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public double getDouble(String key) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns double array value corresponding to the specified key.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public double[] getDoubleArray(String key) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns boolean value corresponding to the specified key.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Date getDate(String key) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns String value corresponding to the specified key.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public String getString(String key) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns the value corresponding to the specified key.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Object getObject(String key) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }


// -- Getters with defaults (map of keys to values) : --------------------------

  /**
   * Returns boolean value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * <p>
   * Implemented to forward the call to {@link getBoolean(String)} but return default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public boolean getBoolean(String key, boolean defaultValue) {
    try {
      return getBoolean(key);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns int value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * <p>
   * Implemented to forward the call to {@link getInt(String)} but return default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int getInt(String key, int defaultValue) {
    try {
      return getInt(key);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns long value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * <p>
   * Implemented to forward the call to {@link getLong(String)} but return default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public long getLong(String key, long defaultValue) {
    try {
      return getLong(key);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns double value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * <p>
   * Implemented to forward the call to {@link getDouble(String)} but return default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public double getDouble(String key, double defaultValue) {
    try {
      return getDouble(key);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns Date value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * <p>
   * Implemented to forward the call to {@link getDate(String)} but return default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Date getDate(String key, Date defaultValue) {
    try {
      return getDate(key);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns String value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * <p>
   * Implemented to forward the call to {@link getString(String)} but return default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public String getString(String key, String defaultValue) {
    try {
      return getString(key);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * <p>
   * Implemented to forward the call to {@link getObject(String)} but return default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Object getObject(String key, Object defaultValue) {
    try {
      return getString(key);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }
  
  
// -- Tabular data (implemented for empty table) : -----------------------------

  /**
   * Returns the number of rows in this <tt>ConditionsSet</tt>.
   * Implemented to return zero.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int getRowCount() throws ConditionsInvalidException {
    checkValidity();
    return 0;
  }
  
  /**
   * Returns the number of columns in this <tt>ConditionsSet</tt>.
   * Implemented to return zero.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int getColumnCount() throws ConditionsInvalidException {
    checkValidity();
    return 0;
  }

  /**
   * Returns the set of column names of this conditions table.
   * Implemented to return an empty set.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Set<String> columnSet() throws ConditionsInvalidException {
    checkValidity();
    return Collections.emptySet();
  }

  /**
   * Returns <tt>true</tt> if this conditions table contains column with the specified name.
   * Implemented to return <tt>false</tt>.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public boolean containsColumn(String columnName) throws ConditionsInvalidException {
    checkValidity();
    return false;
  }
  
  /**
   * Returns the type of values in the specified column.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   * @throws IllegalArgumentException if there is no column with the specified name.
   */
  @Override
  public Class<?> getColumnType(String columnName) throws ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  } 

  /**
   * Returns boolean value corresponding to the specified key and row.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public boolean getBoolean(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns integer value corresponding to the specified key and row.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int getInt(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns integer value corresponding to the specified key and row.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public long getLong(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns double value corresponding to the specified key and row.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public double getDouble(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns double array value corresponding to the specified key and row.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public double[] getDoubleArray(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns boolean value corresponding to the specified key and row.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Date getDate(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns String value corresponding to the specified key and row.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public String getString(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }

  /**
   * Returns String value corresponding to the specified key and row.
   * Implemented to throw IllegalArgumentException.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Object getObject(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    checkValidity();
    throw new IllegalArgumentException();
  }
  
  
// -- Getters with defaults (tabular data) : -----------------------------------

  /**
   * Returns boolean value corresponding to the specified row and column name.
   * Returns the supplied default if there is no currently valid value for the given row and name.
   * <p>
   * Implemented to forward the call to {@link getBoolean(int, String)} but return the default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public boolean getBoolean(int row, String columnName, boolean defaultValue) {
    try {
      return getBoolean(row, columnName);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns int value corresponding to the specified row and column name.
   * Returns the supplied default if there is no currently valid value for the given row and name.
   * <p>
   * Implemented to forward the call to {@link getInt(int, String)} but return the default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int getInt(int row, String columnName, int defaultValue) {
    try {
      return getInt(row, columnName);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns long value corresponding to the specified row and column name.
   * Returns the supplied default if there is no currently valid value for the given row and name.
   * <p>
   * Implemented to forward the call to {@link getLong(int, String)} but return the default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public long getLong(int row, String columnName, long defaultValue) {
    try {
      return getLong(row, columnName);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns double value corresponding to the specified row and column name.
   * Returns the supplied default if there is no currently valid value for the given row and name.
   * <p>
   * Implemented to forward the call to {@link getDouble(int, String)} but return the default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public double getDouble(int row, String columnName, double defaultValue) {
    try {
      return getDouble(row, columnName);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns Date value corresponding to the specified row and column name.
   * Returns the supplied default if there is no currently valid value for the given row and name.
   * <p>
   * Implemented to forward the call to {@link getDate(int, String)} but return the default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Date getDate(int row, String columnName, Date defaultValue) {
    try {
      return getDate(row, columnName);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns String value corresponding to the specified row and column name.
   * Returns the supplied default if there is no currently valid value for the given row and name.
   * <p>
   * Implemented to forward the call to {@link getString(int, String)} but return the default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public String getString(int row, String columnName, String defaultValue) {
    try {
      return getString(row, columnName);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }

  /**
   * Returns value corresponding to the specified row and column name.
   * Returns the supplied default if there is no currently valid value for the given row and name.
   * <p>
   * Implemented to forward the call to {@link getObject(int, String)} but return the default value if it throws IllegalArgumentException.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Object getObject(int row, String columnName, Object defaultValue) {
    try {
      return getString(row, columnName);
    } catch (IllegalArgumentException x) {
      return defaultValue;
    }
  }


}
