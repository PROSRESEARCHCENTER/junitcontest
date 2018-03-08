package jas.hist;

/**
 * A crude way of interfacing special plot components such
 * as 3d lego plot. To be replaced by something more generic
 * one day.
 */
public interface SpecialComponent
{
	void setData(DataSource ds);
	java.awt.Component getDisplayComponent();
}
