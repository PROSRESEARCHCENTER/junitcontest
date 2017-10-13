package org.freehep.j3d;

// Java3D
import javax.media.j3d.Appearance;
import javax.vecmath.Point3d;
import com.sun.j3d.utils.geometry.GeometryInfo;

/** General Cone Segment. All angles are in degrees, all dimensions
  * are full dimensions (not half dimensions).
  * <img src="doc-files/ConeSegment.gif">
  * @version 3.2.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class ConeSegment extends Solid {

  // Constructors --------------------------------------------------------------

  /** Create general cone segment.
    * @param rminm       inner radius at -z/2 
    * @param rminp       inner radius at +z/2 
    * @param rmaxm       outer radius at -z/2 
    * @param rmaxp       outer radius at +z/2 
    * @param l           length
    * @param phimin      starting azimutal angle [deg] 
    * @param phimax      ending   azimutal angle [deg]
    * @param granularity number of segments of curves approximations
    * @param appearance  object' Appearance 
    * @preconditions rminm < rmaxm
    * @preconditions rminp < rmaxp
    * @preconditions phimin > 0 && phimin < 360
    * @preconditions phimax > 0 && phimax < 360
    * @preconditions phimin < phimax
    * @preconditions granularity > 1 */
  public ConeSegment(double rminm,
                     double rminp,
                     double rmaxm,
                     double rmaxp,
                     double l,
                     double phimin,
                     double phimax,
                     int granularity,
                     Appearance appearance) {
    construct(rminm,
              rminp,
              rmaxm,
              rmaxp,
              l,
              phimin,
              phimax,
              granularity,
              appearance);
    }

  /** Create tube segment. */
  public ConeSegment(double rmin,
                     double rmax,
                     double l,
                     double phimin,
                     double phimax,
                     int granularity,
                     Appearance appearance) {
    construct(rmin,
              rmin,
              rmax,
              rmax,
              l,
              phimin,
              phimax,
              granularity,
              appearance);
    }
              
  /** Create tube. */
  public ConeSegment(double rmin,
                     double rmax,
                     double l,
                     int granularity,
                     Appearance appearance) {
    construct(rmin,
              rmin,
              rmax,
              rmax,
              l,
              0,
              360,
              granularity,
              appearance);
    }
              
  /** Create cylinder. */
  public ConeSegment(double r,
                     double l,
                     int granularity,
                     Appearance appearance) {
    construct(0,
              0,
              r,
              r,
              l,
              0,
              360,
              granularity,
              appearance);
    }
              
  // ---------------------------------------------------------------------------

  private void construct(double rminm,
                         double rminp,
              		       double rmaxm,
                         double rmaxp,
                         double l,
                         double phimin,
                         double phimax,
                         int granularity,
                         Appearance appearance) {

    final int count = 4 * granularity;

    final double phimin0 = Math.toRadians(phimin);
    final double phimax0 = Math.toRadians(phimax);
                                                                    
    Point3d pminp = new Point3d();
    Point3d pmaxp = new Point3d();
    Point3d pminm = new Point3d();
    Point3d pmaxm = new Point3d(); 
      
    Point3d[] topCoordinates    = new Point3d[count];      
    Point3d[] bottomCoordinates = new Point3d[count];
    Point3d[] inCoordinates     = new Point3d[count];
    Point3d[] outCoordinates    = new Point3d[count];     
    Point3d[] leftCoordinates   = new Point3d[4];      
    Point3d[] rightCoordinates  = new Point3d[4];

    // Loop                                                                                                          
    double phi = phimin0;
    for (int i = -2; i < count; i += 4) {
		  pminp.set(rminp * Math.cos(phi),
						 	  rminp * Math.sin(phi),
                  l / 2);
			pmaxp.set(rmaxp * Math.cos(phi),
							  rmaxp * Math.sin(phi),
                  l / 2);
			pminm.set(rminm * Math.cos(phi),
						 	  rminm * Math.sin(phi),
                - l /2);
			pmaxm.set(rmaxm * Math.cos(phi),
							  rmaxm * Math.sin(phi),
                - l /2);
  		// Top
      if (i > 0) {
        topCoordinates   [i    ] = new Point3d(pmaxp);
        topCoordinates   [i + 1] = new Point3d(pminp);
        }
      if (i < count - 4) {
        topCoordinates   [i + 2] = new Point3d(pminp);
        topCoordinates   [i + 3] = new Point3d(pmaxp);
        }
			// Bottom
      if (i > 0) {
        bottomCoordinates[i + 1] = new Point3d(pmaxm);
        bottomCoordinates[i    ] = new Point3d(pminm);
        }
      if (i < count - 4) {
        bottomCoordinates[i + 3] = new Point3d(pminm);
        bottomCoordinates[i + 2] = new Point3d(pmaxm);
        }
		  // Inner
      if (i > 0) {
        inCoordinates    [i + 1] = new Point3d(pminm);
        inCoordinates    [i    ] = new Point3d(pminp);
        }
      if (i < count - 4) {
        inCoordinates    [i + 3] = new Point3d(pminp);
        inCoordinates    [i + 2] = new Point3d(pminm);
        }
			// Outer
      if (i > 0) {
        outCoordinates   [i    ] = new Point3d(pmaxm);
        outCoordinates   [i + 1] = new Point3d(pmaxp);
        }
      if (i < count - 4) {
        outCoordinates   [i + 2] = new Point3d(pmaxp);
        outCoordinates   [i + 3] = new Point3d(pmaxm);
        }
			//
      phi += (phimax0 - phimin0) / granularity; 
      }	
    phi -= (phimax0 - phimin0) / granularity; 

    if (phimin > 0 || phimax < 360) {
      // Left
      leftCoordinates   [0] = new Point3d(rminm * Math.cos(phimin0),
		      																rminm * Math.sin(phimin0),
                                          - l / 2);
      leftCoordinates   [1] = new Point3d(rmaxm * Math.cos(phimin0),
			   					  											rmaxm * Math.sin(phimin0),
                                          - l / 2);
      leftCoordinates   [2] = new Point3d(rmaxp * Math.cos(phimin0),
		    																	rmaxp * Math.sin(phimin0),
                                            l / 2);
  	  leftCoordinates   [3] = new Point3d(rminp * Math.cos(phimin0),
		    																	rminp * Math.sin(phimin0),
                                            l / 2);
      // Right                                                                                                              
      rightCoordinates  [1] = new Point3d(rminm * Math.cos(phi),
		    												 					rminm * Math.sin(phi),
                                          - l / 2);
	    rightCoordinates  [0] = new Point3d(rmaxm * Math.cos(phi),
			    											 					rmaxm * Math.sin(phi),
                                          - l / 2);
		  rightCoordinates  [3] = new Point3d(rmaxp * Math.cos(phi),
			  	  										 					rmaxp * Math.sin(phi),
                                            l / 2);
		  rightCoordinates  [2] = new Point3d(rminp * Math.cos(phi),
			  	  										 					rminp * Math.sin(phi),
                                            l / 2);
      }

    // Geometries
    _outGeometry    = addCoordinates(outCoordinates);
    _inGeometry     = addCoordinates(inCoordinates);
    _topGeometry    = addCoordinates(topCoordinates);
    _bottomGeometry = addCoordinates(bottomCoordinates);
    if (phimin > 0 || phimax < 360) {
      _leftGeometry   = addCoordinates(leftCoordinates);
      _rightGeometry  = addCoordinates(rightCoordinates);
      }
    
    // Appearance
    setAppearance(appearance);  
          
    }
    
  GeometryInfo topGeometry() {
    return _topGeometry;
    }
        
  GeometryInfo bottomGeometry() {
    return _bottomGeometry;
    }
        
  GeometryInfo inGeometry() {
    return _inGeometry;
    }
        
  GeometryInfo outGeometry() {
    return _outGeometry;
    }
        
  GeometryInfo leftGeometry() {
    return _leftGeometry;
    }
        
  GeometryInfo rightGeometry() {
    return _rightGeometry;
    }
        
  private GeometryInfo _topGeometry;
    
  private GeometryInfo _bottomGeometry;
    
  private GeometryInfo _inGeometry;
    
  private GeometryInfo _outGeometry;
    
  private GeometryInfo _leftGeometry;
    
  private GeometryInfo _rightGeometry;

  }



