package jas.hist;

import java.awt.Cursor;

public abstract class Handle
{
	public abstract double getX();
	public abstract double getY();
	public abstract void moveTo(double x,double y);

        public Cursor cursor() {
            return null;
        }
}
