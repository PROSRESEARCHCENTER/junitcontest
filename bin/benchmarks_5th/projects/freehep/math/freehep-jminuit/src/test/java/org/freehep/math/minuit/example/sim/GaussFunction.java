package org.freehep.math.minuit.example.sim;

/**
 *
 * @version $Id: GaussFunction.java 8584 2006-08-10 23:06:37Z duns $
 */
class GaussFunction
{
  GaussFunction(double mean, double sig, double constant)
  {
    theMean = mean;
    theSigma = sig;
    theConstant = constant;
  }

  double m() {return theMean;}
  double s() {return theSigma;}
  double c() {return theConstant;}

  double valueAt(double x) 
  {  
    return c()*Math.exp(-0.5*(x-m())*(x-m())/(s()*s()))/(Math.sqrt(2.*Math.PI)*s());
  }
  
  private double theMean;
  private double theSigma;
  private double theConstant;
}
