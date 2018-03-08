/*
 * TDatime.java
 *
 * Created on January 18, 2001, 5:19 PM
 */
package hep.io.root.reps;

import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.RootInput;

import java.util.Date;
import java.util.GregorianCalendar;


/**
 *
 * @author  tonyj
 * @version $Id: TDatimeRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TDatimeRep extends AbstractRootObject implements hep.io.root.interfaces.TDatime
{
   private Date date;
   private int fDatime;

   public Date getDate()
   {
      if (date == null)
      {
         // Root times are seconds since Jan 1 1995 (in timeZone??)
         int year = (fDatime >> 26) + 1995;
         int month = ((fDatime << 6) >> 28) - 1;
         int day = (fDatime << 10) >> 27;
         int hour = (fDatime << 15) >> 27;
         int min = (fDatime << 20) >> 26;
         int sec = (fDatime << 26) >> 26;
         date = new GregorianCalendar(year, month, day, hour, min, sec).getTime();
      }
      return date;
   }

   public int getDatime()
   {
      return fDatime;
   }

   public void readMembers(RootInput in) throws java.io.IOException
   {
      fDatime = in.readInt();
   }

   public String toString()
   {
      return getDate().toString();
   }
}
