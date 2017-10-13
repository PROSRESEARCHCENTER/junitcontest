package org.freehep.math.minuit;

/**
 *
 * @version $Id: MnGlobalCorrelationCoeff.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnGlobalCorrelationCoeff
{
   
   MnGlobalCorrelationCoeff()
   {
      theGlobalCC = new double[0];
   }
   MnGlobalCorrelationCoeff(MnAlgebraicSymMatrix cov)
   {
      try
      {
         MnAlgebraicSymMatrix inv = cov.clone();
         inv.invert();
         theGlobalCC = new double[cov.nrow()];       
         for(int i = 0; i < cov.nrow(); i++)
         {
            double denom = inv.get(i,i)*cov.get(i,i);
            if(denom < 1. && denom > 0.) theGlobalCC[i] = 0;
            else theGlobalCC[i] = Math.sqrt(1. - 1./denom);
         }      
         theValid = true;
      }
      catch (MatrixInversionException x)
      {
         theValid = false;
         theGlobalCC = new double[0];
      }
   }
   
   public double[] globalCC()
   {
      return theGlobalCC;
   }
   
   public boolean isValid()
   {
      return theValid;
   }
   public String toString()
   {
      return MnPrint.toString(this);
   }  
   private double[] theGlobalCC;
   private boolean theValid;
}
