package hep.aida.ref.rootwriter.converter;

import hep.io.root.output.classes.TObjString;

/**
 * Dummy converter to be used for AIDA objects if no real converter is available.
 * Creates a {@link TObjString} with information on name and type of the unconvertible AIDA object.
 *
 * @author onoprien
 */
public class DefaultConverter implements Converter {

  @Override
  public TObjString convert(Object object) throws ConverterException {
    return null;
//    return new TObjString("Not convertible: "+ object.type() +":"+ object.name());
  }

}
