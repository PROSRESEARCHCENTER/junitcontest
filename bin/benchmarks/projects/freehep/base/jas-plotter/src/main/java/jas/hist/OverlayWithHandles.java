package jas.hist;

import jas.plot.CoordinateTransformation;
import jas.plot.DateCoordinateTransformation;
import jas.plot.DoubleCoordinateTransformation;
import jas.plot.Overlay;
import jas.plot.OverlayContainer;
import jas.plot.PlotGraphics;
import jas.plot.PrintHelper;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class OverlayWithHandles implements Overlay, MouseListener, MouseMotionListener
{
    private Cursor defaultCursor = null;
    private static MouseEvent mouseEvent = null;
    private static boolean changeCursor;
    private boolean handlesPainted = false;
    
	protected OverlayWithHandles(DataSource ds)
	{
		if (ds instanceof HasHandles)
		{
			hasHandles = (HasHandles) ds;
		}
	}
	public void paint(PlotGraphics g)
	{
		if (handles != null && !PrintHelper.isPrinting())
		{
			CoordinateTransformation xt = container.getXTransformation();
			CoordinateTransformation yt = container.getYTransformation();
			g.clearTransformation();
			for (int i=0; i<handles.length; i++)
			{
				handles[i].paint(g,xt,yt);
			}
		}
	}	
        
        void paintHandles(MouseEvent e) {
            handlesPainted = true;
            DoubleCoordinateTransformation xp;
            DoubleCoordinateTransformation yp;

            CoordinateTransformation xt = container.getXTransformation();
            if ( xt instanceof DateCoordinateTransformation )
                xp = new DateTransformationConverter( (DateCoordinateTransformation) xt );
            else
                xp = (DoubleCoordinateTransformation) xt;
                
            CoordinateTransformation yt = container.getYTransformation();
            if ( yt instanceof DateCoordinateTransformation )
                yp = new DateTransformationConverter( (DateCoordinateTransformation) yt );
            else
                yp = (DoubleCoordinateTransformation) yt;
                
                Handle[] temp = hasHandles.getHandles(xp.getPlotMin(),xp.getPlotMax(),yp.getPlotMin(),yp.getPlotMax());
		handles = new HandleWrapper[temp.length];
		for (int i=0; i<handles.length; i++) handles[i] = new HandleWrapper(temp[i]);
		container.repaint(); // Could just paint the handles
        }
 
        public void mouseEntered(MouseEvent e)
	{
            paintHandles(e);
	}
	public void mouseExited(MouseEvent e)
	{
		handles = null;
		container.repaint();
                handlesPainted = false;
	}

        public void mouseMoved(MouseEvent event)
	{
            // If the cursor is already inside the plot area, the method mouseEntered
            // is not invoked and the Handles are not painted. This below is to avoid that.
            if ( ! handlesPainted )
                paintHandles(event);
            
            // Given that there is an OverlayWithHandles for each function
            // this method (mouseMoved) is invoked n times, where n is the number of
            // functions. The static variables "mouseEvent" and "changeCursor" ensure
            // that only one OverlayWithHandles is allowed to change the cursor and
            // to have an active handle.
            if ( mouseEvent == null || mouseEvent != event ) {
                mouseEvent = event;
                changeCursor = true;
            }
            
            if ( defaultCursor == null )
                defaultCursor = event.getComponent().getCursor();

            Point p = event.getPoint();
            if ( handles != null ) {
		for (int i=0; i<handles.length; i++)
		{
			if (handles[i].contains(p)) 
			{
                            Cursor cursor = handles[i].cursor();
                            if ( cursor != null ) {
                                event.getComponent().setCursor( cursor );
                                changeCursor = false;
                            }
                            setCurrentHandle(handles[i]);
                            return;
			}
		}
                
                if ( event.getComponent().getCursor() != defaultCursor && changeCursor ) {
                    event.getComponent().setCursor(defaultCursor);            
                }
            }
		setCurrentHandle(null);
	}
	public void mousePressed(MouseEvent event)
	{
		if (currentHandle != null) captureHandle(currentHandle);
	}
	public void mouseReleased(MouseEvent event)
	{
		captureHandle(null);
	}
	public void mouseClicked(MouseEvent event)
	{
	}
	public void mouseDragged(MouseEvent event)
	{
		if (capturedHandle != null)
		{
			capturedHandle.moveTo(event.getPoint());
			container.repaint();
		}
	}
	public void containerNotify(OverlayContainer c)
	{
		if (container != null)
		{
			container.removeMouseListener(this);
			container.removeMouseMotionListener(this);
		}
		this.container = c;
		if (c != null && hasHandles != null)
		{
			c.addMouseListener(this);
			c.addMouseMotionListener(this);			
		}
	}
	
	
	
	private void setCurrentHandle(HandleWrapper h)
	{
		if (currentHandle != h)
		{
			currentHandle = h;
			container.repaint();
		}
	}
	private void captureHandle(HandleWrapper h)
	{
		if (capturedHandle != h)
		{
			capturedHandle = h;
			container.repaint();
		}
	}
	private class HandleWrapper
	{
		HandleWrapper(Handle h)
		{
			this.handle = h;
		}
		void paint(PlotGraphics g, CoordinateTransformation xt, CoordinateTransformation yt)
		{
                    DoubleCoordinateTransformation xp;
                    DoubleCoordinateTransformation yp;
                    if ( xt instanceof DateCoordinateTransformation )
                        xp = new DateTransformationConverter( (DateCoordinateTransformation) xt );
                    else
                        xp = (DoubleCoordinateTransformation) xt;
                
                    if ( yt instanceof DateCoordinateTransformation )
                        yp = new DateTransformationConverter( (DateCoordinateTransformation) yt );
                    else
                        yp = (DoubleCoordinateTransformation) yt;

                    double x = xp.convert(handle.getX());
                    double y = yp.convert(handle.getY());
                    
                    g.setColor(this == currentHandle ? Color.red : Color.black);
                    g.fillRect(x-handleSize,y-handleSize, x+handleSize,y+handleSize);
		}
		boolean contains(Point p)
		{
			CoordinateTransformation xt = (CoordinateTransformation) container.getXTransformation();
			CoordinateTransformation yt = (CoordinateTransformation) container.getYTransformation();
                    DoubleCoordinateTransformation xp;
                    DoubleCoordinateTransformation yp;
                    if ( xt instanceof DateCoordinateTransformation )
                        xp = new DateTransformationConverter( (DateCoordinateTransformation) xt );
                    else
                        xp = (DoubleCoordinateTransformation) xt;
                
                    if ( yt instanceof DateCoordinateTransformation )
                        yp = new DateTransformationConverter( (DateCoordinateTransformation) yt );
                    else
                        yp = (DoubleCoordinateTransformation) yt;

                    double x = xp.convert(handle.getX())-handleSize;
                    double y = yp.convert(handle.getY())-handleSize;
                    return (p.x >= x && p.x <= x+2*handleSize && p.y >= y && p.y > y && p.y <= y+2*handleSize);
		}
		void moveTo(Point p)
		{
                    CoordinateTransformation xt = (CoordinateTransformation) container.getXTransformation();
                    CoordinateTransformation yt = (CoordinateTransformation) container.getYTransformation();
                    DoubleCoordinateTransformation xp;
                    DoubleCoordinateTransformation yp;
                    if ( xt instanceof DateCoordinateTransformation )
                        xp = new DateTransformationConverter( (DateCoordinateTransformation) xt );
                    else
                        xp = (DoubleCoordinateTransformation) xt;
                
                    if ( yt instanceof DateCoordinateTransformation )
                        yp = new DateTransformationConverter( (DateCoordinateTransformation) yt );
                    else
                        yp = (DoubleCoordinateTransformation) yt;
			handle.moveTo(xp.unConvert(p.x),yp.unConvert(p.y));
		}
                Cursor cursor() {
                    return handle.cursor();
                }
                
		private Handle handle;
	}
	private static final double handleSize = 2.5;
	private HandleWrapper[] handles = null;
	private HandleWrapper currentHandle = null;
	private HandleWrapper capturedHandle = null;
	private HasHandles hasHandles;
	protected OverlayContainer container;
}
