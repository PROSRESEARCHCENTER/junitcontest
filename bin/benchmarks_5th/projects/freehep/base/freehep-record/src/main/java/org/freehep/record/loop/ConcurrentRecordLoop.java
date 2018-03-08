package org.freehep.record.loop;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.freehep.record.source.NoSuchRecordException;

/**
 * Implementation of {@link RecordLoop} capable of processing records in multiple threads.
 * 
 * If this loop is constructed with <tt>isInteractive</tt> flag set to <tt>true</tt>,
 * all {@link LoopEvent}s (but not {@link RecordEvent}s) will be dispatched in the AWT
 * event processing thread, and the looping code (including fetching records from the
 * record source) will be run by <tt>loopExecutor</tt>.
 * 
 * If the number of record processing threads is higher than zero, a thread pool with
 * the specified maximum number of threads will be used to supply {@link RecordEvent}s
 * to listeners while executing {@link RecordLoop.Command#GO GO} and {@link RecordLoop.Command#GO_N GO_N}
 * commands.
 *
 * @author onoprien
 * @version $Id$
 */
public class ConcurrentRecordLoop extends DefaultRecordLoop {

// -- Private parts : ----------------------------------------------------------
  
  protected boolean _isInteractive; // interactivity flag. Should be set in constructor and not modified later.
  protected Executor _loopExecutor; // for running looping code. Should be set in constructor and not modified later.

  protected int _nThreads;
  protected ExecutorService _consumerExecutor;
  protected ThreadFactory _threadFactory;
  
  protected Semaphore _semaphore;
  protected Set<Object> _countableRecords;  // identity set guarded by _stateLock monitor
  
  
// -- Construction and initialization : ----------------------------------------
  
  /**
   * Constructs <tt>ConcurrentRecordLoop</tt>.
   * 
   * @param isInteractive Interactivity flag - typically set to <tt>true</tt> in GUI-driven applications.
   * @param nThreads Number of worker threads for record processing.
   * @param loopExecutor Executor for running looping code in GUI-driven applications.
   *                     Not used unless the interactivity flag is set to <tt>true</tt>.
   * @param threadFactory Factory for creating worker threads that will be running {@link RecordListener} code.
   */
  public ConcurrentRecordLoop(boolean isInteractive, int nThreads, Executor loopExecutor, ThreadFactory threadFactory) {
    
    _isInteractive = isInteractive;
    if (isInteractive) _loopExecutor = (loopExecutor == null) ? Executors.newSingleThreadExecutor() : loopExecutor;
    
    _nThreads = nThreads;
    _threadFactory = threadFactory;
    if (nThreads > 0) {
      _consumerExecutor = (threadFactory == null) ? Executors.newFixedThreadPool(nThreads) : 
                                                    Executors.newFixedThreadPool(nThreads, threadFactory);
      _semaphore = new Semaphore(nThreads);
      _countableRecords = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
    }
  }

  /**
   * Constructs <tt>ConcurrentRecordLoop</tt> with default looping code executor and thread factory.
   */
  public ConcurrentRecordLoop(boolean isInteractive, int nThreads) {
    this(isInteractive, nThreads, null, null);
  }

  /** Do-nothing constructor to facilitate subclassing. */
  protected ConcurrentRecordLoop() {}
  
  
// -- Setters : ----------------------------------------------------------------

  /**
   * Sets the number of worker threads used by this record loop.
   * @throws  IllegalStateException if this loop is in {@link RecordLoop.State.LOOPING LOOPING} state.
   */
  public void setNumberOfThreads(int nThreads) {
    synchronized (_stateLock) {
      if (_state == State.LOOPING) throw new IllegalStateException();
      if (_nThreads != nThreads) {
        _nThreads = nThreads;
        if (_consumerExecutor != null) _consumerExecutor.shutdown();
        if (_nThreads == 0) {
          _consumerExecutor = null;
          _semaphore = null;
          _countableRecords = null;
        } else {
          _consumerExecutor = (_threadFactory == null) ? Executors.newFixedThreadPool(nThreads) : 
                                                         Executors.newFixedThreadPool(nThreads, _threadFactory);
          _semaphore = new Semaphore(nThreads);
          _countableRecords = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
        }
      }
    }
  }


// -- Handling listeners : -----------------------------------------------------
  
  protected void fireLoopEvents() {
    if (_loopEvents.isEmpty()) return;
    if (_isInteractive && !SwingUtilities.isEventDispatchThread()) {
      try {
        SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            ConcurrentRecordLoop.super.fireLoopEvents();
          }
        });
      } catch (InterruptedException x) {
        throw new RuntimeException("Unexpected thread interruption while dispatching record loop events", x);
      } catch (InvocationTargetException x) {
        Throwable t = x.getTargetException();
        try {
          throw (RuntimeException) t;
        } catch (ClassCastException ccx) {
          throw new RuntimeException(t);
        }
      }
    } else {
      super.fireLoopEvents();
    }
  }


// -- Looping : ----------------------------------------------------------------
  
  protected void goOne() {
    if (_isInteractive) {  // interactive - run looping logic in executor
      _loopExecutor.execute(new Runnable() {
        public void run() {
          ConcurrentRecordLoop.super.goOne();
        }
      });
    } else { // non-interactive - run looping logic in the calling thread
      super.goOne();
    }
  }

  protected void loop() {
    if (_isInteractive) {
    _loopExecutor.execute(new Runnable() {
      public void run() {
        loopSelect(); //ConcurrentRecordLoop.super.loopConcurrent();
      }
    });
    } else {
      loopSelect();
    }
  }
  
  private void loopSelect() {
    if (_nThreads == 0) {
      ConcurrentRecordLoop.super.loop(); // single-thread version
    } else {
      loopConcurrent(); // multi-thread - use _consumerExecutor to supply records to consumers
    }
  }
  
  protected void loopConcurrent() {
    
    long lastReportTime = _loopTime;
    long lastReportRecords = 0L;
    _semaphore.drainPermits();
    _semaphore.release(_nThreads);
    
    try {
      while (keepLoopingConcurrent()) {
        
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
          _countableRecords.add(record);
          _lastRecord = record;
        }
        
        // Wait for a worker thread to become available :
        
        _semaphore.acquire();
        
        // Consume record in _consumerExecutor :
        
        _consumerExecutor.execute(new Runnable() {
          public void run() {
            consumeRecord(record);
            synchronized (_stateLock) {
              _consumed++;
              _totalConsumed++;
              if (_countableRecords.remove(record)) {
                _countableConsumed++;
                _totalCountableConsumed++;
              }
              _stateLock.notifyAll(); // wake up keepLoopingConcurrent()
            }
            _semaphore.release();
          }
        });
        
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
      
      // Out of loop, wait for record processing tasks to finish :
      
      _semaphore.acquire(_nThreads);
      
      // Finish command execution :
      
      synchronized (_stateLock) {
        _countableRecords.clear();
        boolean stopOnEOF = _stopOnEOF;
        if (_command == Command.GO) {
          if (_commandParameters.length > 0) stopOnEOF = (Boolean) _commandParameters[0];
        } else {
          if (_commandParameters.length > 1) stopOnEOF = (Boolean) _commandParameters[1];
        }
        if (stopOnEOF && (_exception != null && _exception instanceof NoSuchRecordException)) _stopRequested = true;
        _state = State.READY;
        _loopTime = System.currentTimeMillis() - _loopTime;
        queueLoopEvent(Event.SUSPEND);
        clearCommand();
        if (_stopRequested) stop();
      }
      fireLoopEvents();
      
    } catch (InterruptedException x) {} // no cancelation mechanism for now - no support in listener API
  }
  
  protected boolean keepLoopingConcurrent() {
    if (_pauseRequested || _exception != null) return false;
    if (_command == Command.GO) return true;
    long n = (Long) _commandParameters[0];
    synchronized (_stateLock) {
      while (_countableConsumed < n) {
        if (_countableConsumed + _nThreads < n) return true;
        if (_countableConsumed + _countableRecords.size() < n) return true;
        try {
          _stateLock.wait(10000);
        } catch (InterruptedException x) {}
      }
      return false;
    }
  }
  
  public boolean doNotCount(Object record) {
    synchronized (_stateLock) {
      if (_nThreads == 0) {
        return super.doNotCount(record);
      } else {
        return _countableRecords.remove(record);
      }
    }
  }

// -----------------------------------------------------------------------------
}
