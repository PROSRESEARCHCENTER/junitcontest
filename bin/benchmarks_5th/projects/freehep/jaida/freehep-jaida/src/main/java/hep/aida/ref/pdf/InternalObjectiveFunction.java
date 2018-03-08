/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hep.aida.ref.pdf;

import hep.aida.IFitData;
import hep.aida.IFunction;
import hep.aida.dev.IDevFitData;
import hep.aida.dev.IDevFitDataIterator;
import hep.aida.ext.IFitMethod;
import java.util.ArrayList;

    public class InternalObjectiveFunction implements IFunction {

        private ArrayList vars = new ArrayList();
        private Function[] functions;
        private IDevFitDataIterator[] iters;
        private IFitMethod fitMethod;

        public InternalObjectiveFunction(IFitData[] data, Function[] functions, IFitMethod fitMethod) {
            this.functions = functions;
            this.iters = new IDevFitDataIterator[data.length];
            this.fitMethod = fitMethod;

            for ( int i = 0; i < functions.length; i++ ) {

                iters[i] = ( (IDevFitData) data[i] ).dataIterator();
                Function f = functions[i];
                for ( int j = 0; j < f.numberOfParameters(); j++ ) {
                    Parameter p = f.getParameter(j);
                    if ( ! vars.contains(p) )
                        vars.add(p);
                }
            }
        }

        public String normalizationParameter() {
            throw new UnsupportedOperationException();
        }

        public Parameter getVariable(int index) {
            return (Parameter) vars.get(index);
        }

        public double value(double[] values) {
            double value = 0;

            for ( int i = 0; i < vars.size(); i++ )
                ( (Parameter) vars.get(i) ).setValue(values[i]);

            for ( int i = 0; i < functions.length; i++ ) {
                Function f = functions[i];
                value += fitMethod.evaluate(iters[i],f);
            }
            return value;
        }

        public boolean providesGradient() {
            for ( int i = 0; i < functions.length; i++ ) {
                Function f = functions[i];
                for ( int j = 0; j < f.numberOfParameters(); j++ ) {
                    if ( ! f.providesGradientWithRespectToVariable(f.getParameter(i) ) )
                        return false;
                }
            }
            return true;
        }

        public int dimension() {
            return vars.size();
        }

        public double[] gradient(double[] values) {
            double[] grad = new double[vars.size()];
            for ( int i = 0; i < vars.size(); i++ )
                ( (Parameter) vars.get(i) ).setValue(values[i]);

            for ( int i = 0; i < functions.length; i++ ) {
                Function f = functions[i];
                int fPars = f.numberOfParameters();
                double[] g =  fitMethod.evaluateGradient(fPars, iters[i], f);
                for ( int j = 0; j < fPars; j++ ) {
                    Parameter p = f.getParameter(j);
                    grad[ vars.indexOf(p) ] += g[j];
                }
            }
            return grad;
        }

        public int numberOfParameters() {
            return 0;
        }

        public String variableName(int index) {
            return ( (Parameter) vars.get(index) ).name();
        }

        public String[] variableNames() {
            String[] names = new String[vars.size()];
            for ( int i = 0; i < names.length; i++ )
                names[i] = ( (Parameter) vars.get(i) ).name();
            return names;
        }

        public hep.aida.IAnnotation annotation() {
            throw new UnsupportedOperationException();
        }

        public String codeletString() {
            throw new UnsupportedOperationException();
        }

        public int indexOfParameter(String str) {
            throw new UnsupportedOperationException();
        }

        public boolean isEqual(hep.aida.IFunction iFunction) {
            throw new UnsupportedOperationException();
        }

        public double parameter(String str) {
            throw new UnsupportedOperationException();
        }

        public String[] parameterNames() {
            throw new UnsupportedOperationException();
        }

        public double[] parameters() {
            throw new UnsupportedOperationException();
        }

        public void setParameter(String str, double param) throws java.lang.IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public void setParameters(double[] values) throws java.lang.IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public void setTitle(String str) throws java.lang.IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public String title() {
            throw new UnsupportedOperationException();
        }

    }
