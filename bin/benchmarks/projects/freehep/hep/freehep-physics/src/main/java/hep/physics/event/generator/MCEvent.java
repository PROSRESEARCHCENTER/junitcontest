package hep.physics.event.generator;

import hep.physics.event.HEPEvent;
import java.util.List;

/**
 * A simulated event consisting of a set of MC particles.
 * @author Tony Johnson
 */
public interface MCEvent extends HEPEvent
{
   /**
    * Returns the list of Particles associated with this event
    */
   List getMCParticles();
   public static String MC_PARTICLES = "MCParticle";
}
