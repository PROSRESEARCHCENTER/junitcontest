package org.freehep.j3d.plot;
import javax.vecmath.Color3b;

/**
 * The Binned2DDataNormalizer class is responsible for taking the user provided Binned2DData
 * interface, and mapping it to the data format used internally.
 *
 * This involves normalizing the x,y,z axis to go from 0 to 1, and ensuring that we
 * return 0 if the bin indexes are outside the allowed range. This routine also caches
 * a local copy of the data to speed up access in the LegoBuilder class.
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: NormalizedBinned2DData.java 8584 2006-08-10 23:06:37Z duns $
 */

class NormalizedBinned2DData
{
	private int xBins;
	private int yBins;
	private float[][] data;
	private Color3b[][] color;

	NormalizedBinned2DData(Binned2DData in)
	{
		this.initialize(in);
	}
	void initialize(Binned2DData in)
	{
		xBins = in.xBins();
		yBins = in.yBins();

		// Copy the data to a local array, and calculate the Zmin, Zmax

		data = new float[xBins][yBins];
		color = new Color3b[xBins][yBins];

		float zMin = +Float.MAX_VALUE;
		float zMax = -Float.MAX_VALUE;

		for (int i=0; i<xBins; i++)
		{
			for (int j=0; j<yBins; j++)
			{
				float z = in.zAt(i,j);
				if (z < zMin) zMin = z;
				if (z > zMax) zMax = z;
				data[i][j] = z;
				color[i][j] = in.colorAt(i,j);
			}
		}
		//System.out.println("zMin = "+zMin+", zMax = "+zMax);

		// Now normalize the Z values
		for (int i=0; i<xBins; i++)
		{
			for (int j=0; j<yBins; j++)
			{
				float z = data[i][j];
				data[i][j] = (z-zMin)/(zMax-zMin);
			}
		}
	}

	int xBins()
	{
		return xBins;
	}
	int yBins()
	{
		return yBins;
	}
	float zAt(int xIndex, int yIndex)
	{
		try
		{
			return data[xIndex][yIndex];
		}
		catch (ArrayIndexOutOfBoundsException x)
		{
			return 0;
		}
	}
	Color3b colorAt(int xIndex, int yIndex)
	{
		try
		{
			return color[xIndex][yIndex];
		}
		catch (ArrayIndexOutOfBoundsException x)
		{
			return null;
		}
	}
}
