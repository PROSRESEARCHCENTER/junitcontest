package hep.physics.event.generator;

/**
 * An interface to be implemented by all event generators.
 * @author Tony Johnson
 */
public interface EventGenerator
{
   MCEvent generate();
   void reset();
}
