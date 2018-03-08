package org.freehep.math.minuit;

/**
 *
 * @version $Id: MnUserFcn.java 8584 2006-08-10 23:06:37Z duns $
 */
class MnUserFcn extends MnFcn
{  
   MnUserFcn(FCNBase fcn, double errDef, MnUserTransformation trafo)
   {
      super(fcn, errDef);
      theTransform = trafo;
   }
   
   double valueOf(MnAlgebraicVector v)
   {
      return super.valueOf(theTransform.transform(v));
   }
   private MnUserTransformation theTransform;
}