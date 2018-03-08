package hep.io.stdhep;

/**
 * A begin run record
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StdhepBeginRun.java 9132 2006-10-13 05:39:06Z tonyj $
 */
public class StdhepBeginRun extends StdhepRunRecord
{
   public StdhepBeginRun(int nevtreq, int nevtgen, int nevtwrt, float stdecom, float stdxsec, double stdseed1, double stdseed2)
   {
      super(MCFIO_STDHEPBEG, nevtreq, nevtgen, nevtwrt, stdecom, stdxsec, stdseed1, stdseed2);
   }

   StdhepBeginRun()
   {
      super(MCFIO_STDHEPBEG);
   }

   public String toString()
   {
      return "Begin run";
   }
}
