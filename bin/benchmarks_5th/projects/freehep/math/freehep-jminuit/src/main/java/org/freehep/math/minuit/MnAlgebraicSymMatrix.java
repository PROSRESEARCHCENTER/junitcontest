package org.freehep.math.minuit;

/**
 *
 * @version $Id: MnAlgebraicSymMatrix.java 8584 2006-08-10 23:06:37Z duns $
 */
class MnAlgebraicSymMatrix
{
   MnAlgebraicSymMatrix(int n)
   {
      if (n < 0) throw new IllegalArgumentException("Invalid matrix size: "+n);
      theSize = n*(n+1)/2;
      theNRow = n;
      theData  = new double[theSize];
   }
   void invert() throws MatrixInversionException
   {
      if (theSize == 1)
      {
         double tmp = theData[0];
         if (tmp <= 0.) throw new MatrixInversionException();
         theData[0] = 1./tmp;
      }
      else
      {
         int nrow = theNRow;
         double[] s = new double[nrow];
         double[] q = new double[nrow];
         double[] pp = new double[nrow];
         
         for(int i = 0; i < nrow; i++)
         {
            double si =  theData[theIndex(i,i)];
            if (si < 0.) throw new MatrixInversionException();
            s[i] = 1./Math.sqrt(si);
         }
         
         for( int i = 0; i < nrow; i++)
            for( int j = i; j < nrow; j++)
               theData[theIndex(i,j)] *= s[i]*s[j];
         
         for( int i = 0; i < nrow; i++)
         {
            int k = i;
            if(theData[theIndex(k,k)] == 0.) throw new MatrixInversionException();
            q[k] = 1./theData[theIndex(k,k)];
            pp[k] = 1.;
            theData[theIndex(k,k)] = 0.;
            int kp1 = k + 1;
            if(k != 0)
            {
               for(int j = 0; j < k; j++)
               {
                  int index = theIndex(j,k);
                  pp[j] = theData[index];
                  q[j] = theData[index]*q[k];
                  theData[index] = 0.;
               }
            }
            if (k != nrow-1)
            {
               for(int j = kp1; j < nrow; j++)
               {
                  int index = theIndex(k,j);
                  pp[j] = theData[index];
                  q[j] = -theData[index]*q[k];
                  theData[index] = 0.;
               }
            }
            for( int j = 0; j < nrow; j++)
               for(k = j; k < nrow; k++)
                  theData[theIndex(j,k)] += pp[j]*q[k];
         }
         
         for( int j = 0; j < nrow; j++)
            for( int k = j; k < nrow; k++)
               theData[theIndex(j,k)] *= s[j]*s[k];
         
      }
   }
   protected MnAlgebraicSymMatrix clone()
   {
      MnAlgebraicSymMatrix copy = new MnAlgebraicSymMatrix(theNRow);
      System.arraycopy(theData,0,copy.theData,0,theSize);
      return copy;
   }
   MnAlgebraicVector eigenvalues()
   {
      int nrow = theNRow;
      
      double[] tmp = new double[(nrow+1)*(nrow+1)];
      double[] work = new double[1+2*nrow];
      
      for(int i = 0; i < nrow; i++)
         for(int j = 0; j <= i; j++)
         {
            tmp[(1 + i) + (1+j)*nrow] = get(i,j);
            tmp[(1 + i)*nrow + (1+j)] = get(i,j);
         }
      
      int info = mneigen(tmp, nrow, nrow, work.length, work, 1.e-6);
      
      if(info != 0) throw new EigenvaluesException();
      
      MnAlgebraicVector result = new MnAlgebraicVector(nrow);
      for(int i = 0; i < nrow; i++) result.set(i,work[1+i]);
      
      return result;
   }
   private int theIndex(int row, int col)
   {
      if(row > col)
         return col+row*(row+1)/2;
      else
         return row+col*(col+1)/2;
      
   }
   double get(int row, int col)
   {
      if (row>=theNRow || col >= theNRow) throw new ArrayIndexOutOfBoundsException();
      return theData[theIndex(row,col)];
   }
   void set(int row, int col, double value)
   {
      if (row>=theNRow || col >= theNRow) throw new ArrayIndexOutOfBoundsException();
      theData[theIndex(row,col)] = value;
   }
   double[] data()
   {
      return theData;
   }
   
   int size()
   {
      return theSize;
   }
   
   int nrow()
   {
      return theNRow;
   }
   
   int ncol()
   {
      return nrow();
   }
   public String toString()
   {
      return MnPrint.toString(this);
   }
   private int theSize;
   private int theNRow;
   private double[] theData;
   
   private static int mneigen(double[] a, int ndima, int n, int mits, double[] work, double precis)
   {
      
      /* System generated locals */
      int a_dim1, a_offset, i__1, i__2, i__3;
      
      /* Local variables */
      double b, c__, f, h__;
      int i__, j, k, l, m = 0;
      double r__, s;
      int i0, i1, j1, m1, n1;
      double hh, gl, pr, pt;
      
      /* PRECIS is the machine precision EPSMAC */
      /* Parameter adjustments */
      a_dim1 = ndima;
      a_offset = 1 + a_dim1 * 1;
      
      /* Function Body */
      int ifault = 1;
      
      i__ = n;
      i__1 = n;
      for (i1 = 2; i1 <= i__1; ++i1)
      {
         l = i__ - 2;
         f = a[i__ + (i__ - 1) * a_dim1];
         gl = 0.;
         
         if (l >= 1)
         {
            i__2 = l;
            for (k = 1; k <= i__2; ++k)
            {
               /* Computing 2nd power */
               double r__1 = a[i__ + k * a_dim1];
               gl += r__1 * r__1;
            }
         }
         /* Computing 2nd power */
         h__ = gl + f * f;
         
         if (gl <= 1e-35)
         {
            
            work[i__] = 0.;
            work[n + i__] = f;
         }
         else
         {
            ++l;
            
            gl = Math.sqrt(h__);
            
            if (f >= 0.)
            {
               gl = -gl;
            }
            
            work[n + i__] = gl;
            h__ -= f * gl;
            a[i__ + (i__ - 1) * a_dim1] = f - gl;
            f = 0.;
            i__2 = l;
            for (j = 1; j <= i__2; ++j)
            {
               a[j + i__ * a_dim1] = a[i__ + j * a_dim1] / h__;
               gl = 0.;
               i__3 = j;
               for (k = 1; k <= i__3; ++k)
               {
                  gl += a[j + k * a_dim1] * a[i__ + k * a_dim1];
               }
               
               if (j < l)
               {
                  j1 = j + 1;
                  i__3 = l;
                  for (k = j1; k <= i__3; ++k)
                  {
                     gl += a[k + j * a_dim1] * a[i__ + k * a_dim1];
                  }
               }
               work[n + j] = gl / h__;
               f += gl * a[j + i__ * a_dim1];
            }
            hh = f / (h__ + h__);
            i__2 = l;
            for (j = 1; j <= i__2; ++j)
            {
               f = a[i__ + j * a_dim1];
               gl = work[n + j] - hh * f;
               work[n + j] = gl;
               i__3 = j;
               for (k = 1; k <= i__3; ++k)
               {
                  a[j + k * a_dim1] = a[j + k * a_dim1] - f * work[n + k] - gl
                  * a[i__ + k * a_dim1];
               }
            }
            work[i__] = h__;
         }
         --i__;
      }
      work[1] = 0.;
      work[n + 1] = 0.;
      i__1 = n;
      for (i__ = 1; i__ <= i__1; ++i__)
      {
         l = i__ - 1;
         
         if (work[i__] != 0. && l != 0)
         {
            i__3 = l;
            for (j = 1; j <= i__3; ++j)
            {
               gl = 0.;
               i__2 = l;
               for (k = 1; k <= i__2; ++k)
               {
                  gl += a[i__ + k * a_dim1] * a[k + j * a_dim1];
               }
               i__2 = l;
               for (k = 1; k <= i__2; ++k)
               {
                  a[k + j * a_dim1] -= gl * a[k + i__ * a_dim1];
               }
            }
         }
         work[i__] = a[i__ + i__ * a_dim1];
         a[i__ + i__ * a_dim1] = 1.;
         
         if (l != 0)
         {
            
            
            i__2 = l;
            for (j = 1; j <= i__2; ++j)
            {
               a[i__ + j * a_dim1] = 0.;
               a[j + i__ * a_dim1] = 0.;
            }
         }
         
      }
      
      
      n1 = n - 1;
      i__1 = n;
      for (i__ = 2; i__ <= i__1; ++i__)
      {
         i0 = n + i__ - 1;
         work[i0] = work[i0 + 1];
      }
      work[n + n] = 0.;
      b = 0.;
      f = 0.;
      i__1 = n;
      for (l = 1; l <= i__1; ++l)
      {
         j = 0;
         h__ = precis * (Math.abs(work[l]) + Math.abs(work[n + l]));
         
         if (b < h__)
         {
            b = h__;
         }
         
         i__2 = n;
         for (m1 = l; m1 <= i__2; ++m1)
         {
            m = m1;
            
            if (Math.abs(work[n + m]) <= b)
            {
               break;
            }
            
         }
         
         if (m != l)
         {
            
            for (;;)
            {
               if (j == mits)
               {
                  return ifault;
               }
               
               ++j;
               pt = (work[l + 1] - work[l]) / (work[n + l] * (double)2.);
               r__ = Math.sqrt(pt * pt + 1.);
               pr = pt + r__;
               
               if (pt < 0.)
               {
                  pr = pt - r__;
               }
               
               h__ = work[l] - work[n + l] / pr;
               i__2 = n;
               for (i__ = l; i__ <= i__2; ++i__)
               {
                  work[i__] -= h__;
               }
               f += h__;
               pt = work[m];
               c__ = 1.;
               s = 0.;
               m1 = m - 1;
               i__ = m;
               i__2 = m1;
               for (i1 = l; i1 <= i__2; ++i1)
               {
                  j = i__;
                  --i__;
                  gl = c__ * work[n + i__];
                  h__ = c__ * pt;
                  
                  if (Math.abs(pt) < Math.abs(work[n + i__]))
                  {
                     c__ = pt / work[n + i__];
                     r__ = Math.sqrt(c__ * c__ + 1.);
                     work[n + j] = s * work[n + i__] * r__;
                     s = 1. / r__;
                     c__ /= r__;
                  }
                  else
                  {
                     c__ = work[n + i__] / pt;
                     r__ = Math.sqrt(c__ * c__ + 1.);
                     work[n + j] = s * pt * r__;
                     s = c__ / r__;
                     c__ = 1. / r__;
                  }
                  pt = c__ * work[i__] - s * gl;
                  work[j] = h__ + s * (c__ * gl + s * work[i__]);
                  i__3 = n;
                  for (k = 1; k <= i__3; ++k)
                  {
                     h__ = a[k + j * a_dim1];
                     a[k + j * a_dim1] = s * a[k + i__ * a_dim1] + c__ * h__;
                     a[k + i__ * a_dim1] = c__ * a[k + i__ * a_dim1] - s * h__;
                  }
               }
               work[n + l] = s * pt;
               work[l] = c__ * pt;
               
               if (Math.abs(work[n + l]) <= b)
               {
                  break;
               }
            }
         }
         work[l] += f;
      }
      i__1 = n1;
      for (i__ = 1; i__ <= i__1; ++i__)
      {
         k = i__;
         pt = work[i__];
         i1 = i__ + 1;
         i__3 = n;
         for (j = i1; j <= i__3; ++j)
         {
            
            if (work[j] < pt)
            {
               
               k = j;
               pt = work[j];
            }
         }
         
         if (k != i__)
         {
            work[k] = work[i__];
            work[i__] = pt;
            i__3 = n;
            for (j = 1; j <= i__3; ++j)
            {
               pt = a[j + i__ * a_dim1];
               a[j + i__ * a_dim1] = a[j + k * a_dim1];
               a[j + k * a_dim1] = pt;
            }
         }
      }
      ifault = 0;
      
      return ifault;
   } /* mneig_ */
   private class EigenvaluesException extends RuntimeException {};
}
