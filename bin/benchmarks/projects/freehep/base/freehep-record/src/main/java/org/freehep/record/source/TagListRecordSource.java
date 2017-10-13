package org.freehep.record.source;

import java.io.IOException;
import java.util.*;

/**
 * Record source defined by a list of record tags and an underlying {@link RecordSource}.
 * <p>
 * This class is useful when the client needs to work with a number of specific records from a larger source,
 * looping through them or accessing them by tag or index. The underlying source only needs to support access
 * by tag. 
 * <p>
 * This source can be constructed with a fixed list of records, or made extendable at run time.
 *
 * @author Dmitry Onoprienko
 * @version $Id: $
 */
public class TagListRecordSource extends AbstractRecordSource {

// -- Private parts : ----------------------------------------------------------

  final protected RecordSource _source;
  protected ArrayList<RecordTag> _eventList;
  protected boolean _extendable;
  
  protected int _index; // index of the current record; -1 if before the first record
  protected Object _record; // current record


// -- Construction and initialization : ----------------------------------------

  /**
   * Constructs extendable new <tt>TagListRecordSource</tt> with empty tag list.
   */
  public TagListRecordSource(RecordSource source) {
    super(source.getName());
    _source = source;
    _eventList = new ArrayList<RecordTag>(1);
    _extendable = true;
    _index = -1;
  }

  /**
   * Constructs new <tt>TagListRecordSource</tt>.
   * 
   * @param source Underlying source from which records will be fetched by tag.
   * @param eventList Initial list of tags of records available from this source.
   *                  A list of objects whose <tt>toString()</tt> methods will be used to obtain strings
   *                  that will be converted to record tags using the <tt>parseTag(String)</tt> method
   *                  of the underlying source. Objects that cannot be converted to valid tags are ignored.
   * @param extendable If true, asking for a record with a tag that is not in the list
   *                   will extend the list if the tag exists in the underlying source.
   */
  public TagListRecordSource(RecordSource source, List<?> eventList, boolean extendable) {
    super(source.getName());
    _source = source;
    setEventList(eventList, true);
    _extendable = extendable;
  }

// -- Setters : ----------------------------------------------------------------

  /**
   * Sets the list of records in this source.
   * The cursor is positioned before the first record.
   * @param eventList List of tags of records that will be available from this source.
   */
  public void setEventList(List<RecordTag> eventList) {
    if (eventList.isEmpty()) {
      _eventList = new ArrayList<RecordTag>(1);
    } else {
      _eventList = new ArrayList<RecordTag>(eventList);
    }
    _index = -1;
    _record = null;
  }

  /**
   * Sets the list of records in this source.
   * The cursor is positioned before the first record.
   * @param eventList List of objects whose <tt>toString()</tt> methods will be used to obtain strings
   *                  that will be converted to record tags using the <tt>parseTag(String)</tt> method
   *                  of the underlying source.
   * @param ignore If <tt>true</tt>, objects that cannot be converted to valid tags will be ignored.
   *               If <tt>false</tt>, <tt>IllegalArgumentException</tt> will be thrown if any such object is found.
   */
  final public void setEventList(List<?> eventList, boolean ignore) {
    if (eventList.isEmpty()) {
      _eventList = new ArrayList<RecordTag>(1);
    } else {
      if (ignore) {
        _eventList = new ArrayList<RecordTag>(eventList.size());
        for (Object o : eventList) {
          try {
            _eventList.add(_source.parseTag(o.toString()));
          } catch (IllegalArgumentException x) {
          }
        }
        _eventList.trimToSize();
      } else {
        ArrayList<RecordTag> tagList = new ArrayList<RecordTag>(eventList.size());
        for (Object o : eventList) {
          _eventList.add(_source.parseTag(o.toString()));
        }
        _eventList = tagList;
      }
    }
    _index = -1;
    _record = null;
  }
  
  /**
   * Sets the flag that determines whether this source is extendable.
   * @param extendable If true, asking for a record with a tag that is not in the list
   *                   will extend the list if the tag exists in the underlying source.
   */
  public void setExtendable(boolean extendable) {
    _extendable = extendable;
  }

// -- Source parameters : ------------------------------------------------------

  /**
   * Returns the number of records in this source.
   */
  public long size() {
    return _eventList.size();
  }

  /**
   * Returns a list of tags for events in this source.
   */
  public List<RecordTag> getTags() {
    return Collections.unmodifiableList(_eventList);
  }

  
// -- Current record data : ----------------------------------------------------
  
  /**
   * Returns the index of the current record.
   */
  public long getCurrentIndex() {
    return _index;
  }
  
  /**
   * Returns the tag of the current record.
   * 
   * @throws IllegalStateException  if there is no current record.
   */
  public RecordTag getCurrentTag() {
    if (_index == -1) throw new IllegalStateException();
    return _eventList.get(_index);
  }

  /**
   * Returns the current record.
   * 
   * @throws IllegalStateException  if there is no current record.
   * @throws IOException  if retrieving the current record fails for any reason.
   */
  public Object getCurrentRecord() throws IOException {
    if (_index == -1) throw new IllegalStateException();
    if (_record == null) _record = _source.getCurrentRecord();
    return _record;
  }

  /** Releases any resources associated with the current record. */
  public void releaseRecord() {
    _record = null;
    _source.releaseRecord();
  }
  
  
// -- Checking whether this source supports the specified positioning method : -
  
  /** Returns <tt>true</tt> if this source supports reloading current record. */
  public boolean supportsCurrent() {
    return true;
  }
  
  /** Returns <tt>true</tt> if this source supports loading next record. */
  public boolean supportsNext() {
    return true;
  }
  
  /** Returns <tt>true</tt> if this source supports loading previous record. */
  public boolean supportsPrevious() {
    return true;
  }
  
  /** Returns <tt>true</tt> if this source supports selecting records by index. */
  public boolean supportsIndex() {
    return true;
  }
  
  /** Returns <tt>true</tt> if this source supports selecting records by tag. */
  public boolean supportsTag() {
    return true;
  }
  
  /** Returns <tt>true</tt> if this source supports selecting records by offset with respect to the current record. */
  public boolean supportsShift() {
    return true;
  }
 

// -- Checking whether the specified record exist : ----------------------------

  /** 
   * Returns <tt>true</tt> if this source can reload the current record.
   */
  public boolean hasCurrent() {
    return _index != -1;
  }

  /** 
   * Returns <tt>true</tt> if this source can load the next record.
   */
  public boolean hasNext() {
    return _index < _eventList.size()-1;
  }

  /** 
   * Returns <tt>true</tt> if this source can load the previous record.
   */
  public boolean hasPrevious() {
    return _index > 0;
  }
 
  /** 
   * Returns <tt>true</tt> if this source has a record with the specified index.
   */
  public boolean hasIndex(long index) {
    return _index >= 0 && _index < _eventList.size();
  }

  /** 
   * Returns <tt>true</tt> if this source has a record with the specified tag.
   */
  public boolean hasTag(RecordTag tag) {
    return _extendable ? true : _eventList.contains(tag);
  }

  /** 
   * Returns <tt>true</tt> if this source can shift <tt>numberOfRecords</tt> records.
   */
  public boolean hasShift(long numberOfRecords) {
   return hasIndex(_index + numberOfRecords);
  }
  
  
// -- Loading the specified record : -------------------------------------------

  /**
   * Reloads the current record.
   *
   * @throws NoSuchRecordException if there is no current record.
   * @throws IOException  if reloading of the current record fails.
   */
  public void current() throws IOException, NoSuchRecordException {
    if (_index == -1) throw new NoSuchRecordException();
    jump(_eventList.get(_index), _index);
  }

  /**
   * Loads the next record.
   * 
   * @throws NoSuchRecordException if there is no current record.
   * @throws IOException if loading of the next record fails.
   */
  public void next() throws IOException, NoSuchRecordException {
    if (_index >= _eventList.size()-1) throw new NoSuchRecordException();
    int i = _index + 1;
    jump(_eventList.get(i), i);
  }

  /**
   * Loads the previous record.
   * 
   * @throws NoSuchRecordException if there is no previous record.
   * @throws IOException if loading of the previous record fails.
   */
  public void previous() throws IOException, NoSuchRecordException {
    if (_index <= 0) throw new NoSuchRecordException();
    int i = _index - 1;
    jump(_eventList.get(i), i);
  }

  /**
   * Loads the record specified by the index.
   * 
   * @throws NoSuchRecordException if this source does not have a record with the specified index.
   * @throws IOException if loading of the requested record fails.
   */
  public void jump(long index) throws IOException, NoSuchRecordException {
    if (index < 0L || index >= _eventList.size()) throw new NoSuchRecordException();
    int i = (int) index;
    jump(_eventList.get(i), i);
  }

  /**
   * Loads the record specified by the tag.
   * 
   * @throws NoSuchRecordException if this source does not have a record with the specified tag.
   * @throws IOException if loading of the requested record fails.
   */
  public void jump(RecordTag tag) throws IOException, NoSuchRecordException {
    int index = _eventList.indexOf(tag);
    if (index == -1) {
      if (_extendable) {
        _source.jump(tag);
        _index = _eventList.size();
        _eventList.add(tag);
        _record = null;
      } else {
        throw new NoSuchRecordException();
      }
    } else {
      jump(tag, index);
    }
  }

  /**
   * Loads the record specified by the offset with respect to the current cursor position. 
   * 
   * @throws NoSuchRecordException if this source does not have a record with the specified offset.
   * @throws IOException if loading of the requested record fails.
   */
  public void shift(long numberOfRecords) throws IOException, NoSuchRecordException {
    jump(_index + numberOfRecords);
  }
  
  private void jump(RecordTag tag, int index) throws IOException {
    try {
      _source.jump(tag);
      _record = null;
      _index = index;
    } catch (NoSuchRecordException x) {
      throw new IOException(x);
    }
  }

  
// -- Rewinding the source : ---------------------------------------------------
 
  /** Returns <tt>true</tt> if this source supports rewind operation. */
  public boolean supportsRewind() {
    return true;
  }

  /** Returns <tt>true</tt> if this source in its current state can be rewound. */
  public boolean hasRewind() {
    return _index != -1;
  }
  
  /**
   * Positions the cursor of this source before the first record.
   */
  public void rewind() throws IOException {
    releaseRecord();
    _index = -1;
  }
  
  
// -- Closing the source : -----------------------------------------------------

  /**
   * Close the record source and release any associated resources.
   * 
   * @throws IOException  if closing fails.
   */
  public void close() throws IOException {
    _record = null;
    _index = -1;
    _source.close();
  }
  

// -- Utility methods : --------------------------------------------------------
  
  /**
   * Returns a tag corresponding to the specified string.
   */
  public RecordTag parseTag(String s) {
    return _source.parseTag(s);
  }
  

// -----------------------------------------------------------------------------
}
