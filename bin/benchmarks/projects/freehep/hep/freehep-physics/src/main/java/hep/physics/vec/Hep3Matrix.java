package hep.physics.vec;

import hep.physics.matrix.Matrix;

/**
 * Hep 3x3 matrices
 * @see VecOp
 * @author Gary Bower
 * @version $Id: Hep3Matrix.java 9201 2006-10-23 17:42:09Z tonyj $
 */

public interface Hep3Matrix extends Matrix
{
   /**
    * Returns the determinent of the matrix
    */
   double det();
   /**
    * return trace
    */
   double trace(); 
   /**
    * transpose the matrix
    */
   
}
