package org.freehep.math.minuit;
/**
 *
 * @version $Id: VariableMetricMinimizer.java 8584 2006-08-10 23:06:37Z duns $
 */
class VariableMetricMinimizer extends ModularFunctionMinimizer
{

  public VariableMetricMinimizer()
  {
     theMinSeedGen = new MnSeedGenerator();
     theMinBuilder = new VariableMetricBuilder();
  }

  public MinimumSeedGenerator seedGenerator() {return theMinSeedGen;}
  public MinimumBuilder builder() {return theMinBuilder;}


  private MnSeedGenerator theMinSeedGen;
  private VariableMetricBuilder theMinBuilder;
   
}
