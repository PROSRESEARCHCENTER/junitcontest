// Copyright 2004, FreeHEP.
package org.freehep.swing.table;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * Abstract class listens for clicks on a table column header.
 *
 * @author Mark Donszelmann
 * @version $Id: TableColumnHeaderListener.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TableColumnHeaderListener extends MouseAdapter {
    
    public void mouseClicked(MouseEvent evt) {
        JTable table = ((JTableHeader)evt.getSource()).getTable();
        TableColumnModel colModel = table.getColumnModel();

        // The index of the column whose header was clicked
        int vColIndex = colModel.getColumnIndexAtX(evt.getX());
        int mColIndex = table.convertColumnIndexToModel(vColIndex);

        // Return if not clicked on any column header
        if (vColIndex == -1) {
            return;
        }

        // Determine if mouse was clicked between column heads
        Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);
        if (vColIndex == 0) {
            headerRect.width -= 3;    // Hard-coded constant
        } else {
            headerRect.grow(-3, 0);   // Hard-coded constant
        }
        
        if (!headerRect.contains(evt.getX(), evt.getY())) {
            // Mouse was clicked between column heads
            // vColIndex is the column head closest to the click

            // vLeftColIndex is the column head to the left of the click
            int vLeftColIndex = vColIndex;
            if (evt.getX() < headerRect.x) {
                vLeftColIndex--;
            }
        } else {
            headerClicked(table, mColIndex);
        }
    }
    
    public abstract void headerClicked(JTable table, int col);
}
