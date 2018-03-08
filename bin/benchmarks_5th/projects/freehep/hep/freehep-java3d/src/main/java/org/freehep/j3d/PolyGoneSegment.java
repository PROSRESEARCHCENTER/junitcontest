package org.freehep.j3d;

// Java3D
import javax.media.j3d.Appearance;
import javax.vecmath.Point3d;

/** General PolyGone Segment. All angles are in degrees, all dimensions
  * are full dimensions (not half dimensions).
  * <img src="doc-files/PolyGoneSegment.gif">
  * @version 1.1.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class PolyGoneSegment extends Solid {

  // Constructors --------------------------------------------------------------

  /** Create general polygone segment.
    * @param rmins       array of tangent distances of inner surface 
    * @param rmaxs       array of tangent distances of outer surface 
    * @param zs          array of possitions of z-planes
    * @param phimin      starting azimutal angle [deg] 
    * @param phimax      ending   azimutal angle [deg]
    * @param sides       number of sides
    * @param appearance  object' Appearance
    * @preconditions phimin > 0 && phimin < 360
    * @preconditions phimax > 0 && phimax < 360
    * @preconditions phimin < phimax
    * @preconditions rmins.length >= zs.length
    * @preconditions rmaxs.length >= zs.length
    * @preconditions zs.length > 0 
    * @preconditions for (int i = 0; i < zs.length; i++) rmins[i] < rmaxs[i]
    * @preconditions sides > 2 */
  public PolyGoneSegment(double[] rmins,
                         double[] rmaxs,
                         double[] zs,
                         double phimin,
                         double phimax,
                         int sides,
                         Appearance appearance) {
    construct(rmins,
              rmaxs,
              zs,
              phimin,
              phimax,
              sides,
              appearance);
    }        


  // ---------------------------------------------------------------------------

  private void construct(double[] rmins,
                         double[] rmaxs,
                         double[] zs,
                         double phimin,
                         double phimax,
                         int sides,
                         Appearance appearance) {

    final double phimin0 = Math.toRadians(phimin);
    final double phimax0 = Math.toRadians(phimax);
                                         
    // Prepare points

    Point3d[][] inner = new Point3d[zs.length][sides + 1];                        
    Point3d[][] outer = new Point3d[zs.length][sides + 1];
    
    double phi;
    for (int j = 0; j < sides + 1; j++) {
      phi = 2 * j * Math.PI / sides;
      for (int i = 0; i < zs.length; i++) {
        outer[i][j] = new Point3d(rmaxs[i] * Math.cos(phi),
                                  rmaxs[i] * Math.sin(phi),
                                  zs[i]);                        
        inner[i][j] = new Point3d(rmins[i] * Math.cos(phi),
                                  rmins[i] * Math.sin(phi),
                                  zs[i]);
        }
      }
      
    // Find cutoff points
    
    final boolean cutoffMin = phimin > 0;
    final boolean cutoffMax = phimax < 360;
    
    final double delphi = 2 * Math.PI / sides;

    final int jmin = (int)(phimin0 / delphi);
    final int jmax = (int)(phimax0 / delphi);
    
    final double alphamin = delphi / 2 - (phimin0 - jmin * delphi);
    final double alphamax = delphi / 2 - (phimax0 - jmax * delphi);
    
    final double smin = Math.cos(delphi / 2) / Math.cos(alphamin);
    final double smax = Math.cos(delphi / 2) / Math.cos(alphamax); 
    
    Point3d[] outermin = new Point3d[zs.length];
    Point3d[] innermin = new Point3d[zs.length];
    Point3d[] outermax = new Point3d[zs.length];
    Point3d[] innermax = new Point3d[zs.length];
    
    for (int i = 0; i < zs.length; i++) {
      outermin[i] = new Point3d(rmaxs[i] * smin * Math.cos(phimin0),
                                rmaxs[i] * smin * Math.sin(phimin0), 
                                zs[i]); 
      innermin[i] = new Point3d(rmins[i] * smin * Math.cos(phimin0),
                                rmins[i] * smin * Math.sin(phimin0), 
                                zs[i]); 
      outermax[i] = new Point3d(rmaxs[i] * smax * Math.cos(phimax0),
                                rmaxs[i] * smax * Math.sin(phimax0), 
                                zs[i]); 
      innermax[i] = new Point3d(rmins[i] * smax * Math.cos(phimax0),
                                rmins[i] * smax * Math.sin(phimax0), 
                                zs[i]); 
      }
      
    // Construct array

    Point3d[] coordinates0 = new Point3d[zs.length * sides * 10];
      
    int j0;
    int k = 0;
    boolean skip;
     
    int jstart = jmin;
    if (cutoffMin) {
      jstart = jmin + 1;
      } 

    for (int i = 0; i < zs.length; i++) {
      for (int j = jstart; j < jmax; j++) {
        skip = false;
        j0 = j + 1;
        // Front
        if (i == 0) {
          coordinates0[k + 3] = inner[i    ][j ];  
          coordinates0[k + 2] = inner[i    ][j0];  
          coordinates0[k + 1] = outer[i    ][j0];  
          coordinates0[k    ] = outer[i    ][j ]; 
          k += 4;
          }
        // Back
        if (i == zs.length - 1) {
          coordinates0[k    ] = inner[i    ][j ];  
          coordinates0[k + 1] = inner[i    ][j0];  
          coordinates0[k + 2] = outer[i    ][j0];  
          coordinates0[k + 3] = outer[i    ][j ]; 
          k += 4;
          }
        // Sides
        if (i > 0) {
          // Outer
          coordinates0[k    ] = outer[i    ][j ];
          coordinates0[k + 1] = outer[i    ][j0];
          coordinates0[k + 2] = outer[i - 1][j0];
          coordinates0[k + 3] = outer[i - 1][j ];
          k += 4;
          // Inner
          coordinates0[k + 3] = inner[i    ][j ];
          coordinates0[k + 2] = inner[i    ][j0];
          coordinates0[k + 1] = inner[i - 1][j0];
          coordinates0[k    ] = inner[i - 1][j ];
          k += 4;
          }
        }
      }
      
    if (cutoffMin) {
      // Front - phimin
      coordinates0[k    ] = inner[   0            ][jmin + 1];  
      coordinates0[k + 1] = innermin[0            ];  
      coordinates0[k + 2] = outermin[0            ];  
      coordinates0[k + 3] = outer[   0            ][jmin + 1]; 
      k += 4;
      // Back - phimin
      coordinates0[k + 3] = inner[   zs.length - 1][jmin + 1];  
      coordinates0[k + 2] = innermin[zs.length - 1];  
      coordinates0[k + 1] = outermin[zs.length - 1];  
      coordinates0[k    ] = outer[   zs.length - 1][jmin + 1]; 
      k += 4;
      }
    if (cutoffMax) {
      // Front - phimax
      coordinates0[k    ] = innermax[0            ];  
      coordinates0[k + 1] = inner[   0            ][jmax    ];  
      coordinates0[k + 2] = outer[   0            ][jmax    ];  
      coordinates0[k + 3] = outermax[0            ]; 
      k += 4;
      // Back - phimax
      coordinates0[k + 3] = innermax[zs.length - 1];  
      coordinates0[k + 2] = inner[   zs.length - 1][jmax    ];  
      coordinates0[k + 1] = outer[   zs.length - 1][jmax    ];  
      coordinates0[k    ] = outermax[zs.length - 1]; 
      k += 4;
      }
    // Sides
    for (int i = 1; i < zs.length; i++) { 
      if (cutoffMin) {
        // Outer - phimin
        coordinates0[k + 3] = outer[   i    ][jmin + 1];
        coordinates0[k + 2] = outermin[i    ];
        coordinates0[k + 1] = outermin[i - 1];
        coordinates0[k    ] = outer[   i - 1][jmin + 1];
        k += 4;
        // Inner - phimin
        coordinates0[k    ] = inner[   i    ][jmin + 1];
        coordinates0[k + 1] = innermin[i    ];
        coordinates0[k + 2] = innermin[i - 1];
        coordinates0[k + 3] = inner[   i - 1][jmin + 1];
        k += 4;
        }
      if (cutoffMax) {
        // Outer - phimax
        coordinates0[k + 3] = outermax[i    ];
        coordinates0[k + 2] = outer[   i    ][jmax    ];
        coordinates0[k + 1] = outer[   i - 1][jmax    ];
        coordinates0[k    ] = outermax[i - 1];
        k += 4;
        // Inner - phimax
        coordinates0[k    ] = innermax[i    ];
        coordinates0[k + 1] = inner[   i    ][jmax    ];
        coordinates0[k + 2] = inner[   i - 1][jmax    ];
        coordinates0[k + 3] = innermax[i - 1];
        k += 4;
        }
      // Side - phimin
      coordinates0[k + 3] = outermin[i    ];
      coordinates0[k + 2] = innermin[i    ];
      coordinates0[k + 1] = innermin[i - 1];
      coordinates0[k    ] = outermin[i - 1];
      k += 4;
      // Side - phimax
      coordinates0[k    ] = outermax[i    ];
      coordinates0[k + 1] = innermax[i    ];
      coordinates0[k + 2] = innermax[i - 1];
      coordinates0[k + 3] = outermax[i - 1];
      k += 4;
      }   
      
    // Create final array  
      
    Point3d[] coordinates = new Point3d[k];
    for (int i = 0; i < k; i++) {
      coordinates[i] = coordinates0[i];
      }                                                    
        
    // Geometry
    addCoordinates(coordinates);

    // Appearance
    setAppearance(appearance);

    }

  }



