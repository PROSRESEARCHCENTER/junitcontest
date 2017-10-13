package hep.physics.jet;

import hep.physics.filter.Predicate;
import hep.physics.vec.BasicHep3Vector;
import hep.physics.vec.Hep3Vector;
import hep.physics.vec.HepLorentzVector;

import java.util.Collection;
import java.util.Iterator;

/**
 * Event Shape and Thrust utilities.
 * <p>
 * This is a transcription of the Jetset thrust and event shape
 * finders into Java.
 * <p>
 * Event shape extracts from the input enumeration 3-momenta which are formed
 * into a kind of (symmetric) momentum tensor similar to an inertia tensor.
 * From this tensor the 3-principal axes are determined along with their
 * associated eigenvalues.
 * <p>
 * Traditionally, the nomenclature for the three axes are:
 * <ul>
 * <li>Thrust Axis is associated with the largest eigenvalue called the Thrust.
 * <li>Major Axis is associated with the middle eigenvalue called the Major Thrust.
 * <li>Minor Axis is associated with the smallest eigenvalue called the Minor Thrust.
 * </ul>
 *
 * @author G.Bower 9/21/98
 */

public class EventShape
{
   // data: parameters
   // PARU(41): Power of momentum dependence in sphericity finder.
   private double m_dSphMomPower = 2.0;
   // PARU(42): Power of momentum dependence in thrust finder.
   private double m_dDeltaThPower = 0.0;
   // MSTU(44): # of initial fastest particles choosen to start search.
   private int m_iFast = 4;
   // PARU(48): Convergence criteria for axis maximization.
   private double m_dConv = 0.0001;
   // MSTU(45): # different starting configurations that must
   // converge before axis is accepted as correct.
   private int m_iGood = 2;
   // data: results
   // m_dAxes[0] is the Thrust axis.
   // m_dAxes[1] is the Major axis.
   // m_dAxes[2] is the Minor axis.
   private double[][] m_dAxes;
   private double[] m_dThrust;
   private double m_dOblateness;
   private BasicHep3Vector m_EigenVector1;
   private BasicHep3Vector m_EigenVector2;
   private BasicHep3Vector m_EigenVector3;
   private double m_dEigenValue1;
   private double m_dEigenValue2;
   private double m_dEigneValue3;
   
   private final static int m_maxpart = 1000;
   
   /**
    * Create a new instance of EventShape
    */
   public EventShape()
   {
      // The zero element in each array in m_dAxes is ignored. Elements
      // 1,2 and 3 are the x, y, and z direction cosines of the axis.
      // Also the zeroth axis and thrust are ignored.
      m_dAxes = new double[4][4];
      m_dThrust = new double[4];
   }
   /**
    * Call this to input a new event to the event shape routines.
    *
    * @param data An Enumeration of either HepLorentzVectors (HepLorentzVectors) or Hep3Vectors
    */
   public void setEvent(Collection data)
   {
      setEvent(data,null);
   }
   /**
    * Call this to input a new event to the event shape routines.
    *
    * Only elements of the enumeration which are accepted by the predicate will be used
    * for jet finding.
    *
    * @param data An Enumeration of either HepLorentzVectors (HepLorentzVectors) or Hep3Vectors
    * @param cut A predicate that is applied to each element of e, or null to accept all elements
    */
   public void setEvent(Collection data, Predicate cut)
   {
      
      //To make this look like normal physics notation the
      //zeroth element of each array, mom[i][0], will be ignored
      //and operations will be on elements 1,2,3...
      double[][] mom = new double[m_maxpart][6];
      double tmax = 0;
      double phi = 0.;
      double the = 0.;
      double sgn;
      double[][] fast = new double[ m_iFast + 1 ][6];
      double[][] work = new double[11][6];
      double tdi[] = new double[4];
      double tds;
      double tpr[] = new double[4];
      double thp;
      double thps;
      double[][] temp = new double[3][5];
      
      int np = 0;
      for (Iterator i = data.iterator(); i.hasNext(); )
      {
         Object o = i.next();
         if (cut != null && !cut.accept(o)) continue;
         
         if (np >= m_maxpart) throw new RuntimeException("Too many particles input to EventShape");
         
         Hep3Vector v;
         if (o instanceof Hep3Vector)
         {
            v = (Hep3Vector) o;
         }
         else if (o instanceof HepLorentzVector)
         {
            HepLorentzVector l = (HepLorentzVector) o;
            v = l.v3();
         }
         else throw new RuntimeException("Element input to EventShape is not a Hep3Vector or an HepLorentzVector");
         
         mom[np][1] = v.x();
         mom[np][2] = v.y();
         mom[np][3] = v.z();
         mom[np][4] = v.magnitude();
         if ( Math.abs( m_dDeltaThPower ) <= 0.001 )
         {
            mom[np][5] = 1.0;
         }
         else
         {
            mom[np][5] = Math.pow( mom[np][4], m_dDeltaThPower );
         }
         tmax = tmax + mom[np][4]*mom[np][5];
         np++;
      }
      if ( np < 2 )
      {
         m_dThrust[1] = -1.0;
         m_dOblateness = -1.0;
         return;
      }
      // for pass = 1: find thrust axis.
      // for pass = 2: find major axis.
      for ( int pass=1; pass < 3; pass++)
      {
         if ( pass == 2 )
         {
            phi = ulAngle( m_dAxes[1][1], m_dAxes[1][2] );
            ludbrb( mom, 0, -phi, 0., 0., 0. );
            for ( int i = 0; i < 3; i++ )
            {
               for ( int j = 1; j < 4; j++ )
               {
                  temp[i][j] = m_dAxes[i+1][j];
               }
               temp[i][4] = 0;
            }
            ludbrb(temp,0.,-phi,0.,0.,0.);
            for ( int i = 0; i < 3; i++ )
            {
               for ( int j = 1; j < 4; j++ )
               {
                  m_dAxes[i+1][j] = temp[i][j];
               }
            }
            the = ulAngle( m_dAxes[1][3], m_dAxes[1][1] );
            ludbrb( mom, -the, 0., 0., 0., 0. );
            for ( int i = 0; i < 3; i++ )
            {
               for ( int j = 1; j < 4; j++ )
               {
                  temp[i][j] = m_dAxes[i+1][j];
               }
               temp[i][4] = 0;
            }
            ludbrb(temp,-the,0.,0.,0.,0.);
            for ( int i = 0; i < 3; i++ )
            {
               for ( int j = 1; j < 4; j++ )
               {
                  m_dAxes[i+1][j] = temp[i][j];
               }
            }
         }
         for ( int ifas = 0; ifas < m_iFast + 1 ; ifas++ )
         {
            fast[ifas][4] = 0.;
         }
         // Find the m_iFast highest momentum particles and
         // put the highest in fast[0], next in fast[1],....fast[m_iFast-1].
         // fast[m_iFast] is just a workspace.
         for ( int i = 0; i < np; i++ )
         {
            if ( pass == 2 )
            {
               mom[i][4] = Math.sqrt( mom[i][1]*mom[i][1]
                       + mom[i][2]*mom[i][2] );
            }
            for ( int ifas = m_iFast - 1; ifas > -1; ifas-- )
            {
               if ( mom[i][4] > fast[ifas][4] )
               {
                  for ( int j = 1; j < 6; j++ )
                  {
                     fast[ifas+1][j] = fast[ifas][j];
                     if ( ifas == 0 )
                     {
                        fast[ifas][j] = mom[i][j];
                     }
                  }
               }
               else
               {
                  for ( int j = 1; j < 6; j++ )
                  {
                     fast[ifas+1][j] = mom[i][j];
                  }
                  break;
               }
            }
         }
         // Find axis with highest thrust (case 1)/ highest major (case 2).
         for ( int i = 0; i < work.length; i++ )
         {
            work[i][4] = 0.;
         }
         int p = Math.min( m_iFast, np ) - 1;
         // Don't trust Math.pow to give right answer always.
         // Want nc = 2**p.
         int nc = iPow(2,p);
         for ( int n = 0; n < nc; n++ )
         {
            for ( int j = 1; j < 4; j++ )
            {
               tdi[j] = 0.;
            }
            for ( int i = 0; i < Math.min(m_iFast,n); i++ )
            {
               sgn = fast[i][5];
               if (iPow(2,(i+1))*((n+iPow(2,i))/iPow(2,(i+1))) >= i+1)
               {
                  sgn = -sgn;
               }
               for ( int j = 1; j < 5-pass; j++ )
               {
                  tdi[j] = tdi[j] + sgn*fast[i][j];
               }
            }
            tds = tdi[1]*tdi[1] + tdi[2]*tdi[2] + tdi[3]*tdi[3];
            for ( int iw = Math.min(n,9); iw > -1; iw--)
            {
               if( tds > work[iw][4] )
               {
                  for ( int j = 1; j < 5; j++ )
                  {
                     work[iw+1][j] = work[iw][j];
                     if ( iw == 0 )
                     {
                        if ( j < 4 )
                        {
                           work[iw][j] = tdi[j];
                        }
                        else
                        {
                           work[iw][j] = tds;
                        }
                     }
                  }
               }
               else
               {
                  for ( int j = 1; j < 4; j++ )
                  {
                     work[iw+1][j] = tdi[j];
                  }
                  work[iw+1][4] = tds;
               }
            }
         }
         // Iterate direction of axis until stable maximum.
         m_dThrust[pass] = 0;
         thp = -99999.;
         int nagree = 0;
         for ( int iw = 0; iw < Math.min(nc,10) && nagree < m_iGood; iw++ )
         {
            thp = 0.;
            thps = -99999.;
            while ( thp > thps + m_dConv )
            {
               thps = thp;
               for ( int j = 1; j < 4; j++ )
               {
                  if ( thp <= 1E-10 )
                  {
                     tdi[j] = work[iw][j];
                  }
                  else
                  {
                     tdi[j] = tpr[j];
                     tpr[j] = 0;
                  }
               }
               for ( int i = 0; i < np; i++ )
               {
                  sgn = sign(mom[i][5],
                          tdi[1]*mom[i][1] +
                          tdi[2]*mom[i][2] +
                          tdi[3]*mom[i][3]);
                  for ( int j = 1; j < 5 - pass; j++ )
                  {
                     tpr[j] = tpr[j] + sgn*mom[i][j];
                  }
               }
               thp = Math.sqrt(  tpr[1]*tpr[1]
                       + tpr[2]*tpr[2]
                       + tpr[3]*tpr[3])/tmax;
            }
            // Save good axis. Try new initial axis until enough
            // tries agree.
            if ( thp < m_dThrust[pass] - m_dConv )
            {
               break;
            }
            if ( thp > m_dThrust[pass] + m_dConv )
            {
               nagree = 0;
               sgn = iPow( -1, (int)Math.round(Math.random()) );
               for ( int j = 1; j < 4; j++ )
               {
                  m_dAxes[pass][j] = sgn*tpr[j]/(tmax*thp);
               }
               m_dThrust[pass] = thp;
            }
            nagree = nagree + 1;
         }
      }
      // Find minor axis and value by orthogonality.
      sgn = iPow( -1, (int)Math.round(Math.random()));
      m_dAxes[3][1] = -sgn*m_dAxes[2][2];
      m_dAxes[3][2] = sgn*m_dAxes[2][1];
      m_dAxes[3][3] = 0.;
      thp = 0.;
      for ( int i = 0; i < np; i++ )
      {
         thp = thp + mom[i][5]*Math.abs( m_dAxes[3][1]*mom[i][1]
                 +	m_dAxes[3][2]*mom[i][2]);
      }
      m_dThrust[3] = thp/tmax;
      // Rotate back to original coordinate system.
      for ( int i = 0; i < 3; i++ )
      {
         for ( int j = 1; j < 4; j++ )
         {
            temp[i][j] = m_dAxes[i+1][j];
         }
         temp[i][4] = 0;
      }
      ludbrb(temp,the,phi,0.,0.,0.);
      for ( int i = 0; i < 3; i++ )
      {
         for ( int j = 1; j < 4; j++ )
         {
            m_dAxes[i+1][j] = temp[i][j];
         }
      }
      m_dOblateness = m_dThrust[2] - m_dThrust[3];
   }
   // Setting and getting parameters.
   public void setThMomPower(double tp)
   {
      // Error if sp not positive.
      if ( tp > 0. )
      {
         m_dDeltaThPower = tp - 1.0;
      }
      return;
   }
   public double getThMomPower()
   {
      return 1.0 + m_dDeltaThPower;
   }
   public void setFast(int nf)
   {
      // Error if sp not positive.
      if ( nf > 3 )
      {
         m_iFast = nf;
      }
      return;
   }
   public int getFast()
   {
      return m_iFast;
   }
   
   // Returning results
   public BasicHep3Vector thrustAxis()
   {
      return new BasicHep3Vector(m_dAxes[1][1],m_dAxes[1][2],m_dAxes[1][3]);
   }
   public BasicHep3Vector majorAxis()
   {
      return new BasicHep3Vector(m_dAxes[2][1],m_dAxes[2][2],m_dAxes[2][3]);
   }
   public BasicHep3Vector minorAxis()
   {
      return new BasicHep3Vector(m_dAxes[3][1],m_dAxes[3][2],m_dAxes[3][3]);
   }
   /**
    * Element x = Thrust
    * Element y = Major Thrust
    * Element z = Minor Thrust
    */
   public BasicHep3Vector thrust()
   {
      return new BasicHep3Vector(m_dThrust[1],m_dThrust[2],m_dThrust[3]);
   }
   /**
    *  Oblateness = Major Thrust - Minor Thrust
    */
   public double oblateness()
   {
      return m_dOblateness;
   }
   //	BasicHep3Vector eigenVector1()
   //	{
   //		return;
   //	}
   //	BasicHep3Vector eigenVector2()
   //	{
   //		return;
   //	}
   //	BasicHep3Vector eigenVector3()
   //	{
   //		return;
   //	}
   //	double eigenValue1()
   //	{
   //		return;
   //	}
   //	double eigenValue2()
   //	{
   //		return;
   //	}
   //	double eigenValue3()
   //	{
   //		return;
   //	}
   // utilities(from Jetset):
   private double ulAngle(double x, double y)
   {
      double ulangl = 0;
      double r = Math.sqrt(x*x + y*y);
      if ( r < 1.0E-20 )
      {
         return ulangl;
      }
      if ( Math.abs(x)/r < 0.8 )
      {
         ulangl = sign(Math.acos(x/r),y);
      }
      else
      {
         ulangl = Math.asin(y/r);
         if ( x < 0. && ulangl >= 0. )
         {
            ulangl = Math.PI - ulangl;
         }
         else if ( x < 0. )
         {
            ulangl = -Math.PI - ulangl;
         }
      }
      return ulangl;
   }
   private double sign(double a, double b)
   {
      if ( b < 0 )
      {
         return -Math.abs(a);
      }
      else
      {
         return Math.abs(a);
      }
   }
   private void ludbrb(double[][] mom,
           double the,
           double phi,
           double bx,
           double by,
           double bz)
   {
      // Ignore "zeroth" elements in rot,pr,dp.
      // Trying to use physics-like notation.
      double rot[][] = new double[4][4];
      double pr[] = new double[4];
      double dp[] = new double[5];
      int np = mom.length;
      if ( the*the + phi*phi > 1.0E-20 )
      {
         rot[1][1] = Math.cos(the)*Math.cos(phi);
         rot[1][2] = -Math.sin(phi);
         rot[1][3] = Math.sin(the)*Math.cos(phi);
         rot[2][1] = Math.cos(the)*Math.sin(phi);
         rot[2][2] = Math.cos(phi);
         rot[2][3] = Math.sin(the)*Math.sin(phi);
         rot[3][1] = -Math.sin(the);
         rot[3][2] = 0.0;
         rot[3][3] = Math.cos(the);
         for ( int i = 0; i < np; i++ )
         {
            for ( int j = 1; j < 4; j++ )
            {
               pr[j] = mom[i][j];
               mom[i][j] = 0;
            }
            for ( int j = 1; j < 4; j++)
            {
               for ( int k = 1; k < 4; k++)
               {
                  mom[i][j] = mom[i][j] + rot[j][k]*pr[k];
               }
            }
         }
         double beta = Math.sqrt( bx*bx + by*by + bz*bz );
         if ( beta*beta > 1.0E-20 )
         {
            if ( beta >  0.99999999 )
            {
               //send message: boost too large, resetting to <~1.0.
               bx = bx*(0.99999999/beta);
               by = by*(0.99999999/beta);
               bz = bz*(0.99999999/beta);
               beta =   0.99999999;
            }
            double gamma = 1.0/Math.sqrt(1.0 - beta*beta);
            for ( int i = 0; i < np; i++ )
            {
               for ( int j = 1; j < 5; j++ )
               {
                  dp[j] = mom[i][j];
               }
               double bp = bx*dp[1] + by*dp[2] + bz*dp[3];
               double gbp = gamma*(gamma*bp/(1.0 + gamma) + dp[4]);
               mom[i][1] = dp[1] + gbp*bx;
               mom[i][2] = dp[2] + gbp*by;
               mom[i][3] = dp[3] + gbp*bz;
               mom[i][4] = gamma*(dp[4] + bp);
            }
         }
      }
      return;
   }
   private int iPow(int man, int exp)
   {
      int ans = 1;
      for( int k = 0; k < exp; k++)
      {
         ans = ans*man;
      }
      return ans;
   }
}

