package org.freehep.math.minuit;

import java.util.Random;
import junit.framework.TestCase;


/**
 *
 * @author tonyj
 */
public class MnAlgebraicSymMatrixTest extends TestCase
{
   private MnAlgebraicSymMatrix matrix;
   public MnAlgebraicSymMatrixTest(String testName)
   {
      super(testName);
   }

   @Override
   protected void setUp() throws java.lang.Exception
   {
      Random random = new Random(12345);
      matrix = new MnAlgebraicSymMatrix(5);
      for (int i=0; i<5; i++)
         for (int j=0; j<=i; j++)
            matrix.set(i,j,random.nextDouble());      
   }

   public static junit.framework.Test suite()
   {
      junit.framework.TestSuite suite = new junit.framework.TestSuite(MnAlgebraicSymMatrixTest.class);    
      return suite;
   }

   /**
    * Test of invert method, of class org.freehep.minuit.MnAlgebraicSymMatrix.
    */
   public void testInvert()
   {  
      MnAlgebraicSymMatrix m = matrix.clone();
      try
      {
         m.invert();
         MnAlgebraicSymMatrix p = MnUtils.mul(m,matrix);
         for (int i=0; i<p.nrow(); i++)
         {
            for (int j=0; j<p.nrow(); j++)
            {
               assertEquals(i==j ? 1 : 0,p.get(i,j),1e-14);
            }
         }
      }
      catch (MatrixInversionException x)
      {
         fail("MatrixInversionException");
      }
   }

   /**
    * Test of clone method, of class org.freehep.minuit.MnAlgebraicSymMatrix.
    */
   public void testClone()
   {
      MnAlgebraicSymMatrix m = matrix.clone();
      assertEquals(m.nrow(),matrix.nrow());
      assertEquals(m.ncol(),matrix.ncol());
      assertEquals(m.size(),matrix.size());
      
      for (int i=0; i<5; i++)
         for (int j=0; j<=i; j++)
            assertEquals(m.get(i,j),matrix.get(i,j));     
   }

   /**
    * Test of eigenvalues method, of class org.freehep.minuit.MnAlgebraicSymMatrix.
    */
   public void testEigenvalues()
   {
      MnAlgebraicVector v = matrix.eigenvalues();
      System.out.println(v);
   }

   /**
    * Test of get method, of class org.freehep.minuit.MnAlgebraicSymMatrix.
    */
   public void testGet()
   {

   }

   /**
    * Test of set method, of class org.freehep.minuit.MnAlgebraicSymMatrix.
    */
   public void testSet()
   {

   }

   /**
    * Test of data method, of class org.freehep.minuit.MnAlgebraicSymMatrix.
    */
   public void testData()
   {
      MnAlgebraicSymMatrix m = new MnAlgebraicSymMatrix(5);
      double data[] = m.data();
      assertEquals(data.length,15);
   }

   /**
    * Test of size method, of class org.freehep.minuit.MnAlgebraicSymMatrix.
    */
   public void testSize()
   {
      MnAlgebraicSymMatrix m = new MnAlgebraicSymMatrix(5);
      assertEquals(m.size(),15);
   }

   /**
    * Test of nrow method, of class org.freehep.minuit.MnAlgebraicSymMatrix.
    */
   public void testNrow()
   {
      MnAlgebraicSymMatrix m = new MnAlgebraicSymMatrix(5);
      assertEquals(m.nrow(),5);
   }

   /**
    * Test of ncol method, of class org.freehep.minuit.MnAlgebraicSymMatrix.
    */
   public void testNcol()
   {
      MnAlgebraicSymMatrix m = new MnAlgebraicSymMatrix(5);
      assertEquals(m.ncol(),5);
   }
   
   // TODO add test methods here. The name must begin with 'test'. For example:
   // public void testHello() {}
   
}
