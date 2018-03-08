package org.freehep.math.minuit;

/**
 *
 * @version $Id: MnAlgebraicVector.java 8584 2006-08-10 23:06:37Z duns $
 */
class MnAlgebraicVector
{
   MnAlgebraicVector(int size)
   {
      data = new double[size];
   }
   public MnAlgebraicVector clone()
   {
      MnAlgebraicVector result = new MnAlgebraicVector(data.length);
      System.arraycopy(data,0,result.data,0,data.length);
      return result;
   }
   int size()
   {
      return data.length;
   }
   double[] data()
   {
      return data;
   }
   double get(int i)
   {
      return data[i];
   }
   void set(int i, double value)
   {
      data[i] = value;
   }
   double[] asArray()
   {
      return data;
   }
   public String toString()
   {
      return MnPrint.toString(this);
   }
   private double[] data;
}
