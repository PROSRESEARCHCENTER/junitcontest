package hep.physics.vec;

/**
 * Interface to be implemented by 4 Vectors. This interface
 * is deliberately kept simple to minimize the work needed to
 * implement it. Operations for operating on vectors can be found
 * in class VecOp.
 * @see BasicHepLorentzVector
 * @see VecOp
 * @author Gary Bower (grb@slac.stanford.edu)
 * @version $Id: HepLorentzVector.java 9130 2006-10-13 00:02:39Z tonyj $
 */

public interface HepLorentzVector
{
   double t();
   Hep3Vector v3();
   /**
    * The magnitude of the Lorentz vector.
    */
   double magnitude();
   /**
    * The square of the magnitude of the vector
    */
   double magnitudeSquared();
}