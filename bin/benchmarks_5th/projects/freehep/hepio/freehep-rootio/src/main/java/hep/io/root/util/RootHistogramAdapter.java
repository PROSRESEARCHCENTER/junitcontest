package hep.io.root.util;

import hep.io.root.interfaces.TH1;
import hep.io.root.interfaces.TH1D;
import hep.io.root.interfaces.TH1F;
import hep.io.root.interfaces.TH2;
import hep.io.root.interfaces.TH2D;
import hep.io.root.interfaces.TH2F;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.Statistics;


class RootHistogramAdapter
{
   static DataSource create(TH1 rootHisto)
   {
      if      (rootHisto instanceof TH1F) return new Root1FHistogramAdapter((TH1F) rootHisto);
      else if (rootHisto instanceof TH1D) return new Root1DHistogramAdapter((TH1D) rootHisto);
      else if (rootHisto instanceof TH2F) return new Root2FHistogramAdapter((TH2F) rootHisto);
      else if (rootHisto instanceof TH2D) return new Root2DHistogramAdapter((TH2D) rootHisto);
      else return null;
   }
   
   private static class Root1DHistogramAdapter extends Root1HistogramAdapter
   {
      private TH1D th1d;
      Root1DHistogramAdapter(TH1D rootHisto)
      {
         super(rootHisto);
         this.th1d = rootHisto;
      }
      public double[][] rebin(int bins, double p2, double p3, boolean p4, boolean p5)
      {
         double[] data = new double[bins];
         double[] array = th1d.getArray();
         for (int i=0; i<bins; i++) data[i] = array[i+1];
         double[] darray = rootHisto.getSumw2();
         if (darray != null && darray.length>0)
         {
            double[] error = new double[bins];
            for (int i=0; i<bins; i++) error[i] = Math.sqrt(darray[i+1]);
            return new double[][]
            { data, error };
         }
         else
         {
            return new double[][]
            {data};
         }
      }
   }
   private static class Root1FHistogramAdapter extends Root1HistogramAdapter
   {
      private TH1F th1f;
      Root1FHistogramAdapter(TH1F rootHisto)
      {
         super(rootHisto);
         this.th1f = rootHisto;
      }
      public double[][] rebin(int bins, double p2, double p3, boolean p4, boolean p5)
      {
         double[] data = new double[bins];
         float[] array = th1f.getArray();
         for (int i=0; i<bins; i++) data[i] = array[i+1];
         double[] darray = rootHisto.getSumw2();
         if (darray != null && darray.length>0)
         {
            double[] error = new double[bins];
            for (int i=0; i<bins; i++) error[i] = Math.sqrt(darray[i+1]);
            return new double[][]
            { data, error };
         }
         else
         {
            return new double[][]
            {data};
         }
      }
   }
   private static abstract class Root1HistogramAdapter implements Rebinnable1DHistogramData, HasStatistics, Statistics
   {
      protected TH1 rootHisto;
      Root1HistogramAdapter(TH1 rootHisto)
      {
         this.rootHisto = rootHisto;
      }
      public double getMin()
      {
         return rootHisto.getXaxis().getXmin();
      }
      public double getMax()
      {
         return rootHisto.getXaxis().getXmax();
      }
      public int getBins()
      {
         return rootHisto.getXaxis().getNbins();
      }
      public boolean isRebinnable()
      {
         return false;
      }
      public int getAxisType()
      {
         return DOUBLE;
      }
      public String[] getAxisLabels()
      {
         return null;
      }
      public String getTitle()
      {
         return rootHisto.getTitle();
      }
      public Statistics getStatistics()
      {
         return this;
      }
      public String[] getStatisticNames()
      {
         return new String[]
         {"Entries", "Mean", "RMS"};
      }
      private double mean()
      {
         return rootHisto.getTsumwx()/rootHisto.getTsumw();
      }
      public double rms()
      {
         double sumw = rootHisto.getTsumw();
         double sumwx = rootHisto.getTsumwx();
         double sumwx2 = rootHisto.getTsumwx2();
         return Math.sqrt(sumwx2/sumw - sumwx*sumwx/sumw/sumw);
      }
      public double getStatistic(String name)
      {
         if      (name.equals("Entries")) return rootHisto.getEntries();
         else if (name.equals("Mean")) return mean();
         else if (name.equals("RMS")) return rms();
         else return 0;
      }
   }
   private static class Root2DHistogramAdapter extends Root2HistogramAdapter
   {
      private TH2D th2d;
      Root2DHistogramAdapter(TH2D rootHisto)
      {
         super(rootHisto);
         this.th2d = rootHisto;
      }
      public double[][][] rebin(int xbins, double p2, double p3,
      int ybins, double p6, double p7,
      boolean p4, boolean p5, boolean p8)
      {
         double[][] data = new double[xbins][ybins];
         double[] array = th2d.getArray();
         for (int i=0; i<xbins; i++)
            for (int j=0; j<ybins; j++)
               data[i][j] = array[1+i+(j+1)*(xbins+2)];
         double[] darray = rootHisto.getSumw2();
         if (darray != null && darray.length > 0)
         {
            double[][] error = new double[xbins][ybins];
            for (int i=0; i<xbins; i++)
               for (int j=0; j<ybins; j++)
                  error[i][j] = darray[1+j+(i+1)*(ybins+2)];
            return new double[][][]
            { data, error };
         }
         else
         {
            return new double[][][]
            { data };
         }
      }
   }
   private static class Root2FHistogramAdapter extends Root2HistogramAdapter
   {
      private TH2F th2f;
      Root2FHistogramAdapter(TH2F rootHisto)
      {
         super(rootHisto);
         this.th2f = rootHisto;
      }
      public double[][][] rebin(int xbins, double p2, double p3,
      int ybins, double p6, double p7,
      boolean p4, boolean p5, boolean p8)
      {
         double[][] data = new double[xbins][ybins];
         float[] array = th2f.getArray();
         for (int i=0; i<xbins; i++)
            for (int j=0; j<ybins; j++)
               data[i][j] = array[1+i+(j+1)*(xbins+2)];
         double[] darray = rootHisto.getSumw2();
         if (darray != null && darray.length > 0)
         {
            double[][] error = new double[xbins][ybins];
            for (int i=0; i<xbins; i++)
               for (int j=0; j<ybins; j++)
                  error[i][j] = darray[1+j+(i+1)*(ybins+2)];
            return new double[][][]
            { data, error };
         }
         else
         {
            return new double[][][]
            { data };
         }
      }
   }
   private static abstract class Root2HistogramAdapter implements Rebinnable2DHistogramData, HasStatistics, Statistics
   {
      protected TH2 rootHisto;
      
      Root2HistogramAdapter(TH2 rootHisto)
      {
         this.rootHisto = rootHisto;
      }
      public double getXMin()
      {
         return rootHisto.getXaxis().getXmin();
      }
      public double getXMax()
      {
         return rootHisto.getXaxis().getXmax();
      }
      public int getXBins()
      {
         return rootHisto.getXaxis().getNbins();
      }
      public double getYMin()
      {
         return rootHisto.getYaxis().getXmin();
      }
      public double getYMax()
      {
         return rootHisto.getYaxis().getXmax();
      }
      public int getYBins()
      {
         return rootHisto.getYaxis().getNbins();
      }
      public boolean isRebinnable()
      {
         return false;
      }
      public int getXAxisType()
      {
         return DOUBLE;
      }
      public String[] getXAxisLabels()
      {
         return null;
      }
      public int getYAxisType()
      {
         return DOUBLE;
      }
      public String[] getYAxisLabels()
      {
         return null;
      }
      public String getTitle()
      {
         return rootHisto.getTitle();
      }
      public Statistics getStatistics()
      {
         return this;
      }
      public String[] getStatisticNames()
      {
         return new String[]
         {"Entries"};
      }
      public double getStatistic(String p1)
      {
         return rootHisto.getEntries();
      }
   }
}
