package org.freehep.math.minuit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Result of the minimization.
 * <p>
 * The FunctionMinimum is the output of the minimizers and contains the minimization result. 
 * The methods
 * <ul>
 * <li>userState(),
 * <li>userParameters() and
 * <li>userCovariance()
 * </ul>
 * are provided. These can be used as new input to a new minimization after some
 * manipulation. The parameters and/or the FunctionMinimum can be printed using
 * the toString() method or the MnPrint class.
 * @version $Id: FunctionMinimum.java 8584 2006-08-10 23:06:37Z duns $
 * @see #userState()
 * @see #userParameters()
 * @see #userCovariance()
 * @see MnPrint
 */
public class FunctionMinimum
{
   FunctionMinimum(MinimumSeed seed, double up)
   {
      theSeed = seed;
      theStates = new ArrayList<MinimumState>();
      theStates.add(new MinimumState(seed.parameters(), seed.error(), seed.gradient(), seed.parameters().fval(), seed.nfcn()));
      theErrorDef = up;
      theUserState = new MnUserParameterState();
   }
   
   FunctionMinimum(MinimumSeed seed, List<MinimumState> states, double up)
   {
      theSeed = seed;
      theStates = states;
      theErrorDef = up;
      theUserState = new MnUserParameterState();
   }
   
   FunctionMinimum(MinimumSeed seed, List<MinimumState> states, double up, MnReachedCallLimit x)
   {
      theSeed = seed;
      theStates = states;
      theErrorDef = up;
      theReachedCallLimit = true;
      theUserState = new MnUserParameterState();
   }
   
   FunctionMinimum(MinimumSeed seed, List<MinimumState> states, double up, MnAboveMaxEdm x)
   {
      theSeed = seed;
      theStates = states;
      theErrorDef = up;
      theAboveMaxEdm = true;
      theReachedCallLimit = false;
      theUserState = new MnUserParameterState();
   }
   // why not
   void add(MinimumState state)
   {
      theStates.add(state);
   }
   
   MinimumSeed seed()
   {
      return theSeed;
   }
   List<MinimumState> states()
   {
      return theStates;
   }
   
   /** user representation of state at minimum */
   public MnUserParameterState userState()
   {
      if(!theUserState.isValid())
         theUserState = new MnUserParameterState(state(), errorDef(), seed().trafo());
      return theUserState;
   }
   public MnUserParameters userParameters()
   {
      if(!theUserState.isValid())
         theUserState = new MnUserParameterState(state(), errorDef(), seed().trafo());
      return theUserState.parameters();
   }
   public MnUserCovariance userCovariance()
   {
      if(!theUserState.isValid())
         theUserState = new MnUserParameterState(state(), errorDef(), seed().trafo());
      return theUserState.covariance();
   }
   
   private MinimumState lastState()
   {
      return theStates.get(theStates.size()-1);
   }
   // forward interface of last state
   MinimumState state()
   {
      return lastState();
   }
   MinimumParameters parameters()
   {
      return lastState().parameters();
   }
   MinimumError error()
   {
      return lastState().error();
   }
   FunctionGradient grad()
   {
      return lastState().gradient();
   }
   /**
    * Returns the function value at the minimum.
    */
   public double fval()
   {
      return lastState().fval();
   }
   /**
    * returns the expected vertical distance to the minimum (EDM)
    */
   public double edm()
   {
      return lastState().edm();
   }
   /**
    * returns the total number of function calls during the minimization.
    */
   public int nfcn()
   {
      return lastState().nfcn();
   }
   
   public double errorDef()
   {
      return theErrorDef;
   }
   
   /**
    * In general, if this returns <CODE>true</CODE>, the minimizer did find a minimum
    * without running into troubles. However, in some cases
    * a minimum cannot be found, then the return value will be <CODE>false</CODE>.
    * Reasons for the minimization to fail are
    * <ul>
    * <li>the number of allowed function calls has been exhausted</ul>
    * <li>the minimizer could not improve the values of the parameters (and knowing
    * that it has not converged yet)</ul>
    * <li>a problem with the calculation of the covariance matrix</ul>
    * </ul>
    * Additional methods for the analysis of the state at the minimum are provided.
    */
   public boolean isValid()
   {
      return state().isValid() && !isAboveMaxEdm() && !hasReachedCallLimit();
   }
   boolean hasValidParameters()
   {
      return state().parameters().isValid();
   }
   boolean hasValidCovariance()
   {
      return state().error().isValid();
   }
   boolean hasAccurateCovar()
   {
      return state().error().isAccurate();
   }
   boolean hasPosDefCovar()
   {
      return state().error().isPosDef();
   }
   boolean hasMadePosDefCovar()
   {
      return state().error().isMadePosDef();
   }
   boolean hesseFailed()
   {
      return state().error().hesseFailed();
   }
   boolean hasCovariance()
   {
      return state().error().isAvailable();
   }
   boolean isAboveMaxEdm()
   {
      return theAboveMaxEdm;
   }
   boolean hasReachedCallLimit()
   {
      return theReachedCallLimit;
   }
   public String toString()
   {
      return MnPrint.toString(this);
   }
   
   static class MnReachedCallLimit{};
   static class MnAboveMaxEdm{};
   
   private MinimumSeed theSeed;
   private List<MinimumState> theStates;
   private double theErrorDef;
   private boolean theAboveMaxEdm;
   private boolean theReachedCallLimit;
   private MnUserParameterState theUserState;
}
