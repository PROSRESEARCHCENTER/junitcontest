package hep.aida.ref.pdf;

import java.util.ArrayList;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class Sum extends Function {
    
    private Function[] functions;
    private Parameter[] fractions;
    private double[] fractionsValue;
    
    public Sum(String name, Function f1, Function f2) {
        this(name, f1, f2, null);
    }
    
    public Sum(String name, Function f1, Function f2, Parameter p) {
        super(name);
        functions = new Function[] {f1,f2};
        if ( p != null )
            fractions = new Parameter[] {p};
        else
            fractions = makeFractions(1);
        initializeSum(functions, fractions);
    }    
    
    public Sum(String name, ArrayList functionsArray) {
        this(name, functionsArray, null);
    }
    
    public Sum(String name, ArrayList functionsArray, ArrayList fractionsArray) {
        super(name);
        initializeSum(functionsArray, fractionsArray);        
    }

    private Parameter[] makeFractions(int size) {
        Parameter[] fractions = new Parameter[size];
        double val = 1./(size+1);
        for ( int i = 0; i < size; i++ )
            fractions[i] = new Parameter("f"+i,val,0,1);
        return fractions;
    }
    
    private void initializeSum(ArrayList functionsArray, ArrayList fractionsArray) {
        functions = new Function[functionsArray.size()];
        for ( int i = 0; i < functions.length; i++ )
            functions[i] = (Function) functionsArray.get(i);
        
        if ( fractionsArray == null )
            fractions = makeFractions(functionsArray.size()-1);
        else {
            fractions = new Parameter[fractionsArray.size()];
            for ( int i = 0; i < fractions.length; i++ )
                fractions[i] = (Parameter) fractionsArray.get(i);
        }
        
        initializeSum(functions, fractions);
    }
    
    private void initializeSum(Function[] functions, Parameter[] fractions) {
        if ( fractions.length != (functions.length-1) )
            throw new IllegalArgumentException("Invalid size for the provided objects. The number of fractions parameters should be one unit less than the size of the provided functions");
        int nFunctions = functions.length;
            
        VariableList list = new VariableList();

        for ( int i = 0; i < nFunctions; i++ ) {
            Function f = functions[i];
            
            //When adding the functions have to be normalized.
            f.normalize(true);
            
            for ( int j = 0; j < f.numberOfDependents(); j++ ) {
                Dependent dep = f.getDependent(j);
                if ( ! list.contains(dep) )
                    list.add(dep);
            }
            for ( int j = 0; j < f.numberOfParameters(); j++ ) {
                Parameter par = f.getParameter(j);
                if ( ! list.contains(par) )
                    list.add(par);
            }
            if ( i != 0 ) {
                Parameter fraction = fractions[i-1];
                if ( ! list.contains(fraction) )
                    list.add(fraction);
            }
        }        
        
        fractionsValue = new double[fractions.length+1];
        fractionsValue[0] = 1.;

        addVariables(list);
        
    }

    public void variableChanged(Variable var) {
        for ( int i = 0; i < fractions.length; i++ ) {
            if ( var == fractions[i] ) {
                fractionsValue[0] += fractionsValue[i+1];
                fractionsValue[i+1] = var.value();
                fractionsValue[0] -= fractionsValue[i+1];
                break;
            }
        }
    }

    public double functionValue() {
        double val = 0;
        for ( int i = 0; i < functions.length; i++ ) {
            val += functions[i].value()*fractionsValue[i];
        }
        return val;
    }
        
    public boolean hasAnalyticalVariableGradient(Variable var) {
        for ( int i = 0; i < functions.length; i++ ) {
            if ( ! functions[i].hasAnalyticalVariableGradient(var) )
                return false;
        }
        return true;
    }

    public double evaluateAnalyticalVariableGradient(Variable var) {
        double val = 0;
        int index = indexOfFraction(var);
        for ( int i = 0; i < functions.length; i++ ) {
            val += functions[i].evaluateAnalyticalVariableGradient(var)*fractionsValue[i];
        }
        if ( index != -1 ) {
            if ( index == 0 )
                throw new IllegalArgumentException();
            val += functions[index].value() - functions[0].value();
        }
        return val;
    }
    
    private int indexOfFraction(Variable par) {
        for ( int i = 0; i < fractions.length; i++ )
            if ( fractions[i] == par )
                return i+1;
        return -1;
    }
    
    public boolean hasAnalyticalNormalization(Dependent dep) {
        for ( int i = 0; i < functions.length; i++ ) {
            if ( ! functions[i].hasAnalyticalNormalization(dep) )
                return false;
        }
        return true;
    }
    
    public double evaluateAnalyticalNormalization(Dependent dep) {
        return 1;
    }
    
    protected void updateNormalization() {
        for ( int i = 0; i < functions.length; i++ )
            functions[i].updateNormalization();
        super.updateNormalization();
    }
    
    public int nAddend() {
        return functions.length;
    }
    
    public Function addend(int index) {
        if ( index < 0 || index > functions.length - 1 )
            throw new IllegalArgumentException("Illegal index value "+index);
        return functions[index];
    }
    
    public double fraction(int index) {
        if ( index < 0 || index > functions.length - 1 )
            throw new IllegalArgumentException("Illegal index value "+index);
        return fractionsValue[index];        
    }
}
