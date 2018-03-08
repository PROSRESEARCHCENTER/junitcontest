package hep.aida.ref.rootwriter.converter;

/**
 *
 * @author onoprien
 */
public class ConverterException extends Exception {
  
  private String _key;
  private Object _object;
  
  public ConverterException(String key, Object object) {
    _key = key;
    _object = object;
  }
  
  public String getKey() {
    return _key;
  }
  
  public Object getObject() {
    return _object;
  }
  
}
