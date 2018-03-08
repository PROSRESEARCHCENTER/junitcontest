package jas.util;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.swing.ImageIcon;

/**
 * A convenience class for creating small icons where the source file is stored in the
 * CLASSPATH of the application, typically in the same JAR file as the class creating the
 * image
 * 
 * @author Tony Johnson
 */
public class JASIcon extends ImageIcon
{
	/**
	 * Create a JASIcon, for use within the jas.util package
	 * The file is interpreted to be relative to jas.util
	 */
	JASIcon(String file) throws ImageException
	{
		this(null,file);
	}
	/**
	 * Create a JASIcon from the CLASSPATH. 
	 * @param obj The object used as the root of the file path
	 * @param file The path to the image source file, relative to obj
	 * @exception ImageException Thrown if the image source file can not be found
	 * @see #create(Object obj, String file)
	 */
	public JASIcon(Object obj,String file) throws ImageException
	{
		this(obj == null ? null : obj.getClass(),file);
	}
	/**
	 * Create a JASIcon from the CLASSPATH. 
	 * @param c The class used as the root of the file path
	 * @param file The path to the image source file, relative to the root class
	 * @exception ImageException Thrown if the image source file can not be found
	 * @see #create(Object c, String file)
	 */
	public JASIcon(Class c, String file) throws ImageException
	{
		m_c = c == null ? JASIcon.class : c;
		setImageImpl(file);
	}
	/**
	 * Create a JASIcon without specifing the source
	 * @param Class c The class used as the root of the file path when setImage is called
	 * @see #setImage(String file)
	 */
	public JASIcon(Class c)
	{
		m_c = c == null ? JASIcon.class : c;
	}
	/**
	 * This method sets the image of the JASIcon to be that referenced by file. 
	 * The image will display as "broken" if the file can not be found, no exception is thrown
	 * @param file The path to the image source file, relative to the root class
	 * 
	 */
	public void setImage(String file)
	{
		try
		{
			setImageImpl(file);
		}
		catch (ImageException x)
		{
			setImage(brokenIcon.getImage());
		}
		
	}
	private void setImageImpl(String file) throws ImageException
	{
		m_file = file;

		Image image = (Image) imageCache.get(this);
		if (image == null)
		{
			try
			{
				// First try getting a URL for the image, and loading the image using the URL.
				java.net.URL url = m_c.getResource(file);
				if (url != null)
				{
					image = Toolkit.getDefaultToolkit().getImage(url);
				}
				// Some classloaders (notably our own custom class loader for JAR files) cannot
				// return a resource URL but can return the resource as an InputStream, so....
				else
				{
					byte[] data = this.getImageBytes(m_c,m_file);
					if (data == null) throw new ImageException();
					image = Toolkit.getDefaultToolkit().createImage(data);
				}
				imageCache.put(this,image);
			}
			catch (ImageException x)
			{
				imageCache.put(this,noImage);
				throw x;
			}
		}
		else if (image == noImage) throw new ImageException();
		setImage(image);
	}
	/**
	 * Read a string of byes from a file.
	 * We do this to work around a limitation in our custom class loader which results in them
	 * being able to suport getResourceAsStream but not getResource
	 * @see jas.loader.ClassPathLoader#getResource
	 */
	private byte[] getImageBytes(Class c, String file) throws ImageException
	{
		try
		{
			//System.out.println("JASIcon: Using fallback method of loading image "+file);
			InputStream stream = c.getResourceAsStream(file);
			if (stream == null) return null;
			//TODO: There is no realiable way to tell how many bytes are in the stream
			//      so we have this horrible hack here.
			byte[] result = new byte[Math.min(10000,stream.available())];
			int size = 0;
			for (;;)
			{
				if (size == result.length) throw new ImageException("Image too big for buffer");
				int rc = stream.read(result,result.length-size,size);
				if (rc<=0) break;
				size += rc;
			}
			stream.close();
			return result;
		}
		catch (IOException x)
		{
			return null;
		}
	}
	/**
	 * Override Objects hashCode method
	 */
	public int hashCode()
	{
		return m_file.hashCode() + m_c.hashCode();
	}
	/**
	 * Override Objects equals method. Two images are considered equal if they have the same 
	 * class and file.
	 */
	public boolean equals(Object in)
	{
		if (!(in instanceof JASIcon)) return false;
		JASIcon icon = (JASIcon) in;
		if (icon.m_c != m_c) return false;
		return icon.m_file.equals(m_file);
	}
	/**
	 * Create a JASIcon but do not throw an exception if the source cannot be found
	 * (just displays a "broken" icon instead)
	 * @param obj The object used as the root of the file path
	 * @param file The path to the image source file, relative to obj
	 * @return The resulting JASIcon
	 */
	public static JASIcon create(Object obj,String file)
	{
		try
		{
			return new JASIcon(obj,file);
		}
		catch (ImageException x)
		{
			return brokenIcon;
		}
	}
	/**
	 * Create a JASIcon but do not throw an exception if the source cannot be found
	 * (just displays a "broken" icon instead)
	 * //TODO: Should create return a cached version of the JASIcon, instead of a cached
	 * //      version of the image with a new JASIcon object?
	 * @param obj The class used as the root of the file path
	 * @param file The path to the image source file, relative to obj
	 * @return The resulting JASIcon
	 */
	public static JASIcon create(Class c,String file)
	{
		try
		{
			return new JASIcon(c,file);
		}
		catch (ImageException x)
		{
			return brokenIcon;
		}
	}
	private Class m_c;
	private String m_file;
	private static Hashtable imageCache = new Hashtable();
	private static JASIcon brokenIcon;
	private static Image noImage; // Used to flag non existent image in cache
	static
	{
		try
		{
			brokenIcon = new JASIcon("brokenIcon.gif");
			noImage = brokenIcon.getImage();
		}
		catch (ImageException x) 
		{
			System.err.println("Could not load brokenIcon .. this looks bad!");
		}
	}
}
