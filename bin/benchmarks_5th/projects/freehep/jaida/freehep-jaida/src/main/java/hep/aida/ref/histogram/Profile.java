package hep.aida.ref.histogram;

/**
 * Implementation of IProfile.
 *
 * @author The AIDA team at SLAC.
 *
 */

import hep.aida.IProfile;
import hep.aida.ref.event.HistogramEvent;
import hep.aida.ref.event.IsObservable;

public abstract class Profile extends AbstractBaseHistogram implements IProfile, IsObservable {
    
    private String options;
    
    /** 
     * Create a new Profile
     * @param name      The name of the Profile as a ManagedObject.
     * @param title     The title of the Profile.
     * @param dimension The dimension of the Profile.
     *
     */
    protected Profile(String name, String title, int dimension) {
        this( name, title, dimension, "");
    }
    
    protected Profile(String name, String title, int dimension, String options) {
        super(name, title, dimension);        
        this.options = options;
    }

    protected java.util.EventObject createEvent()
    {
       return new HistogramEvent(this);
    }

    protected String options() {
        return options;
    }

    public int nanEntries() {
        return allEntries()-entries()-extraEntries();
    }
}
