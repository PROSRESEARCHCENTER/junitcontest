package org.freehep.swing.table.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.freehep.swing.table.DefaultSortableTableModel;
import org.freehep.swing.table.SortableTableModel;
import org.freehep.swing.table.TableColumnSelector;

/**
 *
 * @author Tony Johnson
 * @version $Id: DefaultSortableTableModelTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class DefaultSortableTableModelTest extends TestCase
{
   
   public DefaultSortableTableModelTest(java.lang.String testName)
   {
      super(testName);
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite(DefaultSortableTableModelTest.class);
      return suite;
   }
   
   public void testBasic()
   {
      TestModel test = new TestModel(100);
      test.testInvariants(test);
      
      DefaultSortableTableModel model = new DefaultSortableTableModel(test);
      test.testInvariants(model);
      
      TestListener l = new TestListener();
      model.addTableModelListener(l);
      TableModelEvent expected = new TableModelEvent(model,0,model.getRowCount()-1,TableModelEvent.ALL_COLUMNS,TableModelEvent.UPDATE);
      
      model.sort(0,true);
      test.testInvariants(model);
      test.testSortOrder(model,0,true);
      test.testSortOrder(model,1,true);
      l.testEvent(1,expected);
      
      model.sort(0,true);
      test.testInvariants(model);
      test.testSortOrder(model,0,true);
      test.testSortOrder(model,1,true);
      l.testEvent(0,null);
      
      model.sort(0,false);
      test.testInvariants(model);
      test.testSortOrder(model,0,false);
      test.testSortOrder(model,1,false);
      l.testEvent(1,expected);

      model.sort(1,false);
      test.testInvariants(model);
      test.testSortOrder(model,1,false);
      test.testSortOrder(model,0,false);
      l.testEvent(1,expected);
      
      model.sort(1,true);
      test.testInvariants(model);
      test.testSortOrder(model,1,true);
      test.testSortOrder(model,0,true);
      l.testEvent(1,expected);
      
      model.sort(2,true);
      test.testInvariants(model);
      test.testSortOrder(model,2,true);
      l.testEvent(1,expected);
      
      model.sort(SortableTableModel.UNSORTED,true);
      test.testInvariants(model);
      test.testEquals(model);
      l.testEvent(1,expected);
      
      model.sort(SortableTableModel.UNSORTED,true);
      test.testInvariants(model);
      test.testEquals(model);
      l.testEvent(0,null);
   }

   public void testColumns()
   {
      TestModel test = new TestModel(100);
      test.testInvariants(test);
      
      TableColumnSelector selector = new TableColumnSelector(test);
      TableModel columns = selector.getFilteredTableModel();
      test.testInvariants(columns);
      
      DefaultSortableTableModel model = new DefaultSortableTableModel(columns);
      test.testInvariants(model);
      
      TestListener l = new TestListener();
      model.addTableModelListener(l);
      
      model.sort(2,false);
      test.testInvariants(model);
      test.testSortOrder(model,2,false);
      
      selector.setHideColumn(1,true);
      test.testSortOrder(model,1,false);
      
      selector.setHideColumn(1,false);
      test.testSortOrder(model,2,false);
      
      selector.setHideColumn(2,true);
      assertEquals(model.getColumnCount(),test.getColumnCount()-1);

      selector.setHideColumn(2,false);
      test.testInvariants(model);
      
      model.sort(2,false);
      test.testInvariants(model);
      test.testSortOrder(model,2,false);
      
      model.sort(0,true);
      test.testInvariants(model);
      test.testSortOrder(model,0,true); 
      
      selector.setHideColumn(0,true);
      
      model.sort(0,true);
      test.testSortOrder(model,0,true); 
      
      selector.setHideColumn(0,false);
      test.testInvariants(model);
      test.testSortOrder(model,0,true); 
      test.testSortOrder(model,1,true); 
   }
   
   public void testAdvanced()
   {
      TestModel test = new TestModel(100);
      test.testInvariants(test);
      
      DefaultSortableTableModel model = new DefaultSortableTableModel(test);
      test.testInvariants(model);
      
      TestListener l = new TestListener();
      model.addTableModelListener(l);
      TableModelEvent expected = new TableModelEvent(model,0,model.getRowCount()-1,TableModelEvent.ALL_COLUMNS,TableModelEvent.UPDATE);
      
      model.sort(0,true);
      test.testInvariants(model);
      test.testSortOrder(model,0,true);
      l.testEvent(1,expected);
      
      Random random = new Random();
      
      for (int j=0; j<100; j++)
      {
         int source = random.nextInt(test.getRowCount());
         int dest = random.nextInt(test.getRowCount());
         
         int n = test.getColumnCount();
         Object[] row = new Object[n];
         for (int i=0; i<n; i++) row[i] = test.getValueAt(source,i);
         test.removeRow(source);
         test.testSortOrder(model,0,true);
         test.testSortOrder(model,1,true);
         l.testEvent(1,null);

         test.insertRow(dest,row);
         test.testInvariants(model);
         test.testSortOrder(model,0,true);
         test.testSortOrder(model,1,true);      
         l.testEvent(1,null);
      }
      for (int j=0; j<100; j++)
      {    
         int row = random.nextInt(test.getRowCount());
         int newValue = random.nextInt(test.getRowCount());
         Object old = test.getValueAt(row,0);
         test.setValueAt(new Integer(newValue),row,0);
         test.testSortOrder(model,0,true);
         //l.testEvent(2,null);
         
         test.setValueAt(old,row,0);
         test.testInvariants(model);
         test.testSortOrder(model,0,true);
         test.testSortOrder(model,1,true);      
         //l.testEvent(2,null);        
      }
   }
   
   private static class TestModel extends DefaultTableModel
   {
      private int[] sum;
      private int size;
      
      TestModel(int size)
      {
         List list = new ArrayList();
         for (int i=0; i<size; i++) list.add(new Integer(i));
         Collections.shuffle(list);

         Integer[][] data = new Integer[size][3];
         Random random = new Random();

         int rSum = 0;
         for (int i=0; i<size; i++)
         {
            int r = random.nextInt(1000);
            rSum += r;
            data[i][0] = (Integer) list.get(i);
            data[i][1] = new Integer(data[i][0].intValue()*2);
            data[i][2] = new Integer(r);
         }
         String[] columnNames = { "a" , "b" , "c" };  
         setDataVector(data,columnNames);
         
         sum = new int[3];
         sum[1] = ((size-1) * size);
         sum[0] = sum[1]/2;
         sum[2] = rSum;
         this.size = size;
      }
      void testInvariants(TableModel in)
      {
         assertEquals(in.getRowCount(),size);
         assertEquals(in.getColumnCount(),3);
         int[] testSum = new int[3];
         for (int i=0; i<size; i++)
         {
            int i0 = ((Integer) in.getValueAt(i,0)).intValue();
            int i1 = ((Integer) in.getValueAt(i,1)).intValue();
            int i2 = ((Integer) in.getValueAt(i,2)).intValue();
            assertEquals(2*i0,i1);
            testSum[0] += i0;
            testSum[1] += i1;
            testSum[2] += i2;
         }
         assertEquals(testSum[0],sum[0]);
         assertEquals(testSum[1],sum[1]);
         assertEquals(testSum[2],sum[2]);
      }
      void testSortOrder(TableModel in, int column, boolean ascending)
      {
         int sign = ascending ? -1 : 1;
         Comparable prev = (Comparable) in.getValueAt(0,column);
         for (int i=1; i<in.getRowCount(); i++)
         {
            Comparable next = (Comparable) in.getValueAt(i,column);
            assertTrue(prev.compareTo(next)*sign >= 0);
            prev = next;
         }
      }
      void testEquals(TableModel in)
      {
         assertEquals(in.getRowCount(), getRowCount());
         assertEquals(in.getColumnCount(), getColumnCount());
         for (int i=0; i<getRowCount(); i++)
         {
            for (int j=0; j<getColumnCount(); j++)
            {
               assertEquals(in.getValueAt(i,j),getValueAt(i,j));
            }
         }
      }
   }
   private static class TestListener implements TableModelListener
   {
      private int n;
      private TableModelEvent lastEvent;

      public void tableChanged(TableModelEvent tableModelEvent)
      {
         n++;
         lastEvent = tableModelEvent;
      }
      void testEvent(int expected, TableModelEvent expectedEvent)
      {
         assertEquals(expected,n);
         n = 0;
         if (expectedEvent != null)
         {
            assertNotNull(lastEvent);
            assertEquals(lastEvent.getSource(),expectedEvent.getSource());
            assertEquals(lastEvent.getType(),expectedEvent.getType());
            assertEquals(lastEvent.getColumn(),expectedEvent.getColumn());
            assertEquals(lastEvent.getFirstRow(),expectedEvent.getFirstRow());
            assertEquals(lastEvent.getLastRow(),expectedEvent.getLastRow());
            lastEvent = null;
         }

      }
   }
   
}
