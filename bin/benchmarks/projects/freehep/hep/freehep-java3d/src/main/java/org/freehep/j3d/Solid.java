package org.freehep.j3d;

// Java3D
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/** General Solid. 
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public abstract class Solid extends Shape3D {

  /** Adds geometry from <code>GeometryInfo.QUAD_ARRAY</code>
    * coordinates. */
  protected GeometryInfo addCoordinates(Point3d[] coordinates) {
    GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
    geometryInfo.setCoordinates(coordinates);
    _normalGenerator.generateNormals(geometryInfo);
    if (_first) {
      setGeometry(geometryInfo.getGeometryArray());
      _first = false;
      }
    else {
      addGeometry(geometryInfo.getGeometryArray());
      }
    return geometryInfo;
    }
    
  private boolean _first = true;  
      
  private static NormalGenerator _normalGenerator = new NormalGenerator();
             
  }



