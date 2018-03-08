package hep.physics.jet;

import hep.physics.vec.HepLorentzVector;


public class DurhamJetFinder extends AbstractJetFinder
{
   public DurhamJetFinder( double ycut )
   {
      super(ycut);
   }
   final double masscut(double ycut, double evis, double esum)
   {
      return standard_masscut(ycut,evis);
   }
   final double calculate_mass(HepLorentzVector part1, HepLorentzVector part2)
   {
      return durham_mass( part1, part2 );
   }
   final void combine_particles( int im, int jm )
   {
      four_vector_combine( im, jm );
   }
}
