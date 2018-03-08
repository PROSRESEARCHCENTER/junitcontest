package org.freehep.record.source;

import java.io.IOException;
import java.util.*;

/**
 * Adapter that simplifies implementing {@link RecordSource}. Provides
 * implementations for all methods except {@link #getCurrentRecord()}.
 * <p>
 * As implemented, this source supports no positioning operations. All <tt>supportsXXX</tt> methods
 * return <tt>false</tt>. All <tt>hasXXX</tt> methods forward the call to <tt>supportsXXX</tt>.
 * All positioning methods throw <tt>UnsupportedOperationException</tt>.
 * 
 * A typical subclass that supports a certain subset of access modes will only need to override
 * methods corresponding to those modes. For example, a simple sequential access source
 * with no rewind capability will override {@link #supportsNext()}, {@link #hasNext()}, {@link #next()},
 * and possibly {@link #size()} if the number of records in the source is known.
 *
 * @author Dmitry Onoprienko
 * @version $Id$
 */
abstract public class AbstractRecordSource implements RecordSource {

// -- Private parts : ----------------------------------------------------------
  
  protected String _name;

// -- Construction and initialization : ----------------------------------------
  
  public AbstractRecordSource() {}
  
  public AbstractRecordSource(String name) {
    _name = name;
  }
  
// -- Setters : ----------------------------------------------------------------
  
  public void setName(String name) {
    _name = name;
  }
  
// -- Source parameters : ------------------------------------------------------

  /** Returns the name of this source. */
  public String getName() {
    return _name;
  }

  /** 
   * Returns the <tt>Class</tt> object that is guaranteed to be a superclass of all records in this source.
   * <p>
   * Implemented to return <tt>Object.class</tt>.
   */
  public Class<?> getRecordClass() {
    return Object.class;
  }

  /**
   * Returns the number of records in this source.
   * <p>
   * Implemented to throw <tt>UnsupportedOperationException</tt>.
   */
  public long size() {throw new UnsupportedOperationException();}

  /**
   * Returns the estimated number of records in this source.
   * <p>
   * Implemented to forward the call to <tt>size()</tt>.
   */
  public long getEstimatedSize() {
    return size();
  }

  /**
   * Returns a list of tags for events in this source.
   * <p>
   * Implemented to throw <tt>UnsupportedOperationException</tt>.
   */
  public List<RecordTag> getTags() {throw new UnsupportedOperationException();}

  
// -- Current record data : ----------------------------------------------------
  
  /**
   * Returns the index of the current record.
   * <p>
   * Implemented to throw <tt>UnsupportedOperationException</tt>.
   */
  public long getCurrentIndex() {throw new UnsupportedOperationException();}
  
  /**
   * Returns the tag of the current record.
   * <p>
   * Implemented to throw <tt>UnsupportedOperationException</tt>.
   */
  public RecordTag getCurrentTag() {throw new UnsupportedOperationException();}

  /** 
   * Releases any resources associated with the current record.
   * <p>
   * Implemented to do nothing.
   */
  public void releaseRecord() {}
  
  
// -- Checking whether this source supports the specified positioning method : -
  
  /** 
   * Returns <tt>true</tt> if this source supports reloading current record.
   */
  public boolean supportsCurrent() {return false;}
  
  /**
   * Returns <tt>true</tt> if this source supports loading next record. 
   */
  public boolean supportsNext() {return false;}
  
  /** 
   * Returns <tt>true</tt> if this source supports loading previous record. 
   */
  public boolean supportsPrevious() {return false;}
  
  /**
   * Returns <tt>true</tt> if this source supports selecting records by index.
   */
  public boolean supportsIndex() {return false;}
  
  /**
   * Returns <tt>true</tt> if this source supports selecting records by tag.
   */
  public boolean supportsTag() {return false;}
  
  /** 
   * Returns <tt>true</tt> if this source supports selecting records by offset with respect to the current record.
   */
  public boolean supportsShift() {return false;}
 

// -- Checking whether the specified record exist : ----------------------------

  /** 
   * Returns <tt>true</tt> if this source can reload the current record.
   */
  public boolean hasCurrent() {return supportsCurrent();}

  /** 
   * Returns <tt>true</tt> if this source can load the next record.
   */
  public boolean hasNext() {return supportsNext();}

  /** 
   * Returns <tt>true</tt> if this source can load the previous record.
   */
  public boolean hasPrevious() {return supportsPrevious();}

  /** 
   * Returns <tt>true</tt> if this source has a record with the specified index.
   */
  public boolean hasIndex(long index) {return supportsIndex();}

  /** 
   * Returns <tt>true</tt> if this source has a record with the specified tag.
   */
  public boolean hasTag(RecordTag tag) {return supportsTag();}

  /** 
   * Returns <tt>true</tt> if this source can shift by <tt>numberOfRecords</tt> records.
   */
  public boolean hasShift(long numberOfRecords) {return supportsShift();}
  
  
// -- Loading the specified record : -------------------------------------------

  /**
   * Reloads the current record.
   */
  public void current() throws IOException, NoSuchRecordException {throw new UnsupportedOperationException();}

  /**
   * Loads the next record.
   */
  public void next() throws IOException, NoSuchRecordException {throw new UnsupportedOperationException();}

  /**
   * Loads the previous record.
   */
  public void previous() throws IOException, NoSuchRecordException {throw new UnsupportedOperationException();}

  /**
   * Loads the record specified by the index.
   */
  public void jump(long index) throws IOException, NoSuchRecordException {throw new UnsupportedOperationException();}

  /**
   * Loads the record specified by the tag.
   */
  public void jump(RecordTag tag) throws IOException, NoSuchRecordException {throw new UnsupportedOperationException();}

  /**
   * Loads the record specified by the offset with respect to the current cursor position. 
   */
  public void shift(long numberOfRecords) throws IOException, NoSuchRecordException {throw new UnsupportedOperationException();}
  
  /**
   * Loads the record specified by name.
   * The specified name is converted to a record tag by calling {@link #parseTag(String)}.
   */
  public void jump(String tagName) throws IOException, NoSuchRecordException {
    jump(parseTag(tagName));
  }

  
// -- Rewinding the source : ---------------------------------------------------
 
  /** 
   * Returns <tt>true</tt> if this source supports rewind operation.
   */
  public boolean supportsRewind() {return false;}
  
  /** 
   * Returns <tt>true</tt> if this source in its current state can be rewound.
   */
  public boolean hasRewind() {return supportsRewind();}
  
  /**
   * Positions the cursor of this source before the first record.
   */
  public void rewind() throws IOException {throw new UnsupportedOperationException();}
  
  
// -- Closing the source : -----------------------------------------------------

  /**
   * Close the record source and release any associated resources.
   * <p>
   * Implemented to do nothing.
   */
  public void close() throws IOException {}
  
  
// -- Utility methods : --------------------------------------------------------
  
  /**
   * Returns a tag corresponding to the specified string.
   * <p>
   * Implemented to return an instance of {@link DefaultRecordTag} with the specified name.
   */
  public RecordTag parseTag(String s) {
    return new DefaultRecordTag(s);
  }


// -----------------------------------------------------------------------------
}
