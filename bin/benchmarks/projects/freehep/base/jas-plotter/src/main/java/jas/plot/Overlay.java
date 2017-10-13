package jas.plot;

public interface Overlay
{
	/**
	 * Called when the overlay needs to paint itself
	 */
	void paint(PlotGraphics g, boolean isPrinting);
	
	/**
	 * Notifies the overlay it has been attached to a container
	 */

	void containerNotify(OverlayContainer c);
}
