 package hep.io.root.reps;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;
import hep.io.root.RootObject;
import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.GenericRootClass;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TKey;
import java.io.IOException;
import java.lang.ref.SoftReference;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TKeyRep.java 15983 2014-05-15 22:33:47Z tonyj $
 */
public abstract class TKeyRep extends AbstractRootObject implements TKey
{
   private AbstractRootObject object;
   private java.util.Date fDatime;
   private RootInput rin;
   private SoftReference soft;
   private String fClassName;
   private String fName;
   private String fTitle;
   private int fNbytes; // number of bytes for the compressed object+key       //
   private int fObjlen; // Length of uncompressed object                       //
   private long fSeekKey; // Address of the object on file (points to fNbytes)   //
   private long fSeekPdir;
   private short fCycle;
   private short fKeylen; // number of bytes for the key structure               //

   /**
    * Get the object corresonding to this key
    */
   public RootObject getObject() throws RootClassNotFound, IOException
   {
      // Use of a soft reference allows the object to be cleared
      // from memory if not in use, and if memory is short.
      // Proxies are messing this up?? --- Does the proxy not hold a reference to the object??
      // AbstractRootObject object = soft == null ? null : (AbstractRootObject) soft.get();
      if (object == null)
      {
         object = createObject(getObjectClass());
         rin.setPosition(fSeekKey + fKeylen);
         if (fObjlen > (fNbytes - fKeylen)) // We need to decompress
         {
            RootInput in = getData();
            object.read(in);
         }
         else
         {
            rin.setMap(fKeylen);
            object.read(rin);
            rin.clearMap();
         }
         soft = new SoftReference(object);
      }
      return object;
   }

   public RootClass getObjectClass() throws RootClassNotFound, IOException
   {
      return rin.getFactory().create(fClassName);
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
   }

   RootInput getData() throws IOException
   {
      //System.out.println("Get Data for TKey "+fName+" "+fClassName+" "+fNbytes +" "+fKeylen+" "+fObjlen);
      if ((fNbytes - fKeylen) < fObjlen)
      {
         RootInput slice = rin.slice(fNbytes - fKeylen, fObjlen);
         slice.setMap(fKeylen);
         return slice;
      }
      else
      {
         RootInput slice = rin.slice(fObjlen);
         slice.setMap(fKeylen);
         return slice;
      }
   }

   private AbstractRootObject createObject(RootClass k) throws RootClassNotFound
   {
      return ((GenericRootClass) k).newInstance();
   }
}
