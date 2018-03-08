package hep.physics.matrix;

import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author tonyj
 */
public class SymmetricMatrixTest extends TestCase
{
   private double[] data = { 0, 1, 2, 3, 2, 3, 4, 4, 5, 6 };
   public SymmetricMatrixTest(String testName)
   {
      super(testName);
   }
   
   /**
    * Test of getPackedArray method, of class hep.physics.matrix.SymmetricMatrix.
    */
   public void testGetPackedArray()
   {
      SymmetricMatrix sm1 = new SymmetricMatrix(4,data,false);
      double[] d = sm1.asPackedArray(true);
      SymmetricMatrix sm2 = new SymmetricMatrix(4,d,true);
      for (int i=0; i<4; i++)
      {
         for (int j=0; j<4; j++)
         {
            assertEquals(sm1.e(i,j),sm2.e(i,j),1e-16);
         }
      }
      
   }
   
   /**
    * Test of getNRows method, of class hep.physics.matrix.SymmetricMatrix.
    */
   public void testGetNRows()
   {
      SymmetricMatrix sm = new SymmetricMatrix(4,data,false);
      assertEquals(4,sm.getNRows());
   }
   
   /**
    * Test of getNColumns method, of class hep.physics.matrix.SymmetricMatrix.
    */
   public void testGetNColumns()
   {
      SymmetricMatrix sm = new SymmetricMatrix(4,data,false);
      assertEquals(4,sm.getNColumns());
   }
   
   /**
    * Test of diagonal method, of class hep.physics.matrix.SymmetricMatrix.
    */
   public void testDiagonal()
   {
      SymmetricMatrix sm = new SymmetricMatrix(4,data,false);
      assertEquals(0 ,sm.diagonal(0),1e-16);
      assertEquals(2,sm.diagonal(1),1e-16);
      assertEquals(4,sm.diagonal(2),1e-16);
      assertEquals(6,sm.diagonal(3),1e-16);
   }
   
   /**
    * Test of e method, of class hep.physics.matrix.SymmetricMatrix.
    */
   public void testE()
   {
      SymmetricMatrix sm = new SymmetricMatrix(4,data,false);
      for (int i=0; i<4; i++)
      {
         for (int j=0; j<4; j++)
         {
            assertEquals(i+j,sm.e(i,j),1e-16);
         }
      }
   }
   public void testInvert()
   {
      Random r = new Random(1234567);
      double[] d = new double[10];
      for (int i=0; i<10; i++) d[i] = r.nextDouble();
      SymmetricMatrix sm = new SymmetricMatrix(4,d,false);
      MutableMatrix mm = new BasicMatrix(4,4);
      MatrixOp.inverse(sm,mm);
      Matrix m3 = MatrixOp.mult(sm,mm);
      double det = MatrixOp.det(m3);
      assertEquals(1, det, 1e-14);      
   }
}
