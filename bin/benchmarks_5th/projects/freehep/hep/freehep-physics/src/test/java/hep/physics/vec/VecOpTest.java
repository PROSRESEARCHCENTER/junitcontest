package hep.physics.vec;

import hep.physics.matrix.MatrixOp;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author tonyj
 */
public class VecOpTest extends TestCase
{
   public VecOpTest(String testName)
   {
      super(testName);
   }

   /**
    * Test of add method, of class hep.physics.vec.VecOp.
    */
   public void testAdd()
   {
      Hep3Vector v = new BasicHep3Vector(1,2,3);
      Hep3Vector w = new BasicHep3Vector(6,5,4);
      Hep3Vector result = VecOp.add(v, w);
      assertEquals(7, result.x(),1e-16);
      assertEquals(7, result.y(),1e-16);
      assertEquals(7, result.z(),1e-16);
   }

   /**
    * Test of sub method, of class hep.physics.vec.VecOp.
    */
   public void testSub()
   {
      Hep3Vector v = new BasicHep3Vector(1,2,3);
      Hep3Vector w = new BasicHep3Vector(6,5,4);
      Hep3Vector result = VecOp.sub(v, w);
      assertEquals(-5, result.x(),1e-16);
      assertEquals(-3, result.y(),1e-16);
      assertEquals(-1, result.z(),1e-16);
   }

   /**
    * Test of invert method, of class hep.physics.vec.VecOp.
    */
   public void testInvert()
   {
      Random r = new Random(1234567);
      Hep3Matrix m1 = new BasicHep3Matrix(r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble());
      Hep3Matrix m2 = VecOp.inverse(m1);
      Hep3Matrix m3 = VecOp.mult(m1,m2);
      double det = m3.det();
      assertEquals(1, det, 1e-14);      
      double det2 = MatrixOp.det(m3);
      assertEquals(1, det, 1e-14);      
   }

   public void testTranspose()
   {
      BasicHep3Matrix m1 = new BasicHep3Matrix();
      m1.setActiveEuler(.5,2,1);
      assertEquals(1.0,m1.det(),1e-14);
      
      Hep3Matrix m2 = VecOp.transposed(m1);
      Hep3Matrix m3 = VecOp.inverse(m1);
      
      m1.transpose();

      assertEquals(1.0,m1.det(),1e-14);
      assertEquals(1.0,m2.det(),1e-14);
      assertEquals(1.0,m3.det(),1e-14);
      
      for (int i=0; i<3; i++)
      {
         for (int j=0; j<3; j++)
         {
            assertEquals(m1.e(i,j),m2.e(i,j),1e-14);
            assertEquals(m2.e(i,j),m3.e(i,j),1e-14);
         }
      }
   }
}
