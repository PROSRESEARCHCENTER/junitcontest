package org.freehep.application.mdi;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import org.freehep.application.Application;
import org.freehep.swing.popup.HasPopupItems;

/**
 * A TabbedPageManager that only shows its tabs when there is more than one
 * page. This is the default PageManager used by the control and console areas.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ControlPageManager.java 14082 2012-12-12 16:16:53Z tonyj $
 */
public class ControlPageManager extends TabbedPageManager {

    /**
     * Creates new ControlPageManager
     */
    public ControlPageManager() {
        setTabPlacement(JTabbedPane.BOTTOM);
    }

    @Override
    protected Component getEmbodiment() {
        return top;
    }

    @Override
    protected boolean close(PageContext page) {
        boolean ok = super.close(page);
        if (!ok) {
            return ok;
        }
        int nPages = getPageCount();
        if (nPages == 1) {
            justOne = tabs.getComponentAt(0);
            tabs.putClientProperty("__index_to_remove__", null); // Work around for java bug 5022375 
            tabs.setComponentAt(0, new JPanel()); // just a placeholder
            justOne.setVisible(true);
            top.remove(tabs);
            top.add(justOne, BorderLayout.CENTER);
            top.revalidate();
        } else if (nPages == 0) {
            top.remove(justOne);
            justOne = null;
            top.revalidate();
        }
        return ok;
    }

    @Override
    public PageContext openPage(Component c, String title, Icon icon, String type, boolean selectOnOpen) {
        PageContext context = super.openPage(c, title, icon, type, selectOnOpen);
        int nPages = getPageCount();
        if (nPages == 1) {
            tabs.setComponentAt(0, new JPanel()); // just a placeholder
            justOne = c;
            c.setVisible(true);
            top.add(justOne, BorderLayout.CENTER);
            top.revalidate();
        } else if (nPages == 2) {
            top.remove(justOne);
            tabs.putClientProperty("__index_to_remove__", null);
            tabs.setComponentAt(0, justOne);
            justOne = null;
            Application.updateComponentTreeUI(tabs); // In case look and feel changed
            top.add(tabs);
            top.revalidate();
            top.repaint(); // Fixes JAS-161 (but why exactly? See http://www.eos.dk/archive/swing/msg02250.html)
        }
        return context;
    }

    @Override
    protected int indexOfPage(PageContext page) {
        if (page.getPage() == justOne) {
            return 0;
        } else {
            return super.indexOfPage(page);
        }
    }

    @Override
    protected void show(PageContext page) {
        if (getPageCount() > 1) {
            super.show(page);
        }
    }

    protected class InternalPanel extends JPanel implements HasPopupItems {

        InternalPanel() {
            super(new BorderLayout());
        }

        @Override
        public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
            if (ControlPageManager.this.getPageCount() == 1) {
                return ControlPageManager.this.modifyPopupMenu(menu, source, p);
            } else {
                return menu;
            }
        }
    }
    private JPanel top = new InternalPanel();
    private Component justOne;
}