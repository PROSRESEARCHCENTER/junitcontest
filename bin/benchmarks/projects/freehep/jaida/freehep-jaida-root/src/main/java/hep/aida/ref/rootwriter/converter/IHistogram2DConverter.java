package hep.aida.ref.rootwriter.converter;

// Notes: 
//
// Histogram getters use "external" representation: (0...N-1) or OVERFLOW or UNDERFLOW
//
// Filling Histogram2D:
//
//            int xCoordToIndex = xAxis.coordToIndex(x); // --> (0...N-1) or OVERFLOW or UNDERFLOW
//            int yCoordToIndex = yAxis.coordToIndex(y); // --> (0...N-1) or OVERFLOW or UNDERFLOW
//            int xBin = mapBinNumber(xCoordToIndex, xAxis()); // UNDERFLOW --> 0, (0...N-1) --> (1...N), OVERFLOW --> N+1
//            int yBin = mapBinNumber(yCoordToIndex, yAxis()); // UNDERFLOW --> 0, (0...N-1) --> (1...N), OVERFLOW --> N+1
//            binner2D.fill(xBin, yBin, x, y, weight);
//            if ( ( xCoordToIndex >= 0 && yCoordToIndex >= 0) || useOutflows() ) {
//                validEntries++;
//                meanX += x*weight;
//                rmsX  += x*x*weight;
//                meanY += y*weight;
//                rmsY  += y*y*weight;
//                sumOfWeights += weight;
//                sumOfWeightsSquared += weight*weight;
//            }
//
// Bbinner getters take indices in the "internal" representation: 0 forunderflow, [1...N], N+1 for overflow.
//
// Filling Binner2D:
//
//    public void fill( int xBin, int yBin, double x, double y, double weight) {
//        entries[xBin][yBin]++;
//        heights[xBin][yBin]     += weight;
//        plusErrors[xBin][yBin]  += weight*weight;
//        meansX[xBin][yBin]      += x*weight;
//        rmssX[xBin][yBin]       += x*x*weight;
//        meansY[xBin][yBin]      += y*weight;
//        rmssY[xBin][yBin]       += y*y*weight;
//    }
//
// ROOT histograms:
//
//   //      For all histogram types: nbins, xlow, xup
//   //        bin = 0;       underflow bin
//   //        bin = 1;       first bin with low-edge xlow INCLUDED
//   //        bin = nbins;   last bin with upper-edge xup EXCLUDED
//   //        bin = nbins+1; overflow bin
//   //      In case of 2-D or 3-D histograms, a "global bin" number is defined.
//   //      For example, assuming a 3-D histogram with binx,biny,binz, the function
//   //        Int_t bin = h->GetBin(binx,biny,binz);
//   //      returns a global/linearized bin number. This global bin is useful
//   //      to access the bin information independently of the dimension.
//   //   -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
//   Int_t nx, ny, nz;
//   if (GetDimension() < 2) {
//      nx  = fXaxis.GetNbins()+2;
//      if (binx < 0)   binx = 0;
//      if (binx >= nx) binx = nx-1;
//      return binx;
//   }
//   if (GetDimension() < 3) {
//      nx  = fXaxis.GetNbins()+2;
//      if (binx < 0)   binx = 0;
//      if (binx >= nx) binx = nx-1;
//      ny  = fYaxis.GetNbins()+2;
//      if (biny < 0)   biny = 0;
//      if (biny >= ny) biny = ny-1;
//      return  binx + nx*biny;
//   }
//   if (GetDimension() < 4) {
//      nx  = fXaxis.GetNbins()+2;
//      if (binx < 0)   binx = 0;
//      if (binx >= nx) binx = nx-1;
//      ny  = fYaxis.GetNbins()+2;
//      if (biny < 0)   biny = 0;
//      if (biny >= ny) biny = ny-1;
//      nz  = fZaxis.GetNbins()+2;
//      if (binz < 0)   binz = 0;
//      if (binz >= nz) binz = nz-1;
//      return  binx + nx*(biny +ny*binz);
//   }

import hep.aida.IAxis;
import hep.aida.IHistogram2D;
import hep.aida.IManagedObject;
import hep.aida.ref.histogram.Histogram2D;
import hep.io.root.output.classes.TArrayD;
import hep.io.root.output.classes.hist.TAxis;
import hep.io.root.output.classes.hist.TH2D;

/**
 * Converter from {@link IHistogram1D IHistogram1D} to ROOT {@link TH1D}.
 * 
 * @author onoprien
 */
public class IHistogram2DConverter implements Converter {

  @Override
  public Object convert(Object object) throws ConverterException {
    
    IHistogram2D aHist;
    IManagedObject aObject;
    try {
      aHist = (IHistogram2D) object;
      aObject = (IManagedObject) object;
    } catch (ClassCastException x) {
      throw new IllegalArgumentException(x);
    }
    
    IAxis aXAxis = aHist.xAxis();
    int nXBins = aXAxis.bins();
    double xMin = aXAxis.lowerEdge();
    double xMax = aXAxis.upperEdge();
    IAxis aYAxis = aHist.yAxis();
    int nYBins = aYAxis.bins();
    double yMin = aYAxis.lowerEdge();
    double yMax = aYAxis.upperEdge();
    int nXCells = nXBins + 2;
    int nYCells = nYBins + 2;
    int nCells = nXCells * nYCells;
    
    double[] data = new double[nCells];
    
    for (int i=0; i<nCells; i++) {  // Ugly but no bulk getters in Histogram2D
      int x = i % nXCells;
      if (x == 0) {
        x = IAxis.UNDERFLOW_BIN;
      } else if (x <= nXBins) {
        x--;
      } else {
        x = IAxis.OVERFLOW_BIN;
      }
      int y = i / nXCells;
      if (y == 0) {
        y = IAxis.UNDERFLOW_BIN;
      } else if (y <= nYBins) {
        y--;
      } else {
        y = IAxis.OVERFLOW_BIN;
      }
      data[i] = aHist.binHeight(x, y);
    }
    
//    for (int x=0; x<nXBins; x++) {
//      for (int y=0; y<nYBins; y++) {
//        data[x+1 + nXCells*(y+1)] = aHist.binHeight(x, y);
//      }
//    }
    
    TH2D rHist = new TH2D(aObject.name(), nXBins, xMin, xMax, nYBins, yMin, yMax, data);
    
    rHist.setTitle(aHist.title());
    
    if (!aXAxis.isFixedBinning()) {
      TAxis rAxis = rHist.getfXaxis();
      double[] bins = new double[nXBins+1];
      for (int i=0; i<nXBins; i++) {
        bins[i] = aXAxis.binLowerEdge(i);
      }
      bins[nXBins] = xMax;
      rAxis.setfXbins(new TArrayD(bins));
    }
    if (!aYAxis.isFixedBinning()) {
      TAxis rAxis = rHist.getfYaxis();
      double[] bins = new double[nYBins+1];
      for (int i=0; i<nYBins; i++) {
        bins[i] = aYAxis.binLowerEdge(i);
      }
      bins[nYBins] = yMax;
      rAxis.setfXbins(new TArrayD(bins));
    }
    
    if (aHist instanceof Histogram2D) { // reference implementation
      
      Histogram2D jHist = (Histogram2D) aHist;
      double[] sums = jHist.getStatistics();
      rHist.setEntries(sums[0]);
      rHist.setfTsumw(sums[1]);
      rHist.setfTsumw2(sums[2]);
      rHist.setfTsumwx(sums[3]);
      rHist.setfTsumwx2(sums[4]);
      rHist.setfTsumwy(sums[5]);
      rHist.setfTsumwy2(sums[6]);
      
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
