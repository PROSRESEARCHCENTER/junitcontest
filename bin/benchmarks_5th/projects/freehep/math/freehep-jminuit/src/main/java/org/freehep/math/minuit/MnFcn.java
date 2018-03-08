package org.freehep.math.minuit;

/**
 *
 * @version $Id: MnFcn.java 8584 2006-08-10 23:06:37Z duns $
 */
class MnFcn
{
  MnFcn(FCNBase fcn, double errorDef)
  {
     theFCN = fcn;
     theNumCall = 0;
     theErrorDef = errorDef;
  }

  double valueOf(MnAlgebraicVector v)
  {
     theNumCall++;
     return theFCN.valueOf(v.asArray());
  }
  int numOfCalls() {return theNumCall;}

  double errorDef()
  {
     return theErrorDef;
  }
  FCNBase fcn() {return theFCN;}

  private FCNBase theFCN;
  protected int theNumCall;
  private double theErrorDef;
}
