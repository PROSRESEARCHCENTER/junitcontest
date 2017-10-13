package hep.physics.particle.properties;

import java.util.Set;

/**
 * @see DefaultParticlePropertyProvider
 * @author Tony Johnson
 */

public interface ParticlePropertyProvider
{
   public ParticleType get(int PDGID) throws UnknownParticleIDException;
   /**
    * Returns a List of all particle types defined by this Particle Property Provider
    */
   public Set types();
}
