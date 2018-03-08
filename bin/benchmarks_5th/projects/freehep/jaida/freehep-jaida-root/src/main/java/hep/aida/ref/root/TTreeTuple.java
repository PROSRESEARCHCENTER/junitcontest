package hep.aida.ref.root;

import hep.aida.ITuple;
import hep.aida.ref.tuple.ReadOnlyAbstractTuple;
import hep.io.root.RootClassNotFound;
import hep.io.root.interfaces.TKey;
import hep.io.root.interfaces.TLeaf;
import hep.io.root.interfaces.TLeafB;
import hep.io.root.interfaces.TLeafC;
import hep.io.root.interfaces.TLeafD;
import hep.io.root.interfaces.TLeafF;
import hep.io.root.interfaces.TLeafI;
import hep.io.root.interfaces.TLeafL;
import hep.io.root.interfaces.TTree;

import java.io.IOException;

import org.freehep.util.Value;


/**
 * A Tuple based on a Root Ttree().
 * No support for subFolders yet.
 * @author tonyj
 */
class TTreeTuple extends ReadOnlyAbstractTuple {

   private TKey key;
   private Value theValue;
   private TLeafColumn[] columns;
   private int nCol;

   TTreeTuple(TKey key, String name)
   {
      super(name, key.getTitle());
      this.key = key;
      nCol = tree().getLeaves().size();
      columns = new TLeafColumn[nCol];
      theValue = new Value();
      
      TLeafFolderColumn subTuple = null;
      TLeaf lastDim = null;
      
      int n = 0;
      for (int i = 0; i < nCol; i++)
      {
         TLeaf leaf = (TLeaf) tree().getLeaves().get(i);
         int nDim = leaf.getArrayDim();
         if (nDim == 0)
         {
            if (leaf instanceof TLeafI)
               columns[n++] = new TLeafIColumn((TLeafI) leaf);
            else if (leaf instanceof TLeafL)
               columns[n++] = new TLeafLColumn((TLeafL) leaf);
            else if (leaf instanceof TLeafF)
               columns[n++] = new TLeafFColumn((TLeafF) leaf);
            else if (leaf instanceof TLeafD)
               columns[n++] = new TLeafDColumn((TLeafD) leaf);
            else if (leaf instanceof TLeafB)
               columns[n++] = new TLeafBColumn((TLeafB) leaf);
            else if (leaf instanceof TLeafC)
               columns[n++] = new TLeafCColumn((TLeafC) leaf);
            else
            {
               System.out.println("Ignored column " + leaf.getName() + " of type " + leaf.getClass());
            }
         }
         else
         {
            // If this is a variable dimension attempt to group items together
            TLeafI dim = (TLeafI) leaf.getLeafCount();
            if (dim == null) columns[n++] = new TLeafObjectColumn(leaf);
            else if (dim == lastDim)
            {
               subTuple.addColumn(leaf);
            }
            else
            {
               subTuple = new TLeafFolderColumn(dim);
               columns[n++] = subTuple;
               subTuple.addColumn(leaf);
               lastDim = dim;
            }
         }
      }
      nCol = n;
   }

    public boolean isInMemory() {
        return true;
    }
    
    public boolean providesColumnDefaultValues() {
        return false;
    }    
    
    public void columnValue(int column, Value v) {
        columns[column].getValue(getRow(),v);
    }    

   public hep.aida.ITuple findTuple(int col) {
      columns[col].getValue(getRow(), theValue);
      return (ITuple) theValue.getObject();
   }


   public double columnMax(int index) throws IllegalArgumentException
   {
      columns[index].maxValue(theValue);
      return theValue.getDouble();
   }

   public double columnMean(int index) throws IllegalArgumentException
   {
      columns[index].meanValue(theValue);
      return theValue.getDouble();
   }

   public double columnMin(int index) throws IllegalArgumentException
   {
      columns[index].minValue(theValue);
      return theValue.getDouble();
   }

   public String columnName(int index) throws IllegalArgumentException
   {
      return columns[index].name();
   }

   public double columnRms(int index) throws IllegalArgumentException
   {
      columns[index].rmsValue(theValue);
      return theValue.getDouble();
   }

   public Class columnType(int index) throws IllegalArgumentException
   {
      return columns[index].type();
   }

   public int columns()
   {
      return nCol;
   }

   public int findColumn(String name) throws IllegalArgumentException
   {
         for (int i = 0; i < columns.length; i++)
         {
            if (columns[i].name().equals(name))
               return i;
         }
         throw new IllegalArgumentException("Unknown column " + name);
   }
   
   public int rows()
   {
      return (int) tree().getEntries();
   }

   public boolean supportsMultipleCursors()
   {
      return true;
   }

   public boolean supportsRandomAccess()
   {
      return true;
   }

   private TTree tree()
   {
      try
      {
         return (TTree) key.getObject();
      }
      catch (RootClassNotFound x)
      {
         throw new RuntimeException("Root Class Not Found " + x.getClassName(),x);
      }
      catch (IOException x)
      {
         throw new RuntimeException("IOException reading root file",x);
      }
   }
   
   public String columnDefaultString(int index) {
       throw new UnsupportedOperationException();
   }
   
   public Object columnDefaultValue(int index) {
       throw new UnsupportedOperationException();       
   }
}
