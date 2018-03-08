package hep.aida.ref.hbook;
import hep.aida.IAxis;
import hep.aida.IManagedObject;
import hep.aida.ref.histogram.FixedAxis;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.histogram.Histogram2D;
import hep.aida.ref.tuple.AbstractTuple;
import hep.io.hbook.ColumnwiseTuple;
import hep.io.hbook.OneDHistogram;
import hep.io.hbook.RowwiseTuple;
import hep.io.hbook.TwoDHistogram;

/**
 * Adapts hbook histograms and Tuples to their corresponding AIDA interfaces
 * @author tonyj
 * @version $Id
 */
class Converter
{
   private static boolean useIdForName = true;
   
   static void setUseIdForName(boolean b) { useIdForName = b; }
   
   private static Histogram1D convert(OneDHistogram h1)
   {
      Histogram1D hist = new Histogram1D();
      String name = (useIdForName) ? String.valueOf(h1.id()) : h1.getName();
      hist.setName(name);
      // This is to prevent problems with paw histograms with zero bins 
      // See JAS-156
      if ( h1.getXNBins() < 1 )
          return null;
      IAxis xAxis = new FixedAxis(h1.getXNBins(),h1.getXMin(),h1.getXMax()); 
      hist.initHistogram1D(xAxis, null);
      double[] heights1d = h1.getBins();
      double[] errors1d = h1.getErrors();
      int[] entries = new int[heights1d.length];
      for ( int i = 0; i < entries.length; i++ )
          entries[i] = (int)heights1d[i];
      hist.setContents(heights1d, errors1d,entries,null,null);
      hist.setNEntries(h1.getNEntries());
      hist.setValidEntries(h1.getNEntries()-hist.extraEntries());
      hist.setMeanAndRms(h1.getXMean(), h1.getXRMS());
      hist.setTitle(h1.getName());
      return hist;
   }
   private static Histogram2D convert(TwoDHistogram h2)
   {
      Histogram2D hist = new Histogram2D();
      String name = (useIdForName) ? String.valueOf(h2.id()) : h2.getName();
      hist.setName(name);
      // This is to prevent problems with paw histograms with zero bins 
      // See JAS-156
      if ( h2.getXNBins() < 1 || h2.getYNBins() < 1)
          return null;
      IAxis xAxis = new FixedAxis(h2.getXNBins(),h2.getXMin(),h2.getXMax()); 
      IAxis yAxis = new FixedAxis(h2.getYNBins(),h2.getYMin(),h2.getYMax()); 
      hist.initHistogram2D(xAxis, yAxis, null);
      double[][] heights2d = h2.getBins();
      double[][] errors2d = h2.getErrors();
      int[][] entries = new int[heights2d.length][heights2d[0].length];
      for ( int i = 0; i < entries.length; i++ )
          for (int k = 0; k < entries[0].length; k++)
              entries[i][k] = (int)heights2d[i][k];
      hist.setContents(heights2d, errors2d,entries,null,null,null,null);
      int nEntries = h2.getNEntries();
      hist.setNEntries(nEntries);
      hist.setValidEntries(nEntries-hist.extraEntries());
      if (h2.getXRMS() != 0 && h2.getXMean() != 0)
      {
         hist.setMeanX(h2.getXMean());
         hist.setRmsX(h2.getXRMS());
      }
      if (h2.getYRMS() != 0 && h2.getYMean() != 0)
      {
         hist.setMeanY(h2.getYMean());
         hist.setRmsY(h2.getYRMS());
      }
      hist.setTitle(h2.getName());
      return hist;
   }
   private static AbstractTuple convert(ColumnwiseTuple t)
   {
      return new HBookColumnwiseTuple(t);
   }
   private static AbstractTuple convert(RowwiseTuple t)
   {
      return new HBookRowwiseTuple(t);
   }
   static IManagedObject convert(Object o)
   {
      if      (o instanceof OneDHistogram)   return convert((OneDHistogram) o);
      else if (o instanceof TwoDHistogram)   return convert((TwoDHistogram) o);
      else if (o instanceof ColumnwiseTuple) return convert((ColumnwiseTuple) o);
      else if (o instanceof RowwiseTuple)    return convert((RowwiseTuple) o);
      else return null;
   }
}
