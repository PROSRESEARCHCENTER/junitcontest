package hep.physics.event.generator.diagnostic;

import hep.physics.event.generator.EventGenerator;
import hep.physics.event.generator.MCEvent;
import hep.physics.particle.Particle;
import hep.physics.particle.properties.ParticlePropertyManager;
import hep.physics.particle.properties.ParticlePropertyProvider;
import hep.physics.particle.properties.ParticleType;
import hep.physics.particle.properties.UnknownParticleIDException;
import hep.physics.vec.BasicHep3Matrix;
import hep.physics.vec.BasicHep3Vector;
import hep.physics.vec.BasicHepLorentzVector;
import hep.physics.vec.Hep3Vector;
import hep.physics.vec.HepLorentzVector;
import hep.physics.vec.VecOp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import hep.physics.event.generator.GeneratorFactory;

/**
 * Generates user specified particle type events with user specified ranges.
 * @author Gary Bower
 * @version $Id: DiagnosticEventGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DiagnosticEventGenerator implements EventGenerator
{
   private int m_ievent;
   private int m_irun = 1;
   private ParticleType m_ptype;
   private ParticleType m_ptypeBar;
   private int m_inparts = 1;
   private double m_dlowp = 5;
   private double m_dhighp = 5;
   private double m_dlowcosth = -1;
   private double m_dhighcosth = 1;
   private double m_dlowphi = 0;
   private double m_dhighphi = 2*Math.PI;
   private BasicHep3Vector m_origin = new BasicHep3Vector(0,0,0);
   private double m_dxrange = 0;
   private double m_dyrange = 0;
   private double m_dzrange = 0;
   private double m_dangres = -1;
   boolean m_bseedset = false;
   private long m_lseed;
   private Random myRandom;
   boolean m_branPPBar = false;
   private GeneratorFactory factory;
   private ParticlePropertyProvider provider;
   //
   // Methods to set and print the various parameters.
   //
   /**
    * Create a diagnostic event generator with default particle property provider and 
    * object factory.
    */
   public DiagnosticEventGenerator()
   {
      this(ParticlePropertyManager.getParticlePropertyProvider());
   }
   /**
    * Create a diagnostic event generator with the default object factory.
    * @param ppp The particle property provider to use.
    */
   public DiagnosticEventGenerator(ParticlePropertyProvider ppp)
   {
      this(ppp,new GeneratorFactory());
   }
   /**
    * Create a diagnostic event generator with user supplied 
    * particle property provider and object factory. The object 
    * factory can be used to customize the particle and events
    * created by the generator, for example to provide experiment
    * specific classes.
    * @param ppp The particle property provider to use
    * @param factory The object factory to use.
    */
   public DiagnosticEventGenerator(ParticlePropertyProvider ppp, GeneratorFactory factory)
   {
      this(ppp,factory,new Random());
   }
   /**
    * Create a diagnostic event generator with user supplied 
    * particle property provider and object factory. The object 
    * factory can be used to customize the particle and events
    * created by the generator, for example to provide experiment
    * specific classes.
    * @param ppp The particle property provider to use
    * @param factory The object factory to use.
    * @param random The random number generator to use.
    */
   public DiagnosticEventGenerator(ParticlePropertyProvider ppp, GeneratorFactory factory, Random random)
   {
      this.provider = ppp;
      this.factory = factory;
      setParticleType(provider.get(13)); //muon
      this.myRandom = random;
   }
   
   /**
    * Get the particle property provider being used by this generator
    */
   public ParticlePropertyProvider getParticlePropertyProvider()
   {
      return provider;
   }
   
   public void reset()
   {
      m_ievent = 0;
   }
   /**
    * Sets the run number. Default = 1.
    */
   public void setRunNumber(int nrun)
   {
      m_irun = nrun;
   }
   
   /**
    * Set the particle type using a Java particle type.
    */
   public void setParticleType(ParticleType ptype)
   {
      m_ptype = ptype;
      try
      {
         m_ptypeBar = ptype.getParticlePropertyProvider().get(-1*ptype.getPDGID());
         m_ptypeBar.getMass(); // Force an exception if unknown particle
      }
      catch (UnknownParticleIDException x)
      {
         m_ptypeBar = ptype;
      }
   }
   public ParticleType getParticleType()
   {
      return m_ptype;
   }
   /**
    * Set the number of particles to generate in a single event.
    * If the angular resolution option is selected nparts pairs
    * of particles will be generated.
    * Default = 1.
    */
   public void setNumberOfParticles(int nparts)
   {
      if( nparts < 1 ) throw new  IllegalArgumentException("Invalid number of particles.");
      m_inparts = nparts;
   }
   public int getNumberOfParticles()
   {
      return m_inparts;
   }
   /**
    * Select the momentum range in GeV. Selecting lowp = highp
    * sets lowp as the value to be used.
    * Default = (5,5).
    */
   public void setMomentumRange(double lowp, double highp)
   {
      if( lowp < 0 | highp <= 0 | lowp > highp ) throw new IllegalArgumentException("Invalid momentum range value.");
      m_dlowp = lowp;
      m_dhighp = highp;
   }
   /**
    * Select the cosine theta range between -1 and 1.
    * Setting lowcosth = highcosth sets lowcosth as the
    * value to be used.
    * Default = (-1,1).
    */
   public void setCosthRange(double lowcosth, double highcosth)
   {
      if( lowcosth < -1 | highcosth > 1 | lowcosth > highcosth ) throw new IllegalArgumentException("Invalid costheta range value.");
      m_dlowcosth = lowcosth;
      m_dhighcosth = highcosth;
   }
   /**
    * Select the phi range between 0 and 2*PI in radians.
    * Setting lowphi = highphi sets lowphi as the
    * value to be used.
    * Default = (0,2*PI).
    */
   public void setPhiRange(double lowphi, double highphi)
   {
      if( lowphi > highphi ) throw new IllegalArgumentException("Invalid phi range value.");
      m_dlowphi = lowphi;
      m_dhighphi = highphi;
   }
   /**
    * Select an origin for the particle.
    * Default = (0,0,0).
    */
   public void setOrigin(double x, double y, double z)
   {
      m_origin.setV(x,y,z);
   }
   /**
    * Randomly varies the x origin by +/-dx.
    */
   public void setXRange(double dx)
   {
      m_dxrange = dx;
   }
   /**
    * Randomly varies the y origin by +/-dy.
    */
   public void setYRange(double dy)
   {
      m_dyrange = dy;
   }
   /**
    * Randomly varies the z origin by +/-dz.
    */
   public void setZRange(double dz)
   {
      m_dzrange = dz;
   }
   /**
    * Randomly change between generating particles and anti-particles, if true.
    */
   public void setRandomParticleAntiParticle(boolean ppbar)
   {
      m_branPPBar = ppbar;
   }
   /**
    * For angular resolution studies. If angres is set non-negative
    * then for each particle generated according to the selected
    * parameters a second particle is generated with identical
    * properties except it is rotated angres radians in a randomly
    * chosen direction from the direction of the original particle.
    * To disable this feature set angres < 0.
    * Default = -1 (ie, disabled). Units = radians.
    */
   public void setTwoParticleRes(double angres)
   {
      m_dangres = angres;
   }
   /**
    * Set the seed for the random number generator. If no seed is
    * set the generator selects a seed based on the date and time,
    * thus repeated runs will in general not give the same results.
    * Default = not set. Units are long integers.
    */
   public void setSeed(long seed)
   {
      m_lseed = seed;
      m_bseedset = true;
   }
   /**
    * Print the parameters.
    */
   public void printParameters()
   {
      System.out.println("Diagnostic Generator Parameter Settings.");
      System.out.println("Particle type = "+m_ptype);
      System.out.println("Number of particles per event = "+m_inparts);
      System.out.println("Momentum range = ("+m_dlowp+", "+m_dhighp+")");
      System.out.println("Cosine theta range = ("+m_dlowcosth+", "+m_dhighcosth+")");
      System.out.println("Phi range = ("+m_dlowphi+", "+m_dhighphi+")");
      System.out.println("Particle origin = ("
      +m_origin.x()+", "+m_origin.y()+", "+m_origin.z()+")");
      System.out.println("Origin ranges = ("
      +m_dxrange+", "+m_dyrange+", "+m_dzrange+")");
      System.out.println("Angular resolution = "+m_dangres);
      System.out.println("Random number seed = "+m_lseed);
   }
   //
   // Methods to run the generator.
   //
   /**
    * Generate a single event with nparts particles or nparts
    * pairs of particles if angres is selected.
    */
   public MCEvent generate()
   {
      if (m_ievent == 0 && m_bseedset) myRandom.setSeed(m_lseed);
      int nExpected = m_dangres < 0 ? m_inparts : 2*m_inparts;
      List partVec = new ArrayList(nExpected);
      
      for (int i = 0; i < m_inparts; i++)
      {
         Hep3Vector origin = selectOrigin();
         HepLorentzVector p = selectP();
         ParticleType ptype = (m_branPPBar && myRandom.nextDouble() < 0.5) ? m_ptypeBar : m_ptype;
         //Set status, prod time, ptype, origin and p.
         Particle part = factory.createParticle(origin,p,ptype,Particle.FINAL_STATE,0.0);
         partVec.add(part);
         if(m_dangres >= 0)
         {
            // Strategy to generate twin identical to p except
            // rotated by angres radians from p in a random direction:
            // Choose v with same mom as p but along z axis. Choose a
            // random psi and rotate v to vtwin by angres radians in
            // the psi direction.
            Hep3Vector v = new BasicHep3Vector(0,0,p.v3().magnitude());
            double ranphi = 2*Math.PI*myRandom.nextDouble();
            BasicHep3Matrix rot = new BasicHep3Matrix();
            rot.setActiveEuler( ranphi, m_dangres, 0 );
            Hep3Vector vtwin = VecOp.mult(rot,v);
            // Now calculate theta and phi needed
            // to rotate v to the direction of p and use them
            // to rotate vtwin.
            double theta = Math.acos(p.v3().z()/p.v3().magnitude());
            Hep3Vector pprojxy = new BasicHep3Vector(p.v3().x(),p.v3().y(),0);
            double xymag = pprojxy.magnitude();
            int signy = 1;
            if ( p.v3().y() < 0 ) signy = -1;
            double phi;
            if ( xymag > 0 ) phi = signy*Math.acos(p.v3().x()/xymag);
            else phi = 0;
            rot.setActiveEuler( Math.PI/2 + phi, theta, 0 );
            HepLorentzVector pTwin = new BasicHepLorentzVector(p.t(),VecOp.mult(rot,vtwin));
            //Write status, prod time, ptype, origin and p.
            ParticleType pTwintype = (m_branPPBar && myRandom.nextDouble() < 0.5 ) ? m_ptypeBar : m_ptype;
            Particle twin = factory.createParticle(origin,pTwin,pTwintype,Particle.FINAL_STATE,0.0);
            partVec.add( twin );
         }
      }
      m_ievent++;
      MCEvent event = factory.createEvent(m_irun, m_ievent);
      event.put(MCEvent.MC_PARTICLES,partVec);
      return event;
   }
   //
   //Protected methods for randoming selecting particle properties. The standard
   //selection is based on uniform distributions. User may write their own
   //methods for alternate distributions.
   //
   protected HepLorentzVector selectP()
   {
      double mom = selectMom();
      double theta = Math.acos(selectCosth());
      double phi = selectPhi();
      double px = mom*Math.sin(theta)*Math.cos(phi);
      double py = mom*Math.sin(theta)*Math.sin(phi);
      double pz = mom*Math.cos(theta);
      double mass = m_ptype.getMass();
      double energy = Math.sqrt(mass*mass + mom*mom);
      return new BasicHepLorentzVector(energy, px, py, pz);
   }
   protected Hep3Vector selectOrigin()
   {
      double x = m_origin.x() + m_dxrange*(myRandom.nextDouble() - 0.5);
      double y = m_origin.y() + m_dyrange*(myRandom.nextDouble() - 0.5);
      double z = m_origin.z() + m_dzrange*(myRandom.nextDouble() - 0.5);
      return new BasicHep3Vector(x,y,z);
   }
   protected double selectMom()
   {
      if(m_dlowp == m_dhighp) return m_dlowp;
      else return m_dlowp + (m_dhighp - m_dlowp)*myRandom.nextDouble();
   }
   protected double selectCosth()
   {
      if(m_dlowcosth == m_dhighcosth) return m_dlowcosth;
      else return m_dlowcosth + (m_dhighcosth - m_dlowcosth)*myRandom.nextDouble();
   }
   protected double selectPhi()
   {
      if(m_dlowphi == m_dhighphi) return m_dlowphi;
      else return m_dlowphi + (m_dhighphi - m_dlowphi)*myRandom.nextDouble();
   }
      
   public long getSeed()
   {
      return m_lseed;
   }
   
   public int getRunNumber()
   {
      return m_irun;
   }
   
   public double getTwoParticleRes()
   {
      return m_dangres;
   }
   
   public boolean isRandomParticleAntiParticle()
   {
      return m_branPPBar;
   }
   
   public double getZRange()
   {
      return m_dzrange;
   }
   
   public double getYRange()
   {
      return m_dyrange;
   }
   
   public double getXRange()
   {
      return m_dxrange;
   }
}
