package hep.aida.ref.plotter.style.registry;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class PlotterState implements IPlotterState {
    private Object object;
    private String path;
    private int overlayIndex;
    private int overlayTotal;
    private int regionIndex;
    private int regionTotal;
    private Map map;
    
    public String toString() {
        String tmp ="";
        tmp += "\tOverlayIndex="+overlayIndex+", OverlayTotal="+overlayTotal+"\n";
        tmp += "\tRegionIndex="+regionIndex+",  RegionTotal="+regionTotal+"\n";
        tmp += "\tPath="+path+",  Object="+object+"\n";
        
        
        tmp += "\n\tAttributes: "+map.size()+"\n";
        Iterator it = map.keySet().iterator();
        int index = 0;
        while (it.hasNext()) {
            Object key = it.next();
            Object value = map.get(key);
            tmp += "\t\t"+index+"\tKey="+key.toString()+"\tValue="+value.toString()+"\n";
            index++;
        }
        return tmp;
    }
    
    public PlotterState() {
        this(null);
    }
    public PlotterState(Object object) {
        this(object, "");
    }
    public PlotterState(Object object, String path) {
        this.object = object;
        this.path = path;
        map = new Hashtable();
        overlayIndex = -1;
        overlayTotal = -1;
        regionIndex = -1;
        regionTotal = -1;
    }
    
    public void setObject(Object obj) {
        object = obj;
    }
    public Object getObject() {
        return object;
    }
    
    public void setObjectPath(String p) { path = p; }
    public String getObjectPath() { 
        if (path == null) path = "";
        return path; 
    }
    
    public void setOverlayIndex(int n) { overlayIndex = n; }
    public void setOverlayTotal(int n) { overlayTotal = n; }
    public int getOverlayIndex() { return overlayIndex; }
    public int getOverlayTotal() { return overlayTotal; }
    
    public void setRegionIndex(int n) { regionIndex = n; }
    public void setRegionTotal(int n) { regionTotal = n; }
    public int getRegionIndex() { return regionIndex; }
    public int getRegionTotal() { return regionTotal; }
        
    public void setAttribute(String key, String value) { map.put(key, value); }
    public String getAttribute(String key) { return (String) map.get(key); }
    
    
    public int getAttributeIndex(String key) {
        Iterator it = map.keySet().iterator();
        int i = 0;
        int index = -2;
        String value = null;
        while (it.hasNext()) {
            value = (String) it.next();
            if (key.equals(value)) {
                index = i;
                break;
            }
            i++;
        }
        return i;
    } 
    public String getAttribute(int index) { 
        Iterator it = map.values().iterator();
        int i = 0;
        Object value = null;
        Object tmp = null;
        while (it.hasNext()) {
            value = it.next();
            if (i == index) {
                value = tmp;
                break;
            }
            i++;
        }
        return (String) value;
    }
    
    public Map getAttributes() {
        return map;
    }  
    
    public void clear() {
        object = null;
        path = null;
        overlayIndex = -1;
        overlayTotal = -1;
        regionIndex = -1;
        regionTotal = -1;
        map.clear();
    }
}
