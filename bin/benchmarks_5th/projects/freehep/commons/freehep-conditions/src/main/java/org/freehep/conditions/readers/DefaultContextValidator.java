package org.freehep.conditions.readers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.freehep.conditions.base.ConditionsReader;

/**
 * Implements commonly used strategies for validating context URLs used by {@link URLConditionsReader}.
 * 
 * @author onoprien
 */
public class DefaultContextValidator implements URLValidator {

// -- Private parts : ----------------------------------------------------------
  
  final boolean _acceptNull;
  final private ConditionsReader _reader;
  final private String[] _names;

// -- Construction and initialization : ----------------------------------------

  /**
   * Equivalent to three-argument constructor with <tt>null</tt> reader and empty list of conditions names.
   */
  public DefaultContextValidator(boolean acceptNull) {
    _acceptNull = acceptNull;
    _reader = null;
    _names = null;
  }

  /**
   * Constructs an instance of DefaultContextValidator.
   * 
   * @param acceptNull Defines whether <tt>null</tt> URL is considered valid.
   * @param reader Reader whose <tt>open(...)</tt> method will be called to validate conditions names.
   * @param conditionsNames Conditions names to check as a part of validation.
   */
  public DefaultContextValidator(boolean acceptNull, ConditionsReader reader, String... conditionsNames) {
    _acceptNull = acceptNull;
    _reader = reader;
    _names = conditionsNames.length == 0 ? null : conditionsNames;
  }
  
// -- Validation : -------------------------------------------------------------

  /** Returns <tt>true</tt> the specified URL is valid. */
  @Override
  public boolean isValid(URL context) {
    if (context == null) return _acceptNull;
    if (_names == null) {
      try {
        context.getContent();
        return true;
      } catch (IOException x) {
        return false;
      }
    } else {
      try {
        for (String name : _names) {
          checkName(name);
        }
      } catch (Exception x) {
        return false;
      }
      return true;
    }
  }
  
  protected void checkName(String name) throws Exception {
    InputStream in = _reader == null ? (new URL(name)).openStream() : _reader.open(name);
    in.close();
  }

}
