package hep.aida.ref.plotter.style.registry;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

public class OpenStyleStorePanel extends JPanel {
    private Component parent;
    private String title;
    private JPanel thisPanel;
    private JComboBox storeList;
    private JButton update;
    private Vector list;
    private Map map;
    
    public OpenStyleStorePanel() {
        this("Open Style Store");
    }
    public OpenStyleStorePanel(Component parent) {
        this(parent, "Open Style Store");
    }
    public OpenStyleStorePanel(String title) {
        this(null, title);
    }
    public OpenStyleStorePanel(Component parent, String title) {
        super();
        this.parent = parent;
        this.title = title;
        thisPanel = this;
        initComponents();
    }
    
    private void initComponents() {
        list = new Vector();
        map = new HashMap();
        
        update = new JButton("Update");
        update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                updateAction();
            }
        });
        
        storeList = new JComboBox();
        this.setBorder(new TitledBorder(new EtchedBorder(), title));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);
        
        this.setLayout(new java.awt.GridBagLayout());
        
        gbc.gridy=0; gbc.gridx=0;
        this.add(new JLabel(" Store: "), gbc);
        gbc.gridx=1;
        this.add(storeList, gbc);
        gbc.gridx=2;
        this.add(update, gbc);
        
        updateAction();
    }
    
    
    public void updateAction() {
         list.clear();
         map.clear();
         
         StyleRegistry registry = StyleRegistry.getStyleRegistry();
         String[] stores = registry.getAvailableStoreNames();
         if (stores != null) {
             for (int i=0; i<stores.length; i++) {
                 map.put(stores[i], registry.getStore(stores[i]));
                 list.add(stores[i]);
             }
         }
         Lookup.Template template = new Lookup.Template(IStyleStore.class);
         Lookup.Result result = FreeHEPLookup.instance().lookup(template);
         for (Iterator i = result.allInstances().iterator(); i.hasNext(); )
         {
            IStyleStore store = (IStyleStore) i.next();
            if (store != null && !map.containsValue(store)) {
                 map.put(store.getStoreName(), store);
                 list.add(store.getStoreName());                
            }
         }
         DefaultComboBoxModel model = new DefaultComboBoxModel(list);
         storeList.setModel(model);
    }
    
    public IStyleStore openStore() throws IOException, org.jdom.JDOMException {
        updateAction();
        IStyleStore store = null;
        Component comp = parent;
        if (comp == null) comp = (Component) SwingUtilities.getAncestorOfClass(Frame.class, this);
        int reply = JOptionPane.showOptionDialog(comp, this, title, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
        if (reply == JOptionPane.YES_OPTION) {
            String name = (storeList.getSelectedItem() instanceof String) ? (String) storeList.getSelectedItem() : storeList.getSelectedItem().toString();
            store = (IStyleStore) map.get(name);
        } 
        return store;
    }
    
}
