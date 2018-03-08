package hep.aida.ref.plotter.style.registry;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.MenuElement;

import org.freehep.util.FreeHEPLookup;


public class CategoryMenu extends JPopupMenu {
    static String none = "[NONE]";
    private IStyleRegistry registry;
    
    public CategoryMenu(String title) {
        super(title);
    }
    
    protected void firePopupMenuWillBecomeVisible() {
        clearMenu();
        fillMenu();
        super.firePopupMenuWillBecomeVisible();
    }
    
    private void clearMenu() {
        MenuElement[] elements = getSubElements();
        if (elements == null) return;
        
        for (int i=0; i<elements.length; i++) {
            if (elements[i] instanceof JMenu) {
                removeAll();
            } else if (elements[i] instanceof JMenuItem){
                remove((JMenuItem) elements[i]);
            }
        }        
    }
    
    private void fillMenu() {
        if (registry == null) registry = (IStyleRegistry) FreeHEPLookup.instance().lookup(IStyleRegistry.class);
        
        String[] keys = registry.getAvailableCategoryKeys();
        if (keys == null) return;
        
        for (int i=0; i<keys.length; i++) {
            String key = keys[i];
            String[] values = registry.getAvailableCategoryValues(key);
            String current = registry.getCategoryCurrentValue(key);
            
            ButtonGroup group = new ButtonGroup();
            JMenu menu = new JMenu(key);
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(new CategoryAction(key, none));
            group.add(item);
            menu.add(item);
            if (current == null || current.trim().equals("")) item.doClick();
            
            for (int j=0; j<values.length; j++) {
                item = new JRadioButtonMenuItem(new CategoryAction(key, values[j]));
                group.add(item);
                menu.add(item);
                if (values[j] != null && values[j].equals(current)) item.doClick();
            }
            this.add(menu);
        }
    }
    
    private void setCategory(String key, String value) {
        if (registry == null) registry = (IStyleRegistry) FreeHEPLookup.instance().lookup(IStyleRegistry.class);
        if (none.equals(value)) value = null;
        System.out.println("Setting Category: key="+key+", value="+value);
        registry.setCategoryCurrentValue(key, value);
    }
    
    class CategoryAction extends AbstractAction {
        String key;
        
        CategoryAction(String key, String value) {
            super(value);
            this.key = key;
        }
        
        public void actionPerformed(ActionEvent ev) {
            String action = ev.getActionCommand();
            setCategory(key, (String) getValue(AbstractAction.NAME));
        }
    }    
}
