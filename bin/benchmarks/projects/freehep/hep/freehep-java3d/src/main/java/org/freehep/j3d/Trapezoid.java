package org.freehep.j3d;

// Java3D
import javax.media.j3d.Appearance;
import javax.vecmath.Point3d;

/** General Trapezoid. All angles are in degrees, all dimensions
  * are full dimensions (not half dimensions).
  * <img src="doc-files/Trapezoid.gif">
  * @version 2.1.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class Trapezoid extends Solid {

  // Constructors --------------------------------------------------------------

  /** Create general trapezoid. 
    * @param xmu        length of x-edge at -z/2 and +ym/2
    * @param xmd        length of x-edge at -z/2 and -ym/2
    * @param xpu        length of x-edge at +z/2 and +ym/2
    * @param xpd        length of x-edge at +z/2 and -ym/2
    * @param ym         length of y-edge at -z/2
    * @param yp         length of y-edge at +z/2
    * @param z          length of z-edge
    * @param inclXZ     angle [deg] of trapezoid z-axis with the real z-axis in XZ plane
    * @param inclYZ     angle [deg] of trapezoid z-axis with the real z-axis in YZ plane
    * @param declYm     angle [deg] of xz-plane at -z/2 with y-axis  
    * @param declYp     angle [deg] of xz-plane at +z/2 with y-axis
    * @param appearance object' Appearance 
    * @preconditions inclXZ < 90 && inclXZ > -90
    * @preconditions inclYZ < 90 && inclYZ > -90 
    * @preconditions declYm < 90 && declYm > -90 
    * @preconditions declYp < 90 && declYp > -90 */
  public Trapezoid(double xmu,
                   double xmd,
                   double xpu,
                   double xpd,
                   double ym,
                   double yp,
                   double z,
                   double inclXZ,
                   double inclYZ,
                   double declYm,
                   double declYp,
                   Appearance appearance) {
    construct(xmu,
              xmd,
              xpu,
              xpd,
              ym,
              yp,
              z,
              inclXZ,
              inclYZ,
              declYm,
              declYp,
              appearance);
    }        

  /** Create trapezoid without declinations. */
  public Trapezoid(double xm,
                   double xp,
                   double ym,
                   double yp,
                   double z,
                   double inclXZ,
                   double inclYZ,
                   Appearance appearance) {
    construct(xm,
              xm,
              xp,
              xp,
              ym,
              yp,
              z,
              inclXZ,
              inclYZ,
              0,
              0,
              appearance);
    }        

  /** Create straigth trapezoid. */
  public Trapezoid(double xm,
                   double xp,
                   double ym,
                   double yp,
                   double z,
                   Appearance appearance) {
    construct(xm,
              xm,
              xp,
              xp,
              ym,
              yp,
              z,
              0,
              0,
              0,
              0,
              appearance);
    }        

  /** Create box. */
  public Trapezoid(double x,
                   double y,
                   double z,
                   Appearance appearance) {
    construct(x,
              x,
              x,
              x,
              y,
              y,
              z,
              0,
              0,
              0,
              0,
              appearance);
    }        

  // ---------------------------------------------------------------------------

  private void construct(double xmu,
                         double xmd,
                         double xpu,
                         double xpd,
                         double ym,
                         double yp,
                         double z,
                         double inclXZ,
                         double inclYZ,
                         double declYm,
                         double declYp,
                         Appearance appearance) {
             
    final double delX  = z  / 2 * Math.tan(Math.toRadians(inclXZ));
    final double delY  = z  / 2 * Math.tan(Math.toRadians(inclYZ));
    final double delZm = ym / 2 * Math.tan(Math.toRadians(declYm));
    final double delZp = yp / 2 * Math.tan(Math.toRadians(declYp));         

    // Construct array
    
    Point3d t00 = new Point3d(- xmu/2 - delX + delZm,   ym/2 - delY, - z/2);
    Point3d t01 = new Point3d(  xmu/2 - delX + delZm,   ym/2 - delY, - z/2);
    Point3d t10 = new Point3d(  xmd/2 - delX - delZm, - ym/2 - delY, - z/2);
    Point3d t11 = new Point3d(- xmd/2 - delX - delZm, - ym/2 - delY, - z/2);
    Point3d b00 = new Point3d(- xpu/2 + delX + delZp,   yp/2 + delY,   z/2);
    Point3d b01 = new Point3d(  xpu/2 + delX + delZp,   yp/2 + delY,   z/2);
    Point3d b10 = new Point3d(  xpd/2 + delX - delZp, - yp/2 + delY,   z/2);
    Point3d b11 = new Point3d(- xpd/2 + delX - delZp, - yp/2 + delY,   z/2);   
    Point3d[] coordinates = {t00, b00, b01, t01,
                             t01, b01, b10, t10,
                             t10, b10, b11, t11,
                             t11, b11, b00, t00,
                             t00, t01, t10, t11,
                             b11, b10, b01, b00};
        
    // Geometry    
    addCoordinates(coordinates);

    // Appearance
    setAppearance(appearance);

    }

  }



