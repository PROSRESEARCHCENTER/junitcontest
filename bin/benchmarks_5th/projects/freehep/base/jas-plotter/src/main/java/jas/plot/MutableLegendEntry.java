package jas.plot;

public interface MutableLegendEntry extends LegendEntry
{
	void setTitle(String newTitle);
	boolean titleIsChanged();
}
