package hep.aida.ref.pdf;

import java.util.ArrayList;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 * A product of PDFs is not a PDF (unless none of the pdf shapres any dependents
 * with any other pdf).
 *
 */
public class Product extends Function {
    
    private Function[] functions;
    
    public Product(String name, Function f1, Function f2) {
        super(name);
        functions = new Function[] {f1,f2};
        initializeProduct(functions);
    }
    
    public Product(String name, ArrayList functionsArray) {
        super(name);
        initializeProduct(functionsArray);
    }
    
    private void initializeProduct(ArrayList functionsArray) {
        functions = new Function[functionsArray.size()];
        for ( int i = 0; i < functions.length; i++ )
            functions[i] = (Function) functionsArray.get(i);
        initializeProduct(functions);
    }
    
    private void initializeProduct(Function[] functions) {
        
        VariableList list = new VariableList();
        
        int nFunctions = functions.length;
        
        for ( int i = 0; i < nFunctions; i++ ) {
            Function f = functions[i];
            
            //When multiplying the functions have to be normalized.
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
            
        }
        addVariables(list);
    }
    
    public double functionValue() {
        double val = 1;
        for ( int i = 0; i < functions.length; i++ )
            val *= functions[i].functionValue();
        return val;
    }
    
    public boolean hasAnalyticalVariableGradient(Variable var) {
        return true;
    }
    
    public double evaluateAnalyticalVariableGradient(Variable var) {
        double val = 0;
        for ( int i = 0; i < functions.length; i++ ) {
            Function g = functions[i];
            double term = g.evaluateAnalyticalVariableGradient(var);
            for ( int j = 0; j < functions.length; j++ ) {
                if ( j != i) {
                    Function f = functions[i];
                    term *= f.functionValue();
                }
            }
            val += term;
        }
        return val;
    }
    
    public boolean hasAnalyticalNormalization(Dependent dep) {
        for ( int i = 0; i < functions.length; i++ ) {
            Function f = functions[i];
            if ( f.hasDependent(dep) ) {
                if ( ! f.hasAnalyticalNormalization(dep) )
                    return false;
                if ( i != functions.length - 1 ) {
                    for ( int j = i+1; j < functions.length; j++ ) {
                        Function g = functions[j];
                        if ( g.hasDependent(dep) )
                            return false;
                    }
                }
            } 
        }
        return false;
    }
    
    public double evaluateAnalyticalNormalization(Dependent dep) {
        for ( int i = 0; i < functions.length; i++ ) {
            Function f = functions[i];
            if ( f.hasDependent(dep) )
                return f.evaluateAnalyticalNormalization(dep);
        }
        return 0;
    }
    
    protected void updateNormalization() {
        for ( int i = 0; i < functions.length; i++ )
            functions[i].updateNormalization();
        super.updateNormalization();
    }
    
}
