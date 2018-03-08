package org.freehep.record.source;

/**
 * Record source with non-blocking positioning methods. 
 * Typical uses would be for live sources (e.g. sampling data from a running experiment) 
 * or reading data over a slow connection.
 * 
 * @version $Id: AsynchronousRecordSource.java 13906 2011-10-19 22:01:30Z onoprien $
 */
public interface AsynchronousRecordSource extends RecordSource {

  /**
   * Sets non-blocking mode.
   * When non blocking is set, all methods that would normally block waiting for the
   * source positioning operation to complete, such as next()
   * previous(), current(), jump(...), shift(...), will instead return immediately. The caller is
   * then responsible to use this interface to check when the requested record is ready
   * before calling getCurrentObject() etc.
   *
   * If non-blocking is not enabled, <tt>AsynchronousRecordSource</tt> behaves like a regular source.
   */
  void setNonBlocking(boolean value);

  /** 
   * Test if non-blocking mode is enabled.
   * @return <code>true</code> if non-blocking mode is enabled.
   */
  boolean isNonBlocking();

  /**
   * Returns true is the requested record is available.
   */
  boolean isRecordReady();

  /**
   * This method will block until the requested record is ready
   */
  void waitForRecordReady() throws InterruptedException;

  /**
   * Add a record listener that will be notified when the record is ready
   */
  void addRecordListener(RecordReadyListener l);

  /**
   * Remove a record listener
   */
  void removeRecordListener(RecordReadyListener l);
}
