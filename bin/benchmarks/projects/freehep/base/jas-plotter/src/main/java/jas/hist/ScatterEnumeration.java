package jas.hist;

public interface ScatterEnumeration
{
	public boolean getNextPoint(double[] a);
	public void resetEndPoint();
	public void restart();
}
