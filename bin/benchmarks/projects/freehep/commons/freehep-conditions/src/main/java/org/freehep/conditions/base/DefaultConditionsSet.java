package org.freehep.conditions.base;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.*;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import org.freehep.conditions.ConditionsSet;
import org.freehep.conditions.ConditionsInvalidException;

/**
 * Default implementation of {@link ConditionsSet}.
 * An object of this type contains a map of String keys to values backed by {@link Properties}
 * instance, and tabular data backed by {@link CachedRowSet} instance. 
 * Rows in the table are numbered starting with zero. Getters that return
 * values of a specific type attempt to convert the data to that type, and throw 
 * IllegalArgumentException if the conversion is impossible.
 * <p>
 * Update policy is inherited from {@link DefaultConditions}.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class DefaultConditionsSet extends AbstractConditionsSet {
  
// -- Private parts : ----------------------------------------------------------
  
  protected Properties _props;
  protected CachedRowSet _table;
  
  
// -- Construction : -----------------------------------------------------------
  
  public DefaultConditionsSet(ConditionsReader reader, String name) {
    super(reader, name);
  }
  
  
// -- Content setters and updating: --------------------------------------------
  
  /**
   * Sets the key-to-value map.
   * The <tt>Properties</tt> object passed to this method will be owned by this <tt>ConditionsSet</tt>.
   */
  @Override
  public void set(Properties properties) {
    _props = properties;
  }
  
  /**
   * Loads <tt>Properties</tt> instance from the specified stream, and sets the key-to-value map.
   * @throws ConditionsInvalidException if loading fails.
   */
  @Override
  public void set(InputStream stream) throws ConditionsInvalidException {
    _props = new Properties();
    try {
      _props.load(stream);
    } catch (IOException x) {
      _props = null;
      throw new ConditionsInvalidException("ConditionsSet "+ getName() +" : failed to load properties ", x);
    }
  }
  
  /**
   * Sets the tabular data content of this <tt>ConditionsSet</tt> based on the specified <tt>ResultSet</tt>.
   */
  @Override
  public void set(ResultSet resultSet) throws ConditionsInvalidException {
    try {
      _table = RowSetProvider.newFactory().createCachedRowSet();
      _table.populate(resultSet);
    } catch (SQLException x) {
      _table = null;
      throw new ConditionsInvalidException("ConditionsSet "+ getName() +" : failed to load database query result set ", x);
    }
  }

  /**
   * Invalidates this <tt>Conditions</tt> object and releases all resources associated with it.
   * No data can be retrieved from this object once it has been destroyed. Any listeners that were
   * registered on this <tt>Conditions</tt> will no longer receive notifications.
   */
  @Override
  public void destroy() {
    _props = null;
    _table = null;
    super.destroy();
  }
  
// -- Map of keys to values (implemented for sets that do not contain a map) : -

  /**
   * Returns the number of key-value pairs in this conditions set.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int size() throws ConditionsInvalidException {
    Properties p = getProperties();
    return p == null ? 0 : getProperties().size();
  }

  /**
   * Returns the set of keys of this conditions set.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Set<String> keySet() throws ConditionsInvalidException {
    Properties p = getProperties();
    return p == null ? Collections.<String>emptySet() : p.stringPropertyNames();
  }

  /**
   * Returns <tt>true</tt> if this conditions set contains value for the specified key.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public boolean containsKey(String key) throws ConditionsInvalidException {
    Properties p = getProperties();
    return p == null ? false : p.containsKey(key);
  }

  /**
   * Returns the type of the value mapped to the specified key by this conditions set.
   * Implemented to return the first type from the sequence int-long-double-boolean-Date-String
   * into which the value can be successfully parsed.
   * 
   * @throws IllegalArgumentException if this conditions set does not have a value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Class getType(String key) throws ConditionsInvalidException {
    Properties p = getProperties();
    if (p == null || p.getProperty(key) == null) throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid key " + key);
    try {
      getInt(key);
      return Integer.TYPE;
    } catch (IllegalArgumentException x) {
      try {
        getLong(key);
        return Long.TYPE;
      } catch (IllegalArgumentException xx) {
        try {
          getDouble(key);
          return Double.TYPE;
        } catch (IllegalArgumentException xxx) {
          try {
            getBoolean(key);
            return Boolean.TYPE;
          } catch (IllegalArgumentException xxxx) {
            try {
              getDate(key);
              return Date.class;
            } catch (IllegalArgumentException xxxxx) {
              return String.class;
            }
          }
        }
      }
    }
  }

  /**
   * Returns boolean value corresponding to the specified key.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public boolean getBoolean(String key) throws IllegalArgumentException, ConditionsInvalidException {
    Properties p = getProperties();
    String value = p == null ? null : p.getProperty(key);
    if (value == null) throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid key " + key);
    value = value.trim();
    if ("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)) return true;
    if ("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value)) return false;
    throw new IllegalArgumentException("ConditionsSet "+ getName() +" : cannot convert "+ value +" to boolean for key " + key);
  }

  /**
   * Returns integer value corresponding to the specified key.
   * 
   * @throws IllegalArgumentException if there is no currently valid integer value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int getInt(String key) throws IllegalArgumentException, ConditionsInvalidException {
    Properties p = getProperties();
    String value = p == null ? null : p.getProperty(key);
    if (value == null) throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid key " + key);
    value = value.trim();
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : cannot convert "+ value +" to int for key " + key, x);
    }
  }

  /**
   * Returns long value corresponding to the specified key.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public long getLong(String key) throws IllegalArgumentException, ConditionsInvalidException {
    Properties p = getProperties();
    String value = p == null ? null : p.getProperty(key);
    if (value == null) throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid key " + key);
    value = value.trim();
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : cannot convert "+ value +" to long for key " + key, x);
    }
  }

  /**
   * Returns double value corresponding to the specified key.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public double getDouble(String key) throws IllegalArgumentException, ConditionsInvalidException {
    Properties p = getProperties();
    String value = p == null ? null : p.getProperty(key);
    if (value == null) throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid key " + key);
    value = value.trim();
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : cannot convert "+ value +" to double for key " + key, x);
    }
  }

  /**
   * Returns double array value corresponding to the specified key.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public double[] getDoubleArray(String key) throws IllegalArgumentException, ConditionsInvalidException {
    Properties p = getProperties();
    String value = p == null ? null : p.getProperty(key);
    if (value == null) throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid key " + key);
    String[] tokens = value.split(",");
    double[] out = new double[tokens.length];
    try {
      for (int i = 0; i < tokens.length; i++) {
        out[i] = Double.parseDouble(tokens[i].trim());
      }
    } catch (NumberFormatException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : cannot convert "+ value +" to double array for key " + key, x);
    }
    return out;
  }

  /**
   * Returns boolean value corresponding to the specified key.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Date getDate(String key) throws IllegalArgumentException, ConditionsInvalidException {
    Properties p = getProperties();
    String value = p == null ? null : p.getProperty(key);
    if (value == null) throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid key " + key);
    value = value.trim();
    try {
      return getConditionsManager().getDateFormat().parse(value);
    } catch (ParseException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : cannot convert "+ value +" to Date for key " + key, x);
    }
  }

  /**
   * Returns String value corresponding to the specified key.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public String getString(String key) throws IllegalArgumentException, ConditionsInvalidException {
    Properties p = getProperties();
    String value = p == null ? null : p.getProperty(key);
    if (value == null) throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid key " + key);
    return value;
  }

  /**
   * Returns String value corresponding to the specified key.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Object getObject(String key) throws IllegalArgumentException, ConditionsInvalidException {
    Properties p = getProperties();
    String value = p == null ? null : p.getProperty(key);
    if (value == null) throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid key " + key);
    return value;
  }
  
  
// -- Tabular data (implemented for empty table) : -----------------------------

  /**
   * Returns the number of rows in this <tt>ConditionsSet</tt>.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int getRowCount() throws ConditionsInvalidException {
    CachedRowSet crs = getRowSet();
    return crs == null ? 0 : crs.size();
  }
  
  /**
   * Returns the number of columns in this <tt>ConditionsSet</tt>.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public int getColumnCount() throws ConditionsInvalidException {
    try {
      CachedRowSet crs = getRowSet();
      return crs == null ? 0 : crs.getMetaData().getColumnCount();
    } catch (SQLException x) {
      throw new ConditionsInvalidException(x);
    }
  }

  /**
   * Returns the set of column names of this conditions table.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public Set<String> columnSet() throws ConditionsInvalidException {
    try {
      CachedRowSet crs = getRowSet();
      if (crs == null) return Collections.emptySet();
      ResultSetMetaData meta = crs.getMetaData();
      int n = meta.getColumnCount();
      LinkedHashSet<String> columns = new LinkedHashSet<>(n*2);
      for (int i=1; i<=n; i++) {
        columns.add(meta.getColumnLabel(i));
      }
      return columns;
    } catch (SQLException x) {
      throw new ConditionsInvalidException(x);
    }
  }

  /**
   * Returns <tt>true</tt> if this conditions table contains column with the specified name.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  public boolean containsColumn(String columnName) throws ConditionsInvalidException {
    try {
      getRowSet().findColumn(columnName);
      return true;
    } catch (SQLException|NullPointerException x) {
      return false;
    }
  }
  
  /**
   * Returns the type of values in the specified column.
   * 
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   * @throws IllegalArgumentException if there is no column with the specified name.
   */
  @Override
  public Class<?> getColumnType(String columnName) throws ConditionsInvalidException {
    try {
      int type = getRowSet().getMetaData().getColumnType(findColumn(columnName));
      switch (type) {
        case Types.BOOLEAN:
          return Boolean.TYPE;
        case Types.INTEGER:
          return Integer.TYPE;
        case Types.BIGINT:
          return Long.TYPE;
        case Types.FLOAT:
        case Types.DOUBLE:
          return Double.TYPE;
        case Types.DATE:
        case Types.TIME:
        case Types.TIMESTAMP:
          return Date.class;
        default:
          return String.class;
      }
    } catch (SQLException|NullPointerException x) {
      throw new IllegalArgumentException(x);
    }
  } 

  /**
   * Returns boolean value corresponding to the specified key and row.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  synchronized public boolean getBoolean(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    try {
      CachedRowSet rs = getRowSet();
      rs.absolute(row+1);
      return rs.getBoolean(columnName);
    } catch (SQLException|RuntimeException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : no boolean in row "+ row +" column "+ columnName, x);
    }
  }

  /**
   * Returns integer value corresponding to the specified key and row.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  synchronized public int getInt(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    try {
      CachedRowSet rs = getRowSet();
      rs.absolute(row+1);
      return rs.getInt(columnName);
    } catch (SQLException|RuntimeException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : no integer in row "+ row +" column "+ columnName, x);
    }
  }

  /**
   * Returns integer value corresponding to the specified key and row.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  synchronized public long getLong(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    try {
      CachedRowSet rs = getRowSet();
      rs.absolute(row+1);
      return rs.getLong(columnName);
    } catch (SQLException|RuntimeException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : no long in row "+ row +" column "+ columnName, x);
    }
  }

  /**
   * Returns double value corresponding to the specified key and row.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  synchronized public double getDouble(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    try {
      CachedRowSet rs = getRowSet();
      rs.absolute(row+1);
      return rs.getDouble(columnName);
    } catch (SQLException|RuntimeException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : no double in row "+ row +" column "+ columnName, x);
    }
  }

  /**
   * Returns double array value corresponding to the specified key and row.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  synchronized public double[] getDoubleArray(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    try {
      CachedRowSet rs = getRowSet();
      rs.absolute(row+1);
      return (double[]) rs.getArray(columnName).getArray();
    } catch (SQLException|RuntimeException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : no double array in row "+ row +" column "+ columnName, x);
    }
  }

  /**
   * Returns boolean value corresponding to the specified key and row.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  synchronized public Date getDate(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    try {
      CachedRowSet rs = getRowSet();
      rs.absolute(row+1);
      return rs.getDate(columnName);
    } catch (SQLException|RuntimeException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : no Date in row "+ row +" column "+ columnName, x);
    }
  }

  /**
   * Returns String value corresponding to the specified key and row.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  synchronized public String getString(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    try {
      CachedRowSet rs = getRowSet();
      rs.absolute(row+1);
      return rs.getString(columnName);
    } catch (SQLException|RuntimeException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : no String in row "+ row +" column "+ columnName, x);
    }
  }

  /**
   * Returns String value corresponding to the specified key and row.
   * 
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  @Override
  synchronized public Object getObject(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException {
    try {
      CachedRowSet rs = getRowSet();
      rs.absolute(row+1);
      return rs.getObject(columnName);
    } catch (SQLException|RuntimeException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : no Object in row "+ row +" column "+ columnName, x);
    }
  }

  
// -- Local methods : ----------------------------------------------------------
  
  protected Properties getProperties() {
    checkValidity();
//    if (_props == null && _table == null) {
//      try {
//        getConditionsReader().update(this);
//      } catch (ConditionsInvalidException x) {
//        _props = null;
//        _table = null;
//        _invalid = true;
//        throw x;
//      }
//    }
//    if (_props == null) _props = new Properties();
    return _props;
  }
  
  protected CachedRowSet getRowSet() {
    checkValidity();
//    if (_props == null && _table == null) {
//      try {
//        getConditionsReader().update(this);
//      } catch (ConditionsInvalidException x) {
//        _props = null;
//        _table = null;
//        _invalid = true;
//        throw x;
//      }
//    }
    return _table;
  }
  
  private int findColumn(String columnName) {
    try {
      return getRowSet().findColumn(columnName);
    } catch (SQLException|NullPointerException x) {
      throw new IllegalArgumentException("ConditionsSet "+ getName() +" : invalid column " + columnName);
    }
  }

}
