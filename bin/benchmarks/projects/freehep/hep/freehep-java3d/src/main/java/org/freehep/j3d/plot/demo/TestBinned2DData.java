package org.freehep.j3d.plot.demo;
import org.freehep.j3d.plot.*;
import java.io.*;

/**
 * A trivial implementation of Binned2DData for test purposes
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: TestBinned2DData.java 8584 2006-08-10 23:06:37Z duns $
 */

public class TestBinned2DData implements Binned2DData
{
	private int xBins;
	private int yBins;
	private Rainbow rainbow = new Rainbow();
   private float[][] data;
	
	public TestBinned2DData() throws IOException
	{
      BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("test.data")));
      xBins = Integer.parseInt(in.readLine());
      yBins = Integer.parseInt(in.readLine());
      data = new float[xBins][yBins];
      for (int i=0; i<xBins; i++)
         for (int j=0; j<yBins; j++)
            data[i][j] = Float.parseFloat(in.readLine());
      in.close();     
	}

	public int xBins()
	{
		return xBins;
	}

	public int yBins()
	{
		return yBins;
	}

	public float xMin()
	{
		return 0f;
	}

	public float xMax()
	{
		return 1f;
	}

	public float yMin()
	{
		return 0f;
	}

	public float yMax()
	{
		return 1f;
	}

	public float zMin()
	{
		return 0f;
	}
	public float zMax()
	{
		return 1f;
	}

	public float zAt(int xIndex, int yIndex)
	{
		return data[xIndex][yIndex];
	}

	public javax.vecmath.Color3b colorAt(int xIndex, int yIndex)
	{
		return rainbow.colorFor(zAt(xIndex,yIndex));
	}
}
