package hep.physics.vec;

/**
 * Interface to be implemented by 3 Vectors. This interface
 * is deliberately kept simple to minimize the work needed to
 * implement it. Operations for operating on vectors can be found
 * in class VecOp.
 * @see VecOp
 * @see BasicHep3Vector
 * @author Gary Bower (grb@slac.stanford.edu)
 * @version $Id: Hep3Vector.java 9130 2006-10-13 00:02:39Z tonyj $
 */

public interface Hep3Vector
{
   double x();
   double y();
   double z();
   /**
    * The length of the 3-vector.
    */
   double magnitude();
   /**
    * The square of the length
    */
   double magnitudeSquared();
   /*
    * Get as double array
    */
   double[] v();
}
