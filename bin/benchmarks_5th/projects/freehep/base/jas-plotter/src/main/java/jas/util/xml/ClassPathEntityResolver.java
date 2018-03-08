package jas.util.xml;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * An implementation of an EntityResolver which can be used to locate
 * DTD files located on the current java class path
 */
public class ClassPathEntityResolver implements EntityResolver
{
	/**
	 * Constructor
	 * @param DTDName The DTDName to resolve
	 * @oaram root A Class in the same package as the DTD 
	 */
	public ClassPathEntityResolver(String DTDName, Class root)
	{
		this.name = DTDName;
		this.root = root;
	}
	/**
	 * Implementation of resolveEntity method
	 */
	public InputSource resolveEntity(String publicId, String systemId) 
	{
	    if (systemId.endsWith(name)) 
		{
			return new InputSource(root.getResourceAsStream(name));
	    } 
		else 
		{
			// use the default behaviour
			return null;
		}
	}
	private String name;
	private Class root;
}

