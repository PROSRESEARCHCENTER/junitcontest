package org.freehep.conditions.base;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.freehep.conditions.RawConditions;

/**
 * Default implementation of {@link RawConditions} interface.
 * Getters forward the call to corresponding methods of the {@link ConditionsReader}.
 * Update procedure is inherited from {@link DefaultConditions}.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class DefaultRawConditions extends DefaultConditions implements RawConditions {

// -- Private parts : ----------------------------------------------------------

  protected Closeable _stream;

// -- Construction and initialization : ----------------------------------------
  
  public DefaultRawConditions(ConditionsReader reader, String name) {
    super(reader, name);
  }
  
// -- Getters : ----------------------------------------------------------------
  
  /** Returns this <tt>Conditions</tt> category. */
  @Override
  public Category getCategory() {
    return Category.RAW;
  }
  
// -- Updating : ---------------------------------------------------------------

  @Override
  public void destroy() {
    try {
      if (_stream != null) _stream.close();
    } catch (IOException x) {
    }
    super.destroy();
  }

// -- Implementing RawConditions : ---------------------------------------------

  @Override
  public InputStream getInputStream() throws IOException {
    checkValidity();
    if (_stream != null) _stream.close();
    _stream = getConditionsReader().open(getName());
    return (InputStream) _stream;
  }

  @Override
  public Reader getReader() throws IOException {
    checkValidity();
    if (_stream != null) _stream.close();
    _stream = getConditionsReader().getReader(getName());
    return (Reader) _stream;
  }
  
  

}
