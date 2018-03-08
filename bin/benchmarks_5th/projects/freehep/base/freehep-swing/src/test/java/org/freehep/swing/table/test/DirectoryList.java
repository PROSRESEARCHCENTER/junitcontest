package org.freehep.swing.table.test;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
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
import org.freehep.swing.table.TableColumnPacker;
import org.freehep.swing.table.TableColumnSelector;
import org.freehep.swing.table.TableSorter;

/**
 * A Test routine for the Table Column Packer.
 *
 * @author Mark Donszelmann
 * @version $Id: DirectoryList.java 10766 2007-06-02 17:29:04Z tonyj $
 */
public class DirectoryList extends TestCase {
    
    private static JTable table;
    private static File dir = new File(".");
    
    private static List files() {
        File[] list = dir.listFiles();
        return Arrays.asList(list);
    }
    
    public void testMain() {
        DirectoryList.main(null);
    }

    public static void main(String[] args) {
        List dir = files();
        final DirectoryTableModel model = new DirectoryTableModel(dir);
        TableColumnSelector selector = new TableColumnSelector(model);
        DefaultSortableTableModel sm = new DefaultSortableTableModel(selector.getFilteredTableModel());
        final TableColumnPacker packer = new TableColumnPacker();
        packer.setHeaderMargin(15);       
        table = new JTable(sm) {
            public void setBounds(int x, int y, int w, int h) {
                super.setBounds(x, y, w, h);
                packer.packColumns(table);
            }
        };
        table.addMouseListener(new PopupListener(selector.createPopupMenu()));
        new TableSorter(table);
        JFrame frame = new JFrame("Directory List");
        frame.setContentPane(new JScrollPane(table));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
      
        // Main thread continues in background to update display
      
        //Thread.currentThread().setDaemon(true);
        while (true) {
            try {
                Thread.sleep(1000);
                final List list = files();
                Runnable r = new Runnable() {
                    public void run() {
                        model.update(list);
                    }
                };
                SwingUtilities.invokeAndWait(r);
            } catch (Exception x) { 
                x.printStackTrace(); 
            }
        }
    }    
   
    private static class DirectoryTableModel extends AbstractTableModel {
        private List files;
        private String[] headers = { "name", "size", "dir", "r/o", "hidden", "modified", "path" };
        private Class[] types = { String.class, Long.class, Boolean.class, Boolean.class , Boolean.class, Date.class, String.class };
      
        DirectoryTableModel(List files) {
            this.files = files;
        }
      
        void update(List newFiles) {
            ListIterator i1 = files.listIterator();
            ListIterator i2 = newFiles.listIterator();

             int row = 0;
             File file1 = null;
             File file2 = null;
             boolean advance1 = true;
             boolean advance2 = true;
         
            while (true) {
                if (advance1) {
                    if (i1.hasNext()) {
                        file1 = (File) i1.next();
                    } else file1 = null;      
                }
                if (advance2) {
                    if (i2.hasNext()) {
                        file2 = (File) i2.next();
                    } else file2 = null;
                }
                if ((file1 == null) && (file2 == null)) break;
                
                int c = file1.getName().compareTo(file2.getName());
                if (c < 0) {
                    i1.remove();
                    fireTableRowsDeleted(row,row);
                    advance1 = true;
                    advance2 = false;
                } else if (c == 0) {
                    if (!file1.equals(file2)) {
                        i1.set(file2);
                        fireTableRowsUpdated(row,row);
                    }
                    row++;
                    advance1 = advance2 = true;
                } else if (c > 0) {
                    i1.add(file2);
                    fireTableRowsInserted(row,row);
                    row++;
                    advance1 = false;
                    advance2 = true;
                }
            }
        }
      
        public int getColumnCount() {
            return headers.length;
        }
      
        public Class getColumnClass(int col) {
            return types[col];
        }

        public String getColumnName(int col) {
            return headers[col];
        } 
      
        public int getRowCount() {
            return files.size();
        }
      
        public Object getValueAt(int row, int col) {
            File file = (File) files.get(row);
            switch (col) {
                case 0: return file.getName();
                case 1: return new Long(file.length());
                case 2: return new Boolean(file.isDirectory());
                case 3: return new Boolean(!file.canWrite());
                case 4: return new Boolean(file.isHidden());
                case 5: return new Date(file.lastModified());
                case 6: return file.getAbsolutePath();
                default: return "N/A";
            }
        }   
    }
}
