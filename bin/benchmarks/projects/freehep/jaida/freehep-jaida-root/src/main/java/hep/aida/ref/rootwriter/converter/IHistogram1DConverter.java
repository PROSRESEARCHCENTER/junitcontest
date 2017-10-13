package hep.aida.ref.rootwriter.converter;

import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.IManagedObject;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.histogram.binner.AbstractBinner1D;
import hep.aida.ref.histogram.binner.EfficiencyBinner1D;
import hep.io.root.output.classes.TArrayD;
import hep.io.root.output.classes.hist.TAxis;
import hep.io.root.output.classes.hist.TH1D;

/**
 * Converter from {@link IHistogram1D IHistogram1D} to ROOT {@link TH1D}.
 *
 * @author onoprien
 */
public class IHistogram1DConverter implements Converter {

  @Override
  public TH1D convert(Object object) throws ConverterException {
    
    // Extract info through AIDA interface only first :
    
    IHistogram1D aHist;
    IManagedObject aObject;
    try {
      aHist = (IHistogram1D) object;
      aObject = (IManagedObject) object;
    } catch (ClassCastException x) {
      throw new IllegalArgumentException(x);
    }
    
    IAxis aAxis = aHist.axis();
    int nBins = aAxis.bins();
    int nCells = nBins + 2;
    double[] data = new double[nCells];
    double xMin = aAxis.lowerEdge();
    double xMax = aAxis.upperEdge();

    data[0] = aHist.binHeight(IAxis.UNDERFLOW_BIN);
    for (int i = 0; i < nBins; i++) {
      data[1 + i] = aHist.binHeight(i);
    }
    data[nBins + 1] = aHist.binHeight(IAxis.OVERFLOW_BIN);
    
    TH1D rHist = new TH1D(aObject.name(), nBins, xMin, xMax, data);
    
    rHist.setTitle(aHist.title());
    
    if (!aAxis.isFixedBinning()) {
      TAxis rAxis = rHist.getfXaxis();
      double[] bins = new double[nBins+1];
      for (int i=0; i<nBins; i++) {
        bins[i] = aAxis.binLowerEdge(i);
      }
      bins[nBins] = xMax;
      rAxis.setfXbins(new TArrayD(bins));
    }
    
    rHist.setfEntries(aHist.entries());
    
    // Now see if we can get more info from a specific implementation :
    
    if (aHist instanceof Histogram1D) { // reference implementation
      
      Histogram1D jHist = (Histogram1D) aHist;
      double[] sums = jHist.getStatistics();
      rHist.setEntries(sums[0]);
      rHist.setfTsumw(sums[1]);
      rHist.setfTsumw2(sums[2]);
      rHist.setfTsumwx(sums[3]);
      rHist.setfTsumwx2(sums[4]);
      
      AbstractBinner1D binner = jHist.binner();
      if (binner instanceof EfficiencyBinner1D) {
        EfficiencyBinner1D effBinner = (EfficiencyBinner1D) binner;
        double[] fSumw2 = new double[nCells];
        for (int i=0; i<nCells; i++) {
          double error = effBinner.plusError(i);
          fSumw2[i] = error * error;
        }
        rHist.setfSumw2(new TArrayD(fSumw2));
      }
      
    } else { // unknown implementation
      
      int entries = aHist.entries();
      if (entries > 0) {
        rHist.setEntries(entries);
        rHist.setfTsumw(aHist.sumBinHeights());
        double sw2 = aHist.sumBinHeights();
        sw2 = (sw2*sw2) / aHist.equivalentBinEntries();
        if (!(Double.isNaN(sw2) || Double.isInfinite(sw2))) rHist.setfTsumw2(sw2);
      }
      
    }
    
    return rHist;
  }

}
