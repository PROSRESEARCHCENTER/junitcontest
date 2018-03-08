package org.freehep.conditions;

import java.util.Date;
import java.util.Set;

/**
 * {@link Conditions} object that contains a map of string keys to values and tabular data.
 * 
 * All getter methods throw <tt>ConditionsInvalidException</tt> if this <tt>ConditionsSet</tt>
 * has not been successfully updated in response to the latest update triggering event.
 *
 * @version $Id: $
 * @author Tony Johnson
 */
public interface ConditionsSet extends Conditions {
  
// -- Map of keys to values : --------------------------------------------------

  /**
   * Returns the number of key-value pairs in this conditions set.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  int size() throws ConditionsInvalidException;

  /**
   * Returns the set of keys of this conditions set.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Set<String> keySet() throws ConditionsInvalidException;

  /**
   * Returns <tt>true</tt> if this conditions set contains value for the specified key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  boolean containsKey(String key) throws ConditionsInvalidException;

  /**
   * Returns the type of value mapped to the specified key by this conditions set.
   * @throws IllegalArgumentException if this conditions set does not have a value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Class getType(String key) throws ConditionsInvalidException;

  /**
   * Returns boolean value corresponding to the specified key.
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  boolean getBoolean(String key) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns boolean value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  boolean getBoolean(String key, boolean defValue) throws ConditionsInvalidException;

  /**
   * Returns integer value corresponding to the specified key.
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  int getInt(String key) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns integer value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  int getInt(String key, int defValue) throws ConditionsInvalidException;

  /**
   * Returns integer value corresponding to the specified key.
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  long getLong(String key) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns integer value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  long getLong(String key, long defValue) throws ConditionsInvalidException;

  /**
   * Returns double value corresponding to the specified key.
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  double getDouble(String key) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns double value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  double getDouble(String key, double defValue) throws ConditionsInvalidException;

  /**
   * Returns double array value corresponding to the specified key.
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  double[] getDoubleArray(String key) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns boolean value corresponding to the specified key.
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Date getDate(String key) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns boolean value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Date getDate(String key, Date defValue) throws ConditionsInvalidException;

  /**
   * Returns String value corresponding to the specified key.
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  String getString(String key) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns String value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  String getString(String key, String defValue) throws ConditionsInvalidException;

  /**
   * Returns String value corresponding to the specified key.
   * @throws IllegalArgumentException if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Object getObject(String key) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns String value corresponding to the specified key.
   * Returns the supplied default if there is no currently valid value for the given key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Object getObject(String key, Object defValue) throws ConditionsInvalidException;
  
  
// -- Tabular data : -----------------------------------------------------------

  /**
   * Returns the number of rows in this <tt>ConditionsTable</tt>.
   * @throws ConditionsInvalidException if this ConditionsTable is currently unavailable.
   */
  int getRowCount() throws ConditionsInvalidException;
  
  /**
   * Returns the number of columns in this <tt>ConditionsTable</tt>.
   * @throws ConditionsInvalidException if this ConditionsTable is currently unavailable.
   */
  int getColumnCount() throws ConditionsInvalidException;

  /**
   * Returns the set of column names of this conditions table.
   * @throws ConditionsInvalidException if this ConditionsTable is currently unavailable.
   */
  Set<String> columnSet() throws ConditionsInvalidException;

  /**
   * Returns <tt>true</tt> if this conditions table contains column with the specified name.
   * @throws ConditionsInvalidException if this ConditionsTable is currently unavailable.
   */
  boolean containsColumn(String columnName) throws ConditionsInvalidException;
  
  /**
   * Returns the type of values in the specified column.
   * @throws ConditionsInvalidException if this ConditionsTable is currently unavailable.
   * @throws IllegalArgumentException if there is no column with the specified name.
   */
  Class<?> getColumnType(String columnName) throws ConditionsInvalidException; 

  /**
   * Returns boolean value corresponding to the specified key and row.
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  boolean getBoolean(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns boolean value corresponding to the specified row and column name.
   * Returns the supplied default if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  boolean getBoolean(int row, String columnName, boolean defValue) throws ConditionsInvalidException;

  /**
   * Returns integer value corresponding to the specified key and row.
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  int getInt(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns integer value corresponding to the specified key and row.
   * Returns the supplied default if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  int getInt(int row, String columnName, int defValue) throws ConditionsInvalidException;

  /**
   * Returns integer value corresponding to the specified key and row.
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  long getLong(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns integer value corresponding to the specified key and row.
   * Returns the supplied default if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  long getLong(int row, String columnName, long defValue) throws ConditionsInvalidException;

  /**
   * Returns double value corresponding to the specified key and row.
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  double getDouble(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns double value corresponding to the specified key and row.
   * Returns the supplied default if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  double getDouble(int row, String columnName, double defValue) throws ConditionsInvalidException;

  /**
   * Returns double array value corresponding to the specified key and row.
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  double[] getDoubleArray(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns boolean value corresponding to the specified key and row.
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Date getDate(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns boolean value corresponding to the specified key and row.
   * Returns the supplied default if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Date getDate(int row, String columnName, Date defValue) throws ConditionsInvalidException;

  /**
   * Returns String value corresponding to the specified key and row.
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  String getString(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns String value corresponding to the specified key and row.
   * Returns the supplied default if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  String getString(int row, String columnName, String defValue) throws ConditionsInvalidException;

  /**
   * Returns String value corresponding to the specified key and row.
   * @throws IllegalArgumentException if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Object getObject(int row, String columnName) throws IllegalArgumentException, ConditionsInvalidException;

  /**
   * Returns String value corresponding to the specified key and row.
   * Returns the supplied default if there is no currently valid value for the given row and key.
   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
   */
  Object getObject(int row, String columnName, Object defValue) throws ConditionsInvalidException;
  
}
