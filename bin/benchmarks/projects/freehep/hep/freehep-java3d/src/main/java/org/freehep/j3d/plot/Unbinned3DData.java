package org.freehep.j3d.plot;
import javax.vecmath.Color3b;
import javax.vecmath.Point3f;

/**
 * A data source for unbinned 3D data which is used by the 3D scatter plot. 
 * Any class which implements this interface can be used to provide Data 
 * for a lego or surface plot.
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: Unbinned3DData.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface Unbinned3DData
{
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
	 * Minimum data value on the Z Axis
	 */
	float zMin();
	
	/**
	 * Maximum data value on the Z Axis
	 */
	float zMax();
	/**
	 * The number of points in the scatter plot
	 */
	int getNPoints();
	/**
	 * The x,y,z coordinate of the specified point 
	 */
	Point3f pointAt(int index);
	/**
	 * Get the Color of the specified point
	 */
	Color3b colorAt(int index);

}
