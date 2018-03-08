package hep.physics.matrix;

import java.io.Serializable;

/**
 * A very simple matrix implementation
 * @author tonyj
 */
public class BasicMatrix implements MutableMatrix, Serializable
{
   static final long serialVersionUID = -3491275185124557222L;
   private double[][] data;
   
   public BasicMatrix(int nRows, int nCols)
   {
      data = new double[nRows][nCols];
   }
   
   /** Creates a new instance of BasicMatrix */
   public BasicMatrix(double[][] data)
   {
      if (data.length <= 0) throw new IllegalArgumentException("Invalid data");
      int nCols = data[0].length;
      for (int i=0; i<data.length; i++)
      {
         if (data[i].length <= 0 || data[i].length != nCols) throw new IllegalArgumentException("Invalid data");
      }
      this.data = data;
   }
   public BasicMatrix(Matrix mIn)
   {
      int nRows = mIn.getNRows();
      int nCols = mIn.getNColumns();
      data = new double[nRows][nCols];
      for (int i=0; i<nRows; i++)
      {
         for (int j=0; j<nCols; j++)
         {
            data[i][j] = mIn.e(i,j);
         }
      }
   }

   public int getNRows()
   {
      return data.length;
   }

   public int getNColumns()
   {
      return data[0].length;
   }

   public double e(int row, int column)
   {
      return data[row][column];
   }
   
   public double det()
   {
      return MatrixOp.det(this);
   }
   
   public String toString()
   {
      return MatrixOp.toString(this);
   }

   public void setElement(int row, int column, double value)
   {
      data[row][column] = value;
   }

   public void invert() throws MatrixOp.IndeterminateMatrixException
   {
      MatrixOp.inverse(this,this);
   }
   
   public void transpose()
   {
      MatrixOp.transposed(this,this);
   }
}
