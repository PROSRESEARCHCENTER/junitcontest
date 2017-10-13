package hep.io.root.interfaces;

import java.io.IOException;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TBasket.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface TBasket extends hep.io.root.RootObject, TKey
{
   int getBufferSize();

   int[] getDisplacement();

   int[] getEntryOffset();

   byte getFlag();

   int getLast();

   int getNevBuf();

   int getNevBufSize();

   //Extra methods
   hep.io.root.core.RootInput setPosition(long index, long basketEntry, TLeaf leaf) throws IOException;

   void readEntryOffsets(int len) throws IOException;
}
