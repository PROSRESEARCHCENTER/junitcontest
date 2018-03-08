package jas.hist.util;
import java.util.EventListener;

public interface SliceListener extends EventListener
{
	void sliceAdded(SliceEvent e);
	void sliceRemoved(SliceEvent e);
}
