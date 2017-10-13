package org.freehep.record.source;

/** 
 * A tag that identifies a record in a {@link RecordSource}. 
 * @version $Id: RecordTag.java 13906 2011-10-19 22:01:30Z onoprien $
 */
public interface RecordTag
{
   /**
    * Returns a human-readable name for the record corresponding to this tag.
    */   
   String humanReadableName();
}
