import hep.analysis.*;
import jasext.root.*;
import hep.io.root.interfaces.*;
import java.util.*;

final public class MyRootAnalysis extends EventAnalyzer
{
	public MyRootAnalysis()
	{
		RootEventSource.setClassLoader(this);
	}
	public void processEvent(final EventData d)
	{
		RootEvent re = (RootEvent) d;
		Event e = (Event) re.getObject(0);
		
		// Make a histogram of the event date
		histogram("Event Number").fill(e.getEvtHdr().getEvtNum());
		histogram("NTracks").fill(e.getNtrack());
		
		// Now loop over the Tracks in the event
		List tracks = e.getTracks();
		Iterator iter = tracks.iterator();
		while (iter.hasNext())
		{
			Track t = (Track) iter.next();
			histogram("meanCharge").fill(t.getMeanCharge());
		}
	}

}