package hep.io.stdhep;

import hep.io.mcfio.MCFIOBlock;
import hep.io.mcfio.MCFIOEvent;
import hep.io.mcfio.MCFIOReader;
import java.io.EOFException;

import java.io.IOException;
import java.io.InputStream;


/**
 * A class for reading stdhep files.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StdhepReader.java 9150 2006-10-16 19:56:07Z tonyj $
 */
public class StdhepReader extends MCFIOReader implements StdhepConstants
{
   /**
    * Open a stdhep file for reading.
    */
   public StdhepReader(String file) throws IOException
   {
      super(file);
   }
   /**
    * Open a stdhep file for reading.
    */
   public StdhepReader(InputStream stream) throws IOException
   {
      super(stream);
   }
   /**
    * Read the next stdhep record from the file.
    * The record may be a begin run, end run, or event record.
    */
   public StdhepRecord nextRecord() throws IOException
   {
      // It is not clear why a stdHEP record would have 0
      // blocks, but it does sometimes seem to happen.
      for (;;)
      {
         MCFIOEvent event = super.nextEvent();
         int nBlocks = event.getNBlocks();
         if (nBlocks == 0)
            continue;
         if (nBlocks != 1)
            throw new IOException("Invalid stdhep record found (NBlocks=" + nBlocks + ")");
         return (StdhepRecord) event.getBlock(0);
      }
   }
   /**
    * Finds the event with run number and event number specified
    * @throws EOFException if the specified event is not found.
    */
   public StdhepRecord goToRecord(int runNumber, int eventNumber) throws IOException
   {
      boolean hasRewound = false;
      while (true)
      {
         try
         {
            while (true)
            {
               MCFIOEvent event = super.nextEvent();
               if (event.getRunNumber() == runNumber && event.getEventNumber() == eventNumber)
               {
                  // Note, this triggers the event to be read.
                  int nBlocks = event.getNBlocks();
                  if (nBlocks == 0)
                     continue;
                  if (nBlocks != 1)
                     throw new IOException("Invalid stdhep record found (NBlocks=" + nBlocks + ")");
                  return (StdhepRecord) event.getBlock(0);       
               }
            }
         }
         catch (EOFException x)
         {
            if (!hasRewound)
            {
               rewind();
               hasRewound = true;
            }
            else throw x;
         }
      }
   }
   /**
    * Skip a number of records.
    * Attention, skipping events efficiently in stdhep files is unfortunately broken
    * due to a bug in the stdhep format. The underlying MCFIO format allows for skipping through
    * events quite efficiently, but it is not possible to tell whether a stdhep record is an 
    * event or a begin or end run record without completely unpacking it, so the following method
    * works, but is fairly useless since you cannot tell how many events were actually skipped.
    */
   public void skip(int nEvents) throws IOException
   {
      super.skip(nEvents);
   }
   
   
   /**
    * Overrides the createUserBlock from MCFIO to create the
    * necessary stdhep records. Override this method to add
    * support for your own record types.
    */
   protected MCFIOBlock createUserBlock(int id) throws IOException
   {
      switch (id)
      {
         case MCFIO_STDHEP:
            return new StdhepEvent();
            
         case MCFIO_STDHEPBEG:
            return new StdhepBeginRun();
            
         case MCFIO_STDHEPEND:
            return new StdhepEndRun();
            
         case MCFIO_STDHEPEV4:
            return new StdhepExtendedEvent();
            
         default:
            return super.createUserBlock(id);
      }
   }


}
