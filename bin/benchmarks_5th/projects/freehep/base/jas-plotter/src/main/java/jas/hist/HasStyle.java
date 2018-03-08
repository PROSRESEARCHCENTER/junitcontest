package jas.hist;

/**
 * An interface that can be implemented by any DataSource that wants to have control
 * of the style used to display the data.
 */
public interface HasStyle
{
	/**
	 * This method is called by the plot to determine what style to be used for a plot.
	 * It is the programmers responsibility to return the correct subclass of JASHistStyle
	 * corresponding to the type of data being implemented by the DataSource.
	 * 
	 * @return The style to be used, or null to use the default style.
	 * @see JASHist1DHistogramStyle
	 * @see JASHist2DHistogramStyle
	 * @see JASHistScatterPlotStyle
	 */
	JASHistStyle getStyle();
}
