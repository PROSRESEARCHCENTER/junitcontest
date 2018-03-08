package hep.aida.ref.rootwriter.converter;

import hep.aida.ICloud2D;
import hep.aida.IManagedObject;
import hep.aida.ref.histogram.FixedAxis;
import hep.aida.ref.histogram.Histogram2D;
import hep.io.root.output.classes.hist.TH2D;

/**
 * Converter from {@link ICloud2D ICloud2D} to ROOT {@link TH2D}.
 * 
 * @author onoprien
 */
public class ICloud2DConverter implements Converter {

  @Override
  public Object convert(Object object) throws ConverterException {
    
    ICloud2D aCloud;
    IManagedObject aObject;
    try {
      aCloud = (ICloud2D) object;
      aObject = (IManagedObject) object;
    } catch (ClassCastException x) {
      throw new IllegalArgumentException(x);
    }
    
    if (aCloud.isConverted()) {
      throw new ConverterException("IHistogram2D", aCloud.histogram());
    }

    double xMin = aCloud.lowerEdgeX();
    double xMax = aCloud.upperEdgeX();
    int nXBins = 50;
    double yMin = aCloud.lowerEdgeY();
    double yMax = aCloud.upperEdgeY();
    int nYBins = 50;
    
    FixedAxis aXAxis = new FixedAxis(nXBins, xMin, xMax);
    FixedAxis aYAxis = new FixedAxis(nYBins, yMin, yMax);
    Histogram2D aHist = new Histogram2D(aObject.name(), aCloud.title(), aXAxis, aYAxis);
    aCloud.fillHistogram(aHist);
    throw new ConverterException("IHistogram2D", aHist);
  }
  
  
}
