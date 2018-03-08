package jas.hist;
import jas.util.NestedRuntimeException;
import jas.util.ObjectFactory;
import jas.util.ObjectFactoryException;

class DefaultFitterFactory extends ObjectFactory implements FitterFactory
{
	DefaultFitterFactory(Class c, String name) throws FitterFactoryError
	{
		super(c);
		this.name = name;

		// Class must be a subclass of Fitter

		if (!inheritsFrom(Fitter.class))
			throw new FitterFactoryError("Function "+name+" does not inherit from Basic1DFunction");
		
		// Class must be declared public

		if (!checkAccess())
			throw new FitterFactoryError("Function "+name+" is not declared public");
		
		// The function needs to have a suitable constructor

		if (!canBeCreatedFrom())
			throw new FitterFactoryError("Function "+name+" does not have a suitable constructor");
	}
	public Fitter createFitter()
	{
		try
		{
			return (Fitter) create();
		}
		catch (ObjectFactoryException x)
		{
			throw new NestedRuntimeException("Unexpected failure to create Fitter "+name,x);
		}
	}
	public String getFitterName()
	{
		return name;
	}
	public String toString()
	{
		return name;
	}
	private String name;
}
