package org.freehep.math.minuit;

/**
 * Causes minimization of the function by the method of MIGRAD, as does the MnMigrad
 * class, but switches to the SIMPLEX method if MIGRAD fails to converge. Constructor
 * arguments, methods arguments and names of methods are the same as for MnMigrad
 * or MnSimplex.
 * @version $Id: MnMinimize.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnMinimize extends MnApplication
{
   /** construct from FCNBase + double[] for parameters and errors with default strategy */
   public MnMinimize(FCNBase fcn, double[] par, double[] err)
   {
      this(fcn,par,err,DEFAULT_STRATEGY);
   }
   /** construct from FCNBase + double[] for parameters and errors */
   public MnMinimize(FCNBase fcn, double[] par, double[] err, int stra)
   {
      this(fcn, new MnUserParameterState(par,err), new MnStrategy(stra));
   }
   /** construct from FCNBase + double[] for parameters and MnUserCovariance with default strategy */
   public MnMinimize(FCNBase fcn, double[] par, MnUserCovariance cov)
   {
      this(fcn,par,cov,DEFAULT_STRATEGY);
   }
   /** construct from FCNBase + double[] for parameters and MnUserCovariance */
   public MnMinimize(FCNBase fcn, double[] par, MnUserCovariance cov, int stra)
   {
      this(fcn, new MnUserParameterState(par, cov), new MnStrategy(stra));
   }
   /** construct from FCNBase + MnUserParameters with default strategy */
   public MnMinimize(FCNBase fcn, MnUserParameters par)
   {
      this(fcn,par,DEFAULT_STRATEGY);
   }
   /** construct from FCNBase + MnUserParameters */
   public MnMinimize(FCNBase fcn, MnUserParameters par, int stra )
   {
      this(fcn, new MnUserParameterState(par), new MnStrategy(stra));
   }
   /** construct from FCNBase + MnUserParameters + MnUserCovariance with default strategy */
   public MnMinimize(FCNBase fcn, MnUserParameters par, MnUserCovariance cov)
   {
      this(fcn,par,cov,DEFAULT_STRATEGY);
   }
   /** construct from FCNBase + MnUserParameters + MnUserCovariance */
   public MnMinimize(FCNBase fcn, MnUserParameters par, MnUserCovariance cov, int stra)
   {
      this(fcn, new MnUserParameterState(par, cov), new MnStrategy(stra));
   }
   /** construct from FCNBase + MnUserParameterState + MnStrategy */
   public MnMinimize(FCNBase fcn, MnUserParameterState par, MnStrategy str)
   {
      super(fcn, par, str);
   }
   ModularFunctionMinimizer minimizer()
   {
      return theMinimizer;
   }
   private CombinedMinimizer theMinimizer = new CombinedMinimizer();
}
