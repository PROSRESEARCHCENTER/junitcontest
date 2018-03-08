package jas.hist;
import jas.plot.DoubleCoordinateTransformation;
import jas.plot.PlotGraphics;

import java.awt.Color;

class ProjectionData extends SliceData
{
	ProjectionData(SliceParameters p)
	{
		super(p);
	}
	void paint(PlotGraphics g, DoubleCoordinateTransformation xt, DoubleCoordinateTransformation yt)
	{
		// Note: phi is the direction in which we project, so the line we project
		// onto is at 90 degrees to that
		double phi = Math.PI/2 + parm.getPhi()+2*Math.PI;
		double phi0 = Math.atan2(xt.getPlotMax()-xt.getPlotMin(),yt.getPlotMax()-yt.getPlotMin())+2*Math.PI;
		
		double yc = (yt.getPlotMin()+yt.getPlotMax())/2;
		double xc = (xt.getPlotMin()+xt.getPlotMax())/2;
		double dx, dy;
		phi %= Math.PI;
		phi0 %= Math.PI;
		if (phi<phi0 || phi > Math.PI - phi0)
		{
			dy = (yc - yt.getPlotMin());
			dx = - dy*Math.tan(phi);
		}
		else 
		{
			dx = (xc - xt.getPlotMin());
			dy = dx * Math.tan(phi - Math.PI/2);
		}
		g.setColor(Color.black);
		g.drawLine(xc-dx, yc-dy, xc+dx,yc+dy);
	}
	public Handle[] getHandles(double xlow, double xhigh, double ylow, double yhigh)
	{
		double xc = (xlow+xhigh)/2;
		double yc = (ylow+yhigh)/2;
		double dx = (xhigh-xlow)/4;
		double dy = (yhigh-ylow)/4;
		double phi0 = Math.atan2(dx,dy)+2*Math.PI;
		double phi = Math.PI/2 + parm.getPhi()+2*Math.PI;
		phi %= Math.PI;
		phi0 %= Math.PI;
		double x,y;
		if (phi < phi0 || phi > Math.PI - phi0)
		{
			y = yc+dy;
			x = xc-dy*Math.tan(phi);
		}
		else
		{
			x = xc+dx;
			y = yc+dx*Math.tan(phi - Math.PI/2);
		}
		Handle[] result = { new ProjectionHandle(x,y,xc,yc) };
		return result;
	}
	private class ProjectionHandle extends Handle
	{
		ProjectionHandle(double x, double y, double xc, double yc)
		{
			this.x = x;
			this.y = y;
			this.xc = xc;
			this.yc = yc;
		}
		public double getX()
		{
			return x;
		}
		public double getY()
		{
			return y;
		}
		public void moveTo(double xNew, double yNew)
		{
			x = xNew;
			y = yNew;
			double phiNew = Math.atan2(yNew-yc,xNew-xc);
			parm.setPhi(phiNew);		
		}
		private double x,y;
		private double xc,yc;
	}
}
class SliceData implements HasHandles, DataSource
{
	SliceData(SliceParameters p)
	{
		this.parm = p;
	}
	/***gpg***	4/20/00
	I would have called these signW and signH respectively. Since they are
	used to tell whether the width or height of a particular point is to the
	right/left or up/down relative to the center handle.  The numbering of
	handles is as follow for MiddleHandles:
	                      +     2     +
	                      3     +     1
	                      +     0     +
						 for CornerHandles:
	                      2     +     1
	                      +     +     +
	                      3     +     0
	note that the width and the height are actually the halfwidth and halfheight.
	***gpg***/
	private final static int[] signA = { +1, +1, -1, -1 };
	private final static int[] signB = { -1, +1, +1, -1 };	
	protected SliceParameters parm;
	
	void paint(PlotGraphics g, DoubleCoordinateTransformation xt, DoubleCoordinateTransformation yt)
	{
		g.setColor(Color.lightGray);
		g.drawLine(getCornerX(1),getCornerY(1),getCornerX(2),getCornerY(2));
		g.drawLine(getCornerX(2),getCornerY(2),getCornerX(3),getCornerY(3));
		g.drawLine(getCornerX(3),getCornerY(3),getCornerX(0),getCornerY(0));
		g.setColor(Color.black);
		g.drawLine(getCornerX(0),getCornerY(0),getCornerX(1),getCornerY(1));
	}
	private double getCornerX(int index)
	{
    /***gpg***  4/20/00
    	Translation + Rotation ---	I don't understand the significance of sin(-phi) or cos(-phi)
									so I have use what I believe is standard notation.
									see above note for the significance of signA and signB
	    	note:	x = signB[index]*parm.getWidth()
					y = signA[index]*parm.getHeight()
					Rotation:
					x' =  x*cos(phi) + y*sin(phi)
					y' = -x*sin(phi) + y*cos(phi)
					Translation:
					x" = x0 + x'
					y" = y0 + y'
	***gpg***/
	//	return parm.getX() - signA[index]*parm.getHeight()*Math.sin(-parm.getPhi())
	//	                   + signB[index]*parm.getWidth() *Math.cos(-parm.getPhi());
		return parm.getX() + signB[index]*parm.getWidth() *Math.cos(parm.getPhi())
		                   + signA[index]*parm.getHeight()*Math.sin(parm.getPhi());
	}
	private double getCornerY(int index)
	{//***gpg***  getCornerX.
	//	return parm.getY() + signA[index]*parm.getHeight()*Math.cos(-parm.getPhi())
	//	                   + signB[index]*parm.getWidth() *Math.sin(-parm.getPhi());
		return parm.getY() - signB[index]*parm.getWidth() *Math.sin( parm.getPhi())
		                   + signA[index]*parm.getHeight()*Math.cos( parm.getPhi());
	}
	private double getPhiOffset(int index)
	{
		return Math.atan2(signA[index]*parm.getHeight(),signB[index]*parm.getWidth());
	}

	public Handle[] getHandles(double xlow, double xhigh, double ylow, double yhigh)
	{
		Handle[] result = new Handle[9];
		for (int i=0; i<4; i++) result[i] = new CornerHandle(i);
		for (int i=0; i<4; i++) result[i+4] = new MiddleHandle(i);
		result[8] = new MoveHandle();
		return result;
	}
	/**
	 * Corner handles allow the slice to be resized and rotated while keeping 
	 * the center fixed and the aspect ratio fixed
	 */
	private class CornerHandle extends Handle
	{
		private int index;
		
		CornerHandle(int i)
		{
			index = i;
		}
		public double getX()
		{
			return getCornerX(index);
		}
		public double getY()
		{
			return getCornerY(index);
		}
		public void moveTo(double xNew, double yNew)
		{
			double x = parm.getX();
			double y = parm.getY();
		//	double width = parm.getWidth();											//***gpg*** removed 4/20/00  reason corner handles only change angle.
		//	double height = parm.getHeight();										//***gpg*** removed 4/20/00
			double phiNew = Math.atan2(yNew-y,xNew-x);
		//	double newSize = Math.sqrt((xNew-x)*(xNew-x) + (yNew - y)*(yNew-y));	//***gpg*** removed 4/20/00
		//	double oldSize = Math.sqrt(width*width + height*height);				//***gpg*** removed 4/20/00
			
			parm.setPhi(-phiNew + getPhiOffset(index));
		//	parm.setWidth(width * newSize/oldSize); 								//***gpg*** removed 4/20/00
		//	parm.setHeight(height * newSize/oldSize);								//***gpg*** removed 4/20/00
		}
	}
	/**
	 * Middle handles allow one dimension to be increased or decreased while keeping
	 * the center and opposite dimension fixed. 
	 */
	private class MiddleHandle extends Handle
	{
		private int index;
		
		MiddleHandle(int i)
		{
			index = i;
		}
		public double getX()
		{
			return (getCornerX(index) + getCornerX((index+1)%4))/2;  //***gpg*** removed 4/20/00
		//***gpg this is the same as above.  4/20/00
		//	if(index % 2 == 0)
		//	{	return parm.getX() + (signA[index]*parm.getHeight())*Math.sin(parm.getPhi());
		//	}else
		//	{	return parm.getX() + (signB[index]*parm.getWidth())*Math.cos(parm.getPhi());
		//	}
		}
		public double getY()
		{
			return (getCornerY(index) + getCornerY((index+1)%4))/2;  //***gpg*** removed 4/20/00
		//***gpg this is the same as above.  4/20/00
		//	if(index % 2 == 0)
		//	{	return parm.getY() + (signA[index]*parm.getHeight())*Math.cos(parm.getPhi());
		//	}else
		//	{	return parm.getY() - (signB[index]*parm.getWidth())*Math.sin(parm.getPhi());
		//	}
		}
		public void moveTo(double xNew, double yNew)
		{
			double x = parm.getX();
			double y = parm.getY();
			double phiNew = Math.atan2(yNew-y,xNew-x);
			double newDist = Math.sqrt((xNew-x)*(xNew-x) + (yNew - y)*(yNew-y));
			if (index % 2 == 0) // change height
			{	newDist *= Math.sin(phiNew-parm.getPhi()); //***gpg*** added 4/20/00  reason only allow height changes
				parm.setHeight(Math.abs(newDist));	
			}else
			{	newDist *= Math.cos(phiNew-parm.getPhi()); //***gpg*** added 4/20/00  reason only allow width changes
				parm.setWidth(Math.abs(newDist));
			}
		//	parm.setPhi(-phiNew + (getPhiOffset(index)+getPhiOffset((index+1)%4))/2);  //***gpg*** removed 4/20/00 reason MiddleHandles are not allowed to change the orientation
		}
	}
	private class MoveHandle extends Handle
	{
		public double getX()
		{
			return parm.getX();
		}
		public double getY()
		{
			return parm.getY();
		}
		public void moveTo(double xNew, double yNew)
		{
			parm.setX(xNew);
			parm.setY(yNew);
		}
	}

	public String getTitle()
	{
		return null;
	}
}
class SliceOverlay extends OverlayWithHandles
{
	protected SliceData data;
	
	SliceOverlay(SliceParameters p)
	{
		this(p.getWidth() == Double.POSITIVE_INFINITY ? new ProjectionData(p) : new SliceData(p));
	}
	SliceOverlay(SliceData d)
	{
		super(d);
		data = d;
	}
	public void paint(PlotGraphics g, boolean isPrinting)
	{
		DoubleCoordinateTransformation xt = (DoubleCoordinateTransformation) container.getXTransformation();
		DoubleCoordinateTransformation yt = (DoubleCoordinateTransformation) container.getYTransformation();
		g.setTransformation(xt,yt);

		data.paint(g,xt,yt);
		super.paint(g);
	}
}
