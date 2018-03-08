package org.freehep.math.minuit;

import java.util.List;

/**
 *
 * @version $Id: ContoursError.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ContoursError
{
   ContoursError(int parx, int pary, List<Point> points,  MinosError xmnos,  MinosError ymnos,  int nfcn)
   {
      theParX = parx;
      theParY = pary;
      thePoints = points;
      theXMinos = xmnos;
      theYMinos = ymnos;
      theNFcn = nfcn;
   }
   
   public List<Point> points()
   {
      return thePoints;
   }
   
   public Point xRange()
   {
      return theXMinos.range();
   }
   
   public Point yRange()
   {
      return theYMinos.range();
   }
   
   public int xpar()
   {
      return theParX;
   }
   public int ypar()
   {
      return theParY;
   }
   
   public MinosError xMinosError()
   {
      return theXMinos;
   }
   
   public MinosError yMinosError()
   {
      return theYMinos;
   }
   
   public int nfcn()
   {
      return theNFcn;
   }
   public double xmin()
   {
      return theXMinos.min();
   }
   public double ymin()
   {
      return theYMinos.min();
   }
   public String toString()
   {
      return MnPrint.toString(this);
   }    
   private int theParX;
   private int theParY;
   private List<Point> thePoints;
   private MinosError theXMinos;
   private MinosError theYMinos;
   private int theNFcn;
}