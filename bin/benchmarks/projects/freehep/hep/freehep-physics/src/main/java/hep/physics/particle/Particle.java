package hep.physics.particle;

import hep.physics.particle.properties.ParticleType;
import hep.physics.vec.Hep3Vector;
import hep.physics.vec.HepLorentzVector;
import java.util.List;

/**
 * Description of a particle equivalent to HEPevt description
 */

public interface Particle
{
   /**
    * Energy
    */
   double getEnergy();
   /**
    * Mass
    */
   double getMass();
   /**
    * Particle Type
    */
   ParticleType getType();
   /**
    * Convenience method for getting particles PDGID
    */
   int getPDGID();
   /**
    * Momentum
    */
   Hep3Vector getMomentum();
   /**
    * Point of origin
    */
   Hep3Vector getOrigin();
   double getOriginX();
   double getOriginY();
   double getOriginZ();
   /**
    * X component of momentum
    */
   double getPX();
   /**
    * Y component of momentum
    */
   double getPY();
   /**
    * Z component of momentum
    */
   double getPZ();
   /**
    * Production time
    */
   double getProductionTime();
   /**
    * Status code, one of FINALSTATE, INTERMEDIATE or DOCUMENTATION
    */
   int getGeneratorStatus();
   /**
    * Returns a list of the decay products of this particle
    */
   List getDaughters();
   /**
    * Returns a list of the parent(s) of this particle
    */
   List getParents();
   /**
    * Returns the particle's charge
    */   
   double getCharge();
   /**
    * Return this particles momentum and energy as a 4-vector
    */
   HepLorentzVector asFourVector();
   
   final static int FINAL_STATE = 1;
   final static int INTERMEDIATE = 2;
   final static int DOCUMENTATION = 3;
}