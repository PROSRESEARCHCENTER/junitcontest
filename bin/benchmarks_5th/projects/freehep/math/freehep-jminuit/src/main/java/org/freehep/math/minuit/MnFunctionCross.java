package org.freehep.math.minuit;

import java.util.logging.Logger;

/**
 *
 * @version $Id: MnFunctionCross.java 16142 2014-09-05 02:52:34Z tonyj $
 */
class MnFunctionCross
{
   MnFunctionCross(FCNBase fcn, MnUserParameterState state, double fval, MnStrategy stra, double errorDef)
   {
      theFCN = fcn;
      theState = state;
      theFval = fval;
      theStrategy = stra;
      theErrorDef = errorDef;
   }
   
   MnCross cross(int[] par, double[] pmid, double[] pdir, double tlr, int maxcalls)
   {
      int npar = par.length;
      int nfcn = 0;
      MnMachinePrecision prec = theState.precision();
      
      double tlf = tlr*theErrorDef;
      double tla = tlr;
      int maxitr = 15;
      int ipt = 0;
      double aminsv = theFval;
      double aim = aminsv + theErrorDef;
      
      double aopt = 0.;
      boolean limset = false;
      double[] alsb = new double[3];
      double[] flsb = new double[3];
      double up = theErrorDef;
      
      double aulim = 100.;
      for(int i = 0; i < par.length; i++)
      {
         int kex = par[i];
         if(theState.parameter(kex).hasLimits())
         {
            double zmid = pmid[i];
            double zdir = pdir[i];
            if(Math.abs(zdir) < theState.precision().eps()) continue;
            
            if(zdir > 0. && theState.parameter(kex).hasUpperLimit())
            {
               double zlim = theState.parameter(kex).upperLimit();
               aulim = Math.min(aulim, (zlim-zmid)/zdir);
            }
            else if(zdir < 0. && theState.parameter(kex).hasLowerLimit())
            {
               double zlim = theState.parameter(kex).lowerLimit();
               aulim = Math.min(aulim, (zlim-zmid)/zdir);
            }
         }
      }
      
      if(aulim  < aopt+tla) limset = true;
      
      MnMigrad migrad = new MnMigrad(theFCN, theState, new MnStrategy(Math.max(0, theStrategy.strategy()-1)));
      
      for(int i = 0; i < npar; i++)
      {
         migrad.setValue(par[i], pmid[i]);
      }
      
      FunctionMinimum min0 = migrad.minimize(maxcalls, tlr);
      nfcn += min0.nfcn();
      
      if(min0.hasReachedCallLimit())
         return new MnCross(min0.userState(), nfcn, new MnCross.CrossFcnLimit());
      if(!min0.isValid()) return new MnCross(nfcn);
      if(limset == true && min0.fval() < aim)
         return new MnCross(min0.userState(), nfcn, new MnCross.CrossParLimit());
      
      ipt++;
      alsb[0] = 0.;
      flsb[0] = min0.fval();
      flsb[0] = Math.max(flsb[0], aminsv + 0.1*up);
      aopt = Math.sqrt(up/(flsb[0]-aminsv)) - 1.;
      if(Math.abs(flsb[0] - aim) < tlf) return new MnCross(aopt, min0.userState(), nfcn);
      
      if(aopt > 1.) aopt = 1.;
      if(aopt < -0.5) aopt = -0.5;
      limset = false;
      if(aopt > aulim)
      {
         aopt = aulim;
         limset = true;
      }
      
      for(int i = 0; i < npar; i++)
      {
         migrad.setValue(par[i], pmid[i] + (aopt)*pdir[i]);
      }
      
      FunctionMinimum min1 = migrad.minimize(maxcalls, tlr);
      nfcn += min1.nfcn();
      
      if(min1.hasReachedCallLimit())
         return new MnCross(min1.userState(), nfcn, new MnCross.CrossFcnLimit());
      if(!min1.isValid()) return new MnCross(nfcn);
      if(limset == true && min1.fval() < aim)
         return new MnCross(min1.userState(), nfcn, new MnCross.CrossParLimit());
      
      ipt++;
      alsb[1] = aopt;
      flsb[1] = min1.fval();
      double dfda = (flsb[1] - flsb[0])/(alsb[1] - alsb[0]);
      
      double ecarmn;
      double ecarmx;
      int ibest = 0;
      int iworst;
      int noless;
      FunctionMinimum min2 = null;
      
      L300: for (;;)
      {
         if(dfda < 0.)
         {
            int maxlk = maxitr - ipt;
            for(int it = 0; it < maxlk; it++)
            {
               alsb[0] = alsb[1];
               flsb[0] = flsb[1];
               aopt = alsb[0] + 0.2*it;
               limset = false;
               if(aopt > aulim)
               {
                  aopt = aulim;
                  limset = true;
               }
               for(int i = 0; i < npar; i++)
               {
                  migrad.setValue(par[i], pmid[i] + (aopt)*pdir[i]);
               }
               min1 = migrad.minimize(maxcalls, tlr);
               nfcn += min1.nfcn();
               
               if(min1.hasReachedCallLimit())
                  return new MnCross(min1.userState(), nfcn, new MnCross.CrossFcnLimit());
               if(!min1.isValid()) return new MnCross(nfcn);
               if(limset == true && min1.fval() < aim)
                  return new MnCross(min1.userState(), nfcn, new MnCross.CrossParLimit());
               ipt++;
               alsb[1] = aopt;
               flsb[1] = min1.fval();
               dfda = (flsb[1] - flsb[0])/(alsb[1] - alsb[0]);
               if(dfda > 0.) break;
            }
            if(ipt > maxitr) return new MnCross(nfcn);
         }
         
         L460: for (;;)
         {
            aopt = alsb[1] + (aim-flsb[1])/dfda;
            double fdist = Math.min(Math.abs(aim  - flsb[0]), Math.abs(aim  - flsb[1]));
            double adist = Math.min(Math.abs(aopt - alsb[0]), Math.abs(aopt - alsb[1]));
            tla = tlr;
            if(Math.abs(aopt) > 1.) tla = tlr*Math.abs(aopt);
            if(adist < tla && fdist < tlf) return new MnCross(aopt, min1.userState(), nfcn);
            if(ipt > maxitr) return new MnCross(nfcn);
            double bmin = Math.min(alsb[0], alsb[1]) - 1.;
            if(aopt < bmin) aopt = bmin;
            double bmax = Math.max(alsb[0], alsb[1]) + 1.;
            if(aopt > bmax) aopt = bmax;
            
            limset = false;
            if(aopt > aulim)
            {
               aopt = aulim;
               limset = true;
            }
            
            for(int i = 0; i < npar; i++)
            {
               migrad.setValue(par[i], pmid[i] + (aopt)*pdir[i]);
            }
            min2 = migrad.minimize(maxcalls, tlr);
            nfcn += min2.nfcn();
            
            if(min2.hasReachedCallLimit())
               return new MnCross(min2.userState(), nfcn, new MnCross.CrossFcnLimit());
            if(!min2.isValid()) return new MnCross(nfcn);
            if(limset == true && min2.fval() < aim)
               return new MnCross(min2.userState(), nfcn, new MnCross.CrossParLimit());
            
            ipt++;
            alsb[2] = aopt;
            flsb[2] = min2.fval();
            
            ecarmn = Math.abs(flsb[2] - aim);
            ecarmx = 0.;
            ibest = 2;
            iworst = 0;
            noless = 0;
            
            for(int i = 0; i < 3; i++)
            {
               double ecart = Math.abs(flsb[i] - aim);
               if(ecart > ecarmx)
               {
                  ecarmx = ecart;
                  iworst = i;
               }
               if(ecart < ecarmn)
               {
                  ecarmn = ecart;
                  ibest = i;
               }
               if(flsb[i] < aim) noless++;
            }
            
            
            if(noless == 1 || noless == 2) break L300;
            if(noless == 0 && ibest != 2) return new MnCross(nfcn);
            if(noless == 3 && ibest != 2)
            {
               alsb[1] = alsb[2];
               flsb[1] = flsb[2];
               continue L300;
            }
            
            flsb[iworst] = flsb[2];
            alsb[iworst] = alsb[2];
            dfda = (flsb[1] - flsb[0])/(alsb[1] - alsb[0]);
         }
      }
      
      do
      {
         MnParabola parbol = MnParabolaFactory.create(new MnParabolaPoint(alsb[0], flsb[0]), new MnParabolaPoint(alsb[1], flsb[1]), new MnParabolaPoint(alsb[2], flsb[2]));
         
         double coeff1 = parbol.c();
         double coeff2 = parbol.b();
         double coeff3 = parbol.a();
         double determ = coeff2*coeff2 - 4.*coeff3*(coeff1 - aim);
         if(determ < prec.eps()) return new MnCross(nfcn);
         double rt = Math.sqrt(determ);
         double x1 = (-coeff2 + rt)/(2.*coeff3);
         double x2 = (-coeff2 - rt)/(2.*coeff3);
         double s1 = coeff2 + 2.*x1*coeff3;
         double s2 = coeff2 + 2.*x2*coeff3;
         
         if(s1*s2 > 0.) logger.info("MnFunctionCross problem 1");
         aopt = x1;
         double slope = s1;
         if(s2 > 0.)
         {
            aopt = x2;
            slope = s2;
         }
         
         tla = tlr;
         if(Math.abs(aopt) > 1.) tla = tlr*Math.abs(aopt);
         if(Math.abs(aopt - alsb[ibest]) < tla && Math.abs(flsb[ibest] - aim) < tlf)
            return new MnCross(aopt, min2.userState(), nfcn);
         
         int ileft = 3;
         int iright = 3;
         int iout = 3;
         ibest = 0;
         ecarmx = 0.;
         ecarmn = Math.abs(aim-flsb[0]);
         for(int i = 0; i < 3; i++)
         {
            double ecart = Math.abs(flsb[i] - aim);
            if(ecart < ecarmn)
            {
               ecarmn = ecart;
               ibest = i;
            }
            if(ecart > ecarmx) ecarmx = ecart;
            if(flsb[i] > aim)
            {
               if(iright == 3) iright = i;
               else if(flsb[i] > flsb[iright]) iout = i;
               else
               {
                  iout = iright;
                  iright = i;
               }
            } else if(ileft == 3) ileft = i;
            else if(flsb[i] < flsb[ileft]) iout = i;
            else
            {
               iout = ileft;
               ileft = i;
            }
         }
         
         if(ecarmx > 10.*Math.abs(flsb[iout] - aim))
            aopt = 0.5*(aopt + 0.5*(alsb[iright] + alsb[ileft]));
         double smalla = 0.1*tla;
         if(slope*smalla > tlf) smalla = tlf/slope;
         double aleft = alsb[ileft] + smalla;
         double aright = alsb[iright] - smalla;
         if(aopt < aleft) aopt = aleft;
         if(aopt > aright) aopt = aright;
         if(aleft > aright) aopt = 0.5*(aleft + aright);
         
         limset = false;
         if(aopt > aulim)
         {
            aopt = aulim;
            limset = true;
         }
         
         for(int i = 0; i < npar; i++)
         {
            migrad.setValue(par[i], pmid[i] + (aopt)*pdir[i]);
         }
         min2 = migrad.minimize(maxcalls, tlr);
         nfcn += min2.nfcn();
         
         if(min2.hasReachedCallLimit())
            return new MnCross(min2.userState(), nfcn, new MnCross.CrossFcnLimit());
         if(!min2.isValid()) return new MnCross(nfcn);
         if(limset == true && min2.fval() < aim)
            return new MnCross(min2.userState(), nfcn, new MnCross.CrossParLimit());
         
         ipt++;
         alsb[iout] = aopt;
         flsb[iout] = min2.fval();
         ibest = iout;
      } while(ipt < maxitr);
      
      return new MnCross(nfcn);
   }
   
   private final FCNBase theFCN;
   private final MnUserParameterState theState;
   private final double theFval;
   private final MnStrategy theStrategy;
   private final double theErrorDef;
   private static final Logger logger = Logger.getLogger(MnFunctionCross.class.getName());
}
