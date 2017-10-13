package hep.physics.jet;

import hep.physics.vec.HepLorentzVector;

public class GenevaJetFinder extends AbstractJetFinder
{
   public GenevaJetFinder( double ycut )
   {
      super(ycut);
   }
   final double masscut(double ycut, double evis, double esum)
   {
      return geneva_masscut(ycut);
   }
   final double calculate_mass(HepLorentzVector part1, HepLorentzVector part2)
   {
      return geneva_mass( part1, part2 );
   }
   final void combine_particles( int im, int jm )
   {
      four_vector_combine( im, jm );
   }
}
