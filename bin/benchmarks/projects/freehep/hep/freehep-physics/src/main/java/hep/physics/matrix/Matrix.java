package hep.physics.matrix;

/**
 * A very simple matrix interface
 * @see MatrixOp
 * @author tonyj
 * @version $Id: Matrix.java 9136 2006-10-13 19:09:06Z tonyj $
 */
public interface Matrix
{
   /**
    * Returns the number of rows
    */
   int getNRows();
   /**
    * Returns the number of columns
    */
   int getNColumns();
   /**
    * Returns the value of the given element
    */
   double e(int row, int column);
}
