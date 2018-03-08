package hep.aida.ref.optimizer.jminuit;

import hep.aida.ext.IOptimizer;
import hep.aida.ext.IOptimizerFactory;

/**
 *
 * @author The AIDA team @SLAC.
 *
 */
public class JMinuitOptimizerFactory implements IOptimizerFactory
{   

   public IOptimizer create()
   {
      return create(names[0]);
   }
   
   public IOptimizer create(String name)
   {
      for ( int i = 0; i < names.length; i++ )
         if (names[i].equalsIgnoreCase(name) ) return new JMinuitOptimizer();
      throw new IllegalArgumentException("Cannot create IOptimizer with name "+name);
   }
   
   public String[] optimizerFactoryNames()
   {
      return names;
   }
   
   private String[] names = {"jminuit"}; 
}
