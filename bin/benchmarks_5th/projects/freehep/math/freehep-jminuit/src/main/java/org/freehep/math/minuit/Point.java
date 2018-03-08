package org.freehep.math.minuit;

/**
 * A class representing a pair of double (x,y) or (lower,upper)
 * @version $Id: Point.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Point
{
   public Point(double first, double second)
   {
      this.first = first;
      this.second = second;
   }
   public double first;
   public double second;
}
