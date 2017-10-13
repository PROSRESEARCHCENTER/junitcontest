package org.freehep.record.loop;

import java.util.*;
import org.freehep.record.source.NoSuchRecordException;

import org.freehep.record.source.RecordSource;

/**
 * Manages fetching records from {@link RecordSource} and supplying them to consumers.
 * 
 * To obtain information on the current state of this loop, users should normally create 
 * a loop snapshot through a call to <tt>getProgress()</tt> (unless an instance of {@link LoopEvent}
 * created at a relevant moment is already available), then use {@link LoopEvent} getters.
 * For legacy code compatibility, <tt>RecordLoop</tt> also provides direct access to individual
 * state variables through <tt>isInterruptRequested(), getSupplied(), getTotalSupplied(),
 * getConsumed(), getTotalConsumed(), getCountableConsumed()</tt>, and <tt>getTotalCountableConsumed()</tt>
 * methods. A record is considered "supplied" as soon as it has been fetched from {@link RecordSource}.
 * A record is considered "consumed" as soon as <tt>recordSupplied(RecordEvent event)</tt> methods
 * of all registered {@link RecordListener}s have returned. If <tt>doNotCount(record)</tt> of this
 * loop is called between the moment the specified record was supplied and the moment it was consumed,
 * that record is not considered "countable".
 *
 * @author Dmitry Onoprienko
 * @version $Id$
 */
public interface RecordLoop {

  /** Enumeration of states of this loop. */
  public enum State {
    IDLE,
    READY,
    LOOPING
  }

  /** Enumeration of commands that can be issued to this loop by users. */
  public enum Command {
    REWIND,
    PREVIOUS,
    PAUSE,
    NEXT,
    GO_N,
    GO,
    JUMP,
    REFRESH,
    STOP,
    CONFIG,
    SET_SOURCE
  }
  
  /**Enumeration of {@link LoopEvent} types that can be fired by this loop. */
  public enum Event {
    /** Fired at the start of record processing, once this loop has moved from {@link RecordLoop.State#IDLE IDLE} to {@link RecordLoop.State#READY READY} state. */
    START,
    /** Fired at the end of record processing, once this loop has moved from {@link RecordLoop.State#READY READY} to {@link RecordLoop.State#IDLE IDLE} state. */
    FINISH,
    /** Fired when record processing is resumed, once this loop has moved from {@link RecordLoop.State#READY READY} to {@link RecordLoop.State#LOOPING LOOPING} state. */
    RESUME,
    /** Fired when record processing is paused, once this loop has moved from {@link RecordLoop.State#LOOPING LOOPING} to {@link RecordLoop.State#READY READY} state. */
    SUSPEND,
    /** Fired once the {@link RecordLoop.Command#CONFIG CONFIG} or the {@link RecordLoop.Command#SET_SOURCE SET_SOURCE} command has been processed by this loop. */
    CONFIGURE,
    /** Fired at certain intervals while this loop is processing records. */
    PROGRESS,
    /** Fired once this loop has executed the {@link RecordLoop.Command#REWIND REWIND} command.*/
    RESET
  }
  
  
// -- Handling listeners : -----------------------------------------------------
  
  /** Adds a listener that will be processing records supplied by this loop. */
  void addRecordListener(RecordListener listener);

  /** Removes the listener processing records supplied by this loop. */
  void removeRecordListener(RecordListener listener);

  /** Returns an unmodifiable list of listeners processing records supplied by this loop. */
  List<RecordListener> getRecordListeners();
  
  /** Adds a listener that will be receiving {@link LoopEvent}s fired by this loop. */
  void addLoopListener(LoopListener listener);
  
  /** Removes the listener receiving {@link LoopEvent}s fired by this loop. */
  void removeLoopListener(LoopListener listener);

  /** Returns an unmodifiable list of listeners receiving {@link LoopEvent}s fired by this loop. */
  List<LoopListener> getLoopListeners();


// -- Getters : ----------------------------------------------------------------

  /**
   * Returns <tt>true</tt> if the specified command is enabled in the current state of this 
   * loop, and a call to <tt>execute(command, parameters)</tt> might succeed.
   * If called without parameters, returns <tt>true</tt> if the call might succeed with at
   * least some parameters. See {@link #execute(Command, Object...)} for the description of 
   * parameters used by different commands.
   * <p>
   * Since not all {@link RecordSource} implementations are able to figure out whether retrieving 
   * the requested record is possible before trying it, the fact that this method returned
   * <tt>true</tt> does not exclude the possibility of failure of the subsequent call to 
   * <tt>execute(command, parameters)</tt>. 
   */
  boolean isEnabled(Command command, Object... parameters);

  /** Returns RecordSource used by this loop. */
  RecordSource getRecordSource();
  
  /** 
   * Creates and returns {@link Event#PROGRESS PROGRESS} event describing the current state
   * of this record loop. This method should be used in preference to individual convenience 
   * getters ({@link #isInterruptRequested},  {@link #getSupplied}, 
   * {@link #getTotalSupplied}, {@link #getConsumed}, {@link #getTotalConsumed}, 
   * {@link #getCountableConsumed}, {@link #getTotalCountableConsumed}) since it provides 
   * a snapshot of this loop in a consistent state.
   */
  LoopEvent getProgress();
  
  /** Returns the current state of this loop. */
  State getState();

  /** Returns the current value of the "interrupt requested" flag. */
  boolean isInterruptRequested();

  /** 
   * Returns the number of records supplied to listeners in the latest 
   * {@link State#LOOPING LOOPING} phase, or zero the loop moved through
   * {@link State#IDLE IDLE} state since then.
   */
  long getSupplied();
  
  /** 
   * Returns the number of records supplied to listeners since the last
   * {@link Event#START START} event.
   */
  long getTotalSupplied();

  /** 
   * Returns the number of records consumed by listeners in the latest 
   * {@link State#LOOPING LOOPING} phase, or zero the loop moved through
   * {@link State#IDLE IDLE} state since then.
   */
  long getConsumed();
  
  /** 
   * Returns the number of records consumed by listeners since the last
   * {@link Event#START START} event.
   */
  long getTotalConsumed();

  /** 
   * Returns the number of countable records consumed by listeners in the latest 
   * {@link State#LOOPING LOOPING} phase, or zero the loop moved through
   * {@link State#IDLE IDLE} state since then.
   */
  long getCountableConsumed();
  
  /** 
   * Returns the number of countable records consumed by listeners since the last
   * {@link Event#START START} event.
   */
  long getTotalCountableConsumed();
  
  /**
   * Returns the last record fetched by this loop from the record source.
   * Returns <tt>null</tt> in no records have been fetched since the last {@link Event#START START} event,
   * or if the last record is no longer available.
   */
  Object getLastRecord();
  
// -- Setters and commands : ---------------------------------------------------
  
  /** 
   * Sets progress reporting interval.
   * {@link Event#PROGRESS PROGRESS} event will be fired every <tt>numberOfRecords</tt> records when looping.
   * Setting the interval to zero means no reporting based on the number of records processed.
   */
  void setProgressByRecords(long numberOfRecords);
  
  /** 
   * Sets progress reporting interval.
   * {@link Event#PROGRESS PROGRESS} event will be fired every <tt>milliseconds</tt> milliseconds when looping.
   * Setting the interval to zero means no reporting based on time elapsed.
   */
  void setProgressByTime(long milliseconds);
  
  /** 
   * Supplies an object used to configure this loop.
   * {@link Event#CONFIGURE CONFIGURE} event is fired once configuration is complete.
   * 
   * @throws IllegalStateException if this loop is in <tt>LOOPING</tt> state.
   */
  void setConfiguration(Object config);

  /** 
   * Sets RecordSource used by this loop.
   * {@link Event#CONFIGURE CONFIGURE} event is fired once the source has been set.
   * 
   * @throws IllegalStateException if this loop is in <tt>LOOPING</tt> state.
   */
  void setRecordSource(RecordSource source);
 
  /**
   * Executes the specified command on this loop.
   * <p>
   * Commands:
   * <table border=1><tr><th>Command</th><th>Parameters</th><th>Action</th><th>Enabled when</th></tr>
   * <tr><td>REWIND</td><td>[boolean stop]</td>
   *     <td>Rewind the record source. If <tt>stop</tt> is <tt>true</tt> and the current state is READY, this loop goes
   *         to IDLE state before rewinding the source.</td>
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
   *         reached. Otherwise, the loop remaines in READY state.</td>
   *     <td>IDLE, READY</td></tr>
   * <tr><td>GO</td><td>[boolean stop]</td>
   *     <td>Supply records to listeners until the end of the source is reached. If <tt>stop</tt> is <tt>true</tt>,
   *         this loop goes to IDLE state once all remaining records in the source are processed. Otherwise, the 
   *         loop remaines in READY state.</td>
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
   * Depending on implementation, this method might block until command execution is finished, or it might
   * launch the command and return immediately.
   * <p>
   * If any of the commands that supply records to listeners attempts to load a non-existing record, 
   * an instance of {@link NoSuchRecordException} thrown by the source will be reported by SUSPEND and FINISH
   * events fired by this loop at the end of the command execution, and will remain referenced by this loop
   * until the next command is issued. It can be retrieved by calling <tt>getProgress().getException()</tt>.
   * <tt>IOException</tt> thrown by the source while retrieving a record is handled in the same way.
   * <p>
   * The handling of all other types of unchecked exceptions thrown by the source or by listeners is implementation dependent.
   * <p>
   * @throws IllegalStateException if the specified command cannot be executed by this loop in its current state.
   */
  void execute(Command command, Object... parameters);
  
  /**
   * If the specified record is being currently supplied to listeners, tells this loop not to count
   * it towards the number of records that will be reported by <tt>getTotalCountableSupplied()</tt>.
   * @return <tt>true</tt> if the call resulted in the specified record being excluded from counting.
   */
  boolean doNotCount(Object record);

  /** 
   * Release all resources owned by this loop.
   * The loop cannot be used once this method has been called.
   */
  void dispose();
  
}
