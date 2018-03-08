package org.freehep.swing.table.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import junit.framework.TestCase;

import org.freehep.swing.popup.PopupListener;
import org.freehep.swing.table.DefaultSortableTableModel;
import org.freehep.swing.table.TableColumnSelector;
import org.freehep.swing.table.TableSorter;
/**
 * A Test routine for the Table Sorter.
 * Note this uses the /proc filesystem so will only work on Unix (if there)
 * @author Tony Johnson
 */
public class TopCpu extends TestCase
{
   private static File proc = new File("/proc");
   private static List processes()
   {
      List result = new ArrayList();
      String[] list = proc.list();
      for (int i=0; i<list.length; i++)
      {
         try
         {
            int n = Integer.parseInt(list[i]);
            File stat = new File(proc,n+"/stat");
            try
            {
               BufferedReader reader = new BufferedReader(new FileReader(stat));
               String line = reader.readLine();
               reader.close();
               result.add(new Process(line));
            }
            catch (IOException x)
            {
               // too bad;
            }
         }
         catch (NumberFormatException x)
         {
            // OK, not a process
         }
      }
      return result;
   }
   public void testMain()
   {
      TopCpu.main(null);
   }
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {
      if (!proc.exists()) throw new RuntimeException("/proc not found, not Unix?");
      List l = processes();
      final ProcessTableModel model = new ProcessTableModel(l);
      TableColumnSelector selector = new TableColumnSelector(model);
      DefaultSortableTableModel sm = new DefaultSortableTableModel(selector.getFilteredTableModel());
      JTable table = new JTable(sm);
      table.addMouseListener(new PopupListener(selector.createPopupMenu()));
      new TableSorter(table);
      JFrame frame = new JFrame("Process List");
      frame.setContentPane(new JScrollPane(table));
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
      
      // Main thread continues in background to update display
      
      //Thread.currentThread().setDaemon(true);
      for (;;)
      {
         try
         {
            Thread.sleep(1000);
            final List ll = processes();
            Runnable r = new Runnable()
            {
               public void run()
               {
                  model.update(ll);
               }
            };
            SwingUtilities.invokeAndWait(r);
         }
         catch (Exception x) { x.printStackTrace(); }
      }
   }
   private static class Process
   {
      private int pid;
      private String exe;
      private String state;
      private int utime;
      private int stime;
      private int dutime;
      private int dstime;
      private int prio;
      
      Process(String data)
      {
         String[] tokens = data.split("\\s+");
         pid = Integer.parseInt(tokens[0]);
         exe = tokens[1];
         state = tokens[2];
         utime = Integer.parseInt(tokens[13]);
         stime = Integer.parseInt(tokens[14]);
         prio  = Integer.parseInt(tokens[17]);
      }
      public String toString()
      {
         return pid+" "+exe+" "+state+" "+utime+" "+stime+" "+prio;
      }
      public boolean equals(Object o)
      {
         if (o instanceof Process)
         {
            Process that = (Process) o;
            return this.pid   == that.pid &&
                   this.dutime == that.dutime &&
                   this.dstime == that.dstime &&
                   this.prio  == that.prio &&
                   this.exe.equals(that.exe) &&
                   this.state.equals(that.state);
         }
         else return false;
      }
   }
   private static class ProcessTableModel extends AbstractTableModel
   {
      private List processes;
      private String[] headers = { "pid", "exe", "state", "utime", "stime", "prio" };
      private Class[] types = { Integer.class, String.class, String.class, Integer.class , Integer.class, Integer.class };
      
      ProcessTableModel(List processes)
      {
         this.processes = processes;
      }
      void update(List newProcesses)
      {
         ListIterator i1 = processes.listIterator();
         ListIterator i2 = newProcesses.listIterator();

         int row = 0;
         int pid1 = 0;
         int pid2 = 0;
         Process process1 = null;
         Process process2 = null;
         boolean advance1 = true;
         boolean advance2 = true;
         
         for (;;)
         {
            if (advance1)
            {
               if (i1.hasNext())
               {
                  process1 = (Process) i1.next();
                  pid1 = process1.pid;
               }
               else pid1 = Integer.MAX_VALUE;      
            }
            if (advance2)
            {
               if (i2.hasNext())
               {
                  process2 = (Process) i2.next();
                  pid2 = process2.pid;
               }
               else pid2 = Integer.MAX_VALUE;
            }
            if (pid1 == Integer.MAX_VALUE && pid2 == Integer.MAX_VALUE) break;
                                    
            if (pid1 < pid2)
            {
               i1.remove();
               fireTableRowsDeleted(row,row);
               advance1 = true;
               advance2 = false;
            }
            else if (pid1 == pid2)
            {
               process2.dutime = process2.utime - process1.utime;
               process2.dstime = process2.stime - process1.stime;
               
               if (!process1.equals(process2))
               {
                  i1.set(process2);
                  fireTableRowsUpdated(row,row);
               }
               row++;
               advance1 = advance2 = true;
            }
            else if (pid1 > pid2)
            {
               i1.add(process2);
               fireTableRowsInserted(row,row);
               row++;
               advance1 = false;
               advance2 = true;
            }
         }
      }
      
      public int getColumnCount()
      {
         return headers.length;
      }
      
      public Class getColumnClass(int col)
      {
         return types[col];
      }

      public String getColumnName(int col)
      {
         return headers[col];
      } 
      
      public int getRowCount()
      {
         return processes.size();
      }
      
      public Object getValueAt(int row, int col)
      {
         Process process = (Process) processes.get(row);
         switch (col)
         {
            case 0: return new Integer(process.pid);
            case 1: return process.exe;
            case 2: return process.state;
            case 3: return new Integer(process.dutime);
            case 4: return new Integer(process.dstime);
            case 5: return new Integer(process.prio);
            default: return null;
         }
      }   
   }
}
