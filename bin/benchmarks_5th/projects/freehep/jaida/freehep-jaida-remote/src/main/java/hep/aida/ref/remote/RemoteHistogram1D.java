/*
 * RemoteHistogram1D.java
 *
 * Created on May 28, 2003, 5:38 PM
 */

package hep.aida.ref.remote;

import hep.aida.IHistogram1D;
import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.Annotation;
import hep.aida.ref.ReadOnlyException;
import hep.aida.ref.event.HistogramEvent;

/**
 * This is implementation of IHistogram1D that can not be modified
 * by the user. This is a simple implementation that does not guarantee
 * internal consistency. So extra care should be taken when setting
 * data for this class.
 * This Class is designed to work with the IDevMutableStore, but can
 * work with other Stores - overwrite makeSureDataIsValid() method.
 * Don't forget to call "setDataValid" after you fill new data, as
 * only this method fires events to notify AIDAListeners about change.
 *
 * Also please note that in our current AIDA implementation:
 *  UNDERFLOW_BIN = -2, OVERFLOW_BIN = -1, bins = [0, nBins-1]
 * But we keep them here as:
 *  UNDERFLOW_BIN = 0, OVERFLOW_BIN = nBins+1, bins = [1, nBins]
 *
 * @author  serbo
 */
public class RemoteHistogram1D extends RemoteSettable1DObject implements IHistogram1D {

    /** Creates a new instance of RemoteHistogram1D */
    public RemoteHistogram1D(String name) {
        this(null, name);
    }

    public RemoteHistogram1D(IDevMutableStore store, String name) {
        this(store, name, name, 1, 0., 1.);
    }

    public RemoteHistogram1D(IDevMutableStore store, String name, String title, int bins, double min, double max) {
        super(name);
        aidaType = "IHistogram1D";
        this.store = store;
        annotation = new Annotation();
        annotation.setFillable(true);
        annotation.addItem(Annotation.titleKey,title,true);
        annotation.setFillable(false);
        dataIsValid = false;
        setAxis(bins, min, max);
    }


    // AIDAObservable methods
    protected java.util.EventObject createEvent()
    {
       return new HistogramEvent(this);
    }


    // Service methods

    public void setTreeFolder(String treeFolder) {
        super.setTreeFolder(treeFolder);

        boolean flbl = annotation.isFillable();
        if (!flbl) annotation.setFillable(true);
        if (annotation.hasKey(Annotation.fullPathKey)) {
            annotation.setValue(Annotation.fullPathKey, treePath);
        } else {
            annotation.addItem(Annotation.fullPathKey, treePath, true);
        }
        if (!flbl) annotation.setFillable(false);
    }



    public void add(hep.aida.IHistogram1D iHistogram1D) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }

    public void fill(double param) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }


    public void scale(double param) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }

}
