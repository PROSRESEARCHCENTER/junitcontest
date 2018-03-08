package hep.aida.ref.optimizer.jminuit;

import hep.aida.ext.IOptimizerConfiguration;
import hep.aida.ext.IOptimizerResult;
import hep.aida.ext.IVariableSettings;
import hep.aida.ref.optimizer.AbstractOptimizer;
import hep.aida.ref.optimizer.OptimizerResult;
import java.util.List;
import java.util.ArrayList;
import org.freehep.math.minuit.FCNBase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;
import org.freehep.math.minuit.MnUserCovariance;
import org.freehep.math.minuit.MnContours;
import org.freehep.math.minuit.Point;
/**
 *
 * JMinuit implementation of IOptimizer
 * @author The AIDA team @SLAC.
 *
 */
class JMinuitOptimizer extends AbstractOptimizer
{
    private FunctionMinimum min;
    private FCNBase fcn;
    private MnUserParameters upar;
    private MnMigrad migrad;
    private ArrayList pars;
    
   JMinuitOptimizer()
   {
      result = new OptimizerResult();
      this.setConfiguration(new JMinuitConfiguration());
   }
   
    public void optimize()
   {
        
        pars = new ArrayList();
      
      // Load the function in Minuit. This will load all the variables.
      if ( function == null ) throw new IllegalArgumentException("Cannot optimize!! The function was not set correctly!");
      String[] variableNames = function.variableNames();
      if ( variableNames == null || variableNames.length == 0 ) throw new IllegalArgumentException("Cannot optimize!! There are no variable parameters in this function!");
      
      upar = new MnUserParameters();
      for ( int i = 0; i<variableNames.length; i++ )
      {
         String varName = variableNames[i];
         IVariableSettings varSet = variableSettings(varName);
         double value = varSet.value();
         if ( Double.isNaN(value) ) throw new IllegalArgumentException("No initial value set for variable "+varName);
         
         if ( varSet.isBound() )
            upar.add( varName, value, varSet.stepSize(), varSet.lowerBound(), varSet.upperBound() );
         else
            upar.add( varName, value, varSet.stepSize());
         if ( varSet.isFixed() ) upar.fix(varName);
         else pars.add(varName);
      }
      
      if (upar.variableParameters() == 0) throw new IllegalArgumentException("Cannot optimize!! There are no free variable registered in Minuit!");
      fcn = FunctionWrapper.create(function);
      migrad = new MnMigrad(fcn,upar); // FixMe: Use other minimizers
      migrad.setErrorDef(((JMinuitConfiguration)configuration).errorDef()); // FixMe: Need to set other parameters here
      migrad.setUseAnalyticalDerivatives(configuration.useFunctionGradient());      
//      migrad.setCheckAnalyticalDerivatives(false);
      
      min = migrad.minimize();
      
      if ( configuration().printLevel() <= IOptimizerConfiguration.NORMAL_OUTPUT )
          System.out.println(min.toString());
      
      double[] parameterVals = new double[variableNames.length];
      for ( int i = 0; i < parameterVals.length; i++ )
          parameterVals[i] = min.userParameters().value(i);
      result.setParameters(parameterVals);

      result.setOptimizationStatus(min.isValid() ? IOptimizerResult.CONVERGED : IOptimizerResult.NOT_CONVERGED);
      double[][] cov;

      if (min.isValid())
      {
         for ( int i = 0; i<variableNames.length; i++ )
         {
            String varName = variableNames[i];
            IVariableSettings varSet = variableSettings(varName);
            if ( ! varSet.isFixed() )
            {
               varSet.setValue(min.userParameters().value(varName));
               double err = min.userParameters().error(varName);
               if ( ! Double.isNaN(err) )
                   varSet.setStepSize(min.userParameters().error(varName));
            }
         }

         MnUserCovariance mat = min.userCovariance();
         
         if ( configuration().printLevel() <= IOptimizerConfiguration.NORMAL_OUTPUT )
             System.out.println(mat.toString());
         
         cov = new double[mat.nrow()][mat.nrow()];
         for (int i=0; i<mat.nrow(); i++)
         {
             for (int j=0; j<mat.nrow(); j++)
             {
                 cov[i][j] = mat.get(i,j);
             }
         }
      }
      else {
          cov = new double[pars.size()][pars.size()];
      }
      result.setCovarianceMatrix( cov );
      
   }
    
    public boolean acceptsConstraints() {
        return true;
    }
    
    public boolean canCalculateContours() {
        return true;
    }

    public double[][] calculateContour(String par1, String par2, int npts, double nSigmas) {
        //Optimizing is not necessary if we find a way to pass FunctionMinimum to the contour.
        optimize();

        MnContours contour = new MnContours(fcn, min);
        
        int px = -1;
        int py = -1;

        if ( pars.contains(par1) )
            px = pars.indexOf(par1);
        else
            throw new IllegalArgumentException("Parameter "+par1+" was not part of the fit.");
        
        if ( pars.contains(par2) )
            py = pars.indexOf(par2);
        else
            throw new IllegalArgumentException("Parameter "+par2+" was not part of the fit.");
        List points = contour.points(px, py, nSigmas, npts);
        
        double[][] result = new double[2][npts];
        for ( int i = 0; i < points.size(); i++ ) {
            Point p = (Point) points.get(i);
            result[0][i] = p.first;
            result[1][i] = p.second;            
        }
        return result;
    }
    
    public void reset() {
        migrad = null;
    }
    
}
