package hep.io.mcfio;

import java.io.IOException;


/**
 * An "event" read from an MCFIO file
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: MCFIOEvent.java 9131 2006-10-13 04:55:11Z tonyj $
 */
public interface MCFIOEvent
{
   MCFIOBlock getBlock(int index) throws IOException;

   int getBlockID(int index) throws IOException;

   int getEventNumber() throws IOException;

   int getNBlocks() throws IOException;

   int getRunNumber() throws IOException;

   int getStoreNumber() throws IOException;

   int getTrigMask() throws IOException;
}
