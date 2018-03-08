package hep.aida.ref.plotter;

import hep.aida.IBaseStyle;
import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
    
public abstract class BaseStyle  extends WeakStyleListener implements IBaseStyle {
    private Hashtable internalParameterHash = new Hashtable();
    private Hashtable parameterHash = new Hashtable();
        
    private Hashtable baseStyles = new Hashtable();
    
    private List parentList = new ArrayList();

    private String name = "plotter";
    
    /**
     * Create a BaseStyle without a parent.
     * The parent, if ever available, can be set via the setParent method.
     *
     */
    protected BaseStyle () {
        super();
        initializeBaseStyle();
        addParameter( new BooleanStyleParameter(Style.IS_VISIBLE, true) );
    }
    
    /**
     * Create a clone of a BaseStyle.
     * @param style The BaseStyle to be cloned.
     *
     */
    protected BaseStyle ( BaseStyle style ) {
        this();
        copyStyle( this, style );
       
        List pl = style.parentList();
        this.setParentList(pl);
     }
    
    private void copyStyle( BaseStyle newStyle, BaseStyle oldStyle ) {
        Collection pars = oldStyle.parameters();
        Iterator iter = pars.iterator();
        while( iter.hasNext() ) {
            AbstractStyleParameter oldPar = (AbstractStyleParameter) iter.next();
            String[] av = oldPar.allowedValues();
            
            if ( ! oldPar.isParameterValueSet() ) 
                newStyle.setParameter(oldPar.name());
            else 
                newStyle.setParameter( oldPar.name(), oldPar.parameterValue(), av);
        }
        String[] names = newStyle.baseStyleNames();
        for ( int i = 0; i < names.length; i++ )
            copyStyle( newStyle.baseStyle( names[i] ), oldStyle.baseStyle( names[i] ) );
    }

    /**
     * Initialize the BaseStyle.
     * This method has to be overwritten by all Style that has to add parameters or
     * internal BaseStyles.
     *
     */
    protected abstract void initializeBaseStyle();
    
    
    /**
     * Set the name of this BaseStyle.
     * @param name The name.
     *
     */
    protected void setName( String name )  {
        this.name = name;
    }
    
    /**
     * Get the name of this BaseStyle.
     * @return The name of the BaseStyle.
     *
     */
    public String name() {
        return name;
    }
    
    /**
     * @return The type of the style.
     */
    public Class type() {
        return this.getClass().getInterfaces()[0];
    }
        
    /**
     * @return A sub style, knowing its name.
     */
    public IBaseStyle child(String name) {
        return (IBaseStyle) baseStyles.get( name );
    }
    
    /**
     * @return The array of sub styles.
     */
    public IBaseStyle[] children() {
        IBaseStyle[] children = new IBaseStyle[baseStyles.size()];
        Set keys = baseStyles.keySet();
        Iterator i = keys.iterator();
        int count = 0;
        while( i.hasNext() )
            children[count++] = (IBaseStyle) baseStyles.get(i.next());
        return children;
    }
    
    /**
     * Set isVisible of the data.
     */
    public boolean setVisible(boolean visible) {
        return setParameter(Style.IS_VISIBLE, String.valueOf(visible) );
    }
    
    /**
     * Get isVisible of the data.
     * return The isVisible of the data.
     */
    public boolean isVisible() {
        String isVisible = parameterValue(Style.IS_VISIBLE);
        return Boolean.valueOf(isVisible).booleanValue();
    }
    
    /**
     * Set the parent for this BaseStyle.
     * @param style The parent for this BaseStyle.
     *
     */
    public void setParent( IBaseStyle style ) {
        if (style == null) return;
        ArrayList list = new ArrayList(1);
        list.add(style);
        setParentList(list);
    }
    
    /**
     * First remove current parents (if present), then add new parents
     * from the list. If <code>list == null || list.size() == 0</code>, 
     * just remove current parents.
     */
    public void setParentList( List list ) {
        if (parentList != null && parentList.size() > 0) {
            int size = parentList.size();
            Object[] tmpArray = parentList.toArray();            
            for (int i=0; i<size; i++) {
                Object obj = tmpArray[i];
                if (obj instanceof IBaseStyle)
                    removeParent((IBaseStyle) obj, false);
            }
            parentList.clear();
        }
        
        if (list == null || list.size() == 0) return;
        for (int i=0; i<list.size(); i++) {
            Object obj = list.get(i);
            if (obj instanceof IBaseStyle)
                addParent((IBaseStyle) obj, false);
        }
        notifyStyleChanged();
    }
        

    /**
     * Remove all parents and all the parents' parents
     */
    public void removeAllParents() {
        /*
        if (parentList != null && parentList.size() > 0) {
            for (int i=0; i<parentList.size(); i++) {
                Object obj = parentList.get(i);
                if (obj instanceof BaseStyle) 
                    ((BaseStyle) obj).removeAllParents();
            }
        }
        */
        // remove all immediate parents
        setParentList(null);
     }
    
    /**
     * This method removes one parent and all its children (recursively).
     * To remove all parents use <code>setParentList(null)</code>
     */
    public void removeParent( IBaseStyle style ) {
        removeParent(style, true);
    }    
    void removeParent( IBaseStyle style, boolean sendNotification ) {
        if (style instanceof BaseStyle)
            ((BaseStyle) style).removeStyleListener(this);
        if (!isMyParent(style)) return;
        parentList.remove(style);
        
        // remove parent children recursively
        IBaseStyle[] parentChildren = style.children();
        for (int i=0; i<parentChildren.length; i++) {
            String name = parentChildren[i].name();
            BaseStyle localChild = baseStyle(name);
            if (localChild != null) localChild.removeParent(parentChildren[i]);
        }
        if ( sendNotification ) notifyStyleChanged();
    }
    
    public void addParent( IBaseStyle style ) {
        addParent(style, true);
    }
    void addParent( IBaseStyle style, boolean sendNotification ) {
        if (isMyParent(style)) return;
        if (style == this) {
            System.out.println("***** SELF-Parent :: skip :: "+style.name());
            return;
        }
        
        if (parentList == null) parentList = new ArrayList();
        parentList.add(style);
        if (style instanceof BaseStyle)
            ((BaseStyle) style).addStyleListener(this);
        
        // add parent children recursively
        IBaseStyle[] parentChildren = style.children();
        for (int i=0; i<parentChildren.length; i++) {
            String name = parentChildren[i].name();
            BaseStyle localChild = baseStyle(name);
            if (localChild != null) localChild.addParent(parentChildren[i]);
        }
        if ( sendNotification ) notifyStyleChanged();
    }
    
    public void addParentList( List pl ) {
        if (pl == null || pl.size() == 0) return;
        if (parentList == null) parentList = new ArrayList();
        for (int i=0; i<pl.size(); i++) {
            Object obj = pl.get(i);
            if (obj instanceof IBaseStyle) addParent((IBaseStyle) obj, false);
        }
        notifyStyleChanged();
    }
    
    // Check if "style" is in the parent list
    public boolean isMyParent(IBaseStyle style) {
        if  (   style == null ||
                parentList == null ||
                parentList.size() == 0  )
            return false;
        return parentList.contains(style);
    }
    
    
    /**
     * Get the parent for this BaseStyle.
     * @return The parent.
     *
     */
    public List parentList() {
        return parentList;
    }
    
    /**
     * Add a BaseStyle to this BaseStyle.
     * This method is to be invoked when a BaseStyle contains other BaseStyles in order
     * for the reset and the setParent methods to work properly.
     *
     */
    protected boolean addBaseStyle( IBaseStyle baseStyle, String name ) {
        BaseStyle bs = (BaseStyle) baseStyle;
        bs.setName(name);
        if ( baseStyles.get( bs.name() ) != null )
            ( (BaseStyle) baseStyles.get(bs.name()) ).removeStyleListener(this);
        baseStyles.put( bs.name(), baseStyle );
        bs.addStyleListener(this);
        notifyStyleChanged();
        return true;
    }
    
    /**
     * Get a BaseStyle contained in this BaseStyle.
     * The indexing is the internal one.
     *
     */
    private BaseStyle baseStyle( String name ) {
        return (BaseStyle) child(name);
    }
    
    /**
     * Get the available BaseStyle names attached to this BaseStyle.
     *
     */
    private String[] baseStyleNames() {
        String[] names = new String[ baseStyles.size() ];
        Enumeration e = baseStyles.keys();
        int count = 0;
        while( e.hasMoreElements() )
            names[ count++ ] = (String) e.nextElement();
        return names;
    }
        
    /**
     * Add a new parameter to this BaseStyle.
     * @param styleParameter The style parameter.
     *
     */
    protected void addParameter( AbstractStyleParameter styleParameter ) {
        addParameter(styleParameter, true);
    }

    private void addParameter( AbstractStyleParameter styleParameter, boolean isInternal) {
        String name = styleParameter.name();
        if ( isRegisteredParameter(name) )
            throw new IllegalArgumentException("Parameter "+styleParameter.name()+" already belongs to this style.");
        if ( isInternal )
            internalParameterHash.put(name, styleParameter);
        parameterHash.put(name, styleParameter);
    }
    
    private boolean isInternalParameter( String parameterName ) {
        return internalParameterHash.containsKey(parameterName);
    }
    
    private boolean isExternalParameter( String parameterName ) {
        return ( ! internalParameterHash.containsKey(parameterName) ) && parameterHash.containsKey(parameterName);
    }
    
    private boolean isRegisteredParameter( String parameterName ) {
        return parameterHash.containsKey(parameterName);
        
    }
    
    public AbstractStyleParameter parameter(String parName) {
        if ( ! isRegisteredParameter(parName) )
            throw new IllegalArgumentException("Parameter with name "+parName+" does not belong to this style.");
        return (AbstractStyleParameter) parameterHash.get(parName);
    }
    
    public AbstractStyleParameter deepestSetParameter(String parameterName) {
    //protected AbstractStyleParameter deepestSetParameter(String parameterName) {
        if ( parameter(parameterName).isParameterValueSet() || parentList == null || parentList.size() == 0)
            return parameter(parameterName);
        else {
            Iterator it = parentList().iterator();
            BaseStyle pbs = null;
            while (it.hasNext()) {
                pbs = (BaseStyle) it.next();
                if (pbs == null) continue;
                if (pbs.isParameterSet(parameterName)) {
                    return pbs.deepestSetParameter(parameterName);
                }
            }
            return (pbs == null) ? parameter(parameterName) : pbs.parameter(parameterName);
        }
    }
       
    
    protected int numberOfParameters() {
        return parameterHash.size();
    }
    
    protected Collection parameters() {
        return parameterHash.values();
    }

    public boolean isParameterSet(String parameterName) {
        return isParameterSet(parameterName, true);
    }
    public boolean isParameterSet(String parameterName, boolean recursive) {
        if ( parameter(parameterName).isParameterValueSet() || !recursive || parentList == null || parentList.size() == 0)
            return parameter(parameterName).isParameterValueSet();
        else {
            boolean isSet = false;
            Iterator it = parentList().iterator();
            while (it.hasNext()) {
                BaseStyle pbs = (BaseStyle) it.next();
                if (pbs == null) continue;
                if (pbs.isParameterSet(parameterName, recursive)) {
                    isSet = true;
                    break;
                }
            }
            return isSet;       
        }
    }
    
    /**
     * Below are the AIDA methods.
     *
     */
    public String[] availableParameterOptions(String parameterName) {
        AbstractStyleParameter par = parameter(parameterName);
        return par.allowedValues();
    }
    
    public String[] availableParameters() {
        int size = numberOfParameters();
        String[] pars = new String[ size ];
        Collection parameters = parameters();
        Iterator iter = parameters.iterator();
        int count = 0;
        while( iter.hasNext() )
            pars[count++] = ( (AbstractStyleParameter) iter.next() ).name();
        return pars;
    }
    
    public String parameterValue(String parameterName) {
        AbstractStyleParameter par = deepestSetParameter(parameterName);
        return par.parameterValue();
    }
    
    public void reset() {
        reset(true);
    }
    
    private void reset( boolean sendNotification ) {
        removeAllParents();
        
        Enumeration parKeys = parameterHash.keys();
        while( parKeys.hasMoreElements() )
            parameter( (String) parKeys.nextElement() ).reset();

        Enumeration keys = baseStyles.keys();
        while( keys.hasMoreElements() )
            baseStyle( (String) keys.nextElement() ).reset(false);

        if ( sendNotification )
            notifyStyleChanged();
    }
    
        
    public boolean setParameter(String parameterName) {
        boolean result = false;
        if ( isRegisteredParameter(parameterName) ) {
            result = parameter(parameterName).setParameter();
            if ( ! isInternalParameter(parameterName) )
                result = false;
        }
        else {
            StringStyleParameter styleParameter = new StringStyleParameter(parameterName, null);
            addParameter(styleParameter, false);
        }
        notifyStyleChanged();
        return result;
    }
    
    public boolean setParameter(String parameterName, String parValue) {
        String[] av = null;
        if (isRegisteredParameter(parameterName) && parameter(parameterName) instanceof RevolvingStyleParameter) {
            String[] oldAv = parameter(parameterName).allowedValues();
            if (oldAv == null || oldAv.length == 0) av = new String[] { parValue };
        }
        return setParameter(parameterName, parValue, av);
    }
    public boolean setParameter(String parameterName, String parValue, String[] parAllowedValues) {
        boolean result = false;
        if ( isRegisteredParameter(parameterName) ) {
            if (parAllowedValues != null) parameter(parameterName).setAllowedValues(parAllowedValues);
            result = parameter(parameterName).setParameter(parValue);
            if ( ! isInternalParameter(parameterName) )
                result = false;
        }
        else {
            StringStyleParameter styleParameter = new StringStyleParameter(parameterName, null);
            addParameter(styleParameter, false);
            if (parAllowedValues != null) styleParameter.setAllowedValues(parAllowedValues);
            styleParameter.setValue(parValue);
        }
        notifyStyleChanged();
        return result;
    }
    
    // Use this method to give styles a different default than the "standard one"
    public void setParameterDefault(String parameterName, String parDefaultValue) {
        if ( ! isRegisteredParameter(parameterName) )
            throw new IllegalArgumentException("Parameter "+parameterName+" does not exist ");
        parameter(parameterName).setDefaultValue(parDefaultValue);
        notifyStyleChanged();
    }

    
    /**
     * Notify the listeners that the style has changed.
     *
     */
    
    public void styleChanged(BaseStyle style) {
        notifyStyleChanged();
    }
    
    void notifyStyleChanged() {
        StyleListener[] tmp = getValidStyleListeners();
        for ( int i = 0; i < tmp.length; i++ ) {
            tmp[i].styleChanged(this);
        }
    }
    
}
