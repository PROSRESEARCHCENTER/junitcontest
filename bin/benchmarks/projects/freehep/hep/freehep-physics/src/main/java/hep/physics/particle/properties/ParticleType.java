package hep.physics.particle.properties;

/**
 * Describes physics properties of a particular type of particle
 * @see ParticlePropertyProvider
 * @author Tony Johnson
 */

public interface ParticleType
{
   /**
    * The particle data group ID
    */
   int getPDGID();
   /**
    * The name of the particle, suitable for printing
    */
   String getName();
   /**
    * The rest mass of the particle (in Gev)
    */
   double getMass();
   /**
    * The charge of the particle
    */
   double getCharge();
   /**
    * 2 x Spin
    */
   int get2xSpin();
   /**
    * Width
    */
   double getWidth();
   /**
    * Get the provider of this particle type
    */
   ParticlePropertyProvider getParticlePropertyProvider();
}
