package hep.physics.particle.properties;

/**
 * Thrown in response to a request for a particle with an unrecognized PDGID
 * @see DefaultParticlePropertyProvider ParticlePropertyProvider
 * @author Tony Johnson
 */
public class UnknownParticleIDException extends RuntimeException
{
   public UnknownParticleIDException(int pdgid)
   {
      super("Unknown particle "+pdgid);
      this.id = pdgid;
   }
   public int getPDGID()
   {
      return id;
   }
   private int id;
}
