package hep.physics.particle.properties;
/**
 * This class represents the collected particle properties. It allows access to the
 * properties of a specific particle via the <code>get</code> method. By default it
 * use <code>DefaultParticleProperties</code> as the source of particle data, but this
 * can be overriden by setting a different ParticlePropertyProvider using the
 * <code>setParticlePropertyProvider</code> method.
 * @see ParticleType ParticlePropertyProvider DefaultParticlePropertyProvider
 * @author Tony Johnson
 */

public class ParticlePropertyManager
{
   public static ParticlePropertyProvider getParticlePropertyProvider()
   {
      return thePPP;
   }
   public static ParticlePropertyProvider getParticlePropertyProvider(String name)
   {
      return thePPP;
   }
   private static final ParticlePropertyProvider thePPP = new DefaultParticlePropertyProvider();
}
