package org.freehep.j3d.plot;
import javax.vecmath.Color3b;

/**
 * A data source for binned 2D data which is used by the both the lego  
 * and the surface plot. Any class which implements this interface can
 * be used to provide Data for a lego or surface plot.
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: Binned2DData.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface Binned2DData
{
	/**
	 * Number of bins on the X axis
	 */
	int xBins();
	
	/**
	 * Number of bins on the Y axis
	 */
	int yBins();
	
	/**
	 * Axis minimum on the X Axis
	 */
	float xMin();
	
	/**
	 * Axis maximum on the X Axis
	 */
	float xMax();
	
	/**
	 * Axis minimum on the Y Axis
	 */
	float yMin();
	
	/**
	 * Axis maximum on  the Y Axis
	 */
	float yMax();
	
	/**
	 * Get Z value at the specified bin
	 */
	float zAt(int xIndex, int yIndex);
	
	/**
	 * Get the Color at the specified bin
	 */
	Color3b colorAt(int xIndex, int yIndex);
	
	/**
	 * Minimum data value on the Z Axis
	 */
	float zMin();
	
	/**
	 * Maximum data value on the Z Axis
	 */
	float zMax();
}
