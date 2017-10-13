package org.freehep.j3d.plot;

import javax.media.j3d.Behavior;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupOnAWTEvent;
import java.awt.AWTEvent;
import java.awt.event.*;
import javax.vecmath.Vector3f;

/**
 * A behaviour for 3d plots which defines certain keyboard events
 * This is used instead of the default KeyNavigatorBehavior to work around
 * bug 4376368 which causes the CPU used to go to 100%
 * see http://developer.java.sun.com/developer/bugParade/bugs/4376368.html
 *
 * Use the arrow keys and page up/page down to move. Hold the shift
 * key to rotate. Use the Home key to restore the default rotation.
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: PlotKeyNavigatorBehavior.java 8584 2006-08-10 23:06:37Z duns $
 *
 */
public class PlotKeyNavigatorBehavior extends Behavior
{
   private Transform3D init, tgr;
   private TransformGroup tg;
   private WakeupOnAWTEvent wup;
   private float step;
   private float angle;

   public PlotKeyNavigatorBehavior(TransformGroup targetTG,
           float moveStep, float rotStep)
   {
      super();
      this.tgr = new Transform3D();
      this.init = new Transform3D();
      this.tg = targetTG;
      this.wup = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
      this.step = moveStep;
      this.angle = (float) Math.toRadians(rotStep);
   }

   public void initialize()
   {
      wakeupOn(wup);
      tg.getTransform(init);
   }

   public void processStimulus(java.util.Enumeration criteria)
   {
      KeyEvent event = (KeyEvent)(wup.getAWTEvent())[0];
      int keyCode = event.getKeyCode();
      boolean shift = (event.getModifiers() & event.SHIFT_MASK) != 0;

      switch (keyCode)
      {
      case KeyEvent.VK_UP:
         move(0f, -1f, 0f, shift);
         break;
      case KeyEvent.VK_DOWN:
         move(0f, 1f, 0f, shift);
         break;
      case KeyEvent.VK_LEFT:
         move(-1f, 0f, 0f, shift);
         break;
      case KeyEvent.VK_RIGHT:
         move(1f, 0f, 0f, shift);
         break;
      case KeyEvent.VK_PAGE_UP:
         move(0f, 0f, 1f, shift);
         break;
      case KeyEvent.VK_PAGE_DOWN:
         move(0f, 0f, -1f, shift);
         break;
      case KeyEvent.VK_HOME:
         tg.setTransform(init);
      }
      wakeupOn(wup);
   }
   private void move(float x, float y, float z, boolean shift)
   {
      if (!shift) translate(x * step, y * step, z * step);
      else        rotate(x * angle, y * angle, z * angle);
   }
   private void translate(float x, float y, float z)
   {
      Transform3D tr = new Transform3D();
      Vector3f vec = new Vector3f(x, y, z);
      tr.setTranslation(vec);
      tg.getTransform(tgr);
      tgr.mul(tr);
      tg.setTransform(tgr);
   }
   private void rotate(float x, float y, float z)
   {
      Transform3D tr = new Transform3D();
      if (x != 0) tr.rotX(x);
      if (y != 0) tr.rotY(y);
      if (z != 0) tr.rotZ(z);
      tg.getTransform(tgr);
      tgr.mul(tr);
      tg.setTransform(tgr);
   }
}

