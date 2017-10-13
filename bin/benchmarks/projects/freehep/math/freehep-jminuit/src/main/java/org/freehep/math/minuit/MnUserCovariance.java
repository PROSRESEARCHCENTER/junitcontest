package org.freehep.math.minuit;

/**
 * MnUserCovariance is the external covariance matrix designed for the interaction of the
 * user. The result of the minimization (internal covariance matrix) is converted into
 * the user representable format. It can also be used as input prior to the minimization.
 * The size of the covariance matrix is according to the number of variable parameters
 * (free and limited).
 * @version $Id: MnUserCovariance.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnUserCovariance
{

   private MnUserCovariance(MnUserCovariance other)
   {
      theData = other.theData.clone();
      theNRow = other.theNRow;
   }
   MnUserCovariance()
   {
      theData = new double[0];
      theNRow = 0;
   }
   /*
    * covariance matrix is stored in upper triangular packed storage format,
    * e.g. the elements in the array are arranged like
    * {a(0,0), a(0,1), a(1,1), a(0,2), a(1,2), a(2,2), ...},
    * the size is nrow*(nrow+1)/2.
    */   
   MnUserCovariance(double[] data, int nrow)
   {
      if (data.length != nrow*(nrow+1)/2) throw new IllegalArgumentException("Inconsistent arguments");
      theData = data;
      theNRow = nrow;
   }
   protected MnUserCovariance clone()
   {
      return new MnUserCovariance(this);
   }
   
   
   public MnUserCovariance(int nrow)
   {
      theData = new double[nrow*(nrow+1)/2];
      theNRow = nrow;
   }
   public double get(int row, int col)
   {
      if (row >= theNRow || col >= theNRow) throw new IllegalArgumentException();
      if(row > col)
         return theData[col+row*(row+1)/2];
      else
         return theData[row+col*(col+1)/2];
   }
   public void set(int row, int col, double value)
   {
      if (row >= theNRow || col >= theNRow) throw new IllegalArgumentException();
      if(row > col)
         theData[col+row*(row+1)/2] = value;
      else
         theData[row+col*(col+1)/2] = value;
   }
   
   void scale(double f)
   {
      for (int i = 0; i < theData.length; i++) theData[i] *= f;
   }
   
   double[] data()
   {
      return theData;
   }
   
   public int nrow()
   {
      return theNRow;
   }
   public int ncol()
   {
      return theNRow;
   }
   int size()
   {
      return theData.length;
   }
   public String toString()
   {
      return MnPrint.toString(this);
   }
   private double[] theData;
   private int theNRow;
}
