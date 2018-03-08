package org.freehep.record.source;

/** 
 * An exception thrown when attempting to load a record that does not exist.
 * 
 * @version $Id: NoSuchRecordException.java 13910 2011-11-04 18:30:26Z onoprien $
 */
public class NoSuchRecordException extends Exception {
  
  public NoSuchRecordException() {}
          
  public NoSuchRecordException(String message) {super(message);}
  
  public NoSuchRecordException(String message, Throwable cause) {super(message, cause);}

  public NoSuchRecordException(Throwable cause) {super(cause);}

}