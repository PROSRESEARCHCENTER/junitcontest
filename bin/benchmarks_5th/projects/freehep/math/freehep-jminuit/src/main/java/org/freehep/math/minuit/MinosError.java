package org.freehep.math.minuit;

/**
 *
 * @version $Id: MinosError.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MinosError
{
   MinosError()
   {
      theUpper = new MnCross();
      theLower = new MnCross();
   }
   
   MinosError(int par, double min, MnCross low, MnCross up)
   {
      theParameter = par;
      theMinValue = min;
      theUpper = up;
      theLower = low;
   }
   public Point range()
   {
      return new Point(lower(), upper());
   }
   public double lower()
   {
      return -1.*lowerState().error(parameter())*(1. + theLower.value());
   }
   public double upper()
   {
      return upperState().error(parameter())*(1. + theUpper.value());
   }
   public int parameter()
   {
      return theParameter;
   }
   public MnUserParameterState lowerState()
   {
      return theLower.state();
   }
   public MnUserParameterState upperState()
   {
      return theUpper.state();
   }
   public boolean isValid()
   {
      return theLower.isValid() && theUpper.isValid();
   }
   public boolean lowerValid()
   {
      return theLower.isValid();
   }
   public boolean upperValid()
   {
      return theUpper.isValid();
   }
   public boolean atLowerLimit()
   {
      return theLower.atLimit();
   }
   public boolean atUpperLimit()
   {
      return theUpper.atLimit();
   }
   public boolean atLowerMaxFcn()
   {
      return theLower.atMaxFcn();
   }
   public boolean atUpperMaxFcn()
   {
      return theUpper.atMaxFcn();
   }
   public boolean lowerNewMin()
   {
      return theLower.newMinimum();
   }
   public boolean upperNewMin()
   {
      return theUpper.newMinimum();
   }
   public int nfcn()
   {
      return theUpper.nfcn() + theLower.nfcn();
   }
   public double min()
   {
      return theMinValue;
   }
   public String toString()
   {
      return MnPrint.toString(this);
   }
   private int theParameter;
   private double theMinValue;
   private MnCross theUpper;
   private MnCross theLower;
}
