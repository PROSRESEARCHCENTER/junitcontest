package hep.graphics.j3d;

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

/** Test for <code>hep.graphics.j3d</code> shapes.
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
    if (name.equals("Helix")) {
      return(new Helix(1,   // charge
                       1,   // eta
                       30,  // phi
                       0.2, // pt
                       30,  // v_phi
                       0.1, // v_rho
                       0.1, // v_z
                       2,   // mField
                       100, // granularity
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
      System.out.println("java hep.graphics.j3d.Test [Helix]");
      System.exit(1);
      }
    else {
      new MainFrame(new Test(args[0]), 512, 512);
      }
    }
  
  }
