package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.Trapezoid;
import org.freehep.j3d.OutlinedShape3D;

// AWT
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

// Java3D
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Alpha;
import javax.media.j3d.RotationInterpolator;
import javax.vecmath.Point3d;
import javax.vecmath.Color3f;

/** Test for <code>hep.graphics.j3d.geant4</code> shapes.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
// TBD: refactor with org.freehep.j3d.Test
public class Test extends Applet {

  public BranchGroup createSceneGraph(String arg) {
    BranchGroup objRoot = new BranchGroup();
    TransformGroup objTrans = new TransformGroup();
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objRoot.addChild(objTrans);
    Color3f color  = new Color3f(1f, 0f, 0f);
    Color3f color0 = new Color3f(1f, 1f, 1f);
    Shape3D shape  = testShape(arg);
    if (shape == null) {
      System.out.println("I don't know how to create " + arg);
      System.exit(1);
      }
    Shape3D shape0 = new Trapezoid(0.03, 
                                   0.10, 
                                   0.06, 
                                   0.15, 
                                   0.20,
                                   new Appearance());
    Shape3D s3d  = OutlinedShape3D.create(shape, color);
    Shape3D s3d0 = OutlinedShape3D.create(shape0, color0);
    objTrans.addChild(s3d);
    objTrans.addChild(s3d0);
    Transform3D yAxis = new Transform3D();
    Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
                                    0, 0,
                                    4000, 0, 0,
                                    0, 0, 0);
    RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, 
                                                            objTrans, 
                                                            yAxis,
                                                            0.0f, 
                                                            (float) Math.PI*2.0f);
    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
    rotator.setSchedulingBounds(bounds);
    objTrans.addChild(rotator);
    objRoot.compile();
    return objRoot;
    }

  protected Shape3D testShape(String name) {
    if (name.equals("G4Box")) {
      return(new G4Box("G4Box",
                       0.1,  // pX
                       0.2,  // pY
                       0.3)); // pZ
      }
    else if (name.equals("G4Para")) {
      return(new G4Para("G4Para",
                        0.1,  // pDx
                        0.2,  // pDy
                        0.3,  // pDz
                        0.5,  // pAlpha
                        0.5,  // pTheta
                        0.5)); // pPhi
      }
    else if (name.equals("G4Trap")) {
      return(new G4Trap("G4Trap",
                        0.5,  // pDz
                        0.5,  // pTheta
                        0.5,  // pPhi
                        0.2,  // pDy1
                        0.1,  // pDx1
                        0.1,  // pDx2
                        0.5,  // pAlp1
                        0.3,  // pDy2
                        0.2,  // pDx3
                        0.2,  // pDx4
                        0.3)); // pAlp2
      }
    else if (name.equals("G4Trd")) {
      return(new G4Trd("G4Trd",
                       0.1,  // dx1
                       0.3,  // dx2
                       0.2,  // dy1
                       0.4,  // dy2
                       0.6)); // dz
      }
    else if (name.equals("G4Cons")) {
      return(new G4Cons("G4Cons",
                        0.1,  // pRmin1
                        0.2,  // pRmin2
                        0.3,  // pRmax1
                        0.4,  // pRmax2
                        0.3,  // pDz
                        0.4,  // pSPhi
                        1.2)); // pDPhi
      }
    else if (name.equals("G4Tubs")) {
      return(new G4Tubs("G4Tubs",
                        0.1,  // pRMin
                        0.3,  // pRMax
                        0.3,  // pDz
                        0.4,  // pSPhi
                        1.2)); // pDPhi
      }
    else if (name.equals("G4Sphere")) {
      return(new G4Sphere("G4Sphere",
                          0.4,  // rmin
                          0.8,  // rmax
                          0,    // phimin
                          2.0,  // phimax
                          -0.4, // thetamin
                          1.3)); // thetamax
      }
    else if (name.equals("G4Polycone")) {
      double[] rmins = {0.1, 0.2, 0.4};
      double[] rmaxs = {0.5, 0.6, 0.8};
      double[] zs    = {-0.5, 0.0, 0.5};
      return(new G4Polycone("G4Polycone",
                            0.2,    // phiStart
                            1.2,    // phiTotal
                            3,      // numZPlanes
                            zs,     // zPlane
                            rmins,  // rInner
                            rmaxs)); // rOuter
      }
    else if (name.equals("G4Polyhedra")) {
      double[] rmins = {0.1, 0.2, 0.4};
      double[] rmaxs = {0.5, 0.6, 0.8};
      double[] zs    = {-0.5, 0.0, 0.5};
      return(new G4Polyhedra("G4Polyhedra",
                             0.2,    // phiStart
                             2.1,    // phiTotal
                             10,     // numSides
                             3,      // numZPlanes
                             zs,     // zPlane
                             rmins,  // rInner
                             rmaxs)); // rOuter
      }
    else if (name.equals("G4Torus")) {
      return(new G4Torus("G4Torus",
                         0.1,  // pRmin
                         0.3,  // pRmax
                         0.5,  // pRtor
                         0.2,  // pSPhi
                         1.5)); // pDPhi
      }
    else {
      return(null);
      }
    }

  public Test(String arg) {
    setLayout(new BorderLayout());
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    Canvas3D c = new Canvas3D(config);
    add("Center", c);
    BranchGroup scene = createSceneGraph(arg);
    SimpleUniverse u = new SimpleUniverse(c);
    u.getViewingPlatform().setNominalViewingTransform();
    u.addBranchGraph(scene);
    }
    
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("java hep.graphics.j3d.geant4.Test G4[Box|Trd|Trap|Para|Cons|Tubs|Polycone|Polyhedra|Sphere|Torus]");
      System.exit(1);
      }
    else {
      new MainFrame(new Test(args[0]), 512, 512);
      }
    }
  
  }
