package jas.hist;

public interface Rebinnable2DHistogramData
	extends DataSource
{
	public double[][][] rebin(int xbins, double xmin, double xmax,
		                      int ybins, double ymin, double ymax, 
							  boolean wantErrors, boolean hurry, boolean overflow);
	public double getXMin();
	public double getXMax();
	public double getYMin();
	public double getYMax();
	public int getXBins();
	public int getYBins();
	public boolean isRebinnable();
	public int getXAxisType();
	public int getYAxisType();
	public String[] getXAxisLabels();
	public String[] getYAxisLabels();
}
