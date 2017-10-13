package org.freehep.math.minuit;

/**
 * Utilities for operating on vectors and matrices
 * @version $Id: MnUtils.java 8584 2006-08-10 23:06:37Z duns $
 */
abstract class MnUtils
{
   static double similarity(MnAlgebraicVector avec, MnAlgebraicSymMatrix mat)
   {
      int n = avec.size();
      MnAlgebraicVector tmp = mul(mat,avec);
      double result = 0;
      for (int i=0; i<n; i++)
      {
         result += tmp.get(i) * avec.get(i);
      }
      return result;
   }
   static MnAlgebraicVector add(MnAlgebraicVector v1, MnAlgebraicVector v2)
   {
      if (v1.size() != v2.size()) throw new IllegalArgumentException("Incompatible vectors");
      MnAlgebraicVector result = v1.clone();
      double[] a = result.data();
      double[] b = v2.data();
      for (int i=0; i<a.length; i++) a[i] += b[i];
      return result;
   }
   static MnAlgebraicSymMatrix add(MnAlgebraicSymMatrix m1, MnAlgebraicSymMatrix m2)
   {
      if (m1.size() != m2.size()) throw new IllegalArgumentException("Incompatible matrices");
      MnAlgebraicSymMatrix result = m1.clone();
      double[] a = result.data();
      double[] b = m2.data();
      for (int i=0; i<a.length; i++) a[i] += b[i];
      return result;
   }
   static MnAlgebraicVector sub(MnAlgebraicVector v1, MnAlgebraicVector v2)
   {
      if (v1.size() != v2.size()) throw new IllegalArgumentException("Incompatible vectors");
      MnAlgebraicVector result = v1.clone();
      double[] a = result.data();
      double[] b = v2.data();
      for (int i=0; i<a.length; i++) a[i] -= b[i];
      return result;
   }
   static MnAlgebraicSymMatrix sub(MnAlgebraicSymMatrix m1, MnAlgebraicSymMatrix m2)
   {
      if (m1.size() != m2.size()) throw new IllegalArgumentException("Incompatible matrices");
      MnAlgebraicSymMatrix result = m1.clone();
      double[] a = result.data();
      double[] b = m2.data();
      for (int i=0; i<a.length; i++) a[i] -= b[i];
      return result;
   }
   static MnAlgebraicVector mul(MnAlgebraicVector v1, double scale)
   {
      MnAlgebraicVector result = v1.clone();
      double[] a = result.data();
      for (int i=0; i<a.length; i++) a[i] *= scale;
      return result;
   }
   static MnAlgebraicSymMatrix mul(MnAlgebraicSymMatrix m1, double scale)
   {
      MnAlgebraicSymMatrix result = m1.clone();
      double[] a = result.data();
      for (int i=0; i<a.length; i++) a[i] *= scale;
      return result;
   }
   static MnAlgebraicVector mul(MnAlgebraicSymMatrix m1, MnAlgebraicVector v1)
   {
      if (m1.nrow() != v1.size()) throw new IllegalArgumentException("Incompatible arguments");
      MnAlgebraicVector result = new MnAlgebraicVector(m1.nrow());
      double[] a = result.data();
      for (int i=0; i<a.length; i++)
      {
         double total = 0;
         for (int k=0; k<a.length; k++)
         {
            total += m1.get(i,k) * v1.get(k);
         }
         a[i] = total;
      }
      return result;
   }
   static MnAlgebraicSymMatrix mul(MnAlgebraicSymMatrix m1, MnAlgebraicSymMatrix m2)
   {
      if (m1.size() != m2.size()) throw new IllegalArgumentException("Incompatible matrices");
      int n = m1.nrow();
      MnAlgebraicSymMatrix result = new MnAlgebraicSymMatrix(n);
      for (int i=0; i<n; i++)
      {
         for (int j=0; j<=i; j++)
         {
            double total = 0;
            for (int k=0; k<n; k++)
            {
               total += m1.get(i,k)*m2.get(k,j);
            }
            result.set(i,j,total);
         }
      }
      return result;
   }
   static double innerProduct(MnAlgebraicVector v1, MnAlgebraicVector v2)
   {
      if (v1.size() != v2.size()) throw new IllegalArgumentException("Incompatible vectors");
      double[] a = v1.data();
      double[] b = v2.data();
      double total = 0;
      for (int i=0; i<a.length; i++)
      {
         total += a[i]*b[i];
      }
      return total;
   }
   static MnAlgebraicSymMatrix div(MnAlgebraicSymMatrix m, double scale)
   {
      return mul(m,1/scale);
   }
   static MnAlgebraicVector div(MnAlgebraicVector m, double scale)
   {
      return mul(m,1/scale);
   }
   static MnAlgebraicSymMatrix outerProduct(MnAlgebraicVector v2)
   {
      // Fixme: check this. I am assuming this is just an outer-product of vector
      //        with itself.
      int n = v2.size();
      MnAlgebraicSymMatrix result = new MnAlgebraicSymMatrix(n);
      double[] data = v2.data();
      for (int i=0;i<n;i++)
      {
         for (int j=0;j<=i; j++)
         {
            result.set(i,j, data[i]*data[j]);
         }
      }
      return result;
   }
   static double absoluteSumOfElements(MnAlgebraicSymMatrix m)
   {
      double[] data = m.data();
      double result = 0;
      for (int i=0; i<data.length; i++) result += Math.abs(data[i]);
      return result;
   }
}
