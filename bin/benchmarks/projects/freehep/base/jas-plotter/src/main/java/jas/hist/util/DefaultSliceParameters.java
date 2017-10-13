package jas.hist.util;
import jas.hist.SliceParameters;

/**
 * Basic implementation of SliceParameters
 * @see jas.hist.SliceParameters
 */
public class DefaultSliceParameters implements SliceParameters
{
	double x;
	double y;
	double width;
	double height;
	double phi;
	
	public DefaultSliceParameters(double x, double y, double width, double height, double phi)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.phi = phi;
	}
	public String toString()
	{
		StringBuffer b = new StringBuffer();
		b.append("SliceParameters: x="+x);
		b.append(",y="+y);
		b.append(",width="+width);
		b.append(",height="+height);
		b.append(",phi="+phi);
		return b.toString();
	}

	public double getX()
	{
		return x;
	}
	public double getY()
	{
		return y;
	}
	public double getWidth()
	{
		return width;
	}
	public double getHeight()
	{
		return height;
	}
	public double getPhi()
	{
		return phi;
	}
	public void setX(double x)
	{
		if (x != this.x)
		{
			this.x = x;
			changed();
		}
	}
	public void setY(double y)
	{
		if (this.y != y)
		{
			this.y = y;
			changed();
		}
	}
	public void setWidth(double width)
	{
		if (this.width != width)
		{
			this.width = width;
			changed();
		}
	}
	public void setHeight(double height)
	{
		if (this.height != height)
		{
			this.height = height;
			changed();
		}
		this.height = height;
	}
	public void setPhi(double phi)
	{
		if (this.phi != phi)
		{
			this.phi = phi;
			changed();
		}
	}
	protected void changed()
	{
	}
}
