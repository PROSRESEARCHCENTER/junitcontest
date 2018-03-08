package org.freehep.j3d;

// Java3D
import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.vecmath.Point3d;
import com.sun.j3d.utils.geometry.GeometryInfo;

/** <code>PolyConeSegment</code>
  * uses several <code>ConeSegment</code>s to get all <code>Shape3D</code>.
  * All angles are in degrees, all dimensions are full dimensions (not 
  * half dimensions).
  * <img src="doc-files/PolyConeSegment.gif">
  * @see ConeSegment  
  * @version 2.1.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class PolyConeSegment extends Solid {

  /** Create general polycone segment.
    * @param rmins       array of radiuses of inner surface 
    * @param rmaxs       array of radiuses of outer surface 
    * @param zs          array of possitions of z-planes
    * @param phimin      starting azimutal angle [deg] 
    * @param phimax      ending   azimutal angle [deg]
    * @param granularity number of segments of curves approximations
    * @param appearance  object' Appearance 
    * @preconditions phimin > 0 && phimin < 360
    * @preconditions phimax > 0 && phimax < 360
    * @preconditions phimin < phimax
    * @preconditions rmins.length >= zs.length
    * @preconditions rmaxs.length >= zs.length
    * @preconditions zs.length > 0 
    * @preconditions for (int i = 0; i < zs.length; i++) rmins[i] < rmaxs[i]
    * @preconditions granularity > 1 */
  public PolyConeSegment(double[] rmins,
                         double[] rmaxs,
                         double[] zs,
                         double phimin,
                         double phimax,
                         int granularity,
                         Appearance appearance) {

    ConeSegment oneCons;
    for (int i = 0; i < zs.length - 1; i++) {
      oneCons = new ConeSegment(rmins[i    ],
                                rmins[i + 1],
                                rmaxs[i    ],
                                rmaxs[i + 1],
                                zs   [i + 1] - zs[i],
                                phimin,
                                phimax,
                                granularity,
                                appearance);

      // Geometries
      addCoordinates(getCoordinates(oneCons.inGeometry(),  (zs[i] + zs[i + 1]) / 2));
      addCoordinates(getCoordinates(oneCons.outGeometry(), (zs[i] + zs[i + 1]) / 2));
      if (phimin > 0 || phimax < 360) {
        addCoordinates(getCoordinates(oneCons.leftGeometry(),  (zs[i] + zs[i + 1]) / 2));
        addCoordinates(getCoordinates(oneCons.rightGeometry(), (zs[i] + zs[i + 1]) / 2));
        }
      if (i == zs.length - 2) {
        addCoordinates(getCoordinates(oneCons.topGeometry(), (zs[i] + zs[i + 1]) / 2));
        }
      if (i == 0) {
        addCoordinates(getCoordinates(oneCons.bottomGeometry(), (zs[i] + zs[i + 1]) / 2));
        }
      }
    
    // Appearance
    setAppearance(appearance);  
      
    }

  private Point3d[] getCoordinates(GeometryInfo gInfo, double z) {
    GeometryArray gArray = gInfo.getGeometryArray();
    final int start  = gArray.getInitialVertexIndex();
    final int length = gArray.getValidVertexCount();
    Point3d[] points = new Point3d[length];    
    for (int j = start; j < length; j++) {
      points[j] = new Point3d();
      }
    gArray.getCoordinates(start, points);
    for (int j = start; j < length; j++) {
      points[j].z = points[j].z + z;
      }
    return points; 
    } 
            
  }



