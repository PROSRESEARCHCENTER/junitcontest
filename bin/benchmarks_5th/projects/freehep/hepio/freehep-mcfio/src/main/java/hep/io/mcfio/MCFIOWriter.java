package hep.io.mcfio;

import hep.io.xdr.XDRBufferedRandomAccessFile;
import java.io.IOException;
import hep.io.xdr.XDRRandomAccessFile;

/**
 * A class for writing MCFIO files.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: MCFIOWriter.java 13660 2009-10-09 23:26:27Z tonyj $
 */
public class MCFIOWriter implements MCFIOConstants
{
   protected static boolean compatibilityMode = true;
   private FileHeader fileHeader;
   private WriteEventTable eventTable;
   private XDRRandomAccessFile xdr;

   /**
    * Open an MCFIO file for writing.
    * @param title The title of the file
    * @param comment The comment associate with the file
    * @param numevts The number of events expected to be written
    * @param blockIds The user defined blocks that will be included in this file
    * @param blockNames Names for the blocks defined by blockIds
    */
   public MCFIOWriter(String file, String title, String comment, int numevts, int[] blockIds, String[] blockNames) throws IOException
   {
      if (compatibilityMode)
      {
         if (title.length() > MCF_XDR_F_TITLE_LENGTH)
            throw new MCFIOException("Title too long");
         if (comment.length() > MCF_XDR_F_TITLE_LENGTH)
            throw new MCFIOException("Comment too long");
      }
      xdr = new XDRBufferedRandomAccessFile(file,false,16192);

      // Leave space for the fileHeader and eventTable
      fileHeader = new FileHeader(getPosition(), title, comment, numevts, blockIds, blockNames);
      xdr.seek(fileHeader.getLength());
      fileHeader.setFirstTable(getPosition());
      eventTable = new WriteEventTable(getPosition());
      xdr.seek(getPosition() + eventTable.getLength());
   }

   /**
    * Set compatibility mode. In the old C implementation
    * there were various limits, such as the length of
    * some strings and the maximum number of particles that
    * could be written to stdhep events. There is no reason
    * to have any of these limits in the Java implementation,
    * but if we ignore them then the resulting file might not
    * be readable by the old C implementation. By default this
    * implementation enforces the same limits as the C implementation,
    * but setting compatibilyMode to false will turn off this
    * limitation.
    */
   public void setCompatibilityMode(boolean mode)
   {
      compatibilityMode = mode;
   }

   public void close() throws IOException
   {
      eventTable.commit(xdr);
      fileHeader.commit(xdr);
      xdr.close();
   }

   /**
    * Write an event
    */
   public void write(MCFIOEvent event) throws IOException
   {
      if (eventTable.isFull())
         eventTable = eventTable.newTable();
      eventTable.add(event);
   }

   private int getPosition() throws IOException
   {
      // note: Java supports file positions specified as longs,
      // but MCFIO only supports ints.
      return (int) xdr.getFilePointer();
   }

   private class WriteEventTable extends EventTable
   {
      WriteEventTable(int pos) throws IOException
      {
         super(pos);
      }

      void add(MCFIOEvent event) throws IOException
      {
         super.add(event, getPosition());

         // Write the event header
         int nBlocks = event.getNBlocks();
         int size = 4 * (12 + (nBlocks * 2));
         xdr.writeInt(EVENTHEADER);
         xdr.writeInt(size);
         xdr.writeString("1.00");

         xdr.writeInt(event.getEventNumber());
         xdr.writeInt(event.getStoreNumber());
         xdr.writeInt(event.getRunNumber());
         xdr.writeInt(event.getTrigMask());
         xdr.writeInt(nBlocks);
         xdr.writeInt(nBlocks);

         int[] ptrs = new int[nBlocks];
         int[] ids = new int[nBlocks];
         int pos = getPosition() + (4 * (2 + (nBlocks * 2)));
         for (int i = 0; i < nBlocks; i++)
         {
            ids[i] = event.getBlockID(i);
            ptrs[i] = pos;
            pos += event.getBlock(i).getLength();
         }
         xdr.writeIntArray(ids);
         xdr.writeIntArray(ptrs);

         // Write the user blocks
         for (int i = 0; i < nBlocks; i++)
         {
            event.getBlock(i).write(xdr);
         }
      }

      void commit(XDRRandomAccessFile xdr) throws IOException
      {
         fileHeader.incrementNumEvents(numevts());
         super.commit(xdr);
      }

      WriteEventTable newTable() throws IOException
      {
         // Commit the current table to disk
         int currentPos = getPosition();
         setNextTable(currentPos);
         commit(xdr);

         // leave space for the next event table
         xdr.seek(currentPos + getLength());
         return new WriteEventTable(currentPos);
      }
   }
}
