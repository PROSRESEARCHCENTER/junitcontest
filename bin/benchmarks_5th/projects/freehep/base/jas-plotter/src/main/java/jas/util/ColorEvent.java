package jas.util;
import java.awt.AWTEvent;
import java.awt.Color;

public class ColorEvent extends AWTEvent
{
	public final static int COLOREVENT = AWTEvent.RESERVED_ID_MAX + 9000;
	ColorEvent(Object source, Color c)
	{
		super(source,COLOREVENT);
		this.color = c;
	}
	public Color getColor()
	{
		return this.color;
	}
	private Color color;
}
