package org.freehep.record.loop;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.freehep.record.source.NoSuchRecordException;
import org.freehep.record.source.RecordTag;
import java.io.IOException;
import java.util.*;

import org.freehep.record.loop.RecordLoop.Command;
import org.freehep.record.loop.RecordLoop.State;
import org.freehep.record.source.RecordSource;

import static org.freehep.record.loop.RecordLoop.State.*;
import static org.freehep.record.loop.RecordLoop.Command.*;

/**
 * Default implementation of {@link RecordLoop}.
 * 
 * If this class is used directly, records are fetched from the record source and 
 * supplied to listeners in a single thread. Subclasses may override some of the steps
 * in command execution to enable concurrent processing.
 * 
 * This class is mostly thread safe, with a few caveats :
 * <ul>
 * <li>Loop logic assumes exclusive access to the record source. Moreover, many
 * {@link RecordSource} implementations are not thread safe. Manipulating the record
 * source directly while it is used by the <tt>DefaultRecordLoop</tt> may lead to
 * unpredictable results.</li>
 * <li>Code in {@link RecordSource} implementations should not call methods of <tt>DefaultRecordLoop</tt>
 * that is using it.</li>
 * </ul>
 *
 * @author Dmitry Onoprienko
 * @version $Id$
 */
public class DefaultRecordLoop implements RecordLoop {

// -- Private parts : ----------------------------------------------------------

  protected final CopyOnWriteArraySet<RecordListener> _recordListeners = new CopyOnWriteArraySet<RecordListener>();
  protected final CopyOnWriteArraySet<LoopListener> _loopListeners = new CopyOnWriteArraySet<LoopListener>();
  
  // Loop events waiting to be dispatched
  protected final ConcurrentLinkedQueue<LoopEvent> _loopEvents = new ConcurrentLinkedQueue<LoopEvent>();
  
  // Lock for guarding access to this loop's state
  protected final Object _stateLock = new Object();
  
    // Write access to these is guarded by _stateLock's monitor :
  
    protected volatile State _state = State.IDLE;
    protected volatile boolean _pauseRequested = false;
    protected volatile boolean _stopRequested = false;
    protected volatile RecordSource _source;
    protected volatile Throwable _exception;
  
    protected volatile Command _command;
    protected volatile Object[] _commandParameters;

    protected volatile Object _currentRecord;
    protected volatile Object _lastRecord;
    protected volatile long _supplied = 0L;
    protected volatile long _totalSupplied = 0L;
    protected volatile long _consumed = 0L;
    protected volatile long _totalConsumed = 0L;
    protected volatile long _countableConsumed = 0L;
    protected volatile long _totalCountableConsumed = 0L;
    protected volatile long _loopTime;

  // Lock for guarding access to RecordSource
  // Threads should not try to acquire _stateLock monitor while holding _sourceLock monitor
  protected final Object _sourceLock = new Object();
  
  // Settable loop properties
  
  protected volatile long _progressRecords = 0L;
  protected volatile long _progressMilliseconds = 0L;
  protected volatile boolean _stopOnRewind = true;
  protected volatile boolean _stopOnEOF = true;

// -- Construction and initialization : ----------------------------------------
  
// -- Handling listeners : -----------------------------------------------------
  
  /** Adds a listener that will be processing records supplied by this loop. */
  public void addRecordListener(RecordListener listener) {
    _recordListeners.add(listener);
  }

  /** Removes the listener processing records supplied by this loop. */
  public void removeRecordListener(RecordListener listener) {
    _recordListeners.remove(listener);
  }

  /** Returns a list of listeners processing records supplied by this loop. */
  public List<RecordListener> getRecordListeners() {
    return new ArrayList(_recordListeners);
  }
  
  /** Adds a listener that will be receiving {@link LoopEvent}s fired by this loop. */
  public void addLoopListener(LoopListener listener) {
    _loopListeners.add(listener);
  }
  
  /** Removes the listener receiving {@link LoopEvent}s fired by this loop. */
  public void removeLoopListener(LoopListener listener) {
    _loopListeners.remove(listener);
  }

  /** Returns a list of listeners receiving {@link LoopEvent}s fired by this loop. */
  public List<LoopListener> getLoopListeners() {
    return new ArrayList(_loopListeners);
  }
  
  protected void queueLoopEvent(Event eventType) {
    _loopEvents.add(createLoopEvent(eventType));
  }
  
  protected void fireLoopEvents() {
    LoopEvent e;
    while ((e = _loopEvents.poll()) != null) {
      for (LoopListener listener : _loopListeners) {
        try {
          listener.process(e);
        } catch (Throwable x) {
          handleClientError(x);
        }
      }
    }
  }
  
  protected LoopEvent createLoopEvent(Event eventType) {
    synchronized (_stateLock) {
      long timeInLoop = (_state == LOOPING) ? System.currentTimeMillis() - _loopTime : _loopTime;
      return new LoopEvent(this, eventType, _state,
                           _command, _commandParameters,
                           _exception, _pauseRequested, _stopRequested,
                           _supplied, _totalSupplied, _consumed, _totalConsumed, _countableConsumed, _totalCountableConsumed,
                           timeInLoop);
    }
  }


// -- Getters : ----------------------------------------------------------------

  /** Returns <tt>true</tt> if the flag requesting stop or pause in the loop is set. */
  public boolean isInterruptRequested() {
    return _pauseRequested;
  }

  /** Returns RecordSource used by this loop. */
  public RecordSource getRecordSource() {
    return _source;
  }
  
  /** 
   * Creates and returns {@link RecordLoop.Event#PROGRESS PROGRESS} event describing the current state
   * of this record loop. This method should be used in preference to individual convenience 
   * getters ({@link #isInterruptRequested},  {@link #getSupplied}, 
   * {@link #getTotalSupplied}, {@link #getConsumed}, {@link #getTotalConsumed}, 
   * {@link #getCountableConsumed}, {@link #getTotalCountableConsumed}) since it provides 
   * a snapshot of this loop in a consistent state.
   */
  public LoopEvent getProgress() {
    return createLoopEvent(Event.PROGRESS);
  }
  
  /** Returns the current state of this loop. */
  public State getState() {
    return _state;
  }

  /** 
   * Returns the number of records supplied to listeners in the latest 
   * {@link RecordLoop.State#LOOPING LOOPING} phase, or zero the loop moved through
   * {@link RecordLoop.State#IDLE IDLE} state since then.
   */
  public long getSupplied() {
    return _supplied;
  }
  
  /** 
   * Returns the number of records supplied to listeners since the last
   * {@link RecordLoop.Event#START START} event.
   */
  public long getTotalSupplied() {
    return _totalSupplied;
  }

  /** 
   * Returns the number of records consumed by listeners in the latest 
   * {@link RecordLoop.State#LOOPING LOOPING} phase, or zero the loop moved through
   * {@link RecordLoop.State#IDLE IDLE} state since then.
   */
  public long getConsumed() {
    return _consumed;
  }
  
  /** 
   * Returns the number of records consumed by listeners since the last
   * {@link RecordLoop.Event#START START} event.
   */
  public long getTotalConsumed() {
    return _totalConsumed;
  }

  /** 
   * Returns the number of countable records consumed by listeners in the latest 
   * {@link RecordLoop.State#LOOPING LOOPING} phase, or zero the loop moved through
   * {@link RecordLoop.State#IDLE IDLE} state since then.
   */
  public long getCountableConsumed() {
    return _countableConsumed;
  }
  
  /** 
   * Returns the number of countable records consumed by listeners since the last
   * {@link RecordLoop.Event#START START} event.
   */
  public long getTotalCountableConsumed() {
    return _totalCountableConsumed;
  }
  
  /**
   * Returns the last record successfully fetched by this loop from the record source.
   * Returns <tt>null</tt> in no records have been fetched since the last {@link RecordLoop.Event#START START} event.
   */
  public Object getLastRecord() {
    return _lastRecord;
  }

// -- Setters : ----------------------------------------------------------------

  /** 
   * Sets RecordSource used by this loop.
   * {@link RecordLoop.Event#CONFIGURE CONFIGURE} event is fired once the source has been set.
   * Equivalent to <tt>execute(SET_SOURCE, source)</tt>.
   * 
   * @throws IllegalStateException if this loop is in <tt>LOOPING</tt> state.
   */
  public void setRecordSource(RecordSource source) {
    execute(SET_SOURCE, source);
  }
  
  /** 
   * Supplies an object used to configure this loop.
   * {@link RecordLoop.Event#CONFIGURE CONFIGURE} event is fired once configuration is complete.
   * Equivalent to <tt>execute(CONFIG, config)</tt>.
   * 
   * @throws IllegalStateException if this loop is in <tt>LOOPING</tt> state.
   */
  public void setConfiguration(Object config) {
    execute(CONFIG, config);
  }
  
  /** 
   * Sets progress reporting interval.
   * {@link RecordLoop.Event#PROGRESS PROGRESS} event will be fired every <tt>numberOfRecords</tt> records when looping.
   * Setting the interval to zero (default) means no reporting.
   */
  public void setProgressByRecords(long numberOfRecords) {
    _progressRecords = numberOfRecords;
  }
  
  /** 
   * Sets progress reporting interval.
   * {@link RecordLoop.Event#PROGRESS PROGRESS} event will be fired every <tt>milliseconds</tt> milliseconds when looping.
   * Setting the interval to zero (default) means no reporting.
   */
  public void setProgressByTime(long milliseconds) {
    _progressMilliseconds = milliseconds;
  }

  /**
   * Sets "stop on rewind" flag.
   * The flag affects default behavior of the {@link RecordLoop.Command#REWIND REWIND} command.
   * If set to <tt>true</tt> (default), this loop will transition to {@link RecordLoop.State#IDLE IDLE}
   * before rewinding the source if {@link RecordLoop.Command#REWIND REWIND} command is issued while
   * the loop is in {@link RecordLoop.State#READY READY} state. {@link RecordLoop.Event#FINISH FINISH}
   * event will be sent to {@link LoopListener}s during the transition. 
   */
  public void setStopOnRewind(boolean stopOnRewind) {
    _stopOnRewind = stopOnRewind;
  }

  /**
   * Sets "stop on end-of-source" flag.
   * The flag affects default behavior of the {@link RecordLoop.Command#GO} or {@link RecordLoop.Command#GO_N} commands.
   * If set to <tt>true</tt> (default), this loop will transition to {@link RecordLoop.State#IDLE IDLE}
   * state (through {@link RecordLoop.State#READY READY}) if the record source is exhausted while executing
   * {@link RecordLoop.Command#GO} or {@link RecordLoop.Command#GO_N} commands. If set to <tt>false</tt>,
   * the loop will pause in {@link RecordLoop.State#READY READY} state.
   */
  public void setStopOnEOF(boolean stopOnEOF) {
    _stopOnEOF = stopOnEOF;
  }
  
  /**
   * If the specified record is being currently supplied to listeners, tells this loop not to count
   * it towards the number of records that will be reported by <tt>getTotalCountableSupplied()</tt>.
   * @return <tt>true</tt> if was marked for exclusion from counting as a result of this call.
   */
  public boolean doNotCount(Object record) {
    synchronized (_stateLock) {
      if (_currentRecord == record) {
        _currentRecord = null;
        return true;
      } else {
        return false;
      }
    }
  }

  /** 
   * Release all resources referenced by this loop.
   * The loop cannot be used once this method has been called.
   */
  public void dispose() {
    if (_source != null) {
      try {
        _source.close();
      } catch (IOException x) {}
    }
    _source = null;
    _currentRecord = null;
  }
  
  
// -- Handling commands : ------------------------------------------------------

  /**
   * Returns <tt>true</tt> if the specified command is enabled in the current state of this 
   * loop, and a call to <tt>execute(command, parameters)</tt> might succeed.
   * If called without parameters, returns <tt>true</tt> if the call might succeed with at
   * least some parameters. See {@link #execute(Command, Object...)} for the description of 
   * parameters used by different commands.
   * <p>
   * Since not all {@link RecordSource} implementations are able to figure out whether loading 
   * the requested record is possible before trying it, the fact that this method returned
   * <tt>true</tt> does not exclude the possibility of <tt>NoLoopRecordException</tt> being
   * thrown by the subsequent call to <tt>execute(command, parameters)</tt>. 
   */
  public boolean isEnabled(Command command, Object... parameters) {
    synchronized (_stateLock) {
      try {
        
        switch (command) {
          case REWIND:
            if (_command != null) return false;
            synchronized (_sourceLock) {return _source.hasRewind();}
            
          case PREVIOUS:
            if (_command != null) return false;
            synchronized (_sourceLock) {return _source.hasPrevious();}
            
          case PAUSE:
            return _state == LOOPING && !_pauseRequested;
            
          case NEXT:
            if (_command != null) return false;
            synchronized (_sourceLock) {return _source.hasNext();}
            
          case GO_N:
            if (_command != null) return false;
            synchronized (_sourceLock) {
              if (parameters.length == 0) {
                return _source.hasNext();
              } else {
                return _source.hasShift((Long) parameters[0]);
              }
            }
            
          case GO:
            if (_command != null) return false;
            synchronized (_sourceLock) {return _source.hasNext();}
            
          case JUMP:
            if (_command != null) return false;
            synchronized (_sourceLock) {
              if (parameters.length == 0) {
                return _source.supportsIndex() || _source.supportsTag() || _source.supportsShift();
              } else if (parameters[0] instanceof RecordTag) {
                return _source.hasTag((RecordTag) parameters[0]);
              } else if (parameters[0] instanceof Boolean) {
                if ((Boolean) parameters[0]) {
                  return _source.hasShift((Long) parameters[1]);
                } else {
                  return _source.hasIndex((Long) parameters[1]);
                }
              }
            }
            
          case REFRESH:
            if (_command != null) return false;
            synchronized (_sourceLock) {return _source.hasCurrent();}
            
          case STOP:
            return _state != IDLE && !_stopRequested;
            
          case CONFIG:
            return _command == null;
            
          case SET_SOURCE:
            return _command == null;
            
          default:
            return false;
        }
       
      } catch (ClassCastException x) {
        throw new IllegalArgumentException(x);
      } catch (IndexOutOfBoundsException x) {
        throw new IllegalArgumentException(x);
      }
    }
  }
 
  /**
   * Executes the specified command on this loop.
   * <p>
   * Commands:
   * <table border=1><tr><th>Command</th><th>Parameters</th><th>Action</th><th>Enabled when</th></tr>
   * <tr><td>REWIND</td><td>[boolean stop]</td>
   *     <td>Rewind the record source. If <tt>stop</tt> is <tt>true</tt> and the current state is READY, this loop goes
   *         to IDLE state before rewinding the source. If the <tt>stop</tt> parameter is omitted, the value of the
   *         "stop on rewind" flag is used.</td>
   *     <td>IDLE, READY</td></tr>
   * <tr><td>PREVIOUS</td><td></td>
   *     <td>Supply the previous record from the record source to listeners.</td>
   *     <td>IDLE, READY</td></tr>
   * <tr><td>PAUSE</td><td></td>
   *     <td>Requests pause in event processing. No new records will be fetched from the source; 
   *         once those already fetched are processed, this loop moves to READY state.</td>
   *     <td>LOOPING</td></tr>
   * <tr><td>NEXT</td><td></td>
   *     <td>Supply the next record from the record source to listeners.</td>
   *     <td>IDLE, READY</td></tr>
   * <tr><td>GO_N</td><td>[int nRecords, boolean stop]</td>
   *     <td>Supply the next <tt>nRecords</tt> records from the source to listeners. If <tt>stop</tt> is <tt>true</tt>,
   *         this loop goes to IDLE state once the specified number of records is processed (or the end of source is
   *         reached. Otherwise, the loop remaines in READY state. If the <tt>stop</tt> parameter is omitted, the value
   *         of the "stop on end-of-source" flag is used.</td>
   *     <td>IDLE, READY</td></tr>
   * <tr><td>GO</td><td>[boolean stop]</td>
   *     <td>Supply records to listeners until the end of the source is reached. If <tt>stop</tt> is <tt>true</tt>,
   *         this loop goes to IDLE state once all remaining records in the source are processed. Otherwise, the 
   *         loop remaines in READY state. If the <tt>stop</tt> parameter is omitted, the value of the
   *         "stop on end-of-source" flag is used.</td>
   *     <td>IDLE, READY</td></tr>
   * <tr><td>JUMP</td><td>[RecordTag tag]<br>[false, int index]<br>[true, int offset]</td>
   *     <td>Supply the specified record to listeners. If the first parameter is <tt>false</tt>, the second parameter
   *         is interpreted as an absolute index of the requested record. If the first parameter is <tt>true</tt>, 
   *         the second parameter is interpreted as an offset with respect to the current record.</td>
   *     <td>IDLE, READY</td></tr>
   * <tr><td>REFRESH</td><td></td>
   *     <td>Reload the current record from the source and supply it to listeners.</td>
   *     <td>IDLE, READY</td></tr>
   * <tr><td>STOP</td><td></td>
   *     <td>Request interrupt if in LOOPING; once interrupted, moves to IDLE state</td>
   *     <td>IDLE, READY, LOOPING</td></tr>
   * <tr><td>CONFIG</td><td>[Object config]</td>
   *     <td>Set loop configuration</td>
   *     <td>IDLE, READY</td></tr>
   * <tr><td>SET_SOURCE</td><td>[RecordSource source]</td>
   *     <td>Set record source</td>
   *     <td>IDLE, READY</td></tr>
   * </table>
   * <p>
   * This method blocks until command execution is finished.
   * <p>
   * If any of the commands that supply records to listeners attempts to load a non-existing record, 
   * an instance of {@link NoSuchRecordException} thrown by the source will be reported by SUSPEND and FINISH
   * events fired by this loop at the end of the command execution, and will remain referenced by this loop
   * until the next command is issued. It can be retrieved by calling <tt>getProgress().getException()</tt>.
   * <tt>IOException</tt> or <tt>UnsupportedOperationException</tt> thrown by the source while retrieving a 
   * record is handled in the same way.
   * <p>
   * All other types of unchecked exceptions thrown by the source or by listeners terminate the program (this behavior
   * can be changed in subclasses by overriding <tt>handleClientError(Throwable)</tt> and
   * <tt>handleSourceError</tt> methods).
   * <p>
   * The method acquires state lock briefly to check whether the command can be executed, and either 
   * rejects the command by throwing <tt>IllegalStateException</tt>, or launches the command 
   * and modifies the state accordingly. No alien code (including loop listener notifications) 
   * is called while the lock is held.
   * <p>
   * @throws IllegalStateException if the specified command cannot be executed by this loop in the current state.
   */
  public void execute(Command command, Object... parameters) {
    
    switch (command) {
      
      case REWIND:
        synchronized (_stateLock) {
          if (_command != null || _source == null) throw new IllegalStateException();
          _command = REWIND;
          _commandParameters = parameters;
          _exception = null;
          if (_state == READY && (parameters.length == 0 ? _stopOnRewind : (Boolean)parameters[0])) stop();
        }
        fireLoopEvents();
        rewind();
        return;

      case PAUSE:
        synchronized (_stateLock) {
          if (_state != LOOPING) throw new IllegalStateException();
          _pauseRequested = true;
        }
        return;
        
      case STOP:
        synchronized (_stateLock) {
          if (_state == LOOPING) {
            _pauseRequested = true;
            _stopRequested = true;
          } else if (_state == READY && _command == null) {
            stop();
          } else if (_state == IDLE && _command == null) {
            return;
          } else {
            throw new IllegalStateException();
          }
        }
        fireLoopEvents();
        return;
        
      case PREVIOUS:
      case NEXT:
      case GO_N:
      case GO:
      case JUMP:
      case REFRESH:
        synchronized (_stateLock) {
          if (_command != null) throw new IllegalStateException();
          _command = command;
          _commandParameters = parameters;
          _exception = null;
          if (_state == IDLE) {
            _state = READY;
            _supplied = 0L;
            _totalSupplied = 0L;
            _consumed = 0L;
            _totalConsumed = 0L;
            _countableConsumed = 0L;
            _totalCountableConsumed = 0L;
            _loopTime = 0L;
            _lastRecord = null;
            queueLoopEvent(Event.START);
          }
          _state = LOOPING;
          _pauseRequested = false;
          _stopRequested = false;
          _supplied = 0L;
          _consumed = 0L;
          _countableConsumed = 0L;
          _loopTime = 0L;
          queueLoopEvent(Event.RESUME);
        }
        fireLoopEvents();
        synchronized (_stateLock) {
          _loopTime = System.currentTimeMillis();
        }
        if (command == GO || command == GO_N) {
          loop();
        } else {
          goOne();
        }
        return;
    
      case CONFIG:
        synchronized (_stateLock) {
          if (_command != null) throw new IllegalStateException();
          _command = CONFIG;
          _commandParameters = parameters;
          _exception = null;
        }
        setConfiguration();
        synchronized (_stateLock) {
          queueLoopEvent(Event.CONFIGURE);
          clearCommand();
        }
        fireLoopEvents();
        return;
        
      case SET_SOURCE:
        synchronized (_stateLock) {
          if (_command != null) throw new IllegalStateException();
          _command = SET_SOURCE;
          _commandParameters = parameters;
          _exception = null;
          _source = (RecordSource) parameters[0];
          queueLoopEvent(Event.CONFIGURE);
          clearCommand();
        }
        fireLoopEvents();
        
    }
  }
  
  
// -- Steps in command execution that can be overridden by subclasses :  -------

  /**
   * Step in execute(REWIND): rewinds the record source.
   * No locks are held when this method is called, but <tt>_command</tt> is set to <tt>REWIND</tt>
   */
  protected void rewind() {
    try {
      synchronized (_sourceLock) {
        _source.rewind();
      }
    } catch (IOException x) {
      synchronized (_stateLock) {
        _exception = x;
      }
    } catch (UnsupportedOperationException x) {
      synchronized (_stateLock) {
        _exception = x;
      }
    } catch (Throwable x) {
      handleSourceError(x);
    }
    synchronized (_stateLock) {
      queueLoopEvent(Event.RESET);
      clearCommand();
      _lastRecord = null;
    }
    fireLoopEvents();
  }
  
  /**
   * Step in execute(GO or GO_N): retrieves the requested number of records and supplies it to consumers.
   * No locks are held when this method is called, but <tt>_command</tt> and <tt>_commandParameters</tt> are set.
   */
  protected void loop() {
    
    long lastReportTime = _loopTime;
    long lastReportRecords = 0L;
    
    while (keepLooping()) {

      // Fetching record from source :

      final Object record;
      try {
        record = fetchRecord();
      } catch (Throwable x) {
        if (x instanceof NoSuchRecordException || x instanceof IOException || x instanceof UnsupportedOperationException) {
          synchronized (_stateLock) {
            _exception = x;
          }
        } else {
          handleSourceError(x);
        }
        break;
      }
      synchronized (_stateLock) {
        _supplied++;
        _totalSupplied++;
        _currentRecord = record;
        _lastRecord = record;
      }
        
      // Consume record in _consumerExecutor :

      consumeRecord(record);
      synchronized (_stateLock) {
        _consumed++;
        _totalConsumed++;
        if (_currentRecord == record) {
          _countableConsumed++;
          _totalCountableConsumed++;
        }
        _currentRecord = null;
      }
        
      // Report progress :

      long currentTime = System.currentTimeMillis();
      synchronized (_stateLock) {
        if ((_progressRecords > 0L && (_consumed - lastReportRecords) >= _progressRecords)
                || (_progressMilliseconds > 0L && (currentTime - lastReportTime) >= _progressMilliseconds)) {
          queueLoopEvent(Event.PROGRESS);
          lastReportTime = currentTime;
          lastReportRecords = _consumed;
        }
      }
      fireLoopEvents();
        
    }

    // Out of loop, finish command execution :
    
    boolean stopOnEOF = _stopOnEOF;
    if (_command == GO) {
      if (_commandParameters.length > 0) stopOnEOF = (Boolean)_commandParameters[0];
    } else {
      if (_commandParameters.length > 1) stopOnEOF = (Boolean)_commandParameters[1];
    }

    synchronized (_stateLock) {
      if (stopOnEOF && (_exception != null && _exception instanceof NoSuchRecordException)) {
        _stopRequested = true;
      }
      _state = State.READY;
      _loopTime = System.currentTimeMillis() - _loopTime;
      queueLoopEvent(Event.SUSPEND);
      clearCommand();
      if (_stopRequested) stop();
    }
    fireLoopEvents();
      
  }
  /**
   * Step in execute(REFRESH or NEXT or PREVIOUS or JUMP): retrieves the requested number of records and supplies it to consumers.
   * No locks are held when this method is called, but <tt>_command</tt> and <tt>_commandParameters</tt> are set.
   */
  protected void goOne() {
    
    Object record;
    try {
      record = fetchRecord();
      synchronized (_stateLock) {
        _currentRecord = record;
        _lastRecord = record;
        _supplied++;
        _totalSupplied++;
      }
      consumeRecord(record);
      synchronized (_stateLock) {
        _consumed++;
        _totalConsumed++;
        if (_currentRecord == record) {
          _countableConsumed++;
          _totalCountableConsumed++;
        }
        _currentRecord = null;
      }
    } catch (Throwable x) {
      if (x instanceof NoSuchRecordException || x instanceof IOException || x instanceof UnsupportedOperationException) {
        synchronized (_stateLock) {
          _exception = x;
        }
      } else {
        handleSourceError(x);
      }
    }
    
    synchronized (_stateLock) {
      _state = State.READY;
      _loopTime = System.currentTimeMillis() - _loopTime;
      queueLoopEvent(Event.SUSPEND);
      clearCommand();
      if (_stopRequested) stop();
    }
    fireLoopEvents();
  }

  /**
   * Step in execute(CONFIG, Object): changes this loop configuration.
   * No locks are held when this method is called, but <tt>_command</tt> is set to <tt>CONFIG</tt>
   * and <tt>_commandParameters</tt> is set to {configuration object}.
   * <p>
   * The implementation provided by this class is empty, subclasses may override to
   * execute configuration changes.
   */
  protected void setConfiguration() {}

  /**
   * Called when one of the listeners throws an exception while processing events fired by this loop.
   * The default implementation provided by this class prints stack trace and exits.
   */
  protected void handleClientError(Throwable x) {
    x.printStackTrace();
    System.exit(1);
  }

  /**
   * Called when the record source throws an unexpected exception.
   * The default implementation provided by this class prints stack trace and exits.
   */
  protected void handleSourceError(Throwable x) {
    x.printStackTrace();
    System.exit(1);
  }
  
  protected boolean keepLooping() {
    if (_pauseRequested || _exception != null) return false;
    if (_command == Command.GO) return true;
    long n = (Long) _commandParameters[0];
    return (_countableConsumed < (Long)_commandParameters[0]);
  }
  
  protected Object fetchRecord() throws NoSuchRecordException, IOException {
    Object record;
    synchronized (_sourceLock) {
      switch (_command) {
        case PREVIOUS:
          _source.previous();
          break;
        case REFRESH:
          _source.current();
          break;
        case NEXT:
        case GO_N:
        case GO:
          _source.next();
          break;
        case JUMP:
          Object p0 = _commandParameters[0];
          if (p0 instanceof RecordTag) {
            _source.jump((RecordTag) p0);
          } else {
            if ((Boolean) p0) {
              _source.shift((Long) _commandParameters[1]);
            } else {
              _source.jump((Long) _commandParameters[1]);
            }
          }
      }
      record = _source.getCurrentRecord();
    }
    if (record == null) throw new NoSuchRecordException();
    return record;
  }
  
  protected void consumeRecord(Object record) {
    RecordEvent event = new RecordEvent(this, record);
    try {
      for (RecordListener listener : _recordListeners) listener.recordSupplied(event);
    } catch (Throwable x) {
      handleClientError(x);
    }
  }

  /** Moves this loop from {@link RecordLoop.State#READY READY} to {@link RecordLoop.State#IDLE IDLE} state. */
  protected void stop() {
    synchronized (_stateLock) {
      _state = IDLE;
      queueLoopEvent(Event.FINISH);
      _stopRequested = false;
      _pauseRequested = false;
      _loopTime = 0L;
    }
  }

  /** Called at the end of command execution to clear command-related state. */
  protected void clearCommand() {
    _command = null;
    _commandParameters = null;
    _exception = null;
  }
  
// -----------------------------------------------------------------------------
}
