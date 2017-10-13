package org.freehep.math.minuit;

import java.util.List;

/**
 *
 * @version $Id: SimplexParameters.java 8584 2006-08-10 23:06:37Z duns $
 */
class SimplexParameters
{
   
   SimplexParameters(List<Pair<Double, MnAlgebraicVector>> simpl, int jh, int jl)
   {
      theSimplexParameters = simpl;
      theJHigh  =jh;
      theJLow = jl;
   }
   
   void update(double y,  MnAlgebraicVector p)
   {
      theSimplexParameters.set(jh(), new Pair<Double, MnAlgebraicVector>(y, p));
      if(y < theSimplexParameters.get(jl()).first) theJLow = jh();
      
      int jh = 0;
      for(int i = 1; i < theSimplexParameters.size(); i++)
      {
         if (theSimplexParameters.get(i).first > theSimplexParameters.get(jh).first) jh = i;
      }
      theJHigh = jh;
      return;
   }
   
   List<Pair<Double, MnAlgebraicVector>> simplex()
   {
      return theSimplexParameters;
   }
   
   Pair<Double, MnAlgebraicVector> get(int i)
   {
      return theSimplexParameters.get(i);
   }
   
   int jh()
   {
      return theJHigh;
   }
   int jl()
   {
      return theJLow;
   }
   double edm()
   {
      return theSimplexParameters.get(jh()).first - theSimplexParameters.get(jl()).first;
   }
   MnAlgebraicVector dirin()
   {
      MnAlgebraicVector dirin = new MnAlgebraicVector(theSimplexParameters.size() - 1);
      for(int i = 0; i < theSimplexParameters.size() - 1; i++)
      {
         double pbig = theSimplexParameters.get(0).second.get(i);
         double plit = pbig;
         for(int j = 0; j < theSimplexParameters.size(); j++)
         {
            if(theSimplexParameters.get(j).second.get(i) < plit) plit = theSimplexParameters.get(j).second.get(i);
            if(theSimplexParameters.get(j).second.get(i) > pbig) pbig = theSimplexParameters.get(j).second.get(i);
         }
         dirin.set(i, pbig - plit);
      }
      
      return dirin;
   }
   
   private List<Pair<Double, MnAlgebraicVector>> theSimplexParameters;
   private int theJHigh;
   private int theJLow; 
}
