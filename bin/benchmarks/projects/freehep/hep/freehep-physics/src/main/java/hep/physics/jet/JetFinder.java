package hep.physics.jet;

import hep.physics.vec.HepLorentzVector;
import hep.physics.filter.Predicate;
import java.util.Collection;
import java.util.List;

/**
 * Interface to be implemented by all jet finders
 * @author Gary Bower
 * @version 12/16/98
 */
public interface JetFinder
{
   /**
    * The number of jets found
    * @return The number of jets found
    */
   int njets();
   /**
    * calculate 4 vector sum for a jet
    * @param index The index of the jet of interest
    * @return The 4 vector of the jet
    */
   HepLorentzVector jet(int index);
   /**
    * Find out which particles are in a paricular jet
    * @param index The index of the jet of interest
    * @return An enumeration of the particles within the jet
    */
   List particlesInJet(int index);
   /**
    * Find out many particles are in a particular jet
    * @param index The index of the jet of interest
    * @return The number of particles
    */
   int nParticlesPerJet(int index);
   /**
    * Find the jet with the fewest particles
    * @return Returns the number of tracks in the jet with the fewest tracks
    */
   int fewestTracks();
   /**
    * Set the current event data
    * @param data An List of 3- or 4-vectors
    */
   void setEvent(Collection data);
   /**
    * Set the current event data
    * @param data An List of 3- or 4-vectors
    * @param cut Only elements of e that are accepted by this predicate will be used in the jet finding
    */
   void setEvent(Collection data, Predicate cut);
}
