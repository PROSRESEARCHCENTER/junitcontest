package org.freehep.math.minuit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * API class for Contours error analysis (2-dim errors).
 * Minimization has to be done before and minimum must be valid.
 * Possibility to ask only for the points or the points and associated Minos
 * errors.
 * @version $Id: MnContours.java 16142 2014-09-05 02:52:34Z tonyj $
 */
public class MnContours
{
   /** construct from FCN + minimum */
   public MnContours(FCNBase fcn, FunctionMinimum min)
   {
      this(fcn,min,MnApplication.DEFAULT_STRATEGY);
   }
   
   /** construct from FCN + minimum + strategy */
   public MnContours(FCNBase fcn, FunctionMinimum min, int stra)
   {
      this(fcn,min,new MnStrategy(stra));
   }
   
   /** construct from FCN + minimum + strategy */
   public MnContours(FCNBase fcn,  FunctionMinimum min, MnStrategy stra)
   {
      theFCN = fcn;
      theMinimum = min;
      theStrategy = stra;
   }
   public List<Point> points(int px,  int py)
   {
      return points(px,py,1);
   }
   public List<Point> points(int px,  int py, double errDef)
   {
      return points(px,py,errDef,20);
   }   
   /**
    * Calculates one function contour of FCN with respect to parameters
    * parx and pary. The return value is a list of (x,y)
    * points. FCN minimized always with respect to all other n - 2 variable parameters
    * (if any). MINUIT will try to find n points on the contour (default 20). To
    * calculate more than one contour, the user needs to set the error definition in
    * its FCN to the appropriate value for the desired confidence level and call this method for each contour.
    */
   public List<Point> points(int px,  int py, double errDef, int npoints)
   {
      ContoursError cont = contour(px, py, errDef, npoints);
      return cont.points();
   }
   
   public ContoursError contour(int px,  int py)
   {
      return contour(px,py,1);
   }
   public ContoursError contour(int px,  int py, double errDef)
   {
      return contour(px,py,errDef,20);
   }
   /**
    * Causes a CONTOURS error analysis and returns the result in form of ContoursError. As
    * a by-product ContoursError keeps the MinosError information of parameters parx and
    * pary. The result ContoursError can be easily printed using MnPrint or toString().
    */
   public ContoursError contour(int px, int py, double errDef, int npoints)
   {
      errDef *= theMinimum.errorDef();
      assert(npoints > 3);
      int maxcalls = 100*(npoints+5)*(theMinimum.userState().variableParameters()+1);
      int nfcn = 0;
      
      List<Point> result = new ArrayList<Point>(npoints);
      List<MnUserParameterState> states = new ArrayList<MnUserParameterState>();
      double toler = 0.05;
      
      //get first four points
      MnMinos minos = new MnMinos(theFCN, theMinimum, theStrategy);
      
      double valx = theMinimum.userState().value(px);
      double valy = theMinimum.userState().value(py);
      
      MinosError mex = minos.minos(px,errDef);
      nfcn += mex.nfcn();
      if(!mex.isValid())
      {
         logger.info("MnContours is unable to find first two points.");
         return new ContoursError(px, py, result, mex, mex, nfcn);
      }
      Point ex = mex.range();
      
      MinosError mey = minos.minos(py,errDef);
      nfcn += mey.nfcn();
      if(!mey.isValid())
      {
         logger.info("MnContours is unable to find second two points.");
         return new ContoursError(px, py, result, mex, mey, nfcn);
      }
      Point ey = mey.range();
      
      MnMigrad migrad = new MnMigrad(theFCN, theMinimum.userState().clone(), new MnStrategy(Math.max(0, theStrategy.strategy()-1)));
      
      migrad.fix(px);
      migrad.setValue(px, valx + ex.second);
      FunctionMinimum exy_up = migrad.minimize();
      nfcn += exy_up.nfcn();
      if(!exy_up.isValid())
      {
         logger.log(Level.INFO, "MnContours is unable to find upper y value for x parameter {0}.", px);
         return new ContoursError(px, py, result, mex, mey, nfcn);
      }
      
      migrad.setValue(px, valx + ex.first);
      FunctionMinimum exy_lo = migrad.minimize();
      nfcn += exy_lo.nfcn();
      if(!exy_lo.isValid())
      {
         logger.log(Level.INFO, "MnContours is unable to find lower y value for x parameter {0}.", px);
         return new ContoursError(px, py, result, mex, mey, nfcn);
      }
      
      MnMigrad migrad1 = new MnMigrad(theFCN, theMinimum.userState().clone(), new MnStrategy(Math.max(0, theStrategy.strategy()-1)));
      migrad1.fix(py);
      migrad1.setValue(py, valy + ey.second);
      FunctionMinimum eyx_up = migrad1.minimize();
      nfcn += eyx_up.nfcn();
      if(!eyx_up.isValid())
      {
         logger.log(Level.INFO, "MnContours is unable to find upper x value for y parameter {0}.", py);
         return new ContoursError(px, py, result, mex, mey, nfcn);
      }
      
      migrad1.setValue(py, valy + ey.first);
      FunctionMinimum eyx_lo = migrad1.minimize();
      nfcn += eyx_lo.nfcn();
      if(!eyx_lo.isValid())
      {
         logger.log(Level.INFO, "MnContours is unable to find lower x value for y parameter {0}.", py);
         return new ContoursError(px, py, result, mex, mey, nfcn);
      }
      
      double scalx = 1./(ex.second - ex.first);
      double scaly = 1./(ey.second - ey.first);
      
      result.add(new Point(valx + ex.first, exy_lo.userState().value(py)));
      result.add(new Point(eyx_lo.userState().value(px), valy + ey.first));
      result.add(new Point(valx + ex.second, exy_up.userState().value(py)));
      result.add(new Point(eyx_up.userState().value(px), valy + ey.second));
      
      MnUserParameterState upar = theMinimum.userState().clone();
      upar.fix(px);
      upar.fix(py);
      
      int[] par = { px, py};
      MnFunctionCross cross = new MnFunctionCross(theFCN, upar, theMinimum.fval(), theStrategy, errDef);
      
      for (int i = 4; i < npoints; i++)
      {
         Point idist1 = result.get(result.size()-1);
         Point idist2 = result.get(0);
         int pos2 = 0;
         double distx = idist1.first - idist2.first;
         double disty = idist1.second - idist2.second;
         double bigdis = scalx*scalx*distx*distx + scaly*scaly*disty*disty;
         
         for(int j=0; j < result.size()-1; j++)
         {
            Point ipair = result.get(j);
            double distx2 = ipair.first - result.get(j+1).first;
            double disty2 = ipair.second - result.get(j+1).second;
            double dist = scalx*scalx*distx2*distx2 + scaly*scaly*disty2*disty2;
            if(dist > bigdis)
            {
               bigdis = dist;
               idist1 = ipair;
               idist2 = result.get(j+1);
               pos2 = j+1;
            }
         }
         
         double a1 = 0.5;
         double a2 = 0.5;
         double sca = 1.;
         
         for (;;)
         {
            if(nfcn > maxcalls)
            {
               logger.info("MnContours: maximum number of function calls exhausted.");
               return new ContoursError(px, py, result, mex, mey, nfcn);
            }
            
            double xmidcr = a1*idist1.first + a2*idist2.first;
            double ymidcr = a1*idist1.second + a2*idist2.second;
            double xdir = idist2.second - idist1.second;
            double ydir = idist1.first - idist2.first;
            double scalfac = sca*Math.max(Math.abs(xdir*scalx), Math.abs(ydir*scaly));
            double xdircr = xdir/scalfac;
            double ydircr = ydir/scalfac;
            double[] pmid = { xmidcr, ymidcr };
            double[] pdir = { xdircr, ydircr };
            
            MnCross opt = cross.cross(par, pmid, pdir, toler, maxcalls);
            nfcn += opt.nfcn();
            if(opt.isValid())
            {
               double aopt = opt.value();
               if (pos2 == 0)
               {
                  result.add(new Point(xmidcr+(aopt)*xdircr, ymidcr + (aopt)*ydircr));                  
               }
               else
               {
                  result.add(pos2, new Point(xmidcr+(aopt)*xdircr, ymidcr + (aopt)*ydircr));
               }
               break;
            }
            if(sca < 0.)
            {
               logger.log(Level.INFO, "MnContours is unable to find point {0} on contour.", (i+1));
               logger.log(Level.INFO, "MnContours finds only {0} points.", i);
               return new ContoursError(px, py, result, mex, mey, nfcn);
            }
            sca = -1.;  
         }
      }
      
      return new ContoursError(px, py, result, mex, mey, nfcn);
   }
   
   MnStrategy strategy()
   {
      return theStrategy;
   }
  
   private FCNBase theFCN;
   private FunctionMinimum theMinimum;
   private MnStrategy theStrategy;
   private static final Logger logger = Logger.getLogger(MnContours.class.getName());
}