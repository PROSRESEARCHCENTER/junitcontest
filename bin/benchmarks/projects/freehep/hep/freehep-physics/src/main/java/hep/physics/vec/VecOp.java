package hep.physics.vec;

import hep.physics.matrix.Matrix;
import hep.physics.matrix.MatrixOp;
import java.io.IOException;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

/**
 * Utility methods for dealing with 3 and 4 vectors.
 * @version $Id: VecOp.java 10570 2007-03-08 17:42:05Z tonyj $
 */
public class VecOp
{
   private VecOp()
   {
   }
   public static Hep3Vector add(Hep3Vector v, Hep3Vector w)
   {
      return (new BasicHep3Vector(v.x() + w.x(), v.y() + w.y(), v.z() + w.z()));
   }
   public static Hep3Vector sub(Hep3Vector v, Hep3Vector w)
   {
      return (new BasicHep3Vector(v.x() - w.x(), v.y() - w.y(), v.z() - w.z()));
   }
   public static Hep3Vector mult(double scalar, Hep3Vector v)
   {
      return (new BasicHep3Vector(scalar*v.x(), scalar*v.y(), scalar*v.z()));
   }
   public static Hep3Vector mult(Hep3Matrix m, Hep3Vector v)
   {
      double w1 = v.x()*m.e(0,0) + v.y()*m.e(0,1) + v.z()*m.e(0,2);
      double w2 = v.x()*m.e(1,0) + v.y()*m.e(1,1) + v.z()*m.e(1,2);
      double w3 = v.x()*m.e(2,0) + v.y()*m.e(2,1) + v.z()*m.e(2,2);
      return (new BasicHep3Vector(w1, w2, w3));
   }
   public static Hep3Matrix mult(Hep3Matrix m1, Hep3Matrix m2)
   {
      double e0 = m1.e(0,0) * m2.e(0,0) + m1.e(0,1) * m2.e(1,0) + m1.e(0,2) * m2.e(2,0);
      double e1 = m1.e(0,0) * m2.e(0,1) + m1.e(0,1) * m2.e(1,1) + m1.e(0,2) * m2.e(2,1);
      double e2 = m1.e(0,0) * m2.e(0,2) + m1.e(0,1) * m2.e(1,2) + m1.e(0,2) * m2.e(2,2);

      double e3 = m1.e(1,0) * m2.e(0,0) + m1.e(1,1) * m2.e(1,0) + m1.e(1,2) * m2.e(2,0);
      double e4 = m1.e(1,0) * m2.e(0,1) + m1.e(1,1) * m2.e(1,1) + m1.e(1,2) * m2.e(2,1);
      double e5 = m1.e(1,0) * m2.e(0,2) + m1.e(1,1) * m2.e(1,2) + m1.e(1,2) * m2.e(2,2);

      double e6 = m1.e(2,0) * m2.e(0,0) + m1.e(2,1) * m2.e(1,0) + m1.e(2,2) * m2.e(2,0);
      double e7 = m1.e(2,0) * m2.e(0,1) + m1.e(2,1) * m2.e(1,1) + m1.e(2,2) * m2.e(2,1);
      double e8 = m1.e(2,0) * m2.e(0,2) + m1.e(2,1) * m2.e(1,2) + m1.e(2,2) * m2.e(2,2);

      return new BasicHep3Matrix(e0,e1,e2,e3,e4,e5,e6,e7,e8);
   }
   public static Hep3Matrix mult(double scalar, Hep3Matrix m)
   {
      double e0 = m.e(0,0) * scalar;
      double e1 = m.e(0,0) * scalar;
      double e2 = m.e(0,0) * scalar;
      
      double e3 = m.e(0,1) * scalar;
      double e4 = m.e(0,1) * scalar;
      double e5 = m.e(0,1) * scalar;
      
      double e6 = m.e(0,2) * scalar;
      double e7 = m.e(0,2) * scalar;
      double e8 = m.e(0,2) * scalar;
      
      return new BasicHep3Matrix(e0,e1,e2,e3,e4,e5,e6,e7,e8);
   }
   public static Hep3Matrix inverse(Hep3Matrix m) throws MatrixOp.IndeterminateMatrixException
   {
      BasicHep3Matrix result = new BasicHep3Matrix();
      MatrixOp.inverse(m,result);
      return result;
   }
   static Hep3Matrix transposed(Hep3Matrix m)
   {
      BasicHep3Matrix result =  new BasicHep3Matrix();
      MatrixOp.transposed(m,result);
      return result;
   }
   public static Hep3Vector neg(Hep3Vector v)
   {
      return new BasicHep3Vector(-v.x(), -v.y(), -v.z());
   }
   public static double dot(Hep3Vector v, Hep3Vector w)
   {
      return v.x()*w.x() + v.y()*w.y() + v.z()*w.z();
   }
   // ww, 07/31/00: cross product added
   public static Hep3Vector cross(Hep3Vector v, Hep3Vector w)
   {
      double u1 = v.y()*w.z() - v.z()*w.y();
      double u2 = v.z()*w.x() - v.x()*w.z();
      double u3 = v.x()*w.y() - v.y()*w.x();
      return new BasicHep3Vector(u1, u2, u3);
   }
   // ww, 08/01/00: unit vector added
   /**
    * returns (0,0,0) vector if input vector has length 0
    */
   public static Hep3Vector unit(Hep3Vector v)
   {
      double mag = v.magnitude();
      if ( mag != 0 )
         return mult(1./mag,v);
      else
         return (new BasicHep3Vector(0., 0., 0.));
   }
   
   public static HepLorentzVector add(HepLorentzVector v, HepLorentzVector w)
   {
      return (new BasicHepLorentzVector(v.t() + w.t(), add(v.v3(),w.v3())));
   }
   public static HepLorentzVector sub(HepLorentzVector v, HepLorentzVector w)
   {
      return (new BasicHepLorentzVector(v.t() - w.t(), sub(v.v3(),w.v3())));
   }
   public static HepLorentzVector mult( double scalar, HepLorentzVector v)
   {
      return (new BasicHepLorentzVector(scalar*v.t(), mult(scalar, v.v3())));
   }
   public static HepLorentzVector neg(HepLorentzVector v)
   {
      return (new BasicHepLorentzVector(-v.t(), neg(v.v3())));
   }
   public static double dot(HepLorentzVector v, HepLorentzVector w)
   {
      return v.t()*w.t() - dot(v.v3(),w.v3());
   }
   public static Hep3Vector centerOfMass(List vecSet)
   {
      boolean empty = true;
      boolean threeVecSet = false;
      boolean fourVecSet = false;
      Hep3Vector cmVec = new BasicHep3Vector();
      for (Iterator i = vecSet.iterator();  i.hasNext();)
      {
         if (empty == true)
         {
            empty = false;
            Object e = i.next();
            if ( e instanceof Hep3Vector )
            {
               threeVecSet = true;
               cmVec = (Hep3Vector) e;
            }
            else if ( e instanceof HepLorentzVector )
            {
               fourVecSet = true;
               HepLorentzVector v = (HepLorentzVector) e;
               cmVec = v.v3();
            }
            else
            {
               throw new RuntimeException("Element is not a 3- or 4-vector");
            }
            continue;
            
         }
         if ( fourVecSet )
         {
            try
            {
               HepLorentzVector v = (HepLorentzVector) i.next();
               cmVec = add(cmVec, v.v3());
            }
            catch ( ClassCastException ex )
            {
               throw new RuntimeException(
                  "Element of 4Vec enumeration is not a 4Vec.");
            }
         }
         else if ( threeVecSet )
         {
            try
            {
               cmVec = add(cmVec, (Hep3Vector) i.next());
            }
            catch ( ClassCastException ex )
            {
               throw new RuntimeException(
                  "Element of 3Vec enumeration is not a 3Vec object.");
            }
         }
      }
      if ( empty == false )
      {
         return cmVec;
      }
      else
      {
         throw new RuntimeException("CM:vector set is empty.");
      }
   }
   /**
    * Boost fourVector with boostVector.
    *
    * Note, that beta=abs(boostVector) needs to be 0 < beta < 1.
    */
   public static HepLorentzVector boost(HepLorentzVector fourVector, Hep3Vector boostVector)
   {
      double beta = boostVector.magnitude();
      
      if ( beta >= 1.0 )
         throw new RuntimeException("Boost beta >= 1.0 !");
      
      double gamma = 1./Math.sqrt(1.-beta*beta);
      
      double     t = fourVector.t();
      Hep3Vector v = fourVector.v3();
      
      double     tp = gamma*(t-dot(boostVector,v));
      Hep3Vector vp = add(v,add(mult((gamma-1.)/(beta*beta)*dot(boostVector,v),boostVector),mult(-gamma*t,boostVector)));
      
      return new BasicHepLorentzVector(tp,vp);
   }
   
   /**
    * Boost fourVector into system of refFourVector.
    */
   public static HepLorentzVector boost(HepLorentzVector fourVector, HepLorentzVector refFourVector)
   {
      Hep3Vector refVector = refFourVector.v3();
      Hep3Vector boostVector = new BasicHep3Vector(refVector.x(),refVector.y(),refVector.z());
      boostVector = mult(1./refFourVector.t(),boostVector);
      return boost(fourVector, boostVector);
   }
   
   public static double cosTheta(Hep3Vector vector)
   {
      return vector.z()/vector.magnitude();
   }
   public static double phi(Hep3Vector vector)
   {
      return Math.atan2(vector.y(),vector.x());
   }
   public static String toString(Hep3Vector v)
   {
      Formatter formatter = new Formatter();
      formatter.format("[%12.5g,%12.5g,%12.5g]",v.x(),v.y(),v.z());
      return formatter.out().toString();
   }
   public static String toString(HepLorentzVector v)
   {
      Formatter formatter = new Formatter();
      formatter.format("[%12.5g,%12.5g,%12.5g,%12.5g]",v.v3().x(),v.v3().y(),v.v3().z(),v.t());
      return formatter.out().toString();
   }
   public static String toString(Hep3Matrix m)
   {
      return MatrixOp.toString(m);
   }
}