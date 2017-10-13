package hep.aida.ref.rootwriter.converter;

import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.IManagedObject;
import hep.aida.IProfile1D;
import hep.aida.ref.histogram.Profile1D;
import hep.aida.ref.histogram.binner.Binner2D;
import hep.io.root.output.classes.TArrayD;
import hep.io.root.output.classes.hist.TAxis;
import hep.io.root.output.classes.hist.TH1D;
import hep.io.root.output.classes.hist.TProfile;

/**
 * Converter from {@link IHistogram1D IHistogram1D} to ROOT {@link TH1D}.
 *
 * @author onoprien
 */
public class IProfile1DConverter implements Converter {

  @Override
  public TH1D convert(Object object) throws ConverterException {
    
    // Extract info through AIDA interface only first :
    
    IProfile1D aHist;
    IManagedObject aObject;
    try {
      aHist = (IProfile1D) object;
      aObject = (IManagedObject) object;
    } catch (ClassCastException x) {
      throw new IllegalArgumentException(x);
    }
    
    IAxis aAxis = aHist.axis();
    int nBins = aAxis.bins();
    int nCells = nBins + 2;
    double xMin = aAxis.lowerEdge();
    double xMax = aAxis.upperEdge();
    
    double[] yw = new double[nCells];
    double[] yyw = new double[nCells];
    double[] w = new double[nCells];
    double[] w2 = new double[nCells];
    
    TProfile rHist = new TProfile(aObject.name(), nBins, xMin, xMax, yyw, yw, w, w2);
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
    
    // Now see if we can get more info from a specific implementation :
    
    if (aHist instanceof Profile1D) { // reference implementation
      
      Profile1D jHist = (Profile1D) aHist;
      double[] sums = jHist.getStatistics();
      rHist.setfEntries(sums[0]);
      rHist.setfTsumw(sums[1]);
      rHist.setfTsumw2(sums[2]);
      rHist.setfTsumwx(sums[3]);
      rHist.setfTsumwx2(sums[4]);
      rHist.setfTsumwy(sums[5]);
      rHist.setfTsumwy2(sums[6]);
      
      Binner2D binner = jHist.binner();
      try {
        for (int i = 0; i < nCells; i++) {
          yw[i] = binner.sumYW(i, 1);
          yyw[i] = binner.sumYYW(i, 1);
          w[i] = binner.height(i, 1);
          w2[i] = binner.sumWW(i, 1);
        }
      } catch (UnsupportedOperationException x) {
        throw new IllegalArgumentException();
      }
      
    } else { // unknown implementation. At the moment, impossible to use.
      
      throw new IllegalArgumentException();
      
//      int nEntries = aHist.entries();
//      if (nEntries > 0) {
//        
//        rHist.setfEntries(nEntries);
//        rHist.setfTsumw(aHist.sumBinHeights());
//
//        data[0] = aHist.binHeight(IAxis.UNDERFLOW_BIN);
//        entries[0] = aHist.binEntries(IAxis.UNDERFLOW_BIN);
//        for (int i = 0; i < nBins; i++) {
//          int n = aHist.binEntries(i);
//          if (n > 0) {
//            entries[1 + i] = n;
//            data[1 + i] = aHist.binHeight(i) * n;
//          }
//        }
//        int over = nBins + 1;
//        data[over] = aHist.binHeight(IAxis.OVERFLOW_BIN);
//        entries[over] = aHist.binEntries(IAxis.OVERFLOW_BIN);
//
//      }
      
    }
    
    return rHist;
  }

}
