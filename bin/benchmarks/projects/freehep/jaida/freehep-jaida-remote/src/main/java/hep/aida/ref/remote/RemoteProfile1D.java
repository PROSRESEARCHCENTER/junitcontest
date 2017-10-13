/*
 * RemoteProfile1D.java
 *
 * Created on October 28, 2004, 11:18 AM
 */

package hep.aida.ref.remote;

import hep.aida.IProfile;
import hep.aida.IProfile1D;
import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.Annotation;
import hep.aida.ref.ReadOnlyException;

/**
 *
 * @author  serbo
 */
public class RemoteProfile1D extends RemoteSettable1DObject implements IProfile,  IProfile1D {
    
    /** Creates a new instance of RemoteHistogram1D */
    public RemoteProfile1D(String name) {
        this(null, name);
    }

    public RemoteProfile1D(IDevMutableStore store, String name) {
        this(store, name, name, 1, 0., 1.);
    }

    public RemoteProfile1D(IDevMutableStore store, String name, String title, int bins, double min, double max) {
        super(name);
        aidaType = "IProfile1D";
        this.store = store;
        annotation = new Annotation();
        annotation.setFillable(true);
        annotation.addItem(Annotation.titleKey,title,true);
        annotation.setFillable(false);
        dataIsValid = false;
        setAxis(bins, min, max);
    }

     
     public void add(hep.aida.IProfile1D iProfile1D) throws java.lang.IllegalArgumentException {
          throw new ReadOnlyException();
   }
    
    public void fill(double param, double param1, double param2) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
}
