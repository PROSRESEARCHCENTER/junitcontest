package hep.io.mcfio;

import java.io.IOException;
import hep.io.xdr.XDRDataInput;
import hep.io.xdr.XDRDataOutput;
import hep.io.xdr.XDRRandomAccessFile;

/**
 * The event table is written as the second block in each
 * file, and then at intervals throughout the file as the
 * previous event table becomes full. The event table allows
 * for a certain degree of random access to events.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: EventTable.java 9131 2006-10-13 04:55:11Z tonyj $
 */
class EventTable extends MCFIOBlock implements MCFIOConstants
{
   private int[] evtnums;
   private int[] ptrEvents;
   private int[] runnums;
   private int[] storenums;
   private int[] trigMasks;
   private int location;
   private int nextLocator;
   private int numevts;

   EventTable() throws IOException
   {
      super(EVENTTABLE);
   }

   EventTable(int pos) throws IOException
   {
      super(EVENTTABLE);
      version = "1.00";
      location = pos;
      nextLocator = -2;
      numevts = 0;

      runnums = new int[MCF_DEFAULT_TABLE_SIZE];
      evtnums = new int[MCF_DEFAULT_TABLE_SIZE];
      storenums = new int[MCF_DEFAULT_TABLE_SIZE];
      trigMasks = new int[MCF_DEFAULT_TABLE_SIZE];
      ptrEvents = new int[MCF_DEFAULT_TABLE_SIZE];
   }

   public void read(XDRDataInput xdr) throws IOException
   {
      super.read(xdr);
      if (!version.equals("1.00"))
         throw new MCFIOException("Unsupported version " + version + " for EventTable");

      nextLocator = xdr.readInt();
      numevts = xdr.readInt();

      // Care is needed here, it appears that MCFIO writes arrays 
      // longer than numevts under some circumstances
      evtnums = xdr.readIntArray(evtnums);
      storenums = xdr.readIntArray(storenums);
      runnums = xdr.readIntArray(runnums);
      trigMasks = xdr.readIntArray(trigMasks);
      ptrEvents = xdr.readIntArray(ptrEvents);
   }

   public void write(XDRDataOutput xdr) throws IOException
   {
      super.write(xdr);
      xdr.writeInt(nextLocator);
      xdr.writeInt(numevts);
      xdr.writeIntArray(evtnums);
      xdr.writeIntArray(storenums);
      xdr.writeIntArray(runnums);
      xdr.writeIntArray(trigMasks);
      xdr.writeIntArray(ptrEvents);
   }

   protected int getLength()
   {
      return 4 * (11 + (evtnums.length * 5));
   }

   boolean isFull()
   {
      return numevts >= evtnums.length;
   }

   void setNextTable(int pos)
   {
      nextLocator = pos;
   }

   void add(MCFIOEvent event, int pos) throws IOException
   {
      runnums[numevts] = event.getRunNumber();
      evtnums[numevts] = event.getEventNumber();
      storenums[numevts] = event.getStoreNumber();
      trigMasks[numevts] = event.getTrigMask();
      ptrEvents[numevts] = pos;
      numevts++;
   }

   void commit(XDRRandomAccessFile xdr) throws IOException
   {
      long pos = xdr.getFilePointer();
      xdr.seek(location);
      write(xdr);
      xdr.seek(pos);
   }

   int evtnum(int index)
   {
      return evtnums[index];
   }

   int nextTable()
   {
      return nextLocator;
   }

   int numevts()
   {
      return numevts;
   }

   int ptrEvent(int index)
   {
      return ptrEvents[index];
   }

   int runnum(int index)
   {
      return runnums[index];
   }

   int storenum(int index)
   {
      return storenums[index];
   }

   int trigMask(int index)
   {
      return trigMasks[index];
   }
}
