package org.freehep.record.source;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Default immutable implementation of {@link RecordTag}.
 *
 * @author Dmitry Onoprienko
 * @version $Id$
 */
public class DefaultRecordTag implements RecordTag {

// -- Private parts : ----------------------------------------------------------
  
  static final private AtomicLong _ordinal = new AtomicLong(0L);
  final protected String _name;

// -- Construction and initialization : ----------------------------------------

  /**
   * Constructs a tag with an auto-generated name.
   * The name is <tt>"_Tag_N"</tt>, where <tt>N</tt> is the numerical ID of this instance.
   * Instances are numbered in the order of creation.
   */
  public DefaultRecordTag() {
    _name = "_Tag_" + _ordinal.getAndIncrement();
  }

  /** Constructs a tag with the specified name. */
  public DefaultRecordTag(String name) {
    _name = name;
  }

  /** Constructs a tag with the name equal to the human readable name of the specified tag. */
  public DefaultRecordTag(RecordTag tag) {
    _name = tag.humanReadableName();
  }

// -- Getters : ----------------------------------------------------------------

  /** Returns the name of this tag. */
  public String humanReadableName() {
    return _name;
  }
  
// -- Overriding Object : ------------------------------------------------------

  /**
   * Indicates whether some other object is "equal to" this tag.
   * @return <tt>true</tt> if the specified object is a {@link RecordTag}, and
   * its human readable name is equal to the name of this tag.
   */
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (! (obj instanceof RecordTag)) return false;
    return _name.equals(((RecordTag)obj).humanReadableName());
  }
  
  public int hashCode() {
    return _name.hashCode();
  }
  
  public String toString() {
    return _name;
  }

// -----------------------------------------------------------------------------
}
