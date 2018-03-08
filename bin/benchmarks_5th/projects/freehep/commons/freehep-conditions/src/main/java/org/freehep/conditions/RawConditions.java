package org.freehep.conditions;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * {@link Conditions} object that presents its content as a stream.
 *
 * @version $Id: $
 * @author Tony Johnson
 */
public interface RawConditions extends Conditions {

  /**
   * Returns input stream for reading the content of this Conditions.
   * 
   * @throws IOException If error occurs while opening the stream.
   * @throws ConditionsInvalidException if this <tt>Conditions</tt> object has 
   *         not been successfully updated in response to the latest update triggering event.
   */
  InputStream getInputStream() throws IOException, ConditionsInvalidException;

  /**
   * Returns <tt>Reader</tt> for accessing the content of this Conditions.
   * 
   * @throws IOException If error occurs while opening the stream.
   * @throws ConditionsInvalidException if this <tt>Conditions</tt> object has 
   *         not been successfully updated in response to the latest update triggering event.
   */
  Reader getReader() throws IOException, ConditionsInvalidException;
  
}
