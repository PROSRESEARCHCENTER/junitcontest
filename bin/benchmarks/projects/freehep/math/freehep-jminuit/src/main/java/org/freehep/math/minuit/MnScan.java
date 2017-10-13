package org.freehep.math.minuit;

import java.util.List;

/**
 * MnScan scans the value of the user function by varying one parameter. It is sometimes 
 * useful for debugging the user function or finding a reasonable starting point.
 * @version $Id: MnScan.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnScan extends MnApplication
{
   /** construct from FCNBase + double[] for parameters and errors with default strategy */
   public MnScan(FCNBase fcn, double[] par, double[] err)
   {
      this(fcn,par,err,DEFAULT_STRATEGY);
   }
  /** construct from FCNBase + double[] for parameters and errors */
  public MnScan(FCNBase fcn, double[] par, double[] err, int stra)
  {
     this(fcn, new MnUserParameterState(par,err), new MnStrategy(stra));
  }
  /** construct from FCNBase + double[] for parameters and MnUserCovariance with default strategy */
  public MnScan(FCNBase fcn, double[] par, MnUserCovariance cov)
  {
     this(fcn,par,cov,DEFAULT_STRATEGY);
  }
  /** construct from FCNBase + double[] for parameters and MnUserCovariance */
  public MnScan(FCNBase fcn, double[] par, MnUserCovariance cov, int stra)
  {
     this(fcn, new MnUserParameterState(par, cov), new MnStrategy(stra));
  } 
  /** construct from FCNBase + MnUserParameters with default strategy */
  public MnScan(FCNBase fcn, MnUserParameters par)
  {
     this(fcn,par,DEFAULT_STRATEGY);
  }
  /** construct from FCNBase + MnUserParameters */
  public MnScan(FCNBase fcn, MnUserParameters par, int stra )
  {
     this(fcn, new MnUserParameterState(par), new MnStrategy(stra));
  }
  /** construct from FCNBase + MnUserParameters + MnUserCovariance with default strategy */
  public MnScan(FCNBase fcn, MnUserParameters par, MnUserCovariance cov)
  {
     this(fcn,par,cov,DEFAULT_STRATEGY);
  }
  /** construct from FCNBase + MnUserParameters + MnUserCovariance */
  public MnScan(FCNBase fcn, MnUserParameters par, MnUserCovariance cov, int stra)
  {
     this(fcn, new MnUserParameterState(par, cov), new MnStrategy(stra));
  }
  /** construct from FCNBase + MnUserParameterState + MnStrategy */
  public MnScan(FCNBase fcn, MnUserParameterState par, MnStrategy str)
  {
     super(fcn, par, str);
  }
   
   ModularFunctionMinimizer minimizer()
   {
      return theMinimizer;
   }
   
   public List<Point> scan(int par)
   {
      return scan(par,41);
   }
   public List<Point> scan(int par, int maxsteps)
   {
      return scan(par,maxsteps,0,0);
   }
   /**
    * Scans the value of the user function by varying parameter number par, leaving all
    * other parameters fixed at the current value. If par is not specified, all variable
    * parameters are scanned in sequence. The number of points npoints in the scan is
    * 40 by default, and cannot exceed 100. The range of the scan is by default 2 standard
    * deviations on each side of the current best value, but can be specified as from low
    * to high. After each scan, if a new minimum is found, the best parameter values are
    * retained as start values for future scans or minimizations. The curve resulting from
    * each scan can be plotted on the output terminal using MnPlot in order to show
    * the approximate behaviour of the function.
    */
   public List<Point> scan(int par, int maxsteps, double low, double high)
   {
      MnParameterScan scan = new MnParameterScan(theFCN, theState.parameters());
      double amin = scan.fval();
      
      List<Point> result = scan.scan(par, maxsteps, low, high);
      if(scan.fval() < amin)
      {
         theState.setValue(par, scan.parameters().value(par));
         amin = scan.fval();
      }
      return result;
   }
   
   private ScanMinimizer theMinimizer = new ScanMinimizer();
}
