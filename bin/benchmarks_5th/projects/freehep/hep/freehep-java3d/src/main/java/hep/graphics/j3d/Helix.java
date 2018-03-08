package hep.graphics.j3d;

// Java
import java.util.ArrayList;
import java.util.Iterator;

// Java3D
import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.GeometryArray;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/** Helix segment in solenoidal magnetic field along  z-axis.
  * Parameters are defined in "vertex representation".
  * <img src="doc-files/Helix.gif">
  * @version 3.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
// TBD: provide perigee parametrisation
// TBD: use PolyLine
public class Helix extends Shape3D {

  /** Create helix.
    * @param charge      particles' charge
    * @param eta         track' polar angle [deg]
    * @param phi         track' azimutal angle [deg]
    * @param pt          track' pt [GeV]
    * @param v_phi       vertex' polar angle [deg]
    * @param v_rho       vertex' radial coordinate
    * @param v_z         vertex' z coordinate
    * @param mField      magnetic field [T]
    * @param granularity number of segments of curves approximations
    * @param appearance  object' Appearance 
    * @preconditions charge >= -1 && charge <= 1
    * @preconditions phi > 0 && phi < 360
    * @preconditions pt > 0
    * @preconditions v_phi > 0 && v_phi < 360
    * @preconditions mField > 0
    * @preconditions  granularity > 1 */
  public Helix(int charge,
               double eta,
               double phi,
               double pt,
               double v_phi,
               double v_rho,
               double v_z,
               double mField,
               int granularity,
               Appearance appearance) {
    if (charge == 0) {
      constructNeutral(eta,
                       pt,
                       v_phi,
                       v_rho,
                       v_z,
                       appearance);
      }
    else {
      constructCharged(charge,
                       eta,
                       phi,
                       pt,
                       v_phi,
                       v_rho,
                       v_z,
                       mField,
                       granularity,
                       appearance);
      }
    }
    
  private void constructNeutral(double eta,
                                double phi,
                                double v_phi,
                                double v_rho,
                                double v_z,
                                Appearance appearance) {
                             
    Point3d start = new Point3d(v_rho * Math.cos(v_phi),
                                v_rho * Math.sin(v_phi),
                                v_z);  
    Point3d end0   = new Point3d(start.x,
                                 start.y,
                                 start.z);
                                
    final double tant = 2 / (Math.exp(eta) - Math.exp(-eta));
    if (tant > _length / _radius) {
      end0.add(new Point3d(_radius * Math.cos(phi),
                           _radius * Math.sin(phi),
                           _radius * tant));
      }
    else {
      end0.add(new Point3d(_length * Math.cos(phi) / tant,
                           _length * Math.sin(phi) / tant,
                           _length));
      }                                     
    
    int[] counts = new int[1];
    counts[0] = 2;
    LineStripArray lineArray = new LineStripArray(2, 
                                                  GeometryArray.COORDINATES|
                                                  GeometryArray.NORMALS,
                                                  counts);
    float[] normal = {0, 0, 0};
    lineArray.setCoordinate(0, start);
    lineArray.setCoordinate(1, end0);
    lineArray.setNormal(0, normal);
    
    setGeometry(lineArray);        
     _end = new Vector3d(end0);
    setAppearance(appearance);
    
    }                  
                                 
  private void constructCharged(int charge,
                                double eta,
                                double phi,
                                double pt,
                                double v_phi,
                                double v_rho,
                                double v_z,
                                double mField,
                                int granularity,
                                Appearance appearance) {

    final double rx = v_rho * Math.cos(v_phi);
    final double ry = v_rho * Math.sin(v_phi);
    final double rz = v_z;
    
    final double px = pt * Math.cos(phi);
    final double py = pt * Math.sin(phi);
    final double pz = pt / 2 * (Math.exp(eta) - Math.exp(-eta));
    
    final double sigma = pt / mField / 0.3;
    final double alpha = sigma * pz / pt;
    
    final double sx = rx - py * sigma * charge;
    final double sy = ry + px * sigma * charge;
    final double st = Math.sqrt(sx * sx + sy *sy);
    
    final double dx = (1 - sigma * pt / st) * sx;
    final double dy = (1 - sigma * pt / st) * sy;
    
    final double sin_dphi_2 = Math.sqrt((rx - dx) * (rx - dx) + (ry - dy) * (ry - dy)) / 2 / sigma;
    final double cos_dphi_2 = Math.sqrt(1 - sin_dphi_2 * sin_dphi_2);
    double dphi = 2 * Math.atan2(sin_dphi_2, cos_dphi_2);
    if ((rx - dx) * px + (ry - dy) * py > 0) {
      dphi = - dphi;
      }
      
    final double phi0 = Math.atan2(ry - sy, rx - sx);
    
    final double delta = 2 * Math.PI / granularity / pt; // TBD: do it better
    
    double x = 0;
    double y = 0;
    double z = 0;
    
    int i = 0;
    double w = 0;
    
    ArrayList points = new ArrayList();
    
    if (Math.sqrt(rx*rx + ry*ry) < _radius && 
        Math.abs(rz)             < _length / 2f) {
      do {
        x = rx + sigma * (Math.cos(phi0) - Math.cos(- charge * delta * w + phi0));                            
        y = ry + sigma * (Math.sin(phi0) - Math.sin(- charge * delta * w + phi0));
        z = rz + alpha * delta * w; 
        points.add(new Point3d(x, y, z));
        i += 1;
        w += 1;
        } while (Math.sqrt(x*x + y*y) < _radius && 
                 Math.abs(z)          < _length / 2f &&
                 i                    < granularity); 
      }
    
    if (i > 0) {
      int[] counts = new int[1];
      counts[0] = points.size();
      LineStripArray lineArray = new LineStripArray(points.size(), 
                                                    GeometryArray.COORDINATES|
                                                    GeometryArray.NORMALS,
                                                    counts);
      float[] normal = {0, 0, 0};
      int j = 0;
      Iterator it = points.iterator();
      while (it.hasNext()) {
        lineArray.setCoordinate(j++, (Point3d)(it.next()));
        }
      lineArray.setNormal(0, normal);
      setGeometry(lineArray);        
      _end = new Vector3d(x, y, z); 
      }                  
    setAppearance(appearance);
      
    }    

  /** End point of the helix. */
  public Vector3d end() {
    return _end;
    }
  
  private Vector3d _end;
  
  /** Set maximal end point of the helix. 
    * The default is 30,12. */
  public static void cutAt(double length, double radius) {
    _length = length;
    _radius = radius;
    }

  private static double _length = 30; // half-length 
  
  private static double _radius = 12;   
    
  }
