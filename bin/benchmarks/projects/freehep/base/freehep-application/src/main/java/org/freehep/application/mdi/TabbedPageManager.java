package org.freehep.application.mdi;

import java.awt.Component;
import java.awt.Point;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TabbedPageManager.java 14082 2012-12-12 16:16:53Z tonyj $
 */
public class TabbedPageManager extends PageManager {

    /**
     * Creates new TabbedPageManager
     */
    public TabbedPageManager() {
        setPageManagerType("Tab");
    }

    public void setTabPlacement(int placement) {
        tabPlacement = placement;
        if (tabs != null) {
            tabs.setTabPlacement(placement);
        }
    }

    public int getTabPlacement() {
        return tabPlacement;
    }

    @Override
    protected void show(PageContext page) {
        tabs.setSelectedComponent(page.getPage());
    }

    @Override
    protected Component getEmbodiment() {
        return tabs;
    }

    @Override
    protected boolean close(PageContext page) {
        boolean ok = super.close(page);
        if (ok) {
            tabs.removeTabAt(indexOfPage(page));
        }
        cl.stateChanged(new ChangeEvent(this));
        return ok;
    }

    private String hackedTitle(String title) {
        return title == null ? CloseButtonTabbedPane.TAB_NAME_TRAILING_SPACE : title + CloseButtonTabbedPane.TAB_NAME_TRAILING_SPACE;
    }

    @Override
    protected void titleChanged(PageContext page) {
        tabs.setTitleAt(indexOfPage(page), hackedTitle(page.getTitle()));
    }

    @Override
    protected void iconChanged(PageContext page) {
        tabs.setIconAt(indexOfPage(page), page.getIcon());
    }

    protected int indexOfPage(PageContext page) {
        return tabs.indexOfComponent(page.getPage());
    }

    @Override
    public PageContext openPage(Component c, String title, Icon icon, String type, boolean selectOnOpen) {
        PageContext context = super.openPage(c, title, icon, type, selectOnOpen);
        tabs.addTab(hackedTitle(title), icon, c);
        // Should the open event be before or after the selection event? For the first page it's after, for all the other ones is before.
        // This is due to the fact that when adding the first tab, the tab is automatically selected, while when adding other tabs they are not
        // selected automatically.
        super.firePageOpened(context);
        if (selectOnOpen) {
            tabs.setSelectedComponent(c);
        }
        return context;
    }

    @Override
    protected void init(List pages, PageContext selected) {
        tabs.removeChangeListener(cl);
        Iterator i = pages.iterator();
        while (i.hasNext()) {
            PageContext context = (PageContext) i.next();
            tabs.addTab(hackedTitle(context.getTitle()), context.getIcon(), context.getPage());
            if (context == selected) {
                tabs.setSelectedComponent(context.getPage());
            }
        }
        super.init(pages, selected);
        tabs.addChangeListener(cl);
        if (selected == null && pages.size() > 0) {
            fireSelectionChanged((PageContext) pages().get(tabs.getSelectedIndex()));
        }
    }

    @Override
    public PageContext getSelectedPage() {
        PageContext selected = super.getSelectedPage();
        if (selected != null) {
            return selected;
        }
        int selectedIndex = tabs.getSelectedIndex();
        if (selectedIndex < 0) {
            return null;
        }
        return (PageContext) pages().get(selectedIndex);
    }

    @Override
    protected void setActive(boolean active) {
        if (active) {
            tabs = new CloseButtonTabbedPane() {
                @Override
                protected void fireCloseTabAt(final int index) {
                    close((PageContext) pages().get(index));
                }

                @Override
                public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
                    return TabbedPageManager.this.modifyPopupMenu(menu, source, p);
                }
            };
            tabs.setTabPlacement(tabPlacement);
            cl = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int index = tabs.getSelectedIndex();
                    if (index != -1) {
                        fireSelectionChanged((PageContext) pages().get(index));
                    }
                }
            };
            tabs.addChangeListener(cl);
        } else {
            tabs.removeAll();
            tabs.removeChangeListener(cl);
            tabs = null;
        }
    }
    protected JTabbedPane tabs;
    private ChangeListener cl;
    private int tabPlacement = JTabbedPane.TOP;
}
