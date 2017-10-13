package hep.physics.jet;

import hep.physics.filter.Predicate;
import hep.physics.vec.BasicHepLorentzVector;
import hep.physics.vec.Hep3Vector;
import hep.physics.vec.HepLorentzVector;
import hep.physics.vec.VecOp;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Base class for jet finders
 * @author G.Bower
 * @version 12/6/98
 */

public abstract class AbstractJetFinder implements JetFinder
{
   private double defaultMassSquared = 0;
   private final static int UNASSOC = -999;
   private int m_injets;
   // m_jet[i] is the 4 vector sum of all the particles in jet i.
   private HepLorentzVector[] m_jet;
   //m_ipart_jet_assoc[i] = j means particle i was placed in jet j.
   private int[] m_ipart_jet_assoc;
   // m_inparts_per_jet[i] = j means jet i has j particles in it.
   private int[] m_inparts_per_jet;
   // m_ifewest_tracks is number of tracks in the jet with the fewest tracks.
   private int m_ifewest_tracks;
   //
   private double m_dycut;
   private boolean m_resultsValid = false;
   private List m_in;
   private List m_4vec;
   private double m_devis = 0;
   //
   protected int m_np;
   protected HepLorentzVector[] m_part;
   //
   protected double[][] ymass;
   //
   abstract double masscut(double ycut, double evis, double esum);
   //
   abstract double calculate_mass(HepLorentzVector p1, HepLorentzVector p2);
   //
   abstract void combine_particles(int im, int jm);
   //
   public int njets()
   {
      if (!m_resultsValid) doFindJets();
      return m_injets;
   }
   public HepLorentzVector jet(int index)
   {
      if (!m_resultsValid) doFindJets();
      return m_jet[index];
   }
   public List particlesInJet(int index)
   {
      if (!m_resultsValid) doFindJets();
      List result = new ArrayList();
      for (int i=0; i<m_ipart_jet_assoc.length; i++) 
         if (m_ipart_jet_assoc[i] == index)result.add(m_in.get(i));
      return result;
   }
   public int nParticlesPerJet(int index)
   {
      if (!m_resultsValid) doFindJets();
      return m_inparts_per_jet[index];
   }
   public int fewestTracks()
   {
      if (!m_resultsValid) doFindJets();
      return m_ifewest_tracks;
   }
   protected AbstractJetFinder(double ycut)
   {
      m_dycut = ycut;
      m_resultsValid = false;
   }
   /**
    * Obtain the current ycut
    * @return The current value of ycut
    */
   public double getYCut()
   {
      return m_dycut;
   }
   /**
    * Set the YCut value.
    * If the new value for ycut is not the same as the old ycut the
    * jet finding will be rerun.
    *
    * @param ycut the new value to be set for ycut
    */
   public void setYCut(double ycut)
   {
      if (m_dycut != ycut) m_resultsValid = false;
      m_dycut = ycut;
   }
   /**
    * Call this to input a new event to the jet finder.
    * @param data An Enumeration of either HepLorentzVectors (4Vectors) or Hep3Vectors
    */
   public void setEvent(Collection data)
   {
      setEvent(data,null);
   }
   /**
    * Set the mass to use when converting 3-vectors to 4-vectors
    * This is a static method so the mass applies to all instances
    * of AbstractJetFinder. The default is 0.
    * @param mass The new value to use
    */
   public void setAssumedMassFor3Vectors(double mass)
   {
      defaultMassSquared = mass*mass;
   }
   
   /**
    * Call this to input a new event to the jet finder.
    * Only elements of the enumeration which are accepted by the predicate will be used
    * for jet finding.
    * @param data An Enumeration of either HepLorentzVectors (4Vectors) or Hep3Vectors
    * @param cut A predicate that is applied to each element of e
    */
   public void setEvent(Collection data, Predicate cut)
   {
      m_resultsValid = false;
      m_in = new ArrayList();
      m_4vec = new ArrayList();
      m_devis = 0;
      for (Iterator i = data.iterator(); i.hasNext(); )
      {
         Object o = i.next();
         if (cut != null && !cut.accept(o)) continue;
         if (o instanceof HepLorentzVector)
         {
            HepLorentzVector in = (HepLorentzVector) o;
            m_devis += in.t();
            m_in.add(in);
            m_4vec.add(in);
         }
         else if (o instanceof Hep3Vector)
         {
            Hep3Vector in = (Hep3Vector) o;
            double energy = Math.sqrt(in.magnitudeSquared() + defaultMassSquared);
            m_devis += energy;
            m_in.add(in);
            m_4vec.add(new BasicHepLorentzVector(energy,in));
         }
         else throw new IllegalArgumentException("Element input to JetFinder is not a IHep3Vector or an IHepLorentzVector");
      }
      m_np = m_4vec.size();
      m_part = new HepLorentzVector[m_np];
   }
   private void doFindJets()
   {
      m_resultsValid = true;
      m_injets = 0;
      if (m_np<2) return;
      
      m_ipart_jet_assoc = new int[m_np];
      for (int m=0; m<m_np; m++) m_ipart_jet_assoc[m] = UNASSOC;
      m_4vec.toArray(m_part);
      
      double esum = m_devis;
      //
      // create invariant mass pair array.
      //
      ymass = new double[m_np][m_np];
      for (int i = 0; i < m_np - 1; i++ )
      {
         for (int j = i + 1 ; j < m_np ; j++ )
         {
            double cmass = calculate_mass(m_part[i], m_part[j]);

            if ( cmass != -9999.0 ) //special just for Geneva algorithm.
            {
               ymass[i][j] = cmass;
            }
            else
            {
               ymass[i][j] = 0.0;
            }
         }
      }
      
      for (;;)
      {
         int im = -1;
         int jm = -1;
         double minmass = Double.MAX_VALUE;
         //
         // find least invariant mass pair.
         //
         for(int i = 0 ; i < m_np - 1 ; i++ )
         {
            if (m_ipart_jet_assoc[i] != UNASSOC) continue;
            for(int j = i + 1 ; j < m_np ; j++ )
            {
               if (m_ipart_jet_assoc[j] != UNASSOC) continue;
               if (ymass[i][j] < minmass)
               {
                  minmass = ymass[i][j];
                  im = i;
                  jm = j;
               }
            }
         }

         if (minmass > masscut(m_dycut,m_devis,esum)) break;
         //
         // combine particles im and jm update associations
         //
         combine_particles(im,jm);
         m_ipart_jet_assoc[jm] = im;
         for(int j = 0 ; j < m_np ; j++ )
         {
            if( m_ipart_jet_assoc[j] == jm )
            {
               m_ipart_jet_assoc[j] = im;
            }
         }
         //
         // Recalculate a mass for all pairs that contain im and a remaining
         // UNASSOC particle(jet). Also recalculate esum.
         //
         esum = 0.0;
         for(int j = 0 ; j < m_np ; j++ )
         {
            if (m_ipart_jet_assoc[j] != UNASSOC ) continue;
            esum = esum + m_part[j].t();
            if( j == im) continue;
            int imin = Math.min(j,im);
            int imax = Math.max(j,im);
            double cmass = calculate_mass(m_part[imin], m_part[imax]);
            if ( cmass >= 0.0 )
            {
               ymass[imin][imax] = cmass;
            }
         }
      }
      //
      // finish up by filling jet array.
      //
      for(int i = 0 ; i < m_np ; i++ )
      {
         if (m_ipart_jet_assoc[i] == UNASSOC) m_injets++;;
      }
      m_jet = new HepLorentzVector[m_injets];
      m_inparts_per_jet = new int[m_injets];
      
      int nj = 0;
      int ntrk;
      m_ifewest_tracks = Integer.MAX_VALUE;
      for(int i = 0 ; i < m_np ; i++ )
      {
         if (m_ipart_jet_assoc[i] != UNASSOC) continue;
         m_jet[nj] = m_part[i];
         ntrk = 1;
         for (int j = 0 ; j < m_np ; j++)
         {
            if(m_ipart_jet_assoc[j] == i)
            {
               m_ipart_jet_assoc[j] = nj;
               ntrk++;
            }
         }
         m_ipart_jet_assoc[i] = nj;
         m_inparts_per_jet[nj] = ntrk;
         if( ntrk < m_ifewest_tracks) m_ifewest_tracks = ntrk;
         nj++;
      }
   }
 
   /**There are a variety of collinear, infra-red safe jet finders available.
    * The differences between them are
    * (1) How they determine the cutoff mass from the ycut parameter.
    * (2) How they combine two four-vectors together as they build up the jet.
    * (3) what mass they assign to a four-vector.
    * The following methods are the algorithms used in these jet finders.
    * A concrete jet finder is an extension of the AbstractJetFinder (the current
    * class) that specifies a masscut method, a combine_particles method and
    * a calculate_mass method.
    */
   //
   // Cutoff mass (masscut) method.
   //
   protected double standard_masscut(double ycut, double evis)
   {
      return ycut*evis*evis;
   }
   protected double geneva_masscut(double ycut)
   {
      return ycut;
   }
   protected double jadeP0_masscut(double ycut, double esum)
   {
      return ycut*esum*esum;
   }
   //
   // combine_particles methods
   //
   protected void four_vector_combine( int im, int jm)
   {
      m_part[im] = VecOp.add( m_part[im], m_part[jm] );
   }
   protected void jadeP_combine( int im, int jm )
   {
      Hep3Vector v = VecOp.add( m_part[im].v3(), m_part[jm].v3() );
      m_part[im] = new BasicHepLorentzVector( v.magnitude(), v);
   }
   protected void jadeE0_combine( int im, int jm )
   {
      HepLorentzVector v = VecOp.add( m_part[im], m_part[jm] );
      double ekeratio = v.t()/v.v3().magnitude();
      m_part[im] = new BasicHepLorentzVector(v.t(),VecOp.mult(ekeratio, m_part[im].v3()));
   }
   //
   // calculate_mass methods
   //
   protected double four_vector_mass(HepLorentzVector part1, HepLorentzVector part2)
   {
      return VecOp.add(part1, part2).magnitudeSquared();
   }
   protected double jade_mass(HepLorentzVector part1, HepLorentzVector part2)
   {
      double e1 = part1.t();
      double e2 = part2.t();
      Hep3Vector v1 = part1.v3();
      Hep3Vector v2 = part2.v3();
      double costh = VecOp.dot(v1,v2)/(v1.magnitude()*v2.magnitude());
      return 2*e1*e2*( 1 - costh );
   }
   protected double durham_mass(HepLorentzVector part1, HepLorentzVector part2)
   {
      double e1 = part1.t();
      double e2 = part2.t();
      Hep3Vector v1 = part1.v3();
      Hep3Vector v2 = part2.v3();
      double costh = VecOp.dot(v1,v2)/(v1.magnitude()*v2.magnitude());
      double lessorE = Math.min(e1,e2);
      return 2 * lessorE*lessorE * ( 1 - costh );
   }
   protected double geneva_mass(HepLorentzVector part1, HepLorentzVector part2)
   {
      double e1 = part1.t();
      double e2 = part2.t();
      if ( e1 == 0.0 & e2 == 0.0 ) return -9999.0;
      Hep3Vector v1 = part1.v3();
      Hep3Vector v2 = part2.v3();
      double costh = VecOp.dot(v1,v2)/(v1.magnitude()*v2.magnitude());
      return (8.0/9.0)*(1 - costh)*(e1*e2)/((e1 + e2)*(e1 + e2));
   }
}
