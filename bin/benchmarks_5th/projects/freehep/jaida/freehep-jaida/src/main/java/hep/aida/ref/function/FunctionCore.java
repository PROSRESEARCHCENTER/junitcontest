/*
 * FunctionCore.java
 *
 * Created on September 4, 2002, 6:15 AM
 */

package hep.aida.ref.function;
import jas.hist.Handle;

import java.util.ArrayList;


/**
 *
 * @author  serbo
 */

// Dimension, numberOfParameters are set at the constructor of a concrete class

public abstract class FunctionCore {

    protected String title;
    protected int dimension;
    protected int numberOfParameters;
    protected double[] p;
    protected String[] parNames;

    private ArrayList listeners = new ArrayList();
    
    public FunctionCore(int dim, int nPar) {
	init(dim, nPar);
    }

    public FunctionCore(int dim, int nPar, double[] parVal) {
	if (parVal != null && nPar != parVal.length)
	    throw new IllegalArgumentException("Number of parameters ("+nPar+") is different from number of parameter values ("+parVal.length+")");
	init(dim, nPar);
	if (parVal != null) {
	    for (int i=0; i<nPar; i++) { 
		p[i] = parVal[i];
	    }	
	}
    }

    protected void init(int dim, int nPar) {
	dimension = dim;
	numberOfParameters = nPar;
	p = new double[nPar];
	parNames = new String[nPar];
	for (int i=0; i<nPar; i++) { 
	    parNames[i] = "p" + i;
	    p[i] = 1.;
	}	
    }

    public int dimension() { return dimension; }

    public String title() { return title; }

    public void setTitle(String t) { title = t; }


    // Value of the function WITHOUT Normalization factor (as if N=1)
    public abstract double functionValue(double[] var);


    // Each concrete FunctionCore has to implement those methods that deal with Gradients and Normalization
    public abstract boolean providesGradient();

    public abstract double[] gradient(double[] x);

    public abstract boolean providesParameterGradient();

    public abstract double[] parameterGradient(double[] x);

    public abstract boolean providesNormalization();

    public abstract double normalizationAmplitude(double[] xMin, double[] xMax);


    // Deal with parameters
    public int numberOfParameters() { return numberOfParameters; }

    public String[] parameterNames() { return parNames; }

    public double[] parameters() { return p; }

    public double parameter(String name) { return p[indexOfParameter(name)]; } 

    public boolean setParameterNames(String[] params) { 
	for (int i=0; i<numberOfParameters(); i++) { parNames[i] = params[i]; }
	return true;
    }

    public int indexOfParameter(String name) {
	for (int i=0; i<numberOfParameters(); i++) {
	    if (name.equals(parNames[i])) return i;
	}
	throw new IllegalArgumentException("Function \""+title()+"\" does not have variable named \"" + name + "\"");
    }

    public void setParameters(double[] params) {
//        System.out.println("************* Setting parameters "+params+" "+p);
//	System.out.print("\nSetting parameters:  ");
//	for (int i=0; i<numberOfParameters(); i++) { System.out.print(params[i]+"   "); }
//	System.out.print("\n");
	for (int i=0; i<numberOfParameters(); i++) { p[i] = params[i]; }
    }

    public void setParameter(String name, double x) throws IllegalArgumentException { 
	p[indexOfParameter(name)] = x;
	//System.out.println("Settong parameter:  "+name+" = "+x);
    }

    public Handle[] getHandles(double xLow, double xHigh, double yLow, double yHigh) {
        return null;
    }
    
    public void addCoreListener( FunctionCoreListener listener ) {
        listeners.add( listener );
    }
    
    void notifyCoreChanged() {
        for ( int i = 0; i < listeners.size(); i++ )
            ( (FunctionCoreListener) listeners.get(i) ).coreChanged();
    }
        
        
}
