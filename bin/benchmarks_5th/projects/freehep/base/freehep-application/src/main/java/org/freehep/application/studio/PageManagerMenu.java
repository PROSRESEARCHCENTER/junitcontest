package org.freehep.application.studio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import org.freehep.application.Application;
import org.freehep.application.mdi.PageManager;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Result;
import org.openide.util.Lookup.Template;

/**
 * A menu that includes a list of all available page managers.
 *
 * @author Tony Johnson
 */
public class PageManagerMenu extends JMenu implements ActionListener {

    @Override
    public void fireMenuSelected() {
        removeAll();
        Studio app = (Studio) Application.getApplication();
        PageManager pm = app.getPageManager();
        Template template = new Template(PageManager.class);
        Result result = app.getLookup().lookup(template);
        for (Iterator i = result.allItems().iterator(); i.hasNext();) {
            Item item = (Item) i.next();
            JRadioButtonMenuItem button = new JRadioButtonMenuItem(item.getId());
            button.setSelected(item.getInstance() == pm);
            button.addActionListener(this);
            add(button);
        }
        super.fireMenuSelected();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Studio app = (Studio) Application.getApplication();
        String command = e.getActionCommand();
        Template template = new Template(PageManager.class, command, null);
        Result result = app.getLookup().lookup(template);
        PageManager manager = (PageManager) result.allInstances().iterator().next();
        app.setPageManager(manager);
        app.getUserProperties().setProperty("pageManagerName", command);

    }
}