package org.freehep.swing.table;

import java.util.Comparator;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Converts any TableModel to a SortableTableModel.
 * @author Tony Johnson
 * @version $Id: DefaultSortableTableModel.java 12627 2007-06-10 04:50:35Z tonyj $
 */
public class DefaultSortableTableModel implements SortableTableModel
{
   private Comparator comparator = new DefaultComparator();
   private TableModel source;
   private EventListenerList listeners = new EventListenerList();
   private TableModelListener internalListener = new InternalTableModelListener();
   private int sortColumn = UNSORTED;
   private boolean ascending = true;
   private int[] rowMap;
   private int[] reverseMap;
   boolean reverseMapValid = false;
   private int nRows;
   
   /**
    * Creates a new instance of DefaultTableSorter
    * @param source The table model to be converted.
    */
   public DefaultSortableTableModel(TableModel source)
   {
      this.source = source;
   }
   private int mapFromSorted(int rowIndex)
   {
      return rowMap == null ? rowIndex : rowMap[rowIndex];
   }
   private int mapToSorted(int rowIndex)
   {
      if (rowMap != null && !reverseMapValid)
      {
         if (reverseMap == null || reverseMap.length < nRows)
         {
            reverseMap = new int[rowMap.length];
         }
         for (int i=0; i<nRows; i++) reverseMap[rowMap[i]] = i;
         reverseMapValid = true;
      }
      return rowMap == null ? rowIndex : reverseMap[rowIndex];
   }
   
   public void addTableModelListener(TableModelListener l)
   {
      if (listeners.getListenerCount() == 0) 
      {
         // If we have not been listening for changes to model, we must assume
         // it has totally changed!
         dataChanged();
         source.addTableModelListener(internalListener);
      }
      listeners.add(TableModelListener.class, l);
   }
   
   public Class getColumnClass(int columnIndex)
   {
      return source.getColumnClass(columnIndex);
   }
   
   public int getColumnCount()
   {
      return source.getColumnCount();
   }
   
   public String getColumnName(int columnIndex)
   {
      return source.getColumnName(columnIndex);
   }
   
   public int getRowCount()
   {
      return source.getRowCount();
   }
   
   public Object getValueAt(int rowIndex, int columnIndex)
   {
      return source.getValueAt(mapFromSorted(rowIndex),columnIndex);
   }
   
   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return source.isCellEditable(mapFromSorted(rowIndex),columnIndex);
   }
   
   public void removeTableModelListener(TableModelListener l)
   {
      listeners.remove(TableModelListener.class, l);
      if (listeners.getListenerCount() == 0) source.removeTableModelListener(internalListener);
   }
   
   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
   {
      source.setValueAt(aValue, mapFromSorted(rowIndex), columnIndex);
   }
   
   public void sort(int column, boolean ascending)
   {
      if (column == UNSORTED)
      {
         if (this.sortColumn != UNSORTED)
         {
            rowMap = null;
            reverseMap = null;
            reverseMapValid = false;
            this.sortColumn = column;
            TableModelEvent ee = new TableModelEvent(this,0,source.getRowCount()-1,TableModelEvent.ALL_COLUMNS);
            fireTableChanged(ee);
         }
      }
      else if (column == this.sortColumn)
      {
         if (ascending != this.ascending)
         {
            this.ascending = ascending;
            for (int i=0; i<nRows/2; i++) swap(i,nRows-1-i);
            reverseMapValid = false;
            TableModelEvent ee = new TableModelEvent(this,0,nRows-1,TableModelEvent.ALL_COLUMNS);
            fireTableChanged(ee);
         }
      }
      else
      {
         if (rowMap == null)
         {
            nRows = source.getRowCount();
            rowMap = new int[nRows+10]; // Leave a little room for expansion
            for (int i=0; i<nRows; i++) rowMap[i] = i;
         }
         this.sortColumn = column;
         this.ascending = ascending;
         reverseMapValid = false;
         sort1(0,nRows);
         TableModelEvent ee = new TableModelEvent(this,0,nRows-1,TableModelEvent.ALL_COLUMNS);
         fireTableChanged(ee);
      }
   }
   private void reSort()
   {
      reverseMapValid = false;
      sort1(0,nRows);
      TableModelEvent ee = new TableModelEvent(this,0,nRows-1,TableModelEvent.ALL_COLUMNS);
      fireTableChanged(ee);
   }
   private void dataChanged()
   {
      // Entire data was changed, including (perhaps) number of rows
      nRows = source.getRowCount();
      rowMap = new int[nRows+10]; // Leave a little room for expansion
      for (int i=0; i<nRows; i++) rowMap[i] = i;
      reverseMapValid = false;
      if (sortColumn != UNSORTED) sort1(0,nRows);
      TableModelEvent ee = new TableModelEvent(this,0,Integer.MAX_VALUE,TableModelEvent.ALL_COLUMNS);
      fireTableChanged(ee);
   }
   // Insert a newly added source row in a sorted list
   private int rowWasInserted(int row)
   {
      if (nRows == rowMap.length)
      {
         int[] newMap = new int[rowMap.length+10];
         System.arraycopy(rowMap,0,newMap,0,rowMap.length);
         rowMap = newMap;
      }
      // Add new row to the end to begin with
      for (int i=0; i<nRows; i++)
      {
         if (rowMap[i] >= row) rowMap[i]++;
      }
      rowMap[nRows] = row;
      // Do a binary search to find out where the new row should go
      int insertPoint = binarySearch(nRows,0,nRows);
      if (insertPoint != nRows)
      {
         System.arraycopy(rowMap,insertPoint, rowMap, insertPoint+1,nRows-insertPoint);
         rowMap[insertPoint] = row;
      }
      nRows++;
      reverseMapValid = false;
      return insertPoint;
   }
   private int binarySearch(int newRow, int start, int end)
   {
      if (start-end < 5)
      {
         for (int i=start; i<end; i++)
         {
            if (compare(newRow,i) <= 0 ) return i;
         }
         return end;
      }
      int mid = end-start >> 1;
      int result = compare(newRow,mid);
      if      (result == 0) return mid;
      else if (result  > 0) return binarySearch(newRow,mid,end);
      else                  return binarySearch(newRow,start,mid);
   }
   private int rowWasDeleted(int row)
   {
      int sortedRow = mapToSorted(row);
      System.arraycopy(rowMap,sortedRow+1,rowMap,sortedRow,nRows-sortedRow-1);
      nRows--;
      for (int i=0; i<nRows; i++)
      {
         if (rowMap[i] > row) rowMap[i]--;
      }
      reverseMapValid = false;
      return sortedRow;
   }
   
   private Object get(int index)
   {
      return source.getValueAt(rowMap[index], sortColumn);
   }
   private int compare(int i, int j)
   {
      return comparator.compare(get(i),get(j)) * (ascending ? 1 : -1);
   }
   private int compare(Object o, int j)
   {
      return comparator.compare(o,get(j)) * (ascending ? 1 : -1);
   }
   private void swap(int i, int j)
   {
      int tmp = rowMap[i];
      rowMap[i] = rowMap[j];
      rowMap[j] = tmp;
   }
   private int med3(int a, int b, int c)
   {
      return compare(a,b)<0 ?
         compare(b,c)<0 ? b : compare(a,c)<0 ? c : a :
            compare(b,c)>0 ? b : compare(a,c)>0 ? c : a;
   }
   private void vecswap(int a, int b, int n)
   {
      for (int i=0; i<n; i++, a++, b++)
         swap(a, b);
   }
   private void sort1(int off, int len)
   {
      // Insertion sort on smallest arrays
      if (len < 7)
      {
         for (int i=off; i<len+off; i++)
            for (int j=i; j>off && compare(j-1,j)>0; j--)
               swap(j, j-1);
         return;
      }
      
      // Choose a partition element, v
      int m = off + (len >> 1);       // Small arrays, middle element
      if (len > 7)
      {
         int l = off;
         int n = off + len - 1;
         if (len > 40) // Big arrays, pseudomedian of 9
         {
            int s = len/8;
            l = med3(l,     l+s, l+2*s);
            m = med3(m-s,   m,   m+s);
            n = med3(n-2*s, n-s, n);
         }
         m = med3(l, m, n); // Mid-size, med of 3
      }
      Object v = get(m);
      
      // Establish Invariant: v* (<v)* (>v)* v*
      int a = off, b = a, c = off + len - 1, d = c;
      int comp;
      while(true)
      {
         while (b <= c && (comp = compare(v,b)) >= 0)
         {
            if (comp == 0) swap(a++, b);
            b++;
         }
         while (c >= b && (comp = compare(v,c)) <= 0)
         {
            if (comp == 0) swap(c, d--);
            c--;
         }
         if (b > c) break;
         swap(b++, c--);
      }
      
      // Swap partition elements back to middle
      int s, n = off + len;
      s = Math.min(a-off, b-a  );  vecswap(off, b-s, s);
      s = Math.min(d-c,   n-d-1);  vecswap(b,   n-s, s);
      
      // Recursively sort non-partition-elements
      if ((s = b-a) > 1) sort1(off, s);
      if ((s = d-c) > 1) sort1(n-s, s);
   }
   
   /**
    * Notifies all listeners of a change to the sorted TableModel.
    * @param event The event to be sent to the listeners.
    */
   protected void fireTableChanged(TableModelEvent event)
   {
      TableModelListener[] l = (TableModelListener[]) listeners.getListeners(TableModelListener.class);
      for (int i=0; i<l.length; i++)
      {
         l[i].tableChanged(event);
      }
   }

   public boolean isSortAscending()
   {
      return ascending;
   }

   public int getSortOnColumn()
   {
      return sortColumn;
   }
   private class InternalTableModelListener implements TableModelListener
   {
      public void tableChanged(TableModelEvent e)
      {
         int column = e.getColumn();
         int first = e.getFirstRow();
         int last = e.getLastRow();
         int type = e.getType();
         if (sortColumn == UNSORTED)
         {
            TableModelEvent ee = new TableModelEvent(DefaultSortableTableModel.this,first,last,column,type);
            fireTableChanged(ee);
         }
         else if (first == TableModelEvent.HEADER_ROW)
         {
            // one or more entire columns was added, removed or changed -- deal with it
            if (type == TableModelEvent.DELETE)
            {
               if (column < sortColumn) sortColumn--;
               else if (column == sortColumn)
               {
                  sort(UNSORTED,true);
               }
            }
            else if (type == TableModelEvent.INSERT)
            {
               if (column <= sortColumn && sortColumn != UNSORTED) sortColumn++;
            }
            else if (type == TableModelEvent.UPDATE)
            {
               if (column == sortColumn || column == TableModelEvent.ALL_COLUMNS) reSort();
            }
            TableModelEvent ee = new TableModelEvent(DefaultSortableTableModel.this,first,last,column,type);
            fireTableChanged(ee);
         }
         else if (column == TableModelEvent.ALL_COLUMNS)
         {
            // one or more rows were added, removed or changed -- deal with it
            
            if (type == TableModelEvent.DELETE)
            {
               for (int i=first; i<=last; i++)
               {
                  int sortedRow = rowWasDeleted(first); // Note, always first because previous first was deleted
                  TableModelEvent ee = new TableModelEvent(DefaultSortableTableModel.this,sortedRow,sortedRow,column,type);
                  fireTableChanged(ee);
               }
            }
            else if (type == TableModelEvent.INSERT)
            {
               for (int i=first; i<=last; i++)
               {
                  int sortedRow = rowWasInserted(i);
                  TableModelEvent ee = new TableModelEvent(DefaultSortableTableModel.this,sortedRow,sortedRow,column,type);
                  fireTableChanged(ee);
               }
            }
            else if (type == TableModelEvent.UPDATE)
            {
               if (Integer.MAX_VALUE == last)
               {
                  dataChanged();
               }
               else
               {
                  for (int i=first; i<=last; i++)
                  {
                     int oldRow = rowWasDeleted(i);
                     int newRow = rowWasInserted(i);
                     if (oldRow == newRow)
                     {
                        TableModelEvent ee = new TableModelEvent(DefaultSortableTableModel.this,newRow,newRow,column,type);
                        fireTableChanged(ee);
                     }
                     else
                     {
                        TableModelEvent ee1 = new TableModelEvent(DefaultSortableTableModel.this,oldRow,oldRow,TableModelEvent.ALL_COLUMNS,TableModelEvent.DELETE);
                        fireTableChanged(ee1);
                        TableModelEvent ee2 = new TableModelEvent(DefaultSortableTableModel.this,newRow,newRow,TableModelEvent.ALL_COLUMNS,TableModelEvent.INSERT);
                        fireTableChanged(ee2);
                     }
                  }
               }
            }
         }
         else if (type == TableModelEvent.UPDATE)
         {
            if (column == sortColumn)
            {
               for (int i=first; i<=last; i++)
               {
                  int oldRow = rowWasDeleted(i);
                  int newRow = rowWasInserted(i);
                  if (oldRow == newRow)
                  {
                     TableModelEvent ee = new TableModelEvent(DefaultSortableTableModel.this,newRow,newRow,column,type);
                     fireTableChanged(ee);
                  }
                  else
                  {
                     TableModelEvent ee1 = new TableModelEvent(DefaultSortableTableModel.this,oldRow,oldRow,TableModelEvent.ALL_COLUMNS,TableModelEvent.DELETE);
                     fireTableChanged(ee1);
                     TableModelEvent ee2 = new TableModelEvent(DefaultSortableTableModel.this,newRow,newRow,TableModelEvent.ALL_COLUMNS,TableModelEvent.INSERT);
                     fireTableChanged(ee2);
                  }
               }
            }
            else
            {
               for (int i=first; i<=last; i++)
               {
                  int sortedRow = mapToSorted(i);
                  TableModelEvent ee = new TableModelEvent(DefaultSortableTableModel.this,sortedRow,sortedRow,column,type);
                  fireTableChanged(ee);
               }
            }
         }
         else throw new UnsupportedOperationException("An unsupported TableModelEvent was found: "+e);
      }
   }
   private class DefaultComparator implements Comparator
   {
      public int compare(Object o1, Object o2)
      {
         if ((o1 instanceof Comparable) && (o2 instanceof Comparable))
            return ((Comparable) o1).compareTo((Comparable) o2);
         else return String.valueOf(o1).compareTo(String.valueOf(o2));
      }
   }
}
