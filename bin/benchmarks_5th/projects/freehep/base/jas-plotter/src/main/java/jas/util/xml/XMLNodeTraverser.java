package jas.util.xml;

import jas.util.ColorConverter;

import java.awt.Color;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Utility class for traversing XML DOM trees
 */
public abstract class XMLNodeTraverser
{
	public void traverse(Node node) throws BadXMLException
	{
		if (node instanceof Element)
		{
			handleElementAttributes((Element) node);
		}
		for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling())
		{
			handleSubNode(n,n.getNodeName());
		}
	}
	protected void handleSubNode(Node node, String name) throws BadXMLException
	{
        int type = node.getNodeType();
        switch (type) 
		{
            case Node.ELEMENT_NODE: 
				handleElement((Element) node,name);
				break;
			case Node.TEXT_NODE:
				handleTextNode((Text) node,name);
                break;
			default:
				handleOtherNode(node,name);
        }
	}
	protected void handleElementAttributes(Element node) throws BadXMLException
	{
		NamedNodeMap nnm = node.getAttributes();
		for (int i=0; i<nnm.getLength(); i++) 
		{
			Attr attr = (Attr) nnm.item(i);
			handleAttributeNode(attr,attr.getName(),attr.getValue());
		}
	}
	protected void handleElement(Element node, String name) throws BadXMLException
	{
		throw new BadXMLException("Unhandled Element node "+node);
	}
	protected void handleTextNode(Text node, String name) throws BadXMLException
	{
		// Just ignore unhandled text
		//throw new BadXMLException("Unhandled Text node "+node);
	}
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		throw new BadXMLException("Unhandled Attribute node "+node);
	}
	protected void handleOtherNode(Node node, String name) throws BadXMLException
	{
		throw new BadXMLException("Unhandled Other node "+node+" type="+node.getNodeType());
	}
	public int toInt(String value) throws BadXMLException
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (Throwable x)
		{
			throw new BadXMLException("Bad integer value "+value);
		}
	}
	public boolean toBoolean(String value) throws BadXMLException
	{
			if      (value.equalsIgnoreCase("true")) return true;
			else if (value.equalsIgnoreCase("false")) return false;
			else throw new BadXMLException("Bad boolean value "+value);
	}
	public double toDouble(String value) throws BadXMLException
	{
		try
		{
			if( value.equals("NaN")) return Double.NaN; 
			else return new Double(value).doubleValue();
		}
		catch (Throwable x)
		{
			throw new BadXMLException("Bad double value "+value);
		}
	}
	public Color toColor(String value) throws BadXMLException
	{
		try
		{
			return ColorConverter.stringToHTMLColor(value);
		}
		catch (ColorConverter.ColorConversionException x)
		{
			throw new BadXMLException(x.getMessage());
		}								 
	}
	/**
	 * Exception to throw for any kind of XML problem
	 */
	public static class BadXMLException extends Exception 
	{  
		public BadXMLException() { super(); }
		public BadXMLException(String s) { super(s); }
	}
}
