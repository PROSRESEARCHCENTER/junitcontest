package org.freehep.math.minuit;

/**
 *
 * @version $Id: MnCross.java 8584 2006-08-10 23:06:37Z duns $
 */
class MnCross
{
   
   MnCross()
   {
      theState = new MnUserParameterState();
   }
   
   MnCross(int nfcn)
   {
      theState = new MnUserParameterState();
      theNFcn = nfcn;
   }
   
   MnCross(double value, MnUserParameterState state, int nfcn)
   {
      theValue = value;
      theState = state;
      theNFcn = nfcn;
      theValid = true;
   }
   
   MnCross(MnUserParameterState state, int nfcn, CrossParLimit x)
   {
      theState = state;
      theNFcn = nfcn;
      theLimset = true;
   }
   
   MnCross(MnUserParameterState state, int nfcn, CrossFcnLimit x)
   {
      theState = state;
      theNFcn = nfcn;
      theMaxFcn = true;
   }
   
   MnCross(MnUserParameterState state, int nfcn, CrossNewMin x)
   {
      theState = state;
      theNFcn = nfcn;
      theNewMin = true;
   }
   
   
   double value()
   {
      return theValue;
   }
   MnUserParameterState state()
   {
      return theState;
   }
   boolean isValid()
   {
      return theValid;
   }
   boolean atLimit()
   {
      return theLimset;
   }
   boolean atMaxFcn()
   {
      return theMaxFcn;
   }
   boolean newMinimum()
   {
      return theNewMin;
   }
   int nfcn()
   {
      return theNFcn;
   }
   
   private double theValue;
   private MnUserParameterState theState;
   private int theNFcn;
   private boolean theValid;
   private boolean theLimset;
   private boolean theMaxFcn;
   private boolean theNewMin;
   
   static class CrossParLimit{};
   static class CrossFcnLimit{};
   static class CrossNewMin{};
}
