package jas.hist;

public interface HasSlices
{
	/** 
	 * Number of slices currently attached to plot
	 */
	int getNSlices();
	/**
	 * Get the parameters of a slice. The SliceParameters can
	 * subsequently be changed to update the slice.
	 * @param n The index of the slice
	 * @return The Slice parameters of the slice with the specified index
	 */
	SliceParameters getSliceParameters(int n);
	/**
	 * Get the data corresponding to the slice. The data will be observable
	 * and will notify its observers if the slice changes, or the data from
	 * which the slice is derived changes
	 * @param n  The index of the slice
	 * @return The data resulting from the slice
	 */
	Rebinnable1DHistogramData getSlice(int n);
	/*
	 * True of the data source allows slices to be added and removed
	 */
	boolean canAddRemoveSlices();
	/**
	 * Create a new slice with the specified initial parameters
	 * @return the index of the slice that was added
	 */
	int addSlice(double x, double y, double width, double height, double phi);
	/**
	 * Remove a slice from the source
	 * @param n The index of the slice
	 */
	void removeSlice(int n);
}

