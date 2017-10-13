package hep.aida.ref.rootwriter.converter;

import hep.aida.ICloud1D;
import hep.aida.IManagedObject;
import hep.aida.ref.histogram.FixedAxis;
import hep.aida.ref.histogram.Histogram1D;

/**
 * Converter from {@link ICloud1D ICloud1D} to ROOT {@link TH1D}.
 * 
 * @author onoprien
 */
public class ICloud1DConverter implements Converter {

  @Override
  public Object convert(Object object) throws ConverterException {
    
    ICloud1D aCloud;
    IManagedObject aObject;
    try {
      aCloud = (ICloud1D) object;
      aObject = (IManagedObject) object;
    } catch (ClassCastException x) {
      throw new IllegalArgumentException(x);
    }
    
    if (aCloud.isConverted()) {
      throw new ConverterException("IHistogram1D", aCloud.histogram());
    }

    double xMin = aCloud.lowerEdge();
    double xMax = aCloud.upperEdge();
    int nBins = 50;
    
    FixedAxis aAxis = new FixedAxis(nBins, xMin, xMax);
    Histogram1D aHist = new Histogram1D(aObject.name(), aCloud.title(), aAxis);
    aCloud.fillHistogram(aHist);
    throw new ConverterException("IHistogram1D", aHist);
  }
  
}
