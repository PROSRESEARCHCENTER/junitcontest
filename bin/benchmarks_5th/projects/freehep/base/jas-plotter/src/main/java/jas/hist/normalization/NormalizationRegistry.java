/*
 * NormalizationRegistry.java
 *
 * Created on January 26, 2001, 5:21 PM
 */

package jas.hist.normalization;

/**
 * The NormalizationRegistry contains a list of NormalizationFactories
 * @author  tonyj
 * @version $Id: NormalizationRegistry.java 11550 2007-06-05 21:44:14Z duns $
 */
public class NormalizationRegistry 
{
    /** Creates a new NormalizationRegistry */
    private NormalizationRegistry() 
    {
    }
    /**
     * Add an entry to the registry
     */
    public void add(NormalizationFactory factory)
    {
    }
    /**
     * Remove an entry from the registry
     */
    public void remove(NormalizationFactory factory)
    {
    }
    /**
     * Find the factory that created a specific normalizer
     */
    public NormalizationFactory findFactory(Normalizer norm)
    {
        // A neat implementation of this would use a weak hashmap
        // so that entries would be GCed when no longer in use.
        // For now we could have a kludgy implementation that assumes
        // that a default factory was used and bases its decission on the
        // class of the entry.
        return null;
    }
    /**
     * Access the unique instance of NormalizationRegistry()
     */
    NormalizationRegistry instance()
    {
        return theRegistry;
    }
    private static NormalizationRegistry theRegistry = new NormalizationRegistry();
}
class DefaultNormalizationFactory implements NormalizationFactory
{
    DefaultNormalizationFactory(Class c)
    {
    }
}
interface NormalizationFactory
{
    //String getName();
    //Icon getIcon();
    //Normalizer createNormalizer();
}
