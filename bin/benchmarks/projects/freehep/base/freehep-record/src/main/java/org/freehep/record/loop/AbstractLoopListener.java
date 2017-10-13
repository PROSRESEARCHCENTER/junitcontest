package org.freehep.record.loop;

/**
 * Adapter to simplify implementing {@link LoopListener}.
 * Forwards an event to an appropriate method based on its {@link RecordLoop.Event} type, 
 * and provides "do nothing" implementations for all methods.
 *
 * @author Dmitry Onoprienko
 * @version $Id$
 */
public class AbstractLoopListener implements LoopListener {
  
// -- Implementing LoopListener : ----------------------------------------------

  /** 
   * Processes an event received from {@link RecordLoop}.
   * Implemented to forward the call to an appropriate method based on the event {@link RecordLoop.Event} type.
   */
  final public void process(LoopEvent event) {
    switch (event.getEventType()) {
      case START:
        start(event); break;
      case FINISH:
        finish(event); break;
      case RESUME:
        resume(event); break;
      case SUSPEND:
        suspend(event); break;
      case CONFIGURE:
        configure(event); break;
      case PROGRESS:
        progress(event); break;
      case RESET:
        reset(event); break;
    }
  }
  
  
// -- Action methods to be overridden by subclasses : --------------------------

  /**
   * Called at the start of a run, whenever the {@link RecordLoop} is moving from <tt>IDLE</tt> to <tt>READY</tt> state. 
   */
  protected void start(LoopEvent event) {}
  
  /**
   * Called at the end of run, whenever the {@link RecordLoop} is moving from <tt>READY</tt> to <tt>IDLE</tt> state. 
   */
  protected void finish(LoopEvent event) {}
  
  /**
   * Called at the start of loop, whenever the {@link RecordLoop} is moving from <tt>READY</tt> to <tt>LOOPING</tt> state. 
   */
  protected void resume(LoopEvent event) {}
  
  /**
   * Called at the end of loop, whenever the {@link RecordLoop} is moving from <tt>LOOPING</tt> to <tt>READY</tt> state. 
   */
  protected void suspend(LoopEvent event) {}
  
  /**
   * Called after {@link RecordLoop#setConfiguration} was called while the loop was in <tt>IDLE</tt> state. 
   */
  protected void configure(LoopEvent event) {}
  
  /**
   * Called to report on {@link RecordLoop} progress while the loop is in <tt>LOOPING</tt> state.
   * Triggered by processing the specified number of events or looping for the specified amount of time.
   */
  protected void progress(LoopEvent event) {}
  
  /**
   * Called after REWIND command was successfully executed on the {@link RecordLoop}. 
   */
  protected void reset(LoopEvent event) {}

}
