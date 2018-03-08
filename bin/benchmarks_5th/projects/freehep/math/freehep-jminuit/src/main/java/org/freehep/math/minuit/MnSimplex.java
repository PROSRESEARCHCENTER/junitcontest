package org.freehep.math.minuit;

/**
 * SIMPLEX is a function minimization method using the simplex method of Nelder and
 * Mead. MnSimplex provides minimization of the function by the method of SIMPLEX
 * and the functionality for parameters interaction. It also retains the result from the
 * last minimization in case the user may want to do subsequent minimization steps with
 * parameter interactions in between the minimization requests. As SIMPLEX is a
 * stepping method it does not produce a covariance matrix.
 * @version $Id: MnSimplex.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnSimplex extends MnApplication
{
   /** construct from FCNBase + double[] for parameters and errors with default strategy */
   public MnSimplex(FCNBase fcn, double[] par, double[] err)
   {
      this(fcn,par,err,DEFAULT_STRATEGY);
   }
  /** construct from FCNBase + double[] for parameters and errors */
  public MnSimplex(FCNBase fcn, double[] par, double[] err, int stra)
  {
     this(fcn, new MnUserParameterState(par,err), new MnStrategy(stra));
  }
  /** construct from FCNBase + double[] for parameters and MnUserCovariance with default strategy */
  public MnSimplex(FCNBase fcn, double[] par, MnUserCovariance cov)
  {
     this(fcn,par,cov,DEFAULT_STRATEGY);
  }
  /** construct from FCNBase + double[] for parameters and MnUserCovariance */
  public MnSimplex(FCNBase fcn, double[] par, MnUserCovariance cov, int stra)
  {
     this(fcn, new MnUserParameterState(par, cov), new MnStrategy(stra));
  } 
  /** construct from FCNBase + MnUserParameters with default strategy */
  public MnSimplex(FCNBase fcn, MnUserParameters par)
  {
     this(fcn,par,DEFAULT_STRATEGY);
  }
  /** construct from FCNBase + MnUserParameters */
  public MnSimplex(FCNBase fcn, MnUserParameters par, int stra )
  {
     this(fcn, new MnUserParameterState(par), new MnStrategy(stra));
  }
  /** construct from FCNBase + MnUserParameters + MnUserCovariance with default strategy */
  public MnSimplex(FCNBase fcn, MnUserParameters par, MnUserCovariance cov)
  {
     this(fcn,par,cov,DEFAULT_STRATEGY);
  }
  /** construct from FCNBase + MnUserParameters + MnUserCovariance */
  public MnSimplex(FCNBase fcn, MnUserParameters par, MnUserCovariance cov, int stra)
  {
     this(fcn, new MnUserParameterState(par, cov), new MnStrategy(stra));
  }
  /** construct from FCNBase + MnUserParameterState + MnStrategy */
  public MnSimplex(FCNBase fcn, MnUserParameterState par, MnStrategy str)
  {
     super(fcn, par, str);
  }
   
  public ModularFunctionMinimizer minimizer() 
  {
     return theMinimizer;
  }
  private SimplexMinimizer theMinimizer = new SimplexMinimizer();
}
