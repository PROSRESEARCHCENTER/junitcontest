package org.freehep.math.minuit;

/** parabola = a*xx + b*x + c
 * @version $Id: MnParabola.java 8584 2006-08-10 23:06:37Z duns $
 */
class MnParabola
{

  MnParabola(double a, double b, double c)
  {
     theA = a;
     theB = b;
     theC = c;
  }

  double y(double x) {return (theA*x*x + theB*x +theC);}
  double x_pos(double y) {return (Math.sqrt(y/theA + min()*min() - theC/theA) + min());}
  double x_neg(double y) {return (-Math.sqrt(y/theA + min()*min() - theC/theA) + min());}
  double min() {return -theB/(2.*theA);}
  double ymin() {return (-theB*theB/(4.*theA) + theC);}

  double a() {return theA;}
  double b() {return theB;}
  double c() {return theC;}

  private double theA;
  private double theB;
  private double theC;
   
}
