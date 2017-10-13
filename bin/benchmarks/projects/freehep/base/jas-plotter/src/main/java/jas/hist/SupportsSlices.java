package jas.hist;
import javax.swing.JMenu;

public interface SupportsSlices
{
	void addSlice(SliceParameters sp);
	void removeAllSlices();
	void fillSliceMenu(JMenu menu);
}
