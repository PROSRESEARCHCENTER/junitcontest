package jas.plot;

import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public interface OverlayContainer
{
	CoordinateTransformation getXTransformation();
	CoordinateTransformation getYTransformation();
	CoordinateTransformation getYTransformation(int index);
	void addMouseListener(MouseListener l);
	void removeMouseListener(MouseListener l);
	void addMouseMotionListener(MouseMotionListener l);
	void removeMouseMotionListener(MouseMotionListener l);
	void repaint();
	Image createImage(int width, int height);
	Image createImage(ImageProducer p);
	boolean prepareImage(Image i, ImageObserver o);
}
