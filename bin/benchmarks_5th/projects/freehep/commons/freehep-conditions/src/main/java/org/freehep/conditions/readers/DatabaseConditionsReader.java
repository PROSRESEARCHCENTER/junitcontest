package org.freehep.conditions.readers;

import org.freehep.conditions.util.DatabaseConnector;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.ConditionsListener;
import org.freehep.conditions.ConditionsUpdateException;
import org.freehep.conditions.ConditionsInvalidException;
import org.freehep.conditions.base.AbstractConditionsSet;
import org.freehep.conditions.base.DefaultConditions;
import org.freehep.conditions.base.DefaultConditionsManager;
import org.freehep.conditions.base.ConditionsReader;
import org.freehep.conditions.base.DefaultConditionsSet;
import org.freehep.conditions.util.Parser;

/**
 * {@link ConditionsReader} that reads data from a relational database.
 * This class can serve as an adapter for implementing specialized database readers.
 * It also provides utilities that can be used by {@link ConditionsConverter}s fetching
 * data from databases.
 * <p>
 * The reader creates and updates conditions of type {@link AbstractConditionsSet}.
 * Conditions sets are filled withe data fetched by executing database queries 
 * constructed based on conditions names.
 * <p>
 * Names of conditions handled by this reader are expected to be in the <tt>prefix:ID?query</tt> format. 
 * Any part may be empty, but either prefix or ID must be present.
 * Some examples of valid names are:
 * <p><pre>
 * jdbc:WIPP:energy-ratio?flavor=vanilla
 * DB:detector_geometry
 * energy-rotation?level=PROD&locale=SLAC
 * jdbc:WIPP:?calib_type=drift
 * </pre></p>
 * The prefix (possibly empty) is used to identify a <tt>ConditionsReader</tt>. 
 * The ID is used to select a SQL query string from the map associated with that database.
 * SQL queries may contain parameters in the <tt>${parameterName}</tt> format. These are resolved first using 
 * the parameters extracted from the query part of the conditions name, then using the parameters
 * of the update triggering event.
 * 
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class DatabaseConditionsReader extends AbstractConditionsReader implements ConditionsListener {

// -- Private parts : ----------------------------------------------------------

  protected DatabaseConnector _connector;
  protected Map<String,String> _queries;
  
  protected long _timeout = 60000L; // default timeout in milliseconds


// -- Construction and initialization : ----------------------------------------

  public DatabaseConditionsReader(DefaultConditionsManager manager, DatabaseConnector connector, Map<String,String> queries) {
    super(manager);
    _connector = connector;
    _queries = queries;
    Map<String,? extends Object> config = new HashMap<>();
    if (_queries != null) {
      for (String sqlQuery : queries.values()) {
        for (String parameter : Parser.getParameterNames(sqlQuery)) {
          config.put(parameter, null);
        }
      }
    }
    setConfiguration(config, true, false);
  }
  
  public DatabaseConditionsReader(DefaultConditionsManager manager, String uri, Properties connectionProperties, Map<String,String> queries) {
    this(manager, new DatabaseConnector(uri, (Properties) connectionProperties.clone()), queries);
  }

  
// -- Setters : ----------------------------------------------------------------
  
  public void setTimeout(long timeout, TimeUnit unit) {
    _timeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
  }


// -- Implementing ConditionsReader : ------------------------------------------

  @Override
  public AbstractConditionsSet createConditions(String name) throws ConditionsInvalidException {
    return new DefaultConditionsSet(this, name);
  }

  @Override
  public boolean update(DefaultConditions conditions) throws ConditionsInvalidException {
    
    if (conditions instanceof AbstractConditionsSet) {

      AbstractConditionsSet conTable = (AbstractConditionsSet) conditions;
      Parser.ConditionsName name = Parser.splitName(conTable.getName());
      
      String sqlQuery = _queries.get(name.getId());
      
      if (sqlQuery == null) throw new ConditionsInvalidException("No query for " + conTable.getName());
      try {
        sqlQuery = Parser.resolveParameters(sqlQuery, name.getQuery(), _config);
        if (sqlQuery == null) throw new ConditionsInvalidException("Unable to resolve query parameters for " + conTable.getName());
        try {
          if (sqlQuery.equals(conTable.getString("_query_", ""))) return false;
        } catch (ConditionsInvalidException x) {
        }
        Connection connection = _connector.getConnection(_timeout, TimeUnit.MILLISECONDS);
        try (Statement stmt = connection.createStatement()) {
          ResultSet rs = stmt.executeQuery(sqlQuery);
          conTable.set(rs);
          Properties p = new Properties();
          p.setProperty("_query_", sqlQuery);
          conTable.set(p);
        } catch (SQLException x) {
          throw new ConditionsInvalidException("Database error", x);
        } finally {
          _connector.releaseConnection(connection);
        }
      } catch (SQLException | TimeoutException x) {
        throw new ConditionsInvalidException("ConditionsSet " + conditions.getName() + " is currently invalid", x);
      }
      
    } else {
      return super.update(conditions);
    }
    
    return true;
  }

  /**
   * Called by ConditionsManager when the update is complete and listeners are being notified.
   * Can be used for after-update cleanup like closing database connections.
   */
  @Override
  public void conditionsChanged(ConditionsEvent event) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  
// -- Utility methods : --------------------------------------------------------
  
  /**
   * Returns database connector used by this reader.
   */
  public DatabaseConnector getConnector() {
    return _connector;
  }
  
}
