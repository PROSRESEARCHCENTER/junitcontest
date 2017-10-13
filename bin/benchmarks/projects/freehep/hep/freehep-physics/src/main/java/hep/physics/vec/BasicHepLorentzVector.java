package hep.physics.vec; 

import java.io.Serializable;

/**
 * BasicImplementation of a HepLorentzVector (4-vector)
 * @author Gary Bower (grb@slac.stanford.edu)
 * @version $Id: BasicHepLorentzVector.java 9146 2006-10-16 19:22:42Z tonyj $
 */

public class BasicHepLorentzVector implements HepLorentzVector, Serializable
{
   static final long serialVersionUID = -6544699016896436061L;
   private double t;
   private Hep3Vector v;
   private boolean vIsOwned = false;
   //
   public BasicHepLorentzVector()
   {
      this.t = 0.;
      this.v = new BasicHep3Vector();
      vIsOwned = true;
   }
   public BasicHepLorentzVector(double t, double x, double y, double z)
   {
      this.t = t;
      this.v = new BasicHep3Vector(x, y, z);
      vIsOwned = true;
   }
   public BasicHepLorentzVector(double t, double[] x)
   {
      this.t = t;
      this.v = new BasicHep3Vector(x);
      vIsOwned = true;
   }
   public BasicHepLorentzVector(double t, float[] x)
   {
      this.t = t;
      this.v = new BasicHep3Vector(x);
      vIsOwned = true;
   }
   public BasicHepLorentzVector(double t, Hep3Vector v)
   {
      this.t = t;
      this.v = v;
      vIsOwned = false;
   }
   public void setV3(double t, double x, double y, double z)
   {
      this.t = t;
      if (vIsOwned) ((BasicHep3Vector) v).setV(x, y, z);
      else
      {
         this.v = new BasicHep3Vector(x,y,z);
         vIsOwned = true;
      }
   }
   public void setV3(double t, Hep3Vector v)
   {
      this.t = t;
      this.v = v;
      vIsOwned = false;
   }
   public void setT(double t)
   {
      this.t = t;
   }
   public double t()
   {
      return t;
   }
   public Hep3Vector v3()
   {
      return v;
   }
   public double magnitude()
   {
      return Math.sqrt(VecOp.dot(this,this));
   }
   public double magnitudeSquared()
   {
      return VecOp.dot(this,this);
   }

   public boolean equals(Object obj)
   {
      if (obj instanceof HepLorentzVector)
      {
         HepLorentzVector that = (HepLorentzVector) obj;
         return v.equals(that.v3()) && that.t()==t;
      }
      else return false;
   }

   public int hashCode()
   {
      return v.hashCode() + (int) Double.doubleToRawLongBits(t);
   }

   public String toString()
   {
      return VecOp.toString(this);
   }
}
