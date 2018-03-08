package org.freehep.record.source;

import java.io.IOException;
import java.util.*;

/**
 * Interface to be implemented by classes that supply records to clients for processing.
 * 
 * @author Dmitry Onoprienko
 * @version $Id$
 */

public interface RecordSource {
  
// -- Source parameters : ------------------------------------------------------

  /** Returns the name of this source. */
  String getName();

  /** Returns the <tt>Class</tt> object that is guaranteed to be a superclass of all records in this source. */
  Class<?> getRecordClass();

  /**
   * Returns the number of records in this source.
   * <p>
   * The number of records returned by this method is guaranteed to be correct when the method
   * is called; however, the size of some <tt>RecordSource</tt> implementations might change in time.
   * 
   * @throws UnsupportedOperationException  if the number of records is unknown.
   */
  long size();

  /**
   * Returns the estimated number of records in this source.
   * <p>
   * Clients may use the estimate provided by this method to give the user feedback on what 
   * portion of the record source has been read, for example. However, the estimate is not
   * guaranteed to be correct.
   * 
   * @throws UnsupportedOperationException  if the number of records is unknown.
   */
  long getEstimatedSize();

  /**
   * Returns a list of tags for events in this source.
   * <p>
   * The list of tags returned by this method is guaranteed to be complete when the method
   * is called; however, the content of some <tt>RecordSource</tt> implementations might change in time.
   * 
   * @throws UnsupportedOperationException  if this source does not support access by tag,
   *                                        or if the list of tags cannot be retrieved.
   */
  List<RecordTag> getTags();

  
// -- Current record data : ----------------------------------------------------
  
  /**
   * Returns the index of the current record.
   * Returns <tt>-1</tt> if this source is positioned before the first record.
   * 
   * @throws IllegalStateException  if the current index is not known.
   * @throws UnsupportedOperationException  if this source does not support access by index,
   */
  long getCurrentIndex();
  
  /**
   * Returns the tag of the current record.
   * 
   * @throws IllegalStateException  if there is no current record, or the current tag is not known.
   * @throws UnsupportedOperationException  if this source does not support access by tag,
   */
  RecordTag getCurrentTag();

  /**
   * Returns the current record.
   * 
   * @throws IllegalStateException  if there is no current record.
   * @throws IOException  if retrieving the current record fails for any reason.
   */
  Object getCurrentRecord() throws IOException;

  /** Releases any resources associated with the current record. */
  void releaseRecord();
  
  
// -- Checking whether this source supports the specified positioning method : -
  
  /** Returns <tt>true</tt> if this source supports reloading current record. */
  boolean supportsCurrent();
  
  /** Returns <tt>true</tt> if this source supports loading next record. */
  boolean supportsNext();
  
  /** Returns <tt>true</tt> if this source supports loading previous record. */
  boolean supportsPrevious();
  
  /** Returns <tt>true</tt> if this source supports selecting records by index. */
  boolean supportsIndex();
  
  /** Returns <tt>true</tt> if this source supports selecting records by tag. */
  boolean supportsTag();
  
  /** Returns <tt>true</tt> if this source supports selecting records by offset with respect to the current record. */
  boolean supportsShift();
 

// -- Checking whether the specified record exist : ----------------------------

  /** 
   * Returns <tt>true</tt> if this source can reload the current record.
   * Also returns <tt>true</tt> if this source does not know whether reloading is possible.
   * <p>
   * Some implementations might not be able to figure out whether reloading is possible
   * before trying it, so the fact that this method returns <tt>true</tt> does not
   * exclude the possibility of <tt>NoSuchRecordException</tt> being thrown by the next
   * call to <tt>current()</tt>.
   */
  boolean hasCurrent();

  /** 
   * Returns <tt>true</tt> if this source can load the next record.
   * Also returns <tt>true</tt> if this source does not know whether it has more records.
   * <p>
   * Some implementations might not be able to figure out whether loading the next record is possible
   * before trying it, so the fact that this method returns <tt>true</tt> does not exclude the 
   * possibility of <tt>NoSuchRecordException</tt> being thrown by the next call to <tt>next()</tt>.
   */
  boolean hasNext();

  /** 
   * Returns <tt>true</tt> if this source can load the previous record.
   * Also returns <tt>true</tt> if this source does not know whether it has a previous record.
   * <p>
   * Some implementations might not be able to figure out whether loading the previous record is possible
   * before trying it, so the fact that this method returns <tt>true</tt> does not exclude the 
   * possibility of <tt>NoSuchRecordException</tt> being thrown by the next call to <tt>previous()</tt>.
   */
  boolean hasPrevious();

  /** 
   * Returns <tt>true</tt> if this source has a record with the specified index.
   * Also returns <tt>true</tt> if this source does not know whether it has a record with the specified index.
   * <p>
   * Some implementations might not be able to figure out whether the requested record exists
   * before trying to load it, so the fact that this method returns <tt>true</tt> does not exclude the 
   * possibility of <tt>NoSuchRecordException</tt> being thrown by the next call to <tt>jump(index)</tt>.
   */
  boolean hasIndex(long index);

  /** 
   * Returns <tt>true</tt> if this source has a record with the specified tag.
   * Also returns <tt>true</tt> if this source does not know whether it has a record with the specified tag.
   * <p>
   * Some implementations might not be able to figure out whether the requested record exists
   * before trying to load it, so the fact that this method returns <tt>true</tt> does not exclude the 
   * possibility of <tt>NoSuchRecordException</tt> being thrown by the next call to <tt>jump(tag)</tt>.
   */
  boolean hasTag(RecordTag tag);

  /** 
   * Returns <tt>true</tt> if this source can shift <tt>numberOfRecords</tt> records.
   * Also returns <tt>true</tt> if this source does not know whether it has a record with the specified offset.
   * <p>
   * Some implementations might not be able to figure out whether the requested record exists
   * before trying to load it, so the fact that this method returns <tt>true</tt> does not exclude the 
   * possibility of <tt>NoSuchRecordException</tt> being thrown by the next call to <tt>shift(numberOfRecords)</tt>.
   */
  boolean hasShift(long numberOfRecords);
  
  
// -- Loading the specified record : -------------------------------------------

  /**
   * Reloads the current record.
   *
   * @throws NoSuchRecordException if there is no current record.
   * @throws IOException  if reloading of the current record fails.
   * @throws UnsupportedOperationException if this source does not support reloading of the current record.
   */
  void current() throws IOException, NoSuchRecordException;

  /**
   * Loads the next record.
   * 
   * @throws NoSuchRecordException if there is no next record.
   * @throws IOException if loading of the next record fails.
   * @throws UnsupportedOperationException if this source does not support loading the next record.
   */
  void next() throws IOException, NoSuchRecordException;

  /**
   * Loads the previous record.
   * 
   * @throws NoSuchRecordException if there is no previous record.
   * @throws IOException if loading of the previous record fails.
   * @throws UnsupportedOperationException if this source does not support loading the previous record.
   */
  void previous() throws IOException, NoSuchRecordException;

  /**
   * Loads the record specified by the index.
   * 
   * @throws NoSuchRecordException if this source does not have a record with the specified index.
   * @throws IOException if loading of the requested record fails.
   * @throws UnsupportedOperationException if this source does not support access by index.
   */
  void jump(long index) throws IOException, NoSuchRecordException;

  /**
   * Loads the record specified by the tag.
   * 
   * @throws NoSuchRecordException if this source does not have a record with the specified tag.
   * @throws IOException if loading of the requested record fails.
   * @throws UnsupportedOperationException if this source does not support access by tag.
   */
  void jump(RecordTag tag) throws IOException, NoSuchRecordException;

  /**
   * Loads the record specified by the offset with respect to the current cursor position. 
   * The offset can be negative or positive. Skipping is guaranteed to be at least as fast as
   * <tt>numberOfRecords</tt> consecutive calls to <tt>next()</tt> or <tt>previous()</tt>, and
   * will be faster if this source implementation permits it.
   * 
   * @throws NoSuchRecordException if this source does not have a record with the specified offset.
   * @throws IOException if loading of the requested record fails.
   * @throws UnsupportedOperationException if this source does not support access by offset.
   */
  void shift(long numberOfRecords) throws IOException, NoSuchRecordException;

  
// -- Rewinding the source : ---------------------------------------------------
 
  /** Returns <tt>true</tt> if this source supports rewind operation. */
  boolean supportsRewind();
  
  /** Returns <tt>true</tt> if this source in its current state can be rewound. */
  boolean hasRewind();
  
  /**
   * Positions the cursor of this source before the first record.
   * 
   * @throws UnsupportedOperationException  if this source does not support rewind.
   * @throws IOException  if rewinding fails for any reason.
   */
  void rewind() throws IOException;
  
  
// -- Closing the source : -----------------------------------------------------

  /**
   * Close the record source and release any associated resources.
   * 
   * @throws IOException  if closing fails.
   */
  void close() throws IOException;
  
  
// -- Utility methods : --------------------------------------------------------
  
  /**
   * Returns a tag corresponding to the specified string.
   * The method should be implemented to convert record names supplied by clients into
   * tags recognized by this source.
   * 
   * @throws IllegalArgumentException if the specified string cannot be parsed.
   */
  RecordTag parseTag(String s);
  
// -----------------------------------------------------------------------------
}
