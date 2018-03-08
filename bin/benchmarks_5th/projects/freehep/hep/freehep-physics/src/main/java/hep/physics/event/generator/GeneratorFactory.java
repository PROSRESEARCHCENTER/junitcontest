package hep.physics.event.generator;

import hep.physics.particle.Particle;
import hep.physics.particle.properties.ParticleType;
import hep.physics.vec.Hep3Vector;
import hep.physics.vec.HepLorentzVector;
import java.util.List;
import hep.physics.particle.BasicParticle;
import hep.physics.event.generator.diagnostic.*;

/**
 * A factory used for creating events and particles.
 * The user can override this class to provide custom implementations
 * of event or particle.
 * @author tonyj
 */

public class GeneratorFactory
{
   /**
    * Create a new event
    * @param run The run number
    * @param event The event number
    * @return The created event.
    */
   public MCEvent createEvent(int run, int event)
   {
      return new GeneratorEvent(run, event);
   }
   /**
    * Create a new particle
    * @param origin The particles creation point
    * @param p The particles momentum and energy
    * @param ptype The particles type
    * @param status The particles status
    * @param time The particles creation time
    * @return The newly created particle
    */
   public BasicParticle createParticle(Hep3Vector origin,HepLorentzVector p,ParticleType ptype,int status, double time)
   {
      return new BasicParticle(origin,p,ptype,status,time);
   }
}