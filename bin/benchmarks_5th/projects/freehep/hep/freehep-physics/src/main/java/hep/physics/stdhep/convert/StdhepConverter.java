package hep.physics.stdhep.convert;

import hep.io.stdhep.*;
import hep.physics.event.generator.GeneratorFactory;
import hep.physics.event.generator.MCEvent;
import hep.physics.particle.BasicParticle;
import hep.physics.particle.Particle;
import hep.physics.particle.properties.ParticlePropertyManager;
import hep.physics.particle.properties.ParticlePropertyProvider;
import hep.physics.particle.properties.ParticleType;
import hep.physics.vec.BasicHep3Vector;
import hep.physics.vec.BasicHepLorentzVector;
import hep.physics.vec.Hep3Vector;
import hep.physics.vec.HepLorentzVector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A class that converts MCEvent<-->StdhepEvent
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StdhepConverter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class StdhepConverter
{
   private ParticlePropertyProvider ppp; 
   private GeneratorFactory factory;
   
   public StdhepConverter()
   {
      this(ParticlePropertyManager.getParticlePropertyProvider());
   }
   public StdhepConverter(ParticlePropertyProvider ppp)
   {
      this(ppp, new GeneratorFactory());
   }
   public StdhepConverter(ParticlePropertyProvider ppp, GeneratorFactory factory)
   {
      this.ppp = ppp;
      this.factory = factory;
   }
   /**
    * Convert from a StdhepEvent to an MCEvent.
    * Useful when reading stdhep files.
    */
   public MCEvent convert(StdhepEvent hepevt)
   {
      MCEvent event = factory.createEvent(0,hepevt.getNEVHEP());
      
      int n = hepevt.getNHEP();
      BasicParticle[] particle = new BasicParticle[n];
      for (int i=0; i<n; i++)
      {
         Hep3Vector origin = new BasicHep3Vector(hepevt.getVHEP(i,0),hepevt.getVHEP(i,1),hepevt.getVHEP(i,2));
         Hep3Vector momentum = new BasicHep3Vector(hepevt.getPHEP(i,0),hepevt.getPHEP(i,1),hepevt.getPHEP(i,2));
         HepLorentzVector p = new BasicHepLorentzVector(hepevt.getPHEP(i,3),momentum);
         ParticleType type = ppp.get(hepevt.getIDHEP(i));
         particle[i] = factory.createParticle(origin,p,type,hepevt.getISTHEP(i), hepevt.getVHEP(i,3));
         particle[i].setMass(hepevt.getPHEP(i,4));
      }
      // Deal with daughters
      for (int i=0; i<n; i++)
      {
         if (hepevt.getJDAHEP(i,0) <= 0) continue;
         for (int j=hepevt.getJDAHEP(i,0)-1;j<hepevt.getJDAHEP(i,1);j++)
         {
            particle[i].addDaughter(particle[j]);
         }
      }
      event.put(MCEvent.MC_PARTICLES,Arrays.asList(particle));
      return event;
   }
   /**
    * Convert from an EventHeader to a StdhepEvent.
    * Useful when writing stdhep files.
    *
    * Note the stdhep format requires all daughters to be stored consecutively.
    * In principle we would have to do a sort on daughter to gaurantee that this could be
    * accomodated. Right now we just throw a RuntimeException if it is not true.
    */
   public StdhepEvent convert(MCEvent event)
   {
      List particles = event.getMCParticles();
      
      int nevhep = event.getEventNumber();
      int nhep = particles.size();
      
      double[] phep = new double[5*nhep];
      double[] vhep = new double[4*nhep];
      int[] isthep = new int[nhep];
      int[] idhep = new int[nhep];
      int[] jmohep = new int[2*nhep];
      int[] jdahep = new int[2*nhep];
      
      int j = 0;
      int k = 0;
      int l = 0;
      int m = 0;     
      
      for (int i=0; i<nhep; i++)
      {
         Particle particle = (Particle) particles.get(i);
         
         idhep[i] = particle.getType().getPDGID();
         isthep[i] = particle.getGeneratorStatus();
         
         phep[j++] = particle.getPX();
         phep[j++] = particle.getPY();
         phep[j++] = particle.getPZ();
         phep[j++] = particle.getEnergy();
         phep[j++] = particle.getMass();
         
         vhep[k++] = particle.getOriginX();
         vhep[k++] = particle.getOriginY();
         vhep[k++] = particle.getOriginZ();
         vhep[k++] = particle.getProductionTime();
         
         // Deal with the particle's parents
         
         Iterator parents = particle.getParents().iterator();
         jmohep[l++] = parents.hasNext() ? particles.indexOf(parents.next()) : 0;
         jmohep[l++] = parents.hasNext() ? particles.indexOf(parents.next()) : 0;
         
         // Deal with the particle's daughters.
         
         Iterator daughters = particle.getDaughters().iterator();
         if (!daughters.hasNext())
         {
            jdahep[m++] = 0;
            jdahep[m++] = -1;
         }
         else
         {
            Object firstDaughter = daughters.next();
            Object lastDaughter = firstDaughter;
            for (int n = particles.indexOf(firstDaughter)+1; daughters.hasNext(); n++)
            {
               lastDaughter = daughters.next();
               if (particles.indexOf(lastDaughter) != n)
                  throw new RuntimeException("Daughters are not consecutive");
            }
            jdahep[m++] = particles.indexOf(firstDaughter);
            jdahep[m++] = particles.indexOf(lastDaughter);
         }
      }
      return new StdhepEvent(nevhep,nhep,isthep,idhep,jmohep,jdahep,phep,vhep);
   }
}
