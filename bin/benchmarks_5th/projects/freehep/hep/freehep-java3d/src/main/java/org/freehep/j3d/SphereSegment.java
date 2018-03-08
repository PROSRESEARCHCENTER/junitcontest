package org.freehep.j3d;

// Java3D
import javax.media.j3d.Appearance;
import javax.vecmath.Point3d;

/** General Sphere Segment. All angles are in degrees.
  * <img src="doc-files/SphereSegment.gif">
  * @version 1.1.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class SphereSegment extends Solid {

  // Constructors --------------------------------------------------------------

  /** Create general sphere segment. 
    * @param rmin        inner radius
    * @param rmax        outer radius
    * @param phimin      starting azimutal angle [deg]
    * @param phimax      ending   azimutal angle [deg]
    * @param thetamin    starting polar    angle [deg]
    * @param thetamax    ending   polar    angle [deg]
    * @param granularity number of segments of curves approximations
    * @param appearance  object' Appearance
    * @preconditions phimin > 0 && phimin < 360
    * @preconditions phimax > 0 && phimax < 360
    * @preconditions phimin < phimax
    * @preconditions thetamin > 0 && thetamin < 360
    * @preconditions thetamax > 0 && thetamax < 360
    * @preconditions thetamin < thetamax 
    * @preconditions rmin < rmax 
    * @preconditions granularity > 1 */
  public SphereSegment(double rmin,
                       double rmax,
                       double phimin,
                       double phimax,
                       double thetamin,
                       double thetamax,
                       int granularity,
                       Appearance appearance) {
    construct(rmin,
              rmax,
              phimin,
              phimax,
              thetamin,
              thetamax,
              granularity,
              appearance);
    }
              
  /** Create full sphere. */
  public SphereSegment(double r,
                       int granularity,
                       Appearance appearance) {
    construct(r,
              0,
              0,
              360,
              -90,
              90,
              granularity,
              appearance);
    }
              
  // ---------------------------------------------------------------------------

  private void construct(double rmin,
              		       double rmax,
                         double phimin,
                         double phimax,
                         double thetamin,
                         double thetamax,
                         int granularity,
                         Appearance appearance) {

    final int count1 = 4 * granularity;
    final int count2 = 4 * granularity * granularity;

    final double phimin0 = Math.toRadians(phimin);
    final double phimax0 = Math.toRadians(phimax);
                                                                    
    final double thetamin0 = Math.toRadians(thetamin);
    final double thetamax0 = Math.toRadians(thetamax);
                                                                    
    Point3d[][] pmin = new Point3d[granularity + 1][granularity + 1];
    Point3d[][] pmax = new Point3d[granularity + 1][granularity + 1];
      
    Point3d[] outCoordinates    = new Point3d[count2];      
    Point3d[] inCoordinates     = new Point3d[count2];      
    Point3d[] topCoordinates    = new Point3d[count1];      
    Point3d[] bottomCoordinates = new Point3d[count1];      
    Point3d[] leftCoordinates   = new Point3d[count1];      
    Point3d[] rightCoordinates  = new Point3d[count1];      

    // Prepare points
    double phi = phimin0;
    double theta;
    for (int i = 0; i < granularity + 1; i++) {
      theta = thetamin0;
      for (int j = 0; j < granularity + 1; j++) {
		    pmin[i][j] = new Point3d(rmin * Math.cos(theta) * Math.cos(phi),
				  		 	                 rmin * Math.cos(theta) * Math.sin(phi),
                                 rmin * Math.sin(theta));
		    pmax[i][j] = new Point3d(rmax * Math.cos(theta) * Math.cos(phi),
				  		 	                 rmax * Math.cos(theta) * Math.sin(phi),
                                 rmax * Math.sin(theta));
        theta += (thetamax0 - thetamin0) / granularity; 
        }
      phi += (phimax0 - phimin0) / granularity; 
      }
      
    // Construct quatratic arrays  
    int k = 0;
    for (int i = 0; i < granularity; i++) {
      for (int j = 0; j < granularity; j++) {
        // Out
        outCoordinates [k    ] = new Point3d(pmax[i    ][j    ]);
        outCoordinates [k + 1] = new Point3d(pmax[i + 1][j    ]);
        outCoordinates [k + 2] = new Point3d(pmax[i + 1][j + 1]);
        outCoordinates [k + 3] = new Point3d(pmax[i    ][j + 1]);
        // In
        if (rmin > 0) {
          inCoordinates  [k + 3] = new Point3d(pmin[i    ][j    ]);
          inCoordinates  [k + 2] = new Point3d(pmin[i + 1][j    ]);
          inCoordinates  [k + 1] = new Point3d(pmin[i + 1][j + 1]);
          inCoordinates  [k    ] = new Point3d(pmin[i    ][j + 1]);
          }
        //
        k += 4;
        }
      }

    // Construct linear arrays
    k = 0;
    for (int i = 0; i < granularity; i++) {
      // Top
      if (thetamax < 90) {
        topCoordinates    [k + 3] = new Point3d(pmax[i    ][0]);
        topCoordinates    [k + 2] = new Point3d(pmax[i + 1][0]);
        topCoordinates    [k + 1] = new Point3d(pmin[i + 1][0]);
        topCoordinates    [k    ] = new Point3d(pmin[i    ][0]);
        }
      // Bottom
      if (thetamin > -90) {
        bottomCoordinates [k    ] = new Point3d(pmax[i    ][granularity]);
        bottomCoordinates [k + 1] = new Point3d(pmax[i + 1][granularity]);
        bottomCoordinates [k + 2] = new Point3d(pmin[i + 1][granularity]);
        bottomCoordinates [k + 3] = new Point3d(pmin[i    ][granularity]);
        }
      if (phimin > 0 || phimax < 360) {
        // Left
        leftCoordinates   [k    ] = new Point3d(pmax[0][i    ]);
        leftCoordinates   [k + 1] = new Point3d(pmax[0][i + 1]);
        leftCoordinates   [k + 2] = new Point3d(pmin[0][i + 1]);
        leftCoordinates   [k + 3] = new Point3d(pmin[0][i    ]);
        // Right
        rightCoordinates  [k + 3] = new Point3d(pmax[granularity][i    ]);
        rightCoordinates  [k + 2] = new Point3d(pmax[granularity][i + 1]);
        rightCoordinates  [k + 1] = new Point3d(pmin[granularity][i + 1]);
        rightCoordinates  [k    ] = new Point3d(pmin[granularity][i    ]);
        }
      //
      k += 4;
      }

    // Geometries
    addCoordinates(outCoordinates);
    if (rmin > 0) {
      addCoordinates(inCoordinates);
      }
    if (thetamax < 90) {
      addCoordinates(topCoordinates);
      }
    if (thetamin > -90) {
      addCoordinates(bottomCoordinates);
      }
    if (phimin > 0 || phimax < 360) {
      addCoordinates(leftCoordinates);
      addCoordinates(rightCoordinates);
      }

    // Appearance
    setAppearance(appearance);  
          
    }
    
  }



