package org.freehep.math.minuit;

import junit.framework.TestCase;
import junit.framework.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @version $Id: MnUserTransformationTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnUserTransformationTest extends TestCase
{
   private MnUserTransformation trans;
   private final static double delta = 1e-16;
   
   public MnUserTransformationTest(String testName)
   {
      super(testName);
   }

   protected void setUp() throws java.lang.Exception
   {
      trans = new MnUserTransformation();
      trans.add("a",1,.5);
      trans.add("b",2,1);
      trans.add("c",3,1.5);
   }

   protected void tearDown() throws java.lang.Exception
   {
   }

   public static junit.framework.Test suite()
   {
      junit.framework.TestSuite suite = new junit.framework.TestSuite(MnUserTransformationTest.class);
      return suite;
   }

   public void testClone()
   {
      MnUserTransformation clone = trans.clone();
      assertEquals(3,clone.variableParameters());
      assertEquals(1.0,clone.value(0),delta);
      assertEquals(2.0,clone.value(1),delta);
      assertEquals(3.0,clone.value(2),delta);
      assertEquals(0.5,clone.error(0),delta);
      assertEquals(1.0,clone.error(1),delta);
      assertEquals(1.5,clone.error(2),delta);
   }

   /**
    * Test of transform method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testTransform()
   {
      MnAlgebraicVector v = new MnAlgebraicVector(3);
      v.set(0,10);
      v.set(1,11);
      v.set(2,12);
      MnAlgebraicVector t = trans.transform(v);
      for (int i=0;i<3;i++) assertEquals(v.get(i),t.get(i),delta);
      
      trans.fix(1);
      
      MnAlgebraicVector v2 = new MnAlgebraicVector(2);
      v2.set(0,10);
      v2.set(1,11);
      MnAlgebraicVector t2 = trans.transform(v2);   
      assertEquals(v2.get(0),t2.get(0),delta);
      assertEquals(2.0,t2.get(1),delta);
      assertEquals(v2.get(1),t2.get(2),delta);
      
      trans.release(1);
      
      MnAlgebraicVector t3 = trans.transform(v);
      for (int i=0;i<3;i++) assertEquals(v.get(i),t3.get(i),delta);
   }

   /**
    * Test of parameters method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testParameters()
   {
      List<MinuitParameter> parms = trans.parameters();
      assertEquals(3,parms.size());

      trans.fix(1);
      
      parms = trans.parameters();
      assertEquals(3,parms.size());
      assertEquals("a",parms.get(0).name());
      assertEquals("b",parms.get(1).name());
      assertEquals("c",parms.get(2).name());
      
      trans.release(1);
      
      parms = trans.parameters();
      assertEquals(3,parms.size());
   }

   /**
    * Test of variableParameters method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testVariableParameters()
   {
      assertEquals(3,trans.variableParameters());
      trans.fix(1);
      assertEquals(2,trans.variableParameters());
      trans.release(1);
      assertEquals(3,trans.variableParameters());
   }

   /**
    * Test of params method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testParams()
   {

   }

   /**
    * Test of errors method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testErrors()
   {

   }

   /**
    * Test of parameter method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testParameter()
   {


   }

   /**
    * Test of add method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testAdd()
   {

   }

   /**
    * Test of fix method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testFix()
   {


   }

   /**
    * Test of release method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testRelease()
   {

   }

   /**
    * Test of setValue method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testSetValue()
   {
      double old = trans.value(1);
      trans.setValue(1,100);
      assertEquals(100,trans.value(1),delta);
      assertFalse(trans.parameter(1).isFixed());
      trans.setValue(1,old);
      assertEquals(old,trans.value(1),delta);     
   }

   /**
    * Test of setError method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testSetError()
   {

   }

   /**
    * Test of setLimits method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testSetLimits()
   {

   }

   /**
    * Test of setUpperLimit method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testSetUpperLimit()
   {

   }

   /**
    * Test of setLowerLimit method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testSetLowerLimit()
   {
   }

   /**
    * Test of removeLimits method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testRemoveLimits()
   {

   }

   /**
    * Test of value method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testValue()
   {

   }

   /**
    * Test of error method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testError()
   {

   }

   /**
    * Test of index method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testIndex()
   {

   }

   /**
    * Test of name method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testName()
   {

   }

   /**
    * Test of int2ext method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testInt2ext()
   {

   }

   /**
    * Test of int2extError method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testInt2extError()
   {

   }

   /**
    * Test of int2extCovariance method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testInt2extCovariance()
   {

   }

   /**
    * Test of ext2int method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testExt2int()
   {

   }

   /**
    * Test of dInt2Ext method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testDInt2Ext()
   {
   }

   /**
    * Test of intOfExt method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testIntOfExt()
   {
      for (int i=0; i<3; i++)
      {
         assertEquals(i,trans.intOfExt(i));
      }
      trans.fix(1);
      assertEquals(0,trans.intOfExt(0));
      assertEquals(1,trans.intOfExt(2));
      trans.release(1);
      for (int i=0; i<3; i++)
      {
         assertEquals(i,trans.intOfExt(i));
      } 
   }

   /**
    * Test of extOfInt method, of class org.freehep.minuit.MnUserTransformation.
    */
   public void testExtOfInt()
   {
      for (int i=0; i<3; i++)
      {
         assertEquals(i,trans.extOfInt(i));
      }
      trans.fix(1);
      assertEquals(0,trans.extOfInt(0));
      assertEquals(2,trans.extOfInt(1));
      trans.release(1);
      for (int i=0; i<3; i++)
      {
         assertEquals(i,trans.extOfInt(i));
      }      
   }
}
