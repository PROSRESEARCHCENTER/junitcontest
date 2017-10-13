package hep.aida.ref.rootwriter.converter;

/**
 * Interface to be implemented by classes that convert AIDA objects into objects that can be saved into some specific {@link Store}.
 *
 * @author onoprien
 */
public interface Converter {
  
  /**
   * Converts the specified object.
   * <p>
   * To use converter chaining, this method might throw {@link ConverterException} to pass 
   * the job to another <tt>Converter</tt>. The <tt>ConverterException</tt> must be 
   * instantiated with a string key to be used in selecting the next converter, and with
   * an object to be passed to that converter (which might be the original unconverted object 
   * or an intermediate object created by this converter).
   * 
   * @return The result of conversion, or <tt>null</tt> if the object needs to be quietly ignored by the store.
   * @throws ConverterException If the conversion should be finished by another converter.
   * @throws IllegalArgumentException if the provided object cannot be handled by this converter.
   */
  Object convert(Object object) throws ConverterException;
  
}
