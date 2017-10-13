package hep.aida.ref.root;

import hep.aida.ITuple;
import hep.aida.ref.tuple.ReadOnlyAbstractTuple;
import hep.io.root.interfaces.TLeaf;
import hep.io.root.interfaces.TLeafB;
import hep.io.root.interfaces.TLeafD;
import hep.io.root.interfaces.TLeafF;
import hep.io.root.interfaces.TLeafI;
import hep.io.root.interfaces.TLeafL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.freehep.util.Value;



/**
 *
 * @author tonyj
 */
class TLeafFolderColumn extends TLeafColumn
{
   private TLeafI dim;
   private TLeafColumn[] columns;
   private EmbeddedTuple embedded;
   
   TLeafFolderColumn(TLeafI dim)
   {
      this.dim = dim;
      embedded = new EmbeddedTuple(name());
   }
   void addColumn(TLeaf leaf)
   {
      // Each row represents an array, of which the first dimension is dim
      TLeafColumn col;
      if (leaf instanceof TLeafI)
         col = new TLeafIColumn((TLeafI) leaf);
      else if (leaf instanceof TLeafL)
         col = new TLeafLColumn((TLeafL) leaf);
      else if (leaf instanceof TLeafF)
         col = new TLeafFColumn((TLeafF) leaf);
      else if (leaf instanceof TLeafD)
         col = new TLeafDColumn((TLeafD) leaf);
      else if (leaf instanceof TLeafB)
         col = new TLeafBColumn((TLeafB) leaf);
      else
      {
         System.out.println("Ignored column " + leaf.getName() + " of type " + leaf.getClass());
         return;
      }
      if (columns == null) columns = new TLeafColumn[] { col };
      else
      {
         List x = new ArrayList(Arrays.asList(columns));
         x.add(col);
         columns = new TLeafColumn[x.size()];
         x.toArray(columns);
      }
   }
   
   void getValue(int row, Value value)
   {
      try
      {
         // We need to return an ITuple as our value
         embedded.setGlobalRow(row);
         value.set(embedded);
      }
      catch (IOException x)
      {
         throw new RuntimeException("IOException accessing tuple", x);
      }
   }
   
   public void defaultValue(Value value)
   {
      //System.out.println("getting default value  "+name());
      value.set((Object) null);
   }
   
   public String name()
   {
      return "Folder["+dim.getName()+"]";
   }
   
   public Class type()
   {
      return EmbeddedTuple.class;
   }
   
   void getArrayValue(int row, int dim, Value value)
   {
      throw new UnsupportedOperationException();
   }
   
   private class EmbeddedTuple extends ReadOnlyAbstractTuple
   {
      private Value theValue = new Value();
      private int nRows;
      private int theRow;
      
      EmbeddedTuple(String name)
      {
         super(name, "");
      }
      void setGlobalRow(int row) throws IOException
      {
         theRow = row;
         nRows = dim.getValue(row);
      }
      
    public boolean providesColumnDefaultValues() {
        return false;
    }    
    
    public void columnValue(int column, Value v) {
         columns[column].getArrayValue(theRow, getRow(), v);
    }    

      public boolean isInMemory()
      {
         return false;
      }
      
      public ITuple findTuple(int param)
      {
         return null;
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
         return columns.length;
      }
      
      public int rows()
      {
         return nRows;
      }
      
      
    public String columnDefaultString(int index) {
        throw new UnsupportedOperationException();
    }
    
    public Object columnDefaultValue(int index) {
        throw new UnsupportedOperationException();        
    }
      
      public boolean supportsMultipleCursors()
      {
         return true;
      }
      
      public boolean supportsRandomAccess()
      {
         return true;
      }
      
   }
}