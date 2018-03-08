package hep.aida.ref.plotter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 *
 * @author The AIDA team @ SLAC.
 */
public abstract class WeakStyleListener implements StyleListener {
    
    protected ArrayList listeners = new ArrayList();
    
    public WeakStyleListener() {
    }
    
    StyleListener[] getValidStyleListeners() {
        StyleListener[] listenerArray = new StyleListener[0];
        if (listeners == null || listeners.size() == 0) return listenerArray;
        
        Object[] objArray = listeners.toArray();
        ArrayList list = new ArrayList(objArray.length);
        for ( int i = 0; i < objArray.length; i++ ) {
            WeakReference wr = (WeakReference) objArray[i];
            StyleListener listener = (StyleListener) wr.get();
            if (listener == null) {
                listeners.remove(wr);
            } else {
                list.add(listener);
            }
        }
        listenerArray = new StyleListener[list.size()];
        listenerArray = (StyleListener[]) list.toArray(listenerArray);
        list.clear();
        return listenerArray;
    }

    public abstract void styleChanged(BaseStyle style);
        
    void addStyleListener( StyleListener listener ) {
        if (hasListener(listener) || listener == null) return;
        listeners.add(new WeakReference(listener));
    }
    
    boolean hasListener(StyleListener listener) {
        Object[] objArray = listeners.toArray();
        for ( int i = 0; i < objArray.length; i++ ) {
            WeakReference wr = (WeakReference) objArray[i];
            StyleListener localListener = (StyleListener) wr.get();
            if (localListener == null) {
                listeners.remove(wr);
            } else {
                if (localListener == listener) return true;
            }
        }
        return false;
    }
    
    void removeStyleListener( StyleListener listener ) {
        Object[] objArray = listeners.toArray();
        for ( int i = 0; i < objArray.length; i++ ) {
            WeakReference wr = (WeakReference) objArray[i];
            StyleListener localListener = (StyleListener) wr.get();
            if (localListener == null) {
                listeners.remove(wr);
            } else {
                if (localListener == listener) {
                    listeners.remove(wr);
                }
            }
        }
    }

    
}
