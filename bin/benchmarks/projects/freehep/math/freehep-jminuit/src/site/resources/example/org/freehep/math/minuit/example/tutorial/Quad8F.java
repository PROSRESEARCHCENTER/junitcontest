package org.freehep.math.minuit.example.tutorial;

import org.freehep.math.minuit.FCNBase;

/**
 *
 * @version $Id: Quad8F.java 8584 2006-08-10 23:06:37Z duns $
 */
class Quad8F implements FCNBase
{
  public double valueOf(double[] par)
  {
    double x = par[0];
    double y = par[1];
    double z = par[2];
    double w = par[3];
    double x0 = par[4];
    double y0 = par[5];
    double z0 = par[6];
    double w0 = par[7];

    return ( (1./70.)*(21*x*x + 20*y*y + 19*z*z - 14*x*z - 20*y*z) + w*w + (1./70.)*(21*x0*x0 + 20*y0*y0 + 19*z0*z0 - 14*x0*z0 - 20*y0*z0) + w0*w0 );
  }
}
