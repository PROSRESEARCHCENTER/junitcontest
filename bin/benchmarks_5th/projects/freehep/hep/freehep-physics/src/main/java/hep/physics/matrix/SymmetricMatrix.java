package hep.physics.matrix;

import java.io.Serializable;

/**
 * A simple implementation of a symmetric matrix. A symmmetric matrix
 * is a square matrix for which <code>e(i,j) == e(j,i)</code>.
 * @author tonyj
 * @version $Id: SymmetricMatrix.java 9205 2006-10-24 06:06:07Z tonyj $
 */
public class SymmetricMatrix implements MutableMatrix, Serializable
{
   static final long serialVersionUID = -7824887420997633477L;
   private final double[] values;
   private final int[] startIndex;
   private final int size;
   
   /** 
    * Creates a new instance of SymmetricMatrix with all elements 
    * set to zero. 
    * @param size The rank of the matrix
    */
   public SymmetricMatrix(int size)
   {
      this.size = size;
      startIndex = new int[size];
      int ii = 0;
      for (int i=0; i<size; )
      {
         startIndex[i] = ii;
         i++;
         ii += i;
      }
      values = new double[ii];
   }
   /** 
    * Construct a SymmetricMatrix by copying an existing matrix.
    * If the input matrix is a SymmetricMatrix it is copied exactly.
    * Otherwise if the input matrix is square the lower left elements are copied
    * If the matrix is not square an illegal argument exception is thrown.
    */
   public SymmetricMatrix(Matrix mIn)
   {
      this(mIn.getNRows());
      if (mIn instanceof SymmetricMatrix)
      {
         SymmetricMatrix m = (SymmetricMatrix) mIn;
         System.arraycopy(m.values,0,values,0,values.length);         
      }
      else
      {
         int nRows = mIn.getNRows();
         if (nRows != mIn.getNColumns()) throw new IllegalArgumentException("Input matrix is not square");
         for (int i=0; i<nRows; i++)
         {
            for (int j=0; j<=i; j++)
            {
               values[elementIndex(i,j)] = mIn.e(i,j);
            }
         }
      }
   }
   /** 
    * Creates a new instance of SymmetricMatrix with the 
    * given initial values.
    * @param size The rank of the matrix
    * @param initialValues The initial values for the matrix
    * @param isLower If true initial values must be m(0,0), m(1,0), m(1,1), ... otherwise m(0,0), m(0,1), m(0,2), ...
    */   
   public SymmetricMatrix(int size, double[] initialValues, boolean isLower)
   {
      this(size);
      if (values.length != initialValues.length) throw new IllegalArgumentException("initialValues have invalid length");
      copy(initialValues,values,!isLower);
   }
   /**
    * Returns the matrix as a packed array
    * @param isLower if true array is packed m(0,0), m(1,0), m(1,1),... else m(0,0), m(0,1), m(0,2), ...
    */
   public double[] asPackedArray(boolean isLower)
   {
      double[] result = new double[values.length];
      copy(values,result,!isLower);
      return result;
   }
   private void copy(double[] source, double[] dest, boolean flip)
   {
      if (flip)
      {
         int k = 0;
         for (int i=0; i<size; i++)
         {
            for (int j=i; j<size; j++)
            {
               dest[elementIndex(i,j)] = source[k++];
            }
         }
      }
      else
      {
         System.arraycopy(source,0,dest,0,dest.length);
      }
   }
   public int getNRows()
   {
      return size;
   }

   public int getNColumns()
   {
      return size;
   }

   /**
    * Returns the diagonal element for the given row/column
    */ 
   public double diagonal(int index)
   {
      return values[elementIndex(index,index)];
   }
   /**
    * Returns a specific element
    */
   public double e(int x, int y)
   {
      return values[elementIndex(x,y)];
   }
   /**
    * Set a specific element
    */
   public void setElement(int x, int y, double value)
   {
      values[elementIndex(x,y)] = value;
   }
   /**
    * Increment a specific element
    */ 
   public void incrementElement(int x, int y, double value)
   {
      values[elementIndex(x,y)] += value;
   }

   private int elementIndex(int x, int y)
   {
      return x>=y ? startIndex[x]+y : startIndex[y]+x;
   } 
 
   public double det()
   {
      return MatrixOp.det(this);
   }
   
   public String toString()
   {
      return MatrixOp.toString(this);
   }

   public void invert() throws MatrixOp.IndeterminateMatrixException
   {
      MatrixOp.inverse(this,this);
   }

   public void transpose()
   {
      // Noop for symmetric matrix
   }
}