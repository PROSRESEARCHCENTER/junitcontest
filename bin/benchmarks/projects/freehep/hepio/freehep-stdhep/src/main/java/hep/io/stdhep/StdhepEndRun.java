package hep.io.stdhep;

import java.io.IOException;


/**
 * An end run record.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StdhepEndRun.java 9132 2006-10-13 05:39:06Z tonyj $
 */
public class StdhepEndRun extends StdhepRunRecord
{
   public StdhepEndRun(int nevtreq, int nevtgen, int nevtwrt, float stdecom, float stdxsec, double stdseed1, double stdseed2)
   {
      super(MCFIO_STDHEPEND, nevtreq, nevtgen, nevtwrt, stdecom, stdxsec, stdseed1, stdseed2);
   }

   StdhepEndRun() throws IOException
   {
      super(MCFIO_STDHEPEND);
   }

   public String toString()
   {
      return "End run";
   }
}
