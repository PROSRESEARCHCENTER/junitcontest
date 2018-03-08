package jas.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

	/**
	 * The object factory is just a convenience class for creating
	 * objects from thier Class pointer. It hides some of the messiness
	 * of the java.lang.relect.* classes.
	 */

public class ObjectFactory
{

	/**
	 * Create an ObjectFactory capable of creating objects of a specific class.
	 *
	 * @param c  The class of objects to be created.
	 */

	public ObjectFactory(Class c)
	{
		m_c = c;
	}
	
	/**
	 *  Creates an object from the specified arguments
	 */

	public Object create() throws ObjectFactoryException
	{
		Object[] arg = new Object[0];
		return create(arg);
	}
	
	/**
	 *  Creates an object from the specified arguments
	 */

	public Object create(Object a1) throws ObjectFactoryException
	{
		Object[] arg = new Object[1];
		arg[0] = a1;
		return create(arg);
	}
	
	/**
	 *  Creates an object from the specified arguments
	 */

	public Object create(Object a1,Object a2) throws ObjectFactoryException
	{
		Object[] arg = new Object[2];
		arg[0] = a1;
		arg[1] = a2;
		return create(arg);
	}
	
	/**
	 *  Creates an object from the specified arguments
	 */

	public Object create(Object a1,Object a2,Object a3) throws ObjectFactoryException
	{
		Object[] arg = new Object[3];
		arg[0] = a1;
		arg[1] = a2;
		arg[2] = a3;
		return create(arg);
	}
	
	/**
	 * Checks that the class is declared public.
	 */

	public boolean checkAccess()
	{
		return Modifier.isPublic(m_c.getModifiers());
	}

	/**
	 * Checks that the class inherits from baseClass
	 */

	public boolean inheritsFrom(Class baseClass)
	{
		return baseClass.isAssignableFrom(m_c);
	}

	/**
	 *  Creates an object from the specified arguments
	 */

	public Object create(Object a1,Object a2,Object a3,Object a4) throws ObjectFactoryException
	{
		Object[] arg = new Object[4];
		arg[0] = a1;
		arg[1] = a2;
		arg[2] = a3;
		arg[3] = a4;
		return create(arg);
	}

	/**
	 *  Creates an object from the specified arguments
	 */

	public Object create(Object[] args) throws ObjectFactoryException
	{
		Class[] argc = argClass(args);
		try
		{
			Constructor x = findConstructor(argc);
			return x.newInstance(args);
		}
		catch (NoSuchMethodException e)
		{
			throw new ObjectFactoryException("Error creating object of class "+m_c,e);
		}
		catch (IllegalAccessException e)
		{
			throw new ObjectFactoryException("Error creating object of class "+m_c,e);
		}
		catch (InstantiationException e)
		{
			throw new ObjectFactoryException("Error creating object of class "+m_c,e);
		}
		catch (InvocationTargetException e)
		{
			throw new ObjectFactoryException(
				"Invocation Target Exception for class"+m_c,e.getTargetException());
		}
	}

	/**
	 *  Determines if object can be constrcted from the specified types of arguments
	 */

	public boolean canBeCreatedFrom()
	{
		Class[] argc = new Class[0];
		return canBeCreatedFrom(argc);
	}

	/**
	 *  Determines if object can be constrcted from the specified types of arguments
	 */

	public boolean canBeCreatedFrom(Class c1)
	{
		Class[] argc = new Class[1];
		argc[0] = c1;
		return canBeCreatedFrom(argc);
	}

	/**
	 *  Determines if object can be constrcted from the specified types of arguments
	 */

	public boolean canBeCreatedFrom(Class c1, Class c2)
	{
		Class[] argc = new Class[2];
		argc[0] = c1;
		argc[1] = c2;
		return canBeCreatedFrom(argc);
	}

	/**
	 *  Determines if object can be constrcted from the specified types of arguments
	 */

	public boolean canBeCreatedFrom(Class c1, Class c2, Class c3)
	{
		Class[] argc = new Class[3];
		argc[0] = c1;
		argc[1] = c2;
		argc[2] = c3;
		return canBeCreatedFrom(argc);
	}

	/**
	 *  Determines if object can be constrcted from the specified types of arguments
	 */

	public boolean canBeCreatedFrom(Class c1, Class c2, Class c3, Class c4)
	{
		Class[] argc = new Class[4];
		argc[0] = c1;
		argc[1] = c2;
		argc[2] = c3;
		argc[3] = c4;
		return canBeCreatedFrom(argc);
	}

	/** 
	 * Determines if object can be constrcted from the specified types of arguments
	 */
	
	public boolean canBeCreatedFrom(Class[] argc)
	{
		try
		{
			findConstructor(argc);
			return true;
		}
		catch (NoSuchMethodException e)
		{
			return false;
		}
	}
		
	/** 
	 * Construct a list of argument classes from a list of arguments
	 */
	
	private Class[] argClass(Object[] args)
	{
		Class[] result = new Class[args.length];

		for (int i=0; i<args.length; i++)
		{
			result[i] = args[i].getClass();
			if (result[i] == Double.class   ) result[i] = Double.TYPE;
			if (result[i] == Boolean.class  ) result[i] = Boolean.TYPE;
			if (result[i] == Integer.class  ) result[i] = Integer.TYPE;
			if (result[i] == Float.class    ) result[i] = Float.TYPE;
			if (result[i] == Byte.class     ) result[i] = Byte.TYPE;
			if (result[i] == Character.class) result[i] = Character.TYPE;
			if (result[i] == Long.class     ) result[i] = Long.TYPE;
			if (result[i] == Short.class    ) result[i] = Short.TYPE;
			if (result[i] == Void.class     ) result[i] = Void.TYPE;
	}
		return result;
	}

	/**
	 *  This routine is more than just a copy of java.lang.reflect.getConstructor
	 *  since it also takes into account allowed widening conversions of the arguments
	 */

	private Constructor findConstructor(Class[] argc) throws NoSuchMethodException
	{
		Constructor[] x = m_c.getConstructors();
		outer: for (int i=0; i<x.length; i++)
		{
			Class[] c = x[i].getParameterTypes();
			if (c.length != argc.length) continue;
			
			for (int j=0; j<c.length; j++)
			{
				if (!c[j].isAssignableFrom(argc[j])) continue outer;
			}
			return x[i];
		}
		throw new NoSuchMethodException("No suitable constructor for class "+m_c);
	}

	public Class getSourceClass()
	{
		return m_c;
	}

	private Class m_c; 
}
