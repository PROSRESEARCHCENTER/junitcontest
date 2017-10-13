package org.freehep.conditions.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.freehep.conditions.Conditions;
import org.freehep.conditions.ConditionsConverter;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.ConditionsUpdateException;
import org.freehep.conditions.ConditionsInvalidException;

/**
 * Interface to be implemented by classes that fetch data for {@link Conditions} objects from storage.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public interface ConditionsReader {
  
  /**
   * Returns <tt>ConditionsManager</tt> this reader is used by.
   */
  DefaultConditionsManager getConditionsManager();

  /**
   * Creates <tt>RawConditions</tt> for the given name.
   * @throws ConditionsInvalidException if the requested conditions can not be created.
   */
  DefaultRawConditions createRawConditions(String name) throws ConditionsInvalidException;

  /**
   * Creates <tt>ConditionsSet</tt> for the given name.
   * @throws ConditionsInvalidException if the named conditions can not be created.
   */
  AbstractConditionsSet createConditions(String name) throws ConditionsInvalidException;

  /**
   * Creates <tt>ConditionsSet</tt> for the given name.
   * @throws ConditionsInvalidException if the named conditions can not be created.
   */
  <T> DefaultCachedConditions<T> createCachedConditions(String name, ConditionsConverter<T> converter) throws ConditionsInvalidException;
  
  /**
   * Opens input stream that can be used by <tt>RawConditions</tt> with the given name.
   * @throws IOException 
   */
  InputStream open(String name) throws IOException, ConditionsInvalidException;
  
  /**
   * Creates <tt>Reader</tt> that can be used by <tt>RawConditions</tt> with the given name.
   * @throws IOException 
   */
  Reader getReader(String name) throws IOException, ConditionsInvalidException;
  
//  Properties getProperties(String name) throws IllegalArgumentException;
  
  /**
   * Updates this reader if necessary in response to the specified event.
   * This method is called by the conditions framework if {@link DefaultConditionsManager} is used.
   * 
   * @param event The event triggering this update.
   * @return <tt>True</tt> if conditions handled by this reader may have changed as a result of the update.
   * @throws ConditionsUpdateException if this reader is unable to update itself.
   */
  boolean update(ConditionsEvent event) throws ConditionsUpdateException;
  
  /**
   * Updates the specified Conditions if necessary in response to the last event.
   * This method is called by the conditions framework if {@link DefaultConditionsManager} is used.
   * The method is called after the reader itself has been updated in a call to
   * {@link #update(ConditionsEvent)}, and only if that call returned <tt>true</tt>.
   * 
   * @return <tt>True</tt> if the Conditions have changed as a result of the update.
   * @throws ConditionsInvalidException if this reader is unable to update the specified <tt>Conditions</tt>.
   */
  boolean update(DefaultConditions conditions) throws ConditionsInvalidException;
  
}
