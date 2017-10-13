package hep.physics.jet;

import hep.physics.vec.HepLorentzVector;

public class JadePJetFinder extends AbstractJetFinder
{
   public JadePJetFinder( double ycut )
   {
      super(ycut);
   }
   final double masscut(double ycut, double evis, double esum)
   {
      return standard_masscut(ycut, evis);
   }
   final double calculate_mass(HepLorentzVector part1, HepLorentzVector part2)
   {
      return four_vector_mass(part1, part2);
   }
   final void combine_particles( int im, int jm )
   {
      jadeP_combine(im, jm);
   }
}
