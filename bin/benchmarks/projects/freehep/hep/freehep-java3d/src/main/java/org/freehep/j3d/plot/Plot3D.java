package org.freehep.j3d.plot;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.behaviors.mouse.*;

/**
 * Abstract class extended by other 3D Plot widgets.
 *
 * Defines default mouse behaviour etc.
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: Plot3D.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class Plot3D extends Canvas3D
{
   protected boolean init = false;
   protected boolean parallelProjection = false;
   protected SimpleUniverse universe;

   Plot3D()
   {
      super(SimpleUniverse.getPreferredConfiguration());
   }
   protected void init()
   {
      Node plot = createPlot();
      BranchGroup scene = defineMouseBehaviour(plot);
      setupLights(scene); // Surface plot wants an extra light
      scene.compile();

      universe = new SimpleUniverse(this);
      universe.getViewingPlatform().setNominalViewingTransform();
      universe.addBranchGraph(scene);

      if (parallelProjection) {
          setProjectionPolicy(universe, parallelProjection);
      }

      init = true;
   }

   // addNotify is called when the Canvas3D is added to a container
   public void addNotify()
   {
      if (!init) init();
      super.addNotify(); // must call for Java3D to operate properly when overriding
   }

   public boolean getParallelProjection()
   {
      return parallelProjection;
   }

   public void setParallelProjection(boolean b)
   {
      if (parallelProjection != b) {
          parallelProjection = b;
          setProjectionPolicy(universe, parallelProjection);
      }
   }

   /**
     * Override to provide plot content
     */
   protected abstract Node createPlot();

   /**
    * Override to provide different mouse behaviour
    */
   protected BranchGroup defineMouseBehaviour(Node scene)
   {
      BranchGroup bg = new BranchGroup();
      Bounds bounds = getDefaultBounds();

      TransformGroup objTransform = new TransformGroup();
      objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      objTransform.addChild(scene);
      bg.addChild(objTransform);

      MouseRotate mouseRotate = new MouseRotate();
      mouseRotate.setTransformGroup(objTransform);
      mouseRotate.setSchedulingBounds(bounds);
      bg.addChild(mouseRotate);

      MouseTranslate mouseTranslate = new MouseTranslate();
      mouseTranslate.setTransformGroup(objTransform);
      mouseTranslate.setSchedulingBounds(bounds);
      bg.addChild(mouseTranslate);

      MouseZoom mouseZoom = new MouseZoom();
      mouseZoom.setTransformGroup(objTransform);
      mouseZoom.setSchedulingBounds(bounds);
      bg.addChild(mouseZoom);

      // Set initial transformation
      Transform3D trans = createDefaultOrientation();
      objTransform.setTransform(trans);

      Behavior keyBehavior = new PlotKeyNavigatorBehavior(objTransform,.1f,10f);
      objTransform.addChild(keyBehavior);
      keyBehavior.setSchedulingBounds(bounds);

      // set up a rotation animating behavior
      // rotator = setupZRotator(dynamicXform);
      // rotator.setSchedulingBounds(bounds);
      // rotator.setEnable(false);
      // dynamicXform.addChild(rotator);

      return bg;
   }

   protected void setupLights(BranchGroup root)
   {
      DirectionalLight lightD = new DirectionalLight();
      lightD.setDirection(new Vector3f(0.0f, -0.7f, -0.7f));
      lightD.setInfluencingBounds(getDefaultBounds());
      root.addChild(lightD);

      //  This second light is added for the Surface Plot, so you
      //  can see the "under" surface
      DirectionalLight lightD1 = new DirectionalLight();
      lightD1.setDirection(new Vector3f(0.0f, 0.7f, 0.7f));
      lightD1.setInfluencingBounds(getDefaultBounds());
      root.addChild(lightD1);


      AmbientLight lightA = new AmbientLight();
      lightA.setInfluencingBounds(getDefaultBounds());
      root.addChild(lightA);
   }

   /**
    * Override to set a different initial transformation
    */
   protected Transform3D createDefaultOrientation()
   {
      Transform3D trans = new Transform3D();
      trans.setIdentity();
      trans.rotX(-Math.PI / 4.);
      trans.setTranslation(new Vector3f(0.f, -.3f, 0.f));
      return trans;
   }

   /**
    * Set the projection policy for the plot - either perspective or projection
    */
   protected void setProjectionPolicy(SimpleUniverse universe, boolean parallelProjection)
   {
        View view = universe.getViewer().getView();
        if (parallelProjection)
            view.setProjectionPolicy(View.PARALLEL_PROJECTION);
        else
            view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
   }

   /**
     * Returns a bounds object that can be used for most behaviours,
     * lighting models, etc.
     */
   protected Bounds getDefaultBounds()
   {
      if (bounds == null)
      {
         Point3d center = new Point3d(0, 0, 0);
         bounds = new BoundingSphere(center, 10);
      }
      return bounds;
   }
   private Bounds bounds;
}

