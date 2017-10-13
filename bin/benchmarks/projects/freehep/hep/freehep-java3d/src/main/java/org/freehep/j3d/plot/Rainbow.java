package org.freehep.j3d.plot;

import javax.vecmath.Color3b;

/**
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: Rainbow.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Rainbow
{
	private double min,max;
	private static Color3b rtable[] = new Color3b[100];
	static
	{
		for(int j=0;j<40;j++)
		{
			byte r = (byte) (255 * (1 - j/40.0));
			byte g = (byte) (255 * (j/40.0));
			byte b = 0;
			rtable[j] = new Color3b(r,g,b);		
		}
		for(int j=40;j<80;j++) 
		{
			byte r =  0;
			byte g = (byte) (255 * (80-j)/40.0);
			byte b = (byte) (255 * (j-40)/40.0);
			rtable[j] = new Color3b(r,g,b);					
		}		
		for(int j=80;j<100;j++) 
		{
			byte r = (byte) (255 * (j-80)/40.0);
			byte g = 0;
			byte b = (byte) (255 * (120-j)/40.0);
			rtable[j] = new Color3b(r,g,b);						
		}
	}
	public Rainbow()
	{
		this(0,1);
	}
	public Rainbow(double min, double max)
	{
		this.min = min;
		this.max = max;
	}
	public Color3b colorFor(double d)
	{
		int i = (int) Math.floor((d-min)*100/(max-min));
		if (i<0) i=0;
		else if (i>=rtable.length) i = rtable.length-1;
		return rtable[i];
 	}
}
