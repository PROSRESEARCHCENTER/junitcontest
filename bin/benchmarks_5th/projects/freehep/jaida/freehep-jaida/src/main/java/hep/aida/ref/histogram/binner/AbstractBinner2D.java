package hep.aida.ref.histogram.binner;

/**
 * Base for implementing 2D binners.
 *
 * @author onoprien
 */
abstract public class AbstractBinner2D implements Binner2D {
  
  @Override
  public double sumWW(int xBin, int yBin) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public double sumXW(int xBin, int yBin) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public double sumXXW(int xBin, int yBin) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public double sumYW(int xBin, int yBin) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public double sumYYW(int xBin, int yBin) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public double sumXYW(int xBin, int yBin) {
    throw new UnsupportedOperationException();
  }
  
}
