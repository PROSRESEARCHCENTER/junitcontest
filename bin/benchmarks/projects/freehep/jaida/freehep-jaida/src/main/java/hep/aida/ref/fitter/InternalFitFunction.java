/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hep.aida.ref.fitter;

import hep.aida.IAnnotation;
import hep.aida.IFunction;
import hep.aida.IModelFunction;
import hep.aida.dev.IDevFitDataIterator;
import hep.aida.ext.IFitMethod;
import java.util.ArrayList;

public class InternalFitFunction implements IFunction {

    private IDevFitDataIterator dataIterator;
    private IModelFunction func;
    private ArrayList varSimpleConstraint1;
    private ArrayList varSimpleConstraint2;
    private IFitMethod fitMethod;

    public InternalFitFunction(IDevFitDataIterator dataIterator, IModelFunction func, IFitMethod fitMethod) {
        this.dataIterator = dataIterator;
        this.func = func;
        varSimpleConstraint1 = new ArrayList();
        varSimpleConstraint2 = new ArrayList();
        this.fitMethod = fitMethod;
    }

    public int dimension() {
        return func.numberOfParameters();
    }

    public double value(double[] x) {
        if (varSimpleConstraint1.size() != 0) {
            applySimpleConstraint(x);
        }
        func.setParameters(x);
        return fitMethod.evaluate(dataIterator, func);
    }

    public boolean providesGradient() {
        return func.providesParameterGradient();
    }

    public String variableName(int i) {
        return func.parameterNames()[i];
    }

    public String[] variableNames() {
        return func.parameterNames();
    }

    public int numberOfParameters() {
        return 0;
    }

    public double[] gradient(double[] x) {
        if (varSimpleConstraint1.size() != 0) {
            applySimpleConstraint(x);
        }
        func.setParameters(x);
        double[] result = fitMethod.evaluateGradient(dimension(), dataIterator, func);
        return result;
    }

    public boolean isEqual(IFunction f) {
        throw new UnsupportedOperationException();
    }

    public IAnnotation annotation() {
        throw new UnsupportedOperationException();
    }

    public String codeletString() {
        throw new UnsupportedOperationException();
    }

    public void setParameters(double[] params) {
        throw new UnsupportedOperationException();
    }

    public double[] parameters() {
        throw new UnsupportedOperationException();
    }

    public int indexOfParameter(String name) {
        throw new UnsupportedOperationException();
    }

    public String[] parameterNames() {
        throw new UnsupportedOperationException();
    }

    public void setParameter(String name, double x) {
        throw new UnsupportedOperationException();
    }

    public double parameter(String name) {
        throw new UnsupportedOperationException();
    }

    public void setTitle(String str) {
        throw new UnsupportedOperationException();
    }

    public String title() {
        throw new UnsupportedOperationException();
    }

    public String normalizationParameter() {
        throw new UnsupportedOperationException();
    }


    protected int dataEntries() {
        return dataIterator.entries();
    }

    protected int indexOfVariable(String varName) {
        return func.indexOfParameter(varName);
    }

    protected void setSimpleConstraint(String varName1, String varName2) {
        int ind1 = indexOfVariable(varName1);
        int ind2 = indexOfVariable(varName2);
        if (ind1 > -1 && ind2 > -1) {
            varSimpleConstraint1.add(new Integer(ind1));
            varSimpleConstraint2.add(new Integer(ind2));
        }
    }

    protected boolean isValidSimpleConstraint(String varName1, String varName2) {
        int ind1 = indexOfVariable(varName1);
        int ind2 = indexOfVariable(varName2);
        if (ind1 > -1 && ind2 > -1) {
            return true;
        }
        return false;
    }

    protected void applySimpleConstraint(double[] x) {
        for (int i = 0; i < varSimpleConstraint1.size(); i++) {
            int ind1 = ((Integer) varSimpleConstraint1.get(i)).intValue();
            int ind2 = ((Integer) varSimpleConstraint2.get(i)).intValue();
            x[ind1] = x[ind2];
        }
    }
}
