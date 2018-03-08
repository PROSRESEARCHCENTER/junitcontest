package org.freehep.j3d;

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

/** Test for <code>org.freehep.j3d</code> shapes.
  * @version 1.0.1
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
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
    if (name.equals("Trapezoid")) {
      return(new Trapezoid(0.1, // xmu
                           0.2, // xmd
                           0.3, // xpu
                           0.4, // xpd
                           0.5, // ym
                           0.6, // yp
                           1.0, // z
                           10,  // inclXZ
                           20,  // inclYm
                           30,  // declYm
                           40,  // declYp
                           new Appearance()));
      }
    else if (name.equals("ConeSegment")) {
      return(new ConeSegment(0.1, // rminm
                             0.2, // rminp
                             0.3, // rmaxm
                             0.6, // rmaxp
                             0.6, // l
                             20,  // phimin
                             120, // phimax
                             10,  // granularity
                             new Appearance()));
      }
    else if (name.equals("SphereSegment")) {
      return(new SphereSegment(0.4, // rmin
                               0.8, // rmax
                               0,   // phimin
                               120, // phimax
                               -40, // thetamin
                               70,  // thetamax
                               10,  // granularity
                               new Appearance()));
      }
    else if (name.equals("PolyConeSegment")) {
      double[] rmins = {0.1, 0.2, 0.4};
      double[] rmaxs = {0.5, 0.6, 0.8};
      double[] zs    = {-0.5, 0.0, 0.5};
      return(new PolyConeSegment(rmins,
                                 rmaxs,
                                 zs, 
                                 20,  // phimin
                                 120, // phimax
                                 10,  // granularity
                                 new Appearance()));
      }
    else if (name.equals("PolyGoneSegment")) {
      double[] rmins = {0.1, 0.2, 0.4};
      double[] rmaxs = {0.5, 0.6, 0.8};
      double[] zs    = {-0.5, 0.0, 0.5};
      return(new PolyGoneSegment(rmins,
                                 rmaxs,
                                 zs, 
                                 20,  // phimin
                                 230, // phimax
                                 10,  // sides
                                 new Appearance()));
      }
    else if (name.equals("TorusSegment")) {
      return(new TorusSegment(0.1, // rmin
                              0.3, // rmax
                              0.5, // rtor
                              20,  // phimin
                              250, // phimax
                              20,  // granularity
                              new Appearance()));
      }
    else if (name.equals("PolyLine")) {
      Point3d[] points = {new Point3d(0.0,  0.1, -0.1),
                          new Point3d(0.1, -0.1,  0.1),
                          new Point3d(0.2,  0.1, -0.1),
                          new Point3d(0.3, -0.1,  0.1),
                          new Point3d(0.4,  0.2, -0.1),
                          new Point3d(0.5, -0.4,  0.1),
                          new Point3d(0.6,  0.8, -0.1),
                          new Point3d(0.7, -0.8,  0.2),
                          new Point3d(0.8,  0.8, -0.4),
                          new Point3d(0.9, -0.8,  0.8)};
      return(new PolyLine(points, // points
                              new Appearance()));
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
      System.out.println("java org.freehep.j3d.Test [Trapezoid|ConeSegment|SphereSegment|PolyConeSegment|PolyGoneSegment|TorusSegment|PolyLine]");
      System.exit(1);
      }
    else {
      new MainFrame(new Test(args[0]), 512, 512);
      }
    }
  
  }
