package org.freehep.application.mdi;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.freehep.application.Application;

/**
 * A menu that includes a list of all active windows.
 *
 * @author Tony Johnson
 */
public class WindowMenu extends JMenu {

    private int nOriginal = -1;
    private PageManager pm;

    @Override
    public void fireMenuSelected() {
        if (nOriginal < 0) {
            nOriginal = getMenuComponentCount();
        }

        MDIApplication app = (MDIApplication) Application.getApplication();
        pm = app.getPageManager();
        app.setSelectedPageManager(pm);
        int nn = 0;
        List pages = pm.pages();
        if (!pages.isEmpty()) {
            addSeparator();
        }
        for (Iterator i = pages.iterator(); i.hasNext(); nn++) {
            PageContext context = (PageContext) i.next();
            JMenuItem item = new WindowMenuItem(context, nn);
            add(item);
        }
        super.fireMenuSelected();
    }

    @Override
    protected void fireMenuDeselected() {
        super.fireMenuDeselected();
        // Fix for Freehep-552
        int n = getMenuComponentCount();
        for (int i = n; i > nOriginal;) {
            this.remove(--i);
        }
    }

    private class WindowMenuItem extends JMenuItem {

        WindowMenuItem(PageContext context, int i) {
            super(i + " " + context.getTitle());
            this.context = context;
            setMnemonic('0' + (char) (i));
            setIcon(context.getIcon());
        }

        @Override
        protected void fireActionPerformed(ActionEvent evt) {
            pm.show(context);
        }
        private PageContext context;
    }
}