package org.freehep.math.minuit;

/**
 * MnMigrad provides minimization of the function by the method of MIGRAD, the most
 * efficient and complete single method, recommended for general functions, 
 * and the functionality for parameters interaction. It also retains the result from
 * the last minimization in case the user may want to do subsequent minimization steps
 * with parameter interactions in between the minimization requests. 
 * The minimization produces as a by-product the error matrix of the parameters, which
 * is usually reliable unless warning messages are produced.
 * @version $Id: MnMigrad.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnMigrad extends MnApplication
{
   /** construct from FCNBase + double[] for parameters and errors with default strategy */
   public MnMigrad(FCNBase fcn, double[] par, double[] err)
   {
      this(fcn,par,err,DEFAULT_STRATEGY);
   }
  /** construct from FCNBase + double[] for parameters and errors */
  public MnMigrad(FCNBase fcn, double[] par, double[] err, int stra)
  {
     this(fcn, new MnUserParameterState(par,err), new MnStrategy(stra));
  }
  /** construct from FCNBase + double[] for parameters and MnUserCovariance with default strategy */
  public MnMigrad(FCNBase fcn, double[] par, MnUserCovariance cov)
  {
     this(fcn,par,cov,DEFAULT_STRATEGY);
  }
  /** construct from FCNBase + double[] for parameters and MnUserCovariance */
  public MnMigrad(FCNBase fcn, double[] par, MnUserCovariance cov, int stra)
  {
     this(fcn, new MnUserParameterState(par, cov), new MnStrategy(stra));
  } 
  /** construct from FCNBase + MnUserParameters with default strategy */
  public MnMigrad(FCNBase fcn, MnUserParameters par)
  {
     this(fcn,par,DEFAULT_STRATEGY);
  }
  /** construct from FCNBase + MnUserParameters */
  public MnMigrad(FCNBase fcn, MnUserParameters par, int stra )
  {
     this(fcn, new MnUserParameterState(par), new MnStrategy(stra));
  }
  /** construct from FCNBase + MnUserParameters + MnUserCovariance with default strategy */
  public MnMigrad(FCNBase fcn, MnUserParameters par, MnUserCovariance cov)
  {
     this(fcn,par,cov,DEFAULT_STRATEGY);
  }
  /** construct from FCNBase + MnUserParameters + MnUserCovariance */
  public MnMigrad(FCNBase fcn, MnUserParameters par, MnUserCovariance cov, int stra)
  {
     this(fcn, new MnUserParameterState(par, cov), new MnStrategy(stra));
  }
  /** construct from FCNBase + MnUserParameterState + MnStrategy */
  public MnMigrad(FCNBase fcn, MnUserParameterState par, MnStrategy str)
  {
     super(fcn, par, str);
  }

  ModularFunctionMinimizer minimizer() 
  {
     return theMinimizer;
  }

  private VariableMetricMinimizer theMinimizer = new VariableMetricMinimizer();
}