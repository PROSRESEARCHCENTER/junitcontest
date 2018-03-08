package org.freehep.record.loop;

import java.util.EventObject;

/**
 * An event sent by {@link RecordLoop} to {@link LoopListener}s.
 * An event represents an immutable snapshot of the loop at the moment when the event was created.
 *
 * @author Dmitry Onoprienko
 * @version $Id$
 */
public class LoopEvent extends EventObject {

// -- Private parts : ----------------------------------------------------------
  
  final protected RecordLoop.Event _type;
  
  final protected RecordLoop.State _state;
  final protected RecordLoop.Command _command;
  final protected Object[] _commandParameters;
  final protected Throwable _exception;
  final protected boolean _pauseRequested;
  final protected boolean _stopRequested;
  
  final protected long _supplied;
  final protected long _totalSupplied;
  final protected long _consumed;
  final protected long _totalConsumed;
  final protected long _countableConsumed;
  final protected long _totalCountableConsumed;
  
  final protected long _timeInLoop;


// -- Construction and initialization : ----------------------------------------
  
  public LoopEvent(RecordLoop loop, RecordLoop.Event type, RecordLoop.State state,
          RecordLoop.Command command, Object[] commandParameters,
          Throwable exception, boolean pauseRequested, boolean stopRequested,
          long supplied, long totalSupplied,
          long consumed, long totalConsumed, long countableConsumed, long totalCountableConsumed,
          long timeInLoop) {
    super(loop);
    _type = type;
    _state = state;
    _command = command;
    _commandParameters = commandParameters;
    _exception = exception;
    _pauseRequested = pauseRequested;
    _stopRequested = stopRequested;
    _supplied = supplied;
    _totalSupplied = totalSupplied;
    _consumed = consumed;
    _totalConsumed = totalConsumed;
    _countableConsumed = countableConsumed;
    _totalCountableConsumed = totalCountableConsumed;
    _timeInLoop = timeInLoop;
  }

// -- Getters : ----------------------------------------------------------------

  /** Returns the record loop that fired this event. */
  public RecordLoop getSource() {
    return (RecordLoop)source;
  }

  /** Returns the type of this event. */
  public RecordLoop.Event getEventType() {
    return _type;
  }
  
  /** Returns the state of the loop when this event was fired. */
  public RecordLoop.State getState() {
    return _state;
  }

  /** Returns the command that was being executed or had just finished executing. */
  public RecordLoop.Command getCommand() {
    return _command;
  }

  /**
   * Returns a reference to the array containing parameters of the command that 
   * was being executed or had just finished executing.
   * <p>
   * <i>Callers should not modify the array or its contents to avoid altering the
   * behavior of the loop, possibly in an unpredictable way.</i>
   */
  public Object[] getCommandParameters() {
    return _commandParameters;
  }
  
  /**
   * Returns <tt>true</tt> if {@link RecordLoop.Command#PAUSE PAUSE} command was issued
   * to this loop while is was in the {@link RecordLoop.State#LOOPING LOOPING} state
   * last time before this event was fired.
   */
  public boolean isPauseRequested() {
    return _pauseRequested;
  }
  
  /**
   * Returns <tt>true</tt> if {@link RecordLoop.Command#STOP STOP} command was issued
   * to this loop while is was in the {@link RecordLoop.State#LOOPING LOOPING} state
   * last time before this event was fired.
   */
  public boolean isStopRequested() {
    return _stopRequested;
  }

  /**
   * Returns the exception thrown while executing the last command.
   * Returns <tt>null</tt> if no exceptions have been thrown.
   */
  public Throwable getException() {
    return _exception;
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
   * Returns the duration of the latest {@link RecordLoop.State#LOOPING LOOPING} phase, in milliseconds.
   */
  public long getTimeInLoop() {
    return _timeInLoop;
  }

// -----------------------------------------------------------------------------
}
