package org.freehep.conditions.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Handles creation and sharing of database connections.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class DatabaseConnector {

// -- Private parts : ----------------------------------------------------------
  
  protected final Object _lock = new Object();
  
  protected String _uri;
  protected Properties _props;
  protected long _keepAlive = 2000;
  protected int _maxConnections = 10;
  
  protected Semaphore _semaphore;
  protected ArrayDeque<Connection> _connections;


// -- Construction and initialization : ----------------------------------------
  
  public DatabaseConnector() {}
  
  public DatabaseConnector(String uri) {
    this(uri, new Properties());
  }
  
  public DatabaseConnector(String uri, Properties connectionProperties) {
    _uri = uri;
    _props = connectionProperties;
    _semaphore = new Semaphore(_maxConnections, true);
    _connections = new ArrayDeque<>(Math.min(_maxConnections, 4));
  }
  
// -- Setters : ----------------------------------------------------------------
  
  public void setURI(String uri) {
    _uri = uri;
  }
  
  public void setUserName(String userName) {
    _props.put("user", userName);
  }
  
  public void setPassword(String password) {
    _props.put("password", password);
  }
  
  public String setProperty(String key, String value) {
    return (String) _props.put(key, value);
  }

  /**
   * Specifies for how long connections are kept open and ready for future use after being returned to the pool.
   * If set to zero, connections are closed immediately upon a call to {@link #releaseConnection}.
   * If negative, connections are kept open indefinitely (unless closed by the client or by a call to {@link #clear}).
   * The default is 2 seconds.
   */
  public void setKeepAlive(long time, TimeUnit unit) {
    _keepAlive = TimeUnit.MILLISECONDS.convert(time, unit);
  }
  
  /** 
   * Sets the maximum number of simultaneous connections that can be obtained from this connector.
   * The default is 10.
   * @throws IllegalArgumentException is the argument is negative.
   * @return True if the allowed number of simultaneous connections has been successfully changed;
   *         false if the change is impossible since more connections are currently in use than the specified maximum.
   */
  public boolean setMaxConnections(int max) {
    if (max < 0) throw new IllegalArgumentException("Maximum number of connections cannot be negative");
    boolean out = true;
    if (max >= _maxConnections) {
      _semaphore.release(max - _maxConnections);
    } else {
      out = _semaphore.tryAcquire(_maxConnections - max);
    }
    if (out) _maxConnections = max;
    return out;
  }

// -- Handling connections : ---------------------------------------------------
  
  /**
   * Returns an open connection to the database and decreases the number of available connections by one.
   * @throws SQLException if an error occurs while opening connection.
   * @throws TimeoutException if no connections are available when this method is called.
   */
  public Connection getConnection() throws SQLException, TimeoutException {
    return getConnection(0L, TimeUnit.SECONDS);
  }
  
  /**
   * Returns an open connection to the database and decreases the number of available connections by one.
   * @throws SQLException if an error occurs while opening connection.
   * @throws TimeoutException if no connections become available within the specified timeout period,
   *                          or if the thread is interrupted while waiting for connection.
   */
  public Connection getConnection(long timeout, TimeUnit unit) throws SQLException, TimeoutException {
    try {
      if (_semaphore.tryAcquire(timeout, unit)) {
        Connection out;
        synchronized (_lock) {
          out = _connections.poll();
        }
        if (out == null) {
          try {
            out = DriverManager.getConnection(_uri, _props);
          } catch (SQLException x) {
            _semaphore.release();
            throw x;
          }
        }
        return out;
      } else {
        throw new TimeoutException();
      }
    } catch (InterruptedException x) {
      throw new TimeoutException();
    }
  }
  
  /**
   * Releases connection back to the pool and increases the number of available connections by one.
   * Calling this method with <tt>null</tt> argument has no effect.
   */
  public void releaseConnection(Connection connection) {
    if (connection != null) {
      try {
        if (!connection.isClosed()) {
          if (_keepAlive == 0L) {
            connection.close();
          } else {
            boolean rejected = true;
            synchronized (_lock) {
              if (_connections.size() < _maxConnections) {
                _connections.push(connection);
                rejected = false;
              }
            }
            if (rejected) connection.close();
          }
        }
      } catch (SQLException x) {
      }
      _semaphore.release();
    }
  }
  
  /**
   * Increases the number of available connections by one.
   */
  public void releaseConnection() {
    _semaphore.release();
  }

  /**
   * Closes all idle connections currently in the pool.
   */
  public void clear() {
    ArrayDeque<Connection> connections = _connections;
    ArrayDeque<Connection> newDeque = new ArrayDeque<>(Math.min(_maxConnections, 4));
    synchronized (_lock) {
      _connections = newDeque;
    }
    for (Connection c : connections) {
      try {
        if (!c.isClosed()) c.close();
      } catch (SQLException x) {
      }
    }
  }

}
