package hep.physics.event.generator;

import hep.physics.event.BaseEvent;
import java.util.List;

/**
 * EventHeader for an event generator. Provides the output of the
 * generator.
 * @author Gary Bower
 * @version $Id: GeneratorEvent.java 8584 2006-08-10 23:06:37Z duns $
 */

public class GeneratorEvent extends BaseEvent implements MCEvent
{
   public GeneratorEvent(int run, int event)
   {
      super(run,event);
   }
   public List getMCParticles()
   {
      return (List) get(MCEvent.MC_PARTICLES);
   }   
}

