package org.freehep.conditions;

import java.util.Date;
import java.util.Set;

/**
 * {@link Conditions} that represents tabular data.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public interface ConditionsTable extends ConditionsSet/*, Iterable<ConditionsSet>*/ {
  
//  /**
//   * Sets current row.
//   * All subsequent calls to data getters that do not explicitly specify the row will return
//   * values for the row set with a call to this method.
//   * @throws ConditionsInvalidException if this ConditionsSet is currently unavailable.
//   * @throws IllegalArgumentException if the specified row does not exist.
//   */
//  void setCurrentRow(int row) throws ConditionsInvalidException;

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
   * Returns boolean value corresponding to the specified key and row.
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
