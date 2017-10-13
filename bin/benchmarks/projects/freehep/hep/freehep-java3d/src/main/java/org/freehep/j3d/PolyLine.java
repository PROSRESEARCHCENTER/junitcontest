package org.freehep.j3d;

// Java3D
import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.GeometryArray;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/** <code>Polyline</code> defined from points.
  * <img src="doc-files/PolyLine.gif">
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
// TBD: provide spline
public class PolyLine extends Shape3D {

  /** Create polyline.
    * @param points      array of <code>Point3D</code>s
    * @param appearance  object' Appearance 
    * @preconditions points.length > 1 */
  public PolyLine(Point3d[] points,
                  Appearance appearance) {
             
    if (points.length < 2) {
      return;
      }
           
    int[] counts = new int[1];
    counts[0] = points.length;
    LineStripArray lineArray = new LineStripArray(points.length, 
                                                  GeometryArray.COORDINATES|
                                                  GeometryArray.NORMALS,
                                                  counts);
    float[] normal = {0, 0, 0};
    lineArray.setCoordinates(0, points);
    lineArray.setNormal(0, normal);
    setGeometry(lineArray);        
    setAppearance(appearance);
      
    }    
    
  }
