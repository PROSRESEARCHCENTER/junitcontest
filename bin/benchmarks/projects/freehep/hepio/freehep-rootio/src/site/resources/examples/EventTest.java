import hep.io.root.*;
import hep.io.root.interfaces.*;
import java.io.IOException;
import java.util.*;

/**
 * An example of how to read events from a Root file.
 * @author  tonyj
 * @version $Id: EventTest.java 8980 2006-09-14 23:37:36Z duns $
 */
public class EventTest 
{
    public static void main (String args[]) throws IOException
    {
        RootFileReader reader = new RootFileReader("Event.root");
        TTree tree = (TTree) reader.get("T");
        TBranch branch = tree.getBranch("event");
        int n = branch.getNEntries();
        System.out.println("nEntries="+n);
        long start = System.currentTimeMillis(); 
        for (int i=0; i<n; i++)
        {
            Event e = (Event) branch.getEntry(i);
            List l = e.getTracks();
            System.out.println("NTracks="+e.getNtrack()+" "+l.size());
            Iterator it = l.iterator();
            while (it.hasNext())
            {
               Track t = (Track) it.next();
               double px = t.getPx();
               //System.out.println("px="+px);
            }
        }
        long stop = System.currentTimeMillis();
        System.out.println("ms/event="+((stop-start)/n));
    }
}
