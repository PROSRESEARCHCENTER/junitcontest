package hep.aida.ref.event;

import java.util.EventObject;
import java.util.Observable;
import java.util.Observer;

/**
 * An ObserverAdapter can be used as a base class for class that wants to act
 * as both AIDAListener and Observable, and relay things it observers to its
 * Observers.
 */

public class ObserverAdapter extends Observable implements AIDAListener {
    // Ideally we would like to only add ourselves as an AIDAListener if at least one person
    // is observing us. This makes things more efficient, and helps to avoid useless references
    // which hinder garbage collection
    
    protected IsObservable histo;
    protected Object update;
    private boolean observing = false;
    
    public ObserverAdapter() { this(null); }
    
    public ObserverAdapter(Object obs) {
        super();
        if (obs instanceof IsObservable) this.histo = (IsObservable) obs;
    }
    
    
    // service methods
    
    public synchronized void setObservable(IsObservable newObs) {
       observe(false);
        histo = newObs;
        if (newObs != null) {
            observe(true);
        }
    }
    
    public synchronized void clearObservable() {
        setObservable(null);
    }
    
    public IsObservable getObservable() {
        return histo;
    }
    
    private void observe(boolean set) {
        //String noObservers = System.getProperty("hep.aida.ref.noObservers");
        //boolean propertyForbidden = ( noObservers != null && Boolean.valueOf(noObservers).booleanValue() );
        //set = !propertyForbidden && set;
        //set = noObservers == null && set;
        if (set != observing) {
            if (histo != null) {
                if (set) histo.addListener(this);
                else     histo.removeListener(this);
            }
            observing = set;
        }
    }
    
    protected void setValid() {
        if (histo != null) histo.setValid(this);
    }
    
    
    // Observable methods
    
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        observe(true);
    }
    
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
        if (countObservers() == 0) observe(false);
    }
    
    public synchronized void deleteObservers() {
        super.deleteObservers();
        observe(false);
    }
    
    // AIDAListener method
    public void stateChanged(EventObject e) {
        setChanged();
        notifyObservers(update);
    }
  
}
