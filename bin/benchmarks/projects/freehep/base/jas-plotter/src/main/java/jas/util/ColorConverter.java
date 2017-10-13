package jas.util;

import java.awt.Color;
import java.util.Hashtable;

public final class ColorConverter 
{
	static final Color hexToColor(String value) throws ColorConversionException
	{
		try
		{
			return Color.decode("0x" + value.substring(1));
		}
		catch (Throwable t)
		{
			throw new ColorConversionException(value);
		}
	}
	static final Color rgbToColor(String value)	throws ColorConversionException
	{
		String red, blue, green;
		int comma1, comma2;
		int r,g,b;
		comma1 = value.indexOf(",");
		comma2 = value.indexOf(",", comma1 + 1);
		if (comma1<2 || comma2<comma1+2 || value.length() < comma1+2)
			throw new ColorConversionException(value);
		try
		{
			red = value.substring(1, comma1);
			green = value.substring(comma1 + 1, comma2);
			blue = value.substring(comma2 + 1, value.length() - 1);
			r = Integer.parseInt(red);
			g = Integer.parseInt(green);
			b = Integer.parseInt(blue);
			return new Color(r,g,b);
		}
		catch (Throwable t)
		{
			throw new ColorConversionException(value);
		}
	}
	
	/**
	 * Convert a color string "RED" (case-insensitive) or "#NNNNNN" or "(RRR,GGG,BBB)" to a Color.
	 * @return the Color (null if no match is found)
	 */
	public static Color stringToHTMLColor(String str) throws ColorConversionException
	{
		if (str == null || str.equals("") || str.equals("default")) return null;
		
		//First we handle #NNNNNN and (RRR,GGG,BBB) colors.
		if (str.charAt(0) == '#') return hexToColor(str);
		if (str.charAt(0) == '(') return rgbToColor(str);
		
		Color c = (Color) cc.stringToColor.get(str.toLowerCase());
		if (c == null) throw new ColorConversionException(str);
		return c;
	}
	
	public static String colorToString(Color c) 
	{
		if (c == null) return "default";
		
		String s = (String) cc.colorToString.get(c);
		if (s != null) return s;
		
		//if we got here, we didn't find a match so let's return a nice (RRR,GGG,BBB) String
		return "(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
	}
	private ColorConverter()
	{
		//first we look for the special named Java colors
		addEntry(Color.black,"Black");
		addEntry(Color.blue,"Blue");
		addEntry(Color.cyan,"Cyan");
		addEntry(Color.darkGray,"Dark Gray");
		addEntry(Color.gray,"Gray");
		addEntry(Color.green,"Green");
		addEntry(Color.lightGray,"Light Gray");
		addEntry(Color.magenta,"Magenta");
		addEntry(Color.orange,"Orange");
		addEntry(Color.pink,"Pink");
		addEntry(Color.red,"Red");
		addEntry(Color.white,"White");
		addEntry(Color.yellow,"Yellow");
		
		//now we look for the HTML3.2 colors (we look for all of them since
		//we don't want to depend on the RGB values of Java and HTML3.2 colors
		//being the same)
		addEntry(new Color(0,0,0),"Black");
		addEntry(new Color(192,192,192),"Silver");
		addEntry(new Color(128,128,128),"Gray");
		addEntry(new Color(255,255,255),"White");
		addEntry(new Color(128,0,0),"Maroon");
		addEntry(new Color(255,0,0),"Red");
		addEntry(new Color(128,0,128),"Purple");
		addEntry(new Color(255,0,255),"Fuchsia");
		addEntry(new Color(0,128,0),"Green");
		addEntry(new Color(0,255,0),"Lime");
		addEntry(new Color(128,128,0),"Olive");
		addEntry(new Color(255,255,0),"Yellow");
		addEntry(new Color(0,0,128),"Navy");
		addEntry(new Color(0,0,255),"Blue");
		addEntry(new Color(0,128,128),"Teal");
		addEntry(new Color(0,255,255),"Aqua");
		
		//now we look for the "all hail Crayola" colors :)
		//(we don't look for those which are also Java named colors)
		addEntry(new Color(0.1f, 0.1f, 0.1f),"Gray 10%");
		addEntry(new Color(0.2f, 0.2f, 0.2f),"Gray 20%");
		addEntry(new Color(0.3f, 0.3f, 0.3f),"Gray 30%");
		addEntry(new Color(0.4f, 0.4f, 0.4f),"Gray 40%");
		addEntry(new Color(0.5f, 0.5f, 0.5f),"Gray 50%");
		addEntry(new Color(0.6f, 0.6f, 0.6f),"Gray 60%");
		addEntry(new Color(0.7f, 0.7f, 0.7f),"Gray 70%");
		addEntry(new Color(0.8f, 0.8f, 0.8f),"Gray 80%");
		addEntry(new Color(0.9f, 0.9f, 0.9f),"Gray 90%");
		addEntry(new Color(255, 136, 28),"Orange");
		addEntry(new Color(120, 62, 27),"Brown");
		addEntry(new Color(0, 125, 32),"Forest Green");
		addEntry(new Color(11, 157, 150),"Turquoise");
		addEntry(new Color(109, 0, 168),"Purple");
		addEntry(new Color(168, 0, 126),"Magenta");
		addEntry(new Color(164, 207, 255),"Sky Blue");
		addEntry(new Color(225, 170, 255),"Violet");
		addEntry(new Color(255, 170, 210),"Light Magenta");
	}
	private void addEntry(Color c, String name)
	{
		stringToColor.put(name.toLowerCase(),c);
		colorToString.put(c,name);
	}
	private Hashtable stringToColor = new Hashtable();
	private Hashtable colorToString = new Hashtable();	
	private static ColorConverter cc = new ColorConverter();
	
	public static class ColorConversionException extends Exception
	{
		ColorConversionException(String value)
		{
			super("Cannot convert "+value+" to Color");
		}
	}
}	
