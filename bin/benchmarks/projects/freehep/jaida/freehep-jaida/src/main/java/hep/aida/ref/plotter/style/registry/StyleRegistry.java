package hep.aida.ref.plotter.style.registry;

import hep.aida.IPlotterFactory;
import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.PlotterFactory;
import hep.aida.ref.plotter.PlotterStyle;
import hep.aida.ref.plotter.Style;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.freehep.util.FreeHEPLookup;

public class StyleRegistry implements IStyleRegistry, IGlobalIndexProvider {
    public static String systemStoreProperty = "hep.aida.ref.plotter.style.registry.system.store";
    public static String groupStoreProperty  = "hep.aida.ref.plotter.style.registry.group.store";
    public static String userStoreProperty   = "hep.aida.ref.plotter.style.registry.user.store";
    private static String systemStoreFile    = "SystemStyleStore.xml";
    private static StyleRegistry registry;
    private IPlotterFactory pf;
    private List stores;
    private Map categories;
    private Object lock;
    
    private int globalIndex = 0;
    
    public static StyleRegistry getStyleRegistry() {
        if (registry == null) registry = new StyleRegistry();
        return registry;
    }
    
    private StyleRegistry() {
        stores = new ArrayList(10);
        categories = new HashMap(5);
        lock = new Object();
        String systemFile = System.getProperty(systemStoreProperty, systemStoreFile);
        String groupFile  = System.getProperty(groupStoreProperty, null);
        String userFile   = System.getProperty(userStoreProperty, null);
        try {
            //URL url = getClass().getResource(systemFile);
            InputStream stream = getClass().getResourceAsStream(systemFile);
            IStyleStore systemStore = StyleStoreXMLReader.restoreFromStream(stream);
            addStore(systemStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (groupFile != null && !groupFile.trim().equals("")) {
            try {
                InputStream stream = getClass().getResourceAsStream(groupFile);
                IStyleStore store = StyleStoreXMLReader.restoreFromStream(stream);
                addStore(store);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (userFile != null && !userFile.trim().equals("")) {
            try {
                InputStream stream = getClass().getResourceAsStream(userFile);
                IStyleStore store = StyleStoreXMLReader.restoreFromStream(stream);
                addStore(store);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (FreeHEPLookup.instance().lookup(IStyleRegistry.class) == null) FreeHEPLookup.instance().add(this);
    }

    
    // IGlobalIndexProvider methods
    
    public int getIndex() {
        int index = globalIndex;
        globalIndex++;
        return index;
    }
    
    public void resetIndex() {
        globalIndex = 0;
    }
    
    // Service methods
    
    IPlotterStyle mergeStyles(List styles, IPlotterState state) {
        if (styles == null || styles.size() == 0) return null;
        if (pf == null) pf = new PlotterFactory();
        PlotterStyle style = (PlotterStyle) pf.createPlotterStyle();
        style.setParameter(Style.PLOTTER_STYLE_NAME, "StyleRegistry");
        
        // Set parameters that are revolving
        /*
        String fillColor    = null;
        String lineColor    = null;
        String errorColor   = null;
        String markerColor  = null;
        String outlineColor = null;
        for (int i=0; i<styles.size(); i++) {
            IPlotterStyle parent = (IPlotterStyle) styles.get(i);
            if (fillColor == null && ((BaseStyle) parent.dataStyle().fillStyle()).isParameterSet("color")) {
                AbstractStyleParameter par = ((BaseStyle) parent.dataStyle().fillStyle()).parameter("color");
                fillColor = par.parValue();
                if (par instanceof RevolvingStyleParameter) ((RevolvingStyleParameter) par).incrementCurrentIndex();
            }
            if (lineColor == null && ((BaseStyle) parent.dataStyle().lineStyle()).isParameterSet("color")) {
                AbstractStyleParameter par = ((BaseStyle) parent.dataStyle().lineStyle()).parameter("color");
                lineColor = par.parValue();
                if (par instanceof RevolvingStyleParameter) ((RevolvingStyleParameter) par).incrementCurrentIndex();
            }
            if (errorColor == null && ((BaseStyle) parent.dataStyle().errorBarStyle()).isParameterSet("color")) {
                AbstractStyleParameter par = ((BaseStyle) parent.dataStyle().errorBarStyle()).parameter("color");
                errorColor = par.parValue();
                if (par instanceof RevolvingStyleParameter) ((RevolvingStyleParameter) par).incrementCurrentIndex();
            }
            if (markerColor == null && ((BaseStyle) parent.dataStyle().markerStyle()).isParameterSet("color")) {
                AbstractStyleParameter par = ((BaseStyle) parent.dataStyle().markerStyle()).parameter("color");
                markerColor = par.parValue();
                if (par instanceof RevolvingStyleParameter) ((RevolvingStyleParameter) par).incrementCurrentIndex();
            }
            if (outlineColor == null && ((BaseStyle) parent.dataStyle().outlineStyle()).isParameterSet("color")) {
                AbstractStyleParameter par = ((BaseStyle) parent.dataStyle().outlineStyle()).parameter("color");
                outlineColor = par.parValue();
                if (par instanceof RevolvingStyleParameter) ((RevolvingStyleParameter) par).incrementCurrentIndex();
            }
        }
        
        if (fillColor != null)    ((BaseStyle) style.dataStyle().fillStyle()).setParameter("color", fillColor);
        if (lineColor != null)    ((BaseStyle) style.dataStyle().lineStyle()).setParameter("color", lineColor);
        if (errorColor != null)   ((BaseStyle) style.dataStyle().errorBarStyle()).setParameter("color", errorColor);
        if (markerColor != null)  ((BaseStyle) style.dataStyle().markerStyle()).setParameter("color", markerColor);
        if (outlineColor != null) ((BaseStyle) style.dataStyle().outlineStyle()).setParameter("color", outlineColor);
         */
        
        style.setParentList(styles);
        return style;
    }
    
    public void addStore(IStyleStore store) {
        if (stores.contains(store))
            throw new IllegalArgumentException("StyleRegistry already contains store: "+store.getStoreName());
        stores.add(store);
        addCategoriesFromStore(store);
    }
    
    public void removeStore(IStyleStore store) {
        if (!stores.contains(store))
            throw new IllegalArgumentException("StyleRegistry does not contains store: "+store.getStoreName());
        stores.remove(store);
        removeCategoriesFromStore(store);
    }
    
    void updateCategories() {
        //categories.clear();
        for (int i=0; i<stores.size(); i++) {
            addCategoriesFromStore((IStyleStore) stores.get(i));
        }
    }
    
    void addCategoriesFromStore(IStyleStore store) {
        //System.out.println("addCategoriesFromStore :: "+store.getStoreName());
        String[] names = store.getAllStyleNames();
        if (names == null) return;
        HashMap hash = new HashMap();
        for (int i=0; i<names.length; i++) {
            String rule = store.getRuleForStyle(names[i]).getDescription();
            
            //System.out.println("\tRule :: "+rule);
            int index = rule.indexOf("attribute(");
            while (index >= 0) {
                rule = rule.substring(index+11);
                index = rule.indexOf("attribute(");
                String token = null;
                if (index > 0) token = rule.substring(0, index);
                else token = rule;
                int index3 = token.indexOf("\").");
                String key = token.substring (0, index3);
                String value = token.substring (index3+1);
                value = value.substring(value.indexOf("\"")+1);
                value = value.substring(0, value.indexOf("\""));
                //System.out.println("\t\tKey :: "+key+"  Value="+value);
                if (key != null && !key.trim().equals("")) {
                    Object obj = hash.get(key);
                    if (obj == null) {
                        ArrayList list = new ArrayList();
                        list.add(value);
                        hash.put(key, list);
                    } else {
                        ArrayList list = (ArrayList) obj;
                        if (!list.contains(value)) list.add(value);
                    }
                }
            }
        }
        if (hash.isEmpty()) return;
        Iterator it = hash.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            ArrayList list = (ArrayList) hash.get(key);
            if (list.isEmpty()) continue;
            Object tmpCat = categories.get(key);
            Category cat = null;
            if (tmpCat == null) cat = new Category(key);
            else cat = (Category) tmpCat;
            for (int i=0; i<list.size(); i++) {
                Object tmp = list.get(i);
                if (tmp != null && !((String) tmp).equals("")) cat.addValue((String) tmp);
            }
            categories.put(key, cat);
        }
        hash.clear();
    }
    
    void removeCategoriesFromStore(IStyleStore store) {
        
    }
    
    
    // IStyleRegistry methods
    
    public String[] getAvailableStoreNames() {
        String[] names = new String[stores.size()];
        for (int i=0; i<stores.size(); i++) {
            names[i] = ((IStyleStore) stores.get(i)).getStoreName();
        }
        return names;
    }
    
    public IStyleStore getStore(String storeName) {
        IStyleStore store = null;
        String name = null;
        for (int i=0; i<stores.size(); i++) {
            name = ((IStyleStore) stores.get(i)).getStoreName();
            if (storeName.equals(name)) {
                store = (IStyleStore) stores.get(i);
                break;
            }
        }
        return store;
    }
    
    
    // To work with categories, this can be a separate service
    // Available category keys are filled from Rules of all available Stores
    
    public String[] getAvailableCategoryKeys() {
        updateCategories();
        String[] catKeys = new String[categories.size()];
        catKeys = (String[]) categories.keySet().toArray(catKeys);
        return catKeys;
    }
    
    public String[] getAvailableCategoryValues(String categoryKey) {
        return ((Category) categories.get(categoryKey)).getValues();
    }
    
    public String getCategoryCurrentValue(String categoryKey) {
        return ((Category) categories.get(categoryKey)).getCurrentValue();
    }
    
    public void setCategoryCurrentValue(String categoryKey, String categoryValue) {
        ((Category) categories.get(categoryKey)).setCurrentValue(categoryValue);
    }
    
    // Following methods are used to obtain cumulative IPlotterStyle
    // for particular region, object, action, and (possibly) categories
    
    public IPlotterStyle getStyleForState(IPlotterState state) {
        //System.out.flush();
        //System.out.println("\n\n********* StyleRegistry.getStyleForState ::  PlotterState:\n"+state.toString());
        //System.out.flush();
        //(new RuntimeException()).printStackTrace();
        IPlotterStyle style = null;
        Vector styles = new Vector();
        int size = stores.size()-1;
        for (int i=0; i<stores.size(); i++) {
            IStyleStore store = (IStyleStore) stores.get(size-i);
            String[] names = store.getAllStyleNames();
            for (int j=0; j<names.length; j++) {
                IStyleRule rule = store.getRuleForStyle(names[j]);
                if (rule.ruleApplies(state)) {
                    style = store.getStyle(names[j]);
                    if (!styles.contains(style)) {
                        styles.add(style);
                    }
                }
            }
        }
        style = mergeStyles(styles, state);
        return style;
    }
    
    
    class Category {
        private String name;
        private String currentValue;
        private List values;
        
        Category(String name) {
            this.name = name;
            values = new ArrayList(10);
        }
        
        String getName() { return name; }
        
        void addValue(String v) {
            if (!values.contains(v)) values.add(v);
        }
        
        String getCurrentValue() { return currentValue; }
        void setCurrentValue(String v) {
            addValue(v);
            currentValue = v;
        }
        
        String[] getValues() {
            String[] valArr = new String[values.size()];
            valArr = (String[]) values.toArray(valArr);
            return valArr;
        }
        
        void removeValue(String v) {
            if (values.contains(v)) {
                values.remove(v);
                if (currentValue != null && currentValue.equals(v)) currentValue = null;
            }
        }
    }
    
    
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
