package org.freehep.j3d;

// Java3D
import javax.media.j3d.Appearance;
import javax.vecmath.Point3d;

/** General Torus Segment. All angles are in degrees.
  * <img src="doc-files/TorusSegment.gif">
  * @version 1.1.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class TorusSegment extends Solid {

  // Constructors --------------------------------------------------------------

  /** Create general torus segment. 
    * @param rmin        radius of inner torus' circle
    * @param rmax        radous of outer torus' circle
    * @param rtor        central radius
    * @param phimin      starting azimutal angle [deg]
    * @param phimax      ending   azimutal angle [deg]
    * @param granularity number of segments of curves approximations
    * @param appearance  object' Appearance
    * @preconditions phimin > 0 && phimin < 360
    * @preconditions phimax > 0 && phimax < 360
    * @preconditions phimin < phimax
    * @preconditions rmin < rmax 
    * @preconditions rtor < rmax 
    * @preconditions granularity > 1 */
  public TorusSegment(double rmin,
                      double rmax,
                      double rtor,
                      double phimin,
                      double phimax,
                      int granularity,
                      Appearance appearance) {
    construct(rmin,
              rmax,
              rtor,
              phimin,
              phimax,
              granularity,
              appearance);
    }
              
  /** Create full torus. */
  public TorusSegment(double r,
                      double rtor,
                      int granularity,
                      Appearance appearance) {
    construct(r,
              r,
              rtor,
              0,
              360,
              granularity,
              appearance);
    }
              
  // ---------------------------------------------------------------------------

  private void construct(double rmin,
              		       double rmax,
                         double rtor,
                         double phimin,
                         double phimax,
                         int granularity,
                         Appearance appearance) {
                         
    final int count1 = 4 * granularity;
    int count2;
    if (phimin == 0 && phimax == 360) {
      count2 = 4 * granularity * granularity;
      }
    else {
      count2 = 4 * granularity * (granularity - 1);
      }

    final double phimin0 = Math.toRadians(phimin);
    final double phimax0 = Math.toRadians(phimax);
                                                                    
    Point3d[][] pmin = new Point3d[granularity][granularity];
    Point3d[][] pmax = new Point3d[granularity][granularity];
      
    Point3d[] outCoordinates    = new Point3d[count2];      
    Point3d[] inCoordinates     = new Point3d[count2];      
    Point3d[] topCoordinates    = new Point3d[count1];      
    Point3d[] bottomCoordinates = new Point3d[count1];      

    // Prepare points
    double phi = phimin0;
    double psi;
    for (int i = 0; i < granularity; i++) {
      psi = 0;
      for (int j = 0; j < granularity; j++) {
		    pmin[i][j] = new Point3d((rtor + rmin * Math.cos(psi)) * Math.cos(phi),
				  		 	                 (rtor + rmin * Math.cos(psi)) * Math.sin(phi),
                                         rmin * Math.sin(psi));
 		    pmax[i][j] = new Point3d((rtor + rmax * Math.cos(psi)) * Math.cos(phi),
				  		 	                 (rtor + rmax * Math.cos(psi)) * Math.sin(phi),
                                         rmax * Math.sin(psi));
        psi += 2 * Math.PI / granularity; 
        }
      phi += (phimax0 - phimin0) / granularity; 
      }
      
    // Construct tubes  
    int k = 0;
    int i1;
    int j1;
    for (int i = 0; i < granularity; i++) {
      if (i != granularity - 1 || (phimin == 0 && phimax == 360)) {
        i1 = ((i == granularity - 1) ? 0 : (i + 1));
        for (int j = 0; j < granularity; j++) {
          j1 = ((j == granularity - 1) ? 0 : (j + 1));
          // Out
          outCoordinates [k    ] = new Point3d(pmax[i ][j ]);
          outCoordinates [k + 1] = new Point3d(pmax[i1][j ]);
          outCoordinates [k + 2] = new Point3d(pmax[i1][j1]);
          outCoordinates [k + 3] = new Point3d(pmax[i ][j1]);
          // In
          if (rmin > 0) {
            inCoordinates  [k + 3] = new Point3d(pmin[i ][j ]);
            inCoordinates  [k + 2] = new Point3d(pmin[i1][j ]);
            inCoordinates  [k + 1] = new Point3d(pmin[i1][j1]);
            inCoordinates  [k    ] = new Point3d(pmin[i ][j1]);
            }
          //
          k += 4;
          }
        }
      }

    // Construct sides  
    if (phimin != 0 || phimax != 360) {
      k = 0;
      for (int j = 0; j < granularity; j++) {
        j1 = ((j == granularity - 1) ? 0 : (j + 1));
        // Top
        topCoordinates    [k    ] = new Point3d(pmin[0              ][j ]);
        topCoordinates    [k + 1] = new Point3d(pmax[0              ][j ]);
        topCoordinates    [k + 2] = new Point3d(pmax[0              ][j1]);
        topCoordinates    [k + 3] = new Point3d(pmin[0              ][j1]);
        // Bottom
        bottomCoordinates [k + 3] = new Point3d(pmin[granularity - 1][j ]);
        bottomCoordinates [k + 2] = new Point3d(pmax[granularity - 1][j ]);
        bottomCoordinates [k + 1] = new Point3d(pmax[granularity - 1][j1]);
        bottomCoordinates [k    ] = new Point3d(pmin[granularity - 1][j1]);
        //
        k += 4;
        }
      }

    // Geometries
    addCoordinates(outCoordinates);
    if (rmin > 0) {     
      addCoordinates(inCoordinates);
      }
    if (phimin != 0 || phimax != 360) {
      addCoordinates(topCoordinates);
      addCoordinates(bottomCoordinates);
      }
    
    // Appearance
    setAppearance(appearance);  
          
    }
    
  }



