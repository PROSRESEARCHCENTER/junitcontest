package hep.physics.matrix;

import hep.physics.matrix.MatrixOp.IndeterminateMatrixException;

/**
 * A matrix that can be changed
 * @author tonyj
 * @version $Id: MutableMatrix.java 9201 2006-10-23 17:42:09Z tonyj $
 */
public interface MutableMatrix extends Matrix
{
   /**
    * Set the given element of the matrix
    */
   void setElement( int row, int column, double value);
   /**
    * Invert this matrix (into itself)
    * @see MatrixOp#inverse(Matrix,MutableMatrix)
    */
   void invert() throws IndeterminateMatrixException;
   /**
    * Tranpose this matrix (into itself)
    * @see MatrixOp#transposed(Matrix,MutableMatrix)
    */
   void transpose();
}
