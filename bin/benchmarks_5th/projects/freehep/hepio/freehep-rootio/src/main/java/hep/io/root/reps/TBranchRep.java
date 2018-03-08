package hep.io.root.reps;

import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.NameMangler;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TBasket;
import hep.io.root.interfaces.TBranch;
import hep.io.root.interfaces.TLeaf;
import hep.io.root.interfaces.TObjArray;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TBranchRep.java 15983 2014-05-15 22:33:47Z tonyj $
 */
public abstract class TBranchRep extends AbstractRootObject implements hep.io.root.interfaces.TBranch
{
   private HashMap mangledMap;
   private HashMap map;
   private RootInput rin;
   private TBasket curBasket;
   private int curIndex;
   private int fWriteBasket;

   private int[] cBasketBytes;
   private long[] cBasketEntry;
   private long[] cBasketSeek;
   
   public int[] getBasketBytes()
   {
      return cBasketBytes;
   }
   
   public long[] getBasketEntry()
   {
      return cBasketEntry;
   }
   
   public long[] getBasketSeek()
   {
      return cBasketSeek;
   }
   
   private int[] saveIntSpace(String source, int size)
   {
      try
      {
         if (size == 0) return null;
         int[] result = new int[size];
         Field field = getClass().getDeclaredField(source);
         Object array = field.get(this);
         for (int i=0; i<size; i++) result[i] = Array.getInt(array,i);
         return result;
      }
      catch (NoSuchFieldException x) // Seems to happen for TBranchClones
      {
         return null;
      }
      catch (Exception x)
      {
         throw new RuntimeException("Wierd error while compressing "+source,x);
      }
   }
   private long[] saveLongSpace(String source, int size)
   {
      try
      {
         if (size == 0) return null;
         long[] result = new long[size];
         Field field = getClass().getDeclaredField(source);
         Object array = field.get(this);
         for (int i=0; i<size; i++) result[i] = Array.getLong(array,i);
         return result;
      }
      catch (NoSuchFieldException x) // Seems to happen for TBranchClones
      {
         return null;
      }
      catch (Exception x)
      {
         throw new RuntimeException("Wierd error while compressing "+source,x);
      }
   }
   public TBranch getBranchForMangledName(String name)
   {
      if (map == null)
         buildMap();
      return (TBranch) mangledMap.get(name);
   }
   
   public TBranch getBranchForName(String name)
   {
      if (map == null)
         buildMap();
      return (TBranch) map.get(name);
   }
   
   public RootInput setPosition(TLeaf leaf, long index) throws IOException
   {
      int i = findBasketForIndex(index);
      TBasket basket = getBasket(i);
      return basket.setPosition(index, i == 0 ? 0 : getBasketEntry()[i], leaf);
   }
   
   public void read(RootInput in) throws IOException
   {
      super.read(in);
      // Clean-up unnecessarily large arrays
      cBasketBytes = saveIntSpace("fBasketBytes",fWriteBasket);
      cBasketEntry = saveLongSpace("fBasketEntry",fWriteBasket+1);
      cBasketSeek = saveLongSpace("fBasketSeek",fWriteBasket);
      
      rin = in.getTop();
      
      // The leaves need to know which branch they are on
      TObjArray leaves = getLeaves();
      if (leaves != null)
      {
         for (int i = 0; i < leaves.size(); i++)
         {
            TLeaf leaf = (TLeaf) leaves.get(i);
            leaf.setBranch(this);
         }
      }
      curIndex = -1;
   }
   
   private TBasket getBasket(int index) throws IOException
   {
      try
      {
         TObjArray baskets = getBaskets();
         if (index<baskets.getLast()) {
            TBasket basket = (TBasket) baskets.get(index);
            if (basket != null) return basket;
         }
         
         if (index == curIndex) return curBasket;
         // Ok read the TBasket
         rin.setPosition(getBasketSeek()[index]);
         
         TBasket basket = (TBasket) rin.readObject("TBasket");
         
         int len = getEntryOffsetLen();
         if (len > 0) basket.readEntryOffsets(len);
         
         curIndex = index;
         return curBasket = basket;
      }
      catch (IOException x)
      {
         curIndex = -1;
         throw x;
      }
   }
   
   private void buildMap()
   {
      NameMangler nameMangler = NameMangler.instance();
      map = new HashMap();
      mangledMap = new HashMap();
      
      TObjArray branches = getBranches();
      int size = branches.size();
      for (int i = 0; i < size; i++)
      {
         TBranch b = (TBranch) branches.get(i);
         String bName = b.getName();
         int pos = bName.indexOf('[');
         if (pos > 0)
            bName = bName.substring(0, pos);
         
         pos = bName.lastIndexOf('.');
         if (pos > 0)
            bName = bName.substring(pos + 1);
         map.put(bName, b);
         mangledMap.put(nameMangler.mangleMember(bName), b);
      }
   }
   
   private int findBasketForIndex(long index)
   {
      // Figure out which basket the entry is in
      if ((index < 0) || (index >= getEntries()))
         throw new ArrayIndexOutOfBoundsException("index=" + index);
      
      long[] entries = getBasketEntry();
      if (entries == null) return 0;
      // TODO: we should do a binary search here
      int n = getWriteBasket();
      for (int i = 0; i < n; i++)
      {
         if ((index >= entries[i]) && (index < entries[i + 1])) return i;
      }
      return n;
      // binary search has problems if arrays contain trailing zeros
//      int result = Arrays.binarySearch(entries,index);
//      if (result < 0) result = - result - 2;
//      return result;
   }
}
