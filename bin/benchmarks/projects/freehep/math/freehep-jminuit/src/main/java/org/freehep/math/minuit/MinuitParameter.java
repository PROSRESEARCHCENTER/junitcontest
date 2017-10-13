package org.freehep.math.minuit;

/**
 *
 * @version $Id: MinuitParameter.java 8584 2006-08-10 23:06:37Z duns $
 */
class MinuitParameter
{
   /** constructor for constant parameter */
   public MinuitParameter(int num, String name, double val)
   {
      theNum = num;
      theValue = val;
      theConst = true;
      theName = name;
   }
   
   /** constructor for standard parameter */
   public MinuitParameter(int num, String name, double val, double err)
   {
      theNum = num;
      theValue = val;
      theError = err;
      theName = name;
   }
   
   /** constructor for limited parameter */
   public MinuitParameter(int num, String name, double val, double err, double min, double max)
   {
      theNum = num;
      theValue = val;
      theError = err;
      theLoLimit = min;
      theUpLimit = max;
      theLoLimValid = true;
      theUpLimValid = true;
      if (min == max) throw new IllegalArgumentException("min == max");
      if(min > max)
      {
         theLoLimit = max;
         theUpLimit = min;
      }
      theName = name;
   }
   protected MinuitParameter clone()
   {
      return new MinuitParameter(this);
   }
   private MinuitParameter(MinuitParameter other)
   {
      theNum = other.theNum;
      theName = other.theName;
      theValue = other.theValue;
      theError = other.theError;
      theConst = other.theConst;
      theFix = other.theFix;
      theLoLimit = other.theLoLimit;
      theUpLimit = other.theUpLimit;
      theLoLimValid = other.theLoLimValid;
      theUpLimValid = other.theUpLimValid;
   }
   
   //access methods
   public int number()
   {
      return theNum;
   }
   public String name()
   {
      return theName;
   }
   public double value()
   {
      return theValue;
   }
   public double error()
   {
      return theError;
   }
   
   //interaction
   public void setValue(double val)
   {
      theValue = val;
   }
   public void setError(double err)
   {
      theError = err;
   }
   public void setLimits(double low, double up)
   {
      if (low == up) throw new IllegalArgumentException("min == max");
      theLoLimit = low;
      theUpLimit = up;
      theLoLimValid = true;
      theUpLimValid = true;
      if(low > up)
      {
         theLoLimit = up;
         theUpLimit = low;
      }
   }
   
   public void setUpperLimit(double up)
   {
      theLoLimit = 0.;
      theUpLimit = up;
      theLoLimValid = false;
      theUpLimValid = true;
   }
   
   public void setLowerLimit(double low)
   {
      theLoLimit = low;
      theUpLimit = 0.;
      theLoLimValid = true;
      theUpLimValid = false;
   }
   
   public void removeLimits()
   {
      theLoLimit = 0.;
      theUpLimit = 0.;
      theLoLimValid = false;
      theUpLimValid = false;
   }
   
   public void fix()
   {
      theFix = true;
   }
   public void release()
   {
      theFix = false;
   }
   
   //state of parameter (fixed/const/limited)
   public boolean isConst()
   {
      return theConst;
   }
   public boolean isFixed()
   {
      return theFix;
   }
   
   public boolean hasLimits()
   {
      return theLoLimValid || theUpLimValid;
   }
   public boolean hasLowerLimit()
   {
      return theLoLimValid;
   }
   public boolean hasUpperLimit()
   {
      return theUpLimValid;
   }
   public double lowerLimit()
   {
      return theLoLimit;
   }
   public double upperLimit()
   {
      return theUpLimit;
   }
   
   private int theNum;
   private String theName;
   private double theValue;
   private double theError;
   private boolean theConst;
   private boolean theFix;
   private double theLoLimit;
   private double theUpLimit;
   private boolean theLoLimValid;
   private boolean theUpLimValid;
}
