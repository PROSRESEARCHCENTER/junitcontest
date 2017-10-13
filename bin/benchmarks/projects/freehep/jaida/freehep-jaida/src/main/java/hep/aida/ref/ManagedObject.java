package hep.aida.ref;

import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITuple;
import hep.aida.ref.event.Connectable;

/**
 * An object which can be stored in a tree.
 * Implementation of IManagedObject.
 *
 * @author The AIDA Team at SLAC
 *
 */
public class ManagedObject extends hep.aida.ref.event.AIDAObservable implements Connectable, hep.aida.dev.IDevManagedObject {
    
    private String name;
    protected boolean fillable;
    private static final Class[] managedClasses = {IHistogram1D.class,IHistogram2D.class,IHistogram3D.class,
            ICloud1D.class,ICloud2D.class,ICloud3D.class,
            IProfile1D.class,IProfile2D.class,
            IDataPointSet.class,
            IFunction.class,
            ITuple.class};
                

    /**
     * Creates a new instance of ManagedObject.
     * @param name The name of the ManagedObject as it will appear in the Tree.
     *
     */
    public ManagedObject(String name) {
        this.name = name;
        fillable = true;
    }

    /**
     * If ManagedObject is fillable, it can modified.
     * othervisw throws ReadOnlyException.
     */
    public void setFillable(boolean fillable) {
        this.fillable = fillable;
    }
    
    public boolean isFillable() {
        return fillable;
    }
    
    /**
     * Get the name of this ManagedObject.
     * Names can only be changed using the ITree.mv().
     * @return The name of the ManagedObject.
     *
     */ 
    public String name() {
        return name;
    }

    /**
     * Set the name of this ManagedObject.
     * @param name The new name of the ManagedObject.
     *
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public String type() { 
        return typeForClass(getClass());
    }
    public static String typeForClass(Class c) {
        for ( int i = 0; i < managedClasses.length; i++ )
            if ( managedClasses[i].isAssignableFrom(c))
                return managedClasses[i].getName().substring( managedClasses[i].getName().lastIndexOf(".")+1 );
        return "IUnknown";        
    }
}
    
    