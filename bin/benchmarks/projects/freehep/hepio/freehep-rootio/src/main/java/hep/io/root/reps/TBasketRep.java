package hep.io.root.reps;

import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TLeaf;

import java.io.IOException;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TBasketRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TBasketRep extends AbstractRootObject implements hep.io.root.interfaces.TBasket
{
   private java.util.Date fDatime;
   private RootInput data;
   private RootInput rin;
   private String fClassName;
   private String fName;
   private String fTitle;
   private byte[] fBufferRef;
   private int[] fDisplacement;
   private int[] fEntryOffset;
   private byte flag;
   private int fBufferSize;
   private int fLast;
   private int fNbytes; // number of bytes for the compressed object+key        //
   private int fNevBuf;
   private int fNevBufSize;
   private int fObjlen; // Length of uncompressed object                        //
   private long fSeekKey; // Address of the object on file (points to fNbytes)   //
   private long fSeekPdir;
   private short fCycle;
   private short fKeylen; // number of bytes for the key structure              //

   public RootInput setPosition(long index, long offset, TLeaf leaf) throws IOException
   {
      int i = (int) (index - offset);
      if (fEntryOffset == null)
      {
         data.setPosition(fKeylen + (i * fNevBufSize));
      }
      else
      {
         data.setPosition(fEntryOffset[i]);
      }
      return data;
   }

   public void readEntryOffsets(int len) throws IOException
   {
      int last = getLast();
      data.setPosition(last);
      fEntryOffset = new int[len];
      data.readArray(fEntryOffset);

      // Currently there is no handling for displacements, I am not clear
      // what they are for anyway.
   }

   public void readMembers(RootInput in) throws IOException
   {
      fNbytes = in.readInt();
      
      int v = in.readVersion();
      fObjlen = in.readInt();
      fDatime = ((hep.io.root.interfaces.TDatime) in.readObject("TDatime")).getDate();
      fKeylen = in.readShort();
      fCycle = in.readShort();
      if (v > 1000)
      {
         fSeekKey = in.readLong();
         fSeekPdir = in.readLong();
      }
      else
      {
         fSeekKey = in.readInt();
         fSeekPdir = in.readInt();         
      }
      fClassName = in.readObject("TString").toString();
      fName = in.readObject("TString").toString();
      fTitle = in.readObject("TString").toString();
      rin = in.getTop();

      v = in.readVersion(this);
      fBufferSize = in.readInt();
      fNevBufSize = in.readInt();
      fNevBuf = in.readInt();
      fLast = in.readInt();
      if (fLast > fBufferSize)
         fBufferSize = fLast;

      flag = in.readByte();

      // flag == 0 indicates the data is in a separate root branch?
      if ((flag != 0) && ((flag % 10) != 2))
      {
         fEntryOffset = new int[fNevBufSize];
         if (fNevBuf != 0)
            in.readArray(fEntryOffset);
         if ((20 < flag) && (flag < 40))
         {
            for (int i = 0; i < fNevBuf; i++)
            {
               fEntryOffset[i] &= ~0xFF000000;
            }
         }
         if (flag > 40)
         {
            fDisplacement = new int[fNevBufSize];
            in.readArray(fDisplacement);
         }
      }
      if ((flag == 1) || (flag > 10))
      {
         int len;
         if (v > 1)
            len = fLast;
         else
            len = in.readInt();
         data = in.slice(len);
      }

      in.checkLength(this);
      if (data == null)
         data = getData();
   }

   abstract RootInput getData() throws IOException;
}
