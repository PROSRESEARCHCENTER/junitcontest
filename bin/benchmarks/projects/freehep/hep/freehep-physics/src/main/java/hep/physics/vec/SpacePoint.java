package hep.physics.vec;

import java.io.Serializable;

/**
 * A Hep3Vector representing a point in space.
 * Derived from work by Norman Graf, Jan Strube and Frank Gaede
 * @version $Id: SpacePoint.java 12630 2007-06-10 16:32:10Z tonyj $
 */

// Fixme: This class has many problems. 
// The v() method is not compatible with Hep3Vector or the magnitude method
// The equals method is not consistent with Object.equals() or hashcode()
// The not equals method is pointless
// The clone method should not trap and throw away exceptions
// There is no way to construct a space point with non-Cartesian representations
public class SpacePoint implements Serializable, Hep3Vector, Cloneable
{
   private Representation _representation;
   private double _x;
   private double _y;
   private double _z;
   private double _xy;
   private double _xyz;
   private double _phi;
   private double _theta;
   
   /**
    * Returns the space point as a double array <b>in its internal representation</b>
    * @see SpacePoint#getRepresentation()
    */
   public double[] v()
   {
      switch(_representation)
      {
         case Cartesian: return new double[] {_x, _y, _z};
         case Spherical: return new double[] {_xyz, _phi, _theta};
         case Cylindrical: return new double[] {_xy, _phi, _z};
         default: return new double[3];
      }
   }
   
   private void cartesianToCylindricalR()
   {
      _xy  = Math.sqrt(_x*_x+_y*_y);
   }
   
   private void cartesianToPhi()
   {
      _phi = Math.atan2(_y,_x);
   }
   
   private void cartesianToTheta()
   {
      if (Double.isNaN(_xy))
         cartesianToCylindricalR();
      _theta = Math.atan2(_xy,_z);
   }
   
   public double magnitude()
   {
      return _xyz;
   }
   
   public double magnitudeSquared()
   {
      return _xyz*_xyz;
   }
   
   
   /**
    * Default constructor.
    * Sets point to be the origin (0,0,0)
    */
   public SpacePoint()
   {
      _x = _y = _z = 0.0;
      _xy = _xyz = 0.0;
      _phi = _theta = 0.0;
   }
   
   /**
    * Copy constructor
    *
    * @param  spt SpacePoint to copy
    */
   public SpacePoint( SpacePoint spt )
   {
      _representation = spt._representation;
      _x = spt.x();
      _y = spt.y();
      _z = spt.z();
      _xy = spt.rxy();
      _xyz = spt.rxyz();
      _phi = spt.phi();
      _theta = spt.theta();
   }
   
   public SpacePoint(Hep3Vector vec)
   {
      _representation = Representation.Cartesian;
      _x = vec.x();
      _y = vec.y();
      _z = vec.z();
      _xyz = Math.sqrt(_x*_x + _y*_y + _z*_z);
      _xy = _phi = _theta = Double.NaN;
   }
   
   private void cylindricalToCartesianX()
   {
      _x = _xy*Math.cos(_phi);
   }
   
   private void sphericalToCartesianX()
   {
      _x = _xyz*Math.cos(_phi)*Math.sin(_theta);
   }
   
   private void sphericalToCartesianY()
   {
      _y = _xyz*Math.sin(_phi)*Math.sin(_theta);
   }
   
   private void sphericalToCartesianZ()
   {
      _z = _xyz*Math.cos(_theta);
   }
   
   /**
    * Cartesian x
    */
   public double x()
   {
      if (Double.isNaN(_x))
         switch(_representation)
         {
            case Spherical: sphericalToCartesianX(); break;
            case Cylindrical: cylindricalToCartesianX(); break;
         }
         return _x;
   }
   
   
   private void cylindricalToCartesianY()
   {
      _y = _xy*Math.sin(_phi);
   }
   
   
   /**
    * Cartesian y
    */
   public double y()
   {
      if (Double.isNaN(_y))
         switch(_representation)
         {
            case Spherical: sphericalToCartesianY(); break;
            case Cylindrical: cylindricalToCartesianY(); break;
         }
         return _y;
   }
   
   /**
    * Cartesian z
    */
   
   public double z()
   {
      if (Double.isNaN(_z))
         sphericalToCartesianZ();
      return _z;
   }
   
   private void sphericalToCylindricalR()
   {
      _xy = _xyz*Math.sin(_theta);
   }
   
   /**
    * Cylindrical r
    * @return double
    */
   public double rxy()
   {
      if (Double.isNaN(_xy))
         switch(_representation)
         {
            case Spherical: sphericalToCylindricalR(); break;
            case Cartesian: cartesianToCylindricalR(); break;
         }
         return _xy;
   }
   
   /**
    * Cylindrical phi
    * @return double
    */
   public double phi()
   {
      if (Double.isNaN(_phi))
         cartesianToPhi();
      return _phi;
   }
   
   /**
    * Spherical r
    * @return double
    */
   public double rxyz()
   {
      return _xyz;
   }
   
   /**
    * Spherical theta
    * @return double
    */
   public double theta()
   {
      if (Double.isNaN(_theta))
         switch(_representation)
         {
            case Cartesian: cartesianToTheta(); break;
            case Cylindrical: cylindricalToTheta(); break;
         }
         return _theta;
   }
   
   private void cylindricalToTheta()
   {
      _theta = Math.atan2(_xy,_z);
   }
   
   /**
    * cos(phi)
    */
   public double cosPhi()
   {
      if ( !Double.isNaN(_x) && !Double.isNaN(_xy) && _xy != 0. )
         return _x/_xy;
      if (Double.isNaN(_phi))
         cartesianToPhi();
      return Math.cos(_phi);
   }
   
   /**
    * sin(phi)
    */
   public double sinPhi()
   {
      if (!Double.isNaN(_y) && !Double.isNaN(_xy) && _xy != 0. )
         return _y/_xy;
      if (Double.isNaN(_phi))
         cartesianToPhi();
      return Math.sin(_phi);
   }
   
   /**
    * sin(theta)
    * @return double
    */
   public double sinTheta()
   {
      if ( !Double.isNaN(_xy) && _xyz != 0. )
         return _xy/_xyz;
      if (Double.isNaN(_theta))
         switch(_representation)
         {
            case Cartesian: cartesianToTheta(); break;
            case Cylindrical: cylindricalToTheta(); break;
         }
         return Math.sin(_theta);
   }
   
   /**
    * cos(theta)
    * @return double
    */
   public double cosTheta()
   {
      if ( !Double.isNaN(_z) && _xyz != 0. )
         return _z/_xyz;
      if (Double.isNaN(_theta))
         switch(_representation)
         {
            case Cartesian: cartesianToTheta(); break;
            case Cylindrical: cylindricalToTheta(); break;
         }
         return Math.cos(_theta);
   }
   
   /**
    * Output Stream
    *
    * @return  String representation of object
    */
   public String toString()
   {
      return  _representation + " SpacePoint: " + "\n" +
              "    x: " + x()     + "\n" +
              "    y: " + y()     + "\n" +
              "    z: " + z()     + "\n" +
              "  rxy: " + rxy()   + "\n" +
              " rxyz: " + rxyz()  + "\n" +
              "  phi: " + phi()   + "\n" +
              "theta: " + theta() + "\n" ;
   }
   
   
   /**
    * Tests for equality within errors
    * @param spt a SpacePoint to compare against
    * @param precision the precision of the comparison
    * @return true if each of the components is within precision
    * of the components of spt
    */
   public boolean equals(SpacePoint spt, double precision)
   {
      return ( Math.abs(x() - spt.x()) < precision ) &&
             ( Math.abs(y() - spt.y()) < precision ) &&
             ( Math.abs(z() - spt.z()) < precision );
   }
   
   /**
    * Tests for equality within errors
    * @param spt a Hep3Vector to compare against
    * @param precision the precision of the comparison
    * @return true if each of the components is within precision
    * of the components of spt
    */
   public boolean equals(Hep3Vector spt, double precision)
   {
      return  ( Math.abs(x() - spt.x()) < precision ) &&
              ( Math.abs(y() - spt.y()) < precision ) &&
              ( Math.abs(z() - spt.z()) < precision );
   }
   
   /**
    * Tests for equality
    * @param   x SpacePoint to compare
    * @return  true if objects are equal
    */
   public boolean equals(SpacePoint x)
   {
      return equals(x, 1e-14);
   }
   
   /**
    *Inequality
    *
    * @param   spt  SpacePoint to compare
    * @return  true if objects are <em> not </em> equal
    */
   public boolean notEquals(SpacePoint spt)
   {
      return ! (equals(spt));
   }
   
   /**
    * Return the distance between two space points.
    * @param spt1 SpacePoint 1
    * @param spt2 SpacePoint 2
    * @return Euclidean distance between points
    */
   public static double distance(SpacePoint spt1, SpacePoint spt2)
   {
      double dx = spt2.x() - spt1.x();
      double dy = spt2.y() - spt1.y();
      double dz = spt2.z() - spt1.z();
      return Math.sqrt( dx*dx + dy*dy + dz*dz );
   }
   
   /**
    *Clone
    *
    * @return  a copy of this object
    */
   public Object clone()
   {
      Object o = null;
      try
      {
         o = super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         e.printStackTrace();
      }
      return o;
   }
   
   /**
    * @return array of doubles, cartesian representation
    */
   public double[] getCartesianArray()
   {
      return new double[] {_x, _y, _z};
   }
   
   /**
    * @return the representations of the object
    */
   public Representation getRepresentation()
   {
      return _representation;
   }
   
   public enum Representation
   {
      Cartesian, Cylindrical, Spherical
   }
}
