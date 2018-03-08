package hep.aida.ref.fitter;

import hep.aida.IFitParameterSettings;
import hep.aida.IFunction;
import hep.aida.dev.IDevFitResult;

import java.util.Hashtable;

/**
 * @author The AIDA team @ SLAC.
 *
 */

public class FitResult implements IDevFitResult {
    
    private String[] constraints = null;
    private String dataDescription = "";
    private String engineName = "";
    private String fitMethod = "";
    private Hashtable parSetHash = new Hashtable();
    private int fitStatus = -1;
    private IFunction function = null;
    private boolean isValid = false;
    private int nDof = -1;
    private double quality = Double.NaN;
    private double[][] covMatrix;
    private double seconds;
    
    public FitResult( int dim ) {
        this( dim, Double.NaN );
    }
    
    public FitResult( int dim, double seconds ) {
        covMatrix = new double[dim][dim];
        this.seconds = seconds;
    }
    
    public String[] constraints() {
        return constraints;
    }
    
    public double covMatrixElement(int i, int j) {
        return covMatrix[i][j];
    }
    
    public String dataDescription() {
        return dataDescription;
    }
    
    public String engineName() {
        return engineName;
    }
    
    public String fitMethodName() {
        return fitMethod;
    }
    
    public IFitParameterSettings fitParameterSettings(String parName) {
        return (IFitParameterSettings) parSetHash.get(parName);
    }
    
    public int fitStatus() {
        return fitStatus;
    }
    
    public IFunction fittedFunction() {
        return function;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public int ndf() {
        return nDof;
    }
    
    public double quality() {
        return quality;
    }
    
    public void setConstraints(String[] constraints) {
        this.constraints = constraints;
    }
    
    public void setCovMatrixElement(int xCol, int yCol, double covEl) {
        covMatrix[xCol][yCol] = covEl;
    }
    
    public void setDataDescription(String dataDescription) {
        this.dataDescription = dataDescription;
    }
    
    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }
    
    public void setFitMethodName(String fitMethod) {
        this.fitMethod = fitMethod;
    }
    
    public void setFitParameterSettings(String parName, IFitParameterSettings parSetting) {
        parSetHash.put(parName,parSetting);
    }
    
    public void setFitStatus(int fitStatus) {
        this.fitStatus = fitStatus;
    }
    
    public void setFittedFunction(IFunction function) {
        this.function = function;
    }
    
    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }
    
    public void setNdf(int nDof) {
        this.nDof = nDof;
    }
    
    public void setQuality(double quality) {
        this.quality = quality;
    }

    
    
    public double[] fittedParameters() {
        return function.parameters();
    }
    
    public String[] fittedParameterNames() {
        return function.parameterNames();
    }
    
    public double fittedParameter(String name) {
        return function.parameter(name);
    }
    
    public double[] errors() {
        int dim = function.numberOfParameters();
        double[] errors = new double[dim];
        for( int i = 0; i < dim; i++ ) 
            errors[i] = Math.sqrt(covMatrixElement(i,i));
        return errors;
    }
        
    public double[] errorsPlus() {
        return errors();
    }
    
    public double[] errorsMinus() {
        return errors();
    }
    
    private String fitStatusToString() {
        switch ( fitStatus ) {
            case 1 :
                return "Diagonal approximation";
            case 2 :
                return "Converged with non positive definite matrix";
            case 3 :
                return "Converged";
            case 4 :
                return "Converged with small gradient";
            case 5 :
                return "Converged with small step size";
            case 6 :
                return "Not Converged";
            case 7 :
                return "Stopped, reached max iterations";
            case 8 :
                return "Stopped, too many large steps. Function might be unbound.";
            default :
                return "Undefined";                
        }
    }
    
    public void printResult() {
        String[] parNames = function.parameterNames();

        System.out.println("************************************************");
        System.out.println("*  Performed "+fitMethodName()+" fit using "+engineName());
        System.out.println("*  Data Set "+dataDescription());
        System.out.println("*  Function "+function.codeletString());
        System.out.println("************************************************");
        System.out.println("*  Performance  *");
        System.out.println("*****************");        
        System.out.println("*  Status  : "+fitStatusToString());
        System.out.println("*  Quality : "+quality());
        System.out.println("*  nDoF    : "+ndf());        
        if ( ! Double.isNaN(seconds) ) System.out.println("*  Time    : "+seconds+" seconds");
        System.out.println("************************************************");
        System.out.println("*  Parameters  *");
        System.out.println("****************");
                
        for( int i = 0; i < parNames.length; i++ ) {
            String parName = parNames[i];
            double parVal = function.parameter(parName);
            IFitParameterSettings fitPar = fitParameterSettings(parName);
            if (fitPar == null) continue;
            if ( ! fitPar.isFixed() ) {
                double err = Math.sqrt(covMatrixElement(i,i));
                if ( err != 0 ) System.out.println("*  "+parName+" : \t "+parVal+"\t "+err);
                else  System.out.println("*  "+parName+" : \t "+parVal+"\t Constrained");
            } else 
                System.out.println("*  "+parName+" : \t "+parVal+"\t Fixed");
        }
        System.out.println("************************************************");
        System.out.println("*  Covariance Matrix  *");
        System.out.println("***********************");
        System.out.print("*  \t");
        for( int i = 0; i < parNames.length; i++ ) System.out.print( parNames[i]+"\t");
        System.out.println();
        for( int i = 0; i < parNames.length; i++ ) {
            System.out.print("*  "+parNames[i]+"\t");
            for ( int j = 0; j < parNames.length; j++ ) {
                double cov = covMatrixElement(i,j);
                if ( cov == 0 ) System.out.print("\t");
                else System.out.print(cov+" ");
            }
            System.out.println();
        }
        System.out.println("************************************************\n\n");
        
    }
}

