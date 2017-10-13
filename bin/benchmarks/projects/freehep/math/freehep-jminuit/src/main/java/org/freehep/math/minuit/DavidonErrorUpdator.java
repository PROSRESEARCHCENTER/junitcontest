package org.freehep.math.minuit;

/**
 *
 * @version $Id: DavidonErrorUpdator.java 8584 2006-08-10 23:06:37Z duns $
 */
class DavidonErrorUpdator implements MinimumErrorUpdator
{
   
   public MinimumError update(MinimumState s0, MinimumParameters p1, FunctionGradient g1)
   {
      MnAlgebraicSymMatrix V0 = s0.error().invHessian();
      MnAlgebraicVector dx = MnUtils.sub(p1.vec(),s0.vec());
      MnAlgebraicVector dg = MnUtils.sub(g1.vec(),s0.gradient().vec());
      
      double delgam = MnUtils.innerProduct(dx, dg);
      double gvg = MnUtils.similarity(dg, V0);
      
      MnAlgebraicVector vg = MnUtils.mul(V0,dg);
      
      MnAlgebraicSymMatrix Vupd = MnUtils.sub(MnUtils.div(MnUtils.outerProduct(dx),delgam),MnUtils.div(MnUtils.outerProduct(vg),gvg));
      
      if(delgam > gvg)
      {
         Vupd = MnUtils.add(Vupd,MnUtils.mul(MnUtils.outerProduct(MnUtils.sub(MnUtils.div(dx,delgam),MnUtils.div(vg,gvg))),gvg));
      }
      
      double sum_upd = MnUtils.absoluteSumOfElements(Vupd);
      Vupd = MnUtils.add(Vupd,V0);
      
      double dcov = 0.5*(s0.error().dcovar() + sum_upd/MnUtils.absoluteSumOfElements(Vupd));
      
      return new MinimumError(Vupd, dcov);
   }
   
}
