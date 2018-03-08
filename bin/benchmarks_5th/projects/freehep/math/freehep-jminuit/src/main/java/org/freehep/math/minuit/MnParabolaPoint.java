package org.freehep.math.minuit;

/**
 *
 * @version $Id: MnParabolaPoint.java 8584 2006-08-10 23:06:37Z duns $
 */
class MnParabolaPoint
{
  MnParabolaPoint(double x, double y)
  {
     theX = x;
     theY = y;
  }

  double x() 
  {
     return theX;
  }

  double y() 
  {
     return theY;
  }

  private double theX;
  private double theY;
}
