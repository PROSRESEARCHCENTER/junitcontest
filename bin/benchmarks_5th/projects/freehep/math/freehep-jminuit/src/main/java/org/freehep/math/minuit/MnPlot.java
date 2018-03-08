package org.freehep.math.minuit;

import java.util.List;

/** MnPlot produces a text-screen graphical output of (x,y) points. E.g.
 * from Scan or Contours.
 * @version $Id: MnPlot.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnPlot
{
   public MnPlot()
   {
      this(80,30);
   }
   public MnPlot(int width, int length)
   {
      thePageWidth = width;
      thePageLength = length;
      if(thePageWidth > 120) thePageWidth = 120;
      if(thePageLength > 56) thePageLength = 56;
   }
   public void plot(List<Point> points)
   {
      double[] x  = new double[points.size()];
      double[] y  = new double[points.size()];
      StringBuffer chpt = new StringBuffer(points.size());
      
      int i = 0;
      for(Point ipoint : points)
      {
         x[i] = ipoint.first;
         y[i] = ipoint.second;
         chpt.append('*');
         i++;
      }
      
      mnplot(x, y, chpt, points.size(), width(), length());
   }
   public void plot(double xmin, double ymin, List<Point> points)
   {
      double[] x = new double[points.size()+2];
      x[0] = xmin;
      x[1] = xmin;
      double[] y = new double[points.size()+2];
      y[0] = ymin;
      y[1] = ymin;
      StringBuffer chpt = new StringBuffer(points.size()+2);
      chpt.append(' ');
      chpt.append('X');
      
      int i = 2;
      for(Point ipoint : points)   
      {
         x[i] = ipoint.first;
         y[i] = ipoint.second;
         chpt.append('*');
         i++;
      }
      
      mnplot(x, y, chpt, points.size()+2, width(), length());
   }
   
   int width()
   {
      return thePageWidth;
   }
   int length()
   {
      return thePageLength;
   }
   
   private int thePageWidth;
   private int thePageLength;
   
   private void mnplot(double[] xpt, double[] ypt, StringBuffer chpt, int nxypt, int npagwd, int npagln)
   {
      //*-*-*-*Plots points in array xypt onto one page with labelled axes*-*-*-*-*
      //*-*    ===========================================================
      //*-*        NXYPT is the number of points to be plotted
      //*-*        XPT(I) = x-coord. of ith point
      //*-*        YPT(I) = y-coord. of ith point
      //*-*        CHPT(I) = character to be plotted at this position
      //*-*        the input point arrays XPT, YPT, CHPT are destroyed.
      //*-*
      //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
      
      /* Local variables */
      double xmin, ymin, xmax, ymax, savx, savy, yprt;
      double bwidx, bwidy, xbest, ybest, ax, ay, bx, by;
      double[] xvalus = new double[12];
      double any, dxx, dyy;
      int iten, i, j, k, maxnx, maxny, iquit, ni, linodd;
      int nxbest, nybest, km1, ibk, isp1, nx, ny, ks, ix;
      boolean overpr;
      StringBuffer cline = new StringBuffer(npagwd);
      for (int ii=0; ii<npagwd; ii++) cline.append(' ');
      char chsav, chbest;
      
      /* Function Body */
      //*-*  Computing MIN
      maxnx = npagwd-20 < 100 ? npagwd-20 : 100;
      if (maxnx < 10) maxnx = 10;
      maxny = npagln;
      if (maxny < 10) maxny = 10;
      if (nxypt <= 1) return;
      xbest  = xpt[0];
      ybest  = ypt[0];
      chbest = chpt.charAt(0);
      //*-*-        order the points by decreasing y
      km1 = nxypt - 1;
      for (i = 1; i <= km1; ++i)
      {
         iquit = 0;
         ni    = nxypt - i;
         for (j = 1; j <= ni; ++j)
         {
            if (ypt[j-1] > ypt[j]) continue;
            savx     = xpt[j-1];
            xpt[j-1] = xpt[j];
            xpt[j]   = savx;
            savy     = ypt[j-1];
            ypt[j-1] = ypt[j];
            ypt[j]   = savy;
            chsav    = chpt.charAt(j-1);
            chpt.setCharAt(j-1,chpt.charAt(j));
            chpt.setCharAt(j,chsav);
            iquit    = 1;
         }
         if (iquit == 0) break;
      }
      //*-*-        find extreme values
      xmax = xpt[0];
      xmin = xmax;
      for (i = 1; i <= nxypt; ++i)
      {
         if (xpt[i-1] > xmax) xmax = xpt[i-1];
         if (xpt[i-1] < xmin) xmin = xpt[i-1];
      }
      dxx   = (xmax - xmin)*.001;
      xmax += dxx;
      xmin -= dxx;
      mnbins(xmin, xmax, maxnx);
      xmin = this.bl;
      xmax = this.bh;
      nx = this.nb;
      bwidx = this.bwid;
      ymax = ypt[0];
      ymin = ypt[nxypt-1];
      if (ymax == ymin) ymax = ymin + 1;
      dyy   = (ymax - ymin)*.001;
      ymax += dyy;
      ymin -= dyy;
      mnbins(ymin, ymax, maxny);
      ymin = this.bl;
      ymax = this.bh;
      ny = this.nb;
      bwidy = this.bwid;
      any = (double) ny;
      //*-*-        if first point is blank, it is an 'origin'
      if (chbest != ' ')
      {
         xbest = (xmax + xmin)*.5;
         ybest = (ymax + ymin)*.5;
      }
      //*-*-        find scale constants
      ax = 1 / bwidx;
      ay = 1 / bwidy;
      bx = -ax*xmin + 2;
      by = -ay*ymin - 2;
      //*-*-        convert points to grid positions
      for (i = 1; i <= nxypt; ++i)
      {
         xpt[i-1] = ax*xpt[i-1] + bx;
         ypt[i-1] = any - ay*ypt[i-1] - by;
      }
      nxbest = (int) (ax*xbest + bx);
      nybest = (int) (any - ay*ybest - by);
      //*-*-        print the points
      ny += 2;
      nx += 2;
      isp1 = 1;
      linodd = 1;
      overpr = false;
      for (i = 1; i <= ny; ++i)
      {
         for (ibk = 1; ibk <= nx; ++ibk)
         { cline.setCharAt(ibk-1,' '); }
         //         cline.setCharAt(nx,'\0');
         //         cline.setCharAt(nx+1,'\0');
         cline.setCharAt(0,'.');
         cline.setCharAt(nx-1,'.');
         cline.setCharAt(nxbest-1,'.');
         if (i == 1 || i == nybest || i == ny)
         {
            for (j = 1; j <= nx; ++j)
            { cline.setCharAt(j-1,'.'); }
         }
         yprt = ymax - (i-1.)*bwidy;
         boolean isplset = false;
         if (isp1 <= nxypt)
         {
            //*-*-        find the points to be plotted on this line
            for (k = isp1; k <= nxypt; ++k)
            {
               ks = (int) ypt[k-1];
               if (ks > i)
               {
                  isp1 = k;
                  isplset = true;
                  break;
               }
               ix = (int) xpt[k-1];
               if (cline.charAt(ix-1) != '.' && cline.charAt(ix-1) != ' ')
               {
                  if (cline.charAt(ix-1) == chpt.charAt(k-1)) continue;
                  overpr = true;
                  //*-*-        OVERPR is true if one or more positions contains more than
                  //*-*-           one point
                  cline.setCharAt(ix-1,'&');
                  continue;
               }
               cline.setCharAt(ix-1,chpt.charAt(k-1));
            }
            if (!isplset) isp1 = nxypt + 1;
            
         }
         if (linodd != 1 && i != ny)
         {
            linodd = 1;
            System.out.printf("                  %s",cline.substring(0,60));
         }
         else
         {
            System.out.printf(" %14.7g ..%s",yprt,cline.substring(0,60));
            linodd = 0;
         }
         System.out.println();
      }
      //*-*-        print labels on x-axis every ten columns
      for (ibk = 1; ibk <= nx; ++ibk)
      {
         cline.setCharAt(ibk-1,' ');
         if (ibk % 10 == 1) cline.setCharAt(ibk-1,'/');
      }
      System.out.printf("                  %s",cline);
      System.out.printf("\n");
      
      for (ibk = 1; ibk <= 12; ++ibk)
      {
         xvalus[ibk-1] = xmin + (ibk-1.)*10*bwidx;
      }
      System.out.printf("           ");
      iten = (nx + 9) / 10;
      for (ibk = 1; ibk <= iten; ++ibk)
      {
         System.out.printf(" %9.4g", xvalus[ibk-1]);
      }
      System.out.printf("\n");
      
      if (overpr)
      {
         String chmess = "   Overprint character is &";
         System.out.printf("                         ONE COLUMN=%13.7g%s",bwidx,chmess);
      }
      else
      {
         String chmess = " ";
         System.out.printf("                         ONE COLUMN=%13.7g%s",bwidx,chmess);
      }
      System.out.println();
   }
   private void mnbins(double a1, double a2, int naa)
   {
      
      //*-*-*-*-*-*-*-*-*-*-*Compute reasonable histogram intervals*-*-*-*-*-*-*-*-*
      //*-*                  ======================================
      //*-*        Function TO DETERMINE REASONABLE HISTOGRAM INTERVALS
      //*-*        GIVEN ABSOLUTE UPPER AND LOWER BOUNDS  A1 AND A2
      //*-*        AND DESIRED MAXIMUM NUMBER OF BINS NAA
      //*-*        PROGRAM MAKES REASONABLE BINNING FROM BL TO BH OF WIDTH BWID
      //*-*        F. JAMES,   AUGUST, 1974 , stolen for Minuit, 1988
      //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
      
      /* Local variables */
      double awid,ah, al, sigfig, sigrnd, alb;
      int kwid, lwid, na=0, log_;
      
      al = a1 < a2 ? a1 : a2;
      ah = a1 > a2 ? a1 : a2;
      if (al == ah) ah = al + 1;
      
      //*-*-       IF NAA .EQ. -1 , PROGRAM USES BWID INPUT FROM CALLING ROUTINE
      boolean skip = (naa == -1 && bwid > 0);
      if (!skip)
      {
         na = naa - 1;
         if (na < 1) na = 1;
      }
      for (;;)
      {
         if (!skip)
         {
            //*-*-        GET NOMINAL BIN WIDTH IN EXPON FORM
            awid = (ah-al) / ((double) na);
            log_ = (int) (Math.log10(awid));
            if (awid <= 1) --log_;
            sigfig = awid*Math.pow(10, -log_);
            //*-*-       ROUND MANTISSA UP TO 2, 2.5, 5, OR 10
            if (sigfig <= 2)
            {
               sigrnd = 2;
            }
            else if (sigfig <= 2.5)
            {
               sigrnd = 2.5;
            }
            else if (sigfig <= 5)
            {
               sigrnd = 5;
            }
            else
            {
               sigrnd = 1;
               ++log_;
            }
            bwid = sigrnd*Math.pow(10, log_);
         }
         alb  = al / bwid;
         lwid = (int) alb;
         if (alb < 0) --lwid;
         bl   = bwid*((double)lwid);
         alb  = ah / bwid + 1;
         kwid = (int) alb;
         if (alb < 0) --kwid;
         bh = bwid*((double) kwid);
         nb = kwid - lwid;
         if (naa <= 5)
         {
            if (naa == -1) return;
            //*-*-        REQUEST FOR ONE BIN IS DIFFICULT CASE
            if (naa > 1 || nb == 1) return;
            bwid *= 2;
            nb = 1;
            return;
         }
         if (nb << 1 != naa) return;
         ++na;
         skip = false;
         continue;
      }
   }
   private double bl;
   private double bh;
   private int nb;
   private double bwid;
}
