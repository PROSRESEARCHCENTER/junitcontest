package org.freehep.math.minuit;

/**
 *
 * @version $Id: SqrtUpParameterTransformation.java 8584 2006-08-10 23:06:37Z duns $
 */
class SqrtUpParameterTransformation
{
   // transformation from internal to external
   double int2ext(double value, double upper)
   {
      return upper + 1. - Math.sqrt( value*value + 1.);
   }
   
   // transformation from external to internal
   double ext2int(double value, double upper, MnMachinePrecision prec)
   {
      double yy = upper - value + 1.;
      double yy2 = yy*yy;
      if (yy2 < (1. + prec.eps2()) )
         return 8*Math.sqrt(prec.eps2());
      else
         return Math.sqrt( yy2 -1);
   }
   
   // derivative of transformation from internal to external
   double dInt2Ext(double value, double upper)
   {
      return - value/( Math.sqrt( value*value + 1.) );
   }
   
}
