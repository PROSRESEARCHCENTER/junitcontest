package hep.physics.vec;

import java.io.Serializable;

/**
 * Basic implementation of a Hep3Vector
 * @author Gary Bower (grb@slac.stanford.edu)
 * @version $Id: BasicHep3Vector.java 9146 2006-10-16 19:22:42Z tonyj $
 */

public class BasicHep3Vector implements Hep3Vector, Serializable
{
   static final long serialVersionUID = -52454965658870098L;
   private double x;
   private double y;
   private double z;
   //
   public BasicHep3Vector()
   {
      x = 0.;
      y = 0.;
      z = 0.;
   }
   public BasicHep3Vector(double dx, double dy, double dz)
   {
      x = dx;
      y = dy;
      z = dz;
   }
   /**
    * Create a BasicHep3Vector from a double array
    * @param d An array {x,y,z}
    */
   public BasicHep3Vector(double[] d)
   {
      if (d.length != 3) throw new IllegalArgumentException("Illegal array length");
      x = d[0];
      y = d[1];
      z = d[2];
   }
   public BasicHep3Vector(float[] f)
   {
      if (f.length != 3) throw new IllegalArgumentException("Illegal array length");
      x = f[0];
      y = f[1];
      z = f[2];
   }
   public void setV(double dx, double dy, double dz)
   {
      x = dx;
      y = dy;
      z = dz;
   }

   public double x()
   {
      return x;
   }
   public double y()
   {
      return y;
   }
   public double z()
   {
      return z;
   }
   public double magnitude()
   {
      return Math.sqrt(x*x + y*y + z*z);
   } 
   public double magnitudeSquared()
   {
      return x*x + y*y + z*z;
   }
   public double[] v()
   {
      return new double[] { x, y, z };
   } 

   public boolean equals(Object obj)
   {
      if (obj instanceof Hep3Vector)
      {
         Hep3Vector that = (Hep3Vector) obj;
         return x == that.x() && y == that.y() && z == that.z();
      }
      else return false;
   }

   public String toString()
   {
      return VecOp.toString(this);
   }

   public int hashCode()
   {
      return (int) (Double.doubleToLongBits(x) +
                    Double.doubleToLongBits(y) +
                    Double.doubleToLongBits(z));
   }
}
