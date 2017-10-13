package hep.aida.ref.function;

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.IFitFactory;
import hep.aida.IFitResult;
import hep.aida.IFitter;
import hep.aida.IFunction;
import hep.aida.IFunctionFactory;
import hep.aida.IPlotter;
import hep.aida.ITree;
import hep.aida.ref.function.AbstractIFunction;
import java.util.Random;

public class UserFunction extends AbstractIFunction {
    
    public UserFunction() {
        this("");
    }
    
    public UserFunction(String title) {
        super(title, 1, 2);
    }
    
    public UserFunction(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }

        public double value(double[] v) {
        //return p[0]*v[0]*v[0]+p[1]*v[0]+p[2];
        return p[0]+p[1]*v[0];
    }
       
    public static void main(String[] args) throws Exception {
        System.out.println("UserFunction");
    }
}
