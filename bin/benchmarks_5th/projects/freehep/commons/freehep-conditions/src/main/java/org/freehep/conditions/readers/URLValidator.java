package org.freehep.conditions.readers;

import java.net.URL;

/**
 * Interface to be implemented by classes that validate URLs.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public interface URLValidator {

  /** Returns <tt>true</tt> the specified URL is valid. */
  boolean isValid(URL url);

}
