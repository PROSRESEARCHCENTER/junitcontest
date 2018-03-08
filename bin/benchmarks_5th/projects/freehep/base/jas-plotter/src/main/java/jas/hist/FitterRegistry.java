package jas.hist;
import jas.util.NestedRuntimeException;

import java.util.Enumeration;
import java.util.Vector;

public class FitterRegistry 
{
	private FitterRegistry()
	{
	}
	/**
	 * Get the (unique) FitterRegistry instance
	 */
	public static FitterRegistry instance()
	{
		return theFitterRegistry;
	}
	/**
	 * Add a fitter to the FitterRegistry
	 * @param c The class to instanciate to get a fitter
	 * @param name The name of the fitter
	 */
	public void registerFitter(Class c, String name)
	{
		registerFitter(createFitterFactory(c,name));
	}
	/**
	 * Create a FitterFactory from a class and a name
	 * @param c The class to instanciate to get a fitter
	 * @param name The name of the fitter
	 */
	public FitterFactory createFitterFactory(Class c, String name)
	{
		try
		{
			return new DefaultFitterFactory(c,name);
		}
		catch (FitterFactoryError e)
		{
			// convert the error to a runtime error
			throw new NestedRuntimeException(e);
		}
	}
	/**
	 * Add a fitter factory to the FitterRegistry
	 * @param f The factory to add
	 */
	public void registerFitter(FitterFactory f)
	{
		m_fitters.addElement(f);
		if (defaultFitterFactory == null) defaultFitterFactory = f;
	}
	/**
	 * Remove a FitterFactory from the registry
	 * @param The FitterFactory to remove
	 */
	public void removeFitterFactory(FitterFactory f)
	{
		m_fitters.removeElement(f);
	}
	/**
	 * Clear the FitterRegistry
	 */
	public void removeAllFitters()
	{
		m_fitters.removeAllElements();
	}
	public Enumeration elements()
	{
		return m_fitters.elements();
	}
	public int size()
	{
		return m_fitters.size();
	}
	public FitterFactory getDefaultFitterFactory()
	{
		return defaultFitterFactory;
	}
	public void setDefaultFitterFactory(FitterFactory f)
	{
		defaultFitterFactory = f;
	}
	/**
	 * Return an instance of the current default fitter
	 */
	public Fitter getDefaultFitter()
	{
		return defaultFitterFactory == null ? null : defaultFitterFactory.createFitter();
	}
	public void setContents(Vector v)
	{
		m_fitters = v;
	}
	private Vector m_fitters = new Vector();
	private static FitterRegistry theFitterRegistry = new FitterRegistry();
	private FitterFactory defaultFitterFactory;
}
