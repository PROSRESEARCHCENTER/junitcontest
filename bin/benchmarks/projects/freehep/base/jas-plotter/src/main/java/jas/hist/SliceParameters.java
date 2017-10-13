package jas.hist;

/**
 * Defines parameters for a slice. x,y define the center of the slice.
 * height is the half-distance along the slice, while width is the half-distance perpendicular
 * to the slice axis. phi defines the slice direction, phi=0 implies the slice is along 
 * the y-axis. 
 */
public interface SliceParameters
{
	double getX();
	double getY();
	double getWidth();
	double getHeight();
	double getPhi();
	
	void setX(double x);
	void setY(double y);
	void setWidth(double width);
	void setHeight(double height);
	void setPhi(double phi);
}
