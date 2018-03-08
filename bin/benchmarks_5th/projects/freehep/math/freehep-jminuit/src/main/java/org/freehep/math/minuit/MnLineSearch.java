package org.freehep.math.minuit;

/**
 *
 * @version $Id: MnLineSearch.java 8584 2006-08-10 23:06:37Z duns $
 */
abstract class MnLineSearch
{
   static MnParabolaPoint search(MnFcn fcn, MinimumParameters st, MnAlgebraicVector step, double gdel, MnMachinePrecision prec)
   {      
      double overal = 1000.;
      double undral = -100.;
      double toler = 0.05;
      double slamin = 0.;
      double slambg = 5.;
      double alpha = 2.;
      int maxiter = 12;
      int niter = 0;
      
      for(int i = 0; i < step.size(); i++)
      {
         if(Math.abs(step.get(i)) < prec.eps()) continue;
         double ratio = Math.abs(st.vec().get(i)/step.get(i));
         if(Math.abs(slamin) < prec.eps()) slamin = ratio;
         if(ratio < slamin) slamin = ratio;
      }
      if(Math.abs(slamin) < prec.eps()) slamin = prec.eps();
      slamin *= prec.eps2();
      
      double F0 = st.fval();
      double F1 = fcn.valueOf(MnUtils.add(st.vec(),step));
      double fvmin = st.fval();
      double xvmin = 0.;
      
      if(F1 < F0)
      {
         fvmin = F1;
         xvmin = 1.;
      }
      double toler8 = toler;
      double slamax = slambg;
      double flast = F1;
      double slam = 1.;
      
      boolean iterate = false;
      MnParabolaPoint p0 = new MnParabolaPoint(0., F0);
      MnParabolaPoint p1 = new MnParabolaPoint(slam, flast);
      double F2 = 0.;
      do
      {
         // cut toler8 as function goes up
         iterate = false;
         MnParabola pb = MnParabolaFactory.create(p0, gdel, p1);
         double denom = 2.*(flast-F0-gdel*slam)/(slam*slam);
         if(Math.abs(denom) < prec.eps())
         {
            denom = -0.1*gdel;
            slam = 1.;
         }
         if(Math.abs(denom) > prec.eps()) slam = -gdel/denom;
         if(slam < 0.) slam = slamax;

         if(slam > slamax) slam = slamax;
         if(slam < toler8) slam = toler8;
         if(slam < slamin)
         {
            return new MnParabolaPoint(xvmin, fvmin);
         }
         if(Math.abs(slam - 1.) < toler8 && p1.y() < p0.y())
         {
            return new MnParabolaPoint(xvmin, fvmin);
         }
         if(Math.abs(slam - 1.) < toler8) slam = 1. + toler8;
         
         F2 = fcn.valueOf(MnUtils.add(st.vec(),MnUtils.mul(step,slam)));
         if(F2 < fvmin)
         {
            fvmin = F2;
            xvmin = slam;
         }
         if(p0.y()-prec.eps() < fvmin && fvmin < p0.y()+prec.eps())
         {
            iterate = true;
            flast = F2;
            toler8 = toler*slam;
            overal = slam - toler8;
            slamax = overal;
            p1 = new MnParabolaPoint(slam, flast);
            niter++;
         }
      } 
      while(iterate && niter < maxiter);
      
      if(niter >= maxiter)
      {
         // exhausted max number of iterations
         return new MnParabolaPoint(xvmin, fvmin);
      }
      
      MnParabolaPoint p2 = new MnParabolaPoint(slam, F2);
      
      do
      {
         slamax = Math.max(slamax, alpha*Math.abs(xvmin));
         MnParabola pb = MnParabolaFactory.create(p0, p1, p2);
         if(pb.a() < prec.eps2())
         {
            double slopem = 2.*pb.a()*xvmin + pb.b();
            if(slopem < 0.) slam = xvmin + slamax;
            else slam = xvmin - slamax;
         } 
         else
         {
            slam = pb.min();
            if(slam > xvmin + slamax) slam = xvmin + slamax;
            if(slam < xvmin - slamax) slam = xvmin - slamax;
         }
         if(slam > 0.)
         {
            if(slam > overal) slam = overal;
         } else
         {
            if(slam < undral) slam = undral;
         }
         
         double F3 = 0.;
         do
         {
            iterate = false;
            double toler9 = Math.max(toler8, Math.abs(toler8*slam));
            // min. of parabola at one point
            if( Math.abs(p0.x() - slam) < toler9 ||
            Math.abs(p1.x() - slam) < toler9 ||
            Math.abs(p2.x() - slam) < toler9)
            {
               return new MnParabolaPoint(xvmin, fvmin);
            }
            
            F3 = fcn.valueOf(MnUtils.add(st.vec(),MnUtils.mul(step,slam)));
             // if latest point worse than all three previous, cut step
            if(F3 > p0.y() && F3 > p1.y() && F3 > p2.y())
            {
               if(slam > xvmin) overal = Math.min(overal, slam-toler8);
               if(slam < xvmin) undral = Math.max(undral, slam+toler8);
               slam = 0.5*(slam + xvmin);
               iterate = true;
               niter++;
            }
         } 
         while(iterate && niter < maxiter);
         
         if(niter >= maxiter)
         {
            // exhausted max number of iterations
            return new MnParabolaPoint(xvmin, fvmin);
         }
         
         // find worst previous point out of three and replace
         MnParabolaPoint p3 = new MnParabolaPoint(slam, F3);
         if(p0.y() > p1.y() && p0.y() > p2.y()) p0 = p3;
         else if(p1.y() > p0.y() && p1.y() > p2.y()) p1 = p3;
         else p2 = p3;
         if(F3 < fvmin)
         {
            fvmin = F3;
            xvmin = slam;
         } 
         else
         {
            if(slam > xvmin) overal = Math.min(overal, slam-toler8);
            if(slam < xvmin) undral = Math.max(undral, slam+toler8);
         }
         
         niter++;
      } 
      while(niter < maxiter);
      
      return new MnParabolaPoint(xvmin, fvmin);
   }
}
