package hep.aida.ref.root;

import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IManagedObject;
import hep.aida.IProfile;
import hep.aida.IProfile1D;
import hep.aida.ITuple;
import hep.aida.ref.ManagedObject;
import hep.aida.ref.histogram.FixedAxis;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.histogram.Histogram2D;
import hep.aida.ref.histogram.Profile1D;
import hep.io.root.RootClassNotFound;
import hep.io.root.interfaces.TAxis;
import hep.io.root.interfaces.TH1;
import hep.io.root.interfaces.TH1D;
import hep.io.root.interfaces.TH1F;
import hep.io.root.interfaces.TH2;
import hep.io.root.interfaces.TH2D;
import hep.io.root.interfaces.TH2F;
import hep.io.root.interfaces.TKey;
import hep.io.root.interfaces.TProfile;
import hep.io.root.interfaces.TTree;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Adapts Root histograms and Trees to their corresponding AIDA interfaces
 * @author tonyj
 * @version $Id
 */
class Converter
{
   static IManagedObject convert(TKey key, String name, boolean useProxies) throws RootClassNotFound, IOException
   {
      IManagedObject result = useProxies ? proxyConvert(key, name) : internalConvert(key, name);
      if (result == null)
      {
         System.err.println("Warning: Ignored " + name + " of type " + key.getObjectClass());
      }
      return result;
   }
   private static Histogram1D convert(TH1 h1, String name)
   {
      Histogram1D hist = new Histogram1D();
      hist.setName(name);

      TAxis axis = h1.getXaxis();
      
      String xAxisLabel = axis.getTitle();
      String yAxisLabel = h1.getYaxis().getTitle();
      
      hist.annotation().addItem("xaxislabel",xAxisLabel,true);
      hist.annotation().addItem("yaxislabel",yAxisLabel,true);
            
      int nBins = axis.getNbins();
      double[] values;
      if (h1 instanceof TH1F)
      {
         TH1F th1f = (TH1F) h1;
         float[] array = th1f.getArray();
         values = new double[nBins + 2];
         for (int i = 0; i < (nBins + 2); i++)
            values[i] = array[i];
      }
      else if (h1 instanceof TH1D)
      {
         TH1D th1d = (TH1D) h1;
         values = th1d.getArray();
      }
      else
         return null;

      //TODO: Deal with variable bins
      IAxis xAxis = new FixedAxis(nBins, axis.getXmin(), axis.getXmax());
      hist.initHistogram1D(xAxis, null);

      double[] errors = h1.getSumw2();
      if ((errors == null) || (errors.length == 0))
      {
         errors = new double[nBins + 2];
         for (int i = 0; i < (nBins + 2); i++)
            errors[i] = Math.sqrt(values[i]);
      }
      else
      {
         for (int i = 0; i < (nBins + 2); i++)
            errors[i] = Math.sqrt(errors[i]);
      }

      double sumw = h1.getTsumw();
      double sumwx = h1.getTsumwx();
      double sumwx2 = h1.getTsumwx2();
      double meanx = sumwx / sumw;
      double rmsx  = Math.sqrt((sumwx2 / sumw) - ((sumwx * sumwx) / sumw / sumw));
      hist.setContents(values, errors, null, null, null);
      hist.setNEntries((int) h1.getEntries());
      hist.setValidEntries((int) h1.getEntries()-hist.extraEntries());
      hist.setMeanAndRms(meanx, rmsx);
      hist.setTitle(h1.getTitle());
      return hist;
   }
   private static Histogram2D convert(TH2 h2, String name)
   {
      Histogram2D hist = new Histogram2D();
      hist.setName(name);
      TAxis xTAxis = h2.getXaxis();
      int nXbins = xTAxis.getNbins();
      TAxis yTAxis = h2.getYaxis();
      int nYbins = yTAxis.getNbins();
      IAxis xAxis = new FixedAxis(nXbins, xTAxis.getXmin(), xTAxis.getXmax());
      IAxis yAxis = new FixedAxis(nYbins, yTAxis.getXmin(), yTAxis.getXmax());
      hist.initHistogram2D( xAxis, yAxis, null);
      
      String xAxisLabel = h2.getXaxis().getTitle();
      String yAxisLabel = h2.getYaxis().getTitle();
      
      hist.annotation().addItem("xaxislabel",xAxisLabel,true);
      hist.annotation().addItem("yaxislabel",yAxisLabel,true);
            
      double[][] values;
      if (h2 instanceof TH2F)
      {
         TH2F th2f = (TH2F) h2;
         float[] array = th2f.getArray();
         values = new double[nXbins+2][nYbins+2];
         int k = 0;
         for (int j=0; j < (nYbins + 2); j++)
            for (int i = 0; i < (nXbins + 2); i++)
            {
                  values[i][j] = array[k++];
            }
      }
      else if (h2 instanceof TH2D)
      {
         TH2D th2d = (TH2D) h2;
         double[] array = th2d.getArray();
         values = new double[nXbins+2][nYbins+2];
         int k = 0;
         for (int j=0; j < (nYbins + 2); j++)
            for (int i = 0; i < (nXbins + 2); i++)
            {
                  values[i][j] = array[k++];
            }
      }
      else return null;
      double[] errors = h2.getSumw2();
      double[][] errors2d;
      if ((errors == null) || (errors.length == 0))
      {
         errors2d = new double[nXbins+2][nYbins+2];
         for (int i = 0; i < (nXbins + 2); i++)
            for (int j=0; j < (nYbins + 2); j++)
            {
               errors2d[i][j] = Math.sqrt(values[i][j]);
            }
      }
      else
      {
         errors2d = new double[nXbins+2][nYbins+2];
         int k = 0;

         for (int j=0; j < (nYbins + 2); j++)
            for (int i = 0; i < (nXbins + 2); i++)
            {
               errors2d[i][j] = errors[k++];
            }
      } 
      
      double sumw = h2.getTsumw();
      double sumwx = h2.getTsumwx();
      double sumwx2 = h2.getTsumwx2();
      double sumwy = h2.getTsumwy();
      double sumwy2 = h2.getTsumwy2();
      
      double meanx = sumwx / sumw;
      double meany = sumwy / sumw;
      double rmsx  = Math.sqrt((sumwx2 / sumw) - ((sumwx * sumwx) / sumw / sumw));
      double rmsy  = Math.sqrt((sumwy2 / sumw) - ((sumwy * sumwy) / sumw / sumw));

      hist.setContents(values,errors2d,null,null,null,null,null);
      hist.setNEntries((int) h2.getEntries());
      hist.setValidEntries((int) h2.getEntries()- hist.extraEntries());
      hist.setMeanX(meanx);
      hist.setRmsX(rmsx);
      hist.setMeanY(meany);
      hist.setRmsY(rmsy);
      
      hist.setTitle(h2.getTitle());
      return hist;
   }
   private static Profile1D convert(TProfile h1, String name)
   {
      Profile1D profile = new Profile1D();
      profile.setName(name);
      TAxis axis = h1.getXaxis();

      String xAxisLabel = axis.getTitle();
      String yAxisLabel = h1.getYaxis().getTitle();
      
      profile.annotation().addItem("xaxislabel",xAxisLabel,true);
      profile.annotation().addItem("yaxislabel",yAxisLabel,true);
      
      
      int nBins = axis.getNbins();
      double[] heights = h1.getArray();
      double[] entriesD = h1.getBinEntries();
      int[] entries = new int[entriesD.length];
      for (int i=0; i<entriesD.length; i++) entries[i] = (int) entriesD[i];

      //TODO: Deal with variable bins
      IAxis xAxis = new FixedAxis(nBins, axis.getXmin(), axis.getXmax());
      profile.initProfile1D(xAxis);

      double[] errors = h1.getSumw2();
      if ((errors == null) || (errors.length == 0))
      {
         errors = new double[nBins + 2];
         for (int i = 0; i < (nBins + 2); i++)
            errors[i] = Math.sqrt(heights[i]);
      }
      else
      {
         for (int i = 0; i < (nBins + 2); i++)
            errors[i] = Math.sqrt(errors[i]);
      }

      double sumw = h1.getTsumw();
      double sumwx = h1.getTsumwx();
      double sumwx2 = h1.getTsumwx2();
      double meanx = sumwx / sumw;
      double rmsx  = Math.sqrt((sumwx2 / sumw) - ((sumwx * sumwx) / sumw / sumw));
      profile.setContents(heights, errors, entries, null, null);
      profile.setNEntries((int) h1.getEntries());
      profile.setValidEntries((int) h1.getEntries()-profile.extraEntries());
      profile.setMean(meanx);
      profile.setRms(rmsx);
      profile.setTitle(h1.getTitle());
      return profile;
   }

   private static IManagedObject internalConvert(TKey key, String name) throws RootClassNotFound, IOException
   {
      Class keyClass = key.getObjectClass().getJavaClass();
      
      if      (TProfile.class.isAssignableFrom(keyClass)) return convert((TProfile) key.getObject(), name);
      else if (TH2.class.isAssignableFrom(keyClass))      return convert((TH2) key.getObject(), name);
      else if (TH1.class.isAssignableFrom(keyClass))      return convert((TH1) key.getObject(), name);
      else if (TTree.class.isAssignableFrom(keyClass))    return new TTreeTuple(key, name);
      else return null;
   }
   private static IManagedObject proxyConvert(TKey key, String name) throws RootClassNotFound, IOException
   {
      Class keyClass = key.getObjectClass().getJavaClass();
      
      if      (TProfile.class.isAssignableFrom(keyClass)) return createProxy(key,IProfile1D.class);
      else if (TH2.class.isAssignableFrom(keyClass))      return createProxy(key,IHistogram2D.class);
      else if (TH1.class.isAssignableFrom(keyClass))      return createProxy(key,IHistogram1D.class);
      else if (TTree.class.isAssignableFrom(keyClass))    return createProxy(key,ITuple.class);
      else return null;
   }
   private static IManagedObject createProxy(TKey key, Class proxyClass)
   {
      Class[] interfaces = { IManagedObject.class, proxyClass };
      InvocationHandler handler = new MyInvocationHandler(key);
      return (IManagedObject) Proxy.newProxyInstance(Converter.class.getClassLoader(), interfaces, handler);
   }
   private static class MyInvocationHandler implements InvocationHandler
   {
      private TKey key;
      private IManagedObject backend;
      MyInvocationHandler(TKey key)
      {
         this.key = key;
      }
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
      {
         String methodName = method.getName();
         int nArgs = args == null ? 0 : args.length;
         if (nArgs ==0)
         {
            if ("name"    .equals(methodName)) return key.getName();
            if ("hashCode".equals(methodName)) return new Integer(key.hashCode());
            if ("toString".equals(methodName)) return key.getName();
            if ("type"    .equals(methodName)) return ManagedObject.typeForClass(proxy.getClass());
         }
         else if (nArgs == 1)
         {
            if ("equals"  .equals(methodName)) return Boolean.valueOf(proxy == args[0]); 
         }
   
         if (backend == null)
         {
//          System.out.println("Conversion of "+key.getName()+" caused by call to "+methodName);
            backend = internalConvert(key,key.getName());
         }
         return method.invoke(backend,args);
      }
   }
}
