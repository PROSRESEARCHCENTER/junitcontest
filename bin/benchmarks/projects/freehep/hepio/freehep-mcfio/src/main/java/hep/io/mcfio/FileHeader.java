package hep.io.mcfio;

import java.io.IOException;
import java.util.Date;
import hep.io.xdr.XDRDataInput;
import hep.io.xdr.XDRDataOutput;
import hep.io.xdr.XDRRandomAccessFile;

/**
 * The FileHeader is at the beginning of each MCFIO file.
 * This is a package private class that represents the fileheader.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: FileHeader.java 9131 2006-10-13 04:55:11Z tonyj $
 */
class FileHeader extends MCFIOBlock implements MCFIOConstants
{
   private String closingDate;
   private String comment;
   private String date;
   private String title;
   private int[] blockIds;
   private String[] blockNames;
   private int firsttable;
   private int location;
   private int numevts;
   private int numevts_expect;

   FileHeader() throws IOException
   {
      super(FILEHEADER);
   }

   FileHeader(int pos, String title, String comment, int numevts, int[] blockIds, String[] blockNames) throws IOException
   {
      super(FILEHEADER);
      version = "1.00";

      if (blockIds.length != blockNames.length)
         throw new IllegalArgumentException("Inconsistent length got blockIds,blockNames");
      this.location = pos;
      this.title = title;
      this.comment = comment;
      this.numevts_expect = numevts;
      this.numevts = 0;
      this.date = (new Date()).toString();
      this.blockIds = blockIds;
      this.blockNames = blockNames;
   }

   public void read(XDRDataInput xdr) throws IOException
   {
      super.read(xdr);
      if (fVersion > 2.01)
         throw new MCFIOException("Unsupported version " + version + " for FileHeader");

      title = xdr.readString();
      comment = xdr.readString();
      date = xdr.readString();
      if (fVersion <= 2.)
         closingDate = date;
      else
         closingDate = xdr.readString();

      numevts_expect = xdr.readInt();
      numevts = xdr.readInt();
      firsttable = xdr.readInt();

      int dimTable = xdr.readInt();

      // MCFIO writes the blocks to appear in the file at the top
      // of the file. We do not use these blocks for anything.
      int nBlocks = xdr.readInt();
      int nTuples = (fVersion >= 2) ? xdr.readInt() : 0;
      if (nTuples > 0)
         throw new IOException("NTuples not supported");
      blockIds = xdr.readIntArray(blockIds);
      blockNames = new String[nBlocks];
      for (int i = 0; i < nBlocks; i++)
         blockNames[i] = xdr.readString();
   }

   public void write(XDRDataOutput xdr) throws IOException
   {
      super.write(xdr);
      xdr.writeString(title);
      xdr.writeString(comment);
      xdr.writeString(date);
      xdr.writeInt(numevts_expect);
      xdr.writeInt(numevts);
      xdr.writeInt(firsttable);
      xdr.writeInt(MCF_DEFAULT_TABLE_SIZE); // dimtable

      xdr.writeInt(blockIds.length); // nblocks
      xdr.writeIntArray(blockIds);
      for (int i = 0; i < blockIds.length; i++)
         xdr.writeString(blockNames[i]);
   }

   protected int getLength()
   {
      int l = 4 * (10 + blockIds.length);
      l += strlen(title);
      l += strlen(comment);
      l += strlen(date);
      for (int i = 0; i < blockNames.length; i++)
         l += strlen(blockNames[i]);
      return l;
   }

   String getComment()
   {
      return comment;
   }

   String getDate()
   {
      return date;
   }

   void setFirstTable(int pos)
   {
      firsttable = pos;
   }

   int getFirstTable()
   {
      return firsttable;
   }

   int getNumberOfEvents()
   {
      return numevts;
   }

   int getNumberOfEventsExpected()
   {
      return numevts_expect;
   }

   String getTitle()
   {
      return title;
   }

   /**
    * Write the block to disk.
    */
   void commit(XDRRandomAccessFile xdr) throws IOException
   {
      long pos = xdr.getFilePointer();
      xdr.seek(location);
      write(xdr);
      xdr.seek(pos);
   }

   void incrementNumEvents(int n)
   {
      numevts += n;
   }

   private int strlen(String s)
   {
      int l = s.length();
      if ((l % 4) != 0)
         l += (4 - (l % 4));
      return l + 4;
   }
}
