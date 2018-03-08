// Copyright 2004, FreeHEP.
package org.freehep.swing.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.BitSet;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Allows the user to select which columns are visible in a table.
 * <p>
 * Example of usage:
 * <pre>
 *    TableModel model = ...
 *    TableColumnSelector selector = new TableColumnSelector(model);
 *    JTable table = new JTable(selector.getFilteredTableModel());
 *    table.addMouseListener(new PopupListener(selector.createPopupMenu()));
 * </pre>
 * @author Tony Johnson
 */
public class TableColumnSelector
{
   private BitSet hidden = new BitSet();
   private TableModel source;
   private TableModel result;
   private EventListenerList listeners = new EventListenerList();
   private TableModelListener internalListener = new InternalTableModelListener();
   
   /**
    * Create a TableColumnSelector.
    * @param model The source table model.
    */   
   public TableColumnSelector(TableModel model)
   {
      this.source = model;
      this.result = new InternalTableModel();
   }
   private int mapFromFilter(int columnIndex)
   {
      int result = columnIndex;
      if (!hidden.isEmpty())
      {
         for (int i=0; columnIndex >= 0 ;i++ )
         {
            if (!hidden.get(i)) columnIndex--;
            else result++;
         }
      }
      return result;
   }
   private int mapToFilter(int columnIndex)
   {
      int result = columnIndex;
      if (!hidden.isEmpty())
      {
         for (int i=0; i<columnIndex; i++)
         {
            if (hidden.get(i)) result--;
         }
      }
      return result;
   }
   
   /**
    * Can be used to fill a JMenu or JPopupMenu with appropriate JCheckBoxMenuItems.
    * @param menu The JMenu or JPopupMenu to populate.
    */   
   public void populateMenu(JComponent menu)
   {
      ActionListener l = new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            int i = Integer.parseInt(e.getActionCommand());
            boolean hide = !((JCheckBoxMenuItem) e.getSource()).isSelected();
            setHideColumn(i,hide);
         }
      };
      for (int i=0; i<source.getColumnCount(); i++)
      {
         JCheckBoxMenuItem item = new JCheckBoxMenuItem(source.getColumnName(i));
         item.setActionCommand(String.valueOf(i));
         item.setSelected(!hidden.get(i));
         item.addActionListener(l);
         menu.add(item);
      }
   }
   /**
    * Creates a JPopupMenu filled with appropriate JCheckBoxMenuItems. Can be used as the popup menu for the JTable.
    * @return The created menu.
    */   
   public JPopupMenu createPopupMenu()
   {
      JPopupMenu result = new JPopupMenu()
      {
         protected void firePopupMenuWillBecomeVisible()
         {
            populateMenu(this);
         }
         protected void firePopupMenuWillBecomeInvisible()
         {
            removeAll();
         }
      };
      return result;
   }
   /**
    * Show or Hide the specied column.
    * @param columnIndex The columnIndex in the source TableModel
    * @param hide if <CODE>true</CODE> hides this column
    */   
   public void setHideColumn(int columnIndex, boolean hide)
   {
      if (hide != hidden.get(columnIndex))
      {
         hidden.set(columnIndex,hide);
         if (listeners.getListenerCount() != 0)
         {
            int type = hide ? TableModelEvent.DELETE : TableModelEvent.INSERT;
            int column = mapToFilter(columnIndex);
            TableModelEvent event = new TableModelEvent(result,TableModelEvent.HEADER_ROW,TableModelEvent.HEADER_ROW,column,type);
            fireTableChanged(event);
         }
      }
   }
   /**
    * Notifies all listeners of a change to the filtered TableModel.
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
   /**
    * Test if a column is hidden.
    * @param columnIndex The columnIndex in the source TableModel.
    * @return <CODE>true</CODE> if this column is hidden
    */   
   public boolean isHideColumn(int columnIndex)
   {
      return hidden.get(columnIndex);
   }
   /**
    * Get the resulting table model. This is the table model that should actually be installed.
    * @return The filtered table mode.
    */   
   public TableModel getFilteredTableModel()
   {
      return result;
   }
   
   private class InternalTableModel implements TableModel
   {
      
      public void addTableModelListener(TableModelListener l)
      {
         if (listeners.getListenerCount() == 0) source.addTableModelListener(internalListener);
         listeners.add(TableModelListener.class, l);
      }
      
      public Class getColumnClass(int columnIndex)
      {
         return source.getColumnClass(mapFromFilter(columnIndex));
      }
      
      public int getColumnCount()
      {
         return source.getColumnCount() - hidden.cardinality();
      }
      
      public String getColumnName(int columnIndex)
      {
         return source.getColumnName(mapFromFilter(columnIndex));
      }
      
      public int getRowCount()
      {
         return source.getRowCount();
      }
      
      public Object getValueAt(int rowIndex, int columnIndex)
      {
         return source.getValueAt(rowIndex,mapFromFilter(columnIndex));
      }
      
      public boolean isCellEditable(int rowIndex, int columnIndex)
      {
         return source.isCellEditable(rowIndex,mapFromFilter(columnIndex));
      }
      
      public void removeTableModelListener(TableModelListener l)
      {
         listeners.remove(TableModelListener.class, l);
         if (listeners.getListenerCount() == 0) source.removeTableModelListener(internalListener);
      }
      
      public void setValueAt(Object aValue, int rowIndex, int columnIndex)
      {
         source.setValueAt(aValue, rowIndex, mapFromFilter(columnIndex));
      }
   }
   private class InternalTableModelListener implements TableModelListener
   {
      public void tableChanged(TableModelEvent e)
      {
         int column = e.getColumn();
         if (column == TableModelEvent.ALL_COLUMNS || !hidden.get(column))
         {
            int first = e.getFirstRow();
            int last = e.getLastRow();
            int type = e.getType();
            if (column != TableModelEvent.ALL_COLUMNS) column = mapToFilter(column);
            TableModelEvent ee = new TableModelEvent(result,first,last,column,type);
            fireTableChanged(ee);        
         }
      }
   }
}
