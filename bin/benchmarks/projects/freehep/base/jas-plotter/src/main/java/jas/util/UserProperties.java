package jas.util;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
/**
 * This class is used to store user prorerties.  There is one instance for the application.  The
 * UserProperties object used is available from the Application object using a <code>getUserProperties()</code>
 * method.
 *  @see Application
 *  @see Application#getUserProperties()
 *  @author Tony Johnson
 *  @author Jonas Gifford
 */
public class UserProperties extends Properties
{
	/**
	 * The Application object will create an instance for the application and save it just before
	 * the application closes.  Therefore, you should not have to use the constructor yourself.  The
	 * UserProperties object used is available from the Application object using a <code>getUserProperties()</code>
	 * method.
	 *  @see Application
	 *  @see Application#getUserProperties()
	 */
	public UserProperties()
	{
		try
		{
			final InputStream input = new FileInputStream(custFile);
			load(input);
			input.close();

			// Ignore personal customization files from early releases

			if (getFloat("property-version",0)<0.9) clear();
			
		}
		catch (IOException x) {}
		setFloat("property-version",0.91f);
      theUserProperties = this;
	}
   // Constructor for use while migrating to freehep framework
   protected UserProperties(Properties def)
   {
      super(def);
      theUserProperties = this;
   }
	/**
	 * @param key the key used to store this property
	 * @param def a default in case the property cannot be retrieved
	 */
	public Rectangle getRectangle(final String key, final Rectangle def)
	{
		try
		{
			final Rectangle result = new Rectangle();
			result.x = getInteger(key.concat("-x"));
			result.y = getInteger(key.concat("-y"));
			result.width = getInteger(key.concat("-w"));
			result.height = getInteger(key.concat("-h"));
			return result;
		}
		catch (Exception e)
		{
			return def;
		}
	}

	/**
	 * @param key the key used to store this property
	 * @param rect the value to store
	 */
	public void setRectangle(final String key, final Rectangle rect)
	{
		put(key.concat("-x"), String.valueOf(rect.x));
		put(key.concat("-y"), String.valueOf(rect.y));
		put(key.concat("-w"), String.valueOf(rect.width));
		put(key.concat("-h"), String.valueOf(rect.height));
	}

	/**
	 * @param key the key used to store this property
	 * @param def a default in case the property cannot be retrieved
	 */
	public java.awt.Color getColor(final String key, final java.awt.Color def)
	{
		try
		{
			return new java.awt.Color(getInteger(key.concat("-r")), getInteger(key.concat("-g")),
				getInteger(key.concat("-b")));
		}
		catch (Exception e)
		{
			return def;
		}
	}

	/**
	 * @param key the key used to store this property
	 * @param c the value to store
	 */
	public void setColor(final String key, final java.awt.Color c)
	{
		put(key.concat("-r"), String.valueOf(c.getRed()));
		put(key.concat("-g"), String.valueOf(c.getGreen()));
		put(key.concat("-b"), String.valueOf(c.getBlue()));
	}

	/**
	 * @param key the key used to store this property
	 * @param def a default in case the property cannot be retrieved
	 */
	public String[] getStringArray(final String key, final String[] def)
	{
		try
		{
			final String[] result = new String[getInteger(key +"-length")];
			for (int i = 0; i < result.length; i++)
				result[i] = getProperty(key +"-"+ i);
			return result;
		}
		catch (Exception e)
		{
			return def;
		}
	}

	/**
	 * @param key the key used to store this property
	 * @param sa the value to store
	 */
	public void setStringArray(final String key, String[] sa)
	{
		if (sa == null)
			sa = new String[0];
			// just output an array of size zero
		put( key +"-length", String.valueOf(sa.length) );
		for (int i = 0; i < sa.length; i++)
			put(key +"-"+ i, sa[i]);
	}

	/**
	 * @param key the key used to store this property
	 * @param def a default in case the property cannot be retrieved
	 */
	public String getString(final String key, final String def)
	{
		try
		{
			final String s = getProperty(key);
			return s == null ? def : s;
		}
		catch (Exception e)
		{
			return def;
		}
	}

	/**
	 * @param key the key used to store this property
	 * @param s the value to store
	 */
	public void setString(final String key, String s)
	{
		if (s == null) s="";
		put(key, s);
	}

	/**
	 * @param key the key used to store this property
	 * @param def a default in case the property cannot be retrieved
	 */
	public boolean getBoolean(final String key, final boolean def)
	{
		final String value = getProperty(key);
		return value==null ? def : Boolean.valueOf(value).booleanValue();
	}

	/**
	 * @param key the key used to store this property
	 * @param value the value to store
	 */
	public void setBoolean(final String key, final boolean value)
	{
		put(key,String.valueOf(value));
	}

	/**
	 * @exception NumberFormatException if the property retrieved cannot be converted to <code>int</code>
	 * @param key the key used to store this property
	 */
	public int getInteger(final String key) throws NumberFormatException
	{
		return Integer.valueOf(getProperty(key)).intValue();
	}

	/**
	 * @param key the key used to store this property
	 * @param def a default in case the property cannot be retrieved
	 */
	public int getInteger(final String key, final int def)
	{
		try
		{
			return Integer.valueOf(getProperty(key)).intValue();
		}
		catch (Exception e)
		{
			return def;
		}
	}

	/**
	 * @param key the key used to store this property
	 * @param f the value to store
	 */
	public void setFloat(final String key, final float f)
	{
		put(key, String.valueOf(f));
	}

	/**
	 * @param key the key used to store this property
	 * @param def a default in case the property cannot be retrieved
	 */
	public float getFloat(final String key, final float def)
	{
		try
		{
			return Float.valueOf(getProperty(key)).floatValue();
		}
		catch (Exception e)
		{
			return def;
		}
	}

	/**
	 * @param key the key used to store this property
	 * @param i the value to store
	 */
	public void setInteger(final String key, final int i)
	{
		put(key, String.valueOf(i));
	}

	/**
	 * Saves the properties.  The application does this for you just before closing, so
	 * you don't have to call this method.
	 */
	public void save() throws IOException
	{
		final OutputStream output = new FileOutputStream(custFile);
		save(output,"Java Analysis Studio Custom Properties");
		output.close();
	}

	/**
	 * This is a utility method for updating a string array of recently used items.
	 * Supply it with an old array and a new item.  If the new item was already in the
	 * old array, then it is simply moved to the beginning.  If it was not in the old
	 * array then it is placed at the front and the other items are shuffled back.
	 * The method will return an array with a maximum size of 4.
	 *  @param oldArray the array to update (may safely be <code>null</code>)
	 *  @param newString the new item to include
	 *  @return the updated array
	 */
	public static String[] updateStringArray(final String[] oldArray, final String newString)
	{
		return updateStringArray(oldArray, newString, 4);
	}

	/**
	 * This is a utility method for updating a string array of recently used items.
	 * Supply it with an old array and a new item.  If the new item was already in the
	 * old array, then it is simply moved to the beginning.  If it was not in the old
	 * array then it is placed at the front and the other items are shuffled back.
	 * The method will return an array with a maximum size defined by the integer
	 * stored by the given key, or 4 if such an integer cannot be found.
	 *  @param oldArray the array to update (may safely be <code>null</code>)
	 *  @param newString the new item to include
	 *  @param lengthKey the key used to find the maximum length of the resulting array
	 *  @return the updated array
	 */
	public String[] updateStringArray(final String[] oldArray, final String newString, final String lengthKey)
	{
		return updateStringArray(oldArray, newString, getInteger(lengthKey, 4));
	}

	/**
	 * This is a utility method for updating a string array of recently used items.
	 * Supply it with an old array and a new item.  If the new item was already in the
	 * old array, then it is simply moved to the beginning.  If it was not in the old
	 * array then it is placed at the front and the other items are shuffled back.
	 * The method will return an array of a maximum size given by the <code>nStored</code> parameter.
	 *  @param oldArray the array to update (may safely be <code>null</code>)
	 *  @param newString the new item to include
	 *  @param nStored the maximum size of the updated array
	 *  @return the updated array
	 */
	public static String[] updateStringArray(String[] oldArray, final String newString, final int nStored)
	// called by either updateRecentClasses or by updateRecentServers
	// (both do basically the same thing so a private method that
	//  contains the essential parts of both is practical)
	{
		if (newString == null) return oldArray;
		if (oldArray != null && oldArray.length > 0)
		{
			if (oldArray[0].equals(newString)) return oldArray;
			/*
			 * If the new String is already at the front, then
			 * there is nothing left to do.
			 */
			for (int i = 1; i < oldArray.length; i++)
			/*
			 * Begin checking from the second element
			 * onwards: is the String already there?
			 */
			{
				if (oldArray[i].equals(newString))
				/*
				 * If it is already there, put
				 * the new one at the front and
				 * shuffle the others backward.
				 */
				{
					while (i > 0)
					{
						oldArray[i] = oldArray[i-1];
						i--;
					}
					oldArray[0] = newString;
					return oldArray;
				}
			}
			if (oldArray.length < nStored)
			{
				final String[] result = new String[oldArray.length + 1];
				result[0] = newString;
				for (int i = 0; i < oldArray.length; i++)
					result[i + 1] = oldArray[i];
				return result;
			}
			else
			{
				for (int i = nStored-1; i > 0; i--)
					oldArray[i] = oldArray[i-1];
				oldArray[0] = newString;
				return oldArray;
			}
		}	
		else
		{
			oldArray = new String[1];
			oldArray[0] = newString;
			return oldArray;
		}
	}
   public static UserProperties getUserProperties()
   {
      return theUserProperties;
   }
	final private String custFile = System.getProperty("user.home").concat(File.separator+"jas.properties");
   private static UserProperties theUserProperties;
}
