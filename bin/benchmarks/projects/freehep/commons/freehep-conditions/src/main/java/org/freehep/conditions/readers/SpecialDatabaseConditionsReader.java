package org.freehep.conditions.readers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.Predicate;
import javax.sql.rowset.RowSetProvider;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.ConditionsUpdateException;
import org.freehep.conditions.ConditionsSet;
import org.freehep.conditions.ConditionsInvalidException;
import org.freehep.conditions.base.AbstractConditionsSet;
import org.freehep.conditions.base.DefaultConditions;
import org.freehep.conditions.base.DefaultConditionsManager;
import org.freehep.conditions.util.DatabaseConnector;
import org.freehep.conditions.util.Parser;

/**
 * Database reader that provides additional functionality for working with databases
 * that have a specific structure. Such databases are currently employed by Fermi, EXO, and 
 * some other projects. The description of EXO calibration database is available
 * <a href="https://confluence.slac.stanford.edu/display/exo/Calibration+Metadata+Database">here</a>.
 * This class may be extended to customize data treatment for a specific project.
 * <p>
 * 
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class SpecialDatabaseConditionsReader extends DatabaseConditionsReader {

// -- Private parts : ----------------------------------------------------------
  
  protected final int FORMAT_DB = 1;
  protected final int FORMAT_URL = 2;
  
  protected String _masterTypeColumn = "calib_type";
  protected String _masterFormatColumn = "data_fmt";
  protected String _masterDataColumn = "data_ident";
  protected String _masterPrimaryKey = "ser_no";
  protected String _masterUpdateTime = "update_time";
  protected String _masterQuery = "SELECT * FROM conditions_test WHERE instrument=\"${detector}\" run_start<=${run} AND run_end>=${run} AND level=\"PROD\"";
  
  protected FilteredRowSet _masterRowSet; // master table
  protected boolean _isMasterValid; // true if _masterRowSet is up to date
  protected int _masterTypeColumnIndex;
  protected String _detector;  // current detector
  protected int _minRun, _maxRun;  // currently valid run range (inclusive)
  
  // Data valid while conditions set is being updated :
  
  protected AbstractConditionsSet _conditions;
  protected Parser.ConditionsName _conName;
  protected String _conDataID;
  


// -- Construction and initialization : ----------------------------------------
  
  public SpecialDatabaseConditionsReader(DefaultConditionsManager manager, DatabaseConnector connector, Map<String,String> queries) {
    super(manager, connector, queries);
    if (queries != null) {
      String master = queries.get("");
      if (master != null) _masterQuery = master;
    }
  }
  
  public SpecialDatabaseConditionsReader(DefaultConditionsManager manager, String uri, Properties connectionProperties, Map<String,String> queries) {
    this(manager, new DatabaseConnector(uri, (Properties) connectionProperties.clone()), queries);
  }

  
// -- Implementing ConditionsReader : ------------------------------------------

  /**
   * Opens input stream that can be used by <tt>RawConditions</tt> with the given name.
   * 
   * @throws IOException
   * @throws ConditionsInvalidException if the <tt>conditionsName</tt> is not a valid URL.
   */
  @Override
  public InputStream open(String conditionsName) throws IOException, ConditionsInvalidException {
    ConditionsSet conditions = getConditionsManager().getConditions(conditionsName);
    try {
      URL url = new URL(conditions.getString(_masterDataColumn));
      return url.openStream();
    } catch (MalformedURLException x) {
      throw new ConditionsInvalidException(x);
    }
  }

  /**
   * Updates this reader if necessary in response to the specified event.
   * <p>
   * A call to this method updates master row set maintained by this <tt>ConditionsReader</tt> by
   * executing master query mapped to "" key in the map provided to the reader's constructor. 
   * If there is no such key, the default master SQL query<br/>
   * <tt>SELECT * FROM conditions_test WHERE instrument="${detector}" run_start&lt;=${run} AND run_end&gt;=${run} AND level="PROD"</tt><br/>
   * is used. Parameters in the SQL query are resolved with values from the specified event.
   * 
   * @param event Update trigger.
   * @return True if conditions handled by this reader might change in response to the specified event.
   * @throws ConditionsUpdateException If the update fails.
   */
  @Override
  synchronized public boolean update(ConditionsEvent event) throws ConditionsUpdateException {
    
    boolean changed = super.update(event);
    if (!changed) return false;
    
    String query = Parser.resolveParameters(_masterQuery, event, null);
    if (query == null) throw new ConditionsUpdateException("Unable to resolve master query", event);
    
    if (_masterRowSet == null) {
      try {
        _masterRowSet = RowSetProvider.newFactory().createFilteredRowSet();
      } catch (SQLException x) {
        throw new ConditionsUpdateException(x, event);
      }
    }
    Connection connection = null;
    try {
      _masterRowSet.setCommand(query);
      connection = _connector.getConnection(_timeout, TimeUnit.MILLISECONDS);
      _masterRowSet.execute(connection);
      _masterTypeColumnIndex = _masterRowSet.findColumn(_masterTypeColumn);
      return true;
    } catch (SQLException x) {
      _masterRowSet = null;
      throw new ConditionsUpdateException(x, event);
    } catch (TimeoutException x) {
      _masterRowSet = null;
      throw new ConditionsUpdateException("No connections available");
    } finally {
      _connector.releaseConnection(connection);
    }
  }

  /**
   * Updates the specified conditions if necessary. The implementation provided by this class
   * handles conditions of type {@link AbstractConditionsSet}, handling of other types is forwarded
   * to the superclass.
   * <p>
   * Given a conditions name in the  <tt>prefix:id?query</tt> format, this
   * method uses the following logic to identify data that need to be retrieved:
   * <ul>
   * <li>Filter the master row set maintained by this <tt>ConditionsReader</tt> based on
   *     the conditions ID and query string by calling {@link filterMaster()} method.</li>
   * <li>If the ID is empty, fill the <tt>Conditions</tt> with rows of the filtered master set and return.
   *     Otherwise,
   * <li>If more than one row passes the filter, call {@link selectRow()} method
   *     to identify the one to be used. The default implementation of {@link selectRow()}
   *     provided by this class selects the most recently updated row.</li>
   * <li>Set the map data of the <tt>Conditions</tt> based on data in the selected row
   *     by calling {@link updateMap()}. If the call returns <tt>false</tt>, indicating no
   *     changes to the conditions, stop and return <tt>false</tt>.</li>
   * <li>Call {@link makeQuery()} method to construct SQL query based on the content
   *     of the selected row.</li>
   * <li>If the call to {@link makeQuery()} returns non-null query, execute it and
   *     set the tabular data of the <tt>Conditions</tt> from the result.</li>
   * <li>Remove the filter from the master row set, and return <tt>true</tt>.</li>
   * </ul>
   * 
   * @param conditions Conditions to be updated.
   * @return True if the conditions have changed as a result of this call.
   * @throws ConditionsInvalidException if unable to update the conditions.
   */
  @Override
  synchronized public boolean update(DefaultConditions conditions) throws ConditionsInvalidException {
    
    if (conditions instanceof AbstractConditionsSet) {
      
      if (_conditions == conditions) return false; // do nothing if this Conditions is alredy being updated (to allow using getters)

      if (_conditions != null) throw new ConcurrentModificationException("ConditionsReader was requested to update " + conditions.getName() +" while updating "+ _conditions.getName());
      if (_masterRowSet == null) throw new ConditionsInvalidException("ConditionsReader for " + conditions.getName() + " is not up to date");

      _conditions = (AbstractConditionsSet) conditions;
      _conName = Parser.splitName(_conditions.getName());

      try {
        
        // filter master based on conditions name:
        
        filterMaster();
        
        // if no ID, fill conditions with rows of filtered master and return:

        if (_conName.getId().isEmpty()) {
          _conditions.set(_masterRowSet);
          return true;
        }
        
        // if more than one row passed filter, select one to be used:
        
        switch (_masterRowSet.size()) {
          case 0:
            throw new ConditionsInvalidException("No valid master table entry");
          case 1:
            _masterRowSet.first();
            break;
          default:
            selectRow();
        }
        
        // set map data in conditions
        
        if (!updateMap()) return false;
        
        // construct final SQL query
        
        String sqlQuery = makeQuery();
        
        // execute query and set tabular data in conditions
        
        if (sqlQuery != null) {
          Connection connection = null;
          Statement stmt = null;
          try {
            connection = _connector.getConnection(_timeout, TimeUnit.MILLISECONDS);
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            _conditions.set(rs);
          } catch (TimeoutException x) {
            throw new ConditionsInvalidException(x);
          } finally {
            try {
              if (stmt != null) stmt.close();
            } catch (SQLException x) {
            }
            _connector.releaseConnection(connection);
          }
        }
        return true;
        
      } catch (SQLException x) {
        throw new ConditionsInvalidException(x);
      } finally {
        try {
          _masterRowSet.setFilter(null);
          _masterRowSet.beforeFirst();
          _conditions = null;
          _conName = null;
          _conDataID = null;
        } catch (SQLException x) {
          throw new RuntimeException(x);
        }
      }

    } else {
      return super.update(conditions);
    }
  }

  /**
   * Filter the master row set maintained by this reader based on the conditions name.
   *
   * @throws ConditionsInvalidException 
   * @throws java.sql.SQLException 
   */
  protected void filterMaster() throws ConditionsInvalidException, SQLException {
    _masterRowSet.setFilter(new RowFilter(_conName));
  }

  /**
   * Sets row on master table that will be used for fetching data for the specified conditions.
   * Implemented to select most recently updated row - subclass to customize.
   *
   * @throws ConditionsInvalidException 
   * @throws java.sql.SQLException 
   */
  protected void selectRow() throws ConditionsInvalidException, SQLException {
    try {
      Date latest = new Date(0L);
      int latestRow = 0;
      _masterRowSet.beforeFirst();
      while (_masterRowSet.next()) {
        Date time = _masterRowSet.getDate(_masterUpdateTime);
        if (time.after(latest)) {
          latest = time;
          latestRow = _masterRowSet.getRow();
        }
      }
      _masterRowSet.absolute(latestRow);
    } catch (SQLException x) {
      throw new RuntimeException(x);
    }
  }

  /**
   * Sets map data in the conditions currently being updated based on the selected row in the master table.
   *
   * @return True if the conditions might have changed.
   * @throws ConditionsInvalidException 
   * @throws java.sql.SQLException 
   */
  protected boolean updateMap() throws ConditionsInvalidException, SQLException {
    
    _conDataID = _masterRowSet.getString(_masterDataColumn);
    try {
      if (_conditions.getString(_masterDataColumn, "").equals(_conDataID))return false;
    } catch (ConditionsInvalidException x) {
    }
    
    Properties prop = new Properties();
    ResultSetMetaData meta = _masterRowSet.getMetaData();
    int nCol = meta.getColumnCount();
    for (int i=1; i<=nCol; i++) {
      String key = meta.getColumnLabel(i);
      String value = _masterRowSet.getString(i);
      if (key != null && value != null) prop.setProperty(key, value);
    }
    _conditions.set(prop);
    
    return true;
  }

  /**
   * Sets row on master table that will be used for fetching data for the specified conditions.
   * Implemented to select most recently updated row - subclass to customize.
   *
   * @return <tt>Null</tt> if tabular data for this conditions should not be fetched from a database.
   * @throws ConditionsInvalidException 
   * @throws java.sql.SQLException 
   */
  protected String makeQuery() throws ConditionsInvalidException, SQLException {
    if (!_masterRowSet.getString(_masterFormatColumn).equals("DB")) return null;
    String[] tokens = _conDataID.split(":");
    if (tokens.length != 3) throw new ConditionsInvalidException("Illegal data identifier");
    return "SELECT * FROM " + tokens[0] + " WHERE " + tokens[1] + "=" + tokens[2] + ";";
  }


// -- Methods to be customized by subclasses : ---------------------------------
  
  
// -- Local methods and classes : ----------------------------------------------
  
  private class RowFilter implements Predicate {
    
    RowFilter(Parser.ConditionsName name) {
      id = name.getId();
      if (id.isEmpty()) id = null;
      query = name.getQuery();
      if (query.isEmpty()) query = null;
    }
    
    String id;
    Map<String,String> query;

    @Override
    public boolean evaluate(RowSet rs) {
      CachedRowSet crs = (CachedRowSet)rs;
      try {
        if (!( (id == null) || id.equals(crs.getString(_masterTypeColumnIndex)) )) {
          return false;
        }
        if (query != null) {
          for (Map.Entry<String,String> e : query.entrySet()) {
            try {
              String value = crs.getString(e.getKey());
              if (!value.equals(e.getValue())) {
                return false;
              }
            } catch (SQLException x) {
              return false;
            }
          }
          
        }
      } catch (SQLException x) {
        throw new RuntimeException(x);
      }
      return true;
    }

    @Override
    public boolean evaluate(Object value, int column) throws SQLException {
      return false;
    }

    @Override
    public boolean evaluate(Object value, String columnName) throws SQLException {
      return false;
    }
    
  }


}
